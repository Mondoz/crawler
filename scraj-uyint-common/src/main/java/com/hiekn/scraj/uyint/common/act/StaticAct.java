package com.hiekn.scraj.uyint.common.act;

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
public interface StaticAct<VALUEIN, VALUEOUT> {
	
	/**
	 * 
	 */
	Logger LOGGER = Logger.getLogger(StaticAct.class);
	
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
	VALUEOUT execute(VALUEIN paramsMap, StaticActContext context) throws Exception;
	
	// all act alias.
	String STATIC_OPEN_ACT_ALIAS = "staticOpen";
	String STATIC_PAGINATION_ACT_ALIAS = "staticPagination";
	String STATIC_FETCH_KV_ACT_ALIAS = "staticFetchKV";
	String STATIC_FETCH_KVS_ACT_ALIAS = "staticFetchKVs";
	String STATIC_FETCH_LIST_ACT_ALIAS = "staticFetchList";
	String STATIC_HANDLE_LIST_ACT_ALIAS = "staticHandleList";
	
	String STATIC_DEDUP_ACT_ALIAS = "dedup";
	String STATIC_STORE_ACT_ALIAS = "store";
	
	
	// all fetch type.
	String STATIC_FETCH_BY_TYPE_XPATH = "byXpath";
	String STATIC_FETCH_BY_TYPE_REGEX = "byRegex";
	String STATIC_FETCH_BY_TYPE_CSS = "byCss";
	String STATIC_FETCH_BY_TYPE_CONSTANT = "byConstant";
	String STATIC_FETCH_BY_TYPE_COMMON = "byCommon";
	
	//  all fetch attribute
	String FETCH_BY_ATTR_INNER_HTML = "innerHTML";
	String FETCH_BY_ATTR_INNER_TEXT = "innerText";
	String FETCH_BY_ATTR_OUTER_HTML = "outerHTML";
	String FETCH_BY_ATTR_OWN_TEXT = "ownText";
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
	
}
