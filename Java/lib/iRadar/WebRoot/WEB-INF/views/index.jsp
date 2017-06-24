<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<%@ page import="com.isoft.Feature" %>
<%
String uri = request.getContextPath();
if(!uri.endsWith("/")){
	uri += "/";
}
uri = uri + "checkcode.action";
%>
<f:html>
<style>
body
{
	margin:0;
	padding:0;
	background-image: url(assets/images/bg.jpg);
	background-repeat: no-repeat;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}

/*login -- starts*/
.login {
	padding: 0;
	width:790px;
	height:312px;
	background-image: url(assets/images/login_bg.png);
	background-repeat: no-repeat;
	background-position:0 0;
	position:absolute;
	top:50%;
	left:50%;
	margin:-156px 0px 0px -395px;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
.login h3{
	margin: 0;
	padding: 30px 0px 0px 240px;
	background: none;
}
.text_input, .submit_btn {
	margin: 8px 0;
	height: 26px;
	font-size: 13px;
	background-repeat: no-repeat;
	border: none;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
.text_input {
	width: 280px;
	color: #34495e;
	border: 1px solid #b2cddd;
}
.submit_btn {
	color:#FFF;
	font-weight:bold;
	background-color: #bfc5ca;
	background-image: url(assets/images/logbg.png);
	background-repeat: repeat-x;
	-webkit-border-radius: .3em;
	-moz-border-radius: .3em;
	-o-border-radius: .3em;
	border-radius: .3em;
	width: 55px;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
.login  a:hover{text-decoration:underline;color: #C20C0C;} 
.login form{
	margin:0;
	padding:15px 0 0 240px;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
.login p{
	margin:0;
	padding:0;
	color:#75a0b9;
	font-size:14px;
	height: 35px;
	font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
/*login -- ends*/
.footer { 
    background: none repeat scroll 0% 0% #FFF;
    margin-top:-23px;
    height: 22px;
    border-top: 1px solid #E1E1E1;
    position: relative;
    top:100%;
    font-family:arial, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑', '宋体', \5b8b\4f53, Tahoma, Arial, Helvetica, STHeiti
}
.footer p {
	text-align:center;
    margin:0;
	padding:0;
	color:#75a0b9;
	font-size:14px;
}
.error{
	float: inherit;
}
</style>
<link rel="shortcut icon" href="platform/iradar/images/general/iradar.ico" />
<f:head/>
<body>

	<div class="login">
		<h3>
			<span style="width: 326px; height: 32px; text-align: center; float: left; font-size: 27px; color: #75a0b9;"><%=Feature.title%></span>
		</h3>
		<form action="login.action" id="loginform" name="loginform"
			method="post">
			<p style="display: none;">
				企业帐号:
				<input id="tenant" name="tenant" class="text_input" type="text" placeholder="企业帐号" value="0" />
			</p>
			<p>
				用&nbsp;户&nbsp;名:
				<input id="username" name="username" class="text_input" type="text" placeholder="用户名" value="<%=Feature.defaultUser%>" />
			</p>
			<p>
				密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码:
				<input id="password" name="password" class="text_input" type="password" placeholder="密码" value="<%=Feature.defaultPassword%>" />
			</p>
			<p>
				验&nbsp;证&nbsp;码:
				<input id="timestamp" type="hidden" name="timestamp" value=""><input type="text" id="randomCode" style="width:60px;" name="randomCode" value="<%=Feature.defaultCheckCode%>"/><img title="点击更换" id="randomImg" style="margin-bottom: -10px;padding: 3px 4px 1px;width: 80px;height: 26px;" alt="加载中" onclick="javascript:refresh();"><a onclick="javascript:refresh();">看不清，换一张</a><br/>
			</p>
			<p>
				<input type="button" id="login-button" class="submit_btn" onclick="$('#loginform').submit()" value="登录" />
				<span id="error" style="width:150px;color:red;font-size: 15px;"></span>
			</p>
		</form>
	</div>
	<div class="footer">
  <p>普华基础软件股份有限公司&nbsp;&copy;2014&nbsp;</p>  
</div>

<script type="text/javascript">
$(document).ready(function() {
	refresh();
});
function checkRandomCode(){
	var randomCode = $('#randomCode').val();
	if(randomCode!=''){
	}else{
		jAlert('请输入验证码!', '信息提示');
	}
}
function refresh() {
	$('#randomCode').val("aaaa");
	var timestamp=(new Date()).valueOf();
	$('#randomImg')[0].src="imageServlet?timestamp="+timestamp;
	$('#timestamp').val(timestamp);
}
 //第一种方法
 $(document).ready(function(){
     var doc=document,inputs=doc.getElementsByTagName('input'),
         supportPlaceholder='placeholder'in doc.createElement('input'),
         placeholder=function(input){
             var text=input.getAttribute('placeholder'),
             defaultValue=input.defaultValue;
             if(defaultValue==''){
                 input.value=text
             }
             input.onfocus=function(){
                 if(input.value===text){this.value=''}
             };
             input.onblur=function(){
                 if(input.value===''){this.value=text}
             }
         };

    if(!supportPlaceholder){
        for(var i=0,len=inputs.length;i<len;i++){
            var input=inputs[i],text=input.getAttribute('placeholder');
            if(input.type==='text'&&text){placeholder(input)}
        }
    }

    $("#tenant").bind('focus',function(){
        $('.error',$(this).parent()).remove();
    });
    
    $("#username").bind('focus',function(){
        $('.error',$(this).parent()).remove();
    });

    $("#password").bind('focus',function(){
        $('.error',$(this).parent()).remove();
    });

    $("#loginform").bind('submit',function(){
    	$('.error').text('');
    	if($("#tenant").val()==''){
    		setErrorInfo('请输入企业帐号');
            return false;
        }
        if($("#username").val()==''){
        	setErrorInfo('请输入用户名');
            return false;
        }
        if($("#password").val()==''){
        	setErrorInfo('请输入密码');
            return false;
        }
        if($("#randomCode").val()==''){
        	setErrorInfo('请输入验证码');
            return false;
        }
        var randomCodeChecked=false;
        var randomCode = $('#randomCode').val();
        $.ajax({
    		url : '<%=uri%>',
    		data : {
    			randomCode : randomCode ,
    			timestamp : $('#timestamp').val()
    		},
    		dataType : "json",
    		async : false,
    		success : function(json) {
    			if (json.success) {
    				randomCodeChecked = true;
    			} else {
    				randomCodeChecked = false;
    			}
    		}
    	});
        if(!randomCodeChecked){
        	setErrorInfo('验证码输入错误或已经超时失效，请重新输入');
        	refresh();
        	return false;
        }
        $.ajax( {
            url : $("#loginform").attr("action"),
            data : {
	            tenant:$("#tenant").val(),
	            username:$("#username").val(),
	            password:$("#password").val()
            },
            dataType : "json",
            async:false,
            success : function(data) {
                if(data.status == 0){
                	setErrorInfo('请输入企业帐号\用户名\密码');
                } else if(data.status == -1){
                	setErrorInfo('用户名或密码错误');
                } else if(data.status == -2){
                	setErrorInfo('用户尚未激活');
                } else if(data.status == -3){
                	setErrorInfo('企业帐号尚未激活');
                } else if(data.status == -4){
                	setErrorInfo('用户已被禁用');
                } else if(data.status == -5){
                	setErrorInfo('企业帐号已被禁用');
                } else {
                    $(location).attr('href','platform/workspace.action?ts='+(new Date()).getTime());
                }
            }
        });
        return false;
    });
});
 
//第二种方法
 $(function(){
 if(!placeholderSupport()){   // 判断浏览器是否支持 placeholder
     $('[placeholder]').focus(function() {
         var input = $(this);
         if (input.val() == input.attr('placeholder')) {
             input.val('');
             input.removeClass('placeholder');
         }
     }).blur(function() {
         var input = $(this);
         if (input.val() == '' || input.val() == input.attr('placeholder')) {
             input.addClass('placeholder');
             input.val(input.attr('placeholder'));
         }
     }).blur();
 };
 })
 function placeholderSupport() {
     return 'placeholder' in document.createElement('input');
 }
 
 //enter键触发登录
 $(document).keydown(function (event) {
	    if (event.keyCode == 13) {
	    	var loginObj = $("input[id=login-button]");
	    	if(loginObj.length > 0) loginObj.triggerHandler('click');
	    }
	});

 function setErrorInfo(errorInfo){
	 $('#error').text(errorInfo);
	 setTimeout('$("#error").text("")',3000);
 }
 </script>
</body>
</f:html>

