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
			开始日期：<input class="easyui-datebox" id="startTime" >
			结束日期：<input class="easyui-datebox" id="endTime" >
			<e:dgSearchButton value="查询" bindGridId="out" id="out" onclick="queryTrend()" />
			<e:dgSearchButton value="导出当前数据" bindGridId="out" id="out" onclick="outReportCSV()"/>
		</e:dgCondition>
		<div id="trend"></div>
	</f:iframebody>
	<script type="text/javascript">
	$(document).ready(
		function aa(){
			var d = new Date();
			var endTime = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();//当前日期
			var lw = new Date(d.getTime() - 1000 * 60 * 60 * 24 * 7);
			var startTime = lw.getFullYear()+"-"+(lw.getMonth()+1)+"-"+lw.getDate();//一周前日期
			$('#startTime').datebox('setValue',startTime); 
			$('#endTime').datebox('setValue',endTime); 
			trend(startTime,endTime)
		}
	);
	
	//导出趋势图数据
	function outReportCSV(){
		var startTime= $('#startTime').datebox('getValue'); 
		var endTime=$('#endTime').datebox('getValue'); 
		window.open("SubscribeCurrentdoReportCSV.action?startTime="+startTime+"&endTime="+endTime);
	}
	
	function queryTrend(){
		var startTime= $('#startTime').datebox('getValue'); 
		var endTime=$('#endTime').datebox('getValue'); 
		trend(startTime,endTime);
	}
	
	function trend(startTime,endTime){
		$(function(){
			 $.ajax( {
		         url : "SubscribeCurrentdoReport.action?startTime="+startTime+"&endTime="+endTime,	         
		         dataType : "json",
		         async:false,
		         success : function(json) {
		        	 $("#trend").insertFusionCharts({
		        			type : "MSLine",
		        			width : "1050",
		        			height : "600",
		        			dataFormat : "json",
		        			dataSource : 
		        			{
		        			    "chart": {
		        			        "caption": "监控服务订阅趋势",
		        			        "bgcolor": "FFFFFF",
		        			        "showalternatehgridcolor": "0",
		        			        "divlinecolor": "CCCCCC",
		        			        "showvalues": "0",
		        			        "showcanvasborder": "0",
		        			        "canvasborderalpha": "0",
		        			        "canvasbordercolor": "CCCCCC",
		        			        "canvasborderthickness": "1",
		        			        "yaxismaxvalue": "100",
		        			        "linethickness": "3",
		        			        "yaxisvaluespadding": "15",
		        			        "legendshadow": "0",
		        			        "legendborderalpha": "0",
		        			        "palettecolors": "#f8bd19,#008ee4,#33bdda,#e44a00,#6baa01,#583e78",
		        			        "showborder": "0"
		        			    },
		        			    "categories": [
		        			        {
		        			            "category": [
		        			                {
		        			                    "label": "one"
		        			                },
		        			                {
		        			                    "label": "two"
		        			                },
		        			                {
		        			                    "label": "three"
		        			                },
		        			                {
		        			                    "label": "four"
		        			                },
		        			                {
		        			                    "label": "five"
		        			                },
		        			                {
		        			                    "label": "six"
		        			                },
		        			                {
		        			                    "label": "seven"
		        			                }
		        			            ]
		        			        }
		        			    ],
		        			    "dataset": [
		        			        {
		        			            "seriesname": "服务1",
		        			            "data": [
		        			                {
		        			                    "value": "50"
		        			                },
		        			                {
		        			                    "value": "90"
		        			                },
		        			                {
		        			                    "value": "60"
		        			                },
		        			                {
		        			                    "value": "80"
		        			                },
		        			                {
		        			                    "value": "65"
		        			                },
		        			                {
		        			                    "value": "50"
		        			                },
		        			                {
		        			                    "value": "35"
		        			                }
		        			            ]
		        			        },
		        			        {
		        			            "seriesname": "服务2",
		        			            "data": [
		        			                {
		        			                    "value": "20"
		        			                },
		        			                {
		        			                    "value": "30"
		        			                },
		        			                {
		        			                    "value": "8"
		        			                },
		        			                {
		        			                    "value": "40"
		        			                },
		        			                {
		        			                    "value": "70"
		        			                },
		        			                {
		        			                    "value": "50"
		        			                },
		        			                {
		        			                    "value": "30"
		        			                }
		        			            ]
		        			        },
		        			        {
		        			            "seriesname": "服务3",
		        			            "data": [
		        			                {
		        			                    "value": "10"
		        			                },
		        			                {
		        			                    "value": "20"
		        			                },
		        			                {
		        			                    "value": "30"
		        			                },
		        			                {
		        			                    "value": "40"
		        			                },
		        			                {
		        			                    "value": "50"
		        			                },
		        			                {
		        			                    "value": "60"
		        			                },
		        			                {
		        			                    "value": "70"
		        			                }
		        			            ]
		        			        }
		        			    ]
		        			}
							});
						}
				})
		});
}
	</script>
</f:html>