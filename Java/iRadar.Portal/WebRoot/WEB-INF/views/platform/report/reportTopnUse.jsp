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
<div align="left"><p>使用率TOPN</p></div>
<div>
<div align="right" >
       Top：<select name="select" onchange=count(this.value) id="limt">
        <option value="10">10</option>
        <option value="50">50</option>
        <option value="100">100</option>
        </select>     
     
     
     组件：  <select name="select1"  onchange=element(this.value) id="host">
        <option value="1">cpu</option>
        <option value="2">内存</option>
        <option value="3">磁盘</option> 
        <option value="4">带宽</option>
        
        </select>  
     </div>
     
    
	<div id="inventoriesList" align="center" style="height:auto;"></div>	
	</div>
		<div  id="chars" style="display: none;">
	  时间： <input class="easyui-datebox"    id="timeUp"></input>
	  到： <input class="easyui-datebox"   id="timeDown"></input>
	<f:inputButton value="查询"  onclick="goChart()"/>		
				
	<f:inputButton value="导出线行数据"   onclick="outCSV()"/>	
	   <div id="dialog">
		</div></div>
		<div align="left">
		<e:dgCondition bindGridId="dataTab">	
		<f:inputHidden id="host1" value="#{vo.host}" styleClass="searchFilter"/>
	    <f:inputHidden id="limt1" value="#{vo.limt}" styleClass="searchFilter"/>
			<e:dgSearchButton value="导出列表数据" onclick="outUseCSV()"  bindGridId="out" id="out"/>
			
		</e:dgCondition>
		</div>
		<e:datagrid id="dataTab" url="/platform/iradar/RTUnumbuerLimit.action" title="使用率列表" fitColumns="true" pagination="false">
		
			<e:header>
			    
				<e:column field="hostid" width="270" title="设备名称" align="center" formatter="nameFormatter"  />
				<e:column field="uselv" width="140" title="使用率" align="center"  />	
			</e:header>
			
		</e:datagrid>				
</f:iframebody>
	<script type="text/javascript">

		    var limtSelect = document.getElementById("limt");
		    var hostSelect = document.getElementById("host");
		    var limt =$('#limt1').val();
		    var host=$('#host1').val();
		   for(var i=0;i<=hostSelect.options.length;i++){
			   if(hostSelect.options[i].value == host){
				   hostSelect.options[i].selected = 'selected';
				   for(var j=0;j<=limtSelect.options.length;j++){
					   if(limtSelect.options[j].value == limt){
						   limtSelect.options[j].selected = 'selected';
					   }
				}
			   }
		    }
		  
		    
	   function getList(host,limt ){
		      
		   window.location.href="RTUdoList.action?host="+host+"&limt="+limt;
		 
		   
	   }
	   function count(value){
		   var host=$('#host').val();
		   getList(host,value );  
		 
	   }
	   function element(value){
		   var limt=$('#limt').val();
		   getList(value,limt );  
		   
	   }
	   var host=null;
		function nameFormatter(value, rowData, rowIndex){
			return "<A href='#|' onclick='checkTrend("+rowIndex+")'>"+value+"</A>";
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
			
			
				$(function(){			
					 $.ajax( {
				         url : "RTUgetChars.action?host="+host+"&timeUp="+timeUp
							+"&timeDown="+timeDown,		         
				         dataType : "json",
				         async:false,
				         success : function(json) {
				        	
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

				}
					 })
				});
			
			
		}
		
		
		  //导出趋势线行数据
			function outCSV(){
				var timeUp = $('#timeUp').datebox('getValue') ;
				var timeDown =$('#timeDown').datebox('getValue') ;
				window.open("RTUdoCSV.action?host="+host+"&timeUp="+timeUp
						+"&timeDown="+timeDown);
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
</f:html>