package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MapsAction extends RadarBaseAction {
	
	private CArray<Map> _maps = null;
	
	@Override
	protected void doInitPage() {
		page("title", _("Network maps"));
		page("file", "maps.action");
		page("hist_arg", new String[] { "sysmapid" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		
		if (PAGE_TYPE_HTML == (Integer)page("type")) {
			define("RDA_PAGE_DO_REFRESH", 1);
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"sysmapid",		array(T_RDA_INT, O_OPT, P_SYS|P_NZERO,	DB_ID,					null),
			"mapname",		array(T_RDA_STR, O_OPT, P_SYS,			null,					null),
			"severity_min",	array(T_RDA_INT, O_OPT, P_SYS,			IN("0,1,2,3,4,5"),		null),
			"fullscreen",		array(T_RDA_INT, O_OPT, P_SYS,			IN("0,1"),				null),
			"favobj",			array(T_RDA_STR, O_OPT, P_ACT,			null,					null),
			"favref",			array(T_RDA_STR, O_OPT, P_ACT,			NOT_EMPTY,				null),
			"favid",				array(T_RDA_INT, O_OPT, P_ACT,			null,					null),
			"favstate",		array(T_RDA_INT, O_OPT, P_ACT,			NOT_EMPTY,				null),
			"favaction",		array(T_RDA_STR, O_OPT, P_ACT,			IN("'add','remove'"),	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		CMapGet moptions = new CMapGet();
		moptions.setOutput(new String[]{"sysmapid", "name"});
		moptions.setPreserveKeys(true);
		_maps = API.Map(getIdentityBean(), executor).get(moptions);
		order_result(_maps, "name");

		String mapName = get_request("mapname");
		if (!empty(mapName)) {
			unset(_REQUEST,"sysmapid");

			for(Map map : _maps) {
				if (mapName.equals(Nest.value(map,"name").asString())) {
					Nest.value(_REQUEST,"sysmapid").$(Nest.value(map,"sysmapid").$());
				}
			}
		} else if (empty(Nest.value(_REQUEST,"sysmapid").$())) {
			Nest.value(_REQUEST,"sysmapid").$(CProfile.get(getIdentityBean(), executor, "web.maps.sysmapid"));

			if (empty(Nest.value(_REQUEST,"sysmapid").$()) && !isset(_maps,Nest.value(_REQUEST,"sysmapid").asLong())) {
				Map firstMap = reset(_maps);
				if (!empty(firstMap)) {
					Nest.value(_REQUEST,"sysmapid").$(Nest.value(firstMap,"sysmapid").$());
				}
			}
		}

		if (isset(_REQUEST,"sysmapid") && !isset(_maps,Nest.value(_REQUEST,"sysmapid").asLong())) {
			access_deny();
		}

		CProfile.update(getIdentityBean(), executor, "web.maps.sysmapid", Nest.value(_REQUEST,"sysmapid").$(), PROFILE_TYPE_ID);
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		boolean result;
		if (isset(_REQUEST,"favobj")) {
			if ("sysmapid".equals(Nest.value(_REQUEST,"favobj").asString())) {
				result = false;

				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor, "web.favorite.sysmapids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { rm4favorites(\"sysmapid\", \""+Nest.value(_REQUEST,"favid").asString()+"\", 0); }\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.sysmapids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { add2favorites(\"sysmapid\", \""+Nest.value(_REQUEST,"favid").asString()+"\"); }\n");
					}
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
				}
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return false;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Display */
		CArray _data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$(),
			"sysmapid", Nest.value(_REQUEST,"sysmapid").$(),
			"maps", _maps
		);

		CMapGet moptions = new CMapGet();
		moptions.setOutput(API_OUTPUT_EXTEND);
		moptions.setSysmapIds(Nest.value(_data,"sysmapid").asLong());
		moptions.setExpandUrls(true);
		//TODO
//		Nest.value(_data,"map").$() = API.Map(getIdentityBean(), executor).get(array(
//			"output" => API_OUTPUT_EXTEND,
//			"sysmapids" => ,
//			"expandUrls" => true,
//			"selectSelements" => API_OUTPUT_EXTEND,
//			"selectLinks" => API_OUTPUT_EXTEND,
//			"preservekeys" => true
//		));
//		Nest.value(_data,"map").$() = reset(Nest.value(_data,"map").$());

//		Nest.value(_data,"pageFilter").$() = new CPageFilter(array(
//			"severitiesMin" => array(
//				"default" => Nest.value(_data,"map","severity_min").$(),
//				"mapId" => _data["sysmapid"]
//			),
//			"severityMin" => get_request("severity_min")
//		));
//		Nest.value(_data,"severity_min").$() = _data["pageFilter"].severityMin;

		// render view
		CView _mapsView = new CView("monitoring.maps", _data);
		_mapsView.render(getIdentityBean(), executor);
		_mapsView.show();
	}

}
