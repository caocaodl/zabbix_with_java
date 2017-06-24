package com.isoft.event;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.event.args.EArgsHost;
import com.isoft.event.args.EArgsHost.Opertaion;
import com.isoft.event.listener.ELModelSubscribe;
import com.isoft.framework.events.Event;
import com.isoft.framework.events.EventCenter;
import com.isoft.framework.events.EventListener;
import com.isoft.iradar.core.utils.EasyMap;

public class EventFacade {
	private final static Map<Class<? extends EventListener>, EventUEI[]> REGISTERS = EasyMap.build(
		ELModelSubscribe.class, new EventUEI[]{
			EventUEI.MODEL_SUBSCRIBE, 
			EventUEI.MODEL_UNSUBSCRIBE
		}
	);
	
	/**
	 * 初始化注册监控类
	 * 
	 * @throws Exception
	 */
	public static void init() throws Exception {
		for(Entry<Class<? extends EventListener>, EventUEI[]> entry: REGISTERS.entrySet()) {
			Class<? extends EventListener> clz = entry.getKey();
			EventUEI[] ueis = entry.getValue();
			
			String[] ueiNames = new String[ueis.length];
			int i=0;
			for(EventUEI uei: ueis) {
				ueiNames[i++] = uei.name(); 
			}
			
			EventCenter.registe(clz.newInstance(), ueiNames);
		}
	}

	/**
	 * 租户“添加”设备,将触发以下事件：<br>
	 * 	{@link com.isoft.event.EventFacade.EventUEI.MODEL_SUBSCRIBE} 租户订阅监控模型事件
	 * 
	 * @param userId 用户ID
	 * @param nodeId 所添加设备ID
	 * @throws Exception
	 */
	public static void notifyAddHost(Long userId, Long nodeId) throws Exception {
		EArgsHost addHostArgs = new EArgsHost(Opertaion.ADD);
		addHostArgs.setUserId(userId);
		addHostArgs.setHostIds(nodeId);
		
		//租户订阅监控模型
		Event e_model_subscribe = new Event(EventUEI.MODEL_SUBSCRIBE.name());
		e_model_subscribe.setArgs(addHostArgs);
		EventCenter.notice(e_model_subscribe);
	}
	
	/**
	 * 租户“删除”设备,将触发以下事件：<br>
	 * 	{@link com.isoft.event.EventFacade.EventUEI.MODEL_UNSUBSCRIBE} 租户取消订阅监控模型事件
	 * 
	 * @param userId 用户ID
	 * @param nodeIds 所删除设备ID
	 * @throws Exception
	 */
	public static void notifyDelHost(Long userId, Long... nodeIds) throws Exception {
		EArgsHost addHostArgs = new EArgsHost(Opertaion.DELETE);
		addHostArgs.setUserId(userId);
		addHostArgs.setHostIds(nodeIds);
		
		//租户取消订阅监控模型
		Event e_model_unsubscribe = new Event(EventUEI.MODEL_UNSUBSCRIBE.name());
		e_model_unsubscribe.setArgs(addHostArgs);
		EventCenter.notice(e_model_unsubscribe);
	}
}
