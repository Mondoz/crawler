package com.hiekn.scraj.uyint.common.html.impl;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hiekn.scraj.uyint.common.html.CommonHtmlFilter;
import com.hiekn.scraj.uyint.common.util.ConstResource;

public class CommonHtmlFilterImpl implements CommonHtmlFilter {

	/**
	 * 对一个文件进行过滤
	 * author: xiaohuqi
	 * @param sourceFilePath 源文件路径
	 * @return 成功返回过滤后的文件源码，失败返回空串
	 */	
	public String filterAFile(String pageSource){
		try{
			//匹配注释的正则表达式
			String remarkRegrex = "<!--[\\s\\S]*?-->";	
			//匹配JS的正则表达式
			String jsRegrex = "(?i)<script[\\s\\S]+?</script>";
			//String jsRegrex = "<script[\\s\\S]+?type[\\s\\S]*?=[\\s\\S]*?[\"']?text/javascript[\"']?[\\s\\S]*?</script>";
			//匹配CSS的正则表达式
			//String cssRegrex = "<style[\\s\\S]+?type[\\s\\S]*?=[\\s\\S]*?[\"']?text/css[\"']?>[\\s\\S]*?</style>";
			String cssRegrex = "(?i)<style[\\s\\S]+?</style>";
			//String cssRegrex = "<link[\\s\\S]+?type[\\s\\S]*?=[\\s\\S]*?[\"']?text/css[\"']?[\\s\\S]*?/>";
			//去掉以上正则表达式能匹配到的内容
			pageSource = pageSource.replaceAll(remarkRegrex, "");
			pageSource = pageSource.replaceAll(jsRegrex, "");
			pageSource = pageSource.replaceAll(cssRegrex, "");
			String regex0 = "&nbsp;";
			pageSource = pageSource.replaceAll(regex0, "");
			
		}catch(Exception e){
			System.out.println("In filterAFile，过滤文件时发生错误：" + e);
		}
		return pageSource;
	}
	
	/**
	 * author xiaohuqi
	 * @param content 源内容
	 * @return 
	 * 过滤内容
	 */
	public String filterContent(String content){
		try{
			String regex = "[\\?\\(\\)]";
			String regex0 = "&nbsp;";
			String regex1 = "&#\\d{1,5};";
			String regex2 = "<[\\s\\S]+?>";
			String regex3 = "<([a-zA-Z]+?)[\\s\\S]+?>([\\s\\S]+?)</\\1>";
			String regex4 = "[\\s]+";
			String regex5 = "[\\r\\n]+";

			content = content.replaceAll(regex, "");
			content = content.replaceAll(regex4, " ");
			content = content.replaceAll(regex5, ConstResource.NEW_LINE_CHARACTER);
			
			Pattern p = Pattern.compile(regex3);
			Matcher m;
			String matchContent = "";
				
			content = content.replaceAll(regex0, "");
			content = content.replaceAll(regex1, "");
			p = Pattern.compile(regex2);
			m = p.matcher(content);			
			matchContent = "";
			while(m.find()){
				matchContent = m.group();
				if(matchContent.getBytes().length == matchContent.length() || 
						matchContent.indexOf("font-family") != -1){	//说明不含中文
					try{
						content = content.replaceFirst(matchContent, "");
					}catch(Exception e){
						int tempIndex = content.indexOf(matchContent);
						content = content.substring(0, tempIndex) + content.substring(tempIndex + matchContent.length());
					}
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}
	
	public String filterContentKeepBr(String content){
		String str = "";
		try{
			String emptyTagRegex = "<(\\w+)[^>]*?>\\s*?</\\1>";
			String fullTagRegex = "<(\\w+)[^>]*?>([\\s\\S]+?)</\\1>";
			String endTagRegex = "</[^\n\r>]+?>";
			String simpleTagRegex = "<[^>]+?>";
			//删除$防止后面报错
			content = content.replaceAll("\\$","");
			str = content.replaceAll(emptyTagRegex, "");	//过滤掉所有空HTML标签
			
			//接下来把除img的其它标签替换为br标签
			Pattern p = Pattern.compile(fullTagRegex);
			Matcher m = p.matcher(str);
			long time1 = Calendar.getInstance().getTimeInMillis();
			while(m.find()){
				if(Calendar.getInstance().getTimeInMillis() - time1 > 10000){
					break;
				}
				if(m.group(1).equalsIgnoreCase("p")){
					str = m.replaceFirst(m.group(2)+"<br />");
				}
				else{
					str = m.replaceFirst(m.group(2));
				}
				m.reset(str);
			}
			
			str = str.replaceAll(endTagRegex, "");
			
			//接下来把简单标签都替换掉，简单标签就是不含有实体的标签
			p = Pattern.compile(simpleTagRegex);
			m = p.matcher(str);
			time1 = Calendar.getInstance().getTimeInMillis();
			while(m.find()){
				if(Calendar.getInstance().getTimeInMillis() - time1 > 10000){
					break;
				}
				if(!m.group().toLowerCase().startsWith("<br")){
					str = m.replaceFirst("");
				}
				else{
					str = m.replaceAll("<br />");
					continue;
				}
				m.reset(str);
			}
			str = str.trim();
			while(str.indexOf("<br />") == 0){
				str = str.substring(6);
				str = str.trim();			
			}
			while(str.lastIndexOf("<br />")+6 == str.length()){
				str = str.substring(0, str.length()-6);
				str = str.trim();			
			}
			
			str = str.replaceAll("(<br />\\s*)+", "<br />");
		}catch(Exception e){
			LOGGER.error("过滤抓取内容时发生异常：\n" + e);
			e.printStackTrace();
		}
		return str;
	}
	
	public String filterContentKeepBrImg(String content) {
		// TODO Auto-generated method stub
		String str = "";
		try{
			
			String emptyTagRegex = "<(\\w+)[^>]*?>\\s*?</\\1>";
			String fullTagRegex = "<(\\w+)[^>]*?>([\\s\\S]+?)</\\1>";
			String endTagRegex = "</[^\n\r>]+?>";
			
			//删除$防止后面报错
			content = content.replaceAll("\\$","");
			//先处理<img></img>的情况
			content = content.replaceAll(">\\s*?</img>", "/>");
			
			str = content.replaceAll(emptyTagRegex, "");	//过滤掉所有空HTML标签
			
			//接下来把除img的其它标签替换为br标签
			Pattern p = Pattern.compile(fullTagRegex);
			Matcher m = p.matcher(str);
			long time1 = Calendar.getInstance().getTimeInMillis();
			while(m.find()){
				if(Calendar.getInstance().getTimeInMillis() - time1 > 10000){
					break;
				}
				if(m.group(1).equalsIgnoreCase("p")){
					str = m.replaceFirst(m.group(2)+"<br />");
				}
				else{
					str = m.replaceFirst(m.group(2));
				}
				m.reset(str);
			}
			
			str = str.replaceAll(endTagRegex, "");
			
		}catch(Exception e){
			LOGGER.error("过滤抓取内容时发生异常：\n" + e);
			e.printStackTrace();
		}
		return str;
	}
}
