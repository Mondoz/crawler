package com.hiekn.scraj.uyint.common.html;

import org.apache.log4j.Logger;

public interface CommonHtmlFilter {
	
	Logger LOGGER = Logger.getLogger(CommonHtmlFilter.class);
	
	/**
	 * 对一个文件进行过滤
	 * author: xiaohuqi
	 * @param sourceFilePath 源文件路径
	 * @return 成功返回过滤后的文件源码，失败返回空串
	 */	
	String filterAFile(String pageSource);
	
	/**
	 * author xiaohuqi
	 * @param content 源内容
	 * @return 
	 * 过滤内容
	 */
	String filterContent(String content);
	
	String filterContentKeepBr(String content);
	
	String filterContentKeepBrImg(String content);
}
