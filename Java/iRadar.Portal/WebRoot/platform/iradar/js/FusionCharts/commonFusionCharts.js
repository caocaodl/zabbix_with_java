

/**
 * 首页概览使用fusionCharts控件标准实现方法
 * @param value 仪表盘数值
 * @param type	应用类型
 */
function useFusionCharts(value,type){
	if(type == 0){		
		jQuery("#totalSituation").insertFusionCharts({
			type:"angulargauge",
			width:"219",
			height:"134",
			dataFormat:"xml",
			dataSource:"<chart caption=\"\" lowerlimit=\"0\" upperlimit=\"100\" lowerlimitdisplay=\"很差\" upperlimitdisplay=\"正常\" palette=\"1\" numbersuffix=\"%\" tickvaluedistance=\"10\" showvalue=\"0\" gaugeinnerradius=\"0\" bgcolor=\"FFFFFF\" pivotfillcolor=\"333333\" pivotradius=\"8\" pivotfillmix=\"333333, 333333\" pivotfilltype=\"radial\" pivotfillratio=\"0,100\" showtickvalues=\"1\" showborder=\"0\"><colorrange><color minvalue=\"0\" maxvalue=\"25\" code=\"6baa01\" /><color minvalue=\"25\" maxvalue=\"55\" code=\"f8bd19\" /><color minvalue=\"55\" maxvalue=\"100\" code=\"e44a00\" /></colorrange><dials><dial value=\""+value+"\" rearextension=\"15\" radius=\"70\" bgcolor=\"228B22\" bordercolor=\"333333\" basewidth=\"8\" /></dials></chart>",
			displayLabel:["良好","正常","一般","较差","很差"]
		});
	}else if(type == 1){
		jQuery("#cpuUsedRateChart").insertFusionCharts({
			type:"angulargauge",
			width:"188",
			height:"120",
			dataFormat:"xml",
			dataSource:"<chart manageresize=\"1\" bgcolor=\"FFFFFF\" ledsize=\"1\" showborder=\"0\" ledborderthickness=\"4\" ledgap=\"0\" upperlimit=\"100\" lowerlimit=\"0\"  majortmnumber=\"11\" majortmcolor=\"666666\" majortmheight=\"9\" minortmnumber=\"5\" minortmcolor=\"666666\" minortmheight=\"3\" >" +
					"<colorrange><color minvalue=\"0\" maxvalue=\"80\" code=\"99cc00\"  /><color minvalue=\"80\" maxvalue=\"100\" code=\"cf0000\"  /></colorrange>" +
					"<dials><dial value=\""+value+"\"  /></dials></chart>"
		});
       
	}else if(type == 2){
		jQuery("#memoryUsedRateChart").insertFusionCharts({
			type:"angulargauge",
			width:"188",
			height:"120",
			dataFormat:"xml",
			dataSource:"<chart manageresize=\"1\" bgcolor=\"FFFFFF\" ledsize=\"1\" showborder=\"0\" ledborderthickness=\"4\" ledgap=\"0\" upperlimit=\"100\" lowerlimit=\"0\"  majortmnumber=\"11\" majortmcolor=\"666666\" majortmheight=\"9\" minortmnumber=\"5\" minortmcolor=\"666666\" minortmheight=\"3\" >" +
					"<colorrange><color minvalue=\"0\" maxvalue=\"80\" code=\"99cc00\"  /><color minvalue=\"80\" maxvalue=\"100\" code=\"cf0000\"  /></colorrange>" +
					"<dials><dial value=\""+value+"\"  /></dials></chart>"
		});
	}else if(type == 3){
		jQuery("#diskUsedRateChart").insertFusionCharts({
			type:"angulargauge",
			width:"188",
			height:"120",
			dataFormat:"xml",
			dataSource:"<chart manageresize=\"1\" bgcolor=\"FFFFFF\" ledsize=\"1\" showborder=\"0\" ledborderthickness=\"4\" ledgap=\"0\" upperlimit=\"100\" lowerlimit=\"0\"  majortmnumber=\"11\" majortmcolor=\"666666\" majortmheight=\"9\" minortmnumber=\"5\" minortmcolor=\"666666\" minortmheight=\"3\" >" +
					"<colorrange><color minvalue=\"0\" maxvalue=\"80\" code=\"99cc00\"  /><color minvalue=\"80\" maxvalue=\"100\" code=\"cf0000\"  /></colorrange>" +
					"<dials><dial value=\""+value+"\"  /></dials></chart>"
		});
	}else if(type == 4){
		jQuery("#netUsedRateChart").insertFusionCharts({
			type:"angulargauge",
			width:"189",
			height:"120",
			dataFormat:"xml",
			dataSource:"<chart manageresize=\"1\" bgcolor=\"FFFFFF\" ledsize=\"1\" showborder=\"0\" ledborderthickness=\"4\" ledgap=\"0\" upperlimit=\"100\" lowerlimit=\"0\"  majortmnumber=\"11\" majortmcolor=\"666666\" majortmheight=\"9\" minortmnumber=\"5\" minortmcolor=\"666666\" minortmheight=\"3\" >" +
					"<colorrange><color minvalue=\"0\" maxvalue=\"80\" code=\"99cc00\"  /><color minvalue=\"80\" maxvalue=\"100\" code=\"cf0000\"  /></colorrange>" +
					"<dials><dial value=\""+value+"\"  /></dials></chart>"
		});
	}
}

/**
 * 设备详情页面设备健康度通用插件(仪表盘)-运营商
 * @param divId	
 * @param value
 */
function angulargaugeForHostDetail(divId,value){
	jQuery("#"+divId).insertFusionCharts({
		type:"angulargauge",
		width:"400",
		height:"170",
		dataFormat:"xml",
		dataSource:"<chart caption=\"\" lowerlimit=\"0\" upperlimit=\"100\" lowerlimitdisplay=\"很差\" upperlimitdisplay=\"正常\" palette=\"1\" numbersuffix=\"%\" tickvaluedistance=\"10\" showvalue=\"0\" gaugeinnerradius=\"0\" bgcolor=\"FFFFFF\" pivotfillcolor=\"333333\" pivotradius=\"8\" pivotfillmix=\"333333, 333333\" pivotfilltype=\"radial\" pivotfillratio=\"0,100\" showtickvalues=\"1\" showborder=\"0\"><colorrange><color minvalue=\"0\" maxvalue=\"25\" code=\"e44a00\" /><color minvalue=\"25\" maxvalue=\"50\" code=\"f8bd19\" /><color minvalue=\"50\" maxvalue=\"75\" code=\"BBFFEE\" /><color minvalue=\"75\" maxvalue=\"100\" code=\"6baa01\" /></colorrange><dials><dial value=\""+value+"\" rearextension=\"15\" radius=\"70\" bgcolor=\"228B22\" bordercolor=\"333333\" basewidth=\"8\" /></dials></chart>",
		displayLabel:["很差","较差","一般","正常","良好"]
	});
}

/**
 * 设备详情页面设备健康度通用插件(横向柱状图)-运营商
 * @param divId
 * @param label
 * @param title
 */
function bar2dForHostDetail(divId,label,title){
	jQuery("#"+divId).insertFusionCharts({
		type:"bar2d",
		width:"400",
		height:"170",
		dataFormat:"xml",
		dataSource:"<chart caption=\""+title+"\" subcaption=\"\" yaxisname=\"\" defaultAnimation=\"1\" numbersuffix=\"\" showvalues=\"1\" plotgradientcolor=\"\"  plotborderalpha=\"0\" alternatevgridalpha=\"0\" divlinealpha=\"0\" canvasborderalpha=\"0\" bgcolor=\"#FFFFFF\" numberscalevalue=\"1024,1024,1024\" numberscaleunit=\"GB,MB,KB\" basefontsize=\"12\" basefontcolor=\"#194920\" palettecolors=\"#f8bd19\" showyaxisvalues=\"0\" showborder=\"0\">\""+label+"\</chart>"
	});
}

/**
 * cpu负载面板(topN)
 */
function cpuLoadPanel(name,value){
	var label = "";
	for(var i = 0; i < name.length;i++){
		label += "<set label=\""+name[i]+"\" value=\""+value[i]+"\" />";
	}
	//defaultAnimation属性 禁止使用动画效果
	jQuery("#cpuLoadPanel").insertFusionCharts({
		type:"bar2d",
		width:"790",
		height:"169",
		dataFormat:"xml",
		dataSource:"<chart caption=\"\" subcaption=\"\" yaxisname=\"\" defaultAnimation=\"1\" numbersuffix=\"\" showvalues=\"1\" plotgradientcolor=\"\"  plotborderalpha=\"0\" alternatevgridalpha=\"0\" divlinealpha=\"0\" canvasborderalpha=\"0\" bgcolor=\"#FFFFFF\" numberscalevalue=\"1024,1024,1024\" numberscaleunit=\"GB,MB,KB\" basefontsize=\"12\" basefontcolor=\"#194920\" palettecolors=\"#f8bd19\" showyaxisvalues=\"0\" showborder=\"0\">\""+label+"\</chart>"
	});

}
function userxq(value){
	jQuery("#trend_chart").insertFusionCharts({
		type:"angulargauge",
		width:"400",
		height:"170",
		dataFormat:"xml",
		dataSource:"<chart caption=\"\" lowerlimit=\"0\" upperlimit=\"100\" lowerlimitdisplay=\"很差\" upperlimitdisplay=\"正常\" palette=\"1\" numbersuffix=\"%\" tickvaluedistance=\"10\" showvalue=\"0\" gaugeinnerradius=\"0\" bgcolor=\"FFFFFF\" pivotfillcolor=\"333333\" pivotradius=\"8\" pivotfillmix=\"333333, 333333\" pivotfilltype=\"radial\" pivotfillratio=\"0,100\" showtickvalues=\"1\" showborder=\"0\"><colorrange><color minvalue=\"0\" maxvalue=\"25\" code=\"e44a00\" /><color minvalue=\"25\" maxvalue=\"50\" code=\"f8bd19\" /><color minvalue=\"50\" maxvalue=\"75\" code=\"BBFFEE\" /><color minvalue=\"75\" maxvalue=\"100\" code=\"6baa01\" /></colorrange><dials><dial value=\""+value+"\" rearextension=\"15\" radius=\"70\" bgcolor=\"228B22\" bordercolor=\"333333\" basewidth=\"8\" /></dials></chart>",
		displayLabel:["很差","较差","一般","正常","良好"]
	});
	
}


/**
 * 设备详情健康度
 */
function showHealth(divId,json){
	var elm = $(divId);
	var _name =[],_value=[];
	jQuery.each(json[0],function(k,v){
		if(k == "key"){			
			jQuery.each(v,function(vk1,vv1){
				_name.push(vv1);
			});
		}else{			
			jQuery.each(v,function(vk2,vv2){
				_value.push(vv2);
			});
		}
	});

	echarts.init(elm).setOption({
	    tooltip : {
	        trigger: 'axis'
	    },
	    polar : [{
           indicator : [
               { text: _name[0], max: 100},
               { text: _name[1], max: 100},
               { text: _name[2], max: 100},
               { text: _name[3], max: 100}
            ]
	    }],
	    series : [{
            type: 'radar',
            data : [{
                value : _value,
                name : '健康度'
            }]
	    }]
	});
}
