package net.sunshow.tools.blogbus2wordpress.app;

import com.alibaba.fastjson.JSON;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItem;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItemComment;
import net.sunshow.tools.blogbus2wordpress.utils.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Properties;

/**
 * Fetch content from blogbus
 */
public class ConvertToWordpress {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(ConvertToWordpress.class.getClassLoader().getResourceAsStream("config.properties"));

        String exportDirectory = properties.getProperty("export.directory") + "/wordpress";

        String blogbusDirectory = properties.getProperty("export.directory") + "/blogbus";

        if (!FileUtils.isExist(exportDirectory)) {
            FileUtils.mkdir(exportDirectory);
        }

        String commentTemplate = FileUtils.readFile(ConvertToWordpress.class.getClassLoader().getResourceAsStream("wordpress.comment.template"), "UTF-8");
        String itemTemplate = FileUtils.readFile(ConvertToWordpress.class.getClassLoader().getResourceAsStream("wordpress.item.template"), "UTF-8");
        String blogTemplate = FileUtils.readFile(ConvertToWordpress.class.getClassLoader().getResourceAsStream("wordpress.template"), "UTF-8");

        int pagesize = Integer.parseInt(properties.getProperty("blogbus.pagesize"));

        StringBuilder itemListBuilder = new StringBuilder();

        for (int i = 1; i <= pagesize; i++) {
            String filename = String.format("%s/index_%s.json", blogbusDirectory, i);
            String content = FileUtils.readFile(filename, "UTF-8");

            List<BlogItem> blogItemList = JSON.parseArray(content, BlogItem.class);

            for (BlogItem blogItem : blogItemList) {
                itemListBuilder.append(outputItem(blogItem, itemTemplate, commentTemplate));
            }
        }

        String output = StringUtils.replaceEach(blogTemplate, new String[]{
                "%%BLOG_TITLE%%",
                "%%BLOG_LINK%%",
                "%%BLOG_DESCRIPTION%%",
                "%%BLOG_ITEM_LIST%%",
        }, new String[]{
                properties.getProperty("wordpress.title"),
                properties.getProperty("wordpress.link"),
                properties.getProperty("wordpress.description"),
                itemListBuilder.toString(),
        });

        FileUtils.createFile(exportDirectory + "/wordpress.xml", output, "UTF-8");
    }

    protected static String outputItem(BlogItem item, String itemTemplate, String commentTemplate) {
        StringBuilder commentListBuilder = new StringBuilder();
        if (item.getCommentList() != null) {
            int id = 1;
            for (int i = item.getCommentList().size() - 1; i >= 0; i--) {
                BlogItemComment blogItemComment = item.getCommentList().get(i);
                String comment = StringUtils.replaceEach(commentTemplate, new String[]{
                        "%%COMMENT_ID%%",
                        "%%COMMENT_AUTHOR%%",
                        "%%COMMENT_URL%%",
                        "%%COMMENT_TIME%%",
                        "%%COMMENT_CONTENT%%",
                }, new String[]{
                        String.valueOf(id),
                        blogItemComment.getAuthor(),
                        blogItemComment.getUrl(),
                        blogItemComment.getPubDate(),
                        blogItemComment.getContent(),
                });
                commentListBuilder.append(comment);
                id ++;
            }
        }

        return StringUtils.replaceEach(itemTemplate, new String[]{
                "%%ITEM_TITLE%%",
                "%%ITEM_CONTENT%%",
                "%%ITEM_ID%%",
                "%%ITEM_TIME%%",
                "%%ITEM_CATEGORY%%",
                "%%ITEM_COMMENT_LIST%%",
        }, new String[]{
                item.getTitle(),
                item.getContent(),
                item.getPostId(),
                item.getPubDate(),
                item.getCategory(),
                commentListBuilder.toString(),
        });
    }
}
