/**
 * 画页面控件 工厂
 */
Gef.ns("Gef.jbs");
Gef.jbs.JBSEditPartFactory = Gef.extend(Gef.gef.EditPartFactory, {
	createEditPart : function(type) {
		return Gef.jbs.JBSEditPartFactory._editPartLib[type] ? 
					eval("new " + Gef.jbs.JBSEditPartFactory._editPartLib[type] + "(type)") : 
					null;
	}
});
Gef.jbs.JBSEditPartFactory._editPartLib = {
	"process" : "Gef.jbs.editpart.ProcessEditPart",
	"transition" : "Gef.jbs.editpart.TransitionEditPart",
	"dashedArrows" : "Gef.jbs.editpart.DashedArrowsEditPart",
	"line" : "Gef.jbs.editpart.LineEditPart",
	"dashedLine" : "Gef.jbs.editpart.DashedLineEditPart",
	"doubleArrowsLine" : "Gef.jbs.editpart.DoubleArrowsLineEditPart",
	"doubleArrowsDashed" : "Gef.jbs.editpart.DoubleArrowsDashedEditPart"
};
Gef.jbs.JBSEditPartFactory.registerEditPart = function(_, $) {
	Gef.jbs.JBSEditPartFactory._editPartLib[_] = $;
};


Gef.ns("Gef.jbs");
Gef.jbs.JBSModelFactory = Gef.extend(Gef.gef.ModelFactory, {
	getId : function($) {
		if (this.map == null)
			this.map = {};
		if (this.map[$] == null)
			this.map[$] = 1;
		else
			this.map[$]++;
		return $ + " " + this.map[$]
	},
	getTypeName : function($) {
		return $
	},
	reset : function() {
		delete this.map;
		this.map = {}
	},
	/**
	 * 根据图元名称 创建图元
	 */
	createModel : function(type) {
		
		var id = this.getId(type);
		return Gef.jbs.JBSModelFactory._modelLib[type] ? 
					eval("new " + Gef.jbs.JBSModelFactory._modelLib[type] + "({id:id,text:id})") : 
					null;
	}
});
Gef.jbs.JBSModelFactory._modelLib = {
	"process" : "Gef.jbs.model.ProcessModel",
	"transition" : "Gef.jbs.model.TransitionModel",
	"dashedArrows" : "Gef.jbs.model.DashedArrowsModel",
	"line" : "Gef.jbs.model.LineModel",
	"dashedLine" : "Gef.jbs.model.DashedLineModel",
	"doubleArrowsLine" : "Gef.jbs.model.DoubleArrowsLineModel",
	"doubleArrowsDashed" : "Gef.jbs.model.DoubleArrowsDashedModel"
};
Gef.jbs.JBSModelFactory.registerModel = function(_, $) {
	Gef.jbs.JBSModelFactory._modelLib[_] = $;
};