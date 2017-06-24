jQuery(function($){
	$("#Period input[type='radio']").click(function(){
		var data= getParameterdata();
		getJsondata(data);
	});
	var data = getParameterdata();
	getJsondata(data);
	
	$("#csv_export").click(function(){
		var period = jQuery("#Period input[type='radio']:checked").val();//trend周期
		var data = ["Day", "Week", "Month", "Quarter"][period];
		var url="events_report.action?csv_export=true&csv_period="+data;
		location.href=url;
	});
	
	
});

function getParameterdata(){
	var period = jQuery("#Period input[type='radio']:checked").val();//trend周期
	var data = {
		"period" : ["Day", "Week", "Month", "Quarter"][period]
	};
	return data;
}

function getJsondata(data){
	var url = "iradar/events_report.action?output=ajax";
	var addNumData = [],restoreNumData = []; 			//告警柱状图 新增和恢复个数
	var hostTypeNameData = [],triggerDataForType = [];  //按类型统计
	var levelNameData = [],triggerDataForLevel = [];    //按等级统计
	
	jQuery.getJSON(url, data, function(json){
		showTriggersNum("leftbottom",json.data.triggerbarData.addNum, json.data.triggerbarData.solveNum);
		
		jQuery.each(json.data.pieForTypeDate,function(i2,item2){
			hostTypeNameData.push(i2);
			triggerDataForType.push(item2);
		});
		showPieImage("rightTop",hostTypeNameData,triggerDataForType,"按监控类型统计");
		
		jQuery.each(json.data.pieForLevelDate,function(i3,item3){
			levelNameData.push(i3);
			triggerDataForLevel.push(item3);
		});
		showPieImage("rightbottom",levelNameData,triggerDataForLevel,"按告警严重级别统计");
	});
}

/**
 * 柱状图
 * @param id
 * @param ydata
 * @param xdata
 * @param title
 * @param unit
 */
function showTriggersNum(id, addNumData, restoreNumData){
	var names = [];
	var valuesNew =[], valuesRestore=[];
	jQuery.each(addNumData, function(k,v){
		names.push(k);
		valuesNew.push(v);
	})
	jQuery.each(restoreNumData, function(k,v){
		valuesRestore.push(v);
	})
	var elm = $(id);
	echarts.init(elm).setOption({
	    tooltip : {
	        show: true,
	        trigger: 'item'
	    },
	    legend: {
	        data:['新增告警', '恢复告警']
	    },
	    
	    xAxis : [{
            type : 'category',
            data : names,
            axisLabel :{
				rotate: 40
			}
	    }],
	    yAxis : [{
            type : 'value',
            splitArea : {show : true}
	    }],
	    series : [{
            name:'新增告警',
            type:'bar',
            itemStyle: {normal: {
                color: '#1bb2d8'
            }},
            data:valuesNew
        },{
            name:'恢复告警',
            type:'bar',
            itemStyle: {normal: {
                color: 'rgba(255, 127, 80, 1)'
            }},
            data:valuesRestore
	    }]
	});
}

/**
 * 展示饼状图
 * @param data
 */
function showPieImage(id,names,values,_title){
	var datas = [];
	jQuery.each(names, function(i, name){
		datas.push({
			value: values[i],
			name: name
		});
	});
	
	var elm = $(id);
	echarts.init(elm).setOption({
	    title : {
	        text: _title,
	        x:'center'
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    legend: {
	        orient : 'vertical',
	        x : 'left',
	        y:30,
	        data:names
	    },
	    series : [
	        {
	            type:'pie',
	            radius : '55%',
	            center: ['50%', '60%'],
	            data: datas
	        }
	    ]
	}).hideLoading({});
}