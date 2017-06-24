package com.isoft.iradar.imports.importers;

import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_search;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTemplateImporter extends CImporter {

	public CTemplateImporter(CArray options, CImportReferencer referencer) {
		super(options, referencer);
	}

	/**
	 * Import templates.
	 * @throws Exception
	 * @param CArray<Map> templates
	 * @return void
	 */
	@Override
	public void doImport(IIdentityBean idBean, CArray<Map> templates) throws Exception {
		templates = rda_toHash(templates, "host");

		checkCircularTemplateReferences(templates);

		for(Map template : templates) {
			// screens are imported separately
			unset(template,"screens");

			if (empty(Nest.value(options,"templateLinkage","createMissing").$())) {
				unset(template,"templates");
			}
		}

		CArray<String> independentTemplates = null;
		do {
			independentTemplates = getIndependentTemplates(idBean, templates);

			CArray<Map> templatesToCreate = array();
			CArray<Map> templatesToUpdate = array();
			CArray templateLinkage = array();
			for(String name : independentTemplates) {
				Map template = templates.get(name);
				unset(templates, name);

				template = resolveTemplateReferences(idBean, template);

				// if we need to add linkages, save linked templates to massAdd later
				if (!empty(Nest.value(options,"templateLinkage","createMissing").$()) && !empty(Nest.value(template,"templates").$())) {
					Nest.value(templateLinkage,template.get("host")).$(Nest.value(template,"templates").$());
					unset(template,"templates");
				}

				if (!empty(Nest.value(template,"templateid").$())) {
					templatesToUpdate.add(template);
				} else {
					templatesToCreate.add(template);
				}
			}

			if (!empty(Nest.value(options,"templates","createMissing").$()) && !empty(templatesToCreate)) {
				CArray<Long[]> newHostIds = API.Template(idBean, this.referencer.getExecutor()).create(templatesToCreate);
				Long[] templateids = newHostIds.get("templateids");
				for (Entry<Object, Map> e : templatesToCreate.entrySet()) {
				    int num = Nest.as(e.getKey()).asInteger();
				    Map createdTemplate = e.getValue();
					Long hostId = templateids[num];
					this.referencer.addTemplateRef(Nest.value(createdTemplate,"host").asString(), hostId);
					this.referencer.addProcessedHost(Nest.value(createdTemplate,"host").asString());

					Object templates_link = templateLinkage.get(createdTemplate.get("host"));
					if (!empty(templates_link)) {
						API.Template(idBean, this.referencer.getExecutor()).massAdd(map(
							"templates", map("templateid", hostId),
							"templates_link", templates_link
						));
					}
				}
			}
			if (!empty(Nest.value(options,"templates","updateExisting").$()) && !empty(templatesToUpdate)) {
				API.Template(idBean, this.referencer.getExecutor()).update(templatesToUpdate);

				for(Map updatedTemplate : templatesToUpdate) {
					this.referencer.addProcessedHost(Nest.value(updatedTemplate,"host").asString());

					Object templates_link = templateLinkage.get(updatedTemplate.get("host"));
					if (!empty(templates_link)) {
						API.Template(idBean, this.referencer.getExecutor()).massAdd(map(
							"templates", updatedTemplate,
							"templates_link", templates_link
						));
					}
				}
			}
		} while (!empty(independentTemplates));

		// if there are templates left in $templates, then they have unresolved references
		for(Map template : templates) {
			CArray unresolvedReferences = array();
			for(Map linkedTemplate : (CArray<Map>)Nest.value(template,"templates").asCArray()) {
				if (empty(this.referencer.resolveTemplate(idBean, Nest.value(linkedTemplate,"name").asString()))) {
					unresolvedReferences.add(Nest.value(linkedTemplate,"name").$());
				}
			}
			throw new Exception(_n("Cannot import template \"%1$s\", linked template \"%2$s\" does not exist.",
				"Cannot import template \"%1$s\", linked templates \"%2$s\" do not exist.",
				Nest.value(template,"host").$(), implode(", ", unresolvedReferences), count(unresolvedReferences)));
		}
	}
	
	/**
	 * Check if templates have circular references.
	 * @throws Exception
	 * @see checkCircularRecursive
	 * @param CArray<Map> templates
	 * @return void
	 */
	protected void checkCircularTemplateReferences(CArray<Map> templates) throws Exception {
		for (Entry<Object, Map> e : templates.entrySet()) {
		    String name = Nest.as(e.getKey()).asString();
		    Map template = e.getValue();
			if (empty(Nest.value(template,"templates").$())) {
				continue;
			}

			CArray<String> checked = null, circTemplates = null;
			for(Map linkedTemplate : (CArray<Map>)Nest.value(template,"templates").asCArray()) {
				checked = array(name);
				if (!empty(circTemplates = checkCircularRecursive(linkedTemplate, templates, checked))) {
					throw new Exception(_s("Circular reference in templates: %1$s.", implode(" - ", circTemplates)));
				}
			}
		}
	}
	
	/**
	 * Recursive function for searching for circular template references.
	 * If circular reference exist it return array with template names with circular reference.
	 *
	 * @param Map linkedTemplate template element to inspect on current recursive loop
	 * @param CArray<Map> templates      all templates where circular references should be searched
	 * @param CArray<String> checked        template names that already were processed,
	 *                              should contain unique values if no circular references exist
	 *
	 * @return CArray<String>
	 */
	protected CArray<String> checkCircularRecursive(Map linkedTemplate, CArray<Map> templates, CArray<String> checked) {
		String linkedTemplateName = Nest.value(linkedTemplate,"name").asString();

		// if current element map name is already in list of checked map names,
		// circular reference exists
		if (in_array(linkedTemplateName, checked)) {
			// to have nice result containing only maps that have circular reference,
			// remove everything that was added before repeated map name
			checked = array_slice(checked, (Integer)array_search(linkedTemplateName, checked));
			// add repeated name to have nice loop like m1->m2->m3->m1
			checked.add(linkedTemplateName);
			return checked;
		} else {
			checked.add(linkedTemplateName);
		}

		// we need to find map that current element reference to
		// and if it has selements check all them recursively
		CArray<Map> ctemplates = Nest.value(templates,linkedTemplateName,"templates").asCArray();
		if (!empty(ctemplates)) {
			for(Map tpl : ctemplates) {
				return checkCircularRecursive(tpl, templates, checked);
			}
		}

		return null;
	}
	
	/**
	 * Get templates that don't have not existing linked templates i.e. all templates that must be linked to these templates exist.
	 * Returns array with template names (host).
	 * @param array templates
	 * @return array
	 */
	protected CArray<String> getIndependentTemplates(IIdentityBean idBean, CArray<Map> templates) {
		for (Entry<Object, Map> e : Clone.deepcopy(templates).entrySet()) {
		    Object num = e.getKey();
		    Map template = e.getValue();
			if (empty(Nest.value(template,"templates").$())) {
				continue;
			}

			for(Map linkedTpl : (CArray<Map>)Nest.value(template,"templates").asCArray()) {
				if (empty(this.referencer.resolveTemplate(idBean, Nest.value(linkedTpl,"name").asString()))) {
					unset(templates, num);
					break;
				}
			}
		}

		return rda_objectValues(templates, "host");
	}

	/**
	 * Change all references in template to database ids.
	 * @throws Exception
	 * @param Map template
	 * @return Map
	 */
	protected Map resolveTemplateReferences(IIdentityBean idBean, Map template) throws Exception {
		Long templateId = null;
		if (!empty(templateId = this.referencer.resolveTemplate(idBean, Nest.value(template,"host").asString()))) {
			Nest.value(template,"templateid").$(templateId);

			// if we update template, existing macros should have hostmacroid
			Long hostMacroId = null;
			for(Map macro : (CArray<Map>)Nest.value(template,"macros").asCArray()) {
				if (!empty(hostMacroId = this.referencer.resolveMacro(idBean, templateId, Nest.value(macro,"macro").asString()))) {
					Nest.value(macro,"hostmacroid").$(hostMacroId);
				}
			}
		}

		CArray<Map> groups = Nest.value(template,"groups").asCArray();
		Long groupid = null;
		for (Entry<Object, Map> e : groups.entrySet()) {
			Object gnum = e.getKey();
			Map group = e.getValue();
			if (empty(groupid = this.referencer.resolveGroup(idBean, Nest.value(group,"name").asString()))) {
				throw new Exception(_s("Group \"%1$s\" does not exist.", Nest.value(group,"name").$()));
			}
			Nest.value(groups, gnum).$(map("groupid", groupid));
		}

		CArray<Map> templates = Nest.value(template,"templates").asCArray();
		if (isset(templates)) {
			for (Entry<Object, Map> e : templates.entrySet()) {
				Object tnum = e.getKey();
				Map parentTemplate = e.getValue();
				Nest.value(templates,tnum).$(map(
					"templateid", this.referencer.resolveTemplate(idBean, Nest.value(parentTemplate,"name").asString())
				));
			}
		}

		return template;
	}
}
