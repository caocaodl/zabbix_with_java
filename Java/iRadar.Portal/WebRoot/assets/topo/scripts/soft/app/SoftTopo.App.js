/**
 * 全局作用域对象
 */
SoftTopo.App = {

	/**
	 * 初始化
	 */
	init: function() {
		window.alert = SoftTopo.Util.showModalWindow;
		this.resizeCanvasCon();
		var graph = new Q.Graph("canvas");
		// SoftTopo.Util.removerLogo();
		this.graph = graph;
		this.createBaseToolbar();
		//create toolbal
		this.createToolbar();
		//创建Data数据对象
		var data = new SoftTopo.Data(graph);
		this.setData(data);
		//create contextmenu
		this.createContextMenu();
		//创建Ajax请求对象
		var ajaxData = new SoftTopo.AjaxData();
		this.setAjaxData(ajaxData);
		this.initTopo();

	},
	/**
	 * 初始化拓扑
	 */
	initTopo: function() {
		this.ajaxData.initTopo();
	},
	/**
	 * 请求数据类
	 * @param {} ajaxData
	 */
	setAjaxData: function(ajaxData) {
		this.ajaxData = ajaxData;
	},
	/**
	 * 获取请求数据类
	 */
	getAjaxData: function() {
		return this.ajaxData;
	},
	/**
	 * 数据处理类
	 * @param {} data
	 */
	setData: function(data) {
		this.data = data;
	},
	/**
	 * 数据处理类
	 */
	getData: function() {
		return this.data;
	},
	/**
	 * graph
	 * @param {} graph
	 */
	setGraph: function(graph) {
		this.graph = graph;
	},
	/**
	 * graph
	 */
	getGraph: function() {
		return this.graph;
	},
	refreshData: function(json) {
		this.data.createData(json);
	},
	resizeCanvasCon: function(isRest) {
		var panelSize = isRest ? $('#graph_panel').data("data") : "",
			$canvas = $("#canvas"),
			conW = panelSize && panelSize.width ? panelSize.width - 20 : SoftTopo.Util.getWinWidth() - 20,
			conH = panelSize && panelSize.height ? panelSize.height - 10 : SoftTopo.Util.getWinHeight() - 10;

		$canvas.parent().width(conW).height(conH);
		$canvas.width(conW).height(conH - 40);
		if (this.graph) {
			this.graph.updateViewport();
		}
	},
	createBaseToolbar: function() {
		//配置原生工具栏显示按钮		
		if (SoftTopo.AppConfig) {
			Q.TOOLBARCONFIG = SoftTopo.AppConfig;
		}
		Q.createToolbar(this.graph, document.getElementById("toolbar"));

	},
	createToolbar: function() {

		var $parent = $(".q-toolbar"),
			$menu = $('<div id="toolbar-menu"></div>');
		$parent.append($menu);
		if (SoftTopo.AppConfig) {
			if (SoftTopo.AppConfig.SAVE) {
				var $save = $('<div  title="保存" class="btn btn-default btn-sm"><div class="icon toolbar-save"></div></div>');
				$save.click($.proxy(this.saveTopo, this));
				$menu.append($save);
			}
			if (SoftTopo.AppConfig.AUTOLAYOUT) {
				var $autoLayout = $('<div  title="布局" class="btn btn-default btn-sm"><div class="icon toolbar-layout"></div></div>');
				$autoLayout.click($.proxy(this.autoLayout, this));
				$menu.append($autoLayout);
			}
			//IE11以下不支持全屏
			if (SoftTopo.AppConfig.MAXIMIZE && !$.browser.msie) {
				var $maximize = $('<div  title="全屏" id="maximize" class="btn btn-default btn-sm"><div class="q-icon toolbar-max"></div></div>');
				$maximize.click($.proxy(this.maximize, this));
				$menu.append($maximize);
				(function initFull(obj) {
					var _this = obj;

					document.addEventListener("fullscreenchange", function() {

						document.fullscreen ? _this.resizeCanvasCon() : _this.resizeCanvasCon(true);
					}, false);

					document.addEventListener("mozfullscreenchange", function() {

						document.mozFullScreen ? _this.resizeCanvasCon() : _this.resizeCanvasCon(true);
					}, false);

					document.addEventListener("webkitfullscreenchange", function() {

						document.webkitIsFullScreen ? _this.resizeCanvasCon() : _this.resizeCanvasCon(true);

					}, false);
					document.addEventListener("MSFullscreenChange", function() {

						document.msFullscreenElement ? _this.resizeCanvasCon() : _this.resizeCanvasCon(true);
					}, false);
				})(this);
			}
		}
		this.toolBarMenu = $menu;
	},
	createContextMenu: function() {
		var optionMenu = [];
		if (this.data.contextMenu) {
			optionMenu = this.data.contextMenu();
		}
		var contextMenu = new SoftTopo.ContextMenu(optionMenu);
		var menu = new Q.PopupMenu();
		this.graph.popupmenu = menu;
		this.contextMenu = contextMenu;

	},
	saveTopo: function() {
		var json = this.graph.exportJSON();
		if (this.ajaxData && this.ajaxData.saveTopo) {
			this.ajaxData.saveTopo(json);
		}
	},
	autoLayout: function() {
		if (this.data && this.data.doLayout) {
			this.data.doLayout();
		}
	},
	maximize: function() {

		var docElm = document.documentElement,
			$maximize = $('#maximize'),
			$graphPanel = $('#graph_panel'),
			width = parseInt($graphPanel.css("width")),
			height = parseInt($graphPanel.css("height"));

		if ($graphPanel.hasClass('q-max')) {
			$graphPanel.removeClass('q-max');
			$maximize.removeClass('active');
			//关闭全屏
			SoftTopo.Util.cancelFullscreen();

		} else {
			$graphPanel.addClass('q-max');
			$maximize.addClass('active');
			$graphPanel.data("data", {
				width: width,
				height: height
			});
			//打开全屏
			SoftTopo.Util.launchFullScreen(docElm);
		}

	},
	getToolbarMenu: function() {
		return this.toolBarMenu;
	},
	/*
	 * 设置导出按钮 锁定与解锁
	 *@parameter lock (true||false)
	 */
	lockExportImage: function(lock) {
		var $exportBtn = $(".q-icon.toolbar-print");
		if ($exportBtn.length) {
			$exportBtn.parent().attr("disabled",lock);
		}
	}

};

/**
 * 程序入口
 */
$(document).ready(function() {
	SoftTopo.App.init();
});