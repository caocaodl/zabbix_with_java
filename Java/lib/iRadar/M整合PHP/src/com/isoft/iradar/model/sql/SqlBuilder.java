package com.isoft.iradar.model.sql;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SqlBuilder {
	public SqlPart select = new SelectPart(this);
	public SqlPart from = new SqlPart(this);
	public WherePart where = new WherePart(this);
	public SqlPart group = new SqlPart(this);
	public SqlPart having = new SqlPart(this);
	public SqlPart order = new OrderPart(this);
	public DualPart dual = new DualPart(this);
	public Integer limit = null;
	public Integer offset = null;
	
	private int anonyIndex = 0;

	private Map<String, Object> namedParams = new HashMap<String, Object>();
	
	public Map<String, Object> getNamedParams() {
		return namedParams;
	}

	public String marshalParam(Object v) {
		return marshalParam(null, v);
	}

	public String marshalParam(String name, Object v) {
		String pname = name;
		if (pname == null || StringUtils.trim(pname).length() == 0) {
			pname = "";
		}
		if (namedParams.containsKey(pname)) {
			while (namedParams.containsKey(pname + "_" + anonyIndex)) {
				anonyIndex++;
			}
			pname = pname + "_" + anonyIndex;
		}
		namedParams.put(pname, v);
		return "#{" + pname + "}";
	}
	
	public SqlBuilder keepKeyClear() {
		select.keepKeyClear();
		from.keepKeyClear();
		where.keepKeyClear();
		group.keepKeyClear();
		having.keepKeyClear();
		order.keepKeyClear();
		return this;
	}
	
	public String createSelectQueryFromParts() {
		String sqlSelect = StringUtils.join(this.select.arrayUnique(),",");
		String sqlFrom = StringUtils.join(this.from.arrayUnique(),",");
		
		String sqlWhere = this.where.isEmpty() ? "" : (" WHERE " + StringUtils.join(this.where.arrayUnique()," AND "));
		String sqlGroup = this.group.isEmpty() ? "" : (" GROUP BY " + StringUtils.join(this.group.arrayUnique(),","));
		String sqlHaving = this.having.isEmpty() ? "" : (" HAVING " + StringUtils.join(this.having.arrayUnique()," AND "));
		String sqlOrder = this.order.isEmpty() ? "" : (" ORDER BY " + StringUtils.join(this.order.arrayUnique(),","));
		String sqlLimit = this.limit == null ? "" : (" limit " + this.limit);
		if (sqlLimit.length() > 0) {
			if (this.offset != null) {
				sqlLimit += " OFFSET " + this.offset;
			}
		}		
		return "SELECT " + (this.from.size()>1?"DISTINCT ":"")+sqlSelect+ " FROM "+ sqlFrom + sqlWhere + sqlGroup + sqlHaving + sqlOrder + sqlLimit;
	}
	
	public String createSegmentSql(Segment segment) {
		if (Segment.where.equals(segment)) {
			return StringUtils.join(this.where.arrayUnique(), " AND ");
		} else if (Segment.group.equals(segment)) {
			return StringUtils.join(this.group.arrayUnique(), ",");
		} else if (Segment.having.equals(segment)) {
			return StringUtils.join(this.having.arrayUnique(), " AND ");
		} else if (Segment.order.equals(segment)) {
			return StringUtils.join(this.order.arrayUnique(), ",");
		} else {
			return "";
		}
	}
	
	public static enum Segment {
		where, group, having, order
	}
}
