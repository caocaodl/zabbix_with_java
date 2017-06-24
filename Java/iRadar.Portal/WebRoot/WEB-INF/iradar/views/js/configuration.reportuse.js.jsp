<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.types.CArray"%>
<%@ page import="com.isoft.iradar.inc.JsUtil"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%
  Map data = (Map)request.getAttribute("data");
%>
<script type="text/javascript">

function getList(host,limt ){
    
	   window.location.href="TopnUseTwo.action?host="+host+"&limt="+limt;	   
}
function count(value){
	   var host=jQuery('#host').val();
	   getList(host,value );  
	 
}
function element(value){
	   var limt=jQuery('#limt').val();
	   getList(value,limt );  
	   
}

function checkTrend(rowIndex){
	var d = new Date();
		var timeDown = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();//当前日期
		var lw = new Date(d.getTime() - 1000 * 60 * 60 * 24 * 7);
		var  timeUp = lw.getFullYear()+"-"+(lw.getMonth()+1)+"-"+lw.getDate();//一周前日期
		var dataSource = $("#dataTab").datagrid('getData');
		 host = dataSource.rows[rowIndex].hostid;
		
		  var $dialog = $('#chars').show().dialog({
		        autoOpen: false,
		        modal: true,
		        top:30,
		        resizable:false,
		        height: 'auto',
		        width:'100%',
		        title: "趋势图",
		      
		    });
		    $dialog.dialog('open');
		    $('#timeUp').datebox('setValue',timeUp); 
			$('#timeDown').datebox('setValue',timeDown); 
		    tend(host,timeUp,timeDown);
		 
}
	function tend(host,timeUp,timeDown){
		
		
		jQuery(function(){			
			jQuery.ajax( {
			         url : "RTUgetChars.action?host="+host+"&timeUp="+timeUp
						+"&timeDown="+timeDown,		         
			         dataType : "json",
			         async:false,
			         success : function(json) {
			        	
			        	 jQuery("#dialog").insertFusionCharts({
			        			type : "Line",
			        			width : "500",
			        			height : "300",

			        			dataFormat : "json",
			        			dataSource : {
			        				"chart" : {
			        					"caption" : "平台",
			        					"xaxisname" : "",
			        					"yaxisname" : "",
			        					"yaxismaxvalue" : "100",
			        					"yaxisminvalue" : "0",
			        					"numberprefix" : "",
			        					"showvalues" : "",
			        					"showborder" : "1",
			        					"showNames" : "1",
			        					
			        					"bgcolor" : "FFFFFF,FFFFFF"
			        				},
			        				"data" : [{
			        					label:"周一",value:"50"
			        				},{
			        				label:"周二",value:"2"

			        				},{
			        				label:"周三",value:"97"

			        				}]
			        			}
			        		});

			}
				 })
			});
		
		
	}
	
	
	  //导出趋势线行数据
		function outCSV(){
			$(function(){						        	
			        	 $("#dialog").insertFusionCharts({
			        			type : "Line",
			        			width : "500",
			        			height : "300",

			        			dataFormat : "json",
			        			dataSource : {
			        				"chart" : {
			        					"caption" : "平台",
			        					"xaxisname" : "",
			        					"yaxisname" : "",
			        					"yaxismaxvalue" : "100",
			        					"yaxisminvalue" : "0",
			        					"numberprefix" : "",
			        					"showvalues" : "",
			        					"showborder" : "1",
			        					"showNames" : "1",
			        					
			        					"bgcolor" : "FFFFFF,FFFFFF"
			        				},
			        				"data" : [{
			        					label:"周一",value:"50"
			        				},{
			        				label:"周二",value:"2"

			        				},{
			        				label:"周三",value:"97"

			        				}]
			        			}
			        		});

			
				
			});
		//	var timeUp = $('#timeUp').datebox('getValue') ;
		//	var timeDown =$('#timeDown').datebox('getValue') ;
		//	window.open("RTUdoCSV.action?host="+host+"&timeUp="+timeUp
		//			+"&timeDown="+timeDown);
		}
	          //     导出当前列表
		function outUseCSV(){
			var host=document.getElementById("host").value ; 
			var limt=document.getElementById("limt").value ; 
		//var host=window.parent.document.getElementById("host").value;	
		//var limt=window.parent.document.getElementById("limt").value;	  
		  
		
			
			window.open("RTUdoUseCSV.action?host="+host+"&limt="+limt
					);
		}
		function goChart(){
			
			var timeUp = $('#timeUp').datebox('getValue') ;
			var timeDown =$('#timeDown').datebox('getValue') ;
			tend(host,timeUp,timeDown);				
		}
</script>