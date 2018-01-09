package com.hiekn.scraj.movert.common.http;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * use firefox web driver.
 * 
 * @author pzn
 *
 */
public class FirefoxDriverProxy implements Closeable {
	
	//
	private FirefoxDriver mFirefoxDriver;
	
	//
	public FirefoxDriverProxy() {
		this(null);
	}
	
	public FirefoxDriverProxy(String proxy) {
		this(ConstResource.FIREFOX_PATH, proxy);
	}
	
	public FirefoxDriverProxy(String firefox, String proxy) {
		File pathToBinary = new File(firefox);
		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();       
		
		if (StringUtils.isNullOrEmpty(proxy)) {
			// Creates a new FirefoxDriver using the default server configuration.
			mFirefoxDriver = new FirefoxDriver(ffBinary, firefoxProfile);
		} else {
			Proxy httpProxy = new Proxy();
			httpProxy.setHttpProxy(proxy)
			.setFtpProxy(proxy)
			.setSslProxy(proxy);
			
			DesiredCapabilities cap = DesiredCapabilities.firefox();
			cap.setCapability(CapabilityType.PROXY, proxy);
			
			mFirefoxDriver = new FirefoxDriver(ffBinary, firefoxProfile, cap);
		}
		
		//
		// Maximizes the current window if it is not already maximized
		mFirefoxDriver.manage().window().maximize();
		// Sets the amount of time to wait for an asynchronous script to finish execution before throwing an error. 
		// If the timeout is negative, then the script will be allowed to run indefinitely.
		mFirefoxDriver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
		// Sets the amount of time to wait for a page load to complete before throwing an error. 
		// If the timeout is negative, page loads can be indefinite.
		mFirefoxDriver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		// Specifies the amount of time the driver should wait 
		// when searching for an element if it is not immediately present. 
		mFirefoxDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
	
	// property provider
	public RemoteWebDriver webDriver() { return mFirefoxDriver; }
	
	// close
	public void close() { 
		if (null != mFirefoxDriver) {
			mFirefoxDriver.quit();
			mFirefoxDriver = null;
		}
	}
}
