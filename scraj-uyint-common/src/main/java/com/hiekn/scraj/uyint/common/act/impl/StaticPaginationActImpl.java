package com.hiekn.scraj.uyint.common.act.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticPaginationAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.proxy.StaticActProxy;
import com.hiekn.scraj.uyint.common.act.util.PaginationUtils;
import com.hiekn.scraj.uyint.common.core.StaticPaginationListener;
import com.hiekn.sfe4j.util.StringUtils;

import us.codecraft.xsoup.Xsoup;

public class StaticPaginationActImpl implements StaticPaginationAct {
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		String currentPageSource = (String) paramsMap.get("dependencySource");// 当前页的源码
    	Map<String, Object> paginationMeta = (Map<String, Object>) paramsMap.get("paginationMeta");// 翻页信息
    	String strategy = (String) paginationMeta.get("strategy");// 翻页策略
    	
    	int sleepMillis = (int) Double.parseDouble((null == paramsMap.get("sleepMillis") ? "0" : paramsMap.get("sleepMillis")).toString());
    	int currentPage = (int) Double.parseDouble((null == paramsMap.get("currentPage") ? "1" : paramsMap.get("currentPage")).toString());// 当前页
    	if (currentPage < 1) currentPage = 1;
    	int maxPage = (int) Double.parseDouble((null == paramsMap.get("maxPage") ? "5" : paramsMap.get("maxPage")).toString());// 最大页数
		int batchPage = (int) Double.parseDouble((null == paramsMap.get("batchPage") ? "5" : paramsMap.get("batchPage")).toString());// 批量提交页数
    	if (batchPage < 1) batchPage = 5;
    	int handledPage = 1;// 处理过的页数
    	List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
    	
    	boolean isContinuePagination = true;// 是否继续执行翻页
    	String nextPageUrl = null;// 下一页的地址
    	pagination:
    	while (isContinuePagination) {
    		if (StringUtils.isNullOrEmpty(currentPageSource)) break;
    		LOGGER.info("execute pagination inner commands, currentPage... " + currentPage);
    		// 执行内部命令
    		List<Map<String, Object>> currentPageList = new ArrayList<Map<String, Object>>();
    		inner:
    		for (Map<String, Object> cmd : innerCommands) {
    			String cmdKey = cmd.keySet().iterator().next();
				Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
    			
				boolean useCustom = false;
				switch (cmdKey) {
    			case STATIC_FETCH_KV_ACT_ALIAS:
    				params.put("dependencySource", currentPageSource);
    				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchKVAct> clazz = (Class<StaticFetchKVAct>) context.loadClass(className);
						StaticFetchKVAct customFetchValueAct = clazz.newInstance();
						mStaticFetchKVActProxy.act(customFetchValueAct);
					} else {
						mStaticFetchKVActProxy.act(mStaticFetchKVAct);
					}
    				StaticKV kvPair = mStaticFetchKVActProxy.execute(params, context);
    				Map<String, Object> singleKeyMap = new HashMap<String, Object>();
					singleKeyMap.put(kvPair.key, kvPair.value);
					currentPageList.add(singleKeyMap);
    				break;
    			case STATIC_FETCH_KVS_ACT_ALIAS:
    				params.put("dependencySource", currentPageSource);
    				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchKVsAct> clazz = (Class<StaticFetchKVsAct>) context.loadClass(className);
						StaticFetchKVsAct customFetchObjectAct = clazz.newInstance();
						mStaticFetchKVsActProxy.act(customFetchObjectAct);
					} else {
						mStaticFetchKVsActProxy.act(mStaticFetchKVsAct);
					}
    				Map<String, Object> multiKeyMap = mStaticFetchKVsActProxy.execute(params, context);
    				if (multiKeyMap.size() > 0) currentPageList.add(multiKeyMap);
    				break;
    			case STATIC_FETCH_LIST_ACT_ALIAS:
    				params.put("dependencySource", currentPageSource);
    				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchListAct> clazz = (Class<StaticFetchListAct>) context.loadClass(className);
						StaticFetchListAct customFetchListAct = clazz.newInstance();
						mStaticFetchListActProxy.act(customFetchListAct);
					} else {
						mStaticFetchListActProxy.act(mStaticFetchListAct);
					}
    				//
					StaticKV pair = mStaticFetchListActProxy.execute(params, context);
    				if (null == pair.key) {
    					currentPageList.addAll((List<Map<String, Object>>) pair.value);
    				} else {
    					Map<String, Object> temp = new HashMap<>();
    					temp.put(pair.key, pair.value);
    					currentPageList.add(temp);
    				}
    				break;
				case STATIC_DEDUP_ACT_ALIAS:
					if (currentPageList.size() == 0) {// 没有提取到数据 结束翻页
						isContinuePagination = false;
						break inner;
					}
					
					int beforeSize = currentPageList.size();
					params.put("kvList", currentPageList);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticDedupAct> clazz = (Class<StaticDedupAct>) context.loadClass(className);
						StaticDedupAct customDedupAct = clazz.newInstance();
						mStaticDedupActProxy.act(customDedupAct);
					} else {
						mStaticDedupActProxy.act(context.readStaticDedupAct());
					}
					
					List<Map<String, Object>> dedupedList = mStaticDedupActProxy.execute(params, context);
					currentPageList.clear();
					currentPageList.addAll(dedupedList);
					if (beforeSize > currentPageList.size() * 2) isContinuePagination = false;;// 跳出翻页
					break;
				}
    		}// 内部命令执行完毕
    		
    		//
    		// 现在真正开始执行翻页
    		//
    		if (currentPageList.size() == 0) {
    			// 当前页没有得到数据
    			// 检查是否因为cookie失效导致
    			if (context.hasCookie()) {
    				context.resetCookie();
    				// 重新请求  并重新执行翻页内部命令
    				context.readHttpReader().readSource(nextPageUrl, "", context.readCookie());
    				// ****  一定要continue 跳回到内部循环去  不然循环就会结束了  也就不会有作用
    				continue;
    			} 
    			// 并不是因为cookie导致的 直接结束循环
    			else {
    				isContinuePagination = false;
    			}
    		} else {
    			resultList.addAll(currentPageList);
    		}
    		
    		//
    		// 当设置了batchPage  检查已经处理的页数 
    		// 如果达到batchPage  则先提交一次
    		//
    		if (handledPage % batchPage == 0 && null != listener) {
    			listener.performAfterPaginationCommands(resultList);
				resultList.clear();
    		}
    		
    		// 检查是否应该终止循环翻页
    		if (!isContinuePagination || handledPage == maxPage) break pagination;;
    		
    		// 继续翻页  先睡眠一会
    		if (sleepMillis > 0) TimeUnit.MILLISECONDS.sleep(new Random().nextInt(sleepMillis) + 1);;
    		
    		// 重置nextPageUrl为 null
    		nextPageUrl = null;
    		if ("rule".equalsIgnoreCase(strategy)) {
    			String paginationRule = (String) paginationMeta.get("paginationRule");
				String paginationUrl = (String) paginationMeta.get("paginationUrl");
				int stdLength =  (int) ((double) (paginationMeta.get("stdLength") == null ? 1.00 : paginationMeta.get("stdLength")));
				String leftAlign = (String) paginationMeta.get("leftAlign");
				String rightAlign = (String) paginationMeta.get("rightAlign");
				
				String numFromPage = "";
				if (paginationRule.indexOf("numFromPage") > -1) {
					String numFromPageRegex = (String) paginationMeta.get("numFromPage");
					Matcher matcher = Pattern.compile(numFromPageRegex).matcher(currentPageSource);
					if (matcher.find()) numFromPage = matcher.group(1);
				}
				//
				nextPageUrl = PaginationUtils.generateNextPageUrl(paginationUrl, 
						paginationRule, currentPage + "", numFromPage, 
						stdLength, leftAlign, rightAlign);
    		} else if ("next".equalsIgnoreCase(strategy)) {
    			String byType = (String) paginationMeta.get("byType");
				String typeValue = (String) paginationMeta.get("typeValue");
				Map<String, Object> formatter = (Map<String, Object>) paginationMeta.get("formatter");
				switch (byType) {
				case STATIC_FETCH_BY_TYPE_CSS:
					nextPageUrl = Jsoup.parse(currentPageSource).select(typeValue).attr("href");
					break;
				case STATIC_FETCH_BY_TYPE_REGEX:
					Matcher m = Pattern.compile(typeValue).matcher(currentPageSource);
	            	if (m.find()) nextPageUrl = m.group();
					break;
				case STATIC_FETCH_BY_TYPE_XPATH:
					nextPageUrl = Xsoup.compile(typeValue).evaluate(Jsoup.parse(currentPageSource)).getElements().attr("href");
					break;
				}
				
				//
				if (!StringUtils.isNullOrEmpty(nextPageUrl)) {
					if (null != formatter && formatter.size() > 0) nextPageUrl = context.readFliver().doFormat(nextPageUrl, GSON.toJson(formatter));
				}
    		}
    		
    		LOGGER.info("get next page url... " + nextPageUrl);
    		
    		// check next page url
    		currentPageSource = null;
    		if (!StringUtils.isNullOrEmpty(nextPageUrl)) {
    			currentPageSource = context.readHttpReader().readSource(nextPageUrl);
    		}
    		
    		// increment handledPage and currentPage
    		currentPage++;
    		handledPage++;
    		
    	}// 循环翻页结束
    	
		return resultList;
	}
	
	public void initial(StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct,
			StaticFetchListAct mStaticFetchListAct) {
		this.mStaticFetchKVAct = mStaticFetchKVAct;
		this.mStaticFetchKVsAct = mStaticFetchKVsAct;
		this.mStaticFetchListAct = mStaticFetchListAct;
		
		//
		this.mStaticFetchKVActProxy = new StaticActProxy<>();
		this.mStaticFetchKVsActProxy = new StaticActProxy<>();
		this.mStaticFetchListActProxy = new StaticActProxy<>();
		this.mStaticDedupActProxy = new StaticActProxy<>();
	}
	
	public void addActListener(StaticPaginationListener listener) {
		this.listener = listener;
	}
	
	private StaticFetchKVAct mStaticFetchKVAct;
	private StaticFetchKVsAct mStaticFetchKVsAct;
	private StaticFetchListAct mStaticFetchListAct;
	
	
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchKVActProxy;
	private StaticActProxy<Map<String, Object>, Map<String, Object>> mStaticFetchKVsActProxy;
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchListActProxy;
	private StaticActProxy<Map<String, Object>, List<Map<String, Object>>> mStaticDedupActProxy;
	
	//
	private StaticPaginationListener listener;
}
