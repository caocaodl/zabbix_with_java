/**
 *物理链路拓扑
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
	 * 获取首页拓扑对象
	 */
SoftTopo.AjaxData.prototype._getDashboardsTopo = function() {
	var $dashboardsPanel = window.parent.$("#JS_contentTab .tabs-panels").children()[0],
		dashboardsWin = $("iframe", $dashboardsPanel)[0].contentWindow,
		topoWin = dashboardsWin.jQuery("iframe", dashboardsWin.document)[0].contentWindow;
	return topoWin.SoftTopo.App;
};
/**
 * 初始化拓扑
 */
SoftTopo.AjaxData.prototype.initTopo = function() {
	var that = this;
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
				that._getDashboardsTopo().getAjaxData().initTopo();
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

/*
 *保存拓扑
 */
SoftTopo.AjaxData.prototype.saveTopo = function(json) {

	if (json && json.datas.length) {
		var _this = this,
			nodes = {},
			parentNodes = {}, //父节点
			surplusNodes = {}, //界面剩余节点(独立存在)
			refParentNodes = [], //即时父节点又是子节点
			refs = json.refs;

		$.each(json.datas, function(index, val) {
			var properties = val.json.properties;
			//过滤连线节点
			if (val._className.indexOf("Edge") == -1 && properties.data) {

				if (properties.data.$ref) {
					properties.data = refs[properties.data.$ref];
				}

				var nodeX = 0,
					nodeY = 0,
					nodeId = properties.data.id,
					ownerHost = properties.data.ownerHost || "",
					hostType = properties.data.hostType;

				if (val.json.location != undefined) {
					nodeX = "" + val.json.location == undefined ? 0 : "" + val.json.location.x != "undefined" ? val.json.location.x : val.json.location.json.x;
					nodeY = "" + val.json.location == undefined ? 0 : "" + val.json.location.y != "undefined" ? val.json.location.y : val.json.location.json.y;
				}
				if (nodeX == 0 && nodeY == 0) {
					nodeY = 1;
				}
				var nodeInfo = {
					"hostType": hostType,
					"topoType": "TopoPhy",
					"hostId": nodeId,
					"name": properties.data.name,
					"ownerHost": ownerHost,
					"X": nodeX,
					"Y": nodeY
				};
				if (hostType === "GROUP") {
					var _groupWidth,
						_groupHeight;
					if (val.json.size) {
						_groupWidth = val.json.size.width;
						_groupHeight = val.json.size.height;
						if (val.json.size.json) {
							_groupWidth = val.json.size.json.width;
							_groupHeight = val.json.size.json.height;
						}
					}
					//缩略图处于收缩状态
					if (!_groupWidth&&properties.data.groupSize) {
						_groupWidth = properties.data.groupSize.width;
						_groupHeight = properties.data.groupSize.height;
					}
					nodeInfo.width = _groupWidth;
					nodeInfo.height = _groupHeight;
				};
				if (val.json.parent && val.json.parent._ref) {
					nodeInfo._ref = val.json.parent._ref;
				}

				val._refId ? parentNodes[val._refId] = nodeInfo : nodes[nodeId] = nodeInfo;
			}
		});
		//父节点与普通节点分离
		$.each(nodes, function(index, node) {
			if (node._ref) {
				//将该节点存入父节点内
				var parent = parentNodes[node._ref];
				parent.children = parent.children || [];
				delete node._ref;
				parent.children.push(node);
			} else {
				//将没有父节点的设备存入到surplusNodes
				surplusNodes[node.hostId] = node;
			}
		});

		$.each(parentNodes, function(index, node) {
			if (node._ref) {
				//将该节点存入父节点内
				var parent = parentNodes[node._ref];
				parent.children = parent.children || [];
				delete node._ref;
				parent.children.push(node);
				refParentNodes.push(index);
			}
		});
		$.each(refParentNodes, function(index, val) {
			delete parentNodes[val];
		});

		$.extend(parentNodes, surplusNodes);
		$.ajax({
			type: "POST",
			url: SoftTopo.saveTopo_url,
			data: {
				nodes: parentNodes,
				removeIds: SoftTopo.App.getData().removeGroupIds
			},
			cache: false,
			// dataType: "json",
			success: function(json) {
				json = JSON.parse(json);
				if (json && json.success) {
					//refresh dashboard topo
					_this._getDashboardsTopo().getAjaxData().initTopo();
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
 * 获取 设备CPU等信息
 */
SoftTopo.AjaxData.prototype.updateHostMonitor = function(hosts) {
	var that = this;
	$.ajax({
		type: "POST",
		url: SoftTopo.updateSubNetTopo_url,
		data: {
			ajaxRequestType: "hostMonitorData",
			hosts: hosts
		},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.data) {
				SoftTopo.App.getData().updateHostMonitor(json);
				that._getDashboardsTopo().getData().updateHostMonitor(json);
			} else {
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
};
/**
 * 获取 设备告警信息
 */
SoftTopo.AjaxData.prototype.updateAlarmNodes = function(hostIds) {
	var that = this;
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
				that._getDashboardsTopo().getData().updateAlarmNodes(json);
			} else {
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
};