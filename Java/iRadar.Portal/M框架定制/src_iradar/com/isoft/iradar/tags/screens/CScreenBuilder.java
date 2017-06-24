package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._page;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HALIGN_CENTER;
import static com.isoft.iradar.inc.Defines.HALIGN_DEFAULT;
import static com.isoft.iradar.inc.Defines.HALIGN_LEFT;
import static com.isoft.iradar.inc.Defines.HALIGN_RIGHT;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_ACTIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_CHART;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_EVENTS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HISTORY;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SERVER_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SYSTEM_STATUS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_URL;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_DATE_DESC;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.VALIGN_BOTTOM;
import static com.isoft.iradar.inc.Defines.VALIGN_DEFAULT;
import static com.isoft.iradar.inc.Defines.VALIGN_MIDDLE;
import static com.isoft.iradar.inc.Defines.VALIGN_TOP;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CScreenItemGet;
import com.isoft.iradar.model.params.CScreenParam;
import com.isoft.iradar.model.params.CTemplateScreenItemGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;

public class CScreenBuilder {
	
	/**
	 * Switch on/off flicker-free screens auto refresh.
	 * @var boolean
	 */
	public Boolean isFlickerfree;

	/**
	 * Page file.
	 * @var string
	 */
	public String  pageFile;
	
	public String screenid;

	/**
	 * Screen data
	 * @var array
	 */
	public Map screen;

	/**
	 * Display mode
	 * @var int
	 */
	public Integer mode;

	public Long timestamp;

	/**
	 * Host id
	 * @var string
	 */
	public String hostid;

	/**
	 * Profile table entity name #1
	 * @var string
	 */
	public String profileIdx;

	/**
	 * Profile table record id belongs to #1
	 * @var int
	 */
	public Integer profileIdx2;

	/**
	 * Is profile will be updated
	 * @var boolean
	 */
	public Boolean updateProfile;

	/**
	 * Time control timeline
	 * @var array
	 */
	public Map timeline;
	
	private SQLExecutor executor;
	
	private IIdentityBean idBean;
	
	public CScreenBuilder(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, new CScreenParam());
	}
	
	public CScreenBuilder(IIdentityBean idBean, SQLExecutor executor, Map options) {
		this.idBean = idBean;
		this.executor = executor;
		isFlickerfree = isset(options,"isFlickerfree") ? Nest.value(options,"isFlickerfree").asBoolean() : true;
		mode = isset(options,"mode") ? Nest.value(options,"mode").asInteger() : SCREEN_MODE_SLIDESHOW;
		timestamp = !empty(Nest.value(options,"timestamp").$()) ? Nest.value(options,"timestamp").asLong() : time();
		hostid = !empty(Nest.value(options,"hostid").$()) ? Nest.value(options,"hostid").asString() : null;

		// get page file
		if (!empty(Nest.value(options,"pageFile").$())) {
			pageFile = Nest.value(options,"pageFile").asString();
		} else {
			pageFile = Nest.value(_page(),"file").asString();
		}

		// get screen
		if (!empty(Nest.value(options,"screen").$())) {
			screen = Nest.value(options,"screen").asCArray();
		} else if (!empty(Nest.value(options,"screenid").$())) {
			CScreenGet soptions = new CScreenGet();
			soptions.setScreenIds(Nest.value(options,"screenid").asLong());
			soptions.setOutput(API_OUTPUT_EXTEND);
			soptions.setSelectScreenItems(API_OUTPUT_EXTEND);
			soptions.setEditable(mode == SCREEN_MODE_EDIT);
			CArray<Map> screens = API.Screen(this.idBean, executor).get(soptions);
			if (!empty(screens)) {
				screen = reset(screens);
			} else {
				access_deny();
			}
		}

		// calculate time
		profileIdx = !empty(Nest.value(options,"profileIdx").$()) ? Nest.value(options,"profileIdx").asString() : "";
		profileIdx2 = !empty(Nest.value(options,"profileIdx2").$()) ? Nest.value(options,"profileIdx2").asInteger() : null;
		updateProfile = isset(options,"updateProfile") ? Nest.value(options,"updateProfile").asBoolean() : true;

		timeline = CScreenBase.calculateTime(idBean, executor, map(
			"profileIdx", profileIdx,
			"profileIdx2", profileIdx2,
			"updateProfile", updateProfile,
			"period", !empty(Nest.value(options,"period").$()) ? Nest.value(options,"period").$() : null,
			"stime", !empty(Nest.value(options,"stime").$()) ? Nest.value(options,"stime").$() : null
		));
	}
	
	/**
	 * Get particular screen object.
	 *
	 * @static
	 *
	 * @param array	options
	 * @param int		options["resourcetype"]
	 * @param int		options["screenitemid"]
	 * @param int		options["hostid"]
	 *
	 * @return CScreenBase
	 */
	public static CScreenBase getScreen(IIdentityBean idBean, SQLExecutor executor) {
		return getScreen(idBean, executor, map());
	}
	
	public static CScreenBase getScreen(IIdentityBean idBean, SQLExecutor executor, Map options) {
		// get resourcetype from screenitem
		if (empty(Nest.value(options,"screenitem").$()) && !empty(Nest.value(options,"screenitemid").$())) {
			if (!empty(Nest.value(options,"hostid").$())) {
				CTemplateScreenItemGet tsioptions = new CTemplateScreenItemGet();
				tsioptions.setScreenItemIds(Nest.value(options,"screenitemid").asLong());
				tsioptions.setHostIds(Nest.value(options,"hostid").asLong());
				tsioptions.setOutput(API_OUTPUT_EXTEND);
				Nest.value(options,"screenitem").$(API.TemplateScreenItem(idBean, executor).get(tsioptions));
			} else {
				CScreenItemGet sioptions = new CScreenItemGet();
				sioptions.setScreenitemIds(Nest.value(options,"screenitemid").asLong());
				sioptions.setOutput(API_OUTPUT_EXTEND);
				Nest.value(options,"screenitem").$(API.ScreenItem(idBean, executor).get(sioptions));
			}
			Nest.value(options,"screenitem").$(reset(Nest.value(options,"screenitem").asCArray()));
		}

		if (rda_empty(Nest.value(options,"resourcetype").$()) && !rda_empty(Nest.value(options,"screenitem","resourcetype").$())) {
			Nest.value(options,"resourcetype").$(Nest.value(options,"screenitem","resourcetype").$());
		}

		if (rda_empty(Nest.value(options,"resourcetype").$())) {
			return null;
		}

		// get screen
		switch (Nest.value(options,"resourcetype").asInteger()) {
			case SCREEN_RESOURCE_GRAPH:
				return new CScreenGraph(idBean, executor, options);

			case SCREEN_RESOURCE_SIMPLE_GRAPH:
				return new CScreenSimpleGraph(idBean, executor, options);

			case SCREEN_RESOURCE_MAP:
				return new CScreenMap(idBean, executor, options);

			case SCREEN_RESOURCE_PLAIN_TEXT:
				return new CScreenPlainText(idBean, executor, options);

			case SCREEN_RESOURCE_HOSTS_INFO:
				return new CScreenHostsInfo(idBean, executor, options);

			case SCREEN_RESOURCE_TRIGGERS_INFO:
				return new CScreenTriggersInfo(idBean, executor, options);

			case SCREEN_RESOURCE_SERVER_INFO:
				return new CScreenServerInfo(idBean, executor, options);

//			case SCREEN_RESOURCE_CLOCK:
//				return new CScreenClock(idBean, executor, options);

			case SCREEN_RESOURCE_SCREEN:
				return new CScreenScreen(idBean, executor, options);

			case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
				return new CScreenTriggersOverview(idBean, executor, options);

			case SCREEN_RESOURCE_DATA_OVERVIEW:
				return new CScreenDataOverview(idBean, executor, options);

			case SCREEN_RESOURCE_URL:
				return new CScreenUrl(idBean, executor, options);

			case SCREEN_RESOURCE_ACTIONS:
				return new CScreenActions(idBean, executor, options);

			case SCREEN_RESOURCE_EVENTS:
				return new CScreenEvents(idBean, executor, options);

			case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
				return new CScreenHostgroupTriggers(idBean, executor, options);

			case SCREEN_RESOURCE_SYSTEM_STATUS:
				return new CScreenSystemStatus(idBean, executor, options);

			case SCREEN_RESOURCE_HOST_TRIGGERS:
				return new CScreenHostTriggers(idBean, executor, options);

			case SCREEN_RESOURCE_HISTORY:
				return new CScreenHistory(idBean, executor, options);

			case SCREEN_RESOURCE_CHART:
				return new CScreenChart(idBean, executor, options);

			default:
				return null;
		}
	}
	
	/**
	 * Process screen with particular screen objects.
	 *
	 * @return CTable
	 */
	public CTable show(IIdentityBean idBean) {
		this.idBean = idBean;
		if (empty(screen)) {
			return new CTableInfo(_("No screens found."));
		}

		CArray<Map> skipedFields = array();
		CArray<Map> screenitems = array();
		Map emptyScreenColumns = array();

		// calculate table columns and rows
		for(Map screenitem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
			screenitems.add(screenitem);

			for (int i = 0; i < Nest.value(screenitem,"rowspan").asInteger() || i == 0; i++) {
				for (int j = 0; j < Nest.value(screenitem,"colspan").asInteger() || j == 0; j++) {
					if (i != 0 || j != 0) {
						if (!isset(skipedFields,Nest.value(screenitem,"y").asInteger() + i)) {
							Nest.value(skipedFields,Nest.value(screenitem,"y").asInteger() + i).$(array());
						}
						Nest.value(skipedFields,Nest.value(screenitem,"y").asInteger() + i,Nest.value(screenitem,"x").asInteger() + j).$(1);
					}
				}
			}
		}

		// create screen table
		CTable screenTable = new CTable();
		screenTable.setAttribute("class",
			in_array(mode, array(SCREEN_MODE_PREVIEW, SCREEN_MODE_SLIDESHOW)) ? "screen_view" : "screen_edit"
		);
		screenTable.setAttribute("id", "iframe");

		// action top row
		CArray<CCol> newColumns = null;
		if (mode == SCREEN_MODE_EDIT) {
			newColumns = array(new CCol(new CImg("images/general/zero.png", "zero", 1, 1)));

			for (int i = 0, size = Nest.value(screen,"hsize").asInteger() + 1; i < size; i++) {
				CImg icon = new CImg("images/general/plus.png", null, null, null, "pointer");
				icon.addAction("onclick", "javascript: location.href = \"screenedit.action?config=1&screenid="+Nest.value(screen,"screenid").asString()+"&add_col="+i+"\";");
				array_push(newColumns, new CCol(icon));
			}

			screenTable.addRow(newColumns);
		}

		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		for (int r = 0; r < Nest.value(screen,"vsize").asInteger(); r++) {
			newColumns = array();
			boolean emptyScreenRow = true;

			// action left cell
			if (mode == SCREEN_MODE_EDIT) {
				CImg icon = new CImg("images/general/plus.png", null, null, null, "pointer");
				icon.addAction("onclick", "javascript: location.href = \"screenedit.action?config=1&screenid="+Nest.value(screen,"screenid").asString()+"&add_row="+r+"\";");

				array_push(newColumns, new CCol(icon));
			}

			for (int c = 0; c < Nest.value(screen,"hsize").asInteger(); c++) {
				if (isset(Nest.value(skipedFields,r,c).$())) {
					continue;
				}

				// screen item
				boolean isEditForm = false;
				Map screenitem = array();

				for(Map tmprow : screenitems) {
					if (Nest.value(tmprow,"x").asInteger() == c && Nest.value(tmprow,"y").asInteger() == r) {
						screenitem = Clone.deepcopy(tmprow);
						break;
					}
				}

				if (empty(screenitem)) {
					screenitem = map(
						"screenitemid", 0,
						"resourcetype", 0,
						"resourceid", 0,
						"width", 0,
						"height", 0,
						"colspan", 1,
						"rowspan", 1,
						"elements", 0,
						"valign", VALIGN_DEFAULT,
						"halign", HALIGN_DEFAULT,
						"style", 0,
						"url", "",
						"dynamic", 0,
						"sort_triggers", SCREEN_SORT_TRIGGERS_DATE_DESC
					);
				}

				if (!empty(Nest.value(screenitem,"screenitemid").$())) {
					emptyScreenRow = false;
					Nest.value(emptyScreenColumns,c).$(1);
				}

				// action
				String action = null;
				if (mode == SCREEN_MODE_EDIT && Nest.value(screenitem,"screenitemid").asLong() != 0L) {
					action = "screenedit.action?form=update"+url_param(idBean, "screenid")+"&screenitemid="+Nest.value(screenitem,"screenitemid").$();
				} else if (mode == SCREEN_MODE_EDIT && Nest.value(screenitem,"screenitemid").asLong() == 0L) {
					action = "screenedit.action?form=update"+url_param(idBean, "screenid")+"&x="+c+"&y="+r;
				} else {
					action = null;
				}

				Object item = null;
				// edit form cell
				if (mode == SCREEN_MODE_EDIT
						&& (isset(_REQUEST,"form") && "update".equals(Nest.value(_REQUEST,"form").asString()))
						&& ((isset(_REQUEST,"x") && Nest.value(_REQUEST,"x").asInteger() == c && isset(_REQUEST,"y") && Nest.value(_REQUEST,"y").asInteger() == r)
								|| (isset(_REQUEST,"screenitemid") && bccomp(Nest.value(_REQUEST,"screenitemid").$(), Nest.value(screenitem,"screenitemid").$()) == 0))) {
					CView screenView = new CView("configuration.screen.constructor.edit", map("screen", screen));
					item = screenView.render(this.idBean, this.executor);
					isEditForm = true;
				}
				// screen cell
				else if (!empty(Nest.value(screenitem,"screenitemid").$()) && isset(screenitem,"resourcetype")) {
					CScreenBase screenBase = getScreen(this.idBean, this.executor, map(
						"isFlickerfree", isFlickerfree,
						"pageFile", pageFile,
						"mode", mode,
						"timestamp", timestamp,
						"hostid", hostid,
						"profileIdx", profileIdx,
						"profileIdx2", profileIdx2,
						"updateProfile", updateProfile,
						"timeline", timeline,
						"resourcetype", Nest.value(screenitem,"resourcetype").$(),
						"screenitem", screenitem
					));

					if (!empty(screenBase)) {
						if (mode == SCREEN_MODE_EDIT && !empty(Nest.value(screenitem,"screenitemid").$())) {
							screenBase.action = "screenedit.action?form=update"+url_param(idBean, "screenid")+"&screenitemid="+Nest.value(screenitem,"screenitemid").$();
						} else if (mode == SCREEN_MODE_EDIT && empty(Nest.value(screenitem,"screenitemid").$())) {
							screenBase.action = "screenedit.action?form=update"+url_param(idBean, "screenid")+"&x="+c+"&y="+r;
						}

						item = screenBase.get();
					} else {
						item = null;
					}
				}
				// change/empty cell
				else {
					item = array(SPACE);
					if (mode == SCREEN_MODE_EDIT) {
						CImg setting = new CImg("icons/setting.ico");
						array_push((CArray)item, BR(), new CLink(setting, action, "empty_change_link"));
					}
				}

				// align
				String halign = "def";
				String valign = "def";
				if(!"update".equals(Nest.value(_REQUEST, "form").asString())){
					if (Nest.value(screenitem,"halign").asInteger() == HALIGN_CENTER) {
						halign = "cntr";
					}
					if (Nest.value(screenitem,"halign").asInteger() == HALIGN_LEFT) {
						halign = "left";
					}
					if (Nest.value(screenitem,"halign").asInteger() == HALIGN_RIGHT) {
						halign = "right";
					}
					if (Nest.value(screenitem,"valign").asInteger() == VALIGN_MIDDLE) {
						valign = "mdl";
					}
					if (Nest.value(screenitem,"valign").asInteger() == VALIGN_TOP) {
						valign = "top";
					}
					if (Nest.value(screenitem,"valign").asInteger() == VALIGN_BOTTOM) {
						valign = "bttm";
					}
				}


				if (mode == SCREEN_MODE_EDIT && !isEditForm) {
					item = new CDiv(item, "draggable");
					((CDiv)item).setAttribute("id", "position_"+r+"_"+c);
					((CDiv)item).setAttribute("data-xcoord", c);
					((CDiv)item).setAttribute("data-ycoord", r);
				}

				// colspan/rowspan
				CCol newColumn = new CCol(item, halign+"_"+valign+" screenitem");
				if (!empty(Nest.value(screenitem,"colspan").$())) {
					newColumn.setColSpan(Nest.value(screenitem,"colspan").$());
				}
				if (!empty(Nest.value(screenitem,"rowspan").$())) {
					newColumn.setRowSpan(Nest.value(screenitem,"rowspan").$());
				}
				array_push(newColumns, newColumn);
			}

			// action right cell
			if (mode == SCREEN_MODE_EDIT) {
				CImg icon = new CImg("images/general/minus.png", null, null, null, "pointer");
				String removeRowLink = null;
				if (emptyScreenRow) {
					removeRowLink = "javascript: location.href = \"screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+"&rmv_row="+r+"\";";
				} else {
					removeRowLink = "javascript: if (Confirm(\""+_("This screen-row is not empty. Delete it?")+"\")) {"+
						" location.href = \"screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+"&rmv_row="+r+"\"; }";
				}
				icon.addAction("onclick", removeRowLink);
				array_push(newColumns, new CCol(icon));
			}
			screenTable.addRow(new CRow(newColumns));
		}

		// action bottom row
		if (mode == SCREEN_MODE_EDIT) {
			CImg icon = new CImg("images/general/plus.png", null, null, null, "pointer");
			icon.addAction("onclick", "javascript: location.href = \"screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+"&add_row="+Nest.value(screen,"vsize").asString()+"\";");
			newColumns = array(new CCol(icon));

			for (int i = 0; i < Nest.value(screen,"hsize").asInteger(); i++) {
				icon = new CImg("images/general/minus.png", null, null, null, "pointer");
				String removeColumnLink = null;
				if (isset(emptyScreenColumns,i)) {
					removeColumnLink = "javascript: if (Confirm(\""+_("This screen-column is not empty. Delete it?")+"\")) {"+
						" location.href = \"screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+"&rmv_col="+i+"\"; }";
				} else {
					removeColumnLink = "javascript: location.href = \"screenedit.action?config=1&screenid="+Nest.value(screen,"screenid").asString()+"&rmv_col="+i+"\";";
				}
				icon.addAction("onclick", removeColumnLink);

				array_push(newColumns, new CCol(icon));
			}

			array_push(newColumns, new CCol(new CImg("images/general/zero.png", "zero", 1, 1)));
			screenTable.addRow(newColumns);
		}

		return screenTable;
	}
	
	/**
	 * Insert javascript to create scroll in time control.
	 *
	 * @static
	 *
	 * @param array options
	 * @param array options['timeline']
	 * @param string options['profileIdx']
	 */
	public static void insertScreenScrollJs(IIdentityBean idBean, SQLExecutor executor) {
		insertScreenScrollJs(idBean, executor, map());
	}
	
	public static void insertScreenScrollJs(IIdentityBean idBean, SQLExecutor executor, Map options) {
		Nest.value(options,"timeline").$(empty(Nest.value(options,"timeline").$()) ? "" : Nest.value(options,"timeline").$());
		Nest.value(options,"profileIdx").$(empty(Nest.value(options,"profileIdx").$()) ? "" : Nest.value(options,"profileIdx").$());

		Map timeControlData = map(
			"id", "scrollbar",
			"loadScroll", 1,
			"mainObject", 1,
			"periodFixed", CProfile.get(idBean, executor, options.get("profileIdx")+".timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);

		rda_add_post_js("timeControl.addObject(\"scrollbar\", "+rda_jsvalue(Nest.value(options,"timeline").$())+", "+rda_jsvalue(timeControlData)+");");
	}


	/**
	 * Insert javascript to make time control synchronizes with NOW!
	 *
	 * @static
	 */
	public static void insertScreenRefreshTimeJs() {
		rda_add_post_js("timeControl.useTimeRefresh("+CWebUser.get("refresh")+");");
	}

	/**
	 * Insert javascript to init screens.
	 *
	 * @static
	 *
	 * @param string screenid
	 */
	public static void insertInitScreenJs(String screenid) {
		rda_add_post_js("init_screen(\""+screenid+"\", \"iframe\", \""+screenid+"\");");
	}

	/**
	 * Insert javascript to start time control rendering.
	 *
	 * @static
	 */
	public static void insertProcessObjectsJs() {
		rda_add_post_js("timeControl.processObjects();");
	}

	/**
	 * Insert javascript to clean all screen items.
	 *
	 * @static
	 */
	public static void insertScreenCleanJs() {
		rda_add_post_js("window.flickerfreeScreen.cleanAll();");
	}

	/**
	 * Insert javascript for standard screens.
	 *
	 * @param array options
	 * @param array options['timeline']
	 * @param string options['profileIdx']
	 *
	 * @static
	 */
	public static void insertScreenStandardJs(IIdentityBean idBean, SQLExecutor executor) {
		insertScreenStandardJs(idBean, executor, map());
	}
	
	/**
	 * Insert javascript for standard screens.
	 *
	 * @param array options
	 * @param array options['timeline']
	 * @param string options['profileIdx']
	 *
	 * @static
	 */
	public static void insertScreenStandardJs(IIdentityBean idBean, SQLExecutor executor, Map options) {
		insertScreenScrollJs(idBean, executor, options);
		insertScreenRefreshTimeJs();
		insertProcessObjectsJs();
	}

	
}
