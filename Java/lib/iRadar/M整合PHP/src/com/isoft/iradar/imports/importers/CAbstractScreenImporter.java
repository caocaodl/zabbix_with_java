package com.isoft.iradar.imports.importers;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class CAbstractScreenImporter extends CImporter {

	public CAbstractScreenImporter(CArray options, CImportReferencer referencer) {
		super(options, referencer);
	}

	/**
	 * Prepare screen data for import.
	 * Each screen element has reference to resource it represents, reference structure may differ depending on type.
	 * Referenced database objects ids are stored to "resourceid" field of screen items.
	 *
	 * @todo: api requests probably should be done in CReferencer class
	 * @throws Exception if referenced object is not found in database
	 *
	 * @param Map screen
	 *
	 * @return Map
	 */
	protected Map resolveScreenReferences(IIdentityBean idBean, Map screen) throws Exception {
		if (!empty(Nest.value(screen,"screenitems").$())) {
			for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
				Map resource = Nest.value(screenItem,"resource").asCArray();
				if (empty(resource)) {
					Nest.value(screenItem,"resourceid").$(0L);
					continue;
				}

				switch (Nest.value(screenItem,"resourcetype").asInteger()) {
					case SCREEN_RESOURCE_HOSTS_INFO:
					case SCREEN_RESOURCE_TRIGGERS_INFO:
					case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
					case SCREEN_RESOURCE_DATA_OVERVIEW:
					case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
						Nest.value(screenItem,"resourceid").$(this.referencer.resolveGroup(idBean, Nest.value(resource,"name").asString()));
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw new Exception(_s("Cannot find group \"%1$s\" used in screen \"%2$s\".",
								Nest.value(resource,"name").$(), Nest.value(screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_HOST_TRIGGERS:
						Nest.value(screenItem,"resourceid").$(this.referencer.resolveHost(idBean, Nest.value(resource,"host").asString()));
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw new Exception(_s("Cannot find host \"%1$s\" used in screen \"%2$s\".",
								Nest.value(resource,"host").$(), Nest.value(screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_GRAPH:
						CArray<Map> dbGraphs = API.Graph(idBean, this.referencer.getExecutor()).getObjects(resource);
						if (empty(dbGraphs)) {
							throw new Exception(_s("Cannot find graph \"%1$s\" used in screen \"%2$s\".",
								Nest.value(resource,"name").$(), Nest.value(screen,"name").$()));
						}

						Map tmp = reset(dbGraphs);
						Nest.value(screenItem,"resourceid").$(Nest.value(tmp,"graphid").asLong());
						break;

					case SCREEN_RESOURCE_SIMPLE_GRAPH:
					case SCREEN_RESOURCE_PLAIN_TEXT:
						Long hostId = this.referencer.resolveHostOrTemplate(idBean, Nest.value(resource,"host").asString());
						Nest.value(screenItem,"resourceid").$(this.referencer.resolveItem(idBean, hostId, Nest.value(resource,"key").asString()));
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw new Exception(_s("Cannot find item \"%1$s\" used in screen \"%2$s\".",
									Nest.value(resource,"host").asString()+":"+Nest.value(resource,"key").asString(), Nest.value(screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_MAP:
						Nest.value(screenItem,"resourceid").$(this.referencer.resolveMap(idBean, Nest.value(resource,"name").asString()));
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw new Exception(_s("Cannot find map \"%1$s\" used in screen \"%2$s\".",
								Nest.value(resource,"name").$(), Nest.value(screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_SCREEN:
						Nest.value(screenItem,"resourceid").$(this.referencer.resolveScreen(Nest.value(resource,"name").asString()));
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw new Exception(_s("Cannot find screen \"%1$s\" used in screen \"%2$s\".",
								Nest.value(resource,"name").$(), Nest.value(screen,"name").$()));
						}
						break;

					default:
						Nest.value(screenItem,"resourceid").$(0L);
						break;
				}

			}
		}

		return screen;
	}
	
}
