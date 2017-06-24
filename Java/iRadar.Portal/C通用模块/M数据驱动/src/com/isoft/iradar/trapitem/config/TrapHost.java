package com.isoft.iradar.trapitem.config;

public class TrapHost {
	private String name;
	private String[] templates;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String[] getTemplates() {
		return templates;
	}
	public void setTemplate(String template) {
		this.templates = TrapConfig.split(template);
	}
	
	public static void main(String[] args) {
		TrapHost t = new TrapHost();
		t.setTemplate("1,2 ,3, 4 , 5\r\n6, \r\n 7, 8");
		for(String s: t.getTemplates()) {
			System.out.println("|"+s+"|");
		}
	}
}
