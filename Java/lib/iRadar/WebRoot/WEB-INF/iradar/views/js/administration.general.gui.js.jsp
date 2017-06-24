<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		'use strict';

		$('#server_check_enabled').change(function() {
			$('#server_check_interval').prop('disabled', !this.checked);
		}).change();
	});
</script>
