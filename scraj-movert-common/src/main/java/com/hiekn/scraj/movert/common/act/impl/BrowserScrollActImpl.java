package com.hiekn.scraj.movert.common.act.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserScrollAct;

public final class BrowserScrollActImpl implements BrowserScrollAct {
	
	public String execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		String pageSource = "";
		//
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		// 滚动js脚本
		String scrollScript = (String) paramsMap.get("scrollScript");
		// 脚本参数
		List<String> arguments = null;
		String argument = (String) paramsMap.get("arguments");
		if (null != argument && argument.length() > 0) {
			arguments = GSON.fromJson(argument, new TypeToken<List<String>>() {
			}.getType());
		}
		//
		int waitMillis = (int) (double) (null == paramsMap.get("waitMillis") ? 2000 : paramsMap.get("waitMillis"));
		
		if (null != arguments && arguments.size() > 0) {
        	((JavascriptExecutor) driver).executeScript(scrollScript, arguments.toArray());
        } else {
        	((JavascriptExecutor) driver).executeScript(scrollScript);
        }
		
		// Let the user actually see something!
        TimeUnit.MILLISECONDS.sleep(waitMillis);
		
//        pageSource = ((WebElement) driver.executeScript("return document.getElementsByTagName('html')[0];"))
//        		.getAttribute("outerHTML");
        
		return pageSource;
	}

}
