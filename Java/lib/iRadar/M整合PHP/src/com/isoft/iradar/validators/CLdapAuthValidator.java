package com.isoft.iradar.validators;

import static com.isoft.types.CArray.map;

import org.apache.commons.lang.NotImplementedException;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.types.CArray;

/**
 * A class for validating LDAP credentials.
 */
public class CLdapAuthValidator extends CValidator<CLdapAuthValidator.Credence> {

	public CArray conf = map(
		"host" , null,
		"port" , null,
		"base_dn" , null,
		"bind_dn" , null,
		"bind_password" , null,
		"search_attribute" , null
	);

	/**
	 * Checks if the given user name and password are valid.
	 *
	 * The value array must have the following attributes:
	 * - user       - user name
	 * - password   - password
	 *
	 * @param array value
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, CLdapAuthValidator.Credence value) {
		throw new NotImplementedException();
	}

	public static class Credence {
		private String user;
		private String password;

		public Credence(String user, String password) {
			this.user = user;
			this.password = password;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

	}
}
