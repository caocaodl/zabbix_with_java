package com.isoft.iradar.validators.schema;

import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.key;
import static com.isoft.iradar.Cphp.unset;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.validators.CPartialValidatorInterface;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;

public class CPartialSchemaValidator extends CSchemaValidator implements CPartialValidatorInterface {

	@Override
	public boolean validatePartial(IIdentityBean idBean, Map array, Map fullArray) {
		CArray unvalidatedFields = array_flip(array_keys(CArray.valueOf(array)));

		// field validators
		for (Entry<Object, CValidator> e : validators.entrySet()) {
			Object field = e.getKey();
			CValidator validator = e.getValue();
			unset(unvalidatedFields, field);

			// if the value is present
			if (isset(array, field)) {
				// validate it if a validator is given, skip it otherwise
				if (validator != null && !validator.validate(idBean, array.get(field))) {
					setError(validator.getError());
					return false;
				}
			}
		}

		// check if any unsupported fields remain
		if (!empty(unvalidatedFields)) {
			Object field = key(unvalidatedFields);
			error(messageUnsupported, field);
			return false;
		}

		// post validators
		for (CValidator validator : postValidators) {
			if (!((CPartialValidatorInterface) validator).validatePartial(idBean, array, fullArray)) {
				setError(validator.getError());
				return false;
			}
		}

		return true;
	}

	@Override
	public void addPostValidator(CValidator validator) {
		// the post validators for the partial schema validator must implement
		// the \"CPartialValidatorInterface\" interface.
		if (!(validator instanceof CPartialValidatorInterface)) {
			throw new ClassCastException(
					"Partial schema validator post validator must implement the \"CPartialValidatorInterface\" interface.");
		}
		super.addPostValidator(validator);
	}
}
