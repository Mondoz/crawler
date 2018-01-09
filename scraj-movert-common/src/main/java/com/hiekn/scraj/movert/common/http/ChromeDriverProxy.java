package com.hiekn.scraj.movert.common.http;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;


/**
 * 
 * use chrome web driver.
 * 
 * @author pzn
 *
 */
public class ChromeDriverProxy implements Closeable {
	
	private ChromeDriver mChromeDriver;
	
	public ChromeDriverProxy() {
		this(null);
	}
	
	public ChromeDriverProxy(String proxy) {
		this(ConstResource.CHROME_PATH, proxy);
	}
	
	public ChromeDriverProxy(String chrome, String proxy) {
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		
		// Using a Chrome executable in a non-standard location
		if (!StringUtils.isNullOrEmpty(chrome)) {
			ChromeOptions opt = new ChromeOptions();
			opt.setBinary(chrome);
			capabilities.setCapability(ChromeOptions.CAPABILITY, opt);
		}
		
		// Add the WebDriver proxy capability.
		if (!StringUtils.isNullOrEmpty(proxy)) {
			Proxy httpProxy = new Proxy();
			httpProxy.setHttpProxy(proxy).setFtpProxy(proxy).setSslProxy(proxy);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
			
		} 
		
		//
		// initial chrome driver with capabilities.
		mChromeDriver = new ChromeDriver(capabilities);
		
		//
		// Maximizes the current window if it is not already maximized
		mChromeDriver.manage().window().maximize();
		// Sets the amount of time to wait for an asynchronous script to finish execution before throwing an error. 
		// If the timeout is negative, then the script will be allowed to run indefinitely.
		mChromeDriver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
		// Sets the amount of time to wait for a page load to complete before throwing an error. 
		// If the timeout is negative, page loads can be indefinite.
		mChromeDriver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		// Specifies the amount of time the driver should wait 
		// when searching for an element if it is not immediately present. 
		mChromeDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
	
	//
	public RemoteWebDriver webDriver() { return mChromeDriver; }
	
	//
	public void close() throws IOException {
		if (null != mChromeDriver) {
			mChromeDriver.quit();
			mChromeDriver = null;
		}
	}
	
	// set the ChromeDriver location in environment variable.
	static {
		if (StringUtils.isNullOrEmpty(ConstResource.CHROME_DRIVER_PATH)) {
			throw new NullPointerException("chrome driver path cannot be null.");
		}
		System.setProperty("webdriver.chrome.driver", ConstResource.CHROME_DRIVER_PATH);
	}
	
}
