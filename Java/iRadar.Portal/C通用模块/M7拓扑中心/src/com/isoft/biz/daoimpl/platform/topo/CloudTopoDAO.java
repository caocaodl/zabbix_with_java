package com.isoft.biz.daoimpl.platform.topo;

import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ICloudTopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.Topo;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CloudTopoDAO extends BaseDAO implements ICloudTopoDAO {

	public CloudTopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	public CArray doGetVMHostName(IIdentityBean identityBean) {
		SQLExecutor executor = getSqlExecutor();
		SqlBuilder sqlparts = new SqlBuilder();
		sqlparts.select.put("host");
		sqlparts.select.put("hostid_os");
		sqlparts.from.put("hosts");
		sqlparts.where.put("hostid_os IS NOT NULL");
		CArray<Map> resultCA = DBselect(executor,sqlparts);
		CArray data = CArray.map();
		for(Map result:resultCA){
			data.put(Nest.value(result, "hostid_os").asString(), Nest.value(result, "host").asString());
		}
		return data;
	}
}
