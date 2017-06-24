package com.isoft.iradar.web.bean;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.common.util.LatestValueHelper.Matcher;
import com.isoft.iradar.common.util.LatestValueHelper.NormalValue;
import com.isoft.iradar.common.util.LatestValueHelper.PrototypeValues;
import com.isoft.iradar.common.util.LatestValueHelper.ValuePrinter;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CSpan;
import com.isoft.types.CArray;

public abstract class Key{
	protected ItemsKey key;
	protected CArray<ItemsKey> keyCArray;
	public abstract Object show(Long hostid);
	
	public static Key status(ItemsKey key) {
		return new Key(key) {
			@Override public Object show(Long hostid) {
				NormalValue nv = normalValue(hostid);
				boolean active = nv.value().asInteger()>0;
				
				CSpan ctn = new CSpan();
				CSpan text = new CSpan();
				if(active) {
					ctn.addItem(new CImg("images/gradients/normal.png"));
					text.addItem("激活");
				}else {
					ctn.addItem(new CImg("images/gradients/anormal.png"));
					text.addItem("未激活");
				}
				text.setAttribute("style", "margin-left: 0.5em;");
				ctn.addItem(text);
				return ctn;
			}
		};
	}
	
	public static Key value(CArray<ItemsKey> key) {
		return value(key,null, false);
	}
	
	public static Key value(ItemsKey key) {
		return value(key,null, false);
	}
	
	public static Key value(ItemsKey key,final Integer round) {
		return value(key,round,false);
	}
	
	public static Key value(ItemsKey key,final boolean format) {
		return value(key,null,format);
	}
	
	public static Key value(ItemsKey key,final Integer round, final boolean format) {
		return new Key(key) {
			@Override public String show(Long hostid) {
				NormalValue nv = normalValue(hostid);
				if(round != null) {
					nv = nv.round(2);
				}
				return format? nv.out().format(): nv.out().print();
			}
		};
	}
	
	public static Key value(CArray<ItemsKey> keyCArray,final Integer round, final boolean format) {
		return new Key(keyCArray) {
			@Override public String show(Long hostid) {
				NormalValue nv = normalValue(hostid);
				if(round != null) {
					nv = nv.round(2);
				}
				return format? nv.out().format(): nv.out().print();
			}
		};
	}
	
	public static Key avg(ItemsKey key) {
		return avg(key, null);
	}
	public static Key avg(ItemsKey key, final Integer round) {
		return avg(key, round, false);
	}
	public static Key avg(ItemsKey key, final boolean format) {
		return avg(key, null, format);
	}
	public static Key avg(ItemsKey key, final Integer round, final boolean format) {
		return new Key(key) {
			@Override public String show(Long hostid) {
				NormalValue nv = prototypeValues(hostid).avg();
				if(round != null) {
					nv = nv.round(round);
				}
				ValuePrinter vp = nv.out(); 
				return format? vp.format(): vp.print();
			}
		};
	}
	
	public static Key sum(ItemsKey key) {
		return sum(key, false);
	}
	public static Key sum(ItemsKey key, final boolean format) {
		return new Key(key) {
			@Override public String show(Long hostid) {
				ValuePrinter vp = prototypeValues(hostid).sum().round(2).out(); 
				return format? vp.format(): vp.print();
			}
		};
	}
	
	public static Key count(ItemsKey key){
		return new Key(key) {
			@Override public String show(Long hostid) {
				ValuePrinter vp = prototypeValues(hostid).count().out();
				return vp.print();
			}
		};
	}
	
	public static Key count(ItemsKey key, final String match){
		return new Key(key) {
			@Override public String show(Long hostid) {
				Matcher m = null;
				if(match != null) {
					m = new Matcher(){
						@Override public boolean match(Object o) {
							return match.equals(String.valueOf(o));
						}
					};
				}
				ValuePrinter vp = prototypeValues(hostid).count(m).out();
				return vp.print();
			}
		};
	}
	
	protected Key(ItemsKey key) {
		this.key = key;
	}
	
	protected Key(CArray<ItemsKey> keyCArray) {
		this.keyCArray = keyCArray;
	}
	
	public ItemsKey itemKey() {
		return this.key;
	}
	
	public CArray<ItemsKey> itemKeyCArray(){
		return this.keyCArray;
	}
	
	protected NormalValue normalValue(Long hostid) {
		if(Cphp.isset(this.keyCArray)){
			String value = LatestValueHelper.NA;
			NormalValue normalValue  = null;
			for(ItemsKey key:keyCArray){
				normalValue = LatestValueHelper.buildByNormalKey(hostid, key.getValue()).value();
				if(!LatestValueHelper.NA.equals(normalValue.out().print()))
					return normalValue;
			}
			return LatestValueHelper.getNullNormalValue();
		}
		return LatestValueHelper.buildByNormalKey(hostid, this.key.getValue()).value();
	}
	protected PrototypeValues prototypeValues(Long hostid) {
		return LatestValueHelper.buildByPrototypeKey(hostid, this.key.getValue()).values();
	}
}