package net.sunshow.tools.blogbus2wordpress.app;

import com.alibaba.fastjson.JSON;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItem;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItemAttachment;
import net.sunshow.tools.blogbus2wordpress.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Fetch content from blogbus
 */
public class DownloadAttachment {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(DownloadAttachment.class.getClassLoader().getResourceAsStream("config.properties"));

        String exportDirectory = properties.getProperty("export.directory") + "/blogbus";

        String attachmentDirectory = properties.getProperty("export.directory") + "/attachment";
        if (!FileUtils.isExist(attachmentDirectory)) {
            FileUtils.mkdir(attachmentDirectory);
        }

        int pagesize = Integer.parseInt(properties.getProperty("blogbus.pagesize"));

        for (int i = 1; i <= pagesize; i++) {
            String filename = String.format("%s/index_%s.json", exportDirectory, i);
            String content = FileUtils.readFile(filename, "UTF-8");

            List<BlogItem> blogItemList = JSON.parseArray(content, BlogItem.class);

            for (BlogItem blogItem : blogItemList) {
                for (BlogItemAttachment attachment : blogItem.getAttachmentList()) {
                    try {
                        org.apache.commons.io.FileUtils.copyURLToFile(new URL(attachment.getSrc()), new File(attachmentDirectory + "/" + attachment.getFilename()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
