package web;


import pojo.Article;
import service.ArticleService;
import service.impl.ArticleServiceImpl;
import utils.TimeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaveArticleServlet extends HttpServlet {

    private ArticleService articleService = new ArticleServiceImpl() ;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Content-Type","text/html,charset=utf-8");
        //  1、获取请求的参数
        String poster = req.getParameter("poster");

        String content = req.getParameter("content");

        // 2,处理业务
        if(content!=null && content.length()!=0) {
            articleService.saveArticle(new Article(poster, TimeUtils.getTime(), content));
            req.getRequestDispatcher("/loadArticleServlet").forward(req, resp);

        }else{
            req.getRequestDispatcher("/article.jsp").forward(req,resp);
        }
    }
}
