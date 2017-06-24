Gef.ns('Gef.jbs');
Gef.jbs.ExtEditor = Gef.extend(Gef.jbs.JBSEditor, {
	constructor : function() {
		Gef.jbs.ExtEditor.superclass.constructor.call(this);
		this.modelFactory = new Gef.jbs.JBSModelFactory();
		this.editPartFactory = new Gef.jbs.JBSEditPartFactory();
	},

	createGraphicalViewer : function() {
		return new Gef.jbs.ExtGraphicalViewer(this);
	},

	getPaletteHelper : function() {
		if (!this.paletteHelper) {
			this.paletteHelper = new Gef.jbs.ExtPaletteHelper(this);
		}
		return this.paletteHelper;
	},

	addSelectionListener : function(selectionListener) {
		this.getGraphicalViewer().getBrowserListener().selectionListenerTracker.addSelectionListener(selectionListener);
	},

	enable : function() {
		this.getGraphicalViewer().getBrowserListener().enable();
	},

	disable : function() {
		this.getGraphicalViewer().getBrowserListener().disable();
	}
});

Gef.override(Gef.jbs.tool.ChangeTypeTool, {
	handleMenuClick : function(item, e) {
		var toolTracker = this.toolTracker;

		var type = item.changedType;
		var oldModel = this.node.editPart.model;
		var newModel = toolTracker.getModelFactory().createModel(type);

		var compoundCommand = new Gef.commands.CompoundCommand();
		compoundCommand.addCommand(new Gef.gef.command.CreateNodeCommand(newModel, oldModel.getParent(), {
			x : oldModel.x,
			y : oldModel.y,
			w : oldModel.w,
			h : oldModel.h
		}));

		Gef.each(oldModel.getIncomingConnections(), function(connection) {
			var connectionType = connection.getType();
			var newConnection = toolTracker.getModelFactory().createModel(connectionType);
			newConnection.text = connection.text;

			compoundCommand.addCommand(new Gef.gef.command.RemoveConnectionCommand(connection));
			compoundCommand.addCommand(new Gef.gef.command.CreateConnectionCommand(newConnection, connection.getSource(), newModel));
			compoundCommand.addCommand(new Gef.gef.command.ResizeConnectionCommand(newConnection, [], connection.innerPoints));
		});
		Gef.each(oldModel.getOutgoingConnections(), function(connection) {
			var connectionType = connection.getType();
			var newConnection = toolTracker.getModelFactory().createModel(connectionType);
			newConnection.text = connection.text;

			compoundCommand.addCommand(new Gef.gef.command.RemoveConnectionCommand(connection));
			compoundCommand.addCommand(new Gef.gef.command.CreateConnectionCommand(newConnection, newModel, connection.getTarget()));
			compoundCommand.addCommand(new Gef.gef.command.ResizeConnectionCommand(newConnection, [], connection.innerPoints));
		});

		compoundCommand.addCommand(new Gef.gef.command.RemoveNodeCommand(oldModel));

		toolTracker.getCommandStack().execute(compoundCommand);

		toolTracker.getSelectionManager().addSelectedNode(newModel.editPart);
	},

	drag : function(toolTracker, request) {
		this.toolTracker = toolTracker;
		var items = [];
		Gef.each(this.allowedTypes, function(item) {
			if (item.type == this.node.editPart.model.getType()) {
				return true;
			}
			items.push({
				text : item.name,
				changedType : item.type,
				handler : this.handleMenuClick,
				scope : this
			});
		}, this);

		var contextMenu = new Ext.menu.Menu({
			items : items
		});
		contextMenu.showAt([ request.point.absoluteX, request.point.absoluteY ]);
	},

	move : function(toolTracker, request) {
	},

	drop : function(toolTracker, request) {
	}
});

Gef.ns("Gef.jbs");
Gef.jbs.ExtGraphicalViewer = Gef.extend(Gef.gef.support.DefaultGraphicalViewer, {
	render : function() {
		this.canvasEl = Ext.getDom('__gef_jbs_center__');
		this.rootEditPart.render();
		this.rendered = true;
	},

	getPaletteLocation : function() {
		if (!this.paletteLocation) {
			var paletteBox = Ext.get('__gef_jbs_palette__').getBox();
			this.paletteLocation = {
				x : paletteBox.x,
				y : paletteBox.y,
				w : paletteBox.width,
				h : paletteBox.height
			};
		}
		return this.paletteLocation;
	},

	getCanvasLocation : function() {
		// if (!this.canvasLocation) {
		var box = Ext.get('__gef_jbs_center__').getBox();
		var scroll = Ext.get('__gef_jbs_center__').getScroll();
		this.canvasLocation = {
			x : box.x,
			y : box.y,
			w : box.width,
			h : box.height
		};
		// }
		return this.canvasLocation;
	}
});

Gef.jbs.ExtPaletteHelper = Gef.extend(Gef.jbs.JBSPaletteHelper, {
	createSource : function() {

		return {
			select : {
				text : 'select',
				creatable : false
			},
			transition : {
				text : 'transition',
				creatable : false,
				isConnection : true
			},
			start : {
				text : 'start',
				w : 48,
				h : 48
			},
			end : {
				text : 'end',
				w : 48,
				h : 48
			},
			cancel : {
				text : 'cancel',
				w : 48,
				h : 48
			},
			error : {
				text : 'error',
				w : 48,
				h : 48
			},
			state : {
				text : 'state',
				w : 90,
				h : 50
			},
			task : {
				text : 'task',
				w : 90,
				h : 50
			},
			decision : {
				text : 'decision',
				w : 48,
				h : 48
			},
			fork : {
				text : 'fork',
				w : 48,
				h : 48
			},
			join : {
				text : 'join',
				w : 48,
				h : 48
			},
			java : {
				text : 'java',
				w : 90,
				h : 50
			},
			script : {
				text : 'script',
				w : 90,
				h : 50
			},
			hql : {
				text : 'hql',
				w : 90,
				h : 50
			},
			sql : {
				text : 'sql',
				w : 90,
				h : 50
			},
			custom : {
				text : 'custom',
				w : 90,
				h : 50
			},
			mail : {
				text : 'mail',
				w : 90,
				h : 50
			},
			subProcess : {
				text : 'subProcess',
				w : 90,
				h : 50
			},
			jms : {
				text : 'jms',
				w : 90,
				h : 50
			},
			ruleDecision : {
				text : 'ruleDecision',
				w : 48,
				h : 48
			},
			rules : {
				text : 'rules',
				w : 90,
				h : 50
			},
			auto : {
				text : 'auto',
				w : 90,
				h : 50
			},
			human : {
				text : 'human',
				w : 90,
				h : 50
			},
			'counter-sign' : {
				text : 'counter-sign',
				w : 90,
				h : 50
			},
			foreach : {
				text : 'foreach',
				w : 48,
				h : 48
			}
		};
	},

	getSource : function() {
		if (!this.source) {
			this.source = this.createSource();
		}
		return this.source;
	},

	render : Gef.emptyFn,

	changeActivePalette : function(paletteConfig) {

		var el = null;

		if (this.getActivePalette()) {
			var oldActivePaletteId = this.getActivePalette().text;
			el = document.getElementById(oldActivePaletteId + '-img');
			el.style.border = '';
		}
		this.setActivePalette(paletteConfig);

		el = document.getElementById(paletteConfig.text + '-img');
		el.style.border = '1px dotted black';
	},

	resetActivePalette : function() {
		this.changeActivePalette({
			text : 'select'
		});
	},

	getPaletteConfig : function(p, t) {

		var id = t.parentNode.id;

		if (!id) {
			return null;
		}

		var source = this.getSource();
		var paletteConfig = this.getSource()[id];

		if (!paletteConfig) {
			return null;
		}

		this.changeActivePalette(paletteConfig);

		if (paletteConfig.creatable === false) {
			return null;
		}

		return paletteConfig;
	}
});

/**
 * 选中节点
 */
Gef.ns('Gef.jbs');
Gef.jbs.ExtSelectionListener = Gef.extend(Gef.gef.tracker.DefaultSelectionListener, {
	constructor : function(propertyGrid) {
		this.propertyGrid = propertyGrid;
	},

	selectNode : function(editPart) {
		var node = editPart.getModel();
		if (this.propertyGrid) {
			this.propertyGrid.updateForm(node);
		}

		this.model = node;
	},

	selectConnection : function(editPart) {
		var connection = editPart.getModel();
		if (this.propertyGrid) {
			this.propertyGrid.updateForm(connection);
		}

		this.model = connection;
	},

	selectDefault : function(editPart) {
		var process = editPart.getModel();
		if (this.propertyGrid) {
			this.propertyGrid.updateForm(process);
		}

		this.model = process;
	},

	setEditor : function(editor) {
		this.editor = editor;

		this.model = editor.getGraphicalViewer().getContents().getModel();
	},

	editText : function(model, text) {
		var command = new Gef.gef.command.EditTextCommand(model, text);
		this.editor.getEditDomain().getCommandStack().execute(command);
	},

	getModel : function() {
		return this.model;
	}
});