package com.isoft.iradar.validators;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;

/**
 * An interface for validators that must support partial array validation.
 *
 * Class CPartialValidatorInterface
 */
public interface CPartialValidatorInterface {
	
	/**
	 * Validates a partial array. Some data may be missing from the given array, then it will be taken from the
	 * full array.
	 *
	 * @abstract
	 *
	 * @param array array
	 * @param array fullArray
	 *
	 * @return bool
	 */
	public boolean validatePartial(IIdentityBean idBean, Map array, Map fullArray);
		
	public String getError();
	
}
