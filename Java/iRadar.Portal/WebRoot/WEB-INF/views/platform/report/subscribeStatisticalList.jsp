<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.HC.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.HC.Charts.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.jqueryplugin.js"/>   
	</f:head>
	<f:iframebody>
		<e:dgCondition bindGridId="dataTab">
			<e:dgSearchButton value="导出当前数据" bindGridId="out" id="out" onclick="outReportCSV()"/>
		</e:dgCondition>
		<div id="trend_chart"></div>
		<e:datagrid id="dataTab" url="/platform/iradar/SubscribeStatisticaldoData.action" title="监控模型订阅统计" fitColumns="true" pagination="false">
			<e:header>
				<e:column field="hostName" width="300" title="监控模型" align="center"/>
				<e:column field="score" width="160" title="订阅数量" align="center" />
			</e:header>
		</e:datagrid>
		
	</f:iframebody>
	<script type="text/javascript">
	$(document).ready(function(){
			$(function(){
				 $.ajax( {
			         url : "SubscribeStatisticaldoData.action",	         
			         dataType : "json",
			         async:false,
			         success : function(json) {
			        	 $("#trend_chart").insertFusionCharts({
			        			type : "column3d",
			        			width : "1050",
			        			height : "600",
			        			dataFormat : "json",
			        			dataSource : 
			        			{
			        			    "chart": {
			        			        "caption": "监控模型订阅统计",
			        			        "yaxisname": "",
			        			        "bgcolor": "#FFFFFF",
			        			        "showvalues": "1",
			        			        "labeldisplay": "WRAP",
			        			        "divlinecolor": "#CCCCCC",
			        			        "divlinealpha": "70",
			        			        "useroundedges": "1",
			        			        "canvasbgcolor": "#FFFFFF",
			        			        "canvasbasecolor": "#CCCCCC",
			        			        "showcanvasbg": "0",
			        			        "palettecolors": "#008ee4,#6baa01,#f8bd19,#e44a00,#33bdda",
			        			        "showborder": "0"
			        			    },
			        			    "data": [
			        			        {
			        			            "label": "模型1",
			        			            "value": "5"
			        			        },
			        			        {
			        			            "label": "模型2",
			        			            "value": "6"
			        			        },
			        			        {
			        			            "label": "模型3",
			        			            "value": "7"
			        			        },
			        			        {
			        			            "label": "模型4",
			        			            "value": "8"
			        			        },
			        			        {
			        			            "label": "模型5",
			        			            "value": "9"
			        			        }
			        			    ]
			        			}
								});
							}
					})
			});		
	});
	//导出趋势图数据
	function outReportCSV(){
		window.open("SubscribeStatisticaldoReportCSV.action");
	}
	
	</script>
</f:html>