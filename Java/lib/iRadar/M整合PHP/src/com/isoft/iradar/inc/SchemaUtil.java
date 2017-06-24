package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.array_merge;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import com.isoft.iradar.inc.schema.SchemaPartI;
import com.isoft.iradar.inc.schema.SchemaPartII;

public class SchemaUtil {

	public final static Map<String, Map<String, Object>> SCHEMAS = (Map) unmodifiableMap(
			array_merge(
				SchemaPartI.SCHEMAS, 
				SchemaPartII.SCHEMAS
			));
	
}
