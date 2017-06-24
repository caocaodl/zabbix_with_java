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


class CXmlImport18 {

	private static $xml = null;
	private static $ZBX_EXPORT_MAP = CArray.array(
		XML_TAG_HOST => CArray.array(
			"attributes" => CArray.array(
				"host" => "name"
			),
			"elements" => CArray.array(
				"name" => "",
				"proxy_hostid" => "",
				"useip" => "",
				"dns" => "",
				"ip" => "",
				"port" => "",
				"status" => "",
				"useipmi" => "",
				"ipmi_ip" => "",
				"ipmi_port" => "",
				"ipmi_authtype" => "",
				"ipmi_privilege" => "",
				"ipmi_username" => "",
				"ipmi_password" => "",
			)
		),
		XML_TAG_MACRO => CArray.array(
			"attributes" => CArray.array(),
			"elements" => CArray.array(
				"value" => "",
				"macro" => "name"
			)
		),
		XML_TAG_HOSTINVENTORY => CArray.array(
			"attributes" => CArray.array(),
			"elements" => CArray.array(
				"devicetype" => "",
				"name" => "",
				"os" => "",
				"serialno" => "",
				"tag" => "",
				"macaddress" => "",
				"hardware" => "",
				"software" => "",
				"contact" => "",
				"location" => "",
				"notes" => "",
				"device_alias" => "",
				"device_type" => "",
				"device_chassis" => "",
				"device_os" => "",
				"device_os_short" => "",
				"device_hw_arch" => "",
				"device_serial" => "",
				"device_model" => "",
				"device_tag" => "",
				"device_vendor" => "",
				"device_contract" => "",
				"device_who" => "",
				"device_status" => "",
				"device_app_01" => "",
				"device_app_02" => "",
				"device_app_03" => "",
				"device_app_04" => "",
				"device_app_05" => "",
				"device_url_1" => "",
				"device_url_2" => "",
				"device_url_3" => "",
				"device_networks" => "",
				"device_notes" => "",
				"device_hardware" => "",
				"device_software" => "",
				"ip_subnet_mask" => "",
				"ip_router" => "",
				"ip_macaddress" => "",
				"oob_ip" => "",
				"oob_subnet_mask" => "",
				"oob_router" => "",
				"date_hw_buy" => "",
				"date_hw_install" => "",
				"date_hw_expiry" => "",
				"date_hw_decomm" => "",
				"site_street_1" => "",
				"site_street_2" => "",
				"site_street_3" => "",
				"site_city" => "",
				"site_state" => "",
				"site_country" => "",
				"site_zip" => "",
				"site_rack" => "",
				"site_notes" => "",
				"poc_1_name" => "",
				"poc_1_email" => "",
				"poc_1_phone_1" => "",
				"poc_1_phone_2" => "",
				"poc_1_cell" => "",
				"poc_1_screen" => "",
				"poc_1_notes" => "",
				"poc_2_name" => "",
				"poc_2_email" => "",
				"poc_2_phone_1" => "",
				"poc_2_phone_2" => "",
				"poc_2_cell" => "",
				"poc_2_screen" => "",
				"poc_2_notes" => ""
			)
		),
		XML_TAG_DEPENDENCY => CArray.array(
			"attributes" => CArray.array(
				"host_trigger" => "description"
			),
			"elements" => CArray.array(
				"depends" => ""
			)
		),
		XML_TAG_ITEM => CArray.array(
			"attributes" => CArray.array(
				"type" => "",
				"key_" => "key",
				"value_type" => ""
			),
			"elements" => CArray.array(
				"name" => "description",
				"ipmi_sensor" => "",
				"delay" => "",
				"history" => "",
				"trends" => "",
				"status" => "",
				"data_type" => "",
				"units" => "",
				"multiplier" => "",
				"delta" => "",
				"formula" => "",
				"lastlogsize" => "",
				"logtimefmt" => "",
				"delay_flex" => "",
				"authtype" => "",
				"username" => "",
				"password" => "",
				"publickey" => "",
				"privatekey" => "",
				"params" => "",
				"trapper_hosts" => "",
				"snmp_community" => "",
				"snmp_oid" => "",
				"port" => "",
				"snmp_port" => "",
				"snmpv3_securityname" => "",
				"snmpv3_securitylevel" => "",
				"snmpv3_authprotocol" => "",
				"snmpv3_authpassphrase" => "",
				"snmpv3_privprotocol" => "",
				"snmpv3_privpassphrase" => ""
			)
		),
		XML_TAG_TRIGGER => CArray.array(
			"attributes" => CArray.array(),
			"elements" => CArray.array(
				"description" => "",
				"type" => "",
				"expression" => "",
				"url" => "",
				"status" => "",
				"priority" => "",
				"comments" => ""
			)
		),
		XML_TAG_GRAPH => CArray.array(
			"attributes" => CArray.array(
				"name" => "",
				"width" => "",
				"height" => ""
			),
			"elements" => CArray.array(
				"ymin_type" => "",
				"ymax_type" => "",
				"ymin_item_key" => "",
				"ymax_item_key" => "",
				"show_work_period" => "",
				"show_triggers" => "",
				"graphtype" => "",
				"yaxismin" => "",
				"yaxismax" => "",
				"show_legend" => "",
				"show_3d" => "",
				"percent_left" => "",
				"percent_right" => ""
			)
		),
		XML_TAG_GRAPH_ELEMENT => CArray.array(
			"attributes" => CArray.array(
				"host_key_" => "item"
			),
			"elements" => CArray.array(
				"drawtype" => "",
				"sortorder" => "",
				"color" => "",
				"yaxisside" => "",
				"calc_fnc" => "",
				"type" => ""
			)
		)
	);

	private static $oldKeys = CArray.array(
		"tcp",
		"ftp",
		"http",
		"imap",
		"ldap",
		"nntp",
		"ntp",
		"pop",
		"smtp",
		"ssh"
	);
	private static $oldKeysPref = CArray.array(
		"tcp_perf",
		"ftp_perf",
		"http_perf",
		"imap_perf",
		"ldap_perf",
		"nntp_perf",
		"ntp_perf",
		"pop_perf",
		"smtp_perf",
		"ssh_perf"
	);

	protected static function mapInventoryName($name) {
		$map = CArray.array(
			"devicetype" => "type",
			"serialno" => "serialno_a",
			"macaddress" => "macaddress_a",
			"hardware" => "hardware_full",
			"software" => "software_full",
			"device_type" => "type_full",
			"device_alias" => "alias",
			"device_os" => "os_full",
			"device_os_short" => "os_short",
			"device_serial" => "serialno_b",
			"device_tag" => "asset_tag",
			"ip_macaddress" => "macaddress_b",
			"device_hardware" => "hardware",
			"device_software" => "software",
			"device_app_01" => "software_app_a",
			"device_app_02" => "software_app_b",
			"device_app_03" => "software_app_c",
			"device_app_04" => "software_app_d",
			"device_app_05" => "software_app_e",
			"device_chassis" => "chassis",
			"device_model" => "model",
			"device_hw_arch" => "hw_arch",
			"device_vendor" => "vendor",
			"device_contract" => "contract_number",
			"device_who" => "installer_name",
			"device_status" => "deployment_status",
			"device_url_1" => "url_a",
			"device_url_2" => "url_b",
			"device_url_3" => "url_c",
			"device_networks" => "host_networks",
			"ip_subnet_mask" => "host_netmask",
			"ip_router" => "host_router",
			"oob_subnet_mask" => "oob_netmask",
			"date_hw_buy" => "date_hw_purchase",
			"site_street_1" => "site_address_a",
			"site_street_2" => "site_address_b",
			"site_street_3" => "site_address_c",
			"poc_1_phone_1" => "poc_1_phone_a",
			"poc_1_phone_2" => "poc_1_phone_b",
			"poc_2_phone_1" => "poc_2_phone_a",
			"poc_2_phone_2" => "poc_2_phone_b",
			"device_notes" => "notes",
		);

		return isset($map[$name]) ? $map[$name] : $name;
	}

	protected static function createDOMDocument() {
		$doc = new DOMDocument("1.0", "UTF-8");
		$doc.preserveWhiteSpace = false;
		$doc.formatOutput = true;

		$root = $doc.appendChild(new DOMElement("zabbix_export"));
		$root.setAttributeNode(new DOMAttr("version", "1.0"));
		$root.setAttributeNode(new DOMAttr("date", zbx_date2str(XML_DATE_DATE_FORMAT)));
		$root.setAttributeNode(new DOMAttr("time", zbx_date2str(XML_TIME_DATE_FORMAT)));

		return $root;
	}

	/**
	 * Converts Simple key from old format to new.
	 *
	 *
	 * @param mixed $oldKey   Simple key in old format
	 *
	 * @return mixed
	 */
	public static function convertOldSimpleKey($oldKey) {
		$newKey = $oldKey;

		$explodedKey = explode(",", $oldKey);

		if (in_CArray.array($explodedKey[0], self::$oldKeys)) {
			$newKey = "net.tcp.service[".$explodedKey[0].",,".$explodedKey[1]."]";
		}
		elseif (in_CArray.array($explodedKey[0], self::$oldKeysPref)) {
			$keyWithoutPerf = explode("_", $explodedKey[0]);
			$newKey = "net.tcp.service.perf[".$keyWithoutPerf[0].",,".$explodedKey[1]."]";
		}

		return $newKey;
	}

	public static function XMLtoArray($parentNode) {
		$array = CArray.array();

		for($parentNode.childNodes as $node) {
			if ($node.nodeType == 3) {
				if ($node.nextSibling) {
					continue;
				}
				if (!$node.isWhitespaceInElementContent()) {
					return $node.nodeValue;
				}
			}

			if ($node.hasChildNodes()) {
				$nodeName = $node.nodeName;

				if (isset($array[$nodeName])) {
					$nodeName .= count($array);
				}
				$array[$nodeName] = self::XMLtoArray($node);
			}
		}

		return $array;
	}

	private static function mapXML2arr($xml, $tag) {
		$array = CArray.array();

		for(self::$ZBX_EXPORT_MAP[$tag]["attributes"] as $attr => $value) {
			if ($value == "") {
				$value = $attr;
			}

			if ($xml.getAttribute($value) != "") {
				$array[$attr] = $xml.getAttribute($value);
			}
		}

		// fill empty values with key if empty
		$map = self::$ZBX_EXPORT_MAP[$tag]["elements"];
		for($map as $db_name => $xml_name) {
			if ($xml_name == "") {
				$map[$db_name] = $db_name;
			}
			else {
				$map[$xml_name] = $db_name;
			}
		}

		for($xml.childNodes as $node) {
			if (isset($map[$node.nodeName])) {
				$array[$map[$node.nodeName]] = $node.nodeValue;
			}
		}

		return $array;
	}

	public static function import($source) {

		libxml_use_internal_errors(true);
		libxml_disable_entity_loader(true);

		$xml = new DOMDocument();
		if (!$xml.loadXML($source, LIBXML_IMPORT_FLAGS)) {
			$text = "";
			for(libxml_get_errors() as $error) {
				switch ($error.level) {
					case LIBXML_ERR_WARNING:
						$text .= _("XML file contains errors").". Warning ".$error.code.": ";
						break;
					case LIBXML_ERR_ERROR:
						$text .= _("XML file contains errors").". Error ".$error.code.": ";
						break;
					case LIBXML_ERR_FATAL:
						$text .= _("XML file contains errors").". Fatal Error ".$error.code.": ";
						break;
				}

				$text .= trim($error.message)." [ Line: ".$error.line." | Column: ".$error.column." ]";
				break;
			}
			libxml_clear_errors();

			throw new Exception($text);
		}

		if ($xml.childNodes->item(0)->nodeName != "zabbix_export") {
			$xml2 = self::createDOMDocument();
			$xml2.appendChild($xml2.ownerDocument->importNode($xml.childNodes->item(0), true));
			self::$xml = $xml2.ownerDocument;
		}
		else {
			self::$xml = $xml;
		}

		return true;
	}

	public static function parseScreen($rules, $xml = null) {
		$xml = is_null($xml) ? self::$xml : $xml;
		$importScreens = self::XMLtoArray($xml);
		if (!isset(Nest.value($importScreens,"zabbix_export","screens").$())) {
			return true;
		}
		$importScreens = Nest.value($importScreens,"zabbix_export","screens").$();

		$screens = CArray.array();

		for($importScreens as $mnum => &$screen) {
			unset(Nest.value($screen,"screenid").$());
			$exists = API.Screen().exists(CArray.array("name" => Nest.value($screen,"name").$()));

			if ($exists && !empty(Nest.value($rules,"screens","updateExisting").$())) {
				$db_screens = API.Screen().get(CArray.array("filter" => CArray.array("name" => Nest.value($screen,"name").$())));
				if (empty($db_screens)) {
					throw new Exception(_s("No permissions for screen \"%1$s\".", Nest.value($screen,"name").$()));
				}

				$db_screen = reset($db_screens);

				Nest.value($screen,"screenid").$() = Nest.value($db_screen,"screenid").$();
			}
			else if ($exists || empty(Nest.value($rules,"screens","createMissing").$())) {
				info(_s("Screen \"%1$s\" skipped - user rule.", Nest.value($screen,"name").$()));
				unset($importScreens[$mnum]);
				continue; // break if not update exist
			}

			if (!isset(Nest.value($screen,"screenitems").$())) {
				Nest.value($screen,"screenitems").$() = CArray.array();
			}

			for(Nest.value($screen,"screenitems").$() as &$screenitem) {
				$nodeCaption = isset(Nest.value($screenitem,"resourceid","node").$()) ? $screenitem["resourceid"]["node"].":" : "";

				if (!isset(Nest.value($screenitem,"resourceid").$())) {
					Nest.value($screenitem,"resourceid").$() = 0;
				}
				if (is_array(Nest.value($screenitem,"resourceid").$())) {
					switch (Nest.value($screenitem,"resourcetype").$()) {
						case SCREEN_RESOURCE_HOSTS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
						case SCREEN_RESOURCE_DATA_OVERVIEW:
						case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
							if (is_array(Nest.value($screenitem,"resourceid").$())) {
								$db_hostgroups = API.HostGroup().getObjects(Nest.value($screenitem,"resourceid").$());
								if (empty($db_hostgroups)) {
									$error = _s("Cannot find group \"%1$s\" used in screen \"%2$s\".",
											$nodeCaption.Nest.value($screenitem,"resourceid","name").$(), Nest.value($screen,"name").$());
									throw new Exception($error);
								}

								$tmp = reset($db_hostgroups);
								Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"groupid").$();
							}
							break;
						case SCREEN_RESOURCE_HOST_TRIGGERS:
							$db_hosts = API.Host().getObjects(Nest.value($screenitem,"resourceid").$());
							if (empty($db_hosts)) {
								$error = _s("Cannot find host \"%1$s\" used in screen \"%2$s\".",
										$nodeCaption.Nest.value($screenitem,"resourceid","host").$(), Nest.value($screen,"name").$());
								throw new Exception($error);
							}

							$tmp = reset($db_hosts);
							Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"hostid").$();
							break;
						case SCREEN_RESOURCE_GRAPH:
							$db_graphs = API.Graph().getObjects(Nest.value($screenitem,"resourceid").$());
							if (empty($db_graphs)) {
								$error = _s("Cannot find graph \"%1$s\" used in screen \"%2$s\".",
										$nodeCaption.$screenitem["resourceid"]["host"].NAME_DELIMITER.Nest.value($screenitem,"resourceid","name").$(), Nest.value($screen,"name").$());
								throw new Exception($error);
							}

							$tmp = reset($db_graphs);
							Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"graphid").$();
							break;
						case SCREEN_RESOURCE_SIMPLE_GRAPH:
						case SCREEN_RESOURCE_PLAIN_TEXT:
							$db_items = API.Item().getObjects(Nest.value($screenitem,"resourceid").$());

							if (empty($db_items)) {
								$error = _s("Cannot find item \"%1$s\" used in screen \"%2$s\".",
										$nodeCaption.$screenitem["resourceid"]["host"].":".Nest.value($screenitem,"resourceid","key_").$(), Nest.value($screen,"name").$());
								throw new Exception($error);
							}

							$tmp = reset($db_items);
							Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"itemid").$();
							break;
						case SCREEN_RESOURCE_MAP:
							$db_sysmaps = API.Map().getObjects(Nest.value($screenitem,"resourceid").$());
							if (empty($db_sysmaps)) {
								$error = _s("Cannot find map \"%1$s\" used in screen \"%2$s\".",
										$nodeCaption.Nest.value($screenitem,"resourceid","name").$(), Nest.value($screen,"name").$());
								throw new Exception($error);
							}

							$tmp = reset($db_sysmaps);
							Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"sysmapid").$();
							break;
						case SCREEN_RESOURCE_SCREEN:
							$db_screens = API.Screen().get(CArray.array("screenids" => Nest.value($screenitem,"resourceid").$()));
							if (empty($db_screens)) {
								$error = _s("Cannot find screen \"%1$s\" used in screen \"%2$s\".",
										$nodeCaption.Nest.value($screenitem,"resourceid","name").$(), Nest.value($screen,"name").$());
								throw new Exception($error);
							}

							$tmp = reset($db_screens);
							Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"screenid").$();
							break;
						default:
							Nest.value($screenitem,"resourceid").$() = 0;
							break;
					}
				}
			}
			unset($screenitem);

			$screens[] = $screen;
		}
		unset($screen);

		$importScreens = $screens;

		for($importScreens as $screen) {
			if (isset(Nest.value($screen,"screenid").$())) {
				API.Screen()->update($screen);
			}
			else {
				API.Screen()->create($screen);
			}

			if (isset(Nest.value($screen,"screenid").$())) {
				info(_s("Screen \"%1$s\" updated.", Nest.value($screen,"name").$()));
			}
			else {
				info(_s("Screen \"%1$s\" added.", Nest.value($screen,"name").$()));
			}

		}
	}

	public static function parseMap($rules) {
		$importMaps = self::XMLtoArray(self::$xml);

		if (!isset(Nest.value($importMaps,"zabbix_export").$())) {
			Nest.value($importMaps,"zabbix_export").$() = $importMaps;
		}

		if (CWebUser::Nest.value($data,"type").$() == USER_TYPE_SUPER_ADMIN && isset(Nest.value($importMaps,"zabbix_export","images").$())) {
			$images = Nest.value($importMaps,"zabbix_export","images").$();
			$images_to_add = CArray.array();
			$images_to_update = CArray.array();
			for($images as $image) {
				if (API.Image()->exists($image)) {
					if (((Nest.value($image,"imagetype").$() == IMAGE_TYPE_ICON) && !empty(Nest.value($rules,"images","updateExisting").$()))
							|| ((Nest.value($image,"imagetype").$() == IMAGE_TYPE_BACKGROUND) && (!empty(Nest.value($rules,"images","updateExisting").$())))
					) {
						$imgs = API.Image().get(CArray.array(
							"output" => CArray.array("imageid"),
							"filter" => CArray.array("name" => Nest.value($image,"name").$())
						));
						$img = reset($imgs);

						Nest.value($image,"imageid").$() = Nest.value($img,"imageid").$();

						// image will be decoded in class.image.php
						Nest.value($image,"image").$() = Nest.value($image,"encodedImage").$();
						unset(Nest.value($image,"encodedImage").$());

						$images_to_update[] = $image;
					}
				}
				else {
					if (((Nest.value($image,"imagetype").$() == IMAGE_TYPE_ICON) && !empty(Nest.value($rules,"images","createMissing").$()))
							|| ((Nest.value($image,"imagetype").$() == IMAGE_TYPE_BACKGROUND) && !empty(Nest.value($rules,"images","createMissing").$()))
					) {

						// No need to decode_base64
						Nest.value($image,"image").$() = Nest.value($image,"encodedImage").$();

						unset(Nest.value($image,"encodedImage").$());
						$images_to_add[] = $image;
					}
				}
			}

			if (!empty($images_to_add)) {
				$result = API.Image().create($images_to_add);
				if (!$result) {
					throw new Exception(_("Cannot add image."));
				}
			}

			if (!empty($images_to_update)) {
				$result = API.Image().update($images_to_update);
				if (!$result) {
					throw new Exception(_("Cannot update image."));
				}
			}
		}


		if (!isset(Nest.value($importMaps,"zabbix_export","sysmaps").$())) {
			return true;
		}
		$importMaps = Nest.value($importMaps,"zabbix_export","sysmaps").$();
		for($importMaps as $mnum => &$sysmap) {
			unset(Nest.value($sysmap,"sysmapid").$());
			$exists = API.Map().exists(CArray.array("name" => Nest.value($sysmap,"name").$()));

			if (!isset(Nest.value($sysmap,"label_format").$())) {
				Nest.value($sysmap,"label_format").$() = SYSMAP_LABEL_ADVANCED_OFF;
			}

			if ($exists && !empty(Nest.value($rules,"maps","updateExisting").$())) {
				$db_maps = API.Map().getObjects(CArray.array("name" => Nest.value($sysmap,"name").$()));
				if (empty($db_maps)) {
					throw new Exception(_s("No permissions for map \"%1$s\".", Nest.value($sysmap,"name").$()));
				}

				$db_map = reset($db_maps);
				Nest.value($sysmap,"sysmapid").$() = Nest.value($db_map,"sysmapid").$();
			}
			else if ($exists || empty(Nest.value($rules,"maps","createMissing").$())) {
				info(_s("Map \"%1$s\" skipped - user rule.", Nest.value($sysmap,"name").$()));
				unset($importMaps[$mnum]);
				continue; // break if not update updateExisting
			}

			if (isset(Nest.value($sysmap,"backgroundid").$())) {
				$image = getImageByIdent(Nest.value($sysmap,"backgroundid").$());

				if (!$image) {
					error(_s("Cannot find background image \"%1$s\" used in map \"%2$s\".",
						Nest.value($sysmap,"backgroundid","name").$(), Nest.value($sysmap,"name").$()));
					Nest.value($sysmap,"backgroundid").$() = 0;
				}
				else {
					Nest.value($sysmap,"backgroundid").$() = Nest.value($image,"imageid").$();
				}
			}
			else {
				Nest.value($sysmap,"backgroundid").$() = 0;
			}

			if (!isset(Nest.value($sysmap,"selements").$())) {
				Nest.value($sysmap,"selements").$() = CArray.array();
			}
			else {
				Nest.value($sysmap,"selements").$() = array_values(Nest.value($sysmap,"selements").$());
			}

			if (!isset(Nest.value($sysmap,"links").$())) {
				Nest.value($sysmap,"links").$() = CArray.array();
			}
			else {
				Nest.value($sysmap,"links").$() = array_values(Nest.value($sysmap,"links").$());
			}

			for(Nest.value($sysmap,"selements").$() as &$selement) {
				$nodeCaption = isset(Nest.value($selement,"elementid","node").$()) ? $selement["elementid"]["node"].":" : "";

				if (!isset(Nest.value($selement,"elementid").$())) {
					Nest.value($selement,"elementid").$() = 0;
				}
				switch (Nest.value($selement,"elementtype").$()) {
					case SYSMAP_ELEMENT_TYPE_MAP:
						$db_sysmaps = API.Map().getObjects(Nest.value($selement,"elementid").$());
						if (empty($db_sysmaps)) {
							$error = _s("Cannot find map \"%1$s\" used in exported map \"%2$s\".",
									$nodeCaption.Nest.value($selement,"elementid","name").$(), Nest.value($sysmap,"name").$());
							throw new Exception($error);
						}

						$tmp = reset($db_sysmaps);
						Nest.value($selement,"elementid").$() = Nest.value($tmp,"sysmapid").$();
						break;
					case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
						$db_hostgroups = API.HostGroup().getObjects(Nest.value($selement,"elementid").$());
						if (empty($db_hostgroups)) {
							$error = _s("Cannot find group \"%1$s\" used in map \"%2$s\".",
									$nodeCaption.Nest.value($selement,"elementid","name").$(), Nest.value($sysmap,"name").$());
							throw new Exception($error);
						}

						$tmp = reset($db_hostgroups);
						Nest.value($selement,"elementid").$() = Nest.value($tmp,"groupid").$();
						break;
					case SYSMAP_ELEMENT_TYPE_HOST:
						$db_hosts = API.Host().getObjects(Nest.value($selement,"elementid").$());
						if (empty($db_hosts)) {
							$error = _s("Cannot find host \"%1$s\" used in map \"%2$s\".",
									$nodeCaption.Nest.value($selement,"elementid","host").$(), Nest.value($sysmap,"name").$());
							throw new Exception($error);
						}

						$tmp = reset($db_hosts);
						Nest.value($selement,"elementid").$() = Nest.value($tmp,"hostid").$();
						break;
					case SYSMAP_ELEMENT_TYPE_TRIGGER:
						$db_triggers = API.Trigger().getObjects(Nest.value($selement,"elementid").$());
						if (empty($db_triggers)) {
							$error = _s("Cannot find trigger \"%1$s\" used in map \"%2$s\".",
									$nodeCaption.$selement["elementid"]["host"].":".Nest.value($selement,"elementid","description").$(), Nest.value($sysmap,"name").$());
							throw new Exception($error);
						}

						$tmp = reset($db_triggers);
						Nest.value($selement,"elementid").$() = Nest.value($tmp,"triggerid").$();
						break;
					case SYSMAP_ELEMENT_TYPE_IMAGE:
					default:
				}

				$icons = CArray.array(
					"iconid_off",
					"iconid_on",
					"iconid_disabled",
					"iconid_maintenance"
				);
				for($icons as $icon) {
					if (isset($selement[$icon])) {
						$image = getImageByIdent($selement[$icon]);
						if (!$image) {
							$error = _s("Cannot find icon \"%1$s\" used in map \"%2$s\".", $selement[$icon]["name"], Nest.value($sysmap,"name").$());
							throw new Exception($error);
						}
						$selement[$icon] = Nest.value($image,"imageid").$();
					}
					else {
						$selement[$icon] = 0;
					}
				}
			}
			unset($selement);

			for(Nest.value($sysmap,"links").$() as &$link) {
				if (!isset(Nest.value($link,"linktriggers").$())) {
					continue;
				}

				for(Nest.value($link,"linktriggers").$() as &$linktrigger) {
					$db_triggers = API.Trigger().getObjects(Nest.value($linktrigger,"triggerid").$());
					if (empty($db_triggers)) {
						$nodeCaption = isset(Nest.value($linktrigger,"triggerid","node").$()) ? $linktrigger["triggerid"]["node"].":" : "";
						$error = _s("Cannot find trigger \"%1$s\" used in map \"%2$s\".",
								$nodeCaption.$linktrigger["triggerid"]["host"].":".Nest.value($linktrigger,"triggerid","description").$(), Nest.value($sysmap,"name").$());
						throw new Exception($error);
					}

					$tmp = reset($db_triggers);
					Nest.value($linktrigger,"triggerid").$() = Nest.value($tmp,"triggerid").$();
				}
				unset($linktrigger);
			}
			unset($link);
		}
		unset($sysmap);


		for($importMaps as $importMap) {
			if (isset(Nest.value($importMap,"sysmapid").$())) {
				$result = API.Map().update($importMap);
				if ($result === false) {
					throw new Exception(_s("Cannot update map \"%s\".", Nest.value($importMap,"name").$()));
				}
				else {
					info(_s("Map \"%s\" updated.", Nest.value($importMap,"name").$()));
				}
			}
			else {
				$result = API.Map().create($importMap);
				if ($result === false) {
					throw new Exception(_s("Cannot create map \"%s\".", Nest.value($importMap,"name").$()));
				}
				else {
					info(_s("Map \"%s\" created.", Nest.value($importMap,"name").$()));
				}
			}
		}

		return true;
	}

	public static function parseMain($rules) {
		$triggersForDependencies = CArray.array();

		if (!empty(Nest.value($rules,"hosts","updateExisting").$())
				|| !empty(Nest.value($rules,"hosts","createMissing").$())
				|| !empty(Nest.value($rules,"templates","createMissing").$())
				|| !empty(Nest.value($rules,"templates","updateExisting").$())
		) {
			$xpath = new DOMXPath(self::$xml);

			$hosts = $xpath.query("hosts/host");

			for($hosts as $host) {
				$host_db = self::mapXML2arr($host, XML_TAG_HOST);

				if (!isset(Nest.value($host_db,"status").$())) {
					Nest.value($host_db,"status").$() = HOST_STATUS_TEMPLATE;
				}
				$current_host = (Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE)
						? API.Template()->exists($host_db)
						: API.Host()->exists($host_db);


				if (!$current_host
						&& ((Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE && empty(Nest.value($rules,"templates","createMissing").$()))
								|| (Nest.value($host_db,"status").$() != HOST_STATUS_TEMPLATE && empty(Nest.value($rules,"hosts","createMissing").$())))
				) {
					continue;
				}

				if ($current_host
						&& ((Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE && empty(Nest.value($rules,"templates","updateExisting").$()))
								|| (Nest.value($host_db,"status").$() != HOST_STATUS_TEMPLATE && empty(Nest.value($rules,"hosts","updateExisting").$())))
				) {
					continue;
				}

				// there were no host visible names in 1.8
				if (!isset(Nest.value($host_db,"name").$())) {
					Nest.value($host_db,"name").$() = Nest.value($host_db,"host").$();
				}

				// host will have no interfaces - we will be creating them separately
				Nest.value($host_db,"interfaces").$() = null;

				// it is possible, that data is imported from 1.8, where there was only one network interface per host
				/**
				 * @todo when new XML format will be introduced, this check should be changed to XML version check
				 */
				$old_version_input = Nest.value($host_db,"status").$() != HOST_STATUS_TEMPLATE;
				if ($old_version_input) {
					// rearranging host structure, so it would look more like 2.0 host
					$interfaces = CArray.array();

					// the main interface is always \"agent\" type
					if (!is_null(Nest.value($host_db,"ip").$())) {
						$interfaces[] = CArray.array(
							"main" => INTERFACE_PRIMARY,
							"type" => INTERFACE_TYPE_AGENT,
							"useip" => Nest.value($host_db,"useip").$(),
							"ip" => Nest.value($host_db,"ip").$(),
							"dns" => Nest.value($host_db,"dns").$(),
							"port" => $host_db["port"]
						);
					}

					// now we need to check if host had SNMP items. If it had, we need an SNMP interface for every different port.
					$items = $xpath.query("items/item", $host);
					$snmp_interface_ports_created = CArray.array();
					for($items as $item) {
						$item_db = self::mapXML2arr($item, XML_TAG_ITEM);
						if ((Nest.value($item_db,"type").$() == ITEM_TYPE_SNMPV1
								|| Nest.value($item_db,"type").$() == ITEM_TYPE_SNMPV2C
								|| Nest.value($item_db,"type").$() == ITEM_TYPE_SNMPV3)
								&& !isset($snmp_interface_ports_created[$item_db["snmp_port"]])
						) {

							$interfaces[] = CArray.array(
								"main" => INTERFACE_PRIMARY,
								"type" => INTERFACE_TYPE_SNMP,
								"useip" => Nest.value($host_db,"useip").$(),
								"ip" => Nest.value($host_db,"ip").$(),
								"dns" => Nest.value($host_db,"dns").$(),
								"port" => $item_db["snmp_port"]
							);
							$snmp_interface_ports_created[$item_db["snmp_port"]] = 1;
						}
					}
					unset($snmp_interface_ports_created); // it was a temporary variable


					// we need to add ipmi interface if at least one ipmi item exists
					for($items as $item) {
						$item_db = self::mapXML2arr($item, XML_TAG_ITEM);
						if (Nest.value($item_db,"type").$() == ITEM_TYPE_IPMI) {
							// when saving a host in 1.8, it's possible to set useipmi=1 and not to fill an IP address
							// we were not really sure what to do with this host,
							// and decided to take host IP address instead and show info message about this
							if (Nest.value($host_db,"ipmi_ip").$() == "") {
								$ipmi_ip = Nest.value($host_db,"ip").$();
								info(_s("Host \"%s\" has \"useipmi\" parameter checked, but has no \"ipmi_ip\" parameter! Using host IP address as an address for IPMI interface.", Nest.value($host_db,"host").$()));
							}
							else {
								$ipmi_ip = Nest.value($host_db,"ipmi_ip").$();
							}
							$interfaces[] = CArray.array(
								"main" => INTERFACE_PRIMARY,
								"type" => INTERFACE_TYPE_IPMI,
								"useip" => INTERFACE_USE_DNS,
								"ip" => "",
								"dns" => $ipmi_ip,
								"port" => $host_db["ipmi_port"]
							);

							// we need only one ipmi interface
							break;
						}
					}
				}

				if ($current_host) {
					$options = CArray.array(
						"filter" => CArray.array("host" => Nest.value($host_db,"host").$()),
						"output" => API_OUTPUT_EXTEND,
						"editable" => 1,
						"selectInterfaces" => API_OUTPUT_EXTEND
					);
					if (Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE) {
						$current_host = API.Template().get($options);
					}
					else {
						$current_host = API.Host().get($options);
					}

					if (empty($current_host)) {
						throw new Exception(_s("No permission for host \"%1$s\".", Nest.value($host_db,"host").$()));
					}
					else {
						$current_host = reset($current_host);
					}


					// checking if host already exists - then some of the interfaces may not need to be created
					if (Nest.value($host_db,"status").$() != HOST_STATUS_TEMPLATE) {
						// for every interface we got based on XML
						for($interfaces as $i => $interface_db) {
							// checking every interface of current host
							for(Nest.value($current_host,"interfaces").$() as $interface) {
								// if all parameters of interface are identical
								if (
									Nest.value($interface,"type").$() == $interface_db["type"]
									&& Nest.value($interface,"ip").$() == $interface_db["ip"]
									&& Nest.value($interface,"dns").$() == $interface_db["dns"]
									&& Nest.value($interface,"port").$() == $interface_db["port"]
									&& Nest.value($interface,"useip").$() == $interface_db["useip"]
								) {
									// this interface is the same as existing one!
									$interfaces[$i]["interfaceid"] = Nest.value($interface,"interfaceid").$();
									break;
								}
							}
						}

					}
					$interfaces_created_with_host = false;
				}
				else {
					if (Nest.value($host_db,"status").$() != HOST_STATUS_TEMPLATE) {
						Nest.value($host_db,"interfaces").$() = $interfaces;
						$interfaces_created_with_host = true;
					}
				}

// HOST GROUPS {{{
				$groups = $xpath.query("groups/group", $host);

				Nest.value($host_db,"groups").$() = CArray.array();
				$groups_to_parse = CArray.array();
				for($groups as $group) {
					$groups_to_parse[] = CArray.array("name" => $group.nodeValue);
				}
				if (empty($groups_to_parse)) {
					$groups_to_parse[] = CArray.array("name" => ZBX_DEFAULT_IMPORT_HOST_GROUP);
				}

				for($groups_to_parse as $group) {
					$current_group = API.HostGroup().exists($group);

					if ($current_group) {
						$options = CArray.array(
							"filter" => $group,
							"output" => API_OUTPUT_EXTEND,
							"editable" => 1
						);
						$current_group = API.HostGroup().get($options);
						if (empty($current_group)) {
							throw new Exception(_s("No permissions for group \"%1$s\".", Nest.value($group,"name").$()));
						}

						$host_db["groups"][] = reset($current_group);
					}
					else {
						$result = API.HostGroup().create($group);
						if (!$result) {
							throw new Exception();
						}

						$options = CArray.array(
							"groupids" => Nest.value($result,"groupids").$(),
							"output" => API_OUTPUT_EXTEND
						);
						$new_group = API.HostGroup().get($options);

						$host_db["groups"][] = reset($new_group);
					}
				}
// }}} HOST GROUPS


// MACROS
				$macros = $xpath.query("macros/macro", $host);
				if ($macros.length > 0) {
					Nest.value($host_db,"macros").$() = CArray.array();
					for($macros as $macro) {
						$host_db["macros"][] = self::mapXML2arr($macro, XML_TAG_MACRO);
					}
				}
// }}} MACROS

				// host inventory
				if ($old_version_input) {
					if (!isset(Nest.value($host_db,"inventory").$())) {
						Nest.value($host_db,"inventory").$() = CArray.array();
					}

					$inventoryNode = $xpath.query("host_profile/*", $host);
					if ($inventoryNode.length > 0) {
						for($inventoryNode as $field) {
							$newInventoryName = self::mapInventoryName($field.nodeName);
							$host_db["inventory"][$newInventoryName] = $field.nodeValue;
						}
					}

					$inventoryNodeExt = $xpath.query("host_profiles_ext/*", $host);
					if ($inventoryNodeExt.length > 0) {
						for($inventoryNodeExt as $field) {
							$newInventoryName = self::mapInventoryName($field.nodeName);
							if (isset($host_db["inventory"][$newInventoryName]) && $field.nodeValue !== "") {
								$host_db["inventory"][$newInventoryName] .= \"\r\n\r\n\";
								$host_db["inventory"][$newInventoryName] .= $field.nodeValue;
							}
							else {
								$host_db["inventory"][$newInventoryName] = $field.nodeValue;
							}
						}
					}

					Nest.value($host_db,"inventory_mode").$() = isset(Nest.value($host_db,"inventory").$()) ? HOST_INVENTORY_MANUAL : HOST_INVENTORY_DISABLED;
				}

// HOSTS
				if (isset(Nest.value($host_db,"proxy_hostid").$())) {
					$proxy_exists = API.Proxy().get(CArray.array("proxyids" => Nest.value($host_db,"proxy_hostid").$()));
					if (empty($proxy_exists)) {
						Nest.value($host_db,"proxy_hostid").$() = 0;
					}
				}

				if ($current_host && (!empty(Nest.value($rules,"hosts","updateExisting").$()) || !empty(Nest.value($rules,"templates","updateExisting").$()))) {
					if (Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE) {
						Nest.value($host_db,"templateid").$() = Nest.value($current_host,"templateid").$();
						$result = API.Template().update($host_db);
						$current_hostid = Nest.value($current_host,"templateid").$();
					}
					else {
						Nest.value($host_db,"hostid").$() = Nest.value($current_host,"hostid").$();
						$result = API.Host().update($host_db);
						$current_hostid = Nest.value($current_host,"hostid").$();
					}
					if (!$result) {
						throw new Exception();
					}
				}
				if (!$current_host && (!empty(Nest.value($rules,"hosts","createMissing").$()) || !empty(Nest.value($rules,"templates","createMissing").$()))) {

					if (Nest.value($host_db,"status").$() == HOST_STATUS_TEMPLATE) {
						$result = API.Template().create($host_db);
						if (!$result) {
							throw new Exception();
						}
						$current_hostid = reset(Nest.value($result,"templateids").$());
					}
					else {
						$result = API.Host().create($host_db);
						if (!$result) {
							throw new Exception();
						}
						$current_hostid = reset(Nest.value($result,"hostids").$());
					}
				}
				$current_hostname = Nest.value($host_db,"host").$();

// TEMPLATES {{{
				if (!empty(Nest.value($rules,"templateLinkage","createMissing").$())) {
					$templates = $xpath.query("templates/template", $host);

					$templateLinkage = CArray.array();
					for($templates as $template) {
						$options = CArray.array(
							"filter" => CArray.array("host" => $template.nodeValue),
							"output" => CArray.array("templateid"),
							"editable" => true
						);
						$current_template = API.Template().get($options);

						if (empty($current_template)) {
							throw new Exception(_s("No permission for template \"%1$s\".", $template.nodeValue));
						}
						$current_template = reset($current_template);

						$templateLinkage[] = $current_template;
					}

					if ($templateLinkage) {
						$result = API.Template().massAdd(CArray.array(
							"hosts" => CArray.array("hostid" => $current_hostid),
							"templates" => $templateLinkage
						));
						if (!$result) {
							throw new Exception();
						}
					}
				}
// }}} TEMPLATES

// ITEMS {{{
				if (!empty(Nest.value($rules,"items","updateExisting").$()) || !empty(Nest.value($rules,"items","createMissing").$())) {
					$items = $xpath.query("items/item", $host);

					// if this is an export from 1.8, we need to make some adjustments to items
					if ($old_version_input) {
						if (!$interfaces_created_with_host) {
							// if host had another interfaces, we are not touching them: they remain as is
							for($interfaces as $i => $interface) {
								// interface was not already created
								if (!isset(Nest.value($interface,"interfaceid").$())) {
									// creating interface
									Nest.value($interface,"hostid").$() = $current_hostid;
									$ids = API.HostInterface().create($interface);
									if ($ids === false) {
										throw new Exception();
									}
									$interfaces[$i]["interfaceid"] = reset(Nest.value($ids,"interfaceids").$());
								}
							}
						}
						else {
							$options = CArray.array(
								"hostids" => $current_hostid,
								"output" => API_OUTPUT_EXTEND
							);
							$interfaces = API.HostInterface().get($options);
						}


						// we must know interface ids to assign them to items
						$agent_interface_id = null;
						$ipmi_interface_id = null;
						$snmp_interfaces = CArray.array(); // hash "port" => "iterfaceid"

						for($interfaces as $interface) {
							switch (Nest.value($interface,"type").$()) {
								case INTERFACE_TYPE_AGENT:
									$agent_interface_id = Nest.value($interface,"interfaceid").$();
									break;
								case INTERFACE_TYPE_IPMI:
									$ipmi_interface_id = Nest.value($interface,"interfaceid").$();
									break;
								case INTERFACE_TYPE_SNMP:
									$snmp_interfaces[$interface["port"]] = Nest.value($interface,"interfaceid").$();
									break;
							}
						}
					}

					for($items as $item) {
						$item_db = self::mapXML2arr($item, XML_TAG_ITEM);
						Nest.value($item_db,"hostid").$() = $current_hostid;

						// item needs interfaces
						if ($old_version_input) {
							// "snmp_port" column was renamed to "port"
							if (Nest.value($item_db,"snmp_port").$() != 0) {
								// zabbix agent items have no ports
								Nest.value($item_db,"port").$() = Nest.value($item_db,"snmp_port").$();
							}
							unset(Nest.value($item_db,"snmp_port").$());

							// assigning appropriate interface depending on item type
							switch (Nest.value($item_db,"type").$()) {
								// zabbix agent interface
								case ITEM_TYPE_ZABBIX:
								case ITEM_TYPE_SIMPLE:
								case ITEM_TYPE_EXTERNAL:
								case ITEM_TYPE_SSH:
								case ITEM_TYPE_TELNET:
									Nest.value($item_db,"interfaceid").$() = $agent_interface_id;
									break;
								// snmp interface
								case ITEM_TYPE_SNMPV1:
								case ITEM_TYPE_SNMPV2C:
								case ITEM_TYPE_SNMPV3:
									// for an item with different port - different interface
									Nest.value($item_db,"interfaceid").$() = $snmp_interfaces[$item_db["port"]];
									break;
								case ITEM_TYPE_IPMI:
									Nest.value($item_db,"interfaceid").$() = $ipmi_interface_id;
									break;
								// no interfaces required for these item types
								case ITEM_TYPE_HTTPTEST:
								case ITEM_TYPE_CALCULATED:
								case ITEM_TYPE_AGGREGATE:
								case ITEM_TYPE_INTERNAL:
								case ITEM_TYPE_ZABBIX_ACTIVE:
								case ITEM_TYPE_TRAPPER:
								case ITEM_TYPE_DB_MONITOR:
									Nest.value($item_db,"interfaceid").$() = null;
									break;
							}

							Nest.value($item_db,"key_").$() = self::convertOldSimpleKey(Nest.value($item_db,"key_").$());
						}

						$current_item = API.Item().get(CArray.array(
							"filter" => CArray.array(
								"hostid" => Nest.value($item_db,"hostid").$(),
								"key_" => $item_db["key_"]
							),
							"webitems" => true,
							"editable" => true,
							"output" => CArray.array("itemid")
						));
						$current_item = reset($current_item);

						if (!$current_item && empty(Nest.value($rules,"items","createMissing").$())) {
							info(_s("Item \"%1$s\" skipped - user rule.", Nest.value($item_db,"key_").$()));
							continue; // break if not update updateExisting
						}
						if ($current_item && empty(Nest.value($rules,"items","updateExisting").$())) {
							info(_s("Item \"%1$s\" skipped - user rule.", Nest.value($item_db,"key_").$()));
							continue; // break if not update updateExisting
						}


// ITEM APPLICATIONS {{{
						$applications = $xpath.query("applications/application", $item);

						$item_applications = CArray.array();
						$applications_to_add = CArray.array();
						$applicationsIds = CArray.array();

						for($applications as $application) {
							$application_db = CArray.array(
								"name" => $application.nodeValue,
								"hostid" => $current_hostid
							);

							$current_application = API.Application().get(CArray.array(
								"filter" => $application_db,
								"output" => API_OUTPUT_EXTEND
							));

							$applicationValue = reset($current_application);

							if ($current_application) {
								if (empty($item_applications)) {
									$item_applications = $current_application;
									$applicationsIds[] = Nest.value($applicationValue,"applicationid").$();
								}
								else {
									if (!in_CArray.array(Nest.value($applicationValue,"applicationid").$(), $applicationsIds)) {
										$item_applications = array_merge($item_applications, $current_application);
										$applicationsIds[] = Nest.value($applicationValue,"applicationid").$();
									}
								}
							}
							else {
								$applications_to_add[] = $application_db;
							}
						}

						if (!empty($applications_to_add)) {
							$result = API.Application().create($applications_to_add);
							if (!$result) {
								throw new Exception();
							}

							$options = CArray.array(
								"applicationids" => Nest.value($result,"applicationids").$(),
								"output" => API_OUTPUT_EXTEND
							);
							$new_applications = API.Application().get($options);

							$item_applications = array_merge($item_applications, $new_applications);
						}
// }}} ITEM APPLICATIONS

						if ($current_item && !empty(Nest.value($rules,"items","updateExisting").$())) {
							Nest.value($item_db,"itemid").$() = Nest.value($current_item,"itemid").$();
							$result = API.Item().update($item_db);
							if (!$result) {
								throw new Exception();
							}

							$current_item = API.Item().get(CArray.array(
								"itemids" => Nest.value($result,"itemids").$(),
								"webitems" => true,
								"output" => CArray.array("itemid")
							));
						}

						if (!$current_item && !empty(Nest.value($rules,"items","createMissing").$())) {
							$result = API.Item().create($item_db);
							if (!$result) {
								throw new Exception();
							}

							$current_item = API.Item().get(CArray.array(
								"itemids" => Nest.value($result,"itemids").$(),
								"webitems" => true,
								"output" => CArray.array("itemid")
							));
						}

						if (!empty($item_applications)) {
							$r = API.Application().massAdd(CArray.array(
								"applications" => $item_applications,
								"items" => $current_item
							));
							if ($r === false) {
								throw new Exception();
							}
						}
					}
				}
// }}} ITEMS


// TRIGGERS {{{
				if (!empty(Nest.value($rules,"triggers","updateExisting").$()) || !empty(Nest.value($rules,"triggers","createMissing").$())) {
					$triggers = $xpath.query("triggers/trigger", $host);

					$triggers_to_add = CArray.array();
					$triggers_to_upd = CArray.array();

					for($triggers as $trigger) {
						$trigger_db = self::mapXML2arr($trigger, XML_TAG_TRIGGER);

						if ($old_version_input) {
							$expressionPart = explode(":", Nest.value($trigger_db,"expression").$());
							$keyName = explode(",", $expressionPart[1], 2);

							if (count($keyName) == 2) {
								$keyValue = explode(".", $keyName[1], 2);
								$key = $keyName[0].\",\".$keyValue[0];

								if (in_CArray.array($keyName[0], self::$oldKeys) || in_CArray.array($keyName[0], self::$oldKeysPref)) {
									Nest.value($trigger_db,"expression").$() = str_replace($key, self::convertOldSimpleKey($key), Nest.value($trigger_db,"expression").$());
								}
							}
						}

						// {HOSTNAME} is here for backward compatibility
						Nest.value($trigger_db,"expression").$() = str_replace("{{HOSTNAME}:", "{".$host_db["host"].":", Nest.value($trigger_db,"expression").$());
						Nest.value($trigger_db,"expression").$() = str_replace("{{HOST.HOST}:", "{".$host_db["host"].":", Nest.value($trigger_db,"expression").$());
						Nest.value($trigger_db,"hostid").$() = $current_hostid;

						if ($current_trigger = API.Trigger().exists($trigger_db)) {
							$ctriggers = API.Trigger().get(CArray.array(
								"filter" => CArray.array(
									"description" => $trigger_db["description"]
								),
								"hostids" => $current_hostid,
								"output" => API_OUTPUT_EXTEND,
								"editable" => 1
							));

							$current_trigger = false;
							for($ctriggers as $ct) {
								$tmp_exp = explode_exp(Nest.value($ct,"expression").$());
								if (strcmp(Nest.value($trigger_db,"expression").$(), $tmp_exp) == 0) {
									$current_trigger = $ct;
									break;
								}
							}
							if (!$current_trigger) {
								throw new Exception(_s("No permission for trigger \"%s\".", Nest.value($trigger_db,"description").$()));
							}
						}
						unset(Nest.value($trigger_db,"hostid").$());


						if (!$current_trigger && empty(Nest.value($rules,"triggers","createMissing").$())) {
							info(_s("Trigger \"%1$s\" skipped - user rule.", Nest.value($trigger_db,"description").$()));
							continue; // break if not update updateExisting
						}
						if ($current_trigger && empty(Nest.value($rules,"triggers","updateExisting").$())) {
							info(_s("Trigger \"%1$s\" skipped - user rule.", Nest.value($trigger_db,"description").$()));
							continue; // break if not update updateExisting
						}

						if ($current_trigger && !empty(Nest.value($rules,"triggers","updateExisting").$())) {
							Nest.value($trigger_db,"triggerid").$() = Nest.value($current_trigger,"triggerid").$();
							$triggers_to_upd[] = $trigger_db;
						}
						if (!$current_trigger && !empty(Nest.value($rules,"triggers","createMissing").$())) {
							$triggers_to_add[] = $trigger_db;
						}
					}

					if (!empty($triggers_to_upd)) {
						$result = API.Trigger().update($triggers_to_upd);
						if (!$result) {
							throw new Exception();
						}

						$options = CArray.array(
							"triggerids" => Nest.value($result,"triggerids").$(),
							"output" => API_OUTPUT_EXTEND
						);
						$r = API.Trigger().get($options);

						$triggersForDependencies = array_merge($triggersForDependencies, $r);
					}
					if (!empty($triggers_to_add)) {
						$result = API.Trigger().create($triggers_to_add);
						if (!$result) {
							throw new Exception();
						}

						$options = CArray.array(
							"triggerids" => Nest.value($result,"triggerids").$(),
							"output" => API_OUTPUT_EXTEND
						);
						$r = API.Trigger().get($options);
						$triggersForDependencies = array_merge($triggersForDependencies, $r);
					}
				}
// }}} TRIGGERS


// GRAPHS {{{
				if (!empty(Nest.value($rules,"graphs","updateExisting").$()) || !empty(Nest.value($rules,"graphs","createMissing").$())) {
					$graphs = $xpath.query("graphs/graph", $host);

					$graphs_to_add = CArray.array();
					$graphs_to_upd = CArray.array();
					for($graphs as $graph) {
// GRAPH ITEMS {{{
						$gitems = $xpath.query("graph_elements/graph_element", $graph);

						$graph_hostids = CArray.array();
						$graph_items = CArray.array();
						for($gitems as $gitem) {
							$gitem_db = self::mapXML2arr($gitem, XML_TAG_GRAPH_ELEMENT);

							$data = explode(":", Nest.value($gitem_db,"host_key_").$());
							$gitem_host = array_shift($data);
							// {HOSTNAME} is here for backward compatibility
							Nest.value($gitem_db,"host").$() = ($gitem_host == "{HOSTNAME}") ? Nest.value($host_db,"host").$() : $gitem_host;
							Nest.value($gitem_db,"host").$() = ($gitem_host == "{HOST.HOST}") ? Nest.value($host_db,"host").$() : $gitem_host;
							if ($old_version_input) {
								$data[0] = self::convertOldSimpleKey($data[0]);
							}
							Nest.value($gitem_db,"key_").$() = implode(":", $data);

							if ($current_item = API.Item().exists($gitem_db)) {
								$current_item = API.Item().get(CArray.array(
									"filter" => CArray.array("key_" => Nest.value($gitem_db,"key_").$()),
									"webitems" => true,
									"editable" => true,
									"host" => Nest.value($gitem_db,"host").$(),
									"output" => CArray.array("itemid", "hostid")
								));
								if (empty($current_item)) {
									throw new Exception(_s("No permission for item \"%1$s\".", Nest.value($gitem_db,"key_").$()));
								}
								$current_item = reset($current_item);

								$graph_hostids[] = Nest.value($current_item,"hostid").$();
								Nest.value($gitem_db,"itemid").$() = Nest.value($current_item,"itemid").$();
								$graph_items[] = $gitem_db;
							}
							else {
								throw new Exception(_s("Item \"%1$s\" does not exist.", Nest.value($gitem_db,"host_key_").$()));
							}
						}
// }}} GRAPH ITEMS

						$graph_db = self::mapXML2arr($graph, XML_TAG_GRAPH);
						Nest.value($graph_db,"hostids").$() = $graph_hostids;


						// do we need to show the graph legend, after it is imported?
						// in 1.8, this setting was present only for pie and exploded graphs
						// for other graph types we are always showing the legend
						if (Nest.value($graph_db,"graphtype").$() != GRAPH_TYPE_PIE && Nest.value($graph_db,"graphtype").$() != GRAPH_TYPE_EXPLODED) {
							Nest.value($graph_db,"show_legend").$() = 1;
						}

						$current_graph = API.Graph().exists($graph_db);

						if ($current_graph) {
							$current_graph = API.Graph().get(CArray.array(
								"filter" => CArray.array("name" => Nest.value($graph_db,"name").$()),
								"hostids" => Nest.value($graph_db,"hostids").$(),
								"output" => API_OUTPUT_EXTEND,
								"editable" => 1
							));

							if (empty($current_graph)) {
								throw new Exception(_s("No permission for graph \"%1$s\".", Nest.value($graph_db,"name").$()));
							}
							$current_graph = reset($current_graph);
						}

						if (!$current_graph && empty(Nest.value($rules,"graphs","createMissing").$())) {
							info(_s("Graph \"%1$s\" skipped - user rule.", Nest.value($graph_db,"name").$()));
							continue; // break if not update updateExisting
						}
						if ($current_graph && empty(Nest.value($rules,"graphs","updateExisting").$())) {
							info(_s("Graph \"%1$s\" skipped - user rule.", Nest.value($graph_db,"name").$()));
							continue; // break if not update updateExisting
						}

						if (!isset(Nest.value($graph_db,"ymin_type").$())) {
							throw new Exception(_s("No \"ymin_type\" field for graph \"%s\".", Nest.value($graph_db,"name").$()));
						}

						if (!isset(Nest.value($graph_db,"ymax_type").$())) {
							throw new Exception(_s("No \"ymax_type\" field for graph \"%s\".", Nest.value($graph_db,"name").$()));
						}

						if (Nest.value($graph_db,"ymin_type").$() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
							$item_data = explode(":", Nest.value($graph_db,"ymin_item_key").$(), 2);
							if (count($item_data) < 2) {
								throw new Exception(_s("Incorrect y min item for graph \"%1$s\".", Nest.value($graph_db,"name").$()));
							}

							if (!$item = get_item_by_key($item_data[1], $item_data[0])) {
								throw new Exception(_s("Missing item \"%1$s\" for host \"%2$s\".", Nest.value($graph_db,"ymin_item_key").$(), Nest.value($host_db,"host").$()));
							}

							Nest.value($graph_db,"ymin_itemid").$() = Nest.value($item,"itemid").$();
						}

						if (Nest.value($graph_db,"ymax_type").$() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
							$item_data = explode(":", Nest.value($graph_db,"ymax_item_key").$(), 2);
							if (count($item_data) < 2) {
								throw new Exception(_s("Incorrect y max item for graph \"%1$s\".", Nest.value($graph_db,"name").$()));
							}

							if (!$item = get_item_by_key($item_data[1], $item_data[0])) {
								throw new Exception(_s("Missing item \"%1$s\" for host \"%2$s\".", Nest.value($graph_db,"ymax_item_key").$(), Nest.value($host_db,"host").$()));
							}

							Nest.value($graph_db,"ymax_itemid").$() = Nest.value($item,"itemid").$();
						}


						Nest.value($graph_db,"gitems").$() = $graph_items;
						if ($current_graph) {
							Nest.value($graph_db,"graphid").$() = Nest.value($current_graph,"graphid").$();
							$graphs_to_upd[] = $graph_db;
						}
						else {
							$graphs_to_add[] = $graph_db;
						}
					}

					if (!empty($graphs_to_add)) {
						$r = API.Graph().create($graphs_to_add);
						if ($r === false) {
							throw new Exception();
						}
					}
					if (!empty($graphs_to_upd)) {
						$r = API.Graph().update($graphs_to_upd);
						if ($r === false) {
							throw new Exception();
						}
					}
				}

// SCREENS
				if (!empty(Nest.value($rules,"screens","updateExisting").$()) || !empty(Nest.value($rules,"screens","createMissing").$())) {
					$screens_node = $xpath.query("screens", $host);

					if ($screens_node.length > 0) {
						$importScreens = self::XMLtoArray($screens_node.item(0));

						for($importScreens as $screen) {

							$current_screen = API.TemplateScreen().get(CArray.array(
								"filter" => CArray.array("name" => Nest.value($screen,"name").$()),
								"templateids" => $current_hostid,
								"output" => API_OUTPUT_EXTEND,
								"editable" => 1,
							));
							$current_screen = reset($current_screen);

							if (!$current_screen && empty(Nest.value($rules,"screens","createMissing").$())) {
								info(_s("Screen \"%1$s\" skipped - user rule.", Nest.value($screen,"name").$()));
								continue;
							}
							if ($current_screen && empty(Nest.value($rules,"screens","updateExisting").$())) {
								info(_s("Screen \"%1$s\" skipped - user rule.", Nest.value($screen,"name").$()));
								continue;
							}

							if (isset(Nest.value($screen,"screenitems").$())) {
								for(Nest.value($screen,"screenitems").$() as &$screenitem) {
									$nodeCaption = isset(Nest.value($screenitem,"resourceid","node").$())
											? $screenitem["resourceid"]["node"].":" : "";

									if (!isset(Nest.value($screenitem,"resourceid").$())) {
										Nest.value($screenitem,"resourceid").$() = 0;
									}

									if (is_array(Nest.value($screenitem,"resourceid").$())) {
										switch (Nest.value($screenitem,"resourcetype").$()) {
											case SCREEN_RESOURCE_GRAPH:
												$db_graphs = API.Graph().getObjects(Nest.value($screenitem,"resourceid").$());

												if (empty($db_graphs)) {
													$error = _s("Cannot find graph \"%1$s\" used in screen \"%2$s\".",
															$nodeCaption.$screenitem["resourceid"]["host"].":".Nest.value($screenitem,"resourceid","name").$(), Nest.value($screen,"name").$());
													throw new Exception($error);
												}

												$tmp = reset($db_graphs);
												Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"graphid").$();
												break;
											case SCREEN_RESOURCE_SIMPLE_GRAPH:
											case SCREEN_RESOURCE_PLAIN_TEXT:
												$db_items = API.Item().getObjects(Nest.value($screenitem,"resourceid").$());

												if (empty($db_items)) {
													$error = _s("Cannot find item \"%1$s\" used in screen \"%2$s\".",
															$nodeCaption.$screenitem["resourceid"]["host"].":".Nest.value($screenitem,"resourceid","key_").$(), Nest.value($screen,"name").$());
													throw new Exception($error);
												}

												$tmp = reset($db_items);
												Nest.value($screenitem,"resourceid").$() = Nest.value($tmp,"itemid").$();
												break;
											default:
												Nest.value($screenitem,"resourceid").$() = 0;
												break;
										}
									}
								}
							}

							Nest.value($screen,"templateid").$() = $current_hostid;
							if ($current_screen) {
								Nest.value($screen,"screenid").$() = Nest.value($current_screen,"screenid").$();

								$result = API.TemplateScreen().update($screen);
								if (!$result) {
									throw new Exception(_("Cannot update screen."));
								}

								info("[".$current_hostname."] "._s("Screen \"%1$s\" updated.", Nest.value($screen,"name").$()));
							}
							else {
								$result = API.TemplateScreen().create($screen);
								if (!$result) {
									throw new Exception(_("Cannot create screen."));
								}

								info("[".$current_hostname."] "._s("Screen \"%1$s\" added.", Nest.value($screen,"name").$()));
							}
						}
					}
				}

			}

// DEPENDENCIES
			$dependencies = $xpath.query("dependencies/dependency");

			if ($dependencies.length > 0) {
				$triggersForDependencies = zbx_objectValues($triggersForDependencies, "triggerid");
				$triggersForDependencies = array_flip($triggersForDependencies);
				$triggerDependencies = CArray.array();
				for($dependencies as $dependency) {

					$triggerDescription = $dependency.getAttribute("description");
					$currentTrigger = get_trigger_by_description($triggerDescription);

					if ($currentTrigger && isset($triggersForDependencies[$currentTrigger["triggerid"]])) {
						$dependsOnList = $xpath.query("depends", $dependency);

						for($dependsOnList as $dependsOn) {
							$depTrigger = get_trigger_by_description($dependsOn.nodeValue);
							if ($depTrigger) {
								if (!isset($triggerDependencies[$currentTrigger["triggerid"]])) {
									$triggerDependencies[$currentTrigger["triggerid"]] = CArray.array(
										"triggerid" => Nest.value($currentTrigger,"triggerid").$(),
										"dependencies" => CArray.array()
									);
								}

								$triggerDependencies[$currentTrigger["triggerid"]]["dependencies"][] = CArray.array(
									"triggerid" => $depTrigger["triggerid"]
								);
							}
						}
					}
				}

				if ($triggerDependencies) {
					API.Trigger()->update($triggerDependencies);
				}
			}
		}
	}
}
