package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;


public class CConfigurationReportUseEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		CSubmit cb=new CSubmit("export","导出线行数据");
		createForm.addItem(cb);
        CDiv div=new CDiv();
        div.attr("id", "dialog");
        createForm.addItem(div);
        Object fromDate = null;
        Object toDate = null;
        fromDate = rdaDateToTime(Nest.value(data,"seven").asString());
        toDate = rdaDateToTime(Nest.value(data,"now").asString());
		CSpan aa=new CSpan();
		aa.addItem("起始时间:");
		createForm.addItem(aa);
		createForm.addItem( createDateSelector("active_since", fromDate, "active_till"));
		CSpan bb=new CSpan();
		bb.addItem("结束时间:");
		createForm.addItem(bb);
		createForm.addItem(createDateSelector("active_till", toDate, "active_since"));
		CSubmit se=new CSubmit("select","查询");
		createForm.addItem(se);
		createForm.addItem(makeFormFooter(
				
					new CButtonCancel()
				
			));
		maintenanceWidget.addItem(createForm);
		return maintenanceWidget;
	}
}
