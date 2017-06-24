package com.isoft.iradar.parsers;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_BYTE_SUFFIXES;
import static com.isoft.iradar.inc.Defines.RDA_TIME_SUFFIXES;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTriggerExpression {
	
	// for parsing of trigger expression
	private final static int STATE_INIT = 0;
	private final static int STATE_AFTER_OPEN_BRACE = 1;
	private final static int STATE_AFTER_OPERATOR = 2;
	private final static int STATE_AFTER_MINUS = 3;
	private final static int STATE_AFTER_CLOSE_BRACE = 4;
	private final static int STATE_AFTER_CONSTANT = 5;
	
	// for parse of item key parameters
	private final static int STATE_NEW = 0;
	private final static int STATE_END = 1;
	private final static int STATE_UNQUOTED = 2;
	private final static int STATE_QUOTED = 3;
	
	/**
	 * Shows a validity of trigger expression
	 */
	public boolean isValid;
	
	/**
	 * An error message if trigger expression is not valid
	 */
	public String error;
	
	/**
	 * An array of trigger functions like {iRadar server:agent.ping.last(0)}
	 * The array isn't unique. Same functions can repeats.
	 *
	 * Example:
	 *   'expressions' => array(
	 *     0 => array(
	 *       'expression' => '{iRadar server:agent.ping.last(0)}',
	 *       'pos' => 0,
	 *       'host' => 'iRadar server',
	 *       'item' => 'agent.ping',
	 *       'function' => 'last(0)',
	 *       'functionName' => 'last',
	 *       'functionParam' => '0',
	 *       'functionParamList' => array (0 => '0')
	 *     )
	 *   )
	 */
	public CArray<Map<String,Object>> expressions;
	
	/**
	 * An array of macros like {TRIGGER.VALUE}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *   'expressions' => array(
	 *     0 => array(
	 *       'expression' => '{TRIGGER.VALUE}'
	 *     )
	 *   )
	 */
	public CArray<Map<String,String>> macros;
	
	/**
	 * An array of user macros like {$MACRO}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *    array(
	 *     0 => array(
	 *       'expression' => '{$MACRO}'
	 *     ),
	 *     1 => array(
	 *       'expression' => '{$MACRO2}'
	 *     ),
	 *     2 => array(
	 *       'expression' => '{$MACRO}'
	 *     )
	 *   )
	 */
	public CArray<Map<String,String>> usermacros;
	
	/**
	 * An array of low-level discovery macros like {#MACRO}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *    array(
	 *     0 => array(
	 *       'expression' => '{#MACRO}'
	 *     ),
	 *     1 => array(
	 *       'expression' => '{#MACRO2}'
	 *     ),
	 *     2 => array(
	 *       'expression' => '{#MACRO}'
	 *     )
	 *   )
	 */
	public CArray<Map<String,String>> lldmacros;
	
	/**
	 * An initial expression
	 */
	public String expression;
	
	/**
	 * An options array
	 *
	 * Supported otions:
	 *   'lldmacros' => true	low-level discovery macros can contain in trigger expression
	 */
	public Map<String,Object> options = (Map)map("lldmacros", true);
	
	/**
	 * A current position on a parsed element
	 */
	private Integer pos;
	
	public CTriggerExpression() {
		this(null);
	}

	public CTriggerExpression(Map options) {
		if (isset(options, "lldmacros")) {
			this.options.put("lldmacros", options.get("lldmacros"));
		}
	}
	
	/**
	 * Parse a trigger expression and set public variables this.isValid, this.error, this.expressions,
	 *   this.macros, this.usermacros and this.lldmacros
	 *
	 * Examples:
	 *   expression:
	 *     {iRadar server:agent.ping.lats(0)}=1 & {TRIGGER.VALUE}={$TRIGGER.VALUE}
	 *   results:
	 *     this.isValid : true
	 *     this.error : ''
	 *     this.expressions : array(
	 *       0 => array(
	 *         'expression' => '{iRadar server:agent.ping.last(0)}',
	 *         'pos' => 0,
	 *         'host' => 'iRadar server',
	 *         'item' => 'agent.ping',
	 *         'function' => 'last(0)',
	 *         'functionName' => 'last',
	 *         'functionParam' => '0',
	 *         'functionParamList' => array (0 => '0')
	 *       )
	 *     )
	 *     this.macros : array(
	 *       0 => array(
	 *         'expression' => '{TRIGGER.VALUE}'
	 *       )
	 *     )
	 *     this.usermacros : array(
	 *       0 => array(
	 *         'expression' => '{$TRIGGER.VALUE}'
	 *       )
	 *     )
	 * @param expression
	 * @return
	 */
	public boolean parse(String expression) {
		// initializing local variables
		this.isValid = true;
		this.error = "";
		this.expressions = new CArray();
		this.macros = new CArray();
		this.usermacros = new CArray();
		this.lldmacros = new CArray();
		
		this.pos = 0;
		this.expression = expression;
		
		int state = STATE_INIT;
		int level = 0;
		PARSE_OVER:
		while (this.pos < this.expression.length()) {
			switch (state) {
				case STATE_INIT:
				case STATE_AFTER_OPEN_BRACE:
				case STATE_AFTER_OPERATOR:
					switch (this.expression.charAt(this.pos)) {
						case ' ':
							break;
						case '-':
							state = STATE_AFTER_MINUS;
							break;
						case '(':
							state = STATE_AFTER_OPEN_BRACE;
							level++;
							break;
						default:
							if (!this.parseConstant()) {
								break PARSE_OVER;
							}
							state = STATE_AFTER_CONSTANT;
					}
					break;
				case STATE_AFTER_MINUS:
					switch (this.expression.charAt(this.pos)) {
						case ' ':
							break;
						case '(':
							state = STATE_AFTER_OPEN_BRACE;
							level++;
							break;
						default:
							if (!this.parseConstant()) {
								break PARSE_OVER;
							}
							state = STATE_AFTER_CONSTANT;
					}
					break;
				case STATE_AFTER_CLOSE_BRACE:
				case STATE_AFTER_CONSTANT:
					switch (this.expression.charAt(this.pos)) {
						case ' ':
							break;
						case '=':
						case '#':
						case '<':
						case '>':
						case '&':
						case '|':
						case '+':
						case '-':
						case '/':
						case '*':
							state = STATE_AFTER_OPERATOR;
							break;
						case ')':
							state = STATE_AFTER_CLOSE_BRACE;
							if (level == 0) {
								break PARSE_OVER;
							}
							level--;
							break;
						default:
							break PARSE_OVER;
					}
					break;
			}
			this.pos++;
		}
		
		if (this.pos == 0) {
			this.error = _("Incorrect trigger expression.");
			this.isValid = false;
		}

		if (level != 0 || (this.pos<this.expression.length()) || state == STATE_AFTER_OPERATOR || state == STATE_AFTER_MINUS) {
			this.error = _("Incorrect trigger expression.")
							+" "+_s("Check expression part starting from \"%1$s\".",
					this.expression.substring(this.pos == 0 ? 0 : this.pos - 1));
			this.isValid = false;
		}

		return this.isValid;
	}
	
	/**
	 * Returns a list of the unique hosts, used in a parsed trigger expression or empty array if expression is not valid
	 * @return
	 */
	public CArray<String> getHosts() {
		CArray<String> hosts = new CArray();
		if (this.isValid) {
			for (Map<String, Object> e : this.expressions) {
				hosts.add((String) e.get("host"));
			}
		}
		return hosts;
	}

	/**
	 * Parses a constant in the trigger expression and moves a current position (this.pos) on a last symbol of the constant
	 *
	 * The constant can be:
	 *  - trigger function like {host:item[].func()}
	 *  - floating point number; can be with suffix [KMGTsmhdw]
	 *  - macro like {TRIGGER.VALUE}
	 *  - user macro like {$MACRO}
	 * @return
	 */
	private boolean parseConstant() {
		if (this.parseFunctionMacro() 
				|| this.parseNumber()
				|| this.parseMacro() 
				|| this.parseUserMacro()
				|| this.parseLLDMacro()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Parses a trigger function macro constant in the trigger expression and
	 * moves a current position (this.pos) on a last symbol of the macro
	 * @return
	 */
	private boolean parseFunctionMacro() {
		int n = this.pos;

		String host = null;
		Object[] rets = null;
		if (!(n < this.expression.length())
				|| this.expression.charAt(n++) != '{'
				|| (rets = this.parseHost(n)) == null) {
			return false;
		}
		if (rets != null) {
			n = (Integer) rets[0];
			host = (String) rets[1];
		}

		String item = null;
		rets = null;
		if (!(n < this.expression.length())
				|| this.expression.charAt(n++) != ':'
				|| (rets = this.parseItem(n)) == null) {
			return false;
		}
		if (rets != null) {
			n = (Integer) rets[0];
			item = (String) rets[1];
		}

		String function = null;
		CArray<String> funcParamList = null;
		rets = null;
		if (!(n < this.expression.length())
				|| this.expression.charAt(n++) != '.'
				|| (rets = this.parseFunction(n)) == null) {
			return false;
		}
		if (rets != null) {
			n = (Integer) rets[0];
			function = (String) rets[1];
			funcParamList = (CArray<String>) rets[2];
		}

		if (!(n < this.expression.length())
				|| this.expression.charAt(n) != '}') {
			return false;
		}

		this.expressions.add((Map)map(
			"expression",this.expression.substring(this.pos, n + 1),
			"pos",this.pos,
			"host",host,
			"item",item,
			"function",function,
			"functionName",function.substring(0, function.indexOf('(')),
			"functionParam",function.substring(function.indexOf('(') + 1, function.length()-1),
			"functionParamList",funcParamList
		));
		this.pos = n;
		return true;
	}

	/**
	 * Parses a host in a trigger function macro constant and moves a position (_pos) on a next symbol after the host
	 * @param pos
	 * @return
	 */
	private Object[] parseHost(int pos){
		int n = pos;

		while ((n<this.expression.length()) && this.isHostChar(this.expression.charAt(n))) {
			n++;
		}

		// is host empty?
		if (pos == n) {
			return null;
		}

		String _host = this.expression.substring(pos, n);
		return new Object[]{n,_host};
	}
	
	/**
	 * Parses an item in a trigger function macro constant and moves a position (_pos) on a next symbol after the item
	 * @param pos
	 * @return
	 */
	private Object[] parseItem(int pos) {
		int n = pos;

		while ((n<this.expression.length()) && this.isKeyChar(this.expression.charAt(n))) {
			n++;
		}

		// for instance, agent.ping.last(0)
		if ((n<this.expression.length()) && this.expression.charAt(n) == '(') {
			while (n > pos && this.expression.charAt(n) != '.') {
				n--;
			}
		}
		// for instance, net.tcp.port[,80]
		else if ((n<this.expression.length()) && this.expression.charAt(n) == '[') {
			int level = 0;
			int state = STATE_END;

			while ((n<this.expression.length())) {
				if (level == 0) {
					// first square bracket + Zapcat compatibility
					if (state == STATE_END && this.expression.charAt(n) == '[') {
						state = STATE_NEW;
					}
					else {
						break;
					}
				}

				switch (state) {
					// a new parameter started
					case STATE_NEW:
						switch (this.expression.charAt(n)) {
							case ' ':
							case ',':
								break;
							case '[':
								level++;
								break;
							case ']':
								level--;
								state = STATE_END;
								break;
							case '"':
								state = STATE_QUOTED;
								break;
							default:
								state = STATE_UNQUOTED;
						}
						break;
					// end of parameter
					case STATE_END:
						switch (this.expression.charAt(n)) {
							case ' ':
								break;
							case ',':
								state = STATE_NEW;
								break;
							case ']':
								level--;
								break;
							default:
								return null;
						}
						break;
					// an unquoted parameter
					case STATE_UNQUOTED:
						switch (this.expression.charAt(n)) {
							case ']':
								level--;
								state = STATE_END;
								break;
							case ',':
								state = STATE_NEW;
								break;
						}
						break;
					// a quoted parameter
					case STATE_QUOTED:
						switch (this.expression.charAt(n)) {
							case '"':
								if (this.expression.charAt(n - 1) != '\\') {
									state = STATE_END;
								}
								break;
						}
						break;
				}
				n++;
			}

			if (level != 0) {
				return null;
			}
		}

		// is key empty?
		if (pos == n) {
			return null;
		}

		String item = this.expression.substring(pos, n);
		return new Object[] { n, item };
	}

	/**
	 * Parses an function in a trigger function macro constant and moves a position (_pos) on a next symbol after the function
	 *
	 * Returns an array if parsed successfully or null otherwise
	 * Returned array contains two elements:
	 *   0 => function name like "last(0)"
	 *   1 => array of parsed function parameters
	 * @param pos
	 * @return
	 */
	private Object[] parseFunction(int pos) {
		int j = pos;

		while ((j<this.expression.length()) && this.isFunctionChar(this.expression.charAt(j))) {
			j++;
		}

		// is function empty?
		if (pos == j) {
			return null;
		}

		if (!(j<this.expression.length()) || this.expression.charAt(j++) != '(') {
			return null;
		}

		int state = STATE_NEW;
		CArray<String> funcParamList = new CArray();
		int _num = 0;
		Nest.value(funcParamList, _num).$("");
		
PARSE_OVER: while (j<this.expression.length()) {
			switch (state) {
				// a new parameter started
				case STATE_NEW:
					switch (this.expression.charAt(j)) {
						case ' ':
							break;
						case ',':
							Nest.value(funcParamList, ++_num).$("");
							break;
						case ')':
							// end of parameters
							break PARSE_OVER;
						case '"':
							state = STATE_QUOTED;
							break;
						default:
							Nest.value(funcParamList, _num).plus(this.expression.charAt(j));
							state = STATE_UNQUOTED;
					}
					break;
				// end of parameter
				case STATE_END:
					switch (this.expression.charAt(j)) {
						case ' ':
							break;
						case ',':
							Nest.value(funcParamList, ++_num).$("");
							state = STATE_NEW;
							break;
						case ')':
							// end of parameters
							break PARSE_OVER;
						default:
							return null;
					}
					break;
				// an unquoted parameter
				case STATE_UNQUOTED:
					switch (this.expression.charAt(j)) {
						case ')':
							// end of parameters
							break PARSE_OVER;
						case ',':
							Nest.value(funcParamList, ++_num).$("");
							state = STATE_NEW;
							break;
						default:
							Nest.value(funcParamList, _num).plus(this.expression.charAt(j));
					}
					break;
				// a quoted parameter
				case STATE_QUOTED:
					switch (this.expression.charAt(j)) {
						case '"':
							state = STATE_END;
							break;
						case '\\':
							if (((j+1)<this.expression.length()) && this.expression.charAt(j + 1) == '"') {
								j++;
							}
							// break; is not missing here
						default:
							Nest.value(funcParamList, _num).plus(this.expression.charAt(j));
							break;
					}
					break;
			}
			j++;
		}

		if (!(j<this.expression.length()) || this.expression.charAt(j++) != ')') {
			return null;
		}

		String function = this.expression.substring( pos, j);
		return new Object[] { j, function, funcParamList };
	}
	
	/**
	 * Parses a number constant in the trigger expression and
	 * moves a current position (this.pos) on a last symbol of the number
	 * @return
	 */
	private boolean parseNumber() {
		int n = this.pos;

		if (this.expression.charAt(n) < '0' || this.expression.charAt(n) > '9') {
			return false;
		}

		n++;
		while ((n<this.expression.length()) && this.expression.charAt(n) >= '0' && this.expression.charAt(n) <= '9') {
			n++;
		}

		if ((n<this.expression.length()) && this.expression.charAt(n) == '.') {
			n++;
			if (!(n<this.expression.length()) || this.expression.charAt(n) < '0' || this.expression.charAt(n) > '9') {
				return false;
			}

			n++;
			while ((n<this.expression.length()) && this.expression.charAt(n) >= '0' && this.expression.charAt(n) <= '9') {
				n++;
			}
		}

		// check for an optional suffix
		if ((n < this.expression.length())
				&& (RDA_BYTE_SUFFIXES + RDA_TIME_SUFFIXES).indexOf(this.expression.charAt(n)) > -1) {
			n++;
		}

		this.pos = n - 1;
		return true;
	}
	
	/**
	 * Parses a macro constant in the trigger expression and
	 * moves a current position (this.pos) on a last symbol of the macro
	 * @return
	 */
	private boolean parseMacro() {
		String[] tmacros = new String[] { "{TRIGGER.VALUE}" };
		int maxLen = this.expression.length();
		for (String macro : tmacros) {
			int len = macro.length();

			if (!this.expression.substring(this.pos, Math.min(this.pos+len, maxLen)).equals(macro)) {
				continue;
			}
			this.macros.add((Map)map("expression", macro));
			this.pos += len - 1;
			return true;
		}
		return false;
	}
	
	/**
	 * Parses an user macro constant in the trigger expression and
	 * moves a current position (this.pos) on a last symbol of the macro
	 * @return
	 */
	private boolean parseUserMacro() {
		int n = this.pos;

		if (this.expression.charAt(n++) != '{') {
			return false;
		}

		if (!(n<this.expression.length()) || this.expression.charAt(n++) != '$') {
			return false;
		}

		if (!(n<this.expression.length()) || !this.isMacroChar(this.expression.charAt(n++))) {
			return false;
		}

		while ((n<this.expression.length()) && this.isMacroChar(this.expression.charAt(n))) {
			n++;
		}

		if (!(n<this.expression.length()) || this.expression.charAt(n) != '}') {
			return false;
		}

		String usermacro = this.expression.substring(this.pos, n + 1);
		this.usermacros.add((Map)map("expression", usermacro));
		this.pos = n;
		return true;
	}
	
	/**
	 * Parses a low-level discovery macro constant in the trigger expression and
	 * moves a current position (this.pos) on a last symbol of the macro
	 * @return
	 */
	private boolean parseLLDMacro() {
		if (!(Boolean)this.options.get("lldmacros")) {
			return false;
		}

		int n = this.pos;

		if (this.expression.charAt(n++) != '{') {
			return false;
		}

		if (!(n<this.expression.length()) || this.expression.charAt(n++) != '#') {
			return false;
		}

		if (!(n<this.expression.length()) || !this.isMacroChar(this.expression.charAt(n++))) {
			return false;
		}

		while ((n<this.expression.length()) && this.isMacroChar(this.expression.charAt(n))) {
			n++;
		}

		if (!(n<this.expression.length()) || this.expression.charAt(n) != '}') {
			return false;
		}
		String lldmacro = this.expression.substring( this.pos, n + 1);
		this.lldmacros.add((Map)map("expression", lldmacro));
		this.pos = n;
		return true;
	}
	
	/**
	 * Returns true if the char is allowed in the host name, false otherwise
	 * @param c
	 * @return
	 */
	private boolean isHostChar(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9') || c == '.' || c == ' ' || c == '_'
				|| c == '-') {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the char is allowed in the item key, false otherwise
	 * @param c
	 * @return
	 */
	private boolean isKeyChar(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-') {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the char is allowed in the function name, false otherwise
	 * @param c
	 * @return
	 */
	private boolean isFunctionChar(char c) {
		if (c >= 'a' && c <= 'z') {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the char is allowed in the macro, false otherwise 
	 * @param c
	 * @return
	 */
	private boolean isMacroChar(char c) {
		if ((c >= 'A' && c <= 'Z') || c == '.' || c == '_'
				|| (c >= '0' && c <= '9')) {
			return true;
		}
		return false;
	}
}
