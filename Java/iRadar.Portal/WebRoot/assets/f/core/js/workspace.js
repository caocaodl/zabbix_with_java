$(function() {

	var ctxpath = window.ctxpath || "";

	var CmdManager = new function() {
		var filter, collapse;

		function filterDFS(db, v, force) {
			if (!db) return;
			var hasHit = false;
			v = v.toLowerCase();
			$.each(db, function() {
				var hit = (force !== undefined) ? force : !(this.text.toLowerCase().indexOf(v) == -1);
				var r = filterDFS(this.children, v, hit ? true : undefined);
				if (r) hit = true;
				if (hit) hasHit = true;
				$("#" + this.domId)[hit ? "show" : "hide"]();
			});
			return hasHit;
		}
		var filterF = function(v) {
			var tree = treeWestManager.currentTree();
			if (tree) {
				var treeState = tree.data("tree");
				if (treeState) {
					filterDFS(treeState.data, v);
				}
			}
		};

		this.filter = function(elm) {
			$(elm).placeholder().monitor(filterF);
		};
		this.collapse = function(elm) {
			$(elm).click(function() {
				westLayoutManager.changeNav("menu");
			});
		};
		this.expand = function(elm) {
			$(elm).click(function() {
				westLayoutManager.changeNav("tree");
			});
		};
	};

	//树样式（较宽，使用easyui的tree来实现itree或simple样式）
	var treeWestManager = new function() {
		var NORMAL = $("#JS_layoutWest>.west_tree");
		var ctn = NORMAL.children(".body");
		var title = NORMAL.find(".title");
		var CLZ_TITLE_ORIGIN = title.attr("class");
		var current = null;

		CmdManager.filter(NORMAL.find(".cmds INPUT"));

		var collapseElm = NORMAL.find(".cmds .collapse");
		CmdManager.collapse(collapseElm);

		function getNavObj(nav) {
			return menuManager.navData(nav);
		}

		var TYPE_EMPTY = {
			build: function(data) {
				var that = this;
				var proxy = $("<DIV />").attr("url", data.url);
				proxy.tree = function(opt, id) {
					var menu = $(this);
					if (opt == "select") {
						var navObj = getNavObj(this);
						var title = navObj.data.name;
						var url = menu.attr("url");
						if (!url) return;
						$.workspace.openTab(title, ctxpath + url, {
							id: "",
							navObj: data
						});
					}
				}
				return proxy;
			},
			initShow: function(menu) {
				menu.tree("select");
			},
			beforeShow: function() {

				if (current) {
					$(".checked", current).removeClass("checked");
					var select = current.tree("getSelected");
					if (select) $(select.target).removeClass("tree-node-selected")
				}
			}
		}

		var TYPE_HOME = $.extend({}, TYPE_EMPTY, {
			build: function(data) {
				var that = this;
				var KEY_HOME = "home";
				var proxy = $("<DIV />").data(KEY_HOME, data.data);
				proxy.tree = function(opt, id) {
					var menu = $(this);
					if (opt == "select") {
						var navObj = getNavObj(this);
						var homeData = menu.data(KEY_HOME);
						var title = homeData.title;
						var url = homeData.url;
						if (!url) return;
						$.workspace.openTab(title, ctxpath + url, {
							id: "",
							navObj: navObj
						}, true);
					}
				}
				return proxy;
			},
			onSelect: function(tree, data) {
				var menuId = data.id;
				if (!menuId) return;

				var cur = tree.tree("getSelected");
				if (cur && cur.target.id == menuId) return;

				var target = $("#" + menuId, ctn);

				var curTabData = menuManager.current().selectData;
				if (curTabData && curTabData.id != menuId) {
					tree.tree("select", target);
					tree.tree("scrollTo", target);
				} else {
					var clz = "tree-node-selected";
					if (cur) $(cur.target).removeClass(clz);
					$(target).addClass(clz);
				}
			}
		});

		var TYPE_TREE = {
			nestCheckedBranch: function(menu, node) {
				menu.find(".checked").removeClass("checked");

				var parent = node;
				while (true) {
					parent = menu.tree("getParent", parent.target);
					if (!parent) break;
					$(parent.target).addClass("checked");
				}
			},
			build: function(data) {
				/**
				 * 创建数据
				 */
				var createData = function(data, iconCls) {
						var that = arguments.callee,
							ul = "<ul>";
						$.each(data, function() {

							var _thisCls = this.iconCls ? " " + this.iconCls : "",
								cls = iconCls ? iconCls + _thisCls : _thisCls;

							ul += "<li><a href='#' alt='" + this.text + "'  class='" + cls + "'>" + this.text + "</a>";
							if (this.children) {
								ul += that(this.children, "treeicon");
							}
							ul += "</li>";
						});
						ul += "</ul>";
						return ul;
					}
					//返回jquey创建的dom元素
				return $(createData(data.data));
			},
			initShow: function(tree) {
				var toSelectNode = null;
				var initial = tree.tree("options").initial;
				if (initial) {
					toSelectNode = tree.tree("find", initial);
				}
				if (!toSelectNode) {
					function dfs(node) {
						var target = node.target;
						if (tree.tree("isLeaf", target)) {
							return node;
						} else {
							var childs = tree.tree("getChildren", target);
							var leaf;
							$.each(childs, function() {
								leaf = dfs(this);
								if (leaf) return false;
							});
							return leaf;
						}
					}
					toSelectNode = dfs(tree.tree("getRoot"));
				}
				if (toSelectNode) {
					tree.tree("select", toSelectNode.target);
				}
			},
			beforeShow: function(tree) {
				//	westLayoutManager.show();
				if (current) {
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

				if (shouldNotShowMenu || canNotCreateMenu) { //隐藏收缩按钮
					collapseElm.hide();
				} else {
					collapseElm.show();
				}
			},
			onSelect: function(tree, data) {
				var menuId = data.id;
				if (!menuId) return;

				var cur = tree.tree("getSelected");
				if (cur && cur.target.id == menuId) return;

				var target = $("#" + menuId, ctn);

				var curTabData = menuManager.current().selectData;
				if (curTabData && curTabData.id != menuId) {
					tree.tree("select", target);
					tree.tree("scrollTo", target);
				} else {
					var clz = "tree-node-selected";
					if (cur) $(cur.target).removeClass(clz);
					$(target).addClass(clz);

					TYPE_TREE.nestCheckedBranch(tree, tree.tree("getNode", target));
				}
			},
			openTab: function(softMenu, data) {
				var that = this,
					ACCORDIONTITLE = "accordion_title"; //标题样式
				$.each(data, function() {
					if (this.href && (this.iconCls && this.iconCls.indexOf(ACCORDIONTITLE) == -1)) {
						var $a = softMenu.find("a[alt=" + this.text + "]"),
							obj = this;
						$a.parent().click(function() {
							var oid = obj.id,
								url = obj.href;
							$.workspace.openTab(oid,
								ctxpath + url, {
									oid: oid
								});
						});
					}
					if (this.children) {
						that.openTab(softMenu, this.children);
					}
				});
			}
		}

		this.types = {
			nodata: TYPE_EMPTY,
			tree: TYPE_TREE,
			home: TYPE_HOME,
			nomenu: TYPE_EMPTY
		}
		this.ctn = ctn;
		this.currentTree = function() {
			//return ctn.children("UL:visible");
			return $(ctn.find("UL")[0]);
		}
	};

	//菜单样式（较窄，使用easyui的menu实现的样式）
	var menuWestManager = new function() {
		var NORMAL = $("#JS_layoutWest>.west_menu");
		var ctn = NORMAL.children(".body");
		var current = null;
		var DATA_KEY = "menu";
		var CLZ_SELECTED = "active";

		CmdManager.expand(NORMAL.find(".cmds .expand"));

		function getNavObj(nav) {
			return menuManager.navData(nav);
		}

		function menuClick(e) {
			var data = $(this).data(DATA_KEY);

			var url = data.href;
			if (!url) return;

			TYPE_TREE.nestCheckMenu(data);

			var id = data.domId,
				title = data.text,
				navObj = getNavObj(data.dom),
				ACCORDIONTITLE = "accordion_title"; //标题样式
			if (!data.iconCls || (data.iconCls && data.iconCls.indexOf(ACCORDIONTITLE) == -1)) {
				$.workspace.openTab(title, ctxpath + url, {
					id: id,
					oid: data.id,
					navObj: navObj
				});
			}
		}

		var TYPE_TREE = {
			nestCheckMenu: function(data) {
				//当选择首页类型的标签时，因不会调用nestCheckMenu方法，所以当用户再点击原来的标签后，会因两个ID相同而不重新加载选中样式，所以需要加有active选中样式做为判断条件，来过滤掉首页标签的情况
				var activeMenus = $("BODY>.menu .active");
				if (this.prevId == data.id && activeMenus.length > 0) {
					return;
				}
				this.prevId = data.id;

				activeMenus.removeClass(CLZ_SELECTED);

				var p = data;
				while (p) {
					$(p.elm).addClass(CLZ_SELECTED);
					p = p.parent;
				}
			},
			build: function(data) {
				var menuData = [];

				function flagMap(o, k) {
					o.map[k] = 1;
					while (o.parent) {
						o = o.parent;
						o.map[k] = 1;
					}
				}

				function filterMenu(c, fm) {
					if (!c) return fm;

					$.each(c, function() {
						var o = $.extend({}, this);
						o.parent = fm.parent;
						o.map = {};

						var cc = [];
						cc.parent = o;

						flagMap(o, this.id);
						//判断是否是动态加载树
						if (this.url || (filterMenu(this.children, cc).over)) {
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
				if (menuData.over) return null;

				function buildMenu(d, ctn, depth) {
					if (!d.parent) {
						$("<DIV />").text(d.text).addClass("root depth0 " + d.id).appendTo(ctn).attr("data-options", "disabled:true");
					}

					$.each(d.children, function(i) {
						var div = $("<DIV />");

						this.dom = menuCtn;
						this.elm = div;

						(depth == 1 ? ctn : ctn.parent()).append(div);
						div.data(DATA_KEY, this);

						var posClass = (i == 0) ? "first" : (i == d.children.length - 1 ? "last" : ""),

							c = this.children;

						div.addClass(posClass + " depth" + depth + " " + this.id + " " + this.iconCls).text(this.text);

						if (c && c.length > 0) {
							div.addClass("branch").attr("data-options", "disabled:true");
							buildMenu(this, div, depth + 1);
						} else {
							div.addClass("leaf").click(menuClick);
						}
					});
				}

				var menuCtn = $("<DIV />");
				$.each(menuData, function() {
					this.dom = menuCtn;
					var btn = $("<A />").text(this.text).addClass(this.iconCls)
						.attr("href", "javascript:void(0)").appendTo(menuCtn)
						.data(DATA_KEY, this).click(menuClick);

					var menu = null;
					if (this.children.length > 0) {
						var menu = $("<DIV />").appendTo("BODY");
						buildMenu(this, menu, 1);
					}

					btn.menubutton({
						menu: menu
					});
					menu.data("parent", btn);

					if (menu) {
						$.extend(menu.menu('options'), {
							onShow: function() {
								var $winHeight = $(window).height(),
									m = $(this),
									$parent = m.data("parent"),
									$parentOffset = $parent.offset();
								$parent.addClass("show");

								if (m.hasClass("menu-top")) {
									//计算页面高度减去菜单按钮top高度的剩余空间高度
									var spaceHeight = parseInt($winHeight) - parseInt($parentOffset.top);
									var menuHeight = spaceHeight > m.height() + $parent.height() ? "auto" : parseInt(spaceHeight) - parseInt($parent.height() / 2) + "px";
									m.css({
										top: $parentOffset.top,
										left: $parent.width(),
										height: menuHeight,
										overflow: "auto"
									});

									$(".menu-item", m).css("height", "auto");

									m.next(".menu-shadow").hide();
								}
							},
							onHide: function() {
								$(this).data("parent").removeClass("show");
							}
						});
					}
				});

				return menuCtn;
			},
			initShow: function(menu) {
				typeAdapter("tree", "initShow", menu);
			},
			beforeShow: function(menu) {
				if (current) {
					current.hide();
				}
				current = menu.show();
				typeAdapter("tree", "beforeShow", menu);
			},
			onSelect: function(menu, data) {
				var menuId = data.oid;
				if (!menuId) return;

				$("A", menu).each(function() {
					var dom = $(this).removeClass(CLZ_SELECTED);
					var data = dom.data(DATA_KEY);
					if (data.map[menuId]) dom.addClass(CLZ_SELECTED);
				});

				var elm = $("BODY>.menu ." + data.oid);
				TYPE_TREE.nestCheckMenu(elm.data(DATA_KEY));
			}
		};

		var originTreeBuildF = TYPE_TREE.build;
		TYPE_TREE.build = function() {
			return originTreeBuildF.apply(this, arguments) || new function() {
				//对于不在控制范围之内的树，增加changeNav为tree的标识，以去掉收缩按钮
				return $("<DIV />").data("changeNav", "tree");
			};
		};

		var typeAdapter = function(type, f, menu) {
			var tree = getNavObj(menu).navs.tree;
			return treeWestManager.types[type][f](tree);
		};

		var proxyType = function(type) {
			var proxyType = treeWestManager.types[type];
			return proxyType;
		};


		var home = proxyType('home');
		var originHomeBeforeF = home.beforeShow;
		home.beforeShow = function(menu) {
			originHomeBeforeF.apply(this, arguments);
			if (current) {
				$("A", current).removeClass(CLZ_SELECTED);
				$("BODY>.menu .active").removeClass(CLZ_SELECTED);
			}
		};

		this.types = {
			nodata: proxyType('nodata'),
			tree: TYPE_TREE,
			home: home,
			nomenu: proxyType('nomenu')
		};
		this.ctn = ctn;
		this.currentTree = function() {
			return ctn.children("DIV:visible");
		};
	};

	//	westLayoutManager（左侧区域管理类，主要控制树和菜单样式的展现）
	var westLayoutManager = new function() {
		var layoutCtn = $("BODY");
		var initWestCtn = $("#JS_layoutWest");
		var initWidth = initWestCtn.width();
		var westCtn;

		var navMgrs = {
			tree: treeWestManager,
			menu: menuWestManager
		};
		var METHODS = ["beforeShow", "initShow", "onSelect"];
		var curNavType = "tree";

		var allNavs = [];

		function getWestCtn() {
			westCtn = westCtn || layoutCtn.layout("panel", "west").parent();
			return westCtn;
		}

		function resize(w) {
			var westPanel = layoutCtn.layout("panel", "west"),
				tree = $(".layout-panel-north .expand").hasClass("tree"),
				opts = westPanel.panel("options");
			if (westPanel.length == 0) return;

			opts.width = w;
			opts.minWidth = 0;

			layoutCtn.layout("resize");

		}

		this.show = function() {
			if (!westCtn || westCtn.length == 0) {
				initWestCtn.removeClass("vhide");
				layoutCtn.layout("add", {
					region: "west",
					split: 0,
					el: initWestCtn
				});
			} else {
				getWestCtn().show();
			}
			resize(initWidth);
		};
		this.hide = function() {
			getWestCtn().hide();
			resize(0);
		};
		this.curMenu = function() {
			return navMgrs[curNavType];
		};
		var changeNav = this.changeNav = function(type) {
			if (type == curNavType) return;

			var curNav = navMgrs[curNavType];
			var curNavObj = menuManager.navData(curNav.currentTree());
			curNav.ctn.parent().hide();
			var curWidth = navMgrs[type].ctn.parent().show().width() + parseInt(initWestCtn.css("border-right-width"));
			curNavType = type;
			menuManager.refresh(curNavObj);
			resize(curWidth);
		};

		this.buildNavs = function(d) {
			var navs = {};
			var type = d.type;
			$.each(navMgrs, function(k, navMgr) {
				var navMgrType = navMgr.types[type];
				var ctn = navMgr.ctn;
				navs[k] = navMgrType.build(d).hide().appendTo(ctn);
			});
			if (type == "tree") {
				var softMenu = $("#JS_layoutWest>.west_tree").children(".body").softMenu({
					backLabel: "icon"
				});
				//注册菜单单击事件
				navMgrs[type].types[type].openTab(softMenu, d.data);
			}
			var result = {
				index: allNavs.length,
				navs: navs
			};
			$.each(METHODS, function() {
				var method = this;
				result[method] = function() {
					var navObj = this;
					var typeObj = navMgrs[curNavType].types[navObj.cfg.type];
					var f = typeObj[method];
					if (f) {
						var nav = navObj.navs[curNavType];
						var changeNavTo = nav.data("changeNav");
						if (!changeNavTo) {
							var args = $.makeArray(arguments);
							args.unshift(nav);
							return f.apply(typeObj, args);
						} else {
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
	var menuManager = new function() {
		var MENU_DATA_KEY = "_menu";
		var CLZ_NAV_ACTIVE = "active";
		var me = this;
		var current = null;

		this.build = function(topNav) {
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
				updateTopNav: function(active) {
					this.data.navDom[active ? "addClass" : "removeClass"](CLZ_NAV_ACTIVE);
				}
			});
			$.each(menuObj.navs, function(k, nav) {
				nav.data(MENU_DATA_KEY, menuObj).addClass(d.cls);
				$.extend(nav, {
					activeTopNav: function() {
						menuManager.navData(this).updateTopNav(true);
						return this;
					},
					inactiveTopNav: function() {
						menuManager.navData(this).updateTopNav(false);
						return this;
					}
				});
			});
			return menuObj;
		};
		this.select = function(menuObj, initShow, data) {
			var selected = null;

			if (!menuObj || current === menuObj) {
				selected = current;
			} else {
				if (current) current.selectData = null;
				selected = current = menuObj;
			}
			current.selectData = data;

			if (selected == menuObj) {
				menuObj.beforeShow();
			}
			if (initShow && selected) {
				menuObj.initShow();
			}
			if (data) {
				menuObj.onSelect(data);
			}

			return selected;
		};
		this.refresh = function(navObj) {
			var cur = navObj || this.current();
			return this.select(cur, false, cur.selectData);
		};
		this.current = function() {
			return current;
		};
		this.navData = function(nav) {
			return nav.data(MENU_DATA_KEY);
		};
	};



	//	tabManager 标签页管理
	var tabManager = new function() {
		var DATA_KEY_TAB_NEEDFRESH = "data_key_tab_needfresh";
		var SCROLL_WIDTH = 0;
		var TPL_IFRAME = "<iframe src='{0}' scrolling='{2}' allowfullscreen mozallowfullscreen webkitallowfullscreen frameborder='0' style='width:{1}px;height:100%;' allowtransparency='true'></iframe>";
		var IFRAME_SCROLLABLE = !$.browser.msie || window.outerHeight;

		var updateTabClz = function(elm) {
			var tabs = $(elm).tabs("tabs");
			var maxIndex = tabs.length - 1;
			$.each(tabs, function(i) {
				var tab = $(this).panel('options').tab;
				tab.removeClass(CLS_TAB_LAST);
				tab.removeClass(CLS_TAB_FIRST);
				if (i == 0) tab.addClass(CLS_TAB_FIRST);
				if (i == maxIndex) tab.addClass(CLS_TAB_LAST);
			});
		}

		var contentTab = $('#JS_contentTab').tabs({
			tabHeight: 40,
			border: false,
			fit: true,
			plain: true,
			onSelect: function(title, index) {
				var tabs = $(this).tabs("tabs");
				$.each(tabs, function(i) {
					$(this).panel('options').tab.css("zIndex", 999 - i);
				});

				var tab = $(tabs[index]).panel('options').tab;
				tab.css("zIndex", 1000);
				updateTabClz(this);
			},
			onAdd: function(title, index) {
				updateTabClz(this);
			},
			onClose: function(title, index) {
				updateTabClz(this);
			},
			onUnselect: function(title, index) {

			}
		});

		var tabsPanelsCtn = contentTab.children(".tabs-panels");
		var widthFix = IFRAME_SCROLLABLE ? 0 : (tabsPanelsCtn.outerWidth() - tabsPanelsCtn.width());
		var FRAME_WIDTH_FIX = 0; // - widthFix;
		var FRAME_WIDTH = 0;
		var CLS_TAB_LAST = "last";
		var CLS_TAB_FIRST = "first";

		//重新计算iframe宽度(由于左侧菜单可能初始化时处于收缩状态，所以需要重新计算宽度)
		function resizeFrame() {
			FRAME_WIDTH = tabsPanelsCtn.width() + FRAME_WIDTH_FIX - SCROLL_WIDTH;
		};
		resizeFrame();

		function refreshTab(title, url) {
			var cfg = {
				tabTitle: title,
				url: url
			};
			var refresh_tab = cfg.tabTitle ?
				contentTab.tabs('getTab', cfg.tabTitle) :
				contentTab.tabs('getSelected');

			if (refresh_tab && refresh_tab.find('iframe').length > 0) {
				var _refresh_ifram = refresh_tab.find('iframe')[0];
				var refresh_url = cfg.url ? cfg.url : _refresh_ifram.src;
				_refresh_ifram.contentWindow.location.href = refresh_url;
			}
		}

		function addTab(menu, title, url, closable) {
			contentTab.tabs('add', {
				menu: menu,
				title: title,
				content: $.stringFormat(TPL_IFRAME, url + "#!" + (new Date().getTime()), FRAME_WIDTH, "yes"),
				closable: closable,
				onBeforeDestroy: function() {
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
				onOpen: function() {
					var opts = $.data(this, 'panel').options;
					menu = opts.menu;
					// first load
					if (!$(this).html()) {
						//set window.location.hash=iframe.src.hash
						var hash = opts.content.split("#!")[1];
						hash = "#!" + hash.substring(0, hash.indexOf("'"));
						window.location.hash = hash;
					}
					if (!menu) return;
					var menuId = menu.id;
					var navObj = menu.navObj;

					$(this).data(DATA_KEY_TAB_NEEDFRESH, false);
					menuManager.select(navObj, false, menu);
					$(this).data(DATA_KEY_TAB_NEEDFRESH, true);

				},
				onResize: function(w, h) {
					$('iframe', this).css({
						"width": w + FRAME_WIDTH_FIX
					});
				}
			});
		}

		var openTab = function(title, url, menuObj, unclosable, refresh) {
			var MAX = 6;
			var exists = contentTab.tabs('exists', title);
			if (exists) {
				var tab = contentTab.tabs('getTab', title);
				var tabIndex = contentTab.tabs('getTabIndex', tab);
				var tabMenuObj = tab.data("panel").options.menu;
				if (tabIndex == 0) { //首页加额外数据
					if (!tabMenuObj.menuObj) {
						tabMenuObj.menuObj = menuObj;
						$.extend(tabMenuObj, menuObj);
					}
				} else { //第一个是首页，不能去重
					var compareP = "id";
					if (tabMenuObj && menuObj && tabMenuObj[compareP] != menuObj[compareP]) { //标题去重
						exists = false;
					}
				}
			}

			if (exists) { //如果tab已经存在,则选中并刷新该tab
				if (contentTab.tabs("tabs").length == 1) { //只有一个的情况，就是只有首页
					var tab = contentTab.tabs('getTab', 0);
					var data = $.data(tab, 'panel');
					if (!data) {
						data = tab.data('panel');
						$.data(tab, 'panel', data)
					}
					data.options.onOpen.call(tab);
				} else {
					contentTab.tabs('select', title);
				}

				if (refresh === undefined) {
					var tab = contentTab.tabs('getSelected');
					refresh = ($(tab).data(DATA_KEY_TAB_NEEDFRESH) !== false);
				}
				if (refresh) {
					refreshTab(title, url);
				}
			} else {
				var tabs = contentTab.tabs("tabs");
				if (tabs.length >= MAX) {
					var index = MAX - 1;
					var tab = tabs[index];
					contentTab.tabs('update', {
						tab: tab,
						options: {
							menu: menuObj,
							title: title
						}
					});
					contentTab.tabs("select", index);

					// $("iframe", tab)[0].contentWindow.location.href = url;

					var hash = "#!" + new Date().getTime();
					$("iframe", tab)[0].src = url + hash;
					window.location.hash = hash;
				} else {
					resizeFrame();
					addTab(menuObj, title, url, !unclosable);
				}
			}
		};

		return {
			addTab: addTab,
			openTab: openTab,
			current: function() {
				return contentTab.tabs("getSelected");
			},
			tabs: function() {
				return contentTab.tabs("tabs");
			}
		};
	};

	//workspace
	$.workspace = new function() {

		var mainNav = $("#JS_mainNav");
		var DATA_KEY_MENU = "DATA_KEY_MENU";

		//read nav
		var initialNavData = mainNav.data("initial");
		var initialNav = initialNavData.nav;
		var firstMenu = null;
		var homeDom = $("<DIV />").data("menu", {
			type: "home",
			data: initialNavData.home
		});

		function loadContent(hash) {

			var activeTab = tabManager.current();
			if (activeTab) {
				var selectIframe = activeTab.find("iframe")[0];
				if (selectIframe.src.indexOf(hash) == -1) {

					//active tab
					var tabs = tabManager.tabs();
					for (var i = 0, len = tabs.length; i < len; i++) {
						var iframe = tabs[i].find("iframe")[0];
						if (iframe.src.indexOf(hash) > -1) {
							tabManager.openTab(tabs[i].panel("options").title);
							break;
						}
					};
				}
			}

		}
		jQuery.history.init(loadContent);
		//构建顶部一级导航

		function hackHref(data) {
			if (!data) return;
			$.each(data, function() {
				var href = this.href;
				if (href) {
					var prefix = (href.indexOf('?') > 0) ? '&' : '?';
					this.href += prefix + "ddreset=1";
				}
				hackHref(this.children);
			})
		}

		var allMenusData = [];
		$("A", mainNav).each(function(i) {
			var that = $(this);
			var clsData = that.attr("class").split(" ");
			var iconCls = clsData[0]
			var id = clsData[1];
			var text = that.text();
			var menuData = that.data("menu");

			hackHref(menuData.data);

			allMenusData.push({
				id: id,
				iconCls: iconCls,
				state: "closed",
				text: text,
				children: menuData.data
			});

			if (i == 0) {
				firstMenu = $(this);
			}
		})

		//重写jeasyui.tree的生成树节点的方法，加入层级样式

		function getTreeView() {
			var nodeIndex = 1;
			return {
				render: function(target, ul, data) {
					var opts = $.data(target, 'tree').options;
					var depth = $(ul).prev('div.tree-node').find('span.tree-indent, span.tree-hit').length;
					var cc = getTreeData(depth, data);
					$(ul).append(cc.join(''));

					function getTreeData(depth, children) {
						var cc = [];
						for (var i = 0; i < children.length; i++) {
							var item = children[i];
							if (item.state != 'open' && item.state != 'closed') {
								item.state = 'open';
							}
							item.domId = '_easyui_tree_' + nodeIndex++;

							//加入自定义的层级样式
							var depthClass = "depth" + depth;
							var treeClass = (depth == 0 ? "root" : ((item.children && item.children.length) ? "branch" : "leaf"));
							var posClass = (i == 0 ? "first" : (i == children.length - 1) ? "last" : "");
							var customClass = depthClass + " " + treeClass + " " + posClass;

							cc.push('<li>');
							cc.push('<div id="' + item.domId + '" class="tree-node ' + customClass + '">');
							for (var j = 0; j < depth; j++) {
								cc.push('<span class="tree-indent"></span>');
							}
							if (item.state == 'closed') {
								cc.push('<span class="tree-hit tree-collapsed"></span>');
								cc.push('<span class="tree-icon tree-folder ' + (item.iconCls ? item.iconCls : '') + '"></span>');
							} else {
								if (item.children && item.children.length) {
									cc.push('<span class="tree-hit tree-expanded"></span>');
									cc.push('<span class="tree-icon tree-folder tree-folder-open ' + (item.iconCls ? item.iconCls : '') + '"></span>');
								} else {
									cc.push('<span class="tree-indent"></span>');
									cc.push('<span class="tree-icon tree-file ' + (item.iconCls ? item.iconCls : '') + '"></span>');
								}
							}
							if (opts.checkbox) {
								if ((!opts.onlyLeafCheck) || (opts.onlyLeafCheck && (!item.children || !item.children.length))) {
									cc.push('<span class="tree-checkbox tree-checkbox0"></span>');
								}
							}
							cc.push('<span class="tree-title">' + opts.formatter.call(target, item) + '</span>');
							cc.push('</div>');

							if (item.children && item.children.length) {
								var tmp = getTreeData(depth + 1, item.children);
								cc.push('<ul style="display:' + (item.state == 'closed' ? 'none' : 'block') + '">');
								cc = cc.concat(tmp);
								cc.push('</ul>');
							}
							cc.push('</li>');
						}
						return cc;
					}
				}
			};
		}

		firstMenu.data("menu", {
			type: "tree",
			data: allMenusData,
			view: getTreeView()
		}).add(homeDom).each(function(i) {
			var that = $(this);
			var menu = menuManager.build(that);
			that.data(DATA_KEY_MENU, menu);
			if (i == 0) {
				menuManager.select(menu);
			}
		}).click(function() {
			var that = $(this);
			var menu = that.data(DATA_KEY_MENU);
			menuManager.select(menu, true);
		});

		$.delay(0, function() {
			homeDom.click();
		});

		//此方法为首页header 收缩菜单按钮事件	
		$(".layout-panel-north .expand").click(function() {
			var that = $(this);
			var tree = that.hasClass("tree");
			westLayoutManager.changeNav(!tree ? "menu" : "tree");
			that.toggleClass("tree")
		});

		if (window.screen.width < 1024) {
			$(".layout-panel-north .expand").trigger("click");
		}

		//消除点击窄菜单的图标时，出现弹出窗闪烁的问题
		$(".west_menu").delegate("A", "mousedown", function(e) {
			$(e).stop();
			return false;
		});

		return {
			openTab: function(titleOrMenuId, url, menu, unclosable, refresh) {
				var navObj = firstMenu.data(DATA_KEY_MENU);

				var KEY_MAP = DATA_KEY_MENU + "_MAP";
				var navMap = firstMenu.data(KEY_MAP);
				if (!navMap) {
					navMap = {};
					var f = function(ds) {
						$.each(ds, function() {
							navMap[this.id] = this;
							if (this.children) {
								f(this.children);
							}
						});
					}
					f(navObj.cfg.data);
					firstMenu.data(KEY_MAP, navMap);
				}

				var nav = navMap[titleOrMenuId];
				if (nav) {
					titleOrMenuId = nav.text;
					menu = {
						id: nav.domId,
						oid: nav.id,
						navObj: navObj
					};
					url = url || (ctxpath + nav.href);
				}
				return tabManager.openTab(titleOrMenuId, url, menu, unclosable, refresh);
			}
		};
	};
});