/**
 * 机房拓扑请求后台数据的方法
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
 * 初始化拓扑(加载机房列表)
 */
SoftTopo.AjaxData.prototype.initTopo = function() {
	$.ajax({
		type: "POST",
		url: SoftTopo.initRoom_url,
		data: {},
		cache: false,
		dataType: "json",
		success: function(json) {

			if (json && json.length) {
				SoftTopo.App.lockExportImage(false);
				//create roomList
				SoftTopo.App.getData().createToolbox(json);
			} else {
				SoftTopo.App.lockExportImage(true);
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
};
SoftTopo.AjaxData.prototype.saveTopo = function(json) {
	if (json && json.datas.length) {
		var nodes = {};
		$.each(json.datas, function(index, val) {
			var properties = val.json.properties;
			//过滤连线节点
			if (val._className.indexOf("Edge") == -1 && properties.data) {
				var nodeId = properties.data.id,
					nodeX = "" + val.json.location.x != "undefined" ? val.json.location.x : val.json.location.json.x,
					nodeY = "" + val.json.location.y != "undefined" ? val.json.location.y : val.json.location.json.y;
				nodes[nodeId] = {
					"topoType": "TopoCab",
					"nodeType": properties.data.hostType == "CABINET" ? "NODECABINET" : "NODEHOST",
					"hostId": nodeId,
					"X": nodeX,
					"Y": nodeY
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
};
/**
 * @parameter
 */
SoftTopo.AjaxData.prototype.getCabListByRoomId = function(roomId) {
	$.ajax({
		type: "POST",
		url: SoftTopo.initCabList_url,
		data: {
			roomId: roomId
		},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json) {
				SoftTopo.App.getData().createCabList(json);
			} else {
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
};
SoftTopo.AjaxData.prototype.updateAlarmNodes = function(nodeIds) {

	$.ajax({
		type: "POST",
		url: SoftTopo.updateAlarm_url,
		data: {
			hostid: nodeIds,
			ajaxRequestType: "hostEventData"
		},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.data) {
				SoftTopo.App.getData().updateAlarmNodes(json.data);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
};