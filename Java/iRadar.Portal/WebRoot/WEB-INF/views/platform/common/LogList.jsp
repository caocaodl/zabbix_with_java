<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head/>
<f:iframebody>
<e:dgCondition bindGridId="dataTab">
	功能模块名称: <f:inputText id="dictFuncName" styleClass="searchFilter"/>
    <e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict2"/>
</e:dgCondition>
<e:datagrid id="dataTab" url="/platform/LogPage.action" title="平台日志" fitColumns="false">
    <e:header>
	    <e:column field="userId" width="140" title="操作用户" sortable="true" align="center"/>
	    <e:column field="funcName" width="180" title="功能模块名称" sortable="false"/>
	    <e:column field="requestUri" width="200" title="功能URL" sortable="false"/>
	    <e:column field="funcMenu" width="140" title="页面操作" sortable="false" />
	    <e:column field="description" width="180" title="参数" sortable="false" />
	    <e:column field="createdAt" width="140" title="创建时间" sortable="false" align="center"/>
    </e:header>
</e:datagrid>
</f:iframebody>
</f:html>