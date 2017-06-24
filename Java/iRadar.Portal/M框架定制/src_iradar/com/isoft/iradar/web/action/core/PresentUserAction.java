package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.common.UserDAO;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PresentUserAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title", "");
		page("file", "present.action");
		page("hist_arg", new String[] {});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {

		// VAR TYPE OPTIONAL FLAGS VALIDATION EXCEPTION
		CArray fields = map(
				// users
				"userid", array(T_RDA_STR, O_OPT, P_SYS, null, null), 
				"filter_usrgrpid", array(T_RDA_INT, O_OPT, P_SYS, DB_ID, null), 
				"name",array(T_RDA_STR, O_OPT, null, null, null, _("Name")), 	
				"user_medias", array(T_RDA_STR, O_OPT, null, NOT_EMPTY, null),
				"user_medias_to_del", array(T_RDA_STR, O_OPT, null, DB_ID, null), 
				"new_media", array(T_RDA_STR, O_OPT, null, null, null),
				"enable_media", array(T_RDA_INT, O_OPT, null, null, null),
				"disable_media", array(T_RDA_INT, O_OPT, null, null, null),
				"lang",array(T_RDA_STR, O_OPT, null, null, null),
				// actions
				"go", array(T_RDA_STR, O_OPT, P_SYS | P_ACT, null, null), 
				"save", array(T_RDA_STR, O_OPT, P_SYS | P_ACT, null, null), 
				"delete",array(T_RDA_STR, O_OPT, P_SYS | P_ACT, null, null), 
				"delete_selected", array(T_RDA_STR, O_OPT, P_SYS | P_ACT, null, null),
				"del_user_media", array(T_RDA_STR, O_OPT, P_SYS | P_ACT, null, null), 
				"cancel", array(T_RDA_STR, O_OPT, P_SYS, null, null),
				// form
				"form", array(T_RDA_STR, O_OPT, P_SYS, null, null),
				"form_refresh", array(T_RDA_STR, O_OPT, null, null, null));
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "alias", RDA_SORT_UP);

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
		IdentityBean infor=getIdBean();
		String userId = infor.getUserId();
		CArray<Map> data = new CArray<Map>();
		CArray<Map> map = new CArray<Map>();
		UserDAO udao = new UserDAO(executor);
		CArray<Map> userIds = new CArray<Map>();
		userIds.put("userid", userId);
		userIds.put("tenantid", getIdentityBean().getTenantId());
		if (isset(_REQUEST, "save")) {
			CArray<Map> use = new CArray<Map>();
			CArray<Map> user = Nest.value(_REQUEST, "user_medias").asCArray();
			for (Entry<Object, Map> e : user.entrySet()) {
				Map users = e.getValue();
				users.put("userid", userId);
				use.push(users);
			}
			DBstart(executor);
			udao.doDeleteMediaByUserId(userIds);
			for (Entry<Object, Map> e : use.entrySet()) {
				Map users = e.getValue();
				users.put("tenantid", getIdentityBean().getTenantId());
				udao.doAddMediaByUserId(users);
			}
			show_messages(true, "设置成功");
			DBend(executor);

		}
		if (isset(_REQUEST, "new_media")) {
			Nest.value(_REQUEST, "user_medias").$(get_request("user_medias", array()));
			CArray user_medias = Nest.value(_REQUEST, "user_medias").asCArray();
			array_push(user_medias, Nest.value(_REQUEST, "new_media").$());
			Nest.value(_REQUEST, "user_medias").$(user_medias);
			data.put("user_medias", Nest.value(_REQUEST, "user_medias").asCArray());

		} else {
			if (isset(_REQUEST, "enable_media")) {
				// id.put("mediaid", Nest.value(_REQUEST,
				// "enable_media").asInteger());
				// udap.doStart(id);
				Nest.value(_REQUEST, "user_medias", _REQUEST.get("enable_media"), "active").$(0);
				data.put("user_medias", Nest.value(_REQUEST, "user_medias").asCArray());
			}
			if (isset(_REQUEST, "disable_media")) {
				// id.put("mediaid", Nest.value(_REQUEST,
				// "disable_media").asInteger());
				// udap.doEnd(id);
				Nest.value(_REQUEST, "user_medias", _REQUEST.get("disable_media"), "active").$(1);
				data.put("user_medias", Nest.value(_REQUEST, "user_medias").asCArray());

			}
			if (isset(_REQUEST, "user_medias_to_del")) {
				CArray<Map> del = Nest.value(_REQUEST, "user_medias_to_del").asCArray();
				for (Entry<Object, Map> e : del.entrySet()) {
					Object mediaTypeId = e.getKey();
					Nest.value(_REQUEST, "user_medias").$s().remove(mediaTypeId);

				}
				data.put("user_medias", Nest.value(_REQUEST, "user_medias").asCArray());
			}

			if (empty(get_request("user_medias", array()))) {
				List<Map> list = udao.doGetMediaByUserId(userIds);
				for (int i = 0; i < list.size(); i++) {
					map.push(list.get(i));

				}
				data.put("user_medias", map);
			} else {
				data.put("user_medias", Nest.value(_REQUEST, "user_medias").asCArray());
			}

		}

		// data = getUserFormData(getIdentityBean(), executor,userId);

		// Nest.value(map,"user_medias").$( Nest.value(data,"user_medias"));
		Nest.value(data, "userid").$(userId);
		Nest.value(data, "form").$(get_request("form"));
		Nest.value(data, "form_refresh").$(get_request("form_refresh", 0));
	

		
		// render view
		CView usersView = new CView("administration.users.edit", data);
		usersView.render(getIdentityBean(), executor);
		usersView.show();

	}

}
