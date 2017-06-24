package com.isoft.iradar.common.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.types.Mapper.Nest;

public class TopNHelper {
	
	private PriorityQueue<Map> queue;
	
	private int n;
	private final Object key;
	
	public TopNHelper(int n, Object key) {
		this(n, key, true);
	}
	
	public TopNHelper(int n, final Object key, boolean desc) {
		this.n = n;
		this.key = key;
		
		final int flag = desc? 1: -1;
		this.queue = new PriorityQueue(n+1,  new Comparator<Map>() {  
	        @Override  
	        public int compare(Map o1, Map o2) {
	        	Double v1 = Nest.value(o1, key).asDouble();
	        	Double v2 = Nest.value(o2, key).asDouble();
	            return v1.compareTo(v2) * flag;
	        }
	    });  
	}
	
	public void put(Map m) {
		if(!Cphp.isset(m, key)) return;
		if(queue.size() < n){ //未达到最大容量，直接添加  
            queue.add(m);  
        }else{ //队列已满  
            queue.add(m);
            queue.poll();
        }  
	}
	
	public List<Map> getResult(){
		List rs = EasyList.build();
		
		Iterator<Map> it = queue.iterator();
		while(it.hasNext()) {
			rs.add(it.next());
		}
		
		Collections.sort(rs, new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				return queue.comparator().compare(o2, o1);
			}
		});
		return rs;
	}
	
	public static void main(String[] args) {
		TopNHelper t = new TopNHelper(4, "v", true);
		t.put(EasyMap.build("v", 1));
		t.put(EasyMap.build("v", 2));
		t.put(EasyMap.build("v", 3));
		t.put(EasyMap.build("v", 4));
		t.put(EasyMap.build("v", 2));
		t.put(EasyMap.build("v", 3));
		System.out.println(t.getResult());
	}
	
}
