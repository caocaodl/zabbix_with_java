package com.isoft.iradar.imports.importers;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CHostImporter extends CImporter {

	public CHostImporter(CArray options, CImportReferencer referencer) {
		super(options, referencer);
	}

	@Override
	public void doImport(IIdentityBean idBean, CArray<Map> hosts) throws Exception {
		CArray<Map> hostsToCreate = array();
		CArray<Map> hostsToUpdate = array();
		CArray templateLinkage = array();
		for(Map host : hosts) {
			// preserve host related templates to massAdd them later
			if (!empty(Nest.value(this.options,"templateLinkage","createMissing").$()) && !empty(Nest.value(host,"templates").$())) {
				for(Map template : (CArray<Map>)Nest.value(host,"templates").asCArray()) {
					Long templateId = this.referencer.resolveTemplate(idBean, Nest.value(template,"name").asString());
					if (empty(templateId)) {
						throw new Exception(_s("Template \"%1$s\" for host \"%2$s\" does not exist.", Nest.value(template,"name").$(), Nest.value(host,"host").$()));
					}
					if(!isset(templateLinkage,host.get("host"))){
						Nest.value(templateLinkage,host.get("host")).$(array());
					}
					Nest.value(templateLinkage,host.get("host")).asCArray().add(map("templateid", templateId));
				}
			}
			unset(host,"templates");

			host = resolveHostReferences(idBean, host);

			if (isset(host, "hostid")) {
				hostsToUpdate.add(host);
			} else {
				hostsToCreate.add(host);
			}
		}

		hostsToUpdate = addInterfaceIds(idBean, hostsToUpdate);

		// a list of hostids which were created or updated to create an interface cache for those hosts
		CArray<Long> processedHostIds = array();
		// create/update hosts
		if (!empty(Nest.value(this.options,"hosts","createMissing").$()) && !empty(hostsToCreate)) {
			CArray<Long[]> newHostIds = API.Host(idBean, this.referencer.getExecutor()).create(hostsToCreate);
			Long[] hostIds = newHostIds.get("hostids");
			for (int hnum = 0; hnum < hostIds.length; hnum++) {
				Long hostid = hostIds[hnum];
				String hostHost = Nest.value(hostsToCreate, hnum, "host").asString();
				Nest.value(processedHostIds, hostHost).$(hostid);
				this.referencer.addHostRef(hostHost, hostid);
				this.referencer.addProcessedHost(hostHost);

				if (!empty(templateLinkage.get(hostHost))) {
					API.Template(idBean, this.referencer.getExecutor()).massAdd(map(
						"hosts", map("hostid", hostid),
						"templates", templateLinkage.get(hostHost)
					));
				}
			}
		}
		
		if (!empty(Nest.value(options,"hosts","updateExisting").$()) && !empty(hostsToUpdate)) {
			API.Host(idBean, this.referencer.getExecutor()).update(hostsToUpdate);
			for(Map host : hostsToUpdate) {
				this.referencer.addProcessedHost(Nest.value(host,"host").asString());
				Nest.value(processedHostIds,host.get("host")).$(Nest.value(host,"hostid").asLong());

				if (!empty(Nest.value(templateLinkage,host.get("host")).$())) {
					API.Template(idBean, this.referencer.getExecutor()).massAdd(map(
						"hosts", host,
						"templates", templateLinkage.get(host.get("host"))
					));
				}
			}
		}

		// create interfaces cache interface_ref->interfaceid
		CHostIfaceGet options = new CHostIfaceGet();
		options.setHostIds(processedHostIds.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> dbInterfaces = API.HostInterface(idBean, this.referencer.getExecutor()).get(options);
		for(Map host : hosts) {
			for(Map iface : (CArray<Map>)Nest.value(host,"interfaces").asCArray()) {
				if (isset(processedHostIds,host.get("host"))) {
					Long hostId = processedHostIds.get(host.get("host"));
					if (!isset(this.referencer.interfacesCache,hostId)) {
						Nest.value(this.referencer.interfacesCache,hostId).$(array());
					}

					for(Map dbInterface : dbInterfaces) {
						if (Cphp.equals(hostId,Nest.value(dbInterface,"hostid").$())
								&& Cphp.equals(Nest.value(dbInterface,"ip").$(), Nest.value(iface,"ip").$())
								&& Cphp.equals(Nest.value(dbInterface,"dns").$(), Nest.value(iface,"dns").$())
								&& Cphp.equals(Nest.value(dbInterface,"useip").$(), Nest.value(iface,"useip").$())
								&& Cphp.equals(Nest.value(dbInterface,"port").$(), Nest.value(iface,"port").$())
								&& Cphp.equals(Nest.value(dbInterface,"type").$(), Nest.value(iface,"type").$())
								&& Cphp.equals(Nest.value(dbInterface,"main").$(), Nest.value(iface,"main").$())) {

							String refName = Nest.value(iface,"interface_ref").asString();
							Nest.value(this.referencer.interfacesCache,hostId,refName).$(Nest.value(dbInterface,"interfaceid").asLong());
						}
					}
				}
			}
		}
	}

	/**
	 * Change all references in host to database ids.
	 * @throws Exception
	 * @param Map host
	 * @return Map
	 */
	protected Map resolveHostReferences(IIdentityBean idBean,  Map host) throws Exception {
		CArray<Map> groups = Nest.value(host,"groups").asCArray();
		for (Entry<Object, Map> e : groups.entrySet()) {
		    Object gnum = e.getKey();
		    Map group = e.getValue();
			Long groupId = this.referencer.resolveGroup(idBean, Nest.value(group,"name").asString());
			if (empty(groupId)) {
				throw new Exception(_s("Group \"%1$s\" for host \"%2$s\" does not exist.", Nest.value(group,"name").$(), Nest.value(host,"host").$()));
			}
			Nest.value(groups,gnum).$(map("groupid", groupId));
		}

		if (isset(host,"proxy")) {
			Long proxyId = null;
			if (empty(Nest.value(host, "proxy").$())) {
				proxyId = 0L;
			} else {
				proxyId = this.referencer.resolveProxy(idBean, Nest.value(host,"proxy","name").asString());
				if (empty(proxyId)) {
					throw new Exception(_s("Proxy \"%1$s\" for host \"%2$s\" does not exist.", Nest.value(host,"proxy","name").$(), Nest.value(host,"host").$()));
				}
			}
			Nest.value(host,"proxy_hostid").$(proxyId);
		}

		Long hostId = null;
		if (!empty(hostId = this.referencer.resolveHost(idBean, Nest.value(host,"host").asString()))) {
			Nest.value(host, "hostid").$(hostId);
			Long hostMacroId = null;
			for(Map macro : (CArray<Map>)Nest.value(host,"macros").asCArray()) {
				if (!empty(hostMacroId = this.referencer.resolveMacro(idBean, hostId, Nest.value(macro,"macro").asString()))) {
					Nest.value(macro,"hostmacroid").$(hostMacroId);
				}
			}
		}

		return host;
	}

	/**
	 * For existing hosts we need to set an interfaceid for existing interfaces or they will be added.
	 * @param CArray<Map> hosts
	 * @return CArray<Map>
	 */
	protected CArray<Map> addInterfaceIds(IIdentityBean idBean, CArray<Map> hosts) {

		CHostIfaceGet options = new CHostIfaceGet();
		options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		
		CArray<Map> dbIfaces = API.HostInterface(idBean, this.referencer.getExecutor()).get(options);
		
		Object hnum = null, inum = null;
		Map host = null, iface = null;
		CArray<Map> ifaces = null;
		for(Map dbIface : dbIfaces) {
			for (Entry<Object, Map> e : hosts.entrySet()) {
			    hnum = e.getKey();
			    host = e.getValue();
				if (!empty(ifaces = Nest.value(host,"interfaces").asCArray()) && idcmp(Nest.value(host,"hostid").asLong(), Nest.value(dbIface,"hostid").asLong())) {
					for (Entry<Object, Map> es : ifaces.entrySet()) {
						inum = es.getKey();
						iface = es.getValue();
						if (Cphp.equals(Nest.value(dbIface,"ip").$(),Nest.value(iface,"ip").$())
							&& Cphp.equals(Nest.value(dbIface,"dns").$(), Nest.value(iface,"dns").$())
							&& Cphp.equals(Nest.value(dbIface,"useip").$(),Nest.value(iface,"useip").$())
							&& Cphp.equals(Nest.value(dbIface,"port").$(),Nest.value(iface,"port").$())
							&& Cphp.equals(Nest.value(dbIface,"type").$(),Nest.value(iface,"type").$())
							&& Cphp.equals(Nest.value(dbIface,"main").$(), Nest.value(iface,"main").$())) {
							Nest.value(hosts,hnum,"interfaces",inum,"interfaceid").$(Nest.value(dbIface,"interfaceid").$());
							break;
						}
					}
				}
				if (empty(Nest.value(hosts, hnum, "interfaces").$())) {
					unset(hosts.get(hnum), "interfaces");
				}
			}
		}

		return hosts;
	}

}
