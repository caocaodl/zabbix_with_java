jQuery(function() {
	var notice = jQuery("textarea[id='content']");

	jQuery("#save").on("click", function() {
		if (notice.val().length > 200) {
			lockButton(false);
			showModalWindow("只允许输入200个字符");
			return false;
		}
	});
});