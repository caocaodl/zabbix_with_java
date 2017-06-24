package com.isoft.iradar.web.views;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.types.CMap;

public abstract class CViewSegment extends CView {

	protected CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
	
	public abstract Object doWidget(IIdentityBean idBean, SQLExecutor executor, Map data);
}
