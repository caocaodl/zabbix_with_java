Gef.ns("Gef.jbs");
Gef.jbs.JBSEditor = Gef.extend(Gef.gef.support.DefaultGraphicalEditorWithPalette, {
	constructor : function() {
		this.modelFactory = new Gef.jbs.JBSModelFactory();
		this.editPartFactory = new Gef.jbs.JBSEditPartFactory();
		Gef.jbs.JBSEditor.superclass.constructor.call(this)
	},
	getPaletteHelper : function() {

		if (!this.paletteHelper)
			this.paletteHelper = new Gef.jbs.JBSPaletteHelper(this);
		return this.paletteHelper
	},
	serial : function() {
		var $ = this.getGraphicalViewer().getContents().getModel();
		var _ = new Gef.jbs.xml.JBSSerializer($);
		
		var A = _.serialize();
		return A
	},
	clear : function() {
		var D = this.getGraphicalViewer(), A = D.getContents(), C = D.getBrowserListener(), _ = D.getEditDomain().getCommandStack(), $ = C
				.getSelectionManager();
		$.selectAll();
		var B = A.getRemoveNodesCommand({
			role : {
				nodes : $.getSelectedNodes()
			}
		});
		_.execute(B);
		$.clearAll();
		this.editDomain.editPartRegistry = []
	},
	reset : function() {
		this.clear();
		var A = this.getGraphicalViewer(), $ = A.getEditDomain().getCommandStack();
		$.flush();
		this.getModelFactory().reset();
		var _ = A.getContents();
		_.text = "untitled";
		_.key = null;
		_.description = null
	},
	resetAndOpen : function($) {
		this.reset();
		var A = new Gef.jbs.xml.JBSDeserializer($), _ = A.decode();
		this.getGraphicalViewer().setContents(_);
		this.updateModelFactory();
		this.getGraphicalViewer().getContents().refresh()
	}
});

Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ProcessEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
	createFigure : function() {

		return new Gef.jbs.figure.ProcessFigure();
	},
	getClass : function() {
		return "process";
	},

	/**
	 * 判断节点是否已经存在
	 */
	canCreate : function(editor) {
		
		var bool = true;
		var modelType = editor.getType();
		Gef.each(this.children, function(editor) {
			if (editor.getModel().type == modelType) {
				
				// Gef.showMessage("validate.only_one_start", "��ͼԪ�Ѵ���!");
				bool = false;
				return false;
			}
		});
		return bool;
	}
});

/**
 */
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ProcessModel = Gef.extend(Gef.model.NodeModel, {
	type : "process",
	encode : function($) {
		var _ = "";

		Gef.each(this.children, function($) {
			
			_ += $.encode("", " ")
		});
		this.dom.tagName = "process";
		this.dom.removeAttribute("version");
		this.dom.setAttribute("name", Gef.PROCESS_NAME);
		this.dom.setAttribute("key", Gef.PROCESS_KEY);
		this.dom.setAttribute("xmlns", "http://jbpm.org/4.4/jpdl");

		return this.dom.encode(_)
	},
	decode : function($, _) {
		this.dom.decode($, _);
		this.text = Gef.PROCESS_NAME;
		this.dom.removeAttribute("version");
		this.dom.setAttribute("name", Gef.PROCESS_NAME);
		this.dom.setAttribute("key", Gef.PROCESS_KEY)
	}
});

Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractNodeFigure = Gef.extend(Gef.figure.NodeFigure, {
	getTools : function() {
		if (!this.tools)
			this.tools = [ new Gef.jbs.tool.TaskTool(), new Gef.jbs.tool.EndTool(), new Gef.jbs.tool.GatewayTool(), new Gef.jbs.tool.LineTool(),
					new Gef.jbs.tool.ChangeTypeTool({
						allowedTypes : [ {
							type : "human",
							name : "\u4eba\u5de5"
						}, {
							type : "counter-sign",
							name : "\u4f1a\u7b7e"
						}, {
							type : "state",
							name : "\u7b49\u5f85"
						}, {
							type : "hql",
							name : "HQL"
						}, {
							type : "sql",
							name : "SQL"
						}, {
							type : "java",
							name : "JAVA"
						}, {
							type : "script",
							name : "\u811a\u672c"
						}, {
							type : "task",
							name : "\u4eba\u5de5\u4efb\u52a1"
						}, {
							type : "mail",
							name : "\u90ae\u4ef6"
						}, {
							type : "custom",
							name : "\u81ea\u5b9a\u4e49"
						}, {
							type : "subProcess",
							name : "\u5b50\u6d41\u7a0b"
						}, {
							type : "jms",
							name : "JMS"
						}, {
							type : "rules",
							name : "\u89c4\u5219"
						} ]
					}) ];
		return this.tools
	}
});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
	getTools : function() {
		if (!this.tools)
			this.tools = [ new Gef.jbs.tool.TaskTool(), new Gef.jbs.tool.EndTool(), new Gef.jbs.tool.GatewayTool(), new Gef.jbs.tool.LineTool(),
					new Gef.jbs.tool.ChangeTypeTool({
						allowedTypes : [ {
							type : "decision",
							name : "\u51b3\u7b56"
						}, {
							type : "fork",
							name : "\u5e76\u53d1"
						}, {
							type : "join",
							name : "\u6c47\u805a"
						}, {
							type : "ruleDecision",
							name : "\u89c4\u5219\u51b3\u7b56"
						} ]
					}) ];
		return this.tools
	}
});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractEndImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
	getTools : function() {
		if (!this.tools)
			this.tools = [ new Gef.jbs.tool.ChangeTypeTool({
				allowedTypes : [ {
					type : "end",
					name : "\u7ed3\u675f"
				}, {
					type : "cancel",
					name : "\u53d6\u6d88"
				}, {
					type : "error",
					name : "\u9519\u8bef"
				} ]
			}) ];
		return this.tools
	}
});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractStartImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
	getTools : function() {
		if (!this.tools)
			this.tools = [ new Gef.jbs.tool.TaskTool(), new Gef.jbs.tool.GatewayTool({
				getY : function() {
					return 20
				}
			}), new Gef.jbs.tool.LineTool({
				getY : function() {
					return 40
				}
			}) ];
		return this.tools
	}
});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ProcessFigure = Gef.extend(Gef.figure.NoFigure, {});

Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.ChangeTypeTool = Gef.extend(Gef.tool.AbstractImageTool, {
	getKey : function() {
		return "changeTypeTool"
	},
	needCheckOutgo : function() {
		return false
	},
	getUrl : function() {
		return Gef.IMAGE_ROOT + "../../tools/wrench_orange.png"
	},
	getX : function($) {
		return 5
	},
	getY : function($) {
		return $ + 5
	},
	getConnectionModelName : function() {
		return "connection"
	},
	drag : function($, _) {
		var A = document.createElement("div");
		A.style.position = "absolute";
		A.style.left = _.point.absoluteX + "px";
		A.style.top = _.point.absoluteY + "px";
		A.style.backgroundColor = "#DDEEDD";
		Gef.each(this.allowedTypes, function(_) {
			if (_.type == this.node.editPart.model.getType())
				return true;
			var $ = document.createElement("div");
			$.onmouseover = function() {
				this.style.backgroundColor = "yellow"
			};
			$.onmouseout = function() {
				this.style.backgroundColor = ""
			};
			$.style.cursor = "pointer";
			$.className = "_gef_changeType";
			$.setAttribute("title", _.type);
			$.innerHTML = _.name;
			A.appendChild($)
		}, this);
		document.body.appendChild(A);
		$.changeTypeDiv = A
	},
	move : function($, _) {
	},
	drop : function(B, C) {
		var A = C.target;
		if (A.className == "_gef_changeType") {
			var E = A.getAttribute("title"), D = this.node.editPart.model, _ = B.getModelFactory().createModel(E), $ = new Gef.commands.CompoundCommand();
			$.addCommand(new Gef.gef.command.CreateNodeCommand(_, D.getParent(), {
				x : D.x,
				y : D.y,
				w : D.w,
				h : D.h
			}));
			Gef.each(D.getIncomingConnections(), function(A) {
				var D = A.getType(), C = B.getModelFactory().createModel(D);
				C.text = A.text;
				$.addCommand(new Gef.gef.command.RemoveConnectionCommand(A));
				$.addCommand(new Gef.gef.command.CreateConnectionCommand(C, A.getSource(), _));
				$.addCommand(new Gef.gef.command.ResizeConnectionCommand(C, [], A.innerPoints))
			});
			Gef.each(D.getOutgoingConnections(), function(A) {
				var D = A.getType(), C = B.getModelFactory().createModel(D);
				C.text = A.text;
				$.addCommand(new Gef.gef.command.RemoveConnectionCommand(A));
				$.addCommand(new Gef.gef.command.CreateConnectionCommand(C, _, A.getTarget()));
				$.addCommand(new Gef.gef.command.ResizeConnectionCommand(C, [], A.innerPoints))
			});
			$.addCommand(new Gef.gef.command.RemoveNodeCommand(D));
			B.getCommandStack().execute($);
			B.getSelectionManager().addSelectedNode(_.editPart)
		}
		document.body.removeChild(B.changeTypeDiv)
	}
});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.TaskTool = Gef.extend(Gef.tool.AbstractImageTool, {
	getKey : function() {
		return "taskTool"
	},
	getUrl : function() {
		return Gef.IMAGE_ROOT + "../../tools/new_task.png"
	},
	getNodeConfig : function() {
		return {
			text : "human",
			w : 90,
			h : 50
		}
	},
	getY : function() {
		return 0
	},
	getConnectionModelName : function() {
		return "transition"
	}
});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.EndTool = Gef.extend(Gef.tool.AbstractImageTool, {
	getKey : function() {
		return "endTool"
	},
	getUrl : function() {
		return Gef.IMAGE_ROOT + "../../tools/new_event.png"
	},
	getNodeConfig : function() {
		return {
			text : "end",
			w : 48,
			h : 48
		}
	},
	getY : function() {
		return 20
	},
	getConnectionModelName : function() {
		return "transition"
	}
});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.GatewayTool = Gef.extend(Gef.tool.AbstractImageTool, {
	getKey : function() {
		return "gatewayTool"
	},
	getUrl : function() {
		return Gef.IMAGE_ROOT + "../../tools/new_gateway_xor_data.png"
	},
	getNodeConfig : function() {
		return {
			text : "decision",
			w : 48,
			h : 48
		}
	},
	getY : function() {
		return 40
	},
	getConnectionModelName : function() {
		return "transition"
	}
});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.LineTool = Gef.extend(Gef.tool.AbstractEdgeTool, {
	getKey : function() {
		return "lineTool"
	},
	getY : function() {
		return 60
	},
	getConnectionModelName : function() {
		return "transition"
	}
});
Gef.ns("Gef.jbs.xml");
Gef.jbs.xml.JBSSerializer = Gef.extend(Gef.gef.xml.XmlSerializer, {});

Gef.jbs.model.ShapeBaseModel = Gef.extend(Gef.model.NodeModel, {
	fill : "",
	stroke : "black",
	strokewide : 1,
	isValid : function() {
		return true
	},
	encode : function(_, $) {
		var A = "";
		Gef.each(this.outgoingConnections, function(_) {
			A += _.encode("", $ + " ")
		});
		Gef.model.JpdlUtil.encodeNodePosition(this);
		this.dom.setAttribute("name", this.text);
		return this.dom.encode(A, $)
	},
	decode : function($, _) {
		this.dom.decode($, _);
		this.text = this.dom.getAttribute("name");
		this.fill = this.dom.getAttribute("fill") || "";
		this.stroke = this.dom.getAttribute("stroke") || "black";
		this.strokewide = this.dom.getAttribute("strokewide") || 1
	}
});
Gef.jbs.figure.ShapeBaseFigure = Gef.extend(Gef.figure.RectFigure, {
	constructor : function($) {
		Gef.jbs.figure.ShapeBaseFigure.superclass.constructor.call(this, $);
		this.w = $.w;
		this.h = $.h;
		this.fill = $.fill;
		this.stroke = $.stroke;
		this.strokewide = $.strokewide;
		this.outputs = [];
		this.incomes = []
	},
	renderVml : function() {
	},
	renderVml0 : function() {
	},
	renderSvg : function() {
	},
	renderSvg0 : function($) {
	},
	moveToVml : function() {
		this.renderVml0(this.el)
	},
	moveToSvg : function(_, $) {
		this.renderSvg0(this.el)
	},
	updateVml : function() {
		this.renderVml0(this.el)
	},
	updateSvg : function() {
		this.renderSvg0(this.el)
	},
	getTools : function() {
		return []
	}
});
/**
 * ����
 */
Gef.jbs.editpart.GenericEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
	_figureClassName : "Exception",
	_getFigureParam : function() {
		return [ "x", "y", "w", "h" ]
	},
	canCreate : function() {
	},
	createFigure : function() {

		var model = this.getModel();
		// ҳ�����нڵ�
		var _obj = this.parent.model.children;

		var p = {}, pk = this._getFigureParam();

		for ( var i = 0; i < pk.length; i++) {

			p[pk[i]] = this.model[pk[i]];
			p["name"] = this.model[pk["text"]];
		}
		var figure = eval("new " + this._figureClassName + "(p)");
		figure.editPart = this;
		return figure
	},
	canResize : function() {
		return true
	}
});
Gef.jbs.editpart.ShapeBaseEditPart = Gef.extend(Gef.jbs.editpart.GenericEditPart, {
	_figureClassName : "Gef.jbs.figure.ShapeBaseFigure",
	_getFigureParam : function() {
		return [ "x", "y", "w", "h", "fill", "stroke", "strokewide" ]
	}
});
Gef.jbs.model.GenericImageModel = Gef.extend(Gef.model.NodeModel, {
	type : "image",
	isValid : function() {
		return true
	},
	encode : function(_, $) {
		var A = "";
		Gef.each(this.outgoingConnections, function(_) {
			
			A += _.encode("", $ + " ");
		});
		Gef.model.JpdlUtil.encodeNodePosition(this);
//		this.dom.setAttribute("name", this.text);
		this.dom.setAttribute("name", this.title);
		// this.dom.setAttribute("url", this.text);
		return this.dom.encode(A, $)
	},
	decode : function($, _) {
		this.dom.decode($, _);
		this.text = this.dom.getAttribute("name");
		// this.url = this.dom.getAttribute("url") || this.url
	}
});
Gef.jbs.figure.GenericImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
	constructor : function($) {
		Gef.jbs.figure.GenericImageFigure.superclass.constructor.call(this, $);
		this.w = $.w;
		this.h = $.h;
		this.url = $.url
	},
	update : function(B, A, $, _) {
		this.x = B;
		this.y = A;
		this.w = $;
		this.h = _;
		if (Gef.isVml)
			this.updateVml();
		else
			this.updateSvg()
	}
});
Gef.jbs.editpart.GenericImageEditPart = Gef.extend(Gef.jbs.editpart.GenericEditPart, {
	_figureClassName : "Gef.jbs.figure.GenericImageFigure",

	_getFigureParam : function() {

		return [ "x", "y", "w", "h", "url" ];
	}
});
Gef.jbs.JBSModelFactory.registerModel("image", "Gef.jbs.model.GenericImageModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("image", "Gef.jbs.editpart.GenericImageEditPart");
Gef.jbs.model.EllipseModel = Gef.extend(Gef.jbs.model.ShapeBaseModel, {
	type : "ellipse"
});
/**
 * ����Բ
 */
Gef.jbs.figure.EllipseFigure = Gef.extend(Gef.jbs.figure.ShapeBaseFigure, {
	renderSvg : function() {

		var $ = document.createElementNS(Gef.svgns, "ellipse");
		this.renderSvg0($);
		this.el = $
	},
	renderSvg0 : function($) {
		$.setAttribute("cx", this.x + this.w / 2 + "px");
		$.setAttribute("cy", this.y + this.h / 2 + "px");
		$.setAttribute("rx", this.w / 2 + "px");
		$.setAttribute("ry", this.h / 2 + "px");
		$.setAttribute("fill", this.fill ? this.fill : "none");
		$.setAttribute("stroke", this.stroke);
		$.setAttribute("stroke-width", this.strokewidth)
	},
	renderVml : function() {
		var $ = document.createElement("v:oval");
		this.renderVml0($);
		this.el = $
	},
	renderVml0 : function($) {
		$.style.left = this.x + "px";
		$.style.top = this.y + "px";
		$.style.width = this.w + "px";
		$.style.height = this.h + "px";
		if (this.fill)
			$.setAttribute("fillcolor", this.fill);
		else
			$.setAttribute("filled", "false");
		$.setAttribute("strokecolor", this.stroke)
	}
});
Gef.jbs.editpart.EllipseEditPart = Gef.extend(Gef.jbs.editpart.ShapeBaseEditPart, {
	_figureClassName : "Gef.jbs.figure.EllipseFigure"
});
Gef.jbs.JBSModelFactory.registerModel("ellipse", "Gef.jbs.model.EllipseModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("ellipse", "Gef.jbs.editpart.EllipseEditPart");
Gef.jbs.model.RectModel = Gef.extend(Gef.jbs.model.ShapeBaseModel, {
	type : "rect",
	rounded : 0,
	encode : function(_, $) {
		this.dom.setAttribute("rounded", this.rounded);
		return Gef.jbs.model.RectModel.superclass.encode.call(this, _, $)
	},
	decode : function($, _) {
		Gef.jbs.model.RectModel.superclass.decode.call(this, $, _);
		this.rounded = this.dom.getAttribute("rounded") || 0
	}
});
Gef.jbs.figure.RectFigure = Gef.extend(Gef.jbs.figure.ShapeBaseFigure, {
	constructor : function($) {
		Gef.jbs.figure.RectFigure.superclass.constructor.call(this, $);
		this.rounded = $.rounded
	},
	renderSvg : function() {

		var $ = document.createElementNS(Gef.svgns, "rect");
		this.renderSvg0($);
		this.el = $
	},
	renderSvg0 : function($) {
		$.setAttribute("x", this.x + "px");
		$.setAttribute("y", this.y + "px");
		$.setAttribute("width", this.w + "px");
		$.setAttribute("height", this.h + "px");
		$.setAttribute("rx", this.rounded);
		$.setAttribute("ry", this.rounded);
		$.setAttribute("fill", this.fill ? this.fill : "none");
		$.setAttribute("stroke", this.stroke);
		$.setAttribute("stroke-width", this.strokewidth)
	},
	renderVml : function() {
		var $ = document.createElement("v:rect");
		this.renderVml0($);
		this.el = $
	},
	renderVml0 : function($) {
		$.style.left = this.x + "px";
		$.style.top = this.y + "px";
		$.style.width = this.w + "px";
		$.style.height = this.h + "px";
		if (this.fill)
			$.fillcolor = this.fill;
		else
			$.filled = "false";
		$.strokecolor = this.stroke;
		$.strokeweight = this.strokewidth + "px"
	}
});
Gef.jbs.editpart.RectEditPart = Gef.extend(Gef.jbs.editpart.ShapeBaseEditPart, {
	_figureClassName : "Gef.jbs.figure.RectFigure",
	_getFigureParam : function() {
		return [ "x", "y", "w", "h", "fill", "stroke", "strokewide", "rounded" ]
	}
});
Gef.jbs.JBSModelFactory.registerModel("rect", "Gef.jbs.model.RectModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("rect", "Gef.jbs.editpart.RectEditPart");