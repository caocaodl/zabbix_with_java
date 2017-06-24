<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.iradar.inc.SchemaUtil"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%
	Map<String, Object> schema = SchemaUtil.SCHEMAS.get("config");
%>
<div id="dialog" style="display:none; white-space: normal;"></div>

<script type="text/javascript">

	jQuery(document).ready(function(){
		jQuery('#resetDefaults').click(function(){

			jQuery('#dialog').text(<%=CJs.encodeJson(Cphp._("Reset all fields to default values?"))%>);
			var w = jQuery('#dialog').outerWidth()+20;

			jQuery('#dialog').dialog({
				buttons: [
					{text: <%=CJs.encodeJson(Cphp._("Reset defaults"))%>, click: function(){
						// Unacknowledged problem events
						jQuery('#problem_unack_color').val("<%=Nest.value(schema,"fields","problem_unack_color","default").asString()%>");
						jQuery('#problem_unack_color').change();
						jQuery('#problem_unack_style').prop(
								'checked',
								<%=Nest.value(schema,"fields","problem_unack_style","default").asInteger() == 0 ? "false" : "true"%>
						);

						// Acknowledged problem events
						jQuery('#problem_ack_color').val("<%=Nest.value(schema,"fields","problem_ack_color","default").asString()%>");
						jQuery('#problem_ack_color').change();
						jQuery('#problem_ack_style').prop(
								'checked',
								<%=Nest.value(schema,"fields","problem_ack_style","default").asInteger() == 0 ? "false" : "true"%>
						);

						// Unacknowledged ok events
						jQuery('#ok_unack_color').val("<%=Nest.value(schema,"fields","ok_unack_color","default").asString()%>");
						jQuery('#ok_unack_color').change();
						jQuery('#ok_unack_style').prop(
								'checked',
								<%=Nest.value(schema,"fields","ok_unack_style","default").asInteger() == 0 ? "false" : "true"%>
						);

						// Acknowledged ok events
						jQuery('#ok_ack_color').val("<%=Nest.value(schema,"fields","ok_ack_color","default").asString()%>");
						jQuery('#ok_ack_color').change();
						jQuery('#ok_ack_style').prop(
								'checked',
								<%=Nest.value(schema,"fields","ok_ack_style","default").asInteger() == 0 ? "false" : "true"%>
						);

						jQuery('#ok_period').val("<%=Nest.value(schema,"fields","ok_period","default").asString()%>");
						jQuery('#blink_period').val("<%=Nest.value(schema,"fields","blink_period","default").asString()%>");

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
