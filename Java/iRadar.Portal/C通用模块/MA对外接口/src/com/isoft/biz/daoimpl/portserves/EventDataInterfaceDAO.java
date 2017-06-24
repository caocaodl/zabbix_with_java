package com.isoft.biz.daoimpl.portserves;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.portserves.IEventDataInterfaceDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class EventDataInterfaceDAO extends BaseDAO implements
		IEventDataInterfaceDAO {

	public EventDataInterfaceDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_LIST = "SQL_LIST";
	public List doList(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String tenantid=Nest.value(paraMap, "tenantid").asString();
		String hostid=Nest.value(paraMap, "hostid").asString();
		String eventLevel=Nest.value(paraMap, "eventLevel").asString();
		String itemNumber=Nest.value(paraMap, "itemNumber").asString();
		long startTime = Nest.value(paraMap, "startTime").asLong();
		long endTime = Nest.value(paraMap, "endTime").asLong();
		String sqls = "	select   "+
			"     e.clock as eventtime,n.ip as hostip,t.priority as eventLevel,t.description as details "+
			" from                                                 "+
			"   events e                                           "+
			" left join triggers t                                 "+
			"  on e.objectid=t.triggerid                           "+
			" inner join functions f                               "+
			"  on f.triggerid = t.triggerid                        "+
			" inner join items i                                   "+
			"  on i.itemid = f.itemid                              "+
			" left join interface n                                "+
			"   on n.hostid = i.hostid                             "+
			" where e.clock between #{startTime}  and  #{endTime}  "+
			" and i.hostid = #{hostid} "+
			" and e.tenantid = #{tenantid}  ";
			if(!empty(eventLevel)){
				sqls +=" and t.priority = #{eventLevel}   ";
			}
			sqls +=" order by e.clock desc       "+
			       " limit  0,"+itemNumber;
		unset(paraMap,"itemNumber");	
		CArray<Map> events =DBselect(executor,
				sqls,
				paraMap
			);
		List<Map> eventlists=new ArrayList<Map>();
		for(Map event:events){
			eventlists.add(event);
		}
		return eventlists;
	}
}
