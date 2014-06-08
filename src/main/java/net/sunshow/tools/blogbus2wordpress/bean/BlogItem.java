package net.sunshow.tools.blogbus2wordpress.bean;

import java.util.Date;
import java.util.List;

/**
 * 一篇blog
 * User: sunshow
 * Date: 6/8/14
 * Time: 8:56 AM
 */
public class BlogItem {

    private String title;
    private Date pubDate;
    private String creator;
    private String content;
    private String excerpt;
    private String postId;
    private String category;

    private List<BlogItemAttachment> attachmentList;

    private List<BlogItemComment> commentList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<BlogItemComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<BlogItemComment> commentList) {
        this.commentList = commentList;
    }

    public List<BlogItemAttachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<BlogItemAttachment> attachmentList) {
        this.attachmentList = attachmentList;
    }
}
