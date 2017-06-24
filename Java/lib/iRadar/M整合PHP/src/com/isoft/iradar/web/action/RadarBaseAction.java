package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.ACCESS_DENY_ABSENCE_ITEM;
import static com.isoft.iradar.inc.Defines.ACCESS_DENY_OBJECT;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.ViewsUtil.includePageFooter;
import static com.isoft.iradar.inc.ViewsUtil.includePageHeader;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.biz.Delegator;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.exception.ExitException;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CWarning;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.utils.DebugUtil;
import com.isoft.web.CDelegator;

public abstract class RadarBaseAction extends RadarCustomAction {

	protected RadarContext ctx = null;
	protected Map<Object, Object> _REQUEST;
	protected Map<String, Object> page;

	protected boolean isHtmlPage() {
		return true;
	}
	
	protected boolean isHtmlStaticPage() {
		return false;
	}
	
	final protected void initRequestContext(){
		if (this.ctx == null) {
			this.ctx = RadarContext.getContext();
		}
		if (this._REQUEST == null) {
			this._REQUEST = RadarContext._REQUEST();
		}
		if (this.page == null) {
			this.page = this.ctx.getPage();
		}
	}

	@Deprecated
	protected RadarContext ctx() {
		return this.ctx;
	}
	
	@Deprecated
	public Map<String, Object> page() {
		if (this.page == null) {
			this.page = this.ctx.getPage();
		}
		return this.page;
	}

	public Object page(String attrName) {
		return this.ctx.getPage(attrName);
	}
	
	public void page(String attrName, Object attrValue) {
		this.ctx.setPage(attrName, attrValue);
	}

	@Deprecated
	protected Map<Object, Object> _REQUEST() {
		if (this._REQUEST == null) {
			this._REQUEST = RadarContext._REQUEST();
		}
		return this._REQUEST;
	}

	protected <T> T _REQUEST(String key) {
		return (T) _REQUEST().get(key);
	}

	protected void _REQUEST(String key, Object value) {
		_REQUEST().put(key, value);
	}
	
	protected Map _page() {
		return ctx.getPage();
	}
	protected <T> T _page(Object key) {
		return (T)ctx.getPage().get(key);
	}
	protected void _page(Object key, Object value) {
		ctx.setPage(String.valueOf(key), value);
	}

	public void doWork() {
		initRequestContext();
		
		if (isIgnoreHeader()) {
			define("RDA_PAGE_NO_MENU", 1);
		}
		
		if (isIgnoreFooter()) {
			define("RDA_PAGE_NO_FOOTER", 1);
		}

		CDelegator.doDelegate(getIdentityBean(),new Delegator<Boolean>() {
			@Override
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				try {
					doInitPage();					
					if (isHtmlPage()) {
						includePageHeader(idBean, executor);
					}
					
					doCheckFields(executor);
					
					boolean doAction = true;
					if (isHtmlStaticPage()) {
						doAction(null);
					} else {
						doPermissions(executor);
						if (doAjax(executor)) {
							includePageFooter(idBean, executor);
							doAction = false;
						} else {
							doPageFilter(executor);
							doAction(executor);
						}
					}
					
					if(doAction && isHtmlPage()) {
						includePageFooter(idBean, executor);
					}
				} catch (ExitException e) {
					if(DebugUtil.isDebugEnabled()) {
						DebugUtil.error(e);
					}
					int mode = e.getCode();
					if(mode == ACCESS_DENY_ABSENCE_ITEM){
						getRequest().getRequestDispatcher("/assets/icons/access_deny_absence_item.png").forward(getRequest(), getResponse());
					} else if (mode > -1) {
						access_deny(executor, mode);
					}
				} catch (Exception e) {
					//ZBase.php -> setErrorHandler
					e.printStackTrace();
					error(e.getMessage());
					throw e;
				}
				return true;
			}
		});
		
	}

	/**
	 * Renders an "access denied" message and stops the execution of the script.
	 *
	 * The mode parameters controls the layout of the message:
	 * - ACCESS_DENY_OBJECT     - render the message when denying access to a specific object
	 * - ACCESS_DENY_PAGE       - render a complete access denied page
	 *
	 * @param int mode
	 */
	private void access_deny(SQLExecutor executor, int mode) {
		// deny access to an object
		if (mode == ACCESS_DENY_OBJECT) {
			includePageHeader(getIdentityBean(), executor);
			show_error_message(_("No permissions to referred object or it does not exist!"));
			includePageFooter(getIdentityBean(), executor);
		}
		// deny access to a page
		else {
			// url to redirect the user to after he loggs in
			Curl curl = new Curl(!empty(_REQUEST.get("request")) ? Nest.value(_REQUEST,"request").asString() : "");
			curl.setArgument("sid", null);
			String url = urlencode(curl.toString());
			
			String header = null;
			CArray message = null;
			CArray buttons = null;
			
			// if the user is logged in - render the access denied message
			if (CWebUser.isLoggedIn()) {
				header = _("Access denied.");
				message = array(
					_("Your are logged in as"),
					" ",
					bold(Nest.as(CWebUser.get("alias")).asString()),
					". ",
					_("You have no permissions to access this page."),
					BR(),
					_("If you think this message is wrong, please consult your administrators about getting the necessary permissions.")
				);

				buttons = array();
				// display the login button only for guest users
				if (CWebUser.isGuest()) {
					buttons.add(new CButton("login", _("Login"),
						"javascript: document.location = \"index.action?request="+url+"\";", "formlist"
					));
				}
				buttons.add(new CButton("back", _("Go to dashboard"),
					"javascript: document.location = \"dashboard.action\"", "formlist"
				));
			}
			// if the user is not logged in - offer to login
			else {
				header = _("You are not logged in.");
				message = array(
					_("You must login to view this page."),
					BR(),
					_("If you think this message is wrong, please consult your administrators about getting the necessary permissions.")
				);
				buttons = array(
					new CButton("login", _("Login"), "javascript: document.location = \"index.action?request="+url+"\";", "formlist")
				);
			}
			
			CWarning warning = new CWarning(header, message);
			warning.setButtons(buttons);

			CView warningView = new CView("general.warning", map(
				"warning" , warning
			));
			warningView.render(this.getIdentityBean(), executor);
		}
	}

	abstract protected void doInitPage();
	abstract protected void doCheckFields(SQLExecutor executor);
	
	abstract protected void doPermissions(SQLExecutor executor);
	abstract protected boolean doAjax(SQLExecutor executor);
	protected void doPageFilter(SQLExecutor executor){};
	abstract protected void doAction(SQLExecutor executor);

}
