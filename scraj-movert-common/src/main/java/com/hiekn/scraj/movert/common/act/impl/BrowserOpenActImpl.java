package com.hiekn.scraj.movert.common.act.impl;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserOpenAct;

public final class BrowserOpenActImpl implements BrowserOpenAct {

	public String execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		//
		String pageSource = "";
		//
		String url = (String) paramsMap.get("url");
		
		if (url == null || url.equals("")) return "";
		
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		
		// 失败重试
		for (int i = 1; i <= MAX_TRIES; i++) {
			try {
				// Load a new web page in the current browser window. This is done using an HTTP GET operation, 
    			// and the method will block until the load is complete. 
				if (i == 1) driver.navigate().to(url);
				// If first failed then refresh.
				else driver.navigate().refresh();
				
				//
				pageSource = driver.getPageSource();
    			
    			break;
			} catch(TimeoutException e) {
				e.printStackTrace();
    			LOGGER.error("TimeoutException ... " + e);
    			if (i == MAX_TRIES) lastExceptionCaught(driver);;
			}
		}
		
		return pageSource;
	}
	
	private void lastExceptionCaught(RemoteWebDriver driver) {
		// just stop load page.
		driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
	}
}
