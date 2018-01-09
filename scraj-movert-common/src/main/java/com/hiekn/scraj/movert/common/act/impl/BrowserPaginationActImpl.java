package com.hiekn.scraj.movert.common.act.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserClickAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchListAct;
import com.hiekn.scraj.movert.common.act.BrowserPaginationAct;
import com.hiekn.scraj.movert.common.act.BrowserScrollAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.scraj.movert.common.core.BrowserPaginationListener;
import com.hiekn.scraj.movert.common.util.MathExpressionUtil;

public class BrowserPaginationActImpl implements BrowserPaginationAct {
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		//
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		//
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		//
		int maxPage = (int) Double.parseDouble((null == paramsMap.get("maxPage") ? "5" : paramsMap.get("maxPage")).toString());// 最大页数
		int batchPage = (int) Double.parseDouble((null == paramsMap.get("batchPage") ? "5" : paramsMap.get("batchPage")).toString());// 批量提交页数
    	if (batchPage < 1) batchPage = 5;
		//
		LOGGER.info("pagination ... strat ... maxPage = " + maxPage);
		// 睡眠
		int sleepMillis = (int) Double.parseDouble((null == paramsMap.get("sleepMillis") ? "0" : paramsMap.get("sleepMillis")).toString());
		int waitMillis = (int) Double.parseDouble((null == paramsMap.get("waitMillis") ? "3000" : paramsMap.get("waitMillis")).toString());
		// 当前页
		int currentPage = (int) Double.parseDouble((null == paramsMap.get("currentPage") ? "1" : paramsMap.get("currentPage")).toString());
		// 内置页数  实际处理的页数
		int handledPage = 1;
		// 
		// 
		String strategy = (String) paramsMap.get("strategy");
		String byType = (String) paramsMap.get("byType");
		String typeValue = (String) paramsMap.get("typeValue");
        // 
        // 
        // 循环翻页内部命令
        List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
        // 翻页中断标志
        boolean isContinue = true;
     	// 实际当前页列表的起始位置
		// 因为滚动和加载更多模式会导致页面列表元素越来越多
		// 导致多次提取会有重复数据
		int actualListIndex = 0;
		
        // =====================
        // 循环翻页 开始
        // =====================
        paginationLoop:
        while (isContinue) {
        	// 当前正在处理页
    		LOGGER.info("execute pagination ... currentPage = " + handledPage);
    		// 当前页提取到的列表
    		List<Map<String, Object>> currentPageList = new ArrayList<Map<String, Object>>();
        	// 达到需要处理的页码
    		if (handledPage == currentPage) {
    			// ----
    			for (Map<String, Object> cmd : innerCommands) {
    				String cmdKey = cmd.keySet().iterator().next();
					Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
					switch (cmdKey) {
					case BROWSER_SCROLL_ACT_ALIAS:
						mBrowserScrollAct.execute(params, context);
						break;
					case BROWSER_CLICK_ACT_ALIAS:
						mBrowserClickAct.execute(params, context);
						break;
					case BROWSER_FETCH_LIST_ACT_ALIAS:
						//
						params.put("actualListIndex", actualListIndex);
						
						BrowserKV browserKV = mBrowserFetchListAct.execute(params, context);
						if (null == browserKV.key) {
							currentPageList.addAll((List<Map<String, Object>>) browserKV.value);
        				} else {
        					Map<String, Object> kv = new HashMap<String, Object>();
        					kv.put(browserKV.key, browserKV.value);
        					currentPageList.add(kv);
        				}
						//
						if ("more".equalsIgnoreCase(strategy) || "scroll".equalsIgnoreCase(strategy)) actualListIndex += currentPageList.size();;
						//
						break;
					case BROWSER_FETCH_KVS_ACT_ALIAS:
						//
						Map<String, Object> extractMap = mBrowserFetchKVsAct.execute(params, context);
	    				// 有可能有一些干扰数据
	    				if (extractMap.size() > 0) currentPageList.add(extractMap);
						break;
					case BROWSER_FETCH_KV_ACT_ALIAS:
						BrowserKV kvPair = mBrowserFetchKVAct.execute(params, context);
						Map<String, Object> singleMap = new HashMap<String, Object>();
						singleMap.put(kvPair.key, kvPair.value);
						currentPageList.add(singleMap);
						break;
					}
    			}
    			// ----
    			// 期望页码+1
    			currentPage++;
        	}
    		
    		//
    		// 检查当前页列表
    		//
    		if (null != currentPageList && currentPageList.size() > 0) resultList.addAll(currentPageList);
    		
    		//
    		//
    		if (handledPage % batchPage == 0 && null != listener) {
    			listener.performAfterPaginationCommands(resultList);
				resultList.clear();
    		}
    		
    		// 满足翻页结束条件
    		if (handledPage == maxPage || !isContinue) break paginationLoop;
    		
    		// sleep for a while
    		// note: each element finished, while sleep
    		if (sleepMillis > 0) TimeUnit.MILLISECONDS.sleep(new Random().nextInt(sleepMillis) + 1);
    		
    		//
    		// 真正执行翻页
    		//
    		for (int tri = 0; tri < 3; tri++) {
    			try {
    				//
    				isContinue = true;
    				
    				// 如果使用规则翻页
    				if ("rule".equals(strategy)) {
    					Map<String, Object> ruleParams = (Map<String, Object>) paramsMap.get("rule");
    					String paginationRule = (String) ruleParams.get("paginationRule");
    					String paginationUrl = (String) ruleParams.get("paginationUrl");
    					
    					String pn = MathExpressionUtil.eval(paginationRule.replaceAll("currentPage", handledPage + ""));
    					String nextUrl = paginationUrl.replaceAll("\\(pn\\)", pn);
    					
    					LOGGER.info("nextUrl ------->" + nextUrl);
    					
    					if (tri == 0) driver.navigate().to(nextUrl);
    					else driver.navigate().refresh();
    					
    				} else {
    					// 使用其他几种方式翻页
    					WebElement webElement = null;
    					WebDriverWait wait = new WebDriverWait(driver, 5);
    					switch (byType) {
    					case BROWSER_FETCH_BY_TYPE_ID:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.id(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_CLASS:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.className(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_CSS:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_TAGNAME:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.tagName(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_NAME:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.name(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_LINK_TEXT:
    						if (typeValue.indexOf("currentPage") > -1) {
    							typeValue = typeValue.replaceAll("currentPage", handledPage + "");
    							typeValue = MathExpressionUtil.eval(typeValue);
    						}
    						//
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_PARTIAL_LINK_TEXT:
    						if (typeValue.indexOf("currentPage") > -1) {
    							typeValue = typeValue.replaceAll("currentPage", handledPage + "");
    							typeValue = MathExpressionUtil.eval(typeValue);
    						}
    						//
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_XPATH:
    						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(typeValue)));
    						break;
    					case BROWSER_FETCH_BY_TYPE_JAVASCRIPT:
    						((JavascriptExecutor) driver).executeScript(typeValue);
    						break;
    					default:
    						break;
    					}
    					
    					webElement.click();
    				}
    				
    				// 翻页正常结束循环
    				break;
    			} catch(TimeoutException e) {
    				// 
    				isContinue = false;
    				//
    				e.printStackTrace();
    				LOGGER.error("TimeoutException ... " + e);
    			} catch(WebDriverException e) {
    				//
    				isContinue = false;
    				//
    				e.printStackTrace();
    				LOGGER.error(e);
    			} 
    		}
    		
    		// 翻页完成需要再一次检查翻页是否成功
    		if (!isContinue) break paginationLoop;
    		
    		// Let the user actually see something!
            if (waitMillis > 0) TimeUnit.MILLISECONDS.sleep(waitMillis);
            
            // 实际页码+1
            handledPage++;
        }
        // =====================
        // 循环翻页 结束
        // =====================
		
        LOGGER.info("pagination ... done ... maxPage = " + maxPage);
		
		return resultList;
	}
	
	public void initial(BrowserClickAct mBrowserClickAct, //
						BrowserScrollAct mBrowserScrollAct, //
						BrowserFetchKVAct mBrowserFetchKVAct,// 
						BrowserFetchKVsAct mBrowserFetchKVsAct, //
						BrowserFetchListAct mBrowserFetchListAct) {
		this.mBrowserClickAct = mBrowserClickAct;
		this.mBrowserScrollAct = mBrowserScrollAct;
		this.mBrowserFetchKVAct = mBrowserFetchKVAct;
		this.mBrowserFetchKVsAct = mBrowserFetchKVsAct;
		this.mBrowserFetchListAct = mBrowserFetchListAct;
	}
	
	public void addListener(BrowserPaginationListener listener) {
		this.listener = listener;
	}
	
	private BrowserClickAct mBrowserClickAct;
	private BrowserScrollAct mBrowserScrollAct;
	private BrowserFetchKVAct mBrowserFetchKVAct;
	private BrowserFetchKVsAct mBrowserFetchKVsAct;
	private BrowserFetchListAct mBrowserFetchListAct;
	
	private BrowserPaginationListener listener;
	
}
