Plugin.reg("operation", function() {
	var result = {};

	result.funcs = {
		/**
		 * 放大缩小
		 */
		updateZoom : function(zoomType) {
			var model = App.getProcessModel();
			var items = model.getChildren();
			if (items.length == 0) {
				return;
			}

			// 修改页面图元坐标
			if (zoomType == "zoomin") {
				 // 放大
				for ( var i = 0; i < items.length; i++) {
					var item = items[i];
					var ix = item.x + 50;
					var iy = item.y + 50;
					var iw = item.w + 15;
					var ih = item.h + 15;
					item.x = ix;
					item.y = iy;
					item.w = iw;
					item.h = ih;
					item.editPart.figure.x = ix;
					item.editPart.figure.y = iy;
					item.editPart.figure.w = iw;
					item.editPart.figure.h = ih;
					item.editPart.figure.updatePor();
					// 修改选中状态图元边框大小
					var defaultGraphicalViewer = item.editPart.getViewer();
					var sel = defaultGraphicalViewer.getBrowserListener().getSelectionManager();
					if (sel.getSelectedCount() > 0) {
						var seNodes = sel.getSelectedNodes();
						for ( var k = 0; k < seNodes.length; k++) {
							var selNode = seNodes[k].getModel();
							if (selNode.text == item.text) {
								sel.selectIn(selNode);
								// 修改右侧属性面板值
								var pro = {
									w : item.w,
									h : item.h,
									x : item.x,
									y : item.y
								}
								var _rightPanel = App.propertyManager.getRight();
								_rightPanel.setVal(pro);
							}
						}
					}
					// 连线
					var connection = defaultGraphicalViewer.getLayer("LAYER_CONNECTION");
					if (connection.getChildren().length > 0) {
						var conChildren = connection.getChildren();
						for ( var j = 0; j < conChildren.length; j++) {
							var con = conChildren[j];
							// con.updatePor();
							con.refresh();
						}
					}
					var handle = defaultGraphicalViewer.getLayer("LAYER_HANDLE");
					if (handle.getChildren().length > 0) {
						var handleChildren = handle.getChildren();
						for ( var j = 0; j < handleChildren.length; j++) {
							var con = handleChildren[j];
							// con.updatePor();
							con.refresh();
						}
					}
				}

			} else if (zoomType == "zoomout") {
				// 缩小
				// for (var i = 0; i < items.length; i++) {
				// var item = items[i];
				// var ix = item.x - 50;
				// var iy = item.y - 50;
				// var iw = item.w - 15;
				// var ih = item.h - 15;
				// item.x = ix;
				// item.y = iy;
				// item.w = iw;
				// item.h = ih;
				// item.editPart.figure.x = ix;
				// item.editPart.figure.y = iy;
				// item.editPart.figure.w = iw;
				// item.editPart.figure.h = ih;
				// item.editPart.figure.updatePor();
				// // 修改选中状态图元边框大小
				// var defaultGraphicalViewer = item.editPart.getViewer();
				// var sel = defaultGraphicalViewer.getBrowserListener()
				// .getSelectionManager();
				// if (sel.getSelectedCount() > 0) {
				// var seNodes = sel.getSelectedNodes();
				// for (var k = 0; k < seNodes.length; k++) {
				// var selNode = seNodes[k].getModel();
				// if (selNode.text == item.text) {
				// sel.selectIn(selNode);
				// // 修改右侧属性面板值
				// var pro = {
				// w : item.w,
				// h : item.h,
				// x : item.x,
				// y : item.y
				// };
				// var _rightPanel = App.propertyManager.getRight();
				// _rightPanel.setVal(pro);
				// }
				// }
				// }
				// // 连线
				// var connection = defaultGraphicalViewer
				// .getLayer("LAYER_CONNECTION");
				// if (connection.getChildren().length > 0) {
				// var conChildren = connection.getChildren();
				// for (var j = 0; j < conChildren.length; j++) {
				// var con = conChildren[j];
				// // con.updatePor();
				// con.refresh();
				// }
				// }
				// var handle = defaultGraphicalViewer.getLayer("LAYER_HANDLE");
				// if (handle.getChildren().length > 0) {
				// var handleChildren = handle.getChildren();
				// for (var j = 0; j < handleChildren.length; j++) {
				// var con = handleChildren[j];
				// // con.updatePor();
				// con.refresh();
				// }
				// }
				//
				// }

//					// 中心点
//					var cenDom = Gef.get("__gef_jbs_center__");
//					// 宽度
//					var cenDomW = cenDom.offsetWidth;
//					// 高度
//					var cenDomH = cenDom.offsetHeight;
//					// 父元素
//					var parent = cenDom.parentNode.parentNode;
//					var parentW = parent.clientWidth;
//					var parentH = parent.clientHeight;
//					var parentScrollTop = parent.scrollTop;
//					var parentScrollLeft = parent.scrollLeft;
//					
//					// 被遮罩宽度高度
//					var cenDomHiddenW = cenDomW - parentScrollLeft - parentW;
//					var cenDomHiddenH = cenDomH - parentScrollTop - parentH;
//				    
//					// 计算cenDom 可见区域大小
//					var clientW = cenDomW + parentScrollLeft - cenDomHiddenW;
//					var clientH = cenDomH + parentScrollTop - cenDomHiddenH;
//					var left = clientW / 2 + "px";
//					var top = clientH / 2 + "px";
//					var offset = {
//						left : clientW,
//						top : clientH
//					}
//					var obj = document.getElementById("123");
//					if (obj) {
//						cenDom.removeChild(obj);
//					}
//					var div = document.createElement("div");
//					div.setAttribute("id", "123");
//					if (Gef.isIE) {
//						div.style.position = "absolute";
//						div.style.width = "5px";
//						div.style.height = "5px";
//						div.style.backgroundColor = "red";
//						div.style.left = left;
//						div.style.top = top;
//					} else {
//						div.setAttribute("style",
//								"position: absolute;width:5px;height:5px;background-color:red;left:"
//										+ left + ";top:" + top + "");
//					}
//					cenDom.appendChild(div);
	//
//					var result = this.real2view(items[0], offset);
	//
//					items[0].x = result.x;
//					items[0].y = result.y;
//					items[0].editPart.figure.x = result.x;
//					items[0].editPart.figure.y = result.y;
//					items[0].editPart.figure.updatePor();

			}

		},
		real2view : function(r, offset) {

			// // 画布的大小1500*1000
			// var size = fd.canvas.getSize();
			var size = Gef.get("__gef_jbs_center__");
			// 1
			var scaleX = 1; // fd.canvas.scaleOffsetX;
			// // 1
			var scaleY = 1; // fd.canvas.scaleOffsetY;
			// width:1500 height:1000
			var sizeS = {
				width : size.offsetWidth * scaleX,
				height : size.offsetHeight * scaleY
			};
			// x:0 y:0
			var curZero = {
				// 750-750-0
				x : sizeS.width / 2 - size.offsetWidth / 2
						- (offset ? offset.left : 0),
				// 500-500-0
				y : sizeS.height / 2 - size.offsetHeight / 2
						- (offset ? offset.top : 0)
			};
			var result = {
				x : r.x * scaleX - curZero.x,
				y : r.y * scaleY - curZero.y
			};
			if (r.x1) {
				result.x1 = r.x1 * scaleX - curZero.x;
				result.w = result.x1 - result.x;
			}
			if (r.y1) {
				result.y1 = r.y1 * scaleY - curZero.y;
				result.h = result.y1 - result.y;
			}

			return result;
		}
	};

	result.cfg = {
		xtype : "buttongroup",
		title : "比例",

		defaults : {
			scale : "small",
			iconAlign : "top"
		},
		items : [ {
			text : "放大",
			iconCls : "tbar-zoomin",
			handler : function(e) {
				this.updateZoom("zoomin");
			},
			scope : this
		}, {
			text : "缩小",
			iconCls : "tbar-zoomout",
			handler : function() {
				this.updateZoom("zoomout");
			},
			scope : this
		} ]
	};

	return result;
});