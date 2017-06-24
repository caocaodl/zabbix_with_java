package com.isoft.iradar.common.util;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.bcdiv;
import static com.isoft.iradar.Cphp.bcmul;
import static com.isoft.iradar.Cphp.bcpow;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.common.util.LatestValueHelper.NA;
import static com.isoft.iradar.common.util.LatestValueHelper.convert;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_NO_UNITS;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_WITH_UNITS;
import static com.isoft.iradar.inc.Defines.RDA_PRECISION_10;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.LatestValueHelper.Matcher;
import com.isoft.iradar.common.util.LatestValueHelper.NormalValue;
import com.isoft.iradar.common.util.LatestValueHelper.PrototypeValues;
import com.isoft.iradar.common.util.LatestValueHelper.ValuePrinter;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.data.cache.CacheHelper;
import com.isoft.iradar.data.cache.ItemDataCollectdCache;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.ItemsUtil;
import com.isoft.iradar.managers.Manager;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;
import com.isoft.types.Mapper.TObject;

public class LatestValueHelper {
	private static final Logger LOG = LoggerFactory.getLogger(LatestValueHelper.class);
	
	public final static String NA =  "--"; //"N/A"
	
	private CArray<Map> items;
	private CArray<CArray<Map>> latestValues;
	
	
	public static CArray<CArray<Map>> fetchValue(CArray<Map> items, boolean useCache) {
		Map<Object,List> typeHash = EasyList.groupBy(items.toList(), "type");
		for(Entry<Object, List> entry: typeHash.entrySet()) {
			Integer type = EasyObject.asInteger(entry.getKey());
			if(Defines.ITEM_TYPE_TRAPPER == type){
				CArray<Map> typeItems = CArray.valueOf(entry.getValue());
				for(Map item:typeItems){
					Nest.value(item, "delay").$(Defines.RDA_HISTORY_PERIOD);
				}
			}
		}
		
		Map<Object, List> hash = EasyList.groupBy(items.toList(), "delay");
		
		CArray<CArray<Map>> historys = CArray.array();
		for(Entry<Object, List> entry: hash.entrySet()) {
			CArray<Map> delayItems = CArray.valueOf(entry.getValue());
			Integer delay = EasyObject.asInteger(entry.getKey());
			if(Cphp.empty(delay)) {
				delay = 60*10;
			}
			int period = delay *4; //四个采集间隔之内的值
			
			if(useCache) {
				Iterator<Map> itemsIterator = delayItems.iterator();
				while(itemsIterator.hasNext()) {
					Map item = itemsIterator.next();
					Long hostid = Nest.value(item, "hostid").asLong(true);
					String key = Nest.value(item, "key_").asString(true);
					if(Cphp.issets(hostid, key)) {
						CArray value = (CArray)CacheHelper.getHostItemData(hostid, key);
						if(Cphp.isset(value)) {
							Cphp.array_add(historys, value);
							itemsIterator.remove();
							if(LOG.isDebugEnabled()) {
								LOG.debug("++cache matche:"+key);
							}
						}
					}
				}
			}
			
			if(!Cphp.empty(delayItems)) {
				if(LOG.isDebugEnabled()) {
					if(useCache) {
						for(Map item: delayItems) {
							String key = Nest.value(item, "key_").asString(true);
							LOG.debug("------cache miss:"+key);
						}
					}
				}
				CArray<CArray<Map>> delayHistorys = Manager.History(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).getLast(delayItems, 1, period); 
				Cphp.array_add(historys, delayHistorys);
			}
		}
		return historys;
	} 
	
	public static LatestValueHelper build(CArray<Map> items) {
		CArray<CArray<Map>> historys = fetchValue(items, true);
		return new LatestValueHelper(items, historys);
	}
	
	public static LatestValueHelper build(Map item) {
		return build(CArray.array(item));
	}
	
	
	/**
	 * 将hostid和key的参数的MAP，全放到一个CArray中，来进行整体处理
	 * 
	 * 
	 * @param keyInfos 集合KEY要求：  设备ID对应 hostid， 键值对应key  
	 * @return
	 */
	public static LatestValueHelper buildByNormalKey(CArray<Map> keyInfos) {
		CArray items = CArray.array();
		if(!empty(keyInfos)) {
			for(Map keyInfo: keyInfos) {
				Long hostid = Nest.value(keyInfo, "hostid").asLong();
				String key = Nest.value(keyInfo, "key").asString();
				CArray<Map> item = DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, key);
				for(Map m: item) {
					items.add(m);
				}
			}
		}
		return build(items);
	}
	public static LatestValueHelper buildByNormalKey(Long hostid, ItemsKey... keys) {
		CArray array = array();
		for(ItemsKey key: keys) array.push(key.getValue());
		return buildByNormalKeys(hostid, array);
	}
	public static LatestValueHelper buildByNormalKey(Long hostid, String... keys) {
		return buildByNormalKeys(hostid, CArray.array(keys));
	}
	public static LatestValueHelper buildByNormalKeys(Long hostid, CArray<String> keys) {
		CArray items = CArray.array();
		if(!empty(keys)) {
			for(String key: keys) {
				CArray<Map> item = DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, key);
				for(Map m: item) {
					items.add(m);
				}
			}
		}
		return build(items);
	}
	
	/**
	 * 将hostid和key的参数的MAP，全放到一个CArray中，来进行整体处理
	 * 
	 * 
	 * @param keyInfos 集合KEY要求：  设备ID对应 hostid， 键值对应key  
	 * @return
	 */
	public static LatestValueHelper buildByPrototypeKey(CArray<Map> keyInfos) {
		CArray items = CArray.array();
		if(!empty(keyInfos)) {
			for(Map keyInfo: keyInfos) {
				Long hostid = Nest.value(keyInfo, "hostid").asLong();
				String key = Nest.value(keyInfo, "key").asString();
				CArray<Map> item = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, key);
				for(Map m: item) {
					items.add(m);
				}
			}
		}
		return build(items);
	}
	public static LatestValueHelper buildByPrototypeKey(Long hostid, String key) {
		return build(DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, key));
	}
	
	
	public LatestValueHelper(CArray<Map> items, CArray<CArray<Map>> latestValues) {
		this.items = items;
		this.latestValues = latestValues;
	}
	
	/**
	 * 一个KEY获取一个值
	 * 
	 * @return
	 */
	public NormalValue value() {
		Map item = Cphp.reset(this.items);
		if(!empty(item) && !empty(this.latestValues)) {
			Object itemid = item.get("itemid");
			CArray<Map> latestValue = this.latestValues.get(itemid);
			
			Map value = Cphp.reset(latestValue);
			if(value != null) {
				Object v = Nest.value(value, "value").$();
				return new SimpleNormalValue(item, v);
			}
		}
		return NullNormalValue.INSTANCE;
	}
	
	/**
	 * 多个KEY多个值，主要用于item原型生成的KEY
	 * 
	 * @return
	 */
	public PrototypeValues values() {
		return new SimplePrototypeValues(items, latestValues);
	}
	
	
	public static interface Matcher{
		boolean match(Object o);
	}
	
	public static interface ValuePrinter{
		String format();
		String print();
	}

	public static interface NormalValue{
		int POW_K = 1;
		int POW_M = 2;
		int POW_G = 3;
		int POW_T = 4;
		int POW_P = 5;
		
		TObj value();
		ValuePrinter out();
		NormalValue round(int scale);
		NormalValue convertUnit(Integer pow);
	}

	public static interface PrototypeValues{
		NormalValue avg();
		NormalValue sum();
		NormalValue count();
		NormalValue count(Matcher m);
	}
	
	public static Double convert(CArray options) {
		// if one or more items is B or Bps, then Y-scale use base 8 and calculated in bytes
		int step = 0;
		String units = Nest.value(options,"units").asString();
		if("Bps".equals(units) || "B".equals(units)) {
			step = 1024;
			Nest.value(options,"convert").$(Nest.value(options,"convert").asBoolean() ? Nest.value(options,"convert").$() : ITEM_CONVERT_NO_UNITS);
		} else {
			if("bps".equals(units) || "b".equals(units)) {
				Nest.value(options,"convert").$(Nest.value(options,"convert").asBoolean() ? Nest.value(options,"convert").$() : ITEM_CONVERT_NO_UNITS);
			}
			step = 1000;
		}

		double abs;
		if (Nest.value(options,"value").asDouble() < 0) {
			abs = bcmul(Nest.value(options,"value").asDouble(), -1);
		} else {
			abs = Nest.value(options,"value").asDouble();
		}
		
		//FIXME: 修复value==0时，出现0.0的情况
		if(abs == 0d) {
			return 0d;
		}else {
			if (bccomp(abs, 1) == -1) {
				Nest.value(options,"value").$(Cphp.round(Nest.value(options,"value").asDouble(), RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT));
				Nest.value(options,"value").$(
						(Nest.value(options,"length").asBoolean() && Nest.value(options,"value").asInteger() != 0)
						? sprintf("%."+options.get("length")+"f",Nest.value(options,"value").$()) 
						: Nest.value(options,"value").$()
					);
				
				return Nest.value(options, "value").asDouble();
			}

			// init intervals
			CArray<CArray<CArray>> digitUnits = null;
			if (is_null(digitUnits)) {
				digitUnits = array();
			}
			if (!isset(digitUnits.get(step))) {
				digitUnits.put(step, array(
					map("pow", 0, "short", "", "long", ""),
					map("pow", 1, "short", _x("K", "Kilo short"), "long", _("Kilo")),
					map("pow", 2, "short", _x("M", "Mega short"), "long", _("Mega")),
					map("pow", 3, "short", _x("G", "Giga short"), "long", _("Giga")),
					map("pow", 4, "short", _x("T", "Tera short"), "long", _("Tera")),
					map("pow", 5, "short", _x("P", "Peta short"), "long", _("Peta")),
					map("pow", 6, "short", _x("E", "Exa short"), "long", _("Exa")),
					map("pow", 7, "short", _x("Z", "Zetta short"), "long", _("Zetta")),
					map("pow", 8, "short", _x("Y", "Yotta short"), "long", _("Yotta"))
				));

				for(CArray data: digitUnits.get(step)) {

					// skip mili & micro for values without units
					Nest.value(data, "value").$(bcpow(step, Nest.value(data,"pow").asDouble(), 9));
				}
			}


			CArray valUnit = map("pow", 0, "short", "", "long", "", "value", Nest.value(options, "value").$());

			if (Nest.value(options,"pow").asBoolean() == false || Nest.value(options,"value").asDouble() == 0) {
				for(Entry<Object, CArray> entry: digitUnits.get(step).entrySet()) {
					//Object _dnum = entry.getKey();
					CArray _data = entry.getValue();
				
					if (bccomp(abs, Nest.value(_data,"value").asDouble()) > -1) {
						valUnit = _data;
					} else {
						break;
					}
				}
			} else {
				for(CArray data: digitUnits.get(step)) {
					if (Nest.value(options,"pow").asDouble() == Nest.value(data,"pow").asDouble()) {
						valUnit = data;
						break;
					}
				}
			}

			if (Cphp.round(Nest.value(valUnit,"value").asDouble(), RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT) > 0) {
				Nest.value(valUnit,"value").$(bcdiv(
						Double.valueOf(sprintf("%.10f",Nest.value(options,"value").asDouble())), 
						Double.valueOf(sprintf("%.10f", Nest.value(valUnit,"value").asDouble())), 
						RDA_PRECISION_10));
			} else {
				Nest.value(valUnit,"value").$(0);
			}
			
			return Nest.value(valUnit, "value").asDouble();
		}
	}
	
	public static NormalValue getNullNormalValue(){
		return NullNormalValue.INSTANCE;
	}
}


class SimpleValuePrinter implements ValuePrinter{
	private Map item;
	private TObj value;
	
	public SimpleValuePrinter(Map item, TObj value) {
		this.item = item;
		this.value = value;
	}
	
	@Override
	public String format() {
		return ItemsUtil.formatHistoryValue(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor(), value.asString(), item);
	}

	@Override
	public String print() {
		return value.asString();
	}
}

class NullNormalValue implements NormalValue{
	public final static NullNormalValue INSTANCE = new NullNormalValue();
	private final static TObject NULL = TObject.as(null);
	
	public final static ValuePrinter PRINTER = new ValuePrinter() {
		@Override
		public String format() {
			return NA;
		}
		@Override
		public String print() {
			return NA;
		}
	};
	@Override
	public TObject value() {
		return NULL;
	}
	@Override
	public ValuePrinter out() {
		return PRINTER;
	}
	@Override
	public NormalValue round(int scale) {
		return this;
	}
	@Override
	public NormalValue convertUnit(Integer pow) {
		return this;
	}
}

class SimpleNormalValue implements NormalValue{
	private Map item;
	private TObj obj;
	
	public SimpleNormalValue(Map item, Object obj) {
		this.item = item;
		this.obj = TObj.as(obj);
	}
	
	@Override
	public TObj value() {
		return obj;
	}

	@Override
	public ValuePrinter out() {
		if(obj.$() == null) return NullNormalValue.PRINTER;
		return new SimpleValuePrinter(item, obj);
	}
	
	@Override
	public NormalValue round(int scale) {
		if(obj.$() == null) return NullNormalValue.INSTANCE;
		
		Double d = obj.asDouble();
		d = Cphp.round(d, scale);
		return new SimpleNormalValue(item, d);
	}
	
	@Override
	public NormalValue convertUnit(Integer pow) {
		if(obj.$() == null) return NullNormalValue.INSTANCE;
		CArray options = map(
			"units", Nest.value(item, "units").asString(),
			"pow", pow,
			"convert", ITEM_CONVERT_WITH_UNITS,
			"value", obj.asDouble(true)
		);
		
		Double d = convert(options);
		return new SimpleNormalValue(item, d);
	}
	
	
}

class SimplePrototypeValues implements PrototypeValues{
	private CArray<Map> items;
	private CArray<CArray<Map>> latestValues;
	public SimplePrototypeValues(CArray<Map> items, CArray<CArray<Map>> latestValues) {
		this.items = items;
		this.latestValues = latestValues;
	}
	
	private NormalValue buildNormal(Object v) {
		return new SimpleNormalValue(Cphp.reset(items), v);
	}

	@Override
	public NormalValue avg() {
		if(empty(this.latestValues)) {
			return NullNormalValue.INSTANCE;
		}
		
		Double sum = 0d;
		int count = 0;
		for(Map item: this.items) {
			Object itemid = item.get("itemid");
			CArray<Map> latestValue = this.latestValues.get(itemid);
			Double v = Nest.value(latestValue, 0, "value").asDouble(true);
			if(v == null) {
				continue;
			}else {
				sum += v;
				count++;
			}
		}
		
		if(count == 0) {
			return NullNormalValue.INSTANCE;
		}else {
			double v = sum/count;
			return buildNormal(v);
		}
	}

	@Override
	public NormalValue sum() {
		if(empty(this.latestValues)) {
			return NullNormalValue.INSTANCE;
		}
		
		Double sum = 0d;
		for(Map item: this.items) {
			Object itemid = item.get("itemid");
			CArray<Map> latestValue = this.latestValues.get(itemid);
			Double v = Nest.value(latestValue, 0, "value").asDouble();
			sum += v==null? 0: v;
		}
		
		return buildNormal(sum);
	}
	
	@Override
	public NormalValue count(Matcher m) {
		if(empty(this.latestValues)) {
			return NullNormalValue.INSTANCE;
		}
		
		int count = 0;
		for(Map item: this.items) {
			Object itemid = item.get("itemid");
			CArray<Map> latestValue = this.latestValues.get(itemid);
			Object v = Nest.value(latestValue, 0, "value").$();
			
			if(m == null) {
				if(v != null) {
					count++;
				}
			}else {
				if(m.match(v)) {
					count++;
				}
			}
		}
		return buildNormal(count);
	}

	@Override
	public NormalValue count() {
		return buildNormal(this.items.size());
	} 
}