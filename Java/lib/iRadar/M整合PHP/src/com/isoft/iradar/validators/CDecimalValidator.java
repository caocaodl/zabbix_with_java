package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.strlen;

import com.isoft.framework.common.interfaces.IIdentityBean;

public class CDecimalValidator extends CValidator<String> {

	/**
	 * Max precision (optional).
	 *
	 * @var int
	 */
	public Integer maxPrecision;

	/**
	 * Max scale (optional).
	 *
	 * @var int
	 */
	public Integer maxScale;

	/**
	 * Error message for format validation.
	 *
	 * @var string
	 */
	public String messageFormat;

	/**
	 * Error message for precision validation (optional).
	 *
	 * @var string
	 */
	public String messagePrecision;

	/**
	 * Error message for natural validation (optional).
	 *
	 * @var string
	 */
	public String messageNatural;

	/**
	 * Error message for scale validation (optional).
	 *
	 * @var string
	 */
	public String messageScale;

	/**
	 * Checks if the given string is correct double.
	 *
	 * @param string value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, String value) {
		// validate format
		if (preg_match("^-?(?:\\d+|\\d*\\.\\d+)$", value) == 0) {
			error(messageFormat, value);
			return false;
		}

		String[] parts = explode("\\.", value);

		String natural = parts[0];
		int naturalSize = strlen(natural);

		String scale;
		int scaleSize;
		if (parts.length > 1) {
			scale = parts[1];
			scaleSize = strlen(scale);
		} else {
			scale = null;
			scaleSize = 0;
		}

		// validate scale without natural
		if (scaleSize > 0 && naturalSize == 0) {
			error(messageFormat, value);
			return false;
		}

		if (maxPrecision != null) {
			int maxNaturals = maxPrecision - maxScale;

			// validate precision
			if (naturalSize + scaleSize > maxPrecision) {
				error(messagePrecision, value, maxNaturals, maxScale);
				return false;
			}

			// validate digits before point
			if (maxScale != null) {
				if (naturalSize > maxNaturals) {
					error(messageNatural, value, maxNaturals);
					return false;
				}
			}
		}

		// validate scale
		if (maxScale != null && scaleSize > maxScale) {
			error(messageScale, value, maxScale);
			return false;
		}

		return true;
	}

}
