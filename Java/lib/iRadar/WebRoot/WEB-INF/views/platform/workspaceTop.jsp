<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="f" uri="/isoft/core"%>
<div class="header_top">
	<div class="logo"></div>
	<ul class="userinfo">
		<li class="spliter"><a href="#" class="icon user">${idBean.userName}</a>欢迎您！</li>
		<li class="spliter end"><a href="../index.action">退出</a></li>
	</ul>
</div>
<f:navMenu nav="0001" homeTitle="预警中心" homeUrl="/platform/iradar/tr_status.action" />