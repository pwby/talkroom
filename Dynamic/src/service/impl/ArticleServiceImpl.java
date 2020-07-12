package service.impl;


import dao.ArticleDao;
import dao.impl.ArticleDaoImpl;
import pojo.Article;
import service.ArticleService;

import java.util.List;

public class ArticleServiceImpl implements ArticleService {

    private ArticleDao articleDao = new ArticleDaoImpl();

    @Override
    public void saveArticle(Article article) {
       articleDao.saveArticle(article);
    }

    @Override
    public List<Article> loadArticle() {
       return articleDao.loadArticle();
    }
}
