package com.isoft.web.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.isoft.biz.dao.common.IPreLoaderDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IPreLoaderHandler;
import com.isoft.consts.ModuleConstant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.model.FuncItem;
import com.isoft.model.PermItem;
import com.isoft.model.SelectItem;
import com.isoft.utils.CacheUtil;
import com.isoft.utils.DataSourceUtil;
import com.isoft.utils.DictUtil;
import com.isoft.web.common.IaasPageAction;

public class ISoftListener extends LoaderListener {

	@Override
	protected void destroyedExtendsPreload(ServletContext servletContext) {

	}

	@Override
	protected void initExtendsPreload(ServletContext servletContext) {
		initDataSources();

		PreLoaderBean loadBean = new PreLoaderBean();
		//loadBean.initExtendSelectItems();
		loadBean.loadSysConfig();
	}

	protected void initDataSources() {
		Context initCtx = null;
		try {
			initCtx = new InitialContext();
			DataSourceEnum[] dataSources = DataSourceEnum.values();
			DataSource dataSource = null;
			for (DataSourceEnum dsItem : dataSources) {
				dataSource = (DataSource) initCtx.lookup(dsItem.getJndiName());
				DataSourceUtil.setDataSource(dsItem.getDsName(), dataSource);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			if (initCtx != null) {
				try {
					initCtx.close();
				} catch (NamingException e) {
				}
			}
		}
	}

	protected class PreLoaderBean extends IaasPageAction {
		
		private IdentityBean idBean = new IdentityBean();
		
		@Override
		protected IdentityBean getIdentityBean() {
			return this.idBean;
		}

		public void loadSysConfig() {

			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IPreLoaderHandler.class);
			request.setCallDAOIF(IPreLoaderDAO.class);
			request.setCallHandlerMethod(IPreLoaderHandler.METHOD_LOADSYSCONFIG);
			request.setModuleName(ModuleConstant.MODULE_COMMON);
			request.setCheckLogin(false);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			Map mapParam = dto.getMapParam();
			List sysDict = (List) mapParam.get("sysDict");
			cacheSysDict(sysDict);

			List<FuncItem> sysFunc = (List) mapParam.get("sysFunc");
			cacheSysFunc(sysFunc);
			
			List<PermItem> sysPerm = (List) mapParam.get("sysPerm");
			cacheSysPerm(sysPerm);

		}

		private void cacheSysDict(List codes) {
			for (Iterator iterator = codes.iterator(); iterator.hasNext();) {
				Map dictItem = (Map) iterator.next();
				String type = (String) dictItem.get("type");
				Map dicts = DictUtil.getDictsByType(type);
				if (dicts == null) {
					dicts = new HashMap();
					DictUtil.setDictsByType(type, dicts);
				}
				dicts.put(dictItem.get("dkey"), dictItem);

				List dictOrder = DictUtil.getOrderDictsByType(type);
				if (dictOrder == null) {
					dictOrder = new ArrayList();
					DictUtil.setOrderDictsByType(type, dictOrder);
				}
				SelectItem item = new SelectItem();
				item.setLabel((String) dictItem.get("dlabel"));
				item.setValue((String) dictItem.get("dkey"));
				dictOrder.add(item);
			}
		}

		private void cacheSysFunc(List<FuncItem> funcs) {
			List<FuncItem> navFuncList = new LinkedList<FuncItem>();
			Map<String, FuncItem> funcMap = new HashMap<String, FuncItem>();
			Map<String, FuncItem> funcLeafMap = new HashMap<String, FuncItem>();
			for (FuncItem item : funcs) {
				if ("-1".equals(item.getPid())) {
					navFuncList.add(item);
				}
				item.setParentFunc(funcMap.get(item.getPid()));
				funcMap.put(item.getId(), item);
				
				funcLeafMap.put(item.getId(), item);
				//funcLeafMap.remove(item.getPid());
			}
			FuncItem sysMgr = new FuncItem();
			sysMgr.setFuncName("系统管理");
			
			FuncItem profFunc = new FuncItem();
			profFunc.setFuncName("个人档案");
			profFunc.setParentFunc(sysMgr);
			
			FuncItem pswdFunc = new FuncItem();
			pswdFunc.setFuncName("修改密码");
			pswdFunc.setParentFunc(sysMgr);
			
			funcLeafMap.put("/platform/ProfIndex.action", profFunc);
			funcLeafMap.put("/platform/ProfPswd.action", pswdFunc);
			CacheUtil.cacheNavFuncList(navFuncList);
			CacheUtil.cacheNavFuncLeaf(funcLeafMap);
			funcMap.clear();
			funcMap = null;
		}
		
		private void cacheSysPerm(List<PermItem> perms) {
			Map permMap = new HashMap();
			for (PermItem item : perms) {
				List permList = (List)permMap.get(item.getUri());
				if(permList == null){
					permList = new LinkedList();
					permMap.put(item.getUri(), permList);
				}
				permList.add(item);
			}
			CacheUtil.cacheUriPerm(permMap);
		}
	}
}
