package com.isoft.struts2.views.tags;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.AndurilContextComponent;

public abstract class AndurilContextTag extends AndurilTagSupport {
    
	private static final long serialVersionUID = 1L;

	@Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        
        AndurilContextComponent ac = (AndurilContextComponent)component;
        ac.setVar(_var);      
    }

    @Override
    public void release() {
        super.release();
        this._var = null;
    }    
    
    private String _var;
    
    public void setVar(String var) {
        this._var = var;
    }
}
