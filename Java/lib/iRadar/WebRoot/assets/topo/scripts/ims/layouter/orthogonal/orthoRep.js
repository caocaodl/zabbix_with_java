var Dictionary  = function(){
	var c = {};
	var os = [];
	
	function key(o){
		var i = os.indexOf(o);
		if(i == -1){
			os.push(o);
			o = os.length-1;
		}
		return i;
	}
	
	this.get = function(k){
		return c[key(k)];
	}
	this.put = function(k, v){
		c[key(k)] = v;
	}
}

OrthoRep = function(param1) {
	this.graph = param1;
};

OrthoRep.prototype = {
	getOppositeDirection : function(param1) {
		if (param1 == 0) {
			return 2;
		}
		if (param1 == 1) {
			return 3;
		}
		if (param1 == 2) {
			return 0;
		}
		return 1;
	}// end function
};


var OrthoShaper = function(){
	function repeat(param1, param2) {
        var _loc_3 = "";
        var _loc_4 = 0;
        while (_loc_4 < param2)
        {
            _loc_3 = _loc_3 + param1;
            _loc_4 = _loc_4 + 1;
        }
        return _loc_3;
    }// end function
	
	//param1:OGraph, param2:CombinatorialEmbedding, param3:OrthoRep
	this.run = function(oGraph, cEmb, oRep) {
        var _loc_31 = undefined;
        var adjacencies = undefined;
        var face = null;
        var graph = null;
        var _loc_13 = null;
        var originNode = null;
        var _loc_15 = null;
        var _loc_16 = 0;
        var _loc_17 = null;
        var _loc_21 = null;
        var _loc_22 = false;
        var adjacencie = null;
        var _loc_24 = null;
        var _loc_25 = null;
        var _loc_26 = null;
        if (oGraph.numOfEdges != 0)
        {
        }
        if (oGraph.faces == null)
        {
            return false;
        }
        var faces = oGraph.faces;
        var externalFace = faces[0];
        $.each(faces, function(i, face)){
            if (face.getActualSize() <= externalFace.getActualSize()){
                return;
            }
            externalFace = face;
        }
        cEmb.externalFace = externalFace;
        
        var _dict_5 = new Dictionary();
        var _dict_6 = new Dictionary();
        var _dict_7 = new Dictionary();
        var nodeSizes_dict = new Dictionary();
        var _dict_9 = new Dictionary();
        
        graph = new Graph(null, "network", 0, 0, 0, 0);
        var _dict_11 = new Dictionary();
        var _dict_12 = new Dictionary();
        
        $.each(oGraph.nodes, function(i, originNode){
            nodeNode = new Node(graph, "node-network node: " + oGraph.nodes.indexOf(originNode), 0, 0, 0, 0);
            _dict_11[originNode] = nodeNode;
            nodeSizes_dict[nodeNode] = 4;
        })
        
        $.each(faces, function(i, face){
            faceNode = new Node(graph, "face-network node: " + faces.indexOf(face), 0, 0, 0, 0);
            _dict_12[face] = faceNode;
            if (face == externalFace){
                nodeSizes_dict[faceNode] = -(2 * face.size + 4);
                return;
            }
            nodeSizes_dict[faceNode] = -(2 * face.size - 4);
        })
        
        _loc_16 = 0;
        
        _loc_30 = graph.nodes;
        
        $.each(_loc_30, function(i, _loc_17){
            _loc_16 = _loc_16 + nodeSizes_dict[originNode];
        });
        
        var _dict_18 = new Dictionary();
        var _dict_19 = new Dictionary();
        var _dict_20 = new Dictionary();
        
        _loc_30 = faces;
        
        $.each(_loc_30, function(i, face){
            _loc_31 = 0;
            adjacencies = face.adjacencies;
            
            $.each(adjacencies, function(i, adjacencie){
                _loc_24 = _dict_11[adjacencie.node];
                _loc_25 = _dict_12[face];
                _loc_26 = new Edge("n-f " + _loc_24.uid + "-" + _loc_25.uid, _loc_24, _loc_25, 0);
                _dict_7.put(_loc_26, 0);
                _dict_5.put(_loc_26, 1);
                _dict_6.put(_loc_26, 4);
                _dict_18.put(_loc_26, adjacencie);
                _dict_19.put(_loc_26, face);
                _dict_20.put(_loc_26, adjacencie.node);
                _loc_24 = _dict_12[adjacencie.twin.rightFace];
                _loc_25 = _dict_12[face];
                _loc_26 = new Edge("f-f " + _loc_24.uid + "-" + _loc_25.uid, _loc_24, _loc_25, 0);
                _dict_7.put(_loc_26, 1);
                _dict_5.put(_loc_26, 0);
                _dict_6.put(_loc_26, Math.MAX_VALUE);
                _dict_18.put(_loc_26, adjacencie;
            })
        })
        
        _loc_21 = new MinimumCostFlow();
        _loc_22 = _loc_21.run(graph, _dict_5, _dict_6, _dict_7, nodeSizes_dict, _dict_9);
        if (_loc_22)
        {
            
            _loc_30 = graph.edges;
            
            $.each(_loc_30, function(i, _loc_26){
                if (_dict_20[_loc_26] == null)
                {
                }
                if (_dict_18[_loc_26] != null)
                {
                }
                if (_dict_9[_loc_26] > 0)
                {
                    (_dict_18[_loc_26] as Adjacency).bends = repeat(0, _dict_9[_loc_26]);
                    (_dict_18[_loc_26] as Adjacency).twin.bends = repeat(1, _dict_9[_loc_26]);
                    return;
                }
                if (_dict_20[_loc_26])
                {
                }
                if (!_dict_19[_loc_26])
                {
                	return;
                }
                (_dict_18[_loc_26] /*as Adjacency*/).angle = _dict_9[_loc_26];
            });
        }
        else
        {
            return false;
        }
        oRep.E = cEmb;
        oRep.faces = cEmb.faces;
        oRep.adjacencies = cEmb.adjacencies;
        return oRep.check();
    }// end function

  

}