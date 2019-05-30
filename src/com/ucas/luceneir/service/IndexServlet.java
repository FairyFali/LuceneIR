package com.ucas.luceneir.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ucas.luceneir.model.Docs;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet("/IndexServlet")
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String indexPath;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexServlet() {
        super();
    }
    
    

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		indexPath = config.getServletContext().getRealPath("/index");
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("Hello World");
		System.out.println(request.getSession().getServletContext().getRealPath("/"));
		String indexPathStr = request.getSession().getServletContext().getRealPath("/index");
		System.out.println(indexPathStr);
		// 返回热点新闻
		ArrayList<Docs> wangyiHotNews = getHotNews("网易", 5);
		ArrayList<Docs> xinlangHotNews = getHotNews("新浪", 5);
		ArrayList<Docs> fenghuangHotNews = getHotNews("凤凰", 5);
		ArrayList<Docs> souhuHotNews = getHotNews("搜狐", 5);
		ArrayList<Docs> tengxunHotNews = getHotNews("腾讯", 5);
		// 添加到request中
		request.setAttribute("wangyiHotNews", wangyiHotNews);
		request.setAttribute("xinlangHotNews", xinlangHotNews);
		request.setAttribute("fenghuangHotNews", fenghuangHotNews);
		request.setAttribute("souhuHotNews", souhuHotNews);
		request.setAttribute("tengxunHotNews", tengxunHotNews);
		
		request.getRequestDispatcher("ir_index.jsp").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * 获取指定来源的热点新闻
	 * @param category
	 * @return
	 */
	public ArrayList<Docs> getHotNews(String category, int num) {
		
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
    		topDocs = indexSearcher.search(query, num, sort);
	    		

	        int totalDocs = topDocs.totalHits;
	        System.out.println(category + "查找到的文档共有：" + totalDocs);
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

	        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                //7.根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDocs[i].doc);
                //8.根据Document对象获取需要的值
                
                System.out.println("[" + i + "]" + document.get("title"));
//                System.out.println("content----"+document.get("content"));
//                System.out.println(document.get("docURL"));
//                System.out.println("score:" + scoreDocs[i].score);
//                System.out.println("hotScore----"+document.get("hotScore"));
//                System.out.println("category----"+document.get("category"));
//                System.out.println("data----"+document.get("data_sort"));
//                System.out.println("image----"+document.get("image"));
                
              
                Docs docs = new Docs( document.get("title"), document.get("content"), document.get("hotScore"),document.get("docURL"), totalDocs);
                docs.setCategory(document.get("category"));
                docs.setImage(document.get("image"));
                docs.setData(document.get("data"));
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
        return docList;
	}

}
