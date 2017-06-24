Plugin.reg("operation", function() {
	var result = {};

	result.funcs = {
        /**
         * 链路操作
         */
		removeSelected : function() {
			
			var viewer = Gef.activeEditor.getGraphicalViewer();
			var browserListener = viewer.getBrowserListener();
			var selectionManager = browserListener.getSelectionManager();

			var edge = selectionManager.selectedConnection;
			var nodes = selectionManager.items;

			var request = {};

			if (edge != null) {
				request.role = {
					name : 'REMOVE_EDGE'
				};
				this.executeCommand(edge, request);
				selectionManager.removeSelectedConnection();
			} else if (nodes.length > 0) {
				request.role = {
					name : 'REMOVE_NODES',
					nodes : nodes
				};
				this.executeCommand(viewer.getContents(), request);
				selectionManager.clearAll();
			}
		},
		/**
		 * 修改布局
		 */
		changeLayout : function(e) {
			
			var viewer = Gef.activeEditor.getGraphicalViewer();
						var commandStack = viewer.getEditDomain()
								.getCommandStack();
						var processModel = viewer.getContents().getModel();
						var childerens = [];
						for (var i = 0; i < processModel.children.length; i++) {
							var model = processModel.children[i];
							model.oldModelSize = {
								oldW : model.w,
								oldH : model.h,
								oldX : model.x,
								oldY : model.y

							}
							var outConnections = model.getOutgoingConnections();

							for (var j = 0; j < outConnections.length; j++) {
								var con = outConnections[j];
								con.oldInnerPoints = con.innerPoints;
							}

							childerens.push(model);
						}

						var com = new Gef.gef.command.ChangeLayoutCommand(childerens,e.value);
						commandStack.execute(com);
		}
	};

	// 布局
	var layoutMenu = new Ext.menu.Menu({
		shadow : "sides",

		items : [{
					text : "左->右",
					iconCls : "tbar-chart-lefttoright",
					handler : result.funcs.changeLayout,
					value:'lefttoright',
					scope : this
				}, {
					text : "上->下",
					iconCls : "tbar-chart-organisation",
					value:'uptodown',
					handler : result.funcs.changeLayout,
					scope : this
				}
//				, {
//					text : "总线型",
//					iconCls : "tbar-chart-organisation",
//					value:'orthogonal',
//					handler : result.funcs.changeLayout,
//					scope : this
//				}
				]
	});
	result.cfg = {
		xtype : "buttongroup",
		title : "操作",

		defaults : {
			scale : "small",
			iconAlign : "top"
		},

		items : [{
					text : '清空',
					iconCls : 'tb-clear',
					handler : function() {
						Gef.activeEditor.clear();

					}
				}, {
					text : '撤销',
					iconCls : 'tb-undo',
					handler : function() {
						var viewer = Gef.activeEditor.getGraphicalViewer();
						var browserListener = viewer.getBrowserListener();
						var selectionManager = browserListener
								.getSelectionManager();
						selectionManager.clearAll();
						var commandStack = viewer.getEditDomain()
								.getCommandStack();
						commandStack.undo();

					},
					scope : this
				}, {
					text : '重做',
					iconCls : 'tb-redo',
					handler : function() {
						var viewer = Gef.activeEditor.getGraphicalViewer();
						var browserListener = viewer.getBrowserListener();
						var selectionManager = browserListener
								.getSelectionManager();
						selectionManager.clearAll();
						var commandStack = viewer.getEditDomain()
								.getCommandStack();

						commandStack.redo();
					},
					scope : this
				},
				{
					text : '删除',
					iconCls : 'tb-delete',
					handler : result.funcs.removeSelected,
					scope : this
				}, {
					xtype : "splitbutton",
					text : "布局",
					menu : layoutMenu,
					iconCls : "tb-activity"
				}]
	};

	return result;
});