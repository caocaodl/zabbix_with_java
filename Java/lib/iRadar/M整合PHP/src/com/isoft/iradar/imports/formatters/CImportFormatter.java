package com.isoft.iradar.imports.formatters;

import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.lang.Clone.deepcopy;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Abstract class to extend for all import formatters.
 * For each different version of configuration import new formatter should be defined. All formatters must return
 * data in one format, so that further processing is independent from configuration import version.
 */
public abstract class CImportFormatter {

	/**
	 * @var CArray configuration import data
	 */
	protected CArray data;

	/**
	 * Data property setter.
	 * @param CArray data
	 */
	public void setData(CArray data) {
		this.data = data;
	}

	/**
	 * Renames array elements keys according to given map.
	 *
	 * @param Map data
	 * @param CArray fieldMap
	 *
	 * @return Map
	 */
	protected Map renameData(Map data, CArray fieldMap) {
		Map<String, Object> cdata = deepcopy(data);
		for (Entry<String, Object> e : cdata.entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();
			if (isset(fieldMap, key)) {
				Nest.value(data, fieldMap.get(key)).$(value);
				unset(data, key);
			}
		}
		return data;
	}

	/**
	 * Get formatted groups data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getGroups();

	/**
	 * Get formatted templates data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getTemplates();

	/**
	 * Get formatted hosts data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getHosts();

	/**
	 * Get formatted applications data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getApplications();

	/**
	 * Get formatted items data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getItems();

	/**
	 * Get formatted discovery rules data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getDiscoveryRules();

	/**
	 * Get formatted graphs data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getGraphs();

	/**
	 * Get formatted triggers data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getTriggers();

	/**
	 * Get formatted images data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getImages();

	/**
	 * Get formatted maps data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getMaps();

	/**
	 * Get formatted screens data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getScreens();

	/**
	 * Get formatted template screens data.
	 * @abstract
	 * @return CArray
	 */
	abstract public CArray getTemplateScreens();
	
}
