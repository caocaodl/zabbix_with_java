package com.isoft.imon.topo.engine.discover.poller.host.huawei;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import com.isoft.imon.topo.util.SimpleXMLUtil;
import com.isoft.imon.topo.util.SysConfigHelper;

/**
 * 华为轮询器辅助类
 * @author Administrator
 * @date 2014年8月7日 
 */
public class HuaweiPollerHelper {
	private List<String> ifIndexToPort;
	private List<String> portToIfIndex;

	/**
	 * 构造方法
	 */
	public HuaweiPollerHelper() {
		initPortIfMapping();
	}

	public Map<String, Method> getMethods(Class<?> clazz, String tag) {
		Map<String, Method> methods = new HashMap<String, Method>();
		String xmlpath = SysConfigHelper.getAttribute("configPath")
				+ "host/huaweiOids.xml";
		Document doc = SimpleXMLUtil.file2Doc(xmlpath);
		if(doc == null){
			throw new NullPointerException();
		}
		Element root = doc.getRootElement();
		
		List<Element> eles = root.getChildren("huawei");
		for (Element ele : eles) {
			if (ele.getAttributeValue(tag) == null)
				continue;
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getName().equals(ele.getAttributeValue(tag))) {
					methods.put(ele.getAttributeValue("sysoid"), method);
					break;
				}
			}
		}
		return methods;
	}

	public void initPortIfMapping() {
		this.ifIndexToPort = new ArrayList<String>();
		this.portToIfIndex = new ArrayList<String>();

		String xmlpath = SysConfigHelper.getAttribute("configPath")
				+ "host/huaweiOids.xml";
		Document doc = SimpleXMLUtil.file2Doc(xmlpath);
		if(doc == null){
			return;
		}
		Element root = doc.getRootElement();
		List<Element> eles1 = root.getChildren("IfIndexToPort");
		for (Element ele : eles1) {
			this.ifIndexToPort.add(ele.getAttributeValue("model"));
		}
		List<Element> eles2 = root.getChildren("PortToIfIndex");
		for (Element ele : eles2)
			this.portToIfIndex.add(ele.getAttributeValue("model"));
	}

	public List<String> getIfIndexToPort() {
		return this.ifIndexToPort;
	}

	public List<String> getPortToIfIndex() {
		return this.portToIfIndex;
	}

	public static int getAverage(String[][] results) {
		if ((results == null) || (results.length == 0)) {
			return -1;
		}
		int load = 0;
		int count = 0;
		for (int i = 0; i < results.length; i++) {
			int _temp = 0;
			try {
				_temp = Integer.parseInt(results[i][0]);
				load += _temp;
			} catch (NumberFormatException localNumberFormatException) {
				localNumberFormatException.printStackTrace();
			}
			if (_temp > 0)
				count++;
		}
		if (count > 0)
			return load / count;
		return 0;
	}
}
