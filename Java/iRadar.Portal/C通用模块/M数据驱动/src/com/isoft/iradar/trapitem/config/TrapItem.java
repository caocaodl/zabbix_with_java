package com.isoft.iradar.trapitem.config;


public class TrapItem {
	private String key;
	private String result;
	private long interval;
	private String[] collectors = {};
	private String collector;
	private TrapTemplate template;
	
	
	public TrapTemplate getTemplate() {
		return template;
	}
	public void setTemplate(TrapTemplate template) {
		this.template = template;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public String[] collectors() {
		return collectors;
	}
	public void setCollectors(String collectors) {
		this.collectors = TrapConfig.split(collectors);
	}
	
	public String getCollector() {
		return collector;
	}
	public void setCollector(String collector) {
		this.collector = collector;
	}
}
