<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<f:html>
<f:head>
</f:head>
<f:iframebody>
<center>
<table>
<tr>
<td>
<f:iconBlock icon="/assets/icons/profile.ico" width="660px" iconStyle="width:56px;height:56px">
	<h1>企业信息设置</h1>
	<p>企业名称&nbsp;:&nbsp;<f:inputText id="name" value="#{vo.name}" size="70" maxlength="20"/></p>
	<p>联&nbsp;&nbsp;系&nbsp;&nbsp;人&nbsp;:&nbsp;<f:inputText id="contact" value="#{vo.contact}" size="70" maxlength="20"/></p>
	<p>联系电话&nbsp;:&nbsp;<f:inputText id="mobile" value="#{vo.mobile}" size="70" maxlength="20"/></p>
	<p>电子邮件&nbsp;:&nbsp;<f:inputText id="email" value="#{vo.email}" size="70" maxlength="100"/></p>
	<p>联系地址&nbsp;:&nbsp;<f:inputText id="address" value="#{vo.address}" size="70" maxlength="100"/></p>
	<p>联系邮编&nbsp;:&nbsp;<f:inputText id="postcode" value="#{vo.postcode}" size="70" maxlength="10"/></p>
	<p><hr/></p>
	<p>修改时间&nbsp;:&nbsp;<f:inputText id="modifiedAt" value="#{vo.modifiedAt}" disabled="true" size="70"/></p>
	<p>修&nbsp;&nbsp;改&nbsp;&nbsp;人&nbsp;:&nbsp;<f:inputText id="modifiedUser" value="#{vo.modifiedUser}" disabled="true" size="70"/></p>
	<p>创建时间&nbsp;:&nbsp;<f:inputText id="createdAt" value="#{vo.createdAt}" disabled="true" size="70"/></p>
	<p>创&nbsp;&nbsp;建&nbsp;&nbsp;人&nbsp;:&nbsp;<f:inputText id="createdUser" value="#{vo.createdUser}" disabled="true" size="70"/></p>
	<div class="cmds">
	    <f:inputButton value="保存企业信息设置" onclick="saveTenant()" rendered="#{permItem.edit$tenantProfile}"/>
	</div>
</f:iconBlock>
</td>
</tr>
</table>
</center>
<f:verbatim rendered="#{permItem.edit$tenantProfile}">
<script type="text/javascript">
function saveTenant(){
	  var name = $.trim($("#name").val());
	  var contact = $.trim($("#contact").val());
	  var mobile = $.trim($("#mobile").val());
	  var email = $.trim($("#email").val());
	  var address = $.trim($("#address").val());
	  var postcode = $.trim($("#postcode").val());
	  if(name==''){
          jAlert('企业名称不能为空!','信息提示',function(){
              $('#name').focus();
          });
          return;
      }
	  if(contact==''){
          jAlert('联系人不能为空!','信息提示',function(){
              $('#contact').focus();
          });
          return;
      }
      if(mobile==''){
          jAlert('联系电话不能为空!','信息提示',function(){
              $('#mobile').focus();
          });
          return;
      }
      if(!isMobile(mobile)){
           jAlert("联系电话输入错误","错误提示",function(){
               $('#mobile').focus();
           });
           return;
      }
      if(email==''){
            jAlert('电子邮件不能为空!','信息提示',function(){
                $('#email').focus();
            });
            return;
      }
      if(!isEmail(email)){
          jAlert("电子邮件格式错误","错误提示",function(){
              $('#email').focus();
          });
          return;
      }
		  
	  $.ajax({
	        url:"ProfTenantEdit.action",
	        type:"post",
	        data:{
	          name:name,
		      contact:contact,
	          mobile:mobile,
	          email:email,
	          address:address,
	          postcode:postcode
	        },
	        dataType:"json",
	        success:function(json){     
	            jAlert("企业信息设置成功","企业信息设置成功");
	        }
	    });
	}
</script>
</f:verbatim>
</f:iframebody>
</f:html>