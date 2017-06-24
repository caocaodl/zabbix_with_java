package com.isoft.struts2.views.tags.ui.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.isoft.model.SelectItem;
import com.isoft.model.SelectItemGroup;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.SelectHolder;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $SelectOneMenu extends AndurilUIComponent implements SelectHolder {

    private List<SelectItem> _selectItemList;
    
	public $SelectOneMenu(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
		this._selectItemList = new ArrayList<SelectItem>();
	}
	
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return true;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	
    	if(this.isDisplayValueOnly()){
    		renderDisplayValueOnly(writer);
    	}else{
    		renderNormal(writer);
    	}
        return false;
    }
    
    public void renderDisplayValueOnly(HtmlResponseWriter writer) throws IOException{
    	List<SelectItem> selectItemList = this.getSelectItemList();
    	String defaultValue = null;
    	Object defaultValueId = this.getValue();
    	
		int i=0;
		for (SelectItem si : selectItemList) {
			if(i==0){
				defaultValue = si.getLabel();
			}
			if(si.getValue().equals(defaultValueId)){
				defaultValue = si.getLabel();
				break;
			}
			i++;
		}
		
    	writer.write(defaultValue);
    	
    }
    protected void renderNormal(HtmlResponseWriter writer) throws IOException{
    	
    	writer.startElement(HTML.SELECT_ELEM);
    	writer.writeAttribute(HTML.ID_ATTR, this.getId());
    	writer.writeAttribute(HTML.NAME_ATTR, StringUtils.isEmpty(getName())?getId():getName());
        writer.writeAttribute(HTML.TYPE_ATTR, HTML.SELECT_ELEM);  
        
//        ValueBinding vb = getValueBinding("value");
//        Object value =  getValue();
        
//        if (vb != null) {
//            writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
//        }
        
        if (isDisabled()) {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE);
        }
        
        if(this._onchange != null && this._onchange.length()>0){
        	writer.writeAttribute(HTML.ONCHANGE_ATTR, this._onchange);
        }
        
        if(this._style != null && this._style.length()>0){
        	writer.writeAttribute(HTML.STYLE_ATTR, this._style);
        }
        
        if(this._styleClass != null && this._styleClass.length()>0){
        	writer.writeAttribute(HTML.CLASS_ATTR, this._styleClass);
        }
        
        List<SelectItem> selectItemList = this.getSelectItemList();
        renderSelectOptions(selectItemList, writer);
        writer.endElement(HTML.SELECT_ELEM);
        
    }
    protected void renderSelectOptions(List selectItemList,HtmlResponseWriter writer )throws IOException{
    	Object defaultValue = getValue();
    	defaultValue = defaultValue!=null?defaultValue:"";
    	for (Iterator it = selectItemList.iterator(); it.hasNext();) {
            SelectItem selectItem = (SelectItem) it.next();
            
            if (selectItem instanceof SelectItemGroup) {
            	
                writer.startElement(HTML.OPTGROUP_ELEM);
                
                writer.writeAttribute(HTML.LABEL_ATTR, selectItem.getLabel());
                SelectItem[] selectItems = ((SelectItemGroup) selectItem).getSelectItems();
                renderSelectOptions(Arrays.asList(selectItems),writer);
                
                writer.endElement(HTML.OPTGROUP_ELEM);
                
            } else {
                Object itemValue = selectItem.getValue();
                String itemStrValue = itemValue!=null?itemValue.toString():"";

                writer.write("\t");
                writer.startElement(HTML.OPTION_ELEM);
                if (itemStrValue != null) {
                    writer.writeAttribute(HTML.VALUE_ATTR, itemStrValue);
                }
                
                if (defaultValue.toString().equals(itemStrValue)) { 
                    writer.writeAttribute(HTML.SELECTED_ATTR,HTML.SELECTED_ATTR);
                }

                boolean disabled = selectItem.isDisabled();
                if (disabled) {
                    writer.writeAttribute(HTML.DISABLED_ATTR,HTML.DISABLED_ATTR);
                }

                writer.writeText(selectItem.getLabel());

                writer.endElement(HTML.OPTION_ELEM);
            }
    	}
    }
    
    public void pushSelectItem(SelectItem selectItem) {
        this._selectItemList.add(selectItem);
    }

    public void pushSelectItems(List<SelectItem> selectItemList) {
        this._selectItemList.addAll(selectItemList);
    }

    public List<SelectItem> getSelectItemList() {
        return this._selectItemList;
    }   
    
    private String name;
    private boolean _disabled;
    private boolean _displayValueOnly;
    private String _onchange;
    private String _style;
    private String _styleClass;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}

	public boolean isDisplayValueOnly() {
		return _displayValueOnly;
	}

	public void setDisplayValueOnly(boolean displayValueOnly) {
		_displayValueOnly = displayValueOnly;
	}

	public String getOnchange() {
		return _onchange;
	}

	public void setOnchange(String onchange) {
		this._onchange = onchange;
	}

	public String getStyle() {
		return _style;
	}

	public void setStyle(String style) {
		this._style = style;
	}

	public String getStyleClass() {
		return _styleClass;
	}

	public void setStyleClass(String styleClass) {
		this._styleClass = styleClass;
	}
}
