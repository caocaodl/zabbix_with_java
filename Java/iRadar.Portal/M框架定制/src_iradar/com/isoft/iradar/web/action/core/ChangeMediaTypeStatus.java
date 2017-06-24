package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EMAIL;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EXEC;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_SMS;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.MediaUtil.media_type2str;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChangeMediaTypeStatus extends RadarBaseAction{

	private Long mediaTypeId;
	private CArray<Map> mediaTypes;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of media types"));
		page("file", "changeMediaTypeStatus.action");
		page("hist_arg", new String[] {});
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变阀值状态所需js
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"mediatypeids",array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID, null),
			"mediatypeid",	array(T_RDA_INT, O_NO,	P_SYS,	DB_ID, "isset({form})&&{form}==\"edit\""),
			"type",				array(T_RDA_INT, O_OPT,	null,	IN(implode(",", array_keys(media_type2str()))), "isset({save})"),
			"description",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})"),
			"smtp_server",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL),
			"smtp_helo",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL),
			"smtp_email",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL),
			"exec_path",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&({type}=="+MEDIA_TYPE_EXEC+"||{type}=="+MEDIA_TYPE_EZ_TEXTING+")"),
			"gsm_modem",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_SMS),
			"username",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&({type}=="+MEDIA_TYPE_JABBER+"||{type}=="+MEDIA_TYPE_EZ_TEXTING+")"),
			"password",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&({type}=="+MEDIA_TYPE_JABBER+"||{type}=="+MEDIA_TYPE_EZ_TEXTING+")"),
			"status",			array(T_RDA_INT, O_OPT,	null,	IN(array(MEDIA_TYPE_STATUS_ACTIVE, MEDIA_TYPE_STATUS_DISABLED)), null),
			// actions
			"save",				array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null, null),
			"delete",			array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null, null),
			"cancel",			array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null, null),
			"go",					array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null, null),
			"form",				array(T_RDA_STR, O_OPT,	P_SYS,	null,	null),
			"form_refresh",	array(T_RDA_INT, O_OPT,	null,	null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "description", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (str_in_array(get_request("go"), array("activate", "disable"))) {
			CArray mediaTypeIds = get_request("mediatypeids", array());
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? MEDIA_TYPE_STATUS_ACTIVE : MEDIA_TYPE_STATUS_DISABLED;
			final CArray<Map> update = array();

			for(Object mediaTypeId : mediaTypeIds) {
				update.add(map(
					"mediatypeid", mediaTypeId,
					"status", status
				));
			}

			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.MediaType(getIdentityBean(), executor).update(update));
				}
			});

			int updated = count(update);
			String messageSuccess = enable
				? _n("Media type enabled", "Media types enabled", updated)
				: _n("Media type disabled", "Media types disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable media type", "Cannot enable media types", updated)
				: _n("Cannot disable media type", "Cannot disable media types", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result);
		}
	}
}
