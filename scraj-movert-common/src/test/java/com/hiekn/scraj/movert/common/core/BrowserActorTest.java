package com.hiekn.scraj.movert.common.core;

import org.junit.Test;

import com.hiekn.scraj.movert.common.core.BrowserActor;

public class BrowserActorTest {
	
	@Test
	public void test01() {
		// 列表 --> 内容
		String json = "[ { 'browserOpen': { 'urls': ['http://mil.news.sina.com.cn/jssd/'] } }, { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeight);', 'waitMillis': 3000 } }, { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeight);', 'waitMillis': 3000 } }, { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeigth);', 'waitMillis': 3000 } }, { 'browserFetchList': { 'byType': 'byCss', 'typeValue': 'div.bc.w-list > div.blk_tw.clearfix', 'innerCommands': [ { 'browserFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'title', 'byType': 'byCss', 'typeValue': 'h3 > a', 'byAttr': 'innerText' }, { 'field': 'url', 'byType': 'byCss', 'typeValue': 'h3 > a', 'byAttr': 'href' } ] } } ] } }, { 'browserHandleList': { 'sleepMillis': 1000, 'innerCommands': [ { 'staticOpen': {'urls': ['$url']} }, { 'staticFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'time', 'byType': 'byCss', 'typeValue': '#page-tools > span > span.titer', 'byAttr': 'innerText' }, { 'field': 'source', 'byType': 'byCss', 'typeValue': '#media_name > a.ent1.fred', 'byAttr': 'innerText' }, { 'field': 'content', 'byType': 'byCss', 'typeValue': 'div#artibody', 'byAttr': 'outerHTML' } ] } } ] } } ]";
		
		
		
		
		BrowserActor actor = new BrowserActor(null, null);
		try {
			actor.perform(1, "sina", 0, json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			actor.browserActContext().cleanup();
			actor.staticActContext().cleanup();
		}
		
	}
	
	@Test
	public void test011() {
		// 列表 --> 内容
		String json = "[{'browserOpen':{'urls':['http://www.gov.cn/zhengce/xxgkzl.htm'],'formatter':{'defaults':[],'customs':[]}}},{'browserPagination':{'maxPage':1,'sleepMillis':1000,'waitMillis':1000,'strategy':'next','byType':'byCss','typeValue':'span.wcm_pointer.nav_go_next','innerCommands':[{'browserScroll':{'scrollScript':'window.scrollTo(0, document.body.scrollHeight);','arguments':'','waitMillis':1000}},{'browserFetchList':{'field':'','byType':'byCss','typeValue':'table.dataList > tbody > tr','innerCommands':[{'browserFetchKVs':{'fetchValueWithKeys':[{'field':'num','byType':'byCss','typeValue':'td:nth-child(1)','byAttr':'innerText','group':0,'valueType':'string','formatter':{'defaults':[],'customs':[]}},{'field':'title','byType':'byCss','typeValue':'td:nth-child(2)','byAttr':'innerText','group':0,'valueType':'string','formatter':{'defaults':[],'customs':[]}},{'field':'paperName','byType':'byCss','typeValue':'td:nth-child(3)','byAttr':'innerText','group':0,'valueType':'string','formatter':{'defaults':[],'customs':[]}},{'field':'esTime','byType':'byCss','typeValue':'td:nth-child(4)','byAttr':'innerText','group':0,'valueType':'string','formatter':{'defaults':[],'customs':[]}},{'field':'pubTime','byType':'byCss','typeValue':'td:nth-child(5)','byAttr':'innerText','group':0,'valueType':'string','formatter':{'defaults':[],'customs':[]}}]}}]}}]}}]";
		
		
		
		BrowserActor actor = new BrowserActor(null, null);
		try {
			actor.perform(1, "sina", 0, json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			actor.browserActContext().cleanup();
			actor.staticActContext().cleanup();
		}
		
	}
	
	@Test
	public void test02() {
		// 翻页 --> 列表 --> 内容
		String json = "[ { 'browserOpen': { 'urls': ['http://mil.news.sina.com.cn/jssd/'] } }, { 'browserPagination': { 'maxPage': 20, 'batchPage': 5, 'sleepMillis': 1000, 'waitMillis': 1000, 'strategy': 'next', 'byType': 'byCss', 'typeValue': 'div.page-wrap.clearfix > p > a:nth-last-child(2)', 'innerCommands': [ { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeight);', 'waitMillis': 2000 } }, { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeight);', 'waitMillis': 2000 } }, { 'browserScroll': { 'scrollScript': 'window.scrollTo(0, document.body.scrollHeight);', 'waitMillis': 2000 } }, { 'browserFetchList': { 'byType': 'byCss', 'typeValue': 'div.bc.w-list > div.blk_tw.clearfix', 'innerCommands': [ { 'browserFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'title', 'byType': 'byCss', 'typeValue': 'h3 > a', 'byAttr': 'innerText' }, { 'field': 'url', 'byType': 'byCss', 'typeValue': 'h3 > a', 'byAttr': 'href' } ] } } ] } } ] } }, { 'browserHandleList': { 'sleepMillis': 1000, 'innerCommands': [ { 'staticOpen': {'urls': ['$url']} }, { 'staticFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'time', 'byType': 'byCss', 'typeValue': '#page-tools > span > span.titer', 'byAttr': 'innerText' }, { 'field': 'source', 'byType': 'byCss', 'typeValue': '#media_name > a.ent1.fred', 'byAttr': 'innerText' }, { 'field': 'content', 'byType': 'byCss', 'typeValue': 'div#artibody', 'byAttr': 'outerHTML' } ] } } ] } } ]";
		
		
		BrowserActor actor = new BrowserActor(null, null);
		try {
			actor.perform(1, "sina", 0, json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			actor.browserActContext().cleanup();
			actor.staticActContext().cleanup();
		}
		
		
	}
}
