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

public class ChartPrototypeAction extends Chart3Action {
	
	@Override
	protected void doInitPage() {
		page("file", "chartprototype.action");
		page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Long hostid = Nest.value(_REQUEST, "hostid").asLong();
		String key = Nest.value(_REQUEST, "key").asString();
		
		super.doCheckFields(executor);
		
		ItemsKey itemsKey = ItemsKey.valueOf(key);
		if(itemsKey!=null && hostid!=null) {
			CArray<Map> items = DataDriver.getItemIds(executor, hostid, itemsKey.getValue());
			
			if(Cphp.empty(items)) {
				access_deny(ACCESS_DENY_ABSENCE_ITEM);
			}
			
			ChartColorUtil.PrevColor prevColor = new ChartColorUtil.PrevColor();
			//{drawtype=0, flags=0, yaxisside=0, gitemid=2825, graphid=782, itemid=29232, color=C80000, calc_fnc=2, sortorder=0, type=0}
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
			}
			
			Nest.value(_REQUEST, "items").$(items);
		}
	}
}
