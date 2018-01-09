package com.hiekn.scraj.movert.common.act.impl;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserTypeTextAct;

public final class BrowserTypeTextActImpl implements BrowserTypeTextAct {

	public String execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		
		String pageSource = "";
		
		//
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		//
		String byType = (String) paramsMap.get("byType");
		String typeValue = (String) paramsMap.get("typeValue");
        String text = (String) paramsMap.get("text");
        int waitMillis = (int) (double) (null == paramsMap.get("waitMillis") ? 1000 : paramsMap.get("waitMillis"));
        
        //
        switch (byType) {
		case BROWSER_FETCH_BY_TYPE_ID:
			driver.findElementById(typeValue).sendKeys(text);
			break;
		case BROWSER_FETCH_BY_TYPE_CLASS:
			driver.findElementByClassName(typeValue).sendKeys(text);
			break;
		case BROWSER_FETCH_BY_TYPE_CSS:
			driver.findElementByCssSelector(typeValue).sendKeys(text);
			break;
		case BROWSER_FETCH_BY_TYPE_TAGNAME:
			driver.findElementByTagName(typeValue).sendKeys(text);
			break;
		case BROWSER_FETCH_BY_TYPE_NAME:
			driver.findElementByName(typeValue).sendKeys(text);
			break;
		case BROWSER_FETCH_BY_TYPE_XPATH:
			driver.findElementByXPath(typeValue).sendKeys(text);
			break;
		}
		
        // Let the user actually see something!
        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(waitMillis) + 1L);
        
//        pageSource = ((WebElement) driver.executeScript("return document.getElementsByTagName('html')[0];"))
//        		.getAttribute("outerHTML");
        
		return pageSource;
	}

}
