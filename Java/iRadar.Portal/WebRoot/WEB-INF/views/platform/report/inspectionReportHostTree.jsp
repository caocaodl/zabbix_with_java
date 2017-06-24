<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<f:html>
<f:head>
</f:head>
<script type="text/javascript">
function addMonitorHost(){
	var hostIds = new Array();
	var nodeObj = $.fn.zTree.getZTreeObj("hostTree").getNodes();
	getCheckedLeafIds(nodeObj,hostIds);
	parent.window.returnValue=hostIds;
	window.close();
}

function beforeClick(treeId, treeNode, clickFlag){
    return true;
}

</script>
<f:body>
<center>
<f:block style="width: 90%;margin:auto;margin-top: 20px;">
    <h1>监控设备设置</h1>
    <hr/>
    <div style="overflow-x: hidden;height: 300px;">
        <f:tree id="hostTree">
            <f:treeNodeIterator value="#{resultList}" var="o">
                <f:treeNode name="${o.name}" id="${o.id}" parentId="${o.pid}" open="${o.open}"              
                    icon="/assets/icons/tree_${o.icon}.png" 
                    checked="${o.checked}"></f:treeNode>
            </f:treeNodeIterator>
        </f:tree>
    </div>
	<hr/>
    <p>
        <f:inputButton value="确定" style="float:right" onclick="addMonitorHost()" rendered="#{permItem.grantRole$user}"/>
    </p>
</f:block>
</center>
</f:body>
</f:html>
