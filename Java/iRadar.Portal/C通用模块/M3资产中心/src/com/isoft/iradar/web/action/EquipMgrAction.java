package com.isoft.iradar.web.action;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.INTERFACE_USE_IP;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.types.CArray.array;

import java.io.Serializable;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.action.core.HostsAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
public class EquipMgrAction extends HostsAction {
	
	private final static String ACTION_NAME = "equipMgr.action";
	private final static CArray<String> INVENTORY_FILEDS = CArray.map(
			"os_full","编号",
			"hardware","部门",
			"software","机房",
			"url_a","机柜",
			"host_networks","资产信息汇总",
			"vendor", "厂商",
			"contract_number","型号"
			);
	
	//表单访问路径名
	protected String getAction(){
		return ACTION_NAME;
	}
	
    @Override
	protected CArray getFormBtns() {
    	CArray btns = super.getFormBtns();
    	btns.remove(1);
		return btns;
	}
    
    @Override
	protected CArray getHeader(CForm form) {
		return array(
    			new CCheckBox("all_hosts", false, "checkAll(\""+form.getName()+"\", \"all_hosts\", \"hosts\");"),
    			make_sorting_header(_("Name"), "name"),
    			"类型",
    			"IP",
    			"编号",
    			"部门",
    			"机房",
    			"机柜",
    			"资产信息汇总",
    			//"报表",
			make_sorting_header(_("Status"), "status")	
		);
	}
    
    protected void prepareEditData(Map data) {
		//设置 设备资产记录字段
		data.put("hostInventoryFieldsFilter", INVENTORY_FILEDS);
		//设置 设备资产记录字段为手动添加  且隐藏选择添加按钮方式（已停用、手动的、自动的）
		data.put("hostInventoryFieldsManual", true);
	}

	@Override
	protected CArray getFileterRow() {
		return array(
    			array(array(bold(_("Name")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_host", Nest.value(_REQUEST,"filter_host").asString(), 20)),
    			array(array(bold(_("IP")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_ip", Nest.value(_REQUEST,"filter_ip").asString(), 20))
    		);
	}
	
	@Override
	protected CArray<Serializable> getRowData(SQLExecutor executor, Map host, CArray applications, CArray items, CArray triggers, CArray graphs, CArray discoveries, 
			CArray httpTests, CArray description, String hostInterface, CLink status, Object hostTemplates) {
		String type=null;
    	String os_full=null;
    	String hardware=null;
    	String software=null;
    	String url_a=null;
    	String host_networks=null;
    	
    	Long hostid = Nest.value(host, "hostid").asLong();
    	Map iface = reset(Nest.value(host,"interfaces").asCArray());
    	
    	CHostGroupGet params = new CHostGroupGet();
		params.setOutput(Defines.API_OUTPUT_EXTEND);
		params.setHostIds(hostid);
		CArray<Map> result = API.HostGroup(getIdentityBean(), executor).get(params);
		CArray  obj = Nest.value(result, "0").asCArray();
		if(!empty(obj)){
		 type=Nest.value(obj, "name").asString();
		}else{		
			type="-";
		}
		
		CHostGet hostGet=new CHostGet();
		hostGet.setOutput(API_OUTPUT_EXTEND);
		hostGet.setHostIds(hostid);
		hostGet.setSelectInventory(API_OUTPUT_EXTEND);
		Map hosts = API.Host(getIdentityBean(), executor).get(hostGet);
		CArray data = 	Nest.value(hosts, "0").asCArray();
		CArray record = Nest.value(data, "inventory").asCArray();
		if(!empty(record)){
			os_full =  Nest.value(record, "os_full").asString();
			hardware =  Nest.value(record, "hardware").asString();
			software =  Nest.value(record, "software").asString();
			url_a =  Nest.value(record, "url_a").asString();
			host_networks =  Nest.value(record, "host_networks").asString();	
		}
		if(empty(os_full)){
			os_full="-";
		}
		if(empty(hardware)){
			hardware="-";
		}
		if(empty(software)){
			software="-";
		}
		if(empty(url_a)){
			url_a="-";
		}
		if(empty(host_networks)){
			host_networks="-";
		}
		
		hostInterface = (Nest.value(iface, "useip").asInteger() == INTERFACE_USE_IP) ? 
				Nest.value(iface,"ip").asString() : Nest.value(iface,"dns").asString();
		if(empty(hostInterface)){
			hostInterface="-";	
		}
		
		return array(
    			new CCheckBox("hosts["+Nest.value(host,"hostid").asString()+"]", false, null, Nest.value(host,"hostid").asString()),
    			description,
    			type,
    			hostInterface,
    			os_full,
    			hardware,
    			software,
    			url_a,
    			host_networks,
    			status
    		);
	}
	
}
