package com.isoft.biz.daoimpl.platform.topo;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ICabTopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.TCabNode;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CabTopoDAO extends BaseDAO implements ICabTopoDAO {

	/**
	 * 系统屏蔽默认的存储设备类型
	 */
//	public CArray serverCA = CArray.array(IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS);
//	public CArray netCA = CArray.array(IMonConsts.MON_NET_CISCO,IMonConsts.MON_COMMON_NET);
//	public CArray storageCA = CArray.array(IMonConsts.MON_STORAGE);
	
	public CabTopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static String SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID = "SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID";
	@SuppressWarnings("unchecked")
	public List<Integer> doHostIdByTopoId(DataPage dataPage,Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID);
		if(dataPage == null){
			return (List<Integer>) executor.executeNameParaQuery(sql, paraMap, Integer.class);	
		}else{
			return (List<Integer>) executor.executeNameParaQuery(dataPage,sql, paraMap, Integer.class);
		}
	}
	
	private static String SQL_CABINET_TOPO_NODE_LIST = "SQL_CABINET_TOPO_NODE_LIST";
	@SuppressWarnings("unchecked")
	public List<TCabNode> doCabinetTopoList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CABINET_TOPO_NODE_LIST);
		return (List<TCabNode>) executor.executeNameParaQuery(sql, paraMap,
				TCabNode.class);
	}
	private static String SQL_T_CABINET_NODE_DEL_BY_PICID_SELECT = "SQL_T_CABINET_NODE_DEL_BY_PICID_SELECT";
	@SuppressWarnings("unchecked")
	public List<TCabNode> doCabinetTopoListBypicid(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_DEL_BY_PICID_SELECT);
		return (List<TCabNode>) executor.executeNameParaQuery(sql, paraMap,
				TCabNode.class);
	}
	private static final String SQL_T_CABINET_NODE_ADD = "SQL_T_CABINET_NODE_ADD";
	public String doTCabinetNodeAdd(Map<String, Object> paraMap) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_T_CABINET_NODE_ADD);
			String id = getFlowcode(NameSpaceEnum.T_CABINET_NODE);
			paraMap.put("nodeId", id);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				return id;
			}
		return null;
	}

	private static final String SQL_T_CABINET_NODE_DEL_BY_TOPOID = "SQL_T_CABINET_NODE_DEL_BY_TOPOID";

	@SuppressWarnings("rawtypes")
	public int doCabinetNodeDelByTopoId(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_DEL_BY_TOPOID);
		return executor.executeInsertDeleteUpdate(sql, param);
	}
	
	private static final String SQL_T_CABINET_NODE_DEL_BY_NODEID = "SQL_T_CABINET_NODE_DEL_BY_NODEID";

	@SuppressWarnings("rawtypes")
	public int doCabinetNodeDelByNodeId(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_DEL_BY_NODEID);
		return executor.executeInsertDeleteUpdate(sql, param);
	}
	private static final String SQL_T_CABINET_NODE_DEL_BY_PICID = "SQL_T_CABINET_NODE_DEL_BY_PICID";

	@SuppressWarnings("rawtypes")
	public int doCabinetNodeDelByPicId(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_DEL_BY_PICID);
		return executor.executeInsertDeleteUpdate(sql, param);
	}
	
	private static final String SQL_T_CABINET_NODE_UPDATE_G = "SQL_T_CABINET_NODE_UPDATE_G";
	public boolean doCabinetUpdateG(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_UPDATE_G);
		String id = (String)paraMap.get("nodeId");
		return executor.executeInsertDeleteUpdate(sql, paraMap)!=0?true:false;
	}
	
	private static final String SQL_GET_MOTOR_ROOM_DATA = "SQL_GET_MOTOR_ROOM_DATA";
	private static final String SQL_GET_CABINET_DATA = "SQL_GET_CABINET_DATA";
	/**
	 * 去除机柜，先注释掉之前的代码，方便后续使用
	 * @param param
	 * @return
	 */
/*	public List<Map> doGetRoomData() {
		SQLExecutor executor = getSqlExecutor();
		Map param = new LinkedMap();
		String sql = getSql(SQL_GET_MOTOR_ROOM_DATA);
		Map sqlVO = getSqlVO(SQL_GET_MOTOR_ROOM_DATA);
		List<Map> roomsList = executor.executeNameParaQuery(sql, param,sqlVO);
		sql = getSql(SQL_GET_CABINET_DATA);
		sqlVO = getSqlVO(SQL_GET_CABINET_DATA);
		for(Map room:roomsList){
			param.put("roomId", room.get("id"));
			room.put("hostType", HostConstants.CATEGORY_ROOM);
			List<Map> cabinetData = executor.executeNameParaQuery(sql, param,sqlVO);
			for(Map cabinet:cabinetData)
				cabinet.put("hostType", HostConstants.CATEGORY_CABINET);
			doCabTopoCabDataSetXY(cabinetData,executor);
			room.put("children", cabinetData);
		}
		return roomsList;
	}*/
	public List<Map> doGetRoomData() {
		SQLExecutor executor = getSqlExecutor();
		Map param = new LinkedMap();
		String sql = getSql(SQL_GET_MOTOR_ROOM_DATA);
		Map sqlVO = getSqlVO(SQL_GET_MOTOR_ROOM_DATA);
		List<Map> roomsList = executor.executeNameParaQuery(sql, param,sqlVO);
		return roomsList;
	}
	
	public List<Map> doGetCabData(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_CABINET_DATA);
		Map sqlVO = getSqlVO(SQL_GET_CABINET_DATA);
		List<Map> cabinetData =  executor.executeNameParaQuery(sql, param,sqlVO);
		Map cabParam = new LinkedMap();
		for(Map cabinet:cabinetData){
			cabParam.put("roomId", param.get("roomId"));
			cabParam.put("cabId", cabinet.get("id"));
			cabinet.put("hostType", HostConstants.CATEGORY_CABINET);
			List<Map> hostDatas = doGetCabTopoData(cabParam);
			cabinet.put("children", hostDatas);
		}
		doCabTopoCabDataSetXY(cabinetData,executor);
		return cabinetData;
	}
	
	private static final String SQL_GET_CABINET_TOPO_DATA = "SQL_GET_CABINET_TOPO_DATA";
	public List<Map> doGetCabTopoData(Map param) {
		SQLExecutor executor = getSqlExecutor();
		
		CArray serverCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_SERVER);
		CArray netCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_NET_DEV);
		CArray storageCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_STORAGE);

//		param.put("groupIds", CArray.array(IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_NET_CISCO,IMonConsts.MON_COMMON_NET,IMonConsts.MON_STORAGE).toList());
		String sql = getSql(SQL_GET_CABINET_TOPO_DATA);
		Map sqlVO = getSqlVO(SQL_GET_CABINET_TOPO_DATA);
		List<Map> datas = executor.executeNameParaQuery(sql, param,sqlVO);
		for(Map data:datas){
			String[] hostTypes = Nest.value(data, "hostType").asString().split(",");
			for(String hostType:hostTypes){
				if(serverCA.containsValue(Nest.as(hostType).asLong())){
					Nest.value(data, "hostType").$(HostConstants.CATEGORY_SERVER);
					break;
				}else if(netCA.containsValue(Nest.as(hostType).asLong())){
					Nest.value(data, "hostType").$(HostConstants.CATEGORY_ROUTE_SWITCH);
					break;
				}else if(storageCA.containsValue(Nest.as(hostType).asLong())){
					Nest.value(data, "hostType").$(HostConstants.CATEGORY_STORAGE);
					break;
				}
			}
		}
		doTopoDataSetXY(datas,executor);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(executor, datas);
		return datas;
	}
	
	public void doTopoDataSetXY(List<Map> dataList,SQLExecutor sqlExecutor){
		Map paraMap = new LinkedMap();
		paraMap.put("topoType", TopoDataOperAction.TOPO_CAB);
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		Map location = topoDataOperDAO.doTopoDataLocOperGet(paraMap);
		for(Map data:dataList){
			if(!Cphp.empty(Nest.value(location, Nest.as(data.get("id")).asString()).$())){
				data.put("X", Nest.value(location, Nest.as(data.get("id")).asString(),"X").asDouble());
				data.put("Y", Nest.value(location, Nest.as(data.get("id")).asString(),"Y").asDouble());
			}
		}
	}
	
	public void doCabTopoCabDataSetXY(List<Map> dataList,SQLExecutor sqlExecutor){
		Map paraMap = new LinkedMap();
		paraMap.put("topoType", TopoDataOperAction.TOPO_CAB);
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		Map location = topoDataOperDAO.doTopoDataLocOperGet(paraMap);
		for(Map data:dataList){
			if(!Cphp.empty(Nest.value(location, Nest.as(data.get("id")).asString()).$())){
				data.put("X", Nest.value(location, Nest.as(data.get("id")).asString(),"X").asDouble());
				data.put("Y", Nest.value(location, Nest.as(data.get("id")).asString(),"Y").asDouble());
			}
		}
	}

}
