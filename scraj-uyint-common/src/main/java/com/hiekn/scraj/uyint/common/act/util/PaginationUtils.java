package com.hiekn.scraj.uyint.common.act.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 1.0
 *
 */
public final class PaginationUtils {
	
	private static final Logger LOGGER = Logger.getLogger(PaginationUtils.class);
	private static final ScriptEngine javaScriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	
	/**
	 * 
	 * @param paginationUrl 翻页模板url   使用 (pn) 页码占位符
	 * @param paginationRule 计算页码的算术表达式   
	 * @param currentPage 当前页码
	 * @param numFromPage 页面中提取的数字
	 * @param stdLength  页码标准长度
	 * @param leftAlign 左对齐字符
	 * @param rightAlign 右对其字符
	 * @return
	 */
	public static final String generateNextPageUrl(String paginationUrl, String paginationRule, 
			String currentPage, String numFromPage, 
			int stdLength, String leftAlign, String rightAlign) {
		LOGGER.info("generate next page url init.");
		try {
			String pn = javaScriptEngine.eval(
					paginationRule.replaceAll("currentPage", currentPage)
					.replaceAll("numFromPage", numFromPage))
					.toString().replaceAll("\\..*", "");
			
			while (stdLength > pn.length()) {
				if (!StringUtils.isNullOrEmpty(leftAlign)) pn = leftAlign + pn;
				else if (!StringUtils.isNullOrEmpty(rightAlign)) pn += rightAlign;
			}
			
			LOGGER.info("generate next page url done.");
			//
			return paginationUrl.replaceAll("\\(pn\\)", pn);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return "";
	}
	
//	public static final String generateNextPageUrl(String paginationUrl, String paginationRule, String currentPage) {
//		try {
//			LOGGER.info("acquire next page url...start");
//			String pn = javaScriptEngine.eval(
//					paginationRule.replaceAll("currentPage", currentPage)).toString().replaceAll("\\..*", "");
//			LOGGER.info("acquire next page url...done");
//			return paginationUrl.replaceAll("\\(pn\\)", pn);
//		} catch (ScriptException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
}
