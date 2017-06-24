/**
 * 继承默认工作台
 */
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultWorkbenchWindow = Gef.extend(Gef.ui.WorkbenchWindow, {
	/**
	 * 重写父类方法返回 Gef.ui.support.DefaultWorkbenchPage
	 * 
	 * @return {}
	 */
	getActivePage : function() {
		if (!this.activePage) {
			this.activePage = new Gef.ui.support.DefaultWorkbenchPage();
			this.activePage.setWorkbenchWindow(this);
		}
		return this.activePage;
	},
	/**
	 * 设置页面宽高
	 */
	render : function() {

		if (!this.rendered) {
			document.getElementsByTagName("html")[0].className += " gef-workbenchwindow";
			if (Gef.isIE) {
				this.width = document.body.offsetWidth;
				this.height = document.body.offsetHeight
			} else {
				this.width = window.innerWidth;
				this.height = window.innerHeight
			}
			this.getActivePage().render();
			this.rendered = true;
		}
	},
	/**
	 * 设置是否刷新
	 */
	setRendered : function(rendered) {
		this.rendered = rendered;
	}
});
/**
 * 
 * 继承默认工作页面
 */
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultWorkbenchPage = Gef.extend(Gef.ui.WorkbenchPage, {
			constructor : function(defaultWorkbenchWindow) {
				this.workbenchWindow = defaultWorkbenchWindow;
			},
			getWorkbenchWindow : function() {
				return this.workbenchWindow;
			},
			setWorkbenchWindow : function(defaultWorkbenchWindow) {
				this.workbenchWindow = defaultWorkbenchWindow;

			},
			getActiveEditor : function() {
				return this.activeEditor;
			},
			openEditor : function(extEditor, jBSEditorInput) {
				this.activeEditor = extEditor;
				extEditor.setWorkbenchPage(this);
				extEditor.init(jBSEditorInput);
			},
			render : function() {
				this.activeEditor.render();
			}
		});
/**
 * 
 */
Gef.ns("Gef.ui");
Gef.ui.EditorPart = Gef.extend(Gef.ui.WorkbenchPart, {
			init : Gef.emptyFn
		});
Gef.ns("Gef.jbs");
Gef.jbs.JBSEditorInput = Gef.extend(Gef.ui.EditorInput, {
			constructor : function($) {
				if (!$)
					$ = "process";
				this.processModel = $
			},
			readXml : function(domXml) {
				var _ = new Gef.jbs.xml.JBSDeserializer(domXml);
				this.processModel = _.decode();

			},
			getName : function() {
				return this.processModel.name
			},
			getObject : function() {

				return this.processModel
			}
		});

/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.GraphicalViewer = Gef.extend(Gef.gef.EditPartViewer, {});
/**
 * 
 */
Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractEditPartViewer = Gef.extend(Gef.gef.EditPartViewer, {
			getContents : function() {
				return this.rootEditPart.getContents();
			},
			setContents : function($) {
				this.rootEditPart.setContents($)
			},
			getRootEditPart : function() {
				return this.rootEditPart
			},
			setRootEditPart : function($) {
				this.rootEditPart = $
			},
			getEditDomain : Gef.emptyFn,
			setEditDomain : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.RootEditPart = Gef.extend(Gef.gef.EditPart, {
			getContents : Gef.emptyFn,
			setContents : Gef.emptyFn,
			getViewer : Gef.emptyFn,
			setViewer : Gef.emptyFn
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractEditPart = Gef.extend(Gef.gef.EditPart, {
			constructor : function() {
				this.children = []
			},
			getParent : function() {
				return this.parent
			},
			setParent : function($) {
				this.parent = $
			},
			getRoot : function() {
				return this.getParent().getRoot()
			},
			getChildren : function() {
				return this.children
			},
			setChildren : function($) {
				this.children = $
			},
			addChild : function($) {
				this.children.push($);
				$.setParent(this);
				this.addChildVisual($)
			},
			removeChild : function($) {
				this.removeChildVisual($);
				$.setParent(null);
				this.children.remove($)
			},
			addChildVisual : Gef.emptyFn,
			removeChildVisual : Gef.emptyFn,
			createChild : function($) {
				var _ = this.createEditPart($);
				return _
			},
			findOrCreateConnection : function($) {
				var _ = this.findOrCreateEditPart($);
				_.setSource($.getSource().getEditPart());
				_.setTarget($.getTarget().getEditPart());
				_.setParent(this.getRoot());
				this.addChildVisual(_);
				return _
			},
			createEditPart : function($) {
			
				return this.getViewer().editor.getEditDomain()
						.createEditPart($)
			},
			findOrCreateEditPart : function($) {
				return this.getViewer().editor.getEditDomain()
						.findOrCreateEditPart($)
			},
			getFigure : function() {
				if (this.figure == null)
					this.figure = this.createFigure();
				return this.figure
			},
			createFigure : Gef.emptyFn,
			getModel : function() {
				return this.model
			},
			setModel : function($) {
				this.model = $;
				$.setEditPart(this);
				$.addChangeListener(this)
			},
			getModelChildren : function() {
				return this.model != null && this.model.children != null
						? this.model.children
						: Gef.emptyArray
			},
			getCommand : Gef.emptyFn,
			refresh : function() {
				this.refreshVisuals();
				this.refreshChildren()
			},
			refreshVisuals : Gef.emptyFn,
			refreshChildren : function() {
				var A = {};
				for (var C = 0; C < this.getChildren().length; C++) {
					var $ = this.getChildren()[C];
					A[$.getModel().getId()] = $
				}
				for (C = 0; C < this.getModelChildren().length; C++) {
					var _ = this.getModelChildren()[C], B = A[_.getId()];
					if (B == null) {
						B = this.createChild(_);
						this.addChild(B)
					}
					B.refresh()
				}
			},
			getViewer : function() {

				return this.getRoot().getViewer()
			}
		});
Gef.ns("Gef.jbs");
Gef.jbs.JBSPaletteHelper = Gef.extend(Gef.gef.support.PaletteHelper, {
			constructor : function($) {
				this.editor = $
			},
			createSource : function() {
				var $ = this;
				return {
					title : "palette",
					buttons : [{
								text : "export",
								handler : function() {
									($.editor.serial())
								}
							}, {
								text : "clear",
								handler : function() {
									$.editor.clear()
								}
							}, {
								text : "reset",
								handler : function() {
									$.editor.reset()
								}
							}],
					groups : [{
								title : "Operations",
								items : [{
											text : "Select",
											iconCls : "gef-tool-select",
											creatable : false
										}, {
											text : "Marquee",
											iconCls : "gef-tool-marquee",
											creatable : false
										}]
							}, {
								title : "Activities",
								items : [{
											text : "transition",
											iconCls : "gef-tool-transition",
											creatable : false,
											isConnection : true
										}, {
											text : "auto",
											iconCls : "gef-tool-java",
											w : 90,
											h : 50
										}, {
											text : "human",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "counter-sign",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "start",
											iconCls : "gef-tool-start",
											w : 48,
											h : 48
										}, {
											text : "end",
											iconCls : "gef-tool-end",
											w : 48,
											h : 48
										}, {
											text : "cancel",
											iconCls : "gef-tool-cancel",
											w : 48,
											h : 48
										}, {
											text : "error",
											iconCls : "gef-tool-error",
											w : 48,
											h : 48
										}, {
											text : "state",
											iconCls : "gef-tool-state",
											w : 90,
											h : 50
										}, {
											text : "hql",
											iconCls : "gef-tool-hql",
											w : 90,
											h : 50
										}, {
											text : "sql",
											iconCls : "gef-tool-sql",
											w : 90,
											h : 50
										}, {
											text : "java",
											iconCls : "gef-tool-java",
											w : 90,
											h : 50
										}, {
											text : "script",
											iconCls : "gef-tool-script",
											w : 90,
											h : 50
										}, {
											text : "task",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "decision",
											iconCls : "gef-tool-decision",
											w : 48,
											h : 48
										}, {
											text : "fork",
											iconCls : "gef-tool-fork",
											w : 48,
											h : 48
										}, {
											text : "join",
											iconCls : "gef-tool-join",
											w : 48,
											h : 48
										}, {
											text : "mail",
											iconCls : "gef-tool-mail",
											w : 90,
											h : 50
										}, {
											text : "custom",
											iconCls : "gef-tool-custom",
											w : 90,
											h : 50
										}, {
											text : "subProcess",
											iconCls : "gef-tool-subProcess",
											w : 90,
											h : 50
										}, {
											text : "group",
											iconCls : "gef-tool-group",
											w : 90,
											h : 50
										}, {
											text : "jms",
											iconCls : "gef-tool-jms",
											w : 90,
											h : 50
										}, {
											text : "ruleDecision",
											iconCls : "gef-tool-ruleDecision",
											w : 48,
											h : 48
										}, {
											text : "rules",
											iconCls : "gef-tool-rules",
											w : 90,
											h : 50
										}, {
											text : "foreach",
											iconCls : "gef-tool-foreach",
											w : 48,
											h : 48
										}]
							}]
				}
			},
			getSource : function() {
				if (!this.source) {
					this.source = this.createSource();
				}
				return this.source;
			},
			render : function(O) {

				var C = this.getSource(), K = document.createElement("div");
				K.className = "gef-drag-handle";
				O.appendChild(K);
				var $ = document.createElement("span");
				K.appendChild($);
				$.unselectable = "on";
				$.innerHTML = C.title;
				var L = this;
				for (var F = 0; F < C.buttons.length; F++) {
					var I = C.buttons[F], _ = document.createElement("a");
					_.href = "javascript:void(0);";
					_.onclick = I.handler;
					_.innerHTML = "|" + I.text + "|";
					$.appendChild(_)
				}
				var A = document.createElement("ul");
				O.appendChild(A);
				for (F = 0; F < C.groups.length; F++) {
					var M = C.groups[F], D = document.createElement("li");
					D.className = "gef-palette-bar";
					A.appendChild(D);
					var H = document.createElement("div");
					H.unselectable = "on";
					H.innerHTML = M.title;
					D.appendChild(H);
					var N = document.createElement("ul");
					D.appendChild(N);
					for (var E = 0; E < M.items.length; E++) {
						var J = M.items[E], G = document.createElement("li");
						G.id = J.text;
						G.className = "gef-palette-item";
						N.appendChild(G);
						var B = document.createElement("span");
						B.innerHTML = J.text;
						B.className = J.iconCls;
						B.unselectable = "on";
						G.appendChild(B)
					}
				}
			},
			getActivePalette : function() {

				return this.activePalette
			},
			setActivePalette : function($) {
				this.activePalette = $
			},
			getPaletteConfig : function(D, _) {
				var $ = _.parentNode.id;
				if (!$)
					return null;
				var B = this.getSource(), E = null;
				Gef.each(B.groups, function(_) {
							Gef.each(_.items, function(_) {
										if (_.text == $) {
											E = _;
											return false
										}
									});
							if (E != null)
								return false
						});
				if (!E)
					return null;
				var A = null;
				if (this.getActivePalette()) {
					var C = this.getActivePalette().text;
					A = document.getElementById(C);
					A.style.background = "white"
				}
				this.setActivePalette(E);
				A = document.getElementById($);
				A.style.background = "#CCCCCC";
				if (E.creatable === false)
					return null;
				return E
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.AbstractRequestTracker = Gef.extend(
		Gef.gef.tracker.RequestTracker, {
			constructor : function($) {
				this.browserListener = $;
				this.reset();
			},
			reset : function() {
				this.status = "NONE";
				this.temp = {};
				this.browserListener.activeTracker = null
			},
			getDraggingRect : function() {
				if (!this.draggingRect) {
					this.draggingRect = new Gef.figure.DraggingRectFigure({
								x : -90,
								y : -90,
								w : 48,
								h : 48
							});
					this.getDraggingLayer().addChild(this.draggingRect);
					this.draggingRect.render()
				}
				return this.draggingRect
			},
			createDraggingRects : function() {
				if (!this.draggingRects)
					this.draggingRects = [];
				var $ = new Gef.figure.DraggingRectFigure({
							x : -90,
							y : -90,
							w : 48,
							h : 48
						});
				this.getDraggingLayer().addChild($);
				$.render();
				this.draggingRects.push($);
				return $
			},
			getDraggingRects : function($) {
				return this.draggingRects[$]
			},
			removeDraggingRects : function($) {
				if (!this.draggingRects)
					this.draggingRects = [];
				Gef.each(this.draggingRects, function($) {
							$.remove()
						}, this);
				this.draggingRects = []
			},
			getDraggingEdge : function() {
				if (!this.draggingEdge) {
					this.draggingEdge = new Gef.figure.DraggingEdgeFigure({
								x1 : -1,
								y1 : -1,
								x2 : -1,
								y2 : -1
							});
					this.getDraggingLayer().addChild(this.draggingEdge);
					this.draggingEdge.render()
				}
				return this.draggingEdge
			},
			isInPalette : function($) {
				return this
						.isIn($, this.getViewer().getPaletteLocation(), true)
			},
			isInCanvas : function($) {
				return this.isIn($, this.getViewer().getCanvasLocation(), true)
			},
			isIn : function(_, A, $) {
				if ($ === true)
					return _.absoluteX > A.x && _.absoluteX < A.x + A.w
							&& _.absoluteY > A.y && _.absoluteY < A.y + A.h;
				else
					return _.x > A.x && _.x < A.x + A.w && _.y > A.y
							&& _.y < A.y + A.h
			},
			getPaletteConfig : function($) {
				return this.getViewer().getPaletteConfig($.point, $.target)
			},
			findEditPartAt : function(H) {
				var I = H.point, B = null, _ = this.browserListener
						.getSelectionManager().getDefaultSelected();
				if (_) {
					var J = this.browserListener.getSelectionManager()
							.findNodeHandle(_);
					if (J && J.getDirectionByPoint)
						if (J.getDirectionByPoint(I))
							return _
				}
				Gef.each(this.getConnectionLayer().getChildren(), function(_) {
					for (var F = 0, E = _.points.length - 1; F < E; F++) {
						var C = _.points[F], A = _.points[F + 1], D = new Geom.Line(
								C[0], C[1], A[0], A[1]), $ = D
								.getPerpendicularDistance(I.x, I.y);
						if ($ < 8) {
							B = this.getEditPartByFigure(_);
							return false
						}
					}
				}, this);
				if (B)
					return B;
				var A = this.getNodeLayer().getChildren();
				for (var C = A.length - 1; C >= 0; C--) {
					var E = A[C], D = H.target.getAttribute("id");
					if (this.isIn(I, E) && D != null
							&& D.indexOf("_Gef_") != -1) {
						B = this.getEditPartByFigure(E);
						return B
					}
				}
				B = this.getContents();
				var F = H.target, G = F.getAttribute("edgeId");
				if (G != null)
					if (F.tagName == "text" || F.tagName == "textbox") {
						var $ = null, $ = this.getConnectionByConnectionId(G);
						if ($ != null)
							B = $.editPart
					}
				return B
			},
			getViewer : function() {
				return this.browserListener.getViewer()
			},
			getEditor : function() {
				return this.getViewer().getEditor()
			},
			getContents : function() {
				return this.getViewer().getContents()
			},
			getModelFactory : function() {
				return this.getEditor().getModelFactory()
			},
			getCommandStack : function() {
				return this.getViewer().getEditDomain().getCommandStack()
			},
			executeCommand : function(A, $) {

				var _ = A.getCommand($);

				if (_ != null) {

					this.getCommandStack().execute(_)
				}

			},
			getDraggingLayer : function() {
				return this.getViewer().getLayer("LAYER_DRAGGING")
			},
			getNodeLayer : function() {
				return this.getViewer().getLayer("LAYER_NODE")
			},
			getConnectionLayer : function() {
				return this.getViewer().getLayer("LAYER_CONNECTION")
			},
			getHandleLayer : function() {
				return this.getViewer().getLayer("LAYER_HANDLE")
			},
			getTargetEditPart : function() {
				return this.getContents()
			},
			getEditPartByFigure : function($) {
				return $.editPart
			},
			isConnection : function() {
				return this.getViewer().getActivePalette() != null
						&& this.getViewer().getActivePalette().isConnection === true
			},
			notConnection : function() {
				return !this.isConnection()
			},
			getSelectionManager : function() {
				return this.browserListener.getSelectionManager()
			},
			getSelectedNodes : function() {
				return this.getSelectionManager().getSelectedNodes()
			},
			hasSelectedNoneOrOne : function() {
				return this.getSelectionManager().getSelectedCount() < 2
			},
			isMultiSelect : function($) {
				return $.e.ctrlKey === true
			},
			notMultiSelect : function($) {
				return !this.isMultiSelect($)
			},
			getConnectionByConnectionId : function(_) {
				var $ = null;
				Gef.each(this.getConnectionLayer().getChildren(), function(A) {
							if (_ == A.el.id)
								$ = A
						}, this);
				return $
			},
			getNodeByNodeId : function(_) {
				var $ = null;
				Gef.each(this.getNodeLayer().getChildren(), function(A) {
							if (_ == A.el.id)
								$ = A
						}, this);
				return $
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.DefaultSelectionListener = Gef.extend(
		Gef.gef.tracker.SelectionListener, {
			selectionChanged : function(A, $, _) {
				if (A.length == 1) {
					var B = A[0];
					if (B == _)
						this.selectDefault(_);
					else if (B.getClass() == "node")
						this.selectNode(B);
					else
						this.selectConnection(B)
				} else
					this.selectDefault(_)
			},
			selectNode : Gef.emptyFn,
			selectConnection : Gef.emptyFn,
			selectDefault : Gef.emptyFn
		});
Gef.ns("Gef.tool");
Gef.tool.AbstractImageTool = Gef.extend(Gef.tool.AbstractTool, {
			getKey : function() {
				return "abstractImageTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/new_event.png"
			},
			getNodeConfig : function() {
				return {
					text : "node",
					w : 48,
					h : 48
				}
			},
			getX : function($) {
				return $ + 5
			},
			getY : function($) {
				return 15
			},
			renderVml : function(A, $) {
				var _ = document.createElement("img");
				_.setAttribute("id", this.getId($));
				_.setAttribute("unselectable", "on");
				_.style.position = "absolute";
				_.style.left = this.getX($.w) + "px";
				_.style.top = this.getY($.h) + "px";
				_.style.width = "16px";
				_.style.height = "16px";
				_.setAttribute("opacity", "0.5");
				_.src = this.getUrl();
				A.appendChild(_);
				this.el = _;
				_.onmouseover = function() {
					_.setAttribute("opacity", "1.0")
				};
				_.onmouseout = function() {
					_.setAttribute("opacity", "0.5")
				}
			},
			renderSvg : function(A, $) {
				var _ = document.createElementNS(Gef.svgns, "image");
				_.setAttribute("id", this.getId($));
				_.setAttribute("x", this.getX($.w));
				_.setAttribute("y", this.getY($.h));
				_.setAttribute("width", 16);
				_.setAttribute("height", 16);
				_.setAttributeNS(Gef.linkns, "xlink:href", this.getUrl());
				_.setAttribute("opacity", "0.5");
				A.appendChild(_);
				this.el = _;
				_.addEventListener("mouseover", function() {
							_.setAttribute("opacity", "1.0")
						}, false);
				_.addEventListener("mouseout", function() {
							_.setAttribute("opacity", "0.5")
						}, false)
			},
			resizeVml : function(B, A, $, _) {
				this.el.style.left = this.getX($) + "px";
				this.el.style.top = this.getY(_) + "px"
			},
			resizeSvg : function(B, A, $, _) {
				this.el.setAttribute("x", this.getX($));
				this.el.setAttribute("y", this.getY(_))
			},
			isClickedVml : function(A) {
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "IMG" && $ == this.getId())
					return true
			},
			isClickedSvg : function(A) {
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "image" && $ == this.getId())
					return true
			},
			drag : function(_) {
				var A = this.getNodeConfig();
				_.getDraggingRect().name = A.text;
				var $ = A.w, C = A.h;
				if (isNaN($) || $ < 0)
					$ = 48;
				if (isNaN(C) || C < 0)
					C = 48;
				var D = $ * -1, B = C * -1;
				_.getDraggingRect().update(D, B, $, C)
			},
			move : function(_, B) {
				var $ = _.getDraggingRect(), A = B.point, D = A.x - $.w / 2, C = A.y
						- $.h / 2;
				$.moveTo(D, C)
			},
			drop : function(A, B) {
				var $ = A.getDraggingRect();
				if (A.isInCanvas(B.point)) {
					var D = $.name;
					B.role = {
						name : "CREATE_NODE",
						rect : {
							x : B.point.x - $.w / 2,
							y : B.point.y - $.h / 2,
							w : $.w,
							h : $.h
						},
						node : A.getModelFactory().createModel(D)
					};
					var C = new Gef.commands.CompoundCommand();
					C.addCommand(new Gef.gef.command.CreateNodeCommand(
							B.role.node, A.getContents().getModel(),
							B.role.rect));
					var _ = this.node.editPart.getModel(), G = B.role.node, E = A
							.getModelFactory().createModel(this
									.getConnectionModelName());
					if (_.getOutgoingConnections().length > 0)
						E.text = "to " + G.text;
					else
						E.text = "";
					C.addCommand(new Gef.gef.command.CreateConnectionCommand(E,
							_, G));
					A.getCommandStack().execute(C)
				}
				var H = $.w * -1, F = $.h * -1;
				$.moveTo(H, F)
			}
		});
Gef.ns("Gef.jbs.xml");
Gef.jbs.xml.JBSDeserializer = Gef.extend(Gef.gef.xml.XmlDeserializer, {
	decode : function() {
		this.modelMap = {};
		this.domMap = {};
		
		var $ = new Gef.jbs.model.ProcessModel();
		this.parseRoot($);
		
		delete this.modelMap;
		delete this.domMap;
		return $
	},
	parseRoot : function(_) {
		var $ = this.xdoc.documentElement;
		_.decode($, []);
		Gef.each($.childNodes, function($) {
					this.parseNodes($, _)
				}, this);
		Gef.each(_.getChildren(), function($) {
					this.parseConnections($)
				}, this)
	},
	parseNodes : function(nodeDom, rootModel) {
		
		var nodeName = nodeDom.nodeName;
		var nodeModel = Gef.jbs.JBSModelFactory._modelLib[nodeName]	? eval("new " + Gef.jbs.JBSModelFactory._modelLib[nodeName]+ "()"): null;
		nodeModel && this.decodeNodeModel(nodeModel, nodeDom, rootModel);
	},
	parseConnections : function($) {
		var _ = this.domMap[$.text];
		Gef.each(_.childNodes, function(_) {
					if (_.nodeName == "transition"
							|| _.nodeName == "dashedArrows"
							|| _.nodeName == "line"
							|| _.nodeName == "dashedLine"
							|| _.nodeName == "doubleArrowsLine"
							|| _.nodeName == "doubleArrowsDashed") {

						this.parseConnection(_, $);
					}
				}, this)
	},
	parseConnection : function(A, _) {
		var nodeName = A.nodeName.replace(/^[a-z]/, function(text) {
					return text.toLocaleUpperCase();
				});

		var B = eval("new Gef.jbs.model." + nodeName + "Model()");
		B.decode(A);
		var $ = A.getAttribute("to"), C = this.modelMap[$];
		if (!C) {
			Gef.error("cannot find targetModel for sourceModel[" + _.text
							+ "], to[" + $ + "]",
					"JBSDeserializer.parseConnection()");
			return;
		}
		B.setSource(_);
		_.addOutgoingConnection(B);
		B.setTarget(C);
		C.addIncomingConnection(B);
		Gef.model.JpdlUtil.decodeConnectionPosition(B);
	}
});
Gef.ns("Gef.model");
Gef.model.NodeModel = Gef.extend(Gef.model.Model, {
	CHILD_ADDED : "CHILD_ADDED",
	NODE_MOVED : "NODE_MOVED",
	NODE_RESIZED : "NODE_RESIZED",
	TEXT_UPDATED : "TEXT_UPDATED",
	CONNECTION_SOURCE_ADDED : "CONNECTION_SOURCE_ADDED",
	CONNECTION_TARGET_ADDED : "CONNECTION_TARGET_ADDED",
	CHILD_REMOVED_FROM_PARENT : "CHILD_REMOVED_FROM_PARENT",
	constructor : function($) {
		
		this.text = "untitled";
		this.x = 0;
		this.y = 0;
		this.w = 0;
		this.h = 0;
		this.children = [];
		this.outgoingConnections = [];
		this.incomingConnections = [];
		Gef.model.NodeModel.superclass.constructor.call(this, $)
	},
	getText : function() {
		return this.text
	},
	setParent : function($) {
		this.parent = $
	},
	getParent : function() {
		return this.parent
	},
	setChildren : function($) {
		this.children = $
	},
	getChildren : function() {
		return this.children
	},
	addChild : function($) {
		this.children.push($);
		$.setParent(this);
		this.notify(this.CHILD_ADDED, $)
	},
	removeChild : function($) {
		this.children.remove($);
		$.setParent(null);
		$.notify("CHILD_REMOVED_FROM_PARENT", $)
	},
	getOutgoingConnections : function() {
		return this.outgoingConnections;
	},
	getIncomingConnections : function() {
		return this.incomingConnections
	},
	addOutgoingConnection : function(model) {

		if (model.getSource() == this
				&& this.outgoingConnections.indexOf(model) == -1) {
			this.outgoingConnections.push(model);
			this.notify(this.CONNECTION_SOURCE_ADDED);
		}
	},
	addIncomingConnection : function($) {
		if ($.getTarget() == this && this.incomingConnections.indexOf($) == -1) {
			this.incomingConnections.push($);
			this.notify(this.CONNECTION_TARGET_ADDED)
		}
	},
	removeOutgoingConnection : function($) {
		if ($.getSource() == this && this.outgoingConnections.indexOf($) != -1)
			this.outgoingConnections.remove($)
	},
	removeIncomingConnection : function($) {
		if ($.getTarget() == this && this.incomingConnections.indexOf($) != -1)
			this.incomingConnections.remove($)
	},
	moveTo : function(_, $) {
		this.x = _;
		this.y = $;

		this.notify(this.NODE_MOVED)
	},
	resize : function(B, A, $, _) {
		this.x = B;
		this.y = A;
		this.w = $;
		this.h = _;
		this.notify(this.NODE_RESIZED)
	},
	updateText : function($) {
		this.text = $;
		this.notify(this.TEXT_UPDATED)
	},
	removeForParent : function() {
		if (!this.parent)
			return;
		this.parent.removeChild(this);
		this.notify(this.CHILD_REMOVED_FROM_PARENT)
	}
});
Gef.ns("Gef.model");
Gef.model.ConnectionModel = Gef.extend(Gef.model.Model, {
			RECONNECTED : "RECONNECTED",
			DISCONNECTED : "DISCONNECTED",
			CONNECTION_RESIZED : "CONNECTION_RESIZED",
			CONNECTION_TEXT_UPDATED : "CONNECTION_TEXT_UPDATED",
			TEXT_POSITION_UPDATED : "TEXT_POSITION_UPDATED",
			SOURCE_CHANGED : "SOURCE_CHANGED",
			TARGET_CHANGED : "TARGET_CHANGED",
			constructor : function($) {
				this.x1 = 0;
				this.y1 = 0;
				this.x2 = 0;
				this.y2 = 0;
				this.text = "untitled";
				this.textX = 0;
				this.textY = 0;
				this.innerPoints = [];
				Gef.model.ConnectionModel.superclass.constructor.call(this, $)
			},
			getText : function() {
				return this.text
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
			reconnect : function() {
				this.notify(this.RECONNECTED);
				this.source.addOutgoingConnection(this);
				this.target.addIncomingConnection(this)
			},
			disconnect : function() {
				this.notify(this.DISCONNECTED);
				this.source.removeOutgoingConnection(this);
				this.target.removeIncomingConnection(this)
			},
			resizeConnection : function($) {
				this.innerPoints = $;
				this.notify(this.CONNECTION_RESIZED)
			},
			updateText : function($) {
				this.text = $;
				this.notify(this.CONNECTION_TEXT_UPDATED)
			},
			updateTextPosition : function(_, $) {
				this.textX = _;
				this.textY = $;
				this.notify(this.TEXT_POSITION_UPDATED)
			},
			changeSource : function($) {
				var _ = this.source;
				this.source = $;
				$.addOutgoingConnection(this);
				_.removeOutgoingConnection(this);
				this.notify(this.SOURCE_CHANGED, {
							newSource : $,
							oldSource : _
						})
			},
			changeTarget : function(_) {
				var $ = this.target;
				this.target = _;
				_.addIncomingConnection(this);
				$.removeIncomingConnection(this);
				this.notify(this.TARGET_CHANGED, {
							newTarget : _,
							oldTarget : $
						})
			}
		});