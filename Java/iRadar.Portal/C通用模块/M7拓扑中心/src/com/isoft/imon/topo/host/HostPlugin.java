package com.isoft.imon.topo.host;

import java.util.Map;

import net.percederberg.mibble.Mib;

import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.MibConvertor;
import com.isoft.imon.topo.platform.plugin.CommonPlugin;
import com.isoft.imon.topo.platform.plugin.Plugin;
import com.isoft.imon.topo.util.SysConfigHelper;

/**
 * @author hero 2014年3月6日
 */
public class HostPlugin extends CommonPlugin implements Plugin {
	private static final String PATH_CONFIG = SysConfigHelper.getAttribute("configPath") + "host/";
	private static final String PATH_MIB = PATH_CONFIG + "mib/";
	private final MibConvertor convertor;

	/**
	 * 构造方法
	 */
	public HostPlugin() {
		this.convertor = new MibConvertor();
	}

	/**
	 * 注册
	 */
	public void register() {
		registerCredences();

		registerElementModels();

		registerCiscoProducts();
		registerHuaweiProducts();
		registerHuawei3ComProducts();

		registerDictionaryData();
		registerPollers();
		registerCategories();
	}

	/**
	 * 
	 */
	public void start() {
	}

	/**
	 * 凭证注册
	 * 
	 * void
	 */
	private void registerCredences() {
		this.pool.registerCredence("SNMP", SnmpCredence.class);
	}

	/**
	 * 注册思科产品 void
	 */
	private void registerCiscoProducts() {
		String vendor = "Cisco";
		this.pool.registerProducer("1.3.6.1.4.1.9.", vendor, "cisco");

		Map<String, String> map = this.convertor.parseSymbol(PATH_MIB + "CISCO-PRODUCTS-MIB.mib");
		this.pool.registerElementModel(map, vendor, "cisco");
	}

	/**
	 * 注册华为产品 void
	 */
	private void registerHuaweiProducts() {
		String vendor = "Huawei";
		this.pool.registerProducer("1.3.6.1.4.1.2011.", vendor, "huawei");

		Map<String, String> map = this.convertor.parseSymbol(PATH_MIB + "HUAWEI-MIB.mib");
		this.pool.registerElementModel(map, vendor, "huawei");
		Map<String, String> map2 = this.convertor.parseSymbol(PATH_MIB + "HUAWEI-3COM-OID-MIB.mib");
		this.pool.registerElementModel(map2, vendor, "huawei");
	}

	/**
	 * 注册华为H3C产品 void
	 */
	private void registerHuawei3ComProducts() {
		String vendor = "H3C";
		this.pool.registerProducer("1.3.6.1.4.1.25506.", vendor, "h3c");

		Map<String, String> map = this.convertor.parseSymbol(PATH_MIB + "hh3c-product-id.mib");
		this.pool.registerElementModel(map, vendor, "h3c");
	}

	/**
	 * 注册element
	 * 
	 * void
	 */
	private void registerElementModels() {
		this.pool.registerElementModel(PATH_CONFIG + "elementModel.xml");
	}

	/**
	 * 
	 * void
	 */
	private void registerDictionaryData() {
		Mib rfc1213 = this.convertor.loadMib(PATH_MIB + "RFC1213.mib");
		DictionaryFactory.getFactory().registerEntries("ipRouteProto", this.convertor.parseType(rfc1213, "ipRouteProto"));
		DictionaryFactory.getFactory().registerEntries("ipRouteType", this.convertor.parseType(rfc1213, "ipRouteType"));
		DictionaryFactory.getFactory().registerEntries("ipNetToMediaType", this.convertor.parseType(rfc1213, "ipNetToMediaType"));
		DictionaryFactory.getFactory().registerEntries("ifType", this.convertor.parseType(rfc1213, "ifType"));
		DictionaryFactory.getFactory().registerEntries("operStatus", this.convertor.parseType(rfc1213, "ifOperStatus"));
		DictionaryFactory.getFactory().registerEntries("adminStatus", this.convertor.parseType(rfc1213, "ifAdminStatus"));
	}

	/**
	 * 
	 * void
	 */
	protected void registerPollers() {
		this.pool.registerPoller(PATH_CONFIG + "pollers.xml");
	}

	/**
	 * 注册类型 void
	 */
	private void registerCategories() {
		this.pool.registerCategory(HostConstants.getRouterCategory());
		this.pool.registerCategory(HostConstants.getRouteSwitchCategory());
		this.pool.registerCategory(HostConstants.getSwitchCategory());
		this.pool.registerCategory(HostConstants.getFirewallCategory());
		this.pool.registerCategory(HostConstants.getServerCategory());
		this.pool.registerCategory(HostConstants.getLinkCategory());
	}

	/**
	 * 
	 */
	public String getName() {
		return "主机管理模块";
	}

	/**
	 * 方法重载
	 */
	@Override
	public void shutdown() {
	}
}