jQuery(function($) {
	var reg = /^(\w)+(\.\w+)*@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	$('#tsave').click(function() {
		var value = $('#Email').val();
		if(value != ""){
			if(!reg.test(value)) {
			    showModalWindow("请输入有效的邮箱地址！");
			    return false;
			}
		}
	}); 
});

function testMobile(str) {
	var reg = /^((13[0-9])|(15[^4,\D])|(18[0,5-9]))\d{8}$/;
	return reg.test(str) ? true : false;
}

jQuery(document).keydown(function(event){
	if(event.keyCode == "13"){
		return false;
	}
}); 