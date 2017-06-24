package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.isset;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.types.CArray;

public class CSetValidator extends CValidator<String> {

	/**
	 * Allowed values.
	 *
	 * @var array
	 */
	public CArray<String> values = new CArray<String>();

	/**
	 * Error message if the value is invalid.
	 *
	 * @var string
	 */
	public String messageInvalid;

	@Override
	public boolean validate(IIdentityBean idBean, String value) {
		CArray<String> v = array_flip(this.values);

		if (!isset(v, value)) {
			this.error(this.messageInvalid, value);
			return false;
		}

		return true;
	}

}
