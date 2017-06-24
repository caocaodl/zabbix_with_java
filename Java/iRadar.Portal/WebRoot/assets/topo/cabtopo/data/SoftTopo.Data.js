/*
 *机房拓扑数据处理类
 *
 */

SoftTopo.Data = function(graph) {
    this.graph = graph;
    this.graph.name = "机房拓扑";
    this.CABINETNODEWIDTH = 230;
    this.CABINETNODEHEIGHT = 500;
    this.HOSTNODEWIDTH = 200;
    this.HOSTNODEHEIGHT = 18;
    this.nodes = [];
    this.nodeTimer = null;
    this.MILLISEC = 60000;
    this.init();

};
SoftTopo.Data.prototype.init = function() {

    var _this = this;

    function fixNode(node) {
        var g = node.host;
        if (!g) {
            return false;
        }

        var gCanvas = _this.graph.getUIBounds(g),
            nodeCanvas = _this.graph.getUIBounds(node);
            gCanvas.y=gCanvas.y-40;
        if (nodeCanvas.x < gCanvas.x || (nodeCanvas.x + nodeCanvas.width) > (gCanvas.x + gCanvas.width)) {
            return false;
        }
        if (nodeCanvas.y < gCanvas.y || (nodeCanvas.y + nodeCanvas.height) > (gCanvas.y + gCanvas.height)) {
            return false;
        }
        return true;

    };
    (function addListener() {
        var oldLocation = {};
        _this.graph.interactionDispatcher.addListener(function(evt) {
            var data = evt.data;

            if (evt.kind == Q.InteractionEvent.ELEMENT_MOVE_START) {
                oldLocation[data.id] = {
                    x: data.x,
                    y: data.y
                };
                return;
            }
            if (evt.kind == Q.InteractionEvent.ELEMENT_MOVING) {

            }
            if (evt.kind == Q.InteractionEvent.ELEMENT_MOVE_END) {
                var nodeData = data.get("data");

                if (nodeData.hostType != "CABINET") {
                    //判断移动范围
                    var range = fixNode(data);
                    if (!range) {
                        data.location = oldLocation[data.id];
                    } else {

                    }
                } else {

                }
                oldLocation = {};
            }
        });
    })();
    /*工具栏*/
    var toolBarMenu = SoftTopo.App.getToolbarMenu();
    if (toolBarMenu) {
        if (SoftTopo.AppConfig.SIDEBAR) {
            function switchSideBar() {
                var cabtopoMenu = $("#cabtopoMenu");
                if (cabtopoMenu.length) {

                    cabtopoMenu.hasClass("hide") ? $(this).attr("title", "隐藏机房列表").find(".toolbar-sideBar").removeClass("toolbar-sideBar-up").toggleClass("toolbar-sideBar-down") : $(this).attr("title", "显示机房列表").find(".toolbar-sideBar").removeClass("toolbar-sideBar-down").toggleClass("toolbar-sideBar-up");
                    cabtopoMenu.toggleClass("hide");
                }
            }
            var $sidebar = $('<div  title="隐藏机房列表" class="btn btn-default btn-sm"><div class="icon toolbar-sideBar toolbar-sideBar-down"></div></div>');
            $sidebar.click(switchSideBar);
            toolBarMenu.append($sidebar);
        }
    }
};

/**
 * 右键菜单
 **/
SoftTopo.Data.prototype.contextMenu = function() {
    if (!SoftTopo.AppConfig.CONTEXTMENU) {
        return [];
    }
    var _this = this,
        menuArr = [];

    //查看详情
    var hostDetail = {
        text: "查看详情",
        action: function(evt, item) {
            if (item.data && item.data.eventData) {
                var nodeData = item.data.eventData.get("data"),
                    url = SoftTopo.detail_url + "?hostid=" + nodeData.hostId + "&groupid=" + nodeData.groupId + "";
                window.top.$.workspace.openTab(nodeData.name + "详情", window.top.ctxpath + url);
            }
        },
        before: function(graph, data, evt) {

            var _showMenu = false;
            if (data && data.get("data")) {
                var nodeData = data.get("data");
                nodeData.hostId && nodeData.groupId ? _showMenu = true : _showMenu = false;
            }
            return _showMenu;
        },
        scope: _this
    };
    menuArr.push(hostDetail);
    return menuArr;
};
/**
 * 初始化机房列表
 */
SoftTopo.Data.prototype.createToolbox = function(json) {
    var _this = this,
        roomMap = {},
        $ul = $("<ul id='cabtopoMenu' class='accordion'></ul>");

    function getCabList(e) {
        var $this = $(this),
            roomId = e.data.roomInfo.id;
        //多次选择同一个机房不进行处理
        if (!$this.hasClass("open")) {
            //create cab
            _this.getCabListByRoomId(roomId);
            $this.addClass("open").siblings().removeClass("open");
        }
    }
    $.each(json, function(index, val) {
        var showName = val.name;
        showName = showName.length > 10 ? showName.substring(0, 10) : showName;
        var $li = $("<li><div class='link' title=" + val.name + "><i class='fa fa-home'></i>" + showName + "</div></li>").on('click', {
            roomInfo: {
                "id": val.id,
                "name": val.name
            }
        }, getCabList).appendTo($ul);
        if (!index) {
            $li.addClass("open");
            //请求第一个机房机柜
            _this.getCabListByRoomId(val.id);
        };
    });
    $(this.graph.html.parentNode).append($ul);

};
SoftTopo.Data.prototype.getCabListByRoomId = function(roomId) {
    SoftTopo.App.getAjaxData().getCabListByRoomId(roomId);
};
SoftTopo.Data.prototype.createCabList = function(cabInfoList) {
    var _this = this;
    this.destroyTimer();
    this.graph.clear();
    this.nodes.length = 0;
    this.nodes = [];
    //计算节点坐标
    (function calculateCoords() {
        var count = 0,
            nodeWidth = _this.CABINETNODEWIDTH,
            nodeHeight = _this.CABINETNODEHEIGHT,
            margin = 20,
            countWidth = 0,
            countHeight = 0;

        $.each(cabInfoList, function(index, node) {
            if (!node.X && !node.Y) {
                if (!count) {
                    node.X = -540;
                    node.Y = -200;
                    countWidth = node.X;
                    countHeight = node.Y;
                } else {
                    node.X = countWidth + count * (nodeWidth + margin);
                }
                if (count % 3 == 0 && count != 0) {
                    countHeight = countHeight + (nodeHeight + margin);
                    count = 0;
                    countWidth = -540;
                    node.X = countWidth;
                }
                node.Y = countHeight;
                count++;
            }
            //计算设备坐标
            if (node.children && node.children.length) {
                var hostCount = 0,
                    hostMargin = 5,
                    hostLeftMargin = 15,
                    // hostHeight = 16,
                    hostHeight = 18,
                    hostCountHeight = 0;
                $.each(node.children, function(childIndex, host) {
                    if (!host.X && !host.Y) {

                        if (!hostCount) {

                            hostCountHeight = node.Y + hostLeftMargin;
                        } else {
                            hostCountHeight = hostCountHeight + (hostHeight + hostMargin);
                        }
                        //禁止设备Y轴超出机柜Y轴
                        if (hostCountHeight > node.Y + (nodeHeight - (hostHeight + hostMargin))) {
                            hostCountHeight = node.Y + hostMargin;
                        }
                        host.X = node.X + hostLeftMargin;
                        host.Y = hostCountHeight;
                        hostCount++;

                    }
                });
            }
        });

    })();
    $.each(cabInfoList, function(index, node) {
        var newCabNode = _this.createCabNode(node.name, node.X, node.Y);
        newCabNode.set("data", {
            id: node.id,
            name: node.name,
            hostType: node.hostType
        });
        SoftTopo.Util.setIcon(newCabNode, "cab");
        if (node.children && node.children.length) {
            $.each(node.children, function(childIndex, host) {
                var newCabHost = _this.createCabHost(host.name, host.X, host.Y, newCabNode);
                newCabHost.set("data", {
                    id: host.id,
                    name: host.name,
                    hostType: host.hostType,
                    hostId: host.hostId,
                    groupId: host.groupId,
                });
                SoftTopo.Util.setIcon(newCabHost, "cabHost");
            });
        }
    });
    if (this.nodes.length) {
        var nodeIds = [];
        $.each(this.nodes, function(index, val) {
            nodeIds.push(val.get("data").id);
        });

        this.nodeTimer = setTimeout(function runTimer() {
            var nodeGroup = [];

            function _updateAlarmNodes(nodeGroup) {
                SoftTopo.App.getAjaxData().updateAlarmNodes(nodeGroup);
            }
            for (var i = 0; i < nodeIds.length; i++) {
                if (i != 0 && i % 20 === 0) {
                    _updateAlarmNodes(nodeGroup);
                    nodeGroup.length = 0;
                }
                nodeGroup.push(nodeIds[i]);
            }
            if (nodeGroup.length) {
                _updateAlarmNodes(nodeGroup);
            };
            _this.nodeTimer = setTimeout(runTimer, _this.MILLISEC);
        }, this.MILLISEC);

    }
    this.graph.zoomToOverview();
};

SoftTopo.Data.prototype.createCabHost = function(name, left, top, host) {
    var node = this.graph.createNode(name, left, top);
    if (host) {
        node.host = node.parent = host;
    }
    node.size = {
        width: this.HOSTNODEWIDTH,
        height: this.HOSTNODEHEIGHT
    }
    node.anchorPosition = Q.Position.LEFT_TOP;
    node.setStyle(Q.Styles.LABEL_COLOR, "#FFF");
    node.setStyle(Q.Styles.LABEL_FONT_SIZE, 16);
    node.setStyle(Q.Styles.LABEL_ANCHOR_POSITION, Q.Position.CENTER_BOTTOM);
    this.nodes.push(node);
    return node;
};
SoftTopo.Data.prototype.createCabNode = function(name, left, top) {
    var cab = this.graph.createNode(name, left, top);
    cab.size = {
        width: this.CABINETNODEWIDTH,
        height: this.CABINETNODEHEIGHT
    }
    cab.setStyle(Q.Styles.LABEL_FONT_SIZE, 20);
    cab.anchorPosition = Q.Position.LEFT_TOP;
    return cab;
};
/*
 *更新告警
 */
SoftTopo.Data.prototype.updateAlarmNodes = function(json) {

    if (this.nodes.length) {
        $.each(this.nodes, function(index, node) {

            var data = json[node.get("data").id],
                uis = node.bindingUIs;
            if (data) {
                var priority = parseInt(data.priority),
                    description = data.description;
                if (priority) {
                    var color = priority >= 3 ? "#F00" : "#FF0";
                    node.alarmLabel = description;
                    node.alarmColor = color;
                    node.setStyle(Q.Styles.RENDER_COLOR, color);
                } else {
                    node.alarmLabel = null;
                    node.alarmColor = null;
                    node.setStyle(Q.Styles.RENDER_COLOR, "");
                }

            }
        });
    }
};
SoftTopo.Data.prototype.destroyTimer = function() {
    if (this.nodeTimer) {
        clearTimeout(this.nodeTimer);
        this.nodeTimer = null;
    }
}