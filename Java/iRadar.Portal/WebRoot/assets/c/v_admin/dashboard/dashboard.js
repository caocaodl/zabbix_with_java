jQuery(function($) {
	sendAjax("showHealth", {}, showHealth);
	sendAjax("showResourceUseRate", {}, showResourceUseRate);
	sendAjax("showCloudServiceState", {}, showCloudServiceState);
	// sendAjax("showServerLoadTop5", {}, showServerLoadTop5);
	sendAjax("showCloudServiceInfo", {}, showCloudServiceInfo);
	sendAjax("showSystemStatus", {}, showSystemStatus);
	sendAjax("showResourceUseTrend", {}, showResourceUseTrend);

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
 * 云平台信息
 */
function showCloudServiceInfo(json) {
	jQuery.each(json.data, function(k, v) {
		if (k == "hostServerCount") {
			jQuery("#information_server").text(v); //服务器		
		} else if (k == "hostSwitchCount") {
			jQuery("#information_switch").text(v); //交换机		
		} else if (k == "hostStorageNum") {
			jQuery("#information_storage").text(v); //存储设备	
		} else if (k == "coreUsedCount") {
			jQuery("#information_core").text(v); //虚拟内核数		
		} else if (k == "memoryCount") {
			jQuery("#information_mem").text(v); //内存
		} else if (k == "cinderCount") {
			jQuery("#information_storageCapacity").text(v); //存储容量			
		} else if (k == "userCount") {
			jQuery("#information_user").text(v); //租户		
		} else if (k == "machineCount") {
			jQuery("#information_machine").text(v); //云主机
		} else if (k == "imagesCount") {
			jQuery("#information_image").text(v); //镜像
		}
	});
}

/**
 * 系统状态
 */
function showSystemStatus(json) {

	var groupName = [],
		datalevel0 = [],
		datalevel1 = [],
		datalevel2 = [],
		datalevel3 = [],
		datalevel4 = [],
		datalevel5 = [];
	jQuery.each(json.data.devices, function(k, v) {
		groupName.push(k); //设备类型		
		jQuery.each(v, function(k1, v1) {
			if (k1 == "0") {
				datalevel0.push(v1);
			} else if (k1 == "1") {
				datalevel1.push(v1);
			} else if (k1 == "2") {
				datalevel2.push(v1);
			} else if (k1 == "3") {
				datalevel3.push(v1);
			} else if (k1 == "4") {
				datalevel4.push(v1);
			} else if (k1 == "5") {
				datalevel5.push(v1);
			}
		});
	});
	jQuery(".system_status .echart_ctn").highcharts({
		chart: {
			polar: true,
			type: 'column'
		},
		series: [{
			name: json.data.severities[5].name,
			color: '#' + json.data.severities[5].color,
			data: datalevel5
		}, {
			name: json.data.severities[3].name,
			color: '#' + json.data.severities[3].color,
			data: datalevel3
		}, {
			name: json.data.severities[2].name,
			color: '#' + json.data.severities[2].color,
			data: datalevel2
		}, {
			name: json.data.severities[1].name,
			color: '#' + json.data.severities[1].color,
			data: datalevel1
		}],

		title: {
			text: null
		},

		legend: {
			align: 'center'
		},

		xAxis: {
			tickmarkPlacement: 'on',
			categories: groupName,
			labels: {
				useHTML: true,
				formatter: function() {
					var title = this.value.length > 8 ? this.value.substring(0, 8) + ".." : this.value;
					return '<span title=' + this.value + ' >' + title + '</span>';
				}
			}
		},

		yAxis: {
			// min: 0,
			// endOnTick: true,
			// reversedStacks: false,
			labels: {
				enabled: false
			}

		},

		plotOptions: {
			series: {
				stacking: 'normal',
				shadow: true,
				groupPadding: 0,
				pointPlacement: 'on'
			}
		}
	});
}

/**
 * 资源使用趋势
 * @param json
 */
function showResourceUseTrend(json) {
	var _time = [],
		_cpuData = [],
		_memoryData = [],
		_diskData = [];
	jQuery.each(json.data, function(k, v) {
		if (k == "cpu") {
			jQuery.each(v, function(k1, v1, index) {
				_time.push(k1);
				_cpuData.push(v1);
			});

		} else if (k == "memory") {
			jQuery.each(v, function(k2, v2) {
				_memoryData.push(v2);
			});

		} else if (k == "disk") {
			jQuery.each(v, function(k3, v3) {
				_diskData.push(v3);
			});
		}
	});

	var elm = jQuery(".resource_use_trend .echart_ctn")[0];
	echarts.init(elm).setOption({
		tooltip: {
			trigger: 'axis',
			formatter: function(v) {
				return v[0][1] + '<br/>' + v[0][0] + ' : ' + (v[0][2]) + "%"
							   + '<br/>' + v[1][0] + ' : ' + (v[1][2]) + "%"
							   + '<br/>' + v[2][0] + ' : ' + (v[2][2]) + "%";
			}
		},
		legend: {
			data: ['CPU', '内存', '磁盘']
		},
		grid: {
			y: 40,
			height: 180
		},
		xAxis: [{
			type: 'category',
			data: _time,
			axisLabel: {
				interval: 3,
				rotate: 20,
				formatter: function(params, ticket, callback) {
					var index = _time.indexOf(params),
						str = "";
					if (index === 0 || index === _time.length - 1) {
						str = params;
					};
					return str;
				}
			}
		}],
		yAxis: [{
			type: 'value',
			axisLabel: {
				formatter: '{value} %'
			}
		}],
		series: [{
				name: 'CPU',
				type: 'line',
				smooth: true,
				showAllSymbol: true,
				symbol: 'circle',
				symbolSize: 1,
				data: _cpuData
			}
			, {
				name: '内存',
				type: 'line',
				smooth: true,
				showAllSymbol: true,
				symbol: 'circle',
				symbolSize: 1,
				data: _memoryData
			}, {
				name: '磁盘',
				type: 'line',
				smooth: true,
				showAllSymbol: true,
				symbol: 'circle',
				symbolSize: 1,
				data: _diskData
			}
		]
	}).hideLoading({});
}

/**
 * CPU负载TOP5
 * @param json
 */
function showServerLoadTop5(json) {
	var label = [],
		value = [];
	jQuery.each(json.data, function(k, v) {
		label.push(k);
		value.push(v);
	})

	var elm = jQuery(".server_load_top5 .echart_ctn")[0];
	echarts.init(elm).setOption({
		tooltip: {
			show: true,
			trigger: 'item'
		},

		xAxis: [{
			type: 'value',
			splitArea: {
				show: true
			}
		}],
		yAxis: [{
			type: 'category',
			data: label
		}],
		series: [{
			name: '服务器负载',
			type: 'bar',
			itemStyle: {
				normal: {
					color: '#1bb2d8'
				}
			},
			data: value
		}]
	});
}
/**
 * 平台服务状态
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

	echarts.init(elm).setOption({
		tooltip: {
			show: true,
			trigger: 'item'
		},
		legend: {
			data: ['可用', '不可用']
		},
		grid: {
			y: 40,
			height: 180
		},
		xAxis: [{
			type: 'category',
			data: ['计算', '存储', '网络', '控制', '门户']
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
					color: '#1bb2d8'
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

/**
 * 资源利用率
 */
function showResourceUseRate(json) {
	var dom = jQuery(".resource_use_rate .echart_ctn");
	var elm = dom[0];
	var values = [];

	var cpuRate = "CPU使用率";
	var memoryRate = "内存使用率\n";
	var diskRate = "磁盘使用率\n";

	jQuery.each(json.data, function(k, v) {
		values.push(v);
		if (k == "memory") {
			memoryRate += v;
		} else if (k == "disk") {
			diskRate += v;
		}
	});

	function buildSeries() {
		var w = dom.width();
		var h = dom.height();

		var initW = w,
			initH = h;

		function cx(x) {
			return initW * x;
		}

		function cy(x) {
			return initH * x;
		}

		function r(x) {
			return initW / 2 * x;
		}

		function al(x) {
			return initW * x / 438;
		}

		var init = [{
			center: [cx(0.5), cy(0.5)],
			radius: r(0.45),
			axisLine: { // 坐标轴线
				lineStyle: {
					width: al(10)
				}
			},
			axisTick: { // 坐标轴小标记
				length: al(15), // 属性length控制线长
				lineStyle: { // 属性lineStyle控制线条样式
					color: 'auto'
				}
			},
			splitLine: { // 分隔线
				length: al(20), // 属性length控制线长
				lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
					color: 'auto'
				}
			},
			title: {
				offsetCenter: [0, al(100)],
				textStyle: {
					fontSize: 14
				}
			}
		}, {
			center: [cx(0.2), cy(0.55)],
			radius: r(0.25),
			axisLine: {
				lineStyle: {
					width: al(8)
				}
			},
			axisTick: { // 坐标轴小标记
				length: al(12), // 属性length控制线长
				lineStyle: { // 属性lineStyle控制线条样式
					color: 'auto'
				}
			},
			splitLine: { // 分隔线
				length: al(20), // 属性length控制线长
				lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
					color: 'auto'
				}
			},
			title: {
				offsetCenter: [0, al(88)],
				textStyle: {
					fontSize: 12
				}
			}
		}, {
			center: [cx(0.8), cy(0.55)],
			radius: r(0.25),
			axisLine: {
				lineStyle: {
					width: al(8)
				}
			},
			axisTick: { // 坐标轴小标记
				length: al(12), // 属性length控制线长
				lineStyle: { // 属性lineStyle控制线条样式
					color: 'auto'
				}
			},
			splitLine: { // 分隔线
				length: al(20), // 属性length控制线长
				lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
					color: 'auto'
				}
			},
			title: {
				offsetCenter: [0, al(88)],
				textStyle: {
					fontSize: 12
				}
			}
		}]


		var cfgs = [{
			type: 'gauge',
			min: 0,
			max: 100,
			splitNumber: 10,
			detail: {
				formatter: '{value}%',
				textStyle: {
					fontSize: 20
				}
			},
			data: [{
				value: values[0],
				name: cpuRate
			}]
		}, {
			type: 'gauge',
			center: ['20%', '55%'], // 默认全局居中
			radius: '50%',
			min: 0,
			max: 100,
			endAngle: 45,
			splitNumber: 5,
			axisLabel: {
				show: false
			},
			pointer: {
				width: 5
			},
			detail: {
				formatter: '{value}%',
				textStyle: {
					fontSize: 15
				}
			},
			data: [{
				value: values[1],
				name: memoryRate
			}]
		}, {
			type: 'gauge',
			center: ['80%', '55%'], // 默认全局居中
			radius: '50%',
			min: 0,
			max: 100,
			startAngle: 135,
			endAngle: -45,
			splitNumber: 5,
			axisLabel: {
				show: false
			},
			pointer: {
				width: 2
			},
			detail: {
				formatter: '{value}%',
				textStyle: {
					fontSize: 15
				}
			},
			data: [{
				value: values[2],
				name: diskRate
			}]
		}];

		jQuery.each(cfgs, function(i) {
			jQuery.extend(this, init[i]);
		})

		return cfgs;
	}

	var chart = echarts.init(elm).setOption({
		series: buildSeries()
	});

	var originSize = chart.resize;
	chart.resize = function() {
		chart.setOption({
			series: buildSeries()
		});
		originSize.apply(chart);
	}
}

/**
 * 系统健康度
 */
function showHealth(json) {
	var elm = jQuery(".platform_health .echart_ctn")[0];
	var cpuHealth, memoryHealth, diskHealth, systemHealth, hostLinkHealth;
	jQuery.each(json.data, function(k, v) {
		if (k == "cpuHealth") {
			cpuHealth = v;
		} else if (k == "memoryHealth") {
			memoryHealth = v;
		} else if (k == "diskHealth") {
			diskHealth = v;
		} else if (k == "systemHealth") {
			systemHealth = v;
		} else if (k == "hostLinkHealth") {
			hostLinkHealth = v;
		}
	});

	echarts.init(elm).setOption({
		tooltip: {
			trigger: 'axis'
		},
		polar: [{
			indicator: [{
				text: '可用性',
				max: 100
			}, {
				text: '内存',
				max: 100
			}, {
				text: '磁盘',
				max: 100
			}, {
				text: 'CPU',
				max: 100
			}]
		}],
		series: [{
			type: 'radar',
			data: [{
				value: [hostLinkHealth, memoryHealth, diskHealth, cpuHealth],
				name: '健康度'
			}]
		}]
	});
}



//hack
function send_params(params) {
	if (typeof(params) === 'undefined') {
		params = [];
	}

	var url = new Curl("dashboard.action");
	url.setQuery('?output=ajax');

	new Ajax.Request(url.getUrl(), {
		'method': 'post',
		'parameters': params,
		'onSuccess': function() {},
		'onFailure': function() {
			document.location = url.getPath() + '?' + Object.toQueryString(params);
		}
	});
}