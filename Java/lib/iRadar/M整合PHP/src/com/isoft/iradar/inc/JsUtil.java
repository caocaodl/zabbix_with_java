package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlentities;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_bool;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_object;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.tags.CObject.unpack_object;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import com.isoft.iradar.core.g;
import com.isoft.iradar.tags.CColorCell;
import com.isoft.iradar.tags.CObject;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class JsUtil {
	
	private JsUtil() {
	}

	private final static Properties JavascriptSegments = new Properties();
	static {
		InputStream jss = null;
		try {
			jss = JsUtil.class.getResourceAsStream("/javascript.segments.xml");
			JavascriptSegments.loadFromXML(jss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jss != null) {
				try {
					jss.close();
					jss = null;
				} catch (Exception e) {
				}
			}
		}
	}

	public static String getJsTemplate(String key) {
		return JavascriptSegments.getProperty(key);
	}

	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @ deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 *
	 * @return string
	 */
	public static String rda_jsvalue(Object value) {
		return rda_jsvalue(value, false);
	}
	
	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @ deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 * @param bool  _asObject  return string containing javascript object
	 *
	 * @return string
	 */
	public static String rda_jsvalue(Object value, boolean asObject) {
		return rda_jsvalue(value, asObject, true);
	}
	
	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @ deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 * @param bool  _asObject  return string containing javascript object
	 * @param bool  _addQuotes whether quotes should be added at the beginning and at the end of string
	 *
	 * @return string
	 */
	public static String rda_jsvalue(Object value, boolean asObject, boolean addQuotes) {
		if (!isArray(value)) {
			if (is_null(value)) {
				return "null";
			} else if (is_bool(value)) {
				return ((Boolean) value ? "true" : "false");
			} else if (is_string(value)) {
				String escaped = str_replace("\r", "", (String) value); // removing caret returns
				escaped = str_replace("\\", "\\\\", escaped); // escaping slashes:  \ =>  \\
				escaped = str_replace("\"", "\\\"", escaped); // escaping  quotes:  " =>  \"
				escaped = str_replace("\n", "\\n", escaped); // changing  LF to  '\n'  string
				escaped = str_replace("'", "\\'", escaped); // escaping  single  quotes:  '  =>  \'
				escaped = str_replace("/", "\\/", escaped); // escaping  forward  slash: /  => \/
				if (addQuotes) {
					escaped = "'" + escaped + "'";
				}
				return escaped;
			} else if (is_object(value)) {
				return unpack_object(value).toString();
			} else {
				return strval(value);
			}
		} else if (count(value) == 0) {
			return asObject ? "{}" : "[]";
		}
	
		Boolean is_object = null;
		CArray<Object> ovalue = (CArray)CArray.valueOf(value);
		CArray<Object> cvalue = Clone.deepcopy(ovalue);
		for (Entry<Object, Object> e : ovalue.entrySet()) {
		    Object id = e.getKey();
		    Object v = e.getValue();
			if ((!isset(is_object) && is_string(id)) || asObject) {
				is_object = true;
			}
			cvalue.put(id, (isset(is_object) ? "\"" + str_replace("'", "\\'", Nest.as(id).asString()) + "\":" : "") + rda_jsvalue(v, asObject, addQuotes));
		}
	
		if (isset(is_object)) {
			return "{" + implode(",", cvalue) + "}";
		} else {
			return "[" + implode(",", cvalue) + "]";
		}
	}
	
	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @.deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 *
	 * @return string
	 */
	public static String rda_jssvalue(Object value) {
		return rda_jssvalue(value, false);
	}
	
	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @.deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 * @param bool  _asObject  return string containing javascript object
	 *
	 * @return string
	 */
	public static String rda_jssvalue(Object value, boolean asObject) {
		return rda_jssvalue(value, asObject, true);
	}
	
	/**
	 * Convert PHP variable to string version of JavaScript style
	 *
	 * @.deprecated use CJs::encodeJson() instead
	 * @see CJs::encodeJson()
	 *
	 * @param mixed _value
	 * @param bool  _asObject  return string containing javascript object
	 * @param bool  _addQuotes whether quotes should be added at the beginning and at the end of string
	 *
	 * @return string
	 */
	public static String rda_jssvalue(Object value, boolean asObject, boolean addQuotes) {
		if (!isArray(value)) {
			if (is_null(value)) {
				return "null";
			} else if (is_bool(value)) {
				return ((Boolean) value ? "true" : "false");
			} else if (is_string(value)) {
				String escaped = str_replace("\r", "", (String) value); // removing caret returns
				escaped = str_replace("\\", "\\\\", escaped); // escaping slashes:  \ =>  \\
				escaped = str_replace("\"", "\\\"", escaped); // escaping  quotes:  " =>  \"
				escaped = str_replace("\n", "\\n", escaped); // changing  LF to  '\n'  string
				escaped = str_replace("\\'", "\\\\'", escaped); // escaping  single  quotes:  '  =>  \'
				escaped = str_replace("/", "\\/", escaped); // escaping  forward  slash: /  => \/
				if (addQuotes) {
					escaped = "'" + escaped + "'";
				}
				return escaped;
			} else if (is_object(value)) {
				return unpack_object(value).toString();
			} else {
				String escaped = strval(value);
				if (addQuotes) {
					escaped = "'" + escaped + "'";
				}
				return escaped;
			}
		} else if (count(value) == 0) {
			return asObject ? "{}" : "[]";
		}
	
		Boolean is_object = null;
		CArray<Object> ovalue = (CArray)CArray.valueOf(value);
		CArray<Object> cvalue = Clone.deepcopy(ovalue);
		for (Entry<Object, Object> e : ovalue.entrySet()) {
		    Object id = e.getKey();
		    Object v = e.getValue();
			if ((!isset(is_object) && is_string(id)) || asObject) {
				is_object = true;
			}
			cvalue.put(id, (isset(is_object) ? "\"" + str_replace("'", "\\'", Nest.as(id).asString()) + "\":" : "") + rda_jssvalue(v, asObject, addQuotes));
		}
	
		if (isset(is_object)) {
			return "{" + implode(",", cvalue) + "}";
		} else {
			return "[" + implode(",", cvalue) + "]";
		}
	}

	public static Object encodeValues(Object value) {
		return encodeValues(value, true);
	}

	public static Object encodeValues(Object value, boolean encodeTwice) {
		if (is_string(value)) {
			String svalue = Nest.as(value).asString();
			svalue = htmlentities(svalue, "ENT_COMPAT", "UTF-8");
			if (encodeTwice) {
				svalue = htmlentities(svalue, "ENT_COMPAT", "UTF-8");
			}
			return svalue;
		} else if (isArray((value))) {
			CArray<Object> cvalue = (CArray)CArray.valueOf(value);
			for (Entry<Object, Object> e : cvalue.entrySet()) {
				e.setValue(encodeValues(e.getValue()));
			}
		} else if (is_object((value))) {
			if(value instanceof CObject){
				CObject cvalue = (CObject) value;
				for (int key = 0, imax = cvalue.itemsCount(); key < imax; key++) {
					cvalue.items.set(key, (String) encodeValues(cvalue.items.get(key), false));
				}
			}
		}
		return value;
	}

	public static void rda_add_post_js(String script) {
		CArray<String> rda_page_post_js = g.RDA_PAGE_POST_JS.$();
		if (!isset(rda_page_post_js)) {
			rda_page_post_js = new CArray();
			g.RDA_PAGE_POST_JS.$(rda_page_post_js);
		}
		if (!in_array(script, rda_page_post_js)) {
			rda_page_post_js.add(script);
		}
	}

	public static void insert_javascript_for_editable_combobox() {
		if (defined("EDITABLE_COMBOBOX_SCRIPT_INSERTTED")) {
			return;
		}
		define("EDITABLE_COMBOBOX_SCRIPT_INSERTTED", 1);
		
		String js = getJsTemplate("javascript_for_editable_combobox");
		insert_js(String.format(js, _("other"), _("other")));
	}

	public static void insert_show_color_picker_javascript() {
		if (g.SHOW_COLOR_PICKER_SCRIPT_INSERTED.$()) {
			return;
		}
		g.SHOW_COLOR_PICKER_SCRIPT_INSERTED.$(true);
		CTable table = new CTable();
	
		// gray colors
		CArray row = array();
		String[] cs= new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		for (String c : cs) {
			String color = c+c+c+c+c+c;
			row.add(new CColorCell(null, color, "set_color(\""+color+"\");"));
		}
		table.addRow(row);
	
		// other colors
		CArray<CArray<Integer>> colors = (CArray)array(
			map("r" , 0, "g" , 0, "b" , 1),
			map("r" , 0, "g" , 1, "b" , 0),
			map("r" , 1, "g" , 0, "b" , 0),
			map("r" , 0, "g" , 1, "b" , 1),
			map("r" , 1, "g" , 0, "b" , 1),
			map("r" , 1, "g" , 1, "b" , 0)
		);
	
		CArray<CArray<String>> brigs = (CArray)array(
			map(0 , "0", 1 , "3"),
			map(0 , "0", 1 , "4"),
			map(0 , "0", 1 , "5"),
			map(0 , "0", 1 , "6"),
			map(0 , "0", 1 , "7"),
			map(0 , "0", 1 , "8"),
			map(0 , "0", 1 , "9"),
			map(0 , "0", 1 , "A"),
			map(0 , "0", 1 , "B"),
			map(0 , "0", 1 , "C"),
			map(0 , "0", 1 , "D"),
			map(0 , "0", 1 , "E"),
			map(0 , "3", 1 , "F"),
			map(0 , "6", 1 , "F"),
			map(0 , "9", 1 , "F"),
			map(0 , "C", 1 , "F")
		);
	
		for (CArray<Integer> c : colors) {
			row = array();
			for (CArray<String> br : brigs) {
				String r = br.get(c.get("r"));
				String g = br.get(c.get("g"));
				String b = br.get(c.get("b"));
	
				String color = r+r+g+g+b+b;
				row.add(new CColorCell(null, color, "set_color(\""+color+"\");"));
			}
			table.addRow(row);
		}
	
		CSpan cancel = new CSpan(_("Cancel"), "link");
		cancel.setAttribute("onclick", "javascript: hide_color_picker();");
	
		CArray tmp = array(table, cancel);
		String js = getJsTemplate("show_color_picker_javascript");
		String script = String.format(js, rda_jsvalue(unpack_object(tmp).toString()))+"\n";
		insert_js(script);
		rda_add_post_js("create_color_picker();");
	}

	public static void insert_javascript_for_visibilitybox() {
		if (defined("CVISIBILITYBOX_JAVASCRIPT_INSERTED")) {
			return;
		}
		define("CVISIBILITYBOX_JAVASCRIPT_INSERTED", 1);
	
		String js = getJsTemplate("javascript_for_visibilitybox");
		insert_js(String.format(js, _("Cannot find objects with name"), _("Cannot create new element")));
	}

	public static void play_sound(String filename) {
		String js = getJsTemplate("javascript_for_play_sound");
		insert_js(String.format(js, filename, filename, filename));
	}
	
	public static void insert_js_function(String fnct_name) {
		String js = null;
		if (fnct_name.equals("add_item_variable")) {
			js = getJsTemplate("javascript_for_insert_function_add_item_variable");
		} else if (fnct_name.equals("add_media")) {
			js = getJsTemplate("javascript_for_insert_function_add_media");
		} else if (fnct_name.equals("add_bitem")) {
			js = getJsTemplate("javascript_for_insert_function_add_bitem");
		} else if (fnct_name.equals("update_bitem")) {
			js = getJsTemplate("javascript_for_insert_function_update_bitem");
		} else if (fnct_name.equals("add_period")) {
			js = getJsTemplate("javascript_for_insert_function_add_period");
		} else if (fnct_name.equals("update_period")) {
			js = getJsTemplate("javascript_for_insert_function_update_period");
		} else if (fnct_name.equals("addSelectedValues")) {
			js = getJsTemplate("javascript_for_insert_function_addSelectedValues");
		} else if (fnct_name.equals("addValue")) {
			js = getJsTemplate("javascript_for_insert_function_addValue");
		} else if (fnct_name.equals("addValues")) {
			js = getJsTemplate("javascript_for_insert_function_addValues");
		} else if (fnct_name.equals("check_all")) {
			js = getJsTemplate("javascript_for_insert_function_check_all");
		} else {
			js = String.format(getJsTemplate("javascript_for_insert_function_default"), fnct_name);
		}
		insert_js(js);
	}

	public static void insert_js(String script) {
		insert_js(script, false);
	}

	public static void insert_js(String script, boolean jQueryDocumentReady) {
		echo(get_js(script, jQueryDocumentReady));
	}

	public static String get_js(String script) {
		return get_js(script, false);
	}

	public static String get_js(String script, boolean jQueryDocumentReady) {
		return jQueryDocumentReady 
				?"<script type=\"text/javascript\">// <![CDATA[\njQuery(document).ready(function() { " + script + " });\n// ]]></script>"
				: "<script type=\"text/javascript\">// <![CDATA[\n" + script + "\n// ]]></script>";
	}
	
	public static void insertPagePostJs() {
		CArray<String> rda_page_post_js = g.RDA_PAGE_POST_JS.$();
		if (!empty(rda_page_post_js)) {
			StringBuilder js = new StringBuilder();
			for (String script:rda_page_post_js) {
				js.append(script).append('\n');
			}
			if (js.length()>0) {
				echo(get_js(js.toString(), true));
			}
		}
	}

}
