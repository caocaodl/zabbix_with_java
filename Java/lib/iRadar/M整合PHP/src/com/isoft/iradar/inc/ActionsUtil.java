package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.$_REQUEST;
import static com.isoft.iradar.Cphp.SORT_ASC;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_multisort;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.core.utils.EasyObject.asLong;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.ALERT_MAX_RETRIES;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_FAILED;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_NOT_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_TYPE_COMMAND;
import static com.isoft.iradar.inc.Defines.ALERT_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_IN;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LESS_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_MORE_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_NOT_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_NOT_IN;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_NOT_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_APPLICATION;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DCHECK;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DHOST_IP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DOBJECT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DRULE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_PORT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSTATUS;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DUPTIME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DVALUE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_METADATA;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_PROXY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TIME_PERIOD;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_SEVERITY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_VALUE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_ITEM_NORMAL;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_ITEM_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_LLDRULE_NORMAL;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_LLDRULE_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_TRIGGER_NORMAL;
import static com.isoft.iradar.inc.Defines.EVENT_TYPE_TRIGGER_UNKNOWN;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_COMMAND;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_DISABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ENABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_REMOVE;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_GLOBAL_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.SHORT_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_object_status2str;
import static com.isoft.iradar.inc.DiscoveryUtil.get_discovery_rule_by_druleid;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.italic;
import static com.isoft.iradar.inc.TranslateDefines.EVENT_ACTION_CMDS_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.EVENT_ACTION_MESSAGES_DATE_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("blue.2.2.5")
public class ActionsUtil {
	
	@CodeConfirmed("blue.2.2.5")
	public static String condition_operator2str(int operator) {
		switch (operator) {
			case CONDITION_OPERATOR_EQUAL:
				return "=";
			case CONDITION_OPERATOR_NOT_EQUAL:
				return "<>";
			case CONDITION_OPERATOR_LIKE:
				return _("like");
			case CONDITION_OPERATOR_NOT_LIKE:
				return _("not like");
			case CONDITION_OPERATOR_IN:
				return _("in");
			case CONDITION_OPERATOR_MORE_EQUAL:
				return ">=";
			case CONDITION_OPERATOR_LESS_EQUAL:
				return "<=";
			case CONDITION_OPERATOR_NOT_IN:
				return _("not in");
			default:
				return _("Unknown");
		}
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static String condition_type2str(int conditionType) {
		switch (conditionType) {
		case CONDITION_TYPE_TRIGGER_VALUE:
			return _("Trigger value");
		case CONDITION_TYPE_MAINTENANCE:
			return _("Maintenance status");
		case CONDITION_TYPE_TRIGGER_NAME:
			return _("Trigger name");
		case CONDITION_TYPE_TRIGGER_SEVERITY:
			return _("Trigger severity");
		case CONDITION_TYPE_TRIGGER:
			return _("Trigger");
		case CONDITION_TYPE_HOST_NAME:
			return _("Host name");
		case CONDITION_TYPE_HOST_GROUP:
			return _("Host group");
		case CONDITION_TYPE_TEMPLATE:
			return _("Template");
		case CONDITION_TYPE_HOST:
			return _("Host");
		case CONDITION_TYPE_TIME_PERIOD:
			return _("Time period");
		case CONDITION_TYPE_DRULE:
			return _("Discovery rule");
		case CONDITION_TYPE_DCHECK:
			return _("Discovery check");
		case CONDITION_TYPE_DOBJECT:
			return _("Discovery object");
		case CONDITION_TYPE_DHOST_IP:
			return _("Host IP");
		case CONDITION_TYPE_DSERVICE_TYPE:
			return _("Service type");
		case CONDITION_TYPE_DSERVICE_PORT:
			return _("Service port");
		case CONDITION_TYPE_DSTATUS:
			return _("Discovery status");
		case CONDITION_TYPE_DUPTIME:
			return _("Uptime/Downtime");
		case CONDITION_TYPE_DVALUE:
			return _("Received value");
		case CONDITION_TYPE_EVENT_ACKNOWLEDGED:
			return _("Event acknowledged");
		case CONDITION_TYPE_APPLICATION:
			return _("Application");
		case CONDITION_TYPE_PROXY:
			return _("Proxy");
		case CONDITION_TYPE_EVENT_TYPE:
			return _("Event type");
		case CONDITION_TYPE_HOST_METADATA:
			return _("Host metadata");
		default:
			return _("Unknown");
		}
	}

	@CodeConfirmed("blue.2.2.5")
	public static CArray<String> discovery_object2str() {
		CArray<String> discoveryObjects = map(
				EVENT_OBJECT_DHOST, _("Device"),
				EVENT_OBJECT_DSERVICE, _("Service")
			);
		return discoveryObjects;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static String discovery_object2str(int object) {
		CArray<String> discoveryObjects = map(
				EVENT_OBJECT_DHOST, _("Device"),
				EVENT_OBJECT_DSERVICE, _("Service")
			);
		if (discoveryObjects.containsKey(object)) {
			return discoveryObjects.get(object);
		} else {
			return _("Unknown");
		}
	}

	@CodeConfirmed("blue.2.2.5")
	public static String condition_value2str(IIdentityBean idBean, SQLExecutor executor, int conditiontype, String value) {
		Long lvalue = asLong(value);
		String str_val;
		switch (conditiontype) {
			case CONDITION_TYPE_HOST_GROUP:
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setGroupIds(lvalue);
				hgoptions.setOutput(new String[]{"name"});
				hgoptions.setLimit(1);
				CArray<Map> groups = API.HostGroup(idBean, executor).get(hgoptions);
	
				if (!empty(groups)) {
					Map group = reset(groups);
					str_val = Nest.value(group,"name").asString();
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_TRIGGER:
				CTriggerGet toptions = new CTriggerGet();
				toptions.setTriggerIds(lvalue);
				toptions.setExpandDescription(true);
				toptions.setOutput(new String[]{"description"});
				toptions.setSelectHosts(new String[]{"name"});
				toptions.setLimit(1);
				CArray<Map> trigs = API.Trigger(idBean, executor).get(toptions);
	
				if (!empty(trigs)) {
					Map trig = reset(trigs);
					Map host = reset(Nest.value(trig,"hosts").asCArray());
	
					str_val = host.get("name")+NAME_DELIMITER+Nest.value(trig,"description").asString();
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_HOST:
			case CONDITION_TYPE_TEMPLATE:
				Map host;
				if (!empty(host = get_host_by_hostid(idBean, executor, lvalue))) {
					str_val = Nest.value(host,"name").asString();
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_TRIGGER_NAME:
			case CONDITION_TYPE_HOST_METADATA:
			case CONDITION_TYPE_HOST_NAME:
				str_val = value;
				break;
			case CONDITION_TYPE_TRIGGER_VALUE:
				str_val = trigger_value2str(lvalue.intValue());
				break;
			case CONDITION_TYPE_TRIGGER_SEVERITY:
				str_val = getSeverityCaption(idBean, executor, lvalue.intValue());
				break;
			case CONDITION_TYPE_TIME_PERIOD:
				str_val = value;
				break;
			case CONDITION_TYPE_MAINTENANCE:
				str_val = _("maintenance");
				break;
			case CONDITION_TYPE_DRULE:
				Map _drule;
				if (!empty(_drule = get_discovery_rule_by_druleid(idBean, executor, lvalue.toString()))) {
					str_val = Nest.value(_drule,"name").asString();
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_DCHECK:
				SqlBuilder sqlParts = new SqlBuilder();
				Map _row = DBfetch(DBselect(executor,
						"SELECT dr.name,c.dcheckid,c.type,c.key_,c.ports"+
						" FROM drules dr,dchecks c"+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "drules", "dr")+
						    " AND dr.druleid=c.druleid"+
							" AND c.dcheckid="+sqlParts.marshalParam(lvalue),
						sqlParts.getNamedParams()
				));
				if (!empty(_row)) {
					str_val = _row.get("name")+NAME_DELIMITER+discovery_check2str(Nest.value(_row,"type").asInteger(), Nest.value(_row,"key_").asString(), Nest.value(_row,"ports").asString());
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_DOBJECT:
				str_val = discovery_object2str(lvalue.intValue());
				break;
			case CONDITION_TYPE_PROXY:
				if (!empty(host = get_host_by_hostid(idBean, executor, lvalue))) {
					str_val = Nest.value(host,"host").asString();
				} else {
					return _("Unknown");
				}
				break;
			case CONDITION_TYPE_DHOST_IP:
				str_val = value;
				break;
			case CONDITION_TYPE_DSERVICE_TYPE:
				str_val = discovery_check_type2str(lvalue.intValue());
				break;
			case CONDITION_TYPE_DSERVICE_PORT:
				str_val = value;
				break;
			case CONDITION_TYPE_DSTATUS:
				str_val = discovery_object_status2str(lvalue.intValue());
				break;
			case CONDITION_TYPE_DUPTIME:
				str_val = value;
				break;
			case CONDITION_TYPE_DVALUE:
				str_val = value;
				break;
			case CONDITION_TYPE_EVENT_ACKNOWLEDGED:
				str_val = (!empty(lvalue)) ? _("Ack") : _("Not Ack");
				break;
			case CONDITION_TYPE_APPLICATION:
				str_val = value;
				break;
			case CONDITION_TYPE_EVENT_TYPE:
				str_val = eventType(lvalue.intValue());
				break;
			default:
				return _("Unknown");
		}
	
		return str_val;
	}
	
	/**
	 * Returns the HTML representation of an action condition.
	 *
	 * @param _conditiontype
	 * @param _operator
	 * @param _value
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray get_condition_desc(IIdentityBean idBean, SQLExecutor executor, int conditionType, int operator, String value){
		return array(condition_type2str(conditionType), 
				SPACE,
				condition_operator2str(operator), 
				SPACE,
				condition_value2str(idBean, executor, conditionType, value)
		);
	}
	
	/**
	 * Generates array with HTML items representing operation with description
	 *
	 * @param int _type short or long description, use const. SHORT_DESCRIPTION and LONG_DESCRIPTION
	 * @param array _data
	 * @param int _data['operationtype'] type of operation: OPERATION_TYPE_MESSAGE, OPERATION_TYPE_COMMAND, ...
	 * @param int _data['opmessage']['mediatypeid'] type id of message media
	 * @param bool _data['opmessage']['default_msg'] should default message be used
	 * @param bool _data['opmessage']['operationid'] if true _data['operationid'] will be used to retrieve default messages from DB
	 * @param string _data['opmessage']['subject'] subject of message
	 * @param string _data['opmessage']['message'] message it self
	 * @param array _data['opmessage_usr'] list of user ids if OPERATION_TYPE_MESSAGE
	 * @param array _data['opmessage_grp'] list of group ids if OPERATION_TYPE_MESSAGE
	 * @param array _data['opcommand_grp'] list of group ids if OPERATION_TYPE_COMMAND
	 * @param array _data['opcommand_hst'] list of host ids if OPERATION_TYPE_COMMAND
	 * @param array _data['opgroup'] list of group ids if OPERATION_TYPE_GROUP_ADD or OPERATION_TYPE_GROUP_REMOVE
	 * @param array _data['optemplate'] list of template ids if OPERATION_TYPE_TEMPLATE_ADD or OPERATION_TYPE_TEMPLATE_REMOVE
	 * @param int _data['operationid'] id of operation
	 * @param int _data['opcommand']['type'] type of command: RDA_SCRIPT_TYPE_IPMI, RDA_SCRIPT_TYPE_SSH, ...
	 * @param string _data['opcommand']['command'] actual command
	 * @param int _data['opcommand']['scriptid'] script id used if _data['opcommand']['type'] is RDA_SCRIPT_TYPE_GLOBAL_SCRIPT
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray get_operation_descr(IIdentityBean idBean, SQLExecutor executor, int _type, Map _data) {
		CArray _result = array();
		
		String _mediatype;
		if (_type == SHORT_DESCRIPTION) {
			switch (Nest.value(_data,"operationtype").asInteger()) {
				case OPERATION_TYPE_MESSAGE:
					CMediaTypeGet mtoptions = new CMediaTypeGet();
					mtoptions.setMediaTypeIds(Nest.value(_data,"opmessage","mediatypeid").asLong());
					mtoptions.setOutput(new String[]{"description"});
					CArray<Map> _mediaTypes = API.MediaType(idBean, executor).get(mtoptions);
					if (empty(_mediaTypes)) {
						_mediatype = _("all media");
					} else {
						Map mediatype = reset(_mediaTypes);
						_mediatype = Nest.value(mediatype,"description").asString();
					}

					if (!empty(Nest.value(_data,"opmessage_usr").$())) {
						CUserGet uoptions = new CUserGet();
						uoptions.setUserIds(rda_objectValues(Nest.value(_data,"opmessage_usr").$(), "userid").valuesAsString());
						uoptions.setOutput(new String[]{"userid", "alias", "name", "surname"});
						CArray<Map> _users = API.User(idBean, executor).get(uoptions);
						order_result(_users, "alias");

						CArray _fullnames = array();
						for(Map _user: _users) {
							_fullnames.add(getUserFullname(_user));
						}

						_result.add( bold(_("Send message to users")+NAME_DELIMITER) );
						_result.add( array(implode(", ", _fullnames), SPACE, _("via"), SPACE, _mediatype) );
						_result.add( BR() );
					}

					if (!empty(Nest.value(_data,"opmessage_grp").$())) {
						CUserGroupGet ugoptions = new CUserGroupGet();
						ugoptions.setUsrgrpIds(rda_objectValues(Nest.value(_data,"opmessage_grp").$(), "usrgrpid").valuesAsLong());
						ugoptions.setOutput(API_OUTPUT_EXTEND);
						CArray<Map> _usrgrps = API.UserGroup(idBean, executor).get(ugoptions);
						order_result(_usrgrps, "name");

						_result.add( bold(_("Send message to user groups")+NAME_DELIMITER) );
						_result.add( array(implode(", ", rda_objectValues(_usrgrps, "name")), SPACE, _("via"), SPACE, _mediatype) );
						_result.add( BR() );
					}
					break;
				case OPERATION_TYPE_COMMAND:
					if (!isset(Nest.value(_data,"opcommand_grp").$())) {
						Nest.value(_data,"opcommand_grp").$(array());
					}
					if (!isset(Nest.value(_data,"opcommand_hst").$())) {
						Nest.value(_data,"opcommand_hst").$(array());
					}

					CHostGet hoptions = new CHostGet();
					hoptions.setHostIds(rda_objectValues(Nest.value(_data,"opcommand_hst").$(), "hostid").valuesAsLong());
					hoptions.setOutput(new String[]{"hostid", "name"});
					CArray<Map> _hosts = API.Host(idBean, executor).get(hoptions);

					for(Map _cmd: (CArray<Map>)Nest.value(_data,"opcommand_hst").asCArray()) {
						if (Nest.value(_cmd,"hostid").asInteger() != 0) {
							continue;
						}

						_result.add( array(bold(_("Run remote commands on current host")), BR()) );
						break;
					}

					if (!empty(_hosts)) {
						order_result(_hosts, "name");

						_result.add( bold(_("Run remote commands on hosts")+NAME_DELIMITER) );
						_result.add( array(implode(", ", rda_objectValues(_hosts, "name")), BR()) );
					}

					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(rda_objectValues(Nest.value(_data,"opcommand_grp").$(), "groupid").valuesAsLong());
					hgoptions.setOutput(new String[]{"groupid", "name"});
					CArray<Map> _groups = API.HostGroup(idBean, executor).get(hgoptions);

					if (!empty(_groups)) {
						order_result(_groups, "name");

						_result.add( bold(_("Run remote commands on host groups")+NAME_DELIMITER) );
						_result.add( array(implode(", ", rda_objectValues(_groups, "name")), BR()) );
					}
					break;
				case OPERATION_TYPE_HOST_ADD:
					_result.add(array(bold(_("Add host")), BR()) );
					break;
				case OPERATION_TYPE_HOST_REMOVE:
					_result.add( array(bold(_("Remove host")), BR()) );
					break;
				case OPERATION_TYPE_HOST_ENABLE:
					_result.add( array(bold(_("Enable host")), BR()) );
					break;
				case OPERATION_TYPE_HOST_DISABLE:
					_result.add( array(bold(_("Disable host")), BR()) );
					break;
				case OPERATION_TYPE_GROUP_ADD:
				case OPERATION_TYPE_GROUP_REMOVE:
					if (!isset(Nest.value(_data,"opgroup").$())) {
						Nest.value(_data,"opgroup").$(array());
					}

					hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(rda_objectValues(Nest.value(_data,"opgroup").$(), "groupid").valuesAsLong());
					hgoptions.setOutput(new String[]{"groupid", "name"});
					_groups = API.HostGroup(idBean, executor).get(hgoptions);

					if (!empty(_groups)) {
						order_result(_groups, "name");

						if (OPERATION_TYPE_GROUP_ADD == Nest.value(_data,"operationtype").asInteger()) {
							_result.add( bold(_("Add to host groups")+NAME_DELIMITER) );
						} else {
							_result.add( bold(_("Remove from host groups")+NAME_DELIMITER) );
						}

						_result.add( array(implode(", ", rda_objectValues(_groups, "name")), BR()) );
					}
					break;
				case OPERATION_TYPE_TEMPLATE_ADD:
				case OPERATION_TYPE_TEMPLATE_REMOVE:
					if (!isset(Nest.value(_data,"optemplate").$())) {
						Nest.value(_data,"optemplate").$(array());
					}

					CTemplateGet toptions = new CTemplateGet();
					toptions.setTemplateIds(rda_objectValues(Nest.value(_data,"optemplate").$(), "templateid").valuesAsLong());
					toptions.setOutput(new String[]{"hostid", "name"});
					CArray<Map> _templates = API.Template(idBean, executor).get(toptions);

					if (!empty(_templates)) {
						order_result(_templates, "name");

						if (OPERATION_TYPE_TEMPLATE_ADD == Nest.value(_data,"operationtype").asInteger()) {
							_result.add( bold(_("Link to templates")+NAME_DELIMITER) );
						} else {
							_result.add( bold(_("Unlink from templates")+NAME_DELIMITER) );
						}

						_result.add( array(implode(", ", rda_objectValues(_templates, "name")), BR()) );
					}
					break;
				default:
			}
		}
		else {
			switch (Nest.value(_data,"operationtype").asInteger()) {
				case OPERATION_TYPE_MESSAGE:
					if (isset(Nest.value(_data,"opmessage","default_msg").$()) && !empty(Nest.value(_data,"opmessage","default_msg").$())) {
						if (isset(Nest.value($_REQUEST(),"def_shortdata").$()) && isset(Nest.value($_REQUEST(),"def_longdata").$())) {
							_result.add( array(bold(_("Subject")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value($_REQUEST(),"def_shortdata").asString())) );
							_result.add( array(bold(_("Message")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value($_REQUEST(),"def_longdata").asString())) );
						}else if (isset(Nest.value(_data,"opmessage","operationid").$())) {
							SqlBuilder sqlParts = new SqlBuilder();
							String _sql = "SELECT a.def_shortdata,a.def_longdata "+
									" FROM actions a,operations o "+
									" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "actions", "a")+
									    " AND a.actionid=o.actionid "+
										" AND o.operationid="+sqlParts.marshalParam(Nest.value(_data,"operationid").$());
							Map _rows;
							if (!empty(_rows = DBfetch(DBselect(executor, _sql, 1, sqlParts.getNamedParams())))) {
								_result.add( array(bold(_("Subject")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value(_rows,"def_shortdata").asString())) );
								_result.add( array(bold(_("Message")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value(_rows,"def_longdata").asString())) );
							}
						}
					}
					else {
						_result.add( array(bold(_("Subject")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value(_data,"opmessage","subject").asString())) );
						_result.add( array(bold(_("Message")+NAME_DELIMITER), BR(), rda_nl2br(Nest.value(_data,"opmessage","message").asString())) );
					}

					break;
				case OPERATION_TYPE_COMMAND:
					switch (Nest.value(_data,"opcommand","type").asInteger()) {
						case RDA_SCRIPT_TYPE_IPMI:
							_result.add( array(bold(_("Run IPMI command")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
							break;
						case RDA_SCRIPT_TYPE_SSH:
							_result.add( array(bold(_("Run SSH commands")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
							break;
						case RDA_SCRIPT_TYPE_TELNET:
							_result.add( array(bold(_("Run TELNET commands")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
							break;
						case RDA_SCRIPT_TYPE_CUSTOM_SCRIPT:
							if (Nest.value(_data,"opcommand","execute_on").asInteger() == RDA_SCRIPT_EXECUTE_ON_AGENT) {
								_result.add( array(bold(_("Run custom commands on iRadar agent")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
							}
							else {
								_result.add( array(bold(_("Run custom commands on iRadar server")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
							}
							break;
						case RDA_SCRIPT_TYPE_GLOBAL_SCRIPT:
							CScriptGet soptions = new CScriptGet();
							soptions.setScriptIds(Nest.value(_data,"opcommand","scriptid").asLong());
							soptions.setOutput(API_OUTPUT_EXTEND);
							CArray<Map> _userScripts = API.Script(idBean, executor).get(soptions);
							Map _userScript = reset(_userScripts);

							_result.add( array(bold(_("Run global script")+NAME_DELIMITER), italic(Nest.value(_userScript,"name").asString())) );
							break;
						default:
							_result.add( array(bold(_("Run commands")+NAME_DELIMITER), BR(), italic(rda_nl2br(Nest.value(_data,"opcommand","command").asString()))) );
					}
					break;
				default:
			}
		}

		return _result;
	}
	
	private static CArray<CArray> _conditions = map(
			EVENT_SOURCE_TRIGGERS, array(
					CONDITION_TYPE_APPLICATION,
					CONDITION_TYPE_HOST_GROUP,
					CONDITION_TYPE_TEMPLATE,
					CONDITION_TYPE_HOST,
					CONDITION_TYPE_TRIGGER,
					CONDITION_TYPE_TRIGGER_NAME,
					CONDITION_TYPE_TRIGGER_SEVERITY,
					CONDITION_TYPE_TRIGGER_VALUE,
					CONDITION_TYPE_TIME_PERIOD,
					CONDITION_TYPE_MAINTENANCE
				),
			EVENT_SOURCE_DISCOVERY, array(
					CONDITION_TYPE_DHOST_IP,
					CONDITION_TYPE_DSERVICE_TYPE,
					CONDITION_TYPE_DSERVICE_PORT,
					CONDITION_TYPE_DRULE,
					CONDITION_TYPE_DCHECK,
					CONDITION_TYPE_DOBJECT,
					CONDITION_TYPE_DSTATUS,
					CONDITION_TYPE_DUPTIME,
					CONDITION_TYPE_DVALUE,
					CONDITION_TYPE_PROXY
				),
			EVENT_SOURCE_AUTO_REGISTRATION, array(
					CONDITION_TYPE_HOST_NAME,
					CONDITION_TYPE_PROXY,
					CONDITION_TYPE_HOST_METADATA
				), 
			EVENT_SOURCE_INTERNAL, array(
					CONDITION_TYPE_APPLICATION,
					CONDITION_TYPE_EVENT_TYPE,
					CONDITION_TYPE_HOST_GROUP,
					CONDITION_TYPE_TEMPLATE,
					CONDITION_TYPE_HOST
				)
		);
	
	/**
	 * Return an array of action conditions supported by the given event source.
	 *
	 * @param int _eventsource
	 *
	 * @return mixed
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray get_conditions_by_eventsource(int _eventsource) {
		if (isset(_conditions.get(_eventsource))) {
			return _conditions.get(_eventsource);
		}
		return _conditions.get(EVENT_SOURCE_TRIGGERS);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Integer> get_opconditions_by_eventsource(int _eventsource) {
		CArray<CArray<Integer>> _conditions = map(
			EVENT_SOURCE_TRIGGERS, array(CONDITION_TYPE_EVENT_ACKNOWLEDGED),
			EVENT_SOURCE_DISCOVERY, array()
		);

		if (isset(_conditions.get(_eventsource))) {
			return _conditions.get(_eventsource);
		}
		return null;
	}
	
	private static CArray<CArray> _operations = map(
		EVENT_SOURCE_TRIGGERS, array(
				OPERATION_TYPE_MESSAGE,
				OPERATION_TYPE_COMMAND
			),
		EVENT_SOURCE_DISCOVERY, array(
				OPERATION_TYPE_MESSAGE,
				OPERATION_TYPE_COMMAND,
				OPERATION_TYPE_HOST_ADD,
				OPERATION_TYPE_HOST_REMOVE,
				OPERATION_TYPE_GROUP_ADD,
				OPERATION_TYPE_GROUP_REMOVE,
				OPERATION_TYPE_TEMPLATE_ADD,
				OPERATION_TYPE_TEMPLATE_REMOVE,
				OPERATION_TYPE_HOST_ENABLE,
				OPERATION_TYPE_HOST_DISABLE
			),
		EVENT_SOURCE_AUTO_REGISTRATION, array(
				OPERATION_TYPE_MESSAGE,
				OPERATION_TYPE_COMMAND,
				OPERATION_TYPE_HOST_ADD,
				OPERATION_TYPE_GROUP_ADD,
				OPERATION_TYPE_TEMPLATE_ADD,
				OPERATION_TYPE_HOST_DISABLE
			),
		EVENT_SOURCE_INTERNAL, array(
				OPERATION_TYPE_MESSAGE
			)
	);
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray get_operations_by_eventsource(int _eventsource) {
		if (isset(_operations.get(_eventsource))) {
			return _operations.get(_eventsource);
		}

		return _operations.get(EVENT_SOURCE_TRIGGERS);
	}
	
	private static CArray<String> types__operation_type2str = map(
		OPERATION_TYPE_MESSAGE, _("Send message"),
		OPERATION_TYPE_COMMAND, _("Remote command"),
		OPERATION_TYPE_HOST_ADD, _("Add host"),
		OPERATION_TYPE_HOST_REMOVE, _("Remove host"),
		OPERATION_TYPE_HOST_ENABLE, _("Enable host"),
		OPERATION_TYPE_HOST_DISABLE, _("Disable host"),
		OPERATION_TYPE_GROUP_ADD, _("Add to host group"),
		OPERATION_TYPE_GROUP_REMOVE, _("Remove from host group"),
		OPERATION_TYPE_TEMPLATE_ADD, _("Link to template"),
		OPERATION_TYPE_TEMPLATE_REMOVE, _("Unlink from template")
	);
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<String> operation_type2str() {
		CArray<String> types = Clone.deepcopy(types__operation_type2str);
		FuncsUtil.order_result(types);
		return types;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static String operation_type2str(int type) {
		if(types__operation_type2str.containsKey(type)){
			return types__operation_type2str.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static void sortOperations(int _eventsource, CArray<Map> _operations) {
		if (_eventsource == EVENT_SOURCE_TRIGGERS || _eventsource == EVENT_SOURCE_INTERNAL) {
			CArray _esc_step_from = array();
			CArray _esc_step_to = array();
			CArray _esc_period = array();
			CArray _operationTypes = array();

			for(Entry<Object, Map> entry: _operations.entrySet()) {
				Object _key = entry.getKey();
				Map _operation = entry.getValue();
				_esc_step_from.put(_key, Nest.value(_operation,"esc_step_from").$() );
				_esc_step_to.put(_key, Nest.value(_operation,"esc_step_to").$() );
				_esc_period.put(_key, Nest.value(_operation,"esc_period").$() );
				_operationTypes.put(_key, Nest.value(_operation,"operationtype").$() );
			}
			array_multisort(_esc_step_from, SORT_ASC, _esc_step_to, SORT_ASC, _esc_period, SORT_ASC, _operationTypes, SORT_ASC, _operations);
		}
		else {
			CArrayHelper.sort(_operations, array("operationtype"));
		}
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<CArray<Integer>> _operators = map(
		CONDITION_TYPE_HOST_GROUP, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		), 
		CONDITION_TYPE_TEMPLATE, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_HOST, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_TRIGGER, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_TRIGGER_NAME, array(
			CONDITION_OPERATOR_LIKE,
			CONDITION_OPERATOR_NOT_LIKE
		),
		CONDITION_TYPE_TRIGGER_SEVERITY, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL,
			CONDITION_OPERATOR_MORE_EQUAL,
			CONDITION_OPERATOR_LESS_EQUAL
		),
		CONDITION_TYPE_TRIGGER_VALUE, array(
			CONDITION_OPERATOR_EQUAL
		),
		CONDITION_TYPE_TIME_PERIOD, array(
			CONDITION_OPERATOR_IN,
			CONDITION_OPERATOR_NOT_IN
		),
		CONDITION_TYPE_MAINTENANCE, array(
			CONDITION_OPERATOR_IN,
			CONDITION_OPERATOR_NOT_IN
		),
		CONDITION_TYPE_DRULE, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DCHECK, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DOBJECT, array(
			CONDITION_OPERATOR_EQUAL
		),
		CONDITION_TYPE_PROXY, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DHOST_IP, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DSERVICE_TYPE, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DSERVICE_PORT, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL
		),
		CONDITION_TYPE_DSTATUS, array(
			CONDITION_OPERATOR_EQUAL
		),
		CONDITION_TYPE_DUPTIME, array(
			CONDITION_OPERATOR_MORE_EQUAL,
			CONDITION_OPERATOR_LESS_EQUAL
		),
		CONDITION_TYPE_DVALUE, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_NOT_EQUAL,
			CONDITION_OPERATOR_MORE_EQUAL,
			CONDITION_OPERATOR_LESS_EQUAL,
			CONDITION_OPERATOR_LIKE,
			CONDITION_OPERATOR_NOT_LIKE
		),
		CONDITION_TYPE_EVENT_ACKNOWLEDGED, array(
			CONDITION_OPERATOR_EQUAL
		),
		CONDITION_TYPE_APPLICATION, array(
			CONDITION_OPERATOR_EQUAL,
			CONDITION_OPERATOR_LIKE,
			CONDITION_OPERATOR_NOT_LIKE
		),
		CONDITION_TYPE_HOST_NAME, array(
			CONDITION_OPERATOR_LIKE,
			CONDITION_OPERATOR_NOT_LIKE
		),
		CONDITION_TYPE_EVENT_TYPE, array(
			CONDITION_OPERATOR_EQUAL
		),
		CONDITION_TYPE_HOST_METADATA, array(
			CONDITION_OPERATOR_LIKE,
			CONDITION_OPERATOR_NOT_LIKE
		)
	);
	
	/**
	 * Return an array of operators supported by the given action condition.
	 *
	 * @param int _conditiontype
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Integer> get_operators_by_conditiontype(int _conditiontype) {
		if (isset(_operators.get(_conditiontype))) {
			return _operators.get(_conditiontype);
		}

		return array();
	}

	@CodeConfirmed("blue.2.2.5")
	public static CArray<Integer> count_operations_delay(CArray<Map> operations) {
		return count_operations_delay(operations, 0);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Integer> count_operations_delay(CArray<Map> operations, int def_period) {
		CArray<Integer> delays = map(1 , 0);
		CArray<Integer> periods = array();
		int max_step = 0, step_to=0, esc_period = 0, esc_step_from = 0;
		for(Map operation:operations){
			step_to = (step_to = MapUtils.getIntValue(operation, "esc_step_to")) > 0 ? step_to : 9999;
			esc_period = (step_to = MapUtils.getIntValue(operation, "esc_period")) > 0 ? esc_period : def_period;
			if (max_step < (esc_step_from=MapUtils.getIntValue(operation, "esc_step_from"))) {
				max_step = esc_step_from;
			}
			for (int i = esc_step_from; i <= step_to; i++) {
				if (!isset(periods,i) || periods.get(i) > esc_period) {
					periods.put(i,esc_period);
				}
			}
		}
		for (int i = 1; i <= max_step; i++) {
			esc_period = isset(periods,i) ? periods.get(i) : def_period;
			delays.put(i+1,delays.get(i)+ esc_period);
		}
		return delays;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo get_action_msgs_for_event(Map _event) {
		CTableInfo _table = new CTableInfo(_("No actions found."));
		_table.setHeader(array(
			_("Time"),
			_("Type"),
			_("Status"),
			_("Retries left"),
			_("Recipient(s)"),
			_("Message"),
			_("Error")
		));

		CArray<Map> _alerts = Nest.value(_event,"alerts").asCArray();
		for(Entry<Object, Map> entry: _alerts.entrySet()) {
			//Object _alertid = entry.getKey();
			Map _alert = entry.getValue();
			
			if (Nest.value(_alert,"alerttype").asInteger() != ALERT_TYPE_MESSAGE) {
				continue;
			}

			Map _mediatype = array_pop(Nest.value(_alert,"mediatypes").asCArray());

			Object _time = rda_date2str(EVENT_ACTION_MESSAGES_DATE_FORMAT, Nest.value(_alert,"clock").asLong());
			if (Nest.value(_alert,"esc_step").asInteger() > 0) {
				_time = array(
					bold(_("Step")+NAME_DELIMITER),
					_alert.get("esc_step"),
					BR(),
					bold(_("Time")+NAME_DELIMITER),
					BR(),
					_time
				);
			}

			CSpan _status, _retries;
			if (Nest.value(_alert,"status").asInteger() == ALERT_STATUS_SENT) {
				_status = new CSpan(_("sent"), "green");
				_retries = new CSpan(SPACE, "green");
			}
			else if (Nest.value(_alert,"status").asInteger() == ALERT_STATUS_NOT_SENT) {
				_status = new CSpan(_("In progress"), "orange");
				_retries = new CSpan(ALERT_MAX_RETRIES - Nest.value(_alert,"retries").asInteger(), "orange");
			}
			else {
				_status = new CSpan(_("not sent"), "red");
				_retries = new CSpan(0, "red");
			}
			String _sendto = Nest.value(_alert,"sendto").asString();

			CArray _message = array(
				bold(_("Subject")+NAME_DELIMITER),
				BR(),
				Nest.value(_alert,"subject").$(),
				BR(),
				BR(),
				bold(_("Message")+NAME_DELIMITER)
			);
			array_push(_message, BR(), rda_nl2br(Nest.value(_alert,"message").asString()));

			CSpan _error;
			if (empty(Nest.value(_alert,"error").$())) {
				_error = new CSpan(SPACE, "off");
			}
			else {
				_error = new CSpan(Nest.value(_alert,"error").$(), "on");
			}

			_table.addRow(array(
				new CCol(_time, "top"),
				new CCol((!empty(Nest.value(_mediatype,"description").$()) ? Nest.value(_mediatype,"description").$() : ""), "top"),
				new CCol(_status, "top"),
				new CCol(_retries, "top"),
				new CCol(_sendto, "top"),
				new CCol(_message, "wraptext top"),
				new CCol(_error, "wraptext top")
			));
		}

		return _table;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo get_action_cmds_for_event(Map _event) {
		CTableInfo _table = new CTableInfo(_("No actions found."));
		_table.setHeader(array(
			_("Time"),
			_("Status"),
			_("Command"),
			_("Error")
		));

		CArray<Map> _alerts = Nest.value(_event,"alerts").asCArray();
		for(Map _alert: _alerts) {
			if (Nest.value(_alert,"alerttype").asInteger() != ALERT_TYPE_COMMAND) {
				continue;
			}

			Object _time = rda_date2str(EVENT_ACTION_CMDS_DATE_FORMAT, Nest.value(_alert,"clock").asLong());
			if (Nest.value(_alert,"esc_step").asInteger() > 0) {
				_time = array(
					bold(_("Step")+NAME_DELIMITER),
					Nest.value(_alert,"esc_step").$(),
					BR(),
					bold(_("Time")+NAME_DELIMITER),
					BR(),
					_time
				);
			}

			CSpan _status;
			switch (Nest.value(_alert,"status").asInteger()) {
				case ALERT_STATUS_SENT:
					_status = new CSpan(_("executed"), "green");
					break;
				case ALERT_STATUS_NOT_SENT:
					_status = new CSpan(_("In progress"), "orange");
					break;
				default:
					_status = new CSpan(_("not sent"), "red");
					break;
			}

			CArray _message = array(bold(_("Command")+NAME_DELIMITER));
			array_push(_message, BR(), rda_nl2br(Nest.value(_alert,"message").asString()));

			CSpan _error = empty(Nest.value(_alert,"error").$()) ? new CSpan(SPACE, "off") : new CSpan(Nest.value(_alert,"error").$(), "on");

			_table.addRow(array(
				new CCol(_time, "top"),
				new CCol(_status, "top"),
				new CCol(_message, "wraptext top"),
				new CCol(_error, "wraptext top")
			));
		}

		return _table;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo get_actions_hint_by_eventid(IIdentityBean idBean, SQLExecutor executor, long eventid) {
		return get_actions_hint_by_eventid(idBean, executor, eventid, null);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo get_actions_hint_by_eventid(IIdentityBean idBean, SQLExecutor executor, long eventid, Integer status) {
		CTableInfo tab_hint = new CTableInfo(_("No actions found."));
		tab_hint.setAttribute("style", "width: 300px;");
		tab_hint.setHeader(array(
			_("User"),
			_("Details"),
			_("Status")
		));
	
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT a.alertid,mt.description,u.alias,u.name,u.surname,a.subject,a.message,a.sendto,a.status,a.retries,a.alerttype"+
				" FROM events e,alerts a"+
					" LEFT JOIN users u ON u.tenantid=a.tenantid AND u.userid=a.userid"+
					" LEFT JOIN media_type mt ON mt.tenantid='-' AND mt.mediatypeid=a.mediatypeid"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "alerts", "a")+
				    " AND a.eventid="+sqlParts.marshalParam(eventid)+
					(is_null(status)?"":" AND a.status="+status)+
					" AND e.eventid=a.eventid"+
					" AND a.alerttype IN ("+ALERT_TYPE_MESSAGE+","+ALERT_TYPE_COMMAND+")"+
				" ORDER BY a.alertid";
		CArray<Map> result = DBselect(executor, sql, 30, sqlParts.getNamedParams());
	
		for (Map row : result) {
			CSpan cstatus;
			if (Nest.value(row,"status").asInteger() == ALERT_STATUS_SENT) {
				cstatus = new CSpan(_("Sent"), "green");
			} else if (Nest.value(row,"status").asInteger() == ALERT_STATUS_NOT_SENT) {
				cstatus = new CSpan(_("In progress"), "orange");
			} else {
				cstatus = new CSpan(_("not sent"), "red");
			}
			
			Object message;
			switch (Nest.value(row,"alerttype").asInteger()) {
				case ALERT_TYPE_MESSAGE:
					message = empty(Nest.value(row,"description").$()) ? "-" : Nest.value(row,"description").$();
					break;
				case ALERT_TYPE_COMMAND:
					message = array(bold(_("Command")+NAME_DELIMITER));
					String[] msg = explode("\n", Nest.value(row,"message").asString());
					for(String m: msg) {
						array_push((CArray)message, BR(), m);
					}
					break;
				default:
					message = "-";
			}
	
			if (empty(Nest.value(row,"alias").$())) {
				Nest.value(row,"alias").$(" - ");
			} else {
				String fullname = "";
				if (!empty(Nest.value(row,"name").$())) {
					fullname = Nest.value(row,"name").asString();
				}
				if (!empty(Nest.value(row,"surname").$())) {
					fullname += !empty(fullname) ? " "+Nest.value(row,"surname").$() : Nest.value(row,"surname").$();
				}
				if (!empty(fullname)) {
					Nest.value(row,"alias").$(Nest.value(row,"alias").$() + " ("+fullname+")");
				}
			}
	
			tab_hint.addRow(array(
				Nest.value(row,"alias").$(),
				message,
				cstatus
			));
		}
		return tab_hint;
	}
	
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray getEventActionsStatus(IIdentityBean idBean, SQLExecutor executor, CArray _eventIds) {
		if (empty(_eventIds)) {
			return array();
		}

		CArray<Map> _actions = array();

		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("a.eventid,a.status,COUNT(a.alertid) AS cnt");
		sqlParts.from.put("alerts a");
		sqlParts.where.dbConditionTenants(idBean, "alerts", "a");
		sqlParts.where.put("a.alerttype IN ("+ALERT_TYPE_MESSAGE+","+ALERT_TYPE_COMMAND+")");
		sqlParts.where.dbConditionInt("a.eventid", _eventIds.valuesAsLong());
		sqlParts.group.put("eventid,status");
		
		CArray<Map> _alerts = DBselect(executor, sqlParts);

		for (Map _alert: _alerts) {
			_actions.put(_alert.get("eventid"), _alert.get("status"), Nest.value(_alert,"cnt").$());
		}

		for(Entry<Object, Map> entry: _actions.entrySet()) {
			Object _eventId = entry.getKey();
			Map _action = entry.getValue();
			
			int _sendCount = isset(_action.get(ALERT_STATUS_SENT)) ? Nest.value(_action, ALERT_STATUS_SENT).asInteger() : 0;
			int _notSendCount = isset(_action.get(ALERT_STATUS_NOT_SENT)) ? Nest.value(_action, ALERT_STATUS_NOT_SENT).asInteger() : 0;
			int _failedCount = isset(_action.get(ALERT_STATUS_FAILED)) ? Nest.value(_action, ALERT_STATUS_FAILED).asInteger() : 0;

			// calculate total
			int _mixed = 0;
			if (_sendCount > 0) {
				_mixed += ALERT_STATUS_SENT;
			}
			if (_failedCount > 0) {
				_mixed += ALERT_STATUS_FAILED;
			}

			// display
			Object _status;
			if (_notSendCount > 0) {
				_status = new CSpan(_("In progress"), "orange");
			}
			else if (_mixed == ALERT_STATUS_SENT) {
				_status = new CSpan(_("Ok"), "green");
			}
			else if (_mixed == ALERT_STATUS_FAILED) {
				_status = new CSpan(_("Failed"), "red");
			}
			else {
				CCol _columnLeft = new CCol((_sendCount > 0) ? new CSpan(_sendCount, "green") : SPACE);
				_columnLeft.setAttribute("width", "10");

				CCol _columnRight = new CCol((_failedCount > 0) ? new CSpan(_failedCount, "red") : SPACE);
				_columnRight.setAttribute("width", "10");

				_status = new CRow(array(_columnLeft, _columnRight));
			}

			CTable _ctable = new CTable(" - ");
			_ctable.addRow(_status);
			_actions.put(_eventId, _ctable);
		}

		return _actions;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray getEventActionsStatHints(IIdentityBean idBean, SQLExecutor executor, CArray eventIds) {
		if (empty(eventIds)) {
			return array();
		}
	
		CArray<Map> actions = array();
		
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> alerts = DBselect(executor,
			"SELECT a.eventid,a.status,COUNT(a.alertid) AS cnt"+
			" FROM alerts a"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "alerts", "a")+
			    " AND a.alerttype IN ("+ALERT_TYPE_MESSAGE+","+ALERT_TYPE_COMMAND+")"+
				" AND "+sqlParts.dual.dbConditionInt("a.eventid", eventIds.valuesAsLong())+
			" GROUP BY eventid,status",
			sqlParts.getNamedParams()
		);
	
		for (Map alert: alerts) {
			String color;
			if (Nest.value(alert,"cnt").asInteger() > 0) {
				if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_SENT) {
					color = "green";
				}
				else if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_NOT_SENT) {
					color = "orange";
				} else {
					color = "red";
				}
	
				CSpan hint = new CSpan(Nest.value(alert,"cnt").$(), color);
				hint.setHint(get_actions_hint_by_eventid(idBean, executor, Nest.value(alert,"eventid").asLong(), Nest.value(alert,"status").asInteger()));
	
				actions.put(alert.get("eventid"), alert.get("status"), hint);
			}
		}
	
		for(Entry<Object, Map> entry: actions.entrySet()) {
			Object eventId = entry.getKey();
			Map action = entry.getValue();
		
			CDiv cdiv = new CDiv(null, "event-action-cont");
			actions.put(eventId, cdiv);
			cdiv.addItem(array(
				new CDiv(isset(action.get(ALERT_STATUS_SENT)) ? Nest.value(action, ALERT_STATUS_SENT).$() : SPACE),
				new CDiv(isset(action.get(ALERT_STATUS_NOT_SENT)) ? Nest.value(action, ALERT_STATUS_NOT_SENT).$() : SPACE),
				new CDiv(isset(action.get(ALERT_STATUS_FAILED)) ? Nest.value(action, ALERT_STATUS_FAILED).$() : SPACE)
			));
		}
	
		return actions;
	}
	
	private static CArray<String> types_eventType = map(
			EVENT_TYPE_ITEM_NOTSUPPORTED, _("Item in \"not supported\" state"),
			EVENT_TYPE_ITEM_NORMAL, _("Item in \"normal\" state"),
			EVENT_TYPE_LLDRULE_NOTSUPPORTED, _("Low-level discovery rule in \"not supported\" state"),
			EVENT_TYPE_LLDRULE_NORMAL, _("Low-level discovery rule in \"normal\" state"),
			EVENT_TYPE_TRIGGER_UNKNOWN, _("Trigger in \"unknown\" state"),
			EVENT_TYPE_TRIGGER_NORMAL, _("Trigger in \"normal\" state")
		);
	
	/**
	 * Returns the names of the \"Event type\" action condition values.
	 *
	 * If the _type parameter is passed, returns the name of the specific value, otherwise - returns an array of all
	 * supported values.
	 *
	 * @param string _type
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray<String> eventType() {
		return Clone.deepcopy(types_eventType);
	}

	/**
	 * Returns the names of the \"Event type\" action condition values.
	 *
	 * If the _type parameter is passed, returns the name of the specific value, otherwise - returns an array of all
	 * supported values.
	 *
	 * @param string _type
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String eventType(int type) {
		CArray<String> types = types_eventType;
		if (types.containsKey(type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
}
