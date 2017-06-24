<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp" %>
<%
    String contextPath = session.getServletContext().getContextPath();
    if(contextPath.equals("/"))contextPath = "";
	
	String basePath = contextPath + "/assets/topo/";
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<title>物理链路拓扑</title>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/soft/base/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/soft/base/demo.css" />	
	<script type="text/javascript" src="<%=basePath%>scripts/jquery/jquery-1.8.2.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/qunee-min.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/graphs.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/common.js"></script>	
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/barui.js"></script>	
	<script type="text/javascript" src="<%=basePath%>scripts/soft/base/alarmballoon.js"></script>
	<script type="text/javascript">
		var basePath="<%=basePath%>";		
	</script> 
   
</head>
<body class="dashboards-topo">	
	<div id="graph_panel" class="q-panel">
		<div class="q-toolbar">
			<div id="toolbar"></div>
		</div>
		<div id="canvas" style=" margin:0 auto;"></div>
	</div>
	<script type="text/javascript" src="<%=basePath%>scripts/soft/config/SoftTopo.LoadScript.js"></script>
	<script type="text/javascript" src="<%=basePath%>physicaltopo/dashboardsData/SoftTopo.AppConfig.js"></script>
	<script type="text/javascript" src="<%=basePath%>physicaltopo/dashboardsData/SoftTopo.AjaxUrl.js"></script>
	<script type="text/javascript" src="<%=basePath%>physicaltopo/dashboardsData/SoftTopo.AjaxData.js"></script>
	<script type="text/javascript" src="<%=basePath%>physicaltopo/dashboardsData/SoftTopo.Data.js"></script>
</body>
</html>
	