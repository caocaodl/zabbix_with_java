package com.isoft.imon.topo.host.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.type.IntegerType;

import com.isoft.imon.topo.admin.factory.DictionaryEntry;

/**
 * 关于MIb文件的操作类
 * 
 * @author soft
 * 
 */
public class MibConvertor {
	public Mib loadMib(String mibFile) {
		mibFile = mibFile.replace("%20", " ");
		MibLoader loader = new MibLoader();
		File file = new File(mibFile);
		loader.addDir(file.getParentFile());
		try {
			return loader.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("can not load " + mibFile);
	}

	public Map<String, String> parseSymbol(String mibFile) {
		return parseSymbol(loadMib(mibFile));
	}

	protected Map<String, String> parseSymbol(Mib mib) {
		Iterator<?> iter = mib.getAllSymbols().iterator();
		Map<String, String> products = new HashMap<String, String>();
		while (iter.hasNext()) {
			MibSymbol _symbol = (MibSymbol) iter.next();
			if ((_symbol instanceof MibValueSymbol)) {
				MibValueSymbol symbol = (MibValueSymbol) _symbol;
				products.put(symbol.getValue().toString(), symbol.getName());
			}
		}
		return products;
	}

	public List<DictionaryEntry> parseType(Mib mib, String symbol) {
		List<DictionaryEntry> des = new ArrayList<DictionaryEntry>();
		Iterator<?> iter = mib.getAllSymbols().iterator();
		while (iter.hasNext()) {
			MibSymbol _symbol = (MibSymbol) iter.next();
			// if ((!(_symbol instanceof MibValueSymbol))
			// || (!((MibValueSymbol) _symbol).getName().equals(symbol)))
			if ((!(_symbol instanceof MibValueSymbol)) || (!_symbol.getName().equals(symbol)))
				continue;
			SnmpObjectType sot = (SnmpObjectType) ((MibValueSymbol) _symbol).getType();
			IntegerType intType = (IntegerType) sot.getSyntax();
			MibValueSymbol[] itss = intType.getAllSymbols();
			if (itss.length > 0) {
				for (int j = 0; j < itss.length; j++) {
					DictionaryEntry de = new DictionaryEntry();
					de.setKey(itss[j].getValue().toString());
					de.setValue(itss[j].getName());
					des.add(de);
				}
			}
		}

		return des;
	}
}
