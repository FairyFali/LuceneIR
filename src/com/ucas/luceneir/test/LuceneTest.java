package com.ucas.luceneir.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ucas.luceneir.model.Docs;
import com.ucas.luceneir.utils.Utils;

public class LuceneTest {
	
	public static String indexPath = "D:\\ProgramInstallation\\eclipseJ2EE\\workspace\\LuceneIR\\WebContent\\index\\";
	public static String dataPath = "D:\\ProgramInstallation\\eclipseJ2EE\\workspace\\LuceneIR\\WebContent\\data\\";

	@Test
	public void createIndexXmlTest() throws IOException, ParserConfigurationException, SAXException {
		// 1.创建Directory
		Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexPath).toFile());
		// 2.创建indexwriter
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_43);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		//indexWriter.deleteAll();
		
		// 3.获得所有文件名
		ArrayList<String> fileNameList = Utils.getFileNameUtil(dataPath);
		System.out.println(fileNameList);
		
		// 4.获得所有的docs
		ArrayList<Docs> totalDocs = new ArrayList<Docs>();
		for (String path : fileNameList) {
			System.out.println(path);
			ArrayList<Docs> docs = Utils.getDocsFromXml(dataPath + path);
			//System.out.println(path + ":" + docs);
			totalDocs.addAll(docs);
		}
		System.out.println("total index file count is " + totalDocs.size());
		// 5.遍历所有的totalDocs
		for (Docs docs : totalDocs) {
			// 建立文档对象
			Document document = new Document();
			document.add(new TextField("title", docs.getTitle(), Store.YES));
            document.add(new TextField("docURL", docs.getDocURL(), Store.YES));
            document.add(new TextField("content", docs.getContent(), Store.YES));
            document.add(new TextField("category", docs.getCategory(), Store.YES));
            document.add(new TextField("data", docs.getData(), Store.YES));
            document.add(new TextField("author", docs.getAuthor(), Store.YES));
            document.add(new TextField("image", docs.getImage(), Store.YES));
            
            // hotScore 用于排序
            document.add(new TextField("hotScore",docs.getHotScore()+"", Store.YES));
        	
        	// 时间 用于排序
        	document.add(new TextField("data_sort",docs.getData_sort() + "", Store.YES));
        	// 添加到index中
        	System.out.println(docs.getTitle() + " " + docs.getCategory());
        	indexWriter.addDocument(document);
		}
		
		// 关闭index
		indexWriter.close();
		System.out.println("index create successfully!");
		return;
	}
	
	@Test
	public void createIndexJsonTest() throws java.text.ParseException, IOException {
		// 获得所有文件名
		ArrayList<String> fileNameList = Utils.getFileNameUtil(dataPath);
		System.out.println(fileNameList);
		// 随机种子
		Random r = new Random();
		// 处理每一个json文件
		String line = "";
		List<Docs> totalDocs = new ArrayList<>();
		for (String path : fileNameList) {
			if(!"json".equals(path.substring(path.lastIndexOf('.')+1))) {
				continue;
			}
			File file = new File(dataPath + "\\" + path);
			String category = null;
			SimpleDateFormat formatter = null;
			if(path.indexOf("wangyi") != -1) {
				category = "网易";
				formatter = new SimpleDateFormat("MM/dd/yyyyHH:mm:ss");
			} else if(path.indexOf("xinlang") != -1) {
				category = "新浪";
				formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			}
			try (FileReader reader = new FileReader(file);
					BufferedReader br = new BufferedReader(reader) ) {
				while ((line = br.readLine()) != null) {
					// 一次读入一行数据
					//line = new String(line.getBytes("GBK"), "UTF-8");
					//System.out.println(line);
					Map map = JSON.parseObject(line, Map.class);
					Docs docs = new Docs();
					String title = (String) map.get("title");
					docs.setTitle(title.trim());
					docs.setCategory(category);
					docs.setDocURL((String)map.get("url"));
					//System.out.println(map.get("content"));
					String content = (String)map.get("content");
					docs.setContent(content.trim());
					String time = (String)map.get("pub_time");
					//System.out.println(time);
					//System.out.println(formatter);
					
					Date date = null;
					if (time == null || "".equals(time) || "None".equals(time)) {
						date = new Date(2000, 01, 01);
					} else {
						date = formatter.parse(time);
					}
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					docs.setData(calendar.get(Calendar.YEAR) + "-" +
							(calendar.get(Calendar.MONTH)+1) +  "-" +
							calendar.get(Calendar.DAY_OF_MONTH));
					docs.setData_sort(calendar.get(Calendar.YEAR)*10000 
							+ (calendar.get(Calendar.MONTH)+1)*100 
							+ calendar.get(Calendar.DAY_OF_MONTH));
					// 没有作者
					docs.setAuthor("佚名");
					// 没有图片
					docs.setImage("noimage");
					//随机赋值给该doc一个热度
					docs.setHotScore(r.nextInt(10000));
					totalDocs.add(docs);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				// TODO: handle exception
				System.out.println("ClassCastException");
			}
		}
		
		System.out.println("total index file count is " + totalDocs.size());
		//System.out.println(totalDocs.get(totalDocs.size()-1));
		// 创建Directory
		Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexPath).toFile());
		// 创建indexwriter
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_43);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		//indexWriter.deleteAll();
		// 遍历所有的totalDocs
		for (Docs docs : totalDocs) {
			// 建立文档对象
			Document document = new Document();
			document.add(new TextField("title", docs.getTitle(), Store.YES));
            document.add(new TextField("docURL", docs.getDocURL(), Store.YES));
            document.add(new TextField("content", docs.getContent(), Store.YES));
            document.add(new TextField("category", docs.getCategory(), Store.YES));
            document.add(new TextField("data", docs.getData(), Store.YES));
            document.add(new TextField("author", docs.getAuthor(), Store.YES));
            document.add(new TextField("image", docs.getImage(), Store.YES));
            
            // hotScore 用于排序
            document.add(new TextField("hotScore",docs.getHotScore()+"", Store.YES));
        	// 时间 用于排序
        	document.add(new TextField("data_sort",docs.getData_sort() + "", Store.YES));
        	// 添加到index中
        	System.out.println(docs.getTitle() + " " + docs.getCategory());
        	indexWriter.addDocument(document);
		}
		
		// 关闭index
		indexWriter.close();
		System.out.println("index create successfully!共" + totalDocs.size() + "条新闻");
		return;
	}
	
	@Test
	public void createIndex() throws IOException, ParserConfigurationException, SAXException, java.text.ParseException {
		createIndexXmlTest();
		createIndexJsonTest();
		System.out.println("xml and json index finished!");
	}
	
	@Test
	public void SearchTest() throws IOException, ParseException, InvalidTokenOffsetsException {
		ArrayList<Docs> resultList = new ArrayList<Docs>();
		String indexPath = "C:\\ProgramInstall\\eclipse\\workspace\\LuceneIR\\WebContent\\index\\";
		String query = "地区";
		int resultNum = 10;
		// 1.索引目录
		Directory directory = FSDirectory.open(new File(indexPath));
		// 2.创建indexsearch
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		// 查询字段
		String[] fields = {"title", "content"};
		// 分词方式
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);// 标准分词
		MultiFieldQueryParser parser2 = new MultiFieldQueryParser(Version.LUCENE_43, fields, analyzer);
		
		Query query2 = parser2.parse(query);
//		Term term = new Term("title", query);
//		WildcardQuery query2 = new WildcardQuery(term);
		// 高亮
		QueryScorer scorer = new QueryScorer(query2, fields[0]);
		SimpleHTMLFormatter fors = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
		Highlighter highlighter = new Highlighter(fors, scorer);
		TopDocs topDocs = searcher.search(query2, 500);
		int totalnews;
		if (topDocs != null) {
			totalnews = topDocs.totalHits;
			System.out.println("符合条件第文档总数：" + totalnews);
			for (int i = 0; i < Math.min(resultNum, topDocs.scoreDocs.length); i++) {
				Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
				// 分词
				TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(),
						topDocs.scoreDocs[i].doc, fields[0], analyzer);
				Fragmenter fragment = new SimpleSpanFragmenter(scorer);
				highlighter.setTextFragmenter(fragment);
				// 高亮标题
				String hl_title = highlighter.getBestFragment(tokenStream, doc.get("title"));

				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), topDocs.scoreDocs[i].doc,
						fields[1], analyzer);
				// 高亮snippet
				String hl_summary = highlighter.getBestFragment(tokenStream, doc.get("content"));

				Docs news = new Docs();
				news.setTitle(doc.get("title"));
				news.setHtitle(hl_title != null ? hl_title : doc.get("title"));
				news.setDocURL(doc.get("link"));
				news.setAuthor(doc.get("author"));
				news.setImage(doc.get("image"));
				news.setContent(doc.get("content"));
				news.setHcontent(hl_summary);
				news.setData(doc.get("data"));
				news.setData_sort(Integer.valueOf(doc.get("data_sort")));
				news.setHotScore(Integer.valueOf(doc.get("hotScore")));
				
				news.setTotalDocs(totalnews);
				
				//System.out.println(news.getHtitle());
				resultList.add(news);
			}
			// 时间排序
			resultList.sort(new Comparator<Docs>() {
				public int compare(Docs o1, Docs o2) {
					return o1.getData_sort() - o2.getData_sort();
				}
			});
			
			for (Docs docs : resultList) {
				System.out.println(docs.getHtitle());
				System.out.println();
			}
			
		}
	}
	
	/**
	 * 通配符查询
	 * @throws IOException 
	 */
	@Test
	public void WildCardTest() throws IOException {
		String indexPath = "C:\\ProgramInstall\\eclipse\\workspace\\LuceneIR\\WebContent\\index\\";
		Directory directory = FSDirectory.open(new File(indexPath));
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		Term term = new Term("title", "*西部*");
		WildcardQuery wildcardQuery = new WildcardQuery(term);

		TopDocs topDocs = indexSearcher.search(wildcardQuery, 10);
		ScoreDoc scoreDocs[] = topDocs.scoreDocs;
		System.out.println("检索到"+scoreDocs.length);
		for (int i = 0; i < scoreDocs.length; i++) {
			Document document = indexSearcher.doc(scoreDocs[i].doc);
			System.out.println(document.get("title"));
		}
		directory.close();
	}
	
	@Test
	public void search() {
		long start_time = System.currentTimeMillis();
		ArrayList<Docs> list = getSearch("公交车行驶中女子大闹", 
				indexPath, 
				1, "all", 2);
		long end_time = System.currentTimeMillis();
		System.out.println("cost time is " + (end_time-start_time)/1000.0);
		for (Docs docs : list) {
			System.out.println(docs.getHtitle());
			System.out.println(docs.getData());
			//System.out.println(docs.getHcontent());
		}
	}
	
	public ArrayList<Docs> getSearch(String keyword, String indexPathStr, int rank, String category, int search_field) {
		// keyword 关键字
		// indexPathStr 索引路径
		// rank 表示排序方式 1默认相关度排序；2按热度排序；3按时间倒序
		// category 表示搜索范围 0表示全局搜索，不同分类
		// search_field 检索方式：0全文标题检索，1全文检索，2标题检索
		ArrayList<Docs> docList = new ArrayList<Docs>();
		DirectoryReader directoryReader = null;
		int totalDocs;
		try {
			// 1.创建Directory
			Directory directory = FSDirectory.open(new File(indexPathStr));
			// 2.创建IndexReader
			directoryReader = DirectoryReader.open(directory);
			// 3.创建IndexSearch
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

//	        Analyzer analyzer = new SmartChineseAnalyzer();
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);// 标准分词
			TopDocs topDocs = null;
			QueryScorer scorer = null; // 用于高亮显示
			if (category.equals("all")) {

				if (search_field == 0) {
//					BooleanClause bc1 = new BooleanClause(query1, Occur.SHOULD);
//					BooleanClause bc2 = new BooleanClause(query2, Occur.SHOULD);
//					BooleanQuery boolQuery = new BooleanQuery.Builder().add(bc1).add(bc2).build();
					// 查询字段
					// title 与 content 同时匹配
					String[] fields = {"title", "content"};
					MultiFieldQueryParser parser2 = new MultiFieldQueryParser(Version.LUCENE_43, fields, analyzer);
					
					Query boolQuery = parser2.parse(keyword);
					// 开始判断排序方式
					if (rank == 1) {
						topDocs = indexSearcher.search(boolQuery, 500);
						scorer = new QueryScorer(boolQuery);
					} else if (rank == 2) {
						// 按hotScore热度降序排列
						Sort sort = new Sort(new SortField("hotScore", SortField.Type.INT, true));
						topDocs = indexSearcher.search(boolQuery, 500, sort);
						scorer = new QueryScorer(boolQuery);
					} else if (rank == 3) {
						// 按时间倒序排列
						Sort sort = new Sort(new SortField("data_sort", SortField.Type.INT, true));
						topDocs = indexSearcher.search(boolQuery, 500, sort);
						scorer = new QueryScorer(boolQuery);
					} else {
						return null;
					}
				} else if (search_field == 1 || search_field == 2) {
					// 全文检索或标题检索
					QueryParser queryParser = null;
					if (search_field == 1) {// 全文检索
						queryParser = new QueryParser(Version.LUCENE_43, "content", analyzer);
					} else if (search_field == 2) {// 标题检索
						queryParser = new QueryParser(Version.LUCENE_43, "title", analyzer);
					}
					Query query = queryParser.parse(keyword);
					// 开始判断排序方式
					if (rank == 1) {
						topDocs = indexSearcher.search(query, 500);
						scorer = new QueryScorer(query);
					} else if (rank == 2) {
						// 按hotScore热度降序排列,true代表降序
						Sort sort = new Sort(new SortField("hotScore", SortField.Type.INT, true));
						topDocs = indexSearcher.search(query, 500, sort);
						scorer = new QueryScorer(query);
					} else if (rank == 3) {
						// 按时间倒序排列
						Sort sort = new Sort(new SortField("data_sort", SortField.Type.INT, true));
						topDocs = indexSearcher.search(query, 500, sort);
						scorer = new QueryScorer(query);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				// 子栏目搜索，垂直搜索。search_field 参数失效
				QueryParser queryParser1 = new QueryParser(Version.LUCENE_43, "category", analyzer);
				Query query1 = queryParser1.parse(category);
				QueryParser queryParser2 = new QueryParser(Version.LUCENE_43, "content", analyzer);
				Query query2 = queryParser2.parse(keyword);
				System.out.println("zilanmu");
				BooleanClause bc1 = new BooleanClause(query1, Occur.MUST);
				BooleanClause bc2 = new BooleanClause(query2, Occur.MUST);
				BooleanQuery boolQuery = new BooleanQuery();
				boolQuery.add(bc1);
				boolQuery.add(bc2);

				if (rank == 1) {
					topDocs = indexSearcher.search(boolQuery, 500);
					scorer = new QueryScorer(boolQuery);
				} else if (rank == 2) {
					// 按hotScore热度降序排列
					Sort sort = new Sort(new SortField("hotScore", SortField.Type.INT, true));
					topDocs = indexSearcher.search(boolQuery, 500, sort);
					scorer = new QueryScorer(boolQuery);
				} else if (rank == 3) {
					// 按时间倒序排列
					Sort sort = new Sort(new SortField("data_sort", SortField.Type.INT, true));
					topDocs = indexSearcher.search(boolQuery, 500, sort);
					scorer = new QueryScorer(boolQuery);
				} else {
					return null;
				}
			}

			totalDocs = topDocs.totalHits;
			System.out.println("查找到的文档共有：" + totalDocs);
			// 6.根据TopDocs获取ScoreDoc对象
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;

			// 高亮显示
			SimpleHTMLFormatter fors = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
			Highlighter highlighter = new Highlighter(fors, scorer);

			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
				// 7.根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = indexSearcher.doc(scoreDocs[i].doc);
				// 8.根据Document对象获取需要的值

				/*
				 * System.out.println("[" + i + "]" + document.get("title"));
				 * System.out.println(document.get("docURL")); System.out.println("score:" +
				 * scoreDocs[i].score);
				 * System.out.println("hotScore----"+document.get("hotScore"));
				 * System.out.println("category----"+document.get("category"));
				 * System.out.println("data----"+document.get("data"));
				 * System.out.println("image----"+document.get("image"));
				 */

				TokenStream tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(),
						topDocs.scoreDocs[i].doc, "title", analyzer);
				Fragmenter fragment = new SimpleSpanFragmenter(scorer);
				highlighter.setTextFragmenter(fragment);
				String highlight_title = highlighter.getBestFragment(tokenStream, document.get("title"));

				tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(), topDocs.scoreDocs[i].doc,
						"content", analyzer);
				String highlight_content = highlighter.getBestFragment(tokenStream, document.get("content"));

				// System.out.println(highlight_title+"-----"+highlight_content);

				if (highlight_title == null) {
					highlight_title = document.get("title");
				}
				if (highlight_content == null) {
					highlight_content = document.get("content");
				}
				Docs docs = new Docs(document.get("title"), highlight_title, document.get("docURL"),
						document.get("content"), highlight_content, document.get("category"), document.get("hotScore"),
						document.get("data"), document.get("author"), document.get("image"), totalDocs);
				docList.add(docs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (directoryReader != null) {
				try {
					directoryReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// System.out.println("文档docList" + docList);
		return docList;
	}
	
	/**
	 * 获取热点新闻
	 */
	@Test
	public void getHotNews() {
		// 参数
		String category = "凤凰";
		
		ArrayList<Docs> docList = new ArrayList<Docs>();
        DirectoryReader directoryReader = null;
        Analyzer analyzer =  new StandardAnalyzer(Version.LUCENE_43);// 标准分词
        TopDocs topDocs = null;
        try {
	        Directory directory = FSDirectory.open(new File(indexPath));
	        directoryReader = DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
    		QueryParser queryParser = new QueryParser(Version.LUCENE_43, "category", analyzer);
    		Query query = queryParser.parse(category);
    		Sort sort = new Sort(new SortField("hotScore", SortField.Type.INT, true));
    		topDocs = indexSearcher.search(query, 20, sort);
	    	
	        int totalDocs = topDocs.totalHits;
	        System.out.println("查找到的文档共有：" + totalDocs);
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

	        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                //7.根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDocs[i].doc);
                //8.根据Document对象获取需要的值
                
                System.out.println("[" + i + "]" + document.get("title"));
                System.out.println("content----"+document.get("content"));
                System.out.println(document.get("docURL"));
                System.out.println("score:" + scoreDocs[i].score);
                System.out.println("hotScore----"+document.get("hotScore"));
                System.out.println("category----"+document.get("category"));
                System.out.println("data----"+document.get("data_sort"));
                System.out.println("image----"+document.get("image"));
                
              
                Docs docs = new Docs( document.get("title"), document.get("content"), document.get("hotScore"),document.get("docURL"), totalDocs);
                docList.add(docs);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (directoryReader != null) {
                try {
                    directoryReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.out.println("文档docList" + docList);
//        return docList;
	}
	
	@Test
	public void tongTest() throws IOException{
		// 同义词词典
		String tongPath = "C:\\ProgramInstall\\eclipse\\workspace\\LuceneIR\\WebContent\\data\\tong.txt";
        Reader testInput = new StringReader("马上");
        Version ver = Version.LUCENE_43;
        Map<String, String> filterArgs = new HashMap<String, String>();
        filterArgs.put("luceneMatchVersion", ver.toString());
        filterArgs.put("synonyms", tongPath);
        filterArgs.put("expand", "true");
        SynonymFilterFactory factory = new SynonymFilterFactory(filterArgs);
        factory.inform(new FilesystemResourceLoader());
        
        WhitespaceAnalyzer whitespaceAnalyzer = new WhitespaceAnalyzer(ver);
        TokenStream ts = factory.create(whitespaceAnalyzer.tokenStream("someField", testInput));

        // 显示
        CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = ts.addAttribute(OffsetAttribute.class);
        ts.reset();
        while (ts.incrementToken())
        {
            String token = termAttr.toString();
            System.out.print(offsetAttribute.startOffset() + "-" + offsetAttribute.endOffset() + "[" + token + "] ");
        }
        System.out.println();
        ts.end();
        ts.close();
    }
	
	@Test
	public void fenciTest() {
		
		
	}
	
}
