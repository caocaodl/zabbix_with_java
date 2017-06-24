<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp" %>
<%@ taglib prefix="f" uri="/isoft/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
response.setHeader("Pragma","No-cache");  
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Cache-Control", "no-store");
response.setDateHeader("Expires", 0);  
%>
<f:html>
<f:head>
	<s:if test="#attr.role != 'lessor'"><script>var isTenant = true;</script></s:if>
	<f:linkCss src="/assets/f/core/css/workspace.css" />
	<f:linkCss src="/assets/c/core/css/workspace.css" />
	<f:linkCss src="/assets/c/import/jquery-slidingmenu/css/jquery-sliding-menu.css" />
	<f:linkCss src="/assets/c/core/css/isoft.percent.css" />

	<f:linkJs src="/assets/c/import/jquery-history/jquery.history.js" />
	<f:linkJs src="/assets/f/core/js/workspace.js" />
	<f:linkJs src="/assets/c/import/jquery-slidingmenu/js/jquery-sliding-menu.js" />
	<f:linkJs src="/assets/c/header/head.js" />
	
	<f:linkJs src="/platform/iradar/js/servercheck.js" />
	
	
	<meta http-equiv="Expires" content="0">
	<meta http-equiv="Cache-Control" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-store">
	<meta http-equiv="Pragma" content="no-cache">
</f:head>
<body class="easyui-layout">
	<div data-options="region:'north'" style="height:70px" class="layout_north"><%@ include file="workspaceTop.jsp" %></div>
	<div data-options="region:'west', split:0" style="width:191px;" id="JS_layoutWest">
		<div class="west_tree">
			<div class="title_ctn">
				<div class="title"></div>
				<ul class="cmds">
					<li class="filter"><input placeholder="请输入要查找的内容" /></li>
					<li class="collapse"><a href="#|">&lt;&lt;</a></li>
				</ul>
			</div>
			<div class="body"></div>
		</div>
		<div class="west_menu hide">
			<div class="title_ctn">
				<div class="title"></div>
				<ul class="cmds">
					<li class="expand"><a href="#|">&gt;&gt;</a></li>
				</ul>
			</div>
			<div class="body"></div>
		</div>
	</div>
	<div data-options="region:'center'"><div id="JS_contentTab"></div></div>
	<div id="message-global-wrap"><div id="message-global"></div></div>
</body>
</f:html>
