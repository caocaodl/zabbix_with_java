function checkSubmit() {
	var $userMediaFormList = jQuery("#userMediaFormList"),
		$checkedList = $userMediaFormList.find(":checked");
	if ($checkedList.length) {
		var form = $userMediaFormList.parents("form");
		var delBtn = jQuery("#del_user_media");
		showModalWindow('提示', '确认删除数据？', [{
			text: '确定',
			click: function() {
				var objVar = document.createElement('input');
				objVar.setAttribute('type', 'hidden');
				objVar.setAttribute('name', delBtn.attr("id"));
				objVar.setAttribute('id', delBtn.attr("id"));
				objVar.setAttribute('value', delBtn.attr("value"));
				form.append(objVar);
				form.submit();
			}
		}, {
			text: '取消',
			click: function() {
				jQuery(this).dialog('destroy');
			}
		}])

	} else {
		//提示请选择..
		alert("请选择需要删除的记录");
		return false;
	}
}

//function onSubmitFotBtn() {
//	var $mediatypeVal = jQuery("#mediatypeid").val();
//	//邮件
//	if ($mediatypeVal === "1" && !testEmail(jQuery("#sendto").val())) {
//		alert("正确填写邮件");
//		return false;
//	}
//	//手机号码
//	if ($mediatypeVal === "2" && !testMobile(jQuery("#sendto").val())) {
//		alert("正确填写手机号码");
//		return false;
//	}
//	return true;
//}

function testMobile(str) {
	var reg = /^((13[0-9])|(15[^4,\D])|(18[0,5-9]))\d{8}$/;
	return reg.test(str) ? true : false;
}