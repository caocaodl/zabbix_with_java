<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
	<f:head>
		<script type="text/javascript">
			function outCSV(){
				var hostGroup = $("#hostGroup").val();
				var os = $("#os").val();
				var vendor = $("#vendor").val();
				var contractNumber = $("#contractNumber").val();
				var hostNetworks = $("#hostNetworks").val();
				
				window.open("ReportInventoriesCSV.action?hostGroup="+hostGroup+"&os="+os
						+"&vendor="+vendor+"&contractNumber="+contractNumber+"&hostNetworks"+hostNetworks);
			}
		</script>	
	</f:head>	
		
	<f:iframebody>
		<e:dgCondition bindGridId="dataTab">
			<f:inputHidden id="hostGroup" value="" styleClass="searchFilter"/>
			<f:inputHidden id="os" value="" styleClass="searchFilter"/>
		 	厂商: <f:inputText id="vendor" styleClass="searchFilter" />
		 	型号: <f:inputText id="contractNumber" styleClass="searchFilter" />
			资产信息汇总：<f:inputText id="hostNetworks" styleClass="searchFilter" />
			<e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict" />
			<e:dgSearchButton value="导出" onclick="outCSV()"  bindGridId="out" id="out"/>
		</e:dgCondition>
		
		<e:datagrid id="dataTab" url="/platform/iradar/ReportPage.action" title="资产列表" fitColumns="true">
			<e:header>
				<e:column field="vendor" width="270" title="厂商" align="center"/>
				<e:column field="contractNumber" width="140" title="型号" align="center"/>
				<e:column field="hostNetworks" width="140" title="资产信息汇总" align="center"/>
				<e:column field="hostGroup" width="140" title="设备类型" align="center"/>
				<e:column field="os" width="140" title="OS类型" align="center"/>
			</e:header>
		</e:datagrid>				
	</f:iframebody>
</f:html>