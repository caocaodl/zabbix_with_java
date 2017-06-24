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


class Curl {

	private $url;
	protected $reference;
	protected $query;
	protected $arguments = CArray.array();

	public function __construct($url = null) {
		if (empty($url)) {
			formatGetArguments();

			url = basename(Nest.value($_SERVER,"SCRIPT_NAME").$());
		}
		else {
			url = $url;

			// parse reference
			$tmp_pos = zbx_strpos(url, "#");
			if ($tmp_pos !== false) {
				reference = zbx_substring(url, $tmp_pos + 1);
				url = zbx_substring(url, 0, $tmp_pos);
			}

			$tmp_pos = zbx_strpos($url, "?");
			// parse query
			if ($tmp_pos !== false) {
				query = zbx_substring($url, $tmp_pos + 1);
				url = $url = zbx_substring($url, 0, $tmp_pos);
			}

			formatArguments();
		}

		if (isset(Nest.value($_COOKIE,"zbx_sessionid").$())) {
			setArgument("sid", substr(Nest.value($_COOKIE,"zbx_sessionid").$(), 16, 16));
		}
	}

	public function formatQuery() {
		$query = CArray.array();

		for(arguments as $key => $value) {
			if (is_null($value)) {
				continue;
			}

			if (is_array($value)) {
				for($value as $vkey => $vvalue) {
					if (is_array($vvalue)) {
						continue;
					}

					$query[] = $key."[".$vkey."]=".rawurlencode($vvalue);
				}
			}
			else {
				$query[] = $key."=".rawurlencode($value);
			}
		}
		query = implode("&", $query);
	}

	public function formatGetArguments() {
		arguments = $_GET;
		if (isset(Nest.value($_COOKIE,"zbx_sessionid").$())) {
			setArgument("sid", substr(Nest.value($_COOKIE,"zbx_sessionid").$(), 16, 16));
		}
		formatQuery();
	}

	public function formatArguments($query = null) {
		if ($query === null) {
			$query = query;
		}
		if ($query !== null) {
			$args = explode("&", $query);
			for($args as $id => $arg) {
				if (empty($arg)) {
					continue;
				}

				if (strpos($arg, "=") !== false) {
					list($name, $value) = explode("=", $arg);
					arguments[$name] = isset($value) ? urldecode($value) : "";
				}
				else {
					arguments[$arg] = "";
				}
			}
		}
		formatQuery();
	}
	/**
	 * Return relative url.
	 *
	 * @return string
	 */
	public function getUrl() {
		formatQuery();

		$url = url;
		$url .= query ? "?".query : "";
		$url .= reference ? "#".urlencode(reference) : "";
		return $url;
	}

	public function removeArgument($key) {
		unset(arguments[$key]);
	}

	public function setArgument($key, $value = "") {
		arguments[$key] = $value;
	}

	public function getArgument($key) {
		return isset(arguments[$key]) ? arguments[$key] : null;
	}

	public function setQuery($query) {
		query = $query;
		formatArguments();
		formatQuery();
	}

	public function getQuery() {
		formatQuery();
		return query;
	}

	public function setReference($reference) {
		reference = $reference;
	}

	// returns the reference of $this url, i.e. "bookmark" in the url "http://server/file.html#bookmark"
	public function getReference() {
		return reference;
	}

	public function toString() {
		return getUrl();
	}
}
