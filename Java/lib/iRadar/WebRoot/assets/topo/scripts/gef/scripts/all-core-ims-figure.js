/**
 * 
 */
Gef.ns("Gef.figure");
Gef.figure.RoundRectFigure = Gef.extend(Gef.figure.RectFigure, {
			renderVml : function() {
				Gef.figure.RoundRectFigure.superclass.renderVml.call(this);
				this.el.arcsize = 0.2;
			},
			renderSvg : function() {
				Gef.figure.RoundRectFigure.superclass.renderSvg.call(this);
				this.el.setAttribute("rx", 10);
				this.el.setAttribute("ry", 10);
			}
		});
/**
 * 页面绘制image img画图形
 */
Gef.ns("Gef.figure");
Gef.figure.ImageFigure = Gef.extend(Gef.figure.RectFigure, {
			renderVml : function() {
				var el = document.createElement("img");
				el.style.left = this.x + "px";
				el.style.top = this.y + "px";
				el.setAttribute("src", this.url);
				el.setAttribute("width", this.w);
				el.setAttribute("height", this.h);
				var text = this.editPart.model.text;
				if (text) {
					el.setAttribute('title', text);
				}
				this.el = el;
			},
			renderSvg : function() {

				var el = document.createElementNS(Gef.svgns, "image");
				el.setAttribute("x", this.x + "px");
				el.setAttribute("y", this.y + "px");
				el.setAttribute("width", this.w + "px");
				el.setAttribute("height", this.h + "px");
				el.setAttributeNS(Gef.linkns, "xlink:href", this.url);
				var text = this.editPart.model.text;
				if (text) {
					el.setAttribute('title', text);
				}
				// 鼠标单击事件
				el.onclick = function() {
					return false;
				};
				this.el = el;
			},
			update : function(x, A, w, h) {
				this.moveTo(x, y);
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.getParentEl().appendChild(this.el);
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el);
			},
			changeImageUrl : function(imgUrl) {
				if (Gef.isVml) {
					this.changeImageUrlVml(imgUrl);
				} else {
					this.changeImageUrlSvg(imgUrl);
				}
			},
			changeImageUrlVml : function(imgSrc) {
				this.el.setAttribute("src", imgSrc);
			},
			changeImageUrlSvg : function(href) {
				this.el.setAttributeNS(Gef.linkns, "xlink:href", href);
			}
		});

/**
 * 创建选中效果
 */
Gef.ns("Gef.figure");
Gef.figure.NodeFigure = Gef.extend(Gef.figure.RoundRectFigure, {
			constructor : function(obj) {
				this.outputs = [];
				this.incomes = [];
				Gef.figure.NodeFigure.superclass.constructor.call(this, obj);
				this.w = 90;
				this.h = 50;
			},
			renderVml : function() {
				var el = document.createElement("v:group");
				el.style.left = this.x;
				el.style.top = this.y;
				el.style.width = this.w;
				el.style.height = this.h;
				el.setAttribute("coordsize", this.w + "," + this.h);
				this.el = el;
				var roundrectEl = document.createElement("v:roundrect");
				roundrectEl.style.position = "absolute";
				roundrectEl.style.left = "5px";
				roundrectEl.style.top = "5px";
				roundrectEl.style.width = (this.w - 10) + "px";
				roundrectEl.style.height = (this.h - 10) + "px";
				roundrectEl.setAttribute("id", Gef.id());
				roundrectEl.setAttribute("arcsize", 0.2);
				roundrectEl.setAttribute("fillcolor", "#F6F7FF");
				roundrectEl.setAttribute("strokecolor", "#03689A");
				roundrectEl.setAttribute("strokeweight", "2");
				roundrectEl.style.verticalAlign = "middle";
				el.appendChild(roundrectEl);
				this.rectEl = roundrectEl;
				var _ = this.getTextPosition(this.w, this.h);
				var textEl = document.createElement("v:textbox");
				textEl.style.textAlign = "center";
				textEl.style.fontFamily = "Verdana";
				textEl.style.fontSize = "12px";
				textEl.setAttribute("id", Gef.id());
				textEl.innerHTML = this.name;
				roundrectEl.appendChild(textEl);
				this.textEl = textEl;
			},
			renderSvg : function() {
				var gEl = document.createElementNS(Gef.svgns, "g");
				gEl.setAttribute("transform", "translate(" + this.x + ","
								+ this.y + ")");
				this.el = gEl;
				var rectEl = document.createElementNS(Gef.svgns, "rect");
				rectEl.setAttribute("id", Gef.id());
				rectEl.setAttribute("x", 5);
				rectEl.setAttribute("y", 5);
				rectEl.setAttribute("width", (this.w - 10) + "px");
				rectEl.setAttribute("height", (this.h - 10) + "px");
				rectEl.setAttribute("rx", 10);
				rectEl.setAttribute("ry", 10);
				rectEl.setAttribute("fill", "#F6F7FF");
				rectEl.setAttribute("stroke", "#03689A");
				rectEl.setAttribute("stroke-width", "2");
				gEl.appendChild(rectEl);
				this.rectEl = rectEl;
				var xy = this.getTextPosition(this.w, this.h);
				var textEl = document.createElementNS(Gef.svgns, "text");
				textEl.setAttribute("id", Gef.id());
				textEl.setAttribute("x", xy.x);
				textEl.setAttribute("y", xy.y);
				textEl.setAttribute("text-anchor", "middle");
				textEl.textContent = this.name;
				gEl.appendChild(textEl);
				this.textEl = textEl;
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.getParentEl().appendChild(this.el);
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el);
			},
			getTextPosition : function(x, y) {
				if (Gef.isVml)
					return this.getTextPositionVml(x, y);
				else
					return this.getTextPositionSvg(x, y);
			},
			getTextPositionVml : function(x, y) {
				var _ = Gef.getTextSize(this.name);
				var xObj = x / 2;
				var yObj = y / 2;
				return {
					x : xObj + "px",
					y : yObj + "px"
				}
			},
			getTextPositionSvg : function(x, y) {
				var _ = Gef.getTextSize(this.name);
				var xObj = x / 2;
				var yObj = y / 2 + _.h / 4;
				return {
					x : xObj + "px",
					y : yObj + "px"
				}
			},
			moveTo : function(x, y) {

				Gef.figure.NodeFigure.superclass.moveTo.call(this, x, y);
				for (var i = 0; i < this.incomes.length; i++) {
					var obj = this.incomes[i];
					obj.refresh();
				}
				for (i = 0; i < this.outputs.length; i++) {
					var obj = this.outputs[i];
					obj.refresh();
				}
			},
			moveToVml : function() {
				this.el.style.left = this.x + "px";
				this.el.style.top = this.y + "px";
			},
			moveToSvg : function(x, y) {
				this.el.setAttribute("transform", "translate(" + this.x + ","
								+ this.y + ")");
			},
			update : function(x, y, w, h) {
				this.x = x;
				this.y = y;
				this.w = w;
				this.h = h;
				if (Gef.isVml) {
					this.resizeVml(x, y, w, h);
				} else {
					this.resizeSvg(x, y, w, h);
				}
			},
			remove : function() {
				for (var i = this.outputs.length - 1; i >= 0; i--) {
					var obj = this.outputs[i];
					obj.remove();
				}
				for (var i = this.incomes.length - 1; i >= 0; i--) {
					obj = this.incomes[i];
					obj.remove();
				}
				Gef.figure.NodeFigure.superclass.remove.call(this);
			},
			hideText : function() {
				this.textEl.style.display = "none";
			},
			updateAndShowText : function(textName) {
				this.name = textName;
				if (Gef.isVml) {
					this.textEl.innerHTML = textName;
				} else {
					this.textEl.textContent = textName;
				}
				this.textEl.style.display = "";
			},
			cancelEditText : function() {
				this.textEl.style.display = "";
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
				if (Gef.isVml) {
					this.resizeVml(x, y, w, h);
				} else {
					this.resizeSvg(x, y, w, h);
				}
				return {
					x : x,
					y : y,
					w : w,
					h : h
				}
			},
			resizeVml : function(x, y, w, h) {
				this.el.style.left = x + "px";
				this.el.style.top = y + "px";
				this.el.style.width = w + "px";
				this.el.style.height = h + "px";
				this.el.coordsize = w + "," + h;
				this.rectEl.style.width = (w - 10) + "px";
				this.rectEl.style.height = (h - 10) + "px";
			},
			resizeSvg : function(x, y, w, h) {
				this.el.setAttribute("transform", "translate(" + x + "," + y
								+ ")");
				this.rectEl.setAttribute("width", (w - 10) + "px");
				this.rectEl.setAttribute("height", (h - 10) + "px");
				var xy = this.getTextPosition(w, h);
				this.textEl.setAttribute("x", xy.x);
				this.textEl.setAttribute("y", xy.y);
			},
			getTools : function() {
				return [];
			}
		});
/**
 * 图形组件
 */
Gef.ns("Gef.figure");
Gef.figure.ImageNodeFigure = Gef.extend(Gef.figure.ImageFigure, {
			constructor : function(obj) {
				this.w = 48;
				this.h = 48;
				this.outputs = [];
				this.incomes = [];
				Gef.figure.ImageNodeFigure.superclass.constructor.call(this,
						obj);
			},
			move : function(x, y) {
				Gef.figure.ImageNodeFigure.superclass.move.call(this, x, y);
				for (var i = 0; i < this.incomes.length; i++) {
					var obj = this.incomes[i];
					obj.refresh();
				}
				for (i = 0; i < this.outputs.length; i++) {
					obj = this.outputs[i];
					obj.refresh();
				}
			},
			remove : function() {
				for (var i = this.outputs.length - 1; i >= 0; i--) {
					var obj = this.outputs[i];
					obj.remove();
				}
				for (i = this.incomes.length - 1; i >= 0; i--) {
					obj = this.incomes[i];
					obj.remove();
				}
				Gef.figure.ImageNodeFigure.superclass.remove.call(this);
			},
			getTools : function() {
				return [];
			}
		});
/**
 * 创建连线
 */
Gef.ns("Gef.figure");
Gef.figure.EdgeFigure = Gef.extend(Gef.figure.PolylineFigure, {
			constructor : function(from, to) {

				this.from = from;
				this.to = to;
				if (!this.name) {
					this.name = "to " + to.name;
				}
				this.from.outputs.push(this);
				this.to.incomes.push(this);
				this.alive = true;
				this.innerPoints = [];
				this.calculate();
				Gef.figure.EdgeFigure.superclass.constructor.call(this, {});
				this.textX = 0;
				this.textY = 0;
				this.conditional = false;

			},
			render : function() {

				this.calculate();
				Gef.figure.EdgeFigure.superclass.render.call(this);
				// this.setConditional(this.conditional);
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.el.setAttribute("strokeweight", 1);
					//线条颜色
				var color=this.editPart.model.dom.attributes["color"];
				this.el.setAttribute("strokecolor", "red");
				this.getParentEl().appendChild(this.el);
				this.stroke = document.createElement("v:stroke");
				this.el.appendChild(this.stroke);
				this.stroke.setAttribute("endArrow", "Classic");
				if (this.lineType) {
					switch (this.lineType) {
						case "dashedArrows" :// 虚线单箭头
							this.stroke.setAttribute("dashstyle", "dot");
							break;
						case "line" :// 直线
							this.stroke.setAttribute("endArrow", "none");
							break;
						case "dashedLine" :// 虚线
							this.stroke.setAttribute("dashstyle", "dot");
							this.stroke.setAttribute("endArrow", "none");
							break;
						case "doubleArrowsLine" :// 实线双单箭头
							this.stroke.setAttribute("startArrow", "Classic");
							break;
						case "doubleArrowsDashed" :// 虚线双箭头
							this.el.setAttribute("startArrow", "Classic");
							this.stroke.setAttribute("dashstyle", "dot");
							break;
					}
				}
				this.fill = document.createElement("v:fill");
				this.el.appendChild(this.fill);
				this.fill.setAttribute("opacity", 0);
				var textEl = document.createElement("textbox");
				textEl.setAttribute("id", Gef.id());
				var $ = this.getTextLocation();
				textEl.style.position = "absolute";
				textEl.style.left = $.x + "px";
				textEl.style.top = ($.y - $.h) + "px";
				textEl.style.textAlign = "center";
				textEl.style.cursor = "pointer";
				textEl.style.fontFamily = "Verdana";
				textEl.style.fontSize = "12px";
				textEl.innerHTML = this.name ? this.name : "";
				textEl.setAttribute("edgeId", this.getId());
				this.getParentEl().appendChild(textEl);
				this.textEl = textEl;
			},
			onRenderSvg : function() {
			
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "none");
				//线条颜色
				var color=this.editPart.model.dom.attributes["color"];
				this.el.setAttribute("stroke", "red");
				this.el.setAttribute("stroke-width", "1");
				this.el.setAttribute("cursor", "pointer");
				this.el.setAttribute("marker-end", "url(#markerEndArrow)");
				if (this.lineType) {
					switch (this.lineType) {
						case "dashedArrows" :// 虚线单箭头 6代表虚线长度px 5代表虚线之间间距
							this.el.setAttribute("stroke-dasharray", "6,5");
							break;
						case "line" :// 直线
							this.el.setAttribute("marker-end", "none");
							break;
						case "dashedLine" :// 虚线
							this.el.setAttribute("stroke-dasharray", "6,5");
							this.el.setAttribute("marker-end", "none");
							break;
						case "doubleArrowsLine" :// 实线双单箭头
							this.el.setAttribute("marker-start",
									"url(#markerStartArrow)");
							break;
						case "doubleArrowsDashed" :// 虚线双箭头
							this.el.setAttribute("marker-start",
									"url(#markerStartArrow)");
							this.el.setAttribute("stroke-dasharray", "6,5");
							break;
					}
				}

				this.getParentEl().appendChild(this.el);
				var textEl = document.createElementNS(Gef.svgns, "text");
				textEl.setAttribute("id", Gef.id());
				var xy = this.getTextLocation();
				textEl.setAttribute("x", xy.x);
				textEl.setAttribute("y", xy.y - 4);
				textEl.setAttribute("cursor", "pointer");
				textEl.textContent = this.name ? this.name : "";
				textEl.setAttribute("edgeId", this.getId());
				this.getParentEl().appendChild(textEl);
				this.textEl = textEl;
			},
			setConditional : function(bool) {
				this.conditional = bool;
				if (Gef.isVml) {
					this.setConditionalVml();
				} else {
					this.setConditionalSvg();
				}
			},
			setConditionalVml : function() {
				if (this.conditional === true) {
					this.stroke.setAttribute("startArrow", "diamond");
				} else {
					this.stroke.setAttribute("startArrow", "none");
				}
			},
			setConditionalSvg : function() {
				if (this.conditional === true) {
					this.el.setAttribute("marker-start", "url(#markerDiamond)");
				} else {
					this.el.setAttribute("marker-start", "");
				}
			},
			calculate : function() {
				var line = new Geom.Line(this.from.x + this.from.w / 2,
						this.from.y + this.from.h / 2, this.to.x + this.to.w
								/ 2, this.to.y + this.to.h / 2);
				var rect = new Geom.Rect(this.from.x, this.from.y, this.from.w,
						this.from.h);
				var toRect = new Geom.Rect(this.to.x, this.to.y, this.to.w,
						this.to.h);
				var rectXy = rect.getCrossPoint(line);
				var toRectXy = toRect.getCrossPoint(line);
				if (rectXy == null || $ == null) {
					this.x1 = 0;
					this.y1 = 0;
					this.x2 = 0;
					this.y2 = 0
				} else {
					this.x1 = rectXy.x;
					this.y1 = rectXy.y;
					this.x2 = toRectXy.x;
					this.y2 = toRectXy.y;
				}
				this.convert();
			},
			recalculate : function(xyObj, obj) {
				var line = new Geom.Line(xyObj.x + xyObj.w / 2, xyObj.y
								+ xyObj.h / 2, obj[0], obj[1]);
				var cross = new Geom.Rect(xyObj.x, xyObj.y, xyObj.w, xyObj.h);
				var xy = cross.getCrossPoint(line);
				return xy;
			},
			convert : function() {
				this.points = [];
				var array = this.points;
				var num = this.innerPoints.length;
				if (num > 0) {
					var xy = this.recalculate(this.from, this.innerPoints[0]);
					if (xy) {
						this.x1 = xy.x;
						this.y1 = xy.y;
					}
				}
				array.push([this.x1, this.y1 + 5]);
				for (var i = 0; i < this.innerPoints.length; i++) {
					array
							.push([this.innerPoints[i][0],
									this.innerPoints[i][1]]);
				}
				if (num > 0) {
					var xy = this.recalculate(this.to,
							this.innerPoints[num - 1]);
					if (xy) {
						this.x2 = xy.x;
						this.y2 = xy.y;
					}
				}
				array.push([this.x2, this.y2]);
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
				this.el.points.value = this.getPoint(0, 0);
				var xy = this.getTextLocation();
				this.textEl.style.left = xy.x + "px";
				this.textEl.style.top = (xy.y - xy.h) + "px";
			},
			updateSvg : function() {
				this.el.setAttribute("points", this.getPoint(0, 0));
                
				var xy = this.getTextLocation();
				this.textEl.setAttribute("x", xy.x);
				this.textEl.setAttribute("y", xy.y - 4);
			},

			refresh : function() {
				
				if (!this.el) {
					this.render();
				}
				this.calculate();
				this.update(this.x1, this.y1, this.x2, this.y2);
			},
			/**
			 * 修改连线文字坐标
			 * 
			 * @return {}
			 */
			getTextLocation : function() {
				
				var textEl = Gef.getTextSize(this.name);
				var w = textEl.w + 2;
				var h = textEl.h + 2;
				var x = (this.x1 + this.x2) / 2 + this.textX - 1;
				var y = (this.y1 + this.y2) / 2 + this.textY + 2;
                var source=this.from;
				var target=this.to;
				if(source.outputs.length>1){
				   //证明源节点有多条对外连线
					x=this.x2+40;
					y=this.y2+20;
				}else if(target.outputs.length>1){
					x=this.x1+40;
					y=this.y1+20;
				}
				
				return {
					x : x,
					y : y,
					w : w,
					h : h
				}
			},
			updateAndShowText : function(textName) {
				this.name = textName;
				if (Gef.isVml) {
					this.textEl.innerHTML = textName ? textName : "";
					var $ = this.getTextLocation();
					this.textEl.style.left = $.x;
					this.textEl.style.top = $.y;
				} else
					this.textEl.textContent = textName ? textName : "";
				this.textEl.style.display = "";
			},
			remove : function() {
				if (this.alive) {
					this.from.outputs.remove(this);
					this.to.incomes.remove(this);
					this.getParentEl().removeChild(this.textEl);
					Gef.figure.EdgeFigure.superclass.remove.call(this);
					this.alive = false;
				}
			},
			modify : function() {
				this.convert();
				if (Gef.isVml)
					this.el.points.value = this.getPoint(0, 0);
				else
					this.el.setAttribute("points", this.getPoint(0, 0));
				this.refresh();
			},
			/**
			 * 设置连线粗细
			 */
			setLineBold : function(name, val) {
				this.el.setAttribute(name, val);
			},
			setLineStrokeVml : function(name, val) {
				this.stroke.setAttribute(name, val);
			},
			removeLineStorkeVml : function(name) {
				this.stroke.removeAttribute(name);
			},
			removeLineSvg : function(name) {
				this.el.removeAttribute(name);
			}

		});
/**
 * 
 */
Gef.ns("Gef.figure");
Gef.figure.DraggingRectFigure = Gef.extend(Gef.figure.RectFigure, {
			constructor : function(obj) {
				Gef.figure.DraggingRectFigure.superclass.constructor.call(this,
						obj);
				this._className = "Gef.DraggingRectFigure";
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "normal";
				this.getParentEl().appendChild(this.el);
				this.stroke = document.createElement("v:stroke");
				this.el.appendChild(this.stroke);
				this.stroke.setAttribute("strokecolor", "black");
				this.stroke.setAttribute("dashstyle", "dot");
				this.fill = document.createElement("v:fill");
				this.el.appendChild(this.fill);
				this.fill.setAttribute("color", "#F6F6F6");
				this.fill.setAttribute("opacity", "0.5");
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "#F6F6F6");
				this.el.setAttribute("opacity", "0.7");
				this.el.setAttribute("stroke", "black");
				this.el.setAttribute("stroke-width", "1");
				this.el.setAttribute("cursor", "normal");
				this.el.setAttribute("stroke-dasharray", "2");
				this.getParentEl().appendChild(this.el);
			},
			update : function(x, y, w, h) {
				var xObj = this.x;
				var yObj = this.y;
				var obj = {
					x : x,
					y : y,
					w : w,
					h : h
				};
				if (w < 0) {
					this.oldX = this.x;
					obj.x = x + w;
					obj.w = -w;
				}
				if (h < 0) {
					obj.y = y + h;
					obj.h = -h;
				}
				Gef.figure.DraggingRectFigure.superclass.update.call(this,
						obj.x, obj.y, obj.w, obj.h);
				if (w < 0)
					this.x = xObj;
				if (h < 0)
					this.y = yObj;
			}
		});
/**
 * 
 */
Gef.ns("Gef.figure");
Gef.figure.DraggingEdgeFigure = Gef.extend(Gef.figure.EdgeFigure, {
			constructor : function(obj) {
				Gef.figure.DraggingEdgeFigure.superclass.constructor.call(this,
						{
							outputs : []
						}, {
							incomes : []
						});
				this._className = "Gef.DraggingEdgeFigure";
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "normal";
				this.getParentEl().appendChild(this.el);
				this.stroke = document.createElement("v:stroke");
				this.el.appendChild(this.stroke);
				this.stroke.color = "#909090";
				this.stroke.dashstyle = "dot";
				this.stroke.endArrow = "Classic";
				this.stroke.weight = 2;
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "none");
				this.el.setAttribute("stroke", "#909090");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "normal");
				this.el.setAttribute("stroke-dasharray", "2");
				this.el.setAttribute("marker-end", "url(#markerEndArrow)");
				this.getParentEl().appendChild(this.el);
			},
			updateForDragging : function(from, to) {
				this.from = from;
				this.x1 = this.from.x;
				this.y1 = this.from.y;
				this.to = {
					x : to.x,
					y : to.y,
					w : 2,
					h : 2
				};
				this.x2 = this.to.x;
				this.y2 = this.to.y;
				this.innerPoints = [];
				this.refresh();
			},
			updateForMove : function(form, node, xyObj) {
				if (node == "start") {
					this.from = {
						x : xyObj.x,
						y : xyObj.y,
						w : 2,
						h : 2
					};
					this.x1 = xyObj.x;
					this.y1 = xyObj.y;
					this.to = form.to;
					this.x2 = form.x2;
					this.y2 = form.y2
				} else {
					this.from = form.from;
					this.x1 = form.x1;
					this.y1 = form.y1;
					this.to = {
						x : xyObj.x,
						y : xyObj.y,
						w : 2,
						h : 2
					};
					this.x2 = xyObj.x;
					this.y2 = xyObj.y;
				}
				this.innerPoints = form.innerPoints;
				this.refresh();
			},
			moveToHide : function() {
				this.from = null;
				this.to = null;
				this.innerPoints = null;
				this.points = [[-1, -1], [-1, -1]];
				this.update(-1, -1, -1, -1);
			},
			updateVml : function() {
				this.el.points.value = this.getPoint(0, 0);
			},
			updateSvg : function() {
				this.el.setAttribute("points", this.getPoint(0, 0));
			}
		});

/**
 * 
 */
Gef.ns("Gef.figure");
Gef.figure.TextEditor = function(A, _) {
	var $ = document.createElement("input");
	$.setAttribute("type", "text");
	$.value = "";
	$.style.position = "absolute";
	$.style.left = "0px";
	$.style.top = "0px";
	$.style.width = "0px";
	$.style.border = "gray dotted 1px";
	$.style.background = "white";
	$.style.display = "none";
	$.style.zIndex = 1000;
	$.style.fontFamily = "Verdana";
	$.style.fontSize = "12px";
	document.body.appendChild($);
	this.el = $;
	this.baseX = A;
	this.baseY = _
};
Gef.figure.TextEditor.prototype = {
	showForNode : function($) {
		this.el.style.left = (this.baseX + $.x + 5) + "px";
		this.el.style.top = (this.baseY + $.y + $.h / 2 - 10) + "px";
		this.el.style.width = ($.w - 10) + "px";
		this.el.value = $.name;
		this.el.style.display = "";
		this.el.focus()
	},
	showForEdge : function(_) {
		var A = _.getTextLocation(), D = A.x, C = A.y, $ = A.w, B = A.h;
		C -= B;
		this.el.style.left = this.baseX + D + "px";
		this.el.style.top = this.baseY + C + "px";
		this.el.style.width = $ + "px";
		this.el.value = _.name;
		this.el.style.display = "";
		this.el.focus()
	},
	getValue : function() {
		return this.el.value
	},
	hide : function() {
		this.el.style.display = "none"
	},
	show : function() {
		this.el.style.display = ""
	}
};