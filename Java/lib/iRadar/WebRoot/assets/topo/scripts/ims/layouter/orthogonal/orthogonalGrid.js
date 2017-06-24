OrthogonalGrid = function(graph, orginGraph, emb, d) {

	this.minY = 2147483647;
	this.minX = 2147483647;
	this.maxX = -2.14748e+009;
	this.maxY = -2.14748e+009;

	this.grid = [];
	this._graph = graph;

	this._ograph = orginGraph;
	this._emb = emb;
	this._d = d;
	this.nodeWidth = 0;
	this.nodeHeight = 0;
	this.init();
	this.correct();
	this.setCellsPosition();

};

OrthogonalGrid.prototype = {
	init : function() {
		var node = null;
		var nodeX = 0;
		var nodeY = 0;
		
		
		var nodes = this._ograph.nodes;
		// 获取节点长宽
		if (nodes.length > 0) {
			this.nodeWidth = nodes[0].width;
			this.nodeHeight = nodes[0].height;
			
		}
		//计算节点坐标范围
		for (var i = 0; i < nodes.length; i++) {
			node = nodes[i];
			this.minX = Math.min(this.minX, parseInt(node.x));
			this.minY = Math.min(this.minY, parseInt(node.y));
			this.maxX = Math.max(this.maxX, parseInt(node.x));
			this.maxY = Math.max(this.maxY, parseInt(node.y));
		}
		
		
		// 最小X距离
		nodeX = this.minX;

		//从min至max生成格子
		while (nodeX <= this.maxX) {
			this.grid[nodeX] = [];
			nodeY = this.minY;
			while (nodeY <= this.maxY) {
				this.grid[nodeX][nodeY] = new OrthogonalGridCell(nodeX, nodeY);
				nodeY = nodeY + 1;
			}
			nodeX = nodeX + 1;
		}
		
		nodes = this._ograph.nodes;

		//在格子上设备节点
		for (var j = 0; j < nodes.length; j++) {
			node = nodes[j];
			node.x = parseInt(node.x);
			node.y = parseInt(node.y);
			
			var cell = this.cell(node.x, node.y);
			if (cell.onode) {
				cell.onode = node;
			}

			// 存储node
			cell.node = node.originalNode;
		}

		return;
	}// end function
	,
	setCellsPosition : function() {

		var _loc_1 = 0;
		var _loc_2 = [];
		var _loc_3 = [];
		var _loc_4 = this.minX;
		// 最小X轴
		// while (_loc_4 <= this.maxX) {
		//
		// _loc_1 = this.minY;
		// while (_loc_1 <= this.maxY) {
		//                    
		// // 判断单元格是否存在值
		// if (this.cell(_loc_4, _loc_1).node) {

		// if (!_loc_2[_loc_4]) {
		// _loc_2[_loc_4] = -Number.MAX_VALUE;
		// }
		// if (!_loc_3[_loc_1]) {
		// _loc_3[_loc_1] = -Number.MAX_VALUE;
		// }
		// //存储单元格宽度
		// _loc_2[_loc_4] = Math.max(_loc_2[_loc_4], this.cell(_loc_4,
		// _loc_1).node.w);
		//									
		// //存储单元格高度
		// _loc_3[_loc_1] = Math.max(_loc_3[_loc_1], this.cell(_loc_4,
		// _loc_1).node.h);
		// }
		// _loc_1 = _loc_1 + 1;
		// }
		//			
		//			
		// _loc_4 = _loc_4 + 1;
		//			
		// }
		_loc_4 = this.minX;
		var nodeCount=0;
		while (_loc_4 <= this.maxX) {

			_loc_1 = this.minY;
			//循环Y轴
			while (_loc_1 <= this.maxY) {

				// if (isNaN(_loc_2[_loc_4])) {
				// _loc_2[_loc_4] = 0;
				// }
				//				
				// if (isNaN(_loc_3[_loc_1])) {
				//
				// _loc_3[_loc_1] = 0;
				// }

				if (_loc_4 != this.minX) {

					// this.cell(_loc_4, _loc_1).x = this.cell((_loc_4 -
					// 1),_loc_1).x+ _loc_2[(_loc_4 - 1)]/ 2+ _loc_2[_loc_4]/ 2
					// + this._d;
					this.cell(_loc_4, _loc_1).x = parseInt(_loc_4
							+ this.nodeWidth / 2 + this.nodeWidth / 2
							+ this._d);

				} else {

					this.cell(_loc_4, _loc_1).x = parseInt(this.nodeWidth / 2
							+ 50);

				}

				if (_loc_1 != this.minY) {

					// this.cell(_loc_4, _loc_1).y = this.cell(_loc_4,(_loc_1 -
					// 1)).y+ _loc_3[(_loc_1 - 1)]/ 2+ _loc_3[_loc_1]/ 2+
					// this._d;

//					this.cell(_loc_4, _loc_1).y = parseInt(_loc_1
//							+ this.nodeHeight / 2 + this.nodeHeight / 2
//							+ this._d);
					
					this.cell(_loc_4, _loc_1).y = parseInt(_loc_1
							+ this.nodeHeight / 2 + this.nodeHeight / 2
							+ this._d);
				} else {

					this.cell(_loc_4, _loc_1).y = parseInt(this.nodeHeight / 2
							+ 50);
				}
				if (this.cell(_loc_4, _loc_1).node) {
                          nodeCount+=1;
					var node = this.cell(_loc_4, _loc_1).node;

					node.x = this.cell(_loc_4, _loc_1).x;
//					node.y = this.cell(_loc_4, _loc_1).y;					
					node.y = nodeCount*this.nodeHeight;

				}
				_loc_1 = _loc_1 + 1;
			}
			_loc_4 = _loc_4 + 1;
		}

		return;
	},
	// 修正定位
	correct : function() {
		var _loc_1 = null;
		var _loc_2 = null;
		var _loc_3 = null;
		var _loc_4 = null;
		var _loc_5 = 0;

		var _loc_6 = this._emb.adjacencies;

		for (var i = 0; i < _loc_6.length; i++) {
			_loc_1 = _loc_6[i];
			_loc_2 = _loc_1.node;

			// _loc_3 = _loc_1.next.node;
			// _loc_4 = _loc_1.prev.node;

			if (!_loc_1.node) {
				continue;
			}
			if (_loc_1.node.y == _loc_1.twinNode().y) {
				continue;
			}

			// if (_loc_1.direction == 1) {
			// this.grid[_loc_1.twinNode().x][_loc_1.node.y] = this.grid[_loc_1
			// .twinNode().x][_loc_1.twinNode().y];
			//
			// this.grid[_loc_1.twinNode().x][_loc_1.twinNode().y] = new
			// OrthogonalGridCell(
			// _loc_2.x, _loc_2.y);
			// _loc_1.twinNode().y = _loc_1.node.y;
			// break;
			// }
			if (this.grid[_loc_1.node.x][_loc_1.twinNode().y].node) {
				this.grid[_loc_1.node.x][_loc_1.twinNode().y - 1] = this.grid[_loc_1
						.twinNode().x][_loc_1.twinNode().y];

			} else {
				this.grid[_loc_1.node.x][_loc_1.twinNode().y] = this.grid[_loc_1
						.twinNode().x][_loc_1.twinNode().y];
			}
			// this.grid[_loc_1.node.x][_loc_1.twinNode().y] = this.grid[_loc_1
			// .twinNode().x][_loc_1.twinNode().y];

			this.grid[_loc_1.twinNode().x][_loc_1.twinNode().y] = new OrthogonalGridCell(
					_loc_2.x, _loc_2.y);

			// _loc_1.twinNode().x = _loc_1.node.x;
		}
		return;
	}// end function
	,
	cell : function(nodeX, nodeY) {
		return this.grid[nodeX][nodeY];
	}// end function
	,
	correctHighDegreeEdges : function(param1) {

		var _loc_7 = undefined;
		var _loc_8 = undefined;
		var _loc_2 = null;
		var _loc_3 = null;
		var _loc_4 = [];
		var _loc_5 = 0;
		var _loc_6 = this._graph;

		for (var i = 0; i < _loc_6.length; i++) {
			_loc_3 = _loc_6[i];
			// if (_loc_3.inEdges.length + _loc_3.outEdges.length <= 4) {
			// continue;
			// }
			_loc_4.push(_loc_3);
			_loc_7 = 0;
			_loc_8 = _loc_3.outgoingConnections;
			for (var k = 0; k < _loc_8.length; k++) {
				_loc_2 = _loc_8[k];
				// if (_loc_2.path.length <= 2) {
				// continue;
				// }
				this.correctEdge(_loc_2, 0, 1, 2, param1);
			}
			_loc_7 = 0;
			_loc_8 = _loc_3.incomingConnections;
			for (var o = 0; o < _loc_8.length; o++) {
				_loc_2 = _loc_8[o];
				// if (_loc_2.path.length <= 2) {
				// continue;
				// }
				this.correctEdge(_loc_2, (_loc_2.path.length - 1),
						_loc_2.path.length - 2, _loc_2.path.length - 3, param1);
			}
		}
		// if (!param1)
		// {
		// _loc_5 = 0;
		// _loc_6 = _loc_4;
		// for (_loc_3 in _loc_6)
		// {
		//                    
		// this.repositionEdges(_loc_3);
		// }
		// }
		return;
	}// end function
	,
	correctEdge : function(param1, param2, param3, param4, param5) {
		if (param5) {
			return;
		}
		var _loc_6 = param1.path[param2];
		var _loc_7 = param1.path[param3];
		var _loc_8 = param1.path[param4];
		if (_loc_7.y == _loc_8.y) {
		}
		if (_loc_7.x != _loc_6.x) {
			if ((_loc_6.x - _loc_7.x) * (_loc_7.x - _loc_8.x) > 0) {
				_loc_7.x = _loc_6.x;
			} else {
				param1.path.splice((Math.min(param2, param3) + 1), 0,
						new Point(_loc_7.x, _loc_6.y));
			}
		}
		if (_loc_7.x == _loc_8.x) {
		}
		if (_loc_7.y != _loc_6.y) {
			if ((_loc_6.y - _loc_7.y) * (_loc_7.y - _loc_8.y) > 0) {
				_loc_7.y = _loc_6.y;
			} else {
				param1.path.splice((Math.min(param2, param3) + 1), 0,
						new Point(_loc_6.x, _loc_7.y));
			}
		}
		return;
	}// end function
};