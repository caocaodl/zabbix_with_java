<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp"%>
<%@ taglib prefix="f" uri="/isoft/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<f:html>
<head>
	<title>全局预览</title>
	<meta name="renderer" content="webkit" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
	<meta name="Author" content="i-soft" />
	<meta charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="styles/default.css" />
	<link rel="stylesheet" type="text/css" href="styles/color.css" />
	<link rel="stylesheet" type="text/css" href="styles/icon.css" />
	<link rel="stylesheet" type="text/css" href="styles/blocks.css" />
	<link rel="stylesheet" type="text/css" href="styles/pages.css" />
	<link rel="stylesheet" type="text/css" href="styles/themes/originalblue/main.css" />
	<link rel="stylesheet" type="text/css" href="styles/themes/originalblue/iradar.css" />
	<link rel="stylesheet" type="text/css" href="styles/themes/originalblue/dashboard.css" />
	
	<script src="js/browsers.js"></script>
	<script src="jsLoader.action?ver=1.3.1&lang=en_US&showGuiMessaging=0"></script>
	<script src="jsLoader.action?ver=1.3.1&lang=en_US&showGuiMessaging=0&files[]=class.pmaster.js"></script>
	
	<!-- <f:linkCss src="/platform/iradar/styles/themes/originalblue/jquery-ui.css" /> -->
	<f:linkCss src="/assets/c/v_tenant/tenantdashboard.css" />
	<f:linkCss src="/assets/c/v_tenant/tenantdashboardstyle.css" />

	<f:linkJs src="/assets/c/import/echarts-2.2.1/echarts-all.js" />
	<f:linkJs src="/assets/c/import/highcharts-4.1.5/highcharts.js" />
	<f:linkJs src="/assets/c/import/highcharts-4.1.5/highcharts-more.js" />
	<f:linkJs src="/assets/c/v_tenant/tenantdashboard.js" />
</head>
<body class="originalblue">
	<div class="col-xs-12">
		<div class="table_wrapper control_basic_position">
			<div id="content">
				<!-- 第一行 -->
				<div class="platform_information cell colspan11 first">
					<label>配额分配情况</label>
					<div>
						<div class="information_user ctn">
							<div id="cloudHost"></div>
							<span>云主机(个)</span>
						</div>
						<div class="information_machine ctn">
							<div id="virtual_kernel"></div>
							<span>虚拟内核(个)</span>
						</div>
						<div class="information_physics ctn">
							<div id="memoryInfo"></div>
							<span>内存(MB)</span>
						</div>
						<div class="information_mem_subnet ctn">
							<div id="subnet"></div>
							<span>子网(个)</span>
						</div>
						<div class="information_mem_port ctn">
							<div id="port"></div>
							<span>端口(个)</span>
						</div>
						<div class="information_mem_router ctn">
							<div id="router"></div>
							<span>路由器(个)</span>
						</div>
						<div class="information_mem_float_ip ctn">
							<div id="float_ip"></div>
							<span>浮动IP(个)</span>
						</div>
						<div class="information_security_group ctn">
							<div id="security_group"></div>
							<span>安全组(个)</span>
						</div>
						<div class="information_security_group_rule ctn">
							<div id="security_group_rule"></div>
							<span>安全组规则(个)</span>
						</div>
						
						<div class="clear"></div>
					</div>
				</div>
				<div class="resource_use_rate cell colspan1">
					<label>公告 <span id="more" title="更多"  class="iconmenu">更多</span></label>
					<div style="height: inherit; overflow: auto;"><s:property value="#attr.notice" escapeHtml="false" /></div>
				</div>
				
				
				<!-- 第二行 -->
				<div class="cloud_service_state cell colspan1 first">
					<label>监控状态</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="server_cpuRate_top5 cell colspan1">
					<label>云主机CPU利用率TOP5</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="server_memoryRate_top5 cell colspan1">
					<label>云主机内存利用率TOP5</label>
					<div class="echart_ctn"></div>
				</div>
				
				
				<!-- 第三行 -->
				<div class="triggerTrend cell colspan1 first">
					<label>告警产生趋势</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="last_event cell colspan2">
					<label>最近前5个告警</label>
					<div><s:property value="#attr.issues" escapeHtml="false" /></div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	<s:property value="#attr.js" escapeHtml="false" />
	function addPopupValues(list) {
        if (!isset('object', list)) {
            throw("Error hash attribute 'list' doesn't contain 'object' index");
            return false;
        }
        if ('undefined' == typeof(Ajax)) {
            throw('Prototype.js lib is required!');
            return false;
        }

        var favorites = {graphid: 1, itemid: 1, screenid: 1, slideshowid: 1, sysmapid: 1};

        if (isset(list.object, favorites)) {
            var favid = [];
            for (var i = 0; i < list.values.length; i++) {
                favid.push(list.values[i][list.object]);
            }

            var params = {
                'favobj': list.object,
                'favid[]': favid,
                'favaction': 'add'
            };
            send_params(params);
        }
    }
	</script>
	
</body>
</f:html>
