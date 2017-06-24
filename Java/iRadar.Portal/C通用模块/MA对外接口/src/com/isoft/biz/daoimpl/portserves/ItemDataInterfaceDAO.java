package com.isoft.biz.daoimpl.portserves;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.portserves.IItemDataInterfaceDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ItemDataInterfaceDAO extends BaseDAO implements IItemDataInterfaceDAO {

	public ItemDataInterfaceDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
		// TODO Auto-generated constructor stub
	}

	private static final String SQL_LIST = "SQL_LIST";
	public List doList(Map paraMap) {
		List<Map> itemslists=new ArrayList<Map>();
		SQLExecutor executor = getSqlExecutor();
		String tenantid = Nest.value(paraMap, "tenantid").asString();
		String itemid=Nest.value(paraMap, "itemid").asString();
		String itemNumber=Nest.value(paraMap, "itemNumber").asString();
		long startTime = Nest.value(paraMap, "startTime").asLong();
		long endTime = Nest.value(paraMap, "endTime").asLong();
		String wheresql ="";
		String itemsql =" select i.itemid,i.name,i.value_type,h.host from items i,hosts h "+
				" where h.hostid=i.hostid    "+
				" and i.itemid = #{itemid} "+
				" and i.tenantid = #{tenantid} "+
				" order by i.itemid " +
				" limit  0,"+itemNumber;
		Map paramapt = new HashMap();
		paramapt.put("itemid", itemid);
		paramapt.put("tenantid", tenantid);
		CArray<Map> items = DBselect(executor, itemsql, paramapt);
		for(Map item:items){
			Map itemparamap = new HashMap();
			itemparamap.put("itemid", Nest.value(item, "itemid").asString());
			itemparamap.put("tenantid", tenantid);
			itemparamap.put("startTime", startTime);
			itemparamap.put("endTime", endTime);
			
			int valueType=Nest.value(item, "value_type").asInteger();
			String table = CHistoryManager.getTableName(valueType);
			String sql = " SELECT  h.value FROM "+ table+" h WHERE itemid= #{itemid}  AND h.tenantid = #{tenantid} "
			+ " AND h.clock between #{startTime}  and  #{endTime} "
			+ " ORDER BY clock DESC LIMIT 0,1 ";
			Map his=DBfetch(DBselect(executor, sql, itemparamap));
			if (his!=null) {
				String lasttime = Nest.value(his,"clock").asString();
				String lastValue = Nest.value(his,"value").asString();
				
				itemslists.add(EasyMap.build( "hostname" ,Nest.value(item, "host").asString(),
						                      "itemname", Nest.value(item, "name").asString(), 
						                      "lastValue", lastValue));
			}
		}
		return itemslists;
	}
}
