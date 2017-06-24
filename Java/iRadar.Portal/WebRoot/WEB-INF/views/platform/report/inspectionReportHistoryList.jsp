<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
	</f:head>		
	<f:iframebody>
		<e:dgCondition bindGridId="dataTab">
			<f:inputHidden id="hostGroup" value="#{vo.inspectionId}" styleClass="searchFilter"/>
		</e:dgCondition>	
		<e:datagrid id="dataTab" url="/platform/iradar/InspectionReportHistoryPage.action" title="巡检报告历史列表" fitColumns="true">
			<e:header>		
				<e:column field="id" width="80" title="巡检历史id" hidden="true"/>
				<e:column field="inspectionName" width="270" title="巡检报告名称" align="center"/>
				<e:column field="createTime"  width="270" title="执行时间" align="center"/>
				<e:column field="issueNumber" width="270" title="问题数目" align="center" />
				<e:column field="itemNumber" width="270" title="监控项数目" align="center" />
			</e:header>
		    <e:dgbutton caption="查看详情" icon="shuffle" onClick="goHistoryInfo" rendered="#{permItem.historyInfo$inspectionReport}"/>
		</e:datagrid>	
	</f:iframebody>	
	
	<script type="text/javascript">
		//查看详情
		function goHistoryInfo(){
			var row = $('#dataTab').datagrid('getSelected');
			if(row){
				//window.location.href="InspectionReportHistoryInfoIndex.action?inspectionHistoryId="+row.id;
				window.showModalDialog("InspectionReportHistoryInfoIndex.action?inspectionHistoryId="+row.id,'',"dialogHeight:500px;dialogWidth:550px;status:no;");
			}else{
				jAlert('请选择记录!', '信息提示');
				return;
			}			
		}
	</script>
</f:html>