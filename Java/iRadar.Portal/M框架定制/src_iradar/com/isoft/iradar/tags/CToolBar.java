package com.isoft.iradar.tags;

import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;

import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.types.CArray;

/**
 * 此类专为页面上方按钮条
 * 
 * @author BluE
 *
 */
public class CToolBar extends CTag {
	private static final long serialVersionUID = -4328098521066785408L;
	
	public CToolBar() {
		super("div", null, null, "toolbar");
	}
	
	public CToolBar(CForm form) {
		this();
		String js = "chkbxRange.submit = function(elm) {"
				+ "var confirmText=elm.getAttribute('confirm');"
				+ "function _submit(){ "
				+ "	var form = document.forms."+ form.getName() +";"
				+ "	for (var key in this.selectedIds) {"
				+ "		if (!empty(this.selectedIds[key])) {"
				+ "			create_var(form.name, this.pageGoName + '[' + key + ']', key, false);"
				+ "		}"
				+ "	}"
				+ "	var objVar = document.createElement('input');"
				+ "	objVar.setAttribute('type', 'hidden');"
				+ "	objVar.setAttribute('name', 'go');"
				+ "	objVar.setAttribute('value', elm.name);"
				+ "	form.appendChild(objVar);"
				+ "	form.submit();"
				+ "}"
				+ "if(confirmText&&showModalWindow&& typeof showModalWindow=='function'){"
				+ " var that=this;"
				+ "showModalWindow(document.title,confirmText,[{"
				+ "text:'确定',click:function(){  _submit.call(that)}"
				+ "},{text:'取消',click:function(){jQuery(this).dialog('destroy')}}])"
				+ "}else{_submit()}"
				+ "};"
				+ "var oldF = chkbxRange.setGo;"
				+ "chkbxRange.goBts = [];"
				+ "chkbxRange.setGo=function(){"
				+ "	oldF.apply(this, arguments);"
				+ "	var isDisable = this.pageGoCount==0;"
				+ "	jQuery.each(chkbxRange.goBts, function(){"
				+ "		var btn = jQuery('#go_'+this);"
				+ "		btn[isDisable?'addClass':'removeClass']('disabled');"
				+ "		if(isDisable){btn.attr('disabled', true);}"
				+ "		else{btn.removeAttr('disabled');}" + "	});" + "}";
		rda_add_post_js(js);
	}
	
	/**
	 * 按钮样式
	 * @param name
	 * @param value
	 * @param action
	 * @param styleClass 样式
	 * @param status 新加的只读标志。如果按钮需要只读，会传过readonly内容的参数；反之不需要的话，传null。
	 */
	public void addSubmit(String name, String value, String action, String styleClass, String status) {
		CForm form = new CForm("get");
		form.setAttribute("style", "display: inline;");
		form.cleanItems();
		CSubmit allButton = new CSubmit(name, value, action, styleClass);
		if("readonly".equals(status)){
			allButton.setEnabled(false);
		}
		form.addItem(allButton);
		this.addItem(form);
	}
	public void addSubmit(String name, String value, String action, String styleClass) {
		addSubmit(name, value, action, styleClass, null);
	}
	
	public void addForm(CForm form) {
		form.setAttribute("style", "display: inline;");
		this.addItem(form);
	}
	
	public void addComboBox(CArray<CComboItem> items){
		for(CComboItem item: items) {
			String styleClass = EasyObject.asString(item.getAttribute("class"));
			Object confirm = item.getAttribute("confirm");
			String name = EasyObject.asString(item.getValue());
			String value = item.items.get(0);
			
			CButton btn = new CButton(name, value, null, styleClass);
			btn.setAttribute("id", "go_"+name);
			btn.setAttribute("confirm", confirm);
			btn.setAttribute("onClick", "chkbxRange.submit(this);");
			this.addItem(btn);
			rda_add_post_js("chkbxRange.goBts.push('"+name+"')");
		}
	}
	
	public void addMore(CArray items) {
		CArray ctnItems = new CArray();
		ctnItems.add(new CDiv("更多", "more_btn"));
		
		CArray links = new CArray();
		for(Object tag: items) {
			CLink link = null;
			if(tag instanceof CComboItem) {
				CComboItem item = (CComboItem)tag;
				String styleClass = EasyObject.asString(item.getAttribute("class"));
				Object confirm = item.getAttribute("confirm");
				String name = EasyObject.asString(item.getValue());
				String value = item.items.get(0);

				link = new CLink(value, "javascript:void(0)");
				link.setAttribute("name", name);
				link.setAttribute("class", styleClass);
				link.setAttribute("id", "go_"+name);
				link.setAttribute("confirm", confirm);
				link.setAttribute("onClick", "chkbxRange.submit(this);");
				rda_add_post_js("chkbxRange.goBts.push('"+name+"')");
			}
			links.add(link);
		}
		ctnItems.add(new CDiv(links, "more_show_ctn"));
		
		this.addItem(new CDiv(ctnItems, "more_ctn"));
		rda_add_post_js("jQuery('.more_btn').hover(function(){$(this).children('.more_show_ctn').slideDown()}, function(){$(this).children('.more_show_ctn').slideUp()});");
	}
	
}
