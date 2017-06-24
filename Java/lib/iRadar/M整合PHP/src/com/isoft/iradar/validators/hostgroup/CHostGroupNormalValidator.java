package com.isoft.iradar.validators.hostgroup;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CHostGroupNormalValidator extends CValidator<Long[]> {

	public String message;

	private SQLExecutor executor;

	public CHostGroupNormalValidator(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * Checks is any of the given host groups are discovered.
	 *
	 * @param mixed hostGroupIds
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, Long[] hostGroupIds) {
		CHostGroupGet options = new CHostGroupGet();
		options.setOutput("name");
		options.setGroupIds(hostGroupIds);
		options.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_CREATED));
		options.setLimit(1);
		CArray<Map> hostGroups = API.HostGroup(idBean, executor).get(options);

		if (!empty(hostGroups)) {
			Map hostGroup = reset(hostGroups);
			error(message, Nest.value(hostGroup, "name").$());
			return false;
		}

		return true;
	}

}
