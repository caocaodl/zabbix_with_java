<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="f" uri="/isoft/core"%>
<div class="header_top">
	<div class="logo"></div>
	<ul class="userinfo">
		<li class="spliter"><a href="#" class="icon user">${idBean.userName}</a>欢迎您！</li>
		<li class="spliter"><a href="javascript:$.workspace.openTab('demo', '../demo.action')">工具</a></li>
		<li class="spliter end"><a href="../index.action">注销</a></li>
	</ul>
</div>
<f:navMenu nav="0101" homeTitle="角色管理" homeUrl="/platform/RoleIndex.action" />