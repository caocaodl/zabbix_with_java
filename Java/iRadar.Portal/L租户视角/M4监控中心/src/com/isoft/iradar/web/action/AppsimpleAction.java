package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_HTTP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_FTP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_IMAP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_LDAP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_NNTP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_NTP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_NTP_STATUS_WIN;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_POP_STATUS;
import static com.isoft.iradar.common.util.ItemsKey.SIMP_SMTP_STATUS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.web.bean.Column.column;
import static com.isoft.iradar.web.bean.Key.value;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.bean.Column;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AppsimpleAction extends RadarBaseAction {

	CArray<Column> columns = array(
			column(_("HTTP Status"), 			    value(SIMP_HTTP_STATUS)),
			column(_("FTP Status"), 			    value(SIMP_FTP_STATUS)),
			column(_("LDAP Status"), 			    value(SIMP_LDAP_STATUS)),
			column(_("NNTP Status"), 			    value(SIMP_NNTP_STATUS)),
			column(_("NTP Status"), 			    value(array(SIMP_NTP_STATUS,SIMP_NTP_STATUS_WIN))),
			column(_("IMAP Status"), 		        value(SIMP_IMAP_STATUS)),
			column(_("POP Status"), 			    value(SIMP_POP_STATUS)),
			column(_("SMTP Status"),                value(SIMP_SMTP_STATUS))
		);

	@Override
	protected void doInitPage() {
		page("title","Simple App");
		page("file", "app_simple.action");
		page("hist_arg", new String[] { "groupid", "type" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] {"tenant/supervisecenter/appsimple.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		CWidget tsimpleappwidget=new CWidget();
		CForm simpleappform=new CForm();
		
		
		
		CTableInfo table = new CTableInfo(_("No values found."));
		CArray mons = array(_("virtualMachineName"),_("virtualMachineIp"),_("HTTP Status"),_("FTP Status"),_("LDAP Status"),_("NNTP Status"),
				            _("NTP Status"),_("IMAP Status"),_("POP Status"),_("SMTP Status"));
		table.setHeader(mons);

		CArray<Map> hosts=array();
		CHostGet option = new CHostGet();
		option.setGroupIds(IMonConsts.MON_VM);
		option.setOutput(new String[]{"hostid", "name"});
		option.setEditable(true);
		hosts = API.Host(getIdentityBean(), executor).get(option);
		CTable paging = getPagingLine(getIdentityBean(), executor, hosts, array("hostid"));
		
		CHostGet options = new CHostGet();
		options.setOutput(new String[] { "name", "hostid", "status","host" });
		options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong()); 
		options.setSelectInterfaces(new String[] { "ip" });
		options.setPreserveKeys(true);		//以主键作为Map中的key
		hosts = API.Host(getIdentityBean(), executor).get(options);
		
		for(Map host:hosts){
			String vmip=null;
			CArray<Map> ipmaps=Nest.value(host, "interfaces").asCArray();
			for(Map ipmap:ipmaps){
				vmip=Nest.value(ipmap, "ip").asString();
			}
			
			String hostname = Nest.value(host, "name").asString();
			Long hostid= Nest.value(host, "hostid").asLong();
			CArray cells = array(hostname, vmip);
			for(Column column: columns) {
				Object text = null;
				
				if(_("NTP Status").equals(column.getName())){
					String value = CommonUtils.getTargetLastValue(executor, getIdentityBean(), ItemsKey.SIMP_NTP_STATUS.getValue(), Nest.as(hostid).asString(), false, false);
					if(LatestValueHelper.NA.equals(value)){
						value = CommonUtils.getTargetLastValue(executor, getIdentityBean(), ItemsKey.SIMP_NTP_STATUS_WIN.getValue(), Nest.as(hostid).asString(), false, false);
						text = TvmUtil.getSimpleAppNtpWinStatus(value);
					}else{
						text = TvmUtil.getSimpleAppStatus(value);
					}
				}else
					text = TvmUtil.getSimpleAppStatus(column.cell(hostid));
				
				CArray<String> attrs = column.attrs();
				if(column.attrs() == null) {
					cells.add(text);
				}else {
					CCol c = new CCol(text);
					for(Entry<Object, String> entry: attrs.entrySet()) {
						c.attr(String.valueOf(entry.getKey()), entry.getValue());
					}
					cells.add(c);
				}
			}
			table.addRow(cells);
		}
		
		simpleappform.addItem(array(table,paging));
		tsimpleappwidget.addItem(simpleappform);
		tsimpleappwidget.show();
	}

}
