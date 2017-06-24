package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
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

public class CAdministrationGeneralValuemappingList extends CViewSegment {

	@Override
	public CTableInfo doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo valueMappingTable = new CTableInfo(_("No value maps found."));
		valueMappingTable.setHeader(array(
			_("Name"),
			_("Value map")
		));

		for(Map valuemap : (CArray<Map>)Nest.value(data,"valuemaps").asCArray()) {
			order_result(Nest.value(valuemap,"maps").asCArray(), "value");

			CArray mappings = array();
			for(Map map:(CArray<Map>)Nest.value(valuemap,"maps").asCArray()) {
				mappings.add(Nest.value(map,"value").$()+SPACE+RARR+SPACE+Nest.value(map,"newvalue").$());
				mappings.add(BR());
			}
			valueMappingTable.addRow(array(
				new CLink(Nest.value(valuemap,"name").$(), "adm.valuemapping.action?form=update&valuemapid="+Nest.value(valuemap,"valuemapid").$()),
				mappings
			));
		}

		return valueMappingTable;
	}

}
