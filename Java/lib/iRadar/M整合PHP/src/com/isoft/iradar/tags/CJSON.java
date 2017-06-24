package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.chr;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.preg_replace;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.isoft.iradar.utils.CJs;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CJSON {
	
	/**
	 *
	 * User-defined configuration, primarily of use in unit testing.
	 *
	 * Keys are ...
	 *
	 * `bypass_ext`
	 * : (bool) Flag to instruct Solar_Json to bypass
	 *   native json extension, ifinstalled.
	 *
	 * `bypass_mb`
	 * : (bool) Flag to instruct Solar_Json to bypass
	 *   native mb_convert_encoding() function, if
	 *   installed.
	 *
	 * `noerror`
	 * : (bool) Flag to instruct Solar_Json to return null
	 *   for values it cannot encode rather than throwing
	 *   an exceptions (PHP-only encoding) or PHP warnings
	 *   (native json_encode() function).
	 *
	 * @var array
	 *
	 */
	protected CArray<Boolean> _config = map(
		"bypass_ext", false,
		"bypass_mb", false,
		"noerror", false
	);
	
	/**
	 *
	 * Marker constants for use in _json_decode()
	 *
	 * @constant
	 *
	 */
	private static int SLICE  = 1;
	private static int IN_STR = 2;
	private static int IN_ARR = 3;
	private static int IN_OBJ = 4;
	private static int IN_CMT = 5;
	
	/**
	 *
	 * Nest level counter for determining correct behavior of decoding string
	 * representations of numbers and boolean values.
	 *
	 * @var int
	 */
	protected int _level;
	
	// used for fallback _json_encode
	private boolean _forceObject = false;

	public String encode(Object obj) {
		return CJs.encodeJson(obj);
		//return this._json_encode(obj);
	}
	
	/**
	 *
	 * Method for use with preg_replace_callback in the _deQuote() method.
	 *
	 * Returns \["keymatch":\]\[value\] where value has had its leading and
	 * trailing double-quotes removed, and stripslashes() run on the rest of
	 * the value.
	 *
	 * @param array _matches Regexp matches
	 *
	 * @return string replacement string
	 *
	 */
	protected String _stripvalueslashes(String[] matches) {
		String str = matches[2].substring(1, matches[2].length()-1);
		return str.replaceAll("\\", "");
	}
	
	/**
	 *
	 * Encodes the mixed _valueToEncode into the JSON format, without use of
	 * native PHP json extension.
	 *
	 * @param mixed _var Any number, boolean, string, array, or object
	 * to be encoded. Strings are expected to be in ASCII or UTF-8 format.
	 *
	 * @return mixed JSON string representation of input value
	 *
	 */
	protected String _json_encode(Object var) {
		if (var == null) {
			return "null";
		} else if (var instanceof Boolean) {
			return (Boolean) var ? "true" : "false";
		} else if (var instanceof Integer || var instanceof Long || var instanceof BigInteger) {
			// BREAK WITH Services_JSON:
			// disabled for compatibility with ext/json. ext/json returns
			// a string for integers, so we will to.
			return String.valueOf(var);
		} else if (var instanceof Double || var instanceof Float) {
			// BREAK WITH Services_JSON:
			// disabled for compatibility with ext/json. ext/json returns
			// a string for floats and doubles, so we will to.
			return String.valueOf(var);
		} else if (var instanceof String) {
			// STRINGS ARE EXPECTED TO BE IN ASCII OR UTF-8 FORMAT
			String obj = (String)var;
			StringBuilder ascii = new StringBuilder();
			byte[] bytes = obj.getBytes();
			int strlen_var = bytes.length;
			/*
			 * Iterate over every character in the string,
			 * escaping with a slash or encoding to UTF-8 where necessary
			 */
			for (int c = 0; c < strlen_var; ++c) {
				int ord_var_c = ord(bytes[c]);
				switch (ord_var_c) {
				case 0x08:
					ascii.append('\b');
					break;
				case 0x09:
					ascii.append('\t');
					break;
				case 0x0A:
					ascii.append('\n');
					break;
				case 0x0C:
					ascii.append('\f');
					break;
				case 0x0D:
					ascii.append('\r');
					break;
				case 0x22:
				case 0x2F:
				case 0x5C:
					// double quote, slash, slosh
					ascii.append("\\\\").append(bytes[c]);
					break;
				default:
					char _char;
					String _utf16 = null;
					if (ord_var_c >= 0x20 && ord_var_c <= 0x7F){
						// characters U-00000000 - U-0000007F (same as ASCII)
						ascii.append((char)bytes[c]);
					} else if ((ord_var_c & 0xE0) == 0xC0){
						// characters U-00000080 - U-000007FF, mask 110XXXXX
						// see http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
						_char = (new String(new byte[] { 
								(byte) ord_var_c,
								(byte) ord(bytes[c + 1])
								})).charAt(0);
						c += 1;
						_utf16 = _utf82utf16(_char);
						ascii.append(String.format("\\u%1$04x", Integer.valueOf(_utf16)));
					} else if ((ord_var_c & 0xF0) == 0xE0){
						// characters U-00000800 - U-0000FFFF, mask 1110XXXX
						// see http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
						_char = (new String(new byte[] { 
								(byte) ord_var_c,
								(byte) ord(bytes[c + 1]),
								(byte) ord(bytes[c + 2]) 
								})).charAt(0);
						c += 2;
						_utf16 = _utf82utf16(_char);
						ascii.append(String.format("\\u%1$04x", Integer.valueOf(_utf16)));
					} else if((ord_var_c & 0xF8) == 0xF0){
						// characters U-00010000 - U-001FFFFF, mask 11110XXX
						// see http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
						_char = (new String(new byte[] { 
								(byte) ord_var_c,
								(byte) ord(bytes[c + 1]),
								(byte) ord(bytes[c + 2]),
								(byte) ord(bytes[c + 3])
								})).charAt(0);
						c += 3;
						_utf16 = _utf82utf16(_char);
						ascii.append(String.format("\\u%1$04x", Integer.valueOf(_utf16)));
					} else if((ord_var_c & 0xFC) == 0xF8){
						// characters U-00200000 - U-03FFFFFF, mask 111110XX
						// see http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
						_char = (new String(new byte[] { 
								(byte) ord_var_c,
								(byte) ord(bytes[c + 1]),
								(byte) ord(bytes[c + 2]),
								(byte) ord(bytes[c + 3]),
								(byte) ord(bytes[c + 4])
								})).charAt(0);
						c += 4;
						_utf16 = _utf82utf16(_char);
						ascii.append(String.format("\\u%1$04x", Integer.valueOf(_utf16)));
					} else if ((ord_var_c & 0xFE) == 0xFC){
						// characters U-04000000 - U-7FFFFFFF, mask 1111110X
						// see http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
						_char = (new String(new byte[] { 
								(byte) ord_var_c,
								(byte) ord(bytes[c + 1]),
								(byte) ord(bytes[c + 2]),
								(byte) ord(bytes[c + 3]),
								(byte) ord(bytes[c + 4]),
								(byte) ord(bytes[c + 5])
								})).charAt(0);
						c += 5;
						_utf16 = _utf82utf16(_char);
						ascii.append(String.format("\\u%1$04x", Integer.valueOf(_utf16)));
					}
				}
			}
			return '"'+ascii.toString()+'"';
		} else if (isArray(var)) {
			/*
			 * As per JSON spec if any array key is not an integer
			 * we must treat the whole array as an object. We
			 * also try to catch a sparsely populated associative
			 * array with numeric keys here because some JS engines
			 * will create an array with empty indexes up to
			 * max_index which can cause memory issues and because
			 * the keys, which may be relevant, will be remapped
			 * otherwise.
			 *
			 * As per the ECMA and JSON specification an object may
			 * have any string as a property. Unfortunately due to
			 * a hole in the ECMA specification ifthe key is a
			 * ECMA reserved word or starts with a digit the
			 * parameter is only accessible using ECMAScript's
			 * bracket notation.
			 */

			// treat as a JSON object
			if (this._forceObject || (var instanceof Map) && !((Map)var).isEmpty() && !array_keys_match_range((Map)var)) {
				Map<Object,Object> m = (Map)var;
				String[] properties = new String[m.size()];
				int i=0;
				for (Entry e : m.entrySet()) {
					properties[i++] = this._name_value(Nest.as(e.getKey()).asString(), e.getValue());
				}
				return '{' + StringUtils.join(properties, ',') + '}';
			}
			
			if (var instanceof List) {
				List datas = (List)var;
				String[] elements = new String[datas.size()];
				for (int i = 0; i < datas.size(); i++) {
					elements[i] = _json_encode(datas.get(i));
				}
				return '[' + StringUtils.join(elements, ',') + ']';
			}
			
			if (var instanceof Map) {
				 Object[] datas = ((Map)var).values().toArray();
				String[] elements = new String[datas.length];
				for (int i = 0; i < datas.length; i++) {
					elements[i] = _json_encode(datas[i]);
				}
				return '[' + StringUtils.join(elements, ',') + ']';
			}
			
			int len = Array.getLength(var);
			String[] elements = new String[len];
			for (int i = 0; i < len; i++) {
				elements[i] = _json_encode(Array.get(var, i));
			}
			return '[' + StringUtils.join(elements, ',') + ']';
		} else {
			Field[] fields = var.getClass().getDeclaredFields();
			CArray<String> properties = new CArray();
			for (Field f : fields) {
				if (f.isAccessible()) {
					try {
						properties.add(this._name_value(f.getName(), f.get(var)));
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			return '{' + StringUtils.join(properties.valuesAsString(), ',') + '}';
		}
	}
	
	private boolean array_keys_match_range(Map var) {
		Object[] idxs = var.keySet().toArray();
		for (int i = 0; i < idxs.length; i++) {
			if (!Nest.as(idxs[i]).asString().equals(String.valueOf(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Array-walking method for use in generating JSON-formatted name-value
	 * pairs in the form of '"name":value'.
	 *
	 * @param string _name name of key to use
	 * @param mixed _value element to be encoded
	 * @return string JSON-formatted name-value pair
	 */
	protected String _name_value(String _name, Object _value) {
		String _encoded_value = this._json_encode(_value);
		return this._json_encode(_name) + ':'+ _encoded_value;
	}
	
	private int ord(char c) {
		return ord(String.valueOf(c).getBytes()[0]);
	}
	
	private int ord(byte b) {
		if ((b & (1 << 7)) > 0) {
			return (1 << 7) + (b & (0xFF - (1 << 7)));
		} else {
			return b;
		}
	}
	
	/**
	 * Convert a string from one UTF-16 char to one UTF-8 char.
	 *
	 * Normally should be handled by mb_convert_encoding, but
	 * provides a slower PHP-only method for installations
	 * that lack the multibye string extension.
	 *
	 * @param string _utf16 UTF-16 character
	 * @return string UTF-8 character
	 */
	protected char _utf162utf8(String utf16) {
//		// oh please oh please oh please oh please oh please
//		if (!_this->_config['bypass_mb'] && function_exists('mb_convert_encoding')) {
//			return mb_convert_encoding(_utf16, 'UTF-8', 'UTF-16');
//		}
		//byte[] bytes = String.valueOf(utf16).getBytes();
		byte[] bytes = utf16.getBytes();
		int csum = (ord(bytes[0]) << 8) | ord(bytes[1]);

		if ((0x7F & csum) == csum) {
			// this case should never be reached, because we are in ASCII range
			// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
			return (char)(0x7F & csum);
		} else if ((0x07FF & csum) == csum) {
			// return a 2-byte UTF-8 character
			// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
			return (new String(new byte[]{(byte)(0xC0 | ((csum >> 6) & 0x1F)),(byte)(0x80 | (csum & 0x3F))})).charAt(0);
		} else if ((0xFFFF & csum) == csum) {
			// return a 3-byte UTF-8 character
			// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
			return (new String(new byte[]{(byte)(0xE0 | ((csum >> 12) & 0x0F)),(byte)(0x80 | ((csum >> 6) & 0x3F)), (byte)(0x80 | (csum & 0x3F))})).charAt(0);
		}
		return "".charAt(0);
	}
		
	/**
	 * Convert a string from one UTF-8 char to one UTF-16 char.
	 *
	 * Normally should be handled by mb_convert_encoding, but
	 * provides a slower PHP-only method for installations
	 * that lack the multibye string extension.
	 *
	 * @param string _utf8 UTF-8 character
	 * @return string UTF-16 character
	 */
	protected String _utf82utf16(char utf8) {
//		// oh please oh please oh please oh please oh please
//		if (!_this->_config['bypass_mb'] && function_exists('mb_convert_encoding')) {
//			return mb_convert_encoding(_utf8, 'UTF-16', 'UTF-8');
//		}
		byte[] bytes = String.valueOf(utf8).getBytes();
		switch (bytes.length) {
			case 1:
				// this case should never be reached, because we are in ASCII range
				// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
				return String.valueOf(utf8);
			case 2:
				// return a UTF-16 character from a 2-byte UTF-8 char
				// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
				return (new String(new byte[]{(byte)(0x07 & (ord(bytes[0]) >> 2)),(byte)((0xC0 & (ord(bytes[0]) << 6)) | (0x3F & ord(bytes[1])))}));
			case 3:
				// return a UTF-16 character from a 3-byte UTF-8 char
				// see: http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8
				return (new String(new byte[]{(byte)((0xF0 & (ord(bytes[0]) << 4)) | (0x0F & (ord(bytes[1]) >> 2))),(byte)((0xC0 & (ord(bytes[1]) << 6)) | (0x7F & ord(bytes[2])))}));
		}
		// ignoring UTF-32 for now, sorry
		return "";
	}
	
	/**
	 * Reduce a string by removing leading and trailing comments and whitespace.
	 *
	 * @param string _str string value to strip of comments and whitespace
	 * @return string string value stripped of comments and whitespace
	 */
	protected String _reduce_string(String str) {
		str = preg_replace("#^\\s*//(.+)$#m", "", str);
		str = preg_replace("#^\\s*/\\*(.+)\\*/#Us", "", str);
		str = preg_replace("#/\\*(.+)\\*/\\s*$#Us", "", str);
		// eliminate extraneous space
		return trim(str);
	}
	
	protected void _exception(int code) {
		_exception(code, array());
	}
	
	protected void _exception(int code, CArray info) {
		
	}
	
	//***************************************************************************
	// 								CHECK JSON									*
	//***************************************************************************
	private final static byte S_ERR = -1;	// Error
	private final static byte S_SPA = 0;	// Space
	private final static byte S_WSP = 1;	// Other whitespace
	private final static byte S_LBE = 2;	// {
	private final static byte S_RBE = 3;	// }
	private final static byte S_LBT = 4;	// [
	private final static byte S_RBT = 5;	// ]
	private final static byte S_COL = 6;	// :
	private final static byte S_COM = 7;	// ,
	private final static byte S_QUO = 8;	// "
	private final static byte S_BAC = 9;	// \
	private final static byte S_SLA = 10;	// /
	private final static byte S_PLU = 11;	// +
	private final static byte S_MIN = 12;	// -
	private final static byte S_DOT = 13;	// .
	private final static byte S_ZER = 14;	// 0
	private final static byte S_DIG = 15;	// 123456789
	private final static byte S__A_ = 16;	// a
	private final static byte S__B_ = 17;	// b
	private final static byte S__C_ = 18;	// c
	private final static byte S__D_ = 19;	// d
	private final static byte S__E_ = 20;	// e
	private final static byte S__F_ = 21;	// f
	private final static byte S__L_ = 22;	// l
	private final static byte S__N_ = 23;	// n
	private final static byte S__R_ = 24;	// r
	private final static byte S__S_ = 25;	// s
	private final static byte S__T_ = 26;	// t
	private final static byte S__U_ = 27;	// u
	private final static byte S_A_F = 28;	// ABCDF
	private final static byte S_E = 29;		// E
	private final static byte S_ETC = 30;	// Everything else
	
	/**
	 * Map of 128 ASCII characters into the 32 character classes.
	 * The remaining Unicode characters should be mapped to S_ETC.
	 *
	 * @var array
	 */
	protected byte[] _ascii_class;

	/**
	 * State transition table.
	 * @var array
	 */
	protected int[][] _state_transition_table;
	
	/**
	 * These modes can be pushed on the "pushdown automata" (PDA) stack.
	 * @constant
	 */
	private final static int MODE_DONE		= 1;
	private final static int MODE_KEY		= 2;
	private final static int MODE_OBJECT	= 3;
	private final static int MODE_ARRAY	= 4;
	
	/**
	 * Max depth allowed for nested structures.
	 * @constant
	 */
	private final static int MAX_DEPTH = 20;
	
	/**
	 * The stack to maintain the state of nested structures.
	 * @var array
	 */
	protected CArray<Integer> _the_stack = array();

	/**
	 * Pointer for the top of the stack.
	 * @var int
	 */
	protected int _the_top;
	
	/**
	 * The isValid method takes a UTF-16 encoded string and determines if it is
	 * a syntactically correct JSON text.
	 *
	 * It is implemented as a Pushdown Automaton; that means it is a finite
	 * state machine with a stack.
	 *
	 * @param string _str The JSON text to validate
	 * @return bool
	 */
	public boolean isValid(String str) {
		int len = rda_strlen(str);
		int state = 0;
		this._the_top = -1;
		this._push(MODE_DONE);
		char b;
		byte c;
		int s;

		for (int idx = 0; idx < len; idx++) {
			b = str.charAt(idx);
			
			if (chr(ord(b) & 127) == b) {
				c = this._ascii_class[ord(b)];
				if (c <= S_ERR) {
					return false;
				}
			} else {
				c = S_ETC;
			}

			// get the next state from the transition table
			s = this._state_transition_table[state][c];

			if (s < 0) {
				// perform one of the predefined actions
				switch (s) {
					// empty }
					case -9:
						if (!this._pop(MODE_KEY)) {
							return false;
						}
						state = 9;
						break;
					// {
					case -8:
						if (!this._push(MODE_KEY)) {
							return false;
						}
						state = 1;
						break;
					// }
					case -7:
						if (!this._pop(MODE_OBJECT)) {
							return false;
						}
						state = 9;
						break;
					// [
					case -6:
						if (!this._push(MODE_ARRAY)) {
							return false;
						}
						state = 2;
						break;
					// ]
					case -5:
						if (!this._pop(MODE_ARRAY)) {
							return false;
						}
						state = 9;
						break;
					// "
					case -4:
						switch (this._the_stack.get(this._the_top)) {
							case MODE_KEY:
								state = 27;
								break;
							case MODE_ARRAY:
							case MODE_OBJECT:
								state = 9;
								break;
							default:
								return false;
						}
						break;
					// '
					case -3:
						switch (this._the_stack.get(this._the_top)) {
							case MODE_OBJECT:
								if (this._pop(MODE_OBJECT) && this._push(MODE_KEY)) {
									state = 29;
								}
								break;
							case MODE_ARRAY:
								state = 28;
								break;
							default:
								return false;
						}
						break;
					// :
					case -2:
						if (this._pop(MODE_KEY) && this._push(MODE_OBJECT)) {
							state = 28;
							break;
						}
					// syntax error
					case -1:
						return false;
				}
			} else {
				// change the state and iterate
				state = s;
			}
		}
		if (state == 9 && this._pop(MODE_DONE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Map the 128 ASCII characters into the 32 character classes.
	 * The remaining Unicode characters should be mapped to S_ETC.
	 *
	 * @return void
	 */
	protected void _mapAscii() {
		this._ascii_class = new byte[]{
			S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR,
			S_ERR, S_WSP, S_WSP, S_ERR, S_ERR, S_WSP, S_ERR, S_ERR,
			S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR,
			S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR, S_ERR,

			S_SPA, S_ETC, S_QUO, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC,
			S_ETC, S_ETC, S_ETC, S_PLU, S_COM, S_MIN, S_DOT, S_SLA,
			S_ZER, S_DIG, S_DIG, S_DIG, S_DIG, S_DIG, S_DIG, S_DIG,
			S_DIG, S_DIG, S_COL, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC,

			S_ETC, S_A_F, S_A_F, S_A_F, S_A_F, S_E  , S_A_F, S_ETC,
			S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC,
			S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC, S_ETC,
			S_ETC, S_ETC, S_ETC, S_LBT, S_BAC, S_RBT, S_ETC, S_ETC,

			S_ETC, S__A_, S__B_, S__C_, S__D_, S__E_, S__F_, S_ETC,
			S_ETC, S_ETC, S_ETC, S_ETC, S__L_, S_ETC, S__N_, S_ETC,
			S_ETC, S_ETC, S__R_, S__S_, S__T_, S__U_, S_ETC, S_ETC,
			S_ETC, S_ETC, S_ETC, S_LBE, S_ETC, S_RBE, S_ETC, S_ETC
		};
	}
	
	/**
	 * The state transition table takes the current state and the current symbol,
	 * and returns either a new state or an action. A new state is a number between
	 * 0 and 29. An action is a negative number between -1 and -9. A JSON text is
	 * accepted if the end of the text is in state 9 and mode is MODE_DONE.
	 *
	 * @return void;
	 */
	protected void _setStateTransitionTable() {
		this._state_transition_table = new int[][]{
			new int[]{ 0, 0,-8,-1,-6,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{ 1, 1,-1,-9,-1,-1,-1,-1, 3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{ 2, 2,-8,-1,-6,-5,-1,-1, 3,-1,-1,-1,20,-1,21,22,-1,-1,-1,-1,-1,13,-1,17,-1,-1,10,-1,-1,-1,-1},
			new int[]{ 3,-1, 3, 3, 3, 3, 3, 3,-4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1, 3, 3, 3,-1,-1,-1,-1,-1,-1, 3,-1,-1,-1, 3,-1, 3, 3,-1, 3, 5,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 6, 6, 6, 6, 6, 6, 6, 6,-1,-1,-1,-1,-1,-1, 6, 6,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 7, 7, 7, 7, 7, 7, 7, 7,-1,-1,-1,-1,-1,-1, 7, 7,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 8, 8, 8, 8, 8, 8, 8, 8,-1,-1,-1,-1,-1,-1, 8, 8,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 3, 3, 3, 3, 3, 3, 3, 3,-1,-1,-1,-1,-1,-1, 3, 3,-1},
			new int[]{ 9, 9,-1,-7,-1,-5,-1,-3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,11,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,12,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 9,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,14,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,15,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 9,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,18,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,19,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 9,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,21,22,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{ 9, 9,-1,-7,-1,-5,-1,-3,-1,-1,-1,-1,-1,23,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{ 9, 9,-1,-7,-1,-5,-1,-3,-1,-1,-1,-1,-1,23,22,22,-1,-1,-1,-1,24,-1,-1,-1,-1,-1,-1,-1,-1,24,-1},
			new int[]{ 9, 9,-1,-7,-1,-5,-1,-3,-1,-1,-1,-1,-1,-1,23,23,-1,-1,-1,-1,24,-1,-1,-1,-1,-1,-1,-1,-1,24,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,25,25,-1,26,26,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,26,26,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{ 9, 9,-1,-7,-1,-5,-1,-3,-1,-1,-1,-1,-1,-1,26,26,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{27,27,-1,-1,-1,-1,-2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			new int[]{28,28,-8,-1,-6,-1,-1,-1, 3,-1,-1,-1,20,-1,21,22,-1,-1,-1,-1,-1,13,-1,17,-1,-1,10,-1,-1,-1,-1},
			new int[]{29,29,-1,-1,-1,-1,-1,-1, 3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}
		};
	}
	
	/**
	 * Push a mode onto the stack. Return false if there is overflow.
	 *
	 * @param int _mode Mode to push onto the stack
	 * @return bool Success/failure of stack push
	 */
	protected boolean _push(int mode) {
		this._the_top++;
		if (this._the_top >= MAX_DEPTH) {
			return false;
		}
		Nest.value(this._the_stack,this._the_top).$(mode);
		return true;
	}

	/**
	 * Pop the stack, assuring that the current mode matches the expectation.
	 * Return false if there is underflow or if the modes mismatch.
	 *
	 * @param int _mode Mode to pop from the stack
	 * @return bool Success/failure of stack pop
	 */
	protected boolean _pop(int mode) {
		if (this._the_top < 0 || Nest.value(this._the_stack,this._the_top).asInteger() != mode) {
			return false;
		}
		Nest.value(this._the_stack,this._the_top).$(0);
		this._the_top--;
		return true;
	}

	public static CArray<Map> decorate(CArray<Map> datas,Decorator decorator) {
		CArray<Map> array = new CArray();
		for (Entry<Object, Map> e : datas.entrySet()) {
		    Object k = e.getKey();
		    Map v = e.getValue();
		    array.put(k, decorator.doDecorator(v));
		}
		return array;
	}
	
	public static abstract class Decorator {
		public Map doDecorator(Map o){
			Map m = new CArray();
			Object k = null, v=null;
			for (Entry<Object, Object> e : ((Map<Object,Object>)o).entrySet()) {
			    k = e.getKey();
			    v = doDecoratorValue(k,e.getValue());
			    m.put(k, v);
			}
			return m;
		}

		protected abstract Object doDecoratorValue(Object key, Object value);
	}
}
