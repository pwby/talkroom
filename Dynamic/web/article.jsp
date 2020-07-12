<%@ page import="service.ArticleService" %>
<%@ page import="service.impl.ArticleServiceImpl" %>
<%@ page import="pojo.Article" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2020/5/8
  Time: 15:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style type="text/css">
        div{
            border:  solid 1px;
            font-size: 15px;
            color:black;
            background:white;
            font-style: italic;

        }
    </style>
</head>
<body style="height:500px;overflow-y: scroll">
  <%
      ArticleService articleService = new ArticleServiceImpl();
      List<Article> list=articleService.loadArticle();
  %>

  <%  if(list.size()>0) {%>
  <% for (int i=list.size()-1;i>=0;i--){ Article article = list.get(i);%>
  <div style="word-break: break-all" >

      <span style="font-style: italic;color:black"><%=article.getTime()%>&nbsp;&nbsp;</span>
      <span style="color:gray;font-style: normal">by>>><%=article.getPoster()%><br/></span>

      <%=article.getContent()%>

  </div>
  <br/>
  <% }%>

  <%}else{ %>
     <img src="image/none.jpg" width="280" height="400"><br/><br/>
  <span  style="font-size: 20px;text-align: center;display:block;">暂无动态</span>
   <% }%>
</body>
</html>
