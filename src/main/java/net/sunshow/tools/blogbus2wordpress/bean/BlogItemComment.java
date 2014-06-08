package net.sunshow.tools.blogbus2wordpress.bean;

import java.util.Date;

/**
 * 评论
 * User: sunshow
 * Date: 6/8/14
 * Time: 9:03 AM
 */
public class BlogItemComment {

    private String author;
    private String url;
    private Date pubDate;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
