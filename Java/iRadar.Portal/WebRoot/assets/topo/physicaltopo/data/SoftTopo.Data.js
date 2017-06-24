/*
 * 物理链路拓扑数据处理类
 *
 */

SoftTopo.Data = function(graph) {
	this.graph = graph;
	this.graph.name = "物理链路拓扑";
	this.nodes = [];
	this.map = {};
	this.hostMonitorTimer = null;
	this.nodeTimer = null;
	this.groups = [];
	this.subNets = [];
	this.MILLISEC = 60000;
	this.GROUPTYPE = "rectGroup";
	this.GROUPHOSTTYPE = "GROUP";
	this.removeGroupIds = "";
	this.init();
};

SoftTopo.Data.prototype.init = function() {
	var _this = this,
		GROUPTYPE = "rectGroup",
		HIGHLIGHTCOLOR = "#FF0",
		currentElement,
		oldLocation = {};

	//启用节点编辑功能
	this.graph.editable = true;
	//禁止节点旋转功能
	this.graph.isRotatable = function() {
		return false;
	};
	//只有缩略图可编辑大小
	this.graph.isResizable = function(node) {
		return node.type == GROUPTYPE;
	};
	this.graph.ondblclick = function(evt) {
		var element = evt.getData();
		if (element) {
			if (element.type && element.type === GROUPTYPE) {
				element.reverseExpanded();
				element.editable = !0;
			} else if (element.type === "Q.Group" && !element.expanded) {
				_this.phyDoLayout(element);
			} else {
				element.showDetail = !element.showDetail;
				element.editable = !1;
				//update cpu info
				if (element.showDetail) {
					var data = element.get("data"),
						hosts = {};
					hosts[data.id] = {
						"hostType": data.hostType,
						"hostid": data.id
					};
					SoftTopo.App.getAjaxData().updateHostMonitor(hosts);
				};
			}

		}
	}
	Q.Defaults.registerInteractions("VPC", [Q.EditInteraction, Q.ResizeInteraction, Q.SelectionInteraction,
		Q.MoveInteraction, Q.WheelZoomInteraction, Q.TooltipInteraction, Q.DoubleClickInteraction
	]);
	//布局
	// this.layout = new Q.TreeLayouter(this.graph);
	// this.layout.layoutType = Q.Consts.LAYOUT_TYPE_EVEN_HORIZONTAL;
	this.layout = new Q.HierarchicLayouter(this.graph);

	function unhighlight(element) {
		element.setStyle(Q.Styles.SHAPE_FILL_COLOR, OLDFILLCOLOR);
	}
	//高亮显示
	function highlight(element) {
		if (currentElement == element) {
			return;
		}
		if (currentElement) {
			unhighlight(currentElement);
		}
		currentElement = element;
		if (!currentElement) {
			return;
		}
		OLDFILLCOLOR = currentElement.getStyle(Q.Styles.SHAPE_FILL_COLOR);
		currentElement.setStyle(Q.Styles.SHAPE_FILL_COLOR, HIGHLIGHTCOLOR);
	}

	//判断鼠标单击的位置是否在某个缩略图内
	function findGroup(evt) {
		var xy = this.graph.toLogical(evt.event),
			groups = [],
			minDistanceX,
			selectGroup;

		for (var i = 0, l = this.groups.length; i < l; i++) {
			var g = this.groups[i],
				groupBounds = this.graph.getUIBounds(g);

			//判断嵌套缩略图情况,拖拽设备放入嵌套内的子缩略图
			if (groupBounds && groupBounds.intersectsPoint(xy.x, xy.y) && g != evt.data) {
				groups.push(g);
			}
		};

		for (var j = 0, len = groups.length; j < len; j++) {
			var group = groups[j],
				distanceX = xy.x - group.x; //计算拖拽设备X轴与缩略图的X轴的距离
			if (!j) {
				minDistanceX = distanceX;
				selectGroup = group;
			} else {
				selectGroup = minDistanceX > distanceX ? group : selectGroup;
			}
		};
		if (selectGroup) {
			return selectGroup;
		};
	}

	//判断该节点是否为缩略图
	function isGroup(data) {
		return data.type === GROUPTYPE;
	}

	function groupIntersected(data) {
		var bounds = data.getBounds();
		for (var i = 0, l = this.groups.length; i < l; i++) {
			var g = this.groups[i];
			if (g != data && g.intersectsRect(bounds)) {
				return true;
			}
		}
		if (data.parent && !data.parent.containsRect(bounds)) {
			return true;
		}
		return false;
	}

	function childrenOutParent(parent) {
		var bounds = parent.getBounds();
		var result = false;
		parent.forEachChild(function(child) {
			if (child.atLeft) {
				return;
			}
			var childBounds = graph.getUIBounds(child);
			if (!bounds.contains(childBounds)) {
				result = true;
				return false;
			}
		})
		return result;
	}

	this.graph.interactionDispatcher.addListener(function(evt) {
		var data = evt.data,
			storageData = data.get("data");
		if (evt.kind == Q.InteractionEvent.RESIZE_START) {
			oldLocation[data.id] = {
				x: data.x,
				y: data.y,
				width: data.size.width,
				height: data.size.height
			};
		}
		if (evt.kind == Q.InteractionEvent.RESIZING) {
			return;
		}
		if (evt.kind == Q.InteractionEvent.RESIZE_END) {
			var oldBounds = oldLocation[data.id];
			oldLocation = {};
			return;
		}
		if (evt.kind == Q.InteractionEvent.ELEMENT_MOVE_START) {
			oldLocation[data.id] = {
				x: data.x,
				y: data.y
			};
			return;
		}
		if (evt.kind == Q.InteractionEvent.ELEMENT_MOVING) {
			var g = findGroup.call(_this, evt);
			highlight(g);
			return;
		}
		if (evt.kind == Q.InteractionEvent.ELEMENT_MOVE_END) {
			highlight();

			var g = findGroup.call(_this, evt);

			if (g) {

				if (storageData.hostType.toLocaleLowerCase() === "vm") {
					var vmParent = data.parent;
					vmParent.parent = vmParent.host = g;
				} else if (storageData.hostType.toLocaleLowerCase() === "subnet") {
					//子网禁止拖拽到缩略图内
					data.x = oldLocation[data.id].x;
					data.y = oldLocation[data.id].y;
				} else {

					if (data == g) { //子缩略图脱离父缩略图
						data.parent = data.host = null;
					} else {
						data.parent = data.host = g;
					}
				}
			} else {
				//虚拟节点禁止和物理机脱离关系(拖拽物理机内的虚拟节点可能会脱离父节点的)
				if (storageData.hostType.toLocaleLowerCase() !== "vm") {
					var _parent = data.parent;
					//子网内的设备拖拽不修改parent（子网内的设备拖拽会脱离父节点移动到上个层级）
					if (_parent && _parent.get("data").hostType.toLocaleLowerCase() === "subnet") {} else {
						data.parent = data.host = null;
					}
				}
			}
			oldLocation = {};
		}
	});
};
/**
 * 右键菜单
 **/
SoftTopo.Data.prototype.contextMenu = function() {
	var _this = this,
		menuArr = [],
		$win = $("#createGroup-win");

	function getGroupData(winData) {

		var $groupName = $("#group-name").val(),
			$data = $win.data("data");
		if ($groupName) {
			_this.createGroup({
				"id": -new Date().getTime(),
				"name": $groupName,
				"hostType": _this.GROUPHOSTTYPE,
				"ownerHost": "",
				"X": $data.x,
				"Y": $data.y
			});
			$win.window("close");
		}
	};

	function createWin(winData) {
		if (!$win.length) {
			var $header = $('<div  class="header"><span>名称:</span><input id="group-name"  class="easyui-validatebox textbox"></div>'),
				$footer = $('<div class="footer"></div>'),
				$saveBtn = $('<a href="javascript:void(0)" class="easyui-linkbutton save">确定</a>'),
				$createCon = $('<div  class="topo-win" ></div>');
			$saveBtn.click(getGroupData).appendTo($footer);
			$createCon.append($header).append($footer);
			$win = $('<div id="createGroup-win"></div>').window({
				title: "创建缩略图",
				width: 300,
				height: 150,
				style: {
					"background": "#058FCD"
				},
				modal: true,
				resizable: false,
				collapsible: false,
				minimizable: false,
				maximizable: false,
				content: $createCon,
				onBeforeOpen: function() {
					var $this = $(this);
					$this.find("#group-name").val("").focus();
					return true;
				}
			});
		} else {
			$win.window("open");
		}
		$win.data("data", winData);
	};
	if (!SoftTopo.AppConfig.CONTEXTMENU) {
		return menuArr;
	}
	//查看详情
	var hostDetail = {
		text: "查看详情",
		action: function(evt, item) {
			if (item.data && item.data.eventData) {
				var nodeData = item.data.eventData.get("data"),
					url = SoftTopo.detail_url + "?hostid=" + nodeData.hostId + "&groupid=" + nodeData.groupId + "";
				window.top.$.workspace.openTab(nodeData.name + "详情", window.top.ctxpath + url);
			}
		},
		before: function(graph, data, evt) {

			var _showMenu = false;
			if (data && data.get("data")) {
				var nodeData = data.get("data");
				nodeData.hostId && nodeData.groupId ? _showMenu = true : _showMenu = false;
			}
			return _showMenu;
		},
		scope: _this
	};
	menuArr.push(hostDetail);
	//create group menu
	if (SoftTopo.AppConfig.GROUP) {

		var createGroupMenu = {
			text: "创建缩略图",
			action: function(evt, item) {
				var _this = this;
				var event = _this.graph.toLogical(evt.x, evt.y);
				createWin(event);
			},
			before: function(graph, data, evt) {
				return true;
			},
			scope: _this
		};
		menuArr.push(createGroupMenu);

		var removeGroupMenu = {
			text: "解散缩略图",
			action: function(evt, item) {

				if (item.data && item.data.eventData) {

					var _this = this,
						group = item.data.eventData,
						parent = group.parent || null,
						children = group.children.datas,
						groupData = group.get("data");
					groupData && groupData.id && groupData.id > 0 ? _this.removeGroupIds += groupData.id + "," : "";

					//修改缩略图内的设备父节点						
					for (var i = 0, len = children.length; i < len; i++) {
						var node = children[i];
						node.parent = node.host = parent;
						--len;
						--i;
					};
					Q.forEach(this.groups, function(node, index) {
						if (group == node) {
							_this.groups.splice(index, 1);
							//remove group
							_this.graph.removeElement(node);
							return;
						}
					});
				}
			},
			before: function(graph, data, evt) {
				//只有缩略图可以解散
				if (data && data.type === this.GROUPTYPE) {
					return true;
				};
				return false;
			},
			scope: _this
		};
		menuArr.push(removeGroupMenu);
	}
	if (SoftTopo.AppConfig.GROUPLAYOUT) {
		var layoutGroupMenu = {
			text: "重新布局",
			action: function(evt, item) {

				if (item.data && item.data.eventData) {
					item.data.eventData.type == item.scope.GROUPTYPE ? item.scope.groupDoLayout(item.data.eventData) : item.scope.phyDoLayout(item.data.eventData);;
				}
			},
			before: function(graph, data, evt) {
				//只有物理机、缩略图可以布局&& 打开状态
				if (data && (data.type === this.GROUPTYPE || data.type === "Q.Group") && data.expanded) {
					return true;
				};
				return false;
			},
			scope: _this
		};
		menuArr.push(layoutGroupMenu);
	}
	return menuArr;
};
/*
 * 物理机排序坐标
 */
SoftTopo.Data.prototype.phyDoLayout = function(parentNode) {
	var parentX = parentNode.x || 100,
		parentY = parentNode.y || 100,
		children = parentNode.children ? parentNode.children.datas : [],
		nodeMaxWidth = 0,
		count = 0,
		nodeHeight = 80,
		margin = 20,
		countWidth = 0,
		countHeight = 0;

	// 获取最大宽度的子节点
	for (var i = 0, len = children.length; i < len; i++) {
		var node = children[i],
			nodeWidth = node.name.length * 12;
		nodeWidth = nodeWidth > 80 ? nodeWidth : 80;
		nodeMaxWidth = Math.max(nodeMaxWidth, nodeWidth);
	};
	$.each(children, function(index, node) {

		if (!count) {
			node.x = parentX;
			node.y = parentY;
			countWidth = node.x;
			countHeight = node.y;
		} else {
			node.x = countWidth + count * (nodeMaxWidth + margin);
		}
		if (count % 3 == 0 && count != 0) {
			countHeight = countHeight + (nodeHeight + margin);
			count = 0;
			node.x = parentX;
			countWidth = node.x;
		}
		node.y = countHeight;
		count++;
	});
};
/*
 * 缩略图排序坐标
 */
SoftTopo.Data.prototype.groupDoLayout = function(parentNode) {

	var _this = this,
		parentX = parentNode.x || 100,
		parentY = parentNode.y || 100,
		children = parentNode.children ? parentNode.children.datas : [],
		nodeMaxWidth = 0,
		count = 0,
		nodeHeight = 80,
		margin = 20,
		countWidth = 0,
		countHeight = 0,
		groupHeight = 0,
		groupWidth = 0,
		childGroupMaxWidth = 0, //嵌套内缩略图的最大宽度
		childGroupMaxHeight = 0;
	// 获取最大宽度的子节点
	for (var i = 0, len = children.length; i < len; i++) {
		var node = children[i],
			nodeWidth = node.name.length * 12;
		if (node.type !== this.GROUPTYPE) {
			nodeWidth = nodeWidth > 80 ? nodeWidth : 80;
			nodeMaxWidth = Math.max(nodeMaxWidth, nodeWidth);
		} else if (node.type === this.GROUPTYPE) { //缩略图内的子缩略图
			var localtion = this.graph.toCanvas(node.x, node.y);
			childGroupMaxWidth = Math.max(childGroupMaxWidth, (localtion.x + parseFloat(node.size.width)));
			childGroupMaxHeight = Math.max(childGroupMaxHeight, (localtion.y + parseFloat(node.size.height)));
		};
	};
	$.each(children, function(index, node) {
		if (node.type === _this.GROUPTYPE) {
			return;
		}
		if (!count) {
			node.x = parentX + nodeMaxWidth / 2;
			node.y = parentY + margin * 2;
			countWidth = node.x;
			countHeight = node.y;
			groupHeight += nodeHeight;
			var childrenCount = children.length >= 3 ? 3 : children.length || 3;
			groupWidth = (nodeMaxWidth + margin / 2) * childrenCount; //默认一排三台设备的宽度

		} else {
			node.x = countWidth + count * (nodeMaxWidth + margin);
		}
		if (count % 3 == 0 && count != 0) {
			countHeight = countHeight + (nodeHeight / 1.5 + margin);
			count = 0;
			node.x = parentX + nodeMaxWidth / 2;
			groupHeight += nodeHeight;
		}
		node.y = countHeight;
		count++;
	});
	//判断缩略图的宽高度与缩略图内嵌套的缩略图宽高度
	var parentLocation = this.graph.toCanvas(parentNode.x, parentNode.y);
	if (groupHeight < childGroupMaxHeight) {
		groupHeight = childGroupMaxHeight - parentLocation.y + margin;
	}
	if (groupWidth < childGroupMaxWidth) {
		groupWidth = childGroupMaxWidth - parentLocation.x + margin;
	}


	function resetSize(node, nodeSize) {

		if (nodeSize.width > parseFloat(node.size.width) || nodeSize.height > parseFloat(node.size.height)) {
			node.size = nodeSize;
		}

		if (node.parent && node.host) {
			var parent = node.parent,
				parentWidth = parseFloat(parent.size.width),
				parentHeight = parseFloat(parent.size.height);

			//判断子节点与父节点坐标
			if ((parent.x + parentWidth) < (node.x + node.size.width)) {
				parentWidth += (node.x + node.size.width) - (parent.x + parentWidth) + margin;
			}
			if ((parent.y + parentHeight) < (node.y + node.size.height)) {
				parentHeight += (node.y + node.size.height) - (parent.y + parentHeight) + margin;

			}
			arguments.callee(parent, {
				width: parseFloat(parentWidth),
				height: parseFloat(parentHeight)
			});

		}
	};
	if (groupWidth && groupHeight) {
		resetSize(parentNode, {
			width: parseFloat(groupWidth),
			height: parseFloat(groupHeight)
		});
		this.graph.topCanvas.clear();
	};

};
/*
 * 对子网数据进行过滤
 */
SoftTopo.Data.prototype.createData = function(json) {
	var _this = this,
		autoLayout = false,
		subNetCount = 0,
		subnetParent = {},
		//储存所有子网节点(该子节点符合subnet=""只有一个父节点)
		subnetChild = {},
		//将所有节点放入map内
		jsonNodesMap = {},
		childrenIds = [];

	this.nodes = [];
	this.nodes.length = 0;
	this.map = {};
	this.graph.clear();
	if (json.nodes && json.nodes.length) {

		$.each(json.nodes, function(index, node) {
			jsonNodesMap[node.id] = node;
			if (node.hostType.toLocaleLowerCase() == "subnet") {
				subNetCount++;
			}
		});
		//过滤节点(将子节点加入父节点内)
		$.each(json.nodes, function(index, node) {
			//存在上级节点(父节点)//subnet 禁止将子网的节点归属到子网节点下
			if (node.ownerHost) {
				var parentNode = jsonNodesMap[node.ownerHost];
				if (parentNode) {
					parentNode.children = parentNode.children || [];
					parentNode.children.push(node);
					childrenIds.push(node.id);
				}
			}
		});
		//jsonNodesMap 删除子节点(子节点已经追加到父节点内)
		$.each(childrenIds, function(index, nodeId) {
			delete jsonNodesMap[nodeId];
		});
		//重新绑定json.nodes
		json.nodes.length = 0;
		json.nodes = [];
		$.each(jsonNodesMap, function(index, node) {
			json.nodes.push(node);
		});

		if (subNetCount >= 2) {
			
			for (var i = 0, len = json.nodes.length; i < len; i++) {
				var node = json.nodes[i];
				if (node.hostType.toLocaleLowerCase() == "subnet") {
					subnetParent[node.id] = node;
				}
				//将符合条件的子节点从json.nodes中删除
				if (node.subnet && node.subnet.indexOf("$") == -1) {
					subnetChild[node.id] = node;
					json.nodes.splice(i, 1);
					--len;
					--i;
				}
			};
			//将子节点追加到父节点内
			$.each(subnetChild, function(index, val) {
				//根据子节点subnet属性值获取父节点
				var parent = subnetParent[val.subnet];
				if (parent && !parent.children) {
					parent.children = [];
				}
				parent.children.push(val);
				//删除子节点连线
				for (var j = 0, edgeLen = json.edges.length; j < edgeLen; j++) {
					var edge = json.edges[j];
					if (edge.from == val.subnet && edge.to == val.id) {
						json.edges.splice(j, 1);
						--edgeLen;
						--j;
					}
				};
			});
		}
		if (json.nodes) {
			
			Q.forEach(json.nodes, function(data) {
				var node,
					hostType = data.hostType.toLocaleLowerCase(),
					hostInfo = _this._getHostType(hostType),
					image = SoftTopo.Util.setIcon("", hostType),
					dataX = data.X ? parseFloat(data.X) : 0,
					dataY = data.Y ? parseFloat(data.Y) : 0;

				if (hostType === "subnet") {
					if (subNetCount > 1) {
						node = _this.createSubnetworkNode(data.name, 150, 0);
						if (data.children && data.children.length) {
							_this.appendChildren(node, data.children);
						}
					}
						
				} else if (hostType === "group") {
					node = _this.createGroup(data);

				} else {

					if (data.children && data.children.length) { //物理机
						node = _this.createPhysicalGroup(data);
						// SoftTopo.Util.setIcon(node, hostType);
					} else { //普通设备
						node = _this.createServer(data.name, "", image, hostInfo);
					}
				}
				if (node) {
					node.parentChildrenDirection = Q.Consts.DIRECTION_BOTTOM;
					node.layoutType = Q.Consts.LAYOUT_TYPE_EVEN;
					node.set("data", data);
					_this.map[data.id] = node;
					node.x = dataX;
					node.y = dataY;
				}
				//只要有一个节点坐标不存在或为0,则调用布局 x=0 y=0 autolayout 
				if (!dataX && !dataY) {
					//存在两个子网或者只有一个子网并且当前节点的类型不是subnet 则调用布局
					if (subNetCount > 1 || (subNetCount <= 1 && hostType != "subnet")) {
						autoLayout = true;
					}
				}

			});
		}

		if (json.edges) {
			Q.forEach(json.edges, function(data) {
				var from = _this.map[data.from];
				var to = _this.map[data.to];
				if (!from || !to) {
					return;
				}
				var edge = _this.graph.createEdge(null, from, to);
				edge.setStyle(Q.Styles.ARROW_TO, false);
				edge.setStyle(Q.Styles.EDGE_WIDTH, 1);
				edge.set("data", data);
			});
		}
	}

	// doLayout
	if (autoLayout) {
		this.doLayout();
	}

	if (this.nodes.length) {
		var hostIds = [],
			shutdownHost = {};
		shutdownHost["data"] = {};
		$.each(this.nodes, function(index, val) {
			var data = val.get("data");
			if (data.hostType !== "GROUP") {

				hostIds.push(data.id);
				if (data.error && data.error != "false") {
					shutdownHost.data[data.id] = {
						error: !0
					}
				}
			}

		});

		this.updateAlarmNodes(shutdownHost);

		this.nodeTimer = setTimeout(function runNodeTimer() {
			var nodeGroup = [];

			function _updateAlarmNodes(nodeGroup) {
				SoftTopo.App.getAjaxData().updateAlarmNodes(nodeGroup);
			}
			for (var i = 0; i < hostIds.length; i++) {
				if (i != 0 && i % 30 === 0) {
					_updateAlarmNodes(nodeGroup);
					nodeGroup.length = 0;
				}
				nodeGroup.push(hostIds[i]);
			}
			if (nodeGroup.length) {
				_updateAlarmNodes(nodeGroup);
			};
			_this.nodeTimer = setTimeout(runNodeTimer, _this.MILLISEC);
		}, this.MILLISEC);
	}
};
/*
 *获取设备类型
 */
SoftTopo.Data.prototype._getHostType = function(hostType) {
	hostType = hostType.toLocaleLowerCase();

	if (hostType === "router" || hostType === "routeswitch" || hostType === "switch") {
		hostType = "net";
	}
	return SoftTopo.Util.getMonitoritem(hostType);
};
/*
 *创建普通设备
 */
SoftTopo.Data.prototype.createServer = function(name, id, icon, data) {

	var server = new Q.CustomServerNode(name, id, icon, data);
	server.showDetail = !server.showDetail;
	this.nodes.push(server);
	this.graph.addElement(server);
	return server;
};
/*
 *创建子网设备
 */
SoftTopo.Data.prototype.createSubnetworkNode = function(name, x, y) {
	var subnetwork = this.graph.createNode(name);
	subnetwork.enableSubNetwork = true;
	subnetwork.backgroundColor = "#DFF";
	subnetwork.image = Q.Graphs.subnetwork;
	subnetwork.setStyle(Q.Styles.BACKGROUND_COLOR, subnetwork.backgroundColor);
	subnetwork.setStyle(Q.Styles.PADDING, 5);
	subnetwork.setStyle(Q.Styles.BORDER, 1);
	subnetwork.setStyle(Q.Styles.BORDER_COLOR, "#888");
	this.graph.addElement(subnetwork);
	return subnetwork;
};
/*
 *创建子网内的设备
 */
SoftTopo.Data.prototype.appendChildren = function(subnetwork, child) {
	var subnetChild = child,
		per = Math.PI * 2 / subnetChild.length,
		angle = 0,
		RX = 300,
		RY = 200;
	for (var i = 0, len = subnetChild.length; i < len; i++) {
		var subChild = subnetChild[i],
			x = subChild.X ? parseFloat(subChild.X) : RX * Math.cos(angle),
			y = subChild.Y ? parseFloat(subChild.Y) : RY * Math.sin(angle),
			node = null,
			hostType = subChild.hostType.toLocaleLowerCase(),
			hostInfo = this._getHostType(hostType);

		if (hostType === "group") {
			node = this.createGroup(subChild);
		} else {
			if (subChild.children) {
				// 创建物理节点
				node = this.createPhysicalGroup(subChild);
			} else {
				var image = SoftTopo.Util.setIcon("", hostType);
				node = this.createServer(subChild.name, "", image, hostInfo);
				this.nodes.push(node);
			}
		}

		node.x = x;
		node.y = y;
		node.set("data", subChild);
		subnetwork.addChild(node);
		angle += per;
		this.map[subChild.id] = node;
		this.graph.addElement(node);
	}
};
/*
 *创建缩略图类型的节点
 *@parameter groupInfo {id:"",name:"",x:"",y:"",ownerHost:"",width:"",height:""}
 */
SoftTopo.Data.prototype.createGroup = function(groupInfo) {


	var _this = this,
		group = createFigure.call(this, groupInfo);
	group.set("data", groupInfo);

	//创建缩略图图形
	function createFigure(groupInfo) {
		var _this = this,
			rect = this.graph.createNode(groupInfo.name, groupInfo.X, groupInfo.Y);

		rect.type = this.GROUPTYPE;
		rect.editTitle = false; //禁止编辑标题
		rect.anchorPosition = Q.Position.LEFT_TOP;
		rect.image = Q.Shapes.getRect(0, 0, 100, 100);
		rect.size = {
			width: groupInfo.width || 400,
			height: groupInfo.height || 150
		};
		rect.setStyle(Q.Styles.SHAPE_STROKE, 0);
		rect.setStyle(Q.Styles.SHAPE_FILL_COLOR, Q.toColor(0x88FFFFFF));
		rect.setStyle(Q.Styles.BORDER, 1);
		rect.setStyle(Q.Styles.BORDER_RADIUS, 0);
		rect.setStyle(Q.Styles.BORDER_COLOR, "#1D4876");
		rect.setStyle(Q.Styles.BORDER_LINE_DASH, [5, 6]);
		rect.setStyle(Q.Styles.LABEL_FONT_STYLE, "bolder");
		rect.setStyle(Q.Styles.LABEL_PADDING, 5);
		rect.setStyle(Q.Styles.LABEL_ANCHOR_POSITION, Q.Position.CENTER_TOP);
		rect.setStyle(Q.Styles.LABEL_POSITION, Q.Position.CENTER_TOP);
		rect.setBounds = function(bounds) {
			this.x = bounds.x;
			this.y = bounds.y;
			this.size = new Q.Size(bounds.width, bounds.height);
		};
		rect.getBounds = function() {
			if (!this.expanded) {
				return new Q.Rect(this.x - 20, this.y - 20, 40, 40);
			}
			return new Q.Rect(this.x, this.y, this.size.width, this.size.height);
		};
		rect.inBounds = function(x, y) {
			return this.getBounds().contains(x, y);
		};
		rect.intersectsRect = function(rect) {
			return rect.intersectsRect(this.x, this.y, this.size.width, this.size.height);
		};
		rect.containsRect = function(rect) {
			return Q.containsRect(this.x, this.y, this.size.width, this.size.height, rect.x, rect.y, rect.width, rect.height);
		};
		rect.updateBoundsByChildren = function() {
			var bounds = new Q.Rect();
			Q.forEachChild(this, function(child) {
				bounds.add(graph.getUIBounds(child));
			});
			this.location = new Q.Point(bounds.x, bounds.y - 15);
			this.size = new Q.Size(Math.max(200, bounds.width), Math.max(70, bounds.height + 15));
		};
		rect.expanded = true;
		rect.reverseExpanded = function() {

			function nodeVisible(parentNode, visible) {
				var _that = arguments.callee;
				parentNode.forEachChild(function(child) {
					child.visible = visible;
					child.invalidateVisibility();

					child.hasChildren();
					if (child.children) {
						var childvisible = child.type === _this.GROUPTYPE && child.expanded == false ? false : visible;
						_that(child, childvisible);
					};
				});
			};
			this.expanded = !this.expanded;
			if (!this.expanded) {
				this._oldSize = this.size;
				this._oldImage = this.image;
				this.image = 'Q-group';
				this.size = null;
				nodeVisible(rect, false);
				var _data = this.get("data");
				_data ? _data : {};
				_data.groupSize = this._oldSize;
			} else {
				nodeVisible(rect, true);
				this.image = this._oldImage;
				this.size = this._oldSize;
			}
			_this.graph.invalidate();
		}
		rect.set("data", {
			"id": groupInfo.id,
			"name": groupInfo.name,
			"hostType": groupInfo.hostType
		});

		return rect;
	};

	if (groupInfo.children && groupInfo.children.length) {
		$.each(groupInfo.children, function(index, tbnailNode) {
			if (tbnailNode.children) {
				var chGroup;
				if (tbnailNode.hostType.toLocaleLowerCase() === "group") {
					//创建缩略图节点
					chGroup = _this.createGroup(tbnailNode);

				} else {
					// 创建物理节点
					chGroup = _this.createPhysicalGroup(tbnailNode);
					_this.map[tbnailNode.id] = chGroup;

				}
				chGroup.parent = chGroup.host = group;

			} else if (tbnailNode.hostType.toLocaleLowerCase() === "group") {
				//创建缩略图节点
				chGroup = _this.createGroup(tbnailNode);
				chGroup.parent = chGroup.host = group;
			} else {
				//创建普通节点
				var tbnailNodeHostInfo = _this._getHostType(tbnailNode.hostType),
					tbnailNodeImage = SoftTopo.Util.setIcon("", tbnailNode.hostType),
					tbnailNodeFigure = _this.createServer(tbnailNode.name, "", tbnailNodeImage, tbnailNodeHostInfo);
				tbnailNodeFigure.x = tbnailNode.X;
				tbnailNodeFigure.y = tbnailNode.Y;
				tbnailNodeFigure.set("data", tbnailNode);
				tbnailNodeFigure.parent = tbnailNodeFigure.host = group;
				_this.map[tbnailNode.id] = tbnailNodeFigure;
				_this.graph.addElement(tbnailNodeFigure);
			}
		});
	}
	this.groups.push(group);
	this.graph.addElement(group);
	return group;
};

/*
 *创建物理机设备
 */
SoftTopo.Data.prototype.createPhysicalGroup = function(groupInfo) {

	var _this = this,
		groupNode = createFigure.call(this, groupInfo);
	//物理机也需要刷新告警信息
	this.nodes.push(groupNode);
	this.subNets.push(groupNode);

	function createFigure(groupInfo) {
		var expanded = false;
		var group = this.graph.createGroup(groupInfo.name, groupInfo.id);
		group.expanded = expanded;
		group.x = groupInfo.X || 1;
		group.y = groupInfo.Y || 1;
		group.set("data", {
			"id": groupInfo.id,
			"name": groupInfo.name,
			"hostType": groupInfo.hostType
		});
		group.setStyle(Q.Styles.GROUP_BACKGROUND_COLOR, "rgba(245,245,245,0.4)");
		// var groupHandle = new Q.LabelUI(expanded ? "-" : "+");
		// groupHandle.backgroundColor = "#2898E0";
		// groupHandle.color = "#FFF";
		// groupHandle.padding = new Q.Insets(0, 4);
		// groupHandle.borderRadius = 0;
		// groupHandle.position = Q.Position.RIGHT_TOP;
		// groupHandle.anchorPosition = Q.Position.LEFT_TOP;
		// groupHandle.type = "GroupHandle";
		// groupHandle.reverseExpanded = function(evt) {
		//     var g = this.parent.data;
		//     g.expanded = !g.expanded;
		// }
		// group.addUI(groupHandle, {
		//     property: "expanded",
		//     callback: function(value, ui) {
		//         ui.data = value ? "-" : "+";
		//     }
		// });

		return group;
	}

	if (groupInfo.children && groupInfo.children.length) {
		var autoLayout = false;
		//判断子节点是否有坐标
		for (var i = 0, len = groupInfo.children.length; i < len; i++) {
			var childNode = groupInfo.children[i];

			if (!childNode.X && !childNode.Y) {
				autoLayout = true;
				break;
			};

		};
		$.each(groupInfo.children, function(index, tbnailNode) {

			//创建普通节点
			var tbnailNodeHostInfo = _this._getHostType(tbnailNode.hostType),
				tbnailNodeImage = SoftTopo.Util.setIcon("", tbnailNode.hostType),
				tbnailNodeFigure = _this.createServer(tbnailNode.name, "", tbnailNodeImage, tbnailNodeHostInfo);
			tbnailNodeFigure.location = new Q.Point(tbnailNode.X || 1, tbnailNode.Y || 1);
			tbnailNodeFigure.set("data", tbnailNode);
			groupNode.addChild(tbnailNodeFigure);
			_this.map[tbnailNode.id] = tbnailNodeFigure;
			_this.nodes.push(tbnailNodeFigure);
			_this.graph.addElement(tbnailNodeFigure);

		});
		// autolayout child
		if (autoLayout) {
			this.phyDoLayout(groupNode);
		}
	}
	return groupNode;
};

SoftTopo.Data.prototype.doLayout = function() {
	//判断用户是否选中缩略图
	var groupModel, datas = this.graph.selectionModel.datas;

	function groupIsVisible(groups, visible) {

		function childrenIsVisible(children, visible) {
			//缩略图内的设备同时禁止布局
			for (var k = 0, childrenLen = children.length; k < childrenLen; k++) {
				var child = children[k];
				child.visible = visible;
				child.invalidateVisibility();
				child.invalidate();
				if (child.type == "Q.Group") { //物理机
					arguments.callee(child, visible);
				}
			};
		}
		for (var i = 0, len = groups.length; i < len; i++) {
			var group = groups[i];
			group.visible = visible;
			group.invalidateVisibility();
			group.invalidate();
			childrenIsVisible(group.children.datas, group.expanded ? visible : false);
		};
	};

	for (var i = 0, len = datas.length; i < len; i++) {
		var selectionModel = datas[i];
		if (selectionModel.type == this.GROUPTYPE) {
			groupModel = selectionModel;
			break;
		};
	};

	function collapseSubNet(subNets, expanded) {

		for (var i = 0, len = subNets.length; i < len; i++) {
			var subNet = subNets[i];
			subNet.expanded = expanded;
		};
	};
	if (groupModel) { //调用缩略图布局
		this.groupDoLayout(groupModel);
	} else { //调用全局布局
		var groups = this.groups,
			that = this;
		collapseSubNet(this.subNets, false);
		groupIsVisible(groups, false);

		this.layout.doLayout({
			callback: function() {

				groupIsVisible(groups, true);
				// that.graph.moveToCenter();
			}
		});
	}
};
/*
 *更新 设备CPU等信息
 */
SoftTopo.Data.prototype.updateHostMonitor = function(json) {
	var _this = this;
	this.nodes.forEach(function(node) {
		var nodeData = node.get("data"),
			data = json.data[nodeData.id],
			hostInfo = _this._getHostType(nodeData.hostType);

		if (data && hostInfo) {
			var tooltip = "";

			$.each(hostInfo, function(index, val) {

				var itemkey = val.itemkey,
					itemCh = val.itemCh,
					itemType = val.itemtype,
					keyVal = "" + data[itemkey],
					showVal = keyVal;
				if (itemType === "bar") {
					showVal = parseFloat(keyVal);
					showVal == 1 ? keyVal = 0 : keyVal = keyVal;
					showVal = showVal ? showVal / 100 : 1 / 100;
				} else {
					var index = keyVal.indexOf("/");
					index > -1 ? showVal = keyVal.substring(0, index) + ".." : keyVal.length > 13 ? showVal = keyVal.substring(0, 13) + ".." : showVal;
				}
				node.set(itemkey, showVal);
				keyVal = itemCh == "CPU" || itemCh == "MEM" ? keyVal + "%" : keyVal;
				tooltip += itemCh + ":" + keyVal + "</br>";
			});
			node.tooltip = tooltip;
			return false;
		}
	});
};
/*
 *更新设备信息(告警信息)
 */
SoftTopo.Data.prototype.updateAlarmNodes = function(json) {

	if (this.nodes.length) {
		var REDCOLOR = "#F00",
			YELLOWCOLOR = "#FF0",
			RENDER_COLOR = "#aeab64";
		//重置告警状态
		function resetAlarm(node, alarmInfo) {
			node.alarmLabel = alarmInfo.description;
			node.alarmColor = alarmInfo.color;
			node.setStyle(Q.Styles.IMAGE_PADDING, alarmInfo.padding);
			node.setStyle(Q.Styles.IMAGE_BACKGROUND_COLOR, alarmInfo.color || "");
		};
		//重置关机状态
		function resetShutdown(node, isShutdown) {
			var nodeColor = "",
				nodeBlendModel = "",
				edgeColor = "";
			if (isShutdown) {
				nodeColor = RENDER_COLOR;
				nodeBlendModel = Q.Consts.BLEND_MODE_SCREEN;
				edgeColor = REDCOLOR;
			}
			var nodeId = node.id;
			node.forEachEdge(function(edge) {
				//判断连线的另一端设备是否正常,如果异常,则不改变连线颜色状态
				var linkNode = edge.from.id === nodeId ? edge.to : edge.from;
				if (!linkNode.get("data").error) {
					edge.setStyle(Q.Styles.EDGE_COLOR, edgeColor);
				}
			});
			node.setStyle(Q.Styles.RENDER_COLOR, nodeColor);
			node.setStyle(Q.Styles.RENDER_COLOR_BLEND_MODE, nodeBlendModel);
		};
		$.each(this.nodes, function(index, node) {

			var nodeOriginalData = node.get("data"),
				data = json.data[nodeOriginalData.id],
				uis = node.bindingUIs;

			if (data) {
				var priority = parseInt(data.priority),
					description = data.description;

				//设备关机
				if (data.error) {
					nodeOriginalData.error = data.error;
					resetAlarm(node, {
						description: null,
						color: null,
						padding: 0
					});
					//连线红色 设备灰色
					resetShutdown(node, data.error);
				} else {
					nodeOriginalData.error = false;
					resetShutdown(node, nodeOriginalData.error);
					if (priority) {
						var color = priority >= 3 ? REDCOLOR : YELLOWCOLOR;

						resetAlarm(node, {
							description: description,
							color: color,
							padding: 3
						});
					} else {

						resetAlarm(node, {
							description: null,
							color: null,
							padding: 0
						});
					}
				}

			}
		});

	}
};