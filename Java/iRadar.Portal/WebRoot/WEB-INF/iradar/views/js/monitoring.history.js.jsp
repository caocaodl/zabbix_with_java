<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<script type="text/javascript">
	function addPopupValues(list) {
		if (!isset('object', list)) {
			throw("Error hash attribute 'list' doesn't contain 'object' index");
			return false;
		}

		var favorites = {'itemid': 1};
		if (isset(list.object, favorites)) {
			for (var i = 0; i < list.values.length; i++) {
				if (!isset(i, list.values) || empty(list.values[i])) {
					continue;
				}
				create_var('rda_filter', 'itemid[' + list.values[i].itemid + ']', list.values[i].itemid, false);
			}

			$('rda_filter').submit();
		}
	}

	function removeSelectedItems(objId, name) {
		var obj = jQuery('#' + objId);
		if (empty(obj)) {
			return false;
		}

		jQuery('option:selected', obj).each(function(){
			var self = jQuery(this);

			if (jQuery('option', obj).length > 1) {
				jQuery('#' + name + '_' + self.val()).remove();
				self.remove();
			}
			else {
				alert(<%=CJs.encodeJson(Cphp._("Cannot remove all items, at least one item should remain."))%>);
				return false;
			}
		});
	}
</script>
