package com.isoft.iradar.api;

import static com.isoft.iradar.inc.FuncsUtil.error;

import com.isoft.biz.daoimpl.radar.CAPIInfo;
import com.isoft.biz.daoimpl.radar.CActionDAO;
import com.isoft.biz.daoimpl.radar.CAlertDAO;
import com.isoft.biz.daoimpl.radar.CAppDAO;
import com.isoft.biz.daoimpl.radar.CConfigurationDAO;
import com.isoft.biz.daoimpl.radar.CDCheckDAO;
import com.isoft.biz.daoimpl.radar.CDHostDAO;
import com.isoft.biz.daoimpl.radar.CDRuleDAO;
import com.isoft.biz.daoimpl.radar.CDServiceDAO;
import com.isoft.biz.daoimpl.radar.CDiscoveryRuleDAO;
import com.isoft.biz.daoimpl.radar.CEventDAO;
import com.isoft.biz.daoimpl.radar.CGraphDAO;
import com.isoft.biz.daoimpl.radar.CGraphItemDAO;
import com.isoft.biz.daoimpl.radar.CGraphPrototypeDAO;
import com.isoft.biz.daoimpl.radar.CHistoryDAO;
import com.isoft.biz.daoimpl.radar.CHostDAO;
import com.isoft.biz.daoimpl.radar.CHostGroupDAO;
import com.isoft.biz.daoimpl.radar.CHostIfaceDAO;
import com.isoft.biz.daoimpl.radar.CHostPrototypeDAO;
import com.isoft.biz.daoimpl.radar.CHttpTestDAO;
import com.isoft.biz.daoimpl.radar.CIconMapDAO;
import com.isoft.biz.daoimpl.radar.CImageDAO;
import com.isoft.biz.daoimpl.radar.CItemDAO;
import com.isoft.biz.daoimpl.radar.CItemPrototypeDAO;
import com.isoft.biz.daoimpl.radar.CMaintenanceDAO;
import com.isoft.biz.daoimpl.radar.CMapDAO;
import com.isoft.biz.daoimpl.radar.CMediaTypeDAO;
import com.isoft.biz.daoimpl.radar.CProxyDAO;
import com.isoft.biz.daoimpl.radar.CScreenDAO;
import com.isoft.biz.daoimpl.radar.CScreenItemDAO;
import com.isoft.biz.daoimpl.radar.CScriptDAO;
import com.isoft.biz.daoimpl.radar.CServiceDAO;
import com.isoft.biz.daoimpl.radar.CTemplateDAO;
import com.isoft.biz.daoimpl.radar.CTemplateScreenDAO;
import com.isoft.biz.daoimpl.radar.CTemplateScreenItemDAO;
import com.isoft.biz.daoimpl.radar.CTriggerDAO;
import com.isoft.biz.daoimpl.radar.CTriggerPrototypeDAO;
import com.isoft.biz.daoimpl.radar.CUserDAO;
import com.isoft.biz.daoimpl.radar.CUserGroupDAO;
import com.isoft.biz.daoimpl.radar.CUserMacroDAO;
import com.isoft.biz.daoimpl.radar.CUserMediaDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;

public class API {
	
	private API() {
	}
	
	/**
	 * @return CActionDAO
	 */
	public static CActionDAO Action(IIdentityBean idBean, SQLExecutor executor) {
		return new CActionDAO(idBean, executor);
	}

	/**
	 * @return CAlertDAO
	 */
	public static CAlertDAO Alert(IIdentityBean idBean, SQLExecutor executor) {
		return new CAlertDAO(idBean, executor);
	}

	/**
	 * @return CAPIInfo
	 */
	public static CAPIInfo APIInfo() {
		return new CAPIInfo();
	}

	/**
	 * @return CApplicationDAO
	 */
	public static CAppDAO Application(IIdentityBean idBean, SQLExecutor executor) {
		return new CAppDAO(idBean, executor);
	}

	/**
	 * @return CConfigurationDAO
	 */
	public static CConfigurationDAO Configuration(IIdentityBean idBean, SQLExecutor executor) {
		return new CConfigurationDAO(idBean, executor);
	}

	/**
	 * @return CDCheckDAO
	 */
	public static CDCheckDAO DCheck(IIdentityBean idBean, SQLExecutor executor) {
		return new CDCheckDAO(idBean, executor);
	}

	/**
	 * @return CDHostDAO
	 */
	public static CDHostDAO DHost(IIdentityBean idBean, SQLExecutor executor) {
		return new CDHostDAO(idBean, executor);
	}

	/**
	 * @return CDiscoveryRuleDAO
	 */
	public static CDiscoveryRuleDAO DiscoveryRule(IIdentityBean idBean, SQLExecutor executor) {
		return new CDiscoveryRuleDAO(idBean, executor);
	}

	/**
	 * @return CDRuleDAO
	 */
	public static CDRuleDAO DRule(IIdentityBean idBean, SQLExecutor executor) {
		return new CDRuleDAO(idBean, executor);
	}

	/**
	 * @return CDServiceDAO
	 */
	public static CDServiceDAO DService(IIdentityBean idBean, SQLExecutor executor) {
		return new CDServiceDAO(idBean, executor);
	}

	/**
	 * @return CEventDAO
	 */
	public static CEventDAO Event(IIdentityBean idBean, SQLExecutor executor) {
		return new CEventDAO(idBean, executor);
	}

	/**
	 * @return CGraphDAO
	 */
	public static CGraphDAO Graph(IIdentityBean idBean, SQLExecutor executor) {
		return new CGraphDAO(idBean, executor);
	}

	/**
	 * @return CGraphItemDAO
	 */
	public static CGraphItemDAO GraphItem(IIdentityBean idBean, SQLExecutor executor) {
		return new CGraphItemDAO(idBean, executor);
	}

	/**
	 * @return CGraphPrototypeDAO
	 */
	public static CGraphPrototypeDAO GraphPrototype(IIdentityBean idBean, SQLExecutor executor) {
		return new CGraphPrototypeDAO(idBean, executor);
	}

	/**
	 * @return CHistoryDAO
	 */
	public static CHistoryDAO History(IIdentityBean idBean, SQLExecutor executor) {
		return new CHistoryDAO(idBean, executor);
	}

	/**
	 * @return CHostDAO
	 */
	public static CHostDAO Host(IIdentityBean idBean, SQLExecutor executor) {
		return new CHostDAO(idBean, executor);
	}

	/**
	 * @return CHostPrototypeDAO
	 */
	public static CHostPrototypeDAO HostPrototype(IIdentityBean idBean, SQLExecutor executor) {
		return new CHostPrototypeDAO(idBean, executor);
	}

	/**
	 * @return CHostGroupDAO
	 */
	public static CHostGroupDAO HostGroup(IIdentityBean idBean, SQLExecutor executor) {
		return new CHostGroupDAO(idBean, executor);
	}

	/**
	 * @return CHostInterfaceDAO
	 */
	public static CHostIfaceDAO HostInterface(IIdentityBean idBean, SQLExecutor executor) {
		return new CHostIfaceDAO(idBean, executor);
	}

	/**
	 * @return CImageDAO
	 */
	public static CImageDAO Image(IIdentityBean idBean, SQLExecutor executor) {
		return new CImageDAO(idBean, executor);
	}

	/**
	 * @return CIconMapDAO
	 */
	public static CIconMapDAO IconMap(IIdentityBean idBean, SQLExecutor executor) {
		return new CIconMapDAO(idBean, executor);
	}

	/**
	 * @return CItemDAO
	 */
	public static CItemDAO Item(IIdentityBean idBean, SQLExecutor executor) {
		return new CItemDAO(idBean, executor);
	}

	/**
	 * @return CItemPrototypeDAO
	 */
	public static CItemPrototypeDAO ItemPrototype(IIdentityBean idBean, SQLExecutor executor) {
		return new CItemPrototypeDAO(idBean, executor);
	}

	/**
	 * @return CMaintenanceDAO
	 */
	public static CMaintenanceDAO Maintenance(IIdentityBean idBean, SQLExecutor executor) {
		return new CMaintenanceDAO(idBean, executor);
	}

	/**
	 * @return CMapDAO
	 */
	public static CMapDAO Map(IIdentityBean idBean, SQLExecutor executor) {
		return new CMapDAO(idBean, executor);
	}

	/**
	 * @return CMediaTypeDAO
	 */
	public static CMediaTypeDAO MediaType(IIdentityBean idBean, SQLExecutor executor) {
		return new CMediaTypeDAO(idBean, executor);
	}

	/**
	 * @return CProxyDAO
	 */
	public static CProxyDAO Proxy(IIdentityBean idBean, SQLExecutor executor) {
		return new CProxyDAO(idBean, executor);
	}

	/**
	 * @return CServiceDAO
	 */
	public static CServiceDAO Service(IIdentityBean idBean, SQLExecutor executor) {
		return new CServiceDAO(idBean, executor);
	}

	/**
	 * @return CScreenDAO
	 */
	public static CScreenDAO Screen(IIdentityBean idBean, SQLExecutor executor) {
		return new CScreenDAO(idBean, executor);
	}

	/**
	 * @return CScreenItemDAO
	 */
	public static CScreenItemDAO ScreenItem(IIdentityBean idBean, SQLExecutor executor) {
		return new CScreenItemDAO(idBean, executor);
	}

	/**
	 * @return CScriptDAO
	 */
	public static CScriptDAO Script(IIdentityBean idBean, SQLExecutor executor) {
		return new CScriptDAO(idBean, executor);
	}

	/**
	 * @return CTemplateDAO
	 */
	public static CTemplateDAO Template(IIdentityBean idBean, SQLExecutor executor) {
		return new CTemplateDAO(idBean, executor);
	}

	/**
	 * @return CTemplateScreenDAO
	 */
	public static CTemplateScreenDAO TemplateScreen(IIdentityBean idBean, SQLExecutor executor) {
		return new CTemplateScreenDAO(idBean, executor);
	}

	/**
	 * @return CTemplateScreenItemDAO
	 */
	public static CTemplateScreenItemDAO TemplateScreenItem(IIdentityBean idBean, SQLExecutor executor) {
		return new CTemplateScreenItemDAO(idBean, executor);
	}

	/**
	 * @return CTriggerDAO
	 */
	public static CTriggerDAO Trigger(IIdentityBean idBean, SQLExecutor executor) {
		return new CTriggerDAO(idBean, executor);
	}

	/**
	 * @return CTriggerPrototypeDAO
	 */
	public static CTriggerPrototypeDAO TriggerPrototype(IIdentityBean idBean, SQLExecutor executor) {
		return new CTriggerPrototypeDAO(idBean, executor);
	}

	/**
	 * @return CUserDAO
	 */
	public static CUserDAO User(IIdentityBean idBean, SQLExecutor executor) {
		return new CUserDAO(idBean, executor);
	}

	/**
	 * @return CUserGroupDAO
	 */
	public static CUserGroupDAO UserGroup(IIdentityBean idBean, SQLExecutor executor) {
		return new CUserGroupDAO(idBean, executor);
	}

	/**
	 * @return CUserMacroDAO
	 */
	public static CUserMacroDAO UserMacro(IIdentityBean idBean, SQLExecutor executor) {
		return new CUserMacroDAO(idBean, executor);
	}

	/**
	 * @return CUserMediaDAO
	 */
	public static CUserMediaDAO UserMedia(IIdentityBean idBean, SQLExecutor executor) {
		return new CUserMediaDAO(idBean, executor);
	}

	/**
	 * @return CHttpTestDAO
	 */
	public static CHttpTestDAO HttpTest(IIdentityBean idBean, SQLExecutor executor) {
		return new CHttpTestDAO(idBean, executor);
	}
	
	public static Boolean Call(Wrapper<Boolean> caller){
		return Call(caller, false);
	}
	
	public static <T> T Call(Wrapper<T> caller, T defaultValue){
		try {
			return caller.doCall();
		} catch (Throwable e) {
			e.printStackTrace();
			error(e.getMessage());
			return defaultValue;
		}
	}
	
	public static abstract class Wrapper<T> {
		protected abstract T doCall() throws Throwable;
	}
}
