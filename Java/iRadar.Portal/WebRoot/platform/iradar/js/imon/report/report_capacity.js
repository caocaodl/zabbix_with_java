var area = new Array("area1", "area2", "area3", "area4", "area5");
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
			var groupid = jQuery("#group").val();
			if (4 == groupid) {
				$(".select_ctn.host")["hide"]();
			} else {
				$(".select_ctn.host")["show"]();
			}
			addHosts();
		}
	});

	$("#selectHost").change(function() {
		getJsonTrenddata();
	});

	$("#type").delegate("INPUT", "change", function() {
		var v = this.value * 1;
		$(".select_ctn.trend_period")[v ? "show" : "hide"]();
		$(".select_ctn.topn_period")[!v ? "show" : "hide"]();
		if (1 == v) {
			$("#group option[value=4]").show();
			var groupid = jQuery("#group").val();
			if (4 == groupid) {
				$(".select_ctn.host")["hide"]();
			} else {
				$(".select_ctn.host")["show"]();
			}
			var trendperiod = jQuery("#trendPeriod input[type='radio']:checked").val();
			if (trendperiod == undefined) {
				jQuery("#trendPeriod input[type='radio']:first").attr('checked', 'checked');
				jQuery("#trendPeriod_label_0").addClass('ui-state-active');
			}
			addHosts();
			//getJsonTrenddataByhostid(host);
		} else {
			$("#group option[value=4]").hide();
			if ($("#group").val() == '4') {
				$("#group").empty();
				$("#group").append("<option value=1>服务器</option>"); //prepend
				$("#group").append("<option value=2>云主机</option>");
				$("#group").append("<option value=3>交换机</option>");
				$("#group").append("<option value=4>云平台</option>");
				$("#group option[value=4]").hide();
			}
			$(".select_ctn.host")["hide"]();
			var data = getParameterdata();
			getJsondata(data);
		}
	});
	init();
});

function init() {
	jQuery("#group option:last").hide();
	var data = getParameterdata();
	getJsondata(data);

}

function getParameterdataByhostid(hostid,hostname) {
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
		"hostid": hostid,
		"hostname":hostname
	};
	return data;
}

function getParameterdata() {
	var groupid = jQuery("#group").val();
	var type = jQuery("#type input[type='radio']:checked").val(); //报表类型
	var topnperiod = jQuery("#topnPeriod input[type='radio']:checked").val(); //topN周期
	var trendperiod = jQuery("#trendPeriod input[type='radio']:checked").val(); //trend周期
	var hostid = jQuery('#selectHost option:selected').val();
	var hostname = jQuery('#selectHost option:selected').text(); 
	var data = {
		"reporttype": "performance",
		"type": type,
		"topnperiod": topnperiod,
		"trendperiod": trendperiod,
		"groupid": groupid,
		"hostid": hostid,
		"hostname":hostname
	};
	return data;
}



function getJsonTrenddata() {
	var url = "iradar/capacity_report.action";
	var data = getParameterdata();
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			Trenddatashow(item);
		});
	});

}

function getJsonTrenddataByhostid(hostid,hostname) {
	var url = "iradar/capacity_report.action";
	var data = getParameterdataByhostid(hostid,hostname);
	var num = 2;
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			Trenddatashow(item);
		});
	});

}

function Trenddatashow(item) {
	var titlearray = new Array();
	jQuery(".w").addClass("trend");
	var groupid = jQuery("#group").val();
	if (groupid == 1) {
		titlearray = ["CPU使用率", "内存使用率", "网络接口上行IO", "网络接口下行IO", "硬盘使用率"];
	} else if (groupid == 2) {
		titlearray = ["CPU使用率", "内存使用率", "硬盘使用率", "", ""];
	} else if (groupid == 3) {
		titlearray = ["发送利用率", "接收利用率", "", "", ""];
	} else if (groupid == 4) {
		titlearray = ["内核数量", "内存大小", "存储空间", "", ""];
	}
	// for(var i=1;i<6;i++){
	//	jQuery('#area'+i+'_title').empty();
	//	jQuery('#area'+i+'_title').text(titlearray[i-1]);
	//}
	for (var i = 1; i < 6; i++) {
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
		}
	}
	num = item.datanum;
	showdataTrend(area[0], item.area1src);
	showdataTrend(area[1], item.area2src);
	if (num > 2) {
		//jQuery('#'+area[2]).show();
		// jQuery('#'+area[2]+"_title").show();
		showdataTrend(area[2], item.area3src);
	} else {
		//jQuery('#'+area[2]).hide();
		//jQuery('#'+area[2]+"_title").hide();
	}
	if (num > 3) {
		//jQuery('#'+area[3]).show();
		// jQuery('#'+area[3]+"_title").show();
		showdataTrend(area[3], item.area4src);
	} else {
		//jQuery('#'+area[3]).hide();
		//jQuery('#'+area[3]+"_title").hide();
	}
	if (num > 4) {
		jQuery('#ttir').show();
		//jQuery('#'+area[4]).show();
		// jQuery('#'+area[4]+"_title").show();
		showdataTrend(area[4], item.area5src);
	} else {
		jQuery('#ttir').hide();
		//jQuery('#'+area[3]).hide();
		//jQuery('#'+area[3]+"_title").hide();
	}



}

function getJsondata(data) {
	var url = "iradar/capacity_report.action";
	var titlearray = new Array();
	var unitarray = new Array();
	var indicatorsarray = new Array();
	jQuery(".w").removeClass("trend");
	var groupid = jQuery("#group").val();
	if (groupid == 1) {
		titlearray = ["CPU使用率", "内存使用率", "硬盘使用率", "网络接口上行IO", "网络接口下行IO"];
		unitarray = ["%", "%", "%", "Bps", "Bps"];
		indicatorsarray =["cpuused", "memory", "disk", "upipos", "downipos"];
	} else if (groupid == 2) {
		titlearray = ["CPU使用率", "内存使用率", "硬盘使用率", "", ""];
		unitarray = ["%", "%", "%", "", ""];
		indicatorsarray =["cpuused", "memory", "disk", "", ""];
	} else if (groupid == 3) {
		titlearray = ["发送利用率", "接收利用率", "", "", ""];
		unitarray = ["%", "%", "", "", ""];
		indicatorsarray =["up", "down", "", "", ""];
	} else if (groupid == 4) {
		titlearray = ["内核数量", "内存大小", "", "", ""];
		unitarray = ["", "", "", "", ""];
		indicatorsarray =["core", "nocore", "", "", ""];
	}

    var groupid = jQuery("#group").val();
		for (var j = 1; j < 6; j++) {
			if (titlearray[j - 1] == "") {
				jQuery('#area' + j + '_title').hide();
				jQuery('#area' + j + '_title').text(titlearray[j - 1]);
				jQuery('#area' + j).empty();
				jQuery('#area' + j).hide();
			} else {
				data.indicators=indicatorsarray[j-1];
				jQuery.ajaxSettings.async = false;
				jQuery.getJSON(url, data, function(dataObj) {
					jQuery.each(dataObj, function(i, item) {
						var Ydata = new Array();
						var Xdata = new Array();
						var fYdata = new Array();
						var fXdata = new Array();
						jQuery.each(item.left0, function(i, item) {
							Ydata.push(item.name);
							Xdata.push(item.value);
						});
						jQuery.each(item.fleft0, function(i, item) {
							fYdata.push(item.name);
							fXdata.push(-item.value);
						});
						showdataTop5(area[item.area], Ydata.reverse(), Xdata.reverse(), fYdata.reverse(), fXdata.reverse(), titlearray[item.area], unitarray[item.area]);
					});
				});
				jQuery('#area' + j + '_title').show();
				jQuery('#area' + j + '_title').text(titlearray[j - 1]);
				jQuery('#area' + j).empty();
				jQuery('#area' + j).show();
			}
		}
}

function addHosts() {
	var host = "";
	var hostname = "";
	jQuery("#selectHost").find("option").remove();
	var url = "iradar/capacity_report.action";
	var data = getParameterdata();
	var j = true;
	jQuery.getJSON(url, data, function(dataObj) {
		jQuery.each(dataObj, function(i, item) {
			if (item.hostdata == '') {
				host = "-1";
				hostname = "-1";
				if (jQuery("#group").val() == '4') {
					host = "1";
					hostname = "1";
				}
			} else {
				jQuery.each(item.hostdata, function(i, item) {
					jQuery("#selectHost").append("<option value='" + item.hostid + "'>" + item.name + "</option>");
				});
				host = item.hostid;
				hostname = item.hostname;
				alert("host="+host+"=hostname="+hostname);
			}
			getJsonTrenddataByhostid(host,hostname);
		});
	});
}

function showdataTop5(id, ydata, xdata, fydata, fxdata, title, unit) {
	var vl = xdata[xdata.length - 1];
	var maxvalue = 200;
	var minvalue = -200;
	if (vl > maxvalue) {
		maxvalue = parseInt(vl) * 2;
		minvalue = -(parseInt(vl) * 2);
	}
	if (ydata.length != 0) {
		var elm = $(id);
		echarts.init(elm, 'macarons').setOption({
			title: {
				text: '单位:' + unit,
				x: 'right',
				y: 30,
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