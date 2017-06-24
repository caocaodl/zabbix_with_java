package com.isoft.iradar.validators.string;

import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.inc.Defines.RDA_PREG_MACRO_NAME_LLD;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;

import com.isoft.framework.common.interfaces.IIdentityBean;

public class CLldMacroStringValidator extends CStringValidator {

	/**
	 * Error message if a string doesn't contain LLD macros.
	 *
	 * @var string
	 */
	public String messageMacro;

	/**
	 * Validates the given string and checks if it contains LLD macros.
	 */
	@Override
	public boolean validate(IIdentityBean idBean, String value) {
		if (!super.validate(idBean, value)) {
			return false;
		}

		// check if a string contains an LLD macro
		if (!rda_empty(value) && preg_match("(\\{#"+RDA_PREG_MACRO_NAME_LLD+"\\})+", value)==0) {
			error(messageMacro);
			return false;
		}

		return true;
	}

}
