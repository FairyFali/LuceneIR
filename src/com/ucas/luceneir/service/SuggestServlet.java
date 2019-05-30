package com.ucas.luceneir.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.util.Version;

import com.google.gson.Gson;

/**
 * Servlet implementation class SuggestServlet
 */
@WebServlet("/SuggestServlet")
public class SuggestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String tongPath;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SuggestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
        tongPath = config.getServletContext().getRealPath("/data/tong.txt");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
        String c = request.getParameter("word");
        System.out.println(c);
        List<String> tongList = tong(c);
        Gson gson = new Gson();
//        ArrayList<String> list = new ArrayList<String>();
//        list.add("abc");
//        list.add("wangfali");
        String result = gson.toJson(tongList);
        response.getWriter().append(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private List<String> tong(String word) throws IOException{
		// 同义词词典
        Reader testInput = new StringReader(word);
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
        List<String> tongList = new ArrayList<String>();
        CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = ts.addAttribute(OffsetAttribute.class);
        ts.reset();
        while (ts.incrementToken())
        {
            String token = termAttr.toString();
            tongList.add(token);
            System.out.print(offsetAttribute.startOffset() + "-" + offsetAttribute.endOffset() + "[" + token + "] ");
        }
        System.out.println();
        ts.end();
        ts.close();
        return tongList;
    }

}
