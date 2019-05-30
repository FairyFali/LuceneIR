package com.ucas.luceneir.model;

public class Docs {
	private String title;
    private String content;
    private String htitle;	//关键字标红的title
    private String hcontent;	//关键字标红的content
    private String docURL;
    private String category;
    private int hotScore;	//热度评分
    private String hotScoreStr;		//热度评分,字符串
    private String data;		//时间，用于显示
    private int data_sort;	//时间，用于排序
    private String author;	//作者
    private String image;		//图片链接
    private String source;
    private String id;
    private int totalDocs;
    
    
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHtitle() {
		return htitle;
	}
	public void setHtitle(String htitle) {
		this.htitle = htitle;
	}
	public String getHcontent() {
		return hcontent;
	}
	public void setHcontent(String hcontent) {
		this.hcontent = hcontent;
	}
	public String getDocURL() {
		return docURL;
	}
	public void setDocURL(String docURL) {
		this.docURL = docURL;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getHotScore() {
		return hotScore;
	}
	public void setHotScore(int hotScore) {
		this.hotScore = hotScore;
	}
	public String getHotScoreStr() {
		return hotScoreStr;
	}
	public void setHotScoreStr(String hotScoreStr) {
		this.hotScoreStr = hotScoreStr;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getData_sort() {
		return data_sort;
	}
	public void setData_sort(int data_sort) {
		this.data_sort = data_sort;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getTotalDocs() {
		return totalDocs;
	}
	public void setTotalDocs(int totalDocs) {
		this.totalDocs = totalDocs;
	}
	
	public Docs(String title, String content, String htitle, String hcontent, String docURL, String category,
			int hotScore, String hotScoreStr, String data, int data_sort, String author, String image, int totalDocs) {
		super();
		this.title = title;
		this.content = content;
		this.htitle = htitle;
		this.hcontent = hcontent;
		this.docURL = docURL;
		this.category = category;
		this.hotScore = hotScore;
		this.hotScoreStr = hotScoreStr;
		this.data = data;
		this.data_sort = data_sort;
		this.author = author;
		this.image = image;
		this.totalDocs = totalDocs;
	}
	
	@Override
	public String toString() {
		return "Docs [title=" + title + ", content=" + ", htitle=" + htitle + ", hcontent=" + hcontent
				+ ", docURL=" + docURL + ", category=" + category + ", hotScore=" + hotScore + ", hotScoreStr="
				+ hotScoreStr + ", data=" + data + ", data_sort=" + data_sort + ", author=" + author + ", image="
				+ image + ", totalDocs=" + totalDocs + "]";
	}
	
	public Docs(String title, String highlight_title, String docURL, String content, String hcontent, String category, String hotScore, String data, String author, String image, int totalDocs) {
		super();
		this.title = title;
		this.content = content;
		this.htitle = highlight_title;
		this.hcontent = hcontent;
		this.docURL = docURL;
		this.category = category;
		this.hotScore = Integer.valueOf(hotScore);
		this.hotScoreStr = hotScore;
		this.data = data;
		this.author = author;
		this.image = image;
		this.totalDocs = totalDocs;
	}
	
	public Docs(String title, String content, String hotScore, String docURL, int totalDocs) {
		super();
		this.title = title;
		this.content = content;
		this.docURL = docURL;
		this.hotScoreStr = hotScore;
		this.totalDocs = totalDocs;
	}
	public Docs() {
		super();
	}
    
	
    
}
