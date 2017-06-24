<?php
/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/


class CHostImporter extends CImporter {

	public function import(array $hosts) {
		$hostsToCreate = CArray.array();
		$hostsToUpdate = CArray.array();
		for($hosts as $host) {
			// preserve host related templates to massAdd them later
			if (Nest.value(options,"templateLinkage","createMissing").$() && !empty(Nest.value($host,"templates").$())) {
				for(Nest.value($host,"templates").$() as $template) {
					$templateId = referencer->resolveTemplate(Nest.value($template,"name").$());
					if (!$templateId) {
						throw new Exception(_s("Template \"%1$s\" for host \"%2$s\" does not exist.", Nest.value($template,"name").$(), Nest.value($host,"host").$()));
					}
					$templateLinkage[$host["host"]][] = CArray.array("templateid" => $templateId);
				}
			}
			unset(Nest.value($host,"templates").$());


			$host = resolveHostReferences($host);

			if (isset(Nest.value($host,"hostid").$())) {
				$hostsToUpdate[] = $host;
			}
			else {
				$hostsToCreate[] = $host;
			}
		}

		$hostsToUpdate = addInterfaceIds($hostsToUpdate);

		// a list of hostids which were created or updated to create an interface cache for those hosts
		$processedHostIds = CArray.array();
		// create/update hosts
		if (Nest.value(options,"hosts","createMissing").$() && $hostsToCreate) {
			$newHostIds = API.Host().create($hostsToCreate);
			for(Nest.value($newHostIds,"hostids").$() as $hnum => $hostid) {
				$hostHost = $hostsToCreate[$hnum]["host"];
				$processedHostIds[$hostHost] = $hostid;
				referencer->addHostRef($hostHost, $hostid);
				referencer->addProcessedHost($hostHost);

				if (!empty($templateLinkage[$hostHost])) {
					API.Template()->massAdd(CArray.array(
						"hosts" => CArray.array("hostid" => $hostid),
						"templates" => $templateLinkage[$hostHost]
					));
				}
			}
		}
		if (Nest.value(options,"hosts","updateExisting").$() && $hostsToUpdate) {
			API.Host()->update($hostsToUpdate);
			for($hostsToUpdate as $host) {
				referencer->addProcessedHost(Nest.value($host,"host").$());
				$processedHostIds[$host["host"]] = Nest.value($host,"hostid").$();

				if (!empty($templateLinkage[$host["host"]])) {
					API.Template()->massAdd(CArray.array(
						"hosts" => $host,
						"templates" => $templateLinkage[$host["host"]]
					));
				}
			}
		}

		// create interfaces cache interface_ref->interfaceid
		$dbInterfaces = API.HostInterface().get(CArray.array(
			"hostids" => $processedHostIds,
			"output" => API_OUTPUT_EXTEND
		));
		for($hosts as $host) {
			for(Nest.value($host,"interfaces").$() as $interface) {
				if (isset($processedHostIds[$host["host"]])) {
					$hostId = $processedHostIds[$host["host"]];
					if (!isset(referencer->interfacesCache[$hostId])) {
						referencer->interfacesCache[$hostId] = CArray.array();
					}

					for($dbInterfaces as $dbInterface) {
						if ($hostId == $dbInterface["hostid"]
								&& Nest.value($dbInterface,"ip").$() == $interface["ip"]
								&& Nest.value($dbInterface,"dns").$() == $interface["dns"]
								&& Nest.value($dbInterface,"useip").$() == $interface["useip"]
								&& Nest.value($dbInterface,"port").$() == $interface["port"]
								&& Nest.value($dbInterface,"type").$() == $interface["type"]
								&& Nest.value($dbInterface,"main").$() == Nest.value($interface,"main").$()) {

							$refName = Nest.value($interface,"interface_ref").$();
							referencer->interfacesCache[$hostId][$refName] = Nest.value($dbInterface,"interfaceid").$();
						}
					}
				}
			}
		}
	}

	/**
	 * Change all references in host to database ids.
	 *
	 * @throws Exception
	 *
	 * @param array $host
	 *
	 * @return array
	 */
	protected function resolveHostReferences(array $host) {
		for(Nest.value($host,"groups").$() as $gnum => $group) {
			$groupId = referencer->resolveGroup(Nest.value($group,"name").$());
			if (!$groupId) {
				throw new Exception(_s("Group \"%1$s\" for host \"%2$s\" does not exist.", Nest.value($group,"name").$(), Nest.value($host,"host").$()));
			}
			$host["groups"][$gnum] = CArray.array("groupid" => $groupId);
		}

		if (isset(Nest.value($host,"proxy").$())) {
			if (empty(Nest.value($host,"proxy").$())) {
				$proxyId = 0;
			}
			else {
				$proxyId = referencer->resolveProxy(Nest.value($host,"proxy","name").$());
				if (!$proxyId) {
					throw new Exception(_s("Proxy \"%1$s\" for host \"%2$s\" does not exist.", Nest.value($host,"proxy","name").$(), Nest.value($host,"host").$()));
				}
			}

			Nest.value($host,"proxy_hostid").$() = $proxyId;
		}

		if ($hostId = referencer->resolveHost(Nest.value($host,"host").$())) {
			Nest.value($host,"hostid").$() = $hostId;
			for(Nest.value($host,"macros").$() as &$macro) {
				if ($hostMacroId = referencer->resolveMacro($hostId, Nest.value($macro,"macro").$())) {
					Nest.value($macro,"hostmacroid").$() = $hostMacroId;
				}
			}
			unset($macro);
		}


		return $host;
	}

	/**
	 * For existing hosts we need to set an interfaceid for existing interfaces or they will be added.
	 *
	 * @param array $hosts
	 *
	 * @return array
	 */
	protected function addInterfaceIds(array $hosts) {

		$dbInterfaces = API.HostInterface().get(CArray.array(
			"hostids" => zbx_objectValues($hosts, "hostid"),
			"output" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));
		for($dbInterfaces as $dbInterface) {
			for($hosts as $hnum => $host) {
				if (!empty(Nest.value($host,"interfaces").$()) && idcmp(Nest.value($host,"hostid").$(), Nest.value($dbInterface,"hostid").$())) {
					for(Nest.value($host,"interfaces").$() as $inum => $interface) {
						if (Nest.value($dbInterface,"ip").$() == $interface["ip"]
								&& Nest.value($dbInterface,"dns").$() == $interface["dns"]
								&& Nest.value($dbInterface,"useip").$() == $interface["useip"]
								&& Nest.value($dbInterface,"port").$() == $interface["port"]
								&& Nest.value($dbInterface,"type").$() == $interface["type"]
								&& Nest.value($dbInterface,"main").$() == Nest.value($interface,"main").$()) {
							$hosts[$hnum]["interfaces"][$inum]["interfaceid"] = Nest.value($dbInterface,"interfaceid").$();
							break;
						}
					}
				}
				if (empty($hosts[$hnum]["interfaces"])) {
					unset($hosts[$hnum]["interfaces"]);
				}
			}
		}

		return $hosts;
	}

}
