package com.hiekn.scraj.movert.common.act.impl;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.scraj.uyint.common.http.HttpFileReader;
import com.hiekn.sfe4j.core.Fliver;
import com.hiekn.sfe4j.util.StringUtils;

public class BrowserFetchKVActImpl implements BrowserFetchKVAct {

	@SuppressWarnings("unchecked")
	public BrowserKV execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		
		Object val = "";
		
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		
		WebElement dependencyWebElement = (WebElement) paramsMap.get("dependencyWebElement");
		
		String field = (String) paramsMap.get("field");
		String byType = (String) paramsMap.get("byType");
		String typeValue = (String) paramsMap.get("typeValue");
		String byAttr = (String) paramsMap.get("byAttr");
		Map<String, Object> formatter = (Map<String, Object>) paramsMap.get("formatter");
		String valueType = (String) paramsMap.get("valueType");
		
		List<WebElement> webElements = null;
		switch (byType) {
		case BROWSER_FETCH_BY_TYPE_ID:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsById(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.id(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_CLASS:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByClassName(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.className(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_CSS:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByCssSelector(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.cssSelector(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_TAGNAME:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByTagName(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.tagName(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_NAME:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByName(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.name(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_LINK_TEXT:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByLinkText(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.linkText(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_PARTIAL_LINK_TEXT:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByPartialLinkText(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.partialLinkText(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_XPATH:
			if (null == dependencyWebElement) {
				webElements = driver.findElementsByXPath(typeValue);
			} else {
				webElements = dependencyWebElement.findElements(By.xpath(typeValue));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_CONSTANT:
			val = getValByRuntime(typeValue);
			break;
		}
		
		//
		if (webElements.size() > 0) {
			WebElement webElement = webElements.get(0);
			switch (byAttr) {
			case FETCH_BY_ATTR_INNER_HTML:
				val = webElement.getAttribute("innerHTML");
				break;
			case FETCH_BY_ATTR_OUTER_HTML:
				val = webElement.getAttribute("outerHTML");
				break;
			case FETCH_BY_ATTR_INNER_TEXT:
				val = webElement.getText();
				break;
			case FETCH_BY_ATTR_TAG_NAME:
				val = webElement.getTagName();
				break;
			case FETCH_BY_ATTR_IMAGE:
				val = getValByFile(webElement, "src", formatter, context.readFliver());
				break;
			case FETCH_BY_ATTR_FILE:
				val = getValByFile(webElement, "href", formatter, context.readFliver());
				break;
			default:// real attribute
				val = webElement.getAttribute(byAttr);
				break;
			}
		}
		
		if (!StringUtils.isNullOrEmpty(val.toString())) {
			// 格式化
			if (null != formatter && formatter.size() > 0) {
				val = context.readFliver().doFormat(val.toString(), GSON.toJson(formatter));
			}
			// 转类型
			if (!StringUtils.isNullOrEmpty(valueType)) val = convert2ValType(val.toString(), valueType);
		} else {
			val = null;
		}
		
		
		//
		if (StringUtils.isNullOrEmpty(field)) return new BrowserKV(val);
		
		return new BrowserKV(field, val);
	}
	
	
	/**
	 * 数据类型转换
	 * 
	 * 数据默认就是string类型的
	 * 
	 * 1. integer: 转换失败  返回-1
	 * 2. long 转换失败  返回-1L
	 * 3. float 转换失败  返回-1.0f
	 * 4. double 转换失败  返回-1.00
	 * 
	 * @param val
	 * @param valueType
	 * @return
	 */
	private Object convert2ValType(String val, String valueType) {
		Object convertedVal = val;
		switch (valueType) {
		case FETCH_VALUE_TYPE_INTEGER:
			try {
				convertedVal = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				convertedVal = Integer.valueOf(-1);
			}
			break;
		case FETCH_VALUE_TYPE_LONG:
			try {
				convertedVal = Long.parseLong(val);
			} catch (NumberFormatException e) {
				convertedVal = Long.valueOf(-1L);
			}
			break;
		case FETCH_VALUE_TYPE_FLOAT:
			try {
				convertedVal = Float.parseFloat(val);
			} catch (NumberFormatException e) {
				convertedVal = Float.valueOf(-1.0f);
			}
			break;
		case FETCH_VALUE_TYPE_DOUBLE:
			try {
				convertedVal = Double.parseDouble(val);
			} catch (NumberFormatException e) {
				convertedVal = Double.valueOf(-1.00);
			}
			break;
		}
		return convertedVal;
	}
	
	private String getValByFile(WebElement element, String attr, Map<String, Object> formatter, Fliver fliver) throws MalformedURLException {
		String url = element.getAttribute(attr);
		
		if (null != formatter && formatter.size() > 0)
			url = fliver.doFormat(url, GSON.toJson(formatter));
		
		return getValByFile(url, null);
	}
	
	private String getValByFile(String fileUrl, String cookie) 
			throws MalformedURLException {
		String val = "";
		val = HttpFileReader.getBase64String(fileUrl, null, cookie);
		return val;
	}
	
	private Object getValByRuntime(String valRef) {
		Object refVal = "";
		switch (valRef) {
		case RT_VALUE_TT_REF:
			refVal = System.currentTimeMillis();
			break;
		case RT_VALUE_TTS_REF:
			refVal = System.currentTimeMillis() / 1000;
			break;
		case RT_VALUE_TIME_REF:
			refVal = RT_TIME_FORMAT.format(System.currentTimeMillis());
			break;
		case RT_VALUE_DATE_REF:
			refVal = RT_DATE_FORMAT.format(System.currentTimeMillis());
			break;
		default:
			refVal = valRef;
			break;
		}
	
		return refVal;
	}
	
}
