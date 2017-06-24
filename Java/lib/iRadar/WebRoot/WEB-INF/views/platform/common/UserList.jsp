<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head/>
<f:iframebody>
<e:dgCondition bindGridId="dataTab">
 	用户名称: <f:inputText id="dictName" styleClass="searchFilter"/>
	电话号码: <f:inputText id="dictMobile" styleClass="searchFilter"/>
 	电子邮件: <f:inputText id="dictEmail" styleClass="searchFilter"/>
    <e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict"/>
</e:dgCondition>
<e:datagrid id="dataTab" url="/platform/UserPage.action" title="用户管理" fitColumns="true" sortName="createdAt" sortOrder="desc">
    <e:header>
        <e:column field="name" width="80" title="名称" sortable="true"/>
	    <e:column field="mobile" width="80" title="手机号码" sortable="true"/>
	    <e:column field="email" width="140" title="电子邮件" sortable="true"/>
	    <e:column field="lastLoginAt" width="140" title="最后登录时间" align="center" sortable="true"/>
	    <e:column field="admin" width="60" title="admin" align="center"/>
	    <e:column field="status" width="60" title="状态" align="center" formatter="#{selectItem.USER_STATUS}"/>
	    <e:column field="modifiedAt" width="140" title="修改时间" align="center" sortable="true"/>
	    <e:column field="modifiedUser" width="80" title="修改人" align="center"/>
	    <e:column field="createdAt" width="140" title="创建时间" align="center" sortable="true"/>
	    <e:column field="createdUser" width="80" title="创建人" align="center"/>
    </e:header>
 
    <e:dgbutton caption="新增用户" onClick="goCreateRecord" />
    <e:dgbutton caption="编辑用户" onClick="goModifyRecord" />
    <e:dgbutton caption="激活用户" onClick="goActiveRecord" />
    <e:dgbutton caption="禁用用户" onClick="goForbidRecord" />
    <e:dgbutton caption="启用用户" onClick="goResumeRecord" />   
    <e:dgbutton caption="删除用户" onClick="goDeleteRecord" />
    <e:dgbutton caption="角色设置" onClick="goGrantRole" />
  
</e:datagrid>

<div id="cu-widget" style="display:none;">
<f:iconBlock icon="/assets/icons/profile.ico" iconStyle="width:56px;height:56px" width="400px">
    <h1 id="title">编辑用户</h1>
    <p><hr/></p>
    <p><br/></p>
    <p>
    <table class="tableblock">
      <tr>
        <th>用户名称:</th>
        <td><input type="hidden" id="id" class="field"/><input type="text" id="name" class="field" maxlength="20" /></td>
      </tr>
      <tr>
        <th>手机号码:</th>
        <td><input type="text" id="mobile" class="field" maxlength="11" /></td>
      </tr>
      <tr>
        <th>电子邮件:</th>
        <td><input type="text" id="email" class="field" maxlength="100" /></td>
      </tr>
    </table>   
    </p>
    <p><br/></p>
    <p><hr/></p>
    <p>
        <f:inputButton value="保存" style="float:right" id="do-save" onclick="goSaveRecord()"/>
    </p>
</f:iconBlock>
</div>
<div id="if-widget" style="display:none;">
<iframe scrolling="no" frameborder="0" style="width:500px;height:390px;"></iframe>
</div>

<script type="text/javascript">
function goCreateRecord(){
	$('.field').val("");
    var rowData = {};
    jsonToField(rowData,'#cu-widget');
    $('#title','#cu-widget').text('添加用户');
    var $dialog = $('#cu-widget').show().dialog({
        autoOpen: false,
        modal: true,
        resizable:false,
        height: 'auto',
        width:'auto',
        title: "添加用户"
    });
    $dialog.dialog('open');
}

function goModifyRecord(){
    var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.status != 'N'){
            jAlert('只能编辑待激活用户!','信息提示');
            return;
        }
        jsonToField(row,'#cu-widget');
    } else {
        jAlert('请选择用户!','信息提示');
        return;
    }
    $('#title','#cu-widget').text('编辑用户');
    var $dialog = $('#cu-widget').show().dialog({
        autoOpen: false,
        modal: true,
        resizable:false,
        height: 'auto',
        width:'auto',
        title: "编辑用户"
    });
    $dialog.dialog('open');
}

function goActiveRecord(){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.status != 'N'){
            jAlert('只能激活待激活用户!','信息提示');
            return;
        }
        jConfirm('确认激活用户?','信息提示',function(bt){
            if(bt == false) return;
            $.ajax( {
                url : 'UserOperActive.action',
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
                            jAlert('当前用户不能激活:'+json.error,'信息提示');
                        } else {
                            jAlert('当前用户不能激活!','信息提示');
                        }
                    }
                }
            });
        });
    } else {
        jAlert('请选择用户!','信息提示');
    }
}

function goForbidRecord(){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.admin == 'Y'){
            jAlert('该用户是管理员,不能被禁用!','信息提示');
            return;
        }
        if(row.status != 'Y'){
            jAlert('只能禁用激活用户!','信息提示');
            return;
        }
        jConfirm('确认禁用用户?','信息提示',function(bt){
            if(bt == false) return;
            $.ajax( {
                url : 'UserOperForbid.action',
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
                            jAlert('当前用户不能禁用:'+json.error,'信息提示');
                        } else {
                            jAlert('当前用户不能禁用!','信息提示');
                        }
                    }
                }
            });
        });
    } else {
        jAlert('请选择用户!','信息提示');
    }
}

function goResumeRecord(){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.status != 'F'){
            jAlert('只能启用禁用用户!','信息提示');
            return;
        }
        jConfirm('确认启用用户?','信息提示',function(bt){
            if(bt == false) return;
            $.ajax( {
                url : 'UserOperResume.action',
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
                            jAlert('当前用户不能启用:'+json.error,'信息提示');
                        } else {
                            jAlert('当前用户不能启用!','信息提示');
                        }
                    }
                }
            });
        });
    } else {
        jAlert('请选择用户!','信息提示');
    }
}

function goDeleteRecord(){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.status != 'N' && row.status != 'F'){
            jAlert('只能删除待激活/禁用用户!','信息提示');
            return;
        }
        jConfirm('确认删除用户?','信息提示',function(bt){
            if(bt == false) return;
            $.ajax( {
                url : 'UserOperDel.action',
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
                            jAlert('当前用户不能删除:'+json.error,'信息提示');
                        } else {
                            jAlert('当前用户不能删除!','信息提示');
                        }
                    }
                }
            });
        });
    } else {
        jAlert('请选择用户!','信息提示');
    }
}

function goSaveRecord(){
    if($.trim($('#name','#cu-widget').val())==''){
        jAlert('用户名称不能为空!','信息提示',function(){
            $('#name','#cu-widget').focus();
        });
        return;
    }
    
    var re = /^[0-9a-zA-Z\u4e00-\u9fa5]+$/;          
       
    if (!re.exec($.trim($('#name','#cu-widget').val()))) {      
    	 jAlert('只能输入汉字、字母或数字!','信息提示',function(){
             $('#name','#cu-widget').focus();
         });
         return;
    }     
    
    if($.trim($('#mobile','#cu-widget').val())==''){
        jAlert('手机号码不能为空!','信息提示',function(){
            $('#mobile','#cu-widget').focus();
        });
        return;
    }
    if(!isMobile($.trim($('#mobile','#cu-widget').val()))){
        jAlert('手机号码格式错误!','信息提示',function(){
            $('#mobile','#cu-widget').focus();
        });
        return;
    }
    if($.trim($('#email','#cu-widget').val())==''){
        jAlert('电子邮件不能为空!','信息提示',function(){
            $('#email','#cu-widget').focus();
        });
        return;
    }
    if(!isEmail($.trim($('#email','#cu-widget').val()))){
        jAlert('电子邮件格式错误!','信息提示',function(){
            $('#email','#cu-widget').focus();
        });
        return;
    }
    var action = 'Add';
    var data = fieldToJson('#cu-widget');
    if(data.id){
        action = 'Edit';
    }
    jConfirm('确认保存用户?','信息提示',function(bt){
        if(bt == false) return;
        $.ajax( {
            url : 'UserOper'+action+'.action',
            data : data,
            dataType : "json",
            async:false,
            success : function(json) {
                if(json.success){
                    $("#dataTab").datagrid("reload");
                    $('#cu-widget').dialog('close');
                } else {
                    if(json.error){
                        jAlert('当前用户保存失败:'+json.error,'信息提示');
                    } else {
                        jAlert('当前用户保存失败!','信息提示');
                    }
                }
            }
        });
    });
}

function goGrantRole(){
	var row = $('#dataTab').datagrid('getSelected');
    if(row){
        if(row.admin == 'Y'){
            jAlert('该用户是管理员,拥有最大权限,禁止角色设置!','信息提示');
            return;
        }
    	if(row.status != 'Y'){
            jAlert('只能设置激活用户的角色!','信息提示');
            return;
        }
        $('iframe','#if-widget').attr('src',ctxpath+"/platform/UserRoleTree.action?userId="+row.id);
        var $dialog = $('#if-widget').show().dialog({
            autoOpen: false,
            modal: true,
            resizable:false,
            height: 'auto',
            width:'auto',
            title: "角色设置"
        });
        $dialog.dialog('open');
    } else {
        jAlert('请选择用户!','信息提示');
    }
}
function closeIfWidget(){
    $('#if-widget').dialog('close');
}
</script>
</f:iframebody>
</f:html>