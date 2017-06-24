package com.isoft.iradar.model;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ord;
import static com.isoft.iradar.inc.ItemsUtil.isKeyIdChar;
import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;

/**
 * Class is used to validate and parse item keys.
 *
 * Example of usage:
 *		_itemKey = new CItemKey("test.key[a, b, c]");
 *		echo _itemKey.isValid(); // true
 *		echo _itemKey.getKeyId(); // test.key
 *		print_r(_itemKey.parameters()); // array("a", "b", "c")
 */
public class CItemKey {
	private final static int STATE_NEW = 0;
	private final static int STATE_END = 1;
	private final static int STATE_UNQUOTED = 2;
	private final static int STATE_QUOTED = 3;

	private String keyId = ""; // main part of the key (for "key[1, 2, 3]" key id would be "key")
	private CArray<String> parameters = array();
	private boolean isValid = true;
	private String error = "";

	/**
	 * Parse key and determine if it is valid.
	 *
	 * @param string key
	 */
	public CItemKey(String key) {
		parseKey(key);
	}

	/**
	 * Returns an error message depending on input parameters.
	 *
	 * @param string _key
	 * @param int _pos
	 *
	 * @return string
	 */
	private String errorMessage(char[] key, int pos) {
		if (pos >= key.length) {
			return (pos == 0) ? _("key is empty") : _("unexpected end of key");
		}
		
		StringBuilder chunk = new StringBuilder();
		int i = pos, maxChunkSize = 50;
		for (; i<key.length; i++) {
			if (0x80 != (0xc0 & ord(key[i])) && maxChunkSize-- == 0) {
				break;
			}
			chunk.append(key[i]);
		}

		if (i<key.length) {
			chunk.append(" ...");
		}

		return _s("incorrect syntax near \"%1$s\"", chunk.toString());
	}

	/**
	 * Parse key and parameters and put them into parameters array.
	 *
	 * @param string _key
	 */
	private void parseKey(String skey) {
		char[] key = skey.toCharArray();
		int pos = 0;

		// checking every byte, one by one, until first "not key_id" char is reached
		while (key.length>pos) {
			if (!isKeyIdChar(key[pos])) {
				break; // _pos now points to the first "not a key name" char
			}
			keyId += key[pos++];
		}

		// checking if key is empty
		if (pos == 0) {
			isValid = false;
			error = errorMessage(key, pos);
			return;
		}

		// invalid symbol instead of "[", which would be the beginning of params
		if (key.length>pos && key[pos] != '[') {
			isValid = false;
			error = errorMessage(key, pos);
			return;
		}

		int state = STATE_END;
		int level = 0;
		int num = 0;
		int l = 0;
		
		label_3:while(pos < key.length) {
			if (level == 0) {
				// first square bracket + Zapcat compatibility
				if (state == STATE_END && key[pos] == '[') {
					state = STATE_NEW;
				} else {
					break;
				}
			}

			switch (state) {
				// a new parameter started
				case STATE_NEW:
					switch (key[pos]) {
						case ' ':
							break;

						case ',':
							if (level == 1) {
								if (!isset(parameters.get(num))) {
									parameters.put(num, "");
								}
								num++;
							}
							break;

						case '[':
							level++;
							if (level == 2) {
								l = pos;
							}
							break;

						case ']':
							if (level == 1) {
								if (!isset(parameters.get(num))) {
									parameters.put(num, "");
								}
								num++;
							}
							else if (level == 2) {
								parameters.put(num, "");
								for (l++; l < pos; l++) {
									parameters.put(num, parameters.get(num)+key[l]);
								}
							}
							level--;
							state = STATE_END;
							break;

						case '\"':
							state = STATE_QUOTED;
							if (level == 1) {
								l = pos;
							}
							break;

						default:
							state = STATE_UNQUOTED;
							if (level == 1) {
								l = pos;
							}
					}
					break;

				// end of parameter
				case STATE_END:
					switch (key[pos]) {
						case ' ':
							break;

						case ',':
							state = STATE_NEW;
							if (level == 1) {
								if (!isset(parameters.get(num))) {
									parameters.put(num, "");
								}
								num++;
							}
							break;

						case ']':
							if (level == 1) {
								if (!isset(parameters.get(num))) {
									parameters.put(num, "");
								}
								num++;
							}
							else if (level == 2) {
								parameters.put(num, "");
								for (l++; l < pos; l++) {
									parameters.put(num, parameters.get(num)+key[l]);
								}
							}
							level--;
							break;

						default:
							break label_3;
					}
					break;

				// an unquoted parameter
				case STATE_UNQUOTED:
					if (key[pos] == ']' || key[pos] == ',') {
						if (level == 1) {
							parameters.put(num, "");
							for (; l < pos; l++) {
								parameters.put(num, parameters.get(num)+key[l]);
							}
						}
						pos--;
						state = STATE_END;
					}
					break;

				// a quoted parameter
				case STATE_QUOTED:
					if (key[pos] == '\"' && key[pos - 1] != '\\') {
						if (level == 1) {
							parameters.put(num, "");
							for (l++; l < pos; l++) {
								if (key[l] != '\\' || key[l + 1] != '\"') {
									parameters.put(num, parameters.get(num)+key[l]);
								}
							}
						}
						state = STATE_END;
					}
					break;
			}

			pos++;
		}

		if (pos == 0 || key.length>pos || level != 0) {
			isValid = false;
			error = errorMessage(key, pos);
		}
	}

	/**
	 * Returns the result of validation.
	 *
	 * @return bool
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Returns the error message if key is invalid.
	 *
	 * @return string
	 */
	public String getError() {
		return error;
	}

	/**
	 * Returns the left part of key without parameters.
	 *
	 * @return string
	 */
	public String getKeyId() {
		return keyId;
	}

	/**
	 * Returns the list of key parameters.
	 *
	 * @return array
	 */
	public CArray<String> getParameters() {
		return parameters;
	}
}

