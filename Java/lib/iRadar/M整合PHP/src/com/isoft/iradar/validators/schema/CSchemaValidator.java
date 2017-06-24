package com.isoft.iradar.validators.schema;

import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.key;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CSchemaValidator extends CValidator<CArray> {

	/**
	 * Array of required field names.
	 *
	 * @var array
	 */
	public CArray<String> required = array();

	/**
	 * Error message if a required field is missing.
	 *
	 * @var string
	 */
	public String messageRequired;

	/**
	 * Error message if an unsupported field is given.
	 *
	 * @var string
	 */
	public String messageUnsupported;

	/**
	 * Array of validators where keys are object field names and values are either CValidator objects or nulls.
	 *
	 * If the value is set to null, it will not be validated, but no messageUnsupported error will be triggered.
	 *
	 * @var array
	 */
	protected CArray<CValidator> validators = array();

	/**
	 * Array of validators to validate the whole object.
	 *
	 * @var array
	 */
	protected CArray<CValidator> postValidators = array();
	
	@Override
	protected void initValidator(Map options) {
		super.initValidator((new Initiator(options)).getInitOptions());
	}

	/**
	 * Checks each object field against the given validator, and then the whole object against the post validators.
	 *
	 * @param array array
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, CArray array) {
		CArray<String> requiredFields = array_flip(required);
		CArray unvalidatedFields = array_flip(array_keys(array));

		// field validators
		for (Entry<Object, CValidator> e : validators.entrySet()) {
		    Object field = e.getKey();
		    CValidator validator = e.getValue();
			unset(unvalidatedFields,field);

			// if the value is present
			if (isset(array,field)) {
				// validate it if a validator is given, skip it otherwise
				if (validator!=null && !validator.validate(idBean, array.get(field))) {
					setError(validator.getError());
					return false;
				}
			} else if (isset(requiredFields,field)) {// if no value is given, check if it's required
				error(messageRequired, field);
				return false;
			}
		}

		// check if any unsupported fields remain
		if (!empty(unvalidatedFields)) {
			Object field = key(unvalidatedFields);
			error(messageUnsupported, field);
			return false;
		}

		// post validators
		for(CValidator validator : postValidators) {
			if (!validator.validate(idBean, array)) {
				setError(validator.getError());
				return false;
			}
		}

		return true;
	}

	/**
	 * Set a validator for a field.
	 *
	 * @param field
	 * @param CValidator validator
	 */
	public void setValidator(String field, CValidator validator) {
		this.validators.put(field, validator);
	}

	/**
	 * Add a post validator.
	 *
	 * @param CValidator validator
	 */
	public void addPostValidator(CValidator validator) {
		this.postValidators.add(validator);
	}

	/**
	 * Set the object name for the current validator and all included validators.
	 *
	 * @param string name
	 */
	public void setObjectName(String name) {
		super.setObjectName(name);

		for (CValidator validator : validators) {
			if (validator != null) {
				validator.setObjectName(name);
			}
		}

		for (CValidator validator : postValidators) {
			validator.setObjectName(name);
		}
		
	}
	
	private class Initiator {
		
		private Map options;

		private Initiator(Map options) {
			this.options = options;
		}
		
		private Map getInitOptions(){
			if(this.options!=null && !this.options.isEmpty()){
				// set validators via the public setter method
				if (isset(options,"validators")) {
					CArray<CValidator> validators = Nest.value(options,"validators").asCArray();
					for (Entry<Object, CValidator> e : validators.entrySet()) {
					    Object field = e.getKey();
					    CValidator validator = e.getValue();
						setValidator((String)field, validator);
					}
				}
				unset(options,"validators");

				// set post validators via the public setter method
				if (isset(options,"postValidators")) {
					CArray<CValidator> postValidators = Nest.value(options,"postValidators").asCArray();
					for(CValidator validator : postValidators) {
						addPostValidator(validator);
					}
				}
				unset(options,"postValidators");
			}
			return this.options;
		}
	}
}
