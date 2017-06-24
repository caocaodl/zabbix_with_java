package com.isoft.iaas.openstack.console.keystone;

import org.apache.commons.cli.CommandLine;

import com.isoft.iaas.openstack.console.utils.Column;
import com.isoft.iaas.openstack.console.utils.Table;
import com.isoft.iaas.openstack.console.utils.TableModel;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Tenants;

public class KeystoneTenantList extends KeystoneCommand {

	public KeystoneTenantList() {
		super("tenant-list");
	}

	@Override
	public void execute(Keystone keystone, CommandLine args) {

		final Tenants tenants = keystone.tenants().list().execute();

		Table t = new Table(new TableModel<Tenant>(tenants.getList()) {

			@Override
			public Column[] getHeaders() {
				return new Column[] { new Column("id", 32, Column.ALIGN_LEFT),
						new Column("name", 32, Column.ALIGN_LEFT),
						new Column("description", 32, Column.ALIGN_LEFT),
						new Column("enabled", 7, Column.ALIGN_LEFT) };
			}

			@Override
			public String[] getRow(Tenant tenant) {
				return new String[] { tenant.getId(), tenant.getName(),
						tenant.getDescription(), tenant.getEnabled().toString() };
			}
		});
		System.out.println(t.render());
	}

}
