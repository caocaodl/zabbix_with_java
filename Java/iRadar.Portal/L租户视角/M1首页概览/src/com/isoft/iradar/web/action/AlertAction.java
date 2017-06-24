package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AlertAction extends RadarBaseAction{
	
	@Override
	protected void doInitPage() {
		String name=Nest.value(_REQUEST,"title").asString();
		page("title", name);
		page("file", "tenan.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
		
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		templateid	TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"tenanid",					array(T_RDA_INT, O_OPT,	null,	null,		null),
			"title",					array(T_RDA_STR, O_OPT,	null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	
	}

	@Override
	public void doAction(SQLExecutor executor) {
		    CForm cf=new CForm();
			AnnouncementDAO an =new AnnouncementDAO(executor);
			String tenanid=Nest.value(_REQUEST,"tenanid").asString();
			CArray<Map> id =new CArray<Map>();
			id.put("announcementid",tenanid);
			List<Map> list=an.doListOne(id);
//			if(list.size()>0){
//				CTable table = new CTable();
//				CRow row1=new CRow();
//		/*		CSpan content=new CSpan("","tenanteditletwo");
//				content.addItem(list.get(0).get("content"));*/
//				CTextArea contentarea = new CTextArea("content", list.get(0).get("content").toString());
//				//table.addItem(row);
//				row1.addItem(contentarea);
//				table.addItem(row1);
//				cf.addItem(table);
//				cf.show();
//			}
			if(list.size()>0){
				CTextArea contentarea = new CTextArea("content", list.get(0).get("content").toString());
				contentarea.attr("readonly", "readonly");
				contentarea.addStyle("padding: 20px 0px 0px 0px; background-color: #FFFFFF;");
				contentarea.show();
			}
		
	}
	
	
	
}
