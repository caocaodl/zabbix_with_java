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
 * Class for rendering html page head part.
 */
class CPageHeader {

	/**
	 * @var string page title
	 */
	protected $title;

	/**
	 * @var array of css file paths
	 */
	protected $cssFiles = CArray.array();

	/**
	 * @var array of css styles
	 */
	protected $styles = CArray.array();

	/**
	 * @var array of js file paths
	 */
	protected $jsFiles = CArray.array();

	/**
	 * @var array of js scripts to render before js files
	 */
	protected $jsBefore = CArray.array();

	/**
	 * @var array of js scripts to render after js files
	 */
	protected $js = CArray.array();

	/**
	 * @param string $title
	 */
	public function __construct($title = "") {
		title = $title;
	}

	/**
	 * Add path to css file to render in page head.
	 *
	 * @param string $path
	 */
	public function addCssFile($path) {
		cssFiles[$path] = $path;
	}

	/**
	 * Add initial css files.
	 */
	public function addCssInit() {
		cssFiles[] = "styles/default.css";
		cssFiles[] = "styles/color.css";
		cssFiles[] = "styles/icon.css";
		cssFiles[] = "styles/blocks.css";
		cssFiles[] = "styles/pages.css";
	}

	/**
	 * Add css style to render in page head.
	 *
	 * @param string $style
	 */
	public function addStyle($style) {
		styles[] = $style;
	}

	/**
	 * Add path to js file to render in page head.
	 *
	 * @param string $path
	 */
	public function addJsFile($path) {
		jsFiles[$path] = $path;
	}

	/**
	 * Add js script to render in page head after js file includes are rendered.
	 *
	 * @param string $js
	 */
	public function addJs($js) {
		js[] = $js;
	}

	/**
	 * Add js script to render in page head before js file includes are rendered.
	 *
	 * @param string $js
	 */
	public function addJsBeforeScripts($js) {
		jsBefore[] = $js;
	}

	/**
	 * Display page head html.
	 */
	public function display() {
		echo <<<HTML
<!doctype html>
<html>
	<head>
		<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>
		<title>title</title>
		<meta name=\"Author\" content=\"Zabbix SIA\" />
		<meta charset=\"utf-8\" />
		<link rel=\"shortcut icon\" href=\"images/general/zabbix.ico\" />

HTML;

		for(cssFiles as $path) {
			echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"".$path."\" />".\"\n\";
		}

		if (!empty(styles)) {
			echo "<style type=\"text/css\">";
			echo implode(\"\n\", styles);
			echo "</style>";
		}

		if (!empty(jsBefore)) {
			echo "<script>";
			echo implode(\"\n\", jsBefore);
			echo "</script>";
		}

		for(jsFiles as $path) {
			echo "<script src=\"".$path."\"></script>".\"\n\";
		}

		if (!empty(js)) {
			echo "<script>";
			echo implode(\"\n\", js);
			echo "</script>";
		}

		echo "</head>".\"\n\";
	}
}
