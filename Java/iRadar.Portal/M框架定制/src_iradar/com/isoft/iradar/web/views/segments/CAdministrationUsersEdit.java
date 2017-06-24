package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_substr;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationUsersEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.users.edit.js");

		CWidget userWidget = new CWidget();

		if (Nest.value(data,"is_profile").asBoolean()) {
			userWidget.addPageHeader("");
		} else {
			userWidget.addPageHeader("");
		}
		// create form
		CForm userForm = new CForm();
		userForm.setName("userForm");
		userForm.addVar("config", get_request("config", 0));
		userForm.addVar("form", Nest.value(data,"form").$());
		userForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
        userForm.addVar("userid", Nest.value(data,"userid").$());
		
		/* Media tab */
		CFormList userMediaFormList = null;
		if (uint_in_array(CWebUser.getType(), array(USER_TYPE_IRADAR_ADMIN, USER_TYPE_SUPER_ADMIN))) {
			userMediaFormList = new CFormList("userMediaFormList");
			userForm.addVar("user_medias", Nest.value(data,"user_medias").$());

			CTableInfo mediaTableInfo = new CTableInfo(_("No media found."));

			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"user_medias").asCArray()).entrySet()) {
			    Object id = e.getKey();
			    Map media = e.getValue();
				CLink status = null;
				if (!isset(media,"active") || !Nest.value(media,"active").asBoolean()) {
					status = new CLink(_("Enabled"), "#", "enabled");
					status.onClick("return create_var(\""+userForm.getName()+"\",\"disable_media\","+id+", true);");
				} else {
					status = new CLink(_("Disabled"), "#", "disabled");
					status.onClick("return create_var(\""+userForm.getName()+"\",\"enable_media\","+id+", true);");
				}

				String mediaUrl = "?dstfrm="+userForm.getName()+
								"&media="+id+
								"&mediatypeid="+Nest.value(media,"mediatypeid").$()+
								"&sendto="+urlencode(Nest.value(media,"sendto").asString())+
								"&period="+Nest.value(media,"period").$()+
								"&severity="+Nest.value(media,"severity").$()+
								"&active="+Nest.value(media,"active").$();

				CArray<CSpan> mediaSeverity = array();
				for (Entry<Object, String> sc : getSeverityCaption(idBean, executor).entrySet()) {
				    int key = Nest.as(sc.getKey()).asInteger();
				    String caption = sc.getValue();
					int mediaActive = (Nest.value(media,"severity").asInteger() & (1 << key));
             
					CSpan cspan = new CSpan(rda_substr(caption, 0, 1), mediaActive>0 ? "enabled" : null);
					cspan.setHint(caption+(mediaActive>0 ? " (on)" : " (off)"), "", "", false ,true);
					Nest.value(mediaSeverity,key).$(cspan);
				}
				String mediaid =Nest.value(media,"mediatypeid").asString();
				CArray<Map> types = DBfetchArrayAssoc(DBselect(executor,
						"SELECT mt.mediatypeid,mt.description"+
						" FROM media_type mt WHERE mt.tenantid='-'"
					), "mediatypeid");
					CArrayHelper.sort(types, array("description"));
					for (Entry<Object, Map> f : types.entrySet()) {
					    Map type = f.getValue();
						if(Nest.value(type,"mediatypeid").asString().equals(mediaid)){
							Nest.value(media,"description").$(Nest.value(type,"description").asString());
						}
					} 
				mediaTableInfo.addRow(array(
					new CCheckBox("user_medias_to_del["+id+"]", false, null, Nest.as(id).asInteger()),
					new CSpan(Nest.value(media,"description").$(), "nowrap"),
					new CSpan(Nest.value(media,"sendto").$(), "nowrap"),
					new CSpan(Nest.value(media,"period").$(), "nowrap"),
					mediaSeverity,
					status,
					new CButton("edit_media", _("Edit"), "return PopUp(\"popup_media.action"+mediaUrl+"\", 550, 400);", "link_menu edit"))
				);
			}


			CButton delUser=new CButton("del_user_media",_("Delete selected"),"checkSubmit()","input link_menu remove");

			
			userMediaFormList.addRow(_("Media Setting"), array(mediaTableInfo,
				new CButton("add_media", _("Add"), "return PopUp(\"popup_media.action?dstfrm="+userForm.getName()+"\", 550, 400);", "link_menu new"),
				SPACE,
				SPACE,
				(count(Nest.value(data,"user_medias").$()) > 0) ? delUser : null
			));
		}

		// append form lists to tab
		CTabView userTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			userTab.setSelected("0");
		}
		//userTab.addTab("userTab", _("User"), userFormList);
		if (isset(userMediaFormList)) {
			userTab.addTab("mediaTab", _("Media Setting"), userMediaFormList);
		}

		// append tab to form
		userForm.addItem(userTab);

		// append buttons to form
		if (empty(Nest.value(data,"userid").$())) {
			userForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel(url_param(idBean, "config"))));
		} else {
			if (Nest.value(data,"is_profile").asBoolean()) {
				userForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel(url_param(idBean, "config"))));
			} else {
				CButtonDelete deleteButton = new CButtonDelete(_("Delete selected user?"), url_param(idBean, "form")+url_param(idBean, "userid")+url_param(idBean, "config"));

				if (bccomp(CWebUser.get("userid"), Nest.value(data,"userid").$()) == 0) {
					deleteButton.setAttribute("disabled", "disabled");
				}

				userForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					array(
//						deleteButton,
//						new CButtonCancel(url_param(idBean, "config"))
					)
				));
			}
		}

		// append form to widget
		userWidget.addItem(userForm);

		return userWidget;
	}

}
