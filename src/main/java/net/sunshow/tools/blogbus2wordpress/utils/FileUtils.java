package net.sunshow.tools.blogbus2wordpress.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件操作工具类
 * @author sunshow
 *
 */
public class FileUtils {
	public static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static boolean mkdir(String pathname) {
        File path = new File(pathname);
        if (!path.exists()) {
            return path.mkdirs();
        }
        return true;
    }

	/**
	 * 有编码方式的文件创建
	 *
	 * @param filePathAndName
	 *            文本文件完整绝对路径及文件名
	 * @param fileContent
	 *            文本文件内容
	 * @param encoding
	 *            编码方式 例如 GBK 或者 UTF-8
	 * @return
	 */
	public static boolean createFile(String filePathAndName, String fileContent, String encoding) {
		if(filePathAndName == null){
			logger.error("创建指定编码文件参数filePathAndName为null,创建失败,请指定具体文件");
			return false;
		}
		if(encoding == null){
			logger.error("创建指定编码文件参数encoding为null,创建失败,请指定具体编码");
			return false;
		}
		try {
			File file = new File(filePathAndName);
			if (!file.exists()) {
				String dirPath = file.getParent();
				if(dirPath != null){
					File dirFolder = new File(dirPath);
					if(!dirFolder.exists()){
						dirFolder.mkdirs();
					}
				}
				file.createNewFile();
			}
			PrintWriter writer = new PrintWriter(file, encoding);
			String content = null;
			if(fileContent == null){
				content = "";
			}else{
				content = fileContent;
			}
			writer.println(content);
			writer.close();
			logger.info("创建并写入内容到文件(filePathAndName:"+filePathAndName+",绝对路径:{})成功,编码格式:{}",file.getAbsolutePath(),encoding);
		} catch (Exception e) {
			logger.error("创建文件(参数filePathAndName:"+filePathAndName+")操作出错,创建失败",e);
			return false;
		}
		return true;
	}

	/**
	 * 判断文件是否存在
	 * @param filePathAndName
	 * @return
	 */
	public static boolean isExist(String filePathAndName) {
		File file = new File(filePathAndName);
		return file.exists();
	}

	/**
	 * 读取文件内容
	 * @param filePathAndName 带有完整绝对路径的文件名
	 * @param encoding 文件打开的编码方式
	 * @return 返回文件的内容
	 */
	public static String readFile(String filePathAndName, String encoding) throws IOException{
		if (filePathAndName == null || filePathAndName.isEmpty()) {
			return null;
		}

		if (!isExist(filePathAndName)) {
			logger.info("文件不存在，路径：{}", filePathAndName);
			return null;
		}
		encoding = encoding.trim();
		StringBuffer sb = new StringBuffer("");
		String content = null;

		FileInputStream fs = new FileInputStream(filePathAndName);
		InputStreamReader isr;
		if (encoding.isEmpty()) {
			isr = new InputStreamReader(fs);
		} else {
			isr = new InputStreamReader(fs, encoding);
		}
		BufferedReader br = new BufferedReader(isr);

		String data = "";
		while ((data = br.readLine()) != null) {
			sb.append(data + " ");
		}
		content = sb.toString();

		br.close();
		isr.close();
		fs.close();

		return content;
	}
}
