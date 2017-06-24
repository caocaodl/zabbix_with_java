/**
 * ���ͼԪ ��ק�¼�
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.CreateNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_CREATE_NODE : "DRAGGING_CREATE_NODE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if ($.eventName != "MOUSE_DOWN" || !this.isInPalette($.point))
					return false;
				var _ = this.getPaletteConfig($);
				if (_ == null || _.creatable === false)
					return false;
				this.paletteConfig = _;
				this.status = this.DRAGGING_CREATE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var A = this.paletteConfig, $ = A.w, C = A.h;
				if (isNaN($) || $ < 0)
					$ = 48;
				if (isNaN(C) || C < 0)
					C = 48;
				var D = $ * -1, B = C * -1;
				this.getDraggingRect().update(D, B, $, C)
			},
			move : function(A) {
				var $ = this.getDraggingRect(), _ = A.point, C = _.x - $.w / 2, B = _.y
						- $.h / 2;
				$.moveTo(C, B)
			},
			drop : function(_) {
				if (this.isInCanvas(_.point)) {
					var $ = this.getDraggingRect(), A = this.paletteConfig.text;
					var id = this.paletteConfig.id;
					_.role = {
						name : "CREATE_NODE",
						rect : {
							x : _.point.x - $.w / 2,
							y : _.point.y - $.h / 2,
							w : $.w,
							h : $.h
						},
						node : this.getModelFactory().createModel(A)
					};
					var node = _.role.node;
					if (node) {
						node.dom.setAttribute("id", id);
					}
					
					this.executeCommand(this.getTargetEditPart(), _)

				}
				this.reset();

			},
			reset : function() {
				Gef.gef.tracker.CreateNodeRequestTracker.superclass.reset
						.call(this);
				this.paletteConfig = null;
				if (this.browserListener.getViewer().rendered) {
					var $ = this.getDraggingRect(), A = $.w * -1, _ = $.h * -1;
					$.moveTo(A, _)
				}
			}
		});

Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.CreateEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_CREATE_EDGE : "DRAGGING_CREATE_EDGE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas($.point) || this.notConnection()
						|| $.eventName != "MOUSE_DOWN")
					return false;
				var _ = this.findEditPartAt($);
				if (_ == null || _.getClass() != "node" || !_.canCreateOutgo())
					return false;
				this.temp.editPart = _;
				this.status = this.DRAGGING_CREATE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.getDraggingEdge().update(-1, -1, -1, -1)
			},
			move : function(B) {
				var A = B.point, $ = this.temp.editPart.getFigure(), C = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, _ = this.getDraggingEdge();
				_.updateForDragging(C, A)
			},
			drop : function(A) {
				var _ = this.getDraggingEdge(), D = this.temp.editPart, B = this
						.findEditPartAt(A);
				if (D != B && B.getClass() == "node" && B.canCreateIncome(D)) {
					var $ = this.getViewer().getActivePalette().text, C = this
							.getModelFactory().createModel($);
					if (D.getOutgoingConnections().length > 0)
						C.text = "to " + B.getModel().text;
					else
						C.text = "";
					A.role = {
						name : "CREATE_EDGE",
						rect : {
							x1 : _.x1,
							y1 : _.y1,
							x2 : _.x2,
							y2 : _.y2
						},
						source : D.getModel(),
						target : B.getModel(),
						model : C
					};
					this.executeCommand(this.temp.editPart, A)
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.CreateEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingEdge().moveToHide()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_NODE : "DRAGGING_MOVE_NODE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false;
				}
				if (!this.isInCanvas($.point) || this.isConnection())
					return false;
				if ($.eventName != "MOUSE_DOWN")
					return false;
				var _ = this.findEditPartAt($);
				if (_ == null || _.getClass() != "node")
					return false;
				this.temp = {
					x : $.point.x,
					y : $.point.y,
					editPart : _
				};
				this.status = this.DRAGGING_MOVE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN") {
					this.drag($);
				} else if ($.eventName == "MOUSE_MOVE") {
					this.move($);

				} else if ($.eventName == "MOUSE_UP") {
					this.drop($);
				}
				return true;
			},
			drag : function($) {
				Gef.each(this.getSelectedNodes(), function(B) {
							var A = B.getFigure(), _ = A.w, D = A.h, E = A.x
									+ $.point.x - this.temp.x, C = A.y
									+ $.point.y - this.temp.y;
							this.createDraggingRects().update(_ * -1, D * -1,
									_, D)
						}, this)
			},
			move : function($) {// ҳ��ͼԪ�ƶ��¼�
				Gef.each(this.getSelectedNodes(), function(C, A) {
					var _ = this.getDraggingRects(A), B = C.getFigure(), E = B.x
							+ $.point.x - this.temp.x, D = B.y + $.point.y
							- this.temp.y;
					_.moveTo(E, D);
				}, this)
			},
			drop : function(A) {
				var $ = this.getDraggingRect(), _ = [];
				Gef.each(this.getSelectedNodes(), function($) {
							_.push($.getModel())
						});
				if (A.point.x != this.temp.x || A.point.y != this.temp.y) {
					A.role = {
						name : "MOVE_NODE",
						nodes : _,
						dx : A.point.x - this.temp.x,
						dy : A.point.y - this.temp.y
					};
					this.executeCommand(this.getContents(), A);
					this.getSelectionManager().refreshHandles();
				}
				this.reset();
				// �޸��Ҳ��������ֵ
				var pro = {
					w : _[0].w,
					h : _[0].h,
					x : _[0].x,
					y : _[0].y
				}

				var _rightPanel = App.propertyManager.getRight();
				_rightPanel.setVal(pro);
			},
			reset : function() {
				Gef.gef.tracker.MoveNodeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.removeDraggingRects()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_EDGE : "DRAGGING_MOVE_EDGE",
			understandRequest : function(C) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(C.point))
					return false;
				if (C.eventName != "MOUSE_DOWN")
					return false;
				var D = C.target;
				if (!D.id.indexOf(":"))
					return false;
				var E = D.id.split(":"), A = E[0], B = E[1];
				if (B != "start" && B != "end")
					return false;
				var _ = this.getConnectionByConnectionId(A);
				if (_ == null)
					return false;
				var $ = this.getSelectionManager().resizeEdgeHandle;

				if ($ == null)
					return false;
				this.temp = {
					editPart : _.editPart,
					handle : $,
					direction : B
				};
				this.status = this.DRAGGING_MOVE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return true
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return false
			},
			drag : function(C) {
				var B = C.point, _ = this.temp.direction, D = this.temp.editPart, $ = null, E = {};
				if (_ == "start")
					$ = D.getTarget().getFigure();
				else
					$ = D.getSource().getFigure();
				var E = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = this.getDraggingEdge();
				A.updateForMove(D.getFigure(), _, B)
			},
			move : function(C) {
				var B = C.point, _ = this.temp.direction, D = this.temp.editPart, $ = null, E = {};
				if (_ == "start")
					$ = D.target.figure;
				else
					$ = D.source.figure;
				var E = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = this.getDraggingEdge();
				A.updateForMove(D.getFigure(), _, B)
			},
			drop : function(D) {
				var C = this.getDraggingEdge(), H = this.findEditPartAt(D), A = this.temp.editPart;
				if (H.getClass() == "node") {
					var B = this.temp.direction;
					if ((B == "start" && H.canCreateOutgo(A.target))
							|| (B == "end" && H.canCreateIncome(A.source))) {
						var _ = null, F = null;
						if (B == "start") {
							_ = H.getModel();
							F = A.target.getModel()
						} else {
							_ = A.source.getModel();
							F = H.getModel()
						}
						var $ = new Gef.commands.CompoundCommand(), I = this.temp.editPart.model, G = I
								.getType(), E = this.getModelFactory()
								.createModel(G);
						$
								.addCommand(new Gef.gef.command.RemoveConnectionCommand(I));
						$
								.addCommand(new Gef.gef.command.CreateConnectionCommand(
										E, _, F));
						$
								.addCommand(new Gef.gef.command.ResizeConnectionCommand(
										E, [], I.innerPoints));
						this.getCommandStack().execute($);
						this.getSelectionManager()
								.addSelectedConnection(E.editPart)
					}
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.MoveEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered) {
					this.getDraggingEdge().moveToHide();
					this.getSelectionManager().refreshHandles()
				}
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveTextRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_TEXT : "DRAGGING_MOVE_TEXT",
			understandRequest : function(B) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(B.point))
					return false;
				if (B.eventName != "MOUSE_DOWN")
					return false;
				var _ = B.target, A = _.getAttribute("edgeId");
				if (A == null)
					return false;
				if (_.tagName != "text" && _.tagName != "textbox")
					return false;
				var $ = null, $ = this.getConnectionByConnectionId(A);
				if ($ == null)
					return false;
				this.temp = {
					editPart : $.editPart,
					x : B.point.x,
					y : B.point.y
				};
				this.status = this.DRAGGING_MOVE_TEXT;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var $ = this.getDraggingText();
				$.refresh();
				this.temp.oldX = $.edge.textX;
				this.temp.oldY = $.edge.textY
			},
			move : function(B) {
				var A = this.getDraggingText(), $ = B.point.x - this.temp.x, _ = B.point.y
						- this.temp.y;
				A.edge.textX = this.temp.oldX + $;
				A.edge.textY = this.temp.oldY + _;
				A.refresh()
			},
			drop : function(A) {
				var C = this.temp.oldX, B = this.temp.oldY, _ = C + A.point.x
						- this.temp.x, $ = B + A.point.y - this.temp.y;
				A.role = {
					name : "MOVE_TEXT",
					oldTextX : C,
					oldTextY : B,
					newTextX : _,
					newTextY : $,
					edge : this.temp.editPart
				};
				this.executeCommand(this.temp.editPart, A);
				this.reset()
			},
			getDraggingText : function() {
				var $ = this.temp.editPart.getFigure();
				return this.getSelectionManager().getDraggingText($)
			},
			reset : function() {
				Gef.gef.tracker.MoveTextRequestTracker.superclass.reset
						.call(this)
			}
		});
/**
 * ͼ�α任��С
 */
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ResizeNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_RESIZE_NODE : "DRAGGING_RESIZE_NODE",
			understandRequest : function(C) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(C.point))
					return false;
				if (C.eventName != "MOUSE_DOWN")
					return false;
				var D = C.target;
				if (D.id.indexOf(":") == -1)
					return false;
				var F = D.id.split(":"), _ = F[0], B = F[1], $ = this
						.getNodeByNodeId(_);
				if ($ == null)
					return false;
				else if (!$.editPart.canResize())
					return false;
				var E = this.getSelectionManager().handles, A = E[$.editPart
						.getModel().getId()];
				if (A == null)
					return false;
				this.temp = {
					editPart : $.editPart,
					handle : A,
					direction : B
				};
				this.status = this.DRAGGING_RESIZE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var A = this.temp.editPart.figure, $ = this.temp.direction;
				if ($ == "n") {
					this.temp.x = A.x + A.w / 2;
					this.temp.y = A.y
				} else if ($ == "s") {
					this.temp.x = A.x + A.w / 2;
					this.temp.y = A.y + A.h
				} else if ($ == "w") {
					this.temp.x = A.x;
					this.temp.y = A.y + A.h / 2
				} else if ($ == "e") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y + A.h / 2
				} else if ($ == "nw") {
					this.temp.x = A.x;
					this.temp.y = A.y
				} else if ($ == "ne") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y
				} else if ($ == "sw") {
					this.temp.x = A.x;
					this.temp.y = A.y + A.h
				} else if ($ == "se") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y + A.h
				}
				this.getDraggingRect().update(A.x, A.y, A.w, A.h)
			},
			move : function(G) {
				var H = G.point, F = this.temp.editPart.getFigure(), A = this.temp.direction, J = F.x, I = F.y, D = F.w, C = F.h, $ = H.x
						- this.temp.x, _ = H.y - this.temp.y;
				if (A == "n") {
					I = I + _;
					C = C - _
				} else if (A == "s")
					C = C + _;
				else if (A == "w") {
					J = J + $;
					D = D - $
				} else if (A == "e")
					D = D + $;
				else if (A == "nw") {
					J = J + $;
					D = D - $;
					I = I + _;
					C = C - _
				} else if (A == "ne") {
					D = D + $;
					I = I + _;
					C = C - _
				} else if (A == "sw") {
					J = J + $;
					D = D - $;
					C = C + _
				} else if (A == "se") {
					D = D + $;
					C = C + _
				}
				var B = {
					x : J,
					y : I,
					w : D,
					h : C
				};
				this.temp.rect = B;
				var E = this.getDraggingRect();
				E.update(B.x, B.y, B.w, B.h)
			},
			drop : function(A) {
				var _ = this.getDraggingRect(), B = this.temp.editPart, E = this.temp.rect.x, D = this.temp.rect.y, $ = this.temp.rect.w, C = this.temp.rect.h;
				if ($ < 0)
					$ = 5;
				if (C < 0)
					C = 5;
				A.role = {
					name : "RESIZE_NODE",
					rect : {
						x : E,
						y : D,
						w : $,
						h : C
					},
					node : B.getModel()
				};
				this.executeCommand(B, A);
				this.temp.handle.refresh();
				this.reset();
				// �޸��Ҳ��������ֵ
				var pro = {
					w : $,
					h : C,
					x : E,
					y : D
				}

				var _rightPanel = App.propertyManager.getRight();
				_rightPanel.setVal(pro);

			},
			reset : function() {
				Gef.gef.tracker.ResizeNodeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingRect().update(-1, -1, 1, 1)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ResizeEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_RESIZE_EDGE : "DRAGGING_RESIZE_EDGE",
			understandRequest : function(J) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(J.point))
					return false;
				if (J.eventName != "MOUSE_DOWN")
					return false;
				var K = J.target, F = K.id;
				if (F == null || F.indexOf(":middle:") == -1)
					return false;
				var I = F.substring(0, F.indexOf(":middle:")), _ = this
						.getConnectionByConnectionId(I);
				if (_ == null)
					return false;
				var $ = F.substring(F.indexOf(":middle:") + ":middle:".length)
						.split(","), C = parseInt($[0], 10), G = parseInt($[1],
						10), D = this.getSelectionManager().resizeEdgeHandle, A = [];
				Gef.each(_.innerPoints, function($) {
							A.push([$[0], $[1]])
						});
				var H = null, E = null, B = null;
				if (C == G) {
					H = _.innerPoints[C];
					if (C == 0)
						E = [_.x1, _.y1];
					else
						E = _.innerPoints[C - 1];
					if (G == _.innerPoints.length - 1)
						B = [_.x2, _.y2];
					else
						B = _.innerPoints[C + 1]
				} else {
					if (C == -1)
						E = [_.x1, _.y1];
					else
						E = _.innerPoints[C];
					if (G >= _.innerPoints.length)
						B = [_.x2, _.y2];
					else
						B = _.innerPoints[G];
					H = [(E[0] + B[0]) / 2, (E[1] + B[1]) / 2];
					_.innerPoints.splice(C + 1, 0, H);
					D.modify()
				}
				this.temp = {
					editPart : _.editPart,
					point : H,
					x : H[0],
					y : H[1],
					oldX : J.point.x,
					oldY : J.point.y,
					prevIndex : C,
					nextIndex : G,
					prev : E,
					next : B,
					oldInnerPoints : A
				};
				this.status = this.DRAGGING_RESIZE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.getSelectionManager().hideDraggingText()
			},
			move : function(A) {
				var $ = A.point.x - this.temp.oldX, _ = A.point.y
						- this.temp.oldY;
				this.temp.point[0] = this.temp.x + $;
				this.temp.point[1] = this.temp.y + _;
				var B = this.getSelectionManager().resizeEdgeHandle;
				if (B)
					B.modify();
				else
					this.reset()
			},
			drop : function($) {
				var _ = this.temp.editPart;
				if (this
						.isSameLine($.point.x, $.point.y, this.temp.prev[0],
								this.temp.prev[1], this.temp.next[0],
								this.temp.next[1]))
					_.getFigure().innerPoints.splice(this.temp.nextIndex, 1);
				$.role = {
					name : "RESIZE_EDGE",
					rect : {
						x : _.figure.x,
						y : _.figure.y,
						w : _.figure.w,
						h : _.figure.h
					},
					oldInnerPoints : this.temp.oldInnerPoints,
					newInnerPoints : _.getFigure().innerPoints
				};
				this.executeCommand(_, $);
				this.reset()
			},
			isSameLine : function(E, _, F, A, C, B) {
				var J = F - E, I = A - _, K = C - E, H = B - _, L = J * K + I
						* H, G = Math.sqrt((J * J + I * I) * (K * K + H * H)), $ = L
						/ G, D = Math.acos($) * 180 / Math.PI;
				return D > 170
			},
			reset : function() {
				Gef.gef.tracker.ResizeEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getSelectionManager().refreshHandles()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MarqueeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MARQUEE : "DRAGGING_MARQUEE",
			understandRequest : function(A) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(A.point))
					return false;
				if (A.eventName != "MOUSE_DOWN")
					return false;
				var B = this.findEditPartAt(A);
				if (B != this.getContents())
					return false;
				var _ = A.target;
				if (Gef.isVml && _.tagName == "DIV") {
					if (_.firstChild && _.firstChild.tagName == "DIV") {
						var $ = _.firstChild.getAttribute("id");
						if ($ != null && $.indexOf("_Gef_") != -1) {
							this.status = this.DRAGGING_MARQUEE;
							this.browserListener.activeTracker = this;
							return true
						}
					}
				} else if (Gef.isSvg && _.tagName == "svg") {
					this.status = this.DRAGGING_MARQUEE;
					this.browserListener.activeTracker = this;
					return true
				} else if (Gef.isSvg && _.tagName == "DIV" && _.firstChild
						&& _.firstChild.tagName == "svg") {
					this.status = this.DRAGGING_MARQUEE;
					this.browserListener.activeTracker = this;
					return true
				}
				return false
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return true
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return false
			},
			drag : function(_) {
				var $ = _.point;
				this.getDraggingRect().update($.x, $.y, 0, 0)
			},
			move : function(A) {
				var $ = this.getDraggingRect(), _ = A.point;
				$.update($.x, $.y, _.x - $.x, _.y - $.y)
			},
			drop : function(_) {
				var A = this.getDraggingRect(), $ = {
					x : _.point.x < A.x ? _.point.x : A.x,
					y : _.point.y < A.y ? _.point.y : A.y,
					w : A.w,
					h : A.h
				};
				this.getSelectionManager().selectIn($);
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.MarqueeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingRect().update(-90, -90, 90, 50)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.DirectEditRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function($) {
				if (this.status != "NONE")
					this.reset();
				if (!this.isInCanvas($.point) || $.eventName != "DBL_CLICK")
					return false;
				if ($.target.tagName != "text" && $.target.tagName != "textbox")
					return false;
				this.status = "EDIT_START";
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function(_) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if (_.eventName == "KEY_DOWN") {
					var $ = _.e.keyCode;
					if ($ == 10 || $ == 13)
						this.status = "EDIT_COMPLETE";
					if ($ == 27)
						this.status = "EDIT_CANCEL"
				}
				if (_.eventName == "MOUSE_DOWN" && _.target.tagName != "INPUT")
					if (this.status == "ALREADY_START_EDIT") {
						_.editType = "EDIT_COMPLETE";
						this.status = "EDIT_COMPLETE"
					}
				if (this.status == "EDIT_START")
					this.startEdit(_);
				else if (this.status == "EDIT_COMPLETE")
					this.completeEdit(_);
				else if (this.status == "EDIT_CANCEL")
					this.cancelEdit(_);
				return false
			},
			startEdit : function(A) {
				var B = this.findEditPartAt(A);
				if (B.getClass() == "node") {
					if (B.getFigure().updateAndShowText != null) {
						this.getTextEditor().showForNode(B.getFigure());
						this.temp.editPart = B;
						this.status = "ALREADY_START_EDIT"
					} else
						this.status = "NONE"
				} else if (this.isText(A.target)) {
					var _ = A.target.getAttribute("edgeId"), $ = this
							.getConnectionByConnectionId(_);
					this.getTextEditor().showForEdge($);
					this.temp.editPart = $.editPart;
					this.status = "ALREADY_START_EDIT"
				}
			},
			completeEdit : function(A) {
				if (!this.temp.editPart)
					return;
				var B = this.temp.editPart, $ = this.getTextEditor().getValue();
				if ($ != B.getModel().name) {
					A.role = {
						name : "EDIT_TEXT",
						text : $
					};
					this.executeCommand(B, A)
				}
				var _ = this.getSelectionManager().draggingText;
				if (_)
					_.refresh();
				this.reset()
			},
			cancelEdit : function($) {
				this.reset()
			},
			isText : function($) {
				return (Gef.isVml && $.tagName == "textbox")
						|| (Gef.isSvg && $.tagName == "text")
			},
			getTextEditor : function() {
				if (!this.textEditor) {
					var A = this.browserListener.getViewer()
							.getCanvasLocation(), _ = A.x, $ = A.y;
					this.textEditor = new Gef.figure.TextEditor(_, $)
				}
				A = this.browserListener.getViewer().getCanvasLocation();
				this.textEditor.baseX = A.x;
				this.textEditor.baseY = A.y;
				this.textEditor.show();
				return this.textEditor
			},
			reset : function() {
				Gef.gef.tracker.DirectEditRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getTextEditor().hide()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function(_) {
				if (this.status != "NONE") {
					this.reset();
				}
				var $ = _.eventName == "MOUSE_DOWN" || _.eventName == "KEY_UP";
				if ($) {
					this.status = "SELECT";
				}

				return $;
			},
			processRequest : function(B) {
				if (this.status == "NONE") {
					this.reset();
					return false;
				}
				if (B.eventName != "MOUSE_DOWN" && B.eventName != "KEY_UP") {
					this.reset();
					return false;
				}
				var C = this.findEditPartAt(B);
				if (this.notMultiSelect(B)) {
					var A = this.getSelectedNodes();
					if (A.length > 1 && A.indexOf(C) != -1)
						return false
				}
				if (C.getClass() == "process")
					;
				else if (C.getClass() == "node") {
					var _ = this.addSelected(C, this.isMultiSelect(B));
					if (_) {
						var $ = this.createNodeHandle(C);
						$.refresh()
					}
				} else if (C.getClass() == "connection") {
					this.clearAll();
					this.addSelectedEdge(C);
				}
				return false
			},
			addSelectedEdge : function($) {
				this.getSelectionManager().addSelectedConnection($)
			},
			removeSelectedEdge : function($) {
				this.getSelectionManager().removeSelectedConnection($)
			},
			addSelected : function(_, $) {
				return this.getSelectionManager().addSelectedNode(_, $)
			},
			removeSelected : function(_, $) {
				this.getSelectionManager().removeSelectedNode(_, $)
			},
			clearAll : function() {
				this.getSelectionManager().clearAll()
			},
			selectAll : function() {
				this.getSelectionManager().selectAll()
			},
			selectIn : function($) {
				this.getSelectionManager().selectIn($)
			},
			createNodeHandle : function($) {
				return this.getSelectionManager().createNodeHandle($)
			},
			removeNodeHandle : function($) {
				return this.getSelectionManager.removeNodeHandle($)
			},
			refreshHandles : function() {
				this.getSelectionManager.refreshHandles()
			},
			reset : function() {
				this.status = "NONE"
			}
		});

Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionListenerTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function($) {
				return $.eventName == "MOUSE_UP" || $.eventName == "KEY_DOWN"
			},
			processRequest : function(B) {
				var $ = this.getSelectionManager();
				if (!this.previousSelected)
					this.previousSelected = [$.getDefaultSelected()];
				var A = $.getCurrentSelected(), _ = $.getDefaultSelected(), C = false;
				if (this.previousSelected.length == A.length) {
					for (var D = 0; D < A.length; D++)
						if (A[D] != this.previousSelected[D]) {
							C = true;
							break
						}
				} else
					C = true;
				if (C === true) {
					Gef.each(this.getSelectionListeners(), function($) {
								$.selectionChanged(A, this.previousSelected, _)
							}, this);
					this.previousSelected = A
				}
				return false
			},
			getSelectionListeners : function() {
				if (!this.selectionListeners)
					this.selectionListeners = [];
				return this.selectionListeners
			},
			addSelectionListener : function($) {
				this.getSelectionListeners().push($)
			}
		});

Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ToolTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			isTool : function(_) {
				var $ = false, A = null;
				Gef.each(this.getSelectedNodes(), function(B) {
							Gef.each(B.getFigure().getTools(), function(B) {
										if (B.isClicked(_)) {
											$ = true;
											A = B;
											return false
										}
										if ($ === true)
											return false
									})
						});
				if ($ === true)
					this.selectedTool = A;
				return $
			},
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if ($.editType != null || $.draggingType != null)
					return false;
				if ($.eventName != "MOUSE_DOWN")
					return false;
				if (!this.isTool($))
					return false;
				var _ = this.getSelectedNodes()[0];
				if (this.selectedTool.needCheckOutgo() && !_.canCreateOutgo())
					return false;
				this.status = "TOOL_SELECTED";
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.selectedTool.drag(this, $)
			},
			move : function($) {
				this.selectedTool.move(this, $)
			},
			drop : function($) {
				this.selectedTool.drop(this, $);
				this.reset()
			}
		});

Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.KeyPressRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			KEY_PRESS : "KEY_PRESS",
			understandRequest : function(_) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (_.target.tagName == "INPUT"
						|| _.target.tagName == "TEXTAREA")
					return false;
				if (_.eventName != "KEY_DOWN")
					return false;
				try {
					this.temp = {
						x : 0,
						y : 0
					};
					this.status = this.KEY_PRESS;
					this.browserListener.activeTracker = this;
					return true
				} catch ($) {
					Gef.error($, "key press");
					return false
				}
			},
			processRequest : function(_) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if (_.eventName == "KEY_DOWN") {
					var $ = _.e.keyCode;
					if ($ == 37 || $ == 38 || $ == 39 || $ == 40)
						this.move(_);
					if ($ == 46) {
						this.status = "REMOVE";
						this.removeAll(_);
						this.reset()
					}
					if (_.e.ctrlKey && $ == 65) {
						this.status = "SELECT_ALL";
						this.selectAllNodes(_);
						this.reset()
					}
				} else if (_.eventName == "KEY_UP") {
					$ = _.e.keyCode;
					if ($ == 37 || $ == 38 || $ == 39 || $ == 40)
						this.drop(_)
				}
				this.browserListener.activeTracker = null;
				return true
			},
			move : function(A) {
				var $ = 0, _ = 0;
				switch (A.e.keyCode) {
					case 38 :
						_ = -1;
						break;
					case 40 :
						_ = 1;
						break;
					case 37 :
						$ = -1;
						break;
					case 39 :
						$ = 1;
						break
				}
				this.temp.x += $;
				this.temp.y += _;
				Gef.each(this.getSelectedNodes(), function(D, A) {
							var C = D.getFigure();
							try {
								var F = C.x + $, E = C.y + _;
								C.moveTo(F, E)
							} catch (B) {
								Gef.error(B, "move key press")
							}
						}, this);
				this.getSelectionManager().refreshHandles()
			},
			drop : function(_) {
				var $ = [];
				Gef.each(this.getSelectedNodes(), function(A) {
							var _ = A.getModel();
							$.push(_)
						});
				if (this.temp.x != 0 || this.temp.y != 0) {
					_.role = {
						name : "MOVE_NODE",
						nodes : $,
						dx : this.temp.x,
						dy : this.temp.y
					};
					this.executeCommand(this.getContents(), _)
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.KeyPressRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.removeDraggingRects()
			},
			removeAll : function(B) {
				try {
					var $ = this.getSelectionManager(), _ = $.selectedConnection, A = $.items;
					if (_ != null) {
						B.role = {
							name : "REMOVE_EDGE"
						};
						this.executeCommand(_, B);
						$.removeSelectedConnection()
					} else if (A.length > 0) {
						B.role = {
							name : "REMOVE_NODES",
							nodes : A
						};
						this.executeCommand(
								this.browserListener.graphicalViewer
										.getContents(), B);
						$.clearAll()
					}
				} catch (C) {
					Gef.error(C, "removeAll")
				}
			},
			selectAllNodes : function($) {
				this.getSelectionManager().selectAll()
			}
		});
