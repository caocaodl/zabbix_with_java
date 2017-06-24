package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_MAP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_diff;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.validators.CColorValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class CMapElementDAO<P extends CParamGet> extends CCoreLongKeyDAO<P> {

	public CMapElementDAO(IIdentityBean idBean, SQLExecutor executor, String tableName,
			String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}
	
	protected CArray<Map> checkSelementInput(CArray<Map> selements, String method) {
		boolean update = ("updateSelements".equals(method));
		boolean delete = ("deleteSelements".equals(method));

		// permissions
		CArray selementDbFields = null;
		CArray<Map> dbSelements = null;
		if (update || delete) {
			selementDbFields  = map("selementid", null);
			dbSelements = fetchSelementsByIds(rda_objectValues(selements, "selementid").valuesAsString());
			if (update) {
				selements = extendFromObjects(selements, dbSelements, array("elementtype", "elementid"));
			}
		} else {
			selementDbFields = map(
				"sysmapid", null,
				"elementid", null,
				"elementtype", null,
				"iconid_off", null,
				"urls", array()
			);
		}

		for(Map selement : selements) {
			if (!check_db_fields(selementDbFields, selement)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for element."));
			}

			Map dbSelement = null;
			if (update || delete) {
				if (!isset(dbSelements,selement.get("selementid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
				dbSelement  = array_merge(Nest.value(dbSelements,selement.get("selementid")).asCArray(), CArray.valueOf(selement));
			} else {
				dbSelement = selement;
			}

			if (isset(selement,"iconid_off") && Nest.value(selement,"iconid_off").asInteger() == 0) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No icon for map element \"%s\".", Nest.value(selement,"label").$()));
			}

			if (checkCircleSelementsLink(Nest.value(dbSelement,"sysmapid").asString(), Nest.value(dbSelement,"elementid").asString(), Nest.value(dbSelement,"elementtype").asInteger())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Circular link cannot be created for map element \"%s\".", Nest.value(dbSelement,"label").$()));
			}
		}

		// check permissions to used objects
		checkSelementPermissions(selements);

		return (update || delete) ? dbSelements : null;
	}
	
	/**
	 * Checks that the user has write permissions to objects used in the map elements.
	 *
	 * @throws APIException if the user has no permissions to at least one of the objects
	 *
	 * @param array _selements
	 */
	protected void checkSelementPermissions(CArray<Map> selements) {
		if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
			return;
		}

		CArray hostIds = array();
		CArray groupIds = array();
		CArray triggerIds = array();
		CArray mapIds = array();
		for(Map selement : selements) {
			switch (Nest.value(selement,"elementtype").asInteger()) {
				case SYSMAP_ELEMENT_TYPE_HOST:
					Nest.value(hostIds,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
					break;
				case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
					Nest.value(groupIds,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
					break;
				case SYSMAP_ELEMENT_TYPE_TRIGGER:
					Nest.value(triggerIds,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
					break;
				case SYSMAP_ELEMENT_TYPE_MAP:
					Nest.value(mapIds,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
					break;
			}
		}

		if ((!empty(hostIds) && !API.Host(this.idBean, this.getSqlExecutor()).isWritable(hostIds.valuesAsLong()))
				|| (!empty(groupIds) && !API.HostGroup(this.idBean, this.getSqlExecutor()).isWritable(groupIds.valuesAsLong()))
				|| (!empty(triggerIds) && !API.Trigger(this.idBean, this.getSqlExecutor()).isWritable(triggerIds.valuesAsLong()))
				|| (!empty(mapIds) && !API.Map(this.idBean, this.getSqlExecutor()).isWritable(mapIds.valuesAsLong()))) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Returns a hash of map elements with the given IDs. The result also includes URL assigned to the elements.
	 *
	 * @param array _selementIds
	 *
	 * @return array
	 */
	protected CArray<Map> fetchSelementsByIds(String... selementIds) {
		CParamGet options = new CParamGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setFilter("selementid", selementIds);
		options.setPreserveKeys(true);
		CArray<Map> selements = select("sysmaps_elements", options);

		if (!empty(selements)) {
			for(Map selement : selements) {
				Nest.value(selement,"urls").$(array());
			}
			options = new CParamGet();
			options.setOutput(API_OUTPUT_EXTEND);
			options.setFilter("selementid", selementIds);
			CArray<Map> selementUrls = select("sysmap_element_url", options);
			for(Map selementUrl : selementUrls) {
				Nest.value(selements,selementUrl.get("selementid"),"urls").asCArray().add(selementUrl);
			}
		}
		return selements;
	}
	
	protected boolean checkLinkInput(CArray<Map> links, String method) {
		boolean update = ("updateLink".equals(method));
		boolean delete = ("deleteLink".equals(method));

		// permissions
		CArray linkDbFields = null;
		CArray<Map> dbLinks = null;
		if (update || delete) {
			linkDbFields = map("linkid", null);
			CParamGet params = new CParamGet();
			params.setFilter("selementid", rda_objectValues(links, "linkid").valuesAsString());
			params.setOutput(new String[]{"linkid"});
			params.setPreserveKeys(true);
			dbLinks = select("sysmap_element_url", params);
		} else {
			linkDbFields = map(
				"sysmapid", null,
				"selementid1", null,
				"selementid2", null
			);
		}

		CColorValidator colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map link : links) {
			if (!check_db_fields(linkDbFields, link)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for map link."));
			}

			if (isset(link,"color") && !colorValidator.validate(this.idBean, Nest.value(link,"color").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, colorValidator.getError());
			}

			if (update || delete) {
				if (!isset(dbLinks,link.get("linkid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
			}
		}

		return true;
	}
	
	public boolean checkCircleSelementsLink(String sysmapId, String elementId, int elementType) {
		if (elementType != SYSMAP_ELEMENT_TYPE_MAP) {
			return false;
		}

		if (bccomp(sysmapId, elementId) == 0) {
			return true;
		}

		Map params = new HashMap();
		params.put("sysmapid", elementId);
		CArray<Map> dbElements = DBselect(getSqlExecutor(),
			"SELECT se.elementid,se.elementtype"+
			" FROM sysmaps_elements se"+
			" WHERE se.sysmapid=#{sysmapid}"+
				" AND se.elementtype="+SYSMAP_ELEMENT_TYPE_MAP,
			params
		);
		for (Map element : dbElements) {
			if (checkCircleSelementsLink(sysmapId, Nest.value(element,"elementid").asString(), Nest.value(element,"elementtype").asInteger())) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Add element to sysmap.
	 *
	 * @param array _elements[0,...]["sysmapid"]
	 * @param array _elements[0,...]["elementid"]
	 * @param array _elements[0,...]["elementtype"]
	 * @param array _elements[0,...]["label"]
	 * @param array _elements[0,...]["x"]
	 * @param array _elements[0,...]["y"]
	 * @param array _elements[0,...]["iconid_off"]
	 * @param array _elements[0,...]["iconid_on"]
	 * @param array _elements[0,...]["iconid_disabled"]
	 * @param array _elements[0,...]["urls"][0,...]
	 * @param array _elements[0,...]["label_location"]
	 * @return 
	 *
	 * @return array
	 */
	protected CArray<Long[]> createSelements(CArray<Map> selements) {
		checkSelementInput(selements, "createSelements");
		CArray<Long> selementIds = insert("sysmaps_elements", selements);
		CArray insertUrls = array();
		for (Entry<Object, Long> e : selementIds.entrySet()) {
            Object key = e.getKey();
            Long selementId = e.getValue();
			for(Map url : (CArray<Map>)Nest.value(selements,key,"urls").asCArray()) {
				Nest.value(url,"selementid").$(selementId);
				insertUrls.add(url);
			}
		}
		insert("sysmap_element_url", insertUrls);
		return map("selementids", selementIds.valuesAsLong());
	}
	
	/**
	 * Update element to sysmap.
	 *
	 * @param array _elements[0,...]["selementid"]
	 * @param array _elements[0,...]["sysmapid"]
	 * @param array _elements[0,...]["elementid"]
	 * @param array _elements[0,...]["elementtype"]
	 * @param array _elements[0,...]["label"]
	 * @param array _elements[0,...]["x"]
	 * @param array _elements[0,...]["y"]
	 * @param array _elements[0,...]["iconid_off"]
	 * @param array _elements[0,...]["iconid_on"]
	 * @param array _elements[0,...]["iconid_disabled"]
	 * @param array _elements[0,...]["url"]
	 * @param array _elements[0,...]["label_location"]
	 * @return 
	 */
	protected CArray<Long> updateSelements(CArray<Map> selements) {
		CArray selementIds = array();

		CArray<Map> dbSelements = checkSelementInput(selements, "updateSelements");

		CArray<Map> update = array();
		CArray urlsToDelete = array();
		CArray<Map> urlsToUpdate = array();
		CArray<Map> urlsToAdd = array();
		for(Map selement : selements) {
			update.add(map(
				"values", selement,
				"where", map("selementid", Nest.value(selement,"selementid").asLong())
			));
			selementIds.add(Nest.value(selement,"selementid").$());

			if (!isset(selement,"urls")) {
				continue;
			}

			CArray diffUrls = rda_array_diff(Nest.value(selement,"urls").asCArray(), Nest.value(dbSelements,selement.get("selementid"),"urls").asCArray(), "name");

			// add
			for(Map newUrl : (CArray<Map>)Nest.value(diffUrls,"first").asCArray()) {
				Nest.value(newUrl,"selementid").$(Nest.value(selement,"selementid").$());
				urlsToAdd.add(newUrl);
			}

			// update url
			for(Map updUrl : (CArray<Map>)Nest.value(diffUrls,"both").asCArray()) {
				urlsToUpdate.add(map(
					"values", updUrl,
					"where", map(
						"selementid", Nest.value(selement,"selementid").$(),
						"name",Nest.value(updUrl,"name").$()
					)
				));
			}

			// delete url
			urlsToDelete = array_merge(urlsToDelete, rda_objectValues(Nest.value(diffUrls,"second").$(), "sysmapelementurlid"));
		}

		update("sysmaps_elements", update);

		if (!empty(urlsToDelete)) {
			delete("sysmap_element_url", (Map)map("sysmapelementurlid", urlsToDelete));
		}

		if (!empty(urlsToUpdate)) {
			update("sysmap_element_url", urlsToUpdate);
		}

		if (!empty(urlsToAdd)) {
			insert("sysmap_element_url", urlsToAdd);
		}

		return map("selementids", selementIds.valuesAsLong());
	}
	
	/**
	 * Delete element from map.
	 *
	 * @param array _selements							multidimensional array with selement objects
	 * @param array _selements[0, ...]["selementid"]	selementid to delete
	 */
	protected CArray<Long> deleteSelements(CArray<Map> selements) {
		CArray<Long> selementIds = rda_objectValues(selements, "selementid");
		checkSelementInput(selements, "deleteSelements");
		delete("sysmaps_elements", (Map)map("selementid", selementIds.valuesAsLong()));
		return selementIds;
	}
	
	/**
	 * Create link.
	 *
	 * @param array _links
	 * @param array _links[0,...]["sysmapid"]
	 * @param array _links[0,...]["selementid1"]
	 * @param array _links[0,...]["selementid2"]
	 * @param array _links[0,...]["drawtype"]
	 * @param array _links[0,...]["color"]
	 *
	 * @return array
	 */
	protected CArray<Long[]> createLinks(CArray<Map> links) {
		checkLinkInput(links, "createLinks");
		CArray<Long> linkIds = insert("sysmaps_links", links);
		return map("linkids", linkIds.valuesAsLong());
	}
	
	protected CArray<Long[]> updateLinks(CArray<Map> links) {
		checkLinkInput(links, "updateLinks");
		CArray<Map> udpateLinks = array();
		for(Map link : links) {
			udpateLinks.add(map("values", link, "where" , map("linkid", Nest.value(link,"linkid").$())));
		}
		update("sysmaps_links", udpateLinks);
		return map("linkids", rda_objectValues(links, "linkid").valuesAsLong());
	}
	
	/**
	 * Delete Link from map.
	 *
	 * @param array _links						multidimensional array with link objects
	 * @param array _links[0, ...]["linkid"]	link ID to delete
	 *
	 * @return array
	 */
	protected CArray<Long> deleteLinks(CArray<Map> links) {
		CArray linkIds = rda_objectValues(links, "linkid");
		checkLinkInput(links, "deleteLinks");
		delete("sysmaps_links", (Map)map("linkid", linkIds.valuesAsLong()));
		return map("linkids", linkIds.valuesAsLong());
	}
	
	/**
	 * Add link trigger to link (sysmap).
	 *
	 * @param array _links[0,...]["linkid"]
	 * @param array _links[0,...]["triggerid"]
	 * @param array _links[0,...]["drawtype"]
	 * @param array _links[0,...]["color"]
	 */
	protected CArray<Long> createLinkTriggers(CArray<Map> linkTriggers) {
		validateCreateLinkTriggers(linkTriggers);
		CArray<Long> linkTriggerIds = insert("sysmaps_link_triggers", linkTriggers);
		return map("linktriggerids", linkTriggerIds.valuesAsLong());
	}

	protected void validateCreateLinkTriggers(CArray<Map> linkTriggers) {
		CArray linkTriggerDbFields = map(
			"linkid", null,
			"triggerid", null
		);

		CColorValidator colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map linkTrigger : linkTriggers) {
			if (!check_db_fields(linkTriggerDbFields, linkTrigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for linktrigger."));
			}

			if (isset(linkTrigger,"color") && !colorValidator.validate(this.idBean, Nest.value(linkTrigger,"color").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, colorValidator.getError());
			}
		}
	}

	protected CArray<Long> updateLinkTriggers(CArray<Map> linkTriggers) {
		validateUpdateLinkTriggers(linkTriggers);

		CArray linkTriggerIds = rda_objectValues(linkTriggers, "linktriggerid");

		CArray<Map> updateLinkTriggers = array();
		for(Map linkTrigger : linkTriggers) {
			updateLinkTriggers.add(map(
				"values", linkTrigger,
				"where", map("linktriggerid", Nest.value(linkTrigger,"linktriggerid").$())
			));
		}

		update("sysmaps_link_triggers", updateLinkTriggers);

		return map("linktriggerids", linkTriggerIds.valuesAsLong());
	}

	protected void validateUpdateLinkTriggers(CArray<Map> _linkTriggers) {
		CArray _linkTriggerDbFields = map("linktriggerid", null);

		CColorValidator _colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map _linkTrigger : _linkTriggers) {
			if (!check_db_fields(_linkTriggerDbFields, _linkTrigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for linktrigger update."));
			}

			if (isset(_linkTrigger,"color") && !_colorValidator.validate(this.idBean, Nest.value(_linkTrigger,"color").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _colorValidator.getError());
			}
		}
	}

	protected CArray<Long> deleteLinkTriggers(CArray<Map> linkTriggers) {
		validateDeleteLinkTriggers(linkTriggers);
		CArray linkTriggerIds = rda_objectValues(linkTriggers, "linktriggerid");
		delete("sysmaps_link_triggers", (Map)map("linktriggerid", linkTriggerIds.valuesAsLong()));
		return map("linktriggerids", linkTriggerIds.valuesAsLong());
	}

	protected void validateDeleteLinkTriggers(CArray<Map> linkTriggers) {
		CArray linktriggerDbFields = map("linktriggerid", null);
		for(Map linkTrigger : linkTriggers) {
			if (!check_db_fields(linktriggerDbFields, linkTrigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for linktrigger delete."));
			}
		}
	}
}
