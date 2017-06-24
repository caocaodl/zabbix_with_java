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


class CMapImporter extends CImporter {

	/**
	 * Import maps.
	 *
	 * @param array $maps
	 *
	 * @return void
	 */
	public function import(array $maps) {
		$maps = zbx_toHash($maps, "name");

		checkCircularMapReferences($maps);

		do {
			$im = getIndependentMaps($maps);

			$mapsToCreate = CArray.array();
			$mapsToUpdate = CArray.array();
			for($im as $name) {
				$map = $maps[$name];
				unset($maps[$name]);

				$map = resolveMapReferences($map);

				if ($mapId = referencer->resolveMap(Nest.value($map,"name").$())) {
					Nest.value($map,"sysmapid").$() = $mapId;
					$mapsToUpdate[] = $map;
				}
				else {
					$mapsToCreate[] = $map;
				}
			}

			if (Nest.value(options,"maps","createMissing").$() && $mapsToCreate) {
				$newMapIds = API.Map().create($mapsToCreate);
				for($mapsToCreate as $num => $map) {
					$mapId = $newMapIds["sysmapids"][$num];
					referencer->addMapRef(Nest.value($map,"name").$(), $mapId);
				}
			}
			if (Nest.value(options,"maps","updateExisting").$() && $mapsToUpdate) {
				API.Map()->update($mapsToUpdate);
			}
		} while (!empty($im));
	}

	/**
	 * Check if map elements have circular references.
	 * Circular references can be only in map elements that represent another map.
	 *
	 * @throws Exception
	 * @see checkCircularRecursive
	 *
	 * @param array $maps
	 *
	 * @return void
	 */
	protected function checkCircularMapReferences(array $maps) {
		for($maps as $mapName => $map) {
			if (empty(Nest.value($map,"selements").$())) {
				continue;
			}

			for(Nest.value($map,"selements").$() as $selement) {
				$checked = CArray.array($mapName);
				if ($circMaps = checkCircularRecursive($selement, $maps, $checked)) {
					throw new Exception(_s("Circular reference in maps: %1$s.", implode(" - ", $circMaps)));
				}
			}
		}
	}

	/**
	 * Recursive function for searching for circular map references.
	 * If circular reference exist it return array with map elements with circular reference.
	 *
	 * @param array $element map element to inspect on current recursive loop
	 * @param array $maps    all maps where circular references should be searched
	 * @param array $checked map names that already were processed,
	 *                       should contain unique values if no circular references exist
	 *
	 * @return array|bool
	 */
	protected function checkCircularRecursive(array $element, array $maps, array $checked) {
		// if element is not map element, recursive reference cannot happen
		if (Nest.value($element,"elementtype").$() != SYSMAP_ELEMENT_TYPE_MAP) {
			return false;
		}

		$elementMapName = Nest.value($element,"element","name").$();

		// if current element map name is already in list of checked map names,
		// circular reference exists
		if (in_CArray.array($elementMapName, $checked)) {
			// to have nice result containing only maps that have circular reference,
			// remove everything that was added before repeated map name
			$checked = array_slice($checked, array_search($elementMapName, $checked));
			// add repeated name to have nice loop like m1->m2->m3->m1
			$checked[] = $elementMapName;
			return $checked;
		}
		else {
			$checked[] = $elementMapName;
		}

		// we need to find maps that reference the current element
		// and if one has selements, check all of them recursively
		if (!empty($maps[$elementMapName]["selements"])) {
			for($maps[$elementMapName]["selements"] as $selement) {
				return checkCircularRecursive($selement, $maps, $checked);
			}
		}

		return false;
	}

	/**
	 * Get maps that don't have map elements that reference not existing map i.e. map elements references can be resolved.
	 * Returns array with map names.
	 *
	 * @param array $maps
	 *
	 * @return array
	 */
	protected function getIndependentMaps(array $maps) {
		for($maps as $num => $map) {
			if (empty(Nest.value($map,"selements").$())) {
				continue;
			}

			for(Nest.value($map,"selements").$() as $selement) {
				if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_MAP) {
					if (!referencer->resolveMap(Nest.value($selement,"element","name").$())) {
						unset($maps[$num]);
						continue 2;
					}
				}
			}
		}

		return zbx_objectValues($maps, "name");
	}

	/**
	 * Change all references in map to database ids.
	 *
	 * @throws Exception
	 *
	 * @param array $map
	 *
	 * @return array
	 */
	protected function resolveMapReferences(array $map) {
		// resolve icon map
		if (!empty(Nest.value($map,"iconmap").$())) {
			Nest.value($map,"iconmapid").$() = referencer->resolveIconMap(Nest.value($map,"iconmap","name").$());
			if (!Nest.value($map,"iconmapid").$()) {
				throw new Exception(_s("Cannot find icon map \"%1$s\" used in map \"%2$s\".", Nest.value($map,"iconmap","name").$(), Nest.value($map,"name").$()));
			}
		}

		if (!empty(Nest.value($map,"background").$())) {
			$image = getImageByIdent(Nest.value($map,"background").$());

			if (!$image) {
				throw new Exception(_s("Cannot find background image \"%1$s\" used in map \"%2$s\".",
					Nest.value($map,"background","name").$(), $map["name"]
				));
			}
			Nest.value($map,"backgroundid").$() = Nest.value($image,"imageid").$();
		}

		if (isset(Nest.value($map,"selements").$())) {
			for(Nest.value($map,"selements").$() as &$selement) {
				switch (Nest.value($selement,"elementtype").$()) {
					case SYSMAP_ELEMENT_TYPE_MAP:
						Nest.value($selement,"elementid").$() = referencer->resolveMap(Nest.value($selement,"element","name").$());
						if (!Nest.value($selement,"elementid").$()) {
							throw new Exception(_s("Cannot find map \"%1$s\" used in map \"%2$s\".",
								Nest.value($selement,"element","name").$(), Nest.value($map,"name").$()));
						}
						break;

					case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
						Nest.value($selement,"elementid").$() = referencer->resolveGroup(Nest.value($selement,"element","name").$());
						if (!Nest.value($selement,"elementid").$()) {
							throw new Exception(_s("Cannot find group \"%1$s\" used in map \"%2$s\".",
								Nest.value($selement,"element","name").$(), Nest.value($map,"name").$()));
						}
						break;

					case SYSMAP_ELEMENT_TYPE_HOST:
						Nest.value($selement,"elementid").$() = referencer->resolveHost(Nest.value($selement,"element","host").$());
						if (!Nest.value($selement,"elementid").$()) {
							throw new Exception(_s("Cannot find host \"%1$s\" used in map \"%2$s\".",
								Nest.value($selement,"element","host").$(), Nest.value($map,"name").$()));
						}
						break;

					case SYSMAP_ELEMENT_TYPE_TRIGGER:
						$el = Nest.value($selement,"element").$();
						Nest.value($selement,"elementid").$() = referencer->resolveTrigger(Nest.value($el,"description").$(), Nest.value($el,"expression").$());

						if (!Nest.value($selement,"elementid").$()) {
							throw new Exception(_s(
								"Cannot find trigger \"%1$s\" used in map \"%2$s\".",
								Nest.value($selement,"element","description").$(),
								$map["name"]
							));
						}
						break;

					case SYSMAP_ELEMENT_TYPE_IMAGE:
						Nest.value($selement,"elementid").$() = 0;
						break;
				}

				$icons = CArray.array(
					"icon_off" => "iconid_off",
					"icon_on" => "iconid_on",
					"icon_disabled" => "iconid_disabled",
					"icon_maintenance" => "iconid_maintenance",
				);
				for($icons as $element => $field) {
					if (!empty($selement[$element])) {
						$image = getImageByIdent($selement[$element]);
						if (!$image) {
							throw new Exception(_s("Cannot find icon \"%1$s\" used in map \"%2$s\".",
								$selement[$element]["name"], Nest.value($map,"name").$()));
						}
						$selement[$field] = Nest.value($image,"imageid").$();
					}
				}
			}
			unset($selement);
		}

		if (isset(Nest.value($map,"links").$())) {
			for(Nest.value($map,"links").$() as &$link) {
				if (empty(Nest.value($link,"linktriggers").$())) {
					unset(Nest.value($link,"linktriggers").$());
					continue;
				}

				for(Nest.value($link,"linktriggers").$() as &$linktrigger) {
					$dbTriggers = API.Trigger().getObjects(Nest.value($linktrigger,"trigger").$());
					if (empty($dbTriggers)) {
						throw new Exception(_s(
							"Cannot find trigger \"%1$s\" used in map \"%2$s\".",
							Nest.value($linktrigger,"trigger","description").$(),
							$map["name"]
						));
					}

					$tmp = reset($dbTriggers);
					Nest.value($linktrigger,"triggerid").$() = Nest.value($tmp,"triggerid").$();
				}
				unset($linktrigger);
			}
			unset($link);
		}

		return $map;
	}
}
