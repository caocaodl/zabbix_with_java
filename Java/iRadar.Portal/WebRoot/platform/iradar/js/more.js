$(document).ready(function(){	
	$(".more_ctn").hover(function(){
		$(this).children(".more_show_ctn").slideDown();
		changeIcon($(this).children(".more_btn"));
	},function(){
		$(this).children(".more_show_ctn").slideUp();
		changeIcon($(this).children(".more_btn"));
	});
	
});

/**
 * 修改主菜单的指示图标
 */
function changeIcon(mainNode) {
	if (mainNode) {
		if (mainNode.css("background-image").indexOf("more_t.png") >= 0) {
			mainNode.css("background-image","url('../../../../platform/iradar/style/images/more_b.png')");
		} else {
			mainNode.css("background-image","url('../../../../platform/iradar/style/images/more_t.png')");
		}
	}
}
