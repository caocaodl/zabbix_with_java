package com.isoft.iradar.validators.object;

import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.validators.CPartialValidatorInterface;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CUpdateDiscoveredValidator extends CValidator<Map> implements CPartialValidatorInterface {
	
	/**
	 * Fields that can be updated for discovered objects. If no fields are set, updating discovered objects
	 * will be forbidden.
	 *
	 * @var array
	 */
	public CArray allowed = array();

	/**
	 * Error message in case updating discovered objects is totally forbidden.
	 *
	 * @var string
	 */
	public String messageAllowed;

	/**
	 * Error message in case we can update only certain fields for discovered objects.
	 *
	 * @var string
	 */
	public String messageAllowedField;

	/**
	 * Checks that only the allowed fields for discovered objects are updated.
	 *
	 * The object must have the \"flags\" property defined.
	 *
	 * @param array object
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, Map object) {
		CArray allowedFields = array_flip(allowed);

		if (Nest.value(object,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED) {
			// ignore the \"flags\" field
			unset(object,"flags");

			for (Object field : object.keySet()) {
				if (!isset(allowedFields,field)) {
					// if we allow to update some fields, throw an error referencing a specific field
					// we check if there is more than 1 field, because the PK must always be present
					if (count(allowedFields) > 1) {
						error(messageAllowedField, field);
					} else {
						error(messageAllowed);
					}
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean validatePartial(IIdentityBean idBean, Map array, Map fullArray) {
		Nest.value(array,"flags").$(Nest.value(fullArray,"flags").$());
		return validate(idBean, array);
	}

}
