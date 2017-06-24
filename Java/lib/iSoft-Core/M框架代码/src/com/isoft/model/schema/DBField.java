package com.isoft.model.schema;

/*
 * 
TABLE_CAT->zabbix
TABLE_SCHEM->null
TABLE_NAME->hosts
COLUMN_NAME->jmx_errors_from
DATA_TYPE->4
TYPE_NAME->INT
COLUMN_SIZE->10
BUFFER_LENGTH->65535
DECIMAL_DIGITS->0
NUM_PREC_RADIX->10
NULLABLE->0
REMARKS->
COLUMN_DEF->0
SQL_DATA_TYPE->0
SQL_DATETIME_SUB->0
CHAR_OCTET_LENGTH->null
ORDINAL_POSITION->29
IS_NULLABLE->NO
SCOPE_CATALOG->null
SCOPE_SCHEMA->null
SCOPE_TABLE->null
SOURCE_DATA_TYPE->null
IS_AUTOINCREMENT->NO
 */
public class DBField {
	private String columnName;
	private int dataType;
	private String typeName;
	private int columnSize;
	private boolean nullable;
	private boolean autoIncrement;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

}
