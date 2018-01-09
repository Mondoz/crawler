package com.hiekn.scraj.movert.common.act;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * 
 * 采集行为接口
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/22
 *
 * @param <VALUEIN>
 * @param <VALUEOUT>
 */
public interface BrowserAct<VALUEIN, VALUEOUT> {
	
	/**
	 * 
	 */
	Logger LOGGER = Logger.getLogger(BrowserAct.class);
	
	/**
	 * 
	 */
	Gson GSON = new Gson();
	
	/**
	 * 
	 * @param params
	 * @param context
	 * @return
	 */
	VALUEOUT execute(VALUEIN paramsMap, BrowserActContext context) throws Exception;
	
	// all act alias.
	String BROWSER_OPEN_ACT_ALIAS = "browserOpen";
	String BROWSER_CLICK_ACT_ALIAS = "browserClick";
	String BROWSER_SCROLL_ACT_ALIAS = "browserScroll";
	String BROWSER_TYPE_TEXT_ACT_ALIAS = "browserTypeText";
	String BROWSER_PAGINATION_ACT_ALIAS = "browserPagination";
	String BROWSER_FETCH_KV_ACT_ALIAS = "browserFetchKV";
	String BROWSER_FETCH_KVS_ACT_ALIAS = "browserFetchKVs";
	String BROWSER_FETCH_LIST_ACT_ALIAS = "browserFetchList";
	String BROWSER_HANDLE_LIST_ACT_ALIAS = "browserHandleList";
	
	String BROWSER_DEDUP_ACT_ALIAS = "dedup";
	String BROWSER_STORE_ACT_ALIAS = "store";
	
	String STATIC_OPEN_ACT_ALIAS = "staticOpen";
	String STATIC_FETCH_KV_ACT_ALIAS = "staticFetchKV";
	String STATIC_FETCH_KVS_ACT_ALIAS = "staticFetchKVs";
	String STATIC_FETCH_LIST_ACT_ALIAS = "staticFetchList";
	
	
	
	// all fetch type.
	String BROWSER_FETCH_BY_TYPE_ID = "byId";
	String BROWSER_FETCH_BY_TYPE_CLASS = "byClass";
	String BROWSER_FETCH_BY_TYPE_TAGNAME = "byTagName";
	String BROWSER_FETCH_BY_TYPE_NAME = "byName";
	String BROWSER_FETCH_BY_TYPE_LINK_TEXT = "byLinkText";
	String BROWSER_FETCH_BY_TYPE_PARTIAL_LINK_TEXT = "byPartialLinkText";
	String BROWSER_FETCH_BY_TYPE_JAVASCRIPT = "byJavaScript";
	String BROWSER_FETCH_BY_TYPE_XPATH = "byXpath";
	String BROWSER_FETCH_BY_TYPE_REGEX = "byRegex";
	String BROWSER_FETCH_BY_TYPE_CSS = "byCss";
	String BROWSER_FETCH_BY_TYPE_CONSTANT = "byConstant";
	String BROWSER_FETCH_BY_TYPE_COMMON = "byCommon";
	
	//  all fetch attribute
	String FETCH_BY_ATTR_INNER_HTML = "innerHTML";
	String FETCH_BY_ATTR_INNER_TEXT = "innerText";
	String FETCH_BY_ATTR_OUTER_HTML = "outerHTML";
	String FETCH_BY_ATTR_HREF = "href";
	String FETCH_BY_ATTR_SRC = "src";
	String FETCH_BY_ATTR_TITLE = "title";
	String FETCH_BY_ATTR_ID = "id";
	String FETCH_BY_ATTR_CLASS = "class";
	String FETCH_BY_ATTR_NAME = "name";
	String FETCH_BY_ATTR_TAG_NAME = "tagName";
	String FETCH_BY_ATTR_FILE = "file";
	String FETCH_BY_ATTR_IMAGE = "image";
	
	// all value type alias.
	String FETCH_VALUE_TYPE_STRING = "string";
	String FETCH_VALUE_TYPE_INTEGER = "integer";
	String FETCH_VALUE_TYPE_LONG = "long";
	String FETCH_VALUE_TYPE_FLOAT = "float";
	String FETCH_VALUE_TYPE_DOUBLE = "double";
	
	// all runtime value references.
	String RT_VALUE_TT_REF = "$tt";// 毫秒
	String RT_VALUE_TTS_REF = "$tts";// 秒
	String RT_VALUE_TIME_REF = "$time";// 时间 yyyy-MM-dd HH:mm:ss
	String RT_VALUE_DATE_REF = "$date";// 日期 yyyy-MM-dd
	
	// all time formatter.
	DateFormat RT_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat RT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
}
