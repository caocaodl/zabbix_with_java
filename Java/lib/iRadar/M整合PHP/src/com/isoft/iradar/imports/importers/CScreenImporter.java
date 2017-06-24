package com.isoft.iradar.imports.importers;

import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_search;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CScreenImporter extends CAbstractScreenImporter {

	public CScreenImporter(CArray options, CImportReferencer referencer) {
		super(options, referencer);
	}

	/**
	 * Import screens.
	 * @param CArray<Map> screens
	 * @return mixed
	 */
	@Override
	public void doImport(IIdentityBean idBean, CArray<Map> screens) throws Exception {
		screens = rda_toHash(screens, "name");

		checkCircularScreenReferences(screens);

		CArray<String> independentScreens = null;
		do {
			independentScreens = getIndependentScreens(screens);

			CArray<Map> screensToCreate = array();
			CArray<Map> screensToUpdate = array();
			for(String name : independentScreens) {
				Map screen = screens.get(name);
				unset(screens,name);

				screen = resolveScreenReferences(idBean, screen);

				Long screenId = null;
				if (!empty(screenId = this.referencer.resolveScreen(Nest.value(screen,"name").asString()))) {
					Nest.value(screen,"screenid").$(screenId);
					screensToUpdate.add(screen);
				} else {
					screensToCreate.add(screen);
				}
			}

			if (!empty(Nest.value(options,"screens","createMissing").$()) && !empty(screensToCreate)) {
				CArray<Long[]> newScreenIds = API.Screen(idBean, this.referencer.getExecutor()).create(screensToCreate);
				Long[] screenids = newScreenIds.get("screenids");
				for (Entry<Object, Map> e : screensToCreate.entrySet()) {
				    int num = Nest.as(e.getKey()).asInteger();
				    Map newScreen = e.getValue();
					Long screenidId = screenids[num];
					this.referencer.addScreenRef(Nest.value(newScreen,"name").asString(), screenidId);
				}
			}
			if (!empty(Nest.value(options,"screens","updateExisting").$()) && !empty(screensToUpdate)) {
				API.Screen(idBean, this.referencer.getExecutor()).update(screensToUpdate);
			}
		} while (!empty(independentScreens));

		// if there are screens left in $screens, then they have unresolved references
		for(Map screen : screens) {
			CArray<String> unresolvedReferences = array();
			for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
				if (Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_SCREEN
						&& empty(referencer.resolveScreen(Nest.value(screenItem,"resource","name").asString()))) {
					unresolvedReferences.add(Nest.value(screenItem,"resource","name").asString());
				}
			}
			unresolvedReferences = array_unique(unresolvedReferences);
			throw new Exception(_n("Cannot import screen \"%1$s\": subscreen \"%2$s\" does not exist.",
				"Cannot import screen \"%1$s\": subscreens \"%2$s\" do not exist.",
				Nest.value(screen,"name").$(), implode(", ", unresolvedReferences), count(unresolvedReferences)));
		}
	}
	
	/**
	 * Check if screens have circular references.
	 * Circular references can be only in screen items that represent another screen.
	 *
	 * @throws Exception
	 * @see checkCircularRecursive
	 * @param CArray<Map> screens
	 *
	 * @return void
	 */
	protected void checkCircularScreenReferences(CArray<Map> screens) throws Exception {
		for (Entry<Object, Map> e : screens.entrySet()) {
		    String screenName = Nest.as(e.getKey()).asString();
		    Map screen = e.getValue();
			if (empty(Nest.value(screen,"screenitems").$())) {
				continue;
			}

			CArray<String> circScreens = null;
			for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
				CArray<String> checked = (CArray)array(screenName);				
				if (!empty(circScreens = checkCircularRecursive(screenItem, screens, checked))) {
					throw new Exception(_s("Circular reference in screens: %1$s.", implode(" - ", circScreens)));
				}
			}
		}
	}

	/**
	 * Recursive function for searching for circular screen references.
	 * If circular reference exist it return array with screens names that fort it.
	 *
	 * @param Map screenItem screen to inspect on current recursive loop
	 * @param CArray<Map> screens    all screens where circular references should be searched
	 * @param CArray<String> checked    screen names that already were processed,
	 *                          should contain unique values if no circular references exist
	 *
	 * @return array|bool
	 */
	protected CArray<String> checkCircularRecursive(Map screenItem, CArray<Map> screens, CArray<String> checked) {
		// if element is not map element, recursive reference cannot happen
		if (Nest.value(screenItem,"resourcetype").asInteger() != SCREEN_RESOURCE_SCREEN) {
			return null;
		}

		String screenName = Nest.value(screenItem,"resource","name").asString();

		// if current screen name is already in list of checked screen names,
		// circular reference exists
		if (in_array(screenName, checked)) {
			// to have nice result containing only screens that have circular reference,
			// remove everything that was added before repeated screen name
			checked = array_slice(checked, (Integer)array_search(screenName, checked));
			// add repeated name to have nice loop like s1->s2->s3->s1
			checked.add(screenName);
			return checked;
		} else {
			checked.add(screenName);
		}

		// we need to find screen that current element reference to
		// and if it has screen items check all them recursively
		if (!empty(Nest.value(screens,screenName,"screenitems").$())) {
			for(Map sItem : (CArray<Map>)Nest.value(screens,screenName,"screenitems").asCArray()) {
				return checkCircularRecursive(sItem, screens, checked);
			}
		}

		return null;
	}

	/**
	 * Get screens that don't have screen items that reference not existing screen i.e. screen items references can be resolved.
	 * Returns array with screen names.
	 * @param CArray<Map> screens
	 * @return CArray<String>
	 */
	protected CArray<String> getIndependentScreens(CArray<Map> screens) {
		for (Entry<Object, Map> e : Clone.deepcopy(screens).entrySet()) {
		    Object num = e.getKey();
		    Map screen = e.getValue();
			if (empty(Nest.value(screen,"screenitems").$())) {
				continue;
			}

			for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
				if (Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_SCREEN) {
					if (empty(this.referencer.resolveScreen(Nest.value(screenItem,"resource","name").asString()))) {
						unset(screens,num);
						break;
					}
				}
			}
		}

		return rda_objectValues(screens, "name");
	}	
}
