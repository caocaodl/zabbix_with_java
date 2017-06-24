<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<f:linkJs src="/assets/c/header/head.js" />
<div id='container'>
<!-- top-bar -->
<div id="top-bar">
	<a href="/home/" class="brand" title="电科凌云·安全云操作系统"></a>
	<div class="first-nav">
		<ul>
			<s:iterator value="#attr.rda_menu.main" var="nav_" status="navStatus">
				<s:set var="nav" value="#nav_.value" scope="request" />
				<s:set var="navStatus" value="#navStatus" scope="request" />
				<li class="${navStatus.index==rda_menu.activeMainIndex? 'active': ''}">
					<a href="${rda_menu.ctxPath}${nav.funcUrl? nav.funcUrl: nav.subFuncList[0].funcUrl}?ddrest=1">
						<span class="text">${nav.funcName}</span>
						<span class="over">${nav.funcName}</span>
					</a>
				</li>
			</s:iterator>
		</ul>
	</div>
	<div class="user-info">
		<div id="profile_editor_switcher" class="dropdown switcher_bar" tabindex='1'>
			<button class="dropdown-toggle" title="admin">
				<span class="avatar wish-manager bigger-150"></span> 管理员：${rda_menu.idBean.userName}
			</button>
			<ul id="editor_list" class="dropdown-menu pull-right">
				<li><a href="${rda_menu.ctxPath}/agents.html" target="_blank">监控客户端下载</a></li>
				<li><a href="${rda_menu.ctxPath}/logout.action">退出</a></li>
			</ul>
			<div class="san_left"></div>
		</div>
	</div>
	<div class="header_top"><div class="notice"></div></div>
</div>
<!-- /top-bar -->

<div id='main_content'>
	<!-- sidebar -->
	<div id="sidebar" class="sidebar">
		<div id="second-nav" class="second-nav" style="height: 565px;">
			<div class="bar-top"></div>
			<div class="bar-middle">
				<div class="slider" style="top: 110px;"></div>
				<ul>
					<s:iterator value="#attr.rda_menu.sub" var="nav_" status="navStatus">
						<s:set var="nav" value="#nav_.value" scope="request" />
						<s:set var="navStatus" value="#navStatus" scope="request" />
						<li class="${nav.contains(rda_menu.curUri)? 'active': ''}" id="${nav.id}">
							<a title="${nav.funcName}" href="${rda_menu.ctxPath}${nav.funcUrl}?ddrest=1">
								<span class="menu-icon bigger-120 wish-overview_monitor ${nav.iconClass}"></span>
								${nav.funcName}
							</a>
						</li>
					</s:iterator>
				</ul>
			</div>
			<div class="bar-bottom"></div>
		</div>
	</div>
	<script type="text/javascript">jQuery(function($){
		function setSplitBarHeight(){
			var contentHeight=$("#content_body").height()+15;
			var visualHeight=$(window).height()-$("#top-bar").height();
			var navHeight=contentHeight>visualHeight?contentHeight:visualHeight;
			$("#second-nav").css("height",navHeight-60);
		}
		$(window).resize(setSplitBarHeight);
		setSplitBarHeight();
		function setSliderTop(id,action){
			var i=0;
			$("#second-nav ul li").each(function(){
				if($(this).attr("id")==id){
					return false;
				}else{
					i++;
				}
			});
			if(action=="animate"){
				$('.slider').stop(true,false).animate({top:110+i*40},200);
			}else if(action=="css"){
				$(".slider").css("top",110+i*40);
			}
		}
		$("#second-nav ul li").hover(function(){
			setSliderTop($(this).attr("id"),"animate");
		},function(){
			setSliderTop($("#second-nav ul li.active").attr("id"),"animate");
		});
		setSliderTop($("#second-nav ul li.active").attr("id"),"css");
		$("#top-bar .first-nav ul li:not('.active')").hover(function(){
			$(this).find("a span.over").css("border-top-width","3px").css("z-index","1").animate({height:'70px'},100);
		},function(){
			$(this).find("a span.over").css("height","0").animate({borderTopWidth:'0px'},100).css("z-index","-1");
		});
		$(".user-info").hover(function(){
			$("#editor_list").show();
		},function(){
			$("#editor_list").hide();
		});
	});</script>
	<!-- /sidebar -->	
		
	<div id="content_body">