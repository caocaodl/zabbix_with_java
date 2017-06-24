
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="f" uri="/isoft/core"%>
<%@ taglib prefix="e" uri="/isoft/easyui"%>
<%@ page import="com.isoft.server.RunParams"%>
<%@ page import="com.isoft.Feature"%>
<%
String uri = request.getContextPath();
if(!uri.endsWith("/")){
	uri += "/";
}
uri = uri + "checkcode.action";
%>
<f:html>
<f:head>
	<f:linkCss src="/assets/c/v_admin/login/login.css" />
</f:head>
<body>
	<div class="login">
		<div class="login-top">
			<div class="loginForm">
				<div class="logo_bj"><div class="logo"></div></div>
				<div class="loginform_box">
					<form action="login.action" method="post" id="loginform">
						<input type="hidden" name="request" class="input hidden" value="" />
						<p style="display: none;">
							<input id="tenant" name="tenant" class="text_input" type="text" placeholder="企业帐号" value="0" />
						</p>
						<ul>
							<li class="hint">
								<div class="ui-corner-all textwhite bold hint_info">
									<span id="error" class="error"></span>
								</div>
							</li>
							<li>
								<!--[if lte IE 9]><input id="username" name="username" maxLength="30" class="text_input admin_login" autocomplete="off" autofocus="autofocus" type="text" value="" /><![endif]-->
								<!--[if gt IE 9]><!--><input id="username" name="username" maxLength="30" class="text_input admin_login" autocomplete="off" autofocus="autofocus" type="text" placeholder="用户名" value="" /><!--<![endif]-->
							</li>
							<li>
								<input type="password" name="password" id="password_fake" class="hidden" autocomplete="off" style="display: none;">
								<!--[if lte IE 9]><input type="password" name="password" id="password" autocomplete="off" class="text_input password_login" autocomplete="off" value="" /><![endif]-->
								<!--[if gt IE 9]><!--><input type="password" name="password" id="password" autocomplete="off" class="text_input password_login" autocomplete="off" placeholder="密码" value="" /><!--<![endif]-->
							</li>
							<li class="rememberPassword">
								<input type="checkbox" id="autologin" value="1" checked="checked" />
								<label for="autologin" class="bold">记住用户名 </label>
								<div style="height: 8px;"></div>
								<input type="button" class="input jqueryinput" name="enter" id="enter" onclick="$('#loginform').submit()"value="登录" />
							</li>
						</ul>
					</form>
				</div>
			</div>
		</div>
		<div class="login-bottom">
			<div class="footer">
				<!-- <ul>
					<li><a href="javascript:;">版本更新</a><b>|</b></li>
					<li><a href="javascript:;">帮助中心</a><b>|</b></li>
					<li><a href="javascript:;">问题反馈</a><b>|</b></li>
					<li><a href="javascript:;">服务协议</a><b>|</b></li>
					<li><a href="javascript:;">权利声明</a></li>
				</ul> -->
				中国电子科技网络信息安全有限公司&nbsp;&nbsp;版权所有
			</div>
		</div>
	</div>
	<f:linkJs src="/assets/f/import/jquery-cookie/jquery.cookie.js" />
	<f:linkJs src="/assets/c/v_admin/login/login.js" />
	<script type="text/javascript">
         $(function(){//Bug12573

             if(window.parent!=window&&(window.frameElement&&jQuery(window.frameElement.parentElement).parents("#JS_contentTab").length)){
        	  		window.parent.window.location.reload();
             }
             $("input:password").bind("copy cut paste",function(e){
                return false;
             });
         })
     </script>
</body>
</f:html>

