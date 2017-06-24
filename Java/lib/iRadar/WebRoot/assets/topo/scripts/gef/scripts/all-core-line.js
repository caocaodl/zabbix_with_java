/**
 * 单箭头连线.实线.编辑器
 * 
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.TransitionEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();
		var figure = new Gef.jbs.figure.TransitionFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		figure.innerPoints = model.innerPoints;
		figure.name = model.text;
		figure.textX = model.textX;
		figure.textY = model.textY;
		figure.editPart = this;
		figure.conditional = model.condition ? true : false;
		return figure;
	}
});
/**
 * 单箭头连线.实线.model模型
 * 
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.TransitionModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "transition",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);
		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 单箭头连线.实线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.TransitionFigure = Gef.extend(Gef.figure.EdgeFigure, {});

/**
 * 单箭头连线.虚线.编辑器
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.DashedArrowsEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();

		var dashedArrowsFigure = new Gef.jbs.figure.DashedArrowsFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		dashedArrowsFigure.innerPoints = model.innerPoints;
		dashedArrowsFigure.name = model.text;
		dashedArrowsFigure.textX = model.textX;
		dashedArrowsFigure.textY = model.textY;
		dashedArrowsFigure.editPart = this;
		dashedArrowsFigure.conditional = model.condition ? true : false;
		return dashedArrowsFigure;
	}
});
/**
 * 单箭头连线.虚线.model模型
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DashedArrowsModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "dashedArrows",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);
		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 单箭头连线.虚线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.DashedArrowsFigure = Gef.extend(Gef.figure.EdgeFigure, {
	// 虚线单箭头
	lineType : 'dashedArrows'
});
/**
 * 连线.直线.编辑器
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.LineEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();
		var lineFigure = new Gef.jbs.figure.LineFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		lineFigure.innerPoints = model.innerPoints;
		lineFigure.name = model.text;
		lineFigure.textX = model.textX;
		lineFigure.textY = model.textY;
		lineFigure.editPart = this;
		lineFigure.conditional = model.condition ? true : false;
		return lineFigure;
	}
});


/**
 * 连线.直线.model模型
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.LineModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "line",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);

		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 连线.直线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.LineFigure = Gef.extend(Gef.figure.EdgeFigure, {
	// 直线
	lineType : 'line'
});
/**
 * 连线.虚线.编辑器
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.DashedLineEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();
		var dashedLineFigure = new Gef.jbs.figure.DashedLineFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		dashedLineFigure.innerPoints = model.innerPoints;
		dashedLineFigure.name = model.text;
		dashedLineFigure.textX = model.textX;
		dashedLineFigure.textY = model.textY;
		dashedLineFigure.editPart = this;
		dashedLineFigure.conditional = model.condition ? true : false;
		return dashedLineFigure;
	}
});
/**
 * 连线.虚线.model模型
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DashedLineModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "dashedLine",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);
		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 连线.虚线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.DashedLineFigure = Gef.extend(Gef.figure.EdgeFigure, {
	// 虚线
	lineType : 'dashedLine'
});
/**
 * 连线.双箭头实线.编辑器
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.DoubleArrowsLineEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();
		var doubleArrowsLineFigure = new Gef.jbs.figure.DoubleArrowsLineFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		doubleArrowsLineFigure.innerPoints = model.innerPoints;
		doubleArrowsLineFigure.name = model.text;
		doubleArrowsLineFigure.textX = model.textX;
		doubleArrowsLineFigure.textY = model.textY;
		doubleArrowsLineFigure.editPart = this;
		doubleArrowsLineFigure.conditional = model.condition ? true : false;
		return doubleArrowsLineFigure;
	}
});
/**
 * 连线.双箭头实线.model模型
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DoubleArrowsLineModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "doubleArrowsLine",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);
		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 连线.双箭头实线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.DoubleArrowsLineFigure = Gef.extend(Gef.figure.EdgeFigure, {
	// 实线
	lineType : 'doubleArrowsLine'
});
/**
 * 连线.双箭头虚线.编辑器
 */
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.DoubleArrowsDashedEditPart = Gef.extend(Gef.gef.editparts.ConnectionEditPart, {
	createFigure : function() {
		var model = this.getModel();
		var doubleArrowsDashedFigure = new Gef.jbs.figure.DoubleArrowsDashedFigure(this.getSource().getFigure(), this.getTarget().getFigure());
		doubleArrowsDashedFigure.innerPoints = model.innerPoints;
		doubleArrowsDashedFigure.name = model.text;
		doubleArrowsDashedFigure.textX = model.textX;
		doubleArrowsDashedFigure.textY = model.textY;
		doubleArrowsDashedFigure.editPart = this;
		doubleArrowsDashedFigure.conditional = model.condition ? true : false;
		return doubleArrowsDashedFigure;
	}
});
/**
 * 连线.双箭头虚线.model模型
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DoubleArrowsDashedModel = Gef.extend(Gef.model.ConnectionModel, {
	type : "doubleArrowsDashed",
	form : 'transition',
	encode : function(obj, str) {
		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("g", Gef.model.JpdlUtil.encodeConnectionPosition(this));
		this.dom.setAttribute("to", this.target.text);
		return this.dom.encode("", str);
	},
	decode : function(obj, str) {
		this.dom.decode(obj, str);
		this.text = this.dom.getAttribute("name");
	}
});
/**
 * 连线.双箭头虚线.页面绘图
 */
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.DoubleArrowsDashedFigure = Gef.extend(Gef.figure.EdgeFigure, {
	// 虚线
	lineType : 'doubleArrowsDashed'
});