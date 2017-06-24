package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.DHOST_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.DSVC_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_port2str;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringDiscovery extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget discoveryWidget = new CWidget("hat_discovery");

		// create header form
		CForm discoveryHeaderForm = new CForm("get");
		discoveryHeaderForm.setName("slideHeaderForm");
		discoveryHeaderForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());

		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CComboBox discoveryRulesComboBox = pageFilter.getDiscoveryCB();

		discoveryHeaderForm.addItem(array(_("Discovery rule")+SPACE, discoveryRulesComboBox));
		discoveryWidget.addHeader(SPACE, discoveryHeaderForm);
		
		// create table
		CTableInfo discoveryTable = new CTableInfo(_("No discovered devices found."));
		String discoverClass = discoveryTable.getAttribute("class").toString();
		discoverClass += " table_discovery";
		discoveryTable.setAttribute("class", discoverClass);
		discoveryTable.makeVerticalRotation();

		CArray header = array(
			make_sorting_header(_("Discovered device"), "ip"),
			new CCol(_("Monitored host"), "left"),
			new CCol(array(_("Uptime")+"/", _("Downtime")), "left")
		);

		for (Object name : Nest.value(data,"services").asCArray().keySet()) {
			header.add(new CCol(name, "vertical_rotation"));
		}
		discoveryTable.setHeader(header, "header");

		for (Map drule : (CArray<Map>)Nest.value(data,"drules").asCArray()) {
			CArray<Map> discovery_info = array();

			String primary_ip = null;
			CArray<Map> dhosts = Nest.value(drule,"dhosts").asCArray();
			String styleclass = null;
			String time = null;
			for(Map dhost : dhosts) {
				String hclass = null;
				Integer htime = null;
				if (Nest.value(dhost,"status").asInteger() == DHOST_STATUS_DISABLED) {
					hclass  = "disabled";
					htime  = Nest.value(dhost,"lastdown").asInteger();
				} else {
					hclass = "enabled";
					htime = Nest.value(dhost,"lastup").asInteger();
				}

				// primary_ip stores the primary host ip of the dhost
				if (isset(primary_ip)) {
					primary_ip = null;
				}

				CArray<Map> dservices = Nest.value(data,"dhosts",dhost.get("dhostid"),"dservices").asCArray();
				for(Map dservice : dservices) {
					dservice = Nest.value(data,"dservices",dservice.get("dserviceid")).asCArray();

					String hostName = "";

					Map host = reset((CArray<Map>)Nest.value(data,"dservices",dservice.get("dserviceid"),"hosts").asCArray());
					if (!is_null(host)) {
						hostName = Nest.value(host,"name").asString();
					}

					String htype = null;
					if (isset(primary_ip)) {
						if (primary_ip.equals(Nest.value(dservice,"ip").asString())) {
							htype  = "primary";
						} else {
							htype = "slave";
						}
					} else {
						primary_ip = Nest.value(dservice,"ip").asString();
						htype = "primary";
					}

					if (!isset(discovery_info,dservice.get("ip"))) {
						Nest.value(discovery_info,dservice.get("ip")).$(map(
							"ip", Nest.value(dservice,"ip").$(),
							"dns", Nest.value(dservice,"dns").$(),
							"type", htype,
							"class", hclass,
							"host", hostName,
							"time", htime,
							"druleid", dhost.get("druleid")
						));
					}

					styleclass = "active";
					time  = "lastup";
					if (Nest.value(dservice,"status").asInteger() == DSVC_STATUS_DISABLED) {
						styleclass = "inactive";
						time = "lastdown";
					}

					Object key_ = Nest.value(dservice,"key_").$();
					if (!rda_empty(key_)) {
						if (isset(Nest.value(data,"macros",key_).$())) {
							key_ = Nest.value(data,"macros",key_,"value").$();
						}
						key_ = NAME_DELIMITER+key_;
					}

					String serviceName = discovery_check_type2str(Nest.value(dservice,"type").asInteger())+discovery_port2str(Nest.value(dservice,"type").asInteger(), Nest.value(dservice,"port").asInteger())+key_;

					Nest.value(discovery_info,dservice.get("ip"),"services",serviceName).$(map(
						"class", styleclass,
						"time", dservice.get(time)
					));
				}
			}

			if (empty(Nest.value(data,"druleid").$()) && !empty(discovery_info)) {
				CCol col = new CCol(array(bold(Nest.value(drule,"name").asString()), SPACE+"("+_n("%d device", "%d devices", count(discovery_info))+")"));
				col.setColSpan(count(Nest.value(data,"services").$()) + 3);
				discoveryTable.addRow(array(col));
			}
			order_result(discovery_info, Nest.value(data,"sort").asString(), Nest.value(data,"sortorder").asString());

			for (Entry<Object, Map> e : discovery_info.entrySet()) {
			    Object ip = e.getKey();
			    Map h_data = e.getValue();
				String dns = Nest.value(h_data,"dns").$().equals("") ? "" : " ("+h_data.get("dns")+")";
				CArray row = array(
					"primary".equals(Nest.value(h_data,"type").$()) ? new CSpan(ip+dns, Nest.value(h_data,"class").asString()) : new CSpan(SPACE+SPACE+ip+dns),
					new CSpan(empty(Nest.value(h_data,"host").$()) ? "-" : Nest.value(h_data,"host").$()),
					new CSpan(((Nest.value(h_data,"time").asLong() == 0 || "slave".equals(Nest.value(h_data,"type").$()))
						? ""
						: convert_units(map("value", time() - Nest.value(h_data,"time").asInteger(), "units", "uptime"))), Nest.value(h_data,"class").asString())
				);

				for (Object name : Nest.value(data,"services").asCArray().keySet()) {
					styleclass = null;
					time = SPACE;
					CDiv hint = new CDiv(SPACE, styleclass);

					CTableInfo hintTable = null;
					if (isset(Nest.value(h_data,"services",name).$())) {
						styleclass = Nest.value(h_data,"services",name,"class").asString();
						time = Nest.value(h_data,"services",name,"time").asString();

						hintTable = new CTableInfo();
						hintTable.setAttribute("style", "width: auto;");

						if ("active".equals(styleclass)) {
							hintTable.setHeader(_("Uptime"));
						} else if ("inactive".equals(styleclass)) {
							hintTable.setHeader(_("Downtime"));
						}
						CCol timeColumn = new CCol(rda_date2age(Nest.value(h_data,"services",name,"time").asLong()), styleclass);
						hintTable.addRow(timeColumn);
					}
					CCol column = new CCol(hint, styleclass);
					if (!is_null(hintTable)) {
						column.setHint(hintTable);
					}
					row.add(column);
				}
				discoveryTable.addRow(row);
			}
		}

		discoveryWidget.addItem(discoveryTable);
		return discoveryWidget;
	}

}
