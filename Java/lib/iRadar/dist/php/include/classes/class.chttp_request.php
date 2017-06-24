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


/**
 * Access the HTTP Request
 */
class CHTTP_request {

	/**
	 * additional HTTP headers not prefixed with HTTP_ in $_SERVER superglobal
	 */
	public $add_headers = CArray.array("CONTENT_TYPE", "CONTENT_LENGTH");

	/**
	 * Retrieve HTTP Body
	 * @param Array Additional Headers to retrieve
	 */
	public function __construct($add_headers = false) {
		retrieve_headers($add_headers);
		body = @file_get_contents("php://input");
	}

	/**
	 * Retrieve the HTTP request headers from the $_SERVER superglobal
	 * @param Array Additional Headers to retrieve
	 */
	public function retrieve_headers($add_headers = false) {
		if ($add_headers) {
			add_headers = array_merge(add_headers, $add_headers);
		}

		if (isset(Nest.value($_SERVER,"HTTP_METHOD").$())) {
			method = Nest.value($_SERVER,"HTTP_METHOD").$();
			unset(Nest.value($_SERVER,"HTTP_METHOD").$());
		}
		else {
			method = isset(Nest.value($_SERVER,"REQUEST_METHOD").$()) ? Nest.value($_SERVER,"REQUEST_METHOD").$() : false;
		}

		protocol = isset(Nest.value($_SERVER,"SERVER_PROTOCOL").$()) ? Nest.value($_SERVER,"SERVER_PROTOCOL").$() : false;
		request_method = isset(Nest.value($_SERVER,"REQUEST_METHOD").$()) ? Nest.value($_SERVER,"REQUEST_METHOD").$() : false;

		headers = CArray.array();
		for($_SERVER as $i => $val) {
			if (zbx_strpos($i, "HTTP_") === 0 || in_CArray.array($i, add_headers)) {
				$name = str_replace(CArray.array("HTTP_", "_"), CArray.array("", "-"), $i);
				headers[$name] = $val;
			}
		}
	}

	/**
	 * Retrieve HTTP Method
	 */
	public function method() {
		return method;
	}

	/**
	 * Retrieve HTTP Body
	 */
	public function body() {
		return body;
	}

	/**
	 * Retrieve an HTTP Header
	 * @param string Case-Insensitive HTTP Header Name (eg: \"User-Agent\")
	 */
	public function header($name) {
		$name = zbx_strtoupper($name);
		return isset(headers[$name]) ? headers[$name] : false;
	}

	/**
	 * Retrieve all HTTP Headers
	 * @return array HTTP Headers
	 */
	public function headers() {
		return headers;
	}

	/**
	 * Return Raw HTTP Request (note: This is incomplete)
	 * @param bool ReBuild the Raw HTTP Request
	 */
	public function raw($refresh = false) {
		if (isset(raw) && !$refresh) {
			return raw; // return cached
		}

		$headers = headers();
		raw = method.\"\r\n\";

		for($headers as $i=>$header) {
			raw .= $i.": ".$header.\"\r\n\";
		}

		raw .= \"\r\n\".body;

		return raw;
	}
}
