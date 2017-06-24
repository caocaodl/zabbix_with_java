Geom = {};
Geom.Point = function(x, y) {
	this.x = x;
	this.y = y;
};

/**
 * 线
 * 
 * @param x1
 * @param y1
 * @param x2
 * @param y2
 * @returns {Geom.Line}
 */
Geom.Line = function(x1, y1, x2, y2) {
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
};
Geom.Line.prototype.getX1 = function() {
	return this.x1;
};
Geom.Line.prototype.getX2 = function() {
	return this.x2;
};
Geom.Line.prototype.getY1 = function() {
	return this.y1;
};
Geom.Line.prototype.getY2 = function() {
	return this.y2;
};
/**
 * 获取直线斜率
 * @returns {Number}
 */
Geom.Line.prototype.getK = function() {
	return (this.y2 - this.y1) / (this.x2 - this.x1);
};
/**
 * 获取直线相对于原点的偏移量
 * @returns {Number}
 */
Geom.Line.prototype.getD = function() {
	return this.y1 - this.getK() * this.x1;
};
Geom.Line.prototype.getDistance = function() {
	return Math.sqrt((this.x1 - this.x2) * (this.x1 - this.x2) + (this.y1 - this.y2) * (this.y1 - this.y2));
};
/**
 * 两条线是否是平行线
 * @param lineObj
 * @returns {Boolean}
 */
Geom.Line.prototype.isParallel = function(lineObj) {
	var x1 = this.x1;
	var x2 = this.x2;
	if ((Math.abs(x1 - x2) < 0.01) && (Math.abs(lineObj.getX1() - lineObj.getX2()) < 0.01)) {
		return true;
	} else if ((Math.abs(x1 - x2) < 0.01) && (Math.abs(lineObj.getX1() - lineObj.getX2()) > 0.01)) {
		return false;
	} else if ((Math.abs(x1 - x2) > 0.01) && (Math.abs(lineObj.getX1() - lineObj.getX2()) < 0.01)) {
		return false;
	} else {
		return Math.abs(this.getK() - lineObj.getK()) < 0.01;
	}
};
/**
 * 是否是同一条直线（平行且在延长线上）
 * @param lineObj
 * @returns {Boolean}
 */
Geom.Line.prototype.isSameLine = function(lineObj) {
	if (this.isParallel(lineObj)) {
		var linK = lineObj.getK();
		var lineD = lineObj.getD();
		if (Math.abs(this.x1 * linK + lineD - this.y1) < 0.01) {
			return true;
		} else {
			return false;
		}
	} else {
		return false;
	}
};
/**
 * 线是否包括点
 * @param point
 * @returns {Boolean}
 */
Geom.Line.prototype.contains = function(point) {
	var x1 = this.x1, 
		y1 = this.y1, 
		x2 = this.x2, 
		y2 = this.y2, 
		px = point.x, 
		py = point.y, 
		p1_p2 = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2), 
		p_p1 = (px - x1) * (px - x1) + (py - y1) * (py - y1),
		p_p2 = (px - x2) * (px - x2) + (py - y2) * (py - y2);
	return p1_p2 > p_p1 && p1_p2 > p_p2
};
/**
 * 获取两条线的相交点
 * @param otherLine
 * @returns
 */
Geom.Line.prototype.getCrossPoint = function(B) {
	if (this.isParallel(B))
		return null;
	var F, D;
	if (Math.abs(this.x1 - this.x2) < 0.01) {
		F = this.x1;
		D = B.getK() * F + B.getD()
	} else if (Math.abs(B.getX1() - B.getX2()) < 0.01) {
		F = B.getX1();
		D = this.getD()
	} else {
		var C = this.getK(), E = B.getK(), $ = this.getD(), _ = B.getD();
		F = (_ - $) / (C - E);
		D = C * F + $
	}
	var A = new Geom.Point(F, D);
	if (B.contains(A) && this.contains(A))
		return A;
	else
		return null
};
/**
 * 获取与x、y的垂直距离
 * 
 * @param 点的x坐标
 * @param 点的y坐标
 * @returns
 */
Geom.Line.prototype.getPerpendicularDistance = function(L, K) {
	var J = new Geom.Point(L, K);
	if (this.x1 == this.x2)
		return this.contains(J) ? Math.abs(this.x1 - L) : 999;
	if (this.y1 == this.y2)
		return this.contains(J) ? Math.abs(this.y1 - K) : 999;
	var _ = this.getK(), 
		A = -1 / _, 
		E = K - A * L, 
		I = new Geom.Line(L, K, 0, E), 
		B = this.getK(), 
		D = I.getK(), 
		F = this.getD(), 
		G = I.getD(), 
		C = (G - F) / (B - D), 
		$ = B * L + F, 
		H = new Geom.Point(C, $);
	if (this.contains(H))
		return new Geom.Line(L, K, C, $).getDistance();
	else
		return 999
};

/**
 * 矩形
 * 
 * @param x
 * @param y
 * @param w
 * @param h
 * @returns {Geom.Rect}
 */
Geom.Rect = function(B, A, $, _) {
	this.x = B;
	this.y = A;
	this.w = $;
	this.h = _
};
Geom.Rect.prototype.getCrossPoint = function(A) {
	var $ = null, D = new Geom.Line(this.x, this.y, this.x + this.w, this.y);
	$ = D.getCrossPoint(A);
	if ($ != null)
		return $;
	var _ = new Geom.Line(this.x, this.y + this.h, this.x + this.w, this.y + this.h);
	$ = _.getCrossPoint(A);
	if ($ != null)
		return $;
	var B = new Geom.Line(this.x, this.y, this.x, this.y + this.h);
	$ = B.getCrossPoint(A);
	if ($ != null)
		return $;
	var C = new Geom.Line(this.x + this.w, this.y, this.x + this.w, this.y + this.h);
	$ = C.getCrossPoint(A);
	return $
};

/**
 * 创建一个基本类库对象
 * 
 * @param {} _
 * @return {}
 */
function createCore(_) {
	var $ = {
		svgns : "http://www.w3.org/2000/svg",
		linkns : "http://www.w3.org/1999/xlink",
		vmlns : "urn:schemas-microsoft-com:vml",
		officens : "urn:schemas-microsoft-com:office:office",
		emptyFn : function() {
		},
		emptyArray : [],
		emptyMap : {},
		devMode : true,
		/**
		 * IE浏览器 初始化加载Vml
		 */
		installVml : function() {
			if ($.isVml) {
				document.attachEvent("onreadystatechange", function() {
					var dom = document;
					if (dom.readyState == "complete") {
						if (!dom.namespaces["v"]) {
							dom.namespaces.add("v", $.vmlns);
						}
						if (!dom.namespaces["o"]) {
							dom.namespaces.add("o", $.officens)
						}
					}
				});
				var sheet = document.createStyleSheet();
				sheet.cssText = "v\\:*{behavior:url(#default#VML)}" + "o\\:*{behavior:url(#default#VML)}";
			}
		},
		seed : 0,
		/**
		 * 页面元素ID
		 * 
		 * @return {}
		 */
		id : function() {
			if (!_) {
				return "_INNER_" + this.seed++;
			} else {
				return "_" + _ + "_" + this.seed++;
			}
		},
		onReady : function(obj) {
			window.onload = function() {
				obj();
			}
		},
		/**
		 * 错误消息
		 * 
		 * @param {}
		 *            obj 错误信息对象
		 * @param {}
		 *            cause 错误信息原因
		 */
		error : function(obj, cause) {
			if ($.devMode !== true) {
				return;
			}
			if ($.isVml) {
				var msg = (cause ? cause : "") + "\n";
				for ( var pro in obj) {
					msg += pro + ":" + obj[pro] + "\n";
				}
				$.debug(msg);
			}

		},
		/**
		 * 页面输出错误消息
		 */
		debug : function() {
			return; //去掉注释窗口
			
			 if (!$.debugDiv) {
				 var obj = document.createElement("div");
				 obj.style.position = "absolute";
				 obj.style.left = "50px";
				 obj.style.top = "50px";
				 document.body.appendChild(obj);
				 
				 var text = document.createElement("textarea");
				 text.rows = 10;
				 text.rols = 40;
				 obj.appendChild(text);
				 
				 var btn = document.createElement("button");
				 btn.innerHTML = "close";
				 btn.onclick = function() {
					 obj.style.display = "none";
				 };
				 obj.appendChild(btn);
				 $.debugDiv = obj;
				 $.debugTextArea = text;
			 }
			 var msgAdd = "";
			 for (var i = 0; i < arguments.length; i++) {
				 msgAdd += "," + arguments[i];
			 }
			 $.debugTextArea.value += "\n" + msgAdd;
			 $.debugDiv.style.display = "";
		},
		/**
		 * 转换成数字类型
		 * 
		 * @param {} num (12px)
		 * @return 数字
		 */
		getInt : function(num) {
			num += "";
			num = num.replace(/px/, "");
			var newNum = parseInt(num, 10);
			return isNaN(newNum) ? 0 : newNum;
		},
		/**
		 * 继承
		 */
		extend : function() {
			/**
			 * 复制对象所有信息
			 * 
			 * @param {} obj 对象
			 */
			var getPro = function(obj) {
				for ( var newPor in obj) {
					this[newPor] = obj[newPor];
				}
			};
			// object构造函数
			var objCon = Object.prototype.constructor;
			return function(obj1, obj2, obj3) {
				if (typeof obj2 == "object") {
					obj3 = obj2;
					obj2 = obj1;
					obj1 = obj3.constructor!=objCon ? obj3.constructor : function(){obj2.apply(this, arguments);}
				}
				var obj1copy = function() {};
				var obj;
				var obj2copy = obj2.prototype;
				obj1copy.prototype = obj2copy;
				obj = obj1.prototype = new obj1copy();
				obj.constructor = obj1;
				obj1.superclass = obj2copy;
				if (obj2copy.constructor == objCon) {
					obj2copy.constructor = obj2;
				}
				obj.override = getPro;
				$.override(obj1, obj3);
				return obj1;
			}
		}(),
		/**
		 * 方法重写
		 * 
		 * @param {} childObj
		 * @param {} parentObj
		 */
		override : function(childObj, parentObj) {
			if (parentObj) {
				// 子类原型
				var parPor = childObj.prototype;
				for ( var por in parentObj) {
					parPor[por] = parentObj[por];
				}
				if ($.isIE && parentObj.toString != childObj.toString) {
					parPor.toString = parentObj.toString;
				}
			}
		},
		/**
		 * 创建命名空间对象
		 * 
		 * @return obj
		 */
		ns : function() {
			for ( var i = 0; i < arguments.length; i++) {
				// 参数值 Gef.commands
				var par = arguments[i];
				var parVal = par.split(".");
				// Gef
				var obj = window[parVal[0]] = window[parVal[0]] || {};
				// parVal[1]的值
				var childObj = parVal.slice(1);

				for ( var k = 0; k < childObj.length; k++) {
					var pro = childObj[k];
					obj = obj[pro] = obj[pro] || {};
				}
			}
			return obj;
		},
		/**
		 * 应用某一对象的一个方法，用另一个对象替换当前对象 (将obj1内的属性值复制给obj)
		 * 
		 * @param {}
		 *            obj
		 * @param {}
		 *            obj1
		 * @param {}
		 *            array
		 * @return obj
		 */
		apply : function(obj, obj1, array) {
			if (array) {
				$.apply(obj, array);
			}
			if (obj && obj1 && typeof obj1 == "object") {
				for ( var pro in obj1) {
					obj[pro] = obj1[pro];
				}
			}
			return obj;
		},
		/**
		 * 应用某一对象的一个方法，用另一个对象替换当前对象 (将obj1内的属性值复制给obj)
		 * 
		 * @param {}
		 *            obj
		 * @param {}
		 *            obj1
		 * @return obj
		 */
		applyIf : function(obj, obj1) {
			if (obj && obj1) {
				for ( var pro in obj1) {
					if (typeof obj[pro] == "undefined") {
						obj[pro] = obj1[pro];
					}
				}
			}
			return obj;
		},
		/**
		 * 连接字符串
		 * 
		 * @param {}
		 *            array
		 * @return {}
		 */
		join : function(array) {
			var str = "";
			for ( var i = 0; i < array.length; i++)
				str += array[i];
			return str;
		},
		/**
		 * 设置显示内容,返回宽高
		 * 
		 * @param {}
		 *            val
		 * @return {w:'',h:''}
		 */
		getTextSize : function(val) {
			if (!$.textDiv) {
				$.textDiv = document.createElement("div");
				$.textDiv.style.position = "absolute";
				$.textDiv.style.fontFamily = "Verdana";
				$.textDiv.style.fontSize = "12px";
				$.textDiv.style.left = "-1000px";
				$.textDiv.style.top = "-1000px";
				document.body.appendChild($.textDiv);
			}

			var dom = $.textDiv;
			dom.innerHTML = val;
			var text = {
				w : Math.max(dom.offsetWidth, dom.clientWidth),
				h : Math.max(dom.offsetHeight, dom.clientHeight)
			};
			return text;
		},
		/**
		 * 判断对象是否存在
		 * 
		 * @param {}
		 *            obj
		 * @return {Boolean}
		 */
		notBlank : function(obj) {
			if (typeof obj == "undefined") {
				return false;
			} else if (typeof obj == "string" && obj.trim().length == 0) {
				return false;
			}
			return true;
		},
		/**
		 * 字符串去除空格
		 * 
		 * @param {}
		 *            str
		 * @return {}
		 */
		safe : function(str) {
			if (str) {
				return str.trim();
			} else {
				return "";
			}
		},
		/**
		 * 获取dom
		 * 
		 * @param {}
		 *            id
		 * @return dom
		 */
		get : function(id) {
			return document.getElementById(id);
		},
		/**
		 * 赋值并返回值
		 * 
		 * @param {}
		 *            id
		 * @param {}
		 *            val
		 * @return val
		 */
		value : function(id, val) {
			var dom = $.get(id);
			if (typeof val != "undefined") {
				dom.value = $.safe(val);
			}
			return $.safe(dom.value);
		},
		/**
		 * 遍历
		 * 
		 * @param {}
		 *            element
		 * @param {}
		 *            fn
		 * @param {}
		 *            val
		 * @return number
		 */
		each : function(element, fn, val) {
			if (typeof element.length == "undefined" || typeof element == "string") {
				element = [ element ];
			}
			for ( var i = 0, k = element.length; i < k; i++) {
				if (fn.call(val || element[i], element[i], i, element) === false) {
					return i;
				}
			}
		},
		/**
		 * 显示消息提醒		 * 
		 * @param {}
		 *            title 消息标题
		 * @param {}
		 *            msg 消息内容
		 */
		showMessage : function(title, msg) {
			alert(msg);
		},
		/**
		 * 判断是否为空(空返回true)
		 * 
		 * @param {}
		 *            str
		 * @return {Boolean}
		 */
		isEmpty : function(str) {
			if (typeof $ == "undefined") {
				return true;
			}
			if (str == null) {
				return true;
			}
			if (typeof str.length != "undefined" && str.length == 0) {
				return true;
			}
			return false;
		},
		/**
		 * 空
		 * 
		 * @param {}
		 *            str
		 * @return {Boolean}
		 */
		notEmpty : function(str) {
			return !this.isEmpty(str);
		}
	};
	(function() {
		// 浏览器信息
		var browserName = navigator.userAgent.toLowerCase();
		var opera = browserName.indexOf("opera") > -1;
		var khtml = (/webkit|khtml/).test(browserName);
		var msie = !opera && browserName.indexOf("msie") > -1;
		var msie7 = !opera && browserName.indexOf("msie 7") > -1;
		var msie8 = !opera && browserName.indexOf("msie 8") > -1;
		var gecko = !khtml && browserName.indexOf("gecko") > -1;
		var IE = msie || msie7 || msie8;
		var other = !IE;
		$.isSafari = khtml;
		$.isIE = msie;
		$.isIE7 = msie7;
		$.isGecko = gecko;
		$.isVml = IE;
		$.isSvg = other;
		if (IE) {
			$.installVml();
		}
		/**
		 * Array对象添加方法
		 */
		$.applyIf(Array.prototype, {
			indexOf : function(obj) {
				for ( var i = 0, count = this.length; i < count; i++) {

					if (this[i] === obj) {
						return i;
					}
				}
				return -1;
			},
			remove : function(obj) {
				var index = this.indexOf(obj);
				if (index != -1) {
					this.splice(index, 1);
				}
				return this;
			}
		});
		/**
		 * String 原型加去除空格方法
		 */
		String.prototype.trim = function() {
			var reg = /^\s+|\s+$/g;
			return function() {
				return this.replace(reg, "");
			}
		}();
	})();
	return $;
}
Gef = createCore("Gef");
Gef.IMAGE_ROOT = "gef/images/activities/48/";
/**
 * 默认工作台
 */
Gef.ns("Gef.ui");
Gef.ui.WorkbenchWindow = Gef.extend(Object, {
	getActivePage : Gef.emptyFn
});
/**
 * 默认工作页面
 */
Gef.ns("Gef.ui");
Gef.ui.WorkbenchPage = Gef.extend(Object, {
	getWorkbenchWindow : Gef.emptyFn,
	getActiveEditor : Gef.emptyFn,
	openEditor : Gef.emptyFn
});
/**
 * 
 */
Gef.ns("Gef.ui");
Gef.ui.WorkbenchPart = Gef.extend(Object, {
	setWorkbenchPage : Gef.emptyFn,
	getWorkbenchPage : Gef.emptyFn
});
/**
 * 目前暂无子类
 */
Gef.ns("Gef.ui");
Gef.ui.ViewPart = Gef.extend(Object, {});
/**
 * 
 */
Gef.ns("Gef.ui");
Gef.ui.EditorInput = Gef.extend(Object, {
	getName : Gef.emptyFn,
	getObject : Gef.emptyFn
});
/**
 * 命令基类
 */
Gef.ns("Gef.commands");
Gef.commands.Command = Gef.extend(Object, {
	execute : Gef.emptyFn,
	undo : Gef.emptyFn,
	redo : Gef.emptyFn
});
/**
 * 命令堆栈（记录命令执行顺序，实现重复和撤消功能）
 */
Gef.ns("Gef.commands");
Gef.commands.CommandStack = Gef.extend(Object, {
	constructor : function() {
		this.undoList = [];
		this.redoList = [];
		this.maxUndoLength = 100;
	},
	/**
	 * 
	 * @param {}
	 *            obj
	 * @return {} obj
	 */
	execute : function(obj) {
		
		while (this.undoList.length > this.maxUndoLength) {
			this.undoList.shift();
		}
		this.undoList.push(obj);
		this.redoList.splice(0, this.redoList.length);
		obj.execute();
		return obj;
	},
	/**
	 * 重复
	 * 
	 * @return {Boolean}
	 */
	redo : function() {
		var list = this.redoList.pop();
		if (list != null) {
			this.undoList.push(list);
			list.redo();
			return this.redoList.length > 0;
		}
		return false;
	},
	/**
	 * 恢复
	 * 
	 * @return {Boolean}
	 */
	undo : function() {
		
		var list = this.undoList.pop();
		if (list != null) {
			while (this.redoList.length > this.maxUndoLength) {
				this.redoList.shift();
			}
			this.redoList.push(list);
			list.undo();
			return this.undoList.length > 0;
		}
		return false;
	},
	/**
	 * 
	 */
	flush : function() {
		this.flushUndo();
		this.flushRedo();
	},
	/**
	 * 
	 */
	flushUndo : function() {
		this.undoList.splice(0, this.undoList.length);
	},
	/**
	 * 
	 */
	flushRedo : function() {
		this.redoList.splice(0, this.redoList.length);
	},
	/**
	 * @return {int}
	 */
	getMaxUndoLength : function() {
		return this.maxUndoLength;
	},
	/**
	 * 
	 */
	setMaxUndoLength : function(num) {
		this.maxUndoLength = num;
	},
	/**
	 * @return {Boolean}
	 */
	canUndo : function() {
		return this.undoList.length > 0;
	},
	/**
	 * @return {Boolean}
	 */
	canRedo : function() {
		return this.redoList.length > 0;
	}
});
/**
 * 图形基类
 */
Gef.ns("Gef.figure");
Gef.figure.Figure = Gef.extend(Object, {
	constructor : function(obj) {
		this.children = [];
		obj = obj ? obj : {};
		obj["fill"] = obj["fill"] || "";
		obj["strok"] = obj["strok"] || "black";
		obj["strokwidth"] = obj["strokwidth"] || 1;
		Gef.apply(this, obj);
	},
	setParent : function(parent) {
		this.parent = parent;
	},
	getParent : function() {
		return this.parent;
	},
	getParentEl : function() {
		return this.parent.el;
	},
	getChildren : function() {
		return this.children;
	},
	addChild : function(child) {
		this.children.push(child);
		child.setParent(this);
	},
	removeChild : function(el) {
		el.remove();
	},
	render : function() {
		if (!this.el) {
			if (Gef.isVml) {
				this.renderVml();
				this.onRenderVml();
			} else {
				this.renderSvg();
				this.onRenderSvg();
			}
		}
		for ( var i = 0; i < this.children.length; i++) {
			this.children[i].render();
		}
	},
	renderSvg : Gef.emptyFn,
	renderVml : Gef.emptyFn,
	onRenderVml : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.style.position = "absolute";
		this.el.style.cursor = "pointer";
		this.el.fillcolor = this["fill"];
		this.el.strokecolor = this["stroke"];
		this.el.strokeweight = this["strokewidth"];
		this.getParentEl().appendChild(this.el);
	},
	onRenderSvg : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.setAttribute("fill", this["fill"]);
		this.el.setAttribute("stroke", this["stroke"]);
		this.el.setAttribute("stroke-width", this["strokewidth"]);
		this.el.setAttribute("cursor", "pointer");
		this.getParentEl().appendChild(this.el);
	},
	getId : function() {
		return this.el.getAttribute("id");
	},
	remove : function() {
		this.parent.getChildren().remove(this);
		this.getParentEl().removeChild(this.el);
	},
	show : function() {
		this.el.style.display = "";
	},
	hide : function() {
		this.el.style.display = "none";
	},
	moveTo : Gef.emptyFn,
	update : Gef.emptyFn
});
/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.EditPartFactory = Gef.extend(Object, {
	createEditPart : Gef.emptyFn
});
/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.ModelFactory = Gef.extend(Object, {
	createModel : Gef.emptyFn
});
/**
 * 无子类继承
 */
Gef.ns("Gef.gef");
Gef.gef.EditDomain = Gef.extend(Object, {
	constructor : function() {
		
		this.commandStack = new Gef.commands.CommandStack();
		this.editPartRegistry = {};
		this.model2EditPart = {};
		this.figure2EditPart = {};
	},
	getCommandStack : function() {		
		return this.commandStack;
	},
	setEditor : function(editor) {
		this.editor = editor;
	},
	createEditPart : function(model) {
		
		var id = model.getId();
		var modelType = model.getType();
		var editPart = this.editor.getEditPartFactory().createEditPart(modelType);
		this.editPartRegistry[id] = editPart;
		editPart.setModel(model);
		this.registerModel(editPart);
		return editPart;
	},
	findOrCreateEditPart : function(model) {
		var id = model.getId();
		var modelType = model.getType();
		var editPart = this.editPartRegistry[id];
		if (!editPart) {
			editPart = this.createEditPart(model);
		}
		return editPart;
	},
	registerModel : function(editPart) {
		var model = editPart.getModel();
		var id = model.getId();
		if (this.model2EditPart[id] != null) {
			this.model2EditPart[id] = editPart;
		}
	},
	findModelByEditPart : function(model) {
		var id = model.getId();
		return this.model2EditPart[id];
	},
	removeModelByEditPart : function(editPart) {
		var model = editPart.getModel();
		var id = model.getId();
		if (this.model2EditPart[id] != null) {
			this.model2EditPart[id] = null;
			delete this.model2EditPart[id];
		}
	},
	registerFigure : function(figure) {
		var fi = figure.getFigure();
		var id = fi.getId();
		if (this.figure2EditPart[id] != null) {
			this.figure2EditPart[id] = figure;
		}
	},
	findFigureByEditPart : function(model) {
		var id = model.getId();
		return this.figure2EditPart[id];
	},
	removeFigureByEditPart : function(editPart) {
		var figure = editPart.getFigure(), id = figure.getId();
		if (this.figure2EditPart[id] != null) {
			this.figure2EditPart[id] = null;
			delete this.figure2EditPart[id];
		}
	}
});
/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.EditPartViewer = Gef.extend(Object, {
	getContents : Gef.emptyFn,
	setContents : Gef.emptyFn,
	getRootEditPart : Gef.emptyFn,
	setRootEditPart : Gef.emptyFn,
	getEditDomain : Gef.emptyFn,
	setEditDomain : Gef.emptyFn
});
/**
 * 
 */
Gef.ns("Gef.gef");
Gef.gef.EditPart = Gef.extend(Object, {
	getModel : Gef.emptyFn,
	getFigure : Gef.emptyFn
});
/**
 * 
 */
Gef.ns("Gef.gef.support");
Gef.gef.support.PaletteHelper = Gef.extend(Object, {
	getSource : Gef.emptyFn,
	render : Gef.emptyFn,
	getPaletteConfig : Gef.emptyFn
});
/**
 * 监听各种事件
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.BrowserListener = Gef.extend(Object, {
	constructor : function(graphicalViewer) {
		// Gef.gef.GraphicalViewer
		this.graphicalViewer = graphicalViewer;
		this.selectionManager = new Gef.gef.tracker.SelectionManager(this);
		this.enabled = true;
		this.dragging = false;
		this.activeTracker = null;
		this.initTrackers();
		this.initEvents();
	},
	/**
	 * 初始化跟踪
	 */
	initTrackers : function() {
		this.trackers = [];
		if (Gef.editable !== false) {
			this.trackers.push(new Gef.gef.tracker.KeyPressRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.DirectEditRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.ToolTracker(this));
			this.trackers.push(new Gef.gef.tracker.CreateNodeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.CreateEdgeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.ResizeNodeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.ResizeEdgeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.MoveEdgeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.MoveNodeRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.MoveTextRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.MarqueeRequestTracker(this))
		}
		this.selectionRequestTracker = new Gef.gef.tracker.SelectionRequestTracker(this);
		this.selectionListenerTracker = new Gef.gef.tracker.SelectionListenerTracker(this);
	},
	/**
	 * 初始化事件
	 */
	initEvents : function() {
		this.initMouseDownEvent();
		this.initMouseMoveEvent();
		this.initMouseUpEvent();
		this.initDoubleClickEvent();
		this.initKeyDownEvent();
		this.initKeyUpEvent();
	},
	/**
	 * 鼠标单击事件
	 */
	initMouseDownEvent : function() {
		var _this = this;
		var downFn = function(ev) {
			var objEvent = Gef.isIE ? event : ev;
			_this.mouseDown(objEvent);

		};
		if (Gef.isIE) {
			document.attachEvent("onmousedown", downFn);
		} else {
			document.addEventListener("mousedown", downFn, false);
		}
	},
	/**
	 * 鼠标移动事件
	 */
	initMouseMoveEvent : function() {
		var _this = this;
		var moveFn = function(ev) {
			var objEvent = Gef.isIE ? event : ev;
			_this.mouseMove(objEvent);

		};
		if (Gef.isIE) {
			document.attachEvent("onmousemove", moveFn);
		} else {

			document.addEventListener("mousemove", moveFn, false);
		}
	},
	/**
	 * 鼠标抬起
	 */
	initMouseUpEvent : function() {
		var _this = this;
		var upFn = function(ev) {

			var objEvent = Gef.isIE ? event : ev;

			_this.mouseUp(objEvent);
		};
		if (Gef.isIE) {
			document.attachEvent("onmouseup", upFn);
		} else {
			document.addEventListener("mouseup", upFn, false);
		}
	},
	/**
	 * 双击
	 */
	initDoubleClickEvent : function() {

		var _this = this;
		var doubleClickFn = function(ev) {
			var objEvent = Gef.isIE ? event : ev;
			_this.doubleClick(objEvent);
		};
		if (Gef.isIE) {
			document.attachEvent("ondblclick", doubleClickFn);
		} else {
			document.addEventListener("dblclick", doubleClickFn, false);
		}
	},
	/**
	 * 键盘按下
	 */
	initKeyDownEvent : function() {

		var _this = this;
		var keyDownFn = function(ev) {
			var objEvent = Gef.isIE ? event : ev;
			_this.keyDown(objEvent);
		};
		if (Gef.isIE) {
			document.attachEvent("onkeydown", keyDownFn);
		} else {
			document.addEventListener("keydown", keyDownFn, false)
		}
	},
	/**
	 * 鼠标抬起
	 */
	initKeyUpEvent : function() {
		var _this = this;
		var keyUpFn = function(ev) {
			var objEvent = Gef.isIE ? event : ev;
			_this.keyUp(objEvent);
		};
		if (Gef.isIE) {
			document.attachEvent("onkeyup", keyUpFn);
		} else {
			document.addEventListener("keyup", keyUpFn, false);
		}
	},
	/**
	 * 触发事件
	 * 
	 * @param {}
	 *            事件名称eventName
	 * @param {}
	 *            A
	 */
	fireEvent : function(eventName, ev) {

		if (this.enabled !== true) {
			return;
		}
		var xy = this.getXY(ev);
		var target = this.getTarget(ev);
		var obj = {
			e : ev,
			eventName : eventName,
			point : xy,
			target : target
		};

		try {

			if (this.selectionRequestTracker.understandRequest(obj)) {
				this.selectionRequestTracker.processRequest(obj);
			}
		} catch (e) {
			Gef.error(e, "select");
		}

		try {
			if (this.activeTracker == null) {
				Gef.each(this.trackers, function(requestTracker) {
					var bool = !requestTracker.understandRequest(obj);
					return bool;
				}, this);
			}
			if (this.activeTracker != null) {
				var bo = this.activeTracker.processRequest(obj);
				if (bo) {
					this.stopEvent(ev);
				}
			}
		} catch (e) {
			Gef.error(e, "fireEvent");
		}
		try {

			if (this.selectionListenerTracker.understandRequest(obj)) {
				this.selectionListenerTracker.processRequest(obj);
			}
		} catch (e) {
			Gef.error(e, "selectlistener");
		}
	},
	mouseDown : function(fn) {
		this.fireEvent("MOUSE_DOWN", fn);
	},
	mouseMove : function(fn) {
		this.fireEvent("MOUSE_MOVE", fn);
	},
	mouseUp : function(fn) {
		this.fireEvent("MOUSE_UP", fn);
	},
	doubleClick : function(fn) {
		this.fireEvent("DBL_CLICK", fn);
	},
	keyDown : function(fn) {
		this.fireEvent("KEY_DOWN", fn);
	},
	keyUp : function(fn) {
		this.fireEvent("KEY_UP", fn);
	},
	getXY : function(ev) {
		var obj = {};
		if (typeof window.pageYOffset != "undefined") {
			obj.x = window.pageXOffset;
			obj.y = window.pageYOffset;
		} else if (typeof document.compatMode != "undefined" && document.compatMode != "BackCompat") {
			obj.x = document.documentElement.scrollLeft;
			obj.y = document.documentElement.scrollTop;
		} else if (typeof document.body != "undefined") {
			obj.x = document.body.scrollLeft;
			obj.y = document.body.scrollTop;
		}
		var canvas = this.graphicalViewer.getCanvasLocation();
		var X = ev.clientX + obj.x;
		var Y = ev.clientY + obj.y;
		return {
			x : X - canvas.x,
			y : Y - canvas.y,
			absoluteX : X,
			absoluteY : Y
		}
	},
	getTarget : function(ev) {
		return Gef.isIE ? ev.srcElement : ev.target;
	},
	/**
	 * 阻止默认事件传递
	 * 
	 * @param {}
	 *            ev
	 */
	stopEvent : function(ev) {
		// if (Gef.isIE) {
		// ev.returnValue = false;
		// } else {
		// ev.preventDefault();
		// }
	},
	getViewer : function() {
		return this.graphicalViewer;
	},
	getSelectionManager : function() {
		return this.selectionManager;
	},
	disable : function() {
		this.enabled = false;
	},
	enable : function() {
		this.enabled = true;
	}
});
/**
 * 
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.RequestTracker = Gef.extend(Object, {
	understandRequest : Gef.emptyFn,
	processRequest : Gef.emptyFn
});
/**
 * 选择管理器
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionManager = Gef.extend(Object, {
	constructor : function(browserListener) {
		this.items = [];
		this.handles = {};
		this.browserListener = browserListener;
	},
	addSelectedConnection : function(editPart) {
		if (this.selectedConnection) {
			this.removeSelectedConnection(this.selectedConnection);
		}
		this.resizeEdgeHandle = new Gef.figure.ResizeEdgeHandle();
		this.resizeEdgeHandle.edge = editPart.getFigure();
		this.addHandle(this.resizeEdgeHandle);
		this.resizeEdgeHandle.render();
		this.selectedConnection = editPart;
	},
	removeSelectedConnection : function(obj) {
		this.resizeEdgeHandle.remove();
		this.selectedConnection = null;
		this.resizeEdgeHandle = null;
	},
	addSelectedNode : function(node, obj) {
		if (this.items.length == 1 && this.items[0] == node) {
			return false;
		}
		if (!obj) {
			this.clearAll();
		}
		var bool = this.items.indexOf(node) != -1;
		if (bool) {
			if (obj) {
				this.removeSelectedNode(node, obj);
				return false;
			}
		} else {
			this.items.push(node);
			this.createNodeHandle(node);
		}
		return true;
	},
	removeSelectedNode : function(node, $) {
		var bool = this.items.indexOf(node) != -1;
		if (bool) {
			this.items.remove(node);
			this.removeNodeHandle(node);
		}
	},
	clearAll : function() {
		Gef.each(this.items, function(node) {
			this.removeNodeHandle(node);
		}, this);
		this.items = [];
		if (this.selectedConnection != null) {
			this.removeSelectedConnection(this.selectedEdge);
		}
		this.hideDraggingText();
	},
	selectAll : function() {
		this.clearAll();
		Gef.each(this.getNodes(), function(node) {
			this.addSelectedNode(node.editPart, true)
		}, this);
	},
	selectIn : function(model) {
		this.clearAll();
		Gef.each(this.getNodes(), function(node) {
			var _node = node;
			var _nodeX = _node.x + _node.w / 2;
			var _nodeY = _node.y + _node.h / 2;
			// 判断页面画框的时候是否框住了图元
			if (_nodeX > model.x && _nodeX < model.x + model.w && _nodeY > model.y && _nodeY < model.y + model.h) {
				this.addSelectedNode(node.editPart, true);
			}
		}, this);
	},
	/**
	 * 创建页面图元选中时边线
	 * 
	 * @param {}
	 *            node
	 * @return {}
	 */
	createNodeHandle : function(node) {
		var id = node.getModel().getId();
		var nodeHandle = this.handles[id];
		if (!nodeHandle) {
			nodeHandle = new Gef.figure.ResizeNodeHandle();
			this.handles[id] = nodeHandle;
			nodeHandle.node = node.getFigure();
			this.addHandle(nodeHandle);
			nodeHandle.render();
		}
		return nodeHandle;
	},
	findNodeHandle : function(node) {
		var id = node.getModel().getId();
		var nodeHandle = this.handles[id];
		return nodeHandle;
	},
	removeNodeHandle : function(node) {
		var id = node.getModel().getId();
		var nodeHandle = this.handles[id];
		if (nodeHandle != null) {
			nodeHandle.remove();
			this.handles[id] = null;
			delete this.handles[id];
		}
		return nodeHandle;
	},
	refreshHandles : function() {
		for ( var obj in this.handles) {
			var handle = this.handles[obj];
			handle.refresh();
		}
		if (this.resizeEdgeHandle) {
			this.resizeEdgeHandle.refresh();
		}
	},
	addHandle : function(handle) {
		var handleDom = this.browserListener.getViewer().getLayer("LAYER_HANDLE");
		handleDom.addChild(handle);
	},
	addDragging : function(dragging) {
		var draggingDom = this.browserListener.getViewer().getLayer("LAYER_DRAGGING");
		draggingDom.addChild(dragging);
	},
	getNodes : function() {
		var nodeDom = this.browserListener.getViewer().getLayer("LAYER_NODE");
		
		return nodeDom.getChildren();
	},
	getSelectedNodes : function() {
		return this.items;
	},
	getSelectedCount : function() {
		return this.items.length;
	},
	getSelectedConnection : function() {
		return this.selectedConnection;
	},
	getDefaultSelected : function() {
		return this.browserListener.getViewer().getContents();
	},
	getCurrentSelected : function() {
		if (this.selectedConnection) {
			return [ this.selectedConnection ];
		} else if (this.items.length > 0) {
			return this.items;
		} else {
			return [ this.getDefaultSelected() ]
		}
	},
	getDraggingText : function(obj) {
		if (!this.draggingText) {
			this.draggingText = new Gef.figure.DraggingTextFigure(obj);
			this.addDragging(this.draggingText);
			this.draggingText.render();
		}
		this.draggingText.edge = obj;
		this.draggingText.show();
		return this.draggingText;
	},
	hideDraggingText : function() {
		if (this.draggingText) {
			this.draggingText.hide();
		}
	}
});
/**
 * 
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionListener = Gef.extend(Object, {
	selectionChanged : Gef.emptyFn
});
Gef.ns("Gef.tool");
Gef.tool.AbstractTool = Gef.extend(Object, {
	constructor : function(obj) {
		Gef.apply(this, obj ? obj : {});
	},
	needCheckOutgo : function() {
		return true;
	},
	getKey : function() {
		return "abstractTool";
	},
	getId : function(node) {
		if (node) {
			this.node = node;
			this.id = node.getId() + ":" + this.getKey();
		}
		return this.id;
	},
	render : function(parentDom, dom) {
		if (Gef.isVml) {
			this.renderVml(parentDom, dom);
		} else {
			this.renderSvg(parentDom, dom);
		}
	},
	renderVml : Gef.emptyFn,
	renderSvg : Gef.emptyFn,
	resize : function(w, h, x, y) {
		if (Gef.isVml) {
			this.resizeVml(w, h, x, y);
		} else {
			this.resizeSvg(w, h, x, y);
		}
	},
	resizeVml : Gef.emptyFn,
	resizeSvg : Gef.emptyFn,
	isClicked : function(ev) {
		if (Gef.isVml) {
			return this.isClickedVml(ev);
		} else {
			return this.isClickedSvg(ev);
		}
	},
	isClickedVml : Gef.emptyFn,
	isClickedSvg : Gef.emptyFn,
	drag : Gef.emptyFn,
	move : Gef.emptyFn,
	drop : Gef.emptyFn
});
Gef.ns("Gef.gef.xml");
Gef.gef.xml.XmlDeserializer = Gef.extend(Object, {
	constructor : function(docXML) {
		this.xdoc = Gef.model.XmlUtil.readXml(docXML);

	},
	decodeNodeModel : function(nodeModel, nodeDom, rootModel) {
		nodeModel.decode(nodeDom, [ "transition" ]);
		Gef.model.JpdlUtil.decodeNodePosition(nodeModel);
		this.modelMap[nodeModel.text] = nodeModel;
		this.domMap[nodeModel.text] = nodeDom;
		rootModel.addChild(nodeModel);
	}
});
Gef.ns("Gef.model");
Gef.model.Model = Gef.extend(Object, {
	constructor : function(obj) {
		this.listeners = [];
		obj = obj ? obj : {};
		Gef.apply(this, obj);
		this.createDom();
	},
	createDom : function() {

		this.dom = new Gef.model.Dom(this.getTagName());

	},

	setTagName : function(tagName) {
		this.dom.tagName = tagName;
	},
	getTagName : function() {
		return this.type;
	},
	addChangeListener : function(obj) {
		this.listeners.push(obj);
	},
	removeChangeListener : function(obj) {
		this.listeners.remove(obj);
	},
	notify : function(str, obj) {
		for ( var i = 0; i < this.listeners.length; i++) {

			this.listeners[i].notifyChanged(str, obj);
		}
	},
	getId : function() {
		if (this.id == null) {
			this.id = Gef.id();
		}
		return this.id;
	},
	getType : function() {

		if (this.type == null) {
			this.type = "node";
		}
		return this.type;
	},
	/**
	 * 页面连线切换连线类型
	 * 
	 * @return {}
	 */
	setType : function(lineType) {
		this.type = lineType;
	},
	/**
	 * 判断右侧属性进入哪个表单
	 * 
	 * @return {}
	 */
	getForm : function() {

		if (this.form == null) {
			this.form = "process";
		}
		return this.form;
	},
	getEditPart : function() {
		return this.editPart;
	},
	setEditPart : function(editPart) {
		this.editPart = editPart;
	}
});
/**
 * 暂无子类
 */
Gef.ns("Gef.model");
Gef.model.ModelChangeListener = Gef.extend(Object, {
	notifyChanged : Gef.emptyFn
});
Gef.ns("Gef.model");
Gef.model.Dom = Gef.extend(Object, {
	constructor : function(str) {
		if (typeof str != "string" || Gef.isEmpty(str)) {
			("Dom must specify a exist tagName");
			return;
		}
		this.tagName = str;
		this.value = null;
		this.parent = null;
		this.step = "";
		this.attributes = {};
		this.elements = [];
	},
	setAttribute : function(attr, val) {

		if (Gef.notEmpty(val)) {
			this.attributes[attr] = val;
		} else {
			this.removeAttribute(attr);
		}
	},
	removeAttribute : function(attr) {
		delete this.attributes[attr];
	},
	hasAttribute : function(attr) {
		var val = this.attributes[attr];
		return Gef.notEmpty(val);
	},
	getAttribute : function(attr) {
		if (this.hasAttribute(attr)) {
			return this.attributes[attr];
		} else {
			return ""
		}
	},
	addElement : function(element) {
		element.updateStep(this.step);
		this.elements.push(element);
	},
	removeElement : function(element) {
		this.elements.remove(element);
	},
	getElementContent : function(tagName) {
		var element = this.getElementByTagName(tagName);
		if (element) {
			return element.value;
		} else {
			return "";
		}
	},
	setElementContent : function(tagName, val) {

		var element = this.getElementByTagName(tagName);
		if (element) {
			if (Gef.notEmpty(val))
				element.value = val;
			else
				this.elements.remove(element);
		} else if (Gef.notEmpty(val)) {
			element = new Gef.model.Dom(tagName);
			element.value = val;
			this.addElement(element);
		}
	},
	getElementAttribute : function(tagName, val) {
		var element = this.getElementByTagName(tagName);
		if (element && element.hasAttribute(val)) {
			return element.getAttribute(val);
		} else {
			return "";
		}
	},
	setElementAttribute : function(tagName, attr, val) {

		var element = this.getElementByTagName(tagName);
		if (element)
			element.setAttribute(attr, val);
		else {
			element = new Gef.model.Dom(tagName);
			element.setAttribute(attr, val);
			this.addElement(element);
		}
	},
	getElementByTagName : function(tagName) {
		for ( var i = 0; i < this.elements.length; i++) {
			var element = this.elements[i];
			if (element.tagName == tagName) {
				return element;
			}
		}
		return null;
	},
	getElementsByTagName : function(tagName) {
		var list = [];
		for ( var i = 0; i < this.elements.length; i++) {
			var element = this.elements[i];
			if (element.tagName == tagName) {
				list.push(element);
			}
			;
		}
		return list;
	},
	getProperty : function(elementName, val) {
		var str = "";
		var B = this.getElementsByTagName("property");
		Gef.each(B, function(B) {
			if (B.getAttribute("name") == elementName) {
				Gef.each(B.elements, function(elementName) {
					if (val == "boolean") {
						str = (elementName.tagName === "true");
					} else if (val == elementName.tagName) {
						str = elementName.getAttribute("value");
					}
					return false;
				});
				return false;
			}
		});
		return str;
	},
	setProperty : function(eleName, str, element) {
		if (Gef.isEmpty(str)) {
			return;
		}
		var F = false;
		var obj = null;
		var list = this.getElementsByTagName("property");
		Gef.each(list, function(dom) {
			if (dom.getAttribute("name") == eleName) {
				obj = dom;
				return false;
			}
		});
		if (obj == null) {
			obj = new Gef.model.Dom("property");
			obj.setAttribute("name", eleName);
			this.addElement(obj)
		}
		if (element == "boolean") {
			for ( var i = obj.elements.length - 1; i >= 0; i--) {
				var _el = obj.elements[i];
				obj.elements.remove(_el);
			}
			var bool = (str == true) ? "true" : "false";
			obj.addElement(new Gef.model.Dom(bool));
		} else {
			obj.setElementAttribute(element, "value", str);
		}
		;
	},
	removeProperty : function(elementName) {
		var list = this.getElementsByTagName("property");
		Gef.each(list, function(el) {
			if (el.getAttribute("name") == elementName) {
				this.elements.remove(el);
				return false;
			}
		}, this);
	},
	updateStep : function(obj) {
		for ( var i = 0; i < obj.length + 2; i++) {
			this.step += " ";
		}
		;
	},
	encode : function(str, xmlStr) {
		if (Gef.notEmpty(this.value) && (Gef.notEmpty(str) || Gef.notEmpty(this.elements))) {
			("can not set insert xml into TextNode");
			return;
		}
		if (Gef.isEmpty(str)) {
			str = "";
		}
		var stepStr = xmlStr ? xmlStr : "";
		xmlStr = stepStr + this.step;
		var tagStr = [ xmlStr, "<", this.tagName ];
		for ( var obj in this.attributes) {
			var attr = this.attributes[obj];
			tagStr.push(" ", obj, "='", this.encodeText(attr), "'");
		}
		if (Gef.isEmpty(this.elements) && Gef.isEmpty(str) && Gef.isEmpty(this.value)) {
			tagStr.push("/>");
		} else if (Gef.notEmpty(this.value)) {
			tagStr.push(">", this.encodeText(this.value), "</", this.tagName, ">");
		} else {
			tagStr.push(">\n");
			// for (var i = 0; i < this.elements.length; i++) {
			// var element = this.elements[i];
			// var elEncode = element.encode("", stepStr);
			// tagStr.push(elEncode);
			// }
			tagStr.push(str);
			tagStr.push(xmlStr, "</", this.tagName, ">")
		}
		tagStr.push("\n");
		return tagStr.join("");
	},

	encodeText : function(str) {
		if (typeof str != "") {
			str += "";
		}
		return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\'/g, "&apos;").replace(/\"/g, "&quot;");
	},
	decode : function(dom, list) {
		list = list ? list : [];
		if (typeof dom == "string") {
			var A = Gef.model.XmlUtil.readXml(xml);
			dom = A.documentElement;
		}
		this.tagName = dom.nodeName;
		for ( var i = 0; i < dom.attributes.length; i++) {
			var attr = dom.attributes[i];
			this.setAttribute(attr.name, attr.nodeValue);
		}
		if (dom.childNodes.length == 1 && dom.childNodes[0].nodeType == 3)
			this.value = dom.childNodes[0].nodeValue;
		else {
			var elements = Gef.model.XmlUtil.elements(dom);
			for (i = 0; i < elements.length; i++) {
				var elObj = elements[i];
				if (list.indexOf(elObj.tagName) != -1) {
					continue;
				}
				var el = new Gef.model.Dom("node");
				el.decode(elObj);
				this.addElement(el);
			}
		}
	}
});