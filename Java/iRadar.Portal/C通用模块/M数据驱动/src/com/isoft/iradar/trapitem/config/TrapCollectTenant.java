package com.isoft.iradar.trapitem.config;

public class TrapCollectTenant extends TrapCollector {

	@Override
	public String getScript() {
		return "__f_cache(__t_cache, function(id){return "+super.getScript()+"}, $tid+'_+"+this.getKey()+"')";
	}
	
}
