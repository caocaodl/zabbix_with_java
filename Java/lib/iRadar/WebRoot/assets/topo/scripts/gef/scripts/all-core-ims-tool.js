
Gef.ns("Gef.tool");
Gef.tool.AbstractEdgeTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "abstractEdgeTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/edges.png"
			},
			getX : function($) {
				return $ + 5
			},
			getY : function() {
				return 40
			},
			drag : function(_) {
				var $ = this.node, A = $.editPart;
				if (A != null && A.getClass() == "node")
					if (A.canCreateOutgo())
						_.temp.editPart = A;
				_.getDraggingEdge().update(-1, -1, -1, -1)
			},
			move : function(_, C) {
				var B = C.point, $ = _.temp.editPart.getFigure(), D = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = _.getDraggingEdge();
				A.updateForDragging(D, B)
			},
			drop : function($, B) {
				var A = $.getDraggingEdge(), E = $.temp.editPart, C = $
						.findEditPartAt(B);
				if (E != C && E.canCreateOutgo(C) && C.getClass() == "node"
						&& C.canCreateIncome(E)) {
					var _ = this.getConnectionModelName(), D = $
							.getModelFactory().createModel(_);
					if (E.getModel().getOutgoingConnections().length > 0)
						D.text = "to " + C.getModel().text;
					else
						D.text = "";
					B.role = {
						name : "CREATE_EDGE",
						rect : {
							x1 : A.x1,
							y1 : A.y1,
							x2 : A.x2,
							y2 : A.y2
						},
						source : E.getModel(),
						target : C.getModel(),
						model : D
					};
					$.executeCommand($.temp.editPart, B)
				}
				$.getDraggingEdge().moveToHide()
			}
		});
Gef.ns("Gef.gef.xml");
Gef.gef.xml.XmlSerializer = function($) {
	this.model = $;
};
Gef.gef.xml.XmlSerializer.prototype = {
	serialize : function() {
	
		return this.model.encode();
	}
};

Gef.ns("Gef.layer");
Gef.layer.Layer = Gef.extend(Gef.figure.GroupFigure, {
			LAYER_MASK : "LAYER_MASK",
			LAYER_LABEL : "LAYER_LABEL",
			LAYER_DRAGGING : "LAYER_DRAGGING",
			LAYER_HANDLE : "LAYER_HANDLE",
			LAYER_NODE : "LAYER_NODE",
			LAYER_CONNECTION : "LAYER_CONNECTION",
			LAYER_SNAP : "LAYER_SNAP",
			LAYER_GRID : "LAYER_GRID",
			constructor : function($) {
				this.name = $;
				this.id = $;
				Gef.layer.Layer.superclass.constructor.call(this)
			},
			getName : function() {
				return this.name
			}
		});
Gef.ns("Gef.layer");
Gef.layer.GridLayer = Gef.extend(Gef.layer.Layer, {});

Gef.ns("Gef.model");
Gef.model.XmlUtil = {
	readXml : function(_) {
		var $ = null;
		if (typeof(DOMParser) == "undefined") {
			$ = new ActiveXObject("Microsoft.XMLDOM");
			$.async = "false";
			$.loadXML(_)
		} else {
			var A = new DOMParser();
			$ = A.parseFromString(_, "application/xml");
			A = null
		}
		if ($.documentElement == null)
			("import error");
		else if ($.documentElement.tagName == "parsererror")
			("import error: " + $.documentElement.firstChild.textContent);
		else
			return $
	},
	elements : function($) {
		var A = [];
		for (var B = 0; B < $.childNodes.length; B++) {
			var _ = $.childNodes[B];
			if (_.nodeType != 3 && _.nodeType != 8)
				A.push(_)
		}
		return A
	},
	decode : function(_) {
		var $ = new Gef.model.Dom("node");
		$.decode(_);
		return $
	}
};
Gef.ns("Gef.model");
Gef.model.JpdlUtil = {
	decodeNodePosition : function(_) {
		var $ = _.dom.getAttribute("g"), A = $.split(",");
		_.x = parseInt(A[0], 10);
		_.y = parseInt(A[1], 10);
		_.w = parseInt(A[2], 10);
		_.h = parseInt(A[3], 10)
	},
	decodeConnectionPosition : function(D) {
		var $ = D.dom.getAttribute("g");
		if (!$)
			return;
		var C = $, A = $.split(":");
		if ($.indexOf(":") != -1) {
			C = A[1];
			if (A[0].length > 0) {
				var E = A[0].split(";"), B = [];
				Gef.each(E, function($) {
							var _ = $.split(",");
							B.push([parseInt(_[0], 10), parseInt(_[1], 10)])
						});
				D.innerPoints = B
			}
		} else
			C = $;
		var _ = C.split(",");
		D.textX = parseInt(_[0], 10);
		D.textY = parseInt(_[1], 10);
		this.decodeTextPosition(D)
	},
	decodeTextPosition : function(J) {
		var P = J.getSource(), 
			K = new Geom.Rect(parseInt(P.x, 10), parseInt(P.y, 10), parseInt(P.w, 10), parseInt(P.h, 10)), 
			N = J.getTarget(), 
			I = new Geom.Rect(parseInt(N.x, 10), parseInt(N.y, 10), parseInt(N.w, 10), parseInt(N.h, 10)), 
			$ = new Geom.Line(parseInt(K.x, 10) + parseInt(K.w, 10) / 2, 
						parseInt(K.y, 10) + parseInt(K.h, 10) / 2, 
						parseInt(I.x, 10) + parseInt(I.w, 10) / 2, 
						parseInt(I.y, 10) + parseInt(I.h, 10) / 2
					), 
			E = K.getCrossPoint($), 
			C = I.getCrossPoint($);
		if ((!E) || (!C))
			return;
		var L = (E.x + C.x) / 2, 
			B = (E.y + C.y) / 2;
		if (J.innerPoints.length > 0) {
			var A = J.innerPoints[0], 
				_ = J.innerPoints[J.innerPoints.length- 1], 
				O = [];
			O.push([E.x, E.y]);
			Gef.each(J.innerPoints, function($) {
						O.push([$[0], $[1]])
					});
			O.push([C.x, C.y]);
			var G = O.length, F = 0, D = 0;
			if ((G % 2) == 0) {
				var H = O[G / 2 - 1], M = O[G / 2];
				F = (H[0] + M[0]) / 2;
				D = (H[1] + M[1]) / 2
			} else {
				F = O[(G - 1) / 2][0];
				D = O[(G - 1) / 2][1]
			}
			var R = parseInt(J.textX + L - F, 10), 
				Q = parseInt(J.textY + B - D, 10);
			J.textX -= L - F;
			J.textY -= B - D
		}
	},
	encodeNodePosition : function($) {
		$.dom.setAttribute("g", $.x + "," + $.y + "," + $.w + "," + $.h)
	},
	encodeConnectionPosition : function(_) {
		var $ = [];
		Gef.each(_.innerPoints, function(B, A) {
					$.push(parseInt(B[0], 10), ",", parseInt(B[1], 10));
					if (A != _.innerPoints.length - 1)
						$.push(";")
				});
		$.push(this.encodeTextPosition(_));
		return $.join("")
	},
	encodeTextPosition : function(J) {
		var P = J.getSource(), 
			K = new Geom.Rect(parseInt(P.x, 10), parseInt(P.y, 10), parseInt(P.w, 10), parseInt(P.h, 10)), 
			N = J.getTarget(), 
			I = new Geom.Rect(parseInt(N.x, 10), parseInt(N.y, 10), parseInt(N.w, 10), parseInt(N.h, 10)), 
			$ = new Geom.Line(
						parseInt(K.x, 10) + parseInt(K.w, 10) / 2, 
						parseInt(K.y, 10) + parseInt(K.h, 10) / 2, 
						parseInt(I.x, 10) + parseInt(I.w, 10) / 2, 
						parseInt(I.y, 10) + parseInt(I.h, 10) / 2
					), 
			E = K.getCrossPoint($), 
			C = I.getCrossPoint($);
		if ((!E) || (!C))
			return;
		var L = (E.x + C.x) / 2, B = (E.y + C.y) / 2;
		if (J.innerPoints.length > 0) {
			var A = J.innerPoints[0], 
				_ = J.innerPoints[J.innerPoints.length- 1], 
				O = [];
			O.push([E.x, E.y]);
			Gef.each(J.innerPoints, function($) {
						O.push([$[0], $[1]])
					});
			O.push([C.x, C.y]);
			var G = O.length, F = 0, D = 0;
			if ((G % 2) == 0) {
				var H = O[G / 2 - 1], M = O[G / 2];
				F = (H[0] + M[0]) / 2;
				D = (H[1] + M[1]) / 2
			} else {
				F = O[(G - 1) / 2][0];
				D = O[(G - 1) / 2][1]
			}
			var R = parseInt(J.textX + L - F, 10), 
				Q = parseInt(J.textY + B - D, 10);
			return ":" + R + "," + Q
		} else if (J.textX != 0 && J.textY != 0)
			return parseInt(J.textX, 10) + "," + parseInt(J.textY, 10);
		else
			return ""
	}
};

JobExecutor = function($) {
	this.replay = $;
	this.running = false
};
JobExecutor.prototype = {
	start : function() {
		if (this.running !== true) {
			this.running = true;
			this.tid = new Date().getTime();
			this.run(this.tid)
		}
	},
	run : function(C) {
		if (this.running !== true)
			return;
		if (C != this.tid)
			return;
		var $ = 0, A = Array.prototype.slice.call(this.replay.tokens, 0);
		for (var D = 0; D < A.length; D++) {
			var _ = A[D];
			if (_.status === "running") {
				$++;
				_.move()
			}
		}
		if ($ !== 0) {
			var B = this;
			setTimeout(function() {
						B.run(C)
					}, 100)
		} else {
			this.running = false;
			A = [];
			for (D = 0; D < this.replay.tokens.length; D++) {
				_ = this.replay.tokens[D];
				if (_.status !== "removed")
					A.push(_)
			}
			this.replay.tokens = A
		}
	}
};
Node = function(A, $) {
	this.name = A.name;
	this.type = A.type;
	this.x = A.x;
	this.y = A.y;
	if (this.type === "start" || this.type === "end"
			|| this.type === "end-error" || this.type === "end-cancel"
			|| this.type === "decision" || this.type === "fork"
			|| this.type === "join") {
		this.w = 48;
		this.h = 48
	} else {
		this.w = A.w;
		this.h = A.h
	}
	this.activity = A;
	this.replay = $;
	this.parent = [];
	this.children = [];
	var _ = this.replay.map[this.name];
	if (typeof _ !== "undefined") {
		if (_ !== this)
			throw new Error("node duplicated, name: " + this.name)
	} else
		this.replay.map[this.name] = this;
	if (!this.isCurrentActivity(this.name))
		this.init()
};
Node.prototype = {
	init : function() {
		if (!this.hasHistory())
			this.findTransitions()
	},
	createChildNode : function(B) {
		var A = B.name, $ = this.replay.map[B.name], _ = null;
		if (typeof $ !== "undefined")
			_ = $;
		else
			_ = new Node(B, this.replay);
		this.children.push(_);
		_.parent.push(this)
	},
	hasHistory : function() {
		var B = this.replay.historyActivities;
		for (var F = 0; F < B.length; F++) {
			var E = B[F];
			if (E.name === this.activity.name) {
				var A = E.t, _ = this.activity.ts;
				for (var C = 0; C < _.length; C++) {
					var $ = _[C];
					if ($.name === A) {
						var D = this.findActivity($.to);
						this.createChildNode(D);
						return true
					}
				}
			}
		}
		return false
	},
	findTransitions : function() {
		var _ = this.activity.ts;
		for (var C = 0; C < _.length; C++) {
			var $ = _[C], A = $.to, B = this.findActivity(A);
			this.createChildNode(B)
		}
	},
	findActivity : function(_) {
		var $ = this.replay.processDefinition;
		for (var B = 0; B < $.length; B++) {
			var A = $[B];
			if (A.name === _)
				return A
		}
	},
	isCurrentActivity : function(_) {
		var $ = this.replay.currentActivities;
		for (var B = 0; B < $.length; B++) {
			var A = $[B];
			if (A === _)
				return true
		}
		return false
	}
};
REPLAY_TOKEN_IMAGE = "user.png";
Replay = function($, A, _, B) {
	this.processDefinition = $;
	this.historyActivities = A;
	this.currentActivities = _;
	this.tokens = [];
	this.map = {};
	this.initialize();
	this.jobExecutor = new JobExecutor(this);
	this.parent = B
};
Replay.prototype = {
	initialize : function() {
		for (var A = 0; A < this.processDefinition.length; A++) {
			var _ = this.processDefinition[A];
			if (_.type === "start") {
				var $ = new Node(_, this);
				this.init = $;
				this.tokens.push(new Token($, this, this.parent));
				break
			}
		}
	},
	notify : function(_) {
		if (_ !== 0) {
			var A = Array.prototype.slice.call(this.tokens, 0);
			for (var B = 0; B < A.length; B++) {
				var $ = A[B];
				if ($.startMove(_) === true)
					this.jobExecutor.start()
			}
		}
	},
	prev : function() {
		this.notify(-1)
	},
	next : function() {
		this.notify(1)
	},
	replay : function() {
		this.destoryToken();
		this.tokens = [new Token(this.init, this, this.parent)];
		this.notify(this.processDefinition.length)
	},
	destoryToken : function() {
		this.jobExecutor.running = false;
		for (var _ = 0; _ < this.tokens.length; _++) {
			var $ = this.tokens[_];
			$.destroy()
		}
		delete this.tokens
	}
};
Token = function(_, $, A) {
	this.replay = $;
	this.src = _;
	this.status = "prepare";
	this.future = 0;
	this.forkIndex = 0;
	this.step = 10;
	this.parent = A
};
Token.prototype = {
	init : function() {
		this.x = this.src.x + this.src.w / 2 - 10;
		this.y = this.src.y + this.src.h / 2 - 10;
		if (this.status === "prepare") {
			this.status = "waiting";
			this.createImage()
		}
	},
	createImage : function() {
		var $ = document.createElement("img");
		this.parent.appendChild($);
		$.style.position = "absolute";
		$.src = REPLAY_TOKEN_IMAGE;
		$.style.left = this.x + "px";
		$.style.top = this.y + "px";
		this.dom = $
	},
	findNext : function() {
		return this.src.children
	},
	findPrev : function() {
		return this.src.parent
	},
	startMove : function(B) {
		if (B === 0)
			return false;
		if (this.status === "waiting" || this.status === "prepare") {
			var A = B > 0 ? this.findNext() : this.findPrev();
			if (A.length === 0) {
				this.future = 0;
				return false
			}
			for (var C = 0; C < A.length; C++) {
				var $ = A[C], _ = this;
				if (C !== 0) {
					_ = new Token(this.src, replay, this.parent);
					this.replay.tokens.push(_)
				}
				_.forkIndex = this.forkIndex + C;
				_.prepare($, B)
			}
			return true
		} else {
			this.future += B;
			return false
		}
	},
	prepare : function($, _) {
		this.init();
		this.dest = $;
		this.future = _;
		this.status = "running";
		this.step = 0;
		this.calculatePoints()
	},
	calculatePoints : function() {
		var H = this.src.x + this.src.w / 2 - 10, A = this.src.y + this.src.h
				/ 2 - 10, E = this.dest.x + this.dest.w / 2 - 10, B = this.dest.y
				+ this.dest.h / 2 - 10;
		this.points = [[H, A]];
		var D = this.findTransition();
		if (D.length == 0) {
			var $ = (E - H) / 10, _ = (B - A) / 10;
			for (var G = 0; G < 10; G++)
				this.points.push([H + $ * (G + 1), A + _ * (G + 1)])
		} else if (D.length == 1) {
			var F = D[0][0] - 10, C = D[0][1] - 10, $ = (F - H) / 5, _ = (C - A)
					/ 5;
			for (G = 0; G < 5; G++)
				this.points.push([H + $ * (G + 1), A + _ * (G + 1)]);
			$ = (E - F) / 5;
			_ = (B - C) / 5;
			for (G = 0; G < 5; G++)
				this.points.push([F + $ * (G + 1), C + _ * (G + 1)])
		}
	},
	findTransition : function() {
		var $ = null;
		if (this.future > 0)
			$ = this.findTransitionByParent();
		else if (this.future < 0)
			$ = this.findTransitionByChild();
		if (!$)
			$ = [];
		return $
	},
	findTransitionByParent : function() {
		for (var B = 0; B < this.dest.parent.length; B++) {
			var _ = this.dest.parent[B];
			if (this.src == _)
				for (var A = 0; A < _.activity.ts.length; A++) {
					var $ = _.activity.ts[A];
					if ($.to == this.dest.activity.name)
						return $.g
				}
		}
		return null
	},
	findTransitionByChild : function() {
		for (var C = 0; C < this.dest.children.length; C++) {
			var A = this.dest.children[C];
			if (this.src == A)
				for (var B = 0; B < this.dest.activity.ts.length; B++) {
					var _ = this.dest.activity.ts[B];
					if (_.to == A.activity.name) {
						if (!_.g)
							return null;
						var $ = [];
						for (C = _.g.length - 1; C >= 0; C--)
							$.push(_.g[C]);
						return $
					}
				}
		}
		return null
	},
	move : function() {
		this.step++;
		if (this.step > 10) {
			if (this.future !== 0)
				if (this.future > 0)
					this.future--;
				else
					this.future++;
			var $ = this.dest;
			if (this.forkIndex > 0)
				if ($.type == "fork" || $.type == "join") {
					this.destroy();
					return
				}
			this.src = $;
			this.init();
			this.status = "waiting";
			this.startMove(this.future)
		} else {
			this.dom.style.left = this.points[this.step][0] + "px";
			this.dom.style.top = this.points[this.step][1] + "px"
		}
	},
	destroy : function() {
		if (typeof this.dom !== "undefined") {
			document.body.removeChild(this.dom);
			delete this.dom
		}
		this.status = "removed"
	}
};
