package com.isoft.iradar.validators;

import java.lang.reflect.Field;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.Cphp;
import com.isoft.types.Mapper.Nest;

public abstract class CValidator<T> {

	/**
	 * Name of the object that can be used in error message. If it is set, it will replace the %1$s placeholder and
	 * all other places holders will be shifted by +1.
	 *
	 * @var string
	 */
	protected String objectName;

	/**
	 * Validation errors.
	 *
	 * @var array
	 */
	private String error;
	
	public static <T extends CValidator> T init(T validator, Map options) {
		validator.initValidator(options);
		return validator;
	}

	protected void initValidator(Map options) {
		// set options
		for (Object key: options.keySet()) {
			Object value = options.get(key);
			try {
				Field method = this.getClass().getField(Nest.as(key).asString());
				if (method != null) {
					method.setAccessible(true);
					method.set(this, value);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns true if the given value is valid, or set's an error and returns false otherwise.
	 *
	 * @abstract
	 *
	 * @param value
	 *
	 * @return bool
	 */
	public abstract boolean validate(IIdentityBean idBean, T value);

	/**
	 * Get first validation error.
	 *
	 * @return string
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Add validation error.
	 *
	 * @param error
	 */
	protected void setError(String error) {
		this.error = error;
	}

	/**
	 * @param string name
	 */
	public void setObjectName(String name) {
		this.objectName = name;
	}

	/**
	 * Throws an exception when trying to set an unexisting validator option.
	 *
	 * @param name
	 * @param value
	 *
	 * @throws Exception
	 */
//	public function __set(name, value) {
//		throw new Exception(sprintf('Incorrect option "%1$s" for validator "%2$s".', name, get_class(_this)));
//	}

	/**
	 * Adds a validation error with custom parameter support. The value of objectName will be passed as the
	 * first parameter.
	 *
	 * @param string 	message
	 * @param mixed 	param 		parameter to be replace the first placeholder
	 * @param mixed 	param,... 	unlimited number of optional parameters
	 *
	 * @return string
	 */
	protected void error(String message, Object... arguments) {
		if(this.objectName != null) {
			arguments = Cphp.array_unshift(arguments, this.objectName);
		}
		this.setError(Cphp.vsprintf(message, arguments));
	}
}
