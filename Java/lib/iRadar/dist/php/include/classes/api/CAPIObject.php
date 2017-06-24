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
?>
<?php

class CAPIObject {
	private $_name;

	public function __construct($name) {
		_name = $name;
	}

	public function __call($method, $params) {
		if (!isset(CWebUser::Nest.value($data,"sessionid").$()))
			CWebUser::Nest.value($data,"sessionid").$() = null;

		$param = empty($params) ? null : reset($params);
		$result = czbxrpc::call(_name.".".$method, $param, CWebUser::Nest.value($data,"sessionid").$());

		// saving API call for the debug statement
		CProfiler::getInstance()->profileApiCall(_name, $method, $params, isset(Nest.value($result,"result").$()) ? Nest.value($result,"result").$() : "");

		if (isset(Nest.value($result,"result").$())) {
			return Nest.value($result,"result").$();
		}
		else {
			$trace = Nest.value($result,"data").$();

			if (isset(Nest.value($result,"debug").$())) {
				$trace .= " [".CProfiler::getInstance().formatCallStack(Nest.value($result,"debug").$())."]";
			}

			error($trace);

			return false;
		}
	}
}
