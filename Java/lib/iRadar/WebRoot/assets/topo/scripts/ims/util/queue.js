PriorityQueue = function(center) {
	this.ce = center;
	this.items = [];
};

PriorityQueue.prototype = {
	add : function(element) {
		this.items.push(element);

		var len = this.items.length;
		for (var i = 0; i < len; i++) {
			for (var j = i; j < len; j++) {
				var elem1 = this.items[i];
				var elem2 = this.items[j];
				if (this.compareTo(elem1, elem2) > 0) {
					this.items[i] = elem2;
					this.items[j] = elem1;
				}
			}
		}
	},

	compareTo : function(elem1, elem2) {
		return this.ce.backwardDistanceTo(elem1)
				- this.ce.backwardDistanceTo(elem2);
	},

	contains : function(element) {
		return this.items.indexOf(element) != -1;
	}
};