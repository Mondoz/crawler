package com.hiekn.scraj.uyint.common.html.bean;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 创建时间：2010-12-2 下午08:12:13
 * 
 */
public class DomTreeItem implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = -1861815746323825878L;
	
	private int id;
	private String name;
	private int level;
	private int parentId;
	private String xPath;
	private String fullXPath;
	private String href;
	private String text;
	private int seqNo;
	private String author;
	private String replyCount;
	private String tagHtml;
	
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getXPath() {
		return xPath;
	}
	public void setXPath(String xPath) {
		this.xPath = xPath;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getFullXPath() {
		return fullXPath;
	}
	public void setFullXPath(String fullXPath) {
		this.fullXPath = fullXPath;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(String replyCount) {
		this.replyCount = replyCount;
	}
	public String getTagHtml() {
		return tagHtml;
	}
	public void setTagHtml(String tagHtml) {
		this.tagHtml = tagHtml;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
