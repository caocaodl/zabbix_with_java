

// * 租户今日监控项目统计
// * 
// * httptestNum  网站监察
// * serviceAppNum 服务应用监察
// * vmNum  云主机监察

function todayMonItem(httptestNum, serviceAppNum, vmNum){
jQuery("#todayMonItemChart").insertFusionCharts({
		type:"Column2D",
		width:"100%",
		height:"200",
		dataFormat:"xml",
		dataSource:"" +
		"<chart caption=\"\" " +
		"bgcolor=\"FFFFFF\" " +
		"yaxisname=\"數目\" " +
		"numberprefix=\"\" " +
		"plotSpacePercent=\"50\" " +
		"showborder=\"1\"" +
		" theme=\"fint\">\r\n" + 
		"<set label=\"网站监察\" value=\""+httptestNum+"\"  />\r\n" + 
		"<set label=\"服务应用监察\" value=\""+serviceAppNum+"\"  />\r\n" + 
		"<set label=\"云主机监察\"  value=\""+vmNum+"\" />\r\n" + 
		"</chart>"
	});
}
//
///**
// * 租户服服务应用监控项目状态统计
// * @param norserviceAppNum 正常服务应用数目
// * @param eventserviceAppNum  产生了事件的服务应用数目
// */
function serviceApp(norserviceAppNum, eventserviceAppNum){
		 jQuery("#serviceAppChart").insertFusionCharts({
			 type:"Pie3D",
				width:"219",
				height:"200",
				dataFormat:"xml",
				dataSource:"" +
				"<chart " +
					"caption=\"\" " +
					"subcaption=\"\" " +
					"startingangle=\"1\" " +
					"showlabels=\"0\" " +
					"showlegend=\"1\" " +
					"pieYScale=\"60\" " +
					"legendIconScale=\"60\" " +
					"plotSpacePercent=\"20\" " +
					"enablemultislicing=\"0\" " +
					"slicingdistance=\"5\" " +
					"showpercentvalues=\"1\" " +
					"showpercentintooltip=\"0\" " +
					"legendPosition=\"right\" " +
					"theme=\"fint\"" +
				">" +  
					"<set label=\"正常\" value=\""+norserviceAppNum+"\" color=\"#5cb85c\"/>" + 
					"<set label=\"故障\" value=\""+eventserviceAppNum+"\" color=\"#f16437\" />" + 
					
				"</chart>"
			});
 
}






