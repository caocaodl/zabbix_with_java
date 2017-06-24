package com.isoft.iradar.core;

import static com.isoft.iradar.core.Scope.GLOBAL;
import static com.isoft.iradar.core.Scope.REQUEST;

import java.util.List;
import java.util.Map;

import com.isoft.Feature;
import com.isoft.iradar.managers.CFactoryRegistry;
import com.isoft.iradar.model.CMessage;
import com.isoft.types.CArray;
import com.isoft.types.IList;
import com.isoft.types.IMap;

public class g {

	public static String RDA_SERVER = Feature.iradarServer;
	public static int RDA_SERVER_PORT = Feature.iradarPort;
	public static boolean RDA_SERVER_RUNNING = false;
	public static long RDA_SERVER_RUNNING_TS = 0;

	public static Var<Long> RDA_LOCMASTERID = new Var<Long>(REQUEST);
	public static Var<Long> RDA_CURMASTERID = new Var<Long>(REQUEST);

	public static Var<List<CMessage>> RDA_MESSAGES = new Var<List<CMessage>>(REQUEST, new IList());

	public static Var<Boolean> SHOW_COLOR_PICKER_SCRIPT_INSERTED = new Var(REQUEST, false);

	public static Var<CArray<String>> RDA_PAGE_POST_JS = new Var<CArray<String>>(REQUEST);
	
	public static Var<Map<Long, List<Long>>> userGroups = new Var<Map<Long, List<Long>>>(REQUEST, new IMap());
	public static Var<Map<String, Object>> config = new Var<Map<String, Object>>(REQUEST);
	public static Var<CArray<CArray<Map>>> identImages = new Var<CArray<CArray<Map>>>(REQUEST);
	public static Var<CArray<Map>> images = new Var<CArray<Map>>(REQUEST);
	public static Var<CArray> valueMaps = new Var<CArray>(REQUEST, new CArray());
	public static Var<CArray<String>> image_id = new Var<CArray<String>>(REQUEST, new CArray());
	public static Var<CArray<CArray<Map>>> _cachedExpressions = new Var<CArray<CArray<Map>>>(REQUEST, new CArray());

	// ----
	public static Var<CFactoryRegistry> factoryRegistryInstance = new Var(GLOBAL);
	public static Var<CArray<String>> weekdaynames = new Var<CArray<String>>(REQUEST);
	public static Var<CArray<String>> weekdaynameslong = new Var<CArray<String>>(REQUEST);
	public static Var<CArray<String>> months = new Var<CArray<String>>(REQUEST);
	public static Var<CArray<String>> monthslong = new Var<CArray<String>>(REQUEST);

	private g() {
	}

}
