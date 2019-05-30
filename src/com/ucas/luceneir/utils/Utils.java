package com.ucas.luceneir.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ucas.luceneir.model.Docs;

public class Utils {
	
	/***
	 * 从xml中获得所有的doc
	 * @param path
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static ArrayList<Docs> getDocsFromXml(String path) 
			throws ParserConfigurationException, SAXException, IOException {
		// 获得docs
		ArrayList<Docs> docs = new ArrayList<Docs>();
		// 判断后缀，一定要有后缀
		if(!"xml".equals(path.substring(path.lastIndexOf('.')+1))) {
			return docs;
		}
		// 随机种子
		Random r = new Random();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		NodeList itemList = document.getElementsByTagName("item");
		// 类别节点
		Node categoryNode = document.getElementsByTagName("category").item(0);
		for(int i=0;i<itemList.getLength();i++) {
			Element item = (Element) itemList.item(i);
			// 目标对象
			Docs doc = new Docs();
			// 获得标题
			Node title = item.getElementsByTagName("title").item(0);
			doc.setTitle(title.getTextContent());
			// 设置链接
			Element linkElement = (Element) item.getElementsByTagName("link").item(0);
            doc.setDocURL(linkElement.getTextContent());
            // 设置作者
            Element authorElement = (Element) item.getElementsByTagName("author").item(0);
            doc.setAuthor(authorElement.getTextContent());
            // 图片链接
            Element imageElement = (Element) item.getElementsByTagName("image").item(0);
            doc.setImage(imageElement.getTextContent());
            // 设置内容
            Element descriptionElement = (Element) item.getElementsByTagName("description").item(0);
            if(descriptionElement != null && descriptionElement.getTextContent() != null) {
            	doc.setContent(descriptionElement.getTextContent());
            } else {
            	continue;
            }
			//随机赋值给该doc一个热度
			doc.setHotScore(r.nextInt(10000));
			//System.out.println("hotScore----"+docs.getHotScore());
			
			//设置时间，data用于显示，data_sort用于排序
			Element dataElement = (Element) item.getElementsByTagName("pubDate").item(0);
			//爬虫爬的日期格式有误，全部设为2000-01-01
			int temp_data_sort = 20000101;
			doc.setData("2000-01-01");
			// 如果时间符合格式，使用正确格式的时间
			if(dataElement.getTextContent().length()==10) {
			    doc.setData(dataElement.getTextContent());
			    String[] temp_data = dataElement.getTextContent().split("-");
			    if(temp_data.length==3) {
			    	temp_data_sort = Integer.parseInt(temp_data[0])*10000+Integer.parseInt(temp_data[1])*100+Integer.parseInt(temp_data[2]);   
			    }
			}
			doc.setData_sort(temp_data_sort);
			// 来源
			doc.setCategory(categoryNode.getTextContent());
			// 添加到docs
			docs.add(doc);
		}
		return docs;
	}

	/**
	 * 返回路径下的所有文件名
	 * @param path
	 * @return
	 */
	public static ArrayList<String> getFileNameUtil(String path) {
		ArrayList<String> fileNameList = new ArrayList<String>();
		File pathFile = new File(path);
		File[] listFiles = pathFile.listFiles();
		if(listFiles == null)
			return fileNameList;
		for (File file : listFiles) {
			// 假设这里面都是文件
			fileNameList.add(file.getName());
		}
		
		return fileNameList;
	}
	
}
