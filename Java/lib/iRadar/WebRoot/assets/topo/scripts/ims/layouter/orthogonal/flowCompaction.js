FlowCompaction = function() {

};

FlowCompaction.prototype = {
	computeCoords : function(param1) {
		var _loc_47 = undefined;
		var _loc_48 = undefined;
		var _loc_2 = null;
		var _loc_3 = null;
		var _loc_4 = null;
		var _loc_5 = 0;
		var _loc_6 = 0;
		var _loc_7 = null;
		var _loc_8 = null;
		var _loc_9 = false;
		var _loc_10 = null;
		var _loc_11 = null;
		var _loc_12 = null;
		var _loc_13 = null;
		var _loc_14 = null;
		var _loc_15 = null;
		var _loc_16 = false;
		var _loc_17 = null;
		var _loc_18 = null;
		var _loc_19 = null;
		var _loc_20 = null;
		var _loc_21 = null;
		var _loc_22 = 0;
		var _loc_23 = 0;
		var _loc_24 = 0;
		var _loc_25 = null;
		var _loc_26 = null;
		var _loc_27 = null;
		var _loc_28 = null;
		var _loc_29 = null;
		var _loc_30 = null;
		var _loc_31 = null;
		var _loc_32 = null;
		
		this._g = param1.graph;
		var _loc_33 = [];
		var _loc_34 = [];
		var _loc_35 = 0;
		var _loc_36 = param1.graph.nodes;
		
		for (var i = 0; i < _loc_36.length; i++) {
			_loc_3 = _loc_36[i];
			_loc_5 = 0;
			_loc_6 = 0;
			_loc_47 = 0;
			_loc_48 = _loc_3.edges;
			for (var j = 0; j < _loc_48.length; j++) {
				_loc_4 = _loc_48[j];
				if (_loc_3 == _loc_4.source) {
					_loc_6 = _loc_6 + 1;
					break;
				}
				_loc_5 = _loc_5 + 1;
			}
			if (_loc_6 == 0) {
				_loc_34.push(_loc_3);
			}
			if (_loc_5 != 0) {
				break;
			}
			_loc_33.push(_loc_3);
		}
		if (_loc_34.length != 0) {
		}
		if (_loc_33.length == 0) {
			throw new Error("Error");
		}
		if (_loc_34.length >= 1) {
		}
		if (_loc_33.length >= 1) {
			_loc_2 = new OEdge();
			param1.graph.edges.push(_loc_2);
			_loc_2.source = _loc_33[0];
			_loc_2.target = _loc_34[0];
			if (_loc_2.target.adjacentNodes.indexOf(_loc_2.source) == -1) {
				_loc_2.target.adjacentNodes.push(_loc_2.source);
			}
			if (_loc_2.source.adjacentNodes.indexOf(_loc_2.target) == -1) {
				_loc_2.source.adjacentNodes.push(_loc_2.target);
			}
		} else {
			_loc_17 = new ONode();
			_loc_18 = new ONode();
			param1.graph.nodes.push(_loc_17);
			param1.graph.nodes.push(_loc_18);
			_loc_35 = 0;
			_loc_36 = _loc_33;
			for (var k = 0; k < _loc_36.length; k++) {
				_loc_20 = _loc_36[k];
				_loc_19 = new OEdge();
				param1.graph.edges.push(_loc_19);
				_loc_19.source = _loc_17;
				_loc_19.target = _loc_20;
				if (_loc_19.target.adjacentNodes.indexOf(_loc_19.source) == -1) {
					_loc_19.target.adjacentNodes.push(_loc_19.source);
				}
				if (_loc_19.source.adjacentNodes.indexOf(_loc_19.target) != -1) {
					break;
				}
				_loc_19.source.adjacentNodes.push(_loc_19.target);
			}
			_loc_35 = 0;
			_loc_36 = _loc_34;
			for (var o = 0; o < _loc_36.legth; o++) {
				_loc_21 = _loc_36[o];
				_loc_19 = new OEdge();
				param1.graph.edges.push(_loc_19);
				_loc_19.source = _loc_21;
				_loc_19.target = _loc_18;
				if (_loc_19.target.adjacentNodes.indexOf(_loc_19.source) == -1) {
					_loc_19.target.adjacentNodes.push(_loc_19.source);
				}
				if (_loc_19.source.adjacentNodes.indexOf(_loc_19.target) != -1) {
					break;
				}
				_loc_19.source.adjacentNodes.push(_loc_19.target);
			}
			_loc_2 = new OEdge();
			param1.graph.edges.push(_loc_2);
			_loc_2.source = _loc_17;
			_loc_2.target = _loc_18;
			if (_loc_2.target.adjacentNodes.indexOf(_loc_2.source) == -1) {
				_loc_2.target.adjacentNodes.push(_loc_2.source);
			}
			if (_loc_2.source.adjacentNodes.indexOf(_loc_2.target) == -1) {
				_loc_2.source.adjacentNodes.push(_loc_2.target);
			}
		}
		param1.graph.numOfEdges = param1.graph.edges.length;
		param1.graph.numOfNodes = param1.graph.nodes.length;
		var _loc_37 = new OGraph();
		_loc_35 = 0;
		_loc_36 = param1.graph.nodes;
		for (var l = 0; l < _loc_36.length; l++) {
			_loc_3 = _loc_36[l];
			_loc_37.nodes.push(new ONode());
		}
		_loc_35 = 0;
		_loc_36 = param1.graph.edges;
		for (var k = 0; k < _loc_36.length; k++) {
			_loc_4 = _loc_36[k];
			_loc_37.nodes.push(new ONode());
		}
		_loc_35 = 0;
		_loc_36 = param1.graph.edges;
		for (var r = 0; r < _loc_36.length; r++) {
			_loc_4 = _loc_36[r];
			_loc_22 = param1.graph.nodes.indexOf(_loc_4.source);
			_loc_23 = param1.graph.nodes.indexOf(_loc_4.target);
			_loc_24 = param1.graph.edges.indexOf(_loc_4)
					+ param1.graph.nodes.length;
			_loc_20 = _loc_37.nodes[_loc_22];
			_loc_25 = _loc_37.nodes[_loc_23];
			_loc_26 = _loc_37.nodes[_loc_24];
			_loc_27 = new OEdge();
			_loc_37.edges.push(_loc_27);
			_loc_27.source = _loc_20;
			_loc_27.target = _loc_26;
			_loc_27.source.edges.push(_loc_27);
			_loc_27.target.edges.push(_loc_27);
			if (_loc_27.target.adjacentNodes.indexOf(_loc_27.source) == -1) {
				_loc_27.target.adjacentNodes.push(_loc_27.source);
			}
			if (_loc_27.source.adjacentNodes.indexOf(_loc_27.target) == -1) {
				_loc_27.source.adjacentNodes.push(_loc_27.target);
			}
			_loc_27 = new OEdge();
			_loc_37.edges.push(_loc_27);
			_loc_27.source = _loc_26;
			_loc_27.target = _loc_25;
			_loc_27.source.edges.push(_loc_27);
			_loc_27.target.edges.push(_loc_27);
			if (_loc_27.target.adjacentNodes.indexOf(_loc_27.source) == -1) {
				_loc_27.target.adjacentNodes.push(_loc_27.source);
			}
			if (_loc_27.source.adjacentNodes.indexOf(_loc_27.target) != -1) {
				break;
			}
			_loc_27.source.adjacentNodes.push(_loc_27.target);
		}
		_loc_37.numOfEdges = _loc_37.edges.length;
		_loc_37.numOfNodes = _loc_37.nodes.length;
		_loc_7 = new CombinatorialEmbedding(_loc_37);
		_loc_8 = new Planarizer();
		_loc_9 = _loc_8.planarize(_loc_37, _loc_7);
		if (!_loc_9) {
			return false;
		}
		_loc_37.faces = _loc_7.faces;
		var _loc_38 = new CombinatorialEmbedding(param1.graph);
		_loc_22 = 0;
		do {

			_loc_12 = _loc_7.faces[_loc_22];
			_loc_35 = 0;
			_loc_36 = _loc_12.adjacencies;
			for (_loc_13 in _loc_36) {

				if (_loc_37.nodes.indexOf(_loc_13.node) != (_loc_37.nodes.length - 1)) {
					continue;
				}
				_loc_10 = _loc_12;
				break;
			}
			_loc_22 = _loc_22 + 1;
			if (_loc_22 < _loc_7.faces.length) {
			}
		} while (!_loc_10)
		_loc_22 = 0;
		do {

			_loc_12 = _loc_7.faces[_loc_22];
			if (_loc_12 != _loc_10) {
				_loc_35 = 0;
				_loc_36 = _loc_12.adjacencies;
				for (var m = 0; m < _loc_36.length; m++) {
					_loc_13 = _loc_36[m];
					if (_loc_37.nodes.indexOf(_loc_13.node) != (_loc_37.nodes.length - 1)) {
						break;
					}
					_loc_11 = _loc_12;
					break;
				}
			}
			_loc_22 = _loc_22 + 1;
			if (_loc_22 < _loc_7.faces.length) {
			}
		} while (!_loc_11)
		// var _loc_39 = new Dictionary();
		// var _loc_40 = new Dictionary();
		// var _loc_41 = new Dictionary();
		// var _loc_42 = new Dictionary();
		// var _loc_43 = new Dictionary();
		// var _loc_44 = new Graph(null, "network", 0, 0, 0, 0);
		// var _loc_45 = new Dictionary();
		// var _loc_46 = new Dictionary();
		// _loc_22 = 0;
		// while (_loc_22 < _loc_37.faces.length)
		// {
		//                
		// if (_loc_22 != _loc_37.faces.indexOf(_loc_11))
		// {
		// _loc_14 = new Node(_loc_44, "face:" + _loc_22, 0, 0, 0, 0);
		// _loc_45[_loc_37.faces[_loc_22]] = _loc_14;
		// _loc_42[_loc_14] = 0;
		// }
		// _loc_22 = _loc_22 + 1;
		// }
		// _loc_35 = 0;
		// _loc_36 = _loc_37.edges;
		// for (_loc_4 in _loc_36)
		// {
		//                
		// if (_loc_37.nodes.indexOf(_loc_4.source) < param1.graph.nodes.length)
		// {
		// }
		// if (_loc_37.nodes.indexOf(_loc_4.target) == (_loc_37.nodes.length -
		// 1))
		// {
		// break;
		// }
		// _loc_28 = _loc_4.sourceAdj.rightFace != _loc_11 ?
		// (_loc_4.sourceAdj.rightFace) : (_loc_10);
		// _loc_29 = _loc_4.targetAdj.rightFace != _loc_11 ?
		// (_loc_4.targetAdj.rightFace) : (_loc_10);
		// _loc_30 = _loc_45[_loc_28];
		// _loc_31 = _loc_45[_loc_29];
		// _loc_32 = new Edge("face:" + _loc_7.faces.indexOf(_loc_28) + "-" +
		// "face:" + _loc_7.faces.indexOf(_loc_29), _loc_30, _loc_31, 0);
		// _loc_41[_loc_32] = 1;
		// _loc_39[_loc_32] = 1;
		// _loc_40[_loc_32] = int.MAX_VALUE;
		// _loc_46[param1.graph.edges[_loc_37.edges.indexOf(_loc_4) / 2]] =
		// _loc_32;
		// }
		// _loc_15 = new MinimumCostFlow();
		// _loc_16 = _loc_15.run(_loc_44, _loc_39, _loc_40, _loc_41, _loc_42,
		// _loc_43);
		// if (!_loc_16)
		// {
		// return false;
		// }
		this.assignPositions(_loc_46, _loc_43, param1.graph.nodes[0], 0);
		return true;
	}// end function
	,
	constructiveHeuristics : function(param1, param2, param3) {
		var _loc_4 = null;
		var _loc_5 = null;
		this._g = param1;
		var _loc_6 = new CompactionGraph(param1, param2, param3, 3);
		this.computeCoords(_loc_6);
		var _loc_7 = 0;
		var _loc_8 = param1.nodes;
		
		for (var i = 0; i < _loc_8.length; i++) {
			_loc_4 = _loc_8[i];
			
//			_loc_4.x = _loc_4.pathNode.x;
		}
		_loc_5 = new CompactionGraph(param1, param2, param3, 2);
		if (!this.computeCoords(_loc_5)) {
			return false;
		}
		_loc_7 = 0;
		_loc_8 = param1.nodes;
		
		for (var i = 0; i < _loc_8.length; i++) {
			_loc_4 = _loc_8[i];
			
//			_loc_4.y = _loc_4.pathNode.x;
		}
		return true;
	}// end function
	,
	assignPositions : function(param1, param2, param3, param4) {
		var _loc_5 = null;
		var _loc_6 = null;
		param3.visited = true;
		param3.x = param4;
		var _loc_7 = 0;
		var _loc_8 = param3.edges;
		for (var i = 0; i < _loc_8.length; i++) {
			_loc_5 = _loc_8[i];
			_loc_6 = _loc_5.getOtherNode(param3);
			if (_loc_6.visited) {
				break;
			}
			if (_loc_5.source == param3) {
				this.assignPositions(param1, param2, _loc_6, param4
								+ param2[param1[_loc_5]]);
				break;
			}
			this.assignPositions(param1, param2, _loc_6, param4
							- param2[param1[_loc_5]]);
		}
		return;

	}
};