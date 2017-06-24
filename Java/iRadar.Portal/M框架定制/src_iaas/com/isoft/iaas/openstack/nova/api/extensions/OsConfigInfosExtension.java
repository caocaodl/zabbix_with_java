package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.OsConfigVal;
import com.isoft.iradar.core.utils.EasyMap;

public class OsConfigInfosExtension {
	
	private final OpenStackClient CLIENT;
	public OsConfigInfosExtension(OpenStackClient client) {
		CLIENT = client;
	}
	
	/**
	 * "type", "controller"
	 * "key", "cpu_allocation_ratio"
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	public GetVal getVal(String type, String key) {
		return new GetVal(Entity.json(EasyMap.build("type", type, "key", key)));
	}
	public class GetVal extends OpenStackRequest<OsConfigVal> {
		public GetVal(Entity entity) {
			super(CLIENT, HttpMethod.POST, "/os-config-infos/1/get_val_from_config_info", entity, OsConfigVal.class);
		}
	}
}
