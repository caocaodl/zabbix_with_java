package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.TIME_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.TIME_TYPE_SERVER;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFlashClock;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenClock extends CScreenBase {

	public CScreenClock(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenClock(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		String error = null;
		Long timeOffset = null;
		Integer timeZone = null;
		String timeType = null;
		
		switch (Nest.value(screenitem,"style").asInteger()) {
			case TIME_TYPE_HOST:
				CItemGet ioptions = new CItemGet();
				ioptions.setItemIds(Nest.value(screenitem,"resourceid").asLong());
				ioptions.setSelectHosts(new String[]{"host"});
				ioptions.setOutput(new String[]{"itemid", "value_type"});
				CArray<Map> items = API.Item(this.idBean, this.executor).get(ioptions);
				Map item = reset(items);
				Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());

				CArray<CArray<Map>> lastValues = Manager.History(this.idBean, this.executor).getLast(array(item));
				if (!empty(lastValues)) {
					Map lastValue = reset(lastValues.get(item.get("itemid")));
					Nest.value(item,"lastvalue").$(Nest.value(lastValue,"value").$());
					Nest.value(item,"lastclock").$(Nest.value(lastValue,"clock").$());
				} else {
					Nest.value(item,"lastvalue").$("0");
					Nest.value(item,"lastclock").$("0");
				}

				timeType = Nest.value(host,"host").asString();
				CArray arr = new CArray();
				preg_match("([+-]{1})([\\d]{1,2}):([\\d]{1,2})", Nest.value(item,"lastvalue").asString(), arr);

				if (!empty(arr)) {
					timeZone = Nest.value(arr,2).asInteger() * SEC_PER_HOUR + Nest.value(arr,3).asInteger() * SEC_PER_MIN;
					if ("-".equals(Nest.value(arr,1).asInteger())) {
						timeZone = 0 - timeZone;
					}
				}

				long lastvalue;
				if ((lastvalue = strtotime(Nest.value(item,"lastvalue").asString()))>0) {
					long _diff = (time() - Nest.value(item,"lastclock").asLong());
					timeOffset = lastvalue + _diff;
				} else {
					error = _("NO DATA");
				}
				break;
			case TIME_TYPE_SERVER:
				error = null;
				timeType = _("SERVER");
				timeOffset = time();
				timeZone = Nest.as(date("Z")).asInteger();
				break;
			default:
				error = null;
				timeType = _("LOCAL");
				timeOffset = null;
				timeZone = null;
				break;
		}

		if (Nest.value(screenitem,"width").asInteger() > Nest.value(screenitem,"height").asInteger()) {
			Nest.value(screenitem,"width").$(Nest.value(screenitem,"height").$());
		}

		CFlashClock item = new CFlashClock(Nest.value(screenitem,"width").$(), Nest.value(screenitem,"height").$(), action);
		item.setTimeError(error);
		item.setTimeType(timeType);
		item.setTimeZone(Nest.as(timeZone).asString());
		item.setTimeOffset(timeOffset);

		CDiv flashclockOverDiv = new CDiv(null, "flashclock");
		flashclockOverDiv.setAttribute("style", "width: "+Nest.value(screenitem,"width").asString()+"px; height: "+Nest.value(screenitem,"height").asString()+"px;");

		return getOutput(array(item, flashclockOverDiv));
	}

}
