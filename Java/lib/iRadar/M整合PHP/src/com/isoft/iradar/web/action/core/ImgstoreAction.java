package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_ICON;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.imageOut;
import static com.isoft.iradar.inc.ImagesUtil.get_default_image;
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
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.g;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.iradar.web.action.RadarFakeAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class ImgstoreAction extends RadarFakeAction {
	
	@Override
	public void doWork() {
		initRequestContext();
		
		define("RDA_PAGE_NO_AUTHERIZATION", 1);
		
		page("file", "imgstore.action");
		page("type", detect_page_type(PAGE_TYPE_IMAGE));
		
		//		VAR		TYPE	OPTIONAL 	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"css",		array(T_RDA_INT, O_OPT, P_SYS, null,				null),
			"imageid",	array(T_RDA_STR, O_OPT, P_SYS, null,				null),
			"iconid",	array(T_RDA_INT, O_OPT, P_SYS, DB_ID,				null),
			"width",	array(T_RDA_INT, O_OPT, P_SYS, BETWEEN(1, 2000),	null),
			"height",	array(T_RDA_INT, O_OPT, P_SYS, BETWEEN(1, 2000),	null)
		);
		check_fields(getIdentityBean(), fields);
		
		boolean resize = false;
		int width = 0, height = 0;
		if (isset(_REQUEST,"width") || isset(_REQUEST,"height")) {
			resize = true;
			width = get_request("width", 0);
			height = get_request("height", 0);
		}
		
		if (isset(_REQUEST,"css")) {
			StringBuilder css = new StringBuilder();
			css.append("div.sysmap_iconid_0 {");
			css.append(" height: 50px;");
			css.append(" width: 50px;");
			css.append(" background-image: url(\"images/general/no_icon.png\"); }\n");
			CArray<Map> images = CDelegator.doDelegate(getIdentityBean(), new Delegator<CArray<Map>>() {
				@Override
				public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
					CImageGet options = new CImageGet();
					options.setOutput("imageid");
					options.setFilter("imagetype", Nest.as(IMAGE_TYPE_ICON).asString());
					options.setSelectImage(true);
					return API.Image(getIdentityBean(), executor).get(options);
				}
			});
			BufferedImage ico = null;
			int w,h;
			for(Map image : images) {
				ico = imageFromBytes((byte[])Nest.value(image,"image").$());
				if (resize) {
					ico = imageThumb(ico, width, height);
				}
				w = ico.getWidth();
				h = ico.getHeight();

				css.append("div.sysmap_iconid_"+Nest.value(image,"imageid").asString()+"{");
				css.append(" height: "+h+"px;");
				css.append(" width: "+w+"px;");
				css.append(" background: url(\"imgstore.action?iconid="+Nest.value(image,"imageid").asString()+"&width="+w+"&height="+h+"\") no-repeat center center;}\n");
			}
			echo(css.toString());
		} else if (isset(_REQUEST,"iconid")) {
			final int iconid = get_request("iconid", 0);
			byte[] bytes = null;
			if (iconid > 0) {
				Map image = CDelegator.doDelegate(getIdentityBean(), new Delegator<Map>() {
					@Override
					public Map doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
						return get_image_by_imageid(executor, Nest.as(iconid).asString());
					}
				});
				bytes = (byte[])Nest.value(image,"image").$();
			} else {
				bytes = get_default_image();
			}
			BufferedImage source = imageFromBytes(bytes);
			if (resize) {
				source = imageThumb(source, width, height);
			}
			imageOut(source);
		} else if (isset(_REQUEST,"imageid")) {
			int imageid = get_request("imageid", 0);
			CArray<String> image_id = g.image_id.$();
			if (isset(image_id,imageid)) {
				echo(image_id.get(imageid));
				unset(image_id,imageid);
			}
		}
	}

}
