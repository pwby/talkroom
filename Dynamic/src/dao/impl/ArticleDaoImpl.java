package dao.impl;

import dao.ArticleDao;
import pojo.Article;

import java.util.List;

public class ArticleDaoImpl extends BaseDao implements ArticleDao {
    @Override
    public List<Article> loadArticle() {
        String sql = "select poster,time,content from article";
        return queryForList(Article.class, sql);
    }

    @Override
    public int saveArticle(Article article) {
        String sql = "insert into article(poster,time,content) values(?,?,?)";
        return update(sql,article.getPoster(),article.getTime(),article.getContent());
    }
}
