package com.isoft.iradar.model.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.ObjectUtils;

import com.isoft.types.Mapper.TArray;

public class SqlPart {
	public int anonyIndex = 0;
	public List<String> namedList = new LinkedList<String>();
	public List<String> valueList = new LinkedList<String>();
	public Map<String, String> namedMap = new LinkedMap();
	
	protected SqlBuilder sqlBuilder;
	
	public SqlPart(SqlBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	public String get(String key) {
		return namedMap.get(key);
	}

	public void put(String value) {
		put(String.valueOf(anonyIndex++), value);
	}

	public void put(String key, String value) {
		if (namedMap.containsKey(key)) {
			int idx = namedList.indexOf(key);
			namedList.remove(idx);
			valueList.remove(idx);
		}
		namedList.add(key);
		valueList.add(value);
		namedMap.put(key, value);
	}
	
	public void putahead(String value) {
		putahead(String.valueOf(anonyIndex++), value);
	}
	
	public void putahead(String key, String value) {
		if (namedMap.containsKey(key)) {
			int idx = namedList.indexOf(key);
			namedList.remove(idx);
			valueList.remove(idx);
		}
		namedList.add(0, key);
		valueList.add(0, value);
		namedMap.put(key, value);
	}
	
	public String dbConditionInt(String key, String field, Long[] values) {
		return dbConditionInt(key, field, values, null);
	}
	
	public String dbConditionInt(String key, String field, Long[] values, SqlDecorator decorator) {
		return dbConditionInt(key, field, values, false, decorator);
	}
	
	public String dbConditionInt(String key, String field, Long[] values, boolean notIn, SqlDecorator decorator) {
		return dbConditionInt(key, field, values, notIn, true, decorator);
	}
	
	public String dbConditionIntSort(String field, Long[] values, boolean sort) {
		return dbConditionInt(null, field, values, false, sort, null);
	}
	
	public String dbConditionInt(String fname, Long[] values, SqlDecorator decorator) {
		return dbConditionInt(null, fname, values, false, true, decorator);
	}
	
	public String dbConditionInt(String fname, Long[] values) {
		return dbConditionInt(null, fname, values, false, true);
	}
	
	public String dbConditionInt(String fname, int[] values) {
		return dbConditionInt(null, fname, TArray.as(values).asLong(), false, true);
	}
	
	public String dbConditionInt(String fname, Long[] values, boolean notIn, SqlDecorator decorator) {
		return dbConditionInt(null, fname, values, notIn, true, decorator);
	}
	
	public String dbConditionInt(String fname, Long[] values, boolean notIn) {
		return dbConditionInt(null, fname, values, notIn, true);
	}
	
	/**
	 * Takes an initial part of SQL query and appends a generated WHERE condition.
	 * The WHERE condition is generated from the given list of values as a mix of
	 * <fieldname> BETWEEN <id1> AND <idN>" and "<fieldname> IN (<id1>,<id2>,...,<idN>)" elements.
	 *
	 * @param string fname		field name to be used in SQL WHERE condition
	 * @param array  values		array of numerical values sorted in ascending order to be included in WHERE
	 * @param bool   notIn		builds inverted condition
	 * @param bool   sort		values mandatory must be sorted
	 *
	 * @return
	 */
	public String dbConditionInt(String key, String fname, Long[] values, boolean notIn, boolean sort) {
		return dbConditionInt(key, fname, values, notIn, sort, null);
	}
	
	public String dbConditionInt(String key, String fname, Long[] values, boolean notIn, boolean sort, SqlDecorator decorator) {
		return dbConditionLong(key, fname, values, notIn, sort, decorator);
	}
	
	public String dbConditionLong(String fname, Long[] values) {
		return dbConditionLong(null, fname, values, false, true);
	}
	
	public String dbConditionLong(String fname, Long[] values, boolean notIn) {
		return dbConditionLong(null, fname, values, notIn, true);
	}
	
	public String dbConditionLong(String key, String fname, Long[] values, boolean notIn, boolean sort) {
		return dbConditionLong(key, fname, values, notIn, sort, null);
	}
	
	public String dbConditionLong(String key, String fname, Long[] values, boolean notIn, boolean sort, SqlDecorator decorator) {
		int MAX_EXPRESSIONS = 950; // maximum  number of values for using "IN (id1>,<id2>,...,<idN>)"
		int MIN_NUM_BETWEEN = 4; // minimum number of consecutive values for using "BETWEEN <id1> AND <idN>"
		
		if (values == null || values.length == 0) {
			if (key != null && key.length() > 0) {
				put(key, "1=0");
			} else {
				put("1=0");
			}
			return "1=0";
		}

		if (sort) {
			Arrays.sort(values);
		}
		
		List<List<Long>> betweens = new LinkedList<List<Long>>();
		List<Long> data = new LinkedList<Long>();
		
		for (int i = 0, size = values.length; i < size; i++) {
			// analyze by chunk
			if ((i + MIN_NUM_BETWEEN) < size
					&& (values[i] + MIN_NUM_BETWEEN) == values[i + MIN_NUM_BETWEEN]) {
				List<Long> between = new LinkedList<Long>();
				for (int sizeMinBetween = i + MIN_NUM_BETWEEN; i < sizeMinBetween; i++) {
					between.add(values[i]);
				}
				i--; // shift 1 back
				// analyze by one
				for (; i < size; i++) {
					if ((i + 1) < size && (values[i] + 1) == values[i + 1]) {
						between.add(values[i+1]);
					} else {
						break;
					}
				}
				betweens.add(between);
			} else {
				data.add(values[i]);
			}
		}
		
		StringBuilder condition = new StringBuilder();
		String operatorAnd = notIn ? " AND " : " OR ";
		
		if (!betweens.isEmpty()) {
			String operatorNot = notIn ? "NOT " : "";
			for (List<Long> between : betweens) {
				if (condition.length() > 0) {
					condition.append(operatorAnd);
				}
				condition.append(operatorNot);
				condition.append(fname);
				condition.append(" BETWEEN ");
				condition.append(this.sqlBuilder.marshalParam(between.get(0)));
				condition.append(" AND ");
				condition.append(this.sqlBuilder.marshalParam(between.get(between.size()-1)));
			}
		}
		
		if(data.size() == 1){
			if (condition.length() > 0) {
				condition.append(operatorAnd);
			}
			String operator = notIn ? "!=" : "=";
			condition.append(fname);
			condition.append(operator);
			condition.append(this.sqlBuilder.marshalParam(data.get(0)));
		} else {
			String operatorNot = notIn ? " NOT" : "";
			for (int i = 0; i < data.size(); i+=MAX_EXPRESSIONS) {
				if (condition.length() > 0) {
					condition.append(operatorAnd);
				}
				condition.append(fname);
				condition.append(operatorNot);
				condition.append(" IN (");
				for (int n = 0; n < MAX_EXPRESSIONS && (i+n)<data.size(); n++) {
					if (n > 0) {
						condition.append(",");
					}
					condition.append(this.sqlBuilder.marshalParam(data.get(i+n)));
				}
				condition.append(")");
			}
		}
		
		String sql = "("+condition.toString()+")";
		if (decorator != null) {
			sql = decorator.decorate(sql);
		}
		if (key != null && key.length() > 0) {
			put(key, sql);
		} else {
			put(sql);
		}
		return sql;
	}
	

	public String dbConditionString(String key, String field, String[] params) {
		return dbConditionString(key, field, params, false, null);
	}
	
	public String dbConditionStringAhead(String key, String field, String[] params) {
		return dbConditionStringAhead(key, field, params, false, null);
	}
	
	public String dbConditionString(String key, String field, String[] params, SqlDecorator decorator) {
		return dbConditionString(key, field, params, false, decorator);
	}
	
	public String dbConditionStringAhead(String key, String field, String[] params, SqlDecorator decorator) {
		return dbConditionStringAhead(key, field, params, false, decorator);
	}

	public String dbConditionString(String field, String[] params) {
		return dbConditionString(null, field, params, false, null);
	}
	
	public String dbConditionStringAhead(String field, String[] params) {
		return dbConditionStringAhead(null, field, params, false, null);
	}
	
	public String dbConditionString(String field, String[] params, SqlDecorator decorator) {
		return dbConditionString(null, field, params, false, decorator);
	}
	
	public String dbConditionStringAhead(String field, String[] params, SqlDecorator decorator) {
		return dbConditionStringAhead(null, field, params, false, decorator);
	}
	
	public String dbConditionString(String key, String fname, String[] values, boolean notIn) {
		return dbConditionString(key, fname, values, notIn, null);
	}
	
	public String dbConditionStringAhead(String key, String fname, String[] values, boolean notIn) {
		return dbConditionStringAhead(key, fname, values, notIn, null);
	}
	
	public String dbConditionString(String key, String fname, String[] values, boolean notIn, SqlDecorator decorator) {
		return dbConditionString(key, fname, values, notIn, false, null);
	}
	
	public String dbConditionStringAhead(String key, String fname, String[] values, boolean notIn, SqlDecorator decorator) {
		return dbConditionString(key, fname, values, notIn, true, null);
	}
	
	public String dbConditionString(String key, String fname, String[] values, boolean notIn, boolean whereahead, SqlDecorator decorator) {
		int MAX_EXPRESSIONS = 950;
		if (values == null || values.length == 0) {
			if (key != null && key.length() > 0) {
				put(key, "1=0");
			} else {
				put("1=0");
			}
			return "1=0";
		}
		if (values.length == 1) {
			String sql;
			if (notIn) {
				sql = fname + "!=" + this.sqlBuilder.marshalParam(values[0]);
			} else {
				sql = fname + "=" + this.sqlBuilder.marshalParam(values[0]);
			}
			if (key != null && key.length() > 0) {
				if (whereahead) {
					putahead(key, sql);
				} else {
					put(key, sql);
				}
			} else {
				if (whereahead) {
					put(sql);
				} else {
					put(sql);
				}
			}
			return sql;
		}
		
		String in = notIn ? " NOT IN " : " IN ";
		String concat = notIn ? " AND " : " OR ";
		
		StringBuilder condition = new StringBuilder();
		for (int i = 0; i < values.length; i+=MAX_EXPRESSIONS) {
			if (condition.length() > 0) {
				condition.append(concat);
			}
			condition.append(fname);
			condition.append(in);
			condition.append(" (");
			for (int n = 0; n < values.length; n++) {
				if (n > 0) {
					condition.append(",");
				}
				condition.append(this.sqlBuilder.marshalParam(values[i+n]));
			}
			condition.append(")");
		}
		
		String sql = "("+condition.toString()+")";
		if(decorator!=null){
			sql = decorator.decorate(sql);
		}
		if (key != null && key.length() > 0) {
			if (whereahead) {
				putahead(key, sql);
			} else {
				put(key, sql);
			}
		} else {
			if (whereahead) {
				put(sql);
			} else {
				put(sql);
			}
		}
		return sql;
	}
	
	public String[] values() {
		return valueList.toArray(new String[0]);
	}

	public String[] arrayUnique() {
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(values()));
		String[] arrays = set.toArray(new String[0]);
		//Comm by benne
		//Collections.sort(Arrays.asList(arrays));
		return arrays;
	}
	
	public boolean containsValue(Object value){
		return namedMap.containsValue(value);
	}

	public boolean isEmpty() {
		return namedMap.isEmpty();
	}

	public int size() {
		return namedMap.size();
	}

	public void clear() {
		this.anonyIndex = 0;
		this.namedMap.clear();
		this.namedList.clear();
		this.valueList.clear();
	}

	public void clear(String key) {
		int idx = this.namedList.indexOf(key);
		this.namedList.remove(key);
		if(idx>-1 && ObjectUtils.equals(this.namedMap.remove(key), this.valueList.get(idx))){
			this.valueList.remove(idx);
		}
	}
	
	public void keepKeyClear() {
		this.valueList.clear();
	}
}
