package com.isoft.iaas.openstack.console.nova;

import org.apache.commons.cli.CommandLine;

import com.isoft.iaas.openstack.console.utils.Column;
import com.isoft.iaas.openstack.console.utils.Table;
import com.isoft.iaas.openstack.console.utils.TableModel;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Servers;

public class NovaServerList extends NovaCommand {

	public NovaServerList() {
		super("list");
	}

	@Override
	public void execute(Nova nova, CommandLine cmd) {

		final Servers servers = nova.servers().list(true).execute();

		Table t = new Table(new TableModel<Server>(servers.getList()) {

			@Override
			public Column[] getHeaders() {
				return new Column[] { new Column("id", 32, Column.ALIGN_LEFT),
						new Column("name", 10, Column.ALIGN_LEFT) };
			}

			@Override
			public String[] getRow(Server server) {
				return new String[] { server.getId(), server.getName() };
			}
		});
		System.out.println(t.render());
	}

}
