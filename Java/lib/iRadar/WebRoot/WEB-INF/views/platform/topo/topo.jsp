<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp" %>
<%
	String contextPath = session.getServletContext().getContextPath();
    if(contextPath.equals("/"))contextPath = "";
	String actionPath = contextPath + "/platform/iradar/";
	String basePath = contextPath + "/assets/topo/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
	<title>网络拓扑</title>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/ext-3.2.0/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/ext-3.2.0/resources/css/xtheme-blue.css" id="ext-skin" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/loading/loading.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>scripts/ux/ext-patch.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>styles/jbpm4.css" />
	<link rel="stylesheet" type="text/css" href="<%=basePath%>styles/ims.css" />
	
	<script type="text/javascript" src="<%=basePath%>scripts/ext-3.2.0/ext-base.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ext-3.2.0/ext-all.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ext-3.2.0/ext-lang-zh_CN.js"></script>
	<script type='text/javascript' src="<%=basePath%>scripts/ux/localXHR.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-base.js"></script>
	<script type="text/javascript">
		Ext.BLANK_IMAGE_URL = '<%=basePath%>scripts/ext-3.2.0/resources/images/default/s.gif';
		Gef.IMAGE_ROOT = '<%=basePath%>scripts/gef/images/activities/48/';
		Gef.basePath = '';
		Gef.lines = {
			select : '<%=basePath%>scripts/gef/images/activities/select32.png',
			transition : '<%=basePath%>scripts/gef/images/activities/32/flow_sequence.png',
			line : '<%=basePath%>scripts/gef/images/activities/32/flow_line.png',
			dashedArrows : '<%=basePath%>scripts/gef/images/activities/32/flow_dashed.png',
			dashedLine : '<%=basePath%>scripts/gef/images/activities/32/flow_dashedline.png',
			doubleArrowsLine : '<%=basePath%>scripts/gef/images/activities/32/flow_double_arrows_line.png',
			doubleArrowsDashed : '<%=basePath%>scripts/gef/images/activities/32/flow_double_arrows_dashed.png'
		}
		
		Gef._basePath = "<%=actionPath%>";
		Gef._mapId = "";

		//系统初始化背景路径
		Gef.systemBg_url=Gef._basePath+"initbackground.do";
		//系统背景列表路径
		Gef.systemBgList_url=Gef._basePath+"common/getBackground.do";
		//修改系统背景路径
		Gef.systemUpBg_url=Gef._basePath+"changebackground.do";
		
		//topo保存数据路径
		Gef.systemSaveData_url=Gef._basePath+"save.action";
		//topo显示数据路径
		Gef.systemShowData_url=Gef._basePath+"topo.action";
		//左边面板数据
		Gef.leftPanel_url=Gef._basePath+"menu.action";
	</script> 
</head>
<body>
	<div id="loading-mask"></div>
	<div id="loading">
		<div class="loading-indicator">
			<img src="<%=basePath%>scripts/loading/extanim32.gif" align="absmiddle" />正在加载数据...
		</div>
	</div>
	
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-twobase.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-command.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-factory.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-figure.js"></script>
	
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims-tool.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims-editparts.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims-figure.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims-tracker.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-ims-jbs.js"></script>
	
	
	<script type="text/javascript" src="<%=basePath%>scripts/gef/scripts/all-core-line.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/gef/all-editor.js"></script>
	
	<script type="text/javascript" src="<%=basePath%>scripts/ims/0_plugin.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/plugins/menu.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/plugins/opertaion.js"></script>
	
	<script type="text/javascript" src="<%=basePath%>scripts/ims/1_app.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/2_layout.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/3_ui.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/4_left_panel.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/util/queue.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/edge.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/grid.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/left2RightGrid.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/up2DownGrid.js"></script>
	<!-- 
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/oNode.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/oEdge.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/oGraph.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/planarizer.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/flowCompaction.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/orthoRep.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/compactionGraph.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/orthogonalGrid.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/orthogonalGridCell.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/adjacency.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/combinatorialEmbedding.js"></script>
	<script type="text/javascript" src="<%=basePath%>scripts/ims/layouter/orthogonal/orthogonalLayout.js"></script>
	 -->
	<script type="text/javascript" src="<%=basePath%>scripts/form/all-forms-ims.js"></script>
	<script type='text/javascript' src="<%=basePath%>scripts/property/all-property-ims.js"></script>
	
</body>
</html>
