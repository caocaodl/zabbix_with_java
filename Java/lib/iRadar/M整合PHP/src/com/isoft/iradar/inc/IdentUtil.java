package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.g;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class IdentUtil {

	private IdentUtil() {
	}
	
	public static Map getImageByIdent(IIdentityBean idBean, SQLExecutor executor, Map ident) {
		if (!isset(ident, "name")) {
			return null;
		}

		CArray<CArray<Map>> images = g.identImages.$();
		if (is_null(images)) {
			images = array();
			CImageGet ioptions = new CImageGet();
			ioptions.setOutput(new String[]{"imageid", "name"});
			CArray<Map> dbImages = API.Image(idBean, executor).get(ioptions);
			for(Map image : dbImages) {
				if (!isset(images,image.get("name"))) {
					images.put(image.get("name"), array());
				}
				Nest.value(images,image.get("name")).asCArray().add(image);
			}
			g.identImages.$(images);
		}

		Nest.value(ident,"name").$(trim(Nest.value(ident,"name").asString(), ' '));
		if (!isset(images, ident.get("name"))) {
			return null;
		}
		
		CArray<Map> searchedImages = images.get(ident.get("name"));
		return reset(searchedImages);
	}
}
