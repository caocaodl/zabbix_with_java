package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class CSeverity extends CTag {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private IIdentityBean idBean;

	/**
	 * @param string options["id"]
	 * @param string options["name"]
	 * @param int    options["value"]
	 */
	public CSeverity(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super("div", "yes");
		this.idBean = idBean;
		attr("id", isset(options,"id") ? Nest.value(options,"id").$() : rda_formatDomId(Nest.value(options,"name").asString()));
		addClass("jqueryinputset control-severity");
		
		CArray items = array();
		StringBuilder jsIds = new StringBuilder();
		StringBuilder jsLabels = new StringBuilder();
		
		for (Entry<Object, String> e : getSeverityCaption(idBean, executor).entrySet()) {
		    int severity = Nest.as(e.getKey()).asInteger();
		    String caption = e.getValue();
			items.add(new CRadioButton(
				Nest.value(options,"name").asString(),
				severity,
				null,
				Nest.value(options,"name").asString()+"_"+severity,
				(Nest.value(options,"value").asInteger() == severity)
			));

			String css = getSeverityStyle(severity);

			CLabel label = new CLabel(caption, Nest.value(options,"name").asString()+"_"+severity, Nest.value(options,"name").asString()+"_label_"+severity);
			label.attr("data-severity", severity);
			label.attr("data-severity-style", css);

			if (Nest.value(options,"value").asInteger() == severity) {
				label.attr("aria-pressed", "true");
				label.addClass(css);
			} else {
				label.attr("aria-pressed", "false");
			}

			items.add(label);

			jsIds.append(", #"+Nest.value(options,"name").$()+"_"+severity);
			jsLabels.append(", #"+Nest.value(options,"name").$()+"_label_"+severity);
		}

		if (jsIds.length()>0) {
			jsIds.delete(0, 2);
			jsLabels.delete(0, 2);
		}

		addItem(items);
		
		String js = getJsTemplate("javascript_for_cseverity");
		String sjsLabels = jsLabels.toString();
		String sjsIds = jsIds.toString();
		String id = Nest.as(getAttribute("id")).asString();
		insert_js(String.format(js, sjsLabels, sjsLabels, id, sjsIds, id), true);
	}

}
