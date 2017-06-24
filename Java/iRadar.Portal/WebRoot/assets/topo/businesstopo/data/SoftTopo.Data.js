/*
 *数据处理类
 *
 */
SoftTopo.Data = function(graph) {
    this.graph = graph;
    this.graph.name = "业务拓扑";
    this.nodes = [];
    this.init();
};
SoftTopo.Data.prototype.init = function() {

    /*工具栏*/
    var toolBarMenu = SoftTopo.App.getToolbarMenu(),
        _this = this;
    if (toolBarMenu) {
        if (SoftTopo.AppConfig.SIDEBAR) {
            function switchSideBar() {
                var cabtopoMenu = $("#bs-menu");
                if (cabtopoMenu.length) {

                    cabtopoMenu.hasClass("hide") ? $(this).attr("title", "隐藏业务列表").find(".toolbar-sideBar").removeClass("toolbar-sideBar-up").toggleClass("toolbar-sideBar-down") : $(this).attr("title", "显示业务列表").find(".toolbar-sideBar").removeClass("toolbar-sideBar-down").toggleClass("toolbar-sideBar-up");
                    cabtopoMenu.toggleClass("hide");
                }
            }
            var $sidebar = $('<div  title="隐藏业务列表" class="btn btn-default btn-sm"><div class="icon toolbar-sideBar toolbar-sideBar-down"></div></div>');
            $sidebar.click(switchSideBar);
            toolBarMenu.append($sidebar);
        }
    }
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

                if (nodeData.nodeType == "NODEHOST" || nodeData.nodeType == "NODEBIZNODE") {
                    data.location = oldLocation[data.id];
                } else {

                }
                oldLocation = {};
            }
        });
    })();
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
                    hostType = nodeData.hostType,
                    url = SoftTopo.detail_url;
                if (hostType && hostType.toLowerCase() === "vm") {

                    url += "tenan.action?form=update&templateid=" + nodeData.type + "&hostid=" + nodeData.hostId;
                } else if (hostType && hostType.toLowerCase() === "mysql") {
                    url += "mysqlinfor.action?form=update&templateid=" + nodeData.type + "&applicationid=" + nodeData.hostId + "&hostid=" + nodeData.ownerHost;
                } else if (hostType && hostType.toLowerCase() === "tomcat") {
                    url += "tomcatinfor.action?form=update&templateid=" + nodeData.type + "&applicationid=" + nodeData.hostId + "&hostid=" + nodeData.ownerHost;
                } else {
                    // SoftTopo.detail_url += "tomcatinfor.action?form=update&templateid=3&applicationid=1030&hostid=10188";
                }
            }

            window.top.$.workspace.openTab(nodeData.name + "详情", window.top.ctxpath + url);

        },
        before: function(graph, data, evt) {

            var _showMenu = false;
            if (data && data.get("data")) {
                var nodeData = data.get("data"),
                    hostType = nodeData.hostType;
                if (hostType && hostType.toLowerCase() === "vm") {

                    nodeData.type && nodeData.hostId ? _showMenu = true : _showMenu = false;
                } else if (hostType && (hostType.toLowerCase() === "mysql" || hostType.toLowerCase() === "tomcat")) {
                    nodeData.type && nodeData.hostId && nodeData.ownerHost ? _showMenu = true : _showMenu = false;
                } else {
                    nodeData.hostId && nodeData.groupId ? _showMenu = true : _showMenu = false;
                }
            }
            return _showMenu;
        },
        scope: _this
    };
    menuArr.push(hostDetail);
    return menuArr;
};

/**
 * 获取业务拓扑
 * @parameter 
 **/
SoftTopo.Data.prototype.getBusinessById = function(e) {
    var $this = $(this),
        bsId = e.data.bsInfo.bizTopoId;
    //多次选择同一个业务拓扑不进行处理
    if (!$this.hasClass("open")) {
        SoftTopo.App.getAjaxData().getBusinessById(bsId);
        $this.addClass("open").siblings().removeClass("open");
        //去除其他列表下样式
        $this.parents(".open").siblings().children(".submenu").children("li.open").removeClass("open");
    }
};

/**
 *清空界面
 **/
SoftTopo.Data.prototype.clearAll = function() {
    this.nodes.length = 0;
    this.nodes = [];
    this.graph.clear();
};

/**创建业务列表
 * @parameter json 数据
 **/
SoftTopo.Data.prototype.createToolbox = function(json) {
    if (!$("#bs-menu").length) {
        var _this = this,
            $ul = $("<ul id='bs-menu' class='accordion'></ul>");
        $.each(json, function(index, val) {
            var showName = val.tenantName;
            showName = showName.length > 10 ? showName.substring(0, 10) : showName;
            var $li = $("<li><div class='link' title=" + val.tenantName + "><i class='fa fa-home'></i><span>" + showName + "</span></div></li>");
            if (val.children && val.children.length) {
                var $chUl = $("<ul  class='submenu'></ul>");
                $.each(val.children, function(chIndex, chVal) {
                    var bsInfo = {
                        "tenantId": chVal.tenantId,
                        "bizTopoId": chVal.bizTopoId,
                        "toponame": chVal.toponame
                    };
                    var childShowName = chVal.toponame;
                    childShowName = childShowName.length > 10 ? childShowName.substring(0, 10) : childShowName;
                    $("<li><span title="+chVal.toponame+" >" + childShowName + "</span></li>").on('click', {
                        bsInfo: bsInfo
                    }, _this.getBusinessById).data("data", bsInfo).appendTo($chUl);

                });
                $chUl.appendTo($li);
            }
            $li.appendTo($ul);
        });
        if (json.length) {
            $(this.graph.html.parentNode).append($ul);
            var accordion = new SoftTopo.Accordion($ul, false),
                $firstLi = $ul.first().children().first(),
                $submenuUl = $firstLi.children(".submenu");
            if ($submenuUl.length) {
                $firstLi.children(".link").trigger("click");
                $submenuUl.children("li").first().trigger("click");
            };
        }
    }
};
/**创建 显示数据
 * @parameter json 数据
 **/
SoftTopo.Data.prototype.createData = function(json) {
    var map = {},
        _this = this,
        oldHostList = json.nodes[1].children,
        newHostList = [],
        oldVmList = json.nodes[2].children,
        newVmList = [];
    this.nodes.length = 0;
    this.nodes = [];
    this.graph.clear();
    //对JSON nodes节点排序设备集合 VM集合
    (function sortNodes() {
        for (var i = 0, vmLen = oldVmList.length; i < vmLen; i++) {
            var vm = oldVmList[i];
            for (var k = 0, hostLen = oldHostList.length; k < hostLen; k++) {
                var host = oldHostList[k];
                if (host.ownerHost == vm.id) {
                    //将设备加入新组
                    newHostList.push(host);
                    oldHostList.splice(k, 1);
                    --hostLen;
                    --k;
                }
            };
        };
    })();
    //过滤完host之后,将剩余的host加入到新组内
    Array.prototype.push.apply(newHostList, oldHostList);
    json.nodes[1].children = newHostList;
    (function sortAll() {
        //1 2层不用排序
        for (var i = 1, len = json.nodes.length - 2; i <= len; i++) {
            var currentNodes = json.nodes[i],
                nextNodes = json.nodes[i + 1],
                newNextNodes = [];
            for (var k = 0, currentLen = currentNodes.children.length; k < currentLen; k++) {
                var currentNode = currentNodes.children[k];

                for (var j = 0, edgeLen = json.edges.length; j < edgeLen; j++) {
                    var edge = json.edges[j];
                    if (edge.to == currentNode.id) {
                        for (var o = 0, nextLen = nextNodes.children.length; o < nextLen; o++) {
                            var nextNode = nextNodes.children[o];
                            if (nextNode.id == edge.from) {
                                newNextNodes.push(nextNode);
                                nextNodes.children.splice(o, 1);
                                --nextLen;
                                --o;
                            }
                        };
                    }
                }
            };
            Array.prototype.push.apply(newNextNodes, nextNodes.children);
            json.nodes[i + 1].children = newNextNodes;
        };
    })();
    //计算节点坐标
    (function calculateCoords() {
        var groupWidth = 100,
            groupHeight = 100,
            groupMargin = 40,
            maxNodeMap = {};

        function calculateGroupWidth(nodes) {
            //分组宽度
            var totalWidth = 0,
                nodeMaxWidth = 0;

            $.each(nodes, function(index, node) {
                var nodeWidth = node.name.length * 12;
                nodeWidth = nodeWidth > 80 ? nodeWidth : 80;
                totalWidth += nodeWidth + groupMargin;
                nodeMaxWidth = Math.max(nodeMaxWidth, nodeWidth);
            });

            return {
                groupMaxWidth: totalWidth,
                nodeMaxWidth: nodeMaxWidth
            }
        };
        $.each(json.nodes, function(index, node) {
            //循环group获取每组最宽的设备以及计算group宽度
            if (node.children) {
                var groupInfo = calculateGroupWidth(node.children);
                groupWidth = Math.max(groupWidth, groupInfo.groupMaxWidth);
                // maxNodeMap[node.id] = groupInfo.nodeMaxWidth;
                maxNodeMap[node.id] = {
                    "nodeMaxWidth": groupInfo.nodeMaxWidth,
                    "groupMaxWidth": groupInfo.groupMaxWidth
                }
            }
        });
        $.each(json.nodes, function(index, node) {
            node.width = groupWidth;
            node.height = groupHeight;

            if (!node.X) {
                node.X = 0 - groupWidth / 2;
            }
            if (!node.Y) {
                node.Y = -160 + index * (groupHeight + groupMargin);
            }
            //顶层节点在group中永远居中
            if (!index) {
                var mainHost = node.children[0];
                mainHost.X = node.X + node.width / 2;
                mainHost.Y = node.Y + node.height / 2;
            } else {
                if (node.children) {

                    var children = node.children,

                        //首先计算group内所有节点宽度总和
                        groupChildTotalWidth = maxNodeMap[node.id].groupMaxWidth - children.length * (groupMargin / 2),

                        nodeWidth = groupChildTotalWidth / children.length,

                        // 计算左右间距剩余距离
                        groupChildDistance = (groupWidth - groupChildTotalWidth) / 2 + nodeWidth / 2;


                    $.each(children, function(index, host) {

                        host.X = node.X + groupChildDistance + nodeWidth * index;
                        host.Y = node.Y + node.height / 2;
                    });
                }
            }

        });
    })();
    if (json.nodes) {
        $.each(json.nodes, function(index, data) {
            var group = _this.createRect(data.name || "", data.X, data.Y, data.width, data.height);

            group.set("data", data);

            if (!index) {
                var mainHost = data.children[0],
                    newNode = _this.createText(mainHost.name, mainHost.X, mainHost.Y, group);
                map[mainHost.id] = newNode;
                newNode.set("data", mainHost);
            } else {
                if (data.children) {
                    var children = data.children;
                    $.each(children, function(index, host) {
                        var newNode = _this.createNode(host.name, host.X, host.Y, group);
                        newNode.set("data", host);
                        if (host.hostType) {
                            SoftTopo.Util.setIcon(newNode, host.hostType);
                        }
                        map[host.id] = newNode;
                    });
                }
            }
        });
    }
    if (json.edges) {
        Q.forEach(json.edges, function(data) {
            var from = map[data.from],
                to = map[data.to];
            if (!from || !to) {
                return;
            }
            var edge = _this.graph.createEdge(null, from, to);
            edge.setStyle(Q.Styles.ARROW_TO, false);
            edge.setStyle(Q.Styles.EDGE_WIDTH, 1);
            edge.set("data", data);
        });
    };
    this.graph.zoomToOverview();
};
/**创建 文本模型(业务名称设备)
 * @parameter name 名称
 *@parameter x X坐标
 *@parameter y Y坐标
 *@parameter host 宿主(设备父级节点)
 **/
SoftTopo.Data.prototype.createText = function(name, x, y, host) {
    var text = this.graph.createText(name, x, y);
    text.setStyle(Q.Styles.LABEL_BORDER, 0.5);
    text.setStyle(Q.Styles.LABEL_PADDING, 5);
    text.setStyle(Q.Styles.LABEL_BORDER_STYLE, "#1D4876");
    text.tooltipType = "text";
    if (host) {
        text.host = text.parent = host;
    }

    text.setStyle(Q.Styles.LABEL_FONT_SIZE, 14);
    text.setStyle(Q.Styles.LABEL_COLOR, "#FFF");
    text.setStyle(Q.Styles.LABEL_BACKGROUND_COLOR, "#058fcd");

    return text;
};
/**创建 矩形(组)
 * @parameter name 组名称
 *@parameter x 组X坐标
 *@parameter y 组Y坐标
 *@parameter width 组宽
 *@parameter height 组高
 *@parameter host 宿主(设备父级节点)
 **/
SoftTopo.Data.prototype.createRect = function(name, x, y, width, height) {
    var rect = this.graph.createNode(name, x, y);
    rect.type = "rectGroup";
    rect.anchorPosition = Q.Position.LEFT_TOP;
    rect.image = Q.Shapes.getRect(0, 0, 100, 100);
    rect.size = {
        width: width,
        height: height
    };
    rect.setStyle(Q.Styles.SHAPE_STROKE, 0);
    rect.setStyle(Q.Styles.SHAPE_FILL_COLOR, Q.toColor(0x88FFFFFF));
    rect.setStyle(Q.Styles.BORDER, 1);
    rect.setStyle(Q.Styles.BORDER_RADIUS, 0);
    rect.setStyle(Q.Styles.BORDER_COLOR, "#1D4876");
    rect.setStyle(Q.Styles.BORDER_LINE_DASH, [5, 6]);
    rect.setStyle(Q.Styles.LABEL_FONT_STYLE, "bolder");
    rect.setStyle(Q.Styles.LABEL_PADDING, 5);
    rect.setStyle(Q.Styles.LABEL_POSITION, Q.Position.LEFT_TOP); //文本位置

    return rect;
};
/**创建 设备
 * @parameter name 设备名称
 *@parameter x 设备X坐标
 *@parameter y 设备Y坐标
 *@parameter host 宿主(设备父级节点)
 **/
SoftTopo.Data.prototype.createNode = function(name, x, y, host) {
    var node = this.graph.createNode(name, x, y);
    node.tooltip = name;
    node.size = {
        width: 50
    };
    if (host) {
        node.host = node.parent = host;
    }
    this.nodes.push(node);

    return node;
};