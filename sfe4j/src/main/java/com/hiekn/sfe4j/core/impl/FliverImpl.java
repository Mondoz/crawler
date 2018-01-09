package com.hiekn.sfe4j.core.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hiekn.sfe4j.core.Fliver;
import com.hiekn.sfe4j.core.FliverContext;
import com.hiekn.sfe4j.util.StringUtils;
import com.hiekn.sfe4j.util.TimeUtils;

/**
 * 
 * @author pzn
 */
public class FliverImpl implements Fliver {
	
	public static void main(String[] args) throws URISyntaxException {
		
		
		String str = "新华网北京7月19日电  7月18日，习近平总书记到宁夏回族自治区考察。"
				+ "从北京直飞固原，驱车70多公里到将台堡，向红军长征会师纪念碑敬献花篮并参观三军会师纪念馆。1936年10月，红军三大主力在会宁和将台堡会师，标志二万五千里长征胜利结束。";
		
		str = FliverImpl.doComplete("http://www.baidu.com", "http://", "http", "", "");
		
		System.out.println(str);
		
	}
	
	
	public final List<Map<String, Object>> clean(List<Map<String, Object>> odata, String cleanTaskJson) {
		LOGGER.info("fliver clean ... init. size ... " + odata.size());
		//
		JSONArray cleanTask = JSON.parseArray(cleanTaskJson);
		JSONObject task;
		String field;
		String formatter;
		for (Map<String, Object> data : odata) {
			for (int i = 0, len = cleanTask.size(); i < len; i++) {
				task = cleanTask.getJSONObject(i);
				field = task.getString("field");
				formatter = task.getString("formatter");
				
				String fv = (String) (null == data.get(field) ? "" : data.get(field));
				fv = doFormat(fv, formatter);
				data.put(field, fv);
			}
		}
		//
		LOGGER.info("fliver clean ... done. size ... " + odata.size());
		return odata;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * @param src
	 * @param mixs
	 * @return
	 */
	public final String doFormat(String src, String mixs) {
		if (StringUtils.isNullOrEmpty(mixs)) return src;
		
		JSONObject mixin = JSON.parseObject(mixs);
		JSONArray defaults = mixin.getJSONArray("defaults");
		JSONArray customs = mixin.getJSONArray("customs");
		
		// 先使用 默认的进行格式化
		for (int i = 0, len = defaults.size(); i < len; i++) {
			String fno = defaults.getString(i);
			JSONObject json = JSON.parseObject(context.findFormatter(fno));
			if (fno.startsWith("1")) {
				src = doFilter(src, json.getString("regex"), json.getBooleanValue("useMulti"));
			} else if (fno.startsWith("2")) {
				src = doFind(src, json.getString("regex"), json.getIntValue("group"), json.getBooleanValue("useMulti"), json.getString("multiSeparator"));
			} else if (fno.startsWith("3")) {
				src = doReplace(src, json.getString("regex"), json.getString("replacement"), json.getBooleanValue("useMulti"));
			} else if (fno.startsWith("4")) {
				src = doSubstring(src, json.getIntValue("beginIndex"), json.getIntValue("endIndex"), 
						json.getString("beginStr"), json.getBooleanValue("includeBeginStr"), json.getBooleanValue("useLastBeginStr"), 
						json.getString("endStr"), json.getBooleanValue("includeEndStr"), json.getBooleanValue("useLastEndStr"));
			} else if (fno.startsWith("5")) {
				src = doTimeFormat(src, json.getString("timeType"));
			}
		}
		
		// 然后使用自定义的进行过滤
		String type;
		String regex;
		boolean useMulti;
		for (int i = 0, len = customs.size(); i < len; i++) {
			JSONObject json = customs.getJSONObject(i);
			type = json.getString("type");
			regex = json.getString("regex");
			useMulti = json.getBooleanValue("useMulti");
			switch (type) {
			case FILTER_TYPE:
				src = doFilter(src, regex, useMulti);
				break;
			case FINDER_TYPE:
				String multiSeparator = null == json.getString("multiSeparator") ? ";" : json.getString("multiSeparator");
				int group = json.getIntValue("group");
				src = doFind(src, regex, group, useMulti, multiSeparator);
				break;
			case REPLACER_TYPE:
				String replacement = null == json.getString("replacement") ? "" : json.getString("replacement");
				src = doReplace(src, regex, replacement, useMulti);
				break;
			case SUBSTRING_TYPE:
				src = doSubstring(src, json.getIntValue("beginIndex"), json.getIntValue("endIndex"), 
						json.getString("beginStr"), json.getBooleanValue("includeBeginStr"), json.getBooleanValue("useLastBeginStr"), 
						json.getString("endStr"), json.getBooleanValue("includeEndStr"), json.getBooleanValue("useLastEndStr"));
				break;
			case TIME_TYPE:
				src = doTimeFormat(
						src, 
						json.getString("timeType"));
				break;
			case COMPLETE_TYPE:
				src = doComplete(
						src, 
						json.getString("prefix"), 
						json.getString("prefixStd"), 
						json.getString("suffix"), 
						json.getString("suffixStd"));
				break;
			}
		}
		
		return src;
	}
	
	
	/**
	 * 
	 * 过滤掉正则表达式匹配的结果
	 * 
	 * @param src
	 * @param regex
	 * @param useMulti
	 * @return
	 */
	public static final String doFilter(String src, String regex, boolean useMulti) {
		if (useMulti) return src.replaceAll(regex, "");
		return src.replaceFirst(regex, "");
	}
	
	/**
	 * 
	 * 根据正则表达式进行查找
	 * 保留正则匹配的结果
	 * 
	 * useMulti表示是否保留多个查找结果
	 * multiSeparator多个查找结果之间的分隔符 默认使用;
	 * <p>
	 * sample：
	 * 	<li>
	 * 	1.
	 * 	src: "这个活动2016年07月05日-2016年07月08日结束"
	 *  pattern: "\\d{4}年\\d{2}月\\d{2}日"
	 *  useMulti: false
	 *  multiSeparator: ""	
	 * 	返回结果："2016年07月05日"
	 *  </li>
	 * 	<li>	
	 *  2.
	 * 	src: "这个活动2016年07月05日-2016年07月08日结束"
	 *  pattern: "\\d{4}年\\d{2}月\\d{2}日"
	 *  useMulti: true
	 *  multiSeparator: ";"	
	 * 	返回结果："2016年07月05日;2016年07月08"
	 * 	</li>
	 * </p>
	 * 
	 * @param src 用来匹配正则的原始字符换
	 * @param regex 待查找的字符串
	 * @param group 取第几个group   0表示整个正则匹配结果  1表示第一个group ...
	 * @param multiFind  是否查找多处
	 * @param multiSeparator 多个时分隔符
	 * @return
	 */
	public static final String doFind(String src, 
			String regex, 
			int group, 
			boolean useMulti, 
			String multiSeparator) {
		
		if (group < 0) group = 0;
		
		Matcher matcher = Pattern.compile(regex).matcher(src);
		// 只返回第一个匹配结果
		if (!useMulti) return matcher.find() ? matcher.group(group) : "";
		
		// 返回多个匹配结果  每个之间用${multiSeparator}隔开
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNullOrEmpty(multiSeparator)) multiSeparator = ";";
		while (matcher.find()) {
			sb.append(matcher.group(group)).append(multiSeparator);
		}
		// 删除最后分隔符
		if (sb.length() > 0) return sb.substring(0, sb.lastIndexOf(multiSeparator));
		
		return "";
	}
	
	/**
	 * 把正则表达式匹配的字符串替换成其他的字符串
	 * 
	 * 
	 * @param src 待进行替换的原始字符串
	 * @param regex 用于查找待替换字符串的正则表达式
	 * @param replacement 用来替换的字符串
	 * @param multiReplace 是否替换多个
	 * @return
	 */
	public static final String doReplace(String src, 
			String regex, 
			String replacement, 
			boolean useMulti) {
		if (useMulti) return src.replaceAll(regex, replacement);
		return src.replaceFirst(regex, replacement);
	}
	
	/**
	 * 
	 * 字符串截取
	 * 
	 * @param src
	 * @param beginIndex
	 * @param endIndex
	 * @param beginStr
	 * @param useLastBeginStr
	 * @param endStr
	 * @param useLastEndStr
	 * @return
	 */
	public static final String doSubstring(String src, 
			int beginIndex, 
			int endIndex, 
			String beginStr, 
			boolean includeBeginStr,
			boolean useLastBeginStr, 
			String endStr, 
			boolean includeEndStr,
			boolean useLastEndStr) {
		
		// beginIndex vs beginStr 都写了  会选择beginStr
		if (!StringUtils.isNullOrEmpty(beginStr)) {
			if (useLastBeginStr) {
				beginIndex = includeBeginStr ? src.lastIndexOf(beginStr) : src.lastIndexOf(beginStr) + beginStr.length();
			} else {
				beginIndex = includeBeginStr ? src.indexOf(beginStr) : src.indexOf(beginStr) + beginStr.length();
			}
		}
		// endIndex vs endStr 都写了  会选择endStr
		if (!StringUtils.isNullOrEmpty(endStr)) {
			if (useLastEndStr) {
				endIndex = includeEndStr ? src.lastIndexOf(endStr) + endStr.length() : src.lastIndexOf(endStr);
			} else {
				endIndex = includeEndStr ? src.indexOf(endStr) + endStr.length() : src.indexOf(endStr);
			}
		}
		
		if (beginIndex < 0) beginIndex = 0;
		if (endIndex < 0) endIndex = src.length() + endIndex;
		if (endIndex < 1) endIndex = src.length();
		
		if (beginIndex > endIndex) throw new RuntimeException("!!!字符串截取，起始位置不能大于结束位置!!!");
		
		return src.substring(beginIndex, endIndex);
	}
	
	/**
	 * 字符串补全
	 * 
	 * 加前后缀
	 * 
	 * @param src
	 * @param prefix
	 * @param prefixStd
	 * @param suffix
	 * @param suffixStd
	 * @return
	 */
	public static final String doComplete(String src, String prefix, String prefixStd, String suffix, String suffixStd) {
		if (!StringUtils.isNullOrEmpty(prefix)) {
			if (!StringUtils.isNullOrEmpty(prefixStd)) {
				if (!src.startsWith(prefixStd)) src = prefix + src;
			}
			else src = prefix + src;;
		}
		if (!StringUtils.isNullOrEmpty(suffix)) {
			if (!StringUtils.isNullOrEmpty(suffixStd)) {
				if (!src.endsWith(suffixStd)) src += suffix;
			}
			else src += suffix;
		}
		return src;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * @param src
	 * @param timeType
	 * @param pattern
	 * @return
	 */
	public final String doTimeFormat(String src, String timeType) {
		String resultTime = "";
		if ("longTime".equalsIgnoreCase(timeType)) {
			resultTime = timeUtils.formatLongTime(src);
		} else {
			resultTime = timeUtils.formatStringTime(src);
		}
		return resultTime;
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	// 
	//
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	static final String FILTER_TYPE = "sfe4j.filter";
	static final String FINDER_TYPE = "sfe4j.finder";
	static final String REPLACER_TYPE = "sfe4j.replacer";
	static final String SUBSTRING_TYPE = "sfe4j.substring";
	static final String TIME_TYPE = "sfe4j.time";
	static final String COMPLETE_TYPE = "sfe4j.complete";
	
	static FliverContext context;
	// initial fliver context
	static {
		try {
			context = new FliverContext();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	//
	private TimeUtils timeUtils;
	// initial timeutils
	{
		timeUtils = new TimeUtils();
	}
}
