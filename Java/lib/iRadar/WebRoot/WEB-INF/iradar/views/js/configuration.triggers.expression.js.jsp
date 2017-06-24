<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="static com.isoft.iradar.inc.ViewsUtil.getSubViewData"%>
<%@ page import="static com.isoft.iradar.inc.JsUtil.rda_jsvalue"%>
<%@ page import="static com.isoft.iradar.Cphp.*"%>
<%@ page import="static com.isoft.iradar.inc.Defines.*"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="java.util.Map"%>
<%
  Map data = getSubViewData("js/configuration.triggers.expression.js");
%>
<script type="text/javascript">
	function add_var_to_opener_obj(obj, name, value) {
		new_variable = window.opener.document.createElement('input');
		new_variable.type = 'hidden';
		new_variable.name = name;
		new_variable.value = value;
		obj.appendChild(new_variable);
	}

	function insertText(obj, value) {
		<% if ("expression".equals(Nest.value(data,"dstfld1").asString())) { %>
		    jQuery(obj).val(jQuery(obj).val() + value);
		<%} else {%>
			jQuery(obj).val(value);
		<%}%>
	}

	jQuery(document).ready(function() {
		'use strict';

		jQuery('#paramtype').change(function() {
			if (jQuery('#expr_type option:selected').val().substr(0, 4) == 'last'
					|| jQuery('#expr_type option:selected').val().substr(0, 6) == 'strlen'
					|| jQuery('#expr_type option:selected').val().substr(0, 4) == 'band') {
				if (jQuery('#paramtype option:selected').val() == <%=PARAM_TYPE_COUNTS%>) {
					jQuery('#param_0').removeAttr('readonly');
				}
				else {
					jQuery('#param_0').attr('readonly', 'readonly');
				}
			}
		});

		jQuery(document).ready(function() {
			if (jQuery('#expr_type option:selected').val().substr(0, 4) == 'last'
					|| jQuery('#expr_type option:selected').val().substr(0, 6) == 'strlen'
					|| jQuery('#expr_type option:selected').val().substr(0, 4) == 'band') {
				if (jQuery('#paramtype option:selected').val() == <%=PARAM_TYPE_COUNTS%>) {
					jQuery('#param_0').removeAttr('readonly');
				}
				else {
					jQuery('#param_0').attr('readonly', 'readonly');
				}
			}
		});
	});
</script>

<%if (!empty(Nest.value(data,"insert").$())) { %>
	<script type="text/javascript">
		insertText(jQuery('#<%=Nest.value(data,"dstfld1").asString()%>', window.opener.document), <%=rda_jsvalue(Nest.value(data,"expression").$())%>);
		close_window();
	</script>
<%}
    if (!empty(Nest.value(data,"cancel").$())) {%>
	<script type="text/javascript">
		close_window();
	</script>
<%}%>
