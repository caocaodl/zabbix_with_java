package com.isoft.iradar.imports.importers;

import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTemplateScreenImporter extends CAbstractScreenImporter {

	public CTemplateScreenImporter(CArray options, CImportReferencer referencer) {
		super(options, referencer);
	}

	/**
	 * Import template screens.
	 * @param CArray<Map> allScreens
	 * @return void
	 */
	@Override
	public void doImport(IIdentityBean idBean, CArray<Map> allScreens) throws Exception {
		CArray<Map> screensToCreate = array();
		CArray<Map> screensToUpdate = array();
		for (Entry<Object, Map> e : allScreens.entrySet()) {
		    String template = Nest.as(e.getKey()).asString();
		    CArray<Map> screens = (CArray)e.getValue();
			// TODO: select all at once out of loop
		    SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbScreens = DBselect(this.referencer.getExecutor(),
					"SELECT s.screenid,s.name FROM screens s WHERE"+
					" s.templateid="+sqlParts.marshalParam(this.referencer.resolveTemplate(idBean, template))+
					" AND "+sqlParts.dual.dbConditionString("s.name", array_keys(screens).valuesAsString()),
					sqlParts.getNamedParams());
			for(Map dbScreen : dbScreens) {
				Nest.value(screens,dbScreen.get("name"),"screenid").$(Nest.value(dbScreen,"screenid").asLong());
			}

			for(Map screen : screens) {
				screen = resolveScreenReferences(idBean, screen);
				if (isset(screen, "screenid")) {
					screensToUpdate.add(screen);
				} else {
					Nest.value(screen,"templateid").$(this.referencer.resolveTemplate(idBean, template));
					screensToCreate.add(screen);
				}
			}
		}

		if (!empty(Nest.value(options,"templateScreens","createMissing").$()) && !empty(screensToCreate)) {
			API.TemplateScreen(idBean, this.referencer.getExecutor()).create(screensToCreate);
		}
		if (!empty(Nest.value(options,"templateScreens","updateExisting").$()) && !empty(screensToUpdate)) {
			API.TemplateScreen(idBean, this.referencer.getExecutor()).update(screensToUpdate);
		}
	}
}
