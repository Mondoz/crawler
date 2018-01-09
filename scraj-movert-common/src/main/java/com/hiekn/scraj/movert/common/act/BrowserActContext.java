package com.hiekn.scraj.movert.common.act;

import org.apache.log4j.Logger;

import com.hiekn.scraj.movert.common.http.FirefoxDriverProxy;
import com.hiekn.sfe4j.core.Fliver;

/**
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 1.0
 */
public class BrowserActContext {
	
	public final void cleanup() {
		LOGGER.info("BrowserActContext cleanup() init, will quit webDriver and reset AcquisiteSize equals 0.");
		// c
		closeWebDriverProxy();
		
		mAcquisiteSize = 0;
		
		LOGGER.info("BrowserActContext cleanup() done.");
	}
	
	//
	public final void writeWebDriverProxy(FirefoxDriverProxy webDriverProxy) {
		this.mWebDriverProxy = webDriverProxy;
	}
	public final FirefoxDriverProxy readWebDriverProxy() {
		return mWebDriverProxy;
	}
	public final void closeWebDriverProxy() {
		if (null != mWebDriverProxy) {
			mWebDriverProxy.close();
			mWebDriverProxy = null;
		}
	}
	
	//
	public final void writeBrowserDedupAct(BrowserDedupAct mBrowserDedupAct) {
		this.mBrowserDedupAct = mBrowserDedupAct;
	}
	public final BrowserDedupAct readBrowserDedupAct() {
		return mBrowserDedupAct;
	}
	
	//
	public final void writeBrowserStoreDataAct(BrowserStoreDataAct mBrowserStoreDataAct) {
		this.mBrowserStoreDataAct = mBrowserStoreDataAct;
	}
	public final BrowserStoreDataAct readBrowserStoreDataAct() {
		return mBrowserStoreDataAct;
	}
	
	//
	public final void resetAcquisiteSize() {
		mAcquisiteSize = 0;
	}
	public final void writeAcquisiteSize(int size) {
		mAcquisiteSize += size;
	}
	public final int readAcquisiteSize() {
		return mAcquisiteSize;
	}
	
	//
	public void writeFliver(Fliver mFliver) {
		this.mFliver = mFliver;
	}
	public Fliver readFliver() {
		return mFliver;
	}
	
	// web driver 
	private FirefoxDriverProxy mWebDriverProxy;
	
	private BrowserDedupAct mBrowserDedupAct;
	private BrowserStoreDataAct mBrowserStoreDataAct;
	
	private int mAcquisiteSize;
	
	private Fliver mFliver;
	
	private static final Logger LOGGER = Logger.getLogger(BrowserActContext.class);
}
