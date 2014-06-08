package net.sunshow.tools.blogbus2wordpress.fetcher;

import net.sunshow.tools.blogbus2wordpress.bean.BlogItem;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItemAttachment;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItemComment;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: sunshow
 * Date: 6/8/14
 * Time: 9:03 AM
 */
public class BlogItemFetcher {

    /**
     * 指定地址抓取博客内容
     * @param url 博客文章地址
     * @return 文章对象
     * @throws Exception
     */
    public static BlogItem fetch(String url) throws Exception {
        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put("Referer", "http://www.blogbus.com/");
        headerParams.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");

        String source = FetchUtils.URLGetWithHeaderParams(url, headerParams, null, "UTF-8");

        Parser pageParser = Parser.createParser(source, "UTF-8");

        // posts block
        BlogItem blogItem = new BlogItem();
        String postsHtml = pageParser.extractAllNodesThatMatch(new CssSelectorNodeFilter("ul[id='posts']")).toHtml();
        Parser postsParser = Parser.createParser(postsHtml, "UTF-8");
        {
            // parse date, title, category from postHeader
            String postDate, title, category;
            {
                String postHeaderHtml = postsParser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='postHeader']")).toHtml();
                Parser parser = Parser.createParser(postHeaderHtml, "UTF-8");
                postDate = parser.extractAllNodesThatMatch(new TagNameFilter("h3")).elementAt(0).toPlainTextString();
                parser.setInputHTML(postHeaderHtml);
                title = parser.extractAllNodesThatMatch(new TagNameFilter("h2")).elementAt(0).getFirstChild().toPlainTextString();
                parser.setInputHTML(postHeaderHtml);
                category = parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("span[class='category'] a")).elementAt(0).toPlainTextString();
            }

            String content;
            final List<BlogItemAttachment> attachmentList = new ArrayList<BlogItemAttachment>();
            {
                postsParser.setInputHTML(postsHtml);

                AndFilter andFilter = new AndFilter(new TagNameFilter("p"), new NotFilter(new CssSelectorNodeFilter("p[class='cc-lisence']")));
                NodeList nodeList = postsParser.extractAllNodesThatMatch(andFilter);

                content = nodeList.toHtml();

                Parser parser = Parser.createParser(content, "UTF-8");

                parser.extractAllNodesThatMatch(new NodeFilter() {
                                                    private static final long serialVersionUID = 5897429403772546452L;

                                                    public boolean accept(Node node) {
                                                        if (node instanceof LinkTag) {
                                                            LinkTag linkTag = (LinkTag) node;
                                                            if (linkTag.getFirstChild() instanceof ImageTag) {
                                                                BlogItemAttachment attachment = new BlogItemAttachment();
                                                                attachment.setLink(linkTag.getLink());

                                                                ImageTag imageTag = (ImageTag) linkTag.getFirstChild();
                                                                attachment.setSrc(imageTag.getImageURL());

                                                                attachment.setFilename(StringUtils.substringAfterLast(attachment.getSrc(), "/"));

                                                                attachmentList.add(attachment);
                                                            }
                                                        }
                                                        return false;
                                                    }
                                                }
                );
            }

            String postTime, creator;
            {
                postsParser.setInputHTML(postsHtml);

                String postFooterHtml = postsParser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='postFooter']")).toHtml();
                Parser parser = Parser.createParser(postFooterHtml, "UTF-8");
                creator = parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("span[class='author']")).elementAt(0).toPlainTextString();
                parser.setInputHTML(postFooterHtml);
                postTime = parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("span[class='time']")).elementAt(0).toPlainTextString();
            }

            String postId = StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), ".html");

            blogItem.setAttachmentList(attachmentList);
            blogItem.setCategory(category);
            blogItem.setContent(content);
            blogItem.setCreator(creator);
            blogItem.setPostId(postId);
            blogItem.setTitle(title);

            blogItem.setPubDate(postDate + " " + postTime);

        }

        // comment block
        pageParser.setInputHTML(source);
        String commentsHtml = pageParser.extractAllNodesThatMatch(new CssSelectorNodeFilter("ul[id='comments']")).toHtml();
        Parser commentsParser = Parser.createParser(commentsHtml, "UTF-8");
        {
            List<BlogItemComment> commentList = new ArrayList<BlogItemComment>();
            NodeList nodeList = commentsParser.extractAllNodesThatMatch(new TagNameFilter("li"));
            for (int i = 0; i < nodeList.size(); i++) {
                String html = nodeList.elementAt(i).toHtml();
                Parser parser = Parser.createParser(html, "UTF-8");

                BlogItemComment blogItemComment = new BlogItemComment();

                String content = parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='cmtBody']")).elementAt(0).toPlainTextString();
                blogItemComment.setContent(content);

                parser.setInputHTML(html);
                blogItemComment.setPubDate(parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("span[class='time']")).elementAt(0).toPlainTextString());

                parser.setInputHTML(html);
                NodeList urlNodeList = parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='menubar'] span[class='author'] a"));
                if (urlNodeList.size() > 0) {
                    LinkTag linkTag = (LinkTag)urlNodeList.elementAt(0);
                    blogItemComment.setUrl(linkTag.getLink());
                    blogItemComment.setAuthor(linkTag.toPlainTextString());
                } else {
                    parser.setInputHTML(html);
                    String author = StringUtils.substringBefore(parser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='menubar'] span[class='author']")).elementAt(0).toPlainTextString(), " ");
                    blogItemComment.setAuthor(author);
                }

                commentList.add(blogItemComment);
            }

            blogItem.setCommentList(commentList);
        }

        return blogItem;
    }

    /**
     * 根据分页页面解析文章列表
     * @param pageUrl 分页地址
     * @return 单页内的文章地址列表
     * @throws Exception
     */
    public static List<String> fetchItemUrlList(String pageUrl) throws Exception {
        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put("Referer", "http://www.blogbus.com/");
        headerParams.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");

        String source = FetchUtils.URLGetWithHeaderParams(pageUrl, headerParams, null, "UTF-8");

        Parser pageParser = Parser.createParser(source, "UTF-8");

        List<String> itemUrlList = new ArrayList<String>();
        NodeList nodeList = pageParser.extractAllNodesThatMatch(new CssSelectorNodeFilter("div[class='postHeader'] h2>a"));
        for (int i = 0; i < nodeList.size(); i++) {
            LinkTag linkTag = (LinkTag)nodeList.elementAt(i);
            itemUrlList.add(linkTag.getLink());
        }
        return itemUrlList;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://8000km.blogbus.com/logs/270341731.html";
        fetch(url);

//        String url = "http://8000km.blogbus.com/index_57.html";
//        fetchItemUrlList(url);
    }
}
