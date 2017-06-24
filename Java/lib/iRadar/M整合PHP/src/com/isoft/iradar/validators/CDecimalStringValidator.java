package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp.preg_match;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.jdk.util.regex.IPattern;

public class CDecimalStringValidator extends CValidator<String> {

	/**
	 * Error message for type and decimal format validation
	 *
	 * @var string
	 */
	public String messageInvalid;

	/**
	 * Checks if the given string is correct double.
	 *
	 * @param string value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, String value) {
		boolean isValid = false;

		//if (is_scalar(value)) {//comments by benne
		isValid = (isValidCommonNotation(value)
			|| isValidDotNotation(value)
			|| isValidScientificNotation(value));
		//}

		if (!isValid) {
			error(messageInvalid, value);
		}

		return isValid;
	}
	
	/**
	 * Validates usual decimal syntax - \"1.0\", \"0.11\", \"0\".
	 *
	 * @param string value
	 *
	 * @return boolean
	 */
	protected boolean isValidCommonNotation(String value){
		return preg_match("^-?\\d+(\\.\\d+)?$", value)>0;
	}

	/**
	 * Validates \"dot notation\" - \".11\", \"22.\"
	 *
	 * @param string value
	 *
	 * @return boolean
	 */
	protected boolean isValidDotNotation(String value) {
		return preg_match("^-?(\\.\\d+|\\d+\\.)$", value)>0;
	}
	
	/**
	 * Validate decimal string in scientific notation - \"10e3\", \"1.0e-5\".
	 *
	 * @param string value
	 *
	 * @return boolean
	 */
	protected boolean isValidScientificNotation(String value) {
		IPattern regex = IPattern.compile("^-?[0-9]+(\\.[0-9]+)?e[+|-]?[0-9]+$", IPattern.CASE_INSENSITIVE);
		return preg_match(regex, value)>0;
	}
}
