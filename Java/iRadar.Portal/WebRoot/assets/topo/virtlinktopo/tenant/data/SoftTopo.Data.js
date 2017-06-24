/*
 *数据处理类
 *
 */

SoftTopo.Data = function(graph) {
    this.graph = graph;
    this.graph.name = "租户-虚拟链路拓扑";
    this.nodes = [];
    this.nodeAlarmTimer = null;
    this.MILLISEC = 60000;
    this.init();
};
SoftTopo.Data.prototype.init = function() {
        this.layout = new Q.TreeLayouter(this.graph);
        this.layout.layoutType = Q.Consts.LAYOUT_TYPE_EVEN_HORIZONTAL;
    }
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
                    url = SoftTopo.detail_url + "?form=update&templateid=1&hostid=" + nodeData.hostId + "";
                window.top.$.workspace.openTab(nodeData.name + "详情", window.top.ctxpath + url);
            }
        },
        before: function(graph, data, evt) {

            var _showMenu = false;
            if (data && data.get("data")) {
                var nodeData = data.get("data");
                nodeData.hostId&&nodeData.hostType.toLocaleLowerCase()=="vm" ? _showMenu = true : _showMenu = false;
            }
            return _showMenu;
        },
        scope: _this
    };
    menuArr.push(hostDetail);
    return menuArr;
};
/*
 * 对子网数据进行过滤
 */
SoftTopo.Data.prototype.createData = function(json) {
    var newJson = {},
        nodeArr = [],
        vmNodeArr = [],
        vmNodeMap = {},
        edgeArr = [],
        vmEdgeArr = [],
        vmEdgeMap = {},
        jsonEdges = json.edges,
        jsonNodes = json.nodes;

    for (var i = 0; i < jsonNodes.length; i++) {
        var jsonNode = jsonNodes[i];
        if (jsonNode.hostType && jsonNode.hostType.toLocaleLowerCase().indexOf("vm") > -1) {
            vmNodeArr.push(jsonNode);
            vmNodeMap[jsonNode.id] = jsonNode;
        } else {
            nodeArr.push(jsonNode);
        }

    };
    // 过滤所有和VM虚拟机有关联的连线

    for (var w = 0; w < vmNodeArr.length; w++) {
        var vmNode = vmNodeArr[w];
        for (var j = 0, len = jsonEdges.length; j < len; j++) {
            var jsonEdge = jsonEdges[j],
                edgeFrom = jsonEdge.from;

            if (jsonEdge.to.toLocaleLowerCase() === vmNode.id) {
                var vmEdge = vmEdgeMap[vmNode.id];
                if (vmEdge) {
                    if (vmEdge.from.indexOf(edgeFrom) == -1) {
                        vmEdge.from += "," + edgeFrom;
                        vmEdgeMap[vmNode.id] = {
                            "id": vmNode.id,
                            "from": vmEdge.from,
                            "name": vmNode.name
                        }; // vmEdge;
                    }
                } else {
                    vmEdgeMap[vmNode.id] = {
                        "id": vmNode.id,
                        "from": edgeFrom,
                        "name": vmNode.name
                    }; //edgeFrom;
                }
                // 删除VM连线
                jsonEdges.splice(j, 1);
                --len;
                --j;

            }

        };
    };
    for (var vmMap in vmEdgeMap) {
        var vm = vmEdgeMap[vmMap];
        var obj = {
            "id": vm.id,
            "name": vm.name,
            "from": vm.from
        };
        vmEdgeArr.push(obj);
    }
    //{"name":"vm1","from":"1,2"},{"name":"vm2","from":"3,4"},{"name":"vm4","from":"1,2,3"}
    //重新调整VM虚拟机的结构(子网)

    var subnetWorkArr = [];
    for (var k = 0, len = vmEdgeArr.length; k < len; k++) {
        var vmEqualityArr = [];
        //默认相等
        var isEquality = true;
        var currentVmForms = vmEdgeArr[k],
            currentVmFormsArr = currentVmForms.from.split(",");
        vmEqualityArr.push({
            "id": currentVmForms.id,
            "name": currentVmForms.name,
            "from": currentVmForms.from
        });
        for (var m = 1, nextLen = vmEdgeArr.length; m < nextLen; m++) {
            var nextVmForms = vmEdgeArr[m],
                nextVmFormsArr = nextVmForms.from.split(",");
            if (currentVmFormsArr.length == nextVmFormsArr.length) {
                //判断第一组from 和以后的from 是否一致
                for (var i = 0; i < currentVmFormsArr.length; i++) {
                    var currentVmForm = currentVmFormsArr[i];
                    if (nextVmForms.from.indexOf(currentVmForm) == -1) {
                        //存在不同节点
                        isEquality = false;
                        break;
                    }else {
                        isEquality = true;
                    }
                }
                //相等 为同一个subnet
                if (isEquality) {
                    vmEqualityArr.push({
                        "id": nextVmForms.id,
                        "name": nextVmForms.name,
                        "from": nextVmForms.from
                    });
                    //删除相同from
                    vmEdgeArr.splice(m, 1);
                    --nextLen;
                    --m;
                    --len;
                }
            }
        }
        subnetWorkArr.push(vmEqualityArr);
        //删除本身
        vmEdgeArr.splice(k, 1);
        --len;
        --k;
    }
    var _nodeAll = {};
    $.each(nodeArr, function(index, node) {
        _nodeAll[node.id] = node;
    });
    for (var i = 0; i < subnetWorkArr.length; i++) {
        var subnet = subnetWorkArr[i],
            children = [],
            froms = subnet[0].from.split(","),
            obj = {
                "id": "subnetWork" + i,
                "subnetType": "true",
                "name": "subnetWork" + i,
                "child": []
            };
        //create subnetwork edges   
        //{"name":"edge","from":"s2","to":"vm4"}
        for (var m = 0; m < froms.length; m++) {
            json.edges.push({
                "name": "edge",
                "from": froms[m],
                "to": obj.id
            });
            if (!m) {
                //修改子网名称
                var _fromNode = _nodeAll[froms[0]];
                obj.name = _fromNode && _fromNode.name ? _fromNode.name + "_子网" : obj.name;
            }
        };


        for (var k = 0; k < subnet.length; k++) {
            var vm = subnet[k],
                vmNode = vmNodeMap[vm.id];
            children.push(vmNode);
        }
        obj.child = children;
        //添加到节点数组
        nodeArr.push(obj);
    }
    //组装JSON
    newJson.nodes = nodeArr;
    newJson.edges = json.edges;
    this.createNode(newJson);
};
/*
 *创建节点
 */

SoftTopo.Data.prototype.createNode = function(json) {
    var map = {},
        _this = this,
        autoLayout = true;
    if (json.nodes) {
        Q.forEach(json.nodes, function(data) {
            //subnetNode
            var node;
            if (data.subnetType) {
                node = _this.createSubnetworkNode(data.name, 150, 0, "#DFF");
                //create child                  
                if (data.child.length) {
                    _this.appendChildren(node, data.child);
                }
            } else {
                node = _this.graph.createNode(data.name);
                SoftTopo.Util.setIcon(node, data.hostType);
            }
            node.parentChildrenDirection = Q.Consts.DIRECTION_BOTTOM;
            node.layoutType = Q.Consts.LAYOUT_TYPE_EVEN;
            node.set("data", data);
            map[data.id] = node;
            if (autoLayout && data.Y && parseFloat(data.X) != 0 && data.Y && parseFloat(data.Y) != 0) {
                autoLayout = false;
            }
        });
    }

    if (json.edges) {
        Q.forEach(json.edges, function(data) {
            var from = map[data.from];
            var to = map[data.to];
            if (!from || !to) {
                return;
            }
            var edge = _this.graph.createEdge(null, from, to);
            edge.setStyle(Q.Styles.ARROW_TO, false);
            edge.setStyle(Q.Styles.EDGE_WIDTH, 1);
            edge.set("data", data);
        });
    }


    this.graph.ondblclick = function(evt) {
        var element = evt.getData();
        if (element) {
            //network node
            if (element.showDetail != undefined) {
                element.showDetail = !element.showDetail;
                if (element.showDetail) {
                    var data = element.get("data"),
                        hosts = {};
                    hosts[data.id] = {
                        "hostType": data.hostType,
                        "hostid": data.id
                    };
                    SoftTopo.App.getAjaxData().updateSubNetTopo(hosts);
                };

            } else if (element.get("data").subnetType && !_this.nodeAlarmTimer) {
                var nodes = element.children.datas,
                    hosts = {},
                    hostIds = [];
                _this.nodes.length = 0;
                _this.nodes = [];
                if(!nodes.length){
                    SoftTopo.App.lockExportImage(true);
                }
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i],
                        nodeData = node.get("data");
                    _this.nodes.push(node);

                    hostIds.push(nodeData.id);
                };
                _this.destroyTimer();



                _this.nodeAlarmTimer = setTimeout(function runNodeTimer() {
                    var nodeGroup = [];

                    function _updateAlarmNodes(nodeGroup) {
                        SoftTopo.App.getAjaxData().updateAlarmNodes(nodeGroup);
                    }
                    for (var i = 0; i < hostIds.length; i++) {
                        if (i != 0 && i % 20 === 0) {
                            _updateAlarmNodes(nodeGroup);
                            nodeGroup.length = 0;
                        }
                        nodeGroup.push(hostIds[i]);
                    }
                    if (nodeGroup.length) {
                        _updateAlarmNodes(nodeGroup);
                    };

                    _this.nodeAlarmTimer = setTimeout(runNodeTimer, _this.MILLISEC);

                }, this.MILLISEC);
                _this.graph.zoomToOverview();
            } else {

            }
        } else {
            //clear timer
            _this.destroyTimer();
            _this.graph.zoomToOverview();
        }
    }


    // doLayout
    if (autoLayout) {
        this.doLayout();
    }
}
SoftTopo.Data.prototype.createSubnetworkNode = function(name, x, y, backgroundColor) {
    var subnetwork = this.graph.createNode(name);
    subnetwork.enableSubNetwork = true;
    subnetwork.backgroundColor = backgroundColor;
    subnetwork.image = Q.Graphs.subnetwork;
    subnetwork.setStyle(Q.Styles.BACKGROUND_COLOR, backgroundColor);
    subnetwork.setStyle(Q.Styles.PADDING, 5);
    subnetwork.setStyle(Q.Styles.BORDER, 1);
    subnetwork.setStyle(Q.Styles.BORDER_COLOR, "#888");
    return subnetwork;
}

SoftTopo.Data.prototype.appendChildren = function(subnetwork, child) {
    var subnetChild = child,
        per = Math.PI * 2 / subnetChild.length,
        angle = 0,
        RX = 300,
        RY = 200;
    for (var i = 0; i < subnetChild.length; i++) {
        var subChild = subnetChild[i],
            x = RX * Math.cos(angle),
            y = RY * Math.sin(angle),
            hostInfo = this._getHostType("vm");
        node = this.createServer(subChild.name, "", Q.Graphs.node, hostInfo);
        node.x = x;
        node.y = y;
        node.set("data", subChild);
        subnetwork.addChild(node);
        angle += per;
        this.nodes.push(node);
    }
}

SoftTopo.Data.prototype.createServer = function(name, id, icon, data) {

    var server = new Q.CustomServerNode(name, id, icon, data);
    server.showDetail=false;
    this.graph.addElement(server);
    return server;
};


SoftTopo.Data.prototype.updateSubNetCallBack = function(json) {

    var _this = this;
    this.nodes.forEach(function(node) {
        var nodeData = node.get("data"),
            data = json.data[nodeData.id],
            hostInfo = _this._getHostType(nodeData.hostType);

        if (data && hostInfo) {
            var tooltip = "";
            $.each(hostInfo, function(index, val) {

                var itemkey = val.itemkey,
                    itemCh = val.itemCh,
                    itemType = val.itemtype,
                    keyVal = "" + data[itemkey],
                    showVal = keyVal;
                if (itemType === "bar") {
                    showVal = parseFloat(keyVal);
                    showVal==1?keyVal=0:keyVal=keyVal;
                    showVal = showVal ? showVal / 100 : 1 / 100;
                } else {
                    var index = keyVal.indexOf("/");
                    index > -1 ? showVal = keyVal.substring(0, index) + ".." : keyVal.length > 13 ? showVal = keyVal.substring(0, 13) + ".." : showVal;
                }
                node.set(itemkey, showVal);
                keyVal=itemCh=="CPU"||itemCh=="MEM"?keyVal+"%":keyVal;
                tooltip += itemCh + ":" + keyVal + "</br>";
            });
            node.tooltip = tooltip;
        }
    })
};
SoftTopo.Data.prototype.destroyTimer = function() {


    if (this.nodeAlarmTimer) {
        clearTimeout(this.nodeAlarmTimer);
        this.nodeAlarmTimer = null;
    }
};
/*
 *更新设备信息(告警信息)
 */
SoftTopo.Data.prototype.updateAlarmNodes = function(json) {

    if (this.nodes.length) {
        $.each(this.nodes, function(index, node) {
            var data = json.data[node.get("data").id];
            if (data) {
                var priority = parseInt(data.priority),
                    description = data.description;
                if (priority) {
                    var color = priority >= 3 ? "#F00" : "#FF0";
                    node.setStyle(Q.Styles.RENDER_COLOR, color);
                    node.alarmLabel = description;
                    node.alarmColor = color;
                    node.setStyle(Q.Styles.IMAGE_PADDING, 3);
                    node.setStyle(Q.Styles.IMAGE_BACKGROUND_COLOR, color);

                } else {
                    node.setStyle(Q.Styles.RENDER_COLOR, "");
                    node.alarmLabel = null;
                    node.alarmColor = null;
                    node.setStyle(Q.Styles.IMAGE_PADDING, 0);
                    node.setStyle(Q.Styles.IMAGE_BACKGROUND_COLOR, "");
                }
               
            }
        });

    }
}

SoftTopo.Data.prototype._getHostType = function(hostType) {
    if (hostType == "Router" || hostType == "RouteSwitch" || hostType == "Switch") {
        hostType = "net";
    }
    return SoftTopo.Util.getMonitoritem(hostType.toLocaleLowerCase());
}
SoftTopo.Data.prototype.doLayout = function() {
    var _this = this;
    this.layout.doLayout({
        callback: function() {
            _this.graph.zoomToOverview();
        }
    });
}