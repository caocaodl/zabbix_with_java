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


class CTemplateImporter extends CImporter {

	/**
	 * Import templates.
	 *
	 * @throws Exception
	 *
	 * @param array $templates
	 *
	 * @return void
	 */
	public function import(array $templates) {
		$templates = zbx_toHash($templates, "host");

		checkCircularTemplateReferences($templates);

		for($templates as &$template) {
			// screens are imported separately
			unset(Nest.value($template,"screens").$());

			if (!Nest.value(options,"templateLinkage","createMissing").$()) {
				unset(Nest.value($template,"templates").$());
			}
		}
		unset($template);

		do {
			$independentTemplates = getIndependentTemplates($templates);

			$templatesToCreate = CArray.array();
			$templatesToUpdate = CArray.array();
			$templateLinkage = CArray.array();
			for($independentTemplates as $name) {
				$template = $templates[$name];
				unset($templates[$name]);

				$template = resolveTemplateReferences($template);

				// if we need to add linkages, save linked templates to massAdd later
				if (Nest.value(options,"templateLinkage","createMissing").$() && !empty(Nest.value($template,"templates").$())) {
					$templateLinkage[$template["host"]] = Nest.value($template,"templates").$();
					unset(Nest.value($template,"templates").$());
				}

				if (!empty(Nest.value($template,"templateid").$())) {
					$templatesToUpdate[] = $template;
				}
				else {
					$templatesToCreate[] = $template;
				}
			}

			if (Nest.value(options,"templates","createMissing").$() && $templatesToCreate) {
				$newHostIds = API.Template().create($templatesToCreate);

				for($templatesToCreate as $num => $createdTemplate) {
					$hostId = $newHostIds["templateids"][$num];
					referencer->addTemplateRef(Nest.value($createdTemplate,"host").$(), $hostId);
					referencer->addProcessedHost(Nest.value($createdTemplate,"host").$());

					if (!empty($templateLinkage[$createdTemplate["host"]])) {
						API.Template()->massAdd(CArray.array(
							"templates" => CArray.array("templateid" => $hostId),
							"templates_link" => $templateLinkage[$createdTemplate["host"]]
						));
					}
				}
			}
			if (Nest.value(options,"templates","updateExisting").$() && $templatesToUpdate) {
				API.Template()->update($templatesToUpdate);

				for($templatesToUpdate as $updatedTemplate) {
					referencer->addProcessedHost(Nest.value($updatedTemplate,"host").$());

					if (!empty($templateLinkage[$updatedTemplate["host"]])) {
						API.Template()->massAdd(CArray.array(
							"templates" => $updatedTemplate,
							"templates_link" => $templateLinkage[$updatedTemplate["host"]]
						));
					}
				}
			}
		} while (!empty($independentTemplates));

		// if there are templates left in $templates, then they have unresolved references
		for($templates as $template) {
			$unresolvedReferences = CArray.array();
			for(Nest.value($template,"templates").$() as $linkedTemplate) {
				if (!referencer->resolveTemplate(Nest.value($linkedTemplate,"name").$())) {
					$unresolvedReferences[] = Nest.value($linkedTemplate,"name").$();
				}
			}
			throw new Exception(_n("Cannot import template \"%1$s\", linked template \"%2$s\" does not exist.",
				"Cannot import template \"%1$s\", linked templates \"%2$s\" do not exist.",
				Nest.value($template,"host").$(), implode(", ", $unresolvedReferences), count($unresolvedReferences)));
		}
	}

	/**
	 * Check if templates have circular references.
	 *
	 * @throws Exception
	 * @see checkCircularRecursive
	 *
	 * @param array $templates
	 *
	 * @return void
	 */
	protected function checkCircularTemplateReferences(array $templates) {
		for($templates as $name => $template) {
			if (empty(Nest.value($template,"templates").$())) {
				continue;
			}

			for(Nest.value($template,"templates").$() as $linkedTemplate) {
				$checked = CArray.array($name);
				if ($circTemplates = checkCircularRecursive($linkedTemplate, $templates, $checked)) {
					throw new Exception(_s("Circular reference in templates: %1$s.", implode(" - ", $circTemplates)));
				}
			}
		}
	}

	/**
	 * Recursive function for searching for circular template references.
	 * If circular reference exist it return array with template names with circular reference.
	 *
	 * @param array $linkedTemplate template element to inspect on current recursive loop
	 * @param array $templates      all templates where circular references should be searched
	 * @param array $checked        template names that already were processed,
	 *                              should contain unique values if no circular references exist
	 *
	 * @return array|bool
	 */
	protected function checkCircularRecursive(array $linkedTemplate, array $templates, array $checked) {
		$linkedTemplateName = Nest.value($linkedTemplate,"name").$();

		// if current element map name is already in list of checked map names,
		// circular reference exists
		if (in_CArray.array($linkedTemplateName, $checked)) {
			// to have nice result containing only maps that have circular reference,
			// remove everything that was added before repeated map name
			$checked = array_slice($checked, array_search($linkedTemplateName, $checked));
			// add repeated name to have nice loop like m1->m2->m3->m1
			$checked[] = $linkedTemplateName;
			return $checked;
		}
		else {
			$checked[] = $linkedTemplateName;
		}

		// we need to find map that current element reference to
		// and if it has selements check all them recursively
		if (!empty($templates[$linkedTemplateName]["templates"])) {
			for($templates[$linkedTemplateName]["templates"] as $tpl) {
				return checkCircularRecursive($tpl, $templates, $checked);
			}
		}

		return false;
	}

	/**
	 * Get templates that don't have not existing linked templates i.e. all templates that must be linked to these templates exist.
	 * Returns array with template names (host).
	 *
	 * @param array $templates
	 *
	 * @return array
	 */
	protected function getIndependentTemplates(array $templates) {
		for($templates as $num => $template) {
			if (empty(Nest.value($template,"templates").$())) {
				continue;
			}

			for(Nest.value($template,"templates").$() as $linkedTpl) {
				if (!referencer->resolveTemplate(Nest.value($linkedTpl,"name").$())) {
					unset($templates[$num]);
					continue 2;
				}
			}
		}

		return zbx_objectValues($templates, "host");
	}

	/**
	 * Change all references in template to database ids.
	 *
	 * @throws Exception
	 *
	 * @param array $template
	 *
	 * @return array
	 */
	protected function resolveTemplateReferences(array $template) {
		if ($templateId = referencer->resolveTemplate(Nest.value($template,"host").$())) {
			Nest.value($template,"templateid").$() = $templateId;

			// if we update template, existing macros should have hostmacroid
			for(Nest.value($template,"macros").$() as &$macro) {
				if ($hostMacroId = referencer->resolveMacro($templateId, Nest.value($macro,"macro").$())) {
					Nest.value($macro,"hostmacroid").$() = $hostMacroId;
				}
			}
			unset($macro);
		}

		for(Nest.value($template,"groups").$() as $gnum => $group) {
			if (!referencer->resolveGroup(Nest.value($group,"name").$())) {
				throw new Exception(_s("Group \"%1$s\" does not exist.", Nest.value($group,"name").$()));
			}
			$template["groups"][$gnum] = CArray.array("groupid" => referencer->resolveGroup(Nest.value($group,"name").$()));
		}

		if (isset(Nest.value($template,"templates").$())) {
			for(Nest.value($template,"templates").$() as $tnum => $parentTemplate) {
				$template["templates"][$tnum] = CArray.array(
					"templateid" => referencer->resolveTemplate(Nest.value($parentTemplate,"name").$())
				);
			}
		}

		return $template;
	}
}
