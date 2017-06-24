<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="f" uri="/isoft/core" %>
<%@ taglib prefix="e" uri="/isoft/easyui" %>
<f:html>
<f:head/>
<body>
    <div id="login-wrapper">
        <div id="login">
            <div id="login-form">
                <form action="login.action" id="loginform" name="loginform" method="post">
                    <div class="input-group">
                        <input id="tenant" name="tenant" type="text" placeholder="企业帐号" value="0"/>                        
                    </div>
                    <div class="input-group">
                        <input id="username" name="username" type="text" placeholder="用户名" value="root"/>                        
                    </div>
                    <div class="input-group">
                        <input id="password" name="password" type="password" placeholder="密码" value="123"/>
                    </div>
                    <div class="form-actions">
                        <div class="action">
                            <a id="login-button" style="cursor:pointer;" onclick="$('#loginform').submit()"><img src="assets/images/login-button.png"></a>
                        </div>
                        <div class="action" >
                            <a href="platform/ProfForgotPwd.action"><span>忘记密码</span></a>
                        </div>    
                    </div>
                </form>
            </div>
        </div>
        <div id="footer">
          <p>普华基础软件股份有限公司&nbsp;&copy;2012&nbsp;</p>
        </div>
    </div>
    
<script type="text/javascript">
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
    	if($("#tenant").val()=='企业帐号'){
            $('<div class="error"><span class="arrow"></span>请输入企业帐号</div>').appendTo($("#tenant").parent());
            return false;
        }
        if($("#username").val()=='用户名'){
            $('<div class="error"><span class="arrow"></span>请输入用户名</div>').appendTo($("#username").parent());
            return false;
        }
        if($("#password").val()=='密码'){
            $('<div class="error"><span class="arrow"></span>请输入密码</div>').appendTo($("#password").parent());
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
                    $('<div class="error"><span class="arrow"></span>请输入企业帐号\用户名\密码</div>').appendTo($("#username").parent());
                } else if(data.status == -1){
                    $('<div class="error"><span class="arrow"></span>用户名或密码错误</div>').appendTo($("#username").parent());
                } else if(data.status == -2){
                    $('<div class="error"><span class="arrow"></span>用户尚未激活</div>').appendTo($("#username").parent());
                } else if(data.status == -3){
                    $('<div class="error"><span class="arrow"></span>企业帐号尚未激活</div>').appendTo($("#tenant").parent());
                } else if(data.status == -4){
                    $('<div class="error"><span class="arrow"></span>用户已被禁用</div>').appendTo($("#username").parent());
                } else if(data.status == -5){
                    $('<div class="error"><span class="arrow"></span>企业帐号已被禁用</div>').appendTo($("#tenant").parent());
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
	    	var loginObj = $("a[id=login-button]");
	    	if(loginObj.length > 0) loginObj.triggerHandler('click');
	    }
	});
 </script>
</body>
</f:html>

