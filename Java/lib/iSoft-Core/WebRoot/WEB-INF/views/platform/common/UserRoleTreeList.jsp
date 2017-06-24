<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<f:html>
<f:head>
</f:head>
<script type="text/javascript">
function grantUserRoles(){
  var roleIds = new Array();
  var nodeObj = $.fn.zTree.getZTreeObj("roleTree").getNodes(); 
  getCheckedLeafIds(nodeObj,roleIds);
  $.ajax({
  		url:"UserGrantRoles.action",
  		type:"post",
  		data:{
		  userId:<%=request.getParameter("userId")%>,
		  roleId:roleIds
		},
  		dataType:"json",
  		success:function(json){		
	  		jAlert("角色设置成功","角色设置成功",function(){
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
    <h1>用户角色权限设置</h1>
    <p><hr/></p>
    <div style="overflow:scroll;width:300px;height:250px">
        <f:tree id="roleTree">
            <f:treeNodeIterator value="#{resultList}" var="o">
                <f:treeNode name="${o.roleName}" id="${o.id}" parentId="${o.pid}" 
                    click="doView('${o.id}','${o.roleName}')" 
                    icon="/assets/icons/tree_${o.icon}.png" 
                    checked="${o.checked}"></f:treeNode>
            </f:treeNodeIterator>
        </f:tree>
    </div>
    <p><hr/></p>
    <p>
        <f:inputButton value="保存角色设置" style="float:right" onclick="grantUserRoles()" rendered="#{permItem.grantRole$user}"/>
    </p>
</f:iconBlock>
</center>
</f:body>
</f:html>
