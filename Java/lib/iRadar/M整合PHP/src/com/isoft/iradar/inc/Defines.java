package com.isoft.iradar.inc;


public interface Defines {
	
	public final static String IRADAR_VERSION = "1.3.1";
	public final static String IRADAR_API_VERSION = "1.3.1";
	public final static int IRADAR_DB_VERSION = 2020000;

	public final static String IRADAR_COPYRIGHT_FROM = "2010";
	public final static String IRADAR_COPYRIGHT_TO = "2014";

	public final static int RDA_LOGIN_ATTEMPTS = 5;
	public final static int RDA_LOGIN_BLOCK = 30; // sec

	public final static int RDA_MIN_PERIOD = 3600; // 1 hour
	public final static int RDA_MAX_PERIOD = 63072000; // the maximum period for the time bar control, ~2 years (2 * 365 * 86400)
	public final static int RDA_MAX_DATE = 2145888000; // 01 Jan 2038 00:00:00
	public final static int RDA_PERIOD_DEFAULT = 3600; // 1 hour
	
	// the maximum period to display history data for the latest data and item overview pages in seconds
	// by default set to 86400 seconds (24 hours)
	public final static int RDA_HISTORY_PERIOD = 86400;

	public final static int RDA_WIDGET_ROWS = 20;

	public final static String RDA_FONTPATH = "/usr/share/fonts/dejavu"; // where to search for font (GD > 2.0.18)
	public final static String RDA_GRAPH_FONT_NAME = "DejaVuSans"; // font file name
	public final static int RDA_GRAPH_LEGEND_HEIGHT = 120; // when graph height is less then this value, some legend will not show up

	public final static int RDA_SCRIPT_TIMEOUT = 60; // in seconds

	public final static int GRAPH_YAXIS_SIDE_DEFAULT = 0; // 0 - LEFT SIDE, 1 - RIGHT SIDE

	public final static int RDA_MAX_IMAGE_SIZE = 1048576; // 1024 * 1024

	public final static float RDA_UNITS_ROUNDOFF_THRESHOLD = 0.01f;
	public final static int RDA_UNITS_ROUNDOFF_UPPER_LIMIT = 2;
	public final static int RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT = 4;
	public final static int RDA_UNITS_ROUNDOFF_LOWER_LIMIT = 6;
	
	public final static int RDA_PRECISION_10 = 10;

	public final static String RDA_DEFAULT_INTERVAL = "1-7,00:00-24:00";

	// for partitioned DB installs!!
	public final static int RDA_HISTORY_DATA_UPKEEP = -1; // in days; -1: disabled, 0: always use trends

	public final static int RDA_SCRIPT_TYPE_CUSTOM_SCRIPT = 0;
	public final static int RDA_SCRIPT_TYPE_IPMI = 1;
	public final static int RDA_SCRIPT_TYPE_SSH = 2;
	public final static int RDA_SCRIPT_TYPE_TELNET = 3;
	public final static int RDA_SCRIPT_TYPE_GLOBAL_SCRIPT = 4;

	public final static int RDA_SCRIPT_EXECUTE_ON_AGENT = 0;
	public final static int RDA_SCRIPT_EXECUTE_ON_SERVER = 1;

	public final static int RDA_FLAG_DISCOVERY_NORMAL = 0x0; // a normal item
	public final static int RDA_FLAG_DISCOVERY_RULE = 0x1; // a low level discovery rule
	public final static int RDA_FLAG_DISCOVERY_PROTOTYPE = 0x2; // an item prototype
	public final static int RDA_FLAG_DISCOVERY_CREATED = 0x4; // an item created via a discovery rule

	public final static int EXTACK_OPTION_ALL = 0;
	public final static int EXTACK_OPTION_UNACK = 1;
	public final static int EXTACK_OPTION_BOTH = 2;

	public final static int TRIGGERS_OPTION_ONLYTRUE = 1;
	public final static int TRIGGERS_OPTION_ALL = 2;

	public final static int RDA_ACK_STS_ANY = 1;
	public final static int RDA_ACK_STS_WITH_UNACK = 2;
	public final static int RDA_ACK_STS_WITH_LAST_UNACK = 3;
	
	public final static int EVENTS_OPTION_NOEVENT = 1;
	public final static int EVENTS_OPTION_ALL = 2;
	public final static int EVENTS_OPTION_NOT_ACK = 3;

	public final static String RDA_FONT_NAME = "DejaVuSans";

	public final static int RDA_AUTH_INTERNAL = 0;
	public final static int RDA_AUTH_LDAP = 1;
	public final static int RDA_AUTH_HTTP = 2;

	public final static String RDA_DB_DB2 = "IBM_DB2";
	public final static String RDA_DB_MYSQL = "MYSQL";
	public final static String RDA_DB_ORACLE = "ORACLE";
	public final static String RDA_DB_POSTGRESQL = "POSTGRESQL";
	public final static String RDA_DB_SQLITE3 = "SQLITE3";
	
	public final static String RDA_STANDALONE_MAX_IDS = "9223372036854775807";
	public final static String RDA_DM_MAX_HISTORY_IDS = "100000000000000";
	public final static String RDA_DM_MAX_CONFIG_IDS = "100000000000";

	public final static int PAGE_TYPE_HTML = 0;
	public final static int PAGE_TYPE_IMAGE = 1;
	public final static int PAGE_TYPE_XML = 2;
	public final static int PAGE_TYPE_JS = 3; // javascript
	public final static int PAGE_TYPE_CSS = 4;
	public final static int PAGE_TYPE_HTML_BLOCK = 5; // simple block of html (as text)
	public final static int PAGE_TYPE_JSON = 6; // simple JSON
	public final static int PAGE_TYPE_JSON_RPC = 7; // api call
	public final static int PAGE_TYPE_TEXT_FILE = 8; // api call
	public final static int PAGE_TYPE_TEXT = 9; // simple text
	public final static int PAGE_TYPE_CSV = 10; // CSV format
	public final static int PAGE_TYPE_TEXT_RETURN_JSON = 11; // input plaintext output json

	public final static int RDA_SESSION_ACTIVE = 0;
	public final static int RDA_SESSION_PASSIVE = 1;

	public final static int RDA_DROPDOWN_FIRST_NONE = 0;
	public final static int RDA_DROPDOWN_FIRST_ALL = 1;

	public final static int T_RDA_STR = 0;
	public final static int T_RDA_INT = 1;
	public final static int T_RDA_DBL = 2;
	public final static int T_RDA_PERIOD = 3;
	public final static int T_RDA_IP = 4;
	public final static int T_RDA_CLR = 5;
	public final static int T_RDA_IP_RANGE = 7;
	public final static int T_RDA_INT_RANGE = 8;
	public final static int T_RDA_DBL_BIG = 9;
	public final static int T_RDA_DBL_STR = 10;

	public final static int O_MAND = 0;
	public final static int O_OPT = 1;
	public final static int O_NO = 2;

	public final static int P_SYS = 1;
	public final static int P_UNSET_EMPTY = 2;
	public final static int P_ACT = 16;
	public final static int P_NZERO = 32;

//		misc parameters
	public final static String IMAGE_FORMAT_PNG = "PNG";
	public final static String IMAGE_FORMAT_JPEG = "JPEG";
	public final static String IMAGE_FORMAT_TEXT = "JPEG";

	public final static int IMAGE_TYPE_UNKNOWN = 0;
	public final static int IMAGE_TYPE_ICON = 1;
	public final static int IMAGE_TYPE_BACKGROUND = 2;

	public final static int ITEM_CONVERT_WITH_UNITS = 0; // - do not convert empty units
	public final static int ITEM_CONVERT_NO_UNITS = 1; // - no units
	public final static int ITEM_CONVERT_SHORT_UNITS = 2; // - to short units
	public final static int ITEM_CONVERT_LONG_UNITS = 3; // - to long units

	public final static String RDA_SORT_UP = "ASC";
	public final static String RDA_SORT_DOWN = "DESC";

	public final static int AUDIT_ACTION_ADD = 0;
	public final static int AUDIT_ACTION_UPDATE = 1;
	public final static int AUDIT_ACTION_DELETE = 2;
	public final static int AUDIT_ACTION_LOGIN = 3;
	public final static int AUDIT_ACTION_LOGOUT = 4;
	public final static int AUDIT_ACTION_ENABLE = 5;
	public final static int AUDIT_ACTION_DISABLE = 6;

	public final static int AUDIT_RESOURCE_USER = 0;
	public final static int AUDIT_RESOURCE_IRADAR_CONFIG = 2;
	public final static int AUDIT_RESOURCE_MEDIA_TYPE = 3;
	public final static int AUDIT_RESOURCE_HOST = 4;
	public final static int AUDIT_RESOURCE_ACTION = 5;
	public final static int AUDIT_RESOURCE_GRAPH = 6;
	public final static int AUDIT_RESOURCE_GRAPH_ELEMENT = 7;
	public final static int AUDIT_RESOURCE_USER_GROUP = 11;
	public final static int AUDIT_RESOURCE_APPLICATION = 12;
	public final static int AUDIT_RESOURCE_TRIGGER = 13;
	public final static int AUDIT_RESOURCE_HOST_GROUP = 14;
	public final static int AUDIT_RESOURCE_ITEM = 15;
	public final static int AUDIT_RESOURCE_IMAGE = 16;
	public final static int AUDIT_RESOURCE_VALUE_MAP = 17;
	public final static int AUDIT_RESOURCE_IT_SERVICE = 18;
	public final static int AUDIT_RESOURCE_MAP = 19;
	public final static int AUDIT_RESOURCE_SCREEN = 20;
	public final static int AUDIT_RESOURCE_SCENARIO = 22;
	public final static int AUDIT_RESOURCE_DISCOVERY_RULE = 23;
	public final static int AUDIT_RESOURCE_SLIDESHOW = 24;
	public final static int AUDIT_RESOURCE_SCRIPT = 25;
	public final static int AUDIT_RESOURCE_PROXY = 26;
	public final static int AUDIT_RESOURCE_MAINTENANCE = 27;
	public final static int AUDIT_RESOURCE_REGEXP = 28;
	public final static int AUDIT_RESOURCE_MACRO = 29;
	public final static int AUDIT_RESOURCE_TEMPLATE = 30;
	public final static int AUDIT_RESOURCE_TRIGGER_PROTOTYPE = 31;

	public final static int CONDITION_TYPE_HOST_GROUP = 0;
	public final static int CONDITION_TYPE_HOST = 1;
	public final static int CONDITION_TYPE_TRIGGER = 2;
	public final static int CONDITION_TYPE_TRIGGER_NAME = 3;
	public final static int CONDITION_TYPE_TRIGGER_SEVERITY = 4;
	public final static int CONDITION_TYPE_TRIGGER_VALUE = 5;
	public final static int CONDITION_TYPE_TIME_PERIOD = 6;
	public final static int CONDITION_TYPE_DHOST_IP = 7;
	public final static int CONDITION_TYPE_DSERVICE_TYPE = 8;
	public final static int CONDITION_TYPE_DSERVICE_PORT = 9;
	public final static int CONDITION_TYPE_DSTATUS = 10;
	public final static int CONDITION_TYPE_DUPTIME = 11;
	public final static int CONDITION_TYPE_DVALUE = 12;
	public final static int CONDITION_TYPE_TEMPLATE = 13;
	public final static int CONDITION_TYPE_EVENT_ACKNOWLEDGED = 14;
	public final static int CONDITION_TYPE_APPLICATION = 15;
	public final static int CONDITION_TYPE_MAINTENANCE = 16;
	public final static int CONDITION_TYPE_DRULE = 18;
	public final static int CONDITION_TYPE_DCHECK = 19;
	public final static int CONDITION_TYPE_PROXY = 20;
	public final static int CONDITION_TYPE_DOBJECT = 21;
	public final static int CONDITION_TYPE_HOST_NAME = 22;
	public final static int CONDITION_TYPE_EVENT_TYPE = 23;
	public final static int CONDITION_TYPE_HOST_METADATA = 24;

	public final static int CONDITION_OPERATOR_EQUAL = 0;
	public final static int CONDITION_OPERATOR_NOT_EQUAL = 1;
	public final static int CONDITION_OPERATOR_LIKE = 2;
	public final static int CONDITION_OPERATOR_NOT_LIKE = 3;
	public final static int CONDITION_OPERATOR_IN = 4;
	public final static int CONDITION_OPERATOR_MORE_EQUAL = 5;
	public final static int CONDITION_OPERATOR_LESS_EQUAL = 6;
	public final static int CONDITION_OPERATOR_NOT_IN = 7;
	
	public final static int EVENT_TYPE_ITEM_NOTSUPPORTED = 0;
	public final static int EVENT_TYPE_ITEM_NORMAL = 1;
	public final static int EVENT_TYPE_LLDRULE_NOTSUPPORTED = 2;
	public final static int EVENT_TYPE_LLDRULE_NORMAL = 3;
	public final static int EVENT_TYPE_TRIGGER_UNKNOWN = 4;
	public final static int EVENT_TYPE_TRIGGER_NORMAL = 5;

	public final static int HOST_STATUS_MONITORED = 0;
	public final static int HOST_STATUS_NOT_MONITORED = 1;
	public final static int HOST_STATUS_TEMPLATE = 3;
	public final static int HOST_STATUS_PROXY_ACTIVE = 5;
	public final static int HOST_STATUS_PROXY_PASSIVE = 6;

	public final static int HOST_MAINTENANCE_STATUS_OFF = 0;
	public final static int HOST_MAINTENANCE_STATUS_ON = 1;

	public final static int INTERFACE_SECONDARY = 0;
	public final static int INTERFACE_PRIMARY = 1;

	public final static int INTERFACE_USE_DNS = 0;
	public final static int INTERFACE_USE_IP = 1;

	public final static int INTERFACE_TYPE_ANY = -1;
	public final static int INTERFACE_TYPE_UNKNOWN = 0;
	public final static int INTERFACE_TYPE_AGENT = 1;
	public final static int INTERFACE_TYPE_SNMP = 2;
	public final static int INTERFACE_TYPE_IPMI = 3;
	public final static int INTERFACE_TYPE_JMX = 4;

	public final static int MAINTENANCE_STATUS_ACTIVE = 0;
	public final static int MAINTENANCE_STATUS_APPROACH = 1;
	public final static int MAINTENANCE_STATUS_EXPIRED = 2;

	public final static int HOST_AVAILABLE_UNKNOWN = 0;
	public final static int HOST_AVAILABLE_TRUE = 1;
	public final static int HOST_AVAILABLE_FALSE = 2;

	public final static int MAINTENANCE_TYPE_NORMAL = 0;
	public final static int MAINTENANCE_TYPE_NODATA = 1;

	public final static int TIMEPERIOD_TYPE_ONETIME = 0;
	public final static int TIMEPERIOD_TYPE_HOURLY = 1;
	public final static int TIMEPERIOD_TYPE_DAILY = 2;
	public final static int TIMEPERIOD_TYPE_WEEKLY = 3;
	public final static int TIMEPERIOD_TYPE_MONTHLY = 4;
	public final static int TIMEPERIOD_TYPE_YEARLY = 5;

	public final static int SYSMAP_LABEL_ADVANCED_OFF = 0;
	public final static int SYSMAP_LABEL_ADVANCED_ON = 1;

	public final static int MAP_LABEL_TYPE_LABEL = 0;
	public final static int MAP_LABEL_TYPE_IP = 1;
	public final static int MAP_LABEL_TYPE_NAME = 2;
	public final static int MAP_LABEL_TYPE_STATUS = 3;
	public final static int MAP_LABEL_TYPE_NOTHING = 4;
	public final static int MAP_LABEL_TYPE_CUSTOM = 5;

	public final static int MAP_LABEL_LOC_BOTTOM = 0;
	public final static int MAP_LABEL_LOC_LEFT = 1;
	public final static int MAP_LABEL_LOC_RIGHT = 2;
	public final static int MAP_LABEL_LOC_TOP = 3;

	public final static int SYSMAP_ELEMENT_TYPE_HOST = 0;
	public final static int SYSMAP_ELEMENT_TYPE_MAP = 1;
	public final static int SYSMAP_ELEMENT_TYPE_TRIGGER = 2;
	public final static int SYSMAP_ELEMENT_TYPE_HOST_GROUP = 3;
	public final static int SYSMAP_ELEMENT_TYPE_IMAGE = 4;

	public final static int SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP = 0;
	public final static int SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS = 1;

	public final static int SYSMAP_ELEMENT_AREA_TYPE_FIT = 0;
	public final static int SYSMAP_ELEMENT_AREA_TYPE_CUSTOM = 1;

	public final static int SYSMAP_ELEMENT_AREA_VIEWTYPE_GRID = 0;

	public final static int SYSMAP_ELEMENT_ICON_ON = 0;
	public final static int SYSMAP_ELEMENT_ICON_OFF = 1;
	public final static int SYSMAP_ELEMENT_ICON_MAINTENANCE = 3;
	public final static int SYSMAP_ELEMENT_ICON_DISABLED = 4;

	public final static int SYSMAP_HIGHLIGHT_OFF = 0;
	public final static int SYSMAP_HIGHLIGHT_ON = 1;

	public final static int SYSMAP_EXPANDPROBLEM_OFF = 0;
	public final static int SYSMAP_EXPANDPROBLEM_ON = 1;

	public final static int SYSMAP_MARKELEMENTS_OFF = 0;
	public final static int SYSMAP_MARKELEMENTS_ON = 1;

	public final static int SYSMAP_GRID_SHOW_ON = 1;
	public final static int SYSMAP_GRID_SHOW_OFF = 0;

	public final static int SYSMAP_EXPAND_MACROS_OFF = 0;
	public final static int SYSMAP_EXPAND_MACROS_ON = 1;

	public final static int SYSMAP_GRID_ALIGN_ON = 1;
	public final static int SYSMAP_GRID_ALIGN_OFF = 0;

	public final static int RDA_ITEM_DELAY_DEFAULT = 30;

	public final static int ITEM_TYPE_IRADAR = 0;
	public final static int ITEM_TYPE_SNMPV1 = 1;
	public final static int ITEM_TYPE_TRAPPER = 2;
	public final static int ITEM_TYPE_SIMPLE = 3;
	public final static int ITEM_TYPE_SNMPV2C = 4;
	public final static int ITEM_TYPE_INTERNAL = 5;
	public final static int ITEM_TYPE_SNMPV3 = 6;
	public final static int ITEM_TYPE_IRADAR_ACTIVE = 7;
	public final static int ITEM_TYPE_AGGREGATE = 8;
	public final static int ITEM_TYPE_HTTPTEST = 9;
	public final static int ITEM_TYPE_EXTERNAL = 10;
	public final static int ITEM_TYPE_DB_MONITOR = 11;
	public final static int ITEM_TYPE_IPMI = 12;
	public final static int ITEM_TYPE_SSH = 13;
	public final static int ITEM_TYPE_TELNET = 14;
	public final static int ITEM_TYPE_CALCULATED = 15;
	public final static int ITEM_TYPE_JMX = 16;

	public final static int ITEM_VALUE_TYPE_FLOAT = 0;
	public final static int ITEM_VALUE_TYPE_STR = 1;
	public final static int ITEM_VALUE_TYPE_LOG = 2;
	public final static int ITEM_VALUE_TYPE_UINT64 = 3;
	public final static int ITEM_VALUE_TYPE_TEXT = 4;

	public final static int ITEM_DATA_TYPE_DECIMAL = 0;
	public final static int ITEM_DATA_TYPE_OCTAL = 1;
	public final static int ITEM_DATA_TYPE_HEXADECIMAL = 2;
	public final static int ITEM_DATA_TYPE_BOOLEAN = 3;

	public final static String RDA_DEFAULT_KEY_DB_MONITOR = "db.odbc.select[<unique short description>]";
	public final static String RDA_DEFAULT_KEY_SSH = "ssh.run[<unique short description>,<ip>,<port>,<encoding>]";
	public final static String RDA_DEFAULT_KEY_TELNET = "telnet.run[<unique short description>,<ip>,<port>,<encoding>]";
	public final static String RDA_DEFAULT_KEY_JMX = "jmx[<object name>,<attribute name>]";

	public final static int SYSMAP_ELEMENT_USE_ICONMAP_ON = 1;
	public final static int SYSMAP_ELEMENT_USE_ICONMAP_OFF = 0;

	public final static int RDA_ICON_PREVIEW_HEIGHT = 24;
	public final static int RDA_ICON_PREVIEW_WIDTH = 24;

	public final static int ITEM_STATUS_ACTIVE = 0;
	public final static int ITEM_STATUS_DISABLED = 1;
	public final static int ITEM_STATUS_NOTSUPPORTED = 3;
	
	public final static int ITEM_STATE_NORMAL = 0;
	public final static int ITEM_STATE_NOTSUPPORTED = 1;

	public final static int ITEM_TYPE_SNMPTRAP = 17;

	public final static int ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV = 0;
	public final static int ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV = 1;
	public final static int ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV = 2;

	public final static int ITEM_AUTHTYPE_PASSWORD = 0;
	public final static int ITEM_AUTHTYPE_PUBLICKEY = 1;
	
	public final static int ITEM_AUTHPROTOCOL_MD5 = 0;
	public final static int ITEM_AUTHPROTOCOL_SHA = 1;
	
	public final static int ITEM_PRIVPROTOCOL_DES = 0;
	public final static int ITEM_PRIVPROTOCOL_AES = 1;

	public final static int ITEM_LOGTYPE_INFORMATION = 1;
	public final static int ITEM_LOGTYPE_WARNING = 2;
	public final static int ITEM_LOGTYPE_ERROR = 4;
	public final static int ITEM_LOGTYPE_FAILURE_AUDIT = 7;
	public final static int ITEM_LOGTYPE_SUCCESS_AUDIT = 8;
	public final static int ITEM_LOGTYPE_CRITICAL = 9;
	public final static int ITEM_LOGTYPE_VERBOSE = 10;

	public final static int GRAPH_ITEM_DRAWTYPE_LINE = 0;
	public final static int GRAPH_ITEM_DRAWTYPE_FILLED_REGION = 1;
	public final static int GRAPH_ITEM_DRAWTYPE_BOLD_LINE = 2;
	public final static int GRAPH_ITEM_DRAWTYPE_DOT = 3;
	public final static int GRAPH_ITEM_DRAWTYPE_DASHED_LINE = 4;
	public final static int GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE = 5;
	public final static int GRAPH_ITEM_DRAWTYPE_BOLD_DOT = 6;

	public final static int MAP_LINK_DRAWTYPE_LINE = 0;
	public final static int MAP_LINK_DRAWTYPE_BOLD_LINE = 2;
	public final static int MAP_LINK_DRAWTYPE_DOT = 3;
	public final static int MAP_LINK_DRAWTYPE_DASHED_LINE = 4;

	public final static int SERVICE_ALGORITHM_NONE = 0; // do not calculate
	public final static int SERVICE_ALGORITHM_MAX = 1; // problem, if one children has a problem
	public final static int SERVICE_ALGORITHM_MIN = 2; // problem, if all children have problems

	public final static float SERVICE_SLA = 99.05f;

	public final static int SERVICE_SHOW_SLA_OFF = 0;
	public final static int SERVICE_SHOW_SLA_ON = 1;

	public final static int SERVICE_STATUS_OK = 0;

	public final static int TRIGGER_MULT_EVENT_DISABLED = 0;
	public final static int TRIGGER_MULT_EVENT_ENABLED = 1;

	public final static int TRIGGER_STATUS_ENABLED = 0;
	public final static int TRIGGER_STATUS_DISABLED = 1;

	public final static int TRIGGER_VALUE_FALSE = 0;
	public final static int TRIGGER_VALUE_TRUE = 1;
	public final static int TRIGGER_VALUE_UNKNOWN = 2; // only in "events" table
	
	public final static int TRIGGER_STATE_NORMAL = 0;
	public final static int TRIGGER_STATE_UNKNOWN = 1;

	public final static int TRIGGER_VALUE_FLAG_NORMAL = 0;
	public final static int TRIGGER_VALUE_FLAG_UNKNOWN = 1;

	public final static int TRIGGER_VALUE_CHANGED_NO = 0;
	public final static int TRIGGER_VALUE_CHANGED_YES = 1;

	public final static int TRIGGER_SEVERITY_NOT_CLASSIFIED = 0;
	public final static int TRIGGER_SEVERITY_INFORMATION = 1;
	public final static int TRIGGER_SEVERITY_WARNING = 2;
	public final static int TRIGGER_SEVERITY_AVERAGE = 3;
	public final static int TRIGGER_SEVERITY_HIGH = 4;
	public final static int TRIGGER_SEVERITY_DISASTER = 5;
	public final static int TRIGGER_SEVERITY_COUNT = 6;

	public final static int ALERT_MAX_RETRIES = 3;

	public final static int ALERT_STATUS_NOT_SENT = 0;
	public final static int ALERT_STATUS_SENT = 1;
	public final static int ALERT_STATUS_FAILED = 2;

	public final static int ALERT_TYPE_MESSAGE = 0;
	public final static int ALERT_TYPE_COMMAND = 1;

	public final static int MEDIA_TYPE_STATUS_ACTIVE = 0;
	public final static int MEDIA_TYPE_STATUS_DISABLED = 1;

	public final static int MEDIA_TYPE_EMAIL = 0;
	public final static int MEDIA_TYPE_EXEC = 1;
	public final static int MEDIA_TYPE_SMS = 2;
	public final static int MEDIA_TYPE_JABBER = 3;
	public final static int MEDIA_TYPE_EZ_TEXTING = 100;

	public final static int EZ_TEXTING_LIMIT_USA = 0;
	public final static int EZ_TEXTING_LIMIT_CANADA = 1;

	public final static String ACTION_DEFAULT_SUBJ_TRIGGER = "{TRIGGER.STATUS}: {TRIGGER.NAME}";
	public final static String ACTION_DEFAULT_SUBJ_AUTOREG = "Auto registration: {HOST.HOST}";
	public final static String ACTION_DEFAULT_SUBJ_DISCOVERY = "Discovery: {DISCOVERY.DEVICE.STATUS} {DISCOVERY.DEVICE.IPADDRESS}";

	public final static String ACTION_DEFAULT_MSG_TRIGGER = "Trigger: {TRIGGER.NAME}\nTrigger status: {TRIGGER.STATUS}\n"+
			"Trigger severity: {TRIGGER.SEVERITY}\nTrigger URL: {TRIGGER.URL}\n\nItem values:\n\n"+
			"1. {ITEM.NAME1} ({HOST.NAME1}:{ITEM.KEY1}): {ITEM.VALUE1}\n"+
			"2. {ITEM.NAME2} ({HOST.NAME2}:{ITEM.KEY2}): {ITEM.VALUE2}\n"+
			"3. {ITEM.NAME3} ({HOST.NAME3}:{ITEM.KEY3}): {ITEM.VALUE3}";
	public final static String ACTION_DEFAULT_MSG_AUTOREG = "Host name: {HOST.HOST}\nHost IP: {HOST.IP}\nAgent port: {HOST.PORT}";
	public final static String ACTION_DEFAULT_MSG_DISCOVERY = "Discovery rule: {DISCOVERY.RULE.NAME}\n\nDevice IP:{DISCOVERY.DEVICE.IPADDRESS}\n"+
			"Device DNS: {DISCOVERY.DEVICE.DNS}\nDevice status: {DISCOVERY.DEVICE.STATUS}\n"+
			"Device uptime: {DISCOVERY.DEVICE.UPTIME}\n\nDevice service name: {DISCOVERY.SERVICE.NAME}\n"+
			"Device service port: {DISCOVERY.SERVICE.PORT}\nDevice service status: {DISCOVERY.SERVICE.STATUS}\n"+
			"Device service uptime: {DISCOVERY.SERVICE.UPTIME}";

	public final static int ACTION_STATUS_ENABLED = 0;
	public final static int ACTION_STATUS_DISABLED = 1;

	public final static int OPERATION_TYPE_MESSAGE = 0;
	public final static int OPERATION_TYPE_COMMAND = 1;
	public final static int OPERATION_TYPE_HOST_ADD = 2;
	public final static int OPERATION_TYPE_HOST_REMOVE = 3;
	public final static int OPERATION_TYPE_GROUP_ADD = 4;
	public final static int OPERATION_TYPE_GROUP_REMOVE = 5;
	public final static int OPERATION_TYPE_TEMPLATE_ADD = 6;
	public final static int OPERATION_TYPE_TEMPLATE_REMOVE = 7;
	public final static int OPERATION_TYPE_HOST_ENABLE = 8;
	public final static int OPERATION_TYPE_HOST_DISABLE = 9;

	public final static int ACTION_EVAL_TYPE_AND_OR = 0;
	public final static int ACTION_EVAL_TYPE_AND = 1;
	public final static int ACTION_EVAL_TYPE_OR = 2;

	public final static int OPERATION_OBJECT_USER = 0;
	public final static int OPERATION_OBJECT_GROUP = 1;

	public final static int LOGFILE_SEVERITY_NOT_CLASSIFIED = 0;
	public final static int LOGFILE_SEVERITY_INFORMATION = 1;
	public final static int LOGFILE_SEVERITY_WARNING = 2;
	public final static int LOGFILE_SEVERITY_AVERAGE = 3;
	public final static int LOGFILE_SEVERITY_HIGH = 4;
	public final static int LOGFILE_SEVERITY_DISASTER = 5;
	public final static int LOGFILE_SEVERITY_AUDIT_SUCCESS = 6;
	public final static int LOGFILE_SEVERITY_AUDIT_FAILURE = 7;

	// screen
	public final static int SCREEN_RESOURCE_GRAPH = 0;
	public final static int SCREEN_RESOURCE_SIMPLE_GRAPH = 1;
	public final static int SCREEN_RESOURCE_MAP = 2;
	public final static int SCREEN_RESOURCE_PLAIN_TEXT = 3;
	public final static int SCREEN_RESOURCE_HOSTS_INFO = 4;
	public final static int SCREEN_RESOURCE_TRIGGERS_INFO = 5;
	public final static int SCREEN_RESOURCE_SERVER_INFO = 6;
	//public final static int SCREEN_RESOURCE_CLOCK = 7;
	public final static int SCREEN_RESOURCE_SCREEN = 8;
	public final static int SCREEN_RESOURCE_TRIGGERS_OVERVIEW = 9;
	public final static int SCREEN_RESOURCE_DATA_OVERVIEW = 10;
	public final static int SCREEN_RESOURCE_URL = 11;
	public final static int SCREEN_RESOURCE_ACTIONS = 12;
	public final static int SCREEN_RESOURCE_EVENTS = 13;
	public final static int SCREEN_RESOURCE_HOSTGROUP_TRIGGERS = 14;
	public final static int SCREEN_RESOURCE_SYSTEM_STATUS = 15;
	public final static int SCREEN_RESOURCE_HOST_TRIGGERS = 16;
	public final static int SCREEN_RESOURCE_HISTORY = 17;
	public final static int SCREEN_RESOURCE_CHART = 18;

	public final static int SCREEN_SORT_TRIGGERS_DATE_DESC = 0;
	public final static int SCREEN_SORT_TRIGGERS_SEVERITY_DESC = 1;
	public final static int SCREEN_SORT_TRIGGERS_HOST_NAME_ASC = 2;
	public final static int SCREEN_SORT_TRIGGERS_TIME_ASC = 3;
	public final static int SCREEN_SORT_TRIGGERS_TIME_DESC = 4;
	public final static int SCREEN_SORT_TRIGGERS_TYPE_ASC = 5;
	public final static int SCREEN_SORT_TRIGGERS_TYPE_DESC = 6;
	public final static int SCREEN_SORT_TRIGGERS_STATUS_ASC = 7;
	public final static int SCREEN_SORT_TRIGGERS_STATUS_DESC = 8;
	public final static int SCREEN_SORT_TRIGGERS_RETRIES_LEFT_ASC = 9;
	public final static int SCREEN_SORT_TRIGGERS_RETRIES_LEFT_DESC = 10;
	public final static int SCREEN_SORT_TRIGGERS_RECIPIENT_ASC = 11;
	public final static int SCREEN_SORT_TRIGGERS_RECIPIENT_DESC = 12;

	public final static int SCREEN_MODE_PREVIEW = 0;
	public final static int SCREEN_MODE_EDIT = 1;
	public final static int SCREEN_MODE_SLIDESHOW = 2;
	public final static int SCREEN_MODE_JS = 3;

	public final static int SCREEN_TYPE_NORMAL = 0;
	public final static int SCREEN_TYPE_TEMPLATED = 1;

	public final static int SCREEN_SIMPLE_ITEM = 0;
	public final static int SCREEN_DYNAMIC_ITEM = 1;

	public final static int SCREEN_REFRESH_TIMEOUT = 30;
	public final static int SCREEN_REFRESH_RESPONSIVENESS = 10;

	public final static int DEFAULT_LATEST_ISSUES_CNT = 20;

	// alignes
	public final static int HALIGN_DEFAULT = 0;
	public final static int HALIGN_CENTER = 0;
	public final static int HALIGN_LEFT = 1;
	public final static int HALIGN_RIGHT = 2;

	public final static int VALIGN_DEFAULT = 0;
	public final static int VALIGN_MIDDLE = 0;
	public final static int VALIGN_TOP = 1;
	public final static int VALIGN_BOTTOM = 2;

	// info module style
	public final static int STYLE_HORIZONTAL = 0;
	public final static int STYLE_VERTICAL = 1;

	// view style [Overview]
	public final static int STYLE_LEFT = 0;
	public final static int STYLE_TOP = 1;

	// time module type
	public final static int TIME_TYPE_LOCAL = 0;
	public final static int TIME_TYPE_SERVER = 1;
	public final static int TIME_TYPE_HOST = 2;

	public final static int FILTER_TASK_SHOW = 0;
	public final static int FILTER_TASK_HIDE = 1;
	public final static int FILTER_TASK_MARK = 2;
	public final static int FILTER_TASK_INVERT_MARK = 3;

	public final static int MARK_COLOR_RED = 1;
	public final static int MARK_COLOR_GREEN = 2;
	public final static int MARK_COLOR_BLUE = 3;

	public final static int PROFILE_TYPE_UNKNOWN = 0;
	public final static int PROFILE_TYPE_ID = 1;
	public final static int PROFILE_TYPE_INT = 2;
	public final static int PROFILE_TYPE_STR = 3;
	public final static int PROFILE_TYPE_ARRAY_ID = 4;
	public final static int PROFILE_TYPE_ARRAY_INT = 5;
	public final static int PROFILE_TYPE_ARRAY_STR = 6;

	public final static int CALC_FNC_MIN = 1;
	public final static int CALC_FNC_AVG = 2;
	public final static int CALC_FNC_MAX = 4;
	public final static int CALC_FNC_ALL = 7;
	public final static int CALC_FNC_LST = 9;

	public final static int SERVICE_TIME_TYPE_UPTIME = 0;
	public final static int SERVICE_TIME_TYPE_DOWNTIME = 1;
	public final static int SERVICE_TIME_TYPE_ONETIME_DOWNTIME = 2;

	public final static int USER_TYPE_IRADAR_USER = 1;
	public final static int USER_TYPE_IRADAR_ADMIN = 2;
	public final static int USER_TYPE_SUPER_ADMIN = 3;

	public final static int RDA_NOT_INTERNAL_GROUP = 0;
	public final static int RDA_INTERNAL_GROUP = 1;

	public final static int GROUP_STATUS_DISABLED = 1;
	public final static int GROUP_STATUS_ENABLED = 0;
	
	public final static int LINE_TYPE_NORMAL = 0;
	public final static int LINE_TYPE_BOLD = 1;

	// IMPORTANT!!! by priority DESC
	public final static int GROUP_GUI_ACCESS_SYSTEM = 0;
	public final static int GROUP_GUI_ACCESS_INTERNAL = 1;
	public final static int GROUP_GUI_ACCESS_DISABLED = 2;
	
	/**
	 * @see access_deny()
	 */
	public final static int ACCESS_EXIT= -1;
	public final static int ACCESS_DENY_OBJECT = 0;
	public final static int ACCESS_DENY_PAGE = 1;
	public final static int ACCESS_DENY_ABSENCE_ITEM = 2;

	public final static int GROUP_API_ACCESS_DISABLED = 0;
	public final static int GROUP_API_ACCESS_ENABLED = 1;

	public final static int GROUP_DEBUG_MODE_DISABLED = 0;
	public final static int GROUP_DEBUG_MODE_ENABLED = 1;

	public final static int PERM_READ_WRITE = 3;
	public final static int PERM_READ = 2;
	public final static int PERM_DENY = 0;

	public final static int PERM_RES_IDS_ARRAY = 1; // return array of nodes id - array(1,2,3,4)
	public final static int PERM_RES_DATA_ARRAY = 2;
	
	public final static int RESOURCE_TYPE_GROUP = 1;

	public final static int PARAM_TYPE_TIME = 0;
	public final static int PARAM_TYPE_COUNTS = 1;

	public final static int RDA_NODE_CHILD = 0;
	public final static int RDA_NODE_LOCAL = 1;
	public final static int RDA_NODE_MASTER = 2;

	public final static int RDA_FLAG_TRIGGER = 0;
	public final static int RDA_FLAG_EVENT = 1;

	public final static int HTTPTEST_AUTH_NONE = 0;
	public final static int HTTPTEST_AUTH_BASIC = 1;
	public final static int HTTPTEST_AUTH_NTLM = 2;

	public final static int HTTPTEST_STATUS_ACTIVE = 0;
	public final static int HTTPTEST_STATUS_DISABLED = 1;

	public final static int HTTPSTEP_ITEM_TYPE_RSPCODE = 0;
	public final static int HTTPSTEP_ITEM_TYPE_TIME = 1;
	public final static int HTTPSTEP_ITEM_TYPE_IN = 2;
	public final static int HTTPSTEP_ITEM_TYPE_LASTSTEP = 3;
	public final static int HTTPSTEP_ITEM_TYPE_LASTERROR = 4;

	public final static String EVENT_ACK_DISABLED = "0";
	public final static String EVENT_ACK_ENABLED = "1";

	public final static String EVENT_NOT_ACKNOWLEDGED = "0";
	public final static String EVENT_ACKNOWLEDGED = "1";

	public final static int EVENTS_NOFALSEFORB_STATUS_ALL = 0; // used with TRIGGERS_OPTION_NOFALSEFORB
	public final static int EVENTS_NOFALSEFORB_STATUS_FALSE = 1; // used with TRIGGERS_OPTION_NOFALSEFORB
	public final static int EVENTS_NOFALSEFORB_STATUS_TRUE = 2; // used with TRIGGERS_OPTION_NOFALSEFORB

	public final static int EVENT_SOURCE_TRIGGERS = 0;
	public final static int EVENT_SOURCE_DISCOVERY = 1;
	public final static int EVENT_SOURCE_AUTO_REGISTRATION = 2;
	public final static int EVENT_SOURCE_INTERNAL = 3;

	public final static int EVENT_OBJECT_TRIGGER = 0;
	public final static int EVENT_OBJECT_DHOST = 1;
	public final static int EVENT_OBJECT_DSERVICE = 2;
	public final static int EVENT_OBJECT_AUTOREGHOST = 3;
	public final static int EVENT_OBJECT_ITEM = 4;
	public final static int EVENT_OBJECT_LLDRULE = 5;

	public final static int GRAPH_YAXIS_TYPE_CALCULATED = 0;
	public final static int GRAPH_YAXIS_TYPE_FIXED = 1;
	public final static int GRAPH_YAXIS_TYPE_ITEM_VALUE = 2;

	public final static int GRAPH_YAXIS_SIDE_LEFT = 0;
	public final static int GRAPH_YAXIS_SIDE_RIGHT = 1;

	public final static int GRAPH_ITEM_SIMPLE = 0;
	public final static int GRAPH_ITEM_SUM = 2;

	public final static int GRAPH_TYPE_NORMAL = 0;
	public final static int GRAPH_TYPE_STACKED = 1;
	public final static int GRAPH_TYPE_PIE = 2;
	public final static int GRAPH_TYPE_EXPLODED = 3;
	public final static int GRAPH_TYPE_3D = 4;
	public final static int GRAPH_TYPE_3D_EXPLODED = 5;
	public final static int GRAPH_TYPE_BAR = 6;
	public final static int GRAPH_TYPE_COLUMN = 7;
	public final static int GRAPH_TYPE_BAR_STACKED = 8;
	public final static int GRAPH_TYPE_COLUMN_STACKED = 9;

	public final static int GRAPH_3D_ANGLE = 70;

	public final static int GRAPH_STACKED_ALFA = 15; // 0..100 transparency

	public final static String GRAPH_ZERO_LINE_COLOR_LEFT = "AAAAAA";
	public final static String GRAPH_ZERO_LINE_COLOR_RIGHT = "888888";

	public final static String GRAPH_TRIGGER_LINE_OPPOSITE_COLOR = "000";

	public final static int RDA_MAX_TREND_DIFF = 3600;

	public final static int RDA_GRAPH_MAX_SKIP_CELL = 16;
	public final static int RDA_GRAPH_MAX_SKIP_DELAY = 4;

	public final static int DOBJECT_STATUS_UP = 0;
	public final static int DOBJECT_STATUS_DOWN = 1;
	public final static int DOBJECT_STATUS_DISCOVER = 2; // only for events
	public final static int DOBJECT_STATUS_LOST = 3; // generated by discovery

	public final static int DRULE_STATUS_ACTIVE = 0;
	public final static int DRULE_STATUS_DISABLED = 1;

	public final static int DSVC_STATUS_ACTIVE = 0;
	public final static int DSVC_STATUS_DISABLED = 1;

	public final static int SVC_SSH = 0;
	public final static int SVC_LDAP = 1;
	public final static int SVC_SMTP = 2;
	public final static int SVC_FTP = 3;
	public final static int SVC_HTTP = 4;
	public final static int SVC_POP = 5;
	public final static int SVC_NNTP = 6;
	public final static int SVC_IMAP = 7;
	public final static int SVC_TCP = 8;
	public final static int SVC_AGENT = 9;
	public final static int SVC_SNMPv1 = 10;
	public final static int SVC_SNMPv2c = 11;
	public final static int SVC_ICMPPING = 12;
	public final static int SVC_SNMPv3 = 13;
	public final static int SVC_HTTPS = 14;
	public final static int SVC_TELNET = 15;

	public final static int DHOST_STATUS_ACTIVE = 0;
	public final static int DHOST_STATUS_DISABLED = 1;

	public final static int IM_FORCED = 0;
	public final static int IM_ESTABLISHED = 1;
	public final static int IM_TREE = 2;

	public final static int EXPRESSION_TYPE_INCLUDED = 0;
	public final static int EXPRESSION_TYPE_ANY_INCLUDED = 1;
	public final static int EXPRESSION_TYPE_NOT_INCLUDED = 2;
	public final static int EXPRESSION_TYPE_TRUE = 3;
	public final static int EXPRESSION_TYPE_FALSE = 4;

	public final static int HOST_INVENTORY_DISABLED = -1;
	public final static int HOST_INVENTORY_MANUAL = 0;
	public final static int HOST_INVENTORY_AUTOMATIC = 1;

	public final static String EXPRESSION_VALUE_TYPE_UNKNOWN = "#ERROR_VALUE_TYPE#";
	public final static String EXPRESSION_HOST_UNKNOWN = "#ERROR_HOST#";
	public final static String EXPRESSION_HOST_ITEM_UNKNOWN = "#ERROR_ITEM#";
	public final static String EXPRESSION_NOT_A_MACRO_ERROR = "#ERROR_MACRO#";
	public final static String EXPRESSION_FUNCTION_UNKNOWN = "#ERROR_FUNCTION#";

	public final static int AVAILABLE_NOCACHE = 0;	// take available objects not from cache

	public final static String SBR = "<br/>\n";
	public final static String SPACE = "&nbsp;";
	public final static String RARR = "&rArr;";
	public final static String SQUAREBRACKETS = "%5B%5D";
	public final static String NAME_DELIMITER = ": ";
	public final static String UNKNOWN_VALUE = "-";

	
	public final static int RDA_MBSTRINGS_OVERLOADED = 1;

	public final static int REGEXP_INCLUDE = 0;
	public final static int REGEXP_EXCLUDE = 1;

	// suffixes
	public final static String RDA_BYTE_SUFFIXES = "KMGT";
	public final static String RDA_TIME_SUFFIXES = "smhdw";

	// preg
	public final static String RDA_PREG_PRINT = "^\\u0000-\\u001F";
	public final static String RDA_PREG_MACRO_NAME = "([A-Z0-9\\._]+)";
	public final static String RDA_PREG_MACRO_NAME_LLD = "([A-Z0-9\\._]+)";
	public final static String RDA_PREG_INTERNAL_NAMES = "([0-9a-zA-Z_\\. \\-]+)"; // !!! Don't forget sync code with C !!!
	public final static String RDA_PREG_PARAMS = "(["+RDA_PREG_PRINT+"]+?)?";
	public final static String RDA_PREG_SIGN = "([&|><=+*\\/#\\-])";
	public final static String RDA_PREG_NUMBER = "([\\-+]?[0-9]+[.]?[0-9]*["+RDA_BYTE_SUFFIXES+"]?)";
	public final static String RDA_PREG_DEF_FONT_STRING = "/^[0-9\\.:% ]+$/";
	public final static String RDA_PREG_DNS_FORMAT = "([0-9a-zA-Z_\\.\\-$]|\\{\\$?"+RDA_PREG_MACRO_NAME+"\\})*";
	public final static String RDA_PREG_HOST_FORMAT = RDA_PREG_INTERNAL_NAMES;
	public final static String RDA_PREG_NODE_FORMAT = RDA_PREG_INTERNAL_NAMES;
	public final static String RDA_PREG_FUNCTION_FORMAT = "([a-z]+(\\("+RDA_PREG_PARAMS+"\\)))";
	public final static String RDA_PREG_MACRO_NAME_FORMAT = "(\\{[A-Z\\.]+\\})";
	public final static String RDA_PREG_EXPRESSION_SIMPLE_MACROS = "(\\{TRIGGER\\.VALUE\\})";
	public final static String RDA_PREG_EXPRESSION_USER_MACROS = "(\\{\\$"+RDA_PREG_MACRO_NAME+"\\})";


	// !!! should be used with "x" modifier
	/**
'(
	(?P>param) # match recursive parameter group
	|
	(\" # match quoted string
		(
			((\\\\)+?[^\\\\]) # match any amount of backslash with non-backslash ending
			|
			[^\"\\\\] # match any character except \ or "
		)*? # match \" or any character except "
	\")
	|
	[^\"\[\],][^,\]]*? #match unquoted string - any character except " [ ] and , at begining and any character except , and ] afterwards
	|
	() # match empty and only empty part
)'
	 */
	public final static String RDA_PREG_ITEM_KEY_PARAMETER_FORMAT = 
		"(" +
			"(?>param)" +
			"|" +
			"(\\\"" +
				"(" +
					"((\\\\\\\\)+?[^\\\\\\\\])" +
					"|" +
					"[^\\\"\\\\\\\\]" +
				")*?" +
			"\\\")" +
			"|" +
			"[^\\\"\\[\\],][^,\\]]*?" +
			"|" +
			"()" +
		")";
	/**
'([0-9a-zA-Z_\. \-]+? # match key
(?P<param>( # name parameter group used in recursion
	\[ # match opening bracket
		(
			\s*?'.RDA_PREG_ITEM_KEY_PARAMETER_FORMAT .' # match spaces and parameter
			(
				\s*?,\s*? # match spaces, comma and spaces
				'.RDA_PREG_ITEM_KEY_PARAMETER_FORMAT .' # match parameter
			)*? # match spaces, comma, spaces, parameter zero or more times
			\s*? #matches spaces
		)
	\] # match closing bracket
))*? # matches non comma seperated brackets with parameters zero or more times
)'
	 */
	public final static String RDA_PREG_ITEM_KEY_FORMAT = 
		"([0-9a-zA-Z_\\. \\-]+?" +
		"(?<param>(" +
			"\\[" +
				"(" +
					"\\s*?"+RDA_PREG_ITEM_KEY_PARAMETER_FORMAT+"" +
					"(" +
						"\\s*?,\\s*?" +
						""+RDA_PREG_ITEM_KEY_PARAMETER_FORMAT+"" +
					")*?" +
					"\\s*?" +
				")" +
			"\\]" +
		"))*?" +
		")";
	
	// regexp ids
	public final static int RDA_KEY_ID = 1;
	public final static int RDA_KEY_NAME_ID = 2;
	public final static int RDA_KEY_PARAM_ID = 6;

	public final static int RDA_HISTORY_COUNT = 5;

	public final static int RDA_USER_ONLINE_TIME = 600; // 10min
	public final static String RDA_GUEST_USER = "guest";

	public final static int RDA_FAVORITES_ALL = -1;

	// allow for testing
	public final static int RDA_ALLOW_UNICODE = 1;

	// IPMI
	public final static int IPMI_AUTHTYPE_DEFAULT = -1;
	public final static int IPMI_AUTHTYPE_NONE = 0;
	public final static int IPMI_AUTHTYPE_MD2 = 1;
	public final static int IPMI_AUTHTYPE_MD5 = 2;
	public final static int IPMI_AUTHTYPE_STRAIGHT = 4;
	public final static int IPMI_AUTHTYPE_OEM = 5;
	public final static int IPMI_AUTHTYPE_RMCP_PLUS = 6;

	public final static int IPMI_PRIVILEGE_CALLBACK = 1;
	public final static int IPMI_PRIVILEGE_USER = 2;
	public final static int IPMI_PRIVILEGE_OPERATOR = 3;
	public final static int IPMI_PRIVILEGE_ADMIN = 4;
	public final static int IPMI_PRIVILEGE_OEM = 5;

	public final static int RDA_HAVE_IPV6 = 1;


	public final static int RDA_SOCKET_TIMEOUT = 10;					// socket timeout limit
	public final static int RDA_SOCKET_BYTES_LIMIT = 1048576;// socket response size limit, 1048576 is 1MB in bytes

	// value is also used in servercheck.js file
	public final static int SERVER_CHECK_INTERVAL = 30;

	// XML export|import tags
	public final static String XML_TAG_MACRO = "macro";
	public final static String XML_TAG_HOST = "host";
	public final static String XML_TAG_HOSTINVENTORY = "host_inventory";
	public final static String XML_TAG_ITEM = "item";
	public final static String XML_TAG_TRIGGER = "trigger";
	public final static String XML_TAG_GRAPH = "graph";
	public final static String XML_TAG_GRAPH_ELEMENT = "graph_element";
	public final static String XML_TAG_DEPENDENCY = "dependency";

	public final static String RDA_DEFAULT_IMPORT_HOST_GROUP = "Imported hosts";

	// API errors
	public final static int RDA_API_ERROR_INTERNAL = 111;
	public final static int RDA_API_ERROR_PARAMETERS = 100;
	public final static int RDA_API_ERROR_PERMISSIONS = 120;
	public final static int RDA_API_ERROR_NO_AUTH = 200;
	public final static int RDA_API_ERROR_NO_METHOD = 300;

	public final static String API_OUTPUT_SHORTEN = "shorten";
	public final static String API_OUTPUT_REFER = "refer";
	public final static String API_OUTPUT_EXTEND = "extend";
	public final static String API_OUTPUT_COUNT = "count";
	public final static String API_OUTPUT_CUSTOM = "custom";

	public final static int SEC_PER_MIN = 60;
	public final static int SEC_PER_HOUR = 3600;
	public final static int SEC_PER_DAY = 86400;
	public final static int SEC_PER_WEEK = 604800; // 7 * SEC_PER_DAY
	public final static int SEC_PER_MONTH = 2592000; // 30 * SEC_PER_DAY
	public final static int SEC_PER_YEAR = 31536000; // 365 * SEC_PER_DAY

	public final static int RDA_JAN_2038 = 2145888000;
	
	public final static int DAY_IN_YEAR = 365;

	public final static int RDA_MIN_PORT_NUMBER = 1;
	public final static int RDA_MAX_PORT_NUMBER = 65534;

	// input fields
	public final static int RDA_TEXTBOX_STANDARD_SIZE = 50;
	public final static int RDA_TEXTBOX_SMALL_SIZE = 25;
	public final static int RDA_TEXTBOX_FILTER_SIZE = 20;
	public final static int RDA_TEXTAREA_STANDARD_WIDTH = 312;
	public final static int RDA_TEXTAREA_BIG_WIDTH = 524;
	public final static int RDA_TEXTAREA_STANDARD_ROWS = 7;

	// validation
	public final static String DB_ID = "({}>=0&&bccomp({},\"10000000000000000000\")<0)&&";
	public final static String NOT_EMPTY = "({}!='')&&";
	public final static String NOT_ZERO = "({}!=0)&&";
	public final static String NO_TRIM = "NO_TRIM";

	public final static int RDA_VALID_OK = 0;
	public final static int RDA_VALID_ERROR = 1;
	public final static int RDA_VALID_WARNING = 2;

	// user default theme
	public final static String THEME_DEFAULT = "default";

	// the default theme
	public final static String RDA_DEFAULT_THEME = "default";

	public final static String IRADAR_HOMEPAGE = "http://www.i-soft.com.cn";
	
	// non translatable date formats
	public final static String TIMESTAMP_FORMAT = "YmdHis";
	public final static String TIMESTAMP_FORMAT_ZERO_TIME = "Ymd0000";

	// actions
	public final static int LONG_DESCRIPTION = 0;
	public final static int SHORT_DESCRIPTION = 1;

	// availability report modes
	public final static int AVAILABILITY_REPORT_BY_HOST = 0;
	public final static int AVAILABILITY_REPORT_BY_TEMPLATE = 1;
	
	public final static int QUEUE_OVERVIEW = 0;
	public final static int QUEUE_OVERVIEW_BY_PROXY = 1;
	public final static int QUEUE_DETAILS = 2;
	
	// item count to display in the details queue
	public final static int QUEUE_DETAIL_ITEM_COUNT = 500;
	
	public static boolean RDA_PAGE_NO_THEME = false;
	
	public static String audio = null;
	
}
