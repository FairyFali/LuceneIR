<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ucas.luceneir.model.Docs" %>
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
    <title>EveryNews-搜索</title>
    <link type="text/css" rel="stylesheet" href="css/bootstrap.min.css">
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
    .active a{
    	color: red;
    }
    .active a:hover{
    	cursor:pointer;
    }
    </style>
  </head>
  <body>
  <div>
	<!--搜索框-->
    <div style="padding-bottom: 0; margin-bottom: 0;" class="left">
        <form action="search" method="get" class="form-horizontal" style="padding-left: 15px;">
            <div class="form-group" style="margin-bottom: 0;">
                <label class="col-sm-2 control-label" style="padding: 0;">
                    <a href="index"><img src="img/ico.png" class="img-responsive" alt=""></a>
                </label>
                <div class="col-sm-6">
                    <div class="input-group" style="margin-top:16px;">
                        <input type="text" name="query" value="${query }" class="form-control" placeholder="请输入您的检索词" style="line-height: 30px; height: 44px;">
                        <div class="input-group-btn">
                            <button class="btn btn-default" style="height: 44px; width: 90px; background-color: #317EF3; color: white;">
                                搜索
                                <i class="glyphicon glyphicon-ok"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <!--子栏目查询-->
    <div class="" style="margin: 0; background-color: #eee; padding-top: 15px; padding-bottom: 0; margin-bottom: 2px;">
        <div class="row" style="padding-bottom: 0; ">
            <div class="col-sm-2 col-xs-0"></div>
            <nav class="navbar navbar-text col-sm-6 visible-sm visible-lg visible-md" style="padding: 0; margin-top: 0; min-height: 0px !important;">
                <div class="container" style="height: 20px;">
                    <ul class="nav navbar-nav" style="height: 20px; margin-bottom: 0;">
                        <li <c:if test="${category=='all' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=all&&pageNow=1" style="height: 20px;padding-top: 0px;margin-bottom: 0;">全网</a></li>
                        <li <c:if test="${category=='新浪' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=新浪&&pageNow=1" style="height: 20px;padding-top: 0;">新浪</a></li>
                        <li <c:if test="${category=='网易' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=网易&&pageNow=1" style="height: 20px;padding-top: 0;">网易</a></li>
                        <li <c:if test="${category=='腾讯' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=腾讯&&pageNow=1" style="height: 20px;padding-top: 0;">腾讯</a></li>
                        <li <c:if test="${category=='搜狐' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=搜狐&&pageNow=1" style="height: 20px;padding-top: 0;">搜狐</a></li>
                        <li <c:if test="${category=='凤凰' }">class="active"</c:if>><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=凤凰&&pageNow=1" style="height: 20px;padding-top: 0;">凤凰</a></li>
                    </ul>
                </div>
            </nav>
        </div>
    </div>
    <!--检索结果-->
    <div class="">
        <div class="row" style="padding-bottom: 0;" style="padding-left:20px;">
            <div class="col-sm-2 col-xs-0"></div>
            <div class="col-sm-6 col-xs-8" style="padding-left: 30px; line-height: 30px;">EveryNews为您找到相关结果约<fmt:formatNumber value="${totalDocs }" pattern="#,#00"/>个，用时<fmt:formatNumber value="${time }" pattern="#0.00"/>s。</div>
            <div class="col-sm-4 col-xs-4"><button class="btn-link" data-toggle="collapse" data-target="#content" style="color: #333;">高级检索</button></div>
        </div>
    </div>
    <!--高级检索-->
    <!--选项-->
    <div id="content" class="collapse">
        <div class="row" style="padding-left:20px ;">
            <div class="col-sm-2"></div>
            <div class="btn-group col-sm-6" style="">
                <a class="btn btn-default <c:if test='${search_field==0 }'>active</c:if>" href="search?query=${query }&&rank=${rank }&&field=0&&category=${category }&&pageNow=1">全文检索</a>
                <a class="btn btn-default <c:if test='${search_field==2 }'>active</c:if>" href="search?query=${query }&&rank=${rank }&&field=2&&category=${category }&&pageNow=1">标题检索</a>
                <a class="btn btn-default <c:if test='${search_field==1 }'>active</c:if>" href="search?query=${query }&&rank=${rank }&&field=1&&category=${category }&&pageNow=1">内容检索</a>
                <div class="btn-group">
                    <button class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                        排序方式
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu">
                        <li><a href="search?query=${query }&&rank=1&&field=${search_field }&&category=${category }&&pageNow=1">相关度排序</a></li>
                        <li><a href="search?query=${query }&&rank=3&&field=${search_field }&&category=${category }&&pageNow=1">时间排序</a></li>
                        <li><a href="search?query=${query }&&rank=2&&field=${search_field }&&category=${category }&&pageNow=1">热点排序</a></li>
                    </ul>
                </div>
            </div>
        </div>

    </div>
    
    <!--新闻-->
    <div class="" style="margin-top: 20px;">
    	<c:forEach items="${docList }" var="d">
        <div class="row" style="padding-left:20px;">
            <div class="col-sm-2"></div>
            <div class="row col-sm-6" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                <div class="col-sm-12 row">
                	<a href="${d.docURL }" class="col-sm-9" style="color: #317EF3;font-weight: bold; font-size: 20px;">${d.htitle }</a>
                	<a href="search?query=${d.title }&&rank=1&&field=${search_field }&&category=${category }&&pageNow=1" class="btn btn-link similarity_button col-sm-2" style="cursor: pointer;" role="button">相似新闻 <span class="glyphicon glyphicon-search "><span></a>
                	
                </div>
                <div class="col-sm-12" style="">
                    <p style="color: #333333;">
                        <span>${d.data }</span>-<span>${d.hcontent }...</span>
                        <p>
                            <label for="" class="label label-success">${d.category }</label>
                            <label for="" class="label label-danger">${d.author }</label>
                            <label for="" class="label label-warning">热度：${d.hotScoreStr }</label>
                            <a href="${d.docURL }" style="height:16px;display: inline-block; width: 80%; overflow: hidden;">${d.docURL }</a>
                        </p>
                    </p>
                </div>
                <div style="padding-left: 12px;">
                    <img src="${d.image }" class="img-responsive img-rounded" alt="">
                </div>
            </div>
        </div>
        </c:forEach>
    </div>
    
    
    <div class="main">
      <div class="row">
        <div class="col-sm-2"></div>
        <div class="col-sm-8 blog-main">
          <nav>
            <ul class="pagination">
              <li><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=${category }&&pageNow=1">首页</a></li>

	            <c:if test="${pageNow != 1}">
	            	<li><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=${category }&&pageNow=${pageNow-1}">上一页</a></li>
	            </c:if>
	            <c:if test="${pageNow-2 > 1}">
	            	<li class="disabled"><a href="#">···</a></li>
	            </c:if>
	            
	            <c:set var="b" value="${pageNow-2>1?pageNow-2:1 }"></c:set>
	            <c:forEach var="i" begin="${b }" end="${pageNow+3 }">
	            	<c:if test="${i!=pageNow && i<=pageCount }">
	            		<li><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=${category }&&pageNow=${i}">${i }</a></li>
		            </c:if>
		            <c:if test="${i==pageNow }">
		            	<li class="active"><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=${category }&&pageNow=${i}">${i }</a></li>
		            </c:if>
	            </c:forEach>
	            <c:if test="${pageNow != pageCount }">
	            	<li class="disabled"><a href="#">···</a></li>
	            	<li><a href="search?query=${query }&&rank=${rank }&&field=${search_field }&&category=${category }&&pageNow=${pageNow+1}">下一页</a></li>
	            </c:if>
            </ul>
          </nav>
        </div><!-- /.blog-main -->
        
      </div><!-- /.row -->
    </div><!-- /.container -->
      <hr>
      <footer>
        <p> 
          <i>Design & Power by</i> 
          <a href="http://fairyFali.github.io">王发利</a> 孔哲 刘锦 &copy; 2019
        </p>
      </footer>
    </div> <!-- /container -->
    </div>
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
