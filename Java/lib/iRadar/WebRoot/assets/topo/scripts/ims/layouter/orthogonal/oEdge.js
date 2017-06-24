OEdge = function() {
	this.edgeToBasicArc;
	this.sourceAdj;// :Adjacency;
	this.cost;
	this.originalEdge;// :Edge;
	this.highDegreeNode ;// :ONode;
	this.target  ;// :ONode;
	this.dissectionEdge;
	this.source  ;
	this.targetAdj;// :Adjacency;

};

OEdge.prototype = {
	addAfter : function(param1) {
		this.originalEdge = param1.originalEdge;
		if (this.originalEdge) {
			this.originalEdge.layoutData.splice((this.originalEdge.layoutData.indexOf(param1) + 1), 0, this);
		}
		return;
	},
	getOtherNode : function(param1) {
		return param1 != this.source ? (this.source) : (this.target);
	}// end function
};