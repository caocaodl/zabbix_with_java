<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="static com.isoft.iradar.Cphp.*"%>
<%@ page import="static com.isoft.iradar.utils.CJs.*"%>
<%@ page import="static com.isoft.iradar.helpers.CHtml.*"%>
<%@ page import="static com.isoft.iradar.inc.ViewsUtil.getSubViewData"%>
<%@ page import="com.isoft.types.CArray"%>
<%@ page import="static com.isoft.types.CArray.*"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Map.Entry"%>
<%
  Map data = getSubViewData("js/configuration.host.prototype.edit.js");
  Map discoveryRule = Nest.value(data,"discovery_rule").asCArray();
  Map hostPrototype = Nest.value(data,"host_prototype").asCArray();
  Map parentHost = Nest.value(data,"parent_host").asCArray();
  %>
<script type="text/x-jquery-tmpl" id="groupPrototypeRow">
	<tr class="form_row">
		<td>
			<input class="input text" name="group_prototypes[#{i}][name]" type="text" size="30" value="#{name}"
				placeholder="{#MACRO}">
		</td>
		<td>
			<input type="button" class="link_menu group-prototype-remove" name="remove" value="<%=encode(_("Remove"))%>" />
			<input type="hidden" name="group_prototypes[#{i}][group_prototypeid]" value="#{group_prototypeid}" />
		</td>
	</tr>
</script>

<script type="text/javascript">
	function addGroupPrototypeRow(groupPrototype) {
		var addButton = jQuery('#group_prototype_add');

		var rowTemplate = new Template(jQuery('#groupPrototypeRow').html());
		groupPrototype.i = addButton.data('group-prototype-count');
		jQuery('#row_new_group_prototype').before(rowTemplate.evaluate(groupPrototype));

		addButton.data('group-prototype-count', addButton.data('group-prototype-count') + 1);
	}

	jQuery(function() {
		jQuery('#group_prototype_add')
			.data('group-prototype-count', jQuery('#tbl_group_prototypes').find('.group-prototype-remove').length)
			.click(function() {
				addGroupPrototypeRow({})
			});

		jQuery('#tbl_group_prototypes').on('click', 'input.group-prototype-remove', function() {
			jQuery(this).closest('.form_row').remove();
		});


		<%if(empty(Nest.value(hostPrototype,"groupPrototypes").$())){%>
			addGroupPrototypeRow({'name': '', 'group_prototypeid': ''});
		<%}%>
		<%for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()){%>
			addGroupPrototypeRow(<%=encodeJson(map(
				"name", Nest.value(groupPrototype,"name").$(),
				"group_prototypeid", isset(groupPrototype,"group_prototypeid") ? Nest.value(groupPrototype,"group_prototypeid").$() : null
			))%>);
		<%}%>

		<%if(!empty(Nest.value(hostPrototype,"templateid").$())){%>
			jQuery("#tbl_group_prototypes").find('input').prop("disabled", "disabled");
		<%}%>
	});
</script>
