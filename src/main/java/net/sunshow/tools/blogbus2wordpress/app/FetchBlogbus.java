package net.sunshow.tools.blogbus2wordpress.app;

import com.alibaba.fastjson.JSON;
import net.sunshow.tools.blogbus2wordpress.bean.BlogItem;
import net.sunshow.tools.blogbus2wordpress.fetcher.BlogItemFetcher;
import net.sunshow.tools.blogbus2wordpress.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Fetch content from blogbus
 */
public class FetchBlogbus {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(FetchBlogbus.class.getClassLoader().getResourceAsStream("config.properties"));

        String exportDirectory = properties.getProperty("export.directory") + "/blogbus";
        if (!FileUtils.isExist(exportDirectory)) {
            FileUtils.mkdir(exportDirectory);
        }

        String baseUrl = properties.getProperty("blogbus.url");

        int pagesize = Integer.parseInt(properties.getProperty("blogbus.pagesize"));

        for (int i = 1; i <= pagesize; i++) {
            String pageUrl = String.format("%s/index_%s.html", baseUrl, i);

            List<BlogItem> blogItemList = new ArrayList<BlogItem>();

            List<String> pageItemUrlList = BlogItemFetcher.fetchItemUrlList(pageUrl);
            for (String url : pageItemUrlList) {
                blogItemList.add(BlogItemFetcher.fetch(url));
                Thread.sleep(1000);
            }

            String filename = String.format("%s/index_%s.json", exportDirectory, i);
            FileUtils.createFile(filename, JSON.toJSONString(blogItemList), "UTF-8");

            Thread.sleep(1000);
        }
    }
}
