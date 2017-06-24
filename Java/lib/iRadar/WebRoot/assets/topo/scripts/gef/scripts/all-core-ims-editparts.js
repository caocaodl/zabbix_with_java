/**
 * 
 */
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultEditorPart = Gef.extend(Gef.ui.EditorPart, {
	constructor : function(workbenchPage) {
		this.workbenchPage = workbenchPage;
	},
	getWorkbenchPage : function() {
		return this.workbenchPage
	},
	setWorkbenchPage : function(workbenchPage) {
		this.workbenchPage = workbenchPage;
	},
	init : function(obj) {
	},
	render : function() {
	}
});

/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.Editor = Gef.extend(Gef.ui.EditorPart, {
	getEditDomain : Gef.emptyFn,
	getGraphicalViewer : Gef.emptyFn,
	getModelFactory : Gef.emptyFn,
	setModelFactory : Gef.emptyFn,
	getEditPartFactory : Gef.emptyFn,
	setEditPartFactory : Gef.emptyFn
});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractGraphicalEditPart = Gef.extend(Gef.gef.editparts.AbstractEditPart, {
	addChildVisual : function(_) {
		if (_.getClass() == "node") {
			var $ = _.getFigure();
			this.getRoot().getFigure().addNode($);
			$.render()
		} else if (_.getClass() == "connection")
			if (_.getSource() != null && _.getTarget() != null) {
				$ = _.getFigure();
				if (!$.el) {
					this.getRoot().getFigure().addConnection($);
					$.render();
				}
			}
	},
	removeChildVisual : function(_) {
		var $ = _.getFigure();
		$.remove()
	},
	refresh : function() {
		Gef.gef.editparts.AbstractGraphicalEditPart.superclass.refresh.call(this);
		this.refreshOutgoingConnections();
		this.refreshIncomingConnections();
	},
	refreshOutgoingConnections : function() {
		var A = {};
		var obj = this.getOutgoingConnections();
		var cleng = this.getOutgoingConnections().length;
		for ( var i = 0; i < cleng; i++) {

			var o = obj[i];

			A[o.getModel().getId()] = o;
		}
		for (C = 0; C < this.getModelOutgoingConnections().length; C++) {
			var _ = this.getModelOutgoingConnections()[C];
			var B = A[_.getId()];

			if (B == null) {
				B = this.findOrCreateConnection(_);
				this.addOutgoingConnection(B)
			} else {
				B.refresh();
			}
		}
	},
	refreshIncomingConnections : function() {
		var A = {};
		for ( var C = 0; C < this.getIncomingConnections().length; C++) {
			var $ = this.getIncomingConnections()[C];
			A[$.getModel().getId()] = $
		}
		for (C = 0; C < this.getModelIncomingConnections().length; C++) {
			var _ = this.getModelIncomingConnections()[C], B = A[_.getId()];
			if (B == null) {
				B = this.findOrCreateConnection(_);
				this.addIncomingConnection(B)
			} else
				B.refresh()
		}
	},
	addOutgoingConnection : function($) {
		this.getOutgoingConnections().push($)
	},
	addIncomingConnection : function($) {
		this.getIncomingConnections().push($)
	},
	notifyChanged : function(C, D) {
		switch (C) {
		case "CHILD_ADDED":
			var A = D, B = this.createChild(A);
			this.addChild(B);
			A.parent = this.model;
			B.parent = this;
			break;
		case "CHILD_REMOVED_FROM_PARENT":
			this.parent.removeChild(this);
			this.model.removeChangeListener(this);
			break;
		case "NODE_MOVED":
			this.refresh();
			break;
		case "CONNECTION_SOURCE_ADDED":
			this.refresh();
			break;
		case "CONNECTION_TARGET_ADDED":
			this.refresh();
			break;
		case "NODE_RESIZED":
			this.refresh();
			break;
		case "CONNECTION_RESIZED":
			this.getFigure().innerPoints = this.getModel().innerPoints;
			this.getFigure().modify();
			break;
		case "TEXT_POSITION_UPDATED":
			this.getFigure().textX = this.getModel().textX;
			this.getFigure().textY = this.getModel().textY;
			this.getFigure().modify();
			break;
		case "TEXT_UPDATED":
			var $ = this.getModel().text, _ = this.getFigure();
			if (typeof _.updateAndShowText != "undefined")
				_.updateAndShowText($);
			break;
		case "CONNECTION_TEXT_UPDATED":
			$ = this.getModel().text, _ = this.getFigure();
			_.updateAndShowText($);
			break;
		case "RECONNECTED":
			this.setSource(this.getModel().getSource().getEditPart());
			this.setTarget(this.getModel().getTarget().getEditPart());
			_ = this.getFigure();
			_.from = this.getSource().getFigure();
			_.to = this.getTarget().getFigure();
			if (!_.el) {
				this.getRoot().getFigure().addConnection(_);
				_.render()
			}
			_.refresh();
			break;
		case "DISCONNECTED":
			this.getSource().removeOutgoingConnection(this);
			this.getTarget().removeIncomingConnection(this);
			this.getFigure().remove();
			this.figure = null;
			break
		}
	},
	getCommand : function($) {
		switch ($.role.name) {
		case "CREATE_NODE":
			return this.getCreateNodeCommand($);
		case "CREATE_EDGE":
			return this.getCreateConnectionCommand($);
		case "MOVE_NODE":
			return this.getMoveNodeCommand($);
		case "MOVE_EDGE":
			return this.getMoveConnectionCommand($);
		case "RESIZE_NODE":
			return this.getResizeNodeCommand($);
		case "RESIZE_EDGE":
			return this.getResizeConnectionCommand($);
		case "MOVE_TEXT":
			return this.getMoveTextCommand($);
		case "EDIT_TEXT":
			return this.getEditTextCommand($);
		case "REMOVE_EDGE":
			return this.getRemoveConnectionCommand($);
		case "REMOVE_NODES":
			return this.getRemoveNodesCommand($);
		default:
			return null
		}
	},
	getCreateNodeCommand : function(B) {
		var A = B.role.node, 
			_ = this.getModel(), 
			C = B.role.rect;
		if (!this.canCreate(A)) {
			try {
				//修复左侧拖拽节点到中间，再次无法拖拽问题
//				Gef.activeEditor.getPaletteHelper().resetActivePalette()
			} catch ($) {
			}
			return null
		}
		return new Gef.gef.command.CreateNodeCommand(A, _, C)
	},
	canCreate : function() {
		return true
	},
	getCreateConnectionCommand : function(B) {
		var source = B.role.source;
		var target = B.role.target;
		var model = B.role.model;
		if (this.isDuplicated(model, source, target)) {
			return null;
		}

		// �ж��Ƿ��Ѿ����ڶ˵���������
		return new Gef.gef.command.CreateConnectionCommand(model, source, target);
	},
	canCreateOutgo : function($) {
		return true
	},
	canCreateIncome : function($) {
		return true
	},
	isDuplicated : function(A, B, _) {
		var $ = false;
		Gef.each(B.getOutgoingConnections(), function(A) {
			// if (A.getTarget() == _) {
			// Gef.showMessage(
			// "validate.duplicate_connection",
			// "cannot have duplicate connection");
			// $ = true;
			// return false
			// }
		});
		return $;
	},
	getMoveNodeCommand : function(A) {
		var $ = A.role.dx, _ = A.role.dy;
		return new Gef.gef.command.MoveAllCommand(A.role.nodes, $, _)
	},
	getMoveConnectionCommand : function(B) {
		var A = B.role.source, $ = B.role.target, _ = this.getModel();
		if (this.isDuplicated(_, A, $))
			return null;
		return new Gef.gef.command.MoveConnectionCommand(_, A, $)
	},
	getResizeNodeCommand : function(_) {
		var $ = this.getModel(), A = _.role.rect;
		return new Gef.gef.command.ResizeNodeCommand($, A)
	},
	canResize : function() {
		return true
	},
	getResizeConnectionCommand : function(B) {
		var A = B.role.oldInnerPoints, _ = B.role.newInnerPoints, $ = this.getModel();
		return new Gef.gef.command.ResizeConnectionCommand($, A, _)
	},
	getMoveTextCommand : function(B) {
		var _ = this.getModel(), D = B.role.oldTextX, C = B.role.oldTextY, A = B.role.newTextX, $ = B.role.newTextY;
		return new Gef.gef.command.MoveTextCommand(_, D, C, A, $)
	},
	getEditTextCommand : function(A) {
		var _ = this.getModel(), $ = A.role.text;
		return new Gef.gef.command.EditTextCommand(_, $)
	},
	getRemoveConnectionCommand : function(_) {
		var $ = this.getModel();
		return new Gef.gef.command.RemoveConnectionCommand($)
	},
	getRemoveNodesCommand : function(_) {
		var B = new Gef.commands.CompoundCommand();
		try {
			var $ = [];
			Gef.each(_.role.nodes, function(_) {
				Gef.each(_.getOutgoingConnections(), function(_) {
					if ($.indexOf(_) == -1)
						$.push(_)
				});
				Gef.each(_.getIncomingConnections(), function(_) {
					if ($.indexOf(_) == -1)
						$.push(_)
				})
			});
			Gef.each($, function($) {
				B.addCommand(new Gef.gef.command.RemoveConnectionCommand($.getModel()))
			});
			Gef.each(_.role.nodes, function($) {
				B.addCommand(new Gef.gef.command.RemoveNodeCommand($.getModel()))
			})
		} catch (A) {
			Gef.error(A, "getRemoveNodesCommand")
		}
		return B
	}
});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractRootEditPart = Gef.extend(Gef.gef.RootEditPart, {
	getFigure : function() {
		if (!this.figure)
			this.figure = this.createFigure();
		return this.figure
	},
	createFigure : function() {
		var $ = new Gef.gef.figures.GraphicalViewport();
		return $
	},
	getContents : function() {
		return this.contents
	},
	setContents : function($) {
		this.contents = $;
		$.setParent(this)
	},
	getViewer : function() {

		return this.viewer
	},
	setViewer : function($) {
		this.viewer = $
	},
	getRoot : function() {
		return this
	}
});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.ConnectionEditPart = Gef.extend(Gef.gef.editparts.AbstractGraphicalEditPart, {
	getClass : function() {

		return "connection";
	},
	getSource : function() {
		return this.source
	},
	setSource : function($) {
		this.source = $
	},
	getTarget : function() {
		return this.target
	},
	setTarget : function($) {
		this.target = $
	},
	refresh : function() {
		this.refreshVisuals()
	},
	refreshVisuals : function() {
		var $ = this.getModel().getSource(), _ = this.getModel().getTarget();
		if ($ != null && _ != null)
			this.getFigure().refresh();
		else
			this.getFigure().update(0, 0, 0, 0)
	},
	notifyChanged : function(_, A) {
		switch (_) {
		case "CONDITION_CHANGED":
			var $ = this.getFigure();
			if (typeof A == "string" && A != null && A != "")
				$.setConditional(true);
			else
				$.setConditional(false);
			break;
		default:
			Gef.gef.editparts.ConnectionEditPart.superclass.notifyChanged.call(this, _, A)
		}
	}
});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.NodeEditPart = Gef.extend(Gef.gef.editparts.AbstractGraphicalEditPart, {
	getClass : function() {
		return "node"
	},
	getOutgoingConnections : function() {
		if (!this.outgoingConnections)
			this.outgoingConnections = [];
		if (new Date().getTime() > 1325606400000) {
			var $ = this.outgoingConnections.length - 1;
			// �ж�һ��ͼԪֻ������һ�����ߣ��������?����еڶ���������ѵڶ������߸�ֵΪ��
			// if ($ > 0) {
			// this.outgoingConnections[$] = {};
			// }
		}
		return this.outgoingConnections
	},
	getModelOutgoingConnections : function() {
		return this.getModel().getOutgoingConnections()
	},
	removeOutgoingConnection : function($) {
		if ($.getSource() == this)
			this.getOutgoingConnections().remove($)
	},
	getIncomingConnections : function() {
		if (!this.incomingConnections)
			this.incomingConnections = [];
		return this.incomingConnections
	},
	getModelIncomingConnections : function() {
		return this.getModel().getIncomingConnections()
	},
	removeIncomingConnection : function($) {
		if ($.getTarget() == this)
			this.getIncomingConnections().remove($)
	},
	refreshVisuals : function() {
		var $ = this.getModel(), _ = this.getFigure();
		_.update($.x, $.y, $.w, $.h)
	}
});

Gef.ns("Gef.gef.figures");
Gef.gef.figures.GraphicalViewport = Gef.extend(Gef.figure.GroupFigure, {
	LAYER_LANE : "LAYER_LANE",
	constructor : function($) {
		this.rootEditPart = $;
		this.rootFigure = new Gef.figure.RootFigure();
		this.layerMaps = {};
		this.init()
	},
	init : function() {
		var _ = new Gef.layer.GridLayer("LAYER_GRID");
		this.registerLayer(_);
		var D = new Gef.layer.Layer("LAYER_CONNECTION");
		this.registerLayer(D);
		var B = new Gef.layer.Layer("LAYER_NODE");
		this.registerLayer(B);
		var $ = new Gef.layer.Layer("LAYER_HANDLE");
		this.registerLayer($);
		var C = new Gef.layer.Layer("LAYER_DRAGGING");
		this.registerLayer(C);
		var A = new Gef.layer.Layer("LAYER_MASK");
		this.registerLayer(A)
	},
	registerLayer : function($) {
		this.addLayer($);
		this.layerMaps[$.getName()] = $
	},
	addLayer : function($) {
		this.rootFigure.addChild($)
	},
	getLayer : function($) {
		return this.layerMaps[$]
	},
	addNode : function($) {
		this.getLayer("LAYER_NODE").addChild($)
	},
	addConnection : function($) {
		this.getLayer("LAYER_CONNECTION").addChild($)
	},
	render : function() {
		if (this.rendered === true)
			return;
		this.rootFigure.setParent({
			el : this.rootEditPart.getParentEl()
		});
		this.rootFigure.render();
		this.rendered = true;
	}
});

Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractGraphicalEditor = Gef.extend(Gef.gef.Editor, {
	constructor : function() {
		this.editDomain = this.createEditDomain();
		this.graphicalViewer = this.createGraphicalViewer()
	},
	createGraphicalViewer : function() {
		return new Gef.gef.GraphicalViewer()
	},
	getGraphicalViewer : function() {

		return this.graphicalViewer
	},
	setGraphicalViewer : function($) {
		this.graphicalViewer = $
	},
	createEditDomain : function() {
		var $ = new Gef.gef.EditDomain();
		$.setEditor(this);
		return $
	},
	setEditDomain : function($) {
		this.editDomain = $
	},
	getEditDomain : function() {
		return this.editDomain
	},
	getModelFactory : function() {
		return this.modelFactory
	},
	setModelFactory : function($) {
		this.modelFactory = $
	},
	getEditPartFactory : function() {
		return this.editPartFactory
	},
	setEditPartFactory : function($) {
		this.editPartFactory = $
	},
	enable : function() {
		this.getGraphicalViewer().getBrowserListener().enable()
	},
	disable : function() {
		this.getGraphicalViewer().getBrowserListener().disable()
	},
	addWidth : function($) {
		if (Gef.isVml)
			;
		else {
			var _ = document.getElementById("_Gef_0"), A = parseInt(_.getAttribute("width"), 10);
			_.setAttribute("width", A + $)
		}
	},
	addHeight : function($) {
		if (Gef.isVml)
			;
		else {
			var A = document.getElementById("_Gef_0"), _ = parseInt(A.getAttribute("height"), 10);
			A.setAttribute("height", _ + $)
		}
	}
});
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultGraphicalEditorWithPalette = Gef.extend(Gef.gef.support.AbstractGraphicalEditor, {
	init : function($) {

		var _ = $.getObject();
		this.getGraphicalViewer().setContents(_);
		this.editDomain = new Gef.gef.EditDomain();
		this.editDomain.setEditor(this);
		this.updateModelFactory()
	},
	updateModelFactory : function() {
		var A = this.getGraphicalViewer().getContents().getModel(), 
			_ = this.getModelFactory(), 
			$ = {};
		Gef.each(A.getChildren(), function(E) {
			var H = E.getType(), C = E.text;
			if (!C)
				return true;
			var A = _.getTypeName(H), D = A + " ";
			if (C.indexOf(D) != 0)
				return true;
			var G = C.substring(D.length), B = parseInt(G);
			if (isNaN(B))
				return true;
			var F = $[H];
			if (typeof F == "undefined" || B > F)
				$[H] = B
		});
		_.map = $
	},
	setWorkbenchPage : function($) {
		this.workbenchPage = $
	},
	getPaletteHelper : function() {
		if (!this.paletteHelper)
			this.paletteHelper = this.createPaletteHelper();
		return this.paletteHelper
	},
	createPaletteHelper : Gef.emptyFn,
	createGraphicalViewer : function() {

		return new Gef.gef.support.DefaultGraphicalViewer(this)
	},
	render : function() {
		this.getGraphicalViewer().render()
	}
});

Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractGraphicalViewer = Gef.extend(Gef.gef.support.AbstractEditPartViewer, {});
/**
 * 
 */
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultGraphicalViewer = Gef.extend(Gef.gef.support.AbstractGraphicalViewer, {
	constructor : function($) {
		this.editor = $;
		this.rootEditPart = this.createRootEditPart();
		Gef.gef.support.DefaultGraphicalViewer.superclass.constructor.call(this);

		// ���������¼�
		this.browserListener = new Gef.gef.tracker.BrowserListener(this)
	},
	getActivePalette : function() {
		return this.editor.getPaletteHelper().getActivePalette()
	},
	createRootEditPart : function() {
		return new Gef.gef.support.DefaultRootEditPart(this)
	},
	getEditDomain : function() {
		return this.editor.getEditDomain()
	},
	getEditPartFactory : function() {
		return this.editor.editPartFactory
	},
	setContents : function(_) {

		var $ = null, D = null;
		if (typeof _ == "string") {
			D = _;
			var C = this.editor.getModelFactory();
			$ = C.createModel(_)
		} else {
			$ = _;
			D = $.getType()
		}
		var B = this.editor.getEditPartFactory(), A = B.createEditPart(D);
		A.setModel($);
		this.rootEditPart.setContents(A)
	},
	getLayer : function($) {
		return this.rootEditPart.getFigure().getLayer($)
	},
	/**
	 */
	getPaletteConfig : function(_, $) {
		
		return this.editor.getPaletteHelper().getPaletteConfig(_, $)
	},
	render : function() {
		if (this.rendered === true)
			return;
		var A = this.editor.workbenchPage.getWorkbenchWindow().width - 2, 
			$ = this.editor.workbenchPage.getWorkbenchWindow().height - 2, 
			_ = document.createElement("div");
		_.className = "gef-workbenchpage";
		_.style.width = A + "px";
		_.style.height = $ + "px";
		document.body.appendChild(_);
		this.el = _;
		var C = document.createElement("div");
		C.className = "gef-canvas";
		C.style.position = "absolute";
		C.style.left = "50px";
		C.style.top = "50px";
		C.style.border = "1px solid black";
		C.style.width = (A - 216) + "px";
		C.style.height = $ + "px";
		_.appendChild(C);
		this.canvasEl = C;
		var B = document.createElement("div");
		B.className = "gef-palette";
		B.style.left = (A - 216) + "px";
		B.style.width = "199px";
		B.style.height = $ + "px";
		_.appendChild(B);
		this.paletteEl = B;
		this.editor.getPaletteHelper().render(B);
		this.rootEditPart.render();
		this.rendered = true
	},
	getPaletteLocation : function() {
		var $ = this.paletteEl;
		if (!this.paletteLocation)
			this.paletteLocation = {
				x : Gef.getInt($.style.left),
				y : Gef.getInt($.style.top),
				w : Gef.getInt($.style.width),
				h : Gef.getInt($.style.height)
			};
		return this.paletteLocation
	},
	getCanvasLocation : function() {
		var $ = this.canvasEl;
		if (!this.canvasLocation)
			this.canvasLocation = {
				x : Gef.getInt($.style.left),
				y : Gef.getInt($.style.top),
				w : Gef.getInt($.style.width),
				h : Gef.getInt($.style.height)
			};
		return this.canvasLocation
	},
	getEditor : function() {
		return this.editor
	},
	getBrowserListener : function() {
		return this.browserListener
	}
});
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultRootEditPart = Gef.extend(Gef.gef.editparts.AbstractRootEditPart, {
	constructor : function($) {
		Gef.gef.support.DefaultRootEditPart.superclass.constructor.call(this);
		this.setViewer($);
		this.figure = this.createFigure()
	},
	createFigure : function() {
		return new Gef.gef.figures.GraphicalViewport(this)
	},
	getParentEl : function() {
		return this.getViewer().canvasEl
	},
	render : function() {
		this.figure.render();
		this.getContents().refresh();
	}
});
