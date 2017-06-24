package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_replace;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.REGEXP_EXCLUDE;
import static com.isoft.iradar.inc.Defines.REGEXP_INCLUDE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.ItemsUtil.get_realhost_by_itemid;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.TriggersUtil.check_right_on_trigger_by_expression;
import static com.isoft.iradar.inc.TriggersUtil.construct_expression;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.splitByFirstLevel;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.jdk.util.regex.IPattern;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TrLogformAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Trigger form"));
		page("file", "tr_logform.action");
		page("type", detect_page_type(PAGE_TYPE_HTML));

		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"description",	array(T_RDA_STR, O_OPT,  null,			NOT_EMPTY,			"isset({save_trigger})", _("description")),
			"itemid",			array(T_RDA_INT, O_OPT,	 P_SYS,			DB_ID,				"isset({save_trigger})"),
			"sform",			array(T_RDA_INT, O_OPT,  null,			IN("0,1"),			null),
			"sitems",			array(T_RDA_INT, O_OPT,  null,			IN("0,1"),			null),
			"triggerid",		array(T_RDA_INT, O_OPT,  P_SYS,			DB_ID,				null),
			"type",				array(T_RDA_INT, O_OPT,  null,			IN("0,1"),			null),
			"priority",			array(T_RDA_INT, O_OPT,  null,			IN("0,1,2,3,4,5"),	"isset({save_trigger})"),
			"expressions",	array(T_RDA_STR, O_OPT,	 null,			NOT_EMPTY,			"isset({save_trigger})", _("expressions")),
			"expr_type",		array(T_RDA_INT, O_OPT,  null,			IN("0,1"),			null),
			"comments",	array(T_RDA_STR, O_OPT,  null,			null,				null),
			"url",					array(T_RDA_STR, O_OPT,  null,			null,				null),
			"status",			array(T_RDA_INT, O_OPT,  null,			IN("0,1"),			null),
			"form_refresh",	array(T_RDA_INT, O_OPT,	 null,			null,				null),
			"save_trigger",	array(T_RDA_STR, O_OPT,	 P_SYS|P_ACT,	null,				null),
			"keys ", 			array(T_RDA_STR, O_OPT,  null,			null,				null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if ((!empty(get_request("itemid")) && !API.Item(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"itemid").asLong()))
				|| (!empty(get_request("triggerid")) && !API.Trigger(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"triggerid").asLong()))) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		String _itemid = get_request("itemid", "0");
		
		//------------------------ <ACTIONS> ---------------------------
		if (isset(_REQUEST,"save_trigger")) {
			show_messages();

			CArray<Map> _exprs = get_request("expressions", null);
			String _expression = null;
			if(!empty(_exprs) && !empty(_expression = construct_expression(getIdentityBean(), executor, Nest.value(_REQUEST,"itemid").asString(), _exprs))){
				if(!check_right_on_trigger_by_expression(this.getIdentityBean(), executor, PERM_READ_WRITE, _expression)) access_deny();

				int _status;
				if (isset(_REQUEST, "status")) {
					_status = TRIGGER_STATUS_DISABLED;
				} else {
					_status = TRIGGER_STATUS_ENABLED;
				}

				//if(isset(_REQUEST,"type"))	{ _type=TRIGGER_MULT_EVENT_ENABLED; }
				//else{ _type=TRIGGER_MULT_EVENT_DISABLED; }
				int _type = TRIGGER_MULT_EVENT_ENABLED;

				final Map _trigger = map();
				boolean _result;
				int _audit_action;
				String _triggerid = null;
				if(isset(_REQUEST,"triggerid")){
					CTriggerGet toptions = new CTriggerGet();
					toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
					toptions.setOutput(API_OUTPUT_EXTEND);
					toptions.setSelectDependencies(API_OUTPUT_REFER);
					CArray<Map> _triggersData = API.Trigger(getIdentityBean(), executor).get(toptions);
					Map _triggerData = reset(_triggersData);

					if(!empty(Nest.value(_triggerData,"templateid").$())){
						Nest.value(_REQUEST,"description").$(Nest.value(_triggerData,"description").$());
						_expression = Nest.as(explode_exp(this.getIdentityBean(), executor,Nest.value(_triggerData,"expression").asString())).asString();
					}

					Nest.value(_trigger,"triggerid").$(Nest.value(_REQUEST,"triggerid").$());
					Nest.value(_trigger,"expression").$(_expression);
					Nest.value(_trigger,"description").$(Nest.value(_REQUEST,"description").$());
					Nest.value(_trigger,"type").$(_type);
					Nest.value(_trigger,"priority").$(Nest.value(_REQUEST,"priority").$());
					Nest.value(_trigger,"status").$(_status);
					Nest.value(_trigger,"comments").$(Nest.value(_REQUEST,"comments").$());
					Nest.value(_trigger,"url").$(Nest.value(_REQUEST,"url").$());

					DBstart(executor);
					_result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Trigger(getIdentityBean(), executor).update(array(_trigger)));
						}
					});
					_result = DBend(executor, _result);
					
					_triggerid = Nest.value(_REQUEST,"triggerid").asString();
					_audit_action = AUDIT_ACTION_UPDATE;
					show_messages(_result, _("Trigger updated"), _("Cannot update trigger"));
				} else {
					Nest.value(_trigger,"expression").$(_expression);
					Nest.value(_trigger,"description").$(Nest.value(_REQUEST,"description").$());
					Nest.value(_trigger,"type").$(_type);
					Nest.value(_trigger,"priority").$(Nest.value(_REQUEST,"priority").$());
					Nest.value(_trigger,"status").$(_status);
					Nest.value(_trigger,"comments").$(Nest.value(_REQUEST,"comments").$());
					Nest.value(_trigger,"url").$(Nest.value(_REQUEST,"url").$());

					DBstart(executor);
					CArray<Long[]> ctTriggers = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Trigger(getIdentityBean(), executor).create(array(_trigger));
						}
					}, null);
						
					if(!empty(ctTriggers)){
						CTriggerGet toptions = new CTriggerGet();
						toptions.setTriggerIds(Nest.array(ctTriggers,"triggerids").asLong());
						toptions.setOutput(API_OUTPUT_EXTEND);
						CArray<Map> _db_triggers = API.Trigger(getIdentityBean(), executor).get(toptions);

						_result = true;
						Map _db_trigger = reset(_db_triggers);
						_triggerid = Nest.value(_db_trigger,"triggerid").asString();
					} else {
						_result = false;
					}
					
					_result = DBend(executor, _result);
					
					// _result = _triggerid;
					_audit_action = AUDIT_ACTION_ADD;
					show_messages(_result, _("Trigger added"), _("Cannot add trigger"));
				}

				if(_result){
					add_audit(getIdentityBean(), executor, _audit_action, AUDIT_RESOURCE_TRIGGER, _("Trigger")+" ["+_triggerid+"] ["+Nest.value(_trigger,"description").asString(true)+"]");
					unset(_REQUEST,"sform");

					rda_add_post_js("closeForm(\"policy_items.action\");");
				}
			}
		}
		//------------------------ </ACTIONS> --------------------------

		//------------------------ <FORM> ---------------------------
		if(isset(_REQUEST,"sform")){
			CFormTable _frmTRLog = new CFormTable(_("Trigger"),"tr_logform.action","POST",null,"sform");
			_frmTRLog.setName("web.triggerlog.service");
//			_frmTRLog.setHelp("web.triggerlog.service.action");
			_frmTRLog.setTableClass("formlongtable formtable");
			_frmTRLog.addVar("form_refresh",get_request("form_refresh",1));

			if(isset(_REQUEST,"triggerid")) _frmTRLog.addVar("triggerid",Nest.value(_REQUEST,"triggerid").$());

			
			String _description = null, _comments = null, _url = null, _expr_incase = null;
			CArray<Map> _expressions = array();
			int _type=0, _priority=0, _status=0;
			
			if(isset(_REQUEST,"triggerid") && !isset(_REQUEST,"form_refresh")){
				_frmTRLog.addVar("form_refresh",get_request("form_refresh",1));

				Map params = new HashMap();
				params.put("triggerid", Nest.value(_REQUEST,"triggerid").$());
				String _sql = "SELECT DISTINCT f.functionid, f.function, f.parameter, t.expression, "+
										" t.description, t.priority, t.comments, t.url, t.status, t.type"+
							" FROM functions f, triggers t, items i "+
							" WHERE t.triggerid=#{triggerid}"+
								" AND i.itemid=f.itemid "+
								" AND f.triggerid = t.triggerid "+
								" AND i.value_type IN ("+ITEM_VALUE_TYPE_LOG+" , "+ITEM_VALUE_TYPE_TEXT+", "+ITEM_VALUE_TYPE_STR+")";

				CArray<Map> _res = DBselect(executor,_sql,params);
				
				String _expression = null, _expr_v;
				CArray _functions = array(), _functionid = array();
				for(Map _rows : _res){
					_description = Nest.value(_rows,"description").asString();
					_expression = Nest.value(_rows,"expression").asString();
					_type = Nest.value(_rows,"type").asInteger();
					_priority = Nest.value(_rows,"priority").asInteger();
					_comments = Nest.value(_rows,"comments").asString();
					_url = Nest.value(_rows,"url").asString();
					_status = Nest.value(_rows,"status").asInteger();
					
					_functionid.add(IPattern.compile("\\{"+Nest.value(_rows,"functionid").asString()+"\\}", IPattern.UNICODE_CASE));  // /Uu  TODO: 需要处理U的非贪婪
					_functions.add(Nest.value(_rows,"function").asString()+"("+Nest.value(_rows,"parameter").asString()+")");
				}

				_expr_v = _expression;
				_expression = preg_replace(_functionid, _functions, _expression);
				_expr_incase = _expression;

				_expression = preg_replace(IPattern.compile("\\(\\(\\((.+?)\\)\\) &", IPattern.CASE_INSENSITIVE), "(($1) &", _expression);
				_expression = preg_replace(IPattern.compile("\\(\\(\\((.+?)\\)\\)$", IPattern.CASE_INSENSITIVE), "(($1)", _expression);

				_expr_v = preg_replace(IPattern.compile("\\(\\(\\((.+?)\\)\\) &", IPattern.CASE_INSENSITIVE), "(($1) &", _expr_v);
				_expr_v = preg_replace(IPattern.compile("\\(\\(\\((.+?)\\)\\)$", IPattern.CASE_INSENSITIVE), "(($1)", _expr_v);

				String[] _expressionSplits = splitByFirstLevel(_expression);
				String[] _expr_vSplits = splitByFirstLevel(_expr_v);

				int _id =0;
				for(String _expr: _expressionSplits) {
					_expr = preg_replace(IPattern.compile("^\\((.*)\\)$", IPattern.UNICODE_CASE),"$1",_expr);

					String _value = preg_replace("([=|#]0)","",_expr);
					_value = preg_replace(IPattern.compile("^\\((.*)\\)$", IPattern.UNICODE_CASE),"$1",_value); // removing wrapping parentheses

					Nest.value(_expressions, _id, "value").$(trim(_value));
					Nest.value(_expressions, _id, "type").$((rda_strpos(_expr,"#0",rda_strlen(_expr)-3) == -1)?(REGEXP_EXCLUDE):(REGEXP_INCLUDE));
					
					_id++;
				}

				_id =0;
				for(String _expr: _expr_vSplits) {
					_expr = preg_replace(IPattern.compile("^\\((.*)\\)$", IPattern.UNICODE_CASE),"$1",_expr);
					String _value = preg_replace(IPattern.compile("\\((.*?)\\)[=|#]0"),"$1",_expr);
					_value = preg_replace(IPattern.compile("^\\((.*)\\)$", IPattern.UNICODE_CASE),"$1",_value);

					if (rda_strpos(_expr,"#0",rda_strlen(_expr)-3) == -1) {
		//REGEXP_EXCLUDE
						_value = str_replace("&", " OR ", _value);
						_value = str_replace("|", " AND ", _value);
					} else {
		//EGEXP_INCLUDE
						_value = str_replace("&", " AND ", _value);
						_value = str_replace("|", " OR ", _value);
					}

					_value = preg_replace(_functionid,_functions,_value);
					_value = preg_replace("([=|#]0)","",_value);

					Nest.value(_expressions, _id, "view").$(trim(_value));
					
					_id++;
				}
			}
			else{
				_description = get_request("description","");
				_expressions = get_request("expressions",array());
				_type = get_request("type",0);
				_priority = get_request("priority",0);
				_comments = get_request("comments","");
				_url = get_request("url","");
				_status = get_request("status",0);
			}
				
			CArray<Map> _keys = get_request("keys",array());

			_frmTRLog.addRow(_("Description"), new CTextBox("description", _description, 80));

			String _itemName = "";

			CArray<Map> _dbItems = DBfetchArray(DBselect(executor,
				"SELECT itemid,hostid,name,key_,templateid"+
				" FROM items"+
				" WHERE itemid=#{itemid}"
				, map("itemid", _itemid)
			));
			_dbItems = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, _dbItems);
			Map _dbItem = reset(_dbItems);

			if (Nest.value(_dbItem,"templateid").asBoolean()) {
				Map _template = get_realhost_by_itemid(getIdentityBean(), executor, Nest.value(_dbItem,"templateid").asString());
				_itemName = Nest.value(_template, "host").$()+NAME_DELIMITER+Nest.value(_dbItem,"name_expanded").$();
			}
			else {
				_itemName = Nest.value(_dbItem,"name_expanded").asString();
			}

			CTextBox _ctb = new CTextBox("item", _itemName, 80);
			_ctb.setAttribute("id","item");
			_ctb.setAttribute("disabled","disabled");

			String _script = "javascript: return PopUp(\"popup.action?dstfrm="+_frmTRLog.getName()+"&dstfld1=itemid&dstfld2=item&srctbl=items&srcfld1=itemid&srcfld2=name\",850,450);";
			CSubmit _cbtn = new CSubmit("select_item",_("Select"),_script,"formlist");

			_frmTRLog.addRow(_("Item"), array(_ctb, _cbtn));
			_frmTRLog.addVar("itemid",_itemid);


			CComboBox _exp_select = new CComboBox("expr_type");
			_exp_select.setAttribute("id","expr_type");
			_exp_select.addItem(REGEXP_INCLUDE,_("Include"));
			_exp_select.addItem(REGEXP_EXCLUDE,_("Exclude"));


			_ctb = new CTextBox("expression","",80);
			_ctb.setAttribute("id","logexpr");

			CButton _cb = new CButton("add_exp",_("Add"),"javascript: add_logexpr();","formlist");
			CButton _cbAdd = new CButton("add_key_and", _("AND"), "javascript: add_keyword_and();","formlist");
			CButton _cbOr = new CButton("add_key_or", _("OR"), "javascript: add_keyword_or();","formlist");
			CCheckBox _cbIregexp = new CCheckBox("iregexp", false, null,1);


			_frmTRLog.addRow(_("Expression"), array(_ctb,BR(),_cbIregexp,_("iregexp"),SPACE,_cbAdd,SPACE,_cbOr,SPACE,_exp_select,SPACE, _cb));

			CTableInfo _keyTable = new CTableInfo(null);
			_keyTable.setAttribute("id","key_list");
			_keyTable.setHeader(array(_("Keyword"), _("Type"), _("Operations")));

			CTableInfo _table = new CTableInfo(null);
			_table.setAttribute("id","exp_list");
			_table.setHeader(array(_("Expression"), _("Type"), _("Position"), _("Operations")));

			long _maxid=0;

			boolean _bExprResult = true;
			CTriggerExpression _expressionData = new CTriggerExpression();
			if (isset(_REQUEST,"triggerid") && !isset(_REQUEST,"save_trigger")
					&& !_expressionData.parse(empty(_expressions) ? "" : construct_expression(getIdentityBean(), executor, _itemid, _expressions))
					&& !isset(_REQUEST,"form_refresh")) {

				info(_expressionData.error);

				unset(_expressions);
				Nest.value(_expressions, 0, "value").$(_expr_incase);
				Nest.value(_expressions, 0, "type").$(0);
				Nest.value(_expressions, 0, "view").$(_expr_incase);
				_bExprResult = false;
			}

			for(Entry<Object, Map> entry: _expressions.entrySet()) {
				Long _id = EasyObject.asLong(entry.getKey());
				Map _expr = entry.getValue();

				CImg _imgup = new CImg("images/general/arrow_up.png","up",12,14);
				_imgup.setAttribute("onclick","javascript:  element_up(\"logtr"+_id+"\");");
				_imgup.setAttribute("onmouseover","javascript: this.style.cursor = \"pointer\";");

				CImg _imgdn = new CImg("images/general/arrow_down.png","down",12,14);
				_imgdn.setAttribute("onclick","javascript:  element_down(\"logtr"+_id+"\");");
				_imgdn.setAttribute("onmouseover","javascript: this.style.cursor = \"pointer\";");

				CSpan _del_url = new CSpan(_("Delete"),"link");
				_del_url.setAttribute("onclick", "javascript: if(confirm(\""+_("Delete expression?")+"\")) remove_expression(\"logtr"+_id+"\"); return false;");

				CRow _row = new CRow(array(htmlspecialchars(Nest.value(_expr,"view").asString()),((Nest.value(_expr, "type").asInteger()==REGEXP_INCLUDE)?_("Include"):_("Exclude")),array(_imgup,SPACE,_imgdn),_del_url));
				_row.setAttribute("id","logtr"+_id);
				_table.addRow(_row);

				_frmTRLog.addVar("expressions["+_id+"][value]",Nest.value(_expr,"value").$());
				_frmTRLog.addVar("expressions["+_id+"][type]",Nest.value(_expr,"type").$());
				_frmTRLog.addVar("expressions["+_id+"][view]",Nest.value(_expr,"view").$());

				_maxid = (_maxid<_id)?_id:_maxid;
			}
			rda_add_post_js("logexpr_count="+(_maxid+1));

			_maxid=0;
			for(Entry<Object, Map> entry: _keys.entrySet()) {
				Long _id = EasyObject.asLong(entry.getKey());
				Map _val = entry.getValue();
				CLink _del_url = new CLink(_("Delete"),"#","action","javascript: if(confirm(\""+_("Delete keyword?")+"\")) remove_keyword(\"keytr"+_id+"\"); return false;");
				CRow _row = new CRow(array(htmlspecialchars(Nest.value(_val,"value").asString()),Nest.value(_val,"type").$(),_del_url));
				_row.setAttribute("id","keytr"+_id);
				_keyTable.addRow(_row);

				_frmTRLog.addVar("keys["+_id+"][value]",Nest.value(_val,"value").$());
				_frmTRLog.addVar("keys["+_id+"][type]",Nest.value(_val,"type").$());

				_maxid = (_maxid<_id)?_id:_maxid;
			}
			rda_add_post_js("key_count="+(_maxid+1));

			_frmTRLog.addRow(SPACE, _keyTable);
			_frmTRLog.addRow(SPACE, _table);

			CComboBox _sev_select = new CComboBox("priority", _priority);
			_sev_select.addItems(getSeverityCaption(getIdentityBean(), executor));
			_frmTRLog.addRow(_("Severity"), _sev_select);
			_frmTRLog.addRow(_("Comments"), new CTextArea("comments", _comments));
			_frmTRLog.addRow(_("URL"), new CTextBox("url", _url, 80));
			_frmTRLog.addRow(_("Disabled"), new CCheckBox("status", _status == TRIGGER_STATUS_DISABLED, null, 1));
			_frmTRLog.addItemToBottomRow(new CSubmit("save_trigger", _("Save"), "javascript:var _form=jQuery(this).parents('form');var _action=_form.attr('action');_action += \"?saction=1\"; _form.attr('action',_action);","buttonorange"));
			_frmTRLog.addItemToBottomRow(SPACE);
			_frmTRLog.addItemToBottomRow(new CButton("cancel", _("Cancel"), "javascript: close_window();","buttongray"));

			if (_bExprResult) {
				
				String headHide_JS = "var $open_body = jQuery(\'body.originalblue\'), \n"+
									 "$header = $open_body.find(\'.header:first\'); \n"+
									 "if ($header.length && !$header.is(':hidden')&&$header.find(\':input:visible\').length == 0) { \n"+
									 "$open_body.css('opacity',0);	$header.hide();$open_body.css(\'opacity\', 1); \n"+
									 "} \n";
				_frmTRLog.show();
				JsUtil.insert_js(headHide_JS,true);
			
			}
		}
	}
}
