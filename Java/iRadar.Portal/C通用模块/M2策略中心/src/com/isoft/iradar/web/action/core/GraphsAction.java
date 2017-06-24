package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_reverse;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FormsUtil.getCopyElementsFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.asort_by_key;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.GraphsUtil.copyGraphToHost;
import static com.isoft.iradar.inc.GraphsUtil.getGraphByGraphId;
import static com.isoft.iradar.inc.GraphsUtil.get_hosts_by_graphid;
import static com.isoft.iradar.inc.GraphsUtil.graphType;
import static com.isoft.iradar.inc.HostsUtil.isTemplate;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CGraphItemGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class GraphsAction extends RadarBaseAction {
	
	private Map discovery_rule;
	
	protected abstract String getAction();

	@Override
	protected void doInitPage() {
		page("title", isset(_REQUEST,"parent_discoveryid") ? _("Configuration of graph prototypes") : _("Configuration of graphs"));
		page("file", getAction());
		page("hist_arg", new String[] {"hostid", "parent_discoveryid"});
		page("css", new String[] {"lessor/devicecenter/graphs.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"parent_discoveryid",	array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,			null),
			"groupid",						array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,			null),
			"hostid",						array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,			null),
			"copy_type",					array(T_RDA_INT, O_OPT, P_SYS,		IN("0,1"),		"isset({copy})"),
			"copy_mode",				array(T_RDA_INT, O_OPT, P_SYS,		IN("0"),		null),
			"graphid",						array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,			"isset({form})&&{form}==\"update\""),
			"name",						array(T_RDA_STR, O_OPT, null,		NOT_EMPTY,		"isset({save})", _("Name")),
			"width",						array(T_RDA_INT, O_OPT, null,		BETWEEN(20, 65535), "isset({save})", _("Width")),
			"height",						array(T_RDA_INT, O_OPT, null,		BETWEEN(20, 65535), "isset({save})", _("Height")),
			"graphtype",					array(T_RDA_INT, O_OPT, null,		IN("0,1,2,3"),	"isset({save})"),
			"show_3d",					array(T_RDA_INT, O_OPT, P_NZERO,	IN("0,1"),		null),
			"show_legend",				array(T_RDA_INT, O_OPT, P_NZERO,	IN("0,1"),		null),
			"ymin_type",					array(T_RDA_INT, O_OPT, null,		IN("0,1,2"),	null),
			"ymax_type",				array(T_RDA_INT, O_OPT, null,		IN("0,1,2"),	null),
			"yaxismin",					array(T_RDA_DBL, O_OPT, null,		null,			"isset({save})&&({graphtype}==0||{graphtype}==1)", _("yaxismin")),
			"yaxismax",					array(T_RDA_DBL, O_OPT, null,		null,			"isset({save})&&({graphtype}==0||{graphtype}==1)", _("yaxismax")),
			"ymin_itemid",				array(T_RDA_INT, O_OPT, null,		DB_ID,			"isset({save})&&isset({ymin_type})&&{ymin_type}==3"),
			"ymax_itemid",				array(T_RDA_INT, O_OPT, null,		DB_ID,			"isset({save})&&isset({ymax_type})&&{ymax_type}==3"),
			"percent_left",				array(T_RDA_DBL, O_OPT, null,		BETWEEN(0, 100), null, _("Percentile line (left)")),
			"percent_right",			array(T_RDA_DBL, O_OPT, null,		BETWEEN(0, 100), null, _("Percentile line (right)")),
			"visible",						array(T_RDA_INT, O_OPT, null,		BETWEEN(0, 1),	null),
			"items",						array(T_RDA_STR, O_OPT, null,		null,			null),
			"show_work_period",	array(T_RDA_INT, O_OPT, null,		IN("1"),		null),
			"show_triggers",			array(T_RDA_INT, O_OPT, null,		IN("1"),		null),
			"group_graphid",			array(T_RDA_INT, O_OPT, null,		DB_ID,			null),
			"copy_targetid",			array(T_RDA_INT, O_OPT, null,		DB_ID,			null),
			"filter_groupid",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,			"isset({copy})&&isset({copy_type})&&{copy_type}==0"),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"clone",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"copy",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,		null,			null),
			"form",							array(T_RDA_STR, O_OPT, P_SYS,		null,			null),
			"form_refresh",				array(T_RDA_INT, O_OPT, null,		null,			null)
		);
		Map percentVisible = get_request("visible",array());
		if (!isset(percentVisible,"percent_left")) {
			unset(_REQUEST,"percent_left");
		}
		if (!isset(percentVisible,"percent_right")) {
			unset(_REQUEST,"percent_right");
		}
		if (isset(_REQUEST,"yaxismin") && rda_empty(Nest.value(_REQUEST,"yaxismin").$())) {
			unset(_REQUEST,"yaxismin");
		}
		if (isset(_REQUEST,"yaxismax") && rda_empty(Nest.value(_REQUEST,"yaxismax").$())) {
			unset(_REQUEST,"yaxismax");
		}
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
		Nest.value(_REQUEST,"items").$(get_request("items", array()));
		Nest.value(_REQUEST,"show_3d").$(get_request("show_3d", 0));
		Nest.value(_REQUEST,"show_legend").$(get_request("show_legend", 0));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
			if (!empty(Nest.value(_REQUEST,"parent_discoveryid").$())) {
				// check whether discovery rule is editable by user
				CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
				droptions.setItemIds(Nest.value(_REQUEST,"parent_discoveryid").asLong());
				droptions.setOutput(API_OUTPUT_EXTEND);
				droptions.setEditable(true);
				droptions.setPreserveKeys(true);
				CArray<Map> discovery_rules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
				discovery_rule = reset(discovery_rules);
				if (empty(discovery_rule)) {
					access_deny();
				}

				// sets corresponding hostid for later usage
				if (empty(Nest.value(_REQUEST,"hostid").$())) {
					Nest.value(_REQUEST,"hostid").$(Nest.value(discovery_rule,"hostid").$());
				}

				// check whether graph prototype is editable by user
				if (isset(Nest.value(_REQUEST,"graphid").$())) {
					CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
					gpoptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
					gpoptions.setOutput(new String[]{"graphid"});
					gpoptions.setEditable(true);
					gpoptions.setPreserveKeys(true);
					CArray<Map> graphPrototype = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
					if (empty(graphPrototype)) {
						access_deny();
					}
				}
			} else if (!empty(Nest.value(_REQUEST,"graphid").$())) {
				// check whether graph is normal and editable by user
				CGraphGet goptions = new CGraphGet();
				goptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
				goptions.setEditable(true);
				goptions.setPreserveKeys(true);
				CArray<Map> graphs = API.Graph(getIdentityBean(), executor).get(goptions);
				if (empty(graphs)) {
					access_deny();
				}
			} else if (!empty(Nest.value(_REQUEST,"hostid").$())) {
				// check whether host is editable by user
				CHostGet hoptions = new CHostGet();
				hoptions.setHostIds(Nest.value(_REQUEST,"hostid").asLong());
				hoptions.setTemplatedHosts(true);
				hoptions.setEditable(true);
				hoptions.setPreserveKeys(true);
				CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
				if (empty(hosts)) {
					access_deny();
				}
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"graphid")) {
			// graph
			CArray<Map> graphs = null;
			if(empty(Nest.value(_REQUEST,"parent_discoveryid").$())){
				CGraphGet goptions = new CGraphGet();
				goptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
				goptions.setOutput(API_OUTPUT_EXTEND);
				graphs = API.Graph(getIdentityBean(), executor).get(goptions);
			} else {
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
				gpoptions.setOutput(API_OUTPUT_EXTEND);
				graphs = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
			}
			Map graph = reset(graphs);

			_REQUEST = array_merge(_REQUEST, graph);

			// graph items
			CGraphItemGet gioptions = new CGraphItemGet();
			gioptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
			gioptions.setSortfield("gitemid");
			gioptions.setOutput(API_OUTPUT_EXTEND);
			gioptions.setExpandData(true);
			Nest.value(_REQUEST,"items").$(API.GraphItem(getIdentityBean(), executor).get(gioptions));

			unset(_REQUEST,"graphid");

			Nest.value(_REQUEST,"form").$("clone");
		} else if (hasRequest("save")) {
			CArray<Map> items = get_request("items", array());

			// remove passing \"gitemid\" to API if new items added via pop-up
			for(Map item : items) {
				if (empty(Nest.value(item,"gitemid").$())) {
					unset(item,"gitemid");
				}
			}

			final Map graph = map(
				"name", get_request("name"),
				"width", get_request("width"),
				"height", get_request("height"),
				"ymin_type", get_request("ymin_type", 0),
				"ymax_type", get_request("ymax_type", 0),
				"yaxismin", get_request("yaxismin", 0f),
				"yaxismax", get_request("yaxismax", 0f),
				"ymin_itemid", get_request("ymin_itemid"),
				"ymax_itemid", get_request("ymax_itemid"),
				"show_work_period", get_request("show_work_period", 0),
				"show_triggers", get_request("show_triggers", 0),
				"graphtype", get_request("graphtype"),
				"show_legend", get_request("show_legend", 1),
				"show_3d", get_request("show_3d", 0),
				"percent_left", get_request("percent_left", 0f),
				"percent_right", get_request("percent_right", 0f),
				"gitems", items
			);

			// create and update graph prototypes
			boolean result;
			if (hasRequest("parent_discoveryid")) {
				Nest.value(graph,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
				if (hasRequest("graphid")) {
					Nest.value(graph,"graphid").$(get_request("graphid"));
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.GraphPrototype(getIdentityBean(), executor).update(array(graph)));
						}
					});
					show_messages(result, _("Graph prototype updated"), _("Cannot update graph prototype"));
				} else {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.GraphPrototype(getIdentityBean(), executor).create(array(graph)));
						}
					});
					show_messages(result, _("Graph prototype added"), _("Cannot add graph prototype"));
				}
				clearCookies(result, get_request("parent_discoveryid"));
			} else {// create and update graphs
				if (hasRequest("graphid")) {
					Nest.value(graph,"graphid").$(get_request("graphid"));
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Graph(getIdentityBean(), executor).update(array(graph)));
						}
					});
					show_messages(result, _("Graph updated"), _("Cannot update graph"));
				} else {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Graph(getIdentityBean(), executor).create(array(graph)));
						}
					});
					show_messages(result, _("Graph added"), _("Cannot add graph"));
				}
				clearCookies(result, get_request("hostid"));
			}

			if (result) {
				if (hasRequest("graphid")) {
					add_audit(getIdentityBean(), executor,
						AUDIT_ACTION_UPDATE,
						AUDIT_RESOURCE_GRAPH,
						"ID ["+Nest.value(graph,"graphid").$()+"] 名称 ["+get_request("name")+"]"
					);
				} else {
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_ADD, AUDIT_RESOURCE_GRAPH, "名称 ["+get_request("name")+"]");
				}
				unset(_REQUEST,"form");
			}
		} else if (hasRequest("delete") && hasRequest("graphid")) {
			final String graphId = get_request("graphid");

			boolean result;
			if (hasRequest("parent_discoveryid")) {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.GraphPrototype(getIdentityBean(), executor).delete(Nest.as(graphId).asLong()));
					}
				});
				show_messages(result, _("Graph prototype deleted"), _("Cannot delete graph prototype"));
				clearCookies(result, get_request("parent_discoveryid"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Graph(getIdentityBean(), executor).delete(Nest.as(graphId).asLong()));
					}
				});
				show_messages(result, _("Graph deleted"), _("Cannot delete graph"));
				clearCookies(result, get_request("hostid"));
			}

			if (result) {
				unset(_REQUEST,"form");
			}
		} else if ("delete".equals(get_request("go")) && hasRequest("group_graphid")) {
			final CArray graphIds = get_request("group_graphid",array());

			if (hasRequest("parent_discoveryid")) {
				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.GraphPrototype(getIdentityBean(), executor).delete(graphIds).valuesAsLong());
					}
				});
				show_messages(result, _("Graph prototypes deleted"), _("Cannot delete graph prototypes"));
				clearCookies(result, get_request("parent_discoveryid"));
			} else {
				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Graph(getIdentityBean(), executor).delete(graphIds).get("graphids"));
					}
				});
				show_messages(result, _("Graphs deleted"), _("Cannot delete graphs"));
				clearCookies(result, get_request("hostid"));
			}
		} else if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"copy") && isset(_REQUEST,"group_graphid")) {
			if (!empty(Nest.value(_REQUEST,"copy_targetid").asCArray()) && isset(_REQUEST,"copy_type")) {
				boolean goResult = true;

				CHostGet options = new CHostGet();
				options.setEditable(true);
				options.setTemplatedHosts(true);

				// hosts
				if (Nest.value(_REQUEST,"copy_type").asInteger() == 0) {
					options.setHostIds(Nest.array(_REQUEST,"copy_targetid").asLong());
				} else {// groups
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(Nest.array(_REQUEST,"copy_targetid").asLong());
					hgoptions.setEditable(true);
					CArray<Map> dbGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
					dbGroups = rda_toHash(dbGroups, "groupid");

					for(Long groupid : Nest.array(_REQUEST,"copy_targetid").asLong()) {
						if (!isset(dbGroups,groupid)) {
							access_deny();
						}
					}
					options.setGroupIds(Nest.array(_REQUEST,"copy_targetid").asLong());
				}

				CArray<Map> dbHosts = API.Host(getIdentityBean(), executor).get(options);

				DBstart(executor);
				for(final Long graphid : Nest.array(_REQUEST,"group_graphid").asLong()) {
					for(final Map host : dbHosts) {
						goResult &= Call(new Wrapper<Boolean>() {
							@Override
							protected Boolean doCall() throws Throwable {
								return !empty(copyGraphToHost(getIdentityBean(), executor, graphid, Nest.value(host,"hostid").asLong()));
							}
						});
					}
				}
				goResult = DBend(executor, goResult);

				show_messages(goResult, _("Graphs copied"), _("Cannot copy graphs"));
				clearCookies(goResult,
					empty(Nest.value(_REQUEST,"parent_discoveryid").$()) ? Nest.value(_REQUEST,"hostid").asString() : Nest.value(_REQUEST,"parent_discoveryid").asString()
				);

				Nest.value(_REQUEST,"go").$("none2");
			} else {
				error(_("No target selected."));
			}
			show_messages();
		}

		int module = Nest.as(page("module")).asInteger();
		
		CArray pfparams = null;
		if (IMonModule.policy.ordinal() == module) {
			pfparams = map(
				"groups", map(
						"not_proxy_hosts", true,
						"editable", true
				),
				"hosts", map(
					"editable", true,
					"templated_hosts", true
				),
				"groupid", IMonGroup.TEMPLATES.id(),
				"hostid", get_request("hostid", null)
			);
		} else {
			pfparams = map(
				"groups", map(
					"not_proxy_hosts", true,
					"editable", true
				),
				"hosts", map(
					"editable", true,
					"templated_hosts", true
				),
				"groupid", get_request("groupid", null),
				"hostid", get_request("hostid", null)
			);
		}

		/* Display */
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, pfparams);

		if (empty(Nest.value(_REQUEST,"parent_discoveryid").$())) {
			if (pageFilter.$("groupid").asLong() > 0) {
				Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").asLong());
			}
			if (pageFilter.$("hostid").asLong() > 0) {
				Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").asLong());
			}
		}

		if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_graphid")) {
			// render view
			CView graphView = new CView("configuration.copy.elements", getCopyElementsFormData(this.getIdentityBean(), executor, "group_graphid"));
			graphView.render(getIdentityBean(), executor);
			graphView.show();
		} else if (isset(_REQUEST,"form")) {
			Map data = map(
				"form", Nest.value(_REQUEST, "form").$(),
				"form_refresh", get_request("form_refresh", 0),
				"graphid",  Nest.value(_REQUEST, "graphid").$(),
				"parent_discoveryid", get_request("parent_discoveryid"),
				"group_gid", get_request("group_gid", array()),
				"hostid", get_request("hostid", 0),
				"normal_only", get_request("normal_only")
			);

			if (!empty(Nest.value(data,"graphid").$()) && !isset(_REQUEST,"form_refresh")) {
				CArray<Map> graphs = null;
				if(empty(Nest.value(data,"parent_discoveryid").$())){
					CGraphGet goptions = new CGraphGet();
					goptions.setGraphIds(Nest.value(data,"graphid").asLong());
					goptions.setOutput(API_OUTPUT_EXTEND);
					goptions.setSelectHosts(new String[]{"hostid"});
					graphs = API.Graph(getIdentityBean(), executor).get(goptions);
				} else {
					CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
					gpoptions.setGraphIds(Nest.value(data,"graphid").asLong());
					gpoptions.setOutput(API_OUTPUT_EXTEND);
					gpoptions.setSelectHosts(new String[]{"hostid"});
					graphs = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
				}
				Map graph = reset(graphs);

				Nest.value(data,"name").$(Nest.value(graph,"name").$());
				Nest.value(data,"width").$(Nest.value(graph,"width").$());
				Nest.value(data,"height").$(Nest.value(graph,"height").$());
				Nest.value(data,"ymin_type").$(Nest.value(graph,"ymin_type").$());
				Nest.value(data,"ymax_type").$(Nest.value(graph,"ymax_type").$());
				Nest.value(data,"yaxismin").$(Nest.value(graph,"yaxismin").$());
				Nest.value(data,"yaxismax").$(Nest.value(graph,"yaxismax").$());
				Nest.value(data,"ymin_itemid").$(Nest.value(graph,"ymin_itemid").$());
				Nest.value(data,"ymax_itemid").$(Nest.value(graph,"ymax_itemid").$());
				Nest.value(data,"show_work_period").$(Nest.value(graph,"show_work_period").$());
				Nest.value(data,"show_triggers").$(Nest.value(graph,"show_triggers").$());
				Nest.value(data,"graphtype").$(Nest.value(graph,"graphtype").$());
				Nest.value(data,"show_legend").$(Nest.value(graph,"show_legend").$());
				Nest.value(data,"show_3d").$(Nest.value(graph,"show_3d").$());
				Nest.value(data,"percent_left").$(Nest.value(graph,"percent_left").$());
				Nest.value(data,"percent_right").$(Nest.value(graph,"percent_right").$());
				Nest.value(data,"templateid").$(Nest.value(graph,"templateid").$());
				Nest.value(data,"templates").$(array());

				// if no host has been selected for the navigation panel, use the first graph host
				if (empty(Nest.value(data,"hostid").$())) {
					Map host = reset(Nest.value(graph,"hosts").asCArray());
					Nest.value(data,"hostid").$(Nest.value(host,"hostid").$());
				}

				// templates
				if (!empty(Nest.value(data,"templateid").$())) {
					long parentGraphid = Nest.value(data,"templateid").asLong();
					CLink link = null;
					Map parentGraph = null;
					Map parentTemplate = null;
					Map parentGraphPrototype = null;
					CArray<Map> parentTemplates = null;
					CGraphPrototypeGet gpoptions = null;
					do {
						parentGraph = getGraphByGraphId(getIdentityBean(), executor, parentGraphid);
						link = null;
						// parent graph prototype link
						if (!empty(get_request("parent_discoveryid"))) {
							gpoptions = new CGraphPrototypeGet();
							gpoptions.setGraphIds(Nest.value(parentGraph,"graphid").asLong());
							gpoptions.setSelectTemplates(API_OUTPUT_EXTEND);
							gpoptions.setSelectDiscoveryRule(new String[]{"itemid"});
							CArray<Map> parentGraphPrototypes = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
							if (!empty(parentGraphPrototypes)) {
								parentGraphPrototype = reset(parentGraphPrototypes);
								parentTemplate = reset(Nest.value(parentGraphPrototype,"templates").asCArray());

								link = new CLink(Nest.value(parentTemplate,"name").asString(),
										getAction()+"?form=update&graphid="+Nest.value(parentGraphPrototype,"graphid").asString()+"&hostid="+Nest.value(parentTemplate,"templateid").asString()+"&parent_discoveryid="+Nest.value(parentGraphPrototype,"discoveryRule","itemid").asString()
								);
							}
						} else {// parent graph link
							parentTemplates = get_hosts_by_graphid(getIdentityBean(), executor, Nest.value(parentGraph,"graphid").asLong());
							parentTemplate = DBfetch(parentTemplates);

							link = new CLink(Nest.value(parentTemplate,"name").asString(),
								getAction()+"?form=update&graphid="+Nest.value(parentGraph,"graphid").asString()+"&hostid="+Nest.value(parentTemplate,"hostid").asString()
							);
						}
						if (isset(link)) {
							Nest.value(data,"templates").asCArray().add(link);
							Nest.value(data,"templates").asCArray().add(SPACE+RARR+SPACE);
						}
						parentGraphid = Nest.value(parentGraph,"templateid").asLong();
					} while (parentGraphid != 0);
					Nest.value(data,"templates").$(array_reverse(Nest.value(data,"templates").asCArray()));
					array_shift(Nest.value(data,"templates").asCArray());
				}

				// items
				CGraphItemGet gioptions = new CGraphItemGet();
				gioptions.setOutput(new String[]{"gitemid", "graphid", "itemid", "type", "drawtype", "yaxisside", "calc_fnc", "color", "sortorder"});
				gioptions.setGraphIds(Nest.value(data,"graphid").asLong());
				gioptions.setSortfield("gitemid");
				Nest.value(data,"items").$(API.GraphItem(getIdentityBean(), executor).get(gioptions));
			} else {
				Nest.value(data,"name").$(get_request("name", ""));
				Nest.value(data,"graphtype").$(get_request("graphtype", GRAPH_TYPE_NORMAL));

				if (Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED) {
					Nest.value(data,"width").$(get_request("width", 400));
					Nest.value(data,"height").$(get_request("height", 300));
				} else {
					Nest.value(data,"width").$(get_request("width", 900));
					Nest.value(data,"height").$(get_request("height", 200));
				}

				Nest.value(data,"ymin_type").$(get_request("ymin_type", GRAPH_YAXIS_TYPE_CALCULATED));
				Nest.value(data,"ymax_type").$(get_request("ymax_type", GRAPH_YAXIS_TYPE_CALCULATED));
				Nest.value(data,"yaxismin").$(get_request("yaxismin", 0f));//加入f，因为该值为浮点数据
				Nest.value(data,"yaxismax").$(get_request("yaxismax", 100f));//为0时，为空则设置为0，另外，同时也会默认为整型
				Nest.value(data,"ymin_itemid").$(get_request("ymin_itemid", 0));
				Nest.value(data,"ymax_itemid").$(get_request("ymax_itemid", 0));
				Nest.value(data,"show_work_period").$(get_request("show_work_period", 0));
				Nest.value(data,"show_triggers").$(get_request("show_triggers", 0));
				Nest.value(data,"show_legend").$(get_request("show_legend", 0));
				Nest.value(data,"show_3d").$(get_request("show_3d", 0));
				Nest.value(data,"visible").$(get_request("visible"));
				Nest.value(data,"percent_left").$(0);
				Nest.value(data,"percent_right").$(0);
				Nest.value(data,"visible").$(get_request("visible", array()));
				Nest.value(data,"items").$(get_request("items", array()));

				if (isset(Nest.value(data,"visible","percent_left").$())) {
					Nest.value(data,"percent_left").$(get_request("percent_left", 0));
				}
				if (isset(Nest.value(data,"visible","percent_right").$())) {
					Nest.value(data,"percent_right").$(get_request("percent_right", 0));
				}
			}

			if (empty(Nest.value(data,"graphid").$()) && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"show_legend").$(1);
				Nest.value(_REQUEST,"show_legend").$(1);
				Nest.value(data,"show_work_period").$(1);
				Nest.value(_REQUEST,"show_work_period").$(1);
				Nest.value(data,"show_triggers").$(1);
				Nest.value(_REQUEST,"show_triggers").$(1);
			}

			// items
			if (!empty(Nest.value(data,"items").$())) {
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_", "flags"});
				ioptions.setSelectHosts(new String[]{"hostid", "name"});
				ioptions.setItemIds(rda_objectValues(Nest.value(data,"items").$(), "itemid").valuesAsLong());
				ioptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString(), Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString(), Nest.as(RDA_FLAG_DISCOVERY_CREATED).asString());
				ioptions.setWebItems(true);
				ioptions.setPreserveKeys(true);
				CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);

				for(Map item : (CArray<Map>)Nest.value(data,"items").asCArray()) {
					Map host = reset(Nest.value(items,item.get("itemid"),"hosts").asCArray());

					Nest.value(item,"host").$(Nest.value(host,"name").$());
					Nest.value(item,"hostid").$(Nest.value(items,item.get("itemid"),"hostid").$());
					Nest.value(item,"name").$(Nest.value(items,item.get("itemid"),"name").$());
					Nest.value(item,"key_").$(Nest.value(items,item.get("itemid"),"key_").$());
					Nest.value(item,"flags").$(Nest.value(items,item.get("itemid"),"flags").$());
				}
				Nest.value(data,"items").$(CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, Nest.value(data,"items").asCArray()));
			}

			Nest.value(data,"items").$(array_values(Nest.value(data,"items").asCArray()));
			int itemCount = count(Nest.value(data,"items").asCArray());
			for (int i = 0; i < itemCount - 1;) {
				// check if we delete an item
				int next = i + 1;
				while (!isset(Nest.value(data,"items",next).$()) && next < (itemCount - 1)) {
					next++;
				}

				if (isset(Nest.value(data,"items",next).$()) && Nest.value(data,"items",i,"sortorder").asString().equals(Nest.value(data,"items",next,"sortorder").asString())) {
					for (int j = next; j < itemCount; j++) {
						if (Nest.value(data,"items",j - 1,"sortorder").asString().equals(Nest.value(data,"items",j,"sortorder").asString())) {
							Nest.value(data,"items",j,"sortorder").$(Nest.value(data,"items",j,"sortorder").asInteger()+1);
						}
					}
				}

				i = next;
			}
			asort_by_key(Nest.value(data,"items").asCArray(), "sortorder");
			Nest.value(data,"items").$(array_values(Nest.value(data,"items").asCArray()));

			// is template
			Nest.value(data,"is_template").$(isTemplate(getIdentityBean(), executor,Nest.value(data,"hostid").asString()));

			// render view
			CView graphView = new CView("configuration.graph.edit", data);
			graphView.render(getIdentityBean(), executor);
			graphView.show();
		} else {
			Map data = map(
				"pageFilter", pageFilter,
				"hostid", (pageFilter.$("hostid").asLong() > 0) ? pageFilter.$("hostid").asLong() : get_request("hostid"),
				"parent_discoveryid", get_request("parent_discoveryid"),
				"graphs", array(),
				"discovery_rule", empty(Nest.value(_REQUEST,"parent_discoveryid").$()) ? null : discovery_rule
			);
			String groupidIsZero=get_request("groupid");
			String hostidIsZero=get_request("hostid");
			if("0".equals(groupidIsZero)&&"0".equals(hostidIsZero)){
				Nest.value(data,"isZero").$(true);
			}
			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			String sortorder = getPageSortOrder(getIdentityBean(), executor);
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			// get graphs
			CArray<Map> graphs = null;
			if(empty(Nest.value(_REQUEST,"parent_discoveryid").$())){
				CGraphGet goptions = new CGraphGet();
				if(!empty(Nest.value(data,"hostid").$())){
					goptions.setHostIds(Nest.value(data,"hostid").asLong());
				}
				if(empty(Nest.value(data,"hostid").$()) && pageFilter.$("groupid").asLong() > 0){
					goptions.setGroupIds(pageFilter.$("groupid").asLong());
				}
				goptions.setEditable(true);
				goptions.setOutput(new String[]{"graphid", "name", "graphtype"});
				goptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				graphs = API.Graph(getIdentityBean(), executor).get(goptions);
			} else {
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				if(!empty(Nest.value(data,"hostid").$())){
					gpoptions.setHostIds(Nest.value(data,"hostid").asLong());
				}
				if(!empty(Nest.value(data,"hostid").$()) && pageFilter.$("groupid").asLong() > 0){
					gpoptions.setGroupIds(pageFilter.$("groupid").asLong());
				}
				gpoptions.setEditable(true);
				gpoptions.setOutput(new String[]{"graphid", "name", "graphtype"});
				gpoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				graphs = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
			}
			Nest.value(data,"graphs").$(graphs);

			if ("graphtype".equals(sortfield)) {
				for (Entry<Object, Map> e : graphs.entrySet()) {
				    Object gnum = e.getKey();
				    Map graph = e.getValue();
					Nest.value(graphs,gnum,"graphtype").$(graphType(Nest.value(graph,"graphtype").$()));
				}
			}

			order_result(graphs, sortfield, sortorder);

			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,
				graphs,
				array("graphid"),
				map(
					"hostid", get_request("hostid"),
					"parent_discoveryid", get_request("parent_discoveryid")
				)
			));

			// get graphs after paging
			if(empty(Nest.value(_REQUEST,"parent_discoveryid").$())){
				CGraphGet goptions = new CGraphGet();
				goptions.setGraphIds(rda_objectValues(graphs, "graphid").valuesAsLong());
				goptions.setOutput(new String[]{"graphid", "name", "templateid", "graphtype", "width", "height"});
				goptions.setSelectDiscoveryRule(new String[]{"itemid", "name"});
				if(empty(Nest.value(data,"hostid").$())){
					goptions.setSelectHosts(new String[]{"name"});
					goptions.setSelectTemplates(new String[]{"name"});
				}
				graphs = API.Graph(getIdentityBean(), executor).get(goptions);
			} else {
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setGraphIds(rda_objectValues(graphs, "graphid").valuesAsLong());
				gpoptions.setOutput(new String[]{"graphid", "name", "templateid", "graphtype", "width", "height"});
				gpoptions.setSelectDiscoveryRule(new String[]{"itemid", "name"});
				if(empty(Nest.value(data,"hostid").$())){
					gpoptions.setSelectHosts(new String[]{"name"});
					gpoptions.setSelectTemplates(new String[]{"name"});
				}
				graphs = API.GraphPrototype(getIdentityBean(), executor).get(gpoptions);
			}
			Nest.value(data,"graphs").$(graphs);

			for (Entry<Object, Map> e : graphs.entrySet()) {
			    Object gnum = e.getKey();
			    Map graph = e.getValue();
				Nest.value(graphs,gnum,"graphtype").$(graphType(Nest.value(graph,"graphtype").$()));
			}

			order_result(graphs, sortfield, sortorder);

			// render view
			CView graphView = new CView("configuration.graph.list", data);
			graphView.render(getIdentityBean(), executor);
			graphView.show();
		}
	}

}
