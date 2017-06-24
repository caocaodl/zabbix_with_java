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


class CMultiSelect extends CTag {

	/**
	 * @param array Nest.value($options,"objectOptions").$() 	an array of parameters to be added to the request URL
	 *
	 * @see jQuery.multiSelect()
	 */
	public function __construct(array $options = CArray.array()) {
		parent::__construct("div", "yes");
		addClass("multiselect");
		attr("id", zbx_formatDomId(Nest.value($options,"name").$()));

		// url
		$url = new Curl("jsrpc.php");
		$url.setArgument("type", PAGE_TYPE_TEXT_RETURN_JSON);
		$url.setArgument("method", "multiselect.get");
		$url.setArgument("objectName", Nest.value($options,"objectName").$());

		if (!empty(Nest.value($options,"objectOptions").$())) {
			for(Nest.value($options,"objectOptions").$() as $optionName => $optionvalue) {
				$url.setArgument($optionName, $optionvalue);
			}
		}

		$params = CArray.array(
			"id" => getAttribute("id"),
			"url" => $url.getUrl(),
			"name" => Nest.value($options,"name").$(),
			"labels" => CArray.array(
				"No matches found" => _("No matches found"),
				"More matches found..." => _("More matches found..."),
				"type here to search" => _("type here to search"),
				"new" => _("new"),
				"Select" => _("Select")
			),
			"data" => empty(Nest.value($options,"data").$()) ? CArray.array() : zbx_cleanHashes(Nest.value($options,"data").$()),
			"ignored" => isset(Nest.value($options,"ignored").$()) ? Nest.value($options,"ignored").$() : null,
			"defaultValue" => isset(Nest.value($options,"defaultValue").$()) ? Nest.value($options,"defaultValue").$() : null,
			"disabled" => isset(Nest.value($options,"disabled").$()) ? Nest.value($options,"disabled").$() : false,
			"selectedLimit" => isset(Nest.value($options,"selectedLimit").$()) ? Nest.value($options,"selectedLimit").$() : null,
			"addNew" => isset(Nest.value($options,"addNew").$()) ? Nest.value($options,"addNew").$() : false,
			"popup" => CArray.array(
				"parameters" => isset(Nest.value($options,"popup","parameters").$()) ? Nest.value($options,"popup","parameters").$() : null,
				"width" => isset(Nest.value($options,"popup","width").$()) ? Nest.value($options,"popup","width").$() : null,
				"height" => isset(Nest.value($options,"popup","height").$()) ? Nest.value($options,"popup","height").$() : null,
				"buttonClass" => isset(Nest.value($options,"popup","buttonClass").$()) ? Nest.value($options,"popup","buttonClass").$() : null
			)
		);

		zbx_add_post_js("jQuery(\"#".getAttribute("id")."\").multiSelect(".CJs::encodeJson($params).")");
	}
}
