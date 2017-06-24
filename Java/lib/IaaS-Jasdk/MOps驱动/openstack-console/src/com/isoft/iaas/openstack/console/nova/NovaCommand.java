package com.isoft.iaas.openstack.console.nova;

import org.apache.commons.cli.CommandLine;

import com.isoft.iaas.openstack.console.Command;
import com.isoft.iaas.openstack.console.Console;
import com.isoft.iaas.openstack.nova.Nova;

public abstract class NovaCommand extends Command {

	public NovaCommand(String name) {
		super(name);
	}

	@Override
	public void execute(Console console, CommandLine args) {
		NovaEnvironment environment = (NovaEnvironment) console
				.getEnvironment();
		execute(environment.CLIENT, args);

	}

	protected abstract void execute(Nova nova, CommandLine args);

}
