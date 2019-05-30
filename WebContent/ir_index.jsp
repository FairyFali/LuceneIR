<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- total index file count is 100004 -->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="com.ucas.luceneir.model.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EveryNews信息检索系统</title>
	<!-- load stylesheets -->
	<!-- Bootstrap style -->
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<style>
	    a{color: #333; font-weight: bold;}
	    body{
	        font-family:'';}
	</style>
</head>
<body>
<div style="width: 80%; margin: 0 auto;">
<!--导航栏-->
<nav class="navbar navbar-default" role="navigation" style="">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target="#example-navbar-collapse">
                <span class="sr-only">切换导航</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#" style="color: #000 !important;">EveryNews首页</a>
        </div>
        <div class="collapse navbar-collapse" id="example-navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#" style="color: #000 !important;">新闻</a></li>
                <li><a href="#" style="color: #000 !important;">经济</a></li>
                <li><a href="#" style="color: #000 !important;">体育</a></li>
                <li><a href="#" style="color: #000 !important;">电视剧</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" style="color: #000 !important;">
                        更多 <b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="#">综艺</a></li>
                        <li><a href="#">科教</a></li>
                        <li><a href="#">旅游</a></li>
                        <li class="divider"></li>
                        <li><a href="#">科教</a></li>
                        <li class="divider"></li>
                        <li><a href="#">直播</a></li>
                    </ul>
                </li>
            </ul>
            <ul class='nav navbar-nav navbar-right'>
                <li><a href="#">登录</a></li>
            </ul>
        </div>
    </div>
</nav>
<!--输入框-->
<div>
    <img src="img/biaoyu.gif" class="center-block" alt="">
    <form action="search" method="get" class="form-horizontal row">
        <div class="form-group">
            <label class="col-sm-2 col-xs-offset-2 control-label" style="padding: 0;">
                <a href="###"><img src="img/ico.png" class="img-responsive" alt=""></a>
            </label>
            <div class="col-sm-6">
                <div class="input-group">
                    <input type="text" name="query" class="form-control" placeholder="请输入您的检索词" style="line-height: 30px; height: 44px;">
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
 <!--热点新闻展示-->
 <div style="width: 740px;" class="center-block row">
     <ul class="nav nav-tabs" style="color: #333;">
         <li class="active"><a href="#allHotNews" data-toggle="tab">全部</a></li>
         <li><a href="#wangyi" data-toggle="tab">网易</a></li>
         <li><a href="#tengxun" data-toggle="tab">腾讯</a></li>
         <li><a href="#xinlang" data-toggle="tab">新浪</a></li>
         <li class="dropdown">
             <a href="#" data-toggle="dropdown">更多 <span class="caret"></span></a>
             <ul class="dropdown-menu">
                 <li><a href="#souhu" data-toggle="tab">搜狐</a></li>
                 <li><a href="#fenghuang" data-toggle="tab">凤凰</a></li>
             </ul>
         </li>
     </ul>

     <div class="tab-content" style="padding: 10px;border: 1px #E9E9E9 solid; border-top: none;">
         <div id="allHotNews" class="tab-pane active in fade">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                     <img src="img/img1.png" alt="" class="media-object">
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="https://baijiahao.baidu.com/s?id=1634835057023393404&wfr=spider&for=pc" style="color: black;">1599元起！苹果新iPod touch登场，但毫无购买欲</a></div>
                     <p style="color: #999;"><span>雷科技</span> <span>05-28 21:55</span></p>
                 </div>
             </div>
         </div>
         <div  id="wangyi" class="tab-pane fade">
    		<c:forEach items="${wangyiHotNews }" var="n">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                 	<c:if test="${!empty n.image && n.image != 'None'}">
                 		<img src="${n.image }" alt="" style="width:200px;" class="media-object img-responsive img-thumbnail">
                 	</c:if>
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="${n.docURL}" style="color: black;">${n.title }</a></div>
                     <p style="color: #999;"><span>${n.category }</span> <span>${n.data }</span></p>
                 </div>
             </div>
             
			</c:forEach>
         </div>
         <div id="tengxun" class="tab-pane fade">
         	<c:forEach items="${tengxunHotNews }" var="n">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                     <c:if test="${!empty n.image && n.image != 'None'}">
                 		<img src="${n.image }" alt="" style="width:200px;" class="media-object img-responsive img-thumbnail">
                 	</c:if>
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="${n.docURL}" style="color: black;">${n.title }</a></div>
                     <p style="color: #999;"><span>${n.category }</span> <span>${n.data }</span></p>
                 </div>
             </div>
             
			</c:forEach>
		 </div>
         <div id="xinlang" class="tab-pane fade">
             <c:forEach items="${xinlangHotNews }" var="n">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                     <c:if test="${!empty n.image && n.image != 'None'}">
                 		<img src="${n.image }" alt="" style="width:200px;" class="media-object img-responsive img-thumbnail">
                 	</c:if>
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="${n.docURL}" style="color: black;">${n.title }</a></div>
                     <p style="color: #999;"><span>${n.category }</span> <span>${n.data }</span></p>
                 </div>
             </div>
             
			</c:forEach>
		</div>
         <div id="souhu" class="tab-pane fade">
			<c:forEach items="${souhuHotNews }" var="n">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                     <c:if test="${!empty n.image && n.image != 'None'}">
                 		<img src="${n.image }" alt="" style="width:200px;" class="media-object img-responsive img-thumbnail">
                 	</c:if>
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="${n.docURL}" style="color: black;">${n.title }</a></div>
                     <p style="color: #999;"><span>${n.category }</span> <span>${n.data }</span></p>
                 </div>
             </div>
             
			</c:forEach>
		 </div>
         <div id="fenghuang" class="tab-pane fade">
             <c:forEach items="${fenghuangHotNews }" var="n">
             <div class="media" style="padding: 7px; border-bottom: 1px #E9E9E9 solid;">
                 <div class="media-left media-middle">
                     <c:if test="${!empty n.image && n.image != 'None'}">
                 		<img src="${n.image }" alt="" style="width:200px;" class="media-object img-responsive img-thumbnail">
                 	</c:if>
                 </div>
                 <div class="media-body" style="line-height: 50px;">
                     <div class="media-heading" style="font-weight: bold; font-size: 20px; "><a href="${n.docURL}" style="color: black;">${n.title }</a></div>
                     <p style="color: #999;"><span>${n.category }</span> <span>${n.data }</span></p>
                 </div>
             </div>
             
			</c:forEach>
         </div>
     </div>
 </div>

<footer style="margin-top: 200px; margin-bottom: 30px;">
    <div style="text-align: center;">
        <p class="lh">Design by
            <a class="sethome" href="fairyfali.github.io" target="_blank"> 王发利 </a> 刘锦 孔哲
        </p>
        <span class="copyright-text"><span>©2019&nbsp;EveryNews&nbsp;</span><a href="#" target="_blank">使用EveryNews前必读</a>&nbsp; </span>
    </div>
</footer>


</div>

	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script>
           
    </script>
</body>
</html>