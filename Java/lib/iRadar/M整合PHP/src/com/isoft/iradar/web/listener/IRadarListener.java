package com.isoft.iradar.web.listener;

import static org.apache.commons.lang.StringUtils.defaultString;

import javax.servlet.ServletContext;

import com.isoft.iradar.core.g;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.listener.ISoftListener;

public class IRadarListener extends ISoftListener {
	
	@Override
	protected void initExtendsPreload(ServletContext ctx, PreloaderBean loadBean) {
		super.initExtendsPreload(ctx, loadBean);
		
		g.RDA_SERVER = defaultString(ctx.getInitParameter("release.iradar.server"),	g.RDA_SERVER);
		g.RDA_SERVER_PORT = Nest.as(defaultString(ctx.getInitParameter("release.iradar..server.port"),	Nest.as(g.RDA_SERVER_PORT).asString())).asInteger();
	}
}
