package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.types.CArray.array;

public class CRadioButtonList extends CDiv {
	
	private static final long serialVersionUID = 1L;

	private final static String ORIENTATION_HORIZONTAL = "horizontal";
	private final static String ORIENTATION_VERTICAL = "vertical";
	
	protected int count;
	protected String name;
	protected String value;
	protected String orientation;

	public CRadioButtonList() {
		this("radio");
	}

	public CRadioButtonList(String name) {
		this(name, "yes");
	}

	public CRadioButtonList(String name, String value) {
		super(null, null, name);
		this.count = 0;
		this.name = name;
		this.value = value;
		this.orientation = ORIENTATION_HORIZONTAL;
	}
	
	public void addValue(String name, String value) {
		this.addValue(name, value, null);
	}
	
	public void addValue(String name, String value, Boolean checked) {
		this.addValue(name, value, checked, null);
	}
	
	public void addValue(String name, String value, Boolean checked, String id) {
		this.count++;

		if (is_null(id)) {
			id = rda_formatDomId(this.name)+'_'+this.count;
		}

		CInput radio = new CInput("radio", this.name, value, null, id);
		if (strcmp(value, this.value) == 0 || (!is_null(checked) && checked)) {
			radio.attr("checked", "checked");
		}

		CLabel label = new CLabel(name, id);

		CDiv outerDiv = new CDiv(array(radio, label));
		if (  ORIENTATION_HORIZONTAL.equals(this.orientation)) {
			outerDiv.addClass("inlineblock");
		}

		addItem(outerDiv);
	}
	
	public void makeHorizaontal(){
		this.orientation = ORIENTATION_HORIZONTAL;
	}
	
	public void makeVertical(){
		this.orientation = ORIENTATION_VERTICAL;
	}

}
