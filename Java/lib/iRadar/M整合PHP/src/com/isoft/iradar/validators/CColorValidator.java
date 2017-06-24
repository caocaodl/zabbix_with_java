package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp._;

import java.util.Map;

import com.isoft.iradar.validators.string.CStringValidator;
import com.isoft.jdk.util.regex.IPattern;

public class CColorValidator extends CStringValidator {
	
	@Override
	protected void initValidator(Map options) {
		super.initValidator(options);
		//Hex color code regex.
		this.regex = "^[0-9a-f]{6}$";
		if (this.messageRegex == null) {
			this.messageRegex = _("Colour \"%1$s\" is not correct: expecting hexadecimal colour code (6 symbols).");
		}
		if (this.messageEmpty == null) {
			this.messageEmpty = _("Empty color.");
		}
	}

	@Override
	protected IPattern compileRegex(String regex) {
		return IPattern.compile(regex, IPattern.CASE_INSENSITIVE);
	}	

}
