package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.imageOut;
import static com.isoft.iradar.inc.ImagesUtil.get_image_by_imageid;
import static com.isoft.iradar.inc.ImagesUtil.imageFromBytes;
import static com.isoft.iradar.inc.ImagesUtil.imageThumb;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.image.BufferedImage;
import java.util.Map;

import com.isoft.biz.Delegator;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.web.action.RadarFakeAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class ImageAction extends RadarFakeAction {
	
	@Override
	public void doWork() {
		initRequestContext();
		
		page("file", "image.action");
		page("title", _("Image"));
		page("type", detect_page_type(PAGE_TYPE_IMAGE));
		
		//		VAR		TYPE	OPTIONAL 	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				"imageid",	array(T_RDA_INT, O_MAND, P_SYS, DB_ID,				null),
				"width",	array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(1, 2000),	null),
				"height",	array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(1, 2000),	null)
		);
		check_fields(getIdentityBean(), fields);
		
		boolean resize = false;
		int width = 0, height = 0;
		if (isset(_REQUEST,"width") || isset(_REQUEST,"height")) {
			resize = true;
			width = get_request("width", 0);
			height = get_request("height", 0);
		}
		
		final int imageid = get_request("imageid", 0);
		Map row = CDelegator.doDelegate(getIdentityBean(), new Delegator<Map>() {
			@Override
			public Map doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				return get_image_by_imageid(executor, Nest.as(imageid).asString());
			}
		});
		
		if(empty(row)){
			error(_("Incorrect image index."));
			return;
		}
		
		byte[] bytes = (byte[])Nest.value(row,"image").$();
		BufferedImage source = imageFromBytes(bytes);
		if (resize) {
			source = imageThumb(source, width, height);
		}
		imageOut(source);
	}

}
