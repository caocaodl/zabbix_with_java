package com.isoft.imon.topo.platform.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import com.isoft.imon.topo.admin.factory.DictionaryEntry;
import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.Credence;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.NetElementModel;
import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.platform.element.Category;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.imon.topo.util.SimpleXMLUtil;

/**
 * 上下文资源池
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public final class ContextResourcesPool {
	private static ContextResourcesPool pool = new ContextResourcesPool();
	private final Map<String, NetElementModel> producers;
	private final List<NetElementModel> models;
	private final Map<String, Class<? extends Credence>> credenceClazzs;
	private List<String> credenceTypes;
	private final List<Category> categories;
	private final List<PollerTemplate> pollerTemplates;
	private final Map<String, NetElementModel> modelOids;

	public static ContextResourcesPool getPool() {
		return pool;
	}

	private ContextResourcesPool() {
		this.producers = new HashMap<String, NetElementModel>();
		this.models = new ArrayList<NetElementModel>();
		this.pollerTemplates = new ArrayList<PollerTemplate>();
		this.categories = new ArrayList<Category>();

		this.credenceClazzs = new HashMap<String, Class<? extends Credence>>();
		this.modelOids = new HashMap<String, NetElementModel>();
	}

	/**
	 * 注册产品
	 * 
	 * @param em
	 */
	public void registerProducer(NetElementModel em) {
		if ((em == null) || (em.getOid() == null))
			throw new IllegalArgumentException("register invalid Producer.");
		this.producers.put(em.getOid(), em);
	}

	/**
	 * 获取类别列表
	 * 
	 * @return
	 */
	public List<Category> getCategories() {
		return Collections.unmodifiableList(this.categories);
	}

	/**
	 * 注册设备模型
	 * 
	 * @param em
	 */
	public void registerElementModel(NetElementModel em) {
		if ((em == null) || (em.getOid() == null)) {
			throw new IllegalArgumentException("register invalid ElementModel,Enterprise=" + null);
		}
		this.models.add(em);
	}

	/**
	 * 注册设备模型
	 * 
	 * @param map
	 * @param enterprise
	 * @param symbol
	 */
	public void registerElementModel(Map<String, String> map, String enterprise, String symbol) {
		for (String Oid : map.keySet()) {
			NetElementModel em = new NetElementModel();
			em.setEnterprise(enterprise);
			em.setSymbol(symbol);
			em.setOid(Oid);
			em.setModel((String) map.get(Oid));
			registerElementModel(em);
		}
	}

	/**
	 * 注册产品
	 * 
	 * @param enterpriseOid
	 * @param enterpriseName
	 * @param symbol
	 */
	public void registerProducer(String enterpriseOid, String enterpriseName, String symbol) {
		NetElementModel em = new NetElementModel();
		em.setOid(enterpriseOid);
		em.setEnterprise(enterpriseName);
		em.setSymbol(symbol);
		em.setModel("Unknown");
		registerProducer(em);
	}

	private static DictionaryEntry buildEntry(String enterprise) {
		DictionaryEntry de = new DictionaryEntry();
		de.setKey(enterprise);
		de.setValue(enterprise);
		return de;
	}
	
	/**
	 * 注册设备模型
	 * 
	 * @param emXML
	 */
	public void registerElementModel(String emXML) {
		Document document = SimpleXMLUtil.file2Doc(emXML);
		if (document == null) {
			return;
		}
		
		Set<DictionaryEntry> enterpriseSet = new HashSet<DictionaryEntry>();
		
		Element root = document.getRootElement();
		List<Element> enterprises = root.getChildren("enterprise");
		String enterprise;
		for (Element pele : enterprises) {
			enterprise = pele.getAttributeValue("name");
			enterpriseSet.add(buildEntry(enterprise));
			
			registerProducer(pele.getAttributeValue("enterpriseOid"), enterprise, pele.getAttributeValue("symbol"));

			List<Element> eles = pele.getChildren("ElementModel");
			for (Element ele : eles) {
				if (ele.getAttributeValue("sysOid") == null)
					throw new IllegalArgumentException(enterprise + " has an invalid ElementModel Node.");
				String symbol = ele.getAttributeValue("symbol");
				if (symbol == null)
					symbol = pele.getAttributeValue("symbol");
				if (ele.getAttributeValue("sysOid") != null) {
					NetElementModel em = new NetElementModel();
					em.setEnterprise(enterprise);
					em.setOid(ele.getAttributeValue("sysOid"));
					em.setSymbol(symbol);
					em.setModel(ele.getAttributeValue("model"));
					em.setCategory(ele.getAttributeValue("category"));
					em.setSysDescr(ele.getAttributeValue("sysDescr"));
					registerElementModel(em);
				}
			}
		}
		List<Element> eles = root.getChildren("ElementModel");
		for (Element ele : eles) {
			NetElementModel em = new NetElementModel();
			em.setOid(ele.getAttributeValue("sysOid"));
			em.setSymbol(ele.getAttributeValue("symbol"));
			em.setModel(ele.getAttributeValue("model"));
			em.setCategory(ele.getAttributeValue("category"));
			em.setSysDescr(ele.getAttributeValue("sysDescr"));
			
			enterprise = ele.getAttributeValue("enterprise");
			em.setEnterprise(enterprise);
			enterpriseSet.add(buildEntry(enterprise));
			
			registerElementModel(em);
		}
		
		DictionaryFactory.getFactory().registerEntries("enterprise", new ArrayList<DictionaryEntry>(enterpriseSet));
	}

	/**
	 * 获取网元模型
	 * 
	 * @param sysOid
	 * @param sysDescr
	 * @return
	 */
	public NetElementModel getNetElementModel(String sysOid, String sysDescr) {
		for (NetElementModel em : this.models) {
			if ((sysOid.equals(em.getOid())) && ((em.getSysDescr() == null) || ((sysDescr != null) && (sysDescr.indexOf(em.getSysDescr()) >= 0)))) {
				return em;
			}
		}

		for (String producer : this.producers.keySet()) {
			if (sysOid.startsWith(producer))
				return (NetElementModel) this.producers.get(producer);
		}
		return null;
	}

	/**
	 * 获取节点类别
	 * 
	 * @return
	 */
	public List<Category> getNodeCategories() {
		List<Category> cates = new ArrayList<Category>();
		for (Category cate : this.categories) {
			if ((cate.getEnName().equals("BroadcastDomain")))
				continue;
			cates.add(cate);
		}
		return cates;
	}

	/**
	 * 注册网元类型
	 * 
	 * @param category
	 */
	public void registerCategory(Category category) {
		if ((category == null) || (this.categories.contains(category)))
			throw new IllegalArgumentException("网元类型为空或已经存在.");
		this.categories.add(category);
	}

	/**
	 * 获取网元类型
	 * 
	 * @param name
	 * @return
	 */
	public Category getCategory(String name) {
		for (Category category : this.categories) {
			if (category.getEnName().equals(name))
				return category;
		}
		return null;
	}

	/**
	 * 注册轮询器
	 * 
	 * @param pollerXML
	 */
	public void registerPoller(String pollerXML) {
		Document document = SimpleXMLUtil.file2Doc(pollerXML);
		if (document == null) {
			return;
		}
		Element root = document.getRootElement();
		List<Element> pollerEles = root.getChildren("poller");
		for (Element pollerEle : pollerEles) {
			PollerTemplate pt = new PollerTemplate();
			pt.setClazz(pollerEle.getAttributeValue("clazz"));
			pt.setCredence(pollerEle.getAttributeValue("credence"));
			if (pollerEle.getAttributeValue("interval") == null)
				pt.setInterval("5:M");
			else
				pt.setInterval(pollerEle.getAttributeValue("interval"));
			if (pollerEle.getAttributeValue("suit") == null)
				pt.setSuit(1);
			else
				pt.setSuit(Integer.parseInt(pollerEle.getAttributeValue("suit")));
			if ("false".equals(pollerEle.getAttributeValue("enabled")))
				pt.setEnabled(false);
			else
				pt.setEnabled(true);
			Map<String, List<String>> include = parseMatchElement(pollerEle.getChild("match").getChild("include"));
			pt.setInclude(include);
			Map<String, List<String>> exclude = parseMatchElement(pollerEle.getChild("match").getChild("exclude"));
			pt.setExclude(exclude);
			if (this.pollerTemplates.contains(pt))
				throw new IllegalArgumentException(pt.getClazz() + "已经存在.");
			this.pollerTemplates.add(pt);
		}
	}

	/**
	 * 解析匹配网元
	 * 
	 * @param element
	 * @return
	 */
	private Map<String, List<String>> parseMatchElement(Element element) {
		if (element == null)
			return null;

		Map<String, List<String>> attrVals = new HashMap<String, List<String>>();
		List<Attribute> attrs = element.getAttributes();
		for (Attribute attr : attrs) {
			String[] _vals = element.getAttributeValue(attr.getName()).split(";");
			List<String> vals = new ArrayList<String>(_vals.length);
			for (String _val : _vals)
				vals.add(_val);
			attrVals.put(attr.getName(), vals);
		}
		return attrVals;
	}

	/**
	 * 获取轮询器
	 * 
	 * @param ne
	 * @param credence
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Poller> getPollers(NetElement ne, String credence) {
		List<Poller> pollers = new ArrayList<Poller>();
		for (PollerTemplate pt : this.pollerTemplates) {
			if (pt.isMyPoller(ne, credence)) {
				Poller<?, ?> poller = (Poller<?, ?>) CommonUtil.getInstance(pt.getClazz());
				poller.setEnabled(pt.isEnabled());
				poller.setIntervalUnit(pt.getIntervalUnit());
				poller.setIntervalValue(pt.getIntervalValue());
				pollers.add(poller);
			}
		}
		return pollers;
	}

	/**
	 * 获取轮询器
	 * 
	 * @param ne
	 * @param kpi
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Poller getPoller(NetElement ne, String kpi) {
		for (PollerTemplate pt : this.pollerTemplates) {
			if (pt.isMyPoller(ne, null)) {
				Poller poller = (Poller) CommonUtil.getInstance(pt.getClazz());
				poller.setEnabled(pt.isEnabled());
				poller.setIntervalUnit(pt.getIntervalUnit());
				poller.setIntervalValue(pt.getIntervalValue());

				return poller;
			}
		}
		return null;
	}

	/**
	 * 获取轮询器名称
	 * 
	 * @param ne
	 * @param kpi
	 * @return
	 */
	public String getPollerName(NetElement ne, String kpi) {
		for (PollerTemplate pt : this.pollerTemplates) {
			if (pt.isMyPoller(ne, null))
				return pt.getClazz();
		}
		return null;
	}

	/**
	 * 获取轮询器模板列表
	 * 
	 * @param ne
	 * @param credence
	 * @return
	 */
	public List<PollerTemplate> getPollerTemplates(NetElement ne, String credence) {
		List<PollerTemplate> templates = new ArrayList<PollerTemplate>();
		for (PollerTemplate pt : this.pollerTemplates) {
			if (pt.isMyPoller(ne, credence))
				templates.add(pt);
		}
		return templates;
	}

	/**
	 * 获取轮询器模板
	 * 
	 * @param pollerClazz
	 * @return
	 */
	public PollerTemplate getPollerTemplate(String pollerClazz) {
		for (PollerTemplate pt : this.pollerTemplates) {
			if (pt.getClazz().equals(pollerClazz))
				return pt;
		}
		return null;
	}

	/**
	 * 注册凭证
	 * 
	 * @param type
	 * @param clazz
	 */
	public void registerCredence(String type, Class<? extends Credence> clazz) {
		if ((type == null) || (clazz == null))
			throw new IllegalArgumentException("register invalid CredenceClazz.");
		if (this.credenceClazzs.containsKey(type))
			throw new IllegalArgumentException("Credence of type " + type + " has exists.");
		this.credenceClazzs.put(type, clazz);
	}

	/**
	 * 新建凭证
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Credence newCredence(String type) {
		try {
			Class class1 = (Class) this.credenceClazzs.get(type);
			if (class1 == null) {
				throw new IllegalArgumentException("Invalid Credence type:" + type);
			}
			Credence credence = (Credence) class1.newInstance();
			credence.setType(type);
			return credence;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Invalid Credence type:" + type);
	}

	/**
	 * 注册模型OID
	 * 
	 * @param oid
	 * @param enterprise
	 * @param category
	 */
	public void registerModelOid(String oid, String enterprise, String category) {
		if ((oid == null) || (this.modelOids.containsKey(oid))) {
			throw new IllegalArgumentException("register invalid Model Oid.");
		}
		NetElementModel em = new NetElementModel();
		em.setOid(oid);
		em.setEnterprise(enterprise);
		em.setCategory(category);
		this.modelOids.put(oid, em);
	}

	/**
	 * 获取模型OID集合列表
	 * 
	 * @return
	 */
	public Map<String, NetElementModel> getModelOids() {
		return Collections.unmodifiableMap(this.modelOids);
	}

	/**
	 * 获取凭证类型集合列表
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getCredenceTypes() {
		if (this.credenceTypes == null)
			this.credenceTypes = new ArrayList(this.credenceClazzs.keySet());
		if (this.credenceTypes.contains("SMI")) {
			this.credenceTypes.remove("SMI");
		}
		return this.credenceTypes;
	}
}
