package com.isoft.web.bean.reportForms;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 资产信息
 * @author LiuBoTao
 *
 */
public class InverntoriesAction extends RadarBaseAction{
	private CArray csvRows = null;;

	@Override
	protected boolean doAjax(SQLExecutor executor) {
	
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// TODO Auto-generated method stub
		CArray fields = map(
				// maintenance
				"groupid" ,									array(T_RDA_INT, O_OPT, P_SYS,	null,null),
				"output" ,									array(T_RDA_INT, O_OPT, P_SYS,	null,null),
				"type" ,									array(T_RDA_INT, O_OPT, P_SYS,	null,null),
				"value" ,									array(T_RDA_INT, O_OPT, P_SYS,	null,null),
				"csv_export",array(T_RDA_STR, O_OPT, P_SYS,	null,		null)
			);
			check_fields(getIdentityBean(), fields);
			
			validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		}
	@Override
	protected void doInitPage() {
		page("title", _(""));
		page("file", "zichanInfor.action");
		page("js", new String[] { "imon/reportform.js" });
		page("css", new String[] {"lessor/reportcenter/inverntories.css"});
		if (isset(_REQUEST, "csv_export")) {
			csvRows  = array();
			page("type", detect_page_type(PAGE_TYPE_CSV));
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Inverntories_export")+time+".csv");
		}
	}

	@Override
	protected void doPermissions(SQLExecutor arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doAction(SQLExecutor executor) {
		if (isset(_REQUEST, "csv_export")) {
			csvRows.add(array("名称", "ip", "编号", "所属部门", "所在机房", "所在机柜", "操作系统", "厂商"));
			CHostGet ch=new CHostGet();
			ch.setOutput(new String[]{"name"});
			ch.setGroupIds(Long.parseLong(Nest.value(_REQUEST, "groupid").asString()));
			ch.setSelectInventory(new String[]{"os_full","hardware","software","url_a","type_full","vendor"});
			ch.setSelectInterfaces(new String[]{"ip"});
			CArray<Map> ca=API.Host(getIdentityBean(), executor).get(ch);
			if (ca.size() > 0) {
				for (Map map : ca) {
					String name = null; // 名称
					String hostInterface = null; // ip地址
					String os_full = null;
					String hardware = null;
					String software = null;
					String url_a = null;
					String type_full = null; // 操作系统
					String vendor = null; // 厂商
					Map systemType = new HashedMap();
					SystemDAO sys = new SystemDAO(executor);
					os_full = Nest.value(map, "inventory","os_full").asString();
					hardware = Nest.value(map, "inventory","hardware").asString();
					software = Nest.value(map,"inventory", "software").asString();
					url_a = Nest.value(map,"inventory", "url_a").asString();
					name = Nest.value(map,"name").asString();
					hostInterface = Nest.value(map,"interfaces","0", "ip").asString();
					type_full = Nest.value(map,"inventory", "type_full").asString();
					vendor = Nest.value(map,"inventory", "vendor").asString();
					if (empty(os_full)) {
						os_full = "-";
					}
					if (empty(hardware)) {
						hardware = "-";
					} else {
						systemType.put("type", "dept");
						systemType.put("dkey", hardware);
						List<Map> all = sys.doSystem(systemType);
						for (Map m : all) {
							hardware = (String) m.get("dlabel");
						}
					}
					if (empty(software)) {
						software = "-";
					} else {
						systemType.put("type", "motor_room");
						systemType.put("dkey", software);
						List<Map> all = sys.doSystem(systemType);
						for (Map m : all) {
							software = (String) m.get("dlabel");
						}
					}
					if (empty(url_a)) {
						url_a = "-";
					} else {
						systemType.put("type", "cabinet");
						systemType.put("dkey", url_a);
						List<Map> all = sys.doSystem(systemType);
						for (Map m : all) {
							url_a = (String) m.get("dlabel");
						}
					}
					if (empty(type_full)) {
						type_full = "-";
					} else {
						systemType.put("type", "system");
						systemType.put("dkey", type_full);
						List<Map> all = sys.doSystem(systemType);
						for (Map m : all) {
							type_full = (String) m.get("dlabel");
						}
					}
					if (empty(vendor)) {
						vendor = "-";
					} else {
						systemType.put("type", "firm");
						systemType.put("dkey", vendor);
						List<Map> all = sys.doSystem(systemType);
						for (Map m : all) {
							vendor = (String) m.get("dlabel");
						}
					}
					csvRows.add(array(name+"\t", hostInterface+"\t", os_full+"\t", hardware+"\t", software+"\t", url_a+"\t", type_full+"\t", vendor+"\t"));
				}
				print(rda_toCSV(csvRows));
	          	return;
			}
		} else {
			CArray<Map> data = new CArray<Map>();
		//	ReportFormsDAO rf = new ReportFormsDAO(executor);
			CArray<Map> limt = new CArray<Map>();
			if (isset(_REQUEST, "value") && !empty(Nest.value(_REQUEST, "value").asString())) {
				limt.put(Nest.value(_REQUEST, "type").asString(), Nest.value(_REQUEST, "value").asString());
			}
			Long ling=Nest.value(_REQUEST, "groupid").asLong();
			CHostGet ch=new CHostGet();
			ch.setOutput(new String[]{"name"});
			ch.setGroupIds(ling);
			ch.setSelectInventory(new String[]{"os_full","hardware","software","url_a","type_full","vendor"});
			ch.setSelectInterfaces(new String[]{"ip"});
			CArray<Map> ca=API.Host(getIdentityBean(), executor).get(ch);
			limt.put("groupid", Nest.value(_REQUEST, "groupid").asString());
			//List<Map> list1 = rf.dogroup(limt);
			data.put("list", ca);
			data.put("groupid", Nest.value(_REQUEST, "groupid").asString());
			CView maintenanceView = new CView("configuration.inventor.list", data);
			maintenanceView.render(getIdentityBean(), executor);
			maintenanceView.show();
		}

	}
}