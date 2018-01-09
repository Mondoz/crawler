package com.hiekn.scraj.uyint.common.html.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import com.hiekn.scraj.uyint.common.html.CommonHtmlFilter;
import com.hiekn.scraj.uyint.common.html.CommonHtmlParser;
import com.hiekn.scraj.uyint.common.html.bean.ContainerTagParameter;
import com.hiekn.scraj.uyint.common.html.bean.DomTreeItem;
import com.hiekn.scraj.uyint.common.html.visitor.DivVisitor;
import com.hiekn.scraj.uyint.common.util.ConstResource;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 创建时间：2010-12-2 下午08:29:54
 * 
 */
public class CommonHtmlParserImpl implements CommonHtmlParser {
	private static final Log log = LogFactory.getLog(CommonHtmlParserImpl.class);
	
	NodeFilter divTagNameFilter;	//div过滤器，可以得到所有的div结点
	DivVisitor divVisitor;	//div访问器，可以得到div中的文本及其它信息
	NodeFilter titleTagNameFilter;	//title过滤器，可以得到title
	NodeFilter metaTagNameFilter;	//meta过滤器，可以得到meta
	NodeFilter h1TagNameFilter;	//title过滤器，可以得到title
	Parser parser;	//A html parser
//	ISearchUtil searchUtil = new SearchUtil();
		
	DomTreeItem treeItem;
	int itemId = 0;
	String tempText = "";
	String tempTagName = "";
//	String tempTagPath = "";
	String exactTagContent = "";
	
	String noiseRegex = "[\\s\\S]*?(来源|注册时间|发表于|作者)[\\:：][\\s\\S]*?";
	Map<String, Float> contentNoiseMap = new HashMap<String, Float>();
		
	public CommonHtmlParserImpl(){
		divTagNameFilter = new TagNameFilter("div");
		divVisitor = new DivVisitor();
		titleTagNameFilter = new TagNameFilter("title");
		metaTagNameFilter = new TagNameFilter("meta");
		h1TagNameFilter = new TagNameFilter("h1");
		parser = new Parser();
		contentNoiseMap.put("作者：", 0.3f);
	}
	
	/**
	 * author xiaohuqi
	 * @param pageSource 网页源码
	 * @return 对应网页的DOM树结点列表
	 * 获取网页的DOM树结点列表，其中对超链接tag有特殊的处理
	 */
	public List<DomTreeItem> parseHTMLToDOMTree(String pageSource){
		List<DomTreeItem> itemList = new ArrayList<DomTreeItem>();
		try{
			Parser parser = new Parser();
			parser.setInputHTML(pageSource);			
			NodeIterator nodeIterator = parser.elements();
			while(nodeIterator.hasMoreNodes()){
				itemList.addAll(getChildrenItemOfNode(nodeIterator.nextNode(), 0, "", "", 0));
			}
		}catch(Exception e){
			e.printStackTrace();			
		}
		return itemList;
	}
	
	/**
	 * author xiaohuqi
	 * @param node
	 * @param parentId
	 * @param xPath
	 * @param seqNo
	 * @return
	 * 递归获取DOM树的结点列表
	 */
	private List<DomTreeItem> getChildrenItemOfNode(Node node, int parentId, String xPath, String fullXPath, int seqNo){
		List<DomTreeItem> itemList = new ArrayList<DomTreeItem>();
		try{
			if(node instanceof TagNode){
				int id = ++ itemId;
				tempTagName = ((TagNode)node).getTagName();
				treeItem = new DomTreeItem();
				treeItem.setId(id);
				treeItem.setName(tempTagName);
				treeItem.setParentId(parentId);
				treeItem.setXPath(xPath + "/" + tempTagName);
				treeItem.setFullXPath(fullXPath + "/" + tempTagName + " " + seqNo);
				if(tempTagName.equalsIgnoreCase("a")){
					treeItem.setHref(((TagNode)node).getAttribute("href"));
				}
				treeItem.setText(((TagNode)node).toPlainTextString().trim());
				treeItem.setTagHtml(((TagNode)node).toHtml());
				
				itemList.add(treeItem);				
				NodeList nodeList = node.getChildren();
				if(nodeList != null){
					int tempSeqNo = 0;	//计数器复位
					for(int i=0;i<nodeList.size();i++){
						if(nodeList.elementAt(i) instanceof TagNode){
							itemList.addAll(getChildrenItemOfNode(nodeList.elementAt(i), id, xPath + "/" + ((TagNode)node).getTagName(),
									fullXPath + "/" + ((TagNode)node).getTagName() + " " + seqNo, tempSeqNo ++));
						}
						else{
							itemList.addAll(getChildrenItemOfNode(nodeList.elementAt(i), id, xPath + "/" + ((TagNode)node).getTagName(),
									fullXPath + "/" + ((TagNode)node).getTagName() + " " + seqNo, tempSeqNo));
						}						
					}
				}
				tempTagName = "";
			}
			else if(node instanceof TextNode){
				tempText = node.getText().trim();
				if(!tempText.equals("")){
					int id = ++ itemId;
					treeItem = new DomTreeItem();
					treeItem.setId(id);
					treeItem.setName(node.getText().trim());
					treeItem.setParentId(parentId);
					treeItem.setXPath(xPath + "/");
					treeItem.setFullXPath(fullXPath + "/");
					treeItem.setText(tempText);
					treeItem.setTagHtml("");
//					treeItem.setNode(node);
					itemList.add(treeItem);
				}
			}			
		}catch(Exception e){
			log.error("In getChildrenItemOfNode" + e);
			e.printStackTrace();
		}
		return itemList;
	}

	/**
	 * author xiaohuqi
	 * @param dtiList 
	 * @param xPath
	 * @return 标签中的内容
	 * 根据具体XPath获取标签中的内容
	 */
	public String getNodeContentByXPath(List<DomTreeItem> dtiList, String xPath){
		String nodeContent = "";
		try{
			for(DomTreeItem dti : dtiList){
				if(dti.getFullXPath().equals(xPath)){
					nodeContent = dti.getText();
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		return nodeContent;
	}
	
	/**
	 * author xiaohuqi
	 * @param dtiList 
	 * @param xPath
	 * @return 标签中的内容
	 * 根据具体XPath获取标签中的HTML
	 */
	public String getNodeHtmlByXPath(List<DomTreeItem> dtiList, String xPath){
		String nodeContent = "";
		try{
			for(DomTreeItem dti : dtiList){
				if(dti.getFullXPath().equals(xPath)){
					nodeContent = dti.getTagHtml();
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		return nodeContent;
	}
	
	/**
	 * @author xiaohuqi
	 * @param pageSource 源页面源码
	 * @param domTreeItemList	DOM树结点列表
	 * @param title	内面标题
	 * @param containerTag	包含内容标签，一般为<div>或<td>
	 * @param maxContainerTagDepth	包含内容标签中包含窗口标签的层数
	 * @return	返回页面的内容位置，失败返回-1
	 * 获取页面内容结果的位置
	 */
	public int getContentPos(String pageSource, List<DomTreeItem> domTreeItemList, String title, 
			int titlePos, String containerTag, int maxContainerTagDepth){
		int contentPos = -1;
		try{
			int pageLength = domTreeItemList.size();	//页面DOM树总结点个数
//			int titlePos = getTitlePos(domTreeItemList, title);	//标题DOM树结点位置
			float m = 0.5f;
//			float k = 20f;
//			float v = 0.3f;
//			System.out.println("--------------" + titlePos + "--------" + pageLength);
//			if(titlePos == -1 && title.length() > 2){	//没找到标题，可能是标题末尾有省略符号
//				titlePos = getTitlePos(domTreeItemList, title.substring(0, title.length() - 2));	//标题DOM树结点位置
//			}
			int powCount = 10;
			if(titlePos == -1){	//始终没找到标题，则置为链表长度的一半
				powCount = 4;
				if(pageLength < 300){
					titlePos = pageLength / 3;
				}
				else if(pageLength < 500){
					titlePos = pageLength / 4;
				}
				else if(pageLength < 800){
					titlePos = pageLength / 5;
				}
				else{
					titlePos = pageLength / 8;
				}
			}
			DomTreeItem dtItem;
			DomTreeItem tempItem;
			boolean matchFlag = false;
			int tempPos = -1;
			ContainerTagParameter ctp;
			int wordCount = 0;	//文字数目
			int linkWordCount = 0;	//链接文字数目
			int nomalTagCount = 0;	//普通标签数目
			int specialTagCount = 0;	//特殊标签数目<p><br />
			int linkTagCount = 0;	//链接标签数目
			String content = "";	//内容
			List<ContainerTagParameter> ctpList = new ArrayList<ContainerTagParameter>();
			
			//分析DIV标签
			if(containerTag.equalsIgnoreCase("div") || containerTag.equals("")){
				for(int i=0;i<domTreeItemList.size();i++){
					dtItem = domTreeItemList.get(i);
					if(dtItem.getXPath().endsWith("/DIV")){
						matchFlag = false;
						tempPos = -1;
						for(int j=i+1;j<domTreeItemList.size();j++){
							tempItem = domTreeItemList.get(j);
							if(!tempItem.getFullXPath().startsWith(dtItem.getFullXPath())){	//不是子标签
								tempPos = j;
								break;
							}
							//内嵌div层数大于2，一般不会是内容标签
							if(tempItem.getXPath().substring(dtItem.getXPath().length()).split("/DIV").length > maxContainerTagDepth){	
								matchFlag = true;
								break;
							}
						}
						if(!matchFlag){	//说明此DIV标签已是最底层DIV
							//接下来统计相关参数
							ctp = new ContainerTagParameter();
							wordCount = 0;	//文字数目
							linkWordCount = 0;	//链接文字数目
							nomalTagCount = 0;	//普通标签数目
							specialTagCount = 0;	//特殊标签数目<p><br />
							linkTagCount = 0;	//链接标签数目
							content = "";
							for(int j=i+1;j<tempPos;j++){	//遍历子标签
								tempItem = domTreeItemList.get(j);
								if(tempItem.getXPath().lastIndexOf("/DIV") >= dtItem.getXPath().length()){	//是子DIV标签中的内容，不计
									continue;
								}
								if(tempItem.getXPath().endsWith("/")){	//TextTag
									wordCount += tempItem.getText().length();
									content = content.concat(tempItem.getText());
									if(tempItem.getXPath().indexOf("/A/") != -1){	//超链接文字
										linkWordCount += tempItem.getText().length();
									}
								}
								else if(tempItem.getXPath().indexOf("/A") != -1){	//超链接标签
									++ linkTagCount;
								}
								else if(tempItem.getXPath().endsWith("P") || tempItem.getXPath().endsWith("BR") || 
										tempItem.getXPath().endsWith("/STRONG")){	//换行或段落标签
									content = content.concat(ConstResource.NEW_LINE_CHARACTER);
									++ specialTagCount;
								}
								else if(tempItem.getXPath().endsWith("/FONT")){
									-- nomalTagCount;
								}
								else{
									++ nomalTagCount;
								}
							}
							if(dtItem.getText().indexOf(title) != -1){
								wordCount = wordCount - title.length();
							}
							ctp.setWordCount(wordCount);
							ctp.setLinkWordCount(linkWordCount);
							ctp.setNomalTagCount(nomalTagCount);
							ctp.setSpecialTagCount(specialTagCount);
							ctp.setLinkTagCount(linkTagCount);
							ctp.setContent(content);
							ctp.setPosition(i);
							ctpList.add(ctp);
						}
					}
				}	//DIV标签处理for循环结束
			}	//end of if DIV标签处理结束
			//接下来处理TD标签
			if(containerTag.equalsIgnoreCase("td") || containerTag.equals("")){
				for(int i=0;i<domTreeItemList.size();i++){
					dtItem = domTreeItemList.get(i);
					if(dtItem.getXPath().endsWith("/TD")){
						matchFlag = false;
						tempPos = -1;
						for(int j=i+1;j<domTreeItemList.size();j++){
							tempItem = domTreeItemList.get(j);
							if(!tempItem.getFullXPath().startsWith(dtItem.getFullXPath())){	//不是子标签
								tempPos = j;
								break;
							}
							//内嵌TD层数大于1，一般不会是内容标签
							if(tempItem.getXPath().substring(dtItem.getXPath().length()).split("/TD").length > maxContainerTagDepth){	
								matchFlag = true;
								break;
							}
						}
						if(!matchFlag){	//说明此TD标签可认为是内容包含标签
							//接下来统计相关参数
							ctp = new ContainerTagParameter();
							//先初始化记录变量
							wordCount = 0;	//文字数目
							linkWordCount = 0;	//链接文字数目
							nomalTagCount = 0;	//普通标签数目
							specialTagCount = 0;	//特殊标签数目<p><br />
							linkTagCount = 0;	//链接标签数目
							content = "";	//内容
							for(int j=i+1;j<tempPos;j++){	//遍历子标签
								tempItem = domTreeItemList.get(j);
								if(tempItem.getXPath().lastIndexOf("/TD") >= dtItem.getXPath().length()){	//是子TD标签中的内容，不计
									continue;
								}
								if(tempItem.getXPath().endsWith("/")){	//TextTag
									wordCount += tempItem.getText().length();
									content = content.concat(tempItem.getText());
									if(tempItem.getXPath().indexOf("/A/") != -1){	//超链接文字
										linkWordCount += tempItem.getText().length();
									}
								}
								else if(tempItem.getXPath().indexOf("/A") != -1){	//超链接标签
									++ linkTagCount;
								}
								else if(tempItem.getXPath().endsWith("/P") || tempItem.getXPath().endsWith("/BR") || 
										tempItem.getXPath().endsWith("/STRONG")){	//换行或段落标签
									content = content.concat(ConstResource.NEW_LINE_CHARACTER);
									++ specialTagCount;
								}
								else if(tempItem.getXPath().endsWith("/FONT")){
									-- nomalTagCount;
								}
								else{
									++ nomalTagCount;
								}
							}
							if(dtItem.getText().indexOf(title) != -1){
								wordCount = wordCount - title.length();
							}
							ctp.setWordCount(wordCount);
							ctp.setLinkWordCount(linkWordCount);
							ctp.setNomalTagCount(nomalTagCount);
							ctp.setSpecialTagCount(specialTagCount);
							ctp.setLinkTagCount(linkTagCount);
							ctp.setContent(content);
							ctp.setPosition(i);
							ctpList.add(ctp);
						}
					}
				}	//TD标签处理for循环结束
			}	//end of if TD标签处理结束				
			
			int maxWordCount = 0;
			ContainerTagParameter maxCtp = null;
			for(ContainerTagParameter aCtp : ctpList){
				if(aCtp.getWordCount() - 5 * aCtp.getLinkWordCount() > ConstResource.CONTENT_MIN_CONFIRM_LENGTH && aCtp.getWordCount() > maxWordCount){
					maxWordCount = aCtp.getWordCount();
					maxCtp = aCtp;
				}
			}
			if(maxCtp != null){
//				return maxCtp.getPosition();
			}
			
			float posWeight = 0f;
			float numerator = 0;
			float denominator = 0;
			
			for(int i=ctpList.size()-1;i>=0;i--){
				ctp = ctpList.get(i);
				if(ctp.getWordCount() - ctp.getLinkWordCount() < ConstResource.CONTENT_MIN_LENGTH){
					ctpList.remove(i);
				}
				else if(domTreeItemList.get(titlePos).getFullXPath().startsWith(domTreeItemList.get(ctp.getPosition()).getFullXPath()) && 
						ctp.getWordCount() - 5 * ctp.getLinkWordCount() < 50){
					ctpList.remove(i);
				}
				else{
					if(ctp.getPosition() < titlePos){
						posWeight = (float)ctp.getPosition() / titlePos;
					}
					else{
						posWeight = (float)(pageLength - ctp.getPosition()) / (pageLength - titlePos);
					}
					if(ctp.getPosition() - titlePos < 10 && ctp.getContent().getBytes().length - ctp.getContent().length() < 20){
						posWeight = 3f * posWeight / 4f;
					}
					else if(ctp.getPosition() - titlePos < 20 && ctp.getContent().getBytes().length - ctp.getContent().length() < 20){
						posWeight = 7f * posWeight / 8f;
					}
//					else if(ctp.getPosition() < titlePos + pageLength / k){
//						if(ctp.getWordCount() > 50){
//							posWeight = 1f;
//						}
//						else{
//							posWeight = (float)((k * ctp.getPosition() - k * v * ctp.getPosition() + pageLength * v - titlePos * k + titlePos * k * v) / pageLength);
//						}
//					}
//					else{
//						posWeight = (float)(k * (ctp.getPosition() - pageLength) / (k * titlePos + pageLength - k * pageLength));
//					}
					if(ctp.getContent().matches(noiseRegex) && ctp.getWordCount() < 100){
						posWeight = 0.6f * posWeight;
					}
					
					posWeight = (float)Math.pow(posWeight, powCount);
//					ctp.setRelevance(posWeight);
//					posWeight = posWeight * posWeight;
//					posWeight = posWeight * posWeight;
//					posWeight = posWeight * posWeight;
//					posWeight = posWeight * posWeight;
					numerator = ctp.getWordCount() - 2 * ctp.getLinkWordCount();	//计算分子
					numerator = numerator > 0 ? numerator : 0l;
					denominator = ctp.getNomalTagCount() + 3 * ctp.getLinkTagCount() - 4 * ctp.getSpecialTagCount();	//计算分母
//					if(denominator == 0){
//						denominator = 1;
//					}
//					else{
//						denominator = denominator > 0 ? denominator : - 1 / denominator;
//					}
					if(denominator <= 0){
						denominator = m;
					}
//					ctp.setRelevance(1/denominator);
						
					ctpList.get(i).setScore(posWeight * numerator / denominator);	//设置最后得分
				}
			}			
//			ctpList = searchUtil.computeRelevance(title, ctpList);	//计算相关度

//			for(int i=ctpList.size()-1;i>=0;i--){
//				if(ctpList.get(i).getRelevance() == 0){
//					ctpList.get(i).setScore(ctpList.get(i).getScore() / 10);
////					ctpList.remove(i);
//				}
//			}
			ctpList = insertSort(ctpList);	//排序
			//接下来处理结果			
			if(ctpList.size() == 0){	//没结果
				contentPos = -1;
			}
			else if(ctpList.size() == 1){	//只有一个
				contentPos = ctpList.get(0).getPosition();
			}
			else if(ctpList.get(0).getWordCount() < 4 * ctpList.get(1).getWordCount()){	//多个结果
				String _xPath0 = domTreeItemList.get(ctpList.get(0).getPosition()).getXPath();
				String _xPath1 = domTreeItemList.get(ctpList.get(1).getPosition()).getXPath();
				String xPath0 = domTreeItemList.get(ctpList.get(0).getPosition()).getFullXPath();
				String xPath1 = domTreeItemList.get(ctpList.get(1).getPosition()).getFullXPath();
				if(_xPath0.endsWith("/TD") && _xPath1.endsWith("/DIV") && (xPath0.startsWith(xPath1) || xPath1.startsWith(xPath0))){
					int wq0 = ctpList.get(0).getWordCount();
					int wq1 = ctpList.get(1).getWordCount();					
					if(xPath0.startsWith(xPath1) && (wq1 - wq0) / wq1 < 0.2){
						contentPos = ctpList.get(0).getPosition();
					}
					else{
						String xPath10 = "";
						for(int i=2;i<(ctpList.size()>5?5:ctpList.size());i++){
							if(domTreeItemList.get(ctpList.get(i).getPosition()).getXPath().endsWith("/DIV")){
								xPath10 = domTreeItemList.get(ctpList.get(i).getPosition()).getFullXPath();
								break;
							}
						}
						if(!xPath10.equals("")){
							if(xPath10.substring(0, xPath10.lastIndexOf(" ")).equals(
									xPath1.substring(0, xPath1.lastIndexOf(" ")))){	//平行标签，取它们的父标签
								String contentXPath = xPath10.substring(0, xPath10.lastIndexOf("/"));
								
								int containerTagDivIndex = Math.max(contentXPath.lastIndexOf("/DIV"), contentXPath.lastIndexOf("/TD"));
								if(containerTagDivIndex != contentXPath.lastIndexOf("/")){
									contentXPath = contentXPath.substring(0, contentXPath.indexOf("/", containerTagDivIndex + 3));
								}
								for(int i=ctpList.get(1).getPosition()-1;i>0;i--){
									if(domTreeItemList.get(i).getFullXPath().equals(contentXPath)){
										contentPos = i;
										break;
									}
								}
							}
							else{
								contentPos = ctpList.get(1).getPosition();
							}
						}
						else{
							contentPos = ctpList.get(1).getPosition();
						}
					}
				}
				else if(_xPath0.endsWith("/DIV") && _xPath1.endsWith("/TD")){
					int wq0 = ctpList.get(0).getWordCount();
					int wq1 = ctpList.get(1).getWordCount();								
					if(xPath1.startsWith(xPath0) && (wq0 - wq1) / wq0 < 0.2){
						contentPos = ctpList.get(1).getPosition();
					}
					else{
						contentPos = ctpList.get(0).getPosition();
					}
				}
				else if(xPath0.substring(xPath0.lastIndexOf("/"), xPath0.lastIndexOf(" ")).equals(
						xPath1.substring(xPath1.lastIndexOf("/"), xPath1.lastIndexOf(" ")))){
					if(xPath0.startsWith(xPath1)){	//同一类型的标签，且其中一个以另一个开头
						contentPos = ctpList.get(1).getPosition();
					}
					else if(xPath1.startsWith(xPath0)){
						contentPos = ctpList.get(0).getPosition();
					}
					else if(xPath0.substring(0, xPath0.lastIndexOf("/")).equals(xPath1.substring(0, xPath1.lastIndexOf("/"))) && 
							ctpList.get(0).getPosition() > titlePos){
						String contentXPath = xPath0.substring(0, xPath0.lastIndexOf("/"));
						while(!contentXPath.substring(contentXPath.lastIndexOf("/")).startsWith("/DIV")){
							contentXPath = contentXPath.substring(0, contentXPath.lastIndexOf("/"));
						}
						DomTreeItem item;
						for(int h=0;h<domTreeItemList.size();h++){
							item = domTreeItemList.get(h);
							if(item.getFullXPath().equals(contentXPath)){
								contentPos = h;
								break;
							}
						}
					}
					else{
						contentPos = ctpList.get(0).getPosition();
					}
				}
				else{
					contentPos = ctpList.get(0).getPosition();
				}
			}
			else{
				contentPos = ctpList.get(0).getPosition();
			}
			
			
//			String contentXPath = domTreeItemList.get(contentPos).getXPath();
//			String contentFullXPath = domTreeItemList.get(contentPos).getFullXPath();
//			System.out.println(contentFullXPath);
//			int wqSum = 0;
//			double lwSum = 0.0;
////			
//			List<String> xPathList = new ArrayList<String>();
//			System.out.println(titlePos);
//			System.out.println("普通文字\t文字数\t链接文字\t普通标签\t特殊标签\t链接标签\t位置\t相关度\t\t分数\t\t内容\t\t");
//			int i=0;
//			for(ContainerTagParameter aCtp : ctpList){
//				if(!domTreeItemList.get(aCtp.getPosition()).getXPath().equals(contentXPath)){
//					continue;
//				}
//				System.out.println(i + "\t" + aCtp.getPosition() + "\t" + (aCtp.getWordCount() - aCtp.getLinkWordCount()) + "\t" + aCtp.getWordCount() + "\t" + 
//						aCtp.getLinkWordCount() + "\t" + aCtp.getNomalTagCount() + "\t" + aCtp.getSpecialTagCount() + "\t" + 
//						aCtp.getLinkTagCount() + "\t" + aCtp.getPosition() + "\t" + aCtp.getRelevance() + "\t" + 
//						aCtp.getScore() + "\t" + aCtp.getContent().replaceAll("\r\n", ""));
//				System.out.println(domTreeItemList.get(aCtp.getPosition()).getFullXPath() + "\n");
//				xPathList.add(domTreeItemList.get(aCtp.getPosition()).getFullXPath());
//				wqSum += aCtp.getWordCount() - aCtp.getLinkWordCount();
//				lwSum += aCtp.getRelevance();
//				if(++ i > 100){
//					break;
//				}
//			}
//			List<Integer> diffPosList = new ArrayList<Integer>();
//			int k = 0;
//			for(String xPath : xPathList){
//				while((i = xPath.indexOf('/', i)) != -1){
//					if(!xPath.substring(0, i).equals(contentFullXPath.substring(0, i))){
//						break;
//					}
//					++k;
//				}
//				diffPosList.add(k);
//			}
//			System.out.println(wqSum);
//			System.out.println(lwSum);
			
		}catch(Exception e){
			e.printStackTrace();
			log.error("In getContentXPath，Exception: " + e);
			return -1;
		}		
		return contentPos;
	}
	
	/**
	 * @author xiaohuqi
	 * @param pageSource 源页面源码
	 * @param domTreeItemList	DOM树结点列表
	 * @param title	内面标题
	 * @param containerTag	包含内容标签，一般为<div>或<td>
	 * @return	返回页面的内容位置，失败返回-1
	 * 获取页面内容结果的位置
	 */
	public int getContentPos(String pageSource, List<DomTreeItem> domTreeItemList, String title, 
			int titlePos, String containerTag){
		return getContentPos(pageSource, domTreeItemList, title, titlePos, containerTag, 4);
	}
	
	/**
	 * author xiaohuqi
	 * @param domTreeItemList 网页结点树列表
	 * @param title	标题
	 * @return	标题位置
	 * 搜索标题位置
	 */
	public int getTitlePos(List<DomTreeItem> domTreeItemList, String title){
		int titlePos = -1;
		try{
			String tempTitle = title;
			titlePos = matchTitle(domTreeItemList, title);
			while(titlePos == -1 && tempTitle.length() > title.length() / 3 && tempTitle.length() > 4){	//没找到标题，可能是标题末尾有省略符号或其它字符信息，从末尾开始截取
				tempTitle = tempTitle.substring(0, tempTitle.length() - 2);
				titlePos = matchTitle(domTreeItemList, tempTitle);	//标题DOM树结点位置
				if(titlePos != -1){
					return titlePos;
				}
			}
			tempTitle = title;
			while(titlePos == -1 && tempTitle.length() > title.length() / 3 && tempTitle.length() > 4){	//没找到标题，可能是标题开头有些其它字符信息
				tempTitle = tempTitle.substring(2, tempTitle.length());
				titlePos = matchTitle(domTreeItemList, tempTitle);	//标题DOM树结点位置
				if(titlePos != -1){
					return titlePos;
				}
			}
			tempTitle = title;
			while(titlePos == -1 && tempTitle.length() > title.length() / 5 && tempTitle.length() > 4){	//没找到标题，可能是标题首尾都有其它字符信息
				tempTitle = tempTitle.substring(1, tempTitle.length() - 1);
				titlePos = matchTitle(domTreeItemList, tempTitle);	//标题DOM树结点位置
				if(titlePos != -1){
					return titlePos;
				}
			}
			tempTitle = title;
			while(titlePos == -1 && tempTitle.length() > title.length() / 5 && tempTitle.length() > 4){	//没找到标题，可能是标题首尾都有其它字符信息
				tempTitle = tempTitle.substring(1, tempTitle.length() - 2);
				titlePos = matchTitle(domTreeItemList, tempTitle);	//标题DOM树结点位置
				if(titlePos != -1){
					return titlePos;
				}
			}
			tempTitle = title;
			while(titlePos == -1 && tempTitle.length() > title.length() / 5 && tempTitle.length() > 4){	//没找到标题，可能是标题首尾都有其它字符信息
				tempTitle = tempTitle.substring(2, tempTitle.length() - 1);
				titlePos = matchTitle(domTreeItemList, tempTitle);	//标题DOM树结点位置
				if(titlePos != -1){
					return titlePos;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("In getTitlePos, Exception: " + e);
		}
		return titlePos;
	}
	
	private int matchTitle(List<DomTreeItem> domTreeItemList, String title){
		int pos = -1;
		try{
			String tempXPath = "";
			int bodyPos = -1;
			int tempCount = 0;
			List<Integer> rescueList = new ArrayList<Integer>();
			for(int i=0;i<domTreeItemList.size();i++){
				if(bodyPos == -1 && domTreeItemList.get(i).getXPath().endsWith("/BODY")){
					bodyPos = i;
				}
				if(domTreeItemList.get(i).getXPath().indexOf("/BODY") != -1 && 
						domTreeItemList.get(i).getXPath().endsWith("/") && 
//						!domTreeItemList.get(i).getXPath().endsWith("/A/") && 
						domTreeItemList.get(i).getText().indexOf(title) != -1){
//					pos = i;
//					break;
					tempCount = 0;	
					tempXPath = domTreeItemList.get(i).getFullXPath();
					tempXPath = tempXPath.substring(0, tempXPath.length() - 1 );	
					if(domTreeItemList.get(i).getXPath().endsWith("/A/")){
						tempXPath = tempXPath.substring(0, tempXPath.lastIndexOf("/") - 1 );	
					}
					for(int j=i;j>0;j--){
						if(!domTreeItemList.get(j).getFullXPath().startsWith(tempXPath)){
							break;
						}
						if(domTreeItemList.get(j).getFullXPath().substring(tempXPath.length()).indexOf("/A") != -1){
							++ tempCount;
						}
					}
					if(tempCount < 4 && i - bodyPos > 10){
						pos = i;
						break;
					}
					rescueList.add(i);
				}
			}
			if(pos == -1 && rescueList.size() != 0){
				pos = rescueList.get(0);
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("In matchContent: " + e);
		}
		return pos; 
	}

	private List<ContainerTagParameter> insertSort(List<ContainerTagParameter> ctpList){
		List<ContainerTagParameter> returnList = new ArrayList<ContainerTagParameter>();
		try{
			float maxScore = 0l;
			int maxPos = 0;
			while(ctpList.size() > 0){
				maxScore = ctpList.get(0).getScore();
				maxPos = 0;
				for(int i=1;i<ctpList.size();i++){
					if(ctpList.get(i).getScore() > maxScore){
						maxScore = ctpList.get(i).getScore();
						maxPos = i;
					}
				}
				returnList.add(ctpList.get(maxPos));
				ctpList.remove(maxPos);				
			}			
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}		
		return returnList;
	}

	public String getContentText(String title, String source) {
		String content = "";
		CommonHtmlFilter htmlFilter = new CommonHtmlFilterImpl();
		source = htmlFilter.filterAFile(source);
		List<DomTreeItem> domTreeItemList = parseHTMLToDOMTree(source);
		int titlePos = getTitlePos(domTreeItemList, title);
		int contentPos = getContentPos(source, domTreeItemList, title, titlePos, "", 6);
		if (contentPos < 0) {
			return content;
		}
		String xpath = domTreeItemList.get(contentPos).getFullXPath();
		content = getNodeContentByXPath(domTreeItemList, xpath);
		content = htmlFilter.filterContentKeepBr(content).trim();
		return content;
	}

	public String getContentHtml(String title, String source) {
		String content = "";
		CommonHtmlFilter htmlFilter = new CommonHtmlFilterImpl();
		source = htmlFilter.filterAFile(source);
		List<DomTreeItem> domTreeItemList = parseHTMLToDOMTree(source);
		int titlePos = getTitlePos(domTreeItemList, title);
		int contentPos = getContentPos(source, domTreeItemList, title, titlePos, "", 6);
		if (contentPos < 0) {
			return content;
		}
		String xpath = domTreeItemList.get(contentPos).getFullXPath();
		content = getNodeHtmlByXPath(domTreeItemList, xpath);
		content = htmlFilter.filterContentKeepBr(content).trim();
		return content;
	}
	
}
