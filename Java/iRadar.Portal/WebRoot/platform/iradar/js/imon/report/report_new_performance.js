var area = new Array("area1", "area2", "area3", "area4");
//var host ="";

jQuery(function($) {
	$("#topnPeriod input[type='radio']").click(function() {
		var data = getParameterdata();
		getJsondata(data);
	});

	$("#trendPeriod input[type='radio']").click(function() {
		getJsonTrenddata();
	});

	$("#group").change(function() {
		var type = jQuery("#type input[type='radio']:checked").val();
		if (0 == type) {
			var data = getParameterdata();
			getJsondata(data);
		} else {
			addHosts();
		}
	});

	$("#csv_export").click(function() {
		var period = jQuery("#topnPeriod input[type='radio']:checked").val(); //topN周期
		var groupid = jQuery("#group").val(); //设备类型
		var url = "performance_report.action?csv_export=true&topnperiod=" + period + "&groupid=" + groupid;

		location.href = url;
	});

	$("#selectHost").change(function() {
		getJsonTrenddata();
	});

	$("#type").delegate("INPUT", "change", function() {
		var v = this.value * 1;
		$(".select_ctn.trend_period, .select_ctn.host")[v ? "show" : "hide"]();
		$(".select_ctn.topn_period")[!v ? "show" : "hide"]();
		if (1 == v) {
			jQuery(".csv_exportid")["hide"]();
			var trendperiod = jQuery("#trendPeriod input[type='radio']:checked").val();
			if (trendperiod == undefined) {
				jQuery("#trendPeriod input[type='radio']:first").attr('checked', 'checked');
				jQuery("#trendPeriod_label_0").addClass('ui-state-active');
			}
			addHosts();
		} else {
			jQuery(".csv_exportid")["show"]();
			var data = getParameterdata();
			getJsondata(data);
		}
	});

	init();
});

function init() {
	var data = getParameterdata();
	getJsondata(data);
}

function getParameterdataByhostid(hostid) {
	var groupid = jQuery("#group").val();
	var type = jQuery("#type input[type='radio']:checked").val(); //报表类型
	var topnperiod = jQuery("#topnPeriod input[type='radio']:checked").val(); //topN周期
	var trendperiod = jQuery("#trendPeriod input[type='radio']:checked").val(); //trend周期
	var data = {
		"reporttype": "performance",
		"type": type,
		"topnperiod": topnperiod,
		"trendperiod": trendperiod,
		"groupid": groupid,
		"hostid": hostid
	};
	return data;
}


function getParameterdata() {
	var groupid = jQuery("#group").val();
	var type = jQuery("#type input[type='radio']:checked").val(); //报表类型
	var topnperiod = jQuery("#topnPeriod input[type='radio']:checked").val(); //topN周期
	var trendperiod = jQuery("#trendPeriod input[type='radio']:checked").val(); //trend周期
	var hostid = jQuery('#selectHost option:selected').val();
	var data = {
		"reporttype": "performance",
		"type": type,
		"topnperiod": topnperiod,
		"trendperiod": trendperiod,
		"groupid": groupid,
		"hostid": hostid
	};
	return data;
}

function getJsonTrenddata(data) {
	showloading();
	var url = "iradar/performance_report.action";
	var data = getParameterdata();
	jQuery('#titlediv').hide();
	jQuery('.hostname').hide();
	jQuery('#cdata').empty();
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			var titlearray = new Array();
			var groupid = jQuery("#group").val();
			if (groupid == 1) {
				titlearray = ["CPU利用率", "内存利用率", "磁盘读速率", "磁盘写速率"];
				unitarray = ["%", "%", "", ""];
			} else if (groupid == 2) {
				titlearray = ["CPU利用率", "内存利用率", "磁盘读速率", "磁盘写速率"];
				unitarray = ["%", "%", "", ""];
			} else if (groupid == 3) {
				titlearray = ["发送丢包率", "接收丢包率", "发送速率", "接收速率"];
				unitarray = ["%", "%", "Bps", "Bps"];
			}

			for (var i = 1; i < 5; i++) {
				if (titlearray[i - 1] == "") {
					jQuery('#area' + i + '_title').hide();
					jQuery('#area' + i + '_title').text(titlearray[i - 1]);
					jQuery('#area' + i).empty();
					jQuery('#area' + i).hide();
				} else {
					jQuery('#area' + i + '_title').show();
					jQuery('#area' + i + '_title').text(titlearray[i - 1]);
					jQuery('#area' + i).empty();
					jQuery('#area' + i).show();
					jQuery('#area'+i).css("display","table");
				}
			}
			showdataTrend(area[0], item.area1src);
			showdataTrend(area[1], item.area2src);
			showdataTrend(area[2], item.area3src);
			showdataTrend(area[3], item.area4src);
			hideloading();
		});
	});

}

function getJsonTrenddataByhostid(hostid) {
	var url = "iradar/performance_report.action";
	var data = getParameterdataByhostid(hostid);
	showloading();
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			Trenddatashow(item);
			hideloading();
		});
	});
}

function Trenddatashow(item) {
	var titlearray = new Array();
	jQuery(".w").addClass("trend");
	var groupid = jQuery("#group").val();
	jQuery('#titlediv').hide();
	jQuery('.hostname').hide();
	jQuery('#cdata').empty();
	if (groupid == 1) {
		titlearray = ["CPU利用率", "内存利用率", "磁盘读速率", "磁盘写速率"];
		unitarray = ["%", "%", "", ""];
	} else if (groupid == 2) {
		titlearray = ["CPU利用率", "内存利用率", "磁盘读速率", "磁盘写速率"];
		unitarray = ["%", "%", "", ""];
	} else if (groupid == 3) {
		titlearray = ["发送丢包率", "接收丢包率", "发送速率", "接收速率"];
		unitarray = ["%", "%", "Bps", "Bps"];
	}
	for (var i = 1; i < 5; i++) {
		if (titlearray[i - 1] == "") {
			jQuery('#area' + i + '_title').hide();
			jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			jQuery('#area' + i).empty();
			jQuery('#area' + i).hide();
		} else {
			jQuery('#area' + i + '_title').show();
			jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			jQuery('#area' + i).empty();
			jQuery('#area' + i).show();
			jQuery('#area'+i).css("display","table");
		}
	}

	showdataTrend(area[0], item.area1src);
	showdataTrend(area[1], item.area2src);
	showdataTrend(area[2], item.area3src);
	showdataTrend(area[3], item.area4src);
}

function getJsondata(data) {
	jQuery("#group").attr("disabled","disabled");
	showloading();
	var url = "iradar/performance_report.action";
	var titlearray = new Array();
	var unitarray = new Array();
	var indicatorsarray = new Array();
	var hosttitlearray = new Array();

	var reporttitle;
	jQuery(".w").removeClass("trend");
	var groupid = jQuery("#group").val();

	if (groupid == 1) {
		titlearray = ["CPU平均利用率", "内存平均利用率", "", ""];
		unitarray = ["%", "%", "", ""];
		indicatorsarray = ["cpuused", "memory", "", ""];
		reporttitle = "服务器运行";
		hosttitlearray = ["设备名称", "IP地址", "CPU平均利用率", "CPU最大利用率", "内存平均利用率", "内存最大利用率", "磁盘利用率", "磁盘读速率", "磁盘写速率", "网络上行速率", "网络下行速率"];
	} else if (groupid == 2) {
		titlearray = ["CPU平均利用率", "内存平均利用率", "", ""];
		unitarray = ["%", "%", "", ""];
		indicatorsarray = ["cpuused", "memory", "", ""];
		reporttitle = "云主机运行";
		hosttitlearray = ["设备名称", "IP地址", "CPU平均利用率", "CPU最大利用率", "内存平均利用率", "内存最大利用率", "磁盘利用率", "磁盘读速率", "磁盘写速率", "网络上行速率", "网络下行速率"];
	} else if (groupid == 3) {
		titlearray = ["发送丢包率", "接收丢包率", "发送速率", "接收速率"];
		unitarray = ["%", "%", "Bps", "Bps"];
		indicatorsarray = ["netuppacket", "netdownpacket", "netuprate", "netupdown"];
		reporttitle = "交换机运行";
		hosttitlearray = ["设备名称", "IP地址", "发送丢包率", "接收丢包率", "发送速率", "接收速率"];
	}
	var now = new Date();
	var topnperiod = jQuery("#topnPeriod input[type='radio']:checked").val();
	var rtype = "当前情况";
	var timetext = "";
	if (topnperiod == 1) {
		rtype = "日报";
		timetext = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
	} else if (topnperiod == 2) {
		rtype = "周报";
		var nexttime = new Date(new Date().getTime() - 604800000);
		timetext = nexttime.getFullYear() + "-" + (nexttime.getMonth() + 1) + "-" + nexttime.getDate() + "至" +
			now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
	} else if (topnperiod == 3) {
		rtype = "月报";
		var nexttime = new Date(new Date().getTime() - 2592000000);
		timetext = nexttime.getFullYear() + "-" + (nexttime.getMonth() + 1) + "-" + nexttime.getDate() + "至" +
			now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
	}
	jQuery('#titlediv').show();
	jQuery('#titlediv').text(reporttitle+ rtype + "          " + timetext + "   " );
	var flagarray = new Array();
	for (var j = 1; j < 5; j++) {
		if (titlearray[j - 1] == "") {
			jQuery('#area' + j + '_title').hide();
			jQuery('#area' + j + '_title').text(titlearray[j - 1]);
			jQuery('#area' + j).empty();
			jQuery('#area' + j).hide();
		} else {
			var groupid = jQuery("#group").val();
			data.indicators = indicatorsarray[j - 1];
			jQuery.getJSON(url, data, function(dataObj) {
				jQuery.each(dataObj, function(i, item) {
					var Ydata = new Array();
					var Xdata = new Array();
					var maxdata = new Array();
					jQuery.each(item.left0, function(i, item) {
						Ydata.push(item.name);
						Xdata.push(item.value);
						maxdata.push(item.maxvalue);
					});
					showdataTop5(area[item.area], Ydata, Xdata, maxdata, titlearray[item.area], unitarray[item.area]);
					flagarray.push(j);
					if (groupid == 1) {
						if (flagarray.length == 2) {
							hideloading();
						}
					} else if (groupid == 2) {
						if (flagarray.length == 2) {
							hideloading();
						}
					} else if (groupid == 3) {
						if (flagarray.length == 4) {
							hideloading();
						}
					}

				});
			});
			jQuery('#area' + j + '_title').show();
			jQuery('#area' + j + '_title').text(titlearray[j - 1]);
			jQuery('#area' + j).empty();
			jQuery('#area' + j).show();
		}
	}
	jQuery('.hostname').show();
	jQuery('#cdata').empty();
	data.type = 2;
	var titlehtml = '<tr class="header">';
	for (var j = 0; j < hosttitlearray.length; j++) {
		titlehtml += "<td>" + hosttitlearray[j] + "</td>";
	}
	titlehtml += "</tr>";
	jQuery(titlehtml).appendTo('#cdata');
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery("#group").removeAttr("disabled");
		jQuery.each(dataObj, function(i, item) {
			jQuery.each(item.hostdata, function(i, item) {
				var trclass = 'class="even_row"';
				if (i % 2 == 1) {
					trclass = 'class="odd_row"';
				}
				var html = '<tr ' + trclass + ' >';
				jQuery.each(item, function(k, v) {
					html += "<td>" + v + "</td>";
				});
				html += "</tr>";
				jQuery(html).appendTo('#cdata');
			});
		});
	});

}

function addHosts() {
	var AUTOCOMPLETE = "autocomplete",
		businessURL = "iradar/performance_report.action",
		$host = jQuery("#selectHost"),
		$inputAuto = jQuery("#" + $host.attr("id") + "-" + AUTOCOMPLETE);
	$host.children().remove();

	var parameter = getParameterdata();
	if ($inputAuto.length) {
		$inputAuto.val("").autocomplete("clearSource");
	};

	jQuery.getJSON(businessURL, parameter, function(data) {

		if (data instanceof Array && data.length) {

			var newOption = "",
				obj = data[0],
				host = obj.hostdata ? obj.hostid : "-1",
				autoOptionArr = [];
			jQuery.each(obj.hostdata, function(index, item) {
				newOption += "<option value='" + item.hostid + "'>" + item.name + "</option>";
				autoOptionArr.push({
					label: item.name,
					val: item.hostid
				});
			});
			if(newOption!=""){
				$host.append(newOption);
			   if ($host.hasClass(AUTOCOMPLETE)) {
				   $inputAuto.autocomplete("refreshSource", autoOptionArr);
			   }
			}
			getJsonTrenddataByhostid(host);
		}
	});
}

function showdataTop5(id, ydata, xdata, maxdata, title, unit) {
	var vl = xdata[xdata.length - 1];
	var maxvalue = 200;
	var minvalue = -200;
	if (vl > maxvalue) {
		maxvalue = parseInt(vl) * 2 + parseInt(vl)/8;
		minvalue = -(parseInt(vl) * 2 + parseInt(vl)/8);
	}
	if (ydata.length != 0) {
		var elm = $(id);
		echarts.init(elm, 'macarons').setOption({
			title: {
				text: '单位:' + unit,
				x: 100,
				y: 30,
				textAlign: "right",
				textStyle: {
					fontSize: 13,
					fontWeight: 'bolder',
					color: '#333'
				}
			},
			tooltip: {
				show: true,
				trigger: 'item'
			},
			legend: {
				y: 'bottom',
				data: ['平均值', '最大值']
			},
			toolbox: {
				show: true
			},
			calculable: true,
			xAxis: [{
				type: 'category',
				data: ydata,
				axisLabel :{
					rotate: 5
				}
			}],
			yAxis: [{
				type: 'value',
				axisLabel: {
					formatter: '{value} '+ unit
				}
			}],
			series: [{
				name: '平均值',
				type: 'bar',
				data: xdata,
				tooltip: {
					trigger: 'item',
					formatter: function(v) {
						return v[0] + '<br/>' + v[1] + ' : ' + (v[2]) + unit;
					}
				}
			}, {
				name: '最大值',
				type: 'bar',
				data: maxdata,
				tooltip: {
					trigger: 'item',
					formatter: function(v) {
						return v[0] + '<br/>' + v[1] + ' : ' + (v[2]) + unit;
					}
				}
			}]
		});
	} else {
		var myChart = echarts.init(jQuery("#" + id).get(0));
		if (myChart) {
			myChart.clear();
		}
		jQuery('#' + id).text("暂无数据");
		jQuery('#' + id).addClass("fontcl");
	}
}

function showdataTrend(id, src) {
	if (src == "undefined" || src == "" || src == null) {
		jQuery('#' + id).text("无渲染数据");
		jQuery('#' + id).addClass("fontcl");
	} else {
		jQuery('#' + id).empty();
		var html = '<div style="text-align:center;padding-top:20px" ><img alt="" name="" src="' + src + '" border="0"/></div>';
		jQuery(html).appendTo('#' + id);
	}
}


function showloading() {
	hideloading();
	var elm = jQuery(document.body);
	var html = "<div><div id='loaddiv' style='position: absolute;top: 50%;left: 50%;text-align: center;height: 30px;background: rgb(246, 249, 251) none repeat scroll 0% 0%;font-size: 14px;color: rgb(114, 114, 114);line-height: 30px;'>" +
		"<img src='./images/general/loading.gif' width='50' height='50' /></div></div>";
	elm.append(html);
}

function hideloading() {
	jQuery("#loaddiv").remove();
}