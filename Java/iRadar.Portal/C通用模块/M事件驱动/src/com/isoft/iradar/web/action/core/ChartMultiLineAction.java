package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.inc.Defines.ACCESS_DENY_ABSENCE_ITEM;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChartMultiLineAction extends Chart3Action {
	
	@Override
	protected void doInitPage() {
		page("file", "chartmultiline.action");
		page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Long hostid = Nest.value(_REQUEST, "hostid").asLong();
		CArray<String> keys = Nest.value(_REQUEST, "keys").asCArray();
		CArray<Map> itemsCA = CArray.array();
		
		super.doCheckFields(executor);
		
		ChartColorUtil.PrevColor prevColor = new ChartColorUtil.PrevColor();
		for(String key:keys){
			ItemsKey itemsKey = ItemsKey.valueOf(key);
			if(itemsKey!=null && hostid!=null) {
				CArray<Map> items = DataDriver.getItemId(executor, hostid, itemsKey.getValue());
				if(Cphp.empty(items))
					access_deny(ACCESS_DENY_ABSENCE_ITEM);
				for(Map item: items) {
					Nest.value(item, "drawtype").$(Defines.GRAPH_ITEM_DRAWTYPE_LINE);
					Nest.value(item, "flags").$(0);
					Nest.value(item, "yaxisside").$(Defines.GRAPH_YAXIS_SIDE_LEFT);
					Nest.value(item, "gitemid").$(null);
					Nest.value(item, "graphid").$(null);
					Nest.value(item, "color").$(ChartColorUtil.getNextColor(prevColor, 1));
					Nest.value(item, "calc_fnc").$(Defines.CALC_FNC_AVG);
					Nest.value(item, "sortorder").$(0);
					Nest.value(item, "type").$(Defines.GRAPH_ITEM_SIMPLE);
					itemsCA.add(item);
				}
			}
		}
		Nest.value(_REQUEST, "items").$(itemsCA);
	}
	
}
