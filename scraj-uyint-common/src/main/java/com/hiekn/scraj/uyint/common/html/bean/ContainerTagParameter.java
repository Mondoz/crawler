package com.hiekn.scraj.uyint.common.html.bean;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 创建时间：2010-12-19 上午09:38:45
 * 容器标签的相关参数，用于定位正文内容
 */
public class ContainerTagParameter implements java.io.Serializable {
	private static final long serialVersionUID = -2340864693611743191L;

	private int wordCount;	//文字数目
	private int linkWordCount;	//链接文字数目
	private int nomalTagCount;	//普通标签数目
	private int specialTagCount;	//特殊标签数目<p><br />
	private int linkTagCount;	//链接标签数目
	private String content;	//内容
	private int position;	//位置
	private float relevance;	//相关度
	private float lw;
	private float tf;
	private float score;	//最终得分
	
	public int getLinkWordCount() {
		return linkWordCount;
	}
	public void setLinkWordCount(int linkWordCount) {
		this.linkWordCount = linkWordCount;
	}
	public int getNomalTagCount() {
		return nomalTagCount;
	}
	public void setNomalTagCount(int nomalTagCount) {
		this.nomalTagCount = nomalTagCount;
	}
	public int getSpecialTagCount() {
		return specialTagCount;
	}
	public void setSpecialTagCount(int specialTagCount) {
		this.specialTagCount = specialTagCount;
	}
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
	public int getLinkTagCount() {
		return linkTagCount;
	}
	public void setLinkTagCount(int linkTagCount) {
		this.linkTagCount = linkTagCount;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public float getRelevance() {
		return relevance;
	}
	public void setRelevance(float relevance) {
		this.relevance = relevance;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public float getLw() {
		return lw;
	}
	public void setLw(float lw) {
		this.lw = lw;
	}
	public float getTf() {
		return tf;
	}
	public void setTf(float tf) {
		this.tf = tf;
	}
	
}
