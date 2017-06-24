<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<f:html>
<f:head>
</f:head>
<script type="text/javascript">
function grantRoleFuncs(){
  var funcIds = new Array();
  var nodeObj = $.fn.zTree.getZTreeObj("funcTree").getNodes(); 
  getCheckedLeafIds(nodeObj,funcIds);
  $.ajax({
  		url:"RoleGrantFuncs.action",
  		type:"post",
  		data:{
		  roleId:<%=request.getParameter("roleId")%>,
		  funcId:funcIds
		},
  		dataType:"json",
  		success:function(json){		
	  		jAlert("权限设置成功","权限设置成功",function(){
	  			window.parent.closeIfWidget();
	  		});
  		}
  	});
}

function beforeClick(treeId, treeNode, clickFlag){
    return true;
}

</script>
<f:body>
<center>
<f:iconBlock icon="/assets/icons/profile.ico" width="420px" iconStyle="width:56px;height:56px">
    <h1>角色权限设置</h1>
    <p><hr/></p>
    <div style="overflow:scroll;width:300px;height:250px">
        <f:tree id="funcTree">
            <f:treeNodeIterator value="#{resultList}" var="o">
                <f:treeNode name="${o.funcName}" id="${o.id}" parentId="${o.pid}" 
                    icon="/assets/icons/tree_${o.icon}.png" 
                    checked="${o.checked}"></f:treeNode>
            </f:treeNodeIterator>
        </f:tree>
    </div>
    <p><hr/></p>
    <p>
        <f:inputButton value="保存权限设置" style="float:right" onclick="grantRoleFuncs()" rendered="#{permItem.grantFunc$role}"/>
    </p>
</f:iconBlock>
</center>
</f:body>
</f:html>
