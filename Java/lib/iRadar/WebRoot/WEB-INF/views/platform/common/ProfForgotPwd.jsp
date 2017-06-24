<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--><html lang="en"> <!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <meta http-equiv='pragma' content='no-cache'>
    <meta http-equiv='cache-control' content='no-cache, must-revalidate'>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <f:linkCss src="/assets/stylesheets/application.css"/>
    <f:linkCss src="/assets/jquery.alerts-1.1/jquery.alerts.css"/>
	<f:linkCss src="/assets/stylesheets/content.css"/>
	<f:linkCss src="/assets/stylesheets/jqueryui/jquery.ui.all.css"/>
	<f:linkCss src="/assets/jquery.jqgrid-4.4.1/css/ui.jqgrid.css"/>
	<f:linkCss src="/assets/jquery.jqgrid-4.4.1/css/ui.jqgrid.crack.css"/>
	<f:linkCss src="/assets/jquery.jqgrid-4.4.1/css/ui.jqgrid.isoft.css"/>
    <f:linkJs src="/assets/jquery/jquery-1.7.2.min.js"/>
    <f:linkJs src="/assets/javascripts/jquery-ui.min.js"/>
    <f:linkJs src="/assets/javascripts/application.js"/>
    <f:linkJs src="/assets/jquery.alerts-1.1/jquery.alerts.js"/>
    <f:linkJs src="/assets/core/isoft.core.js"/>
</head>
<body>
    <div id="animation">
        <img src="../assets/images/nebula-small.png" id="nebula-small" />
        <img src="../assets/images/star_large.png" id="star-large" class="stars" />
        <img src="../assets/images/star_middle.png" id="star-middle" class="stars" />
        <img src="../assets/images/star_small.png" id="star-small" class="stars" />
    </div>
    <div id="login-wrapper">
        <div id="login">
            <div id="logo">
                <p id="unicom-logo"><img src="../assets/images/unicom-logo.png" /></p>
                <p id="system-name"><img src="../assets/images/system-title.png" /></p>
                <p style="font-size:12px; color:red; display:inline-block;">!推荐使用IE8+,Firefox,Chrome浏览器浏览</p>
            </div>
            <div id="login-form">
                <form action="" id="loginform" name="loginform" method="post">
                    <div class="input-group">
                        企业账号：<input id="tenant" name="tenant" type="text" placeholder="企业帐号" value="" maxlength="10"/>                        
                    </div>
                    <div class="input-group">
                        用户名称：<input id="username" name="username" type="text" placeholder="用户名称" value="" maxlength="20"/>                        
                    </div>
                    <div class="input-group">
                        邮箱地址：<input id="email" name="email" type="text" placeholder="邮箱地址" value="" maxlength="50"/>
                    </div>
                    <div class="form-actions">
                        <div class="action">
                            <a id="login-button" style="cursor:pointer;" onclick="passwordReset();"><button type="button" value="找回密码">找回密码</button></a>
                        </div>   
                        <div class="action">
                            <a id="login-button" style="cursor:pointer;" href="../index.action"><button type="button" value="返回首页">返回首页</button></a>
                        </div>   
                    </div>
                </form>
            </div>
        </div>
        <div id="footer">
          <p></p>
        </div>
    </div>
    
<script type="text/javascript">
function passwordReset(){
	var tenant = $.trim($("#tenant").val());
	if(tenant == ""){
		jAlert("企业账号不能为空!","信息提示");
		return false;
	}
	
	var username = $.trim($("#username").val());
	if(username == ""){
		jAlert("用户名称不能为空!","信息提示");
		return false;
	}
	
	var email = $("#email").val();
	if(email == ""){
		jAlert("邮箱地址不能为空!","信息提示");
		return false;
	}
	
	if(!isEmail($.trim(email))){
		jAlert("邮箱地址格式不正确!","信息提示");
		return false;
	}
	
	var url = "ProfPwdReset.action";

	  
	  $.ajax({
	        url:url,
	        type:"post",
	        data:{
		      tenantId:tenant,
		      userName:username,
		      email:email
	        },
	        dataType:"json",
	        async:false,
	        success:function(json){
				if(json.success){
					jAlert("找回密码邮件已发送至 "+email+" ,请查收邮件!","信息提示");
					window.location.href = "ProfForgotPwd.action?ts="+new Date().getTime();
				}else{
					if(json.error){
						jAlert("找回密码失败:"+json.error+"!","信息提示");
					}else{
						jAlert("找回密码失败!","信息提示");
					}
				}
	        }
	    });
}
 </script>
</body>
</html>

