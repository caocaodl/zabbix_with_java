Adjacency = function(param1, param2) {
	this.basicArc;
	this.next;
	this.angle;
	this.rightFace;
	this.bends = "";
	this.twin;
	this.visited;
	this.marked = false;
	this.prev;
	this.direction;
	this.node = param1;
	this.edge = param2;
};

Adjacency.prototype = {
	twinNode : function() {
		return this.twin.node;
	},

	getNext : function() {
		return this.next;
	}// end function
	,
	setNext : function(param1) {
		this.next = param1;
		if (this.next) {
			this.next.prev = this;
		}
		return;
	}// end function
};