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
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.MediaUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationMediatypesList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget mediaTypeWidget = new CWidget();

		// create new media type button
		CForm createForm = new CForm("get");
		createForm.addItem(new CSubmit("form", _("Create media type")));
		mediaTypeWidget.addPageHeader(_("CONFIGURATION OF MEDIA TYPES"), createForm);
		mediaTypeWidget.addHeader(_("Media types"));
		mediaTypeWidget.addHeaderRowNumber();

		// create form
		CForm mediaTypeForm = new CForm();
		mediaTypeForm.setName("mediaTypesForm");

		// create table
		CTableInfo mediaTypeTable = new CTableInfo(_("No media types found."));
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

			// append row
			mediaTypeTable.addRow(array(
				new CCheckBox("mediatypeids["+mediaType.get("mediatypeid")+"]", false, null, Nest.value(mediaType,"mediatypeid").asInteger()),
				new CLink(Nest.value(mediaType,"description").$(), "?form=edit&mediatypeid="+Nest.value(mediaType,"mediatypeid").$()),
				MediaUtil.media_type2str(Nest.value(mediaType,"typeid").asInteger()),
				status,
				actionColumn,
				details
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected media types?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected media types?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected media types?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"mediatypeids\";");

		// append table to form
		mediaTypeForm.addItem(array(Nest.value(data,"paging").$(), mediaTypeTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		mediaTypeWidget.addItem(mediaTypeForm);

		return mediaTypeWidget;
	}

}
