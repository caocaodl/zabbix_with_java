package com.isoft.struts2.views.tags;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.AndurilUIComponent;

public abstract class AndurilUITag extends AndurilTagSupport {

	private static final long serialVersionUID = 1L;
	private String _id = null;
    private String _converter = null;
    
    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        
        AndurilUIComponent aui = (AndurilUIComponent)component;
        if (_id != null) {
            aui.setId(_id);
        }
        if (_converter != null) {
            aui.setConverter(_converter);
        }        
    }

    @Override
    public void release() {
        super.release();
        this._id = null;
        this._converter = null;        
    }    
    
    @Override
    public void setId(String id) {
        this._id = id;
    }
    
    public String getId() {
		return _id;
	}

	public void setConverter(String converter) {
        this._converter = converter;
    }
}
