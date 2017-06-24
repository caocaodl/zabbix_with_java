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
 * A class for rendering HTML trees.
 */
class CTree {

	public $tree;
	public $fields;
	public $treename;
	private $size;
	private $maxlevel;

	public function __construct($treename, $value = CArray.array(), $fields = CArray.array()) {
		maxlevel = 0;
		tree = $value;
		fields = $fields;
		treename = $treename;
		size = count($value);
		unset($value, $fields);

		if (!checkTree()) {
			destroy();
			return false;
		}
		else {
			countDepth();
		}
	}

	public function getTree() {
		return tree;
	}

	public function getHTML() {
		$html[] = createJS();
		$html[] = simpleHTML();
		return $html;
	}

	private function makeHeaders() {
		$c = 0;
		$tr = new CRow(Nest.value(fields,"caption").$(), "header");
		unset(Nest.value(fields,"caption").$());

		for(fields as $id => $caption) {
			$tr.addItem($caption);
			$fields[$c] = $id;
			$c++;
		}
		fields = $fields;
		return $tr;
	}

	private function simpleHTML() {
		$table = new CTableInfo();
		$table.addRow(makeHeaders());

		for(tree as $id => $rows) {
			$table.addRow(makeRow($id));
		}
		return $table;
	}

	private function makeRow($id) {
		$table = new CTable();
		$tr = makeSImgStr($id);
		$tr.addItem(tree[$id]["caption"]);

		$table.addRow($tr);

		$tr = new CRow();
		$tr.addItem($table);
		$tr.setAttribute("id", "id_".$id);
		$tr.setAttribute("style", tree[$id]["parentid"] != "0" ? "display: none;" : "");

		for(fields as $value) {
			$tr.addItem(makeCol($id, $value));
		}
		return $tr;
	}

	/**
	 * Returns a column object for the given row and field.
	 *
	 * @param $rowId
	 * @param $colName
	 *
	 * @return CCol
	 */
	protected function makeCol($rowId, $colName) {
		return new CCol(tree[$rowId][$colName]);
	}

	private function makeSImgStr($id) {
		$tr = new CRow();
		$count = isset(tree[$id]["nodeimg"]) ? zbx_strlen(tree[$id]["nodeimg"]) : 0;

		for ($i = 0; $i < $count; $i++) {
			$td = new CCol();
			$img = null;

			switch (tree[$id]["nodeimg"][$i]) {
				case "O":
					$img = new CImg("images/general/tree/zero.gif", "o", "22", "14");
					break;

				case "I":
					$td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
					$img = new CImg("images/general/tree/zero.gif", "i", "22", "14");
					break;

				case "L":
					$td.setAttribute("valign", "top");
					$div = new CTag("div", "yes");
					$div.setAttribute("style", "height: 10px; background-image: url(images/general/tree/pointc.gif);");

					if (tree[$id]["nodetype"] == 2) {
						$img = new CImg("images/general/tree/plus.gif", "y", "22", "14");
						$img.setAttribute("onclick", treename.".closeSNodeX(\"".$id."\", this);");
						$img.setAttribute("id", "idi_".$id);
						$img.setAttribute("class", "pointer");
					}
					else {
						$img = new CImg("images/general/tree/pointl.gif", "y", "22", "14");
					}
					$div.addItem($img);
					$img = $div;
					break;

				case "T":
					$td.setAttribute("valign", "top");
					if (tree[$id]["nodetype"] == 2) {
						$td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
						$img = new CImg("images/general/tree/plus.gif", "t", "22", "14");
						$img.setAttribute("onclick", treename.".closeSNodeX(\"".$id."\", this);");
						$img.setAttribute("id", "idi_".$id);
						$img.setAttribute("class", "pointer");
						$img.setAttribute("style", "top: 1px; position: relative;");
					}
					else {
						$td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
						$img = new CImg("images/general/tree/pointl.gif", "t", "22", "14");
					}
					break;
			}

			$td.addItem($img);
			$tr.addItem($td);
		}

		return $tr;
	}

	private function countDepth() {
		for(tree as $id => $rows) {
			if (Nest.value($rows,"id").$() == "0") {
				continue;
			}
			$parentid = tree[$id]["parentid"];

			tree[$id]["nodeimg"] = getImg($id, isset(tree[$parentid]["nodeimg"]) ? tree[$parentid]["nodeimg"] : "");
			tree[$parentid]["nodetype"] = 2;
			tree[$id]["Level"] = isset(tree[$parentid]["Level"]) ? tree[$parentid]["Level"] + 1 : 1;

			maxlevel>tree[$id]["Level"] ? "" : maxlevel = tree[$id]["Level"];
		}
	}

	public function createJS() {
		$js = "<script src=\"js/class.ctree.js\" type=\"text/javascript\"></script>".\"\n\".
				"<script type=\"text/javascript\"> var ".treename."_tree = {};";

		for(tree as $id => $rows) {
			$parentid = Nest.value($rows,"parentid").$();
			tree[$parentid]["nodelist"] .= $id.",";
		}

		for(tree as $id => $rows) {
			if (Nest.value($rows,"nodetype").$() == "2") {
				Nest.value($rows,"nodelist").$() = rtrim(Nest.value($rows,"nodelist").$(), ",");
				$js .= treename."_tree[\"".$id."\"] = { status: \"close\", nodelist : \"".$rows["nodelist"]."\", parentid : \"".$rows["parentid"]."\"};";
				$js .= \"\n\";
			}
		}

		$js.= "var ".treename." = null";
		$js.= "</script>".\"\n\";

		zbx_add_post_js(treename." = new CTree(\"tree_".CWebUser::$data["alias"]."_".treename."\", ".treename."_tree);");

		return new CJSscript($js);
	}

	private function getImg($id, $img) {
		$img = str_replace("T", "I", $img);
		$img = str_replace("L", "O", $img);
		$ch = "L";

		$childs = tree[tree[$id]["parentid"]]["childnodes"];
		$childs_last = count(tree[tree[$id]["parentid"]]["childnodes"]) - 1;

		if (isset($childs[$childs_last]) && $childs[$childs_last] != $id) {
			$ch = "T";
		}
		$img .= $ch;
		return $img;
	}

	private function checkTree() {
		if (!is_array(tree)) {
			return false;
		}
		for(tree as $id => $cell) {
			tree[$id]["nodetype"] = 0;

			$parentid = Nest.value($cell,"parentid").$();
			tree[$parentid]["childnodes"][] = $id;
			tree[$id]["nodelist"] = "";
		}
		return true;
	}

	private function destroy() {
		unset(tree);
	}
}
