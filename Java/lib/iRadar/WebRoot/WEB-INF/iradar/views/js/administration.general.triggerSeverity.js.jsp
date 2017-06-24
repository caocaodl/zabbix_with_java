<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.iradar.inc.SchemaUtil"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="java.util.Map"%>
<%
    Map<String,Object> schema = SchemaUtil.SCHEMAS.get("config");
%>
<div id="dialog" style="display:none; white-space: normal;"></div>

<script type="text/javascript">

	jQuery(document).ready(function(){

		jQuery("#resetDefaults").click(function(){

			jQuery('#dialog').text(<%=CJs.encodeJson(Cphp._("Reset all names and colours to default values?"))%>);
			var w = jQuery('#dialog').outerWidth()+20;

			jQuery('#dialog').dialog({
				buttons: [
					{text: <%=CJs.encodeJson(Cphp._("Reset defaults"))%>, click: function(){
						jQuery('#severity_name_0').val("<%=Nest.value(schema,"fields","severity_name_0","default").asString()%>");
						jQuery('#severity_name_1').val("<%=Nest.value(schema,"fields","severity_name_1","default").asString()%>");
						jQuery('#severity_name_2').val("<%=Nest.value(schema,"fields","severity_name_2","default").asString()%>");
						jQuery('#severity_name_3').val("<%=Nest.value(schema,"fields","severity_name_3","default").asString()%>");
						jQuery('#severity_name_4').val("<%=Nest.value(schema,"fields","severity_name_4","default").asString()%>");
						jQuery('#severity_name_5').val("<%=Nest.value(schema,"fields","severity_name_5","default").asString()%>");
						jQuery('#severity_color_0').val("<%=Nest.value(schema,"fields","severity_color_0","default").asString()%>");
						jQuery('#severity_color_0').change();
						jQuery('#severity_color_1').val("<%=Nest.value(schema,"fields","severity_color_1","default").asString()%>");
						jQuery('#severity_color_1').change();
						jQuery('#severity_color_2').val("<%=Nest.value(schema,"fields","severity_color_2","default").asString()%>");
						jQuery('#severity_color_2').change();
						jQuery('#severity_color_3').val("<%=Nest.value(schema,"fields","severity_color_3","default").asString()%>");
						jQuery('#severity_color_3').change();
						jQuery('#severity_color_4').val("<%=Nest.value(schema,"fields","severity_color_4","default").asString()%>");
						jQuery('#severity_color_4').change();
						jQuery('#severity_color_5').val("<%=Nest.value(schema,"fields","severity_color_5","default").asString()%>");
						jQuery('#severity_color_5').change();
						jQuery(this).dialog("destroy");
					} },
					{text: <%=CJs.encodeJson(Cphp._("Cancel"))%>, click: function(){
						jQuery(this).dialog("destroy");
					}}
				],
				draggable: true,
				modal: true,
				width: (w > 600 ? 600 : 'inherit'),
				resizable: false,
				minWidth: 200,
				minHeight: 100,
				title: <%=CJs.encodeJson(Cphp._("Reset confirmation"))%>,
				close: function(){ jQuery(this).dialog('destroy'); }
			});

			jQuery('#dialog').dialog('widget').find('button:first').addClass('main');
		});
	});

</script>
