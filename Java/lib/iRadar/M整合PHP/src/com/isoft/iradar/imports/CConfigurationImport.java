package com.isoft.iradar.imports;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.formatters.C20ImportFormatter;
import com.isoft.iradar.imports.formatters.CImportFormatter;
import com.isoft.iradar.imports.importers.CHostImporter;
import com.isoft.iradar.imports.importers.CScreenImporter;
import com.isoft.iradar.imports.importers.CTemplateScreenImporter;
import com.isoft.iradar.imports.readers.CImportReader;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class for importing configuration data.
 */
public class CConfigurationImport {

	/**
	 * @var CImportReader
	 */
	protected CImportReader reader;

	/**
	 * @var CImportFormatter
	 */
	protected CImportFormatter formatter;

	/**
	 * @var CImportReferencer
	 */
	protected CImportReferencer referencer;

	/**
	 * @var array
	 */
	protected CArray options;

	/**
	 * @var string with import data in one of supported formats
	 */
	protected String source;

	/**
	 * @var array with data read from source string
	 */
	protected CArray data;

	/**
	 * @var array with formatted data received from formatter
	 */
	protected CArray formattedData = array();
	
	/**
	 * Constructor.
	 * Source string must be suitable for reader class,
	 * i.e. if string contains json then reader should be able to read json.
	 *
	 * @param String source
	 * @param CArray options
	 */
	public CConfigurationImport(String source, CArray options){
		this.options = map(
			"groups", map("createMissing", false),
			"hosts", map("updateExisting", false, "createMissing", false),
			"templates", map("updateExisting", false, "createMissing", false),
			"templateScreens", map("updateExisting", false, "createMissing", false),
			"applications", map("updateExisting", false, "createMissing", false),
			"templateLinkage", map("createMissing", false),
			"items", map("updateExisting", false, "createMissing", false),
			"discoveryRules", map("updateExisting", false, "createMissing", false),
			"triggers", map("updateExisting", false, "createMissing", false),
			"graphs", map("updateExisting", false, "createMissing", false),
			"screens", map("updateExisting", false, "createMissing", false),
			"maps", map("updateExisting", false, "createMissing", false),
			"images", map("updateExisting", false, "createMissing", false)
		);
		this.options = array_merge(this.options, options);
		this.source = source;
	}
	
	/**
	 * Import groups.
	 */
	protected void processGroups() {
//		$groups = getFormattedGroups();
//		if (empty($groups)) {
//			return;
//		}
//
//		// skip the groups that already exist
//		for($groups as $gnum => $group) {
//			if (referencer->resolveGroup(Nest.value($group,"name").$())) {
//				unset($groups[$gnum]);
//			}
//		}
//
//		if ($groups) {
//			// reset indexing because ids from api does not preserve input array keys
//			$groups = array_values($groups);
//			$newGroups = API.HostGroup(executor).create($groups);
//			for(Nest.value($newGroups,"groupids").$() as $gnum => $groupid) {
//				referencer->addGroupRef($groups[$gnum]["name"], $groupid);
//			}
//		}
	}

	/**
	 * Import templates.
	 *
	 * @throws Exception
	 */
	protected void processTemplates() {
//		if ($templates = getFormattedTemplates()) {
//			$templateImporter = new CTemplateImporter(options, referencer);
//			$templateImporter.import($templates);
//		}
	}

	/**
	 * Import hosts.
	 *
	 * @throws Exception
	 */
	protected void processHosts() {
//		if ($hosts = getFormattedHosts()) {
//			$hostImporter = new CHostImporter(options, referencer);
//			$hostImporter.import($hosts);
//		}
	}

	/**
	 * Import applications.
	 */
	protected void processApplications() {
//		$allApplciations = getFormattedApplications();
//		if (empty($allApplciations)) {
//			return;
//		}
//
//		$applicationsToCreate = array();
//		for($allApplciations as $host => $applications) {
//			if (!referencer->isProcessedHost($host)) {
//				continue;
//			}
//
//			$hostid = referencer->resolveHostOrTemplate($host);
//			for($applications as $application) {
//				Nest.value($application,"hostid").$() = $hostid;
//				$appId = referencer->resolveApplication($hostid, Nest.value($application,"name").$());
//				if (!$appId) {
//					$applicationsToCreate[] = $application;
//				}
//			}
//		}
//
//		// create the applications and create a hash hostid->name->applicationid
//		if (!empty($applicationsToCreate)) {
//			$newApplicationsIds = API.Application(executor).create($applicationsToCreate);
//			for(Nest.value($newApplicationsIds,"applicationids").$() as $anum => $applicationId) {
//				$application = $applicationsToCreate[$anum];
//				referencer->addApplicationRef(Nest.value($application,"hostid").$(), Nest.value($application,"name").$(), $applicationId);
//			}
//		}
//
//		// refresh applications because templated ones can be inherited to host and used in items
//		referencer->refreshApplications();
	}
	
	/**
	 * Import items.
	 */
	protected void processItems() {
//		CArray $allItems = getFormattedItems();
//		if (empty($allItems)) {
//			return;
//		}
//
//		CArray $itemsToCreate = array();
//		CArray $itemsToUpdate = array();
//		for($allItems as $host => $items) {
//			if (!referencer->isProcessedHost($host)) {
//				continue;
//			}
//
//			$hostid = referencer->resolveHostOrTemplate($host);
//			for($items as $item) {
//				Nest.value($item,"hostid").$() = $hostid;
//
//				if (isset(Nest.value($item,"applications").$()) && Nest.value($item,"applications").$()) {
//					$applicationsIds = array();
//					for(Nest.value($item,"applications").$() as $application) {
//						if ($applicationId = referencer->resolveApplication($hostid, Nest.value($application,"name").$())) {
//							$applicationsIds[] = $applicationId;
//						}
//						else {
//							throw new Exception(_s("Item \"%1$s\" on \"%2$s\": application \"%3$s\" does not exist.",
//								Nest.value($item,"name").$(), $host, Nest.value($application,"name").$()));
//						}
//					}
//					Nest.value($item,"applications").$() = $applicationsIds;
//				}
//
//				if (isset(Nest.value($item,"interface_ref").$()) && Nest.value($item,"interface_ref").$()) {
//					Nest.value($item,"interfaceid").$() = referencer->interfacesCache[$hostid][$item["interface_ref"]];
//				}
//
//				if (isset(Nest.value($item,"valuemap").$()) && Nest.value($item,"valuemap").$()) {
//					$valueMapId = referencer->resolveValueMap(Nest.value($item,"valuemap","name").$());
//					if (!$valueMapId) {
//						throw new Exception(_s(
//							"Cannot find value map \"%1$s\" used for item \"%2$s\" on \"%3$s\".",
//							Nest.value($item,"valuemap","name").$(),
//							Nest.value($item,"name").$(),
//							$host
//						));
//					}
//					Nest.value($item,"valuemapid").$() = $valueMapId;
//				}
//
//				$itemsId = referencer->resolveItem($hostid, Nest.value($item,"key_").$());
//
//				if ($itemsId) {
//					Nest.value($item,"itemid").$() = $itemsId;
//					$itemsToUpdate[] = $item;
//				}
//				else {
//					$itemsToCreate[] = $item;
//				}
//			}
//		}
//
//		// create/update the items and create a hash hostid->key_->itemid
//		if (Nest.value(options,"items","createMissing").$() && $itemsToCreate) {
//			$newItemsIds = API.Item(executor).create($itemsToCreate);
//			for(Nest.value($newItemsIds,"itemids").$() as $inum => $itemid) {
//				$item = $itemsToCreate[$inum];
//				referencer->addItemRef(Nest.value($item,"hostid").$(), Nest.value($item,"key_").$(), $itemid);
//			}
//		}
//		if (Nest.value(options,"items","updateExisting").$() && $itemsToUpdate) {
//			API.Item(executor)->update($itemsToUpdate);
//		}
//
//		// refresh items because templated ones can be inherited to host and used in triggers, grahs, etc.
//		referencer->refreshItems();
	}
	
	/**
	 * Import discovery rules.
	 *
	 * @throws Exception
	 */
	protected void processDiscoveryRules(IIdentityBean idBean) throws Exception {
		CArray<CArray<Map>> allDiscoveryRules = getFormattedDiscoveryRules();
		if (empty(allDiscoveryRules)) {
			return;
		}

		// unset rules that are related to hosts we did not process
		for (Entry<Object, CArray<Map>> e : allDiscoveryRules.entrySet()) {
		    String host = Nest.as(e.getKey()).asString();
		    //CArray<Map> $discoveryRules = e.getValue();
			if (!this.referencer.isProcessedHost(host)) {
				unset(allDiscoveryRules,host);
			}
		}

		CArray<Map> itemsToCreate = array();
		CArray<Map> itemsToUpdate = array();
		for (Entry<Object, CArray<Map>> e : allDiscoveryRules.entrySet()) {
		    String host = Nest.as(e.getKey()).asString();
		    CArray<Map> discoveryRules = e.getValue();
			Long hostid = this.referencer.resolveHostOrTemplate(idBean, host);
			for(Map item : discoveryRules) {
				Nest.value(item,"hostid").$(hostid);

				if (isset(item,"interface_ref")) {
					Nest.value(item,"interfaceid").$(this.referencer.interfacesCache.get(hostid).get(item.get("interface_ref")));
				}
				unset(item,"item_prototypes");
				unset(item,"trigger_prototypes");
				unset(item,"graph_prototypes");
				unset(item,"host_prototypes");

				Long itemId = this.referencer.resolveItem(idBean, hostid, Nest.value(item,"key_").asString());
				if (!empty(itemId)) {
					Nest.value(item,"itemid").$(itemId);
					itemsToUpdate.add(item);
				} else {
					itemsToCreate.add(item);
				}
			}
		}

		// create/update discovery rules and add processed rules to array $processedRules
		CArray processedRules = array();
		if (Nest.value(options,"discoveryRules","createMissing").asBoolean() && !empty(itemsToCreate)) {
			CArray<Long[]> newItemsIds = API.DiscoveryRule(idBean, this.referencer.getExecutor()).create(itemsToCreate);
			Long[] itemids = newItemsIds.get("itemids");
			for (int inum = 0; inum < itemids.length; inum++) {
				Long itemid = itemids[inum];
				Map item = itemsToCreate.get(inum);
				this.referencer.addItemRef(Nest.value(item,"hostid").asLong(), Nest.value(item,"key_").asString(), itemid);
			}
			for(Map item : itemsToCreate) {
				Nest.value(processedRules,item.get("hostid"),item.get("key_")).$(1);
			}

		}
		if (Nest.value(options,"discoveryRules","updateExisting").asBoolean() && !empty(itemsToUpdate)) {
			API.DiscoveryRule(idBean, this.referencer.getExecutor()).update(itemsToUpdate);
			for(Map item : itemsToUpdate) {
				Nest.value(processedRules,item.get("hostid"),item.get("key_")).$(1);
			}
		}

		// refresh discovery rules because templated ones can be inherited to host and used for prototypes
		this.referencer.refreshItems();

		// process prototypes
		CArray<Map> prototypesToUpdate = array();
		CArray<Map> prototypesToCreate = array();
		CArray<Map> hostPrototypesToUpdate = array();
		CArray<Map> hostPrototypesToCreate = array();
		for (Entry<Object, CArray<Map>> e : allDiscoveryRules.entrySet()) {
		    String host = Nest.as(e.getKey()).asString();
		    CArray<Map> discoveryRules = e.getValue();
			Long hostid = this.referencer.resolveHostOrTemplate(idBean, host);
			for(Map item : discoveryRules) {
				// if rule was not processed we should not create/update any of its prototypes
				if (!isset(Nest.value(processedRules,hostid,item.get("key_")).$())) {
					continue;
				}

				Nest.value(item,"hostid").$(hostid);
				Long itemId = this.referencer.resolveItem(idBean, hostid, Nest.value(item,"key_").asString());

				// prototypes
				for(Map prototype : (CArray<Map>)Nest.value(item,"item_prototypes").asCArray()) {
					Nest.value(prototype,"hostid").$(hostid);

					CArray applicationsIds = array();
					for(Map application : (CArray<Map>)Nest.value(prototype,"applications").asCArray()) {
						applicationsIds.add(this.referencer.resolveApplication(idBean, hostid, Nest.value(application,"name").asString()));
					}
					Nest.value(prototype,"applications").$(applicationsIds);

					if (isset(prototype,"interface_ref")) {
						Nest.value(prototype,"interfaceid").$(this.referencer.interfacesCache.get(hostid).get(prototype.get("interface_ref")));
					}

					if (!empty(Nest.value(prototype,"valuemap").$())) {
						Long valueMapId = this.referencer.resolveValueMap(Nest.value(prototype,"valuemap","name").asString());
						if (empty(valueMapId)) {
							throw new Exception(_s(
								"Cannot find value map \"%1$s\" used for item prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value(prototype,"valuemap","name").$(),
								Nest.value(prototype,"name").$(),
								Nest.value(item,"name").$(),
								host
							));
						}
						Nest.value(prototype,"valuemapid").$(valueMapId);
					}

					Long prototypeId = this.referencer.resolveItem(idBean, hostid, Nest.value(prototype,"key_").asString());
					Nest.value(prototype,"rule").$(map("hostid", hostid, "key", Nest.value(item,"key_").$()));
					if (!empty(prototypeId)) {
						Nest.value(prototype,"itemid").$(prototypeId);
						prototypesToUpdate.add(prototype);
					} else {
						prototypesToCreate.add(prototype);
					}
				}

				// host prototype
				for(Map hostPrototype : (CArray<Map>)Nest.value(item,"host_prototypes").asCArray()) {
					// resolve group prototypes
					CArray groupLinks = array();
					for(Map groupLink : (CArray<Map>)Nest.value(hostPrototype,"group_links").asCArray()) {
						Long groupId = this.referencer.resolveGroup(idBean, Nest.value(groupLink,"group","name").asString());
						if (empty(groupId)) {
							throw new Exception(_s(
								"Cannot find host group \"%1$s\" for host prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value(groupLink,"group","name").$(),
								Nest.value(hostPrototype,"name").$(),
								Nest.value(item,"name").$(),
								host
							));
						}
						groupLinks.add(map("groupid", groupId));
					}
					Nest.value(hostPrototype,"groupLinks").$(groupLinks);
					Nest.value(hostPrototype,"groupPrototypes").$(Nest.value(hostPrototype,"group_prototypes").$());
					unset(hostPrototype,"group_links");
					unset(hostPrototype,"group_prototypes");

					// resolve templates
					CArray templates = array();
					for(Map template : (CArray<Map>)Nest.value(hostPrototype,"templates").asCArray()) {
						Long templateId = this.referencer.resolveTemplate(idBean, Nest.value(template,"name").asString());
						if (empty(templateId)) {
							throw new Exception(_s(
								"Cannot find template \"%1$s\" for host prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value(template,"name").$(),
								Nest.value(hostPrototype,"name").$(),
								Nest.value(item,"name").$(),
								host
							));
						}
						templates.add(map("templateid", templateId));
					}
					Nest.value(hostPrototype,"templates").$(templates);

					Long hostPrototypeId = this.referencer.resolveHostPrototype(idBean, hostid, itemId, Nest.value(hostPrototype,"host").asString());
					if (!empty(hostPrototypeId)) {
						Nest.value(hostPrototype,"hostid").$(hostPrototypeId);
						hostPrototypesToUpdate.add(hostPrototype);
					} else {
						Nest.value(hostPrototype,"ruleid").$(itemId);
						hostPrototypesToCreate.add(hostPrototype);
					}
				}

				if (isset(item,"interface_ref")) {
					Nest.value(item,"interfaceid").$(this.referencer.interfacesCache.get(hostid).get(item.get("interface_ref")));
				}
				unset(item,"item_prototypes");
				unset(item,"trigger_prototypes");
				unset(item,"graph_prototypes");
				unset(item,"host_prototypes");

				Long itemsId = this.referencer.resolveItem(idBean, hostid, Nest.value(item,"key_").asString());
				if (!empty(itemsId)) {
					Nest.value(item,"itemid").$(itemsId);
					itemsToUpdate.add(item);
				} else {
					itemsToCreate.add(item);
				}
			}
		}

		if (!empty(prototypesToCreate)) {
			for(Map prototype : prototypesToCreate) {
				Nest.value(prototype,"ruleid").$(this.referencer.resolveItem(idBean, Nest.value(prototype,"rule","hostid").asLong(), Nest.value(prototype,"rule","key").asString()));
			}
			CArray<Long[]> newPrototypeIds = API.ItemPrototype(idBean, this.referencer.getExecutor()).create(prototypesToCreate);
			Long[] itemids = newPrototypeIds.get("itemids");
			for (int inum = 0; inum < itemids.length; inum++) {
				Long itemid = itemids[inum];
				Map item = prototypesToCreate.get(inum);
				this.referencer.addItemRef(Nest.value(item,"hostid").asLong(), Nest.value(item,"key_").asString(), itemid);
			}
		}
		if (!empty(prototypesToUpdate)) {
			for(Map prototype : prototypesToCreate) {
				Nest.value(prototype,"ruleid").$(this.referencer.resolveItem(idBean, Nest.value(prototype,"rule","hostid").asLong(), Nest.value(prototype,"rule","key").asString()));
			}
			API.ItemPrototype(idBean, this.referencer.getExecutor()).update(prototypesToUpdate);
		}

		if (!empty(hostPrototypesToCreate)) {
			API.HostPrototype(idBean, this.referencer.getExecutor()).create(hostPrototypesToCreate);
		}
		if (!empty(hostPrototypesToUpdate)) {
			API.HostPrototype(idBean, this.referencer.getExecutor()).update(hostPrototypesToUpdate);
		}

		// refresh prototypes because templated ones can be inherited to host and used in triggers prototypes or graph prototypes
		this.referencer.refreshItems();

		// first we need to create item prototypes and only then graph prototypes
		CArray triggersToCreate = array();
		CArray triggersToUpdate = array();
		CArray graphsToCreate = array();
		CArray graphsToUpdate = array();
		for (Entry<Object, CArray<Map>> e : allDiscoveryRules.entrySet()) {
		    String host = Nest.as(e.getKey()).asString();
		    CArray<Map> discoveryRules = e.getValue();
			Long hostid = this.referencer.resolveHostOrTemplate(idBean, host);
			for(Map item : discoveryRules) {
				// if rule was not processed we should not create/update any of its prototypes
				if (!isset(Nest.value(processedRules,hostid,item.get("key_")).$())) {
					continue;
				}

				// trigger prototypes
				for(Map trigger : (CArray<Map>)Nest.value(item,"trigger_prototypes").asCArray()) {
					Long triggerId = this.referencer.resolveTrigger(idBean, Nest.value(trigger,"description").asString(), Nest.value(trigger,"expression").asString());

					if (!empty(triggerId)) {
						Nest.value(trigger,"triggerid").$(triggerId);
						triggersToUpdate.add(trigger);
					} else {
						triggersToCreate.add(trigger);
					}
				}

				// graph prototypes
				for(Map graph : (CArray<Map>)Nest.value(item,"graph_prototypes").asCArray()) {
					CArray graphHostIds = array();

					if (!empty(Nest.value(graph,"ymin_item_1").$())) {
						Long hostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(graph,"ymin_item_1","host").asString());
						Long itemId = !empty(hostId)
							? this.referencer.resolveItem(idBean, hostId, Nest.value(graph,"ymin_item_1","key").asString())
							: null;

						if (empty(itemId)) {
							throw new Exception(_s(
								"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MIN value for graph prototype \"%3$s\" of discovery rule \"%4$s\" on \"%5$s\".",
								Nest.value(graph,"ymin_item_1","key").$(),
								Nest.value(graph,"ymin_item_1","host").$(),
								Nest.value(graph,"name").$(),
								Nest.value(item,"name").$(),
								host
							));
						}

						Nest.value(graph,"ymin_itemid").$(itemId);
					}

					if (!empty(Nest.value(graph,"ymax_item_1").$())) {
						Long hostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(graph,"ymax_item_1","host").asString());
						Long temId = !empty(hostId)
							? this.referencer.resolveItem(idBean, hostId, Nest.value(graph,"ymax_item_1","key").asString())
							: null;

						if (empty(temId)) {
							throw new Exception(_s(
								"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MAX value for graph prototype \"%3$s\" of discovery rule \"%4$s\" on \"%5$s\".",
								Nest.value(graph,"ymax_item_1","key").$(),
								Nest.value(graph,"ymax_item_1","host").$(),
								Nest.value(graph,"name").$(),
								Nest.value(item,"name").$(),
								host
							));
						}

						Nest.value(graph,"ymax_itemid").$(temId);
					}

					Long gitemHostId = null;
					for(Map gitem : (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
						if (empty(gitemHostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(gitem,"item","host").asString()))) {
							throw new Exception(_s("Cannot find host or template \"%1$s\" used in graph \"%2$s\".",
								Nest.value(gitem,"item","host").$(), Nest.value(graph,"name").$()));
						}
						Nest.value(gitem,"itemid").$(this.referencer.resolveItem(idBean, gitemHostId, Nest.value(gitem,"item","key").asString()));
						Nest.value(graphHostIds,gitemHostId).$(gitemHostId);
					}

					// TODO: do this for all graphs at once
					SqlBuilder sqlParts = new SqlBuilder();
					String sql = "SELECT g.graphid"+
							" FROM graphs g,graphs_items gi,items i"+
							" WHERE g.graphid=gi.graphid"+
								" AND gi.itemid=i.itemid"+
								" AND g.name="+sqlParts.marshalParam(Nest.value(graph,"name").asString())+
								" AND "+sqlParts.dual.dbConditionInt("i.hostid", graphHostIds.valuesAsLong());
					Map graphExists = DBfetch(DBselect(this.referencer.getExecutor(),sql,sqlParts.getNamedParams()));

					if (!empty(graphExists)) {
						CGraphPrototypeGet options = new CGraphPrototypeGet();
						options.setGraphIds(Nest.value(graphExists,"graphid").asLong());
						options.setOutput(new String[]{"graphid"});
						options.setEditable(true);
						CArray<Map> dbGraph = API.GraphPrototype(idBean, this.referencer.getExecutor()).get(options);
						if (empty(dbGraph)) {
							throw new Exception(_s("No permission for graph \"%1$s\".", Nest.value(graph,"name").$()));
						}
						Nest.value(graph,"graphid").$(Nest.value(graphExists,"graphid").asLong());
						graphsToUpdate.add(graph);
					} else {
						graphsToCreate.add(graph);
					}
				}
			}
		}

		if (!empty(triggersToCreate)) {
			API.TriggerPrototype(idBean, this.referencer.getExecutor()).create(triggersToCreate);
		}
		if (!empty(triggersToUpdate)) {
			API.TriggerPrototype(idBean, this.referencer.getExecutor()).update(triggersToUpdate);
		}

		if (!empty(graphsToCreate)) {
			API.GraphPrototype(idBean, this.referencer.getExecutor()).create(graphsToCreate);
		}
		if (!empty(graphsToUpdate)) {
			API.GraphPrototype(idBean, this.referencer.getExecutor()).update(graphsToUpdate);
		}
	}
	
	/**
	 * Import graphs.
	 * @throws Exception
	 */
	protected void processGraphs(IIdentityBean idBean) throws Exception {
		CArray<Map> allGraphs = getFormattedGraphs();
		if (empty(allGraphs)) {
			return;
		}

		CArray graphsToCreate = array();
		CArray graphsToUpdate = array();
		for(Map graph : allGraphs) {
			CArray graphHostIds = array();

			if (!empty(Nest.value(graph,"ymin_item_1").$())) {
				Long hostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(graph,"ymin_item_1","host").asString());
				Long itemId = !empty(hostId)
					? this.referencer.resolveItem(idBean, hostId, Nest.value(graph,"ymin_item_1","key").asString())
					: null;

				if (empty(itemId)) {
					throw new Exception(_s(
						"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MIN value for graph \"%3$s\".",
						Nest.value(graph,"ymin_item_1","key").$(),
						Nest.value(graph,"ymin_item_1","host").$(),
						Nest.value(graph,"name").$()
					));
				}

				Nest.value(graph,"ymin_itemid").$(itemId);
			}

			if (!empty(Nest.value(graph,"ymax_item_1").$())) {
				Long hostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(graph,"ymax_item_1","host").asString());
				Long itemId = !empty(hostId)
					? this.referencer.resolveItem(idBean, hostId, Nest.value(graph,"ymax_item_1","key").asString())
					: null;

				if (empty(itemId)) {
					throw new Exception(_s(
						"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MAX value for graph \"%3$s\".",
						Nest.value(graph,"ymax_item_1","key").$(),
						Nest.value(graph,"ymax_item_1","host").$(),
						Nest.value(graph,"name").$()
					));
				}

				Nest.value(graph,"ymax_itemid").$(itemId);
			}

			if (isset(Nest.value(graph,"gitems").$()) && !empty(Nest.value(graph,"gitems").$())) {
				for(Map gitem : (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					Long gitemHostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(gitem,"item","host").asString());

					if (empty(gitemHostId)) {
						throw new Exception(_s(
							"Cannot find host or template \"%1$s\" used in graph \"%2$s\".",
							Nest.value(gitem,"item","host").$(),
							Nest.value(graph,"name").$()
						));
					}

					Nest.value(gitem,"itemid").$(this.referencer.resolveItem(idBean, gitemHostId, Nest.value(gitem,"item","key").asString()));

					Nest.value(graphHostIds,gitemHostId).$(gitemHostId);
				}
			}

			// TODO: do this for all graphs at once
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT g.graphid"+
					" FROM graphs g,graphs_items gi,items i"+
					" WHERE g.graphid=gi.graphid"+
						" AND gi.itemid=i.itemid"+
						" AND g.name="+sqlParts.marshalParam(Nest.value(graph,"name").$())+
						" AND "+sqlParts.dual.dbConditionInt("i.hostid", graphHostIds.valuesAsLong());
			Map graphExists = DBfetch(DBselect(this.referencer.getExecutor(), sql, sqlParts.getNamedParams()));

			if (!empty(graphExists)) {
				CGraphGet options = new CGraphGet();
				options.setGraphIds(Nest.value(graphExists,"graphid").asLong());
				options.setOutput(new String[]{"graphid"});
				options.setEditable(true);
				CArray<Map> dbGraph = API.Graph(idBean, this.referencer.getExecutor()).get(options);
				if (empty(dbGraph)) {
					throw new Exception(_s("No permission for graph \"%1$s\".", Nest.value(graph,"name").$()));
				}
				Nest.value(graph,"graphid").$(Nest.value(graphExists,"graphid").$());
				graphsToUpdate.add(graph);
			} else {
				graphsToCreate.add(graph);
			}
		}

		if (Nest.value(options,"graphs","createMissing").asBoolean() && !empty(graphsToCreate)) {
			API.Graph(idBean, this.referencer.getExecutor()).create(graphsToCreate);
		}
		if (Nest.value(options,"graphs","updateExisting").asBoolean() && !empty(graphsToUpdate)) {
			API.Graph(idBean, this.referencer.getExecutor()).update(graphsToUpdate);
		}
	}
	
	/**
	 * Import triggers.
	 * @throws Exception 
	 */
	protected void processTriggers(IIdentityBean idBean) throws Exception {
		CArray<Map> allTriggers = getFormattedTriggers();
		if (empty(allTriggers)) {
			return;
		}

		CArray<Map> triggersToCreate = array();
		CArray triggersToUpdate = array();
		CArray<CArray<Map>> triggersToCreateDependencies = array();
		for(Map trigger : allTriggers) {
			Long triggerId = this.referencer.resolveTrigger(idBean, Nest.value(trigger,"description").asString(), Nest.value(trigger,"expression").asString());
			if (!empty(triggerId)) {
				CArray deps = array();
				for(Map dependency : (CArray<Map>)Nest.value(trigger,"dependencies").asCArray()) {
					Long depTriggerId = this.referencer.resolveTrigger(idBean, Nest.value(dependency,"name").asString(), Nest.value(dependency,"expression").asString());
					if (empty(depTriggerId)) {
						throw new Exception(_s("Trigger \"%1$s\" depends on trigger \"%2$s\", which does not exist.", Nest.value(trigger,"description").$(), Nest.value(dependency,"name").$()));
					}
					deps.add(map("triggerid", depTriggerId));
				}

				Nest.value(trigger,"dependencies").$(deps);
				Nest.value(trigger,"triggerid").$(triggerId);
				triggersToUpdate.add(trigger);
			} else {
				triggersToCreateDependencies.add(Nest.value(trigger,"dependencies").asCArray());
				unset(trigger,"dependencies");
				triggersToCreate.add(trigger);
			}
		}

		CArray triggerDependencies = array();
		CArray<Map> newTriggers = array();
		CArray<Long[]> newTriggerIds = null;
		Long[] triggerids = null;
		if (Nest.value(options,"triggers","createMissing").asBoolean() && !empty(triggersToCreate)) {
			newTriggerIds = API.Trigger(idBean, this.referencer.getExecutor()).create(triggersToCreate);
			triggerids = newTriggerIds.get("triggerids");
			for (int tnum = 0; tnum < triggerids.length; tnum++) {
				Long triggerId = triggerids[tnum];
				Map trigger = triggersToCreate.get(tnum);
				this.referencer.addTriggerRef(Nest.value(trigger,"description").asString(), Nest.value(trigger,"expression").asString(), triggerId);
				Nest.value(newTriggers,triggerId).$(trigger);
			}
		}

		// if we have new triggers with dependencies and they were created, create their dependencies
		if (!empty(triggersToCreateDependencies) && isset(newTriggerIds)) {
			for (int tnum = 0; tnum < triggerids.length; tnum++) {
				Long triggerId = triggerids[tnum];
				CArray deps = array();
				for(Map dependency : triggersToCreateDependencies.get(tnum)) {
					Long depTriggerId = this.referencer.resolveTrigger(idBean, Nest.value(dependency,"name").asString(), Nest.value(dependency,"expression").asString());
					if (empty(depTriggerId)) {
						Map trigger = newTriggers.get(triggerId);
						throw new Exception(_s("Trigger \"%1$s\" depends on trigger \"%2$s\", which does not exist.", Nest.value(trigger,"description").$(), Nest.value(dependency,"name").$()));
					}
					deps.add(map("triggerid", depTriggerId));
				}

				if (!empty(deps)) {
					triggerDependencies.add(map(
						"triggerid", triggerId,
						"dependencies", deps
					));
				}
			}
		}

		if (Nest.value(options,"triggers","updateExisting").asBoolean() && !empty(triggersToUpdate)) {
			API.Trigger(idBean, this.referencer.getExecutor()).update(triggersToUpdate);
		}

		if (!empty(triggerDependencies)) {
			API.Trigger(idBean, this.referencer.getExecutor()).update(triggerDependencies);
		}

		// refresh triggers because template triggers can be inherited to host and used in maps
		this.referencer.refreshTriggers();
	}
	
	/**
	 * Import images.
	 * @throws Exception
	 */
	protected void processImages(IIdentityBean idBean) {
		CArray allImages = getFormattedImages();
		if (empty(allImages)) {
			return;
		}

		CArray imagesToUpdate = array();
		allImages = rda_toHash(allImages, "name");

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbImages = DBselect(this.referencer.getExecutor(),
				"SELECT i.imageid,i.name FROM images i WHERE "+sqlParts.dual.dbConditionString("i.name", array_keys(allImages).valuesAsString()),
				sqlParts.getNamedParams());
		for(Map dbImage : dbImages) {
			Nest.value(dbImage,"image").$(Nest.value(allImages,dbImage.get("name"),"image").$());
			imagesToUpdate.add(dbImage);
			unset(allImages,dbImage.get("name"));
		}

		if (Nest.value(options,"images","createMissing").asBoolean()) {
			API.Image(idBean, this.referencer.getExecutor()).create(array_values(allImages));
		}

		if (Nest.value(options,"images","updateExisting").asBoolean()) {
			API.Image(idBean, this.referencer.getExecutor()).update(imagesToUpdate);
		}
	}
	
	/**
	 * Import maps.
	 * @throws Exception 
	 */
	protected void processMaps(IIdentityBean idBean) throws Exception {
		CArray maps = null;
		if (!empty(maps = getFormattedMaps())) {
			CHostImporter mapImporter = new CHostImporter(options, referencer);
			mapImporter.doImport(idBean, maps);
		}
	}

	/**
	 * Import screens.
	 * @throws Exception 
	 */
	protected void processScreens(IIdentityBean idBean) throws Exception {
		CArray screens = null;
		if (!empty(screens = getFormattedScreens())) {
			CScreenImporter screenImporter = new CScreenImporter(this.options, this.referencer);
			screenImporter.doImport(idBean, screens);
		}
	}

	/**
	 * Import template screens.
	 * @throws Exception 
	 */
	protected void processTemplateScreens(IIdentityBean idBean) throws Exception {
		CArray screens = null;
		if (!empty(screens = getFormattedTemplateScreens())) {
			CTemplateScreenImporter screenImporter = new CTemplateScreenImporter(this.options, this.referencer);
			screenImporter.doImport(idBean, screens);
		}
	}
	
	/**
	 * Method for creating an import formatter for the specified import version.
	 * @param String version
	 * @return CImportFormatter
	 * @throws InvalidArgumentException
	 */
	protected CImportFormatter getFormatter(String version) {
		if ("2.0".equals(version)) {
			return new C20ImportFormatter();
		}
		throw new IllegalArgumentException("Unknown import version.");
	}

	/**
	 * Get configuration import version.
	 * @return String
	 */
	protected String getImportVersion() {
		if (isset(Nest.value(this.data,"zabbix_export","version").$())) {
			return Nest.value(this.data,"zabbix_export","version").asString();
		}		
		if (isset(Nest.value(this.data,"iradar_export","version").$())) {
			return Nest.value(this.data,"iradar_export","version").asString();
		}
		return "0";
	}
	
	/**
	 * Get formatted groups, if either \"createMissing\" groups option is true.
	 * @return CArray
	 */
	protected CArray getFormattedGroups() {
		if (!isset(this.formattedData,"groups")) {
			Nest.value(this.formattedData,"groups").$(array());
			if (Nest.value(this.options,"groups","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"groups").$(this.formatter.getGroups());
			}
		}
		return Nest.value(this.formattedData,"groups").asCArray();
	}

	/**
	 * Get formatted templates, if either \"createMissing\" or \"updateExisting\" templates option is true.
	 * @return CArray
	 */
	protected CArray getFormattedTemplates() {
		if (!isset(this.formattedData,"templates")) {
			Nest.value(this.formattedData,"templates").$(array());
			if (Nest.value(this.options,"templates","updateExisting").asBoolean() || Nest.value(this.options,"templates","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"templates").$(this.formatter.getTemplates());
			}
		}
		return Nest.value(this.formattedData,"templates").asCArray();
	}

	/**
	 * Get formatted hosts, if either \"createMissing\" or \"updateExisting\" hosts option is true.
	 * @return CArray
	 */
	protected CArray getFormattedHosts() {
		if (!isset(this.formattedData,"hosts")) {
			Nest.value(this.formattedData,"hosts").$(array());
			if (Nest.value(this.options,"hosts","updateExisting").asBoolean() || Nest.value(this.options,"hosts","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"hosts").$(this.formatter.getHosts());
			}
		}
		return Nest.value(this.formattedData,"hosts").asCArray();
	}

	/**
	 * Get formatted applications, if either \"createMissing\" or \"updateExisting\" applications option is true.
	 * @return CArray
	 */
	protected CArray getFormattedApplications() {
		if (!isset(this.formattedData,"applications")) {
			Nest.value(formattedData,"applications").$(array());
			if (Nest.value(this.options,"templates","updateExisting").asBoolean()
					|| Nest.value(this.options,"templates","createMissing").asBoolean()
					|| Nest.value(this.options,"hosts","updateExisting").asBoolean()
					|| Nest.value(this.options,"hosts","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"applications").$(this.formatter.getApplications());
			}
		}
		return Nest.value(this.formattedData,"applications").asCArray();
	}

	/**
	 * Get formatted items, if either \"createMissing\" or \"updateExisting\" items option is true.
	 * @return CArray
	 */
	protected CArray getFormattedItems() {
		if (!isset(this.formattedData,"items")) {
			Nest.value(this.formattedData,"items").$(array());
			if (Nest.value(this.options,"items","updateExisting").asBoolean() || Nest.value(this.options,"items","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"items").$(this.formatter.getItems());
			}
		}
		return Nest.value(this.formattedData,"items").asCArray();
	}

	/**
	 * Get formatted discovery rules, if either \"createMissing\" or \"updateExisting\" discovery rules option is true.
	 * @return CArray
	 */
	protected CArray getFormattedDiscoveryRules() {
		if (!isset(this.formattedData,"discoveryRules")) {
			Nest.value(this.formattedData,"discoveryRules").$(array());
			if (Nest.value(this.options,"discoveryRules","updateExisting").asBoolean() || Nest.value(this.options,"discoveryRules","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"discoveryRules").$(this.formatter.getDiscoveryRules());
			}
		}
		return Nest.value(this.formattedData,"discoveryRules").asCArray();
	}

	/**
	 * Get formatted triggers, if either \"createMissing\" or \"updateExisting\" triggers option is true.
	 * @return CArray
	 */
	protected CArray getFormattedTriggers() {
		if (!isset(this.formattedData,"triggers")) {
			Nest.value(this.formattedData,"triggers").$(array());
			if (Nest.value(this.options,"triggers","updateExisting").asBoolean() || Nest.value(this.options,"triggers","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"triggers").$(this.formatter.getTriggers());
			}
		}
		return Nest.value(this.formattedData,"triggers").asCArray();
	}

	/**
	 * Get formatted graphs, if either \"createMissing\" or \"updateExisting\" graphs option is true.
	 * @return CArray
	 */
	protected CArray getFormattedGraphs() {
		if (!isset(this.formattedData,"graphs")) {
			Nest.value(this.formattedData,"graphs").$(array());
			if (Nest.value(this.options,"graphs","updateExisting").asBoolean() || Nest.value(this.options,"graphs","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"graphs").$(this.formatter.getGraphs());
			}
		}
		return Nest.value(this.formattedData,"graphs").asCArray();
	}

	/**
	 * Get formatted images, if user is super admin and either \"createMissing\" or \"updateExisting\" images option is true.
	 * @return CArray
	 */
	protected CArray getFormattedImages() {
		if (!isset(this.formattedData,"images")) {
			Nest.value(this.formattedData,"images").$(array());
			if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN
					&& Nest.value(this.options,"images","updateExisting").asBoolean() || Nest.value(this.options,"images","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"images").$(this.formatter.getImages());
			}
		}
		return Nest.value(this.formattedData,"images").asCArray();
	}

	/**
	 * Get formatted maps, if either \"createMissing\" or \"updateExisting\" maps option is true.
	 * @return CArray
	 */
	protected CArray getFormattedMaps() {
		if (!isset(this.formattedData,"maps")) {
			Nest.value(this.formattedData,"maps").$(array());
			if (Nest.value(this.options,"maps","updateExisting").asBoolean() || Nest.value(this.options,"maps","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"maps").$(this.formatter.getMaps());
			}
		}
		return Nest.value(this.formattedData,"maps").asCArray();
	}
	
	/**
	 * Get formatted screens, if either \"createMissing\" or \"updateExisting\" screens option is true.
	 * @return CArray
	 */
	protected CArray getFormattedScreens() {
		if (!isset(this.formattedData,"screens")) {
			Nest.value(this.formattedData,"screens").$(array());
			if (Nest.value(this.options,"screens","updateExisting").asBoolean() || Nest.value(this.options,"screens","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"screens").$(this.formatter.getScreens());
			}
		}
		return Nest.value(this.formattedData,"screens").asCArray();
	}

	/**
	 * Get formatted template screens, if either \"createMissing\" or \"updateExisting\" template screens option is true.
	 * @return CArray
	 */
	protected CArray getFormattedTemplateScreens() {
		if (!isset(this.formattedData,"templateScreens")) {
			Nest.value(this.formattedData,"templateScreens").$(array());
			if (Nest.value(this.options,"templateScreens","updateExisting").asBoolean() || Nest.value(this.options,"templateScreens","createMissing").asBoolean()) {
				Nest.value(this.formattedData,"templateScreens").$(this.formatter.getTemplateScreens());
			}
		}
		return Nest.value(this.formattedData,"templateScreens").asCArray();
	}
}
