<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
	</f:head>		
	<f:iframebody>	
		<e:dgCondition bindGridId="dataTab">
			<f:inputHidden id="inspectionHistoryId" value="#{vo.inspectionHistoryId}" styleClass="searchFilter"/>
		</e:dgCondition>
		<e:datagrid id="dataTab" url="/platform/iradar/InspectionReportHistoryInfo.action" title="巡检报告历史项记录" fitColumns="true">
			<e:header>
				<e:column field="historyInfoId" width="80" title="巡检历史项id" hidden="true"/>
				<e:column field="applicationName" width="270" title="应用集" align="center"/>
				<e:column field="itemName" width="270" title="监控项" align="center"/>
				<e:column field="itemValue" width="270" title="监控值" align="center"/>
				<e:column field="isIssue" formatter="#{selectItem.SUCCESS_STATUS}" width="270" title="是否有问题" align="center" />
			</e:header>
		</e:datagrid>	
	</f:iframebody>	
</f:html>