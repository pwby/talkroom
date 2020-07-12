<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2020/5/8
  Time: 16:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>动态页面</title>
    <style type="text/css">
        div{
            border: black solid 1px;
            font-style: italic;
            font-size: 20px;
            color:yellow;
            background: black;
        }
    </style>
</head>
<body >
<div style="height: 600px;width:400px;margin: 0 auto;">
    <div align="center" style="width: 400px; height: 50px;font-style: italic;font-size: 30px;"><span>记录生活</span></div>
    <div>
        <table  border="1" cellspacing="0" style="height: 500px">
            <tr>
                <td >
                    <a href="postArticle.html" target="main" style="background: yellow; font-size: 20px">发表文章</a>

                </td>
                <td rowspan="2">
                    <iframe src="article.jsp" name="main" height="500px" style="background: white"></iframe>
                </td>
            </tr>
            <tr>
                <td>
                    <a href="article.jsp" target="main" style="background: yellow;font-size: 20px;">文章列表</a>
                </td>

            </tr>
        </table>
    </div>
</div>
</body>
</html>
