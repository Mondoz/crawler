package com.hiekn.scraj.movert.common.core;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.http.ChromeDriverProxy;
import com.hiekn.scraj.movert.common.http.FirefoxDriverProxy;

public class WebDriverTest {
	
	@Test
	public void testTab() throws InterruptedException {
		try (FirefoxDriverProxy driverProxy = new FirefoxDriverProxy();) {
			RemoteWebDriver driver = driverProxy.webDriver();
			
			driver.get("http://mil.news.sina.com.cn/jssd/");
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
			
			// open a new tab.
		    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
		    driver.get("http://toutiao.com");
		    String attribute = driver.findElement(By.cssSelector("li.item.clearfix")).getAttribute("outerHTML");
		    System.out.println(attribute);
		    TimeUnit.SECONDS.sleep(2);
	        // close the newly opened tab.
		    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "w");
	        // switch back to first tab.
		    driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "1");
	        
	        driver.findElement(By.cssSelector("div.page-wrap.clearfix > p > a:nth-last-child(2)")).click();
	        driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			TimeUnit.SECONDS.sleep(2);
		    
		    TimeUnit.SECONDS.sleep(5);
		}
	}
	
	@Test
	public void testScroll() throws InterruptedException, IOException {
		try (ChromeDriverProxy driverProxy = new ChromeDriverProxy();) {
			RemoteWebDriver driver = driverProxy.webDriver();
			
			driver.get("http://toutiao.com/");
			
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			System.out.println("sleep...");
			TimeUnit.SECONDS.sleep(5);
			
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			System.out.println("sleep...");
			TimeUnit.SECONDS.sleep(5);
			
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			System.out.println("sleep...");
			TimeUnit.SECONDS.sleep(5);
		}
	}
	
}
