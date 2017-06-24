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

class CVisibilityBox extends CCheckBox {

	public function __construct($name = "visibilitybox", $value = "yes", $object_name = null, $replace_to = null) {
		if (!is_array($object_name)) {
			$object_name = CArray.array($object_name);
		}
		object_name = $object_name;
		replace_to = unpack_object($replace_to);

		$action = "";
		for(object_name as $obj_name) {
			if (empty($obj_name)) {
				continue;
			}
			$action .= "visibility_status_changeds(this.checked, ".zbx_jsvalue($obj_name).", ".zbx_jsvalue(replace_to).");";
		}
		parent::__construct($name, $value, $action, 1);
		insert_javascript_for_visibilitybox();
	}

	public function toString($destroy = true) {
		if (!isset(Nest.value(attributes,"checked").$())) {
			for(object_name as $obj_name) {
				if (empty($obj_name)) {
					continue;
				}
				zbx_add_post_js("visibility_status_changeds(false, ".zbx_jsvalue($obj_name).", ".zbx_jsvalue(replace_to).");");
			}
		}
		return parent::toString($destroy);
	}
}
?>
