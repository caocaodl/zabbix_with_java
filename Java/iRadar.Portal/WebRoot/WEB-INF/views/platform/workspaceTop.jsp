<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="f" uri="/isoft/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="header_top">
	<div class="expand"></div>
	<div class="logo"></div>
	<div class="notice"></div> 
	<ul class="userinfo">
		<li class="spliter"><a href="#" class="icon user">${idBean.userName}</a>欢迎您！</li>
		<li class="spliter"><a target="_blank" class="down" title="监控客户端下载" href="../agents.html">&nbsp;</a></li>
		<li class="spliter end"><a href="../logout.action" class="exit" title="退出">&nbsp;</a></li>
	</ul>
</div>

<s:if test="#attr.role == 'lessor'">
	<f:navMenu nav="0001" homeTitle="首页概览" homeUrl="/platform/iradar/dashboards.action"/>
</s:if><s:else>
	<f:navMenu nav="0001" homeTitle="首页概览" homeUrl="/platform/iradar/tenantdashboards.action"/>
</s:else>
