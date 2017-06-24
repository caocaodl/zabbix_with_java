jQuery(function($) {
	var soundSupport = jQuery.browser.chrome || jQuery.browser.firefox || jQuery.browser.opera;
	var tenantType = window.isTenant;

	//sound
	var sounder = {
		play: $.noop,
		stop: $.noop
	}
	if(soundSupport){
		$('<audio  id="warnChatAudio"><source src="../assets/c/header/sound/alarm_disaster.mp3" type="audio/mp3"></audio>').appendTo('body');
		$('<audio  id="faultChatAudio"><source src="../assets/c/header/sound/alarm_1.mp3" type="audio/mp3"></audio>').appendTo('body');
		sounder.play = function(f){
			$('#'+f+'ChatAudio')[0].play();
		}
		sounder.stop = function(f){
			$('#'+f+'ChatAudio')[0].pause();
		}
	}else{
		$('<bgsound id="song" src="" />').appendTo('body');
		var sounds = {
			warn: "../assets/c/header/sound/alarm_1.wav",
			fault: "../assets/c/header/sound/alarm_disaster.wav"
		}
		sounder.play = function(f){
			$('#song').attr("src", sounds[f]);
		}
		sounder.stop = function(){
			$('#song').attr("src", '');
		}
	}
	
	//tenant
	var runner = new function(){
		var INTERVAL = 1000 * 30, CLZ_ACTIVE = "active";
		
		var prefix = tenantType? "tactivealarm": "activealarm";
		var url = "iradar/"+prefix+".action?actAlarmFaultNum";
		
		var ctn = $(".header_top .notice");
		ctn.append('<span class="hint_mail"><a id="alarm"></a></span>');
		
		$("#alarm").parent().andSelf().click(function(){$.workspace.openTab(tenantType? "00100001": "00040001");});
		$("#fault").parent().andSelf().click(function(){$.workspace.openTab("00040003");});
		
		var warnF = function(num){
			$('#alarm').html(num);
			
			var has = parseInt(num)>0;
			$('#alarm')[has? 'addClass': 'removeClass'](CLZ_ACTIVE);
			sounder[has? 'play': 'stop']('warn');
		}
		this.run = function(){
			$.getJSON(url, function(dataObj) {
				$.each(dataObj, function(i, item) {
					warnF(item.actAlarmNum);
				});
				setTimeout(function(){runner.run();}, INTERVAL);
			});
		}
	}
	runner.run();
});