package com.isoft.iradar.validators;

import java.util.Map;

import com.isoft.iradar.validators.string.CStringValidator;

public class CIdValidator extends CStringValidator {

	@Override
	protected void initValidator(Map options) {
		super.initValidator(options);
		// Numeric ID regex.
		this.regex = "^\\d+$";
	}

}
