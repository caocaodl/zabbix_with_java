function inite() {
	jQuery.getJSON("intvoisport.action?refresh&output=ajax", function(data) {
		var datas = [];
		var names = [];
		jQuery.each(data, function(k, item) {
			jQuery.each(item, function(k1, v1) {
				jQuery.each(v1, function(k2, v2) {
					if (k2 == "names") {
						jQuery.each(v2, function(k3, v3) {
							names.push(v3);
						});
					} else {
						jQuery.each(v2, function(k4, v4) {
							datas.push({
								name: v4.name,
								y: v4.values
							});
						});
					}
				});
			});
		});
		var _title = "按物理类型统计";
		var id = "todayMonItemChart";
		var elm = $(id);
		var chart = echarts.init(elm);

		jQuery("#todayMonItemChart").highcharts({
			chart: {

			},
			title: {
				text: _title
			},
			tooltip: {
				pointFormat: '<b>{point.y}({point.percentage:.1f}%)</b>'
			},
			plotOptions: {
				pie: {
					size:'75%',
					allowPointSelect: true,
					cursor: 'pointer',
					dataLabels: {
						useHTML: true,
						formatter: function() {
							var title = this.key.length > 8 ? this.key.substring(0, 8) + ".." : this.key;
							return '<span title=' + this.key + '>' + title + '</span>';
						}
					},
					showInLegend: true
				}
			},
			legend: {
				align: 'left', //水平方向位置
				verticalAlign: 'top', //垂直方向位置
				x: 0, //距离x轴的距离
				y: 10 //距离Y轴的距离

			},
			series: [{
				type: 'pie',
				data: datas
			}]
		});
	});

}

function initeInventoryHostType() {
	jQuery.getJSON("intvoisport.action?refresh&output=ajax&inventHostTypeRefresh=1", function(data) {
		var datas = [];
		var names = [];
		jQuery.each(data, function(k, item) {
			jQuery.each(item, function(k1, v1) {
				jQuery.each(v1, function(k2, v2) {
					if (k2 == "names") {
						jQuery.each(v2, function(k3, v3) {
							names.push(v3);
						});
					} else {
						jQuery.each(v2, function(k4, v4) {
							datas.push({
								name: v4.name,
								y: v4.values
							});
						});
					}
				});
			});
		});
		var _title = "按厂商统计";
		jQuery("#inventoryHostTypeChart").highcharts({
			chart: {

			},
			title: {
				text: _title
			},
			tooltip: {
				pointFormat: '<b>{point.y}({point.percentage:.1f}%)</b>'
			},
			plotOptions: {
				pie: {
					size:'75%',
					allowPointSelect: true,
					cursor: 'pointer',
					dataLabels: {
						useHTML: true,
						formatter: function() {
							var title = this.key.length > 8 ? this.key.substring(0, 8) + ".." : this.key;
							return '<span title=' + this.key + '>' + title + '</span>';
						}
					}
				}
			},
			series:[{
				type: 'pie',
				data:datas
			}]
		});
	});

}

function loadnew(groupid) {
	jQuery(".message").text("");
	jQuery("#newdata").load('zichanInfor.action?groupid=' + groupid);
}

function selectNext() {
	var type = jQuery("#type").val();
	var value = jQuery("#value").val();
	var groupid = jQuery("#groupid").val();
	jQuery("#newdata").load('zichanInfor.action?groupid=' + groupid + '&type=' + type + '&value=' + value);
}