package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EMAIL;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EXEC;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_SMS;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.MediaUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
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

public class CAdministrationMediatypesList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget mediaTypeWidget = new CWidget();
		
		// create form
		CForm mediaTypeForm = new CForm();
		mediaTypeForm.setName("mediaTypesForm");
		
		CToolBar tb = new CToolBar(mediaTypeForm);
		tb.addSubmit("form", _("Create media type"),"","orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected media types?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected media types?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected media types?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"mediatypeids\";");
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		mediaTypeWidget.addItem(headerActions);

		// create table
		CTableInfo mediaTypeTable = new CTableInfo(_("No media types found."));		
		
		String mediaClass = mediaTypeTable.getAttribute("class").toString();
		mediaClass += " media";
		mediaTypeTable.setAttribute("class", mediaClass);
		mediaTypeTable.setHeader(array(
			new CCheckBox("all_media_types", false, "checkAll(\""+mediaTypeForm.getName()+"\", \"all_media_types\", \"mediatypeids\");"),
			make_sorting_header(_("Name"), "description"),
			make_sorting_header(_("Type"), "type"),
			_("Status"),
			_("Used in actions"),
			_("Details")
		));

		for(Map mediaType : (CArray<Map>)Nest.value(data,"mediatypes").asCArray()) {
			String details = null;
			switch (Nest.value(mediaType,"typeid").asInteger()) {
				case MEDIA_TYPE_EMAIL:
					details  =
						_("SMTP server")+NAME_DELIMITER+"\""+mediaType.get("smtp_server")+"\", "+
						_("SMTP helo")+NAME_DELIMITER+"\""+mediaType.get("smtp_helo")+"\", "+
						_("SMTP email")+NAME_DELIMITER+"\""+mediaType.get("smtp_email")+"\"";
					break;

				case MEDIA_TYPE_EXEC:
					details = _("Script name")+NAME_DELIMITER+"\""+mediaType.get("exec_path")+"\"";
					break;

				case MEDIA_TYPE_SMS:
					details = _("GSM modem")+NAME_DELIMITER+"\""+mediaType.get("gsm_modem")+"\"";
					break;

				case MEDIA_TYPE_JABBER:
					details = _("Jabber identifier")+NAME_DELIMITER+"\""+mediaType.get("username")+"\"";
					break;

				case MEDIA_TYPE_EZ_TEXTING:
					details = _("Username")+NAME_DELIMITER+"\""+mediaType.get("username")+"\"";
					break;

				default:
					details = "";
					break;
			}

			// action list
			Object actionLinks = array();
			if (!empty(Nest.value(mediaType,"listOfActions").$())) {
				for(Map action : (CArray<Map>)Nest.value(mediaType,"listOfActions").asCArray()) {
					((CArray)actionLinks).add( new CLink(Nest.value(action,"name").$(), "actionconf.action?form=update&actionid="+Nest.value(action,"actionid").$()));
					((CArray)actionLinks).add(", ");
				}
				array_pop(((CArray)actionLinks));
			} else {
				actionLinks = "-";
			}
			CCol actionColumn = new CCol(actionLinks);
			actionColumn.setAttribute("style", "white-space: normal;");

			String statusLink = "media_types.action?go="+((Nest.value(mediaType,"status").asInteger() == MEDIA_TYPE_STATUS_DISABLED) ? "activate" : "disable")+
				"&mediatypeids"+SQUAREBRACKETS+"="+Nest.value(mediaType,"mediatypeid").$();

			CLink status = (MEDIA_TYPE_STATUS_ACTIVE == Nest.value(mediaType,"status").asInteger())
				? new CLink(_("Enabled"), statusLink, "enabled")
				: new CLink(_("Disabled"), statusLink, "disabled");
				
			//自定义A标签属性 用于页面实现Ajax异步修改状态
			Object _go = Nest.value(mediaType,"status").asInteger() == MEDIA_TYPE_STATUS_DISABLED ? "activate" : "disable";
			Object _mediatypeids = Nest.value(mediaType,"mediatypeid").$();
			String[] ids = status.getUrl().split("=");
			Object _sid = ids[ids.length-1];
				
			status.setAttribute("go", _go);
			status.setAttribute("mediatypeids", _mediatypeids);
			status.setAttribute("sid", _sid);
			status.setAttribute("onclick", "changeMediaTypeStatus(this)");
			status.setAttribute("href", "javascript:void(0)");
				
			CCol cstatus = new CCol(new CDiv(status, "switch"));
			
			// append row
			mediaTypeTable.addRow(array(
				new CCheckBox("mediatypeids["+mediaType.get("mediatypeid")+"]", false, null, Nest.value(mediaType,"mediatypeid").asInteger()),
				new CLink(Nest.value(mediaType,"description").$(), "?form=edit&mediatypeid="+Nest.value(mediaType,"mediatypeid").$()),
				MediaUtil.media_type2str(Nest.value(mediaType,"typeid").asInteger()),
				cstatus,
				actionColumn,
				details
			));
		}

		// append table to form
		mediaTypeForm.addItem(array(mediaTypeTable, Nest.value(data,"paging").$()));

		// append form to widget
		mediaTypeWidget.addItem(mediaTypeForm);

		return mediaTypeWidget;
	}

}
