package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.JsUtil.insert_javascript_for_visibilitybox;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CVisibilityBox extends CCheckBox {
	
	private static final long serialVersionUID = 1L;

	public static CVisibilityBox instance() {
		return instance("visibilitybox");
	}
	
	public static CVisibilityBox instance(String name) {
		return instance(name, true);
	}
	
	public static CVisibilityBox instance(String name, Boolean checked) {
		return instance(name, checked, null);
	}
	
	public static CVisibilityBox instance(String name, Boolean checked, CArray<String> object_name) {
		return instance(name, checked, object_name, null);
	}

	private CArray<String> object_name = null;
	private String replace_to = null;
	
	public static CVisibilityBox instance(String name, Boolean checked, Object object_name, Object replace_to) {
		CArray objnames = null;
		if(object_name == null){
			objnames = array();
		} else if (! (object_name instanceof CArray)) {
			objnames = array(object_name);
		} else {
			objnames = (CArray)object_name;
		}
		String replace = unpack_object(replace_to).toString();
		
		String action = "";
		
		for(Object obj_name : objnames) {
			if (empty(obj_name)) {
				continue;
			}
			action += "visibility_status_changeds(this.checked, "+rda_jsvalue(obj_name)+", "+rda_jsvalue(replace)+");";
		}
		
		CVisibilityBox box = new CVisibilityBox(name, checked, action, "1");
		box.object_name = objnames;
		box.replace_to = replace;
		insert_javascript_for_visibilitybox();
		return box;
	}

	private CVisibilityBox(String name, boolean checked, String action, String value) {
		super(name, checked, action, value);
	}

	@Override
	public String toString(Boolean destroy) {
		if (!isset(Nest.value(attributes,"checked").$()) && object_name!=null) {
			for(String obj_name:object_name) {
				if (empty(obj_name)) {
					continue;
				}
				rda_add_post_js("visibility_status_changeds(false, "+rda_jsvalue(obj_name)+", "+rda_jsvalue(replace_to)+");");
			}
		}
		return super.toString(destroy);
	}

}
