/**
 * 全局作用域对象
 */
Ext.ns('App');
App = {
	webforms : {},
	/**
	 * 初始化
	 */
	init : function() {
		// 提示信息
		Ext.QuickTips.init();
		// 属性管理器对象
		this.propertyManager = new App.property.PropertyManager();
		// 整个页面布局
		var viewport = new Ext.Viewport({
					layout : 'border',
					items : [this.createNorth(), this.createSouth(),
							this.createWest(), this.createEast(),
							this.createCenter()]

				});

		// this.initEditor();
		/**
		 * 遮罩效果
		 */
		setTimeout(function() {
					Ext.get('loading').remove();
					Ext.get('loading-mask').fadeOut({
								remove : true
							});

				}, 100);
	},
	/**
	 * 加载默认数据
	 */
	initEditor : function() {
		// Ext编辑器对象
		var editor = new Gef.jbs.ExtEditor();
		var input = new Gef.jbs.JBSEditorInput();

		var workbenchWindow = new Gef.ui.support.DefaultWorkbenchWindow();
		workbenchWindow.getActivePage().openEditor(editor, input);
		workbenchWindow.render();
		Gef.activeEditor = editor;
		
		this.propertyManager.initSelectionListener(editor);
		this.getSystemData(input, workbenchWindow, editor);
	},
	getProcessModel : function() {
		var viewer = Gef.activeEditor.getGraphicalViewer();
		var processEditPart = viewer.getContents();
		return processEditPart.model;
	},
	getGraphicalViewer : function() {
		var viewer = Gef.activeEditor.getGraphicalViewer();
		return viewer;
	},
	/**
	 * 创建北部面板
	 * 
	 * @returns
	 */
	createNorth : function() {

		var p = null;
		if (Gef.MODE_DEMO === true) {
			p = new Ext.Panel({
						region : 'north'
					});
		} else {
			p = new Ext.Panel({
				region : 'north'// ,
					// html : '<h1 id="pageh1">Web网络拓扑图 - 图形控件演示<h1>'
				});
		}

		App.northPanel = p;
		return p;
	},
	/**
	 * 创建南部面板
	 * 
	 * @returns
	 */
	createSouth : function() {
		var p = this.propertyManager.getBottom();

		return p;
	},
	/**
	 * 创建西部面板
	 * 
	 * @returns {App.PalettePanel}
	 */
	createWest : function() {
		var p = new App.PalettePanel({
					collapsible : true
				});
		App.westPanel = p;
		return p;
	},
	/**
	 * 创建东部面板
	 * 
	 * @returns
	 */
	createEast : function() {

		var p = this.propertyManager.getRight();
		var o = new Ext.Panel({
					region : 'east',
					iconCls : 'tb-prop',
					border : false,
					width : 200,
					layout : 'fit',
					title : '查询面板',
					collapsible : true,
					collapsed : true,
					items : p,
					listeners : {
						expand : function() {
							this.findById("queryFormPanel").doLayout();
							this.findById("queryFormPanel").setHeight(120);
						},
						scope : p

					}
				})
		return o;
	},
	/**
	 * 创建中间面板
	 * 
	 * @returns
	 */
	createCenter : function() {
		var p = new App.CanvasPanel();

		App.centerPanel = p;
		return p;
	},

	getSelectionListener : function() {
		if (!this.selectionListener) {
			this.selectionListener = new Gef.jbs.ExtSelectionListener(null);
		}
		return this.selectionListener;
	},
	getSystemData : function(input, workbenchWindow, editor) {
		var initAppLayout = function() {
			var childrens = App.getProcessModel().children;
			var isInitial = true;
			$.each(childrens, function(){
				if (this.x != 0 || this.y != 0) {
					isInitial = false;
					return false;
				}
			})
			if(isInitial){
				 for (var i = 0; i < Plugin.items.length; i++) {
					var item = Plugin.items[i];
					if (item.name == "operation") {
						var changeLayout = item.f().funcs["changeLayout"];
						if (changeLayout) {
							changeLayout({value : "uptodown"});
						}
					}
				}
			}
		}
		
		Ext.Ajax.request({
			method : 'post',
			url : Gef.systemShowData_url,
			success : function(response) {
				if (response.responseText) {
					var domXml = response.responseText;
					input.readXml(domXml);
					workbenchWindow.getActivePage().openEditor(editor, input);
					workbenchWindow.setRendered(false);
					workbenchWindow.render();
					initAppLayout();
				}
			},
			failure : function(response) {
				Ext.Msg.alert('系统错误', response.responseText);
			},
			params : {// 传递参数
			// id : ""
			}
		});
	}
};

// Gef.PALETTE_TYPE = 'plain';
Gef.PALETTE_TYPE = 'accordion';
/**
 * 程序入口
 */
Ext.onReady(App.init, App);

App.CanvasPanel = Ext.extend(Ext.Panel, {
	initComponent : function() {
		this.region = 'center';
		this.autoScroll = true;

		var tbars = [];

		var me = this;

		$.each(Plugin.items, function() {
					var r = this.f.apply(me);
					tbars.push(r.cfg);
					$.extend(me, r.funcs);
				});

		// 顶部工具条
		var toolbarPanel = new Ext.Panel({
					broder : false,
					bodyStyle : 'border:none;height:0px;',
					tbar : tbars
				});
		this.tbar = toolbarPanel;

		App.CanvasPanel.superclass.initComponent.call(this);
	},

	afterRender : function() {
		App.CanvasPanel.superclass.afterRender.call(this);

		var width = 2500;
		var height = 2000;

		Ext.DomHelper.append(this.body, [{
			id : '__gef_jbs__',
			tag : 'div',
			style : 'width:' + (width + 10) + 'px;height:' + (height + 10)
					+ 'px;',
			children : [{
						id : '_gef_jbs_center_bg',
						tag : 'img',
						style : 'z-index:1;position:absolute;width:0px;height:0px;'
					}, {
						id : '__gef_jbs_center__',
						tag : 'div',
						style : 'z-index:2;position:absolute;width:' + width
								+ 'px;height:' + height + 'px;float:left;'
					}]
		}]);

		this.body.on('contextmenu', this.onContextMenu, this);
		// this.getSystemBgInfo();

	},

	onContextMenu : function(e) {

		if (!this.contextMenu) {

			this.contextMenu = new Ext.menu.Menu({
						items : [{
									text : '详情',
									iconCls : 'tb-prop',
									handler : this.showNodeDetail,
									scope : this
								}, {
									text : '****',
									iconCls : 'tb-prop',
									handler : this.showNodeDetail2,
									scope : this
								}]
					});
		}
		e.preventDefault();

		var tagName = e.target.tagName.toLowerCase();
		var tagId = e.target.id;
		// 只有页面node节点可以显示右侧菜单属性
		if (tagId.indexOf("_Gef_") > -1
				&& (tagName == "image" || tagName == "img")) {

			this.contextMenu.showAt(e.getXY());
		}

	},

	showWindow : function() {

		App.propertyManager.changePropertyStatus('max');

	},
	/**
	 * 显示节点详情
	 */
	showNodeDetail : function() {

		var nodeDataId = App.propertyManager.selectionListener.model.dom
				.getAttribute("id");

		if (nodeDataId) {

		}

	},
	/**
	 * 显示节点详情
	 */
	showNodeDetail2 : function() {

		var nodeDataId = App.propertyManager.selectionListener.model.dom
				.getAttribute("id");

		if (nodeDataId) {
			console.log(nodeDataId);
		}

	},

	executeCommand : function(editPart, request) {

		var command = editPart.getCommand(request);
		if (command != null) {
			Gef.activeEditor.getGraphicalViewer().getEditDomain()
					.getCommandStack().execute(command);
		}
	},

	/**
	 * 修改位置(右侧属性窗体弹出,修改缩略图位置)
	 * 
	 * @param {}
	 *            x
	 */
	updatePlatformPosition : function(x) {
		// 修改位置
		var floater = Ext.fly('__gef_jbs__platform');
		var right = floater.dom.style.right;
		right = Gef.getInt(right);
		floater.dom.style.right = (right + x) + "px";

	},
	/**
	 * 获取系统背景图片信息
	 */
	getSystemBgInfo : function() {
		App.systemBgId = 0;
		// 加载数据库背景
		Ext.Ajax.request({
			method : 'post',
			url : 'getBackground.action',
			success : function(response) {
				var respText = Ext.util.JSON.decode(response.responseText);

				if (respText.length > 0) {
					var imgObj = $("#_gef_jbs_center_bg");
					var bg = imgObj.attr("src");
					bg = bg ? bg : "";
					var imgW = imgObj.css("width");
					var imgH = imgObj.css("height");
					App.systemBgId = respText[0].id;
					var bgObj = {
						oldSrc : bg,
						oldW : imgW,
						oldH : imgH,
						oldId : App.systemBgId,
						newSrc : "images/ims/background/"+ respText[0].src + "",
						newId : respText[0].id,
						newW : respText[0].width,
						newH : respText[0].height
					}
					var com = new Gef.gef.command.ChangeBgCommand(bgObj);
					com.execute();
				}
			},
			failure : function(response) {
				Ext.Msg.alert('系统错误', response.responseText);
			},
			params : {// 传递参数
			// id : ""
			}
		});
	}
});
