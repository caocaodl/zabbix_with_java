package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._page;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.FuncsUtil.show_message;
import static com.isoft.iradar.inc.FuncsUtil.rdaAddSecondsToUnixtime;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CScreenItemGet;
import com.isoft.iradar.model.params.CTemplateScreenItemGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CScreenBase {
	
	/**
	 * @see CScreenBuilder::isFlickerfree
	 */
	public Boolean isFlickerfree;

	/**
	 * Page file.
	 * @var string
	 */
	public String pageFile;

	/**
	 * @see CScreenBuilder::mode
	 */
	public Integer mode;

	/**
	 * @see CScreenBuilder::timestamp
	 */
	public Long timestamp;

	/**
	 * Resource (screen) type
	 * @var int
	 */
	public Integer resourcetype;

	/**
	 * Screen id
	 * @var int
	 */
	public Long screenid;

	/**
	 * Screen item data
	 * @var array
	 */
	public Map screenitem;

	/**
	 * Action
	 * @var string
	 */
	public String action;

	/**
	 * Group id
	 * @var int
	 */
	public Long groupid;

	/**
	 * Host id
	 * @var int
	 */
	public Long hostid;

	/**
	 * Time control timeline
	 * @var array
	 */
	public Map timeline;

	/**
	 * @see CScreenBuilder::profileIdx
	 */
	public String profileIdx;

	/**
	 * @see CScreenBuilder::profileIdx2
	 */
	public Integer profileIdx2;

	/**
	 * @see CScreenBuilder::updateProfile
	 */
	public Boolean updateProfile;

	/**
	 * Time control dom element id
	 *
	 * @var string
	 */
	public String dataId;

	protected SQLExecutor executor;
	
	protected IIdentityBean idBean;
	
	public CScreenBase(IIdentityBean idBean, SQLExecutor executor){
		this(idBean, executor, array());
	}
	
	/**
	 * Init screen data.
	 *
	 * @param array		options
	 * @param boolean	options["isFlickerfree"]
	 * @param string	options["pageFile"]
	 * @param int		options["mode"]
	 * @param int		options["timestamp"]
	 * @param int		options["resourcetype"]
	 * @param int		options["screenid"]
	 * @param array		options["screenitem"]
	 * @param string	options["action"]
	 * @param int		options["groupid"]
	 * @param int		options["hostid"]
	 * @param int		options["period"]
	 * @param int		options["stime"]
	 * @param string	options["profileIdx"]
	 * @param int		options["profileIdx2"]
	 * @param boolean	options["updateProfile"]
	 * @param array		options["timeline"]
	 * @param string	options["dataId"]
	 */
	public CScreenBase(IIdentityBean idBean, SQLExecutor executor, Map options) {
		this.idBean = idBean;
		this.executor = executor;
		this.isFlickerfree = isset(Nest.value(options,"isFlickerfree").$()) ? Nest.value(options,"isFlickerfree").asBoolean() : true;
		this.mode = isset(Nest.value(options,"mode").$()) ? Nest.value(options,"mode").asInteger() : SCREEN_MODE_SLIDESHOW;
		this.timestamp = !empty(Nest.value(options,"timestamp").$()) ? Nest.value(options,"timestamp").asLong() : time();
		this.resourcetype = isset(Nest.value(options,"resourcetype").$()) ? Nest.value(options,"resourcetype").asInteger() : null;
		this.screenid = !empty(Nest.value(options,"screenid").$()) ? Nest.value(options,"screenid").asLong() : null;
		this.action = !empty(Nest.value(options,"action").$()) ? Nest.value(options,"action").asString() : null;
		this.groupid = !empty(Nest.value(options,"groupid").$()) ? Nest.value(options,"groupid").asLong() : null;
		this.hostid = !empty(Nest.value(options,"hostid").$()) ? Nest.value(options,"hostid").asLong() : null;
		this.dataId = !empty(Nest.value(options,"dataId").$()) ? Nest.value(options,"dataId").asString() : null;

		// get page file
		if (!empty(Nest.value(options,"pageFile").$())) {
			this.pageFile = Nest.value(options,"pageFile").asString();
		} else {
			this.pageFile = Nest.value(_page(),"file").asString();
		}

		// calculate timeline
		this.profileIdx = !empty(Nest.value(options,"profileIdx").$()) ? Nest.value(options,"profileIdx").asString() : "";
		this.profileIdx2 = !empty(Nest.value(options,"profileIdx2").$()) ? Nest.value(options,"profileIdx2").asInteger() : null;
		this.updateProfile = isset(Nest.value(options,"updateProfile").$()) ? Nest.value(options,"updateProfile").asBoolean() : true;
		this.timeline = !empty(Nest.value(options,"timeline").$()) ? Nest.value(options,"timeline").asCArray() : null;
		if (empty(this.timeline)) {
			this.timeline = calculateTime(idBean, executor, map(
				"profileIdx" , this.profileIdx,
				"profileIdx2" , this.profileIdx2,
				"updateProfile" , this.updateProfile,
				"period" , !empty(Nest.value(options,"period").$()) ? Nest.value(options,"period").$() : null,
				"stime" , !empty(Nest.value(options,"stime").$()) ? Nest.value(options,"stime").$() : null
			));
		}

		// get screenitem
		if (!empty(Nest.value(options,"screenitem").$())) {
			this.screenitem = Nest.value(options,"screenitem").asCArray();
		} else if (!empty(Nest.value(options,"screenitemid").$())) {
			CArray<Map> screenitems = null;
			if (!empty(this.hostid)) {
				CTemplateScreenItemGet tsoptions = new CTemplateScreenItemGet();
				tsoptions.setScreenItemIds(Nest.value(options, "screenitemid").asLong());
				tsoptions.setHostIds(this.hostid);
				tsoptions.setOutput(API_OUTPUT_EXTEND);
				screenitems = API.TemplateScreenItem(idBean, executor).get(tsoptions);
			} else {
				CScreenItemGet sioptions = new CScreenItemGet();
				sioptions.setScreenitemIds(Nest.value(options, "screenitemid").asLong());
				sioptions.setOutput(API_OUTPUT_EXTEND);
				screenitems = API.ScreenItem(idBean, executor).get(sioptions);
			}
			this.screenitem = reset(screenitems);
		}

		// get screenid
		if (empty(this.screenid) && !empty(this.screenitem)) {
			this.screenid = Nest.value(this.screenitem,"screenid").asLong();
		}

		// get resourcetype
		if (is_null(this.resourcetype) && !empty(Nest.value(this.screenitem,"resourcetype").$())) {
			this.resourcetype = Nest.value(this.screenitem,"resourcetype").asInteger();
		}

		// create action url
		if (empty(this.action)) {
			this.action = "screenedit.action?form=update&screenid="+this.screenid+"&screenitemid="+Nest.value(this.screenitem,"screenitemid").$();
		}
	}
	
	/**
	 * Create and get unique screen id for time control.
	 * @return string
	 */
	public String getDataId() {
		if (empty(this.dataId)) {
			this.dataId = !empty(this.screenitem) ? Nest.value(this.screenitem,"screenitemid").asString()+"_"+Nest.value(this.screenitem,"screenid").asString() : "1";
		}
		return this.dataId;
	}

	/**
	 * Get unique screen container id.
	 * @return string
	 */
	public String getScreenId() {
		return "flickerfreescreen_"+getDataId();
	}

	/**
	 * Get profile url params.
	 * @return string
	 */
	public String getProfileUrlParams() {
		return "&updateProfile="+(this.updateProfile?1:0)+"&profileIdx="+this.profileIdx+"&profileIdx2="+this.profileIdx2;
	}
		
	/**
	 * Get enveloped screen inside container.
	 * @return CDiv
	 */
	protected CDiv getOutput() {
		return getOutput(null);
	}
	
	/**
	 * Get enveloped screen inside container.
	 *
	 * @param object	item
	 *
	 * @return CDiv
	 */
	protected CDiv getOutput(Object item) {
		return getOutput(item, true);
	}
	
	/**
	 * Get enveloped screen inside container.
	 *
	 * @param object	item
	 * @param boolean	insertFlickerfreeJs
	 *
	 * @return CDiv
	 */
	protected CDiv getOutput(Object item, boolean insertFlickerfreeJs) {
		return getOutput(item, insertFlickerfreeJs, array());
	}

	/**
	 * Get enveloped screen inside container.
	 *
	 * @param object	item
	 * @param boolean	insertFlickerfreeJs
	 * @param array		flickerfreeData
	 *
	 * @return CDiv
	 */
	protected CDiv getOutput(Object item, boolean insertFlickerfreeJs, Map flickerfreeData) {
		if (insertFlickerfreeJs) {
			insertFlickerfreeJs(flickerfreeData);
		}

		CDiv div = null;
		if (mode == SCREEN_MODE_EDIT) {
			CImg setting = new CImg("icons/setting.ico");
			div = new CDiv(array(item, BR(), new CLink(setting, action)), "flickerfreescreen", getScreenId());
		} else {
			div = new CDiv(item, "flickerfreescreen", getScreenId());
		}

		div.setAttribute("data-timestamp", timestamp);
		div.addStyle("position: relative;");

		return div;
	}

	
	/**
	 * Insert javascript flicker-free screen data.
	 */
	public void insertFlickerfreeJs() {
		insertFlickerfreeJs(array());
	}

	/**
	 * Insert javascript flicker-free screen data.
	 * @param array data
	 */
	public void insertFlickerfreeJs(Map data) {
		Map jsData = map(
			"id" , getDataId(),
			"isFlickerfree" , this.isFlickerfree,
			"pageFile" , this.pageFile,
			"resourcetype" , this.resourcetype,
			"mode" , this.mode,
			"timestamp" , this.timestamp,
			"interval" , CWebUser.get("refresh"),
			"screenitemid" , !empty(Nest.value(this.screenitem,"screenitemid").$()) ? Nest.value(this.screenitem,"screenitemid").$() : null,
			"screenid" , !empty(Nest.value(this.screenitem,"screenid").$()) ? Nest.value(this.screenitem,"screenid").$() : this.screenid,
			"groupid" , this.groupid,
			"hostid" , this.hostid,
			"timeline" , this.timeline,
			"profileIdx" , this.profileIdx,
			"profileIdx2" , this.profileIdx2,
			"updateProfile" , this.updateProfile,
			"data" , !empty(data) ? data : null
		);
		rda_add_post_js("window.flickerfreeScreen.add("+rda_jsvalue(jsData)+");");
	}
	
	/**
	 * Insert javascript flicker-free screen data.
	 *
	 * @static
	 *
	 * @param array		options
	 * @param string		options["profileIdx"]
	 * @param int			options["profileIdx2"]
	 * @param boolean	options["updateProfile"]
	 * @param int			options["period"]
	 * @param string		options["stime"]
	 *
	 * @return array
	 */
	public static Map calculateTime(IIdentityBean idBean, SQLExecutor executor) {
		return calculateTime(idBean, executor, array());
	}
	
	/**
	 * Insert javascript flicker-free screen data.
	 *
	 * @static
	 *
	 * @param array		options
	 * @param string		options["profileIdx"]
	 * @param int			options["profileIdx2"]
	 * @param boolean	options["updateProfile"]
	 * @param int			options["period"]
	 * @param string		options["stime"]
	 *
	 * @return array
	 */
	public static Map calculateTime(IIdentityBean idBean, SQLExecutor executor, Map options) {
		if (!array_key_exists("updateProfile", options)) {
			Nest.value(options,"updateProfile").$(true);
		}
		if (empty(Nest.value(options,"profileIdx2").$())) {
			Nest.value(options,"profileIdx2").$(0);
		}

		// show only latest data without update is set only period
		if (!empty(Nest.value(options,"period").$()) && empty(Nest.value(options,"stime").$())) {
			Nest.value(options,"updateProfile").$(false);
			Nest.value(options,"profileIdx").$("");
		}

		// period
		if (empty(Nest.value(options,"period").$())) {
			Nest.value(options,"period").$(!empty(Nest.value(options,"profileIdx").$())
					? CProfile.get(idBean, executor, options.get("profileIdx")+".period", RDA_PERIOD_DEFAULT, Nest.value(options,"profileIdx2").asLong())
					: RDA_PERIOD_DEFAULT);
		} else {
			if (Nest.value(options,"period").asLong() < RDA_MIN_PERIOD) {
				show_message(_n("Minimum time period to display is %1$s hour.",
						"Minimum time period to display is %1$s hours.", (int)(RDA_MIN_PERIOD / SEC_PER_HOUR)));
				Nest.value(options,"period").$(RDA_MIN_PERIOD);
			} else if (Nest.value(options,"period").asLong() > RDA_MAX_PERIOD) {
				show_message(_n("Maximum time period to display is %1$s day.",
						"Maximum time period to display is %1$s days.", (int)(RDA_MAX_PERIOD / SEC_PER_DAY)));
				Nest.value(options,"period").$(RDA_MAX_PERIOD);
			}
		}
		if (Nest.value(options,"updateProfile").asBoolean() && !empty(Nest.value(options,"profileIdx").$())) {
			CProfile.update(idBean, executor, options.get("profileIdx")+".period", Nest.value(options,"period").$(), PROFILE_TYPE_INT, Nest.value(options,"profileIdx2").asLong());
		}

		// stime
		long time = time();
		String usertime = null;
		String stimeNow = null;
		Integer isNow = null;

		if (!empty(Nest.value(options,"stime").$())) {
			long stimeUnix = rdaDateToTime(Nest.value(options,"stime").asString());

			if (stimeUnix > time || rdaAddSecondsToUnixtime(Nest.value(options,"period").asLong(), stimeUnix) > time) {
				stimeNow = Nest.value(options,"stime").asString();
				Nest.value(options,"stime").$(date(TIMESTAMP_FORMAT, time - Nest.value(options,"period").asLong()));
				usertime = date(TIMESTAMP_FORMAT, time);
				isNow = 1;
			} else {
				usertime = date(TIMESTAMP_FORMAT, rdaAddSecondsToUnixtime(Nest.value(options,"period").asLong(), stimeUnix));
				isNow = 0;
			}

			if (Nest.value(options,"updateProfile").asBoolean() && !empty(Nest.value(options,"profileIdx").$())) {
				CProfile.update(idBean, executor, options.get("profileIdx")+".stime", Nest.value(options,"stime").$(), PROFILE_TYPE_STR, Nest.value(options,"profileIdx2").asLong());
				CProfile.update(idBean, executor, options.get("profileIdx")+".isnow", isNow, PROFILE_TYPE_INT, Nest.value(options,"profileIdx2").asLong());
			}
		} else {
			if (!empty(Nest.value(options,"profileIdx").$())) {
				isNow = (Integer)CProfile.get(idBean, executor, options.get("profileIdx")+".isnow", null, Nest.value(options,"profileIdx2").asLong());
				if (!empty(isNow)) {
					Nest.value(options,"stime").$(date(TIMESTAMP_FORMAT, time - Nest.value(options,"period").asInteger()));
					usertime = date(TIMESTAMP_FORMAT, time);
					stimeNow = date(TIMESTAMP_FORMAT, rdaAddSecondsToUnixtime(SEC_PER_YEAR, Nest.value(options,"stime").$()));

					if (Nest.value(options,"updateProfile").asBoolean()) {
						CProfile.update(idBean, executor, options.get("profileIdx")+".stime", Nest.value(options,"stime").$(), PROFILE_TYPE_STR, Nest.value(options,"profileIdx2").asLong());
					}
				} else {
					Nest.value(options,"stime").$(CProfile.get(idBean, executor, options.get("profileIdx")+".stime", null, Nest.value(options,"profileIdx2").asLong()));
					usertime = date(TIMESTAMP_FORMAT, rdaAddSecondsToUnixtime(Nest.value(options,"period").asInteger(), Nest.value(options,"stime").$()));
				}
			}

			if (empty(Nest.value(options,"stime").$())) {
				Nest.value(options,"stime").$(date(TIMESTAMP_FORMAT, time - Nest.value(options,"period").asInteger()));
				usertime = date(TIMESTAMP_FORMAT, time);
				stimeNow = date(TIMESTAMP_FORMAT, rdaAddSecondsToUnixtime(SEC_PER_YEAR, Nest.value(options,"stime").$()));
				isNow = 1;

				if (Nest.value(options,"updateProfile").asBoolean() && !empty(Nest.value(options,"profileIdx").$())) {
					CProfile.update(idBean, executor, options.get("profileIdx")+".stime", Nest.value(options,"stime").$(), PROFILE_TYPE_STR, Nest.value(options,"profileIdx2").asLong());
					CProfile.update(idBean, executor, options.get("profileIdx")+".isnow", isNow, PROFILE_TYPE_INT, Nest.value(options,"profileIdx2").asLong());
				}
			}
		}

		return map(
			"period" , Nest.value(options,"period").$(),
			"stime" , Nest.value(options,"stime").$(),
			"stimeNow" , !empty(stimeNow) ? stimeNow : Nest.value(options,"stime").$(),
			"starttime" , date(TIMESTAMP_FORMAT, time - RDA_MAX_PERIOD),
			"usertime" , usertime,
			"isNow" , isNow
		);
	}
	
	public Object get() {
		return null;
	}
	

	/**
	 * Easy way to view time data.
	 *
	 * @static
	 *
	 * @param array	options
	 * @param int		options["period"]
	 * @param string	options["stime"]
	 * @param string	options["stimeNow"]
	 * @param string	options["starttime"]
	 * @param string	options["usertime"]
	 * @param int		options["isNow"]
	 */
	public static String debugTime() {
		return debugTime(array());
	}
	
	/**
	 * Easy way to view time data.
	 *
	 * @static
	 *
	 * @param array	options
	 * @param int		options["period"]
	 * @param string	options["stime"]
	 * @param string	options["stimeNow"]
	 * @param string	options["starttime"]
	 * @param string	options["usertime"]
	 * @param int		options["isNow"]
	 */
	public static String debugTime(Map time) {
		return "period="+rda_date2age(0, Nest.value(time,"period").asLong())+", ("+Nest.value(time,"period").$()+")<br/>"+
				"starttime="+date("F j, Y, g:i a", rdaDateToTime(Nest.value(time,"starttime").asString()))+", ("+Nest.value(time,"starttime").$()+")<br/>"+
				"stime="+date("F j, Y, g:i a", rdaDateToTime(Nest.value(time,"stime").asString()))+", ("+Nest.value(time,"stime").$()+")<br/>"+
				"stimeNow="+date("F j, Y, g:i a", rdaDateToTime(Nest.value(time,"stimeNow").asString()))+", ("+Nest.value(time,"stimeNow").$()+")<br/>"+
				"usertime="+date("F j, Y, g:i a", rdaDateToTime(Nest.value(time,"usertime").asString()))+", ("+Nest.value(time,"usertime").$()+")<br/>"+
				"isnow="+Nest.value(time,"isNow").$()+"<br/>";
	}

}
