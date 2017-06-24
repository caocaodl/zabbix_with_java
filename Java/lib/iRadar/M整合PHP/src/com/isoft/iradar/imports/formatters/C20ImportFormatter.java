package com.isoft.iradar.imports.formatters;

import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.helpers.CArrayHelper.convertFieldToArray;
import static com.isoft.iradar.helpers.CArrayHelper.getByKeys;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_NOTSUPPORTED;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Import formatter for version 2.0
 */
public class C20ImportFormatter extends CImportFormatter {

	@Override
	public CArray getGroups() {
		if (!isset(this.data, "groups")) {
			return array();
		}
		return array_values(Nest.value(data, "groups").asCArray());
	}

	@Override
	public CArray getTemplates() {
		CArray templatesData = array();

		CArray<Map> templates = Nest.value(data,"templates").asCArray();
		if (!empty(templates)) {
			CArray<Map> screens = null;
			for(Map template : templates) {
				template = renameData(template, map("template", "host"));

				convertFieldToArray(template, "templates");
				if (empty(Nest.value(template,"templates").$())) {
					unset(template,"templates");
				}
				convertFieldToArray(template, "macros");
				convertFieldToArray(template, "groups");

				convertFieldToArray(template, "screens");
				screens = Nest.value(template, "screens").asCArray();
				if (!empty(screens)) {
					for(Map screen : screens) {
						screen = renameData(screen, map("screen_items", "screenitems"));
					}
				}


				templatesData.add(getByKeys(template, array(
					"groups", "macros", "screens", "templates", "host", "status", "name"
				)));
			}
		}

		return templatesData;
	}

	@Override
	public CArray getHosts() {
		CArray hostsData = array();

		CArray<Map> hosts = Nest.value(this.data,"hosts").asCArray();
		if (!empty(hosts)) {
			CArray<Map> interfaces = null;
			for (Map host : hosts) {
				host = renameData(host, map("proxyid", "proxy_hostid"));

				convertFieldToArray(host, "interfaces");
				interfaces = Nest.value(host,"interfaces").asCArray();
				if (!empty(interfaces)) {
					for (Entry<Object, Map> e : interfaces.entrySet()) {
					    Object inum = e.getKey();
					    Map iface = e.getValue();
						Nest.value(interfaces,inum).$(renameData(iface, map("default", "main")));
					}
				}

				convertFieldToArray(host, "templates");
				if (empty(Nest.value(host,"templates").$())) {
					unset(host,"templates");
				}
				convertFieldToArray(host, "macros");
				convertFieldToArray(host, "groups");

				if (!empty(Nest.value(host,"inventory").$()) && isset(Nest.value(host,"inventory","inventory_mode").$())) {
					Nest.value(host,"inventory_mode").$(Nest.value(host,"inventory","inventory_mode").$());
					unset(host,"inventory","inventory_mode");
				} else {
					Nest.value(host,"inventory_mode").$(HOST_INVENTORY_DISABLED);
				}

				hostsData.add(getByKeys(host, array(
					"inventory", "proxy", "groups", "templates", "macros", "interfaces", "host", "status",
					"ipmi_authtype", "ipmi_privilege", "ipmi_username", "ipmi_password", "name", "inventory_mode"
				)));
			}
		}

		return hostsData;
	}

	@Override
	public CArray getApplications() {
		CArray applicationsData = array();

		CArray<Map> hosts = Nest.value(this.data,"hosts").asCArray();
		if (isset(hosts)) {
			CArray<Map> applications = null;
			for (Map host : hosts) {
				applications = Nest.value(host, "applications").asCArray();
				if (!empty(applications)) {
					for (Map application : applications) {
						Nest.value(applicationsData, host.get("host"), application.get("name")).$(application);
					}
				}
			}
		}
		
		CArray<Map> templates = Nest.value(this.data,"templates").asCArray();
		if (isset(templates)) {
			CArray<Map> applications = null;
			for (Map template : templates) {
				applications = Nest.value(template, "applications").asCArray();
				if (!empty(applications)) {
					for (Map application : applications) {
						Nest.value(applicationsData, template.get("template"), application.get("name")).$(application);
					}
				}
			}
		}

		return applicationsData;
	}

	@Override
	public CArray getItems() {
		CArray itemsData = array();

		CArray<Map> hosts = Nest.value(this.data,"hosts").asCArray();
		if (isset(hosts)) {
			CArray<Map> items = null;
			for (Map host : hosts) {
				items = Nest.value(host, "items").asCArray();
				if (!empty(items)) {
					for (Map item : items) {
						// if a host item has the \"Not supported\" status, convert it to \"Active\"
						if (Nest.value(item,"status").asInteger() == ITEM_STATUS_NOTSUPPORTED) {
							Nest.value(item, "status").$(ITEM_STATUS_ACTIVE);
						}
						item = formatItem(item);
						Nest.value(itemsData, host.get("host"), item.get("key_")).$(item);
					}
				}
			}
		}
		
		CArray<Map> templates = Nest.value(this.data,"templates").asCArray();
		if (isset(templates)) {
			CArray<Map> items = null;
			for (Map template : templates) {
				items = Nest.value(template, "items").asCArray();
				if (!empty(items)) {
					for (Map item : items) {
						item = formatItem(item);
						Nest.value(itemsData, template.get("template"), item.get("key_")).$(item);
					}
				}
			}
		}

		return itemsData;
	}

	@Override
	public CArray getDiscoveryRules() {
		CArray discoveryRulesData = array();

		CArray<Map> hosts = Nest.value(this.data,"hosts").asCArray();
		if (isset(hosts)) {
			CArray<Map> discovery_rules = null;
			for(Map host : hosts) {
				discovery_rules = Nest.value(host,"discovery_rules").asCArray();
				if (!empty(discovery_rules)) {
					for (Map item : discovery_rules) {
						// if a discovery rule has the \"Not supported\" status, convert it to \"Active\"
						if (Nest.value(item,"status").asInteger() == ITEM_STATUS_NOTSUPPORTED) {
							Nest.value(item,"status").$(ITEM_STATUS_ACTIVE);
						}
						item = formatDiscoveryRule(item);
						Nest.value(discoveryRulesData,host.get("host"),item.get("key_")).$(item);
					}
				}
			}
		}

		CArray<Map> templates = Nest.value(this.data,"templates").asCArray();
		if (isset(templates)) {
			CArray<Map> discovery_rules = null;
			for(Map template : templates) {
				discovery_rules = Nest.value(template,"discovery_rules").asCArray();
				if (!empty(discovery_rules)) {
					for (Map item : discovery_rules) {
						item = formatDiscoveryRule(item);
						Nest.value(discoveryRulesData,template.get("template"),item.get("key_")).$(item);
					}
				}
			}
		}

		return discoveryRulesData;
	}

	@Override
	public CArray getGraphs() {
		CArray graphsData = array();

		CArray<Map> graphs = Nest.value(this.data,"graphs").asCArray();
		if (isset(graphs) && !empty(graphs)) {
			CArray<Map> gitems = null;
			for (Map graph : graphs) {
				graph = renameGraphFields(graph);

				gitems = Nest.value(graph, "gitems").asCArray();
				if (isset(gitems) && !empty(gitems)) {
					Nest.value(graph, "gitems").$(array_values(gitems));
				}

				graphsData.add(graph);
			}
		}

		return graphsData;
	}

	@Override
	public CArray getTriggers() {
		CArray triggersData = array();

		CArray<Map> triggers = Nest.value(this.data,"triggers").asCArray();
		if (!empty(triggers)) {
			for(Map trigger : triggers) {
				convertFieldToArray(trigger, "dependencies");
				triggersData.add(renameTriggerFields(trigger));
			}
		}

		return triggersData;
	}

	@Override
	public CArray getImages() {
		CArray imagesData = array();

		CArray<Map> images = Nest.value(this.data,"images").asCArray();
		if (!empty(images)) {
			for (Map image : images) {
				imagesData.add(renameData(image, map("encodedImage", "image")));
			}
		}

		return imagesData;
	}

	@Override
	public CArray getMaps() {
		CArray mapsData = array();

		CArray<Map> maps = Nest.value(this.data,"maps").asCArray();
		if (!empty(maps)) {
			CArray<Map> selements = null, links = null;
			for (Map map : maps) {
				convertFieldToArray(map, "selements");
				selements = Nest.value(map, "selements").asCArray();
				for (Map selement : selements) {
					convertFieldToArray(selement, "urls");
				}

				convertFieldToArray(map, "links");
				links = Nest.value(map, "links").asCArray();
				for (Map link : links) {
					convertFieldToArray(link, "linktriggers");
				}

				convertFieldToArray(map, "urls");
				mapsData.add(map);
			}
		}
		return mapsData;
	}

	@Override
	public CArray getScreens() {
		CArray screensData = array();
		CArray<Map> screens = Nest.value(this.data,"screens").asCArray();
		if (!empty(screens)) {
			for (Map screen : screens) {
				screen = renameData(screen, map("screen_items", "screenitems"));
				convertFieldToArray(screen, "screenitems");
				screensData.add(screen);
			}
		}
		return screensData;
	}

	@Override
	public CArray getTemplateScreens() {
		CArray screensData = array();
		CArray<Map> templates = Nest.value(this.data,"templates").asCArray();
		if (isset(templates)) {
			CArray<Map> screens = null;
			for (Map template : templates) {
				screens = Nest.value(template, "screens").asCArray();
				if (!empty(screens)) {
					for (Map screen : screens) {
						screen = renameData(screen, map("screen_items", "screenitems"));
						convertFieldToArray(screen, "screenitems");
						Nest.value(screensData, template.get("template"), screen.get("name")).$(screen);
					}
				}
			}
		}
		return screensData;
	}
	
	/**
	 * Format item.
	 * @param Map item
	 * @return array
	 */
	protected Map formatItem(Map item) {
		item = renameItemFields(item);
		if (empty(Nest.value(item,"applications").$())) {
			Nest.value(item,"applications").$(array());
		}
		return item;
	}
	
	/**
	 * Format discovery rule.
	 * @param Map discoveryRule
	 * @return Map
	 */
	protected Map formatDiscoveryRule(Map discoveryRule) {
		discoveryRule = renameItemFields(discoveryRule);

		CArray<Map> itemprototypes = Nest.value(discoveryRule,"item_prototypes").asCArray();
		if (!empty(itemprototypes)) {
			for (Map prototype : itemprototypes) {
				prototype = renameItemFields(prototype);
				convertFieldToArray(prototype, "applications");
			}
		} else {
			Nest.value(discoveryRule, "item_prototypes").$(array());
		}

		CArray<Map> triggerprototypes = Nest.value(discoveryRule,"trigger_prototypes").asCArray();
		if (!empty(triggerprototypes)) {
			for (Map trigger : triggerprototypes) {
				trigger = renameTriggerFields(trigger);
			}
		} else {
			Nest.value(discoveryRule, "trigger_prototypes").$(array());
		}

		CArray<Map> graphprototypes = Nest.value(discoveryRule,"graph_prototypes").asCArray();
		if (!empty(graphprototypes)) {
			for (Map graph : graphprototypes) {
				graph = renameGraphFields(graph);
			}
		} else {
			Nest.value(discoveryRule, "graph_prototypes").$(array());
		}

		CArray<Map> hostprototypes = Nest.value(discoveryRule, "host_prototypes").asCArray();
		if (!empty(hostprototypes)) {
			for (Map hostPrototype : hostprototypes) {
				convertFieldToArray(hostPrototype, "group_prototypes");
				convertFieldToArray(hostPrototype, "templates");
			}
		} else {
			Nest.value(discoveryRule, "host_prototypes").$(array());
		}

		return discoveryRule;
	}
	
	/**
	 * Rename items, discovery rules, item prototypes fields.
	 * @param Map item
	 * @return Map
	 */
	protected Map renameItemFields(Map item) {
		return renameData(item, map("key", "key_", "allowed_hosts", "trapper_hosts"));
	}
	
	/**
	 * Rename triggers, trigger prototypes fields.
	 * @param Map trigger
	 * @return Map
	 */
	protected Map renameTriggerFields(Map trigger) {
		trigger = renameData(trigger, map("description", "comments"));
		return renameData(trigger, map("name", "description", "severity", "priority"));
	}

	/**
	 * Rename graphs, graph prototypes fields.
	 * @param Map graph
	 * @return Map
	 */
	protected Map renameGraphFields(Map graph) {
		return renameData(graph, map(
			"type", "graphtype",
			"ymin_type_1", "ymin_type",
			"ymax_type_1", "ymax_type",
			"graph_items", "gitems"
		));
	}
	
}
