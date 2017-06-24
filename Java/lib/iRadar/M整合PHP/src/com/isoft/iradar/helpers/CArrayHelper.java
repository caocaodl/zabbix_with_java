package com.isoft.iradar.helpers;

import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.strnatcasecmp;
import static com.isoft.iradar.Cphp.uasort;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.types.CArray.array;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import static com.isoft.types.CArray.*;
import com.isoft.types.Mapper.Nest;

public abstract class CArrayHelper {

	/**
	 * Get from array only values with given keys.
	 * If requested key is not in given array exception is thrown.
	 *
	 * @static
	 * @throws InvalidArgumentException
	 *
	 * @param array array
	 * @param array keys
	 *
	 * @return array
	 */
	public static <T> CArray<T> getByKeysStrict(CArray<T> array, CArray keys) {
		CArray<T> result = array();
		for(Object key: keys) {
			T v = array.get(key);
			if (!isset(v)) {
				throw new IllegalArgumentException(sprintf("Array does not have element with key \"%1s\".", key));
			}
			result.put(key, v);
		}
		return result;
	}

	/**
	 * Get values with the keys from array.
	 * If the requested key is not in the given array it is skipped.
	 *
	 * @static
	 *
	 * @param array array
	 * @param array keys
	 *
	 * @return array
	 */
	public static Map getByKeys(Map array, CArray keys) {
		Map result = array();
		for(Object key: keys) {
			Object v = array.get(key);
			if (isset(v)) {
				result.put(key, v);
			}
		}
		return result;
	}

	/**
	 * Converts the field with the given key to either an empty array, if the field is empty, or to an array with numeric keys.
	 * If the field with the given key does not exist, does nothing.
	 *
	 * @param Map  array
	 * @param String fieldKey
	 */
	public static void convertFieldToArray(Map array, String fieldKey) {
		if (array_key_exists(fieldKey, array)) {
			Object v = array.get(fieldKey);
			if (isArray(v)) {
				array.put(fieldKey, array_values(array(v)));
			} else {
				array.put(fieldKey, array());
			}
		}
	}

	/**
	 * Sort array by multiple fields.
	 *
	 * @static
	 *
	 * @param array array  array to sort passed by reference
	 * @param array fields fields to sort, can be either string with field name or array with 'field' and 'order' keys
	 */
	public static void sort(CArray array, final CArray fields) {
		for(Entry<Object, Object> e: (Set<Entry>)fields.entrySet()) {
			Object fid = e.getKey();
			Object field = e.getValue();
			
			if (!isArray(field)) {
				fields.put(fid, map("field", field, "order", RDA_SORT_UP));
			}
		}
		uasort(array, new Comparator<Map>() {
			@Override public int compare(Map _a, Map _b) {
				for(Object fieldO: fields) {
					Map mfield = (Map)fieldO;
					Object field = mfield.get("field");
					
					Object a = _a.get(field);
					Object b = _b.get(field);
					
					int cmp;
					
					// if field is not set or is null, treat it as smallest string
					// strnatcasecmp() has unexpected behaviour with null values
					if (!isset(a) && !isset(b)) {
						cmp = 0;
					} else if (!isset(a)) {
						cmp = -1;
					} else if (!isset(b)) {
						cmp = 1;
					} else {
						cmp = strnatcasecmp(EasyObject.asString(a), EasyObject.asString(b));
					}

					if (cmp != 0) {
						return cmp * (RDA_SORT_UP.equals(mfield.get("order"))? 1: -1);
					}
				}
				return 0;
			}
		});
	}

	/**
	 * Unset values that are contained in a2 from a1. Skip arrays and keys given in skipKeys.
	 *
	 * @param array a1         array to modify
	 * @param array a2         array to compare with
	 * @param array skipKeys   fields to ignore
	 *
	 * @return array
	 */
	public static Map unsetEqualValues(Map a1, Map a2) {
		return unsetEqualValues(a1, a2, array());
	}
	
	public static Map unsetEqualValues(Map a1, Map a2, CArray skipKeys) {
		a1 = Clone.deepcopy(a1);
		a2 = Clone.deepcopy(a2);
		// ignore given fields
		for(Object key: skipKeys) {
			unset(a2,key);
		}
		for(Entry<Object, Object> e: Clone.deepcopy((Map<Object, Object>)a1).entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();			
			// check if the values under key are equal, skip arrays
			if (isset(a2,key) && !isArray(value) && Nest.value(a2,key).asString(true).equals(Nest.value(a1,key).asString(true))) {
				unset(a1,key);
			}
		}
		return a1;
	}

	/**
	 * Checks if array arrays contains arrays with duplicate values under the uniqueField key. If a duplicate exists,
	 * returns the first duplicate, otherwise returns null.
	 *
	 * Example 1:
	 * data = array(
	 *     array('name' => 'CPU load'),
	 * 	   array('name' => 'CPU load'),
	 * 	   array('name' => 'Free memory')
	 * );
	 * var_dump(CArrayHelper::findDuplicate(data, 'name')); // returns array with index 1
	 *
	 * Example 2:
	 * data = array(
	 *     array('host' => 'iRadar server', 'name' => 'CPU load'),
	 * 	   array('host' => 'iRadar server', 'name' => 'Free memory'),
	 * 	   array('host' => 'Linux server', 'name' => 'CPU load'),
	 * 	   array('host' => 'iRadar server', 'name' => 'CPU load')
	 * );
	 * var_dump(CArrayHelper::findDuplicate(data, 'name', 'host')); // returns array with index 3
	 *
	 * @param array arrays         an array of arrays
	 * @param string uniqueField   key to be used as unique criteria
	 * @param string uniqueField2	second key to be used as unique criteria
	 *
	 * @return null|array           the first duplicate found or null if there are no duplicates
	 */
	public static Map findDuplicate(CArray<Map> arrays, String uniqueField, String uniqueField2) {
		CArray uniqueValues = new CArray();
		for(Map array: arrays) {
			Object value = array.get(uniqueField);
			if (uniqueField2 != null) {
				Object uniqueByValue = array.get(uniqueField2);
				if (isset(uniqueValues, uniqueByValue) && isset(Nest.value(uniqueValues,uniqueByValue,value).$())) {
					return array;
				} else {
					if(!isset(uniqueValues, uniqueByValue)){
						Nest.value(uniqueValues,uniqueByValue).$(new CArray());
					}
					Nest.value(uniqueValues,uniqueByValue,value).$(value);
				}
			} else {
				if (isset(uniqueValues,value)) {
					return array;
				} else {
					Nest.value(uniqueValues,value).$(value);
				}
			}
		}
		return null;
	}
	
	public static Map findDuplicate(CArray<Map> arrays, String uniqueField) {
		return findDuplicate(arrays, uniqueField, null);
	}
}
