package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_CUSTOM;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_IP;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_LABEL;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_NAME;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_NOTHING;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_STATUS;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_MAP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_TRIGGER;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CAreaMap;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MapsUtil {

	private MapsUtil() {
	}

	@CodeConfirmed("benne.2.2.4")
	public static void get_map_elements(IIdentityBean idBean, SQLExecutor executor, Map db_element, Map elements) {
		int elementtype = Nest.value(db_element,"elementtype").asInteger();
		switch (elementtype) {
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				if(!elements.containsKey("hosts_groups")){
					elements.put("hosts_groups", array());
				}
				Nest.value(elements, "hosts_groups").asCArray().add(Nest.value(db_element,"elementid").$());
				break;
			case SYSMAP_ELEMENT_TYPE_HOST:
				if(!elements.containsKey("hosts")){
					elements.put("hosts", array());
				}
				Nest.value(elements, "hosts").asCArray().add(Nest.value(db_element,"elementid").$());
				break;
			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				if(!elements.containsKey("triggers")){
					elements.put("triggers", array());
				}
				Nest.value(elements, "triggers").asCArray().add(Nest.value(db_element,"elementid").$());
				break;
			case SYSMAP_ELEMENT_TYPE_MAP:
				SqlBuilder sqlParts = new SqlBuilder();
				String sql = 	"SELECT DISTINCT se.elementtype,se.elementid"+
									" FROM sysmaps_elements se"+
									" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "sysmaps_elements", "se")+
									" AND se.sysmapid="+sqlParts.marshalParam(Nest.value(db_element,"elementid").$());
				CArray<Map> db_mapselements = DBUtil.DBselect(executor, sql, sqlParts.getNamedParams());
				if(db_mapselements!=null && !db_mapselements.isEmpty()){
					for(Map db_mapelement: db_mapselements){
						get_map_elements(idBean, executor, db_mapelement, elements);
					}
				}
				break;
		}
	}
	
	public static CArray<String> sysmapElementLabel() {
		CArray<String> _labels = map(
			MAP_LABEL_TYPE_LABEL, _("Label"),
			MAP_LABEL_TYPE_IP, _("IP address"),
			MAP_LABEL_TYPE_NAME, _("Element name"),
			MAP_LABEL_TYPE_STATUS, _("Status only"),
			MAP_LABEL_TYPE_NOTHING, _("Nothing"),
			MAP_LABEL_TYPE_CUSTOM, _("Custom label")
		);

		return _labels;
	}
	
	public static String sysmapElementLabel(int _label) {
		CArray<String> _labels = map(
			MAP_LABEL_TYPE_LABEL, _("Label"),
			MAP_LABEL_TYPE_IP, _("IP address"),
			MAP_LABEL_TYPE_NAME, _("Element name"),
			MAP_LABEL_TYPE_STATUS, _("Status only"),
			MAP_LABEL_TYPE_NOTHING, _("Nothing"),
			MAP_LABEL_TYPE_CUSTOM, _("Custom label")
		);

		if (isset(_labels,_label)) {
			return _labels.get(_label);
		} else {
			return null;
		}
	}

	@Deprecated
	public static CArray<Map> getParentMaps(Object $) {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public static CAreaMap getActionMapBySysmap(Object $, CArray<Object> map) {
		// TODO Auto-generated method stub
		return new CAreaMap();
	}

	@Deprecated
	public static CArray<String> sysmap_element_types() {
		// TODO Auto-generated method stub
		return null;
	}
}
