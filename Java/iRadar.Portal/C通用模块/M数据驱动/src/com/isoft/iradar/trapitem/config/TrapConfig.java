package com.isoft.iradar.trapitem.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.iradar.core.utils.EasyList;
import com.isoft.types.CArray;

public class TrapConfig {
	protected final static Logger LOG = LoggerFactory.getLogger(TrapConfig.class);
	public final static String CONFIG_FILE_NAME = "collect_iaas.xml";
	
	private Digester digester;
	private File configFile;
	private Long lastModified = 0L;
	
	private List<TrapHost> hosts = EasyList.build();
	private CArray<TrapTemplate> templates = CArray.map();
	private CArray<TrapCollector> collectors = CArray.map();
	private String init;
	private String[] vmTemplates;
	
	public static String[] split(String str) {
		return str.split("[\\s,]+");
	}
	
	public TrapConfig() {
		String fileName = this.getClass().getClassLoader().getResource(CONFIG_FILE_NAME).getFile();
		try {
			fileName = URLDecoder.decode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		this.configFile = new File(fileName);
		this.digester = new Digester();
		addRule(this.digester);
	}
	
	private void addRule(Digester dg) {
		dg.addSetProperties("root", "vm_templates", "vmtpls");
		
		dg.addCallMethod("root/init", "setInit", 0);
		
		dg.addObjectCreate("root/host", TrapHost.class);
		dg.addSetProperties("root/host");
		dg.addSetNext("root/host", "addHost");
		
		dg.addObjectCreate("root/collectors/admin/collector", TrapCollectAdmin.class);
		dg.addSetProperties("root/collectors/admin/collector");
		dg.addCallMethod("root/collectors/admin/collector", "setScript", 0);
		dg.addSetNext("root/collectors/admin/collector", "addCollector", TrapCollector.class.getName());
		
		dg.addObjectCreate("root/collectors/tenant/collector", TrapCollectTenant.class);
		dg.addSetProperties("root/collectors/tenant/collector");
		dg.addCallMethod("root/collectors/tenant/collector", "setScript", 0);
		dg.addSetNext("root/collectors/tenant/collector", "addCollector", TrapCollector.class.getName());
		
		dg.addObjectCreate("root/collectors/ovirt/collector", TrapCollectOvirt.class);
		dg.addSetProperties("root/collectors/ovirt/collector");
		dg.addCallMethod("root/collectors/ovirt/collector", "setScript", 0);
		dg.addSetNext("root/collectors/ovirt/collector", "addCollector", TrapCollector.class.getName());
		
		dg.addObjectCreate("root/template", TrapTemplate.class);
		dg.addSetProperties("root/template");
		
		dg.addSetProperties("root/template/ovirt", "collector", "collector");
		
		dg.addObjectCreate("root/template/admin/item", TrapItem.class);
		dg.addSetProperties("root/template/admin/item");
		dg.addCallMethod("root/template/admin/item", "setResult", 0);
		dg.addSetNext("root/template/admin/item", "addAdminItem");
		
		dg.addObjectCreate("root/template/tenant/item", TrapItem.class);
		dg.addSetProperties("root/template/tenant/item");
		dg.addCallMethod("root/template/tenant/item", "setResult", 0);
		dg.addSetNext("root/template/tenant/item", "addTenantItem");
		
		dg.addObjectCreate("root/template/ovirt/item", TrapItem.class);
		dg.addSetProperties("root/template/ovirt/item");
		dg.addCallMethod("root/template/ovirt/item", "setResult", 0);
		dg.addSetNext("root/template/ovirt/item", "addOvirtItem");
		
		dg.addSetNext("root/template", "addTemplate");
	}
	
	public void addHost(TrapHost host) {
		this.hosts.add(host);
	}
	public void addTemplate(TrapTemplate template) {
		this.templates.put(template.getName(), template);
	}
	public void addCollector(TrapCollector collector) {
		this.collectors.put(collector.getKey(), collector);
	}
	public String getInit() {
		return init;
	}
	public String[] getVmTemplates() {
		return vmTemplates;
	}
	
	public void setVmtpls(String vmTemplates) {
		this.vmTemplates = split(vmTemplates);
	}
	public void setInit(String init) {
		this.init = init;
	}
	public CArray<TrapCollector> getCollectors(){
		return collectors;
	}
	public List<TrapHost> getHosts(){
		return hosts;
	}
	public CArray<TrapTemplate> getTemplates(){
		return templates;
	}
	
	public void reload() {
		if(!needReload()) return;
		
		this.hosts.clear();
		this.templates.clear();
		this.digester.clear();
		this.digester.push(this);
		try {
			this.digester.parse(this.configFile);
		} catch (Exception e) {
			LOG.error("parse collect file exception:"+CONFIG_FILE_NAME, e);
		}
	}
	
	private boolean needReload() {
		long timestamp = this.configFile.lastModified();
		boolean result = timestamp > 0 && timestamp > this.lastModified;
		this.lastModified = timestamp;
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		TrapConfig tc = new TrapConfig();
		while(true) {
			tc.reload();
			System.out.println(tc.getHosts().size() + " " + tc.getTemplates().size());
			Thread.sleep(1000);
		}
	}
}
