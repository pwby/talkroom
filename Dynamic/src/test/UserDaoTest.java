package test;

import dao.ArticleDao;
import dao.impl.ArticleDaoImpl;
import org.junit.Test;
import pojo.Article;

import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest {

    ArticleDao articleDao = new ArticleDaoImpl();


    @Test
    public void loadArticle() {
        List<Article> list =articleDao.loadArticle();

        if (list.size()>0){
            for(int i = list.size()-1;i>=0;i--){
                System.out.println(list.get(i).getContent());
            }
        } else {
            System.out.println("无数据");
        }
    }

    @Test
    public void saveUser() {
        Article article = new Article();
        article.setContent("savesuccess");
        System.out.println( articleDao.saveArticle(article));
    }
}