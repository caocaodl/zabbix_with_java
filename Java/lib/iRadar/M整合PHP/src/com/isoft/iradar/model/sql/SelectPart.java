package com.isoft.iradar.model.sql;

import java.util.ArrayList;
import java.util.List;

public class SelectPart extends SqlPart {

	public SelectPart(SqlBuilder sqlBuilder) {
		super(sqlBuilder);
	}

	@Override
	public String[] arrayUnique() {
		String[] fields = super.arrayUnique();
		String ignore = null;
		List<String> fieldList = new ArrayList<String>();
		for (String field : fields) {
			if (field.endsWith(".*")) {
				ignore = field.substring(0, field.length() - 1);
				fieldList.add(field);
				continue;
			}
			if (ignore != null && field.startsWith(ignore)) {
				continue;
			} else {
				fieldList.add(field);
			}
		}
		return fieldList.toArray(new String[0]);
	}

}
