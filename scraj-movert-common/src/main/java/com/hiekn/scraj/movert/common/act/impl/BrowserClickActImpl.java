package com.hiekn.scraj.movert.common.act.impl;

import java.util.Map;
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

public final class BrowserClickActImpl implements BrowserClickAct {
	
	public String execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		//
		String pageSource = "";
		//
		String byType = (String) paramsMap.get("byType");
		String typeValue = (String) paramsMap.get("typeValue");
		int waitMillis = (int) (double) (null == paramsMap.get("waitMillis") ? 3000 : paramsMap.get("waitMillis"));
		
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		
		for (int i = 0; i < MAX_TRIES; i++) {
			try {
				if (i == 0) {
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
						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(typeValue)));
						break;
					case BROWSER_FETCH_BY_TYPE_PARTIAL_LINK_TEXT:
						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(typeValue)));
						break;
					case BROWSER_FETCH_BY_TYPE_XPATH:
						webElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(typeValue)));
						break;
					case BROWSER_FETCH_BY_TYPE_JAVASCRIPT:
						((JavascriptExecutor) driver).executeScript(typeValue);
						break;
					}
					
					webElement.click();
				} else {
					driver.navigate().refresh();
				}
				
				break;
			} catch(TimeoutException e) {
				e.printStackTrace();
				LOGGER.error(e);
			} catch(WebDriverException e) {
				e.printStackTrace();
				LOGGER.error(e);
			} 
			
		}
		
		//
		TimeUnit.MILLISECONDS.sleep(waitMillis);
		
//		pageSource = ((WebElement) driver.executeScript("return document.getElementsByTagName('html')[0];"))
//				.getAttribute("outerHTML");
		
		return pageSource;
	}

}
