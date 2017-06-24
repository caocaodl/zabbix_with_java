package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.types.CArray.array;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.biz.daoimpl.reportForms.ReportFormsDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationInventorList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();
		CDiv exporedata = new CDiv("", "exportclass");
		CForm createForm = new CForm("get");
		createForm.addItem(new CSubmit("csv_export", _("Export to CSV"), "", "orange export"));
		CTextBox cb = new CTextBox("groupid", Nest.value(data, "groupid").asString());
		cb.addStyle("display:none");
		createForm.addItem(cb);
		CTableInfo table = new CTableInfo();

		table.setHeader(array("名称", "ip", "编号", "所属部门", "所在机房", "所在机柜", "操作系统", "厂商"));

		CArray<Map> list =Nest.value(data, "list").asCArray();

		for (Map map : list) {
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
			if (empty(hostInterface)) {
				hostInterface = "-";
			}
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

			((CTableInfo) table).addRow(array(name, hostInterface, os_full, hardware, software, url_a, type_full, vendor));
		}
		createForm.addItem(array(table));
		exporedata.addItem(createForm);
		maintenanceWidget.addItem(exporedata);
		return maintenanceWidget;
	}

}
