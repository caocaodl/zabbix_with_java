package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_ICON;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

//TODO 尚未实现完
public class AdmImagesAction extends RadarBaseAction {
	
    private File image; //上传的文件
    private String imageFileName; //文件名称
    private String imageContentType; //文件类型
    
    private Map dbImage;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of images"));
		page("file", "adm.images.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				"imageid",    array(T_RDA_INT, O_NO,	P_SYS,		DB_ID,		"isset({form})&&{form}==\"update\""),
				"name",		array(T_RDA_STR, O_NO,	null,		NOT_EMPTY,	"isset({save})"),
				"imagetype",	array(T_RDA_INT, O_OPT, null,		IN("1,2"),	"isset({save})"),
				"save",	        array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
				"delete",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
				"form",		    array(T_RDA_STR, O_OPT, P_SYS,		null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (isset(_REQUEST,"imageid")) {
			Map params = new HashMap();
			params.put("imageid", get_request("imageid"));
			this.dbImage = DBfetch(DBselect(executor, "SELECT i.imagetype,i.name FROM images i WHERE i.imageid=#{imageid}", params));
			if (empty(this.dbImage)) {
				FuncsUtil.access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
//			if (isset(_REQUEST,"imageid")) {
//				_msgOk = _("Image updated");
//				_msgFail = _("Cannot update image");
//			} else {
//				_msgOk = _("Image added");
//				_msgFail = _("Cannot add image");
//			}
//
//			try {
//			    DBstart(executor);
//			
//				if (isset(this.image)) {
//					_file = new CUploadFile(Nest.value($_FILES,"image").$());
//
//					_image = null;
//					if (_file.wasUploaded()) {
//						_file.validateImageSize();
//						_image = base64_encode(_file.getContent());
//					}
//				}
//
//				if (isset(Nest.value(_REQUEST,"imageid").$())) {
//					_result = API.Image().update(array(
//						"imageid" => Nest.value(_REQUEST,"imageid").$(),
//						"name" => Nest.value(_REQUEST,"name").$(),
//						"imagetype" => Nest.value(_REQUEST,"imagetype").$(),
//						"image" => _image
//					));
//
//					_audit_action = "Image ["._REQUEST["name"]."] updated";
//				}
//				else {
//					_result = API.Image().create(array(
//						"name" => Nest.value(_REQUEST,"name").$(),
//						"imagetype" => Nest.value(_REQUEST,"imagetype").$(),
//						"image" => _image
//					));
//
//					_audit_action = "Image ["._REQUEST["name"]."] added";
//				}
//
//				if (_result) {
//					add_audit(AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IMAGE, _audit_action);
//					unset(Nest.value(_REQUEST,"form").$());
//				}
//
//			    DBend(executor, result);
//				show_messages(_result, _msgOk, _msgFail);
//			}
//			catch (Exception $e) {
//			    DBend(executor, false);
//				error($e.getMessage());
//			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"imageid")) {
//			_image = get_image_by_imageid(Nest.value(_REQUEST,"imageid").$());
//			_result = API.Image().delete(Nest.value(_REQUEST,"imageid").$());
//
//			if (_result) {
//				add_audit(AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IMAGE, "Image ["._image["name"]."] deleted");
//				unset(Nest.value(_REQUEST,"form").$(), _image, Nest.value(_REQUEST,"imageid").$());
//			}
//
//			show_messages(_result, _("Image deleted"), _("Cannot delete image"));
		}

		/* Display */
		CForm _form = new CForm();
		_form.cleanItems();
		CComboBox _generalComboBox = new CComboBox("configDropDown", "adm.images.action", "redirect(this.options[this.selectedIndex].value);");
		_generalComboBox.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
			"adm.images.action", _("Images"),
			"adm.iconmapping.action", _("Icon mapping"),
			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"adm.other.action", _("Other")
		));
		_form.addItem(_generalComboBox);

		if (!isset(_REQUEST,"form")) {
			_form.addItem(new CSubmit("form", _("Create image")));
		}

		CWidget _imageWidget = new CWidget();
		_imageWidget.addPageHeader(_("CONFIGURATION OF IMAGES"), _form);

		CArray _data = map(
			"form", get_request("form"),
			"widget", _imageWidget
		);

		CView _imageForm = null;
		if (!empty(Nest.value(_data,"form").$())) {
			if (isset(_REQUEST,"imageid")) {
				Nest.value(_data,"imageid").$(Nest.value(_REQUEST,"imageid").$());
				Nest.value(_data,"imagename").$(Nest.value(this.dbImage,"name").$());
				Nest.value(_data,"imagetype").$(Nest.value(this.dbImage,"imagetype").$());
			} else {
				Nest.value(_data,"imageid").$(null);
				Nest.value(_data,"imagename").$(get_request("name", ""));
				Nest.value(_data,"imagetype").$(get_request("imagetype", 1));
			}
			_imageForm = new CView("administration.general.image.edit", _data);
		} else {
			Nest.value(_data,"imagetype").$(get_request("imagetype", IMAGE_TYPE_ICON));
			CImageGet ioptions = new CImageGet();
			ioptions.setFilter("imagetype", Nest.value(_data,"imagetype").asString());
			ioptions.setOutput(new String[]{"imageid", "imagetype", "name"});
			CArray<Map> images = API.Image(getIdentityBean(), executor).get(ioptions);
			Nest.value(_data,"images").$(images);
			order_result(images, "name");

			_imageForm = new CView("administration.general.image.list", _data);
		}

		_imageWidget.addItem(_imageForm.render(getIdentityBean(), executor));
		_imageWidget.show();
	}
}
