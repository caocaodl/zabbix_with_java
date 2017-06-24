package com.isoft.iradar.validators.host;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CHostNormalValidator extends CValidator<Long[]> {

	public String message;
	private SQLExecutor executor;

	public CHostNormalValidator(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * Checks is any of the given hosts are discovered.
	 *
	 * @param hostIds
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, Long[] hostIds) {
		CHostGet options = new CHostGet();
		options.setOutput(new String[] { "host" });
		options.setHostIds(hostIds);
		options.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_CREATED));
		options.setLimit(1);
		CArray<Map> hosts = API.Host(idBean, this.executor).get(options);
		if (!empty(hosts)) {
			Map host = reset(hosts);
			error(message, Nest.value(host, "host").$());
			return false;
		}
		return true;
	}

}
