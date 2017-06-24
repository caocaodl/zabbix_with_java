$(function(){
	var contentTab = $("#JS_contentTab");
	
	var f = function(){
		var tab = contentTab.tabs('getSelected');
		if(tab != null){
			var iframe = $("iframe", tab);
			var iframeE = iframe[0];
			var cw =  iframeE.contentWindow || iframeE;
			var body = cw.document.body;
			if(body){
				var contentH = $(body).height();
				var minH = tab.height();
				contentH = Math.max(contentH, minH);
				var iframeH = iframe.height();
				if(iframeH != contentH){
					iframe.height(contentH);
				}
			}
		}
		setTimeout(f, 300);
	}
	f();
});