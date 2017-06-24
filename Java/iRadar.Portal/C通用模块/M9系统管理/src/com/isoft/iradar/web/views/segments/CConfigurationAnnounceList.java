package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationAnnounceList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();

		// create form
		CForm maintenanceForm = new CForm();
		maintenanceForm.setName("maintenanceForm");
		
		CToolBar tb = new CToolBar(maintenanceForm);
		tb.addSubmit("form", "创建公告", "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("enable","启用公告" );
		goOption.setAttribute("confirm", "确定启用公告吗");
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("cease","停用公告" );
		goOption.setAttribute("confirm", "确定停用公告吗");
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", "确定删除公告吗");
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"announcementids\";");
		rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"announcementid").$()+"\";");		
		rda_add_post_js("cookie.prefix = \""+Nest.value(data,"announcementid").$()+"\";");
		
		// header
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		maintenanceWidget.addItem(headerActions);

		// create table
		CTableInfo maintenanceTable = new CTableInfo(_("No maintenance periods found."));
		maintenanceTable.setHeader(array(
			new CCheckBox("all_announcementids", false, "checkAll(\""+maintenanceForm.getName()+"\", \"all_announcementids\", \"announcementids\");"),
			"标题",
			"发布人",
			"生效时间",
			"停止时间",
			"状态"
		));
		
		String activeSince = null;
		String activeTill = null;
		
		for(Map maintenance :(CArray<Map>)Nest.value(data,"maintenances").asCArray()) {
			Integer maintenanceid = Nest.value(maintenance, "announcementid").asInteger();
			String user = Nest.value(maintenance, "issuer").asString();
			if ("".equals(user) || user == null) {
				user = "-";
			}
			activeSince = Nest.value(maintenance, "active_since").asString();
			activeTill = Nest.value(maintenance, "active_till").asString();
			Long timestamp = Long.parseLong(activeSince) * 1000; // 开始时间
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
			Long timestamp1 = Long.parseLong(activeTill) * 1000; // 结束时间
			String date1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp1));
			Date date2 = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowdate = format.format(date2);
			Date now_date1 = null; // 系统时间
			int stu = Nest.value(maintenance, "status").asInteger();// 状态
			String sta = "";
			if (stu == 3) {

				sta = "停用";
			} else {
				try {
					now_date1 = format.parse(nowdate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Long nowCompare = now_date1.getTime();
				int resultstart = nowCompare.compareTo(timestamp);
				int resultEnd = nowCompare.compareTo(timestamp1);

				if (resultstart >= 0 && resultEnd <= 0) {
					sta = "生效";// 生效
				}
				if (resultEnd >= 0) {
					sta = "已过期";// 已过期

				}
				if (resultstart < 0) {
					sta = "未开始";// 已过期

				}
			}
			maintenanceTable.addRow(array(
				new CCheckBox("announcementids["+Nest.as(maintenanceid).asString()+"]", false, null, Nest.as(maintenanceid).asString()),
				new CLink(Nest.value(maintenance,"title").$(), "announce.action?form=update&announcementid="+maintenanceid),
				user,
				date,
				date1,
				sta
				
			));
		}

		// append table to form
		maintenanceForm.addItem(array(maintenanceTable, Nest.value(data,"paging").$()));

		// append form to widget
		maintenanceWidget.addItem(maintenanceForm);
		return maintenanceWidget;
	}
}
