package net.sunshow.tools.blogbus2wordpress.fetcher;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 抓取工具类
 * @author sunshow
 *
 */
public class FetchUtils {

    private static final transient Logger logger = LoggerFactory.getLogger(FetchUtils.class);

	private static final String URL_PARAM_CONNECT_FLAG = "&";

	private static MultiThreadedHttpConnectionManager connectionManager = null;

	private static int connectionTimeOut = 30000;
	private static int socketTimeOut = 30000;
	private static int maxConnectionPerHost = 20;
	private static int maxTotalConnections = 20;

	private static HttpClient client;

	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
		connectionManager.getParams().setSoTimeout(socketTimeOut);
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
		connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
		client = new HttpClient(connectionManager);
	}

	/**
	 * 带参数头的GET方式提交数据,伪造抓取身份
	 * @param url 待请求的URL
	 * @param headerParams 消息头参数
	 * @param params 要提交的数据
	 * @param enc 编码
	 * @return 响应结果
	 * @throws java.io.IOException IO异常
	 */
	public static String URLGetWithHeaderParams(String url, Map<String, String> headerParams,Map<String, String> params,String enc) {
		String responseData = null;
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer("");
		String tmpParam = null;
		tmpParam = getUrl(params, enc);
		if (!url.contains("?")) {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("?").append(tmpParam);
			}else{
				strtTotalURL.append(url);
			}
		} else {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("&").append(getUrl(params, enc));
			}else{
				strtTotalURL.append(url);
			}
		}

		try {
			getMethod = new GetMethod(strtTotalURL.toString());
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			//为了防止被抓取网站屏蔽，伪造一些信息
            if (headerParams != null) {
                for (String headName : headerParams.keySet()) {
                    getMethod.addRequestHeader(headName, headerParams.get(headName));
                }
            }
            // 执行getMethod
			int statusCode = client.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				String responseEncoding = getMethod.getResponseCharSet();
                if (responseEncoding == null || responseEncoding.trim().length() == 0) {
                    responseEncoding = enc;
                }
				InputStream in = getMethod.getResponseBodyAsStream();
				//这里的编码规则要与页面的相对应  
				BufferedReader br = new BufferedReader(new InputStreamReader(in, StringUtils.replace(responseEncoding, "\"", "")));
				String tempbf = null;;  
				StringBuffer sb = new StringBuffer(100);  
				while (true) {
					tempbf = br.readLine();
					if (tempbf == null) {
						break;
					} else {
						sb.append(tempbf);  
					}
				}
				responseData = sb.toString();
				logger.debug("GET请求响应内容："+responseData);
				br.close();
			} else {
				logger.error("GET请求失败,请求地址:"+strtTotalURL.toString()+",响应状态码:" + getMethod.getStatusCode());
			}
		} catch (HttpException e) {
			logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题,get请求url:"+strtTotalURL.toString(),e);
		} catch (IOException e) {
			logger.error("发生网络异常,get请求url:"+strtTotalURL.toString(),e);
		}
		if (getMethod != null) {
			getMethod.releaseConnection();
		}
		return responseData;
	}

	/**
	 * 据Map生成URL字符串
	 * @param map Map
	 * @param valueEnc URL编码
	 * @return URL
	 */
	private static String getUrl(Map<String, String> map, String valueEnc) {
		if (null == map || map.keySet().size() == 0) {
			return "";
		}
		StringBuffer url = new StringBuffer();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			if (map.containsKey(key)) {
				String val = map.get(key);
				String str = val != null ? val : "";
				try {
					url.append(URLEncoder.encode(key, valueEnc));
					url.append("=");
					url.append(URLEncoder.encode(str, valueEnc));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(), e);
					e.printStackTrace();
					continue;
				}
				url.append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = "";
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals(strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}
}
