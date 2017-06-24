function angulargaugeForHostDetail(divId,data){
	var Ydata1 = new Array(); 
	var Xdata1 = new Array(); 
	var dataObj=eval("("+data+")");
	jQuery.each(dataObj, function(key, val) {
		Xdata1.push(val.time);
		Ydata1.push(val.num);
	});
	var elm = $(divId);
	echarts.init(elm,'macarons').setOption({
		tooltip : {
			show: true,
			trigger: 'item'
		},
		calculable : true,
		xAxis : [{
			type : 'category',
			data : Xdata1
		}],
		yAxis : [{
			type : 'vlue'
		}],
		series : [{
			name: "每日统计",
			type:'bar',
		    data: Ydata1
		}]
	});
	
}

