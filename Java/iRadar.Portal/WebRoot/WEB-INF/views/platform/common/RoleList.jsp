<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head/>
<f:iframebody>
<e:dgCondition bindGridId="dataTab">
  角色名称: <f:inputText id="ditcRoleName" styleClass="searchFilter"/>
    <e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict"/>
</e:dgCondition>
<e:datagrid id="dataTab" url="/platform/RolePage.action" title="角色管理" fitColumns="false">
    <e:header>
        <e:column field="roleName" width="80" title="角色名称" sortable="false"/>
	    <e:column field="roleDesc" width="200" title="角色描述" sortable="false"/>
	    <e:column field="modifiedAt" width="140" title="修改时间" align="center"/>
	    <e:column field="modifiedUser" width="80" title="修改人" align="center"/>
	    <e:column field="createdAt" width="120" title="创建时间" align="center"/>
	    <e:column field="createdUser" width="80" title="创建人" align="center"/>
    </e:header>
    
    <e:dgbutton caption="新增角色" onClick="goCreateRecord" icon="icon-add"/>
    <e:dgbutton caption="编辑角色" onClick="goModifyRecord" icon="icon-edit"/>
    <e:dgbutton caption="删除角色" onClick="goDeleteRecord" icon="icon-remove"/>
    <e:dgbutton caption="权限设置" onClick="goGrantFunc" icon="icon-user"/>
    
</e:datagrid>
<div id="cu-widget" style="display:none;">
<f:iconBlock icon="/assets/icons/profile.ico" iconStyle="width:56px;height:56px" width="400px">
    <h1 id="title">编辑角色</h1>
    <section>
	    <table class="tableblock">
	      <tr>
	        <th>角色名称:</th>
	        <td><input type="hidden" id="id" class="field"/>
	        <input type="text" id="roleName" class="field" maxlength="20"/></td>
	      </tr>
	       <tr>
	        <th>角色描述:</th>
	        <td><input type="text" id="roleDesc" class="field" maxlength="50"/></tr>
	    </table>
    </section>
    <div class="cmds">
        <f:inputButton value="保存" id="do-save" onclick="goSaveRecord()"/>
    </div>
</f:iconBlock>
</div>
<div id="if-widget" style="display:none;">
<iframe scrolling="no" frameborder="0" style="width:500px;height:390px;"></iframe>
</div>


<script type="text/javascript">

function goCreateRecord(){
	$('.field').val("");
	var rowData = {name:'benne'};
    jsonToField(rowData,'#cu-widget');
    $('#title','#cu-widget').text('添加角色');
    var $dialog = $('#cu-widget').show().dialog({
        autoOpen: false,
        modal: true,
        resizable:false,
        height: 'auto',
        width:'auto',
        title: "添加角色"
    });
    $dialog.dialog('open');
}

function goModifyRecord(){
    var row = $('#dataTab').datagrid('getSelected');
    if(row){
        jsonToField(row,'#cu-widget');
    } else {
    	jAlert('请选择角色!','信息提示');
    	return;
    }
    $('#title','#cu-widget').text('编辑角色');
    var $dialog = $('#cu-widget').show().dialog({
        autoOpen: false,
        modal: true,
        resizable:false,
        height: 'auto',
        width:'auto',
        title: "编辑角色"
    });
    $dialog.dialog('open');
}

function goDeleteRecord(){
    var row = $('#dataTab').datagrid('getSelected');
    if(row){
        jConfirm('确认删除角色?','信息提示',function(bt){
        	if(bt == false) return;
            $.ajax( {
                url : 'RoleOperDel.action',
                data : {
                   id:row.id
                },
                dataType : "json",
                async:false,
                success : function(json) {
                    if(json.success){
                        $("#dataTab").datagrid("reload");
                    } else {
                        if(json.error){
                            jAlert('当前角色不能删除:'+json.error,'信息提示');
                        } else {
                        	jAlert('当前角色不能删除!','信息提示');
                        }
                    }
                }
            });
        });
    } else {
        jAlert('请选择角色!','信息提示');
    }
}

function goSaveRecord(){
	if($('#roleName','#cu-widget').val()==''){
		jAlert('角色名称不能为空!','信息提示',function(){
			$('#roleName','#cu-widget').focus();
		});
		return;
	}

	var re = /^[0-9a-zA-Z\u4e00-\u9fa5]+$/;          
    
    if (!re.exec($.trim($('#roleName','#cu-widget').val()))) {      
    	 jAlert('只能输入汉字、字母或数字!','信息提示',function(){
             $('#roleName','#cu-widget').focus();
         });
         return;
    }     
 
	var action = 'Add';
	var data = fieldToJson('#cu-widget');
	if(data.id){
		action = 'Edit';
	}
    $.ajax( {
            url : 'RoleOper'+action+'.action',
            data : data,
            dataType : "json",
            async:false,
            success : function(json) {
                if(json.success){
                    $("#dataTab").datagrid("reload");
                    $('#cu-widget').dialog('close');
                } else {
                    if(json.error){
                        jAlert('当前角色保存失败:'+json.error,'信息提示');
                    } else {
                    	jAlert('当前角色保存失败!','信息提示');
                    }
                }
            }
    });
}

function goGrantFunc(tabId){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
    	$('iframe','#if-widget').attr('src',ctxpath+"/platform/RoleFuncTree.action?roleId="+row.id);
        var $dialog = $('#if-widget').show().dialog({
            autoOpen: false,
            modal: true,
            resizable:false,
            height: 'auto',
            width:'auto',
            title: "权限设置"
        });
        $dialog.dialog('open');
    } else {
        jAlert('请选择角色!','信息提示');
    }
}
function closeIfWidget(){
    $('#if-widget').dialog('close');
}
</script>
</f:iframebody>
</f:html>