var area = new Array("area1", "area2", "area3", "area4");

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

	$("#selectHost").change(function() {
		getJsonTrenddata();
	});

	$("#csv_export").click(function() {
		var period = jQuery("#topnPeriod input[type='radio']:checked").val(); //topN周期
		var groupid = jQuery("#group").val(); //设备类型
		var url = "business_report.action?csv_export=true&topnperiod=" + period + "&groupid=" + groupid;

		location.href = url;
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



function getJsonTrenddata() {
	var url = "iradar/business_report.action";
	var data = getParameterdata();
	showloading();
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			Trenddatashow(item);
			hideloading();
		});
	});

}

function getJsonTrenddataByhostid(hostid) {
	var url = "iradar/business_report.action";
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
	if (groupid == 1) {
		titlearray = ["连接数", "告警次数", ""];
	} else if (groupid == 2) {
		titlearray = ["连接数", "每秒错误次数", "告警次数"];
	} else if (groupid == 3) {
		titlearray = ["响应时间", "http状态", ""];
	}

	for (var i = 1; i < 4; i++) {
		if (titlearray[i - 1] == "") {
			jQuery('#area' + i + '_title').hide();
			jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			jQuery('#area' + i).empty();
			jQuery('#area' + i).hide();
		} else {
			jQuery('#area' + i + '_title').show();
			if (groupid == 2) {
				jQuery('#area' + i + '_title').text(titlearray[i - 1]).attr("title", " IIS-IIS当前连接数\n Tomcat-活动会话数\n Websphere-JVM历史最大线程数 \n Weblogic-当前JMS服务数");
			} else {
				jQuery('#area' + i + '_title').text(titlearray[i - 1]).removeAttr("title");
				jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			}
			jQuery('#area' + i).empty();
			jQuery('#area' + i).show();
		}
	}

	num = item.datanum;
	showdataTrend(area[0], item.area1src);
	if (groupid == 1) {
		var Ydata1 = new Array();
		var Xdata1 = new Array();
		if (typeof(item.left0) == "undefined") {} else {
			jQuery.each(item.left0, function(i, item) {
				Xdata1.push(item.moment);
				Ydata1.push(item.eventsnum);
			});
		}
		showCustomTrend(area[1], Xdata1, Ydata1, "告警次数");
	} else if (groupid == 2) {
		showdataTrend(area[1], item.area2src);
		var Ydata1 = new Array();
		var Xdata1 = new Array();
		if (typeof(item.left0) == "undefined") {} else {
			jQuery.each(item.left0, function(i, item) {
				Xdata1.push(item.moment);
				Ydata1.push(item.eventsnum);
			});
		}
		showCustomTrend(area[2], Xdata1, Ydata1, "告警次数");
	} else {
		showdataTrend(area[1], item.area2src);
		if (num > 2) {
			jQuery('#' + area[2]).show();
			jQuery('#' + area[2] + "_title").show();
			showdataTrend(area[2], item.area3src);
		} else {
			jQuery('#' + area[2]).hide();
			jQuery('#' + area[2] + "_title").hide();
		}
	}
}

function getJsondata(data) {
	showloading();
	var url = "iradar/business_report.action";
	var Ydata1 = new Array();
	var Xdata1 = new Array();
	var fYdata1 = new Array();
	var fXdata1 = new Array();

	var Ydata2 = new Array();
	var Xdata2 = new Array();
	var fYdata2 = new Array();
	var fXdata2 = new Array();

	var Ydata3 = new Array();
	var Xdata3 = new Array();
	var fYdata3 = new Array();
	var fXdata3 = new Array();

	var titlearray = new Array();
	var unitarray = new Array();
	jQuery(".w").removeClass("trend");
	var groupid = jQuery("#group").val();
	var num = 2;

	if (groupid == 1) {
		titlearray = ["连接数", "告警次数", ""];
		unitarray = ["个", "次", ""];
	} else if (groupid == 2) {
		titlearray = ["连接数", "告警次数", ""];
		unitarray = ["个", "次"];
	} else if (groupid == 3) {
		titlearray = ["响应时间", "告警次数", ""];
		unitarray = ["", "次", ""];
	}

	for (var i = 1; i < 4; i++) {
		if (titlearray[i - 1] == "") {
			jQuery('#area' + i + '_title').hide();
			jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			jQuery('#area' + i).empty();
			jQuery('#area' + i).hide();
		} else {
			jQuery('#area' + i + '_title').show();
			if (groupid == 2) {
				jQuery('#area' + i + '_title').text(titlearray[i - 1]).attr("title", " IIS-当前连接数\n Tomcat-活动会话数\n Websphere-活动线程数 \n Weblogic-当前JMS服务数");
			} else {
				jQuery('#area' + i + '_title').text(titlearray[i - 1]).removeAttr("title");
				jQuery('#area' + i + '_title').text(titlearray[i - 1]);
			}
			jQuery('#area' + i).empty();
			jQuery('#area' + i).show();
		}
	}
	jQuery.ajaxSettings.async = false;
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			num = item.datanum;
			jQuery.each(item.left0, function(i, item) {
				Ydata1.push(item.name);
				Xdata1.push(item.value);
			});
			jQuery.each(item.fleft0, function(i, item) {
				fYdata1.push(item.name);
				fXdata1.push(-item.value);
			});
			if (groupid == 1) {
				jQuery.each(item.left1, function(i, item) {
					Ydata2.push(item.priority);
					Xdata2.push(item.eventsnum);
				});
			} else if (groupid == 2) {
				jQuery.each(item.left1, function(i, item) {
					Ydata2.push(item.name);
					Xdata2.push(item.value);
				});
				/*jQuery.each(item.fleft1, function(i, item) {
					fYdata2.push(item.name);
					fXdata2.push(-item.value);
				});*/

				jQuery.each(item.left1, function(i, item) {
					Ydata3.push(item.priority);
					Xdata3.push(item.eventsnum);
				});

			} else if (groupid == 3) {
				jQuery.each(item.left1, function(i, item) {
					Ydata2.push(item.priority);
					Xdata2.push(item.eventsnum);
				});
				/*	jQuery.each(item.left2, function(i, item) {
						Ydata3.push(item.priority);
						Xdata3.push(item.eventsnum);
					});*/
			}

		});

		showdataTop5(area[0], Ydata1.reverse(), Xdata1.reverse(), fYdata1.reverse(), fXdata1.reverse(), titlearray[0], unitarray[0]);
		if (groupid == 1) {
			showPieImage(area[1], Ydata2, Xdata2);
		} else if (groupid == 2) {
			//showdataTop5(area[1],Ydata2.reverse(),Xdata2.reverse(),fYdata2.reverse(),fXdata2.reverse(),titlearray[1],unitarray[1]);
			showPieImage(area[1], Ydata3, Xdata3);
		} else if (groupid == 3) {
			showPieImage(area[1], Ydata2, Xdata2);
			// showPieImage(area[2],Ydata3,Xdata3);
		}

	});
	hideloading();
}

function addHosts() {
	var AUTOCOMPLETE = "autocomplete",
		businessURL = "iradar/business_report.action",
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

function showdataTop5(id, ydata, xdata, fydata, fxdata, title, unit) {
	var vl = xdata[xdata.length - 1];
	var maxvalue = 200;
	var minvalue = -200;
	if (vl > (maxvalue/2)) {
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
			calculable: false,
			xAxis: [{
				type: 'value',
				splitArea: {
					show: true
				},
				position: 'bottom',
				splitLine: {
					show: false
				},
				axisLine: {
					show: false
				},
				axisLabel: {
					show: false
				},
				min: 0,
				max: maxvalue
			}, {
				type: 'value',
				splitArea: {
					show: true
				},
				position: 'top',
				splitLine: {
					show: false
				},
				axisLine: {
					show: false
				},
				axisLabel: {
					show: false
				},
				min: minvalue,
				max: 0
			}],
			yAxis: [{
				type: 'category',
				position: 'left',
				axisLabel: {
					formatter: function(val) {
						val = val.replace(/(.{8})/g, '$1\n');
						return val;
					},
					textStyle: {
						fontSize: 9,
						color: '#333'
					}
				},
				data: ydata
			}, {
				type: 'category',
				//position : 'right',
				axisLabel: {
					formatter: function(val) {
						val = val.replace(/(.{8})/g, '$1\n');
						return val;
					},
					textStyle: {
						fontSize: 9,
						color: '#333'
					}
				},
				data: fydata
			}],
			series: [{
				name: title,
				type: 'bar',
				tooltip: {
					trigger: 'item',
					formatter: function(v) {
						return v[0] + '<br/>' + v[1] + ' : ' + (v[2]) + unit;
					}
				},
				itemStyle: {
					normal: {
						color: (function() {
							var zrColor = zrender.tool.color; //require('zrender/tool/color');
							return zrColor.getLinearGradient(
								0, 80, 0, 700,
								//['orangered','yellow','lightskyblue']
								[
									[0, 'orange'],
									[0.5, 'orangered'],
									[1, 'purple']
								]
							)
						})(),
						label: {
							show: true,
							position: 'insideLeft',
							textStyle: {
								color: '#333'
							},
							formatter: function(params) {
								return (params.value);
							}
						}
					}
				},
				data: xdata
			}, {
				name: title,
				type: 'bar',
				xAxisIndex: 1,
				yAxisIndex: 1,
				tooltip: {
					trigger: 'item',
					formatter: function(v) {
						return v[0] + '<br/>' + v[1] + ' : ' + (-v[2]) + unit;
					}
				},
				itemStyle: {
					normal: {
						color: (function() {
							var zrColor = zrender.tool.color; //require('zrender/tool/color');
							return zrColor.getLinearGradient(
								0, 80, 0, 700, [
									[0, 'lightskyblue'],
									[0.5, 'lightblue'],
									[1, 'lightgreen']
								]
							)
						})(),
						label: {
							show: true,
							position: 'insideRight',
							textStyle: {
								color: '#333'
							},
							formatter: function(params) {
								return (-params.value);
							}
						}
					}
				},
				data: fxdata
			}]
		});
	} else {
		jQuery('#' + id).text("暂无数据");
		jQuery('#' + id).addClass("fontcl");
	}
}

function showPieImage(id, names, values) {
	if (names.length != 0) {
		var datas = [];
		jQuery.each(names, function(i, name) {
			datas.push({
				value: values[i],
				name: name
			});
		});
		var elm = $(id);
		echarts.init(elm).setOption({
			tooltip: {
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
			legend: {
				orient: 'vertical',
				x: 'right',
				data: names
			},
			calculable: false,
			series: [{
				type: 'pie',
				radius: '55%',
				center: ['50%', '60%'],
				data: datas
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


function showCustomTrend(id, ydata, xdata, title) {
	if (ydata.length != 0) {
		var elm = $(id);
		echarts.init(elm, 'macarons').setOption({
			tooltip: {
				show: true,
				trigger: 'item'
			},
			calculable: false,
			xAxis: [{
				type: 'category',
				data: ydata
			}],
			yAxis: [{
				type: 'vlue'
			}],
			series: [{
				name: title,
				type: 'bar',
				data: xdata
			}]
		});
	} else {
		jQuery('#' + id).text("无渲染数据");
		jQuery('#' + id).addClass("fontcl");
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