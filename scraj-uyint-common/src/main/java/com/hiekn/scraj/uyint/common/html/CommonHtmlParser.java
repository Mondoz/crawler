package com.hiekn.scraj.uyint.common.html;

import java.util.List;

import com.hiekn.scraj.uyint.common.html.bean.DomTreeItem;

public interface CommonHtmlParser {
	
	/**
	 * author xiaohuqi
	 * @param pageSource 网页源码
	 * @param pageTitle 网页标题
	 * @return 对应网页的DOM树结点列表
	 * 获取网页的DOM树结点列表，其中对超链接tag有特殊的处理
	 */
	List<DomTreeItem> parseHTMLToDOMTree(String pageSource);
	
	/**
	 * author xiaohuqi
	 * @param dtiList 
	 * @param xPath
	 * @return 标签中的内容
	 * 根据具体XPath获取标签中的内容
	 */
	String getNodeContentByXPath(List<DomTreeItem> dtiList, String xPath);
	
	/**
	 * author xiaohuqi
	 * @param dtiList 
	 * @param xPath
	 * @return 标签中的内容
	 * 根据具体XPath获取标签中的HTML
	 */
	String getNodeHtmlByXPath(List<DomTreeItem> dtiList, String xPath);
	
	/**
	 * @author xiaohuqi
	 * @param pageSource 源页面源码
	 * @param domTreeItemList	DOM树结点列表
	 * @param title	内面标题
	 * @param containerTag	包含内容标签，一般为<div>或<td>
	 * @return	返回页面的内容位置
	 * 获取页面内容结果的位置
	 */
	int getContentPos(String pageSource, List<DomTreeItem> domTreeItemList, String title, 
			int titlePos, String containerTag);
	
	/**
	 * @author xiaohuqi
	 * @param pageSource 源页面源码
	 * @param domTreeItemList	DOM树结点列表
	 * @param title	内面标题
	 * @param containerTag	包含内容标签，一般为<div>或<td>
	 * @param maxContainerTagDepth	包含内容标签中包含窗口标签的层数，之前默认为4
	 * @return	返回页面的内容位置，失败返回-1
	 * 获取页面内容结果的位置
	 */
	int getContentPos(String pageSource, List<DomTreeItem> domTreeItemList, String title, 
			int titlePos, String containerTag, int maxContainerTagDepth);
	
	/**
	 * author xiaohuqi
	 * @param domTreeItemList 网页结点树列表
	 * @param title	标题
	 * @return	标题位置
	 * 搜索标题位置
	 */
	int getTitlePos(List<DomTreeItem> domTreeItemList, String title);
	
	/**
	 * 
	 * 获取内容文本
	 * 
	 * @param title
	 * @param source
	 * @return
	 */
	String getContentText(String title, String source);
	
	/**
	 * 
	 * 获取内容html
	 * 
	 * @param title
	 * @param source
	 * @return
	 */
	String getContentHtml(String title, String source);
	
}
