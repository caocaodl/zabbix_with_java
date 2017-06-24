package com.isoft.web.bean;

import static com.isoft.iradar.Cphp._;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class LicenseAction extends HomeAction {
	
	private final static String SQL_GET_HOSTS = "select h.host,h.name alias,p.host proxy "+
																			"from hosts h "+
																			"left join hosts p on p.status=5 and h.proxy_hostid=p.hostid "+
																			"where h.hostid_os is not null "+
																			"and exists( "+
																			"	select 1 from hosts_groups hg "+
																			"	where hg.groupid = 201 "+
																			"	and hg.hostid = h.hostid "+
																			"	and hg.tenantid = h.tenantid "+
																			") and h.tenantid=#{tenantid} "+
																			"order by h.name asc";

	public String download() {
		final IdentityBean idBean = getIdentityBean();
		List<Map> cloudHosts = CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator<List<Map>>() {
			@Override
			public List<Map> doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map params = new HashMap();
				params.put("tenantid", idBean.getTenantId());
				return executor.executeNameParaQuery(SQL_GET_HOSTS, params);
			}
		});
		
		try {
			doGenCloudHostLicense(idBean, cloudHosts);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void doGenCloudHostLicense(IdentityBean idBean, List<Map> cloudHosts) throws IOException {
		HttpServletResponse response = getResponse();
		
		response.setHeader("Pragma","No-cache");   
		response.setHeader("Cache-Control","no-cache");   
		response.setDateHeader("Expires", 0); 

		response.setContentType("Application/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment; filename="+ this.getZipFilename());
		
		ZipOutputStream zos = new ZipOutputStream(this.getResponse().getOutputStream());
		
		if (cloudHosts != null && !cloudHosts.isEmpty()) {
			for (Map host : cloudHosts) {
				String aliasname = Nest.value(host, "alias").asString(true);
				String hostname = Nest.value(host, "host").asString(true);
				String proxyname = Cphp.empty(Nest.value(host, "proxy").asString())?Feature.iradarServer:Nest.value(host, "proxy").asString();
				if (proxyname == null) {
					proxyname = "";
				}
				
				String linuxLicense = _("iradar_active_agent.license.for.linux")
						.replace("#{tenantid}", idBean.getTenantId())
						.replace("#{hostname}", hostname)
						.replace("#{proxy}", proxyname);
				
				String windowsx86License = _("iradar_active_agent.license.for.windows.x86")
						.replace("#{tenantid}", idBean.getTenantId())
						.replace("#{hostname}", hostname)
						.replace("#{proxy}", proxyname);
				
				String windowsx64License = _("iradar_active_agent.license.for.windows.x64")
						.replace("#{tenantid}", idBean.getTenantId())
						.replace("#{hostname}", hostname)
						.replace("#{proxy}", proxyname);
				
				try {
					aliasname = aliasname.replace('/', '_');
					doZip(_("cloud.hosts.license")+"/linux/"+ aliasname, "iradar_agentd.license", linuxLicense.getBytes(), zos);
					doZip(_("cloud.hosts.license")+"/windows.x86/"+ aliasname, "iradar_agentd.license", windowsx86License.getBytes(), zos);
					doZip(_("cloud.hosts.license")+"/windows.x64/"+ aliasname, "iradar_agentd.license", windowsx64License.getBytes(), zos);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (Role.isTenant(idBean.getTenantRole())) {
			if (cloudHosts != null && !cloudHosts.isEmpty()) {
				doZip(null,"readme.txt", _("License Readme").getBytes(), zos);
			} else {
				doZip(null,"readme.txt", _("License Readme without cloud hosts").getBytes(), zos);
			}
		} else {
			String linuxLicense = _("iradar_passive_agent.license.for.linux")
					.replace("#{tenantid}", idBean.getTenantId())
					.replace("#{server}", Feature.iradarServer);
			doZip(_("server.hosts.license")+"/linux", "iradar_agentd.license", linuxLicense.getBytes(), zos);
			
			String windowsx86License = _("iradar_passive_agent.license.for.windows.x86")
					.replace("#{tenantid}", idBean.getTenantId())
					.replace("#{server}", Feature.iradarServer);
			doZip(_("server.hosts.license")+"/windows.x86", "iradar_agentd.license", windowsx86License.getBytes(), zos);
			
			String windowsx64License = _("iradar_passive_agent.license.for.windows.x64")
					.replace("#{tenantid}", idBean.getTenantId())
					.replace("#{server}", Feature.iradarServer);
			doZip(_("server.hosts.license")+"/windows.x64", "iradar_agentd.license", windowsx64License.getBytes(), zos);
			
			doZip(null,"readme.txt", _("License Readme").getBytes(), zos);
		}
		zos.flush();
		zos.close();
	}
	
	private void doZip(String base, String name, byte[] bytes, ZipOutputStream zos) throws IOException {
		if (base == null || base.length() == 0) {
			zos.putNextEntry(new ZipEntry(name));
		} else {
			zos.putNextEntry(new ZipEntry(base + "/" + name));
		}
		zos.write(bytes);
	}
	
	private String getZipFilename() {
		String dig = Long.toString(System.currentTimeMillis()%100000000, 32);
		return "agent.license." + dig + ".zip";
	}	
}
