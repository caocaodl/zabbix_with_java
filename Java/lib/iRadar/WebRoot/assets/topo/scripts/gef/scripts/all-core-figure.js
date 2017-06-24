/*******************************************************************************
 * 所有继承Gef.figure.Figure的子类
 ******************************************************************************/
/**
 * 图形组
 */
Gef.ns("Gef.figure");
Gef.figure.GroupFigure = Gef.extend(Gef.figure.Figure, {
			renderVml : function() {
				var el = document.createElement("div");
				el.id = this.id;
				this.el = el;
				this.getParentEl().appendChild(el);
			},
			renderSvg : function() {
				var el = document.createElementNS(Gef.svgns, "g");
				el.setAttribute("id", this.id);
				this.el = el;
				this.getParentEl().appendChild(el);
			},
			onRenderVml : function() {
			},
			onRenderSvg : function() {
			}
		});
/**
 * 图形——直线
 */
Gef.ns("Gef.figure");
Gef.figure.LineFigure = Gef.extend(Gef.figure.Figure, {
			
			renderVml : function() {
				var dom = document.createElement("v:line");
				dom.from = this.x1 + "," + this.y1;
				dom.to = this.x2 + "," + this.y2;
				this.el = dom;
			},
			renderSvg : function() {
				var dom = document.createElementNS(Gef.svgns, "line");
				dom.setAttribute("x1", this.x1 + "px");
				dom.setAttribute("y1", this.y1 + "px");
				dom.setAttribute("x2", this.x2 + "px");
				dom.setAttribute("y2", this.y2 + "px");
				this.el = dom;
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Jpdl.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.el.setAttribute("strokeweight", 2);
				this.el.setAttribute("strokecolor", "blue");
				this.getParentEl().appendChild(this.el);
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Jpdl.id());
				this.el.setAttribute("fill", "white");
				this.el.setAttribute("stroke", "blue");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el);
			},
			update : function(x1, y1, x2, y2) {

				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
				if (Gef.isVml) {
					this.updateVml();
				} else {
					this.updateSvg();
				}
			},
			updateVml : function() {
				this.el.from = this.x1 + "," + this.y1;
				this.el.to = this.x2 + "," + this.y2;
			},
			updateSvg : function() {
				
				this.el.setAttribute("x1", this.x1 + "px");
				this.el.setAttribute("y1", this.y1 + "px");
				this.el.setAttribute("x2", this.x2 + "px");
				this.el.setAttribute("y2", this.y2 + "px");
			}
		});
/**
 * 图形——多边形边线(Polygon)
 */
Gef.ns("Gef.figure");
Gef.figure.PolylineFigure = Gef.extend(Gef.figure.Figure, {
			getPoint : function(x, y) {

				var str = "";
				for (var i = 0; i < this.points.length; i++) {
					var point = this.points[i];
					str += (point[0] + x) + "," + (point[1] + y) + " ";
				}
				
				return str;
			},
			renderVml : function() {
				var dom = document.createElement("v:polyline");
				dom.setAttribute("points", this.getPoint(0, 0));
				this.el = dom;
			},
			renderSvg : function() {
				var el = document.createElementNS(Gef.svgns, "polyline");
				el.setAttribute("points", this.getPoint(0, 0));
				this.el = el;
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.el.setAttribute("strokeweight", 2);
				this.el.setAttribute("strokecolor", "blue");
				Gef.model.root.appendChild(this.el);
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "none");
				this.el.setAttribute("stroke", "blue");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "pointer");
				Gef.model.root.appendChild(this.el);
			},
			onSelectVml : function() {
				this.el.setAttribute("strokeweight", "4");
				this.el.setAttribute("strokecolor", "green");
			},
			onSelectSvg : function() {
				this.el.setAttribute("stroke-width", "4");
				this.el.setAttribute("stroke", "green");
			},
			onDeselectVml : function() {
				this.el.setAttribute("strokeweight", "2");
				this.el.setAttribute("strokecolor", "blue");
			},
			onDeselectSvg : function() {
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("stroke", "blue");
			}
		});
/**
 * 图形——矩形
 */
Gef.ns("Gef.figure");
Gef.figure.RectFigure = Gef.extend(Gef.figure.Figure, {
			renderVml : function() {
				var el = document.createElement("v:rect");
				el.style.left = this.x + "px";
				el.style.top = this.y + "px";
				el.style.width = this.w + "px";
				el.style.height = this.h + "px";
				this.el = el;
			},
			renderSvg : function() {
				var el = document.createElementNS(Gef.svgns, "rect");
				el.setAttribute("x", this.x + "px");
				el.setAttribute("y", this.y + "px");
				el.setAttribute("width", this.w + "px");
				el.setAttribute("height", this.h + "px");
				this.el = el;
			},
			move : function(x, y) {
				this.moveTo(this.x + x, this.y + y);
			},
			moveTo : function(x, y) {
				this.x = x;
				this.y = y;
				if (Gef.isVml) {
					this.moveToVml();
				} else {
					this.moveToSvg();
				}
			},
			moveToVml : function() {
				this.el.style.left = this.x + "px";
				this.el.style.top = this.y + "px";
			},
			moveToSvg : function(x, y) {

				this.el.setAttribute("x", this.x);
				this.el.setAttribute("y", this.y);
			},
			/**
			 * 更新图元
			 */
			updatePor : function() {
				if (Gef.isVml) {
					this.updateVml();
				} else {
					this.updateSvg();
				}
			},
			update : function(x, y, w, h) {
				this.x = x;
				this.y = y;
				this.w = w;
				this.h = h;
				if (Gef.isVml) {
					this.updateVml();
				} else {
					this.updateSvg();
				}
			},
			updateVml : function() {
				this.moveToVml();
				this.el.style.width = this.w + "px";
				this.el.style.height = this.h + "px";
			},
			updateSvg : function() {
				this.moveToSvg();
				this.el.setAttribute("width", this.w);
				this.el.setAttribute("height", this.h)
			},
			resize : function(direction, widthObj, heightObj) {
				var x = this.x;
				var y = this.y;
				var w = this.w;
				var h = this.h;
				if (direction == "n") {
					y = y + heightObj;
					h = h - heightObj;
				} else if (direction == "s")
					h = h + heightObj;
				else if (direction == "w") {
					x = x + widthObj;
					w = w - widthObj;
				} else if (direction == "e")
					w = w + widthObj;
				else if (direction == "nw") {
					x = x + widthObj;
					w = w - widthObj;
					y = y + heightObj;
					h = h - heightObj;
				} else if (direction == "ne") {
					w = w + widthObj;
					y = y + heightObj;
					h = h - heightObj;
				} else if (direction == "sw") {
					x = x + widthObj;
					w = w - widthObj;
					h = h + heightObj;
				} else if (direction == "se") {
					w = w + widthObj;
					h = h + heightObj;
				}
				this.update(x, y, w, h);
				return {
					x : x,
					y : y,
					w : w,
					h : h
				}
			}
		});
/**
 * 页面绘制根节点图形
 */
Gef.ns("Gef.figure");
Gef.figure.RootFigure = Gef.extend(Gef.figure.Figure, {
			render : function() {
				this.getParentEl().onselectstart = function() {
					return false;
				};
				Gef.figure.RootFigure.superclass.render.call(this);
			},
			renderVml : function() {
				var el = document.createElement("div");
				el.setAttribute("id", Gef.id());
				this.getParentEl().appendChild(el);
				this.el = el;
			},
			renderSvg : function() {
				var parentEl = this.getParentEl();
				var svgEl = parentEl.ownerDocument.createElementNS(Gef.svgns,
						"svg");
				svgEl.setAttribute("id", Gef.id());
				svgEl.setAttribute("width", parentEl.style.width.replace(/px/,
								""));
				svgEl.setAttribute("height", parentEl.style.height.replace(
								/px/, ""));
				svgEl.style.fontFamily = "Verdana";
				svgEl.style.fontSize = "12px";
				parentEl.appendChild(svgEl);
				var defsEl = svgEl.ownerDocument.createElementNS(Gef.svgns,
						"defs");
				svgEl.appendChild(defsEl);
				// 创建末尾箭头
				var markerEndEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "marker");
				markerEndEl.setAttribute("id", "markerEndArrow");
				markerEndEl.setAttribute("markerUnits", "userSpaceOnUse");
				markerEndEl.setAttribute("markerWidth", 8);
				markerEndEl.setAttribute("markerHeight", 8);
				markerEndEl.setAttribute("refX", 8);
				markerEndEl.setAttribute("refY", 4);
				markerEndEl.setAttribute("orient", "auto");
				var endPathEl = svgEl.ownerDocument.createElementNS(Gef.svgns,
						"path");
				endPathEl.setAttribute("d", "M 0 0 L 8 4 L 0 8 z");
				endPathEl.setAttribute("stroke", "#909090");
				endPathEl.setAttribute("fill", "#909090");
				markerEndEl.appendChild(endPathEl);
				defsEl.appendChild(markerEndEl);
				// 创建末尾箭头(缩略图)
				var minMarkerEndEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "marker");
				minMarkerEndEl.setAttribute("id", "minMarkerEndArrow");
				minMarkerEndEl.setAttribute("markerUnits", "userSpaceOnUse");
				minMarkerEndEl.setAttribute("markerWidth", 8);
				minMarkerEndEl.setAttribute("markerHeight", 4);
				minMarkerEndEl.setAttribute("refX", 2);
				minMarkerEndEl.setAttribute("refY", 2);
				minMarkerEndEl.setAttribute("orient", "auto");
				var endPathEl = svgEl.ownerDocument.createElementNS(Gef.svgns,
						"path");
				endPathEl.setAttribute("d", "M 0 0 L 8 4 L 0 8 z");
				endPathEl.setAttribute("stroke", "#909090");
				endPathEl.setAttribute("fill", "#909090");
				minMarkerEndEl.appendChild(endPathEl);
				defsEl.appendChild(minMarkerEndEl);
				// 创建开始箭头
				var markerStartEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "marker");
				markerStartEl.setAttribute("id", "markerStartArrow");
				markerStartEl.setAttribute("markerUnits", "userSpaceOnUse");
				markerStartEl.setAttribute("markerWidth", 8);
				markerStartEl.setAttribute("markerHeight", 8);
				markerStartEl.setAttribute("refX", 8);
				markerStartEl.setAttribute("refY", 4);
				markerStartEl.setAttribute("orient", "auto");
				var startPathEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "path");
				startPathEl.setAttribute("d", "M 0 6 L 10 10 L 8 0 z");
				startPathEl.setAttribute("stroke", "#909090");
				startPathEl.setAttribute("fill", "#909090");
				markerStartEl.appendChild(startPathEl);
				defsEl.appendChild(markerStartEl);
				// 创建开始箭头(缩略图)
				var minMarkerStartEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "marker");
				minMarkerStartEl.setAttribute("id", "minMarkerStartArrow");
				minMarkerStartEl.setAttribute("markerUnits", "userSpaceOnUse");
				minMarkerStartEl.setAttribute("markerWidth", 8);
				minMarkerStartEl.setAttribute("markerHeight", 4);
				minMarkerStartEl.setAttribute("refX", 4);
				minMarkerStartEl.setAttribute("refY", 2);
				minMarkerStartEl.setAttribute("orient", "auto");
				var startPathEl = svgEl.ownerDocument.createElementNS(
						Gef.svgns, "path");
				startPathEl.setAttribute("d", "M 0 6 L 10 10 L 8 0 z");
				startPathEl.setAttribute("stroke", "#909090");
				startPathEl.setAttribute("fill", "#909090");
				minMarkerStartEl.appendChild(startPathEl);
				defsEl.appendChild(minMarkerStartEl);
				// 创建空心菱形◇
				// var markerDiamondEl = svgEl.ownerDocument.createElementNS(
				// Gef.svgns, "marker");
				// markerDiamondEl.setAttribute("id", "markerDiamond");
				// markerDiamondEl.setAttribute("markerUnits",
				// "userSpaceOnUse");
				// markerDiamondEl.setAttribute("markerWidth", 16);
				// markerDiamondEl.setAttribute("markerHeight", 8);
				// markerDiamondEl.setAttribute("refX", 8);
				// markerDiamondEl.setAttribute("refY", 4);
				// markerDiamondEl.setAttribute("orient", "auto");
				// var pathDom = svgEl.ownerDocument.createElementNS(Gef.svgns,
				// "path");
				// pathDom.setAttribute("d", "M 0 4 L 8 8 L 16 4 L 8 0 z");
				// pathDom.setAttribute("stroke", "#909090");
				// pathDom.setAttribute("fill", "#FFFFFF");
				// markerDiamondEl.appendChild(pathDom);
				// defsEl.appendChild(markerDiamondEl);
				// 创建圆形
				// var markerCircleEl = svgEl.ownerDocument.createElementNS(
				// Gef.svgns, "marker");
				// markerCircleEl.setAttribute("id", "markerCircle");
				// markerCircleEl.setAttribute("markerUnits", "userSpaceOnUse");
				// markerCircleEl.setAttribute("markerWidth", 16);
				// markerCircleEl.setAttribute("markerHeight", 8);
				// markerCircleEl.setAttribute("refX", 8);
				// markerCircleEl.setAttribute("refY", 4);
				// markerCircleEl.setAttribute("orient", "auto");
				// var circleDom =
				// svgEl.ownerDocument.createElementNS(Gef.svgns,
				// "circle");
				// circleDom.setAttribute("r", "3");
				// circleDom.setAttribute("cx", "5");
				// circleDom.setAttribute("cy", "5");
				// circleDom.setAttribute("stroke", "#909090");
				// circleDom.setAttribute("fill", "#909090");
				// markerCircleEl.appendChild(circleDom);
				// defsEl.appendChild(markerCircleEl);

				this.el = svgEl;
			},
			onRenderVml : function() {
			},
			onRenderSvg : function() {
			}
		});
/**
 * 图形——空
 */
Gef.ns("Gef.figure");
Gef.figure.NoFigure = Gef.extend(Gef.figure.Figure, {
			render : Gef.emptyFn,
			update : Gef.emptyFn
		});
/**
 * 图形——拖动文本
 */
Gef.ns("Gef.figure");
Gef.figure.DraggingTextFigure = Gef.extend(Gef.figure.Figure, {
	constructor : function(edge) {
		Gef.figure.DraggingTextFigure.superclass.constructor.call(this);
		this.edge = edge;
	},
	getTextLocation : function() {
		var textEl = this.edge.getTextLocation();
		var x = textEl.x;
		var y = textEl.y;
		var w = textEl.w;
		var h = textEl.h;
		var cx = w / 2;
		var cy = h / 2;
		y -= h;
		return {
			x : x,
			y : y,
			w : w,
			h : h,
			cx : cx,
			cy : cy
		}
	},
	renderVml : function() {
		var textEl = this.getTextLocation();
		var x = textEl.x;
		var y = textEl.y;
		var w = textEl.w;
		var h = textEl.h;
		var cx = textEl.cx;
		var cy = textEl.cy;
		var groupEl = document.createElement("v:group");
		groupEl.style.left = x;
		groupEl.style.top = y;
		groupEl.style.width = w;
		groupEl.style.height = h;
		groupEl.setAttribute("coordsize", w + "," + h);
		this.el = groupEl;
		var rectEl = document.createElement("v:rect");
		rectEl.filled = "f";
		rectEl.strokecolor = "black";
		rectEl.style.left = "0px";
		rectEl.style.top = "0px";
		rectEl.style.width = w + "px";
		rectEl.style.height = h + "px";
		groupEl.appendChild(rectEl);
		this.rectEl = rectEl;
		this.nwEl = this.createItemVml(0, 0, "nw");
		this.nehl = this.createItemVml(w, 0, "ne");
		this.swEl = this.createItemVml(0, h, "sw");
		this.sehl = this.createItemVml(w, h, "se");
	},
	createItemVml : function(x, y, cursor) {
		var rectEl = document.createElement("v:rect");
		rectEl.id = this.edge.getId() + ":" + cursor;
		rectEl.fillcolor = "black";
		rectEl.style.cursor = cursor + "-resize";
		rectEl.style.left = (x - 2) + "px";
		rectEl.style.top = (y - 2) + "px";
		rectEl.style.width = "4px";
		rectEl.style.height = "4px";
		this.el.appendChild(rectEl);
		return rectEl;
	},
	renderSvg : function() {
		var A = this.getTextLocation(), G = A.x, F = A.y, $ = A.w, E = A.h, C = A.cx, B = A.cy, _ = document
				.createElementNS(Gef.svgns, "g");
		_.setAttribute("transform", "translate(" + G + "," + F + ")");
		this.el = _;
		var D = document.createElementNS(Gef.svgns, "rect");
		D.setAttribute("x", 0);
		D.setAttribute("y", 0);
		D.setAttribute("width", $);
		D.setAttribute("height", E);
		D.setAttribute("fill", "none");
		D.setAttribute("stroke", "black");
		this.rectEl = D;
		this.el.appendChild(D);
		this.nwEl = this.createItemSvg(0, 0, "nw");
		this.neEl = this.createItemSvg($, 0, "ne");
		this.swEl = this.createItemSvg(0, E, "sw");
		this.seEl = this.createItemSvg($, E, "se")
	},
	createItemSvg : function(x, y, cursor) {
		var rectEl = document.createElementNS(Gef.svgns, "rect");
		rectEl.setAttribute("id", this.edge.getId() + ":" + cursor);
		rectEl.setAttribute("cursor", cursor + "-resize");
		rectEl.setAttribute("x", x - 2);
		rectEl.setAttribute("y", y - 2);
		rectEl.setAttribute("width", "5");
		rectEl.setAttribute("height", "5");
		rectEl.setAttribute("fill", "black");
		rectEl.setAttribute("stroke", "white");
		this.el.appendChild(rectEl);
		return rectEl;
	},
	resize : function(x, y, w, h) {
		if (Gef.isVml) {
			this.resizeVml(x, y, w, h);
		} else {
			this.resizeSvg(x, y, w, h);
		}
	},
	resizeVml : function(xObj, yObj, wObj, hObj) {
		var textObj = this.getTextLocation();
		var x = textObj.x;
		var y = textObj.y;
		var w = textObj.w;
		var h = textObj.h;
		var cx = textObj.cx;
		var cy = textObj.cy;
		this.el.style.left = x + "px";
		this.el.style.top = y + "px";
		this.el.style.width = w + "px";
		this.el.style.height = h + "px";
		this.el.coordsize = w + "," + h;
		this.rectEl.style.width = w + "px";
		this.rectEl.style.height = h + "px";
		this.neEl.style.left = (w - 2) + "px";
		this.swEl.style.top = (h - 2) + "px";
		this.seEl.style.left = (w - 2) + "px";
		this.seEl.style.top = (h - 2) + "px";
	},
	resizeSvg : function(xObj, yObj, wObj, hObj) {
		var textObj = this.getTextLocation();
		var x = textObj.x;
		var y = textObj.y;
		var w = textObj.w;
		var h = textObj.h;
		var cx = textObj.cx;
		var cy = textObj.cy;
		this.el.setAttribute("transform", "translate(" + x + "," + y + ")");
		this.rectEl.setAttribute("width", w);
		this.rectEl.setAttribute("height", h);
		this.neEl.setAttribute("x", w - 2);
		this.swEl.setAttribute("y", h - 2);
		this.seEl.setAttribute("x", w - 2);
		this.seEl.setAttribute("y", h - 2);
	},
	refresh : function() {
		this.resize(this.edge.x1, this.edge.y1, this.edge.x2, this.edge.y2);
		this.edge.refresh();
	}
});
/**
 * 图形——页面图元选中时边线 图元拖拽变化移动
 */
Gef.ns("Gef.figure");
Gef.figure.ResizeNodeHandle = Gef.extend(Gef.figure.Figure, {
			constructor : function(node) {
				this.children = [];
				this.node = node;
			},
			renderVml : function() {
				var node = this.node;
				var x = node.x;
				var y = node.y;
				var w = node.w;
				var h = node.h;
				var nc = w / 2;
				var wh = h / 2;
				var groupEl = document.createElement("v:group");
				groupEl.style.left = x;
				groupEl.style.top = y;
				groupEl.style.width = w;
				groupEl.style.height = h;
				groupEl.setAttribute("coordsize", w + "," + h);
				this.el = groupEl;
				var rectEl = document.createElement("v:rect");
				rectEl.filled = "f";
				rectEl.strokecolor = "black";
				rectEl.style.left = "0px";
				rectEl.style.top = "0px";
				rectEl.style.width = w + "px";
				rectEl.style.height = h + "px";
				groupEl.appendChild(rectEl);
				this.rectEl = rectEl;
				this.nEl = this.createItemVml(nc, 0, "n");
				this.sEl = this.createItemVml(nc, h, "s");
				this.wEl = this.createItemVml(0, wh, "w");
				this.eEl = this.createItemVml(w, wh, "e");
				this.nwEl = this.createItemVml(0, 0, "nw");
				this.neEl = this.createItemVml(w, 0, "ne");
				this.swEl = this.createItemVml(0, h, "sw");
				this.seEl = this.createItemVml(w, h, "se");
				Gef.each(node.getTools(), function(obj) {
							obj.render(groupEl, node);
						});
			},
			createItemVml : function(x, y, cursor) {
				var rectEl = document.createElement("v:rect");
				rectEl.id = this.node.getId() + ":" + cursor;
				rectEl.fillcolor = "black";
				rectEl.strokecolor = "white";
				rectEl.style.cursor = cursor + "-resize";
				rectEl.style.left = (x - 2) + "px";
				rectEl.style.top = (y - 2) + "px";
				rectEl.style.width = "5px";
				rectEl.style.height = "5px";
				this.el.appendChild(rectEl);
				return rectEl;
			},
			getDirectionByPoint : function(xy) {
				var array = [["nw", "n", "ne"], ["w", "", "e"],
						["sw", "s", "se"]];
				var w = this.w / 2;
				var h = this.h / 2;
				for (i = 0; i <= 2; i++)
					for (j = 0; j < 2; j++) {
						if (i == 1 && j == 1) {
							continue;
						}
						var D = this.x + w * i;
						var C = this.y + h * j;
						if (xy.x >= D - 2.5 && xy.x <= D + 2.5
								&& xy.y >= C - 2.5 && xy.y <= C + 2.5) {
							return array[i][j];
						}
					}
				return null;
			},
			renderSvg : function() {
				var node = this.node;
				var x = node.x;
				var y = node.y;
				var w = node.w;
				var h = node.h;
				var nw = w / 2;
				var wh = h / 2;
				var gEl = document.createElementNS(Gef.svgns, "g");
				gEl.setAttribute("transform", "translate(" + x + "," + y + ")");
				this.el = gEl;
				var rectEl = document.createElementNS(Gef.svgns, "rect");
				rectEl.setAttribute("x", 0);
				rectEl.setAttribute("y", 0);
				rectEl.setAttribute("width", w);
				rectEl.setAttribute("height", h);
				rectEl.setAttribute("fill", "none");
				rectEl.setAttribute("stroke", "black");
				this.rectEl = rectEl;
				this.el.appendChild(rectEl);
				this.nEl = this.createItemSvg(nw, 0, "n");
				this.sEl = this.createItemSvg(nw, h, "s");
				this.wEl = this.createItemSvg(0, wh, "w");
				this.eEl = this.createItemSvg(w, wh, "e");
				this.nwEl = this.createItemSvg(0, 0, "nw");
				this.neEl = this.createItemSvg(w, 0, "ne");
				this.swEl = this.createItemSvg(0, h, "sw");
				this.seEl = this.createItemSvg(w, h, "se");
				Gef.each(node.getTools(), function(obj) {
							obj.render(gEl, node);
						})
			},
			createItemSvg : function(x, y, cursor) {
				var rectEl = document.createElementNS(Gef.svgns, "rect");
				rectEl.setAttribute("id", this.node.getId() + ":" + cursor);
				rectEl.setAttribute("cursor", cursor + "-resize");
				rectEl.setAttribute("x", x - 2);
				rectEl.setAttribute("y", y - 2);
				rectEl.setAttribute("width", "5");
				rectEl.setAttribute("height", "5");
				rectEl.setAttribute("fill", "black");
				rectEl.setAttribute("stroke", "white");
				this.el.appendChild(rectEl);
				return rectEl;
			},
			resize : function(x, y, w, h) {
				if (Gef.isVml)
					this.resizeVml(x, y, w, h);
				else
					this.resizeSvg(x, y, w, h);
			},
			resizeVml : function(x, y, w, h) {
				this.el.style.left = x + "px";
				this.el.style.top = y + "px";
				this.el.style.width = w + "px";
				this.el.style.height = h + "px";
				this.el.coordsize = w + "," + h;
				this.rectEl.style.width = w + "px";
				this.rectEl.style.height = h + "px";
				this.nEl.style.left = (w / 2 - 2) + "px";
				this.sEl.style.left = (w / 2 - 2) + "px";
				this.sEl.style.top = (h - 2) + "px";
				this.wEl.style.top = (h / 2 - 2) + "px";
				this.eEl.style.left = (w - 2) + "px";
				this.eEl.style.top = (h / 2 - 2) + "px";
				this.neEl.style.left = (w - 2) + "px";
				this.swEl.style.top = (h - 2) + "px";
				this.seEl.style.left = (w - 2) + "px";
				this.seEl.style.top = (h - 2) + "px";
				Gef.each(this.node.getTools(), function(obj) {
							obj.resize(x, y, w, h);
						})
			},
			resizeSvg : function(x, y, w, h) {
				this.el.setAttribute("transform", "translate(" + x + "," + y
								+ ")");
				this.rectEl.setAttribute("width", w);
				this.rectEl.setAttribute("height", h);
				this.nEl.setAttribute("x", w / 2 - 2);
				this.sEl.setAttribute("x", w / 2 - 2);
				this.sEl.setAttribute("y", h - 2);
				this.wEl.setAttribute("y", h / 2 - 2);
				this.eEl.setAttribute("x", w - 2);
				this.eEl.setAttribute("y", h / 2 - 2);
				this.neEl.setAttribute("x", w - 2);
				this.swEl.setAttribute("y", h - 2);
				this.seEl.setAttribute("x", w - 2);
				this.seEl.setAttribute("y", h - 2);
				Gef.each(this.node.getTools(), function(obj) {
							obj.resize(x, y, w, h);
						});
			},
			refresh : function() {
				this.resize(this.node.x, this.node.y, this.node.w, this.node.h);
			}
		});
/**
 * 图形——选中连线时显示的边线
 */
Gef.ns("Gef.figure");
Gef.figure.ResizeEdgeHandle = Gef.extend(Gef.figure.Figure, {
	renderVml : function() {
		var F = this.edge.x1, A = this.edge.y1, D = this.edge.x2, B = this.edge.y2, C = this.edge.innerPoints, H = Math
				.max(F, D), E = Math.max(A, B), I = document
				.createElement("v:group");
		I.style.width = H + "px";
		I.style.height = E + "px";
		I.setAttribute("coordsize", H + "," + E);
		this.getParentEl().appendChild(I);
		this.el = I;
		var K = document.createElement("v:polyline");
		K.setAttribute("points", this.edge.getPoint(0, 0));
		K.filled = "false";
		K.strokeweight = "1";
		K.strokecolor = "black";
		K.style.position = "absolute";
		I.appendChild(K);
		this.lineEl = K;
		this.startEl = this.createItem(F, A, "start");
		this.endEl = this.createItem(D, B, "end");
		var G = 0, _ = [F, A], J = [];
		for (; G < C.length; G++) {
			var $ = C[G];
			J.push(this.createItem((_[0] + $[0]) / 2, (_[1] + $[1]) / 2,
					"middle:" + (G - 1) + "," + G));
			_ = $;
			J.push(this.createItem($[0], $[1], "middle:" + G + "," + G))
		}
		J.push(this.createItem((_[0] + D) / 2, (_[1] + B) / 2, "middle:"
						+ (G - 1) + "," + G));
		this.items = J
	},
	renderSvg : function() {
		var I = this.edge.x1, C = this.edge.y1, G = this.edge.x2, D = this.edge.y2, E = this.edge.innerPoints, $ = document
				.createElementNS(Gef.svgns, "g");
		this.getParentEl().appendChild($);
		this.el = $;
		var F = document.createElementNS(Gef.svgns, "polyline");		
		F.setAttribute("points", this.edge.getPoint(0, 0));
		F.setAttribute("fill", "none");
		F.setAttribute("stroke", "black");
		$.appendChild(F);
		this.lineEl = F;
		this.startEl = this.createItem(I, C, "start");
		this.endEl = this.createItem(G, D, "end");
		var H = 0, B = [I, C], A = [];
		for (; H < E.length; H++) {
			var _ = E[H];
			A.push(this.createItem((B[0] + _[0]) / 2, (B[1] + _[1]) / 2,
					"middle:" + (H - 1) + "," + H));
			B = _;
			A.push(this.createItem(_[0], _[1], "middle:" + H + "," + H))
		}
		A.push(this.createItem((B[0] + G) / 2, (B[1] + D) / 2, "middle:"
						+ (H - 1) + "," + H));
		this.items = A
	},
	createItem : function(A, _, $) {
		if (Gef.isVml)
			return this.createItemVml(A, _, $);
		else
			return this.createItemSvg(A, _, $)
	},
	createItemVml : function(B, A, _) {
		var $ = document.createElement("v:rect");
		$.id = this.edge.getId() + ":" + _;
		$.fillcolor = "black";
		$.strokecolor = "white";
		$.style.left = (B - 2) + "px";
		$.style.top = (A - 2) + "px";
		$.style.width = "5px";
		$.style.height = "5px";
		$.style.cursor = "move";
		this.el.appendChild($);
		return $
	},
	createItemSvg : function(B, A, _) {
		var $ = document.createElementNS(Gef.svgns, "rect");
		$.setAttribute("id", this.edge.getId() + ":" + _);
		$.setAttribute("x", B - 2);
		$.setAttribute("y", A - 2);
		$.setAttribute("width", 5);
		$.setAttribute("height", 5);
		$.setAttribute("fill", "black");
		$.setAttribute("stroke", "white");
		$.setAttribute("cursor", "move");
		this.el.appendChild($);
		return $
	},
	update : function() {
		if (Gef.isVml)
			this.updateVml();
		else
			this.updateSvg()
	},
	updateVml : function() {
		var G = this.edge.x1, _ = this.edge.y1, D = this.edge.x2, A = this.edge.y2;
		this.lineEl.points.value = this.edge.getPoint(0, 0);
		this.startEl.style.left = (G - 2) + "px";
		this.startEl.style.top = (_ - 2) + "px";
		this.endEl.style.left = (D - 2) + "px";
		this.endEl.style.top = (A - 2) + "px";
		var B = this.edge.innerPoints, F = 0, C = G, E = _;
		for (; F < B.length; F++) {
			var $ = B[F];
			this.items[F * 2].style.left = ((C + $[0]) / 2 - 2) + "px";
			this.items[F * 2].style.top = ((E + $[1]) / 2 - 2) + "px";
			C = $[0];
			E = $[1];
			this.items[F * 2 + 1].style.left = ($[0] - 2) + "px";
			this.items[F * 2 + 1].style.top = ($[1] - 2) + "px"
		}
		this.items[F * 2].style.left = ((C + D) / 2 - 2) + "px";
		this.items[F * 2].style.top = ((E + A) / 2 - 2) + "px"
	},
	updateSvg : function() {
		var G = this.edge.x1, _ = this.edge.y1, D = this.edge.x2, A = this.edge.y2;
		this.lineEl.setAttribute("points", this.edge.getPoint(0, 0));
		this.startEl.setAttribute("x", G - 2);
		this.startEl.setAttribute("y", _ - 2);
		this.endEl.setAttribute("x", D - 2);
		this.endEl.setAttribute("y", A - 2);
		var B = this.edge.innerPoints, F = 0, C = G, E = _;
		for (; F < B.length; F++) {
			var $ = B[F];
			this.items[F * 2].setAttribute("x", (C + $[0]) / 2 - 2);
			this.items[F * 2].setAttribute("y", (E + $[1]) / 2 - 2);
			C = $[0];
			E = $[1];
			this.items[F * 2 + 1].setAttribute("x", $[0] - 2);
			this.items[F * 2 + 1].setAttribute("y", $[1] - 2)
		}
		this.items[F * 2].setAttribute("x", (C + D) / 2 - 2);
		this.items[F * 2].setAttribute("y", (E + A) / 2 - 2)
	},
	modify : function() {
		var A = this.edge.innerPoints.length, $ = this.items.length;
		if (A * 2 + 1 > $) {
			this.items.push(this.createItem(0, 0, "middle:" + (A - 1) + ","
							+ (A - 1)));
			this.items.push(this
					.createItem(0, 0, "middle:" + (A - 1) + "," + A))
		} else if (A * 2 + 1 < $) {
			var _ = null;
			_ = this.items[$ - 1];
			this.el.removeChild(_);
			this.items.remove(_);
			_ = this.items[$ - 2];
			this.el.removeChild(_);
			this.items.remove(_)
		}
		this.edge.refresh();
		this.update()
	},
	refresh : function() {
		this.modify()
	}
});