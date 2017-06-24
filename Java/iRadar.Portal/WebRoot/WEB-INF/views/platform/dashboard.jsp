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
	<f:linkCss src="/assets/c/v_admin/dashboard/dashboard.css" />
	<f:linkCss src="/assets/c/v_admin/dashboard/dashboardstyle.css" />

	<f:linkJs src="/assets/c/import/echarts-2.2.1/echarts-all.js" />
	<f:linkJs src="/assets/c/import/highcharts-4.1.5/highcharts.js" />
	<f:linkJs src="/assets/c/import/highcharts-4.1.5/highcharts-more.js" />
	<f:linkJs src="/assets/c/v_admin/dashboard/dashboard.js" />
	<style type="text/css"><%=request.getAttribute("severityCss")%></style>	
</head>
<body class="originalblue">
	<div class="col-xs-12">
		<div class="table_wrapper control_basic_position">
			<div id="content">
				<!-- 第一行 -->
				<div class="platform_information cell colspan1 first">
					<label>平台信息统计</label>
					<div>
						<div class="information_server ctn">
							<div id="information_server"></div>
							<span>服务器</span>
						</div>
						<div class="information_switch ctn">
							<div id="information_switch"></div>
							<span>交换机</span>
						</div>
						<div class="information_storage ctn">
							<div id="information_storage"></div>
							<span>存储设备</span>
						</div>
						<div class="information_core ctn">
							<div id="information_core">
							</div>
							<span>虚拟内核</span>
						</div>
						<div class="information_mem ctn">
							<div id="information_mem"></div>
							<span>内存</span>
						</div>
						<div class="information_storageCapacity ctn">
							<div id="information_storageCapacity"></div>
							<span>存储容量</span>
						</div>
						<div class="information_user ctn">
							<div id="information_user"></div>
							<span>租户</span>
						</div>
						<div class="information_machine ctn">
							<div id="information_machine"></div>
							<span>云主机</span>
						</div>
						<div class="information_image ctn">
							<div id="information_image"></div>
							<span>镜像</span>
						</div>
						<%--
						<div class="information_physics ctn">
							<div id="information_physics"></div>
							<span>计算节点数</span>
						</div>
						--%><div class="clear"></div>
					</div>
				</div>
				<div class="platform_health cell colspan1">
					<label>健康度</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="resource_use_rate cell colspan1">
					<label>资源利用率</label>
					<div class="echart_ctn"></div>
				</div>
				
				<!-- 第二行 -->
				<div class="cloud_service_state cell colspan1 first">
					<label>平台服务状态</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="server_load_top5 cell colspan1">
					<label>网络拓扑<span title="详情" onclick="javascript:top.jQuery.workspace.openTab('物理链路拓扑', '/imon/platform/iradar/NetTopoPhyIndex.action')" class="iconmenu menu_icon shadow">&nbsp;</span></label>
					<div class="echart_ctn">
						<iframe src="NetTopoPhyDashIndex.action" width="99.9%" height="100%" scrolling="no"></iframe>
					</div>
				</div>
				<div class="resource_use_trend cell colspan1">
					<label>资源使用趋势</label>
					<div class="echart_ctn"></div>
				</div>
				
				<!-- 第三行 -->
				<div class="system_status cell colspan1 first">
					<label>系统状态</label>
					<div class="echart_ctn"></div>
				</div>
				<div class="last_event cell colspan2">
					<label>最近发生的告警</label>
					<div><s:property value="#attr.issues" escapeHtml="false" /></div>
				</div>
				
				<!-- 第四行 -->
				<div class="graph_report cell first">
					<label>图形报表<span title="菜单" onclick="javascript:create_page_menu(event,'graphs',130);" class="iconmenu menu_icon shadow">&nbsp;</span></label>
					<div id="hat_favgrph"><s:property value="#attr.graph" escapeHtml="false" /></div>
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
