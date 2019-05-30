<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ucas.luceneir.model.Docs" %>
<%
    String query = (String) request.getAttribute("query");
    List<Docs> list = (List<Docs>) request.getAttribute("docList");
    int totalDoc = (Integer)request.getAttribute("totalDocs");
    double time = Double.parseDouble(request.getAttribute("time").toString());
    int pageNow = (Integer) request.getAttribute("pageNow");
    int pageCount = (Integer) request.getAttribute("pageCount");
    int rank = (Integer) request.getAttribute("rank");
    String category = (String)request.getAttribute("category");
    int field = (Integer)request.getAttribute("search_field");
%>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="favicon.ico">
    <title><%=query%>-新闻搜索</title>
    <!-- Bootstrap -->
    <link href="style/bootstrap.min.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
      @font-face {
        font-family: 'Glyphicons Halflings';
        src: url('style/fonts/glyphicons-halflings-regular.eot');
        src: url('style/fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'), url('style/fonts/glyphicons-halflings-regular.woff') format('woff'), url('style/fonts/glyphicons-halflings-regular.ttf') format('truetype'), url('style/fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
      }
      .glyphicon {
        position: relative;
        top: 1px;
        display: inline-block;
        font-family: 'Glyphicons Halflings';
        -webkit-font-smoothing: antialiased;
        font-style: normal;
        font-weight: normal;
        line-height: 1;
        -moz-osx-font-smoothing: grayscale;
      }
     .main{
        padding-top: 20px;
     }
    /* Sidebar modules for boxing content */
    .sidebar-module {
      padding: 15px;
      margin: 0 -15px 15px;
    }
    .sidebar-module-inset {
      margin: 1px;
      background-color: #f5f5f5;
      border-radius: 4px;
    }
    .sidebar-module-inset p:last-child,
    .sidebar-module-inset ul:last-child,
    .sidebar-module-inset ol:last-child {
      margin-bottom: 0;
    }
    .navbar-form .form-control {
        width: 400px;    
    }
    /*
     * Masthead for nav
     */
    .blog-masthead {
      padding-top: 50px;
      background-color: #337ab7;
      -webkit-box-shadow: inset 0 -2px 5px rgba(0,0,0,.1);
              box-shadow: inset 0 -2px 5px rgba(0,0,0,.1);
    }
    /* Nav links */
    .blog-nav-item {
      position: relative;
      display: inline-block;
      padding: 10px;
      font-weight: 500;
      color: #cdddeb;
    }
    .blog-nav-item:hover,
    .blog-nav-item:focus {
      color: #fff;
      text-decoration: none;
    }
    /* Active state gets a caret at the bottom */
    .blog-nav .active {
      color: #fff;
    }
    .blog-nav .active:after {
      position: absolute;
      bottom: 0;
      left: 50%;
      width: 0;
      height: 0;
      margin-left: -5px;
      vertical-align: middle;
      content: " ";
      border-right: 5px solid transparent;
      border-bottom: 5px solid;
      border-left: 5px solid transparent;
    }
    .panel-body a{
      color: black;
      text-decoration: none;
    }
    .panel-body img{
      margin-top:10px;
      width:100%;
    }
    .similarity_button{
      float: right;
    }
    .news-title{
      line-height: 2;
      background: #337ab7;
      border: none;
    }
    footer{
      text-align: center;
    }
    .h1, .h2, .h3, .h4, .h5, .h6, h1, h2, h3, h4, h5, h6,.popover-content, .panel-title {
      color: black;
    }
    .popover{
      max-width: 800px;
    }
    </style>
  </head>
  <body>
    <nav class="navbar navbar-default navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" style="color: #337ab7; font-weight: bold; font-size: 25px; font-style: italic;" href="index">EveryNews <span style="color: black">新闻搜索</span></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <form  action="search" method="get" class="navbar-form navbar-left">
            <div class="form-group" style="height: 30px; overflow: visible;">
              <input type="text"  name="query" id="query" value="<%=query%>" class="form-control">
              <div id="suggest" style="background-color: white;"></div>
            </div>
            <button type="submit" class="btn btn-primary">搜索</button>
          </form>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>
    <div class="blog-masthead">
      <div class="container">
        <nav class="blog-nav">
        
        <%	if(category.equals("all")){ %>	
        <a class="blog-nav-item active" href="#">全部</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=all&&pageNow=1">全部</a>
        <% }%>
        
        <%	if(category.equals("网易")){ %>
        <a class="blog-nav-item active" href="#">网易</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=网易&&pageNow=1">网易</a>
        <% }%>
        
        <%	if(category.equals("搜狐")){ %>
        <a class="blog-nav-item active" href="#">搜狐</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=搜狐&&pageNow=1">搜狐</a>
        <% }%>
        
        <%	if(category.equals("新浪")){ %>
        <a class="blog-nav-item active" href="#">新浪</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=新浪&&pageNow=1">新浪</a>
        <% }%>
        
        <%	if(category.equals("腾讯")){ %>
        <a class="blog-nav-item active" href="#">腾讯</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=腾讯&&pageNow=1">腾讯</a>
        <% }%>
        
        <%	if(category.equals("凤凰")){ %>
        <a class="blog-nav-item active" href="#">凤凰</a>
        <% } else{%>
        <a class="blog-nav-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=凤凰&&pageNow=1">凤凰</a>
        <% }%>
        
        </nav>
      </div>
    </div>
    <!-- Main jumbotron for a primary marketing message or call to action -->
    <!--div class="jumbotron">
      <div class="container">
        <h1>Hello, world!</h1>
        <p>This is a template for a simple marketing or informational website. It includes a large callout called a jumbotron and three supporting pieces of content. Use it as a starting point to create something more unique.</p>
        <p><a class="btn btn-primary btn-lg" href="#" role="button">Learn more &raquo;</a></p>
      </div>
    </div-->
    <div class="container main">
      <div class="blog-header">
        <p class="lead blog-description">共检索到<span style="color:red;"><%=totalDoc%></span>条新闻，用时<span style="color:red;"><%=time%></span>秒</p>
      </div>
      <div class="row">
        <div class="col-sm-8 blog-main">
        <%
            if (list.size() > 0) {
                Iterator<Docs> iter = list.iterator();
                Docs docs;
                while (iter.hasNext()) {
                    docs = iter.next();
        %>
          <div class="panel panel-primary">
            <div class="panel-heading">
              <div class="panel-title" style="color: white;">
                <button type="button" class="news-title"  data-toggle="popover" data-placement="bottom" title="<%=docs.getTitle()%>"  data-content="<%=docs.getContent()%>"><%=docs.getHtitle()%></button>
                <a href="search?query=<%=docs.getTitle()%>&&rank=1&&field=<%=field%>&&category=<%=category%>&&pageNow=1" class="btn btn-primary similarity_button" role="button">相似新闻 <span class="glyphicon glyphicon-search "><span></a>
              </div>
              <div style="clear: both;"></div>
            </div>
            <div class="panel-footer">
              <span class="label label-success left"><%=docs.getData()%></span>
              <span class="label label-default left"><%=docs.getAuthor()%></span>
              <span class="label label-warning left">热度：<%=docs.getHotScoreStr()%></span>
              <span class="label label-danger left"><%=docs.getCategory()%></span>
            </div>
            <div class="panel-body">
              <a href="<%=docs.getDocURL()%>" >
              <%=docs.getHcontent().length() > 800 ? docs.getHcontent().substring(0, 800) : docs.getHcontent()%>
              </a>
			<%
              if(docs.getImage().length()>4){
			%>
				<img src="<%=docs.getImage() %>"/>
			<%
              }
			%>
            </div>
          </div>
		<%
                }
            }
        %>
          <nav>
            <ul class="pagination">
              <li><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=1">首页</a></li>
	            <%
	                if (pageNow !=1){
	            %>
	            <li><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=<%=pageNow - 1%>">上一页</a></li>
	            <%
	                }
	            %>
	            <%
	                if (pageNow-2 >1){
	            %>
	            <li class="disabled"><a href="#">···</a></li>
	            <%
	                }
	            %>
	            <%
	                for (int i = Math.max(pageNow-2,1); i <= pageNow+3; i++) {
	                		if(i!=pageNow&&i<=pageCount){
	            %>
	            <li><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=<%=i%>"><%=i%></a></li>
	            <%		
	                		}if(i==pageNow){//active
	            %>
	            <li class="active"><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=<%=i%>"><%=i%></a></li>
	            <%		}
	                }
	            %>
	            <%
	                if (pageNow != pageCount){
	            %>
	            <li class="disabled"><a href="#">···</a></li>
	            <li><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=<%=pageNow+1%>">下一页</a></li>
	            <!--li><a href="search?query=<%=query%>&&rank=<%=rank%>&&field=<%=field%>&&category=<%=category%>&&pageNow=<%=pageCount%>">末页</a></li-->
	            <%
	                }
	            %>
            </ul>
          </nav>
        </div><!-- /.blog-main -->
        <div class="col-sm-3 col-sm-offset-1 blog-sidebar">
          <div class="list-group">
            <a href="#" class="list-group-item active">
              检索方式
            </a>
	        <%	if(field==0){ %>
	        <a href="#" class="list-group-item disabled">全文检索</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=0&&category=<%=category%>&&pageNow=1">全文检索</a>
	        <% }%>
	        <%	if(field==2){ %>
	        <a href="#" class="list-group-item disabled">标题检索</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=2&&category=<%=category%>&&pageNow=1">标题检索</a>
	        <% }%>
	        <%	if(field==1){ %>
	        <a href="#" class="list-group-item disabled">内容检索</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=<%=rank%>&&field=1&&category=<%=category%>&&pageNow=1">内容检索</a>
	        <% }%>
          </div>
          <div class="list-group">
            <a href="#" class="list-group-item active">
              排序方式
            </a>
            <%	if(rank==1){ %>
	        <a href="#" class="list-group-item disabled">相关度排序</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=1&&field=<%=field%>&&category=<%=category%>&&pageNow=1">相关度排序</a>
	        <% }%>
	        <%	if(rank==3){ %>
	        <a href="#" class="list-group-item disabled">时间排序</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=3&&field=<%=field%>&&category=<%=category%>&&pageNow=1">时间排序</a>
	        <% }%>
	        <%	if(rank==2){ %>
	        <a href="#" class="list-group-item disabled">热度排序</a>
	        <% } else{%>
	        <a class="list-group-item" href="search?query=<%=query%>&&rank=2&&field=<%=field%>&&category=<%=category%>&&pageNow=1">热度排序</a>
	        <% }%>
          </div>
        </div><!-- /.blog-sidebar -->
      </div><!-- /.row -->
    </div><!-- /.container -->
      <hr>
      <footer>
        <p> 
          <i>Design & Power by</i> 
          <a href="http://fairFali.github.io">王发利</a> 孔哲 刘锦 &copy; 2019
        </p>
        <form>
        	<input type="text" name="cc">
        	<input type="button" name="提交" id="sub">
        </form>
      </footer>
    </div> <!-- /container -->
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="style/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="style/bootstrap.min.js"></script>
    <script type="text/javascript">

	    function submit_category_form() {
			var form = $('#category_form');
			form.submit();
		}
	    
      $(function () {
    	  
    	  
    	$('[data-toggle="popover"]').popover();
        
        $("input[name=query]").on('blur', function(e){
        //$("#sub").click(function(){
        	var query = $("input[name=query]").val();
        	//$("#suggest").empty();
            $.post("suggest",
                {"word":$("input[name=query]").val()},
              	function(data,textStatus){
                   console.log(data[0]);
                   //alert(data);
                   if(data.length > 1){
	                   for (var i = 0; i < Math.min(data.length, 5); i++) {
	                	   if($("input[name=query]").val() == data[i])continue;
							$("#suggest").append("<p style='background-color:; padding-left:12px;' id='sug'"+i+" class='sug-click'>" + data[i] + "</p>");
					   }
                   }
                },
              "json"   
            );
         });
        // 离开
         /* $("input[name=query]").on('blur',function(){
        	 $("#suggest").empty();
         } */
        
        $("#suggest").on("click",".sug-click",function(){
        	var value = $(this).html();
        	$("input[name=query]").val(value);
        	$("#suggest").empty();
		});
      })
      
    </script>
  </body>
</html>
