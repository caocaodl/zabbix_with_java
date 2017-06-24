package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.helpers.CHtml.serialize;
import static com.isoft.iradar.Cphp.unserialize;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class SoundsUtil {
	
	private final static File scandir = new File(RadarContext.request().getSession(true).getServletContext().getRealPath("/platform/iradar/audio"));

	public static CArray<String> getSounds() {
		CArray soundList = array();		
		String[] sounds = scandir.list();
		for(String sound : sounds) {
			if (preg_match("^([\\w\\d_]+)\\.(wav|ogg)$", sound.toLowerCase())==0) {
				continue;
			}
			String[] parts = explode("\\.", sound);
			soundList.put(parts[0],parts[1]);
		}
		return soundList;
	}
	
	public static CArray getMessageSettings(IIdentityBean idBean, SQLExecutor executor) {
		CArray defSeverities = map(
			TRIGGER_SEVERITY_NOT_CLASSIFIED, 1,
			TRIGGER_SEVERITY_INFORMATION, 1,
			TRIGGER_SEVERITY_WARNING, 1,
			TRIGGER_SEVERITY_AVERAGE, 1,
			TRIGGER_SEVERITY_HIGH, 1,
			TRIGGER_SEVERITY_DISASTER, 1
		);

		CArray messages = map(
			"enabled", 0,
			"timeout", 60,
			"last.clock", 0,
			"triggers.recovery", 1,
			"triggers.severities", null,
			"sounds.mute", 0,
			"sounds.repeat", 1,
			"sounds.recovery", "alarm_ok.wav",
			"sounds."+TRIGGER_SEVERITY_NOT_CLASSIFIED, "no_sound.wav",
			"sounds."+TRIGGER_SEVERITY_INFORMATION, "alarm_information.wav",
			"sounds."+TRIGGER_SEVERITY_WARNING, "alarm_warning.wav",
			"sounds."+TRIGGER_SEVERITY_AVERAGE, "alarm_average.wav",
			"sounds."+TRIGGER_SEVERITY_HIGH, "alarm_high.wav",
			"sounds."+TRIGGER_SEVERITY_DISASTER, "alarm_disaster.wav"
		);

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbProfiles = DBselect(executor,
			"SELECT p.idx,p.source,p.value_str"+
			" FROM profiles p"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "profiles", "p")+
			    " AND p.userid="+sqlParts.marshalParam(CWebUser.get("userid"))+
				" AND "+sqlParts.dual.dbConditionString("p.idx", new String[]{"web.messages"}),
			sqlParts.getNamedParams()
		);
		for (Map profile : dbProfiles) {
			messages.put(profile.get("source"), profile.get("value_str"));
		}

		if (is_null(messages.get("triggers.severities"))) {
			messages.put("triggers.severities",defSeverities);
		} else {
			messages.put("triggers.severities",unserialize(Nest.value(messages,"triggers.severities").asString()));
		}
		return messages;
	}
	
	public static Object updateMessageSettings(IIdentityBean idBean, SQLExecutor executor, Map<String,Object> messages) {
		if (!isset(messages,"enabled")) {
			Nest.value(messages,"enabled").$(0);
		}
		if (isset(messages,"triggers.severities")) {
			messages.put("triggers.severities", serialize(messages.get("triggers.severities")));
		}
		
		CArray<Map> dbMessages = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbProfiles = DBselect(executor,
			"SELECT p.profileid,p.idx,p.source,p.value_str"+
			" FROM profiles p"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "profiles", "p")+
			    " AND p.userid="+sqlParts.marshalParam(CWebUser.get("userid"))+
				" AND "+sqlParts.dual.dbConditionString("p.idx", new String[]{"web.messages"}),
			sqlParts.getNamedParams()
		);
		for (Map profile : dbProfiles) {
			Nest.value(profile,"value").$(Nest.value(profile,"value_str").$());
			dbMessages.put(profile.get("source"), profile);
		}

		CArray<Map> inserts = array();
		CArray<Map> updates = array();

		for (Entry<String, Object> e : messages.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			Map values = map(
				"userid", CWebUser.get("userid"),
				"idx", "web.messages",
				"source", key,
				"value_str",  value,
				"type", PROFILE_TYPE_STR
			);

			if (!isset(dbMessages, key)) {
				inserts.add(values);
			} else if (!value.equals(Nest.value(dbMessages,key,"value").$())) {
				updates.add(map(
					"values", values,
					"where", map("profileid", Nest.value(dbMessages,key,"profileid").$())
				));
			}
		}

		try {
			CDB.insert(idBean, executor, "profiles", inserts);
			CDB.update(idBean, executor, "profiles", updates);
		} catch (APIException e) {
			error(e.getMessage());
		}
		return messages;
	}
	
}
