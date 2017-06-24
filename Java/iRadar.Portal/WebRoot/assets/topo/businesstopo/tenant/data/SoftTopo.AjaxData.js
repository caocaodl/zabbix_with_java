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
SoftTopo.AjaxData = function() {};

/**
 * 初始化 右侧拓扑列表
 */
SoftTopo.AjaxData.prototype.initTopo = function() {
	var _this = this;
	$.ajax({
		type: "POST",
		url: SoftTopo.initTopoList_url,
		data: {},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json&&json.length) {
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
/**
 *获取业务拓扑
 **/
SoftTopo.AjaxData.prototype.getBusinessById = function(bsId) {
	$.ajax({
		type: "POST",
		url: SoftTopo.initTopo_url,
		data: {
			"bizTopoId": bsId
		},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.nodes) {
				SoftTopo.App.refreshData(json);
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
 * 保存 业务拓扑
 **/
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
					"topoType": "TopoBiz",
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
 * 保存创建业务拓扑数据
 **/
SoftTopo.AjaxData.prototype.saveNodeData = function(busiessInfo) {
	var _this = this;
	$.ajax({
		type: "POST",
		url: SoftTopo.saveNodeData_url,
		data: busiessInfo,
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && !json.error) {
				//更新 业务拓扑列表
				SoftTopo.App.getData().updateTopoList(json);
			} else {
				// alert("保存失败");
				if (json.error) {
					alert(json.error);
				}
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("保存失败");
		}
	});

};
/**删除业务拓扑
 * @parameter bizTopoId 
 **/
SoftTopo.AjaxData.prototype.deleteTopoById = function(bizTopoId) {
	var successData = null;
	$.ajax({
		type: "POST",
		url: SoftTopo.deleteTopoById_url,
		data: {
			bizTopoId: bizTopoId
		},
		async: false,
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.bizTopoId) {
				successData = json;
			} else {
				// alert("删除失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("删除失败");
		}
	});
	return successData;
};
/**编辑业务拓扑
 * @parameter busiessInfo 
 **/
SoftTopo.AjaxData.prototype.editorTopo = function(busiessInfo) {

	var _this = this;
	$.ajax({
		type: "POST",
		url: SoftTopo.editorTopoById_url,
		data: busiessInfo,
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.bizTopoId) {
				var data = SoftTopo.App.getData();
				data.closeWin();
				data.updateTopoName(json);
				_this.getBusinessById(busiessInfo.bizTopoId);
			} else {
				// alert("更新失败");
				if (json.error) {
					alert(json.error);
				}
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("更新失败");
		}
	});
};