package pojo;


/**
 * @description:
 * @author: pwby
 * @create: 2020-05-07 17:00
 **/

public class Article {
    private String poster;   //发表者
    private String time;     //时间
    private String content;  //内容


    public Article(String poster,String time,String content){
        this.poster=poster;
        this.time=time;
        this.content=content;

    }
    public Article(){

    }
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
