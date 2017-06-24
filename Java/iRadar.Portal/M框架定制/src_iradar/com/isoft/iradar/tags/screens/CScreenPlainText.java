package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sscanf;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SCREEN_DYNAMIC_ITEM;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_same_item_for_host;
import static com.isoft.iradar.inc.ValuemapUtil.applyValueMap;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHistoryGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenPlainText extends CScreenBase {

	public CScreenPlainText(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenPlainText(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		// if screen is defined in template, then "real_resourceid" is defined and should be used
		if (!empty(Nest.value(screenitem,"real_resourceid").$())) {
			Nest.value(screenitem,"resourceid").$(Nest.value(screenitem,"real_resourceid").$());
		}

		if (Nest.value(screenitem,"dynamic").asInteger() == SCREEN_DYNAMIC_ITEM && !empty(this.hostid)) {
			Long newitemid = get_same_item_for_host(executor,Nest.value(screenitem,"resourceid").asLong(), hostid);
			Nest.value(screenitem,"resourceid").$(!empty(newitemid)? newitemid : 0);
		}

		if (Nest.value(screenitem,"resourceid").asLong() == 0L) {
			CTableInfo table = new CTableInfo(_("No values found."));
			table.setHeader(array(_("Timestamp"), _("Item")));
			return getOutput(table);
		}

		CArray<Map> items = resolveItemNames(this.idBean, this.executor, array(get_item_by_itemid(executor,Nest.value(screenitem,"resourceid").asString())));
		Map item = reset(items);

		String[] orderField = null;
		switch (Nest.value(item,"value_type").asInteger()) {
			case ITEM_VALUE_TYPE_TEXT:
			case ITEM_VALUE_TYPE_LOG:
				orderField = new String[]{"id"};
				break;
			case ITEM_VALUE_TYPE_FLOAT:
			case ITEM_VALUE_TYPE_UINT64:
			default:
				orderField = new String[]{"itemid", "clock"};
		}

		CArray<Map> hosts = get_host_by_itemid(this.idBean, executor, new String[]{Nest.value(screenitem,"resourceid").asString()});
		Map host = reset(hosts);

		CTableInfo table = new CTableInfo(_("No values found."));
		table.setHeader(array(_("Timestamp"), Nest.value(host,"name").asString()+NAME_DELIMITER+Nest.value(item,"name_expanded").asString()));

		long stime = rdaDateToTime(Nest.value(timeline,"stime").asString());

		CHistoryGet hoptions = new CHistoryGet();
		hoptions.setHistory(Nest.value(item,"value_type").asInteger());
		hoptions.setItemIds(Nest.value(screenitem,"resourceid").asLong());
		hoptions.setOutput(API_OUTPUT_EXTEND);
		hoptions.setSortorder(RDA_SORT_DOWN,RDA_SORT_DOWN);
		hoptions.setSortfield(orderField);
		hoptions.setLimit(Nest.value(screenitem,"elements").asInteger());
		hoptions.setTimeFrom(stime);
		hoptions.setTimeTill(stime + Nest.value(timeline,"period").asLong());
		CArray<Map> histories = API.History(this.idBean, this.executor).get(hoptions);
		Object value = null;
		for(Map history : histories) {
			switch (Nest.value(item,"value_type").asInteger()) {
				case ITEM_VALUE_TYPE_FLOAT:
					value = sscanf(Nest.value(history,"value").asString(), "%f").get(0);
					break;
				case ITEM_VALUE_TYPE_TEXT:
				case ITEM_VALUE_TYPE_STR:
				case ITEM_VALUE_TYPE_LOG:
					value = !empty(Nest.value(screenitem,"style").$()) ? new CJSScript(Nest.value(history,"value").$()) : Nest.value(history,"value").$();
					break;
				default:
					value = Nest.value(history,"value").$();
					break;
			}
			if (Nest.value(item,"valuemapid").asLong() > 0) {
				value = applyValueMap(this.idBean, this.executor, Nest.as(value).asString(), Nest.value(item,"valuemapid").asLong());
			}
			table.addRow(array(rda_date2str(_("d M Y H:i:s"), Nest.value(history,"clock").asLong()), new CCol(value, "pre")));
		}
		return getOutput(table);
	}

}
