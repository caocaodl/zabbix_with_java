$(function(){
	var ctxpath = window.ctxpath || "";
	
	var CmdManager = new function(){
		var filter, collapse;
		
		function filterDFS(db, v, force){
			if(!db) return;
			var hasHit = false;
			v = v.toLowerCase();
			$.each(db, function(){
				var hit = (force!==undefined)? force: !(this.text.toLowerCase().indexOf(v) == -1);
				var r = filterDFS(this.children, v, hit? true: undefined);
				if(r) hit = true;
				if(hit) hasHit = true;
				$("#"+this.domId)[hit? "show": "hide"]();
			});
			return hasHit;
		}
		var filterF = function(v){
			var tree = treeWestManager.currentTree();
			if(tree){
				var treeState = tree.data("tree");
				if(treeState) {
					filterDFS(treeState.data, v);
				}
			}
		};
		
		this.filter = function(elm){
			$(elm).placeholder().monitor(filterF);
		};
		this.collapse = function(elm){
			$(elm).click(function(){
				westLayoutManager.changeNav("menu");
			});
		};
		this.expand = function(elm){
			$(elm).click(function(){
				westLayoutManager.changeNav("tree");
			});
		};
	};
	
	//树样式（较宽，使用easyui的tree来实现itree或simple样式）
	var treeWestManager = new function(){
		var NORMAL = $("#JS_layoutWest>.west_tree");
		var ctn = NORMAL.children(".body");
		var title = NORMAL.find(".title");
		var CLZ_TITLE_ORIGIN = title.attr("class");
		var current = null;
		
		CmdManager.filter(NORMAL.find(".cmds INPUT"));
		
		var collapseElm = NORMAL.find(".cmds .collapse");
		CmdManager.collapse(collapseElm);
		
		function getNavObj(nav){
			return menuManager.navData(nav);
		}
		
		var TYPE_EMPTY = {
			build: function(data){
				var that = this;
				var proxy = $("<DIV />").attr("url", data.url);
				proxy.tree = function(opt, id){
					var menu = $(this);
					if(opt == "select"){
						var navObj = getNavObj(this);
						var title = navObj.data.name;
						var url = menu.attr("url");
						if(!url) return;
						$.workspace.openTab(title, ctxpath+url, {
							id: "",
							navObj: data
						});
					}
				}
				return proxy;
			},
			initShow: function(menu){
				menu.tree("select");
			},
			beforeShow: function(){
//				westLayoutManager.hide();
				if(current){
					var select = current.tree("getSelected");
					if(select) $(select.target).removeClass("tree-node-selected")
				}
			}
		}
		
		var TYPE_HOME = $.extend({}, TYPE_EMPTY, {
			build: function(data){
				var that = this;
				var KEY_HOME = "home";
				var proxy = $("<DIV />").data(KEY_HOME, data.data);
				proxy.tree = function(opt, id){
					var menu = $(this);
					if(opt == "select"){
						var navObj = getNavObj(this);
						var homeData = menu.data(KEY_HOME);
						var title = homeData.title;
						var url = homeData.url;
						if(!url) return;
						$.workspace.openTab(title, ctxpath+url, {
							id: "",
							navObj: navObj
						}, true);
					}
				}
				return proxy;
			},
			onSelect: function(tree, data){
				var menuId = data.id;
				if(!menuId) return;
				
				var cur = tree.tree("getSelected");
				if(cur && cur.target.id == menuId) return;
				
				var target = $("#"+menuId, ctn);
				
				var curTabData = menuManager.current().selectData;
				if(curTabData && curTabData.id!=menuId){
		        	tree.tree("select", target);
		        	tree.tree("scrollTo", target);
				}else{
					var clz = "tree-node-selected";
					if(cur) $(cur.target).removeClass(clz);
					$(target).addClass(clz);
				}
			}
		});
		
		var TYPE_TREE = {
			build: function(data){
				var ul = $("<UL />");
				ul.tree($.extend({
					onSelect: function(node){
						var menu = $(this);
						var data = menu.tree("getData", node.target);
						var url = data.href;
						if(!url) return;
						
						var id = data.domId;
						var title = data.text;
						var navObj = getNavObj(menu);
						$.workspace.openTab(title, ctxpath+url, {
							id: id,
							oid: data.id,
							navObj: navObj
						});
					},
					loader: function(param, success, error, nodedata){
						var that = $(this);
						var opts = that.tree('options');
						var url = null;
						if(nodedata){
							url = nodedata.url;
							if(!url){
								var parent = nodedata;
								while(true){
									parent = that.tree("getParent", parent.target);
									if(!parent) break;
									url = parent.url;
									if(url) break;
								}
							}
						}
						if(!url) url = opts.url;
						if(!url) return error.apply(this, arguments);

						$.ajax({
							type: opts.method,
							url: ctxpath+url,
							data: param,
							dataType: 'json',
							success: function(data){success(data);},
							error: function(){error.apply(this, arguments);}
						});
					}
				}, data));
				return ul;
			},
			initShow: function(tree){
				var toSelectNode = null;
				var initial = tree.tree("options").initial;
				if(initial){
					toSelectNode = tree.tree("find", initial);
				}
				if(!toSelectNode){
					function dfs(node){
						var target = node.target;
						if(tree.tree("isLeaf", target)){
							return node;
						}else{
							var childs = tree.tree("getChildren", target);
							var leaf;
							$.each(childs, function(){
								leaf = dfs(this);
								if(leaf) return false;
							});
							return leaf;
						}
					}
					toSelectNode = dfs(tree.tree("getRoot"));
				}
				if(toSelectNode){
					tree.tree("select", toSelectNode.target);
				}
			},
			beforeShow: function(tree){
//				westLayoutManager.show();
				if(current) {
					current.hide().inactiveTopNav();
				}
				current = tree;
				navObj = menuManager.navData(tree.show().activeTopNav());
				
				var data = navObj.data;
				var clz = [CLZ_TITLE_ORIGIN, data.clazz].join(" ");
				title.text(data.name).attr("class", clz);
				
				var navObj = getNavObj(tree);
				var canNotCreateMenu = (navObj.navs.menu.data("changeNav") == "tree");
				var shouldNotShowMenu = navObj.navs.tree.hasClass("itree");
				
				if(shouldNotShowMenu || canNotCreateMenu){//隐藏收缩按钮
					collapseElm.hide();
				}else{
					collapseElm.show();
				}
			},
			onSelect: function(tree, data){
				var menuId = data.id;
				if(!menuId) return;
				
				var cur = tree.tree("getSelected");
				if(cur && cur.target.id == menuId) return;
				
				var target = $("#"+menuId, ctn);
				
				var curTabData = menuManager.current().selectData;
				if(curTabData && curTabData.id!=menuId){
		        	tree.tree("select", target);
		        	tree.tree("scrollTo", target);
				}else{
					var clz = "tree-node-selected";
					if(cur) $(cur.target).removeClass(clz);
					$(target).addClass(clz);
				}
			}
		}
		
		this.types = {
			nodata: TYPE_EMPTY,
			tree: TYPE_TREE,
			home: TYPE_HOME,
			nomenu: TYPE_EMPTY
		}
		this.ctn = ctn;
		this.currentTree = function(){
			return ctn.children("UL:visible");
		}
	};
	
	//菜单样式（较窄，使用easyui的menu实现的样式）
	var menuWestManager = new function(){
		var NORMAL = $("#JS_layoutWest>.west_menu");
		var ctn = NORMAL.children(".body");
		var current = null;
		var DATA_KEY = "menu";
		var CLZ_SELECTED = "active";
		
		
		CmdManager.expand(NORMAL.find(".cmds .expand"));
		
		function getNavObj(nav){
			return menuManager.navData(nav);
		}
		
		function menuClick(){
			var data = $(this).data(DATA_KEY);
			
			var url = data.href;
			if(!url) return;
			
			var id = data.domId;
			var title = data.text;
			var navObj = getNavObj(data.dom);
			$.workspace.openTab(title, ctxpath+url, {
				id: id,
				oid: data.id,
				navObj: navObj
			});
		}
		
		
		var TYPE_TREE = {
			build: function(data){
				var menuData = [];
				
				function flagMap(o, k){
					o.map[k] = 1;
					while(o.parent){
						o = o.parent;
						o.map[k] = 1;
					}
				}
				
				function filterMenu(c, fm){
					if(!c) return fm;
					
					$.each(c, function(){
						var o = $.extend({}, this);
						o.parent = fm.parent;
						o.map = {};
						
						var cc = [];
						cc.parent = o;
						
						flagMap(o, this.id);
						//判断是否是动态加载树
						if(this.url || (filterMenu(this.children, cc).over)){
							fm.over = true;
							return false;
						}
						o.children = cc;
						fm.push(o);
					});
					return fm;
				}
				//对树的数据进行整理
				filterMenu(data.data, menuData);
				//over表示是需要动态加载的树，这样的树不在控制范围之内
				if(menuData.over) return null;
				
				function buildMenu(d, ctn){
					$.each(d.children, function(){
						this.dom = menuCtn;
						var div = $("<DIV />").appendTo(ctn);
						div.data(DATA_KEY, this);
						
						var c = this.children;
						if(c && c.length>0){
							$("<span />").text(this.text).appendTo(div);
							var divCtn = $("<DIV />").appendTo(div);
							buildMenu(this, divCtn);
						}else{
							div.text(this.text);
						}
					});
				}
				
				var menuCtn = $("<DIV />");
				$.each(menuData, function(){
					this.dom = menuCtn;
					var btn = $("<A />").text(this.text).addClass(this.iconCls)
						.attr("href", "javascript:void(0)").appendTo(menuCtn)
						.data(DATA_KEY, this).click(menuClick);
					
					var menu = null;
					if(this.children.length > 0){
						var menu = $("<DIV />").appendTo("BODY");
						buildMenu(this, menu);
					}
					
					btn.menubutton({
						menu: menu
					});
					
					if(menu){
						menu.menu('options').onShow = function(){
							var m = $(this);
							if(m.hasClass("menu-top")){
								var p = m.offset();
								m.css({
									top: p.top - 55,
									left: p.left + 70
								});
								
								var shadow = m.next(".menu-shadow").first();
								p = shadow.offset();
								shadow.css({
									top: p.top - 55,
									left: p.left + 70
								});
							}
						};
					}
				});
				
				return menuCtn;
			},
			initShow: function(menu){
				typeAdapter("tree", "initShow", menu);
			},
			beforeShow: function(menu){
				if(current) {
					current.hide();
				}
				current = menu.show();
				typeAdapter("tree", "beforeShow", menu);
			},
			onSelect: function(menu, data){
				var menuId = data.oid;
				if(!menuId) return;
				
				$("A", menu).each(function(){
					var dom = $(this).removeClass(CLZ_SELECTED);
					var data = dom.data(DATA_KEY);
					if(data.map[menuId]) dom.addClass(CLZ_SELECTED);
				});
			}
		};
		
		var originTreeBuildF = TYPE_TREE.build;
		TYPE_TREE.build = function(){
			return originTreeBuildF.apply(this, arguments) || new function(){
				//对于不在控制范围之内的树，增加changeNav为tree的标识，以去掉收缩按钮
				return $("<DIV />").data("changeNav", "tree");
			};
		};
		
		var typeAdapter = function(type, f, menu){
			var tree = getNavObj(menu).navs.tree;
			return treeWestManager.types[type][f](tree);
		};
		
		var proxyType = function(type){
			var proxyType = treeWestManager.types[type];
			return proxyType;
		};
		
		
		var home = proxyType('home');
		var originHomeBeforeF = home.beforeShow;
		home.beforeShow = function(menu){
			originHomeBeforeF.apply(this, arguments);
			if(current){
				$("A", current).removeClass(CLZ_SELECTED);
			}
		};
		
		this.types = {
			nodata: proxyType('nodata'),
			tree: TYPE_TREE,
			home: home,
			nomenu: proxyType('nomenu')
		};
		this.ctn = ctn;
		this.currentTree = function(){
			return ctn.children("DIV:visible");
		};
	};
	
	//	westLayoutManager（左侧区域管理类，主要控制树和菜单样式的展现）
	var westLayoutManager = new function(){
		var layoutCtn = $("BODY");
		var initWestCtn = $("#JS_layoutWest");
		var initWidth = initWestCtn.width();
		var westCtn;
		
		var navMgrs = {
			tree: treeWestManager
			,menu: menuWestManager
		};
		var METHODS = ["beforeShow", "initShow", "onSelect"];
		var curNavType = "tree";
		
		var allNavs = [];
		
		function getWestCtn(){
			westCtn = westCtn || layoutCtn.layout("panel", "west").parent();
			return westCtn;
		}
		function resize(w){
			var westPanel = layoutCtn.layout("panel", "west");
			if(westPanel.length == 0) return;
			var opts = westPanel.panel("options");
			opts.width = w;
			opts.minWidth = 0;
			layoutCtn.layout("resize");
		}
		
		this.show = function(){
			if(!westCtn || westCtn.length==0){
				initWestCtn.removeClass("vhide");
				layoutCtn.layout("add", {region:"west", split:0, el:initWestCtn});
			}else{
				getWestCtn().show();
			}
			resize(initWidth);
		};
		this.hide = function(){
			getWestCtn().hide();
			resize(0);
		};
		this.curMenu = function(){
			return navMgrs[curNavType];
		};
		var changeNav = this.changeNav = function(type){
			if(type == curNavType) return;
			
			var curNav = navMgrs[curNavType];
			var curNavObj = menuManager.navData(curNav.currentTree());
			curNav.ctn.parent().hide();
			var curWidth = navMgrs[type].ctn.parent().show().width();
			curNavType = type;
			menuManager.refresh(curNavObj);
			resize(curWidth);
		};
		
		this.buildNavs = function(d){
			var navs = {};
			var type = d.type;
			$.each(navMgrs, function(k, navMgr){
				var navMgrType = navMgr.types[type];
				var ctn = navMgr.ctn;
				navs[k] = navMgrType.build(d).hide().appendTo(ctn);
			});
			
			var result = {
				index: allNavs.length,
				navs: navs
			};
			$.each(METHODS, function(){
				var method = this;
				result[method] = function(){
					var navObj = this;
					var typeObj = navMgrs[curNavType].types[navObj.cfg.type];
					var f = typeObj[method];
					if(f) {
						var nav = navObj.navs[curNavType];
						var changeNavTo = nav.data("changeNav");
						if(!changeNavTo){
							var args = $.makeArray(arguments);
							args.unshift(nav);
							return f.apply(typeObj, args);
						}else{
							changeNav(changeNavTo);
							arguments.callee.call(navObj);
						}
					}
				};
			});
			
			allNavs.push(result);
			return result;
		};
	};
	
	//	menuManager 顶部一级导航
	var menuManager = new function(){
		var MENU_DATA_KEY = "_menu";
		var CLZ_NAV_ACTIVE = "active";
		var me = this;
		var current = null;
		
		this.build = function(topNav){
			var name = topNav.text();
			var clazz = topNav.attr("class");
			var d = topNav.data("menu");
			d = d || {
				type: 'nodata',
				data: null
			};
			
			var menuObj = westLayoutManager.buildNavs(d);
			$.extend(menuObj, {
				cfg: d,
				data: {
					name: name,
					clazz: clazz,
					navDom: topNav
				},
				updateTopNav: function(active){
					this.data.navDom[active? "addClass": "removeClass"](CLZ_NAV_ACTIVE);
				}
			});
			$.each(menuObj.navs, function(k, nav){
				nav.data(MENU_DATA_KEY, menuObj).addClass(d.cls);
				$.extend(nav, {
					activeTopNav: function(){
						menuManager.navData(this).updateTopNav(true);
						return this;
					},
					inactiveTopNav: function(){
						menuManager.navData(this).updateTopNav(false);
						return this;
					}
				});
			});
			return menuObj;
		};
		this.select = function(menuObj, initShow, data){
			var selected = null;
			
			if(!menuObj || current===menuObj) {
				selected = current;
			}else{
				if(current) current.selectData = null;
				selected = current = menuObj;
			}
			current.selectData = data;
			
			if(selected == menuObj){
				menuObj.beforeShow();
			}
			if(initShow && selected){
				menuObj.initShow();
			}
			if(data) {
				menuObj.onSelect(data);
			}
			
			return selected;
		};
		this.refresh = function(navObj){
			var cur = navObj || this.current();
			return this.select(cur, false, cur.selectData);
		};
		this.current = function(){
			return current;
		};
		this.navData = function(nav){
			return nav.data(MENU_DATA_KEY);
		};
	};
	
	
	
	//	tabManager 标签页管理
	var tabManager = new function(){
		var DATA_KEY_TAB_NEEDFRESH = "data_key_tab_needfresh";
		var SCROLL_WIDTH = 0;
		var TPL_IFRAME = "<iframe src='{0}' scrolling='{2}' frameborder='0' style='width:{1}px;height:100%;' allowtransparency='true'></iframe>";
		var IFRAME_SCROLLABLE = true || ($.browser.msie && !window.outerHeight);
		
		var updateTabClz = function(elm){
			var tabs = $(elm).tabs("tabs");
			var maxIndex = tabs.length - 1;
			$.each(tabs, function(i){
				var tab = $(this).panel('options').tab;
				tab.removeClass(CLS_TAB_LAST);
				tab.removeClass(CLS_TAB_FIRST);
				if(i == 0) tab.addClass(CLS_TAB_FIRST);
				if(i == maxIndex) tab.addClass(CLS_TAB_LAST);
			});
		}
		
		var contentTab = $('#JS_contentTab').tabs({
			border: false,
			fit: true,
			plain: true,
			onSelect: function(title, index){
				var tabs = $(this).tabs("tabs");
				$.each(tabs, function(i){
					$(this).panel('options').tab.css("zIndex", 999-i);
				});
				
				var tab = $(tabs[index]).panel('options').tab;
				tab.css("zIndex", 1000);
				
				updateTabClz(this);
			},
			onAdd: function(title, index){
				updateTabClz(this);
			},
			onClose: function(title, index){
				updateTabClz(this);
			}
		});
		
		var tabsPanelsCtn = contentTab.children(".tabs-panels");
		var widthFix = IFRAME_SCROLLABLE? 0: (tabsPanelsCtn.outerWidth()-tabsPanelsCtn.width());
		var FRAME_WIDTH_FIX =   - widthFix;
		var FRAME_WIDTH = contentTab.width() + FRAME_WIDTH_FIX - SCROLL_WIDTH;
		var CLS_TAB_LAST = "last";
		var CLS_TAB_FIRST = "first";
		
		
		function refreshTab(title, url){
	        var cfg = {tabTitle:title, url:url};
	        var refresh_tab = cfg.tabTitle? 
	        		contentTab.tabs('getTab',cfg.tabTitle): 
	        		contentTab.tabs('getSelected');
	        		
			if(refresh_tab && refresh_tab.find('iframe').length > 0){
				var _refresh_ifram = refresh_tab.find('iframe')[0];
				var refresh_url = cfg.url? cfg.url: _refresh_ifram.src;
				_refresh_ifram.contentWindow.location.href=refresh_url;
			}
		}
		
		function addTab(menu, title, url, closable){
			contentTab.tabs('add', {
				menu: menu,
		        title: title,
		        content: $.stringFormat(TPL_IFRAME, url, FRAME_WIDTH, "no"),
		        closable: closable,
		        onBeforeDestroy : function() {
		        	var frame = $('iframe', this);
		        	try {
		        		if (frame.length > 0) {
		        			var f = frame[0];
		        			f.contentWindow.document.write('');
		        			f.contentWindow.close();
		        			frame.remove();
		        			if ($.browser.msie) {
		        				CollectGarbage();
		        			}
		        		}
		        	} catch (e) {}
		        },
		        onOpen: function(){
		        	var opts = $.data(this, 'panel').options;
		        	menu = opts.menu; 
		        	if(!menu) return;
		        	var menuId = menu.id;
		        	var navObj = menu.navObj;
		        	
		        	$(this).data(DATA_KEY_TAB_NEEDFRESH, false);
		        	menuManager.select(navObj, false, menu);
		        	$(this).data(DATA_KEY_TAB_NEEDFRESH, true);
		        },
		        onResize: function(w, h){
		        	$('iframe', this).css({
		        		"width": w + FRAME_WIDTH_FIX
		        	});
		        }
		    });
		}
		
		
		var openTab = function(title, url, menuObj, unclosable, refresh){
			var MAX = 6;
			
			var exists = contentTab.tabs('exists', title);
			if(exists){
				var tab = contentTab.tabs('getTab', title);
				var tabIndex = contentTab.tabs('getTabIndex', tab);
				var tabMenuObj = tab.data("panel").options.menu;
				if(tabIndex == 0){//首页加额外数据
					if(!tabMenuObj.menuObj){
						tabMenuObj.menuObj = menuObj;
						$.extend(tabMenuObj, menuObj);
					}
				}else{//第一个是首页，不能去重
					var compareP = "id";
					if(tabMenuObj[compareP] != menuObj[compareP]){ //标题去重
						exists = false;
					}
				}
			}
			
			if (exists) {//如果tab已经存在,则选中并刷新该tab
				if(contentTab.tabs("tabs").length == 1){ //只有一个的情况，就是只有首页
					var tab = contentTab.tabs('getTab', 0);
					var data = $.data(tab, 'panel');
					if(!data){
						data = tab.data('panel');
						$.data(tab, 'panel', data)
					}
					data.options.onOpen.call(tab);
				}else{
					contentTab.tabs('select', title);
				}
		        
		        if(refresh === undefined){
		        	var tab = contentTab.tabs('getSelected');
		        	refresh = ($(tab).data(DATA_KEY_TAB_NEEDFRESH) !== false);
		        }
		        if(refresh) refreshTab(title, url);
			} else {
				var tabs = contentTab.tabs("tabs");
				if(tabs.length >= MAX){
					var index = MAX-1;
					var tab = tabs[index];
					contentTab.tabs('update', {
						tab: tab,
						options: {
							menu: menuObj,
					        title: title
						}
					});
					contentTab.tabs("select", index);
					$("iframe", tab)[0].contentWindow.location.href=url;
				}else{
					addTab(menuObj, title, url, !unclosable);
				}
			}
		};
		
		return {
			addTab: addTab,
			openTab: openTab,
			current: function(){
				return contentTab.tabs("getSelected");
			}
		};
	};
	
	//workspace
	$.workspace = new function(){
		var mainNav  = $("#JS_mainNav");
		var DATA_KEY_MENU = "DATA_KEY_MENU";
		
		//read nav
		var initialNavData = mainNav.data("initial");
		var initialNav = initialNavData.nav;
		var firstMenu = null;
		var homeDom = $("<DIV />").data("menu", {type: "home", data: initialNavData.home});
		//构建顶部一级导航
		$("A", mainNav).add(homeDom).each(function(i){
			var that = $(this);
			var menu = menuManager.build($(this));
			that.data(DATA_KEY_MENU, menu);
			if(initialNav && that.hasClass(initialNav)){
				menuManager.select(menu);
				initialNav = null;
			}else if(i == 0){
				firstMenu = menu;
			}
		}).click(function(){
			var that = $(this);
			var menu = that.data(DATA_KEY_MENU);
			menuManager.select(menu, true);
		});
		//initialNav is not null means, there isn't menu matches the initialNav
		if(initialNav && firstMenu){
			menuManager.select(firstMenu);
		}
		
		$.delay(0, function(){homeDom.click();});
		
//		westLayoutManager.changeNav("menu");
		
		return {
			openTab: function(title, url, menu, unclosable, refresh){
				return tabManager.openTab(title, url, menu, unclosable, refresh);
			}
		};
	};
});

