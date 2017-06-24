OrthogonalLayout = function(process) {
	this.process = process;

};

OrthogonalLayout.prototype = {
	doLayout : function() {

		this.performComputation();
	},
	// 计算
	performComputation : function() {
		var _loc_14 = undefined;
		var _loc_15 = undefined;
		var _loc_16 = undefined;
		var _loc_17 = undefined;
		var _loc_1 = null;
		var _loc_6 = null;
		var _loc_7 = null;
		var _loc_8 = null;
		var _loc_9 = null;
		var _loc_10 = null;
		
		//
//		if (graph.numberOfNodes < 2) {
//			if (enableMoveToCenter) {
//				moveToCenter();
//			}
//			if (!graph.isRootGraph) {
//				graph.fitToContent();
//			}
//			return;
//		}
		
		
		var oGraph = new OGraph(this.process);
//		if (oGraph.nodes.length == 2) {
//			_loc_7 = graph.nodes[0];
//			_loc_8 = graph.nodes[1];
//			_loc_7.x = 50 + _loc_7.width / 2;
//			_loc_7.y = 50 + Math.max(_loc_7.height, _loc_8.height) / 2;
//			_loc_8.y = 50 + Math.max(_loc_7.height, _loc_8.height) / 2;
//			_loc_8.x = _loc_7.x + _loc_7.width / 2 + this._nodesSpacing + _loc_8.width / 2;
//			_loc_14 = 0;
//			_loc_15 = _loc_7.outEdges;
//			foreach(_loc_9 in _loc_15)
//			{
//				_loc_9.path = [ new Point(_loc_7.x, _loc_7.y), new Point(_loc_8.x, _loc_8.y) ];
//			}
//			_loc_14 = 0;
//			_loc_15 = _loc_8.outEdges;
//			foreach(_loc_9 in _loc_15)
//			{
//				_loc_9.path = [ new Point(_loc_8.x, _loc_8.y), new Point(_loc_7.x, _loc_7.y) ];
//			}
//			return;
//		}

		var _loc_12 = new CombinatorialEmbedding(oGraph);
		var _loc_13 = new Planarizer().planarize(oGraph, _loc_12);

		_loc_12.expandHighDegreeVertices();

		oGraph.check();
		oGraph.faces = [];
		oGraph.numOfNodes = oGraph.nodes.length;
		oGraph.numOfEdges = oGraph.edges.length;
		_loc_14 = 0;
		var nodes = oGraph.nodes;
		for ( var i = 0; i < nodes.length; i++) {
			_loc_1 = nodes[i];
			_loc_1.adjacentNodes = [];
			_loc_16 = 0;
			_loc_17 = _loc_1.edges;
			for ( var j = 0; j < _loc_17.length; j++) {
				_loc_10 = _loc_17[j];
				_loc_1.adjacentNodes.push(_loc_10.getOtherNode(_loc_1));
			}
		}

		var cEmb = new CombinatorialEmbedding(oGraph);
		new Planarizer().planarize(oGraph, cEmb);
		var oRep = new OrthoRep(oGraph);
		var oShaper = new OrthoShaper();
		oShaper.run(oGraph, cEmb, oRep);
		oRep.normalize();
		oRep.dissect();
		oRep.orientate();
		cEmb.faces = oRep.faces;
		oGraph.faces = oRep.faces;
		var flowCompaction = new FlowCompaction();
		flowCompaction.constructiveHeuristics(oGraph, cEmb, oRep);
		
		
		// 表格布局
		_loc_6 = new OrthogonalGrid(this.process, oGraph, cEmb, 20);
//		 _loc_6.drawEdges(false);
//		_loc_6.checkEdgeDrawing();
//		_loc_6.correctHighDegreeEdges(false);
//		_loc_6.checkEdgeDrawing();
//		if (enableMoveToCenter) {
//			moveToCenter(true);
//		}
//		if (!graph.isRootGraph) {
//			graph.fitToContent();
//		}

		return;
	}
};