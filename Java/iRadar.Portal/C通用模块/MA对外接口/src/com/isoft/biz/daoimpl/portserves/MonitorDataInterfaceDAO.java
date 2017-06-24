package com.isoft.biz.daoimpl.portserves;

import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.portserves.IMonitorDataInterfaceDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MonitorDataInterfaceDAO extends BaseDAO implements IMonitorDataInterfaceDAO {

	public MonitorDataInterfaceDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
		// TODO Auto-generated constructor stub
	}

	public List doList(Map paraMap) {
		List<Map> itemslists=new ArrayList<Map>();
		SQLExecutor executor = getSqlExecutor();
		String tenantid = Nest.value(paraMap, "tenantid").asString();
		String hostname = Nest.value(paraMap, "hostname").asString();
		String templatename=Nest.value(paraMap, "templatename").asString();
		String itemNumber=Nest.value(paraMap, "itemNumber").asString();
		long startTime = Nest.value(paraMap, "startTime").asLong();
		long endTime = Nest.value(paraMap, "endTime").asLong();
		String wheresql ="";
		String templatewheresql ="";
		if(hostname !=null){
			wheresql=" and h.name= '"+hostname+"'";
		}
        if(templatename!=null){
        	templatewheresql=" and  h.hostid in(select  t.hostid from hosts_templates t where t.templateid in(select l.hostid from hosts l where l.status=3 and l.name='"+templatename+"') and t.tenantid= '"+tenantid+"')";
		}
		String itemsql =" select n.ip,i.name,i.itemid,i.units,i.key_,i.valuemapid,i.value_type  from items i,hosts h "+
				" left join interface n   "+
				" on n.hostid=h.hostid    "+
				" where h.hostid=i.hostid    "+
				" and h.tenantid = #{tenantid} " +
				" and i.flags in ("+Defines.RDA_FLAG_DISCOVERY_NORMAL+","+Defines.RDA_FLAG_DISCOVERY_CREATED+")"+
				 wheresql +
				 templatewheresql +
				" order by h.hostid,i.itemid ";
		Map paramapt = new HashMap();
		paramapt.put("tenantid", tenantid);
		CArray<Map> items = DBselect(executor, itemsql, paramapt);
		
		items = CMacrosResolverHelper.resolveItemNames(IRadarContext.IDBEAN_PLATFORM, executor, items);
		
		for(Map item:items){
			Map itemparamap = new HashMap();
			itemparamap.put("itemid", Nest.value(item, "itemid").asString());
			itemparamap.put("tenantid", tenantid);
			itemparamap.put("startTime", startTime);
			itemparamap.put("endTime", endTime);
			
			
			
			int valueType=Nest.value(item, "value_type").asInteger();
			String table = CHistoryManager.getTableName(valueType);
			String sql = " SELECT h.clock ,h.value FROM "+ table+" h WHERE itemid= #{itemid}  AND h.tenantid = #{tenantid} "
			+ " AND h.clock between #{startTime}  and  #{endTime} "
			+ " ORDER BY clock DESC  "
			+ " limit  "+itemNumber;
			CArray<Map> historys=DBselect(executor, sql, itemparamap);
			List<Map> hislist=new ArrayList<Map>();
			for(Map his:historys){
				if (his!=null) {
					String lasttime = Nest.value(his,"clock").asString();
					String lastValue = Nest.value(his,"value").asString();
					hislist.add(EasyMap.build("lasttime" ,lasttime,
                                               "lastValue", lastValue));
				}
			}
			itemslists.add(EasyMap.build( 
                    "hostIp", Nest.value(item, "ip").asString(),
                    "itemName", Nest.value(item, "name_expanded").asString(),
                    "historydata",hislist));
		}	
		return itemslists;
	}
}
