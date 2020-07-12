package dao;

import pojo.Article;

import java.util.List;

public interface ArticleDao {

    public List<Article> loadArticle();

    public int saveArticle(Article article);

}
