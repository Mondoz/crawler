package com.hiekn.scraj.uyint.common.act.impl;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.html.CommonHtmlParser;
import com.hiekn.scraj.uyint.common.html.impl.CommonHtmlParserImpl;
import com.hiekn.scraj.uyint.common.http.HttpFileReader;
import com.hiekn.sfe4j.core.Fliver;
import com.hiekn.sfe4j.util.StringUtils;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import us.codecraft.xsoup.Xsoup;

public class StaticFetchKVActImpl implements StaticFetchKVAct {
	
	@SuppressWarnings("unchecked")
	public StaticKV execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		String field = (String) paramsMap.get("field");
		Object val = null;
		String dependencySource = (String) paramsMap.get("dependencySource");
		if (!StringUtils.isNullOrEmpty(dependencySource)) {
			String byType = (String) paramsMap.get("byType");
			String typeValue = (String) paramsMap.get("typeValue");
			String byAttr = (String) paramsMap.get("byAttr");
			Map<String, Object> formatter = (Map<String, Object>) paramsMap.get("formatter");
			String valueType = (String) paramsMap.get("valueType");
			
			//
			switch (byType) {
			case STATIC_FETCH_BY_TYPE_CSS:
				val = getValByJsoup(dependencySource, typeValue, byAttr, formatter, context.readFliver());
				break;
			case STATIC_FETCH_BY_TYPE_REGEX:
				int group = null == paramsMap.get("group") ? 0 : ((Double) paramsMap.get("group")).intValue();
				val = getValByRegex(dependencySource, typeValue, group);
				break;
			case STATIC_FETCH_BY_TYPE_XPATH:
				val = getValByJsoup(dependencySource, typeValue, byAttr, formatter, context.readFliver());
				break;
			case STATIC_FETCH_BY_TYPE_CONSTANT:
				val = getValByRuntime(typeValue);
				break;
			case STATIC_FETCH_BY_TYPE_COMMON:
				// 列表解析的时候会传入title
				String title = (String) paramsMap.get("title");
				// 直接内容解析的时候依赖前面提取的title
				// 如果没有title传入,则不能进行内容通用提取算法
				if (!StringUtils.isNullOrEmpty(title)) {
					// 通用解析
					CommonHtmlParser htmlParser = new CommonHtmlParserImpl();
					val = htmlParser.getContentHtml(title, dependencySource);
				}
				break;
			}
			
			if (null != val && !StringUtils.isNullOrEmpty(val.toString())) {
				// 格式化
				if (null != formatter && formatter.size() > 0) {
					val = context.readFliver().doFormat(val.toString(), GSON.toJson(formatter));
				}
				// 转类型
				if (!StringUtils.isNullOrEmpty(valueType)) val = convert2ValType(val.toString(), valueType);
			} else {
				val = null;
			}
			
		}
		
		if (StringUtils.isNullOrEmpty(field)) return new StaticKV(val);
		
		return new StaticKV(field, val);
	}
	
	/**
	 * 数据类型转换
	 * 
	 * 数据默认就是string类型的
	 * 
	 * 1. integer: 转换失败  返回-1
	 * 2. long 转换失败  返回-1L
	 * 3. float 转换失败  返回-1.0f
	 * 4. double 转换失败  返回-1.00
	 * 
	 * @param val
	 * @param valueType
	 * @return
	 */
	private Object convert2ValType(String val, String valueType) {
		Object convertedVal = val;
		switch (valueType) {
		case FETCH_VALUE_TYPE_INTEGER:
			try {
				convertedVal = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				convertedVal = Integer.valueOf(-1);
			}
			break;
		case FETCH_VALUE_TYPE_LONG:
			try {
				convertedVal = Long.parseLong(val);
			} catch (NumberFormatException e) {
				convertedVal = Long.valueOf(-1L);
			}
			break;
		case FETCH_VALUE_TYPE_FLOAT:
			try {
				convertedVal = Float.parseFloat(val);
			} catch (NumberFormatException e) {
				convertedVal = Float.valueOf(-1.0f);
			}
			break;
		case FETCH_VALUE_TYPE_DOUBLE:
			try {
				convertedVal = Double.parseDouble(val);
			} catch (NumberFormatException e) {
				convertedVal = Double.valueOf(-1.00);
			}
			break;
		}
		return convertedVal;
	}
	
	private String getValByFile(Element element, String attr, Map<String, Object> formatter, String cookie, Fliver fliver) 
			throws MalformedURLException {
		String url = element.attr(attr);
		
		if (null != formatter && formatter.size() > 0)
			url = fliver.doFormat(url, GSON.toJson(formatter));
		
		return getValByFile(url, cookie);
	}
	
	private String getValByFile(Node element, String attr, Map<String, Object> formatter, String cookie, Fliver fliver) 
			throws MalformedURLException {
		String url = element.getAttribute(attr);
		
		if (null != formatter && formatter.size() > 0)
			url = fliver.doFormat(url, GSON.toJson(formatter));
		
		return getValByFile(url, cookie);
	}
	
	private String getValByFile(String fileUrl, String cookie) 
			throws MalformedURLException {
		String val = "";
		val = HttpFileReader.getBase64String(fileUrl, null, cookie);
		return val;
	}
	
	private Object getValByRuntime(String valRef) {
		Object refVal = "";
		switch (valRef) {
		case RT_VALUE_TT_REF:
			refVal = System.currentTimeMillis();
			break;
		case RT_VALUE_TTS_REF:
			refVal = System.currentTimeMillis() / 1000;
			break;
		case RT_VALUE_TIME_REF:
			refVal = RT_TIME_FORMAT.format(System.currentTimeMillis());
			break;
		case RT_VALUE_DATE_REF:
			refVal = RT_DATE_FORMAT.format(System.currentTimeMillis());
			break;
		default:
			refVal = valRef;
			break;
		}
	
		return refVal;
	}
	
	private String getValByRegex(String dependencySource, String typeValue, int group) {
		String val = "";
		Matcher matcher = Pattern.compile(typeValue).matcher(dependencySource);
		if (matcher.find()) val = matcher.group(group);
		return val;
	}
	
	private String getValByJsoup(String dependencySource, String typeValue, String byAttr, Map<String, Object> formatter, Fliver fliver) 
			throws MalformedURLException {
		String val = "";
//		Element xe = Xsoup.compile(typeValue).evaluate(Jsoup.parse(dependencySource)).getElements().first();
		Element xe = Jsoup.parse(dependencySource).select(typeValue).first();
		if (null != xe) {
			switch (byAttr) {
			case FETCH_BY_ATTR_INNER_HTML: 
				val = xe.html();
				break;
			case FETCH_BY_ATTR_OUTER_HTML: 
				val = xe.outerHtml();
				break;
			case FETCH_BY_ATTR_INNER_TEXT:
				val = xe.text();
				break;
			case FETCH_BY_ATTR_OWN_TEXT:
				val = xe.ownText();
				break;
//			case FETCH_BY_ATTR_HREF: 
//				val = xe.attr("href");
//				break;
//			case FETCH_BY_ATTR_SRC: 
//				val = xe.attr("src");
//				break;
//			case FETCH_BY_ATTR_TITLE: 
//				val = xe.attr("title");
//				break;
//			case FETCH_BY_ATTR_ID: 
//				val = xe.attr("id");
//				break;
//			case FETCH_BY_ATTR_CLASS:
//				val = xe.attr("class");
//				break;
//			case FETCH_BY_ATTR_NAME: 
//				val = xe.attr("name");
//				break;
			case FETCH_BY_ATTR_TAG_NAME: 
				val = xe.tagName();
				break;
			case FETCH_BY_ATTR_FILE: 
				val = getValByFile(xe, "href", formatter, null, fliver);
				formatter = null;
				break;
			case FETCH_BY_ATTR_IMAGE: 
				val = getValByFile(xe, "src", formatter, null, fliver);
				formatter = null;
				break;
			default:
				val = xe.attr(byAttr);
				break;
			}
		}
		return val;
		
	}
	
	private String getValByJerry(String dependencySource, String typeValue, String byAttr, Map<String, Object> formatter, Fliver fliver) 
			throws MalformedURLException {
		String val = "";
		Node je = Jerry.jerry(dependencySource).$(typeValue).get(0);
		if (je != null) {
			switch (byAttr) {
			case FETCH_BY_ATTR_INNER_HTML: 
				val = je.getInnerHtml();
				break;
			case FETCH_BY_ATTR_OUTER_HTML: 
				val = je.getHtml();
				break;
			case FETCH_BY_ATTR_INNER_TEXT:
				val = je.getTextContent();
				break;
			case FETCH_BY_ATTR_HREF: 
				val = je.getAttribute("href");
				break;
			case FETCH_BY_ATTR_SRC: 
				val = je.getAttribute("src");
				break;
			case FETCH_BY_ATTR_TITLE: 
				val = je.getAttribute("title");
				break;
			case FETCH_BY_ATTR_ID: 
				val = je.getAttribute("id");
				break;
			case FETCH_BY_ATTR_CLASS:
				val = je.getAttribute("class");
				break;
			case FETCH_BY_ATTR_NAME: 
				val = je.getAttribute("name");
				break;
			case FETCH_BY_ATTR_TAG_NAME: 
				val = je.getNodeName();
				break;
			case FETCH_BY_ATTR_FILE: 
				val = getValByFile(je, "href", formatter, null, fliver);
				formatter = null;
				break;
			case FETCH_BY_ATTR_IMAGE: 
				val = getValByFile(je, "src", formatter, null, fliver);
				formatter = null;
				break;
			default:
				val = je.getAttribute(byAttr);
				break;
			}
		}
		return val;
	}
	
	private final DateFormat RT_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final DateFormat RT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
}
