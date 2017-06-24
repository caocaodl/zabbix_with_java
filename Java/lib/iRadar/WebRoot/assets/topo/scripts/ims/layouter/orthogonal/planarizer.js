Planarizer = function() {
	this._adjacencies = new Array();
	this._graph;
	this.faces = new Array();
	this._faces = new Array();
	this.adj = new Array();
};

Planarizer.prototype = {
	planarize : function(param1, param2) {
		
		// this.copyFromGraph(param1);
		// var _loc_3 = new Embedder(this._graph).run();
		// if (_loc_3)
		// {
		// this.createAdjacencyList(param1, this._graph);
		// this.createEmbedding(param1, this._graph);
		this.computeAdjacencies(param1, this._graph);
//		this.computeFaces();
		param2.faces = this._faces;
		param2.adjacencies = this._adjacencies;
		
		param1.faces = this._faces;
		// }
//		return _loc_3;
	}// end function
	,
	computeAdjacencies:function (param1, param2) 
        {
            var _loc_3 = null;
            var _loc_4 = null;
            var _loc_5 = null;
            var _loc_6 = null;
            var _loc_7 = null;
            var _loc_8 = 0;
            var _loc_9 = 0;
            var _loc_10 = 0;
            var _loc_11 = null;
            this._adjacencies = new Array();
//            var _loc_12 = new Dictionary();
//            var _loc_13 = new Dictionary();
            var _loc_14 = 0;
            var _loc_15 = param1.edges;
          
            for  (var i=0;i<_loc_15.length;i++)
            {
                _loc_5 = _loc_15[i];
                _loc_3 = new Adjacency(_loc_5.source, _loc_5);
                _loc_4 = new Adjacency(_loc_5.target, _loc_5);
                _loc_5.sourceAdj = _loc_3;
                _loc_5.targetAdj = _loc_4;
//                _loc_12[_loc_5] = _loc_3;
//                _loc_13[_loc_5] = _loc_4;
                _loc_3.twin = _loc_4;
                _loc_4.twin = _loc_3;
                this._adjacencies.push(_loc_3);
                this._adjacencies.push(_loc_4);
               
            }
             
//            _loc_14 = 0;
//            _loc_15 = this._adjacencies;
//            for  (_loc_3 in _loc_15)
//            {
//                
//                _loc_6 = _loc_3.node;
//                _loc_7 = _loc_3.edge.getOtherNode(_loc_6);
//                _loc_8 = param1.nodes.indexOf(_loc_7);
//                _loc_9 = param1.nodes.indexOf(_loc_6);
//                _loc_10 = this.adj[_loc_8].indexOf(_loc_9) + 1;
//                if (_loc_10 == this.adj[_loc_8].length)
//                {
//                    _loc_10 = 0;
//                }
//                _loc_11 = param1.getEdge(_loc_7, param1.nodes[this.adj[_loc_8][_loc_10]]);
//                if (_loc_12[_loc_11].node == _loc_7)
//                {
//                    _loc_3.next = _loc_12[_loc_11];
//                    continue;
//                }
//                if (_loc_13[_loc_11].node == _loc_7)
//                {
//                    _loc_3.next = _loc_13[_loc_11];
//                    continue;
//                }
//                throw new Error("ERROR IN EMBEDDING");
//            }
            return;
        }// end function

};