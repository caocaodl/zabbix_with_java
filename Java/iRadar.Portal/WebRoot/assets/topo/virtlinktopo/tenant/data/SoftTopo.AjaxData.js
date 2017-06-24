/**
 * 请求后台数据的方法
 * 示例
 * 	$.ajax({
 *		type: "POST",//post||get
 *		url: "",//请求路径
 *		data: {},//发送到服务器端参数
 *		async:true,//默认true异步请求
 *		cache: false,//设置为false将不会从浏览器缓存中加载请求信息
 *		dataType: "json",//预期服务器返回的数据类型		
 *		success: function(json) {},
 *		error: function(XMLHttpRequest, textStatus, errorThrown) {}
 *	});
 */
SoftTopo.AjaxData = function() {}

/**
 * 初始化拓扑
 */
SoftTopo.AjaxData.prototype.initTopo = function() {
		$.ajax({
			type: "POST",
			url: SoftTopo.initTopo_url,
			data: {},
			cache: false,
			dataType: "json",
			success: function(json) {
				if (json && json.nodes.length) {
					SoftTopo.App.lockExportImage(false);
					SoftTopo.App.refreshData(json);
				} else {
					SoftTopo.App.lockExportImage(true);
					// alert("加载失败");
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				// alert("加载失败");
			}
		});
	}
	/**
	 * 更新
	 */
SoftTopo.AjaxData.prototype.updateSubNetTopo = function(hosts) {

		$.ajax({
			type: "POST",
			url: SoftTopo.updateSubNetTopo_url,
			data: {
				hosts: hosts,
				ajaxRequestType: "hostMonitorData"
			},
			cache: false,
			dataType: "json",
			success: function(json) {

				if (json && json.data) {
					SoftTopo.App.getData().updateSubNetCallBack(json);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {

			}
		});
	}
	/**
	 * 获取 设备告警
	 */
SoftTopo.AjaxData.prototype.updateAlarmNodes = function(hostIds) {

	$.ajax({
		type: "POST",
		url: SoftTopo.updateNodes_url,
		data: {
			ajaxRequestType: "hostEventData",
			hostid: hostIds
		},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.data) {
				SoftTopo.App.getData().updateAlarmNodes(json);
			} else {
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
}
SoftTopo.AjaxData.prototype.saveTopo = function(json) {
	if (json && json.datas.length) {
		var nodes = {};
		$.each(json.datas, function(index, val) {
			var properties = val.json.properties;
			//过滤连线节点
			if (val._className.indexOf("Edge") == -1 && properties.data) {
				var nodeId = properties.data.id;
				nodes[nodeId] = {
					"topoType": "TopoVirtTenant",
					"hostId": nodeId,
					"X": val.json.location.x,
					"Y": val.json.location.y
				};
			}
		});

		$.ajax({
			type: "POST",
			url: SoftTopo.saveTopo_url,
			data: {
				nodes: nodes
			},
			cache: false,
			// dataType: "json",
			success: function(json) {
				json = JSON.parse(json);
				if (json && json.success) {
					alert("保存成功");
				} else {
					// alert("保存失败");
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				// alert("保存失败");
			}
		});
	}
}