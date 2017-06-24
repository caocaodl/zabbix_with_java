package com.isoft.iaas.openstack.console.keystone;

import org.apache.commons.cli.CommandLine;

import com.isoft.iaas.openstack.console.Command;
import com.isoft.iaas.openstack.console.Console;
import com.isoft.iaas.openstack.keystone.Keystone;

public abstract class KeystoneCommand extends Command {

	public KeystoneCommand(String name) {
		super(name);
	}

	@Override
	public void execute(Console console, CommandLine args) {
		KeystoneEnvironment environment = (KeystoneEnvironment) console
				.getEnvironment();
		execute(environment.CLIENT, args);

	}

	protected abstract void execute(Keystone keystone, CommandLine args);

}
