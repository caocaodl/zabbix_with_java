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
	//$('#randomImg')[0].src="imageServlet?timestamp="+timestamp;
	$('#timestamp').val(timestamp);
}
 //第一种方法
 $(document).ready(function(){	 
	 //获取cookie里名字为“username”的值，并设到对应input里	

	 $("#username").val($.cookie("username"));
	 $("#password").val('');//将密码框置空
	 var ck=$.cookie("checked");	
	 ck=="true"?ck=true:ck=false;
	 $("#autologin").attr('checked',ck);	 	 
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
        $(this).css({border:"1px solid #058fcd"});
    }).bind('blur',function(){
    	$(this).css({border:"1px solid #cbccd1"});
    });

    $("#password").bind('focus',function(){
        $('.error',$(this).parent()).remove();
        $(this).css({border:"1px solid #058fcd"});
    }).bind('blur',function(){
    	$(this).css({border:"1px solid #cbccd1"});
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
                } else if(data.status == -10){
                	setErrorInfo('请使用管理员用户登录系统');
                } else {
                	if($("#autologin").attr("checked")==="checked"){
                		//往cookie里面设一个名字为 "username"的值，这个值应该是input的value
                		 var username=$("#username").val();
                		 var checked=$("#autologin").attr("check");
                		 $.cookie("username", username, {expires:7}); 
                		 $.cookie("checked",true);
                	}else{
                		//删除cookie里名字为"username"的值
                		 $.removeCookie("username");
                		 $.cookie("checked",false);
                	}
                	
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
         };
     }).blur(function() {
         var input = $(this);
         if (input.val() == '' || input.val() == input.attr('placeholder')) {
             input.addClass('placeholder');
             input.val(input.attr('placeholder'));
         };
     }).blur();
 };
 })
 function placeholderSupport() {
     return 'placeholder' in document.createElement('input');
 }
 
 //enter键触发登录
 $(document).keydown(function (event) {
	    if (event.keyCode == 13) {
	    	var loginObj = $("input#enter");
	    	if(loginObj.length > 0) loginObj.click();
	    }
	});

 function setErrorInfo(errorInfo){
	 $('#error').text(errorInfo);
	 //setTimeout('$("#error").text("")',3000);
 };