<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp" %>
<%@ taglib prefix="f" uri="/isoft/core"%>
<f:html>
<link rel="shortcut icon" href="iradar/images/general/iradar.ico" />
<f:head>
	<f:linkCss src="/assets/f/core/css/workspace.css" />
	<f:linkCss src="/assets/c/core/css/workspace.css" />
	<f:linkJs src="/assets/f/core/js/workspace.js" />
	<f:linkJs src="/assets/c/core/js/fix.js" />
</f:head>
<body class="easyui-layout">
	<div data-options="region:'north'" style="height:100px" class="layout_north"><%@ include file="workspaceTop.jsp" %></div>
	<div data-options="region:'west', split:0" style="width:260px;" id="JS_layoutWest">
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
</body>
</f:html>
