/**
 * 改变阀值规则状态
 * @param value
 */
function changeStatus(_this){
	var _url = "changeStatus.action?go="+jQuery(_this).attr("go")+"&hostid="+jQuery(_this).attr("hostid")+"&g_triggerid="+jQuery(_this).attr("g_triggerid")+"&sid="+jQuery(_this).attr("sid");
	var divid = jQuery(_this).attr("random");	//随机数
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(jQuery(_this).attr("class") == "disabled"){
				jQuery(_this).attr("class","enabled");	//已启用
				jQuery(_this).text("已启用");
				jQuery("#"+divid).attr("class","status_icon iconok");
			}else{
				jQuery(_this).attr("class","disabled");	//已停用
				jQuery(_this).text("已停用");
				jQuery("#"+divid).attr("class","");
			}
			window.location.href = window.location.href;
		}
	});
}

/**
 * 改变发现策略状态
 * @param value
 */
function changeDiscoveryStatus(_this){
	var _url = "changeDiscoveryStatus.action?go="+jQuery(_this).attr("go")+"&g_druleid="+jQuery(_this).attr("g_druleid")+"&sid="+jQuery(_this).attr("sid");
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(msg!=""){
				jQuery(_this).parent().parent().parent().children('td:eq(1)').empty();//状态改变，名称列也随之改变
				jQuery(_this).parent().parent().parent().children('td:eq(1)').html(msg);
				if(jQuery(_this).attr("go") == "activate"){
					jQuery(_this).attr("class","enabled");	//已启用
					jQuery(_this).text("已启用");
					jQuery(_this).attr("go","disable");
				}else{
					jQuery(_this).attr("class","disabled");	//已启用
					jQuery(_this).text("已停用");
					jQuery(_this).attr("go","activate");
					
				}
			}
		
			window.location.href = window.location.href;
		}
	});
}

/**
 * 改变动作规则状态
 * @param value
 */
function changeActionStatus(_this){
	var _url = "changeActionStatus.action?go="+jQuery(_this).attr("go")+"&g_actionid="+jQuery(_this).attr("g_actionid")+"&sid="+jQuery(_this).attr("sid")+"&eventsource="+jQuery(_this).attr("eventsource");
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(jQuery(_this).attr("class") == "disabled"){
				jQuery(_this).attr("class","enabled");	//已启用
				jQuery(_this).text("已启用");
			}else{
				jQuery(_this).attr("class","disabled");	//已停用
				jQuery(_this).text("已停用");
			}
			window.location.href = window.location.href;
		}
	});
}

/**
 * 改变通知策略状态
 * @param value
 */
function changeMediaTypeStatus(_this){
	var _url = "changeMediaTypeStatus.action?go="+jQuery(_this).attr("go")+"&mediatypeids="+jQuery(_this).attr("mediatypeids")+"&sid="+jQuery(_this).attr("sid");
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(jQuery(_this).attr("class") == "disabled"){
				jQuery(_this).attr("class","enabled");	//已启用
				jQuery(_this).text("已启用");
			}else{
				jQuery(_this).attr("class","disabled");	//已停用
				jQuery(_this).text("已停用");
			}
			window.location.href = window.location.href;
		}
	});
}

/**
 * 改变web监控状态
 * @param value
 */
function changeHttpConfStatus(_this){
	var _url = "changeHttpConfStatus.action?go="+jQuery(_this).attr("go")+"&statusflag="+jQuery(_this).attr("statusflag")+"&sid="+jQuery(_this).attr("sid")+"&group_httptestid[]="+jQuery(_this).attr("group_httptestid[]")+"&hostid="+jQuery(_this).attr("hostid");
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(jQuery(_this).attr("class") == "disabled"){
				jQuery(_this).attr("class","enabled");	//已启用
				jQuery(_this).text("已启用");
				jQuery(_this).attr("go","disable");
			}else{
				jQuery(_this).attr("class","disabled");	//已启用
				jQuery(_this).text("已停用");
				jQuery(_this).attr("go","activate");
			}
			window.location.href = window.location.href;
		}
	});
}

/**
 * 改变设备列表状态
 * @param value
 */
function changeMonitorStatus(_this){
	var _url = "changeMonitorStatus.action?go="+jQuery(_this).attr("go")+"&hosts="+jQuery(_this).attr("hosts")+"&sid="+jQuery(_this).attr("sid")+"&templateid="+jQuery(_this).attr("templateid")+"&selHostType="+jQuery(_this).attr("selHostType")+"&selTemplateId="+jQuery(_this).attr("selTemplateId")+"&selMaintenanceId="+jQuery(_this).attr("selMaintenanceId");
	jQuery.ajax({
		url: _url,
		cache: true,	//禁止缓存
		async: false,	//同步
		timeout: 30000, 
		error: function(XMLHttpRequest, textStatus, errorThrown){
			
		},
		success: function(msg) {
			if(msg!=""){
				var olds=msg.split(",")[0];
				var sel =msg.split(",")[1];
				if(sel=='true\n'){
				   if(olds == "0"){
					   jQuery(_this).attr("class","disabled");//已停用
					   jQuery(_this).text("停止监控");
				       jQuery(_this).attr("go","activate");
				   }else {	
					    jQuery(_this).attr("class","enabled");	//已启用
						jQuery(_this).text("监控中");
						jQuery(_this).attr("go","disable");
					}
				}else{
				   showModalWindow("修改未成功");
				} 
			}
			window.location.href = window.location.href;
		}
	});
}

