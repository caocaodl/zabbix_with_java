<?php
/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/

class CTriggerExpression {
	// for parsing of trigger expression
	const STATE_INIT = 0;
	const STATE_AFTER_OPEN_BRACE = 1;
	const STATE_AFTER_OPERATOR = 2;
	const STATE_AFTER_MINUS = 3;
	const STATE_AFTER_CLOSE_BRACE = 4;
	const STATE_AFTER_CONSTANT = 5;

	// for parsing of item key parameters
	const STATE_NEW = 0;
	const STATE_END = 1;
	const STATE_UNQUOTED = 2;
	const STATE_QUOTED = 3;

	/**
	 * Shows a validity of trigger expression
	 *
	 * @var bool
	 */
	public $isValid;

	/**
	 * An error message if trigger expression is not valid
	 *
	 * @var string
	 */
	public $error;

	/**
	 * An array of trigger functions like {Zabbix server:agent.ping.last(0)}
	 * The array isn't unique. Same functions can repeats.
	 *
	 * Example:
	 *   "expressions" => CArray.array(
	 *     0 => CArray.array(
	 *       "expression" => "{Zabbix server:agent.ping.last(0)}",
	 *       "pos" => 0,
	 *       "host" => "Zabbix server",
	 *       "item" => "agent.ping",
	 *       "function" => "last(0)",
	 *       "functionName" => "last",
	 *       "functionParam" => "0",
	 *       "functionParamList" => array (0 => "0")
	 *     )
	 *   )
	 *
	 * @var array
	 */
	public $expressions = CArray.array();

	/**
	 * An array of macros like {TRIGGER.VALUE}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *   "expressions" => CArray.array(
	 *     0 => CArray.array(
	 *       "expression" => "{TRIGGER.VALUE}"
	 *     )
	 *   )
	 *
	 * @var array
	 */
	public $macros = CArray.array();

	/**
	 * An array of user macros like {$MACRO}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *    CArray.array(
	 *     0 => CArray.array(
	 *       "expression" => "{$MACRO}"
	 *     ),
	 *     1 => CArray.array(
	 *       "expression" => "{$MACRO2}"
	 *     ),
	 *     2 => CArray.array(
	 *       "expression" => "{$MACRO}"
	 *     )
	 *   )
	 *
	 * @var array
	 */
	public $usermacros = CArray.array();

	/**
	 * An array of low-level discovery macros like {#MACRO}
	 * The array isn't unique. Same macros can repeats.
	 *
	 * Example:
	 *    CArray.array(
	 *     0 => CArray.array(
	 *       "expression" => "{#MACRO}"
	 *     ),
	 *     1 => CArray.array(
	 *       "expression" => "{#MACRO2}"
	 *     ),
	 *     2 => CArray.array(
	 *       "expression" => "{#MACRO}"
	 *     )
	 *   )
	 *
	 * @var array
	 */
	public $lldmacros = CArray.array();

	/**
	 * An initial expression
	 *
	 * @var string
	 */
	public $expression;

	/**
	 * An options array
	 *
	 * Supported options:
	 *   "lldmacros" => true	low-level discovery macros can contain in trigger expression
	 *
	 * @var array
	 */
	public $options = CArray.array("lldmacros" => true);

	/**
	 * A current position on a parsed element
	 *
	 * @var integer
	 */
	private $pos;

	/**
	 * @param array $options
	 * @param bool $options["lldmacros"]
	 */
	public function __construct($options = CArray.array()) {
		if (isset(Nest.value($options,"lldmacros").$())) {
			Nest.value(options,"lldmacros").$() = Nest.value($options,"lldmacros").$();
		}
	}

	/**
	 * Parse a trigger expression and set public variables isValid, error, expressions,
	 *   macros, usermacros and lldmacros
	 *
	 * Examples:
	 *   expression:
	 *     {Zabbix server:agent.ping.lats(0)}=1 & {TRIGGER.VALUE}={$TRIGGER.VALUE}
	 *   results:
	 *     isValid : true
	 *     error : ""
	 *     expressions : CArray.array(
	 *       0 => CArray.array(
	 *         "expression" => "{Zabbix server:agent.ping.last(0)}",
	 *         "pos" => 0,
	 *         "host" => "Zabbix server",
	 *         "item" => "agent.ping",
	 *         "function" => "last(0)",
	 *         "functionName" => "last",
	 *         "functionParam" => "0",
	 *         "functionParamList" => array (0 => "0")
	 *       )
	 *     )
	 *     macros : CArray.array(
	 *       0 => CArray.array(
	 *         "expression" => "{TRIGGER.VALUE}"
	 *       )
	 *     )
	 *     usermacros : CArray.array(
	 *       0 => CArray.array(
	 *         "expression" => "{$TRIGGER.VALUE}"
	 *       )
	 *     )
	 *
	 * @param string $expression
	 *
	 * @return bool returns true if expression is valid, false otherwise
	 */
	public function parse($expression) {
		// initializing local variables
		isValid = true;
		error = "";
		expressions = CArray.array();
		macros = CArray.array();
		usermacros = CArray.array();
		lldmacros = CArray.array();

		pos = 0;
		expression = $expression;

		$state = self::STATE_INIT;
		$level = 0;

		while (isset(expression[pos])) {
			switch ($state) {
				case self::STATE_INIT:
				case self::STATE_AFTER_OPEN_BRACE:
				case self::STATE_AFTER_OPERATOR:
					switch (expression[pos]) {
						case " ":
							break;
						case "-":
							$state = self::STATE_AFTER_MINUS;
							break;
						case "(":
							$state = self::STATE_AFTER_OPEN_BRACE;
							$level++;
							break;
						default:
							if (!parseConstant()) {
								break 3;
							}
							$state = self::STATE_AFTER_CONSTANT;
					}
					break;
				case self::STATE_AFTER_MINUS:
					switch (expression[pos]) {
						case " ":
							break;
						case "(":
							$state = self::STATE_AFTER_OPEN_BRACE;
							$level++;
							break;
						default:
							if (!parseConstant()) {
								break 3;
							}
							$state = self::STATE_AFTER_CONSTANT;
					}
					break;
				case self::STATE_AFTER_CLOSE_BRACE:
				case self::STATE_AFTER_CONSTANT:
					switch (expression[pos]) {
						case " ":
							break;
						case "=":
						case "#":
						case "<":
						case ">":
						case "&":
						case "|":
						case "+":
						case "-":
						case "/":
						case "*":
							$state = self::STATE_AFTER_OPERATOR;
							break;
						case ")":
							$state = self::STATE_AFTER_CLOSE_BRACE;
							if ($level == 0) {
								break 3;
							}
							$level--;
							break;
						default:
							break 3;
					}
					break;
			}
			pos++;
		}

		if (pos == 0) {
			error = _("Incorrect trigger expression.");
			isValid = false;
		}

		if ($level != 0 || isset(expression[pos]) || $state == self::STATE_AFTER_OPERATOR || $state == self::STATE_AFTER_MINUS) {
			error = _("Incorrect trigger expression.")." "._s("Check expression part starting from \"%1$s\".",
					substr(expression, pos == 0 ? 0 : pos - 1));
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Returns a list of the unique hosts, used in a parsed trigger expression or empty array if expression is not valid
	 *
	 * @return array
	 */
	public function getHosts() {
		if (!isValid) {
			return CArray.array();
		}

		return array_unique(zbx_objectValues(expressions, "host"));
	}

	/**
	 * Parses a constant in the trigger expression and moves a current position (pos) on a last symbol of the constant
	 *
	 * The constant can be:
	 *  - trigger function like {host:item[].func()}
	 *  - floating point number; can be with suffix [KMGTsmhdw]
	 *  - macro like {TRIGGER.VALUE}
	 *  - user macro like {$MACRO}
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseConstant() {
		if (parseFunctionMacro() || parseNumber() || parseMacro() || parseUserMacro()
				|| parseLLDMacro()) {
			return true;
		}

		return false;
	}

	/**
	 * Parses a trigger function macro constant in the trigger expression and
	 * moves a current position (pos) on a last symbol of the macro
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseFunctionMacro() {
		$j = pos;

		if (!isset(expression[$j]) || expression[$j++] != "{" || ($host = parseHost($j)) === null) {
			return false;
		}

		if (!isset(expression[$j]) || expression[$j++] != ":" || ($item = parseItem($j)) === null) {
			return false;
		}

		if (!isset(expression[$j]) || expression[$j++] != "."
				|| !(list($function, $functionParamList) = parseFunction($j))) {
			return false;
		}

		if (!isset(expression[$j]) || expression[$j] != "}") {
			return false;
		}

		expressions[] = CArray.array(
			"expression" => substr(expression, pos, $j - pos + 1),
			"pos" => pos,
			"host" => $host,
			"item" => $item,
			"function" => $function,
			"functionName" => substr($function, 0, strpos($function, "(")),
			"functionParam" => substr($function, strpos($function, "(") + 1, -1),
			"functionParamList" => $functionParamList
		);
		pos = $j;
		return true;
	}

	/**
	 * Parses a host in a trigger function macro constant and moves a position ($pos) on a next symbol after the host
	 *
	 * @return string returns a host name if parsed successfully or null otherwise
	 */
	private function parseHost(&$pos)
	{
		$j = $pos;

		while (isset(expression[$j]) && isHostChar(expression[$j])) {
			$j++;
		}

		// is host empty?
		if ($pos == $j) {
			return null;
		}

		$host = substr(expression, $pos, $j - $pos);
		$pos = $j;
		return $host;
	}

	/**
	 * Parses an item in a trigger function macro constant and moves a position ($pos) on a next symbol after the item
	 *
	 * @return string returns an item name if parsed successfully or null otherwise
	 */
	private function parseItem(&$pos)
	{
		$j = $pos;

		while (isset(expression[$j]) && isKeyChar(expression[$j])) {
			$j++;
		}

		// for instance, agent.ping.last(0)
		if (isset(expression[$j]) && expression[$j] == "(") {
			while ($j > $pos && expression[$j] != ".") {
				$j--;
			}
		}
		// for instance, net.tcp.port[,80]
		elseif (isset(expression[$j]) && expression[$j] == "[") {
			$level = 0;
			$state = self::STATE_END;

			while (isset(expression[$j])) {
				if ($level == 0) {
					// first square bracket + Zapcat compatibility
					if ($state == self::STATE_END && expression[$j] == "[") {
						$state = self::STATE_NEW;
					}
					else {
						break;
					}
				}

				switch ($state) {
					// a new parameter started
					case self::STATE_NEW:
						switch (expression[$j]) {
							case " ":
							case ",":
								break;
							case "[":
								$level++;
								break;
							case "]":
								$level--;
								$state = self::STATE_END;
								break;
							case "\"":
								$state = self::STATE_QUOTED;
								break;
							default:
								$state = self::STATE_UNQUOTED;
						}
						break;
					// end of parameter
					case self::STATE_END:
						switch (expression[$j]) {
							case " ":
								break;
							case ",":
								$state = self::STATE_NEW;
								break;
							case "]":
								$level--;
								break;
							default:
								return null;
						}
						break;
					// an unquoted parameter
					case self::STATE_UNQUOTED:
						switch (expression[$j]) {
							case "]":
								$level--;
								$state = self::STATE_END;
								break;
							case ",":
								$state = self::STATE_NEW;
								break;
						}
						break;
					// a quoted parameter
					case self::STATE_QUOTED:
						switch (expression[$j]) {
							case "\"":
								if (expression[$j - 1] != "\\") {
									$state = self::STATE_END;
								}
								break;
						}
						break;
				}
				$j++;
			}

			if ($level != 0) {
				return null;
			}
		}

		// is key empty?
		if ($pos == $j) {
			return null;
		}

		$item = substr(expression, $pos, $j - $pos);
		$pos = $j;
		return $item;
	}

	/**
	 * Parses an function in a trigger function macro constant and moves a position ($pos) on a next symbol after the function
	 *
	 * Returns an array if parsed successfully or null otherwise
	 * Returned array contains two elements:
	 *   0 => function name like \"last(0)\"
	 *   1 => array of parsed function parameters
	 *
	 * @return array
	 */
	private function parseFunction(&$pos)
	{
		$j = $pos;

		while (isset(expression[$j]) && isFunctionChar(expression[$j])) {
			$j++;
		}

		// is function empty?
		if ($pos == $j) {
			return null;
		}

		if (!isset(expression[$j]) || expression[$j++] != "(") {
			return null;
		}

		$state = self::STATE_NEW;
		$num = 0;
		$functionParamList = CArray.array();
		$functionParamList[$num] = "";

		while (isset(expression[$j])) {
			switch ($state) {
				// a new parameter started
				case self::STATE_NEW:
					switch (expression[$j]) {
						case " ":
							break;
						case ",":
							$functionParamList[++$num] = "";
							break;
						case ")":
							// end of parameters
							break 3;
						case "\"":
							$state = self::STATE_QUOTED;
							break;
						default:
							$functionParamList[$num] .= expression[$j];
							$state = self::STATE_UNQUOTED;
					}
					break;
				// end of parameter
				case self::STATE_END:
					switch (expression[$j]) {
						case " ":
							break;
						case ",":
							$functionParamList[++$num] = "";
							$state = self::STATE_NEW;
							break;
						case ")":
							// end of parameters
							break 3;
						default:
							return null;
					}
					break;
				// an unquoted parameter
				case self::STATE_UNQUOTED:
					switch (expression[$j]) {
						case ")":
							// end of parameters
							break 3;
						case ",":
							$functionParamList[++$num] = "";
							$state = self::STATE_NEW;
							break;
						default:
							$functionParamList[$num] .= expression[$j];
					}
					break;
				// a quoted parameter
				case self::STATE_QUOTED:
					switch (expression[$j]) {
						case "\"":
							$state = self::STATE_END;
							break;
						case "\\":
							if (isset(expression[$j + 1]) && expression[$j + 1] == "\"") {
								$j++;
							}
							// break; is not missing here
						default:
							$functionParamList[$num] .= expression[$j];
							break;
					}
					break;
			}
			$j++;
		}

		if (!isset(expression[$j]) || expression[$j++] != ")") {
			return null;
		}

		$function = substr(expression, $pos, $j - $pos);
		$pos = $j;
		return CArray.array($function, $functionParamList);
	}

	/**
	 * Parses a number constant in the trigger expression and
	 * moves a current position (pos) on a last symbol of the number
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseNumber() {
		$j = pos;

		if (expression[$j] < "0" || expression[$j] > "9") {
			return false;
		}

		$j++;
		while (isset(expression[$j]) && expression[$j] >= "0" && expression[$j] <= "9") {
			$j++;
		}

		if (isset(expression[$j]) && expression[$j] == ".") {
			$j++;
			if (!isset(expression[$j]) || expression[$j] < "0" || expression[$j] > "9") {
				return false;
			}

			$j++;
			while (isset(expression[$j]) && expression[$j] >= "0" && expression[$j] <= "9") {
				$j++;
			}
		}

		// check for an optional suffix
		if (isset(expression[$j]) && strpos(ZBX_BYTE_SUFFIXES.ZBX_TIME_SUFFIXES, expression[$j]) !== false) {
			$j++;
		}

		pos = $j - 1;
		return true;
	}

	/**
	 * Parses a macro constant in the trigger expression and
	 * moves a current position (pos) on a last symbol of the macro
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseMacro() {
		$macros = CArray.array("{TRIGGER.VALUE}");

		for($macros as $macro) {
			$len = strlen($macro);

			if (substr(expression, pos, $len) != $macro) {
				continue;
			}

			macros[] = CArray.array("expression" => $macro);
			pos += $len - 1;
			return true;
		}
		return false;
	}

	/**
	 * Parses an user macro constant in the trigger expression and
	 * moves a current position (pos) on a last symbol of the macro
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseUserMacro() {
		$j = pos;

		if (expression[$j++] != "{") {
			return false;
		}

		if (!isset(expression[$j]) || expression[$j++] != "$") {
			return false;
		}

		if (!isset(expression[$j]) || !isMacroChar(expression[$j++])) {
			return false;
		}

		while (isset(expression[$j]) && isMacroChar(expression[$j])) {
			$j++;
		}

		if (!isset(expression[$j]) || expression[$j] != "}") {
			return false;
		}

		$usermacro = substr(expression, pos, $j - pos + 1);
		usermacros[] = CArray.array("expression" => $usermacro);
		pos = $j;
		return true;
	}

	/**
	 * Parses a low-level discovery macro constant in the trigger expression and
	 * moves a current position (pos) on a last symbol of the macro
	 *
	 * @return bool returns true if parsed successfully, false otherwise
	 */
	private function parseLLDMacro() {
		if (!Nest.value(options,"lldmacros").$()) {
			return false;
		}

		$j = pos;

		if (expression[$j++] != "{") {
			return false;
		}

		if (!isset(expression[$j]) || expression[$j++] != "#") {
			return false;
		}

		if (!isset(expression[$j]) || !isMacroChar(expression[$j++])) {
			return false;
		}

		while (isset(expression[$j]) && isMacroChar(expression[$j])) {
			$j++;
		}

		if (!isset(expression[$j]) || expression[$j] != "}") {
			return false;
		}

		$lldmacro = substr(expression, pos, $j - pos + 1);
		lldmacros[] = CArray.array("expression" => $lldmacro);
		pos = $j;
		return true;
	}

	/**
	 * Returns true if the char is allowed in the host name, false otherwise
	 *
	 * @return bool
	 */
	private function isHostChar($c) {
		if (($c >= "a" && $c <= "z") || ($c >= "A" && $c <= "Z") || ($c >= "0" && $c <= "9")
				|| $c == "." || $c == " " || $c == "_" || $c == "-") {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the char is allowed in the item key, false otherwise
	 *
	 * @return bool
	 */
	private function isKeyChar($c) {
		if (($c >= "a" && $c <= "z") || ($c >= "A" && $c <= "Z") || ($c >= "0" && $c <= "9")
				|| $c == "." || $c == "_" || $c == "-") {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the char is allowed in the function name, false otherwise
	 *
	 * @return bool
	 */
	private function isFunctionChar($c) {
		if ($c >= "a" && $c <= "z") {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the char is allowed in the macro, false otherwise
	 *
	 * @return bool
	 */
	private function isMacroChar($c) {
		if (($c >= "A" && $c <= "Z") || $c == "." || $c == "_" || ($c >= "0" && $c <= "9")) {
			return true;
		}

		return false;
	}
}
