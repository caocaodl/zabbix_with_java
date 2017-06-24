package com.isoft.iradar.validators.event;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_AUTOREGHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_ITEM;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_LLDRULE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.EventsUtil.eventObject;
import static com.isoft.iradar.inc.EventsUtil.eventSource;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CEventSourceObjectValidator extends CValidator<Map> {
	
	/**
	 * Supported source-object pairs.
	 *
	 * @var array
	 */
	public CArray<CArray<Integer>> pairs = map(
		EVENT_SOURCE_TRIGGERS, map(
			EVENT_OBJECT_TRIGGER, 1
		),
		EVENT_SOURCE_DISCOVERY, map(
			EVENT_OBJECT_DHOST, 1,
			EVENT_OBJECT_DSERVICE, 1
		),
		EVENT_SOURCE_AUTO_REGISTRATION, map(
			EVENT_OBJECT_AUTOREGHOST, 1
		),
		EVENT_SOURCE_INTERNAL, map(
			EVENT_OBJECT_TRIGGER, 1,
			EVENT_OBJECT_ITEM, 1,
			EVENT_OBJECT_LLDRULE, 1
		)
	);

	/**
	 * Checks if the given source-object pair is valid.
	 *
	 * @param value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, Map value) {
		CArray<Integer> objects = this.pairs.get(Nest.value(value,"source").asInteger());
		if (!isset(objects,Nest.value(value,"object").$())) {
			StringBuilder supportedObjects = new StringBuilder();
			for(Integer object : objects) {
				supportedObjects.append(object);
				supportedObjects.append(" - ");
				supportedObjects.append(eventObject(object));
			}

			setError(
				_s("Incorrect event object \"%1$s\" (%2$s) for event source \"%3$s\" (%4$s), only the following objects are supported: %5$s.",
					Nest.value(value,"object").$(),
					eventObject(Nest.value(value,"object").asInteger()),
					Nest.value(value,"source").$(),
					eventSource(Nest.value(value,"source").asInteger()),
					rtrim(supportedObjects.toString(), ", ")
				)
			);
			return false;
		}
		return true;
	}

}
