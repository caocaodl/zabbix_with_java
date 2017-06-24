var Plugin = new function(){
	var items = this.items = []; 
	this.reg = function(name, f){
	  
		var o = {};
		o.name = name;
		o.f = f;
		items.push(o);
	}
};
