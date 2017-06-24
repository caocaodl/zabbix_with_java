<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.HC.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.HC.Charts.js"/>  
	 <f:linkJs src="/platform/iradar/js/FusionCharts/FusionCharts.jqueryplugin.js"/>  
	 <f:linkCss src="/platform/iradar/styles/themes/originalblue/iradar.css"/> 
	</f:head>
	<f:iframebody>
		<div>性能TOPN</div>
		
		<div align="right">
			个数：
			<select id="rows" onchange="getCount(this.options[this.options.selectedIndex].value)">
				<option value="10" >10</option>
				<option value="50" >50</option>
				<option value="100" >100</option>
			</select>
		</div>
		
		<e:dgCondition bindGridId="dataTab">
			<f:inputHidden id="top" value="#{vo.top}" styleClass="searchFilter"/>
			<e:dgSearchButton value="导出当前数据" bindGridId="out" id="out" onclick="outCSV()"/>
		</e:dgCondition>
		
		<e:datagrid id="dataTab" url="/platform/iradar/PerformancedoPage.action"  title="性能TOPN" fitColumns="true" pagination="false">
			<e:header>
				<e:column field="hostId" width="300" title="设备ID" align="center" hidden="true"/>
				<e:column field="hostName" width="300" title="设备名称" align="center" formatter="nameFormatter" />
				<e:column field="score" width="160" title="分数" align="center" />
			</e:header>
		</e:datagrid>
	</f:iframebody>
	<script type="text/javascript">
	    var limtSelect = document.getElementById("rows");
	    var limt =$('#top').val();
	   for(var i=0;i<=limtSelect.options.length;i++){
		   if(limtSelect.options[i].value == limt){
			   limtSelect.options[i].selected = 'selected';
			   
		   }
	    }
	  
	     

		var hostId = null;
		
		//导出TOPN数据
		function outCSV(){
			var rows = $("#rows").val();
			window.open("PerformancedoTOPNCSV.action?top="+rows);
		}
		//导出趋势图数据
		function outTrendCSV(){
			var startTime= $('#startTime').datebox('getValue'); 
			var endTime=$('#endTime').datebox('getValue'); 
			window.open("PerformancedoTrendCSV.action?hostId="+hostId+"&startTime="+startTime+"&endTime="+endTime);
		}
		//设置行数
		function getCount(count){
	   		window.location.href="PerformancedoSetRows.action?top="+count;
		}
		function queryTrend(){
			var startTime= $('#startTime').datebox('getValue'); 
			var endTime=$('#endTime').datebox('getValue'); 
			trend(hostId,startTime,endTime);
		}
		function nameFormatter(value, rowData, rowIndex){
			return "<A href='#|' onclick='checkTrend("+rowIndex+")'>"+value+"</A>";
		}
		function checkTrend(rowIndex){
			var dataSource = $("#dataTab").datagrid('getData');
			var data = dataSource.rows[rowIndex];
		    hostId = data.hostId;
			var d = new Date();
			var endTime = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();//当前日期
			var lw = new Date(d.getTime() - 1000 * 60 * 60 * 24 * 7);
			var startTime = lw.getFullYear()+"-"+(lw.getMonth()+1)+"-"+lw.getDate();//一周前日期
			
			var $dialog = $('#trend').show().dialog({
	        	top:30,
	            autoOpen: false,
	            modal: true,
	            resizable:false,
	            height: 'auto',
	            width:'auto',
	            title: "设备性能趋势图"
	        });
	        $dialog.dialog('open');
	        trend(hostId,startTime,endTime);
	        $('#startTime').datebox('setValue',startTime); 
			$('#endTime').datebox('setValue',endTime); 
	    }
		function trend(hostId,startTime,endTime){
			$(function(){
				 $.ajax( {
			         url : "PerformancedoTrend.action?hostId="+hostId+"&startTime="+startTime+"&endTime="+endTime,		         
			         dataType : "json",
			         async:false,
			         success : function(json) {
			        	 $("#trend_chart").insertFusionCharts({
			        			type : "Line",
			        			width : "500",
			        			height : "300",
			        			dataFormat : "json",
			        			dataSource : {
			        				"chart" : {
			        					"caption" : "设备性能趋势图",
			        					"xaxisname" : "",
			        					"yaxisname" : "",
			        					"yaxismaxvalue" : "100",
			        					"yaxisminvalue" : "0",
			        					"numberprefix" : "",
			        					"showvalues" : "0",
			        					"showborder" : "0",
			        					"bgcolor" : "FFFFFF,FFFFFF"
			        				},
			        				"data" : [{
			        					label:"q",value:"50"
			        				},{
			        				label:"w",value:"2"

			        				},{
			        				label:"e",value:"97"

			        				}]
			        				}
								});
							}
					})
			});
	}
	</script>
</f:html>