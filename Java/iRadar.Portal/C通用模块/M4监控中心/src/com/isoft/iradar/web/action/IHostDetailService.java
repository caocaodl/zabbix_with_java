package com.isoft.iradar.web.action;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.types.CArray;

public interface IHostDetailService {

	/**
	 * 获取设备详情页面关键指标展示表格
	 * @return
	 */
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data);
	/**
	 * 设备详情页面健康度面板区域显示内容
	 * @param divID		divID
	 * @param styleClass div引用样式
	 * @param data		map类型数据
	 * @param title		标题
	 * @param num		数值
	 * @return
	 */
	public CDiv getHealthFunsionCharts(String divID,String styleClass,Map data);
	
	/**
	 * 组装要显示的设备详情信息
	 * @param data	数据源
	 * @param overviewFormList	要返回的表单
	 * @return
	 */
	public CFormList getOverviewForm(IIdentityBean idBean, SQLExecutor executor,Map data,String hostid);
}
