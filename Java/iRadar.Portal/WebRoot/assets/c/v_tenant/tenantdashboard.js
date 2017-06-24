jQuery(function($) {
	sendAjax("showCloudServiceState", {}, showCloudServiceState);
	sendAjax("showCloudCPURateTop5", {}, showCloudCPURateTop5);
	sendAjax("showCloudmemoryRateTop5", {}, showCloudmemoryRateTop5);
	sendAjax("showTriggerTrend", {}, showTriggerTrend);
	sendAjax("showCloudServiceInfo", {}, showCloudServiceInfo);
	var resizeTicket;
	window.onresize = function() {
		clearTimeout(resizeTicket);
		resizeTicket = setTimeout(function() {
			$("#content .echart_ctn").each(function() {
				var me = $(this);
				var id = me.attr("_echarts_instance_");
				if (id) {
					var chart = echarts.getInstanceById(id);
					chart.resize();
				}
			});
		}, 200);
	}
	$("#more").click(function(){
	    var pathName=window.document.location.pathname;
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
		top.jQuery.workspace.openTab('公告', projectName+'/platform/iradar/announce.action?show=true');
	});
});

function sendAjax(cmd, data, callback) {
	var url = "?output=ajax";
	var cfg = {
		"cmd": cmd
	};
	data = jQuery.extend(data || {}, cfg);
	jQuery.getJSON(url, data, callback);
}

/**
 * 配额分配情况
 */
function showCloudServiceInfo(json) {
	var datas = json.data;
	jQuery("#cloudHost").text(datas["cloudHostUsed"] + "/" + datas["cloudHostTotal"]); //云主机
	jQuery("#virtual_kernel").text(datas["virtual_kernelUsed"] + "/" + datas["virtual_kernelTotal"]); //虚拟内核
	jQuery("#memoryInfo").text(datas["memoryInfoUsed"] + "/" + datas["memoryInfoTotal"]); //内存
	jQuery("#subnet").text(datas["subnetUsed"] + "/" + datas["subnetTotal"]); //子网
	jQuery("#port").text(datas["portUsed"] + "/" + datas["portTotal"]); //端口
	jQuery("#router").text(datas["routerUsed"] + "/" + datas["routerTotal"]); //路由器
	jQuery("#float_ip").text(datas["float_ipUsed"] + "/" + datas["float_ipTotal"]); //浮动IP
	jQuery("#security_group").text(datas["security_groupUsed"] + "/" + datas["security_groupTotal"]); //安全组
	jQuery("#security_group_rule").text(datas["security_group_ruleUsed"] + "/" + datas["security_group_ruleTotal"]); //安全组规则

}

/**
 * 云主机CPU利用率TOP5
 */
function showCloudCPURateTop5(json) {
	var _names = [],
		_values = [],
		_map = {},
		_index = 1;

	jQuery.each(json.data, function(k, v) {
		_names.push(k);
		_map[k] = "TOP" + _index;
		_values.push(v);
		++_index;
	});

	
	if(_names[0]== undefined){
		var elm = jQuery(".server_cpuRate_top5");
		var html="<div style='text-align: center;height: 30px;background: rgb(246, 249, 251) none repeat scroll 0% 0%;font-size: 14px;color: rgb(114, 114, 114);line-height: 30px;'>暂无数据</div>"
		elm.children().last().html(html);
	}else{
		var elm = jQuery(".server_cpuRate_top5 .echart_ctn")[0];
		echarts.init(elm, "macarons").setOption({
			tooltip: {
				show: true,
				trigger: 'axis',
				formatter: function(v) {
					return  v[0][0]  + '<br/>' + v[0][1] + ' : ' + (v[0][2]) + "%";
				}
			},
			xAxis: [{
				type: 'category',
				data: _names,
				axisLabel: {
					show: true,
					formatter: function(val) {
						return _map[val];
					}
				}
			}],
			yAxis: [{
				type: 'value',
				boundaryGap: [0, 0.01],
				axisLabel: {
					show: true,
					interval: 'auto',
					formatter: '{value}%'
				}
			}],
			series: [{
				name: 'CPU利用率',
				type: 'bar',
				data: _values
			}]
		}).hideLoading({});
	}
}

/**
 * 云主机内存利用率TOP5
 */
function showCloudmemoryRateTop5(json) {
	var _names = [],
		_values = [],
		_map = {},
		_index = 1;
	
	jQuery.each(json.data, function(k, v) {
		_names.push(k);
		_map[k] = "TOP" + _index;
		_values.push(v);
		_index++;
	});

	if(_names[0]== undefined){
		var elm = jQuery(".server_memoryRate_top5");
		var html="<div style='text-align: center;height: 30px;background: rgb(246, 249, 251) none repeat scroll 0% 0%;font-size: 14px;color: rgb(114, 114, 114);line-height: 30px;'>暂无数据</div>"
		elm.children().last().html(html);
	}else{
		var elm = jQuery(".server_memoryRate_top5 .echart_ctn")[0];
		echarts.init(elm, "macarons").setOption({
			tooltip: {
				show: true,
				trigger: 'axis',
				formatter: function(v) {
					return  v[0][0]  + '<br/>' + v[0][1] + ' : ' + (v[0][2]) + "%";
				}
			},
			xAxis: [{
				type: 'category',
				data: _names,
				axisLabel: {
					show: true,
					formatter: function(val) {
						return _map[val];
					}
				}
			}],
			yAxis: [{
				type: 'value',
				boundaryGap: [0, 0.01],
				axisLabel: {
					show: true,
					interval: 'auto',
					formatter: '{value}%'
				}
			}],
			series: [{
				name: '内存利用率',
				type: 'bar',
				itemStyle: {
					normal: {
						color: '#1bb2d8'
					}
				},
				data: _values
			}]
		}).hideLoading({});
	}
}

/**
 * 告警产生趋势
 * @param json
 */
function showTriggerTrend(json) {
	var _time = [],
		_cpuData = [],
		_memoryData = [],
		_website = [];
	jQuery.each(json.data, function(k, v) {
		if (k == "cpu") {
			jQuery.each(v, function(k1, v1) {
				_time.push(k1);
				_cpuData.push(v1);
			});
		} else if (k == "memory") {
			jQuery.each(v, function(k2, v2) {
				_memoryData.push(v2);
			});

		} else if (k == "website") {
			jQuery.each(v, function(k3, v3) {
				_website.push(v3);
			});
		}
	});

	var elm = jQuery(".triggerTrend .echart_ctn")[0];
	echarts.init(elm).setOption({
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data: ['云主机', '服务', '网站']
		},
		calculable: false,
		xAxis: [{
			type: 'category',
			boundaryGap: false,
			data: _time
		}],
		yAxis: [{
			type: 'value'
		}],
		series: [{
			name: '云主机',
			type: 'line',
			stack: '总量',
			itemStyle: {
				normal: {
					areaStyle: {
						type: 'default'
					}
				}
			},
			data: _cpuData
		}, {
			name: '服务',
			type: 'line',
			stack: '总量',
			itemStyle: {
				normal: {
					areaStyle: {
						type: 'default'
					}
				}
			},
			data: _memoryData
		}, {
			name: '网站',
			type: 'line',
			stack: '总量',
			itemStyle: {
				normal: {
					areaStyle: {
						type: 'default'
					}
				}
			},
			data: _website
		}]
	});
}


/**
 * 监控状态
 */
function showCloudServiceState(json) {
	var elm = jQuery(".cloud_service_state .echart_ctn")[0];
	var availabe = [],
		disable = [];
	jQuery.each(json.data, function(k, v) {
		if (k == "normal") {
			jQuery.each(v, function(ki, vi) {
				availabe.push(vi);
			});
		} else {
			jQuery.each(v, function(kj, vj) {
				disable.push(vj);
			});
		}
	});

	echarts.init(elm, "macarons").setOption({
		tooltip: {
			show: true,
			trigger: 'item'
		},
		legend: {
		    	y : 'top',
		        data:['可用','不可用']
		    },
		xAxis: [{
			type: 'category',
			data: ['云主机', '服务', '网站']
		}],
		yAxis: [{
			type: 'value',
			splitArea: {
				show: true
			}
		}],
		series: [{
			name: '可用',
			type: 'bar',
			itemStyle: {
				normal: {
					color: '#28c6b9'
				}
			},
			data: availabe
		}, {
			name: '不可用',
			type: 'bar',
			itemStyle: {
				normal: {
					color: 'rgba(255, 127, 80, 1)'
				}
			},
			data: disable
		}]
	});
}


function showloading(){
	var elm = jQuery(document.body);
	var html="<div><div id='loaddiv' style='position: absolute;top: 50%;left: 50%;text-align: center;height: 30px;background: rgb(246, 249, 251) none repeat scroll 0% 0%;font-size: 14px;color: rgb(114, 114, 114);line-height: 30px;'>" +
			"<img src='./images/general/loading.gif' width='50' height='50' /></div></div>";
	elm.append(html);
}

function hideloading(){
	jQuery("#loaddiv").parent().remove();
	jQuery("#loaddiv").remove();
}