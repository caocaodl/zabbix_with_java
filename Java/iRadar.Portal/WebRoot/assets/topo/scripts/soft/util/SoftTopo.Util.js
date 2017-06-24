/**
 * 工具类
 */
SoftTopo.Util = {
	ERROR: basePath + "images/soft/error.png",
	WARNING: basePath + "images/soft/warning.png",
	INFO: basePath + "images/soft/info.png",
	MONITORITEM: {
		"vm": [{
			"itemkey": "cpu",
			"itemCh": "CPU",
			"itemtype": "bar"
		}, {
			"itemkey": "memory",
			"itemCh": "MEM",
			"itemtype": "bar"
		}, {
			"itemkey": "upflow",
			"itemCh": "网络上行",
			"itemtype": "text"
		}, {
			"itemkey": "downflow",
			"itemCh": "网络下行",
			"itemtype": "text"
		}],
		"tomcat": [{
			"itemkey": "errorcount",
			"itemCh": "每秒请求错误数",
			"itemtype": "text"
		}, {
			"itemkey": "activesessions",
			"itemCh": "活动会话数",
			"itemtype": "text"
		}, {
			"itemkey": "curthreadsbusy",
			"itemCh": "繁忙线程数",
			"itemtype": "text"
		}, {
			"itemkey": "heapmemoryusage",
			"itemCh": "堆当前使用量",
			"itemtype": "text"
		}],
		"mysql": [{
			"itemkey": "freememory",
			"itemCh": "缓存空闲内存",
			"itemtype": "text"
		}, {
			"itemkey": "threadsconnected",
			"itemCh": "当前连接数",
			"itemtype": "text"
		}, {
			"itemkey": "connections",
			"itemCh": "总连接数",
			"itemtype": "text"
		}, {
			"itemkey": "system_connections",
			"itemCh": "系统会话个数",
			"itemtype": "text"
		}],
		"server": [{
			"itemkey": "cpu",
			"itemCh": "CPU",
			"itemtype": "bar"
		}, {
			"itemkey": "memory",
			"itemCh": "MEM",
			"itemtype": "bar"
		}, {
			"itemkey": "upflow",
			"itemCh": "网络上行",
			"itemtype": "text"
		}, {
			"itemkey": "downflow",
			"itemCh": "网络下行",
			"itemtype": "text"
		}],
		"net": [{
			"itemkey": "RuningTime",
			"itemCh": "运行时间",
			"itemtype": "text"
		}, {
			"itemkey": "ifNumber",
			"itemCh": "网口数量",
			"itemtype": "text"
		}, {
			"itemkey": "NetIFInOutERR",
			"itemCh": "网络丢包",
			"itemtype": "text"
		}, {
			"itemkey": "netRate",
			"itemCh": "网络速率",
			"itemtype": "text"
		}]
	},
	_uinit: function() {
		alert($(window).height()); // 浏览器当前窗口可视区域高度
		alert($(document).height()); // 浏览器当前窗口文档的高度
		alert($(document.body).height()); // 浏览器当前窗口文档body的高度
		alert($(document.body).outerHeight(true)); // 浏览器当前窗口文档body的总高度 包括(border padding margin)
		alert($(window).width()); // 浏览器当前窗口可视区域宽度
		alert($(document).width()); // 浏览器当前窗口文档对象宽度
		alert($(document.body).width()); // 浏览器当前窗口文档body的宽度
		alert($(document.body).outerWidth(true)); // 浏览器当前窗口文档body的总宽度 包括(border padding margin)
	},
	/**
	 * 获取元素相对滚动条左侧的偏移
	 */
	scrollLeft: function() {
		return $(document).scrollLeft();
	},
	/**
	 * 获取元素相对滚动条顶部的偏移
	 */
	scrollTop: function() {
		return $(document).scrollTop();
	},
	/**
	 * 浏览器当前窗口可视区域宽度
	 */
	getWinWidth: function() {
		return $(window).width();
	},
	/**
	 * 浏览器当前窗口可视区域高度
	 */
	getWinHeight: function() {
		return $(window).height();
	},
	/**
	 * 设置图标
	 */
	setIcon: function(node, type) {
		var image;
		switch (type.toLocaleLowerCase()) {
			case "vm":
				image = Q.Graphs.subnetwork;
				break;
			case "router":
				image = Q.Graphs.exchanger2;
				break;
			case "subnet":
				image = Q.Graphs.exchanger;
				break;
			case "cloud":
				image = Q.Graphs.cloud;
				break;
			case "server":
				image = Q.Graphs.server;
				break;
			case "g_group":
				image = icons[9];
				break;
			case "mysql":
				image = basePath + "/images/soft/mysql.png";
				break;
			case "tomcat":
				image = basePath + "/images/soft/tomcat.png";
				break;
			case "switch":
				image = Q.Graphs.exchanger;
				break;
			case "cab":
				image = basePath + "/images/soft/cab.png";
				break;
			case "cabhost":
				image = basePath + "/images/soft/card.png";
				break;
			case "routeswitch":
				image = basePath + "/images/soft/rSwitch.png";
				break;
			default:
				image = Q.Graphs.node;
				break;
		}
		node ? node.image = image : "";
		return image;
	},
	getMonitoritem: function(itemName) {
		return this.MONITORITEM[itemName];
	},
	/*
	 *删除页面logo
	 */
	removerLogo: function() {
		jQuery("canvas:nth-child(2)").remove();
	},
	/*
	 *全屏
	 */
	launchFullScreen: function(element) {

		if (element.requestFullScreen) {
			element.requestFullScreen();
		} else if (element.mozRequestFullScreen) {
			element.mozRequestFullScreen();
		} else if (element.webkitRequestFullScreen) {
			element.webkitRequestFullScreen();
		} else if (element.msRequestFullscreen) {
			element.msRequestFullscreen();
		}
	},
	/*
	 *取消全屏
	 */
	cancelFullscreen: function() {
		if (document.cancelFullScreen) {
			document.cancelFullScreen();
		} else if (document.mozCancelFullScreen) {
			document.mozCancelFullScreen();
		} else if (document.webkitCancelFullScreen) {
			document.webkitCancelFullScreen();
		} else if (document.msExitFullscreen) {
			document.msExitFullscreen();
		}
	},
	/**
	 * Display jQuery model window.
	 *
	 * @param string title					modal window title
	 * @param string text					window message
	 * @param array  buttons				window buttons
	 * @param array  buttons[]['text']		button text
	 * @param object buttons[]['click']		button click action
	 */
	showModalWindow: function(title, text, buttons) {

		var modalWindow = jQuery('#modalWindow');

		if (arguments.length == 1) {
			text = title;
			title = '提示';
		}

		buttons = buttons || [{
			text: '关闭',
			handler: function() {
				modalWindow.dialog('destroy');
			}
		}];
		if (modalWindow.length == 0) {
			modalWindow = jQuery('<div>', {
				id: 'modalWindow',
				css: {
					padding: '10px',
					display: 'none',
					'white-space': 'normal'
				}
			});

			jQuery('body').append(modalWindow);
		}

		modalWindow
			.html(text)
			.dialog({

				title: title,
				buttons: buttons,
				draggable: true,
				modal: true,
				resizable: false,
				minWidth: 200,
				minHeight: 120

			}).show();
	}
}