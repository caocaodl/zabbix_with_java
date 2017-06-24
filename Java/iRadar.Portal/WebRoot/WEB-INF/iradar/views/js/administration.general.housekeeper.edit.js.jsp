<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="static com.isoft.iradar.Cphp.*"%>
<%@ page import="static com.isoft.iradar.utils.CJs.*"%>
<%@ page import="static com.isoft.biz.daoimpl.radar.CDB.*"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="java.util.Map"%>
<%
  Map<String, Object> schema = getSchema("config");
%>
<div id="dialog" style="display:none; white-space: normal;"></div>
<script type="text/javascript">
	jQuery(function() {
		jQuery('#hk_events_mode').change(function() {
			jQuery('#hk_events_trigger').prop('disabled', !this.checked);
			jQuery('#hk_events_internal').prop('disabled', !this.checked);
			jQuery('#hk_events_discovery').prop('disabled', !this.checked);
			jQuery('#hk_events_autoreg').prop('disabled', !this.checked);
		});

		jQuery('#hk_services_mode').change(function() {
			jQuery('#hk_services').prop('disabled', !this.checked);
		});

		jQuery('#hk_audit_mode').change(function() {
			jQuery('#hk_audit').prop('disabled', !this.checked);
		});

		jQuery('#hk_sessions_mode').change(function() {
			jQuery('#hk_sessions').prop('disabled', !this.checked);
		});

		jQuery('#hk_history_global').change(function() {
			//jQuery('#hk_history').prop('disabled', !this.checked);
		});

		jQuery('#hk_trends_global').change(function() {
			//jQuery('#hk_trends').prop('disabled', !this.checked);
		});

		// reset button
		jQuery("#resetDefaults").click(function(){

			  jQuery('#dialog').text(<%=encodeJson(_("Reset all fields to default values?"))%>);
			//var w = jQuery('#dialog').outerWidth()+20;
			  var w = 600;

			jQuery('#dialog').dialog({
				buttons: [
					{text: <%=encodeJson(_("Reset defaults"))%>, click: function(){
						// events and alerts
						<%if (Nest.value(schema,"fields","hk_events_mode","default").asInteger() == 1){%>
							jQuery('#hk_events_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_events_mode').prop('checked', false);
						<%}%>

						jQuery('#hk_events_mode').trigger('change');

						jQuery('#hk_events_trigger').val("<%=Nest.value(schema,"fields","hk_events_trigger","default").$()%>");
						jQuery('#hk_events_internal').val("<%=Nest.value(schema,"fields","hk_events_internal","default").$()%>");
						jQuery('#hk_events_discovery').val("<%=Nest.value(schema,"fields","hk_events_discovery","default").$()%>");
						jQuery('#hk_events_autoreg').val("<%=Nest.value(schema,"fields","hk_events_autoreg","default").$()%>");

						// IT services
						<%if(Nest.value(schema,"fields","hk_services_mode","default").asInteger()==1){%>
							jQuery('#hk_services_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_services_mode').prop('checked', false);
						<%}%>

						jQuery('#hk_services_mode').trigger('change');

						jQuery('#hk_services').val("<%=Nest.value(schema,"fields","hk_services","default").$()%>");

						// audit
						<%if(Nest.value(schema,"fields","hk_audit_mode","default").asInteger()==1){%>
							jQuery('#hk_audit_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_audit_mode').prop('checked', false);
						<%}%>

						jQuery('#hk_audit_mode').trigger('change');

						jQuery('#hk_audit').val("<%=Nest.value(schema,"fields","hk_audit","default").$()%>");

						// user sessions
						<%if(Nest.value(schema,"fields","hk_sessions_mode","default").asInteger()==1){%>
							jQuery('#hk_sessions_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_sessions_mode').prop('checked', false);
						<%}%>

						jQuery('#hk_sessions_mode').trigger('change');

						jQuery('#hk_sessions').val("<%=Nest.value(schema,"fields","hk_sessions","default").$()%>");

						// history
						<%if(Nest.value(schema,"fields","hk_history_mode","default").asInteger()==1){%>
							jQuery('#hk_history_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_history_mode').prop('checked', false);
						<%}%>

						<%if(Nest.value(schema,"fields","hk_history_global","default").asInteger()==1){%>
							jQuery('#hk_history_global').prop('checked', true);
						<%}else{%>
							jQuery('#hk_history_global').prop('checked', false);
						<%}%>

						jQuery('#hk_history_global').trigger('change');

						jQuery('#hk_history').val("<%=Nest.value(schema,"fields","hk_history","default").$()%>");

						// trends
						<%if(Nest.value(schema,"fields","hk_trends_mode","default").asInteger()==1){%>
							jQuery('#hk_trends_mode').prop('checked', true);
						<%}else{%>
							jQuery('#hk_trends_mode').prop('checked', false);
						<%}%>

						<%if(Nest.value(schema,"fields","hk_trends_global","default").asInteger()==1){%>
							jQuery('#hk_trends_global').prop('checked', true);
						<%}else{%>
							jQuery('#hk_trends_global').prop('checked', false);
						<%}%>

						jQuery('#hk_trends_global').trigger('change');

						jQuery('#hk_trends').val("<%=Nest.value(schema,"fields","hk_trends","default").$()%>");

						jQuery(this).dialog("destroy");
					}},
					{text: <%=encodeJson(_("Cancel"))%>, click: function(){
						jQuery(this).dialog("destroy");
					}}
				],
				draggable: true,
				modal: true,
				width: (w > 600 ? 600 : 'inherit'),
				resizable: false,
				minWidth: 200,
				minHeight: 100,
				title: <%=encodeJson(_("Reset confirmation"))%>,
				close: function(){ jQuery(this).dialog('destroy'); }
			});

			jQuery('#dialog').dialog('widget').find('button:first').addClass('main');
		});
	});
</script>
