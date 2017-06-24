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


class CJSONrpc {

	const VERSION = "2.0";

	public $json;

	private $_multicall;
	private $_error;
	private $_response;
	private $_error_list;
	private $_zbx2jsonErrors;
	private $_jsonDecoded;

	public function __construct($jsonData) {
		json = new CJSON();
		initErrors();

		_multicall = false;
		_error = false;
		_response = CArray.array();
		_jsonDecoded = json->decode($jsonData, true);

		if (!_jsonDecoded) {
			jsonError(null, "-32700", null, null, true);
			return;
		}

		if (!isset(Nest.value(_jsonDecoded,"jsonrpc").$())) {
			_multicall = true;
		}
		else {
			_jsonDecoded = CArray.array(_jsonDecoded);
		}
	}

	public function execute($encoded = true) {
		for(_jsonDecoded as $call) {
			// notification
			if (!isset(Nest.value($call,"id").$())) {
				Nest.value($call,"id").$() = null;
			}

			if (!validate($call)) {
				continue;
			}

			$params = isset(Nest.value($call,"params").$()) ? Nest.value($call,"params").$() : null;
			$auth = isset(Nest.value($call,"auth").$()) ? Nest.value($call,"auth").$() : null;

			$result = czbxrpc::call(Nest.value($call,"method").$(), $params, $auth);
			processResult($call, $result);
		}

		if (!$encoded) {
			return _response;
		}
		else {
			return json->encode(_response);
		}
	}

	public function validate($call) {
		if (!isset(Nest.value($call,"jsonrpc").$())) {
			jsonError(Nest.value($call,"id").$(), "-32600", _("JSON-rpc version is not specified."), null, true);
			return false;
		}

		if (Nest.value($call,"jsonrpc").$() != self::VERSION) {
			jsonError(Nest.value($call,"id").$(), "-32600", _s("Expecting JSON-rpc version 2.0, \"%s\" is given.", Nest.value($call,"jsonrpc").$()), null, true);
			return false;
		}

		if (!isset(Nest.value($call,"method").$())) {
			jsonError(Nest.value($call,"id").$(), "-32600", _("JSON-rpc method is not defined."));
			return false;
		}

		if (isset(Nest.value($call,"params").$()) && !is_array(Nest.value($call,"params").$())) {
			jsonError(Nest.value($call,"id").$(), "-32602", _("JSON-rpc params is not an Array."));
			return false;
		}

		return true;
	}

	public function processResult($call, $result) {
		if (isset(Nest.value($result,"result").$())) {
			// Notifications MUST NOT be answered
			if (Nest.value($call,"id").$() === null) {
				return;
			}

			$formedResp = CArray.array(
				"jsonrpc" => self::VERSION,
				"result" => Nest.value($result,"result").$(),
				"id" => $call["id"]
			);

			if (_multicall) {
				_response[] = $formedResp;
			}
			else {
				_response = $formedResp;
			}
		}
		else {
			Nest.value($result,"data").$() = isset(Nest.value($result,"data").$()) ? Nest.value($result,"data").$() : null;
			Nest.value($result,"debug").$() = isset(Nest.value($result,"debug").$()) ? Nest.value($result,"debug").$() : null;
			$errno = _zbx2jsonErrors[$result["error"]];

			jsonError(Nest.value($call,"id").$(), $errno, Nest.value($result,"data").$(), Nest.value($result,"debug").$());
		}
	}

	public function isError() {
		return _error;
	}

	private function jsonError($id, $errno, $data = null, $debug = null, $force_err = false) {
		// Notifications MUST NOT be answered, but error MUST be generated on JSON parse error
		if (is_null($id) && !$force_err) {
			return;
		}

		_error = true;

		if (!isset(_error_list[$errno])) {
			$data = _s("JSON-rpc error generation failed. No such error \"%s\".", $errno);
			$errno = "-32400";
		}

		$error = _error_list[$errno];

		if (!is_null($data)) {
			Nest.value($error,"data").$() = $data;
		}
		if (!is_null($debug)) {
			Nest.value($error,"debug").$() = $debug;
		}


		$formed_error = CArray.array(
			"jsonrpc" => self::VERSION,
			"error" => $error,
			"id" => $id
		);

		if (_multicall) {
			_response[] = $formed_error;
		}
		else {
			_response = $formed_error;
		}
	}

	private function initErrors() {
		_error_list = CArray.array(
			"-32700" => CArray.array(
				"code" => -32700,
				"message" => _("Parse error"),
				"data" => _("Invalid JSON. An error occurred on the server while parsing the JSON text.")
			),
			"-32600" => CArray.array(
				"code" => -32600,
				"message" => _("Invalid Request."),
				"data" => _("The received JSON is not a valid JSON-RPC Request.")
			),
			"-32601" => CArray.array(
				"code" => -32601,
				"message" => _("Method not found."),
				"data" => _("The requested remote-procedure does not exist / is not available")
			),
			"-32602" => CArray.array(
				"code" => -32602,
				"message" => _("Invalid params."),
				"data" => _("Invalid method parameters.")
			),
			"-32603" => CArray.array(
				"code" => -32603,
				"message" => _("Internal error."),
				"data" => _("Internal JSON-RPC error.")
			),
			"-32500" => CArray.array(
				"code" => -32500,
				"message" => _("Application error."),
				"data" => _("No details")
			),
			"-32400" => CArray.array(
				"code" => -32400,
				"message" => _("System error."),
				"data" => _("No details")
			),
			"-32300" => CArray.array(
				"code" => -32300,
				"message" => _("Transport error."),
				"data" => _("No details")
			)
		);

		_zbx2jsonErrors = CArray.array(
			ZBX_API_ERROR_NO_METHOD => "-32601",
			ZBX_API_ERROR_PARAMETERS => "-32602",
			ZBX_API_ERROR_NO_AUTH => "-32602",
			ZBX_API_ERROR_PERMISSIONS => "-32500",
			ZBX_API_ERROR_INTERNAL => "-32500"
		);
	}
}
