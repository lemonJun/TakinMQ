package com.bj58.spat.esbclient.util;

import java.io.File;

public class FileHelper {

	/**
	 * 获取当前class父目录
	 * @param cls
	 * @return 当前class父目录 URL
	 */
	public static String getCurrentPath(Class<?> cls) {
		String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = path.replaceFirst("file:/", "");
		path = path.replaceAll("!/", "");
		if(path.lastIndexOf(File.separator) >= 0){
			path = path.substring(0, path.lastIndexOf(File.separator));
		}
		if(path.substring(0,1).equalsIgnoreCase("/")){
			String osName = System.getProperty("os.name").toLowerCase();
			if(osName.indexOf("window") >= 0){
				path = path.substring(1);
			}
		}
		return path;
	}
}
