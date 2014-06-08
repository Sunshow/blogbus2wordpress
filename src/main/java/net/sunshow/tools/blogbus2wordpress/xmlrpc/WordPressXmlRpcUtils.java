package net.sunshow.tools.blogbus2wordpress.xmlrpc;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * WordPress XmlRpc
 * User: sunshow
 * Date: 6/8/14
 * Time: 11:25 AM
 */
public class WordPressXmlRpcUtils {

    static byte[] readImageAsByteArray(String imageName) {
        byte[] b = null;
        try {
            RandomAccessFile f = new RandomAccessFile(imageName, "r");
            b = new byte[(int) f.length()];
            f.read(b);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void main(String[] args) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://8000km.sunshow.net/xmlrpc.php"));
        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
        client.setConfig(config);

        Map data = new HashMap();
        data.put("name", "FABDC6DE5128E5DDED460DC8C194BDB7_500.jpg");
        data.put("type", "image/jpeg");
        data.put("bits", readImageAsByteArray("/Users/sunshow/Downloads/FABDC6DE5128E5DDED460DC8C194BDB7_500.jpg"));
        data.put("post_id", 5404171);
        data.put("overwrite", true);

        Object[] params = new Object[]{new Integer(1), "8000km", "123456", data};
        Map result = (Map) client.execute("wp.uploadFile", params);
        for (Object o : result.keySet()) {
            System.out.println(o + "=" + result.get(o));
        }
    }
}
