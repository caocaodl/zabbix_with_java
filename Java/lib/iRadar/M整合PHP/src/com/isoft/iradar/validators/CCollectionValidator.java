package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.types.CArray;

public class CCollectionValidator extends CValidator<Object> {
	
	/**
	 * If set to false, the array cannot be empty.
	 *
	 * @var bool
	 */
	public boolean empty = false;

	/**
	 * Name of a field that must have unique values in the whole collection.
	 *
	 * @var string
	 */
	public String uniqueField;

	/**
	 * Second field to be used as a uniqueness criteria.
	 *
	 * @var string
	 */
	public String uniqueField2;

	/**
	 * Error message if the array is empty.
	 *
	 * @var string
	 */
	public String messageEmpty;

	/**
	 * Error message if the given value is not an array.
	 *
	 * @var array
	 */
	public String messageInvalid;

	/**
	 * Error message if duplicate objects exist.
	 *
	 * @var string
	 */
	public String messageDuplicate;

	/**
	 * Checks if the given array of objects is valid.
	 *
	 * @param array value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, Object value) {
		if (!isArray(value)) {
			error(messageInvalid);
			return false;
		}

		// check if it's empty
		if (!empty && empty(value)) {
			error(messageEmpty);
			return false;
		}

		// check for objects with duplicate values
		if (!empty(uniqueField)) {
			Map duplicate = null;
			if (!empty(duplicate  = CArrayHelper.findDuplicate((CArray)CArray.valueOf(value), uniqueField, uniqueField2))) {
				error(messageDuplicate, duplicate.get(uniqueField));
				return false;
			}
		}
		return true;
	}

}
