CompactionGraph = function(param1, param2, param3, param4) {
	var _loc_5 = null;
	param1.check();
	this._initialGraph = param1;
	this._dir = param4;
	this.graph = new OGraph(null);
	var _loc_6 = 0;
	var _loc_7 = param1.nodes;
	for (var i = 0; i < _loc_7.length; i++) {
		_loc_5 = _loc_7[i];
		_loc_5.visited = false;
	}
	this.insertPathVertices(param1);
	this.insertBasicArcs(param1);
	param1.numOfEdges = param1.edges.length;
	param1.numOfNodes = param1.nodes.length;
};

CompactionGraph.prototype = {
	insertPathVertices : function(param1) {
		var _loc_6 = undefined;
		var _loc_2 = null;
		var _loc_3 = null;
		param1.check();
		var _loc_4 = 0;
		var _loc_5 = param1.nodes;
		
		for (var i = 0; i < _loc_5.length; i++) {
			_loc_6 = _loc_5[i];
			_loc_2 = _loc_6;
			if (_loc_2.visited) {
				break;
			}
			_loc_3 = new ONode();
			
			this.graph.nodes.push(_loc_3);
			this.dfsInsertPathVertex(_loc_2, _loc_3);
			if (_loc_3.path.length == 2) {
			}
			if (_loc_3.pathToEdge) {
				break;
			}
			var _loc_9 = null;
			_loc_6 = null;
			_loc_3.pathToEdge = _loc_9;
		}
		return;
	}// end function
	,
	dfsInsertPathVertex : function(param1, param2) {
		var _loc_6 = undefined;
		var _loc_8 = undefined;
		var _loc_9 = undefined;
		var _loc_10 = undefined;
		var _loc_3 = null;
		var _loc_4 = null;
		var _loc_5 = null;
		var _loc_11 = true;
		_loc_6 = true;
		param1.visited = _loc_11;
		param2.path.unshift(param1);
		var _loc_11 = param2;
		_loc_6 = param2;
		
		param1.pathNode = _loc_11;
		_loc_6 = 0;
		var _loc_7 = param1.edges;
		for (var i = 0; i < _loc_7.length; i++) {
			_loc_8 = _loc_7[i];
			_loc_4 = _loc_8;
			_loc_8 = 0;
			_loc_9 = [_loc_4.sourceAdj, _loc_4.targetAdj];
			for (var k = 0; k < _loc_9.length; k++) {
				_loc_10 = _loc_9[k];
				_loc_3 = _loc_10;
				if (_loc_3.direction != this._dir) {
				}
				
				if (_loc_3.direction == new OrthoRep()
						.getOppositeDirection(this._dir)) {
					break;
				}
				if (!param2.pathToEdge) {
					var _loc_15 = _loc_3.edge;
					_loc_10 = _loc_3.edge;
					param2.pathToEdge = _loc_15;
				}
				_loc_5 = _loc_3.edge.source;
				if (_loc_5 == param1) {
					var _loc_15 = _loc_3.edge.target;
					_loc_10 = _loc_3.edge.target;
					_loc_5 = _loc_15;
				}
				if (_loc_5.visited) {
					break;
				}
				this.dfsInsertPathVertex(_loc_5, param2);
			}
		}
		return;
	}// end function
	,
	insertVertexSizeArcs : function() {
		var _loc_1 = 0;
		var _loc_2 = 0;
		var _loc_3 = null;
		var _loc_4 = 0;
		var _loc_5 = this._initialGraph.nodes;
		// for (_loc_3 in _loc_5)
		// {
		//                
		// }
		return;
	}// end function
	,
	insertBasicArcs:  function (param1) 
        {
            var _loc_7 = undefined;
            var _loc_8 = undefined;
            var _loc_9 = undefined;
            var _loc_2 = null;
            var _loc_3 = null;
            var _loc_4 = null;
            var _loc_5 = 0;
            var _loc_6 = param1.faces;
            for  (var i=0;i<_loc_6.length;i++)
            {
                _loc_7 = _loc_6[i];
                _loc_3 = _loc_7;
                _loc_7 = 0;
                _loc_8 = _loc_3.adjacencies;
                for  (var k=0;k<_loc_8.length;k++)
                {
                    _loc_9 = _loc_8[k];
                    _loc_2 = _loc_9;
                    var _loc_14= false;
                    _loc_9 = false;
                    _loc_2.visited = _loc_14;
                }
            }
            _loc_5 = 0;
            _loc_6 = param1.faces;
            for  (var j=0;j<_loc_6.length;j++)
            {
                _loc_7 = _loc_6[j];
                _loc_3 = _loc_7;
                _loc_7 = 0;
                _loc_8 = _loc_3.adjacencies;
                for  (_loc_9 in _loc_8)
                {
                    
                    _loc_2 = _loc_9;
                    if (_loc_2.direction == this._dir)
                    {
                    }
                    if (!_loc_2.visited)
                    {
                        _loc_4 = new OEdge();
                        this.graph.edges.push(_loc_4);
                        var _loc_14 = _loc_2.node.pathNode;
                        _loc_9 = _loc_2.node.pathNode;
                        _loc_4.source = _loc_14;
                        var _loc_14 = _loc_2.twinNode.pathNode;
                        _loc_9 = _loc_2.twinNode.pathNode;
                        _loc_4.target = _loc_14;
                        _loc_4.source.edges.push(_loc_4);
                        _loc_4.target.edges.push(_loc_4);
                        if (_loc_4.target.adjacentNodes.indexOf(_loc_4.source) == -1)
                        {
                            _loc_4.target.adjacentNodes.push(_loc_4.source);
                        }
                        if (_loc_4.source.adjacentNodes.indexOf(_loc_4.target) == -1)
                        {
                            _loc_4.source.adjacentNodes.push(_loc_4.target);
                        }
                        var _loc_14 = _loc_4;
                        _loc_9 = _loc_4;
                        _loc_2.basicArc = _loc_14;
                        var _loc_14 = 1;
                        _loc_9 = 1;
                        _loc_4.cost = _loc_14;
                        continue;
                    }
                    if (_loc_2.direction != 4)
                    {
                        break;
                    }
//                    throw new Error("Undefined Direction");
                }
            }
            return;
        }// end function

};