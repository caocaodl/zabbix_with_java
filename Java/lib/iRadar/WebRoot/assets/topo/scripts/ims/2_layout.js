/**
 * 页面布局
 * 
 * @param {}
 *            editor
 */
Layout = function(editor, type) {
	this.editor = editor;
	this.layoutType = type;
	this.processEditPart = editor.getGraphicalViewer().getContents();
	this.processModel = this.processEditPart.getModel();
};
/**
 * 页面布局 属性
 * 
 * @type
 */
Layout.prototype = {

	doLayout : function() {
		var diagram = new Diagram();
		diagram.init(this.processModel);

		var sorter = new TopologicalSorter(diagram);
		// 获取排序后的节点
		var sortedElements = sorter.getSortedElements();

		var sortedIds = [];
		for (var i = 0; i < sortedElements.length; i++) {
			sortedIds.push(sortedElements[i].id);
		}
		var layouter = null;
		if (this.layoutType == "uptodown") {

			layouter = new UpToDownGridLayouter(diagram, sortedIds);
			
		} else if (this.layoutType == "lefttoright") {
			layouter = new LeftToRightGridLayouter(diagram, sortedIds);
			
			
		} else if (this.layoutType == "orthogonal") {
			// 总线型
			layouter = new OrthogonalLayout(diagram.getNodeArray());			
		}
		layouter.doLayout();
		var edgeMap = diagram.getEdgeMap();

			for (var edgeId in edgeMap) {
				var edge = edgeMap[edgeId];

				new EdgeLayouter(layouter.grid, edge, this.layoutType);
			}
			diagram.updateModel();

	}
};
/**
 * 图形类
 */
Diagram = function() {
};

Diagram.prototype = {
	getNodeArray : function() {
		var nodes = new Array();
		var nodeMap = this.getNodeMap();

		for (var edgeId in nodeMap) {
			var edge = nodeMap[edgeId];
			nodes.push(edge);
		}
		return nodes;
	},
	/**
	 * 获取节点集合
	 * 
	 * @return {}
	 */
	getNodeMap : function() {
		return this.nodeMap;
	},
	/**
	 * 获取连线集合
	 * 
	 * @return {}
	 */
	getEdgeMap : function() {
		return this.edgeMap;
	},
	/**
	 * 初始化加载 (将节点和连线存储到集合)
	 * 
	 * @param {}
	 *            process 流程模型
	 */
	init : function(process) {
		this.process = process;
		// 存储节点对象
		this.nodeMap = {};
		// 存储连接线对象
		this.edgeMap = {};

		for (var i = 0; i < this.process.children.length; i++) {
			var child = this.process.children[i];
			var node = new Node();
			node.id = child.text;
			node.type = child.type;
			node.x = child.x;
			node.y = child.y;
			node.w = child.w;
			node.h = child.h;

			this.nodeMap[node.id] = node;
		}

		for (var i = 0; i < this.process.children.length; i++) {
			var child = this.process.children[i];
			// 循环对外连线
			for (var j = 0; j < child.getOutgoingConnections().length; j++) {
				var connection = child.getOutgoingConnections()[j];
				this.createEdge(connection);
			}
		}
	},
	/**
	 * 存储连接线
	 * 
	 * @param {}
	 *            connection 连线对象
	 */
	createEdge : function(connection) {
		// 拼接连线ID
		var connectionId = connection.getSource().text + '_'
				+ connection.getTarget().text;

		var edge = this.edgeMap[connectionId];
		// 判断该连线是否已经存在
		if (!edge) {
			edge = new Edge();
			edge.id = connectionId;
			edge.name = connection.text;

			// 集合里获取源节点和目标节点
			edge.source = this.nodeMap[connection.getSource().text];
			edge.target = this.nodeMap[connection.getTarget().text];
			// 源节点存储 对外连线(节点连线主动连接出去)
			edge.source.outgoingLinks.push(edge);
			// 目标节点存储 (其它连线主动连接过来)
			edge.target.incomingLinks.push(edge);

			this.edgeMap[connectionId] = edge;
		}
	},

	updateModel : function() {
		for (var nodeId in this.nodeMap) {
			var item = this.nodeMap[nodeId];
			var model = this.getModel(nodeId);
            
			model.x = item.x;
			model.y = item.y;

			model.getEditPart().getFigure().x = model.x;
			model.getEditPart().getFigure().y = model.y;

			for (var i = 0; i < item.outgoingLinks.length; i++) {
				var connection = item.outgoingLinks[i];
				
				var connectionModel = this
						.getConnectionModel(model, connection);
				if (connectionModel == null) {
					continue;
				}

				connectionModel.innerPoints = typeof connection.innerPoints == 'undefined'
						? []
						: connection.innerPoints;
				// 连线文字坐标
				connectionModel.textX = 0;
				connectionModel.textY = 0;
				connectionModel.getEditPart().getFigure().innerPoints = connectionModel.innerPoints;
				connectionModel.getEditPart().getFigure().textX = connectionModel.textX;
				connectionModel.getEditPart().getFigure().textY = connectionModel.textY;
			}
		}
         
		this.process.getEditPart().refresh();
	},

	getModel : function(name) {
		var model = null;

		Gef.each(this.process.children, function(item) {
					if (item.text == name) {
						model = item;
						return false;
					}
				});

		return model;
	},

	getConnectionModel : function(nodeModel, edge) {
		var model = null;

		Gef.each(nodeModel.getOutgoingConnections(), function(item) {
					if (item.getTarget().text == edge.getTarget().id) {
						model = item;
						return false;
					}
				});

		return model;
	}
};

/**
 * 节点类
 */
Node = function() {
	// 外部连线(其它连线主动连接过来)集合
	this.incomingLinks = [];
	// 对外连线(节点连线主动连接出去)集合
	this.outgoingLinks = [];
};

Node.prototype = {
	/**
	 * 外部连线(其它连线主动连接过来)集合
	 * 
	 * @return {}
	 */
	getIncomingLinks : function() {
		return this.incomingLinks;
	},
	/**
	 * 对外连线(节点连线主动连接出去)集合
	 * 
	 * @return {}
	 */
	getOutgoingLinks : function() {
		return this.outgoingLinks;
	},
	/**
	 * 主动连接该节点的外部节点集合
	 * 
	 * @return {}
	 */
	getPrecedingElements : function() {
		var previousElements = [];
		for (var i = 0; i < this.incomingLinks.length; i++) {
			previousElements.push(this.incomingLinks[i].source);
		}
		return previousElements;
	},
	/**
	 * 对外连接（目标节点）集合
	 * 
	 * @return {}
	 */
	getFollowingElements : function() {
		var followingElements = [];
		for (var i = 0; i < this.outgoingLinks.length; i++) {
			followingElements.push(this.outgoingLinks[i].target);
		}
		return followingElements;
	},
	/**
	 * 查询是否存在连接过来的连线
	 * 
	 * @return {} Boolean
	 */
	isJoin : function() {

		return this.incomingLinks.length > 1;
	},
	/**
	 * 查询是否存在连接出去的连线
	 * 
	 * @return {} Boolean
	 */
	isSplit : function() {
		return this.outgoingLinks.length > 1;
	},

	prevSplit : function() {
		var distance = 1000;
		var candidateDistance = 0;
		var split = null;
		var candidate = null;

		var precedingElements = this.getPrecedingElements();
		for (var i = 0; i < precedingElements.length; i++) {
			var elem = precedingElements[i];
			if (elem.isSplit()) {
				return elem;
			}

			candidate = elem.prevSplit();
			if (this.isJoin()) {
				// if this is not a join, we have only one precedingElement.
				candidateDistance = elem.backwardDistanceTo(candidate);
			}
			if (candidateDistance < distance) {
				split = candidate;
				distance = candidateDistance;
			}
		}
		return split;
	},

	backwardDistanceTo : function(other) {
		return this._backwardDistanceTo(other, []);
	},

	_backwardDistanceTo : function(other, historyElements) {
		if (other == this) {
			return 0;
		}
		if (historyElements.indexOf(this) != -1) {
			return 1000;
		}
		var d = 1000;
		var newHistory = [];
		newHistory.push(this);
		var precedingElements = this.getPrecedingElements();
		for (var i = 0; i < precedingElements.length; i++) {
			var el = precedingElements[i];
			d = Math.min(d, el._backwardDistanceTo(other, newHistory));
		}
		return d == 1000 ? d : d + 1;
	}
};

/**
 * 连接线类
 */
Edge = function() {
	this.source = null;
	this.target = null;
};

Edge.prototype = {
	/**
	 * 源节点
	 * 
	 * @return {}
	 */
	getSource : function() {
		return this.source;
	},
	/**
	 * 目标节点
	 * 
	 * @return {}
	 */
	getTarget : function() {
		return this.target;
	},
	/**
	 * 交换源节点和目标节点
	 */
	reverseOutgoingAndIncoming : function() {

		var index = 0;

		var oldSource = this.source;
		var oldTarget = this.target;

		index = oldSource.outgoingLinks.indexOf(this);

		oldSource.outgoingLinks.splice(index, 1);

		index = oldTarget.incomingLinks.indexOf(this);
		oldTarget.incomingLinks.splice(index, 1);

		var newSource = oldTarget;
		var newTarget = oldSource;

		newSource.outgoingLinks.push(this);
		newTarget.incomingLinks.push(this);

		this.source = newSource;
		this.target = newTarget;

	}
};
/**
 * 拓扑结构分类解析
 * 
 * @param {}
 *            diagram
 */
TopologicalSorter = function(diagram) {
	this.diagram = diagram;
	this.prepareDataAndSort(true);
	this.prepareDataAndSort(false);
};

TopologicalSorter.prototype = {
	/**
	 * 获取排序后的节点
	 * 
	 * @return {}
	 */
	getSortedElements : function() {
		return this.sortedElements;
	},
	/**
	 * 
	 * @param {}
	 *            shouldBackpatch
	 */
	prepareDataAndSort : function(shouldBackpatch) {
		// 所有独立存在的节点(没有被其它节点连线连接过来，但该节点可以连接其它节点)集合
		this.sortedElements = [];
		// 节点集合
		this.elementsToSort = {};
		this.backwardsEdges = [];
		// 节点数量
		this.elementsToSortCount = 0;

		this.addAllChildren();
		this.topologicalSort();
		if (shouldBackpatch === true) {
			this.backpatchBackwardsEdges();
		}

		this.reverseBackwardsEdges();
	},
	/**
	 * 添加所有节点
	 */
	addAllChildren : function() {
		for (var nodeId in this.diagram.nodeMap) {
			var node = this.diagram.nodeMap[nodeId];
			this.elementsToSort[nodeId] = new SortableLayoutingElement(node);
			this.elementsToSortCount++;
		}
	},
	/**
	 * 拓扑逻辑排序
	 */
	topologicalSort : function() {
		var count = 0;
		var oldCount = 0;
		while (this.elementsToSortCount > 0) {

			var freeElements = this.getFreeElements();

			if (freeElements.length > 0) {

				for (var i = 0; i < freeElements.length; i++) {
					var freeElement = freeElements[i];
					// 存储排序节点
					this.sortedElements.push(freeElement.node);
					// 查询该节点的外连接节点
					this.freeElementsFrom(freeElement);
					delete this.elementsToSort[freeElement.node.id];
				}
			} else {
				// 循环结构
				var entry = this.getLoopEntryPoint();

				for (var i = 0; i < entry.incomingLinks.length; i++) {
					var backId = entry.incomingLinks[i];
					entry.reverseIncomingLinkFrom(backId);
					var elem = this.elementsToSort[backId];
					elem.reverseOutgoingLinkTo(entry.node.id);

					this.backwardsEdges.push(new BackwardsEdge(backId,
							entry.node.id));
				}
			}
		}
	},

	backpatchBackwardsEdges : function() {
		var newBackwardsEdges = [];
		for (var i = 0; i < this.backwardsEdges.length; i++) {
			newBackwardsEdges.push(this.backwardsEdges[i]);
		}
		for (var i = 0; i < this.backwardsEdges.length; i++) {
			var edge = this.backwardsEdges[i];
			var sourceId = edge.getSource();
			var targetId = edge.getTarget();

			var sourceElement = this.diagram.nodeMap[sourceId];

			while (!(sourceElement.isJoin() || sourceElement.isSplit())) {
				var newSourceElement = sourceElement.getPrecedingElements()[0];

				targetId = newSourceElement.id;
				newBackwardsEdges.push(new BackwardsEdge(targetId, sourceId));

				sourceElement = newSourceElement;
				sourceId = targetId;
			}
		}

		this.backwardsEdges = newBackwardsEdges;
	},

	reverseBackwardsEdges : function() {
		var edgeMap = this.diagram.edgeMap;
		for (var i = 0; i < this.backwardsEdges.length; i++) {
			var backwardsEdge = this.backwardsEdges[i];

			var sourceId = backwardsEdge.getSource();
			var targetId = backwardsEdge.getTarget();

			var sourceElement = this.diagram.nodeMap[sourceId];
			var targetElement = this.diagram.nodeMap[targetId];

			var edge = this.getEdge(edgeMap, sourceElement, targetElement);

			backwardsEdge.setEdge(edge);

			if (edge) {
				// reverse edge outgoing and incoming
				edge.reverseOutgoingAndIncoming();
			}
		}
	},
	/**
	 * 获取独立存在的节点(没有被其它节点连线连接过来，但该节点可以连接其它节点)
	 * 
	 * @return {}
	 */
	getFreeElements : function() {

		var freeElements = [];
		for (var nodeId in this.elementsToSort) {
			var elem = this.elementsToSort[nodeId];
			// 查询该节点是否存在外部接入过来的连线(如果不存在，证明是首节点)
			if (elem.isFree()) {
				freeElements.push(elem);
			}
		}
		return freeElements;
	},
	/**
	 * 查询该节点的对外连线
	 * 
	 * @param {}
	 *            freeElement
	 */
	freeElementsFrom : function(freeElement) {
		// 循环节点的对外连接节点集合
		for (var i = 0; i < freeElement.outgoingLinks.length; i++) {
			var id = freeElement.outgoingLinks[i];
			// 外连接节点（目标节点）
			var targetElement = this.elementsToSort[id];
			if (targetElement) {
				// 外连接（目标节点）删除和该节点的联系
				targetElement.removeIncomingLinkFrom(freeElement.node.id);
			}
		}
		this.elementsToSortCount--;
	},

	getLoopEntryPoint : function() {
		for (var nodeId in this.elementsToSort) {
			var candidate = this.elementsToSort[nodeId];
			if (candidate.oldInCount > 1
					&& candidate.oldInCount > candidate.incomingLinks.length) {
				return candidate;
			}
		}
		throw new Error('Could not find a valid loop entry point');
	},

	getEdge : function(edgeMap, sourceElement, targetElement) {
		for (var i = 0; i < sourceElement.outgoingLinks.length; i++) {
			var edge = sourceElement.outgoingLinks[i];
			if (edge.getTarget().id == targetElement.id) {
				return edge;
			}
		}
		return null;
	}
};
/**
 * 
 * @param {}
 *            node
 */
SortableLayoutingElement = function(node) {
	this.node = node;
	this.incomingLinks = [];
	this.outgoingLinks = [];

	// 循环所有接入该节点的外部节点
	for (var i = 0; i < node.incomingLinks.length; i++) {
		// 存储外部节点ID
		this.incomingLinks.push(node.incomingLinks[i].source.id);
	}
	// 循环该节点连接出去的所有节点
	for (var i = 0; i < node.outgoingLinks.length; i++) {
		this.outgoingLinks.push(node.outgoingLinks[i].target.id);
	}
	// 主动连接该节点的集合数量
	this.oldInCount = this.incomingLinks.length;

	this.isJoin = node.isJoin();
};

SortableLayoutingElement.prototype = {
	/**
	 * 查询该节点是否存在外部接入过来的连线
	 * 
	 * @return {} Boolean
	 */
	isFree : function() {
		return this.incomingLinks.length == 0;
	},
	/**
	 * 删除节点
	 * 
	 * @param {}
	 *            sourceId
	 */
	removeIncomingLinkFrom : function(sourceId) {
		var index = this.incomingLinks.indexOf(sourceId);
		this.incomingLinks.splice(index, 1);
	},

	reverseIncomingLinkFrom : function(id) {
		this.removeIncomingLinkFrom(id);
		this.outgoingLinks.push(id);
	},

	reverseOutgoingLinkTo : function(id) {
		var index = this.outgoingLinks.indexOf(id);
		this.outgoingLinks.splice(index, 1);
		this.incomingLinks.push(id);
	}
};

BackwardsEdge = function(source, target) {
	this.source = source;
	this.target = target;
};

BackwardsEdge.prototype = {
	getEdge : function() {
		return this.edge;
	},

	setEdge : function(edge) {
		this.edge = edge;
	},

	getSource : function() {
		return this.source;
	},

	getTarget : function() {
		return this.target;
	}
};