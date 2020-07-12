package test;


import org.junit.Test;
import pojo.Article;
import service.ArticleService;
import service.impl.ArticleServiceImpl;

import java.util.List;

public class ArticleServiceTest {

    ArticleService articleService = new ArticleServiceImpl();

    @Test
    public void saveArticle() {
        Article  article = new Article();
        article.setContent("saveservice");
        articleService.saveArticle(article);

    }

    @Test
    public void loadArticle() {
        List<Article> list = articleService.loadArticle();
        if(list.size()>0){
            System.out.println(list);
        }

    }

}