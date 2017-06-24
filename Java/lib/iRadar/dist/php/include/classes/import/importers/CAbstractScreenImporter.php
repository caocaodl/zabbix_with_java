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


abstract class CAbstractScreenImporter extends CImporter {

	/**
	 * Prepare screen data for import.
	 * Each screen element has reference to resource it represents, reference structure may differ depending on type.
	 * Referenced database objects ids are stored to "resourceid" field of screen items.
	 *
	 * @todo: api requests probably should be done in CReferencer class
	 * @throws Exception if referenced object is not found in database
	 *
	 * @param array $screen
	 *
	 * @return array
	 */
	protected function resolveScreenReferences(array $screen) {
		if (!empty(Nest.value($screen,"screenitems").$())) {
			for(Nest.value($screen,"screenitems").$() as &$screenItem) {
				$resource = Nest.value($screenItem,"resource").$();
				if (empty($resource)) {
					Nest.value($screenItem,"resourceid").$() = 0;
					continue;
				}

				switch (Nest.value($screenItem,"resourcetype").$()) {
					case SCREEN_RESOURCE_HOSTS_INFO:
					case SCREEN_RESOURCE_TRIGGERS_INFO:
					case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
					case SCREEN_RESOURCE_DATA_OVERVIEW:
					case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
						Nest.value($screenItem,"resourceid").$() = referencer->resolveGroup(Nest.value($resource,"name").$());
						if (!Nest.value($screenItem,"resourceid").$()) {
							throw new Exception(_s("Cannot find group \"%1$s\" used in screen \"%2$s\".",
								Nest.value($resource,"name").$(), Nest.value($screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_HOST_TRIGGERS:
						Nest.value($screenItem,"resourceid").$() = referencer->resolveHost(Nest.value($resource,"host").$());
						if (!Nest.value($screenItem,"resourceid").$()) {
							throw new Exception(_s("Cannot find host \"%1$s\" used in screen \"%2$s\".",
								Nest.value($resource,"host").$(), Nest.value($screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_GRAPH:
						$dbGraphs = API.Graph().getObjects($resource);
						if (empty($dbGraphs)) {
							throw new Exception(_s("Cannot find graph \"%1$s\" used in screen \"%2$s\".",
								Nest.value($resource,"name").$(), Nest.value($screen,"name").$()));
						}

						$tmp = reset($dbGraphs);
						Nest.value($screenItem,"resourceid").$() = Nest.value($tmp,"graphid").$();
						break;

					case SCREEN_RESOURCE_SIMPLE_GRAPH:
					case SCREEN_RESOURCE_PLAIN_TEXT:
						$hostId = referencer->resolveHostOrTemplate(Nest.value($resource,"host").$());
						Nest.value($screenItem,"resourceid").$() = referencer->resolveItem($hostId, Nest.value($resource,"key").$());
						if (!Nest.value($screenItem,"resourceid").$()) {
							throw new Exception(_s("Cannot find item \"%1$s\" used in screen \"%2$s\".",
									$resource["host"].":".Nest.value($resource,"key").$(), Nest.value($screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_MAP:
						Nest.value($screenItem,"resourceid").$() = referencer->resolveMap(Nest.value($resource,"name").$());
						if (!Nest.value($screenItem,"resourceid").$()) {
							throw new Exception(_s("Cannot find map \"%1$s\" used in screen \"%2$s\".",
								Nest.value($resource,"name").$(), Nest.value($screen,"name").$()));
						}
						break;

					case SCREEN_RESOURCE_SCREEN:
						Nest.value($screenItem,"resourceid").$() = referencer->resolveScreen(Nest.value($resource,"name").$());
						if (!Nest.value($screenItem,"resourceid").$()) {
							throw new Exception(_s("Cannot find screen \"%1$s\" used in screen \"%2$s\".",
								Nest.value($resource,"name").$(), Nest.value($screen,"name").$()));
						}
						break;

					default:
						Nest.value($screenItem,"resourceid").$() = 0;
						break;
				}

			}
			unset($screenItem);
		}

		return $screen;
	}
}
