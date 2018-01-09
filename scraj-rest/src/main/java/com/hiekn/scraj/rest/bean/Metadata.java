package com.hiekn.scraj.rest.bean;

public class Metadata {
	
	private Integer id;
	private String name;
	private Long intervalTime;
	private String intervalExp;
	private Integer intervalType;
	private String cookie;
	private String conf;
	private Integer confType;
	private String confGraph;
	private Integer confPriority;
	private Integer confParallel;
	private Integer completed;
	private Integer switchFlag;
	private Integer groupId;
	
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getIntervalTime() {
		return intervalTime;
	}
	public void setIntervalTime(long intervalTime) {
		this.intervalTime = intervalTime;
	}
	public String getIntervalExp() {
		return intervalExp;
	}
	public void setIntervalExp(String intervalExp) {
		this.intervalExp = intervalExp;
	}
	public int getIntervalType() {
		return intervalType;
	}
	public void setIntervalType(int intervalType) {
		this.intervalType = intervalType;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getConf() {
		return conf;
	}
	public void setConf(String conf) {
		this.conf = conf;
	}
	public int getConfType() {
		return confType;
	}
	public void setConfType(int confType) {
		this.confType = confType;
	}
	public String getConfGraph() {
		return confGraph;
	}
	public void setConfGraph(String confGraph) {
		this.confGraph = confGraph;
	}
	public int getConfPriority() {
		return confPriority;
	}
	public void setConfPriority(int confPriority) {
		this.confPriority = confPriority;
	}
	public Integer getConfParallel() {
		return confParallel;
	}
	public void setConfParallel(Integer confParallel) {
		this.confParallel = confParallel;
	}
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	public int getSwitchFlag() {
		return switchFlag;
	}
	public void setSwitchFlag(int switchFlag) {
		this.switchFlag = switchFlag;
	}
}
