package com.isoft.iradar.model;


import static com.isoft.iradar.Cphp.isset;
import static com.isoft.types.CArray.array;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;

public class CRequestParameters {

	private static final int NAME_CHAR = 0;
	private static final int IDX_OPEN = 1;
	private static final int IDX_CLOSED = 2;
	
	private static final Pattern PNAME = Pattern.compile("\\[[\\d\\w_-]+\\]$");

	public static CMap<Object, Object> parse(HttpServletRequest request) {
		String pname = null;
		String name = null;
		int status = 0, i = 0;
		List<String> idxs = null;
		StringBuilder segment = null;
		char c;
		Object value;
		CArray<Object> parasMap = new CArray<Object>();
		Enumeration<String> pnames = request.getParameterNames();
		while (pnames.hasMoreElements()) {
			pname = pnames.nextElement();
			status = 0;
			name = null;
			idxs = new ArrayList();
			segment = new StringBuilder();
			i = 0;
			while (i < pname.length()) {
				c = pname.charAt(i++);
				switch (c) {
				case '[':
					if (status == NAME_CHAR) {
						status = IDX_OPEN;
						name = segment.toString();
						segment.delete(0, segment.length());
						break;
					}
					if (status == IDX_CLOSED) {
						status = IDX_OPEN;
						break;
					}
					if (c == '[') {
						segment.append(c);
						break;
					}
				case ']':
					if (status == IDX_OPEN) {
						status = IDX_CLOSED;
						idxs.add(segment.toString());
						segment.delete(0, segment.length());
						break;
					}
				default:
					segment.append(c);
				}
			}

			if (status == NAME_CHAR) {
				name = segment.toString();
			}
			
			if (status == IDX_OPEN) {
				if (segment.length() > 0 && idxs.isEmpty()) {
					name += "_" + segment.toString();
				}
			}

			segment = null;
			
			String[] vs = request.getParameterValues(pname);
			if (idxs.isEmpty()) {
				//修复PHP中相同的KEY可能会在一个Request中提交两次
				value = vs[vs.length-1];
				parasMap.put(name, value);
			} else {
				if(vs.length == 1) {
					value = vs[0];
				}else if(vs.length > 1) {
					//修复PHP中相同的KEY可能会在一个Request中提交两次
					Matcher match = PNAME.matcher(pname);
					boolean isSame = match.find();
//					for(String v: vs) {
//						if(!v.equals(vs[0])) {
//							isSame = false;
//							break;
//						}
//					}
					value = isSame? vs[vs.length-1]: vs; 
				}else {
					value = vs;
				}
				idxs.add(0, name);
//				idxs.add(value);
				
				CArray map = parasMap;
				Iterator<String> it = idxs.iterator();
				while(it.hasNext()) {
					Object v = null;
					CArray container = null;
					String key = it.next();
					if(it.hasNext()) {//不是最后一个，则创建或重用其容器
						container = (CArray) map.get(key.length() > 0 ? key : 0);
						if(container == null) {
							v = container = array();
						}
					}else {//最后一个key，则使用request的value
						v = value;
					}
					
					//如果key是空的[]，则使用add方法，否则使用put方法
					if(v != null) {
						if(key.length() == 0) {
							if(v instanceof String[]) {
								String[] ss = (String[])v;
								for(String s: ss) {
									map.add(s);
								}
							}else {
								if(it.hasNext()) {//形如interface[][isNew], interface[][dns]这样的字段，[]需要固定为0
									if(!isset(map, 0)){
										Nest.value(map, 0).$(container = array());
									}
									Nest.value(map, 0).asCArray().putAll((CArray)v);
								}else {
									map.add(v);
								}
							}
						}else {
							map.put(key, v);
							if(map.size()>1 && NumberUtils.isDigits(key)){
								map.ksort();
							}
						}
					}
					
					if(container != null) {
						map = container;
					}
				}
//				parasMap.put(idxs.toArray());
//				parasMap.add(pnames);
			}
		}
		return parasMap;
	}
	
//	public static void main(String[] args) {
//		org.springframework.mock.web.MockHttpServletRequest r = new org.springframework.mock.web.MockHttpServletRequest();
//		r.addParameter("m[0][k]", "_k");
//		r.addParameter("a[]", "0");
//		r.addParameter("a[]", "1");
//		CMap m = parse(r);
//		System.out.println(m);
//	}
}
