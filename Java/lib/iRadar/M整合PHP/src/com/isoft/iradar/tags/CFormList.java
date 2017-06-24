package com.isoft.iradar.tags;

import static com.isoft.types.CArray.array;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.types.CArray;

public class CFormList extends CDiv {
	
	private static final long serialVersionUID = 1L;

	protected CList formList = null;
	protected boolean editable = true;
	protected CArray<String> formInputs = array("ctextbox", "cnumericbox", "ctextarea", "ccombobox", "ccheckbox", "cpassbox", "cipbox");

	public CFormList() {
		this(null);
	}
	public CFormList(String _id) {
		this(_id, null);
	}
	public CFormList(String _id, String _class) {
		this(_id, _class, true);
	}
	public CFormList(String _id, String _class, boolean _editable) {
		super();
		this.editable = _editable;
		this.formList = new CList(null, "formlist");

		if (!Cphp.empty(_id)) {
			this.attr("id", FuncsUtil.rda_formatDomId(_id));
		}

		this.attr("class", _class);
	}
	
	public void addRow(Object _term) {
		addRow(_term, null);
	}
	public void addRow(Object _term, Object _description) {
		addRow(_term, _description, false);
	}
	public void addRow(Object _term, Object _description, boolean _hidden) {
		addRow(_term, _description, _hidden, null);
	}
	public void addRow(Object _term, Object _description, boolean _hidden, String _id) {
		addRow(_term, _description, _hidden, _id, null);
	}
	public void addRow(Object _term, Object _description, boolean _hidden, String _id, String _class) {
		Object _label = _term;

		if (Cphp.is_object(_description)) {
			String _inputClass = FuncsUtil.rda_strtolower(Cphp.get_class(_description));

			if (Cphp.in_array(_inputClass, this.formInputs)) {
				_label = new CLabel(_term, (String)((CTag)_description).getAttribute("id"));
			}
		}

		String _defaultClass = _hidden ? "formrow hidden" : "formrow";

		if (_class == null) {
			_class = _defaultClass;
		}
		else {
			_class += " "+_defaultClass;
		}

		if (_description == null) {
			this.formList.addItem(array(new CDiv(Defines.SPACE, "dt right"), new CDiv(_label, "dd")), _class, _id);
		}
		else {
			this.formList.addItem(array(new CDiv(_label, "dt right"), new CDiv(_description, "dd")), _class, _id);
		}
	}

	public void addInfo(String _text) {
		addInfo(_text, null);
	}
	public void addInfo(String _text, String _label) {
		this.formList.addItem(
			array(
				new CDiv(!Cphp.empty(_label) ? _label: Cphp._("Info"), "dt right listInfoLabel"),
				new CDiv(_text, "objectgroup inlineblock border_dotted ui-corner-all listInfoText")
			),
			"formrow listInfo"
		);
	}

	
	public String toString() {
		return toString(true);
	}
	public String toString(boolean _destroy) {
		this.addItem(this.formList);
		return super.toString(_destroy);
	}

	public CObject addVar(String _name, String _value) {
		return addVar(_name, _value, null);
	}
	public CObject addVar(String _name, String _value, String _id) {
		if (_value != null) {
			return this.addItem(new CVar(_name, _value, _id));
		}
		return null;
	}
}
