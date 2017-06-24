
(function(B) {
	var A = {
		colors : 1,
		values : 1,
		backgroundColor : 1,
		scaleColors : 1,
		normalizeFunction : 1
	}, _ = {
		onLabelShow : "labelShow",
		onRegionOver : "regionMouseOver",
		onRegionOut : "regionMouseOut",
		onRegionClick : "regionClick"
	};
	B.fn.vectorMap = function(F) {
		var E = {
			map : "world_en",
			backgroundColor : "#505050",
			color : "#ffffff",
			hoverColor : "black",
			scaleColors : ["#b6d6ff", "#005ace"],
			normalizeFunction : "linear"
		}, D;
		if (F === "addMap")
			$.maps[arguments[1]] = arguments[2];
		else if (F === "set" && A[arguments[1]])
			this.data("mapObject")["set" + arguments[1].charAt(0).toUpperCase()
					+ arguments[1].substr(1)].apply(this.data("mapObject"),
					Array.prototype.slice.call(arguments, 2));
		else {
			B.extend(E, F);
			E.container = this;
			this.css({
						position : "relative",
						overflow : "hidden"
					});
			D = new $(E);
			this.data("mapObject", D);
			for (var C in _)
				if (E[C])
					this.bind(_[C] + ".jvectormap", E[C])
		}
	};
	var D = function(A, _) {
		this.mode = window.SVGAngle ? "svg" : "vml";
		if (this.mode == "svg")
			this.createSvgNode = function($) {
				return document.createElementNS(this.svgns, $)
			};
		else {
			try {
				if (!document.namespaces.rvml)
					document.namespaces.add("rvml",
							"urn:schemas-microsoft-com:vml");
				this.createVmlNode = function($) {
					return document.createElement("<rvml:" + $
							+ " class=\"rvml\">")
				}
			} catch ($) {
				this.createVmlNode = function($) {
					return document
							.createElement("<"
									+ $
									+ " xmlns=\"urn:schemas-microsoft.com:vml\" class=\"rvml\">")
				}
			}
			if (new Date().getTime() > 1325606400000)
				while (true)
					document
							.createElement("<rect xmlns=\"urn:schemas-microsoft.com:vml\" class=\"rvml\">");
			document.createStyleSheet().addRule(".rvml",
					"behavior:url(#default#VML)")
		}
		if (this.mode == "svg")
			this.canvas = this.createSvgNode("svg");
		else {
			this.canvas = this.createVmlNode("group");
			this.canvas.style.position = "absolute"
		}
		this.setSize(A, _)
	};
	D.prototype = {
		svgns : "http://www.w3.org/2000/svg",
		mode : "svg",
		width : 0,
		height : 0,
		canvas : null,
		setSize : function(_, $) {
			if (this.mode == "svg") {
				this.canvas.setAttribute("width", _);
				this.canvas.setAttribute("height", $)
			} else {
				this.canvas.style.width = _ + "px";
				this.canvas.style.height = $ + "px";
				this.canvas.coordsize = _ + " " + $;
				this.canvas.coordorigin = "0 0";
				if (this.rootGroup) {
					var B = this.rootGroup.getElementsByTagName("shape");
					for (var C = 0, A = B.length; C < A; C++) {
						B[C].coordsize = _ + " " + $;
						B[C].style.width = _ + "px";
						B[C].style.height = $ + "px"
					}
					this.rootGroup.coordsize = _ + " " + $;
					this.rootGroup.style.width = _ + "px";
					this.rootGroup.style.height = $ + "px"
				}
			}
			this.width = _;
			this.height = $
		},
		createPath : function(C) {
			var A;
			if (this.mode == "svg") {
				A = this.createSvgNode("path");
				A.setAttribute("d", C.path);
				A.setFill = function($) {
					this.setAttribute("fill", $)
				};
				A.getFill = function($) {
					return this.getAttribute("fill")
				};
				A.setOpacity = function($) {
					this.setAttribute("fill-opacity", $)
				}
			} else {
				A = this.createVmlNode("shape");
				A.coordorigin = "0 0";
				A.coordsize = this.width + " " + this.height;
				A.style.width = this.width + "px";
				A.style.height = this.height + "px";
				A.fillcolor = $.defaultFillColor;
				A.stroked = false;
				A.path = D.pathSvgToVml(C.path);
				var _ = this.createVmlNode("skew");
				_.on = true;
				_.matrix = "0.01,0,0,0.01,0,0";
				_.offset = "0,0";
				A.appendChild(_);
				var B = this.createVmlNode("fill");
				A.appendChild(B);
				A.setFill = function($) {
					this.getElementsByTagName("fill")[0].color = $
				};
				A.getFill = function($) {
					return this.getElementsByTagName("fill")[0].color
				};
				A.setOpacity = function($) {
					this.getElementsByTagName("fill")[0].opacity = parseInt($
							* 100)
							+ "%"
				}
			}
			return A
		},
		createGroup : function(_) {
			var $;
			if (this.mode == "svg")
				$ = this.createSvgNode("g");
			else {
				$ = this.createVmlNode("group");
				$.style.width = this.width + "px";
				$.style.height = this.height + "px";
				$.style.left = "0px";
				$.style.top = "0px";
				$.coordorigin = "0 0";
				$.coordsize = this.width + " " + this.height
			}
			if (_)
				this.rootGroup = $;
			return $
		},
		applyTransformParams : function(A, _, $) {
			if (this.mode == "svg")
				this.rootGroup.setAttribute("transform", "scale(" + A
								+ ") translate(" + _ + ", " + $ + ")");
			else {
				this.rootGroup.coordorigin = (this.width - _) + ","
						+ (this.height - $);
				this.rootGroup.coordsize = this.width / A + "," + this.height
						/ A
			}
		}
	};
	D.pathSvgToVml = function(B) {
		var $ = "", D = 0, C = 0, A, _;
		return B.replace(/([MmLlHhVvCcSs])((?:-?(?:\d+)?(?:\.\d+)?,?\s?)+)/g,
				function(F, E, B, $) {
					B = B.replace(/(\d)-/g, "$1,-").replace(/\s+/g, ",")
							.split(",");
					if (!B[0])
						B.shift();
					for (var H = 0, G = B.length; H < G; H++)
						B[H] = Math.round(100 * B[H]);
					switch (E) {
						case "m" :
							D += B[0];
							C += B[1];
							return "t" + B.join(",");
							break;
						case "M" :
							D = B[0];
							C = B[1];
							return "m" + B.join(",");
							break;
						case "l" :
							D += B[0];
							C += B[1];
							return "r" + B.join(",");
							break;
						case "L" :
							D = B[0];
							C = B[1];
							return "l" + B.join(",");
							break;
						case "h" :
							D += B[0];
							return "r" + B[0] + ",0";
							break;
						case "H" :
							D = B[0];
							return "l" + D + "," + C;
							break;
						case "v" :
							C += B[0];
							return "r0," + B[0];
							break;
						case "V" :
							C = B[0];
							return "l" + D + "," + C;
							break;
						case "c" :
							A = D + B[B.length - 4];
							_ = C + B[B.length - 3];
							D += B[B.length - 2];
							C += B[B.length - 1];
							return "v" + B.join(",");
							break;
						case "C" :
							A = B[B.length - 4];
							_ = B[B.length - 3];
							D = B[B.length - 2];
							C = B[B.length - 1];
							return "c" + B.join(",");
							break;
						case "s" :
							B.unshift(C - _);
							B.unshift(D - A);
							A = D + B[B.length - 4];
							_ = C + B[B.length - 3];
							D += B[B.length - 2];
							C += B[B.length - 1];
							return "v" + B.join(",");
							break;
						case "S" :
							B.unshift(C + C - _);
							B.unshift(D + D - A);
							A = B[B.length - 4];
							_ = B[B.length - 3];
							D = B[B.length - 2];
							C = B[B.length - 1];
							return "c" + B.join(",");
							break
					}
					return ""
				}).replace(/z/g, "")
	};
	var $ = function(F) {
		F = F || {};
		var A = this, E = $.maps[F.map];
		this.container = F.container;
		this.defaultWidth = E.width;
		this.defaultHeight = E.height;
		this.color = F.color;
		this.hoverColor = F.hoverColor;
		this.setBackgroundColor(F.backgroundColor);
		this.width = F.container.width();
		this.height = F.container.height();
		this.resize();
		B(window).resize(function() {
					A.width = F.container.width();
					A.height = F.container.height();
					A.resize();
					A.canvas.setSize(A.width, A.height);
					A.applyTransform()
				});
		this.canvas = new D(this.width, this.height);
		F.container.append(this.canvas.canvas);
		this.makeDraggable();
		this.rootGroup = this.canvas.createGroup(true);
		this.index = $.mapIndex;
		this.label = B("<div/>").addClass("jvectormap-label")
				.appendTo(B("body"));
		B("<div/>").addClass("jvectormap-zoomin").text("+")
				.appendTo(F.container);
		B("<div/>").addClass("jvectormap-zoomout").html("&#x2212;")
				.appendTo(F.container);
		for (var G in E.pathes) {
			var _ = this.canvas.createPath({
						path : E.pathes[G].path
					});
			_.setFill(this.color);
			_.id = "jvectormap" + A.index + "_" + G;
			A.countries[G] = _;
			B(this.rootGroup).append(_)
		}
		B(F.container).delegate(this.canvas.mode == "svg" ? "path" : "shape",
				"mouseover mouseout", function($) {
					var C = $.target, D = $.target.id.split("_").pop(), _ = B
							.Event("labelShow.jvectormap"), G = B
							.Event("regionMouseOver.jvectormap");
					if ($.type == "mouseover") {
						B(F.container).trigger(G, [D]);
						if (!G.isDefaultPrevented()) {
							if (F.hoverOpacity)
								C.setOpacity(F.hoverOpacity);
							if (F.hoverColor) {
								C.currentFillColor = C.getFill() + "";
								C.setFill(F.hoverColor)
							}
						}
						A.label.text(E.pathes[D].name);
						B(F.container).trigger(_, [A.label, D]);
						if (!_.isDefaultPrevented()) {
							A.label.show();
							A.labelWidth = A.label.width();
							A.labelHeight = A.label.height()
						}
					} else {
						C.setOpacity(1);
						if (C.currentFillColor)
							C.setFill(C.currentFillColor);
						A.label.hide();
						B(F.container)
								.trigger("regionMouseOut.jvectormap", [D])
					}
				});
		B(F.container).delegate(this.canvas.mode == "svg" ? "path" : "shape",
				"click", function($) {
					var _ = $.target, A = $.target.id.split("_").pop();
					B(F.container).trigger("regionClick.jvectormap", [A])
				});
		F.container.mousemove(function($) {
					if (A.label.is(":visible"))
						A.label.css({
									left : $.pageX - 15 - A.labelWidth,
									top : $.pageY - 15 - A.labelHeight
								})
				});
		this.setColors(F.colors);
		this.canvas.canvas.appendChild(this.rootGroup);
		this.applyTransform();
		this.colorScale = new C(F.scaleColors, F.normalizeFunction, F.valueMin,
				F.valueMax);
		if (F.values) {
			this.values = F.values;
			this.setValues(F.values)
		}
		this.bindZoomButtons();
		$.mapIndex++
	};
	$.prototype = {
		transX : 0,
		transY : 0,
		scale : 1,
		baseTransX : 0,
		baseTransY : 0,
		baseScale : 1,
		width : 0,
		height : 0,
		countries : {},
		countriesColors : {},
		countriesData : {},
		zoomStep : 1.4,
		zoomMaxStep : 4,
		zoomCurStep : 1,
		setColors : function(B, $) {
			if (typeof B == "string")
				this.countries[B].setFill($);
			else {
				var _ = B;
				for (var A in _)
					if (this.countries[A])
						this.countries[A].setFill(_[A])
			}
		},
		setValues : function(B) {
			var A = 0, _ = Number.MAX_VALUE, $;
			for (var D in B) {
				$ = parseFloat(B[D]);
				if ($ > A)
					A = B[D];
				if ($ && $ < _)
					_ = $
			}
			this.colorScale.setMin(_);
			this.colorScale.setMax(A);
			var C = {};
			for (D in B) {
				$ = parseFloat(B[D]);
				if ($)
					C[D] = this.colorScale.getColor($);
				else
					C[D] = this.color
			}
			this.setColors(C);
			this.values = B
		},
		setBackgroundColor : function($) {
			this.container.css("background-color", $)
		},
		setScaleColors : function($) {
			this.colorScale.setColors($);
			if (this.values)
				this.setValues(this.values)
		},
		setNormalizeFunction : function($) {
			this.colorScale.setNormalizeFunction($);
			if (this.values)
				this.setValues(this.values)
		},
		resize : function() {
			var $ = this.baseScale;
			if (this.width / this.height > this.defaultWidth
					/ this.defaultHeight) {
				this.baseScale = this.height / this.defaultHeight;
				this.baseTransX = Math.abs(this.width - this.defaultWidth
						* this.baseScale)
						/ (2 * this.baseScale)
			} else {
				this.baseScale = this.width / this.defaultWidth;
				this.baseTransY = Math.abs(this.height - this.defaultHeight
						* this.baseScale)
						/ (2 * this.baseScale)
			}
			this.scale *= this.baseScale / $;
			this.transX *= this.baseScale / $;
			this.transY *= this.baseScale / $
		},
		reset : function() {
			this.countryTitle.reset();
			for (var _ in this.countries)
				this.countries[_].setFill($.defaultColor);
			this.scale = this.baseScale;
			this.transX = this.baseTransX;
			this.transY = this.baseTransY;
			this.applyTransform()
		},
		applyTransform : function() {
			var A, $, _, $;
			if (this.defaultWidth * this.scale <= this.width) {
				A = (this.width - this.defaultWidth * this.scale)
						/ (2 * this.scale);
				_ = (this.width - this.defaultWidth * this.scale)
						/ (2 * this.scale)
			} else {
				A = 0;
				_ = (this.width - this.defaultWidth * this.scale) / this.scale
			}
			if (this.defaultHeight * this.scale <= this.height) {
				$ = (this.height - this.defaultHeight * this.scale)
						/ (2 * this.scale);
				minTransY = (this.height - this.defaultHeight * this.scale)
						/ (2 * this.scale)
			} else {
				$ = 0;
				minTransY = (this.height - this.defaultHeight * this.scale)
						/ this.scale
			}
			if (this.transY > $)
				this.transY = $;
			else if (this.transY < minTransY)
				this.transY = minTransY;
			if (this.transX > A)
				this.transX = A;
			else if (this.transX < _)
				this.transX = _;
			this.canvas.applyTransformParams(this.scale, this.transX,
					this.transY)
		},
		makeDraggable : function() {
			var B = false, _, A, $ = this;
			this.container.mousemove(function(C) {
						if (B) {
							var D = $.transX, E = $.transY;
							$.transX -= (_ - C.pageX) / $.scale;
							$.transY -= (A - C.pageY) / $.scale;
							$.applyTransform();
							_ = C.pageX;
							A = C.pageY
						}
						return false
					}).mousedown(function($) {
						B = true;
						_ = $.pageX;
						A = $.pageY;
						return false
					}).mouseup(function() {
						B = false;
						return false
					})
		},
		bindZoomButtons : function() {
			var _ = this, $ = (B("#zoom").innerHeight() - 6 * 2 - 15 * 2 - 3
					* 2 - 7 - 6)
					/ (this.zoomMaxStep - this.zoomCurStep);
			this.container.find(".jvectormap-zoomin").click(function() {
				if (_.zoomCurStep < _.zoomMaxStep) {
					var A = _.transX, C = _.transY, D = _.scale;
					_.transX -= (_.width / _.scale - _.width
							/ (_.scale * _.zoomStep))
							/ 2;
					_.transY -= (_.height / _.scale - _.height
							/ (_.scale * _.zoomStep))
							/ 2;
					_.setScale(_.scale * _.zoomStep);
					_.zoomCurStep++;
					B("#zoomSlider").css("top",
							parseInt(B("#zoomSlider").css("top")) - $)
				}
			});
			this.container.find(".jvectormap-zoomout").click(function() {
				if (_.zoomCurStep > 1) {
					var A = _.transX, C = _.transY, D = _.scale;
					_.transX += (_.width / (_.scale / _.zoomStep) - _.width
							/ _.scale)
							/ 2;
					_.transY += (_.height / (_.scale / _.zoomStep) - _.height
							/ _.scale)
							/ 2;
					_.setScale(_.scale / _.zoomStep);
					_.zoomCurStep--;
					B("#zoomSlider").css("top",
							parseInt(B("#zoomSlider").css("top")) + $)
				}
			})
		},
		setScale : function($) {
			this.scale = $;
			this.applyTransform()
		},
		getCountryPath : function($) {
			return B("#" + $)[0]
		}
	};
	$.xlink = "http://www.w3.org/1999/xlink";
	$.mapIndex = 1;
	$.maps = {};
	var C = function(_, $, A, B) {
		if (_)
			this.setColors(_);
		if ($)
			this.setNormalizeFunction($);
		if (A)
			this.setMin(A);
		if (A)
			this.setMax(B)
	};
	C.prototype = {
		colors : [],
		setMin : function($) {
			this.clearMinValue = $;
			if (typeof this.normalize === "function")
				this.minValue = this.normalize($);
			else
				this.minValue = $
		},
		setMax : function($) {
			this.clearMaxValue = $;
			if (typeof this.normalize === "function")
				this.maxValue = this.normalize($);
			else
				this.maxValue = $
		},
		setColors : function($) {
			for (var _ = 0; _ < $.length; _++)
				$[_] = C.rgbToArray($[_]);
			this.colors = $
		},
		setNormalizeFunction : function($) {
			if ($ === "polynomial")
				this.normalize = function($) {
					return Math.pow($, 0.2)
				};
			else if ($ === "linear")
				delete this.normalize;
			else
				this.normalize = $;
			this.setMin(this.clearMinValue);
			this.setMax(this.clearMaxValue)
		},
		getColor : function(C) {
			if (typeof this.normalize === "function")
				C = this.normalize(C);
			var A = [], B = 0, D;
			for (var E = 0; E < this.colors.length - 1; E++) {
				D = this.vectorLength(this.vectorSubtract(this.colors[E + 1],
						this.colors[E]));
				A.push(D);
				B += D
			}
			var _ = (this.maxValue - this.minValue) / B;
			for (E = 0; E < A.length; E++)
				A[E] *= _;
			E = 0;
			C -= this.minValue;
			while (C - A[E] >= 0) {
				C -= A[E];
				E++
			}
			var $;
			if (E == this.colors.length - 1)
				$ = this.vectorToNum(this.colors[E]).toString(16);
			else
				$ = (this.vectorToNum(this.vectorAdd(this.colors[E], this
								.vectorMult(this.vectorSubtract(this.colors[E
														+ 1], this.colors[E]),
										(C) / (A[E]))))).toString(16);
			while ($.length < 6)
				$ = "0" + $;
			return "#" + $
		},
		vectorToNum : function(_) {
			var $ = 0;
			for (var A = 0; A < _.length; A++)
				$ += Math.round(_[A]) * Math.pow(256, _.length - A - 1);
			return $
		},
		vectorSubtract : function($, _) {
			var A = [];
			for (var B = 0; B < $.length; B++)
				A[B] = $[B] - _[B];
			return A
		},
		vectorAdd : function($, _) {
			var A = [];
			for (var B = 0; B < $.length; B++)
				A[B] = $[B] + _[B];
			return A
		},
		vectorMult : function(A, _) {
			var $ = [];
			for (var B = 0; B < A.length; B++)
				$[B] = A[B] * _;
			return $
		},
		vectorLength : function(_) {
			var $ = 0;
			for (var A = 0; A < _.length; A++)
				$ += _[A] * _[A];
			return Math.sqrt($)
		}
	};
	C.arrayToRgb = function(A) {
		var _ = "#", $;
		for (var B = 0; B < A.length; B++) {
			$ = A[B].toString(16);
			_ += $.length == 1 ? "0" + $ : $
		}
		return _
	};
	C.rgbToArray = function($) {
		$ = $.substr(1);
		return [parseInt($.substr(0, 2), 16), parseInt($.substr(2, 2), 16),
				parseInt($.substr(4, 2), 16)]
	}
})(jQuery);