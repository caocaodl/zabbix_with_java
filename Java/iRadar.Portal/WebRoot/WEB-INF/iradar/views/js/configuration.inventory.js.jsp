<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.types.CArray"%>
<%@ page import="com.isoft.iradar.inc.JsUtil"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%
  Map data = (Map)request.getAttribute("data");
%>
<script type="text/javascript">

function selected(value){
	jQuery.post("Cabinetcabinet.action?seq_no="+value+"&type=cabinet", function(data) {
		if(value){
			var dataObj=eval("("+data+")");//转换为json对象 
			jQuery("#cabinet").empty();
			jQuery("#cabinet").append("<option class='sel_font_type' value=''>请选择</option>");
			jQuery.each(dataObj, function(i, item) {
				
				jQuery("#cabinet").append("<option value='"+item.dkey+"'>"+item.dlabel+"</option>");
	        });
		}else{
			jQuery("#cabinet").empty();
			jQuery("#cabinet").append("<option class='sel_font_type' value=''>请选择</option>");
		}
	}); 
	
}

</script>