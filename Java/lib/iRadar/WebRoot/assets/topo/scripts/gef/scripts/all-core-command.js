/**
 * 复合命令
 */
Gef.ns("Gef.commands");
Gef.commands.CompoundCommand = Gef.extend(Gef.commands.Command, {
			constructor : function() {
				this.commandList = [];
			},
			addCommand : function(command) {
				this.commandList.push(command);
			},
			getCommandList : function() {
				return this.commandList;
			},
			execute : function() {
				for (var i = 0; i < this.commandList.length; i++) {
					this.commandList[i].execute();
				}
			},
			undo : function() {
				for (var i = this.commandList.length - 1; i >= 0; i--) {
					this.commandList[i].undo();
				}
			},
			redo : function() {
				for (var i = 0; i < this.commandList.length; i++) {
					this.commandList[i].redo();
				}
			}
		});
/**
 * 创建节点命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.CreateNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $, A) {

				this.childNode = _;
				this.parentNode = $;
				this.rect = A
			},
			execute : function() {
				this.childNode.x = this.rect.x;
				this.childNode.y = this.rect.y;
				this.childNode.w = this.rect.w;
				this.childNode.h = this.rect.h;
				this.redo();
			},
			redo : function() {
				this.parentNode.addChild(this.childNode)
			},
			undo : function() {
				this.parentNode.removeChild(this.childNode)
			}
		});
/**
 * 创建连线
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.CreateConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(model, source, target) {
				this.connection = model;
				this.sourceNode = source;
				this.targetNode = target;
			},
			execute : function() {

				this.connection.setSource(this.sourceNode);
				this.connection.setTarget(this.targetNode);
				this.redo();
			},
			redo : function() {

				this.connection.reconnect();
			},
			undo : function() {
				this.connection.disconnect();
			}
		});
/**
 * 节点移动命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, _) {
				this.node = $;
				this.rect = _
			},
			execute : function() {
				this.oldX = this.node.x;
				this.oldY = this.node.y;
				this.newX = this.rect.x;
				this.newY = this.rect.y;
				this.redo()
			},
			redo : function() {
				this.node.moveTo(this.newX, this.newY)
			},
			undo : function() {
				this.node.moveTo(this.oldX, this.oldY)
			}
		});
/**
 * 连接线移动命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, A, $) {
				this.connection = _;
				this.sourceNode = A;
				this.targetNode = $
			},
			execute : function() {

				this.oldSourceNode = this.connection.getSource();
				this.oldTargetNode = this.connection.getTarget();
				this.newSourceNode = this.sourceNode;
				this.newTargetNode = this.targetNode;
				this.redo()
			},
			redo : function() {

				this.connection.setSource(this.newSourceNode);
				this.connection.setTarget(this.newTargetNode);
				this.connection.reconnect()
			},
			undo : function() {

				this.connection.setSource(this.oldSourceNode);
				this.connection.setTarget(this.oldTargetNode);
				this.connection.reconnect()
			}
		});
/**
 * 节点调整大小命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ResizeNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, _) {
				this.node = $;
				this.rect = _;
			},
			execute : function() {
				this.oldX = this.node.x;
				this.oldY = this.node.y;
				this.oldW = this.node.w;
				this.oldH = this.node.h;
				this.newX = this.rect.x;
				this.newY = this.rect.y;
				this.newW = this.rect.w;
				this.newH = this.rect.h;
				this.redo()
			},
			redo : function() {
				//禁止改变大小
				// this.node.resize(this.newX, this.newY, this.newW, this.newH);
				this.node.moveTo(this.newX, this.newY);
				// this.node.resize(this.newX, this.newY, this.newW, this.newH)
			},
			undo : function() {
				this.node.resize(this.oldX, this.oldY, this.oldW, this.oldH)
			}
		});
/**
 * 连接线调整命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ResizeConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, A, _) {
				this.connection = $;
				this.oldInnerPoints = A;
				this.newInnerPoints = _
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection.resizeConnection(this.newInnerPoints)
			},
			undo : function() {

				this.connection.resizeConnection(this.oldInnerPoints)
			}
		});
/**
 * 节点删除命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.RemoveNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($) {
				this.node = $;
				this.parentNode = $.getParent()
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.node.removeForParent()
			},
			undo : function() {
				var _ = this.node, $ = this.parentNode;
				$.addChild(_)
			}
		});
/**
 * 连接线删除命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.RemoveConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($) {
				this.connection = $;
				this.sourceNode = $.getSource();
				this.targetNode = $.getTarget()
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection.disconnect()
			},
			undo : function() {
				this.connection.reconnect()
			}
		});
/**
 * 文字移动命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveTextCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, C, B, A, $) {
				this.connection = _;
				this.oldTextX = C;
				this.oldTextY = B;
				this.newTextX = A;
				this.newTextY = $
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection
						.updateTextPosition(this.newTextX, this.newTextY)
			},
			undo : function() {
				this.connection
						.updateTextPosition(this.oldTextX, this.oldTextY)
			}
		});
/**
 * 文字编辑命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.EditTextCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $) {
				this.model = _;
				this.oldText = _.name;
				this.newText = $
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.model.updateText(this.newText)
			},
			undo : function() {
				this.model.updateText(this.oldText)
			}
		});
/**
 * 整体移动
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveAllCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(A, $, _) {
				this.dx = $;
				this.dy = _;
				this.nodes = [];
				Gef.each(A, function($) {
							if (this.nodes.indexOf($) == -1)
								this.nodes.push($)
						}, this);
				var B = [];
				Gef.each(this.nodes, function($) {
							Gef.each($.getOutgoingConnections(), function($) {
										Gef.each(A, function(_) {
													if ($.getTarget() == _)
														B.push($)
												})
									})
						});
				this.connections = B
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				var A = this.nodes, $ = this.dx, _ = this.dy;
				Gef.each(A, function(A) {

							A.moveTo(A.x + $, A.y + _)
						});
				Gef.each(this.connections, function(A) {
							var B = A.innerPoints;
							Gef.each(B, function(A) {
										A[0] += $;
										A[1] += _
									});
							A.resizeConnection(B)
						})
			},
			undo : function() {
				var A = this.nodes, $ = this.dx, _ = this.dy;
				Gef.each(A, function(A) {
							A.moveTo(A.x - $, A.y - _)
						});
				Gef.each(this.connections, function(A) {
							var B = A.innerPoints;
							Gef.each(B, function(A) {
										A[0] -= $;
										A[1] -= _
									});
							A.resizeConnection(B)
						})
			}
		});
/**
 * 更改节点类型命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ChangeNodeTypeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $) {
				this.oldModel = _;
				this.newModel = $;
				this.text = _.text;
				this.dom = _.dom
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.oldModel.w = this.newModel.w;
				this.oldModel.h = this.newModel.h;
				this.oldModel.dom = this.newModel.dom;
				this.oldModel.updateText(this.newModel.text);
				this.oldModel.resize(this.oldModel.x, this.oldModel.y,
						this.oldModel.w, this.oldModel.h)
			},
			undo : function() {
				this.newModel.w = this.w;
				this.newModel.h = this.h;
				this.newModel.dom = this.dom;
				this.newModel.updateText(this.text);
				this.newModel.resize(this.newModel.x, this.newModel.y,
						this.newModel.w, this.newModel.h)
			}
		});
/**
 * 更改背景图片命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ChangeBgCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(bgObj) {
				this.bgObj = bgObj;
			},
			execute : function() {
				$("#_gef_jbs_center_bg").attr("src", this.bgObj.newSrc).css({
							width : this.bgObj.newW,
							height : this.bgObj.newH
						});
				this.updateData(App.systemBgId, this.bgObj.newId);

			},
			redo : function() {
				$("#_gef_jbs_center_bg").attr("src", this.bgObj.newSrc).css({
							width : this.bgObj.newW,
							height : this.bgObj.newH
						});
				this.updateData(App.systemBgId, this.bgObj.newId);
			},
			undo : function() {
				$("#_gef_jbs_center_bg").attr("src", this.bgObj.oldSrc).css({
							width : this.bgObj.oldW,
							height : this.bgObj.oldH
						});
				this.updateData(App.systemBgId, this.bgObj.oldId);
			},
			updateData : function(oldId, newId) {

				// 加载数据库背景
				// Ext.Ajax.request({
				// method : 'post',
				// url : 'getBackground.action',
				// success : function(response) {
				// App.systemBgId = newId;
				// },
				// failure : function(response) {
				// Ext.Msg.alert('系统错误', response.responseText);
				// },
				// params : {// 传递参数
				// oldId : oldId,
				// newId : newId
				// }
				// });
			}
		});
/**
 * 更改背景皮肤命令
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ChangeBgSkinCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(bgObj) {
				this.bgObj = bgObj;
			},
			execute : function() {
				Ext.util.CSS.swapStyleSheet('ext-skin',
						'scripts/ext-3.2.0/resources/css/'
								+ this.bgObj.newSkinName + '.css');
			},
			redo : function() {
				Ext.util.CSS.swapStyleSheet('ext-skin',
						'scripts/ext-3.2.0/resources/css/'
								+ this.bgObj.newSkinName + '.css');
			},
			undo : function() {
				Ext.util.CSS.swapStyleSheet('ext-skin',
						'scripts/ext-3.2.0/resources/css/'
								+ this.bgObj.oldSkinName + '.css');
			}
		});
/**
 * 更改布局 撤销 恢复
 */
Gef.ns("Gef.gef.command");
Gef.gef.command.ChangeLayoutCommand = Gef.extend(Gef.commands.Command, {
			/**
			 * 
			 * @param {}
			 *            commandList
			 * @param {}
			 *            type 布局类型
			 */
			constructor : function(commandList, type) {
				// 布局类型
				this.layoutType = type;
				this.commandList = commandList;
			},
			execute : function() {
				var viewer = Gef.activeEditor.getGraphicalViewer();
				var browserListener = viewer.getBrowserListener();
				var selectionManager = browserListener.getSelectionManager();
				selectionManager.clearAll();
				new Layout(Gef.activeEditor,this.layoutType).doLayout();
			},
			/**
			 * 恢复
			 */
			undo : function() {

				for (var i = 0; i < this.commandList.length; i++) {
					var node = this.commandList[i];
					var x = node.x;
					var y = node.y;
					var w = node.w;
					var h = node.h;
					// 恢复旧数据
					node.resize(node.oldModelSize.oldX, node.oldModelSize.oldY,
							node.oldModelSize.oldW, node.oldModelSize.oldH);

					var outConnections = node.getOutgoingConnections();
					for (var j = 0; j < outConnections.length; j++) {
						var connection = outConnections[j];
						var innerPoint = connection.innerPoints;
						connection.resizeConnection(connection.oldInnerPoints);
						connection.oldInnerPoints = innerPoint;
					}

					node.oldModelSize = {
						oldW : w,
						oldH : h,
						oldX : x,
						oldY : y
					}

				}

			},
			/**
			 * 重做
			 */
			redo : function() {

				for (var i = 0; i < this.commandList.length; i++) {
					var node = this.commandList[i];
					var x = node.x;
					var y = node.y;
					var w = node.w;
					var h = node.h;
					node.resize(node.oldModelSize.oldX, node.oldModelSize.oldY,
							node.oldModelSize.oldW, node.oldModelSize.oldH);

					var outConnections = node.getOutgoingConnections();
					for (var j = 0; j < outConnections.length; j++) {
						var connection = outConnections[j];
						var innerPoint = connection.innerPoints;
						connection.resizeConnection(connection.oldInnerPoints);
						connection.oldInnerPoints = innerPoint;
					}
					node.oldModelSize = {
						oldW : w,
						oldH : h,
						oldX : x,
						oldY : y
					}
				}
			}
		});