package com.isoft.model.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTable {
	private String tableName;
	private List<DBField> fieldList = new ArrayList<DBField>();
	private Map<String, DBField> fieldMap = new HashMap<String, DBField>();
	private List<String> keys = new ArrayList<String>(2);

	public DBTable(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public List<DBField> getFieldList() {
		return fieldList;
	}

	public Map<String, DBField> getFieldMap() {
		return fieldMap;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void addField(DBField field){
		if(!fieldMap.containsKey(field.getColumnName())){
			fieldMap.put(field.getColumnName(), field);
			fieldList.add(field);
		}
	}
	
	public void addPrimaryKey(String field){
		if(!keys.contains(field)){
			keys.add(field);
		}
	}
	
	public boolean containsField(String field){
		return fieldMap.containsKey(field);
	}
	
	public DBField getField(String field){
		return fieldMap.get(field);
	}
}
