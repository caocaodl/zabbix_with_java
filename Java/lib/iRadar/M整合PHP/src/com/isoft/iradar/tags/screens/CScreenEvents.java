package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.EventsUtil.getLastEvents;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.TriggersUtil.addTriggerValueStyle;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenEvents extends CScreenBase {

	public CScreenEvents(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenEvents(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		CParamGet options = new CParamGet();
		options.put("monitored", true);
		options.put("value", array(TRIGGER_VALUE_TRUE, TRIGGER_VALUE_FALSE));
		options.put("triggerLimit", Nest.value(screenitem,"elements").$());
		options.put("eventLimit", Nest.value(screenitem,"elements").$());

		CTableInfo item = new CTableInfo(_("No events found."));
		item.setHeader(array(
			_("Time"),
			_("Host"),
			_("Description"),
			_("Value"),
			_("Severity")
		));

		CArray<Map> events = getLastEvents(idBean, executor, options);
		for(Map event : events) {
			Map trigger = Nest.value(event,"trigger").asCArray();
			Map host = Nest.value(event,"host").asCArray();

			CSpan statusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));

			// add colors and blinking to span depending on configuration and trigger parameters
			addTriggerValueStyle(this.idBean, this.executor, statusSpan, Nest.value(event,"value").asInteger(), Nest.value(event,"clock").asInteger(), Nest.value(event,"acknowledged").asBoolean());

			item.addRow(array(
				rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong()),
				Nest.value(host,"name").$(),
				new CLink(
					Nest.value(trigger,"description").$(),
					"tr_events.action?triggerid="+Nest.value(event,"objectid").$()+"&eventid="+Nest.value(event,"eventid").asString()
				),
				statusSpan,
				getSeverityCell(this.idBean, this.executor, Nest.value(trigger,"priority").asInteger())
			));
		}

		return getOutput(item);
	}

}
