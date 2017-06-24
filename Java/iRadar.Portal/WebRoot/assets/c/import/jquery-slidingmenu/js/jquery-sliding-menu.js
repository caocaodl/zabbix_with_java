/*
 *
 *	jQuery Sliding Menu Plugin
 *	Mobile app list-style navigation in the browser
 *
 *	Written by Ali Zahid
 *	http://designplox.com/jquery-sliding-menu
 *
 */

(function($) {
	var usedIds = [];

	$.fn.softMenu = function(options) {
		var selector = this.selector;

		var settings = $.extend({
			dataJSON: false,
			backLabel: '返回'

		}, options);

		return this.each(function() {
			var self = this,
				menu = $(self),
				data;

			if (menu.hasClass('sliding-menu')) {
				return;
			}

			var menuWidth = menu.width();

			if (settings.dataJSON) {
				data = processJSON(settings.dataJSON);
			} else {
				data = process(menu);
			}

			menu.empty().addClass('sliding-menu');

			var rootPanel;

			if (settings.dataJSON) {
				$(data).each(function(index, item) {
					var panel = $('<ul></ul>');

					if (item.root) {
						rootPanel = '#' + item.id;
					}

					panel.attr('id', item.id);
					panel.addClass('menu-panel');
					panel.width(menuWidth);

					$(item.children).each(function(index, item) {
						var link = $('<a></a>');

						link.attr('class', item.styleClass);
						link.attr('href', item.href);
						link.text(item.label);

						var li = $('<li></li>');

						li.append(link);

						panel.append(li);

					});

					menu.append(panel);

				});
			} else {
				$(data).each(function(index, item) {
					var panel = $(item);

					if (panel.hasClass('menu-panel-root')) {
						rootPanel = '#' + panel.attr('id');
					}

					panel.width(menuWidth);
					menu.append(item);

				});
			}

			rootPanel = $(rootPanel);
			rootPanel.addClass('menu-panel-root');

			var currentPanel = rootPanel;

			menu.height(rootPanel.height());

			var wrapper = $('<div></div>').addClass('sliding-menu-wrapper');//.width(data.length * menuWidth);

			menu.wrapInner(wrapper);

			wrapper = $('.sliding-menu-wrapper', menu);
			//计算返回上级按钮所占宽度

			var aTop = menu.find("a.top")[0],
				aBack = menu.find("a.back"),
				paddingLeft = (menuWidth - aTop.offsetWidth) / 2;

			aBack.css({
				"padding-left": paddingLeft,
				"padding-right": paddingLeft
			});
			menu.find(".menu-panel").addClass("close");


			$('a', $('.sliding-menu-wrapper')).on('click', function(e) {
				var $this = $(this),
					href = $this.attr('href'),
					label = $this.text();

				function switchOpen() {
					target.addClass("open").removeClass("close").siblings().removeClass("open").addClass("close");
					$(this).css('margin-left', "0");
				};
				if (href == '#') {
					e.preventDefault();
				} else if (href.indexOf('#menu-panel') == 0) {
					var target = $(href),
						isBack = $this.hasClass('back'),
						isTop = $this.hasClass('top'),
						marginLeft = parseInt(wrapper.css('margin-left'));

					if (isBack) {
						if (href == '#menu-panel-back') {
							target = currentPanel.prev();
						}

						wrapper.animate({
							marginLeft: marginLeft + menuWidth
						}, 1, switchOpen);
					} else if (isTop) {
						if (href == '#menu-panel-top') {
							target = currentPanel.prevAll(".menu-panel-root");
						}
						wrapper.animate({
							marginLeft: 0
						}, 1, switchOpen);
					} else {

						target.insertAfter(currentPanel);
						var $back = $('.back', target);
						//显示上级菜单名称
						if (settings.backLabel === true) {
							$back.text(label);
						} else if (settings.backLabel === "icon") { //显示上级菜单图标
							var DATA_KEY = "top_icon_cls";
							var iconCls = $this.attr("class").split(" ")[0];
							if (iconCls == "treeicon") {
								iconCls = menu.data(DATA_KEY);
							} else {
								menu.data(DATA_KEY, iconCls);
							}
							$('.top', target).attr("class", "top " + iconCls);
						} else { //显示名称
							$back.text(settings.backLabel);
						}

						wrapper.animate({
							marginLeft: marginLeft - menuWidth
						}, 1, switchOpen);

					}


					currentPanel = target;
					var tarHeight = target.height();
					if (target.height() == undefined || target.height() == 'undefined' || parseInt(target.height()) == 0) {
						tarHeight = parseInt(menu.parent().parent().height());
					}
					menu.animate({
						height: tarHeight
					}, 1);

					e.preventDefault();
				} else {

				}

			});

			return this;

		});

		function process(data) {
			var ul = $('ul', data),
				panels = [];

			$(ul).each(function(index, item) {
				var panel = $(item),
					handler = panel.prev(),
					id = getNewId();

				if (handler.length == 1) {
					handler.addClass('nav').attr('href', '#menu-panel-' + id);
				}

				panel.attr('id', 'menu-panel-' + id);

				if (index == 0) {
					panel.addClass('menu-panel-root ');
				} else {
					panel.addClass('menu-panel ');
					var li = $('<li></li>'),
						back = $('<a></a>').addClass('back').attr('href', '#menu-panel-back'),
						top = $('<a></a>').addClass('top').attr('href', '#menu-panel-top');

					panel.prepend(back).prepend(top);
				}

				panels.push(item);

			});

			return panels;
		}

		function processJSON(data, parent) {
			var root = {
					id: 'menu-panel-' + getNewId(),
					children: [],
					root: (parent ? false : true)
				},
				panels = [];

			if (parent) {
				root.children.push({
					styleClass: 'back',
					href: '#' + parent.id

				});
			}

			$(data).each(function(index, item) {
				root.children.push(item);

				if (item.children) {
					var panel = processJSON(item.children, root);

					item.href = '#' + panel[0].id;
					item.styleClass = 'nav';

					panels = panels.concat(panel);
				}

			});

			return [root].concat(panels);
		}

		function getNewId() {
			var id;

			do {
				id = Math.random().toString(36).substring(3, 8);
			}
			while (usedIds.indexOf(id) >= 0);

			usedIds.push(id);

			return id;
		}

	};

}(jQuery));