<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head title="修改密码">
<f:linkJs src="/resources/js/rl/src/RealLight.js"/>
</f:head>
<f:iframebody>
<center>
<table>
<tr>
<td>
<f:iconBlock icon="/assets/icons/hi.ico" width="360px" iconStyle="width:56px;height:56px">
    <h1>修改密码</h1>
    <p><hr/></p>
    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;现密码&nbsp;:&nbsp;<f:inputSecret id="curPswd" maxlength="20"/></p>
    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;新密码&nbsp;:&nbsp;<f:inputSecret id="newPswd" maxlength="20"/></p>
    <p>&nbsp;&nbsp;&nbsp;确认新密码&nbsp;:&nbsp;<f:inputSecret id="repPswd" maxlength="20"/></p>
    <p><hr/></p>
    <p>
        <f:inputButton value="保存密码设置" style="float:right" onclick="savePswd()"  />
    </p>
</f:iconBlock>
</td>
</tr>
</table>
</center>
<script type="text/javascript">
function savePswd(){
	  var curPswd = $("#curPswd").val();
	  var newPswd = $("#newPswd").val();
	  var repPswd = $("#repPswd").val();
	  if(curPswd == ''){
		  jAlert("请输入现密码","错误提示");
		  return;
	  }
	  if(newPswd == ''){
          jAlert("请输入新密码","错误提示");
          return;
      }
	  if(repPswd == ''){
          jAlert("请输入确认新密码","错误提示");
          return;
      }

	  if(repPswd != newPswd){
          jAlert("确认新密码和新密码不一致","错误提示");
          return;
      }

	  if(curPswd == newPswd){
          jAlert("新密码和当前密码相同","错误提示");
          return;
      }
		  
	  $.ajax({
	        url:"ProfOperPswd.action",
	        type:"post",
	        data:{
		      curPswd:curPswd,
		      newPswd:newPswd
	        },
	        dataType:"json",
	        success:function(json){
	            if(json.success){
	                jAlert("新密码设置成功","新密码设置成功",function(result){
	    	                if(result){
	    	                	$("#curPswd").val("");
	    	                	$("#newPswd").val("");
	    	                	$("#repPswd").val("");
	    	                	}
	                });
	            } else {
	            	jAlert("新密码设置失败","新密码设置失败");
	            }
	        }
	    });
	}
</script>
</f:iframebody>
</f:html>