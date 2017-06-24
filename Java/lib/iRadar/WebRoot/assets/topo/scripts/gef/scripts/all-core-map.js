Ext.ns('App');
// 当前拖放控件对象
App.whichIt = null;
// 当前拖放控件x坐标
App.currentX = 0;
// 当前拖放控件y坐标
App.currentY = 0;
// 坐标差X
App.distanceX = 0;
// 坐标差Y
App.distanceY = 0;
/**
 * 加载控件
 */
App.initMapObj = function() {
	this.initMiniature();
}
/**
 * 初始化缩略图
 */
App.initMiniature = function() {
	this.initMiniatureHTML();
	this.initMiniatureDiv();
}
/**
 * 创建缩略图(图形连线)
 */
App.initMiniatureHTML = function() {

	// 生成缩略图
	var html = App.getBigMapHTML();
	var svgParentEl = $('<div></div>');
	svgParentEl.attr('id', '__gef_jbs__minCon').addClass('min-mapCon')
			.html(html);

	$('#__gef_jbs__platform').append(svgParentEl);
}
/**
 * 解析大图
 * 
 * @return xml
 */
App.getBigMapHTML = function() {
	var html = "";
	// 大图区域
	var centerEl = $("#__gef_jbs_center__");

	var childrens = centerEl.children();
	if (childrens.length > 0) {
		if (Gef.isIE) {
			html = App.vmlChild(childrens[0]);
		} else {
			html = App.svgChild(childrens[0]);
		}
	}
	return html;
}
/**
 * 清空缩略图
 */
App.clearMinMap = function() {

	$("#__gef_jbs__minCon").html("");
	App.whichIt == null;
}
/**
 * 刷新
 */
App.refreshMinMap = function() {
	var html = App.getBigMapHTML();

	
	$("#__gef_jbs__minCon").html("").html(html);

}
/**
 * 
 * @param {}
 *            obj
 * @return html
 */
App.svgChild = function(obj) {
	var html = "";
	var child = obj;
	// 标签名称
	var tagName = child.tagName;
	if (tagName === "defs" || tagName === "text" || child.id === "LAYER_HANDLE") {
		return "";
	}

	if (tagName === "svg") {
		html += "<" + tagName + " xmlns=http://www.w3.org/2000/svg";
	} else {
		html += "<" + tagName;
	}
	// 属性
	var attributes = child.attributes;
	for (var k = 0; k < attributes.length; k++) {
		var attr = attributes[k];
		if (attr.name === "marker-end" || attr.name === "marker-start") {
			if (attr.value.indexOf("markerEndArrow") != -1) {
				html += " " + attr.name + '=url(#minMarkerEndArrow)';
			}
			if (attr.value.indexOf("markerStartArrow") != -1) {
				html += " " + attr.name + '=url(#minMarkerStartArrow)';
			}
		}
		if (attr.name === "id" || attr.name === "edgeId") {
			html += " " + attr.name + '="' + attr.value + '_min" ';
		} else if (attr.name === "width") {
			html += ' width="' + attr.value / 8 + '" ';
		} else if (attr.name === "height") {
			html += ' height="' + attr.value / 8 + '" ';
		} else if (attr.name === "points") {
			// 连线坐标
			// points="538.6975308641975,254 590.3024691358024,357 "
			var points = attr.value.split(",");
			var pointStr = "";
			for (var i = 0; i < points.length; i++) {
				if (points[i].indexOf(" ") != -1) {
					var p = points[i].split(" ");
					if (p[0]) {
						pointStr += p[0] / 8 + " "
					}
					if (p[1]) {
						pointStr += p[1] / 8 + ","
					}
				} else {
					pointStr += points[i] / 8 + ",";
				}
			}
			html += ' points="' + pointStr + '" ';
		} else if (attr.name === "x") {
			html += ' x="' + attr.value / 8 + '" ';
		} else if (attr.name === "y") {
			html += ' y="' + attr.value / 8 + '" ';
		} else {
			html += ' ' + attr.name + '="' + attr.value + '" ';
		}
	}

	html += '>';
	var childrens = child.children;
	for (var i = 0; i < childrens.length; i++) {
		html += App.svgChild(childrens[i]);
	}
	// var innnerHTML = "";
	// if (tagName === "text") {
	// innnerHTML = child.innerHTML;
	// }
	// html += innnerHTML + '</' + tagName + '>';
	html += '</' + tagName + '>';
	return html;
}
/**
 * 
 * @param {}
 *            obj
 * @return {}
 */
App.vmlChild = function(obj) {
	var html = "";
	var child = obj;
	// 标签名称
	var tagName = child.tagName;
	var isV = "";
	var textObj = true;
	if (tagName === "polyline") {

		// 获取属性
		var id = child.getAttribute("id") + "_min";

		var style = "position: absolute; cursor: pointer;";
		var points = child.getAttribute("points").value.split(",");

		var point = "";
		for (var m = 0; m < points.length; m++) {
			var po = points[m];
			po = po.slice(0, po.length - 2) / 8 + "pt";
			point += po + ",";
		}
		point = point.slice(0, point.length - 1);
		var strokecolor = child.getAttribute("strokecolor");
		var strokeweight = child.getAttribute("strokeweight") + "pt";
		html += "<v:polyline id='" + id + "' style='" + style + "' points='"
				+ point + "' strokecolor='" + strokecolor + "' strokeweight='"
				+ strokeweight + "'";
		isV = "v:";
	} else if (tagName === "stroke") {
		var startArrow = child.getAttribute("startArrow");
		var endArrow = child.getAttribute("endArrow");
		var dashstyle = child.getAttribute("dashstyle");
		html += "<v:stroke ";
		if (startArrow != "") {
			html += " startarrow='" + startArrow + "'";
		}
		if (endArrow != "") {
			html += " endarrow='" + endArrow + "'";
		}
		if (dashstyle != "") {
			html += " dashstyle='" + dashstyle + "'";
		}
		isV = "v:";
	} else if (tagName === "fill") {

		var opacity = child.getAttribute("opacity");
		html += "<v:fill ";
		if (opacity !== "") {
			html += " opacity='" + opacity + "'";
		}
		isV = "v:";
	} else if (tagName === "textbox") {
		textObj = false;
	} else {
		html += "<" + tagName;
		// 属性
		var attributes = child.attributes;
		for (var k = 0; k < attributes.length; k++) {

			var attr = attributes[k];
			// IE下判读该属性是否为自己设定属性
			if (attr.specified) {

				switch (attr.name) {
					case "id" :
						html += ' id="' + attr.value + '_min" ';
						break;
					case "style" :
						// 定位
						var pos = child.getAttribute("style").position;
						var txtAlign = child.getAttribute("style").textAlign;
						var fontFa = child.getAttribute("style").fontFamily;
						var fontSi = child.getAttribute("style").fontSize;
						var cursor = child.getAttribute("style").cursor;
						var top = child.getAttribute("style").top;
						var left = child.getAttribute("style").left;
						var width = child.getAttribute("style").width;
						var height = child.getAttribute("style").height;
						html += ' style="';
						if (pos != "") {
							html += 'position:' + pos + ';';
						}
						if (txtAlign != "") {
							html += 'text-align:' + txtAlign + ';';
						}
						if (fontFa != "") {
							html += 'font-family:' + fontFa + ';';
						}
						if (fontSi != "") {
							html += 'font-size:' + fontSi + ';';
						}
						if (cursor != "") {
							html += 'cursor:' + cursor + ';';
						}
						if (top != "") {
							top = top.slice(0, top.length - 2);
							html += 'top:' + top / 8 + 'px;';
						}
						if (left != "") {
							left = left.slice(0, left.length - 2);
							html += 'left:' + left / 8 + 'px;';
						}
						if (width != "") {
							width = width.slice(0, width.length - 2);
							html += 'width:' + width / 8 + 'px;';
						}
						if (height != "") {
							height = height.slice(0, height.length - 2);
							html += 'height:' + height / 8 + 'px;';
						}
						html += '"';
						break;
					case "src" :
						var path = attr.value;
						path = path.slice(path.indexOf("images"), path.length);
						html += ' src="' + path + '"';
						break;

				}

			}
		}

	}
	if (textObj) {
		html += '>';
	}

	var childrens = child.children;
	for (var i = 0; i < childrens.length; i++) {
		html += App.vmlChild(childrens[i]);
	}
	// var innnerHTML = "";
	// if (tagName === "textbox") {
	// innnerHTML = child.innerHTML;
	// }
	if (textObj) {
		html += '</' + isV + tagName + '>';
	}
	return html;
}

// 生成缩略图拖动层
App.initMiniatureDiv = function() {
	// 大图DIV
	var bigMap = $("#__gef_jbs_center__");
	var bigMapW = Gef.getInt(bigMap.css("width"));
	var bigMapH = Gef.getInt(bigMap.css("height"));

	// 缩略图窗体
	var obj = $("#__gef_jbs__platform");
	// 右下角打开/关闭图标
	var minClosedBtn = document.createElement('div');
	minClosedBtn.className = "min-btn min-btn-closed";
	minClosedBtn.onclick = function() {

		var width = Gef.getInt(obj.css("width"));

		var lastChild = obj.children("div").last();
		// 关闭状态宽度为13px,即将打开
		if (width < 150) {
			obj.css({
						width : bigMapW / 8 + "px",
						height : bigMapH / 8 + "px"
					});
			$("#__gef_jbs__minCon").css({
						width : bigMapW / 8 + "px",
						height : bigMapH / 8 + "px"

					});
			// 设置小图标样式
			lastChild.removeClass().addClass("min-btn");
		} else {// 打开
			obj.css({
						width : "13px",
						height : "13px"
					});
			$("#__gef_jbs__minCon").css({
						width : "13px",
						height : "13px"

					});
			lastChild.removeClass().addClass("min-btn min-btn-closed");

		}

	}
	// 缩略图移动方块
	var floaterObj = document.createElement('div');
	floaterObj.id = 'floater';
	floaterObj.className = 'min-map';
	obj.append(floaterObj);
	obj.append(minClosedBtn);
	document.onmousedown = App.graspMiniature;
	document.onmousemove = App.moveMiniature;
	document.onmouseup = App.dropMiniature;

}

// 抓取控件
App.graspMiniature = function(e) {

	var event = window.event || e;
	App.whichIt = Gef.isIE ? event.srcElement : event.target;
	if (App.whichIt.tagName != 'DIV' && App.whichIt.id != 'floater') {
		App.whichIt = null;
		return;
	}
	// 找到 id=floater的节点
	while (App.whichIt.id.indexOf('floater') == -1) {
		App.whichIt = App.whichIt.parentElement;
		if (App.whichIt == null) {
			return true;
		}
	}

	App.distanceX = event.clientX - App.whichIt.offsetLeft;
	App.distanceY = event.clientY - App.whichIt.offsetTop;
	return true;
}

// 移动控件
App.moveMiniature = function(e) {
	var event = window.event || e;
	var node = $("#LAYER_NODE_min");
	if (App.whichIt == null || node.children().length == 0) {
		return false;
	}

	var newX = (event.clientX + document.body.scrollLeft);
	var newY = (event.clientY + document.body.scrollTop);
	// 小方块left左边偏移量
	App.currentX = newX - App.distanceX;
	App.currentY = newY - App.distanceY;
	App.syncMiniature();
	// 同步大图
	App.syncMainMap();
	event.returnValue = false;
	return false;

}

// 释放控件
App.dropMiniature = function() {
	var event = window.event || e;
	var node = $("#LAYER_NODE_min");
	if (App.whichIt == null || node.children().length == 0) {
		return false;
	}

	// App.syncMainMap();
	App.whichIt = null;

}

// 同步大地图
App.syncMainMap = function() {

	// 大图移动
	var cenEl = $("#__gef_jbs__");
	var cenElW = Gef.getInt(cenEl.css("width"));
	var cenElH = Gef.getInt(cenEl.css("height"));
	// var cenElOldL = Gef.getInt(cenEl[0].offsetLeft);
	// var cenElOldT = Gef.getInt(cenEl[0].offsetTop);
	// 大图父元素
	var parentEl = cenEl.offsetParent();
	var parentElW = Gef.getInt(parentEl.css("width"));
	var parentElH = Gef.getInt(parentEl.css("height"));

	// 被大图遮罩的宽度高度
	// var hiddenW = cenElW - Math.abs(cenElOldL) - parentElW;
	// var hiddenH = cenElH - Math.abs(cenElOldT) - parentElH;

	// 1 计算大图偏移量 (整个大图偏移量/大图宽度)得出1px每个像素偏移量*遮罩的宽度
	// var cenElL = App.currentX * 8 / cenElW * hiddenW;
	// var cenElT = App.currentY * 8 / cenElH * hiddenH;
	// 2
	// var cenElL = parentElW / 2;
	// var cenElT = parentElH / 2;
	// cenElL = cenElL - eventX * 8;
	// cenElT = cenElT - eventY * 8;
	// cenEl.css({
	// "left" : cenElL + "px",
	// "top" : cenElT + "px"
	// });

	// 3
	var minConEl = $("#floater");
	var left = Gef.getInt(minConEl.css("left"));
	var top = Gef.getInt(minConEl.css("top"));
	// 小图父窗体
	var minParentEl = minConEl.parent();
	var w = Gef.getInt(minParentEl.css("width"));
	var h = Gef.getInt(minParentEl.css("height"));
	parentEl.get(0).scrollLeft = left * (parentElW / w);
	parentEl.get(0).scrollTop = top * (parentElH / h);
	// $("#__gef_jbs__platform").get(0).scrollLeft=5;
	// console.log($("#__gef_jbs__platform").get(0).scrollLeft);
}

/**
 * 同步缩略图
 */
App.syncMiniature = function(event) {

	// 小图移动
	var minConEl = $("#floater");
	var minConElW = Gef.getInt(minConEl.css("width"));
	var minConElH = Gef.getInt(minConEl.css("height"));
	// 小图父窗体
	var parentEl = minConEl.parent();
	var parentElW = Gef.getInt(parentEl.css("width"));
	var parentElH = Gef.getInt(parentEl.css("height"));
	var right = parentElW - 2 - minConElW;
	var bottom = parentElH - 2 - minConElH;
	//
	var eventX = App.currentX;
	var eventY = App.currentY;
	// 判断小图是否超出范围
	if (eventX < 0) {
		eventX = 0;
	}
	if (eventY < 0) {
		eventY = 0;
	}
	if (eventX > right) {
		eventX = right;
	}
	if (eventY > bottom) {
		eventY = bottom;
	}
	minConEl.css({
				"left" : eventX + "px",
				"top" : eventY + "px"
			});

}
