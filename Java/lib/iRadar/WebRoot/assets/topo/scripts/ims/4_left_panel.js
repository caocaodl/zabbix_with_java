Gef.ns("Gef.planner");
Gef.ns("Gef.planner.model");
Gef.ns("Gef.planner.figure");
Gef.ns("Gef.planner.editpart");
/**
 * 注册图元class类
 * 
 * @param id
 * @param type
 * @param title
 * @param url
 * @param w
 * @param h
 */
function registerClass(id, type, title, url, w, h) {

	function capitaliseFirstLetter(string) {
		return string.charAt(0).toUpperCase() + string.slice(1);
	}

	var preType = type;
	var title = title;
	Gef.planner.model[preType + "Model"] = Gef.extend(
			Gef.jbs.model.GenericImageModel, {
				type : preType,
				url : url,
				title : title,
				constructor : function(conf) {
					Gef.planner.model[preType + "Model"].superclass.constructor
							.call(this, conf);
					// 增加title属性
					this.title = title;
					this.w = w;
					this.h = h;
				}
			});

	Gef.planner.figure[preType + "Figure"] = Gef.extend(
			Gef.jbs.figure.GenericImageFigure, {});

	Gef.planner.editpart[preType + "EditPart"] = Gef.extend(
			Gef.jbs.editpart.GenericImageEditPart, {
				_figureClassName : "Gef.planner.figure." + preType + "Figure"
			});

	// 高级图元对象
	Gef.jbs.JBSModelFactory.registerModel(type, "Gef.planner.model." + preType
					+ "Model");

	Gef.jbs.JBSEditPartFactory.registerEditPart(type, "Gef.planner.editpart."
					+ preType + "EditPart");

}
/**
 * 默认图元配置信息
 */
var tpls = [];
// 注册左边高级图元事件
// (function regClasses(arr) {
//
// for (var i = 0; i < tpls.length; i++) {
// registerClass.apply(this, tpls[i]);
// }
// })(tpls);

/**
 * 基本图元配置
 * 
 * @param arr
 * @returns {Array}
 */
// function makePaletteArray1(arr) {
// var ret = [{
// name : 'select',
// image : Gef.lines.select,
// title : '选择'
// }, {
// name : 'transition',
// image : Gef.lines.transition,
// title : '实线单箭头'
// }, {
// name : 'line',
// image : Gef.lines.line,
// title : '直线'
// }, {
// name : 'dashedArrows',
// image : Gef.lines.dashedArrows,
// title : '虚线单箭头'
// }, {
// name : 'dashedLine',
// image : Gef.lines.dashedLine,
// title : '虚线'
// }, {
// name : 'doubleArrowsLine',
// image : Gef.lines.doubleArrowsLine,
// title : '实线双箭头'
//
// }, {
// name : 'doubleArrowsDashed',
// image : Gef.lines.doubleArrowsDashed,
// title : '虚线双箭头'
// }];
// return ret;
// }
/**
 * 配置图元
 * 
 * @param arr
 * @returns
 */
function makeModelArray(arr) {

	var ret = {
		select : {
			text : 'select',
			creatable : false
		},
		transition : {
			text : 'transition',
			creatable : false,
			isConnection : true
		},
		dashedArrows : {
			text : 'dashedArrows',
			creatable : false,
			isConnection : true

		},
		line : {
			text : 'line',
			creatable : false,
			isConnection : true
		},
		dashedLine : {
			text : 'dashedLine',
			creatable : false,
			isConnection : true
		},
		doubleArrowsLine : {
			text : 'doubleArrowsLine',
			creatable : false,
			isConnection : true
		},
		doubleArrowsDashed : {
			text : 'doubleArrowsDashed',
			creatable : false,
			isConnection : true
		}
	};

	for (var i = 0; i < tpls.length; i++) {

		ret[tpls[i][1]] = {
			id : tpls[i][0],
			title : tpls[i][2],// 存数据库title字段
			text : tpls[i][1],// 存数据库name字段
			w : tpls[i][4],
			h : tpls[i][5]
		};
	}
	return ret;
}
/**
 * 获取左边图元的基本信息类
 */
Gef.jbs.ExtPaletteHelper = Gef.extend(Gef.jbs.JBSPaletteHelper, {
			/**
			 * 获取基本图元信息
			 * 
			 * @returns
			 */
			createSource : function() {
				return makeModelArray();
			},

			getSource : function() {

				if (!this.source) {
					this.source = this.createSource();
				}
				return this.source;
			},

			render : Gef.emptyFn,
			/**
			 * 鼠标单击 修改样式
			 * 
			 * @param paletteConfig
			 */
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
			/**
			 * 鼠标单击获取配置信息
			 * 
			 * @param p
			 * @param t
			 * @returns
			 */
			getPaletteConfig : function(p, t) {

				var id = t.parentNode.id;

				if (!id) {
					return null;
				}

				// 获取图元配置信息
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
 * 画西部面板
 */
App.PalettePanel = Ext.extend(Ext.Panel, {
	initComponent : function() {
		this.region = 'west';
		this.title = '图元库';
		this.iconCls = 'tb-activity';
		this.width = 130;
//		this.collapsed = true;
		
		this.initPalette();
		this.listeners = {

			expand : function() {
					
				// Gef.activeEditor.constructor();

			},
			scope : this

		}
		App.PalettePanel.superclass.initComponent.call(this);

	},

	initPalette : function() {
		var paletteType = null;
		if (!Gef.PALETTE_TYPE) {
			paletteType = 'accordion';
		} else {
			paletteType = Gef.PALETTE_TYPE;
		}
		this.configLayout(paletteType);
		this.configItems(paletteType);
	},

	createHtml : function(array, divId) {
		if (divId) {
			var html = '<div id="' + divId + '" unselectable="on">';
		} else {
			var html = '<div unselectable="on">';
		}

		for (var i = 0; i < array.length; i++) {
			var item = array[i];
			html += '<div id="'
					+ item.name
					+ '" class="paletteItem-'
					+ item.name
					+ '" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on"><img width="32" height="32" id="'
					+ item.name + '-img" class="paletteItem-' + item.name
					+ '" src="' + Gef.basePath + item.image
					+ '" unselectable="on"><br>' + item.title + '</div>';
		}
		html += '</div>';

		return html;
	},

	/**
	 * this.layout = 'accordion';
	 */
	configLayout : function(type) {
		if (!type || type == 'plain') {
			//
		} else if (type && type == 'accordion') {
			this.layout = 'accordion';
		}
	},

	configItems : function(type) {
		if (type && type == 'accordion') {
			this.createItemsForAccordion();
		} else if (!type || type == 'plain') {
		}
	},
	addItems : function() {

	},
	createItemsForAccordion : function() {
		// 加载json 分类

		this.id = '__gef_jbs_palette__';
		this.items = [];

		var moudelInfo = new Ext.data.JsonStore({
					autoDestroy : false,
					url : Gef.leftPanel_url,
					fields : ['name', 'title', 'childNodes'],
					autoLoad : true
				});

		moudelInfo.on("load", function(store, records, options) {
					if (store.getCount() == 0) {
						alert("未取到模块信息！");
					} else {
						var dataLength = store.getCount();
						var items = new Array();

						for (var i = 0; i < dataLength; i++) {
							var data = store.getAt(i);
							var item = {
								title : data.get("title"),
								iconCls : 'tb-activity',
								autoScroll : true
							};

							if (data.get("childNodes")) {
								var html = this.createHtml(data
										.get("childNodes"));
								item.html = html;
								// 重新赋值

								this.setTpls(data.get("childNodes"));
							}
							items.push(item);
						}
										
						this.add(items);
						this.doLayout();	
						
						// 注册左边图元对象的事件
						for (var i = 0; i < tpls.length; i++) {
							registerClass.apply(this, tpls[i]);
						}
						// 页面初始化
						App.initEditor();
                    
					}
				}, this);
	},
	setTpls : function(arr) {

		for (var i = 0; i < arr.length; i++) {

			tpls.push([arr[i].id, arr[i].name, arr[i].title, arr[i].image,
					arr[i].width, arr[i].height]);
		}
	}

});