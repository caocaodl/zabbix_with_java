package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralIconmapList extends CViewSegment {

	@Override
	public CTableInfo doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo iconMapTable = new CTableInfo(_("No icon maps found."));
		iconMapTable.setHeader(array(
			_("Name"),
			_("Icon map")
		));
		iconMapTable.addItem(BR());

		for(Map iconMap : (CArray<Map>)Nest.value(data,"iconmaps").asCArray()) {
			CArray row = array();
			for(Map mapping : (CArray<Map>)Nest.value(iconMap,"mappings").asCArray()) {
				row.add(Nest.value(data,"inventoryList",mapping.get("inventory_link")).asString()+NAME_DELIMITER+
						Nest.value(mapping,"expression").asString()+SPACE+RARR+SPACE+Nest.value(data,"iconList",mapping.get("iconid")).asString());
				row.add(BR());
			}

			iconMapTable.addRow(array(
				new CLink(Nest.value(iconMap,"name").$(), "adm.iconmapping.action?form=update&iconmapid="+Nest.value(iconMap,"iconmapid").$()),
				row
			));
		}

		return iconMapTable;
	}

}
