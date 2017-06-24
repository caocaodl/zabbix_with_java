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


class CTag extends CObject {

	/**
	 * Encodes the "<", ">", "\"" and "&" symbols.
	 */
	const ENC_ALL = 1;

	/**
	 * Encodes all symbols in ENC_ALL except for "&".
	 */
	const ENC_NOAMP = 2;

	/**
	 * The HTML encoding strategy to use for the contents of the tag.
	 *
	 * @var int
	 */
	protected $encStrategy = self::ENC_NOAMP;

	/**
	 * The HTML encoding strategy for the \"value\", \"name\" and \"id\" attributes.
	 *
	 * @var int
	 */
	protected $attrEncStrategy = self::ENC_ALL;

	public function __construct($tagname = null, $paired = "no", $body = null, $class = null) {
		parent::__construct();
		attributes = CArray.array();
		dataAttributes = CArray.array();

		if (!is_string($tagname)) {
			return error("Incorrect tagname for CTag \"".$tagname."\".");
		}

		tagname = $tagname;
		paired = $paired;
		tag_start = tag_end = tag_body_start = tag_body_end = "";

		if (is_null($body)) {
			tag_end = tag_body_start = "";
		}
		else {
			addItem($body);
		}
		addClass($class);
	}

	// do not put new line symbol (\n) before or after html tags, it adds spaces in unwanted places
	public function startToString() {
		$res = tag_start."<".tagname;
		for(attributes as $key => $value) {
			if ($value === null) {
				continue;
			}

			// a special encoding strategy should be used for the \"value\", \"name\" and \"id\" attributes
			$strategy = in_CArray.array($key, CArray.array("value", "name", "id"), true) ? attrEncStrategy : encStrategy;
			$value = encode($value, $strategy);
			$res .= " ".$key."=\"".$value."\"";
		}
		$res .= (paired === "yes") ? ">" : " />";

		return $res;
	}

	public function bodyToString() {
		return tag_body_start.parent::toString(false);
	}

	public function endToString() {
		$res = (paired === "yes") ? tag_body_end."</".tagname.">" : "";
		$res .= tag_end;

		return $res;
	}

	public function toString($destroy = true) {
		$res = startToString();
		$res .= bodyToString();
		$res .= endToString();
		if ($destroy) {
			destroy();
		}

		return $res;
	}

	public function addItem($value) {
		// the string contents of an HTML tag should be properly encoded
		if (is_string($value)) {
			$value = encode($value, getEncStrategy());
		}

		parent::addItem($value);
	}

	public function setName($value) {
		if (is_null($value)) {
			return $value;
		}
		if (!is_string($value)) {
			return error("Incorrect value for SetName \"".$value."\".");
		}

		return setAttribute("name", $value);
	}

	public function getName() {
		if (isset(Nest.value(attributes,"name").$())) {
			return Nest.value(attributes,"name").$();
		}

		return null;
	}

	public function addClass($cssClass) {
		if (!isset(Nest.value(attributes,"class").$()) || zbx_empty(Nest.value(attributes,"class").$())) {
			Nest.value(attributes,"class").$() = $cssClass;
		}
		else {
			Nest.value(attributes,"class").$() .= " ".$cssClass;
		}

		return Nest.value(attributes,"class").$();
	}

	/**
	 * HTML class check for existing, return thue if exist
	 *
	 * @param string $cssClass
	 *
	 * @return bool
	 */
	public function hasClass($cssClass) {
		$chkClass = explode(" ", getAttribute("class"));
		return in_CArray.array($cssClass, $chkClass);
	}

	public function attr($name, $value) {
		if (!is_null($value)) {
			setAttribute($name, $value);
		}
	}

	public function getAttribute($name) {
		return isset(attributes[$name]) ? attributes[$name] : null;
	}

	public function setAttribute($name, $value) {
		if (!is_null($value)) {
			if (is_object($value)) {
				$value = unpack_object($value);
			}
			elseif (is_array($value)) {
				$value = CHtml::serialize($value);
			}
			attributes[$name] = $value;
		}
		else {
			removeAttribute($name);
		}
	}

	/**
	 * Sets multiple HTML attributes.
	 *
	 * @param array $attributes		defined as CArray.array(attributeName1 => value1, attributeName2 => value2, ...)
	 */
	public function setAttributes(array $attributes) {
		for($attributes as $name => $value) {
			setAttribute($name, $value);
		}
	}

	public function removeAttr($name) {
		removeAttribute($name);
	}

	public function removeAttribute($name) {
		unset(attributes[$name]);
	}

	public function addAction($name, $value) {
		attributes[$name] = $value;
	}

	public function setHint($text, $width = "", $class = "", $byClick = true, $encode = true) {
		if (empty($text)) {
			return false;
		}

		encodeValues($text);
		$text = unpack_object($text);

		addAction("onmouseover", "hintBox.HintWraper(event, this, ".zbx_jsvalue($text).", \"".$width."\", \"".$class."\");");
		if ($byClick) {
			addAction("onclick", "hintBox.showStaticHint(event, this, ".zbx_jsvalue($text).", \"".$width."\", \"".$class."\");");
		}

		return true;
	}

	/**
	 * Set data for menu popup.
	 *
	 * @param array $data
	 */
	public function setMenuPopup(array $data) {
		attr("data-menu-popup", $data);
	}

	public function onClick($handleCode) {
		addAction("onclick", $handleCode);
	}

	public function addStyle($value) {
		if (!isset(Nest.value(attributes,"style").$())) {
			Nest.value(attributes,"style").$() = "";
		}
		if (isset($value)) {
			Nest.value(attributes,"style").$() .= htmlspecialchars(strval($value));
		}
		else {
			unset(Nest.value(attributes,"style").$());
		}
	}

	public function setEnabled($value = "yes") {
		if ((is_string($value) && ($value == "yes" || $value == "enabled" || $value == "on") || $value == "1") || (is_int($value) && $value <> 0)) {
			unset(Nest.value(attributes,"disabled").$());
		}
		elseif ((is_string($value) && ($value == "no" || $value == "disabled" || $value == "off") || $value == "0") || (is_int($value) && $value == 0)) {
			Nest.value(attributes,"disabled").$() = "disabled";
		}
	}

	public function error($value) {
		error("class(".get_class($this).") - ".$value);
		return 1;
	}

	public function getForm($method = "post", $action = null, $enctype = null) {
		$form = new CForm($method, $action, $enctype);
		$form.addItem($this);

		return $form;
	}

	public function setTitle($value = "title") {
		setAttribute("title", $value);
	}

	/**
	 * Sanitizes a string according to the given strategy before outputting it to the browser.
	 *
	 * @param string	$value
	 * @param int		$strategy
	 *
	 * @return string
	 */
	protected function encode($value, $strategy = self::ENC_NOAMP) {
		if ($strategy == self::ENC_NOAMP) {
			$value = str_replace(CArray.array("<", ">", "\""), CArray.array("&lt;", "&gt;", "&quot;"), $value);
		}
		else {
			$value = CHtml::encode($value);
		}

		return $value;
	}

	/**
	 * @param int $encStrategy
	 */
	public function setEncStrategy($encStrategy) {
		encStrategy = $encStrategy;
	}

	/**
	 * @return int
	 */
	public function getEncStrategy() {
		return encStrategy;
	}
}
