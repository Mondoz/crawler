package com.hiekn.scraj.uyint.common.act;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hiekn.scraj.uyint.common.http.HttpReader;
import com.hiekn.sfe4j.core.Fliver;

public class StaticActContext {
	
	public final void cleanup() {
		LOGGER.info("StaticActContext cleanup() init, will close HttpReder, clear cookies and reset AcquisiteSize equals 0.");
		
		if (mHttpReader != null) {
			mHttpReader.close();
			mHttpReader = null;
		}
		if (mCookies != null) {
			mCookies.clear();
			mCookies = null;
		}
		
		// 
		mCookie = null;
		mAcquisiteSize = 0;
		taskId = 0;
		
		LOGGER.info("StaticActContext cleanup() done.");
	}
	
	//
	public final void resetHttpReader(HttpReader httpReader) {
		if (mHttpReader != null) {
			mHttpReader.close();
			mHttpReader = null;
		}
		this.mHttpReader = httpReader;
	}
	public final HttpReader readHttpReader() {
		return mHttpReader;
	}
	public final void writeHttpReader(HttpReader httpReader) {
		this.mHttpReader = httpReader;
	}
	
	//
	public final void writeCookies(List<String> cookies) {
		this.mCookies = cookies;
	}
	public final String readCookie() {
		return mCookie;
	}
	/**
	 * 如果还有cookie， 重置一个新的cookie  
	 * 否则重置为""
	 */
	public final void resetCookie() {
		if (hasCookie()) mCookie = mCookies.remove(mCookies.size() - 1);
		else mCookie = "";
	}
	public final boolean hasCookie() {
		return null != mCookies && mCookies.size() > 0;
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
	public final void writeStaticDedupAct(StaticDedupAct act) {
		this.mStaticDedupAct = act;
	}
	public final StaticDedupAct readStaticDedupAct() {
		return mStaticDedupAct;
	}
	
	
	//
	public final void writeStaticStoreDataAct(StaticStoreDataAct act) {
		this.mStaticStoreDataAct = act;
	}
	public final StaticStoreDataAct readStaticStoreDataAct() {
		return mStaticStoreDataAct;
	}
	
	/**
	 * 
	 * 加载不在classpath下的jar包
	 * 
	 * 可以是jar所在目录或者jar包
	 * 
	 * @param jarPath
	 * @throws MalformedURLException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void loadJar(String jarPath) throws MalformedURLException, NoSuchMethodException, SecurityException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (null != mClassLoader) return;
		
		mClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		
		List<URL> urls = new ArrayList<>();
		File f = new File(jarPath);
		if (f.isDirectory()) {
			for (File tf : f.listFiles()) {
				urls.add(tf.toURI().toURL());
			}
		} else {
			urls.add(f.toURI().toURL());
		}

		if (urls.size() == 0) return;
		
		// disallow if already loaded 
		for (URL url : mClassLoader.getURLs()) {
			if (urls.contains(url)) urls.remove(url);
		}
		
		// get addURL method 
		Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		// promote the method to public access
		addURL.setAccessible(true);
		
		for (URL url : urls) {
			LOGGER.info("classloder load new jar ... " + url);
			// invoke addURL method
			addURL.invoke(mClassLoader, url);
		}
	}
	
	/**
	 * 加载不在classpath下jar包的指定类
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return mClassLoader.loadClass(className);
	}
	
	public void writeFliver(Fliver mFliver) {
		this.mFliver = mFliver;
	}
	public Fliver readFliver() {
		return mFliver;
	}
	
	public void writeTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int readTaskId() {
		return taskId;
	}
	
	private Fliver mFliver;
	private StaticStoreDataAct mStaticStoreDataAct;
	private StaticDedupAct mStaticDedupAct;
	private HttpReader mHttpReader;
	private List<String> mCookies;
	private String mCookie;// 当前正在使用cookie
	private int mAcquisiteSize;
	private int taskId;
	
	/**
	 * 类加载器
	 */
	private URLClassLoader mClassLoader;
	
	static final Logger LOGGER = Logger.getLogger(StaticActContext.class);
	
}
