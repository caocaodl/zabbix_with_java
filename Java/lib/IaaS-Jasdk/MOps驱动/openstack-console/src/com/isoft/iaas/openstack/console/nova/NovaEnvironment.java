package com.isoft.iaas.openstack.console.nova;

import org.apache.commons.cli.CommandLine;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.console.Command;
import com.isoft.iaas.openstack.console.Console;
import com.isoft.iaas.openstack.console.Environment;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.nova.Nova;

public class NovaEnvironment extends Environment {

	public final Nova CLIENT;

	public static final Command NOVA = new Command("nova") {

		@Override
		public void execute(Console console, CommandLine args) {

			if (args.getArgs().length == 1) {
				Keystone keystone = new Keystone(
						(String) console.getProperty("keystone.endpoint"));

				Access access = keystone
						.tokens()
						.authenticate(
								new UsernamePassword(
										console.getProperty("keystone.username"),
										console.getProperty("keystone.password")))
						.withTenantName(
								console.getProperty("keystone.tenant_name"))
						.execute();

				System.out.println(console.getProperty("nova.endpoint"));

				Nova client = new Nova(console.getProperty("nova.endpoint")
						+ args.getArgs()[0]);
				client.setTokenProvider(new OpenStackSimpleTokenProvider(access
						.getToken().getId()));

				NovaEnvironment environment = new NovaEnvironment(
						console.getEnvironment(), client);

				environment.register(new NovaServerList());

				console.setEnvironment(environment);

			}

		}

	};

	public NovaEnvironment(Environment parent, Nova client) {
		super(parent);
		CLIENT = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.woorea.wsh.Environment#getPrompt()
	 */
	@Override
	public String getPrompt() {
		return "nova> ";
	}

}
