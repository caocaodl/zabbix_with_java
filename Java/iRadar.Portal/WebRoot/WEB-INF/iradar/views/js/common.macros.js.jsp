<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="static com.isoft.iradar.inc.ViewsUtil.*"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="java.util.Map"%>
<%
  Map data = getSubViewData("js/common.macros.js");
%>
<script type="text/x-jquery-tmpl" id="macroRow">
	<tr class="form_row">
		<td>
			<input class="input text" type="text" id="macros_#{macroNum}_macro" name="macros[#{macroNum}][macro]" size="30" maxlength="64"
				placeholder="{$MACRO}" style="text-transform:uppercase;">
		</td>
		<td>
			<span style="vertical-align:top;"><%=Defines.RARR%></span>
		</td>
		<td>
			<input class="input text" type="text" id="macros_#{macroNum}_value" name="macros[#{macroNum}][value]" size="40" maxlength="255" placeholder="value">
		</td>
		<td>
			<input class="input link_menu macroRemove remove" type="button" id="macros_#{macroNum}_remove" name="macros_#{macroNum}_remove" value="">
		</td>
	</tr>
</script>
<script type="text/javascript">
	jQuery(function() {
		'use strict';

		var rowTemplate = new Template(jQuery('#macroRow').html());

		function addMacroRow() {
			if (addMacroRow.macro_count === void(0)) {
				addMacroRow.macro_count = <%=Cphp.count(data.get("macros"))%>;
			}

			jQuery('#row_new_macro').before(rowTemplate.evaluate({macroNum: addMacroRow.macro_count}));
			addMacroRow.macro_count++;

			createPlaceholders();
		}

		jQuery('#macro_add').click(addMacroRow);

		jQuery('#tbl_macros').on('click', 'input.macroRemove', function() {
			var e = jQuery(this);

			// check if the macro has an hidden ID element, if it does - increment the deleted macro counter
			var macroNum = e.attr('id').split('_')[1];
			if (jQuery('#macros_' + macroNum + '_id').length) {
				var count = jQuery('#save').data('removedCount') + 1;
				jQuery('#save').data('removedCount', count);
			}
			e.closest('.form_row').remove();
		});

		jQuery('#save').click(function() {
			var that=jQuery(this),
			 removedCount = that.data('removedCount'),
			 form = that.parents("form").get(0),
			 objVar = document.createElement('input');
					objVar.setAttribute('type', 'hidden');
					objVar.setAttribute('name', that.attr("name"));
					objVar.setAttribute('value', that.attr("value"));
					form.appendChild(objVar);
					
			if (removedCount) {

				var content = <%= CJs.encodeJson(Cphp._("Are you sure you want to delete")) %> +' ' + removedCount + ' ' + <%= CJs.encodeJson(Cphp._("macro(s)")) %> +'?';
				showModalWindow('提示', content, [{
						text: '确定',
						click: function() {
							form.submit();

						}
					}, {
						text: '取消',
						click: function() {
							jQuery(this).dialog('destroy');
						}
					}]);
				// return confirm(<%=CJs.encodeJson(Cphp._("Are you sure you want to delete"))%> + ' ' + removedCount + ' ' + <%=CJs.encodeJson(Cphp._("macro(s)"))%>+'?');
			}else{
				form.submit();
			}

		});
	});
</script>
