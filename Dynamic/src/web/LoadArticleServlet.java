package web;


import pojo.Article;
import service.ArticleService;
import service.impl.ArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class LoadArticleServlet extends HttpServlet {

    private ArticleService articleService = new ArticleServiceImpl() ;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Content-Type","text/html,charset=utf-8");
        List<Article> list=articleService.loadArticle();
        req.setAttribute("list",list);

        req.getRequestDispatcher("/listArticle.jsp").forward(req,resp);
    }
}
