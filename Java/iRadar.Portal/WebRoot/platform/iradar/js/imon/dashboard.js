function Popupprompts(data){ 
	var screenWidth = jQuery(window).width();//当前窗口宽度
	var screenHeight = jQuery(window).height();//当前窗口高度
	jQuery("#maskid").empty();
	jQuery("#maskid").css({
		"display":"",
		"position": "fixed",
		"background": "#000",
		"z-index": "1001",
		"-moz-opacity": "0.5",
		"opacity":".50",
		"filter": "alpha(opacity=80)",
		"width":screenWidth,
		"height":screenHeight});
	var strHtml='<div class="madkdiv" id="madkdivid">'
    strHtml +=	'<div><h4 class="alert-heading">'+data[0].title+'</h4></div>'
    strHtml += '<div class="p_text">'+data[0].comment+'</div>';
    strHtml += '<p class="p_button"><input type="button" class="buttonorange" value="知道了" onclick="hidemask()" /></p>';
    strHtml +='</div>';
//    jQuery(strHtml).appendTo('#maskid');
    jQuery(strHtml).appendTo(jQuery('body'));
}

function hidemask(){
	jQuery("#maskid").empty();
	jQuery("#maskid").hide();
	jQuery("#madkdivid").remove();
	jQuery("#maskid").css({
		"display":"none"
	});
}


