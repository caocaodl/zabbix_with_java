Ext.ns('App.property');
App.property.RightPanel = Ext.extend(Ext.Panel, {
			layout : 'fit',
			border:false,
			initComponent : function() {
				 var tabPanel = new Ext.Panel({
				 });
				this.tabPanel =tabPanel;
				this.items = [this.tabPanel];
				App.property.RightPanel.superclass.initComponent
						.call(this);
			},

			setPropertyManager : function(propertyManager) {
				this.propertyManager = propertyManager;
			},

			getTabPanel : function() {
				return this.tabPanel;
			},

			hide : function() {
				
//				this.clearItem(this.tabPanel);
//				App.property.AbstractPropertyPanel.superclass.hide.call(this);
			},

			clearItem : function(p) {

//				if (typeof p.items != 'undefined') {
//					var item = null;
//					while ((item = p.items.last())) {
//						p.remove(item, true);
//					}
//				}
			}
		});

Ext.ns('App.property');

App.property.BottomPanel = Ext.extend(Ext.Panel, {
			region : 'south',
			height : 200//,

		});

Ext.ns('App.property');

App.property.MaxWindow = Ext.extend(Ext.Window, {
			title : '属性面板',
			iconCls : 'tb-prop',
			layout : 'fit',
			stateful : false,

			closable : false,
			width : 500,
			height : 400,
			// FIXME: 希望实现，不disable editor，编辑window中的元素时，不会选中editor中的元素
			modal : false,
			constrainHeader : true,
			autoScroll : true,

			tools : [{
				id : 'restore',
				handler : function(event, toolEl, panel) {
					panel.propertyManager.changePropertyStatus(panel.restore
							.getStatusName());
				}
			}],

			// ========================================================================

			initComponent : function() {
				var tabPanel = new Ext.TabPanel({
							enableTabScroll : true,
							layoutOnTabChange : true
						});
				this.tabPanel = tabPanel;
				this.items = [tabPanel];
				App.property.MaxWindow.superclass.initComponent.call(this);
			},

			afterRender : function() {
				App.property.MaxWindow.superclass.afterRender.call(this);

				this.dd.endDrag = function(e) {
					try {
						this.win.unghost();
						// this.win.saveState();

						var x = e.xy[0];
						var y = e.xy[1];
						var propertyManager = this.win.propertyManager;
						var size = Ext.getBody().getViewSize();

						if (y > size.height - 200) {
							propertyManager.changePropertyStatus('bottom');
						} else if (x > size.width - 200) {
							propertyManager.changePropertyStatus('right');
						}
					} catch (e) {
						Gef.error(e);
					}
				}.createDelegate(this.dd);
			},

			setPropertyManager : function(propertyManager) {
				this.propertyManager = propertyManager;
			},

			getTabPanel : function() {
				return this.tabPanel;
			},

			clearItem : function(p) {
				if (typeof p.items != 'undefined') {
					var item = null;
					while ((item = p.items.last())) {
						p.remove(item, true);
					}
				}
			},

			// ========================================================================

			hide : function() {
				this.clearItem(this.tabPanel);

				if (this.el) {
					if (Gef.activeEditor) {
						// Gef.activeEditor.enable();
					}
					App.property.MaxWindow.superclass.hide.call(this);
				}
			},

			show : function() {
				if (Gef.activeEditor) {
					// Gef.activeEditor.disable();
				}
				delete this.x;
				delete this.y;
				App.property.MaxWindow.superclass.show.call(this);
			},

			getStatusName : function() {
				return 'max';
			},

			setRestore : function(restore) {
				this.restore = restore;
			}
		});
/**
 * 属性管理器
 */
Ext.ns('App.property');
App.property.PropertyManager = Ext.extend(Object, {
			/**
			 * 构造函数
			 */
			constructor : function() {
				this.bottomPanel = new App.property.BottomPanel();
//				this.bottomPanel.setPropertyManager(this);

				this.rightPanel = new App.property.RightPanel();
				this.rightPanel.setPropertyManager(this);

				this.maxWindow = new App.property.MaxWindow();
				this.maxWindow.setPropertyManager(this);

				var propertyStatus = Cookies.get('_gef_jbpm4_property_status');
				if (propertyStatus != 'bottom') {
					propertyStatus = 'right';
				}
				this.changePropertyStatus(propertyStatus);

				this.initMap();
			},

			changePropertyStatus : function(status) {
				try {
					status = status ? status : 'right';
					Cookies.set('_gef_jbpm4_property_status', status);

					switch (status) {
						case 'right' :
							this.current = this.rightPanel;
							this.current.show();
							if (this.form) {
								this.form.decorate(this.current.getTabPanel(),
										this.model);
							}

							this.maxWindow.hide();
							this.bottomPanel.hide();
							if (this.rightPanel.ownerCt) {
								this.rightPanel.ownerCt.doLayout();
							}
							// 修改缩略图位置
							var rightPanelWidth = this.rightPanel.getWidth();
							App.centerPanel
									.updatePlatformPosition(+rightPanelWidth);
							break;
						case 'bottom' :
							this.current = this.bottomPanel;
							this.current.show();
							if (this.form) {
								this.form.decorate(this.current.getTabPanel(),
										this.model);
							}

							this.maxWindow.hide();
							this.rightPanel.hide();
							if (this.rightPanel.ownerCt) {
								this.rightPanel.ownerCt.doLayout();
							}
							break;
						case 'max' :
							this.maxWindow.setRestore(this.current);
							this.current = this.maxWindow;
							this.current.show();
							if (this.form) {
								this.form.decorate(this.current.getTabPanel(),
										this.model);
							}
							var rightPanelWidth = this.rightPanel.getWidth();
							this.bottomPanel.hide();
							this.rightPanel.hide();
							if (this.rightPanel.ownerCt) {
								this.rightPanel.ownerCt.doLayout();
							}
							// 修改缩略图位置
							App.centerPanel
									.updatePlatformPosition(-rightPanelWidth);
							break;
					}
				} catch (e) {
					Gef.error(e);
				}
			},

			getBottom : function() {
				return this.bottomPanel;
			},

			getRight : function() {
				return this.rightPanel;
			},

			getMax : function() {
				return this.max;
			},

			getCurrent : function() {
				return this.current;
			},

			getSelectionListener : function() {
				return this.selectionListener;
			},

			initMap : function() {
				this.formMap = {
//					process : App.form.ProcessForm,
//					transition : App.form.TransitionForm,
//					'switch_cisco' : App.form.SwitchCiscoForm,
					// 查询面板
					queryForm : App.form.QueryForm
				};
			},
			/**
			 * 更新画布
			 * 
			 * @param model
			 */
			updateForm : function(model) {

				// this.model = model;
				//
				// var modelType = model.getType();
				// var constructor = this.formMap[modelType];
				// // 判断进入哪个表单
				// var modelForm = model.getForm();
				// if (modelForm == "transition") {
				// constructor = App.form.TransitionForm;
				// }
				//
				// if (!constructor) {
				//
				// constructor = App.form.SwitchCiscoForm;
				// }
				//
				// this.form = new constructor;
				// this.form.decorate(this.current.getTabPanel(), model);
			},

			/**
			 * 更新查询面板
			 * 
			 * @param model
			 */
			updateQueryForm : function(model) {
				this.model = model;
				this.form = new App.form.QueryForm();
				this.form.decorate(this.current.getTabPanel(), model);
			},
			initSelectionListener : function(editor) {
				this.selectionListener = new Gef.jbs.ExtSelectionListener(this);
				editor.addSelectionListener(this.selectionListener);
				this.selectionListener.setEditor(editor);
				var model = this.selectionListener.getModel();
				// 右侧显示查询面板
				this.updateQueryForm(model);

			}

		});

var Cookies = {};
Cookies.set = function(name, value) {
	var argv = arguments;
	var argc = arguments.length;
	var expires = (argc > 2) ? argv[2] : null;
	var path = (argc > 3) ? argv[3] : '/';
	var domain = (argc > 4) ? argv[4] : null;
	var secure = (argc > 5) ? argv[5] : false;
	document.cookie = name + "=" + escape(value)
			+ ((expires == null) ? "" : ("; expires=" + expires.toGMTString()))
			+ ((path == null) ? "" : ("; path=" + path))
			+ ((domain == null) ? "" : ("; domain=" + domain))
			+ ((secure == true) ? "; secure" : "");
};

Cookies.get = function(name) {
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	var j = 0;
	while (i < clen) {
		j = i + alen;
		if (document.cookie.substring(i, j) == arg)
			return Cookies.getCookieVal(j);
		i = document.cookie.indexOf(" ", i) + 1;
		if (i == 0)
			break;
	}
	return null;
};

Cookies.clear = function(name) {
	if (Cookies.get(name)) {
		document.cookie = name + "=" + "; expires=Thu, 01-Jan-70 00:00:01 GMT";
	}
};

Cookies.getCookieVal = function(offset) {
	var endstr = document.cookie.indexOf(";", offset);
	if (endstr == -1) {
		endstr = document.cookie.length;
	}
	return unescape(document.cookie.substring(offset, endstr));
};

/**
 * 右侧属性面板
 */
Ext.ns('App.property');
App.property.RightPanel2 = Ext.extend(App.property.AbstractPropertyPanel, {
			region : 'east',
			width : 200,
			// draggable : {
			// insertProxy : false,
			// onDrag : function(e) {
			// var pel = this.proxy.getEl();
			// this.x = pel.getLeft(true);
			// this.y = pel.getTop(true);
			// },
			// endDrag : function(e) {
			// var x = this.x;
			// var y = this.y;
			// var propertyManager = this.panel.propertyManager;
			// var size = Ext.getBody().getViewSize();
			//
			// if (x < size.width - 200) {
			// if (y > size.height - 200) {
			// propertyManager.changePropertyStatus('bottom');
			// } else {
			// propertyManager.changePropertyStatus('max');
			// }
			// }
			// }
			// },
			getStatusName : function() {
				return 'right';
			},
			/**
			 * 设置属性值
			 * 
			 * @param pro
			 *            属性对象
			 */
			setVal : function(pro) {
				var _this = this;
				if (typeof _this.items != 'undefined') {

					// 宽度
					var wObj = _this.find('name', 'ims-pro-width');

					if (wObj && pro.w) {
						if (isNaN(pro.w)) {
							pro.w = pro.w.split('-')[0];
						}
						wObj[0].setValue(parseInt(pro.w));
					}
					// 高度
					var hObj = _this.find('name', 'ims-pro-height');
					if (hObj) {
						hObj[0].setValue(pro.h);
					}
					// X
					var xObj = _this.find('name', 'ims-pro-x');
					if (xObj) {
						xObj[0].setValue(pro.x);
					}
					// Y
					var yObj = _this.find('name', 'ims-pro-y');
					if (yObj) {
						yObj[0].setValue(pro.y);
					}

				}
			}
		});
