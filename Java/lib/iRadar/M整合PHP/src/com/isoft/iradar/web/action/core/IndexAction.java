package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGOUT;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.FuncsUtil.clear_messages;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.ViewsUtil.forward;
import static com.isoft.iradar.inc.ViewsUtil.redirect;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CMessage;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.Mapper.Nest;

public class IndexAction extends RadarBaseAction {
	@Override
	protected void doInitPage() {
		define("RDA_PAGE_NO_AUTHORIZATION", true);
		
		page("title", _("IRADAR"));
		page("file", "index.action");
		
		//add this code to avoid the CViewPageHeader call the show method of CObject, which will change the Reponse status to commited 
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
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
		// logout
		if (isset(_REQUEST("reconnect"))) {
			add_audit(getIdentityBean(), executor,AUDIT_ACTION_LOGOUT, AUDIT_RESOURCE_USER, _("Manual Logout"));
			CWebUser.logout();
			redirect("index.action");
			return;
		}
		
		// login via form
		if (isset(_REQUEST("enter")) && _REQUEST("enter").equals(_("Sign in"))) {
			// try to login
			if (CWebUser.login(get_request("name", ""), get_request("password", ""))) {
				// save remember login preference
				Map user = map("autologin", get_request("autologin", 0));
				if (Nest.value(CWebUser.data(),"autologin").asInteger()!=Nest.value(user,"autologin").asInteger()) {
					API.User(getIdentityBean(), executor).updateProfile(user);
				}
				add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, (String)CWebUser.get("userid"), "", null, null, null);

				String request = get_request("request");
				String url = empty(request) ? (String)CWebUser.get("url") : request;
				if (empty(url) || url.equals(page("file"))) {
					url = "dashboard.action";
				}
				if(url.endsWith(".php")){
					url = url.substring(0,url.length()-4)+".action";
				}
				//url = "adm.gui.action";
				redirect(url);
				return;
			} else {// login failed, fall back to a guest account
				CWebUser.checkAuthentication(null);
			}
		} else {
			// login the user from the session, if the session id is empty - login as a guest
			CWebUser.checkAuthentication(RadarContext.sessionId());
		}
		
		// the user is not logged in, display the login form
		{
			if (isset(_REQUEST, "enter")) {
				_REQUEST("autologin",get_request("autologin", 0));
			}
			List<CMessage> messages = clear_messages();
			if (messages != null && !messages.isEmpty()) {
				CMessage message = messages.get(0);
				_REQUEST("message", message.getMessage());
			}
			String view = "general.login";
			forward(view);
		}

	}
	
}
