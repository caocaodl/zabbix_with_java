package com.isoft.imon.topo.engine.discover;

import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.util.NmsException;

/**
 * 嗅探器接口
 * 
 * @author ldd 2014-2-18
 */
public abstract interface Sniffer {

	/**
	 * 进行嗅探工作
	 * 
	 */
	abstract NetElement doSniff(Host host) throws NmsException;

	/**
	 * 获取凭证类型
	 * 
	 * @return String
	 */
	abstract String getCredenceType();
}
