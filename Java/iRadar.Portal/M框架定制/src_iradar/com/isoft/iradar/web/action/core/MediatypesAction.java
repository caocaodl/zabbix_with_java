package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MEDIA_TYPE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EMAIL;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EXEC;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_SMS;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.MediaUtil.media_type2str;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MediatypesAction extends RadarBaseAction {
	
	private Long mediaTypeId;
	private CArray<Map> mediaTypes;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of media types"));
		page("file", "media_types.action");
		page("hist_arg", new String[] {});
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变阀值状态所需js
		page("css", new String[] {"lessor/strategy/mediatypes.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"mediatypeids",array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID, null),
			"mediatypeid",	array(T_RDA_INT, O_NO,	P_SYS,	DB_ID, "isset({form})&&{form}==\"edit\""),
			"type",				array(T_RDA_INT, O_OPT,	null,	IN(implode(",", array_keys(media_type2str()))), "isset({save})"),
			"description",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})","名称"),
			"smtp_server",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL,"SNMP服务器"),
			"smtp_helo",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL),
			"smtp_email",	array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+MEDIA_TYPE_EMAIL,"发件人地址"),
			"exec_path",		array(T_RDA_STR, O_OPT,	null,	NOT_EMPTY, "isset({save})&&isset({type})&&({type}=="+MEDIA_TYPE_EXEC+"||{type}=="+MEDIA_TYPE_EZ_TEXTING+")","脚本名称"),
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
		mediaTypeId = get_request_asLong("mediatypeid");		
		/* Permissions */
		if (isset(_REQUEST,"mediatypeid")) {
			CMediaTypeGet params = new CMediaTypeGet();
			params.setMediaTypeIds(mediaTypeId);
			params.setOutput(API_OUTPUT_EXTEND);
			mediaTypes = API.MediaType(getIdentityBean(), executor).get(params);
			if (empty(mediaTypes)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"mediatypeids") || !isArray(Nest.value(_REQUEST,"mediatypeids").$())) {
				access_deny();
			} else {
				CMediaTypeGet params = new CMediaTypeGet();
				params.setMediaTypeIds(Nest.array(_REQUEST,"mediatypeids").asLong());
				params.setCountOutput(true);
				long mediaTypeChk = API.MediaType(getIdentityBean(), executor).get(params);
				if (mediaTypeChk != count(Nest.value(_REQUEST,"mediatypeids").$())) {
					access_deny();
				}
			}
		}

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
			final Map mediaType = map(
				"type", get_request("type"),
				"description", get_request("description"),
				"smtp_server", get_request("smtp_server"),
				"smtp_helo", get_request("smtp_helo"),
				"smtp_email", get_request("smtp_email"),
				"exec_path", get_request("exec_path"),
				"gsm_modem", get_request("gsm_modem"),
				"username", get_request("username"),
				"passwd", get_request("password"),
				"status", get_request("status", MEDIA_TYPE_STATUS_DISABLED)
			);

			if (is_null(Nest.value(mediaType,"passwd").$())) {
				unset(mediaType,"passwd");
			}

			boolean result;
			int action;
			if (!empty(mediaTypeId)) {
				Nest.value(mediaType,"mediatypeid").$(mediaTypeId);
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.MediaType(getIdentityBean(), executor).update(array(mediaType)));
					}
				});
				action = AUDIT_ACTION_UPDATE;
				show_messages(result, _("Media type updated"), _("Cannot update media type"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.MediaType(getIdentityBean(), executor).create(array(mediaType)));
					}
				});
				action = AUDIT_ACTION_ADD;
				show_messages(result, _("Media type added"), _("Cannot add media type"));
			}

			if (result) {
				add_audit(getIdentityBean(), executor,action, AUDIT_RESOURCE_MEDIA_TYPE, _("Media type")+" ["+Nest.value(mediaType,"description").asString()+"]");
				unset(_REQUEST,"form");
				clearCookies(result);
			}
		} else if (isset(_REQUEST,"delete") && !empty(mediaTypeId)) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.MediaType(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"mediatypeid").asLong()));
				}
			});
			if (result) {
				unset(_REQUEST,"form");
			}
			show_messages(result, _("Media type deleted"), _("Cannot delete media type"));
			clearCookies(result);
		} else if (str_in_array(get_request("go"), array("activate", "disable"))) {
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
//			String messageSuccess = enable
//				? _n("Media type enabled", "Media types enabled", updated)
//				: _n("Media type disabled", "Media types disabled", updated);
//			String messageFailed = enable
//				? _n("Cannot enable media type", "Cannot enable media types", updated)
//				: _n("Cannot disable media type", "Cannot disable media types", updated);
//
//			show_messages(result, messageSuccess, messageFailed);
//			show_messages(result, _("startsuccess"), _("endsuccess"));
			if(status==0){
				show_messages(result, _("startsuccess") ,"");
			}else{
				show_messages(result,_("endsuccess") ,"");
			}
			
			clearCookies(result);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return!empty(API.MediaType(getIdentityBean(), executor).delete(get_request("mediatypeids", array()).valuesAsLong()));
				}
			});
			show_messages(goResult, _("Media type deleted"), _("Cannot delete media type"));
			clearCookies(goResult);
		}
		
		/* Display */
		if (!empty(Nest.value(_REQUEST,"form").$())) {
			Map data = map(
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0),
				"mediatypeid", mediaTypeId
			);

			if (isset(data,"mediatypeid") && empty(Nest.value(_REQUEST,"form_refresh").$())) {
				Map mediaType = reset(mediaTypes);

				Nest.value(data,"type").$(Nest.value(mediaType,"type").$());
				Nest.value(data,"description").$(Nest.value(mediaType,"description").$());
				Nest.value(data,"smtp_server").$(Nest.value(mediaType,"smtp_server").$());
				Nest.value(data,"smtp_helo").$(Nest.value(mediaType,"smtp_helo").$());
				Nest.value(data,"smtp_email").$(Nest.value(mediaType,"smtp_email").$());
				Nest.value(data,"exec_path").$(Nest.value(mediaType,"exec_path").$());
				Nest.value(data,"gsm_modem").$(Nest.value(mediaType,"gsm_modem").$());
				Nest.value(data,"username").$(Nest.value(mediaType,"username").$());
				Nest.value(data,"password").$(Nest.value(mediaType,"passwd").$());
				Nest.value(data,"status").$(Nest.value(mediaType,"status").$());
			} else {
				Nest.value(data,"type").$(get_request("type", MEDIA_TYPE_EMAIL));
				Nest.value(data,"description").$(get_request("description", ""));
				Nest.value(data,"smtp_server").$(get_request("smtp_server", "localhost"));
				Nest.value(data,"smtp_helo").$(get_request("smtp_helo", "localhost"));
				Nest.value(data,"smtp_email").$(get_request("smtp_email", "iradar@localhost"));
				Nest.value(data,"exec_path").$(get_request("exec_path", ""));
				Nest.value(data,"gsm_modem").$(get_request("gsm_modem", "/dev/ttyS0"));
				Nest.value(data,"username").$(get_request("username", (Nest.value(data,"type").asInteger() == MEDIA_TYPE_EZ_TEXTING) ? "username" : "user@server"));
				Nest.value(data,"password").$(get_request("password", ""));
				Nest.value(data,"status").$(get_request("status", MEDIA_TYPE_STATUS_ACTIVE));
			}

			// render view
			CView mediaTypeView = new CView("administration.mediatypes.edit", data);
			mediaTypeView.render(getIdentityBean(), executor);
			mediaTypeView.show();
		} else {
			Map data = map();

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			// get media types
			CMediaTypeGet options = new CMediaTypeGet();
			options.setOutput(API_OUTPUT_EXTEND);
			options.setPreserveKeys(true);
			options.setEditable(true);
			options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> mediatypes = API.MediaType(getIdentityBean(), executor).get(options);
			Nest.value(data,"mediatypes").$(mediatypes);

			if (!empty(mediatypes)) {
				// get media types used in actions
				CActionGet params = new CActionGet();
				params.setMediaTypeIds(rda_objectValues(mediatypes, "mediatypeid").valuesAsLong());
				params.setOutput(new String[]{"actionid", "name"});
				params.setSelectOperations(new String[]{"operationtype", "opmessage"});
				params.setPreserveKeys(true);
				CArray<Map> actions = API.Action(getIdentityBean(), executor).get(params);

				for (Entry<Object, Map> e : mediatypes.entrySet()) {
	                Object key = e.getKey();
	                Map mediaType = e.getValue();
					Nest.value(mediatypes,key,"typeid").$(Nest.value(mediatypes,key,"type").$());
					Nest.value(mediatypes,key,"type").$(media_type2str(Nest.value(mediatypes,key,"type").asInteger()));
					CArray<Map> listOfActions = array();
					Nest.value(mediatypes,key,"listOfActions").$(listOfActions);
					if (!empty(actions)) {
						for (Entry<Object, Map> ae : actions.entrySet()) {
			                Object actionId = ae.getKey();
			                Map action = ae.getValue();
			                CArray<Map> operations = Nest.value(action,"operations").asCArray();
							for(Map operation : operations) {
								if (Nest.value(operation,"operationtype").asInteger() == OPERATION_TYPE_MESSAGE
										&& Nest.value(operation,"opmessage","mediatypeid").asInteger() == Nest.value(mediaType,"mediatypeid").asInteger()) {

									Nest.value(listOfActions,actionId).$(map(
										"actionid", actionId,
										"name", Nest.value(action,"name").asString()
									));
								}
							}
						}
						order_result(listOfActions, "name");
					}
				}

				// sorting & paging
				order_result(mediatypes, getPageSortField(getIdentityBean(), executor, "description"), getPageSortOrder(getIdentityBean(), executor));
				Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, mediatypes, array("mediatypeid")));
			} else {
				CArray arr = array();
				Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, arr, array("mediatypeid")));
			}

			// render view
			CView mediaTypeView = new CView("administration.mediatypes.list", data);
			mediaTypeView.render(getIdentityBean(), executor);
			mediaTypeView.show();
		}
	}
}
