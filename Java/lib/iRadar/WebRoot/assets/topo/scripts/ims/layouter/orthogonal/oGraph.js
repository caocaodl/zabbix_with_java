OGraph = function(process) {
	this.originalGraph = process;
	this.nodes = new Array();
	this.numOfNodes;
	this.faces = new Array();
	this.numOfEdges;
	this.edges = new Array();
	this.init();
};

OGraph.prototype = {

	init : function() {
		this.nodes = [];
		this.edges = [];
		if (!this.originalGraph) {
			return;
		}
		
		// 页面所有节点集合
		var processModels = this.originalGraph;
		for (var i = 0; i < processModels.length; i++) {
			//原始节点对象
			var originalNode = processModels[i];
			//新节点
			var newNode = new ONode();
			newNode.x = originalNode.x;
			newNode.y = originalNode.y;
			newNode.width = originalNode.w;
			newNode.height = originalNode.h;			
			newNode.originalNode = originalNode;
			
			originalNode.layoutData = newNode;

			this.nodes.push(newNode);
            
			//循环节点对外连接
			var outCons=originalNode.outgoingLinks;
			for (var j = 0; j < outCons.length; j++) {
				//连线对象
				var connection = outCons[j];
				// if (connection.isLoop) {
				// continue;
				// }
				var newEdge = new OEdge();
				newEdge.originalEdge = connection;
				
				connection.layoutData = [newEdge];
				
				this.edges.push(newEdge);
			}
		}

		// _loc_12 = 0;
		// processModels = this.originalGraph.children;
		// for (var i = 0; i < processModels.length; i++) {
		// connection = processModels[i].getOutgoingConnections();
		// // if (connection.isLoop) {
		// // continue;
		// // }
		// newEdge = new OEdge();
		// newEdge.originalEdge = connection;
		// connection.layoutData = [newEdge];
		// this.edges.push(newEdge);
		// }

		// 连线数量集合
		var edges = this.edges;

		for  (var k=0;k<this.edges.length;k++) {
			
			var edgeObj = this.edges[k];
			var connectionObj = edgeObj.originalEdge;
			// 原始目标
			var source = connectionObj.source;
			//原始目标node对象
			var newSourceNode = source.layoutData;
			
			// 新目标节点
			var target = connectionObj.target;			
			newTargetNode = target.layoutData;
			
			if (newTargetNode.adjacentNodes.indexOf(newSourceNode) == -1) {
				
				edgeObj.source = newSourceNode;
				edgeObj.target = newTargetNode;
				newSourceNode.edges.push(edgeObj);
				newTargetNode.edges.push(edgeObj);
				newSourceNode.adjacentNodes.push(newTargetNode);
				newTargetNode.adjacentNodes.push(newSourceNode);
			} else {
				
				var newEdge = new OEdge();
				this.edges.push(newEdge);
				var newNode = new ONode();
				this.nodes.push(newNode);
				
				edgeObj.source = newSourceNode;
				edgeObj.target = newNode;
				newSourceNode.edges.push(edgeObj);
				newNode.edges.push(edgeObj);
				newEdge.source = newNode;
				newEdge.target = newTargetNode;
				newNode.edges.push(newEdge);
				newTargetNode.edges.push(newEdge);
				newSourceNode.adjacentNodes.push(newNode);
				newNode.adjacentNodes.push(newSourceNode);
				newNode.adjacentNodes.push(newTargetNode);
				newTargetNode.adjacentNodes.push(newNode);
				newEdge.addAfter(edgeObj);
			}
		}

		this.numOfEdges = this.edges.length;
		this.numOfNodes = this.nodes.length;
		return;
	}
	, check:function () 
        {
            var _loc_5 = undefined;
            var _loc_6 = undefined;
            var _loc_1 = null;
            var _loc_2 = null;
            var _loc_3 = 0;
            var _loc_4 = this.nodes;
            for  (var i=0;i<_loc_4.length;i++)
            {
                _loc_2 = _loc_4[i];
                _loc_5 = 0;
                _loc_6 = _loc_2.edges;
                for  (var j=0;j<_loc_6.length;j++)
                {
                    _loc_1 = _loc_6[j];
                    if (_loc_1.source != _loc_2)
                    {
                    }
                    if (_loc_1.target == _loc_2)
                    {
                        continue;
                    }
                    return false;
                }
            }
            _loc_3 = 0;
            _loc_4 = this.edges;
            for  (var k=0;k<_loc_4.length;k++)
            {
                _loc_1 = _loc_4[k];
                if (_loc_1.source.edges.indexOf(_loc_1) != -1)
                {
                }
                if (_loc_1.target.edges.indexOf(_loc_1) != -1)
                {
                }
                if (_loc_1.target.edges.length != 0)
                {
                }
                if (_loc_1.source.edges.length != 0)
                {
                    continue;
                }
                return false;
            }
            return true;
        }// end function
};