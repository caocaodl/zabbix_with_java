package com.isoft.iradar.validators.string;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.validators.CValidator;
import com.isoft.jdk.util.regex.IPattern;

public class CStringValidator extends CValidator<String> {

	/**
	 * If set to false, the string cannot be empty.
	 *
	 * @var bool
	 */
	public boolean empty = false;

	/**
	 * Maximum string length.
	 *
	 * @var int
	 */
	public int maxLength;

	/**
	 * Regex to match the string against.
	 *
	 * @var string
	 */
	public String regex;

	/**
	 * Error message if the string is empty.
	 *
	 * @var string
	 */
	public String messageEmpty;

	/**
	 * Error message if the string is too long.
	 *
	 * @var string
	 */
	public String messageMaxLength;

	/**
	 * Error message if the string doesn't match the regex.
	 *
	 * @var string
	 */
	public String messageRegex;

	/**
	 * Checks if the given string is:
	 * - empty
	 * - not too long
	 * - matches a certain regex
	 *
	 * @param string value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, String value) {
		if (rda_empty(value)) {
			if (this.empty) {
				return true;
			} else {
				error(this.messageEmpty);
				return false;
			}
		}

		if (this.maxLength>0 && rda_strlen(value) > this.maxLength) {
			error(this.messageMaxLength, this.maxLength);
			return false;
		}

		if (!empty(this.regex) && !rda_empty(value) && preg_match(compileRegex(this.regex), value)==0) {
			error(this.messageRegex, value);
			return false;
		}

		return true;
	}

	protected  IPattern compileRegex(String regex){
		return IPattern.compile(regex);
	}	
	
}
