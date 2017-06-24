package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_TEXT_RETURN_JSON;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.utils.CJs.encodeJson;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMultiSelect extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CMultiSelect() {
		this(array());
	}
	
	public CMultiSelect(CArray options) {
		super("div", "yes");
		addClass("multiselect");
		attr("id", rda_formatDomId(Nest.value(options,"name").asString()));
		
		// url
		Curl url = new Curl("jsrpc.action");
		url.setArgument("type", PAGE_TYPE_TEXT_RETURN_JSON);
		url.setArgument("method", "multiselect.get");
		url.setArgument("objectName", Nest.value(options,"objectName").asString());

		CArray<Object> objectOptions = Nest.value(options,"objectOptions").asCArray();
		if (!empty(objectOptions)) {
            for (Entry<Object, Object> e : objectOptions.entrySet()) {
                Object optionName = e.getKey();
                Object optionvalue = e.getValue();
				url.setArgument(Nest.as(optionName).asString(), optionvalue);
			}
		}
		
		CArray params = map(
			"id", getAttribute("id"),
			"url", url.getUrl(),
			"name", Nest.value(options,"name").$(),
			"labels", map(
				"No matches found", _("No matches found"),
				"More matches found...", _("More matches found..."),
				"type here to search", _("type here to search"),
				"new", _("new"),
				"Select", _("Select")
			),
			"data", (empty(Nest.value(options,"data").$()) ? array() : rda_cleanHashes(Nest.value(options,"data").asCArray())).toArray(),
			"ignored", isset(Nest.value(options,"ignored").$()) ? Nest.value(options,"ignored").$() : null,
			"defaultValue", isset(Nest.value(options,"defaultValue").$()) ? Nest.value(options,"defaultValue").$() : null,
			"disabled", isset(Nest.value(options,"disabled").$()) ? Nest.value(options,"disabled").asBoolean() : false,
			"selectedLimit", isset(Nest.value(options,"selectedLimit").$()) ? Nest.value(options,"selectedLimit").$() : null,
			"addNew", isset(Nest.value(options,"addNew").$()) ? Nest.value(options,"addNew").asBoolean() : false,
			"popup", map(
				"parameters", isset(Nest.value(options,"popup","parameters").$()) ? Nest.value(options,"popup","parameters").$() : null,
				"width", isset(Nest.value(options,"popup","width").$()) ? Nest.value(options,"popup","width").$() : null,
				"height", isset(Nest.value(options,"popup","height").$()) ? Nest.value(options,"popup","height").$() : null,
				"buttonClass", isset(Nest.value(options,"popup","buttonClass").$()) ? Nest.value(options,"popup","buttonClass").$() : null
			)
		);
		
		rda_add_post_js("jQuery(\"#"+getAttribute("id")+"\").multiSelect("+encodeJson(params)+")");
	}
}
