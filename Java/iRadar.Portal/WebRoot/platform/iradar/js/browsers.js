var agt = navigator.userAgent.toLowerCase(),
	IE6 = (agt.indexOf('msie 6.0') != -1),
	IE7 = (agt.indexOf('msie 7.0') != -1),
	IE8 = (agt.indexOf('msie 8.0') != -1),
	IE9 = (agt.indexOf('msie 9.0') != -1),
	IE10 = (agt.indexOf('msie 10.0') != -1),
	IE11 = !!agt.match(/trident\/.*rv:11/),
	IE = (IE6 || IE7 || IE8 || IE9 || IE10 || IE11),
	CR = (agt.indexOf('chrome') != -1),
	SF = (agt.indexOf('safari') != -1 && !CR),
	KQ = (agt.indexOf('konqueror') && agt.indexOf('khtml') != -1 && agt.indexOf('applewebkit') == -1),
	GK = (agt.indexOf('gecko') != -1);

// redirect outdated browser to warning page
if (document.cookie.indexOf('browserwarning_ignore') < 0) {
	if (IE6 || IE7 || KQ) {
		window.location.replace('../../browserwarning.jsp');
	}
}
