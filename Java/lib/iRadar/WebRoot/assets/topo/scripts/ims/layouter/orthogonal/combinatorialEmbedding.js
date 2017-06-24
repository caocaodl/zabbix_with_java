CombinatorialEmbedding = function(param1) {
	this.graph = param1;
	this.adjacencies = new Array();
	this.externalFace;
	this.faces = new Array();
};

CombinatorialEmbedding.prototype = {
	// 展开顶点
	expandHighDegreeVertices : function() {
		var _loc_1 = 0;
		var _loc_2 = null;
		var _loc_3 = null;
		var _loc_4 = null;
		var _loc_5 = null;
		var _loc_6 = 0;
		var _loc_7 = null;
		var _loc_8 = undefined;
		var _loc_9 = undefined;
		var _loc_10 = undefined;
		var _loc_11 = undefined;
		var _loc_12 = 0;

		// node节点
		var graphNodes = this.graph.nodes;
		for (var i = 0; i < graphNodes.length; i++) {
			var node = graphNodes[i];
			if (node.edges.length <= 4) {
				break;
			}
			
			node.highDegreeNode = node;
			// 存储具有4个以上外连接的节点
			var highNodes = [node];

			// 循环相邻的节点
			for (var j = 1; j < node.adjacentNodes.length; j++) {
				var nodeObj = new ONode();
				nodeObj.highDegreeNode = nodeObj;
				nodeObj.x = node.x;
				nodeObj.y = node.y;
				nodeObj.width = node.w;
				nodeObj.height = node.h;
				highNodes.push(nodeObj);

				this.graph.nodes.push(node);
			}
			var edges = [];
			// 循环所有顶点
			for (var k = 0; k < highNodes.length; k++) {
				var newEdgeObj = new OEdge();
				newEdgeObj.highDegreeNode = node;
				edges.push(newEdgeObj);
				this.graph.edges.push(newEdgeObj);
			}

			if (node.edges.length == 0) {
				return false;
			}
			var edgeObj = node.edges[0];

			for (var j = 0; j < edges.length; j++) {
				if (!edgeObj.sourceAdj) {
					return false;
				}
				
				if (edgeObj.sourceAdj.node != node) {
					if (edgeObj.targetAdj.node != node) {
						return false;
					}
					_loc_8 = edgeObj.targetAdj.prev.edge;
				} else {
					_loc_8 = edgeObj.sourceAdj.prev.edge;
				}
				
				if (_loc_6 > 0) {
					if (edgeObj.source != node) {
						if (edgeObj.target != node) {
							return false;
						}
						edgeObj.target = highNodes[_loc_6];
					} else {
						edgeObj.source = highNodes[_loc_6];
					}
					node.edges.splice(node.edges.indexOf(edgeObj), 1);
					highNodes[_loc_6].edges.push(edgeObj);
				}
				_loc_9 = highNodes[_loc_6];
				_loc_10 = highNodes[
				        _loc_6 != (highNodes.length - 1)
						? ((_loc_6 + 1))
						: (0)
					];
				_loc_11 = edges[_loc_6];
				_loc_11.source = _loc_10;
				_loc_10.edges.push(_loc_11);
				_loc_11.target = _loc_9;
				_loc_9.edges.push(_loc_11);
				_loc_11.sourceAdj = new Adjacency(_loc_11.source, _loc_11);
				_loc_11.targetAdj = new Adjacency(_loc_11.target, _loc_11);
				_loc_11.sourceAdj.twin = _loc_11.targetAdj;
				_loc_11.targetAdj.twin = _loc_11.sourceAdj;
				edgeObj = _loc_8;
			}
		}
		return this.check();
	}// end function
	,
	check : function() {
		var _loc_1 = null;
		var _loc_2 = null;
		var _loc_3 = 0;
		var _loc_4 = null;
		var _loc_5 = null;
		var _loc_6 = 0;
		var _loc_7 = this.adjacencies;
		for (var j = 0; j < _loc_7.length; j++) {
			_loc_2 = _loc_7[j];
			_loc_2.visited = false;
		}
		_loc_6 = 0;
		_loc_7 = this.faces;

		for (var i = 0; i < _loc_7.length; i++) {
			_loc_1 = _loc_7[i];
			_loc_3 = 0;
			_loc_4 = _loc_1.adjacencies[0];
			_loc_5 = _loc_4;
			do {

				if (_loc_5.visited) {
					return false;
				}
				_loc_5.visited = true;
				_loc_3 = _loc_3 + 1;
				_loc_5 = _loc_5.next;
			} while (_loc_4 != _loc_5)
			if (_loc_3 == _loc_1.size) {
				continue;
			}
			return false;
		}
		_loc_6 = 0;
		_loc_7 = this.adjacencies;
		for (var k = 0; k < _loc_7.length; k++) {
			_loc_2 = _loc_7[k];
			if (_loc_2.visited) {
				continue;
			}
			return false;
		}
		return true;
	}// end function
};