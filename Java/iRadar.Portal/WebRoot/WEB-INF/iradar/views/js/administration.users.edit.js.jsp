<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<script type="text/javascript">
	jQuery(document).ready(function() {
		jQuery('#autologout_visible').bind('click', function() {
			if (this.checked) {
				jQuery('#autologout').prop('disabled', false);
				jQuery('#autologin').attr('checked', false);
			}
			else {
				jQuery('#autologout').prop('disabled', true);
			}
		});
		jQuery('#autologin').bind('click', function() {
			if (this.checked) {
				jQuery('#autologout').prop('disabled', true);
				jQuery('#autologout_visible').attr('checked', false);
			}
		});
	});
</script>
