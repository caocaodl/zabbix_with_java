package com.isoft.iradar.model;

import static com.isoft.iradar.Cphp.array_intersect_key;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.TArray;

public class CRelationMap {

	private CArray<CArray> map = new CArray<CArray>();
	private CArray relatedIds = new CArray();

	public void addRelation(Object baseObjectId, Object relatedObjectId) {
		this.map.put(baseObjectId, relatedObjectId, relatedObjectId);
		this.relatedIds.put(relatedObjectId, relatedObjectId);
	}

	public Long[] getRelatedLongIds() {
		return TArray.as(this.relatedIds.keys()).asLong();
	}
	
	public String[] getRelatedStringIds() {
		return TArray.as(this.relatedIds.keys()).asString();
	}

	public void mapMany(CArray<Map> baseObjects, CArray<Map> relatedObjects, String name) {
		mapMany(baseObjects, relatedObjects, name, null);
	}

	public void mapMany(CArray<Map> baseObjects, CArray<Map> relatedObjects, String name, Integer limit) {
		for (Entry<Object, Map> e : baseObjects.entrySet()) {
			Object baseObjectId = e.getKey();
			Map baseObject = e.getValue();
			CArray<Map> matchingRelatedObjects = new CArray();

			CArray<Object> mapObj = this.map.get(baseObjectId);
			if (isset(mapObj) && !empty(mapObj)) {
				matchingRelatedObjects = array_values(array_intersect_key(relatedObjects, mapObj));
				if (!empty(matchingRelatedObjects)) {
					if (!empty(limit)) {
						matchingRelatedObjects = array_slice(matchingRelatedObjects, 0, limit);
					}
				}
			}

			baseObject.put(name, matchingRelatedObjects);
		}
	}

	/**
	 * Maps multiple related objects to the base objects and adds them under the _name property.
	 * Each base object will have only one related object.
	 *
	 * @param array  _baseObjects		a hash of base objects with IDs as keys
	 * @param array  _relatedObjects	a hash of related objects with IDs as keys
	 * @param string _name				the name of the property under which the related object will be added
	 *
	 * @return array
	 */
	public void mapOne(CArray<Map> _baseObjects, CArray<Map> _relatedObjects,
			String _name) {
		for (Entry<Object, Map> e : _baseObjects.entrySet()) {
			Object _baseObjectId = e.getKey();
			Map _baseObject = e.getValue();

			Object _matchingRelatedObject = array();
			CArray map = CArray.valueOf(this.map.get(_baseObjectId));
			if (isset(map)) {
				Object _matchingRelatedId = reset(map);
				Object relatedObject = _relatedObjects.get(_matchingRelatedId);
				if (isset(relatedObject)) {
					_matchingRelatedObject = relatedObject;
				}
			}
			_baseObject.put(_name, _matchingRelatedObject);
		}
	}
}
