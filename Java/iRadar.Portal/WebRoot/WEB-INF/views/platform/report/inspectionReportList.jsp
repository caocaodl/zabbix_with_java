<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
		<f:linkJs src="/assets/f/import/jQueryValidate/js/jquery.validate.js"/>
		<f:linkJs src="/assets/f/import/jQueryValidate/js/additional-methods.js"/>
		<f:linkJs src="/assets/f/import/jQueryValidate/js/messages_cn.js"/>
		<f:linkCss src="/assets/f/import/jQueryValidate/css/jquery.validate.css"/>
	</f:head>		
	<f:iframebody>
		<e:dgCondition bindGridId="dataTab">
			名称：<f:inputText id="name" styleClass="searchFilter" />
			<e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict" />
		</e:dgCondition>		
		<e:datagrid id="dataTab" url="/platform/iradar/InspectionReportPage.action" title="巡检报告列表" fitColumns="true">
			<e:header>
				<e:column field="id" width="80" title="报告id" hidden="true"/>
				<e:column field="name" width="270" title="名称" align="center"/>
				<e:column field="time" formatter="#{selectItem.HOUR}" width="140" title="时间" align="center"/>
				<e:column field="frequent" formatter="#{selectItem.INSPECTION_FREQUENT}" width="140" title="频度" align="center"/>
				<e:column field="status" formatter="#{selectItem.INSPECTION_STATUS}" width="140" title="状态" align="center"/>
				<e:column field="description" width="140" title="备注" align="center"/>
			</e:header>
			<e:dgbutton caption="新增" icon="shuffle" onClick="goAdd"/>
		    <e:dgbutton caption="编辑" icon="shuffle" onClick="goUpdate"/>
		    <e:dgbutton caption="停用" icon="shuffle" onClick="goForbid"/>
		    <e:dgbutton caption="启用" icon="shuffle" onClick="goResume"/>
		    <e:dgbutton caption="历史数据" icon="shuffle" onClick="goHistory"/>
		</e:datagrid>	
		
		<div id="cu-widget" style="display:none;">				
			<f:block style="width: 95%;">
			    <h1 id="title"></h1>
			    <hr/>
			    	<form action="" method="post" name="form1" id="form1">
					    <table class="tableblock">
					        <tr>
					            <th>名称:</th>
					            <td>
					            	<input type="hidden" id="id" class="field" value="#{vo.id}"/>
					            	<input type="text" id="name" name="name" class="field" value="#{vo.name}" maxlength="128" />
					            </td>
					        </tr>
					        <tr>
					            <th>时间:</th>
					            <td>
					            	<f:selectOneMenu id="time" styleClass="field" value="#{vo.time}">
										<f:selectItems value="#{selectItem.HOUR}"/>
									</f:selectOneMenu>
					            </td>
					        </tr>
					        <tr>
					        	<th>频度：</th>
					        	<td>
					            	<f:selectOneMenu id="frequent" styleClass="field"  value="#{vo.frequent}" onchange="changeFrequent(this.value);">
										<f:selectItems value="#{selectItem.INSPECTION_FREQUENT}"/>
									</f:selectOneMenu>		        		
					        	</td>
					        </tr>
					        <tr id="tr_week" style="display: none">
					        	<th>周：</th>
					        	<td>
					            	<f:selectOneMenu id="week" styleClass="field"  value="#{vo.week}">
										<f:selectItems value="#{selectItem.WEEK}"/>
									</f:selectOneMenu>		        		
					        	</td>			        	
					        </tr>					        
					        <tr id="tr_day" style="display: none">
					        	<th>日期：</th>
					        	<td>
					            	<f:selectOneMenu id="day" styleClass="field"  value="#{vo.day}">
										<f:selectItems value="#{selectItem.DAY}"/>
									</f:selectOneMenu>		        		
					        	</td>			        	
					        </tr>
					        <tr>
					        	<th>监控内容：</th>
					        	<td>		        		
					            	<input type="button" value="设置监控项" onclick="addMonitor()"/>
					            	<input type="hidden" id="hostIds" class="field" value=""/>
					        	</td>			        	
					        </tr>	
					        <tr>
					        	<th>是否启用：</th>
					        	<td>
					            	<f:selectOneMenu id="status" styleClass="field"  value="#{vo.status}">
										<f:selectItems value="#{selectItem.INSPECTION_STATUS}"/>
									</f:selectOneMenu>		        		
					        	</td>			        	
					        </tr>	
					        <tr>
					        <th>发送方式：</th>
					        	<td>
					            	<f:selectOneMenu id="sendMode" styleClass="field"  value="#{vo.sendMode}">
					            		<f:selectItems value="#{selectItem.INSPECTION_SENDMODE}"/>
									</f:selectOneMenu>		        		
					        	</td>			        	
					        </tr>			        		        
					        <tr>
					        	<th>备注：</th>
					        	<td>
					        		<f:inputTextArea id="description" styleClass="field" value="#{vo.description}"/>    		
					        	</td>			        	
					        </tr>			        			        
					    </table>
			    	<hr/>
			        <f:inputButton value="保存" style="float:right" id="do-save"/>
			    </form>
			</f:block>	
			
			<div id="if-widget" style="display:none;">
			<iframe scrolling="no" frameborder="0" style="width:500px;height:390px;"></iframe>
		</div>		
	</f:iframebody>	
	
	<script type="text/javascript">
	
		$(document).ready(function(){
			validator=jQuery("#form1").validate({
				 rules:{
					name:{
						required:true,
						rangelength:[1,128],
						stringCheck:true,
						remote:{                                          
					      type:"POST",
					      url:"InspectionReportCheckName.action",             
					      data:{
					        name:function(){return $("#name","#cu-widget").val();},
					        id:function(){return $("#id","#cu-widget").val();}
					      } 
					     } 
					}
				},
				messages: {
					name: {
					required: '名称不能为空',
					rangelength:jQuery.format("名称位数必须在{1}到{128}字符之间！"),
					remote:'名称已存在或不可用'
					}
				}
	   	 	});
			
			//保存按钮点击事件  校验数据
			jQuery("#do-save").click(function() {
				 if(!validator.form()){ 
		        	validator.focusInvalid();
		        	return false;
		        }else{
		        	submitData();
		        }
			}); 
		});	
		
		//保存或修改 确认操作
		function submitData() {
			var action = 'Add';
			var data = fieldToJson('#cu-widget');
			if (data.id) {
				action = 'Update';
			}
			jConfirm('确认保存报告?', '信息提示', function(bt) {
				if (bt == false)
					return;
				$.ajax({
					url : 'InspectionReport' + action + '.action',
					data : data,
					dataType : "json",
					async : false,
					success : function(json) {
						if (json.success) {
							$("#dataTab").datagrid("reload");
							$('#cu-widget').dialog('close');
						} else {
							if (json.error) {
								if (json.error == 2) {
									jAlert('当前报告保存失败:报告不存在或不可编辑', '信息提示');
								}	
							} else {
								jAlert('当前报告保存失败!', '信息提示');
							}
						}
					}
				});
			});
		}
	
		/*新增*/
		function goAdd() {
			$('.field').val("");
			var rowData = {};
			jsonToField(rowData, '#cu-widget');
			validator.resetForm();
			$("#form1 INPUT").each(function(){
		  		$(this).data("previousValue", null);
		  	});
			
			//设置频度展示内容
			changeFrequent('day');
			
			var $dialog = $('#cu-widget').show().dialog({
				autoOpen : false,
				modal : true,
				resizable : false,
				height : 'auto',
				width : '500',
				title : "新增巡检报告"
			});
			$dialog.dialog('open');
		}
		
		/*编辑*/
		function goUpdate() {
			var row = $('#dataTab').datagrid('getSelected');
			if (row.status=='N'){//停用的才能修改
				jsonToField(row, '#cu-widget');
				validator.resetForm();
				$("#form1 INPUT").each(function(){
			  		$(this).data("previousValue", null);
			  	});				
				//设置频度展示内容
				changeFrequent(row.frequent);
				
				var $dialog = $('#cu-widget').show().dialog({
					autoOpen : false,
					modal : true,
					resizable : false,
					height : 'auto',
					width : 'auto',
					title : "编辑渠道"
				});
				$dialog.dialog('open');
			}else{
				jAlert('需先停用才能修改', '信息提示');
			}
		}

		//启用
		function goResume(){
			var row = $('#dataTab').datagrid('getSelected');
			if(row.status=='N'){
				jConfirm('确认启用报告?', '信息提示', function(bt) {
					if (bt == false)
						return;
					else{
						goUpdateStatus(row.id, 'Y');
					}
				});								
			}			
		}
		
		//停用
		function goForbid() {
			var row = $('#dataTab').datagrid('getSelected');
			if(row.status=='Y'){
				jConfirm('确认停用报告?', '信息提示', function(bt) {
					if (bt == false)
						return;
					else{
						goUpdateStatus(row.id, 'N');
					}
				});								
			}
		}

		//启用或停用 修改状态公用函数
		function goUpdateStatus(id, status) {
			$.ajax({
				url : 'InspectionReportUpdateStatus.action',
				data : {
					id : id,
					status : status
				},
				dataType : "json",
				async : false,
				success : function(json) {
					if (json.success) {
						$("#dataTab").datagrid("reload");
						jAlert('操作成功', '信息提示');
					} else {
						jAlert('操作失败', '信息提示');
					}
				}
			});
		}
		
		//添加、修改监控项
		function addMonitor(){
			var inspectionId = $("#id").val();
			var returnValue=window.showModalDialog("InspectionReportHostApplication.action?inspectionId="+inspectionId,'',"dialogHeight:500px;dialogWidth:550px;status:no;");
			$("#hostIds").val(returnValue);
		} 

		//查看历史记录
		function goHistory(){
			var row = $('#dataTab').datagrid('getSelected');
			if(row){
				window.location.href="InspectionReportHistoryIndex.action?inspectionId="+row.id;
			}else{
				jAlert('请选择记录', '信息提示');
			}
		}
		
		//修改时间频度，选择时间
		function changeFrequent(val){
			if(val=='day'){
				$('#tr_week').val();
				$('#tr_week').hide();
				$('#tr_day').val();
				$('#tr_day').hide();
			}else if(val=='week'){
				$('#tr_week').val();
				$('#tr_week').show();
				$('#tr_day').val();
				$('#tr_day').hide();
			}else if(val=='month'){
				$('#tr_week').val();
				$('#tr_week').hide();
				$('#tr_day').val();
				$('#tr_day').show();
			}
		}
	</script>
</f:html>