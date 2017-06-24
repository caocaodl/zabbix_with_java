<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head/>
<f:iframebody>
	<f:toolbar id="toolbar">
	    <f:toolbarButton name="启动" id="Startup" disabled="#{true}" onclick="alert(1);" icon="btnconfig"/>
	    <f:toolbarButton name="关闭" id="Shutdown" disabled="#{false}" icon="btnconfig"/>
	</f:toolbar>

	<f:tabPanel id="dcinfo">
		<f:tabPage title="基本信息">
			<f:panel title="test" id="P6" style="width:auto;height:auto;padding:10px;background:#fafafa;" closable="true" maximizable="false" minimizable="false">
				<f:selectOneMenu value="2">
			    	<f:selectItem itemLabel="" itemValue=""/>
			    	<f:selectItem itemLabel="上海" itemValue="上海"/>
			    	<f:selectItem itemLabel="北京" itemValue="北京"/>
			    	<f:selectItem itemLabel="深圳" itemValue="深圳"/>
			    	<f:selectItem itemLabel="香港" itemValue="香港"/>
			    </f:selectOneMenu>
			    
			    <f:selectOneMenu value="上海" displayValueOnly="true">
			    	<f:selectItem itemLabel="" itemValue=""/>
			    	<f:selectItem itemLabel="上海" itemValue="上海"/>
			    	<f:selectItem itemLabel="北京" itemValue="北京"/>
			    	<f:selectItem itemLabel="深圳" itemValue="深圳"/>
			    	<f:selectItem itemLabel="香港" itemValue="香港"/>
			    </f:selectOneMenu>
			
				<table>
					<tr>
						<td>虚拟主机(台):</td>
						<td>1</td>
					</tr>
					<tr>
						<td>运行状态(台):</td>
						<td>2</td>
					</tr>
					<tr>
						<td>停止状态(台):</td>
						<td>3</td>
					</tr>
					<tr>
						<td>出错状态(台):</td>
						<td>4</td>
					</tr>
				</table>
			</f:panel>
		</f:tabPage>
		<f:tabPage title="虚拟机"> test </f:tabPage>
		<f:tabPage title="报警"> test </f:tabPage>
	</f:tabPanel>


	<script> var beforeClick = $.noop </script>
	<f:tree id="func">
		<f:treeAsync enable="true" autoParam="['id=pid']" url="/platform/lessor/AssetViewTreeLoad.action" dataFilter="$.noop" />

		<f:treeNode name="系统总览" id="overview" parentId="-1" icon="/assets/icons/assettree/tenants.png" noCheck="true" isParent="true">
			<f:treeNode name="云应用概览" id="vm" parentId="-1" icon="/assets/icons/assettree/vms.png" noCheck="true" isParent="false"
				click="doView('id','name','/platform/lessor/AssetViewCloudApp.action')" >
			</f:treeNode>
		</f:treeNode>
		
		<f:treeNodeIterator value="#{resultList}" var="o">
                <f:treeNode name="${o.funcName}" id="${o.id}" parentId="${o.pid}" 
                    icon="/assets/icons/tree_${o.icon}.png" 
                    checked="${o.checked}"></f:treeNode>
            </f:treeNodeIterator>
	</f:tree>


	<f:block style="width:1000px; display: none">

		<f:inputButton value="f:inputButton" />
		<f:inputHidden value="f:inputHidden" />
		<f:inputSecret value="f:inputSecret" />
		<f:inputText value="f:inputText" id="test" />
		<f:inputTextArea value="f:inputTextArea" />

	</f:block>
		
</f:iframebody>
</f:html>