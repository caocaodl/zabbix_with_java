<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="static com.isoft.iradar.inc.ViewsUtil.*"%>
<%@ page import="java.util.Map"%>
<%
  Map data = getSubViewData("js/monitoring.triggerComment.js");
%>
<script type="text/javascript">
	jQuery(document).ready(function() {
		'use strict';

		jQuery('#edit').click(function() {
			jQuery('#comments').val(<%=CJs.encodeJson(Nest.value(data, "trigger", "comments").$())%>);
			jQuery('#comments').removeAttr('readonly');
			jQuery('#edit').button('disable');
			jQuery('#save').button('enable');
		});
	});
</script>
