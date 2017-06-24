<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head>
    <title></title>
</f:head>
<f:iframebody>
<e:dgCondition bindGridId="dataTab">
 	租户名称: <f:inputText id="dictName" styleClass="searchFilter"/>
 	联系人: <f:inputText id="dictContact" styleClass="searchFilter"/>
	联系电话: <f:inputText id="dictMobile" styleClass="searchFilter"/>
 	联系邮箱: <f:inputText id="dictEmail" styleClass="searchFilter"/>
    <e:dgSearchButton value="查询" bindGridId="dataTab" id="searchDict"/>
</e:dgCondition>
<e:datagrid id="dataTab" url="/platform/iradar/TenantPage.action" title="租户管理" fitColumns="false">
    <e:header>	     
	    <e:column field="id" width="80" title="id" sortable="false" align="center" hidden="true"/>
	    <e:column field="name" width="160" title="租户名称" sortable="false"/> <!--formatter="{baseLinkUrl:ctxpath+'/platform/lessor/AssetViewTenantDetail.action',idName: 'tenantId'}"-->
	    <e:column field="contact" width="80" title="联系人" sortable="false" align="center"/>
	    <e:column field="mobile" width="80" title="联系电话" sortable="false" align="center"/>
	    <e:column field="email" width="160" title="联系邮箱" sortable="false" align="right"/>
	    <e:column field="address" width="160" title="联系地址" sortable="false"/>
	    <e:column field="postcode" width="60" title="邮编" sortable="false" align="center"/>
	    <e:column field="status" width="100" title="状态" sortable="false" formatter="#{selectItem.TENANT_STATUS}" align="center"/>
	    <e:column field="osTenantId" width="80" title="osTenantId" sortable="false" align="center" hidden="true"/>
	    <e:column field="modifiedAt" width="120" title="修改时间" sortable="false" align="center"/>
	    <e:column field="modifiedUser" width="80" title="修改人" sortable="false" align="center"/>
	    <e:column field="createdAt" width="120" title="创建时间" sortable="false" align="center" hidden="true" />
	    <e:column field="createdUser" width="80" title="创建人" sortable="false" align="center" hidden="true" />
    </e:header>	     
	<e:dgbutton caption="新增" icon="shuffle" onClick="goCreateRecord" rendered="#{permItem.add$tenantmgr}"/>
    <e:dgbutton caption="编辑" icon="shuffle" onClick="goModifyRecord" rendered="#{permItem.edit$tenantmgr}"/>
    <e:dgbutton caption="激活" icon="shuffle" onClick="goActiveTenant" rendered="#{permItem.active$tenantmgr}"/>
    <e:dgbutton caption="禁用" icon="shuffle" onClick="goForbidTenant" rendered="#{permItem.forbid$tenantmgr}"/>
    <e:dgbutton caption="启用" icon="shuffle" onClick="goResumeTenant" rendered="#{permItem.resume$tenantmgr}"/>
    <e:dgbutton caption="释放资源" icon="shuffle" onClick="goReleaseTenant" rendered="#{permItem.release$tenantmgr}"/>
    <e:dgbutton caption="删除" icon="shuffle" onClick="goDeleteTenant" rendered="#{permItem.delete$tenantmgr}"/>
    </e:datagrid>
<div id="cu-widget" style="display:none;">
<f:iconBlock icon="/assets/icons/profile.ico" iconStyle="width:56px;height:56px" width="400px">
    <h1 id="title">租户编辑</h1>
    <p><hr/></p>
    <p><br/></p>
    <p>
    <table class="tableblock">
        <tr>
            <th>租户名称:</th>
            <td>
		        <input type="hidden" id="id" class="field"/>
		        <input type="text" id="name" class="field" maxlength="20"/>
            </td>
        </tr>
        <tr>
            <th>联系人:</th>
            <td>
                <input type="text" id="contact" class="field" maxlength="20"/>
            </td>
        </tr>
        <tr>
            <th>联系电话:</th>
            <td>
                <input type="text" id="mobile" class="field" maxlength="20"/>
            </td>
        </tr>
        <tr>
            <th>联系邮箱:</th>
            <td>
                <input type="text" id="email" class="field" maxlength="100"/>
            </td>
        </tr>
        <tr>
            <th>联系地址:</th>
            <td>
                <input type="text" id="address" class="field" maxlength="100"/>
            </td>
        </tr>
        <tr>
            <th>邮编:</th>
            <td>
                <input type="text" id="postcode" class="field" maxlength="6"/>
            </td>
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
<script type="text/javascript">
	function getUrl(rowids){
	    return ctxpath+"/platform/lessor/vdc/AerialViewTenantDetail.action?tenantId="+(rowids);
	}
    function checkName(value,colname){
      var ret = true;
      var rowData = $('#dataTab').datagrid('getSelected');
      var id = null;
      if(rowData){
    	  id = rowData.id;
      }
      $.ajax( {
          url : ctxpath+"/platform/lessor/biz/TenantCheckName.action",
          data : {
              id:id==null?'':id,
              name:value
          },
          dataType : "json",
          async:false,
          success : function(json) {
              ret = json.success;
          }
      });
      return [ret,colname+': 名称重复!'];
    }
    function goCreateRecord(tabId){
        var rowData = {};
        jsonToField(rowData,'#cu-widget');
        $('#title','#cu-widget').text('添加租户');
        var $dialog = $('#cu-widget').show().dialog({
            autoOpen: false,
            modal: true,
            resizable:false,
            height: 'auto',
            width:'auto',
            title: "添加租户"
        });
        $dialog.dialog('open');
    }

    function goModifyRecord(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        if(id){
            jsonToField(rowData,'#cu-widget');
        } else {
            jAlert('请选择租户!','信息提示');
            return;
        }
        $('#title','#cu-widget').text('租户编辑');
        var $dialog = $('#cu-widget').show().dialog({
            autoOpen: false,
            modal: true,
            resizable:false,
            height: 'auto',
            width:'auto',
            title: "租户编辑"
        });
        $dialog.dialog('open');
    }

    function goSaveRecord(){
        if($.trim($('#name','#cu-widget').val())==''){
            jAlert('租户名称不能为空!','信息提示',function(){
                $('#name','#cu-widget').focus();
            });
            return;
        }
        if(!isLimitName($.trim($('#name','#cu-widget').val()),3,20,'租户名称')) {
            return;
        }
        if($.trim($('#contact','#cu-widget').val())==''){
            jAlert('联系人不能为空!','信息提示',function(){
                $('#contact','#cu-widget').focus();
            });
            return;
        }
        if($.trim($('#mobile','#cu-widget').val())==''){
            jAlert('联系电话不能为空!','信息提示',function(){
                $('#mobile','#cu-widget').focus();
            });
            return;
        }
        if(!isMobile($.trim($('#mobile','#cu-widget').val()))){
            jAlert('联系电话格式错误!','信息提示',function(){
                $('#mobile','#cu-widget').focus();
            });
            return;
        }
        if($.trim($('#email','#cu-widget').val())==''){
            jAlert('联系邮箱不能为空!','信息提示',function(){
                $('#email','#cu-widget').focus();
            });
            return;
        }
        if(!isEmail($.trim($('#email','#cu-widget').val()))){
            jAlert('联系邮箱格式错误!','信息提示',function(){
                $('#email','#cu-widget').focus();
            });
            return;
        }
        if($.trim($('#postcode','#cu-widget').val())!=''){
        	if(!isNumber($.trim($('#postcode','#cu-widget').val()))||
        			$.trim($('#postcode','#cu-widget').val()).length!=6){
	        	jAlert('邮编只能为6位数字!','信息提示',function(){
	            	$('#postcode','#cu-widget').focus();
	         	});
	            return;
        	}
        }
        var action = 'Add';
        var data = fieldToJson('#cu-widget');
        if(data.id){
            action = 'Edit';
        }
        jConfirm('确认保存租户?','信息提示',function(bt){
            if(bt == false) return;
            $.ajax( {
                url : 'TenantOper'+action+'.action',
                data : data,
                dataType : "json",
                async:false,
                success : function(json) {
                    if(json.success){
                        //$("#dataTab").trigger("reloadGrid",[{current:true}]);
                        $("#dataTab").datagrid("reload");
                        $('#cu-widget').dialog('close');
                    } else {
                        if(json.error){
                            jAlert('当前租户保存失败:'+json.error,'信息提示');
                        } else {
                            jAlert('当前租户保存失败!','信息提示');
                        }
                    }
                }
            });
        });
    }
    
    function goActiveTenant(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        if(id){
            if(rowData.status != 'N'){
                jAlert('只能激活待激活的租户!','信息提示');
            } else {
                jConfirm('激活当前租户?','信息提示',function(bt){
                    if(bt == false) return;
                    $.ajax( {
                        url : 'TenantOperActive.action',
                        data : {
                           id:id
                        },
                        dataType : "json",
                        async:false,
                        success : function(json) {
                            if(json.success){
                                //$("#"+tabId).trigger("reloadGrid",[{current:true}]);
                                $("#dataTab").datagrid("reload");
                            } else {
                                jAlert('当前租户不能被激活:'+json.error,'信息提示');
                            }
                        }
                    });
                });
            }
        } else {
            jAlert('请选择租户!','信息提示');
        }
    }

    function goForbidTenant(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        if(id){
            if(rowData.status != 'Y'){
                jAlert('只能禁用激活的租户!','信息提示');
            } else {
                jConfirm('禁用当前租户?','信息提示',function(bt){
                    if(bt == false) return;
                    $.ajax( {
                        url : 'TenantOperForbid.action',
                        data : {
                           id:id
                        },
                        dataType : "json",
                        async:false,
                        success : function(json) {
                            if(json.success){
                                //$("#"+tabId).trigger("reloadGrid",[{current:true}]);
                                $("#dataTab").datagrid("reload");
                            } else {
                                jAlert('当前租户不能被禁用!','信息提示');
                            }
                        }
                    });
                });
            }
        } else {
            jAlert('请选择租户!','信息提示');
        }
    }

    function goResumeTenant(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        if(id){
            if(rowData.status != 'F'){
                jAlert('只能启用禁用[资源待释放]的租户!','信息提示');
            } else {
                jConfirm('启用当前租户?','信息提示',function(bt){
                    if(bt == false) return;
                    $.ajax( {
                        url : 'TenantOperResume.action',
                        data : {
                           id:id
                        },
                        dataType : "json",
                        async:false,
                        success : function(json) {
                            if(json.success){
                                //$("#"+tabId).trigger("reloadGrid",[{current:true}]);
                                $("#dataTab").datagrid("reload");
                            } else {
                                jAlert('当前租户不能被启用!','信息提示');
                            }
                        }
                    });
                });
            }
        } else {
            jAlert('请选择租户!','信息提示');
        }
    }

    function goReleaseTenant(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        var osTenantId = rowData["osTenantId"];
        if(id){
            if(rowData.status != 'F'){
                jAlert('只能释放禁用租户的资源!','信息提示');
            } else {
                jConfirm('释放当前租户的资源?','信息提示',function(bt){
                    if(bt == false) return;
                    $.ajax( {
                        url : 'TenantOperRelease.action',
                        data : {
                           id:id,
                           osTenantId:osTenantId
                        },
                        dataType : "json",
                        async:false,
                        success : function(json) {
                            if(json.success){
                                //$("#"+tabId).trigger("reloadGrid",[{current:true}]);
                                $("#dataTab").datagrid("reload");
                            } else {
                                jAlert('当前租户的资源不能被释放!','信息提示');
                            }
                        }
                    });
                });
            }
        } else {
            jAlert('请选择租户!','信息提示');
        }
    }

    function goDeleteTenant(tabId){
    	var rowData = $('#dataTab').datagrid('getSelected');
        var id = null;
        if(rowData){
      	  id = rowData.id;
        }
        if(id){
            if(rowData.status != 'R' && rowData.status != 'N'){
                jAlert('只能删除待激活/被禁用且释放资源的租户!','信息提示');
            } else {
                jConfirm('删除当前的租户?','信息提示',function(bt){
                    if(bt == false) return;
                    $.ajax( {
                        url : 'TenantOperDel.action',
                        data : {
                           id:id
                        },
                        dataType : "json",
                        async:false,
                        success : function(json) {
                            if(json.success){
                                //$("#"+tabId).trigger("reloadGrid",[{current:true,del:true}]);
                                $("#dataTab").datagrid("reload");
                            } else {
                                jAlert('当前租户不能被删除!','信息提示');
                            }
                        }
                    });
                });
            }
        } else {
            jAlert('请选择租户!','信息提示');
        }
    }
</script>
</f:iframebody>
</f:html>