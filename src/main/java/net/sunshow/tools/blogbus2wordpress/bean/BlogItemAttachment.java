package net.sunshow.tools.blogbus2wordpress.bean;

/**
 * 附件
 * User: sunshow
 * Date: 6/8/14
 * Time: 9:03 AM
 */
public class BlogItemAttachment {

    private String filename;
    private String link;    // 链接地址
    private String src;     // 附件地址

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
