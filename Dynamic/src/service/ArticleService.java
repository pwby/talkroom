package service;


import pojo.Article;

import java.util.List;

public interface ArticleService {

    public void saveArticle(Article article);

    public List<Article> loadArticle();

}
