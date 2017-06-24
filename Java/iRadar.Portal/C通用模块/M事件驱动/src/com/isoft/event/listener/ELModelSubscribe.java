package com.isoft.event.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.isoft.event.EventUEI;
import com.isoft.event.args.EArgsHost;
import com.isoft.framework.events.Event;
import com.isoft.framework.events.EventArgs;
import com.isoft.framework.events.EventListener;

/**
 * 模型订阅相关监控类，主要监听
 * 
 * @author BluE
 *
 */
public class ELModelSubscribe implements EventListener {
	private final static String DATE_FORMAT = "yyyy-MM-dd";
	
	@Override
	public String getName() {
		return ELModelSubscribe.class.getName();
	}

	@Override
	public void onEvent(Event e) {
		String uei = e.getUei();
		Set<Long> modelIds = getModelIds(e);
		String timeStamp = getTimeStamp(e);
		
		//订阅为true，取消订阅为false
		boolean isSubscribe = EventUEI.MODEL_SUBSCRIBE.name().equals(uei);
		int delta = isSubscribe? 1: -1;
		
		updateModelSubscribeCount(modelIds, timeStamp, delta);
	}
	
	/**
	 * 更新模型所在日期的变化量
	 * 
	 * @param modelIds
	 * @param timeStamp
	 * @param delta
	 */
	private void updateModelSubscribeCount(Set<Long> modelIds, String timeStamp, int delta) {
		//TODO:
	}
	
	/**
	 * 获取事件的时间戳
	 * 	如果参数中有，则以参数为准；
	 * 	否则以事件触发时间为准
	 * 
	 * @param e
	 * @return
	 */
	private String getTimeStamp(Event e) {
		Date date;
		
		EventArgs args = e.getArgs();
		if(args instanceof EArgsHost) {
			date = ((EArgsHost)args).getTimeStamp();
		}else {
			date = new Date(Long.valueOf(e.getTime()));
		}
		
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	/**
	 * 获取所涉及到的模型ID
	 * 
	 * @param e
	 * @return
	 */
	private Set<Long> getModelIds(Event e) {
		EventArgs args = e.getArgs();
		
		if(args instanceof EArgsHost) {
			return getModelIdsByHost((EArgsHost)args);
		}
		
		return null;
	}
	
	/**
	 * 通过 {@link com.isoft.event.args.EArgsHost} 获取模型的ID
	 * 
	 * @param hostArgs
	 * @return
	 */
	private Set<Long> getModelIdsByHost(EArgsHost hostArgs){
		//TODO: 
		return null;
	}
	

}
