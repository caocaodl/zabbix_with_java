Ext.ns('App.form');
App.form.AbstractForm = Ext.extend(Object, {
			eventNames : [['start', '开始'], ['end', '结束']],

			clearItem : function(p) {
				if (typeof p.items != 'undefined') {
					var item = null;
					while ((item = p.items.last())) {
						p.remove(item, true);
					}
				}
			},
			addRecord : function(grid, record) {
				grid.stopEditing();
				var index = grid.getStore().getCount();
				grid.getStore().insert(index, record);
				grid.startEditing(index, 0);
			},

			removeRecord : function(grid) {
				Ext.Msg.confirm('信息', '确定删除？', function(btn) {
							if (btn != 'yes') {
								return;
							}
							var sm = grid.getSelectionModel();
							var cell = sm.getSelectedCell();

							var record = grid.getStore().getAt(cell[0]);
							grid.getStore().remove(record);
						});
			}
		});
/**
 * xml 根节点
 */
Ext.ns('App.form');
App.form.ProcessForm = Ext.extend(App.form.AbstractForm, {

			decorate : function(tabPanel, model) {

				this.clearItem(tabPanel);
				this.resetBasic(tabPanel, model);

			},

			resetBasic : function(tabPanel, model) {

				var p = new Ext.form.FormPanel({
							title : '基本配置',
							labelWidth : 50,
							labelAlign : 'right',
							border : false,
							defaultType : 'textfield',
							defaults : {
								anchor : '90%'
							},
							bodyStyle : {
								padding : '6px 0 0'
							},
							items : [

							{
								name : 'ims-pro-name',
								fieldLabel : '名称',
								xtype : 'textfield',
								value : '',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-name', newValue);
									}
								}
							}, {
								name : 'ims-pro-width',
								fieldLabel : '宽度',
								xtype : 'textfield',
								value : '',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-width', newValue);
									}
								}
							}, {
								name : 'ims-pro-height',
								fieldLabel : '高度',
								xtype : 'textfield',
								value : '',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-height', newValue);
									}
								}
							}, {
								name : 'ims-pro-x',
								fieldLabel : 'X轴',
								xtype : 'textfield',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-x', newValue);
									}
								}
							}, {
								name : 'ims-pro-y',
								fieldLabel : 'Y轴',
								xtype : 'textfield',
								value : '',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-y', newValue);
									}
								}
							}, {
								name : 'ims-pro-type',
								fieldLabel : '类型',
								xtype : 'combo',
								anchor : '90%',
								forceSelection : true,// 值为true时将限定选中的值为列表中的值，值为false则允许用户将任意文本设置到字段（默认为
								// false）。
								selectOnFocus : true,// 值为 ture
								// 时表示字段获取焦点时自动选择字段既有文本(默认为
								// false)。
								mode : 'local',
								store : new Ext.data.SimpleStore({
											fields : ['value', 'text'],
											data : [[1, '缩略图'], [2, '子拓扑图'],
													[3, '服务器'], [4, '机柜信息']]
										}),
								editable : false,
								triggerAction : 'all',
								valueField : 'value',
								displayField : 'text',
								listeners : {
								// 'blur' : function(field) {
								// var newValue = field.getValue();
								// model.dom.setElementContent('ims-pro-type',
								// newValue);
								// }
								}
							}, {
								name : 'ims-pro-descn',
								fieldLabel : '备注',
								xtype : 'textarea',
								listeners : {
									'blur' : function(field) {
										var newValue = field.getValue();
										model.dom.setElementContent(
												'ims-pro-descn', newValue);
									}
								}
							}]
						});

				tabPanel.add(p);
				tabPanel.activate(p);
			}
		});
/**
 * 所有连线属性
 */
Ext.ns('App.form');
App.form.TransitionForm = Ext.extend(App.form.AbstractForm, {
	// eventNames : [ [ 'take', '进入' ] ],
	model : "",

	decorate : function(tabPanel, model) {
		this.setModel(model);
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);

	},
	setModel : function(model) {
		this.model = model;
	},

	resetBasic : function(tabPanel, model) {

		var items = [{
					name : 'name',
					fieldLabel : '名称',
					value : model.text ? model.text : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							model.text = newValue;
							model.editPart.figure.updateAndShowText(newValue);
						}
					}
				}, {
					name : 'ims-pro-linebold',
					fieldLabel : '线条粗细',
					xtype : 'combo',
					anchor : '90%',
					mode : 'local',
					store : new Ext.data.SimpleStore({
						fields : ['value', 'text'],
						data : [
								[1,
										'<hr style="background-color:#000;color:#000;height:1px;">'],
								[2,
										'<hr style="background-color:#000;color:#000;height:2px;">'],
								[3,
										'<hr style="background-color:#000;color:#000;height:3px;">']]
					}),
					editable : false,
					triggerAction : 'all',
					valueField : 'value',
					displayField : 'text',
					listeners : {
						// 下拉列表被选中
						'select' : this.selectLineBold,
						scope : this
					}
				}, {
					name : 'ims-pro-lineType',
					fieldLabel : '线条类型',
					xtype : 'combo',
					anchor : '90%',
					mode : 'local',
					store : new Ext.data.SimpleStore({
								fields : ['value', 'text'],
								data : [["transition", '————>'],
										["line", '————'],
										["dashedArrows", '----->'],
										["dashedLine", '-----'],
										["doubleArrowsLine", '<————>'],
										["doubleArrowsDashed", '<---->']]
							}),
					editable : false,
					triggerAction : 'all',
					valueField : 'value',
					displayField : 'text',
					listeners : {
						// 下拉列表被选中
						'select' : this.selectLineType,
						scope : this
					}
				}, {
					name : 'description',
					fieldLabel : '备注',
					xtype : 'textarea',
					value : model.dom.getElementContent('description'),
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							model.dom
									.setElementContent('description', newValue);
						}
					}
				}

		];

		var p = new Ext.form.FormPanel({
					title : '基本配置',
					labelWidth : 70,
					labelAlign : 'right',
					border : false,
					defaultType : 'textfield',
					defaults : {
						anchor : '90%'
					},
					bodyStyle : {
						padding : '6px 0 0'
					},
					items : items
				});

		tabPanel.add(p);
		tabPanel.activate(p);
	},
	/**
	 * 下拉框的值被选中,修改选线粗细
	 * 
	 * @param {}
	 *            field
	 */
	selectLineBold : function(field) {
		var newValue = field.getValue();
		switch (newValue) {
			case 1 :
				field.setRawValue("正常");
				break;
			case 2 :
				field.setRawValue("中粗");
				break;
			case 3 :
				field.setRawValue("特粗");
				break;
		}
		// 获取页面图形对象
		var figure = this.model.getEditPart().getFigure();
		// 设置连线粗细
		if (Gef.isIE) {
			figure.setLineBold("strokeweight", newValue);
		} else {
			figure.setLineBold("stroke-width", newValue);
		}
	},
	/**
	 * 下拉框的值被选中,修改选线类型
	 * 
	 * @param {}
	 *            field
	 */
	selectLineType : function(field) {
		var newValue = field.getValue();
		// 获取页面图形对象
		var figure = this.model.getEditPart().getFigure();
		this.model.setType(newValue);
		this.model.setTagName(newValue);
		switch (newValue) {
			case "transition" :// 实线单箭头
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "Classic");
					figure.setLineStrokeVml("dashstyle", "Solid");
				} else {
					figure.setLineBold("marker-end", "url(#markerEndArrow)");
					figure.removeLineSvg("stroke-dasharray");
				}
				break;
			case "line" :// 实线
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "none");
					figure.setLineStrokeVml("dashstyle", "Solid");
				} else {
					figure.removeLineSvg("marker-end");
					figure.removeLineSvg("stroke-dasharray");
				}
				break;
			case "dashedArrows" :// 虚线单箭头
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "Classic");
					figure.setLineStrokeVml("dashstyle", "dot");
				} else {
					figure.setLineBold("stroke-dasharray", "6,5");
					figure.setLineBold("marker-end", "url(#markerEndArrow)");
				}
				break;
			case "dashedLine" :// 虚线
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "none");
					figure.setLineStrokeVml("dashstyle", "dot");
				} else {
					figure.setLineBold("stroke-dasharray", "6,5");
					figure.setLineBold("marker-end", "none");
				}
				break;
			case "doubleArrowsLine" :// 双箭头实线
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "Classic");
					figure.setLineStrokeVml("startArrow", "Classic");
					figure.setLineStrokeVml("dashstyle", "Solid");
				} else {
					figure.removeLineSvg("stroke-dasharray");
					figure.setLineBold("marker-end", "url(#markerEndArrow)");
					figure
							.setLineBold("marker-start",
									"url(#markerStartArrow)");
				}
				break;
			case "doubleArrowsDashed" :// 双箭头虚线
				if (Gef.isIE) {
					figure.setLineStrokeVml("endArrow", "Classic");
					figure.setLineStrokeVml("startArrow", "Classic");
					figure.setLineStrokeVml("dashstyle", "dot");
				} else {
					figure.setLineBold("stroke-dasharray", "6,5");
					figure.setLineBold("marker-end", "url(#markerEndArrow)");
					figure
							.setLineBold("marker-start",
									"url(#markerStartArrow)");
				}
				break;
		}
	}

});
/**
 * switch_cisco 属性面板 switch_cisco
 */
Ext.ns('App.form');
App.form.SwitchCiscoForm = Ext.extend(App.form.AbstractForm, {
	decorate : function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		// this.resetEvent(tabPanel, model);
		// this.resetTimer(tabPanel, model);
	},
	resetBasic : function(tabPanel, model) {

		var items = [

		{
					name : 'ims-pro-name',
					fieldLabel : '名称',
					xtype : 'textfield',
					value : model.title ? model.title : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							model.title = newValue;
							model.dom.setElementContent('ims-pro-name',
									newValue);
						}
					}
				}, {
					name : 'ims-pro-width',
					fieldLabel : '宽度',
					xtype : 'numberfield',
					value : model.w ? model.w : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();

							model.dom.setElementContent('ims-pro-width',
									newValue);
						},
						'change' : function(field) {
							// 修改画布图片大小
							var newValue = field.getValue();
							model.w = newValue;
							model.editPart.figure.w = model.w;
							model.editPart.figure.updatePor();
							// 修改选中状态图元边框大小
							var defaultGraphicalViewer = model.editPart
									.getViewer();
							var sel = defaultGraphicalViewer
									.getBrowserListener().getSelectionManager();
							sel.selectIn(model);

						}
					}
				}, {
					name : 'ims-pro-height',
					fieldLabel : '高度',
					// xtype : 'textfield',
					xtype : 'numberfield',
					value : model.h ? model.h : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							model.dom.setElementContent('ims-pro-height',
									newValue);
						},
						'change' : function(field) {
							// 修改画布图片大小
							var newValue = field.getValue();
							model.h = newValue;
							model.editPart.figure.h = newValue;
							model.editPart.figure.updatePor();
							// 修改选中状态图元边框大小
							var defaultGraphicalViewer = model.editPart
									.getViewer();
							var sel = defaultGraphicalViewer
									.getBrowserListener().getSelectionManager();
							sel.selectIn(model);
						}
					}
				}, {
					name : 'ims-pro-x',
					fieldLabel : 'X轴',
					// xtype : 'textfield',
					xtype : 'numberfield',
					value : model.x ? model.x : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							model.dom.setElementContent('ims-pro-x', newValue);
						},
						'change' : function(field) {
							// 修改画布图片大小
							var newValue = field.getValue();
							model.x = newValue;
							model.editPart.figure.x = newValue;
							model.editPart.figure.updatePor();
							// 修改选中状态图元边框大小
							var defaultGraphicalViewer = model.editPart
									.getViewer();
							var sel = defaultGraphicalViewer
									.getBrowserListener().getSelectionManager();
							sel.clearAll();
							sel.selectIn(model);
						}
					}
				}, {
					name : 'ims-pro-y',
					fieldLabel : 'Y轴',
					// xtype : 'textfield',
					xtype : 'numberfield',
					value : model.y ? model.y : '',
					listeners : {
						'blur' : function(field) {
							var newValue = field.getValue();
							// model.y = newValue;
							// model.editPart.figure.y = newValue;
							model.dom.setElementContent('ims-pro-y', newValue);
						},
						'change' : function(field) {
							// 修改画布图片大小
							var newValue = field.getValue();
							model.y = newValue;
							model.editPart.figure.y = newValue;
							model.editPart.figure.updatePor();
							// 修改选中状态图元边框大小
							var defaultGraphicalViewer = model.editPart
									.getViewer();
							var sel = defaultGraphicalViewer
									.getBrowserListener().getSelectionManager();
							sel.selectIn(model);

						}
					}
				}, {
					name : 'ims-pro-type',
					fieldLabel : '类型',
					xtype : 'combo',
					anchor : '90%',
					forceSelection : true,// 值为true时将限定选中的值为列表中的值，值为false则允许用户将任意文本设置到字段（默认为
					// false）。
					selectOnFocus : true,// 值为 ture
					// 时表示字段获取焦点时自动选择字段既有文本(默认为
					// false)。
					mode : 'local',
					store : new Ext.data.SimpleStore({
								fields : ['value', 'text'],
								data : [[1, '缩略图'], [2, '子拓扑图'], [3, '服务器'],
										[4, '机柜信息']]
							}),
					editable : false,
					triggerAction : 'all',
					valueField : 'value',
					displayField : 'text',
					listeners : {
					// 'blur' : function(field) {
					// var newValue = field.getValue();
					// model.dom.setElementContent('ims-pro-type',
					// newValue);
					// }
					}
				}, {
					name : 'ims-pro-descn',
					fieldLabel : '备注',
					xtype : 'textarea',
					// value : model.y ? model.y : '',
					listeners : {
						'blur' : function(field) {
							alert();
							var newValue = field.getValue();
							model.dom.setElementContent('ims-pro-descn',
									newValue);
						}
					}
				}];

		var p = new Ext.form.FormPanel({
					title : '基本配置',
					labelWidth : 50,
					labelAlign : 'right',
					border : false,
					defaultType : 'textfield',
					defaults : {
						anchor : '90%'
					},
					bodyStyle : {
						padding : '6px 0 0'
					},
					items : items
				});

		tabPanel.add(p);
		tabPanel.activate(p);
	}

});
/**
 * 查询面板
 */
Ext.ns('App.form');
App.form.QueryForm = Ext.extend(App.form.AbstractForm, {

			decorate : function(tabPanel, model) {
				this.clearItem(tabPanel);
				this.resetBasic(tabPanel, model);
			},
			resetBasic : function(tabPanel, model) {
				// 查询按钮
				var queryBtn = new Ext.Button({
							text : "查询",
							
							listeners : {// 添加监听事件 可以结合handler测试这两个事件哪个最先执行
								"click" : function() {
									// 设备厂商
									var frimVal = this.getVal("facilityFirm");
									var firmText = this.getText("facilityFirm");
									firmText = frimVal ? firmText : "";

									// 设备类型
									var typeVal = this.getVal("facilityType");
									var typeText = this.getText("facilityType");
									typeText = typeVal ? typeText : "";

									// IP
									var ipVal = this.getVal("facilityIp");
									this.filterNode(typeText, firmText, ipVal);
								},
								scope : this

							}

						});
				var items = [

				{
					name : 'facilityType',
					fieldLabel : '设备类型',
					xtype : 'combo',
					forceSelection : true,// 值为true时将限定选中的值为列表中的值，值为false则允许用户将任意文本设置到字段（默认为
					// false）。
					selectOnFocus : true,// 值为 ture
					// 时表示字段获取焦点时自动选择字段既有文本(默认为
					// false)。
					mode : 'local',
					store : new Ext.data.SimpleStore({
								fields : ['value', 'text'],
								data : [[0, '--设备类型--'], [1, '服务器'],
										[2, '路由交换机'], [3, '存储设备'], [4, '虚拟设备']]
							}),
					editable : false,
					triggerAction : 'all',
					valueField : 'value',
					displayField : 'text',
					listeners : {
						'select' : function(combo, record, index) {
							// 过滤页面节点
							var text = record.data.text;
							var val = record.data.value;
							this.changeFacilityType(text, val);

						},
						scope : this
					}
				}, {
					name : 'facilityFirm',
					fieldLabel : '设备厂商',
					xtype : 'combo',
					anchor : '90%',
					forceSelection : true,// 值为true时将限定选中的值为列表中的值，值为false则允许用户将任意文本设置到字段（默认为
					// false）。
					selectOnFocus : true,// 值为 ture
					// 时表示字段获取焦点时自动选择字段既有文本(默认为
					// false)。
					mode : 'local',
					store : new Ext.data.SimpleStore({
								fields : ['value', 'text'],
								data : [[0, '--设备厂商--'], [1, 'Kylin'],
										[2, 'Microsoft'], [3, 'Redhat'],
										[4, 'Cisco']]
							}),
					editable : false,
					triggerAction : 'all',
					valueField : 'value',
					displayField : 'text',
					listeners : {
						'select' : function(combo, record, index) {
							// 过滤页面节点
							var text = record.data.text;
							var val = record.data.value;
							this.changeFacilityFirm(text, val);

						},
						scope : this
					}
				}, {
					name : 'facilityIp',
					fieldLabel : 'IP地址',
					xtype : 'textfield'

				}];

				var p = new Ext.form.FormPanel({
							id : 'queryFormPanel',
							labelWidth : 60,
							labelAlign : 'right',
							border : false,
							defaultType : 'textfield',

							defaults : {
								anchor : '90%'
							},
							bodyStyle : {
								padding : '6px 0 0'								
							},
							buttonAlign : 'center',
							buttons : [queryBtn],
							items : items
						});
				tabPanel.add(p);
				tabPanel.doLayout();
			},
			/**
			 * 设备类型
			 */
			changeFacilityType : function(text, val) {
				// 设备厂商
				var frimVal = this.getVal("facilityFirm");
				var firmText = this.getText("facilityFirm");
				firmText = frimVal ? firmText : "";

				// 设备类型
				text = val ? text : "";
				this.filterNode(text, firmText, "");
			},
			/**
			 * 设备厂商
			 */
			changeFacilityFirm : function(text, val) {
				// 设备类型
				var typeVal = this.getVal("facilityType");
				var typeText = this.getText("facilityType");
				typeText = typeVal ? typeText : "";

				// 设备厂商
				text = val ? text : "";
				this.filterNode(typeText, text, "");
			},
			/**
			 * 过滤页面节点
			 * 
			 * @param {}
			 *            queryText
			 */
			filterNode : function(typeText, firmText, ipVal) {

				var model = App.getProcessModel();
				// 页面所有节点
				var childs = model.getChildren();
				for (var i = 0; i < childs.length; i++) {
					var node = childs[i];
					// 节点显示信息
					var nodeText = node.text;
					var tagName = node.dom.tagName.split("_")[0];
					// 设备类型不为空
					if (typeText != "") {

						// 设备厂商不为空
						if (firmText != "") {
							// ip不为空
							if (ipVal != "") {

								if (nodeText.indexOf(ipVal) != -1
										&& nodeText.indexOf(typeText) != -1
										&& tagName.indexOf(firmText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}
							} else {
								if (nodeText.indexOf(typeText) != -1
										&& tagName.indexOf(firmText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							}

						} else {
							// ip
							if (ipVal != "") {
								if (nodeText.indexOf(ipVal) != -1
										&& nodeText.indexOf(typeText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							} else {
								if (nodeText.indexOf(typeText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							}

						}
					} else {
						// 设备厂商不为空
						if (firmText != "") {

							// ip
							if (ipVal != "") {

								if (nodeText.indexOf(ipVal) != -1
										&& tagName.indexOf(firmText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							} else {
								if (tagName.indexOf(firmText) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							}

						} else {

							// ip
							if (ipVal != "") {

								if (nodeText.indexOf(ipVal) != -1) {
									node.editPart.figure.el.style.opacity = 1;
								} else {
									node.editPart.figure.el.style.opacity = 0.2;
								}

							} else {

								node.editPart.figure.el.style.opacity = 1;

							}

						}

					}
				}

			},
			/**
			 * 获取值
			 */
			getVal : function(fieldName) {

				var formPanel = App.propertyManager.rightPanel.tabPanel.items.items[0]
						.getForm();
				var val = formPanel.findField(fieldName).getValue();
				return val;
			},
			/**
			 * 获取text
			 */
			getText : function(fieldName) {
				var formPanel = App.propertyManager.rightPanel.tabPanel.items.items[0]
						.getForm();
				var text = formPanel.findField(fieldName).getRawValue();
				return text;
			}

		});
