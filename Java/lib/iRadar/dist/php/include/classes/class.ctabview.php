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


class CTabView extends CDiv {

	protected $id = "tabs";
	protected $tabs = CArray.array();
	protected $headers = CArray.array();
	protected $selectedTab = null;

	/**
	 * Disabled tabs IDs, tab option
	 *
	 * @var array
	 */
	protected $disabledTabs = CArray.array();

	public function __construct($data = CArray.array()) {
		if (isset(Nest.value($data,"id").$())) {
			id = Nest.value($data,"id").$();
		}
		if (isset(Nest.value($data,"selected").$())) {
			setSelected(Nest.value($data,"selected").$());
		}
		if (isset(Nest.value($data,"disabled").$())) {
			setDisabled(Nest.value($data,"disabled").$());
		}
		parent::__construct();
		attr("id", zbx_formatDomId(id));
		attr("class", "tabs");
	}

	public function setSelected($selected) {
		selectedTab = $selected;
	}

	/**
	 * Disable tabs
	 *
	 * @param array		$disabled	disabled tabs IDs (first tab - 0, second - 1...)
	 *
	 * @return void
	 */
	public function setDisabled($disabled) {
		disabledTabs = $disabled;
	}

	public function addTab($id, $header, $body) {
		headers[$id] = $header;
		tabs[$id] = new CDiv($body);
		tabs[$id].attr("id", zbx_formatDomId($id));
	}

	public function toString($destroy = true) {
		if (count(tabs) == 1) {
			setAttribute("class", "min-width ui-tabs ui-widget ui-widget-content ui-corner-all widget");

			$header = reset(headers);
			$header = new CDiv($header);
			$header.addClass("ui-corner-all ui-widget-header header");
			$header.setAttribute("id", "tab_".key(headers));
			addItem($header);

			$tab = reset(tabs);
			$tab.addClass("ui-tabs ui-tabs-panel ui-widget ui-widget-content ui-corner-all widget");
			addItem($tab);
		}
		else {
			$headersList = new CList();

			for(headers as $id => $header) {
				$tabLink = new CLink($header, "#".$id, null, null, false);
				$tabLink.setAttribute("id", "tab_".$id);
				$headersList.addItem($tabLink);
			}

			addItem($headersList);
			addItem(tabs);

			if (selectedTab === null) {
				$activeTab = get_cookie("tab", 0);
				$createEvent = "";
			}
			else {
				$activeTab = selectedTab;
				$createEvent = "create: function() { jQuery.cookie(\"tab\", ".selectedTab."); },";
			}

			$disabledTabs = (disabledTabs === null) ? "" : "disabled: ".CJs::encodeJson(disabledTabs).",";

			zbx_add_post_js('
				jQuery(\"#".id."\").tabs({
					".$createEvent."
					".$disabledTabs."
					active: ".$activeTab.",
					activate: function(event, ui) {
						jQuery.cookie(\"tab\", ui.newTab.index().toString());
					}
				})
				.css(\"visibility\", \"visible\");'
			);
		}

		return parent::toString($destroy);
	}
}
