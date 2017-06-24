package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_INTERVAL;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_COUNT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.JsUtil.insert_js_function;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.utils.CJs.encodeJson;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupMediaAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Media"));
		Nest.value(page, "file").$(_("popup_media.action"));
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"dstfrm",			array(T_RDA_STR, O_MAND,P_SYS,	NOT_EMPTY, null),
		
			"media",			array(T_RDA_INT, O_OPT,	P_SYS,	null,			null),
			"mediatypeid",	array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,			"isset({add})"),
			"sendto",			array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY,		"isset({add})"),
			"period",			array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY,		"isset({add})"),
			"active",			array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY,		"isset({add})"),
		
			"severity",			array(T_RDA_INT, O_OPT,	null,	NOT_EMPTY,	null),
			/* actions */
			"add",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			/* other */
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"form_refresh",array(T_RDA_STR, O_OPT, null,	null,	null)
		);		
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (RDA_GUEST_USER.equals(Nest.as(CWebUser.get("alias")).asString())) {
			access_deny();
		}
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		insert_js_function("add_media");
		if (isset(_REQUEST,"add")) {
			CTimePeriodValidator validator = CValidator.init(new CTimePeriodValidator(),map());
			if (validator.validate(getIdentityBean(), Nest.value(_REQUEST,"period").asString())) {
				int severity = 0;
				Nest.value(_REQUEST,"severity").$(get_request("severity", array()));
				for(Object id : Nest.value(_REQUEST,"severity").asCArray()) {
					severity |= 1 << Nest.as(id).asInteger();
				}

				echo("<script type=\"text/javascript\">"+
						"add_media(\""+Nest.value(_REQUEST,"dstfrm").asString()+"\","+
						Nest.value(_REQUEST,"media").asString()+","+
						rda_jsvalue(Nest.value(_REQUEST,"mediatypeid").asString())+","+
						encodeJson(Nest.value(_REQUEST,"sendto").asString())+",\""+
						Nest.value(_REQUEST,"period").asString()+"\","+
						Nest.value(_REQUEST,"active").asString()+","+
						severity+");"+
						"</script>");
			} else {
				error(validator.getError());
			}
		}

		CArray<Integer> severity = null;
		if (isset(_REQUEST,"media") && !isset(_REQUEST,"form_refresh")) {
			Integer rq_severity = get_request("severity", 63);

			severity = array();
			for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
				if ((rq_severity & (1 << i))>0) {
					Nest.value(severity,i).$(i);
				}
			}
		} else {
			severity = get_request("severity", array(0, 1, 2, 3, 4, 5));
		}

		Integer media = get_request("media", -1);
		String sendto = get_request("sendto", "");
		Integer mediatypeid = get_request("mediatypeid", 0);
		Integer active = get_request("active", 0);
		String period = get_request("period", RDA_DEFAULT_INTERVAL);

		CFormTable frmMedia = new CFormTable(_("New media"));
		frmMedia.addVar("media", media);
		frmMedia.addVar("dstfrm", Nest.value(_REQUEST,"dstfrm").$());

		CComboBox cmbType = new CComboBox("mediatypeid", mediatypeid);
		CArray<Map> types = DBfetchArrayAssoc(DBselect(executor,
			"SELECT mt.mediatypeid,mt.description"+
			" FROM media_type mt WHERE mt.tenantid='-'"
		), "mediatypeid");
		CArrayHelper.sort(types, array("description"));
		for (Entry<Object, Map> e : types.entrySet()) {
		    Object mediaTypeId = e.getKey();
		    Map type = e.getValue();
			cmbType.addItem(mediaTypeId,
				Nest.value(type,"description").asString()
			);
		}
		frmMedia.addRow(_("Type"), cmbType);
		frmMedia.addRow(_("Send to"), new CTextBox("sendto", sendto, 48));
		frmMedia.addRow(_("When active"), new CTextBox("period", period, 48));

		CArray frm_row = array();
		for (Entry<Object, String> e : getSeverityCaption(getIdentityBean(), executor).entrySet()) {
		    int i = Nest.as(e.getKey()).asInteger();
		    String caption = e.getValue();
			frm_row.add(array(
				new CCheckBox("severity["+i+"]", severity.containsKey(i), null, i),
				caption
			));
			frm_row.add(BR());
		}
		frmMedia.addRow(_("Use if severity"), frm_row);

		CComboBox cmbStat = new CComboBox("active", active);
		cmbStat.addItem(0, _("Enabled"));
		cmbStat.addItem(1, _("Disabled"));
		frmMedia.addRow(_("Status"), cmbStat);

		frmMedia.addItemToBottomRow(array(
			new CSubmit("add", (media > -1) ? _("Save") : _("Add")),
			new CButtonCancel(null, "close_window();")
		));
		frmMedia.show();
	}

}
