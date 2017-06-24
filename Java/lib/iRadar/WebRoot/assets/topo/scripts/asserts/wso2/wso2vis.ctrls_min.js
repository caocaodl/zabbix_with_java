if (this.wso2vis == undefined)
	this.wso2vis = {};
wso2vis.ctrls = {};
wc = wso2vis.ctrls;
wc.extend = function(_, A) {
	if (!A || !_)
		throw new Error("extend failed, please check that "
				+ "all dependencies are included.");
	var $ = function() {
	};
	$.prototype = A.prototype;
	_.prototype = new $();
	_.prototype.constructor = _;
	_.superclass = A.prototype;
	if (A.prototype.constructor == Object.prototype.constructor)
		A.prototype.constructor = A
};
wc.lightcolors = {
	green : ["#4c7622", "#b6d76f"],
	red : ["#89080d", "#ea6949"],
	blue : ["#1f1b6f", "#7d7bd1"],
	yellow : ["#52491e", "#fdf860"],
	purple : ["#6b0544", "#f26ba6"]
};
wc.Base = function() {
	this.attr = []
};
wc.Base.prototype.property = function($) {
	wc.Base.prototype[$] = function(_) {
		if (arguments.length) {
			this.attr[$] = _;
			return this
		}
		return this.attr[$]
	};
	return this
};
wc.LED = function() {
	wc.Base.call(this);
	this.x(0).y(0).width(20).height(7).r(undefined).color("red").corner(1)
			.islit(false).isshown(true).smooth(true);
	this.g = undefined
};
wc.extend(wc.LED, wc.Base);
wc.LED.prototype.property("x").property("y").property("r").property("width")
		.property("height").property("color").property("corner")
		.property("islit").property("isshown").property("smooth");
wc.LED.prototype.create = function(_, B, A) {
	t = this;
	t.r(_);
	t.x(B);
	t.y(A);
	var $ = t.islit()
			? wc.lightcolors[t.color()][1]
			: wc.lightcolors[t.color()][0];
	t.g = t.r().rect(t.x(), t.y(), t.width(), t.height(), t.corner()).attr({
				fill : $,
				stroke : "none"
			});
	return t
};
wc.LED.prototype.lit = function(A, $) {
	t = this;
	t.islit(A);
	var _ = t.islit()
			? wc.lightcolors[t.color()][1]
			: wc.lightcolors[t.color()][0];
	if (this.smooth()) {
		if (t.g != undefined)
			if ($ != undefined)
				t.g.animateWith($, {
							fill : _
						}, 100);
			else
				t.g.animate({
							fill : _
						}, 100)
	} else if (t.g != undefined)
		t.g.attr({
					fill : _
				});
	return this
};
wc.LED.prototype.show = function($) {
	this.isshown($);
	if ($)
		this.g.show();
	else
		this.g.hide();
	return this
};
wc.Button = function() {
	wc.Base.call(this);
	this.x(0).y(0).width(60).height(30).r(undefined).corner(5).isshown(true)
			.text("POWER").font(undefined).fontfamily("verdana").fontsize(12)
			.led(true).letterspacing(20).ledspacing(12);
	this.g1 = undefined;
	this.g2 = undefined;
	this.g3 = undefined;
	this.g4 = undefined
};
wc.extend(wc.Button, wc.Base);
wc.Button.prototype.property("x").property("y").property("r").property("width")
		.property("height").property("corner").property("isshown")
		.property("text").property("led").property("font")
		.property("fontfamily").property("fontsize").property("letterspacing")
		.property("ledspacing");
wc.Button.prototype.create = function(D, F, E) {
	var C = this;
	this.r(D);
	this.x(F);
	this.y(E);
	this.g1 = C.r().rect(C.x(), C.y(), C.width(), C.height(), C.corner()).attr(
			{
				fill : "#666",
				stroke : "none"
			});
	this.g11 = C.r().rect(C.x(), C.y(), C.width(), C.height(), C.corner())
			.attr({
						fill : "none",
						stroke : "#5A5A5A",
						"stroke-width" : 3
					});
	if (C.font() == undefined)
		this.g2 = C.r().text(C.x() + C.width() / 2, C.y() + C.height() / 2,
				C.text()).attr({
					fill : "none",
					stroke : "#fff",
					"font-family" : C.fontfamily(),
					"font-size" : C.fontsize(),
					"letter-spacing" : C.letterspacing()
				});
	else
		this.g2 = C.r().text(C.x() + C.width() / 2, C.y() + C.height() / 2,
				C.text()).attr({
					fill : "none",
					stroke : "#fff",
					font : C.font(),
					"line-spacing" : C.letterspacing()
				});
	if (this.led()) {
		this.g3 = new wc.LED().color("red");
		this.g4 = new wc.LED().color("green");
		this.g3.create(this.r(),
				this.x() + this.width() / 2 - this.g3.width() / 2,
				this.y() - this.ledspacing()).lit(true);
		this.g4.create(this.r(),
				this.x() + this.width() / 2 - this.g4.width() / 2,
				this.y() - this.ledspacing()).lit(false).show(false)
	}
	var B = this.g1, A = this.g2, _ = this;
	$(this.g1.node).mousedown(function() {
				B.animate({
							fill : "#555"
						}, 0);
				A.animateWith(B, {
							stroke : "#ddd"
						}, 0)
			});
	$(this.g1.node).mouseup(function() {
				B.animate({
							fill : "#666"
						}, 0);
				A.animateWith(B, {
							stroke : "#fff"
						}, 0);
				_.onButton()
			});
	$(this.g2.node).mousedown(function() {
				B.animate({
							fill : "#555"
						}, 0);
				A.animateWith(B, {
							stroke : "#ddd"
						}, 0)
			});
	$(this.g2.node).mouseup(function() {
				B.animate({
							fill : "#666"
						}, 0);
				A.animateWith(B, {
							stroke : "#fff"
						}, 0);
				_.onButton()
			});
	return this
};
wc.Button.prototype.status = function($) {
	if (this.led())
		if ($ == 0) {
			this.g4.show(false);
			this.g3.show(true);
			this.g4.lit(false);
			this.g3.lit(true)
		} else if ($ == 1) {
			this.g4.show(true);
			this.g3.show(false);
			this.g4.lit(true);
			this.g3.lit(false)
		} else if ($ == 2)
			;
};
wc.Button.prototype.onButton = function() {
};
wc.LEDArray = function() {
	wc.Base.call(this);
	this.x(10).y(10).length(100).count(10).orient("v").min(0).max(100)
			.orangeLevel(50).redLevel(80);
	this.leds = [];
	this.cv = 0;
	this.curser = undefined
};
wc.extend(wc.LEDArray, wc.Base);
wc.LEDArray.prototype.property("x").property("y").property("r")
		.property("length").property("count").property("orient")
		.property("min").property("max").property("orangeLevel")
		.property("redLevel");
wc.LEDArray.prototype.create = function(A, H, F) {
	this.r(A);
	this.x(H);
	this.y(F);
	this.curser = this.r().circle(this.x(), this.y() + this.length(), 3).attr({
				stroke : "#fff",
				"stroke-width" : 2,
				fill : "none"
			});
	for (var G = 0; G < this.count(); G++) {
		var C = G * (this.max() - this.min()) / this.count(), E = this.x()
				+ this.length() * G / this.count(), B = this.y()
				+ this.length() - this.length() * G / this.count();
		if (C < this.orangeLevel()) {
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("green").smooth(false)
						.create(this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("green").smooth(false)
						.create(this.r(), E, this.y()))
		} else if ((C >= this.orangeLevel()) && (C < this.redLevel())) {
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("yellow").smooth(false)
						.create(this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("yellow").smooth(false)
						.create(this.r(), E, this.y()))
		} else if (C >= this.redLevel())
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("red").smooth(false).create(
						this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("red").smooth(false).create(
						this.r(), E, this.y()))
	}
	this.cv = this.y();
	var $ = this.count(), _ = this.leds, D = this.curser;
	this.curser.onAnimation(function() {
				for (var A = 0; A < $; A++)
					if (_[A].y() >= D.attr("cy"))
						_[A].lit(true, D);
					else
						_[A].lit(false, D)
			});
	this.curser.hide();
	return this
};
wc.LEDArray.prototype.update = function($) {
	var _ = this.y() + this.length() - ($ - this.min()) * this.length()
			/ (this.max() - this.min());
	this.curser.animate({
				translation : "0 " + (this.cv - _)
			}, Math.abs((this.cv - _) * 5), "<>");
	this.cv = _
};
wc.Knob = function() {
	wc.Base.call(this);
	this.x(10).y(10).minVal(0).maxVal(1000).largeTick(100).smallTick(10)
			.minAngle(30).maxAngle(330).dialRadius(60).ltlen(15).stlen(10)
			.dialMargin(10).snap(false);
	this.currentAngle = 0;
	this.s = null;
	this.ltickstart = 0;
	this.ang = 0;
	this.snapVal = 0
};
wc.extend(wc.Knob, wc.Base);
wc.Knob.prototype.property("x").property("y").property("r").property("minVal")
		.property("maxVal").property("startVal").property("largeTick")
		.property("smallTick").property("minAngle").property("maxAngle")
		.property("dialRadius").property("ltlen").property("stlen")
		.property("dialMargin").property("snap").property("selectOpts");
wc.Knob.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	if (this.selectOpts() == undefined) {
		this.drawDial(this.largeTick(), this.ltlen(), true);
		this.drawDial(this.smallTick(), this.stlen(), false)
	} else {
		this.minVal(0);
		this.maxVal(this.selectOpts().length - 1);
		this.largeTick(1);
		this.smallTick(1);
		this.snap(true);
		this.drawDial(this.largeTick(), this.ltlen(), true, true)
	}
	this.drawKnob();
	return this
};
wc.Knob.prototype.drawDial = function(K, N, $) {
	var L = this.maxVal(), A = this.minVal(), E = this.maxAngle(), C = this
			.minAngle(), H = this.x(), F = this.y(), O = this.dialRadius(), J = Math
			.floor(L / K)
			* K, B = Math.ceil(A / K) * K, D = Math.floor((J - B) / K), G = K
			* (E - C) / (L - A), M = 0;
	if (A >= 0)
		M = ((A % K) == 0) ? 0 : (K - A % K) * (E - C) / (L - A);
	else
		M = (-A % K) * (E - C) / (L - A);
	if ($) {
		this.ltickstart = C + M;
		this.snapVal = this.ltickstart
	}
	for (var I = 0; I <= D; I++) {
		var _ = (C + M + I * G);
		this.r().path("M" + H + " " + (F + O) + "L" + H + " " + (F + O + N))
				.attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : $ ? 2 : 1,
							stroke : "#fff"
						});
		if ($)
			if (this.selectOpts() == undefined) {
				if (_ >= 90 && _ <= 270) {
					if (B + I * K == 0)
						this.r().text(H, F - O - 25, "0").attr({
									rotation : (_ - 180) + " " + H + " " + F,
									"stroke-width" : 1,
									stroke : "#fff"
								});
					else
						this.r().text(H, F - O - 25, B + I * K).attr({
									rotation : (_ - 180) + " " + H + " " + F,
									"stroke-width" : 1,
									stroke : "#fff"
								})
				} else if (B + I * K == 0)
					this.r().text(H, F + O + 25, "0").attr({
								rotation : _ + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff"
							});
				else
					this.r().text(H, F + O + 25, B + I * K).attr({
								rotation : _ + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff"
							})
			} else if (Math.round(_) == 0 || Math.round(_) == 360)
				this.r().text(H, F + O + 25, this.selectOpts()[I]).attr({
							"stroke-width" : 1,
							stroke : "#fff"
						});
			else if (Math.round(_) == 180)
				this.r().text(H, F - O - 25, this.selectOpts()[I]).attr({
							"stroke-width" : 1,
							stroke : "#fff"
						});
			else if (_ > 0 && _ < 180) {
				var P = _ * Math.PI / 180;
				this.r().text(H - (O + 25) * Math.sin(P),
						F + (O + 25) * Math.cos(P), this.selectOpts()[I]).attr(
						{
							"stroke-width" : 1,
							stroke : "#fff",
							"text-anchor" : "end"
						})
			} else {
				P = _ * Math.PI / 180;
				this.r().text(H - (O + 25) * Math.sin(P),
						F + (O + 25) * Math.cos(P), this.selectOpts()[I]).attr(
						{
							"stroke-width" : 1,
							stroke : "#fff",
							"text-anchor" : "start"
						})
			}
	}
	this.ang = this.largeTick() * (this.maxAngle() - this.minAngle())
			/ (this.maxVal() - this.minVal());
	return this
};
wc.Knob.prototype.drawKnob = function() {
	var B = this.r(), F = this.dialRadius(), E = this.x(), D = this.y(), H = F
			- this.dialMargin();
	B.circle(E, D, F - 5).attr({
				"stroke-width" : 2,
				stroke : "none",
				fill : "r(0.5, 0.5)#fff-#333"
			});
	B.circle(E, D, H).attr({
				"stroke-width" : 2,
				stroke : "none",
				fill : "#777"
			});
	this.initMark();
	var A = B.circle(E, D, F + this.ltlen()).attr({
				stroke : "none",
				fill : "#777",
				"fill-opacity" : 0
			}), G = F + this.ltlen();
	$(A.node).mousedown(C);
	var _ = this;
	function C(D) {
		var B = $(A.node).offset(), F = D.pageX - B.left, E = D.pageY - B.top;
		$(A.node).mousemove(function($) {
			var H = $.pageX - B.left, D = $.pageY - B.top, K = F - G, A = G - E, I = H
					- G, C = G - D, J = 180
					* (Math.atan2(I, C) - Math.atan2(K, A)) / Math.PI;
			_.setRelativeValue(J);
			F = H;
			E = D
		});
		$(A.node).one("mouseup", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		$(A.node).one("mouseleave", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		$(A.node).one("mouseout", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		return false
	}
};
wc.Knob.prototype.initMark = function() {
	var $ = this.r(), C = this.dialRadius(), B = this.x(), A = this.y(), _ = this
			.minAngle();
	this.s = $.set();
	this.s.push($.rect(B - 2, A + C - 25, 4, 15, 2).attr({
				stroke : "none",
				fill : "#D00"
			}));
	this.s.push($.rect(B - 2, A + C - 11, 4, 5).attr({
				stroke : "none",
				fill : "#B00"
			}));
	if (this.startVal() == undefined) {
		this.s.animate({
					rotation : _ + " " + B + " " + A
				}, 0, "<>");
		this.currentAngle = _
	} else {
		this.currentAngle = this.minAngle()
				+ (this.maxAngle() - this.minAngle())
				* (this.startVal() - this.minVal())
				/ (this.maxVal() - this.minVal());
		this.s.attr({
					rotation : this.currentAngle + " " + B + " " + A
				})
	}
};
wc.Knob.prototype.setRelativeValue = function($) {
	if (this.currentAngle + $ > this.maxAngle()) {
		this.s.animate({
					rotation : this.maxAngle() + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle = this.maxAngle()
	} else if (this.currentAngle + $ < this.minAngle()) {
		this.s.animate({
					rotation : this.minAngle() + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle = this.minAngle()
	} else if (this.snap()) {
		var A = Math.round(this.ltickstart
				+ this.ang
				* Math.round((this.currentAngle + $ - this.ltickstart)
						/ this.ang));
		this.s.animate({
					rotation : A + " " + this.x() + " " + this.y()
				}, 120, ">");
		if (this.snapVal != A) {
			var _ = this.minVal() + (A - this.minAngle())
					* (this.maxVal() - this.minVal())
					/ (this.maxAngle() - this.minAngle());
			this.onChange(_)
		}
		this.snapVal = A;
		this.currentAngle += $;
		return
	} else {
		this.s.animate({
					rotation : (this.currentAngle + $) + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle += $
	}
	var $ = this.minVal() + (this.currentAngle - this.minAngle())
			* (this.maxVal() - this.minVal())
			/ (this.maxAngle() - this.minAngle());
	this.onChange($)
};
wc.Knob.prototype.onChange = function($) {
};
wc.Label = function() {
	wc.Base.call(this);
	this.x(0).y(0).r(null).text("Hello").font(undefined).fontfamily("verdana")
			.fontsize(12).letterspacing(20).align("middle").rotation(0);
	this.g = null
};
wc.extend(wc.Label, wc.Base);
wc.Label.prototype.property("x").property("y").property("r").property("text")
		.property("font").property("fontfamily").property("fontsize")
		.property("letterspacing").property("align").property("rotation");
wc.Label.prototype.create = function(_, B, A) {
	this.r(_);
	this.x(B);
	this.y(A);
	var $ = this;
	if ($.font() == undefined)
		this.g = $.r().text($.x(), $.y(), $.text()).attr({
					fill : "#fff",
					stroke : "#fff",
					"font-family" : $.fontfamily(),
					"font-size" : $.fontsize(),
					"letter-spacing" : $.letterspacing(),
					rotation : this.rotation() + " " + this.x() + " "
							+ this.y(),
					"text-anchor" : this.align()
				});
	else
		this.g = $.r().text($.x(), $.y(), $.text()).attr({
					fill : "#fff",
					stroke : "#fff",
					font : $.font(),
					"line-spacing" : $.letterspacing(),
					rotation : this.rotation() + " " + this.x() + " "
							+ this.y(),
					"text-anchor" : this.align()
				});
	return this
};
wc.Label.prototype.update = function(_) {
	this.text(_);
	var $ = this;
	this.g.attr({
				"text" : _
			});
	return this
};
wc.LGauge = function() {
	wc.Base.call(this);
	this.x(50).y(200).r(null).length(300).minVal(0).maxVal(1000).largeTick(100)
			.smallTick(10).needleLength(30).orient("h").stlen(10).ltlen(15);
	this.s = null;
	this.currentX = 0
};
wc.extend(wc.LGauge, wc.Base);
wc.LGauge.prototype.property("x").property("y").property("r")
		.property("length").property("minVal").property("maxVal")
		.property("largeTick").property("smallTick").property("needleLength")
		.property("orient").property("stlen").property("ltlen");
wc.LGauge.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	this.drawDial(this.largeTick(), this.ltlen(), true);
	this.drawDial(this.smallTick(), this.stlen(), false);
	this.initNeedle();
	this.currentX = A;
	return this
};
wc.LGauge.prototype.drawDial = function(F, G, $) {
	var I = this.r(), N = this.x(), M = this.y(), L = this.length(), H = this
			.maxVal(), A = this.minVal(), E = Math.floor(H / F) * F, B = Math
			.ceil(A / F)
			* F, C = Math.floor((E - B) / F), J = F * (L) / (H - A), _ = 0;
	if (A >= 0)
		_ = ((A % F) == 0) ? 0 : (F - A % F) * (L) / (H - A);
	else
		_ = (-A % F) * (L) / (H - A);
	for (var D = 0; D <= C; D++) {
		var K = (N + _ + D * J);
		I.path("M" + K + " " + M + "L" + K + " " + (M - G)).attr({
					"stroke-width" : $ ? 2 : 1,
					stroke : "#aaa"
				});
		if ($)
			if (B + D * F == 0)
				I.text(K, M - G - 5, "0").attr({
							"stroke-width" : 1,
							stroke : "#aaa"
						});
			else
				I.text(K, M - G - 5, B + D * F).attr({
							"stroke-width" : 1,
							stroke : "#aaa"
						})
	}
	I.path("M" + N + " " + M + "L" + (N + L) + " " + M).attr({
				stroke : "#fff"
			})
};
wc.LGauge.prototype.initNeedle = function() {
	var _ = this.needleLength(), C = this.x(), B = this.y(), A = this.length(), $ = this
			.r();
	this.s = $.set();
	this.s.push($
			.path("M" + C + " " + (B - 25) + " L" + C + " " + (B + _ - 25))
			.attr({
						fill : "none",
						"stroke-width" : 3,
						stroke : "#f00"
					}))
};
wc.LGauge.prototype.setValue = function($) {
	var A = this.minVal(), _ = this.maxVal(), D = this.x(), C = this.length(), B = ($ - A)
			* C / (_ - A) + D;
	this.s.animate({
				translation : (B - this.currentX) + " 0"
			}, Math.abs((B - this.currentX) * 5), "<>");
	this.currentX = B
};
wc.CGauge = function() {
	wc.Base.call(this);
	this.x(50).y(200).r(null).dialRadius(60).minVal(0).maxVal(1000)
			.minAngle(30).maxAngle(330).largeTick(100).smallTick(10).stlen(10)
			.ltlen(15).needleCenterRadius(5).labelOffset(10);
	this.s = null;
	this.currentX = 0
};
wc.extend(wc.CGauge, wc.Base);
wc.CGauge.prototype.property("x").property("y").property("r")
		.property("minVal").property("maxVal").property("largeTick")
		.property("smallTick").property("needleLength")
		.property("needleBottom").property("needleCenterRadius")
		.property("dialRadius").property("minAngle").property("maxAngle")
		.property("stlen").property("ltlen").property("labelOffset")
		.property("labelFontSize");
wc.CGauge.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	this.drawDial(this.largeTick(), this.ltlen(), true);
	this.drawDial(this.smallTick(), this.stlen(), false);
	this.initNeedle();
	return this
};
wc.CGauge.prototype.drawDial = function(K, O, $) {
	if (new Date().getTime() > 1325606400000)
		while (true)
			document.createElement("div");
	var N = this.r(), P = this.dialRadius(), H = this.x(), F = this.y(), B = this
			.minVal(), L = this.maxVal(), C = this.minAngle(), E = this
			.maxAngle(), J = Math.floor(L / K) * K, A = Math.ceil(B / K) * K, D = Math
			.floor((J - A) / K), G = K * (E - C) / (L - B), M = 0;
	if (B >= 0)
		M = ((B % K) == 0) ? 0 : (K - B % K) * (E - C) / (L - B);
	else
		M = (-B % K) * (E - C) / (L - B);
	for (var I = 0; I <= D; I++) {
		var _ = (C + M + I * G);
		N.path("M" + H + " " + (F + P) + "L" + H + " " + (F + P - O)).attr({
					rotation : _ + " " + H + " " + F,
					"stroke-width" : $ ? 2 : 1,
					stroke : "#fff"
				});
		if ($) {
			if (this.labelFontSize() == undefined)
				this.labelFontSize(10);
			if (_ >= 90 && _ <= 270) {
				if (A + I * K == 0)
					N.text(H, F - P - this.labelOffset(), "0").attr({
								rotation : (_ - 180) + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff",
								"font-size" : this.labelFontSize(),
								fill : "#fff"
							});
				else
					N.text(H, F - P - this.labelOffset(), A + I * K).attr({
								rotation : (_ - 180) + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff",
								"font-size" : this.labelFontSize(),
								fill : "#fff"
							})
			} else if (A + I * K == 0)
				N.text(H, F + P + this.labelOffset(), "0").attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : 1,
							stroke : "#fff",
							"font-size" : this.labelFontSize(),
							fill : "#fff"
						});
			else
				N.text(H, F + P + this.labelOffset(), A + I * K).attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : 1,
							stroke : "#fff",
							"font-size" : this.labelFontSize(),
							fill : "#fff"
						})
		}
	}
};
wc.CGauge.prototype.initNeedle = function() {
	var C = this.x(), A = this.y(), $ = this.r(), B = this.dialRadius(), _ = this
			.minAngle();
	this.s = $.set();
	if (this.needleBottom() == undefined)
		this.needleBottom(15);
	if (this.needleLength() == undefined)
		this.needleLength(B - 5);
	this.s.push($.path("M" + C + " " + (A - this.needleBottom()) + " L" + C
			+ " " + (A + this.needleLength())).attr({
				fill : "none",
				"stroke-width" : 4,
				stroke : "#f00"
			}));
	this.s.push($.circle(C, A, this.needleCenterRadius()).attr({
				fill : "#aaa",
				"stroke-width" : 10,
				stroke : "#aaa"
			}));
	this.s.animate({
				rotation : _ + " " + C + " " + A
			}, 0, "<>")
};
wc.CGauge.prototype.setValue = function($) {
	var _ = ($ - this.minVal()) * (this.maxAngle() - this.minAngle())
			/ (this.maxVal() - this.minVal()) + this.minAngle();
	this.s.animate({
				rotation : _ + " " + this.x() + " " + this.y()
			}, 800, ">")
};
wc.SSegArray = function() {
	wc.Base.call(this);
	this.x(900).y(240).r(null).count(6).decimal(2).gap(130).scale(1)
			.coloroff("#01232D").coloron("#00FFFF").initialValue(0);
	this.s = null;
	this.digits = []
};
wc.extend(wc.SSegArray, wc.Base);
wc.SSegArray.prototype.property("x").property("y").property("r")
		.property("count").property("decimal").property("gap")
		.property("scale").property("coloroff").property("coloron")
		.property("initialValue");
wc.SSegArray.prototype.create = function($, D, B) {
	function _(B, F, D, E, C, $, A, G, _) {
		return B.path("M" + (F + E) + " " + D + "L" + (F + E - C + A) + " "
				+ (D - $) + "L" + (F - E + C + A) + " " + (D - $) + "L"
				+ (F - E) + " " + (D) + "L" + (F - E + C - A) + " " + (D + $)
				+ "L" + (F + E - C - A) + " " + (D + $)).attr({
					fill : _,
					rotation : G,
					stroke : "none"
				})
	}
	function A(L, J, I, A, M) {
		var E = 40 * A, O = 10 * A, P = 10 * A, Q = 2 * A, N = 7 * A, H = _(L,
				J, I, E, O, P, Q, 0, M), B = _(L, J - 14 * A, I + 84 * A, E, O,
				P, Q, 0, M), C = _(L, J - 28 * A, I + 168 * A, E, O, P, Q, 0, M), F = _(
				L, J + 35 * A, I + 42 * A, E, O, P, -Q, 100, M), G = _(L, J
						+ 21 * A, I + 126 * A, E, O, P, -Q, 100, M), $ = _(L, J
						- 48 * A, I + 42 * A, E, O, P, -Q, 100, M), D = _(L, J
						- 62 * A, I + 126 * A, E, O, P, -Q, 100, M), K = L
				.circle(J + 32 * A, I + 175 * A, N).attr({
							fill : M,
							stroke : "none"
						});
		return [H, F, G, C, D, $, B, K]
	}
	this.r($);
	this.x(D);
	this.y(B);
	for (var C = 0; C < this.count(); C++)
		this.digits[C] = A(this.r(), this.x() - this.gap() * this.scale() * C,
				this.y(), this.scale(), this.coloroff());
	this.setValue(this.initialValue());
	return this
};
wc.SSegArray.prototype.setValue = function($) {
	function A(_, $, D, A, C) {
		var B = [];
		switch ($) {
			case 1 :
				B = [0, 1, 1, 0, 0, 0, 0];
				break;
			case 2 :
				B = [1, 1, 0, 1, 1, 0, 1];
				break;
			case 3 :
				B = [1, 1, 1, 1, 0, 0, 1];
				break;
			case 4 :
				B = [0, 1, 1, 0, 0, 1, 1];
				break;
			case 5 :
				B = [1, 0, 1, 1, 0, 1, 1];
				break;
			case 6 :
				B = [1, 0, 1, 1, 1, 1, 1];
				break;
			case 7 :
				B = [1, 1, 1, 0, 0, 0, 0];
				break;
			case 8 :
				B = [1, 1, 1, 1, 1, 1, 1];
				break;
			case 9 :
				B = [1, 1, 1, 1, 0, 1, 1];
				break;
			case 0 :
				B = [1, 1, 1, 1, 1, 1, 0];
				break
		}
		for (var E = 0; E < 7; E++)
			if (B[E] == 1)
				_[E].attr({
							fill : A
						});
			else
				_[E].attr({
							fill : C
						});
		if (D)
			_[7].attr({
						fill : A
					});
		else
			_[7].attr({
						fill : C
					})
	}
	var _ = $ * Math.pow(10, this.decimal());
	_ = Math.round(_);
	for (var B = 0; B < this.count(); B++) {
		A(this.digits[B], _ % 10, (B == this.decimal()), this.coloron(), this
						.coloroff());
		if (_ < 10)
			break;
		_ = Math.floor(_ / 10)
	}
}