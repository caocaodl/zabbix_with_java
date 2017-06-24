/*
 *数据处理类
 *
 */

SoftTopo.Data = function(graph) {
    this.graph = graph;
    this.graph.name="云主机从属拓扑";
    this.nodes = [];
    this.layout = null;
    this.toolbox = null;
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
                url=SoftTopo.detail_url+ "?hostid=" + nodeData.hostId + "&groupid=" +nodeData.groupId + "";
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
SoftTopo.Data.prototype.createData = function(json) {
    var map = {}, _this = this;
    if (json.nodes) {
        Q.forEach(json.nodes, function(data) {
            var node = _this.createNode(data.name, 0, 0);
            if (data.hostType) {
                SoftTopo.Util.setIcon(node, data.hostType);
            }
            node.set("data", data);            
            map[data.id] = node;
        });

        this.layout = new Q.SpringLayouter(this.graph, 200);
        this._createToolbox();
        this.graph.interactionDispatcher.addListener(function(evt) {
            if (evt.kind == Q.InteractionEvent.ELEMENT_MOVING) {
                _this.layout ? _this.layout.start() : "";
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
            var edge = _this.createEdge(from, to);
            edge.set("data", data);
        });
    }
    this.layout ? this.layout.start() : "";

}
SoftTopo.Data.prototype.createNode = function(name, x, y) {
    var node = this.graph.createNode(name, x, y);
    node.setStyle(Q.Styles.LABEL_PADDING, new Q.Insets(3, 5));
    this.nodes.push(node);
    return node;
}
SoftTopo.Data.prototype.createEdge = function(from, to) {
    var edge = this.graph.createEdge(from, to);
    edge.setStyle(Q.Styles.ARROW_TO, false);
    edge.uiClass = Q.SimpleEdgeUI;
    return edge;
}
SoftTopo.Data.prototype.randomNode = function() {
    return this.nodes[Q.randomInt(nodes.length)];
}
SoftTopo.Data.prototype._createSlider = function(parent, label, min, max, value, onchange) {
    var div = document.createElement("div");
    var step = (max - min) / 100;
    div.innerHTML = label + ": " + min + "<input type='range' value='" + value + "' step='" + step + "' min='" + min + "' max='" + max + "'>" + max;
    div.firstElementChild.onchange = function(evt) {
        onchange(evt.target.value);
    }
    parent.appendChild(div);
}
SoftTopo.Data.prototype._createToolbox = function() {
    var _this=this,
        default_repulsion = 90,
        default_attractive = 0.1,
        default_elastic = 5,
        toolbox = document.createElement("div");
    this.layout.repulsion = default_repulsion;
    this.layout.attractive = default_attractive;
    this.layout.elastic = default_elastic;
    toolbox.id = "toolbox";
    Q.css(toolbox, {
        position: "absolute",
        top: "100px",
        right: "0px"
    });
    this.graph.html.parentNode.appendChild(toolbox);

    this._createSlider(toolbox, "互斥", 0, 200, default_repulsion, function(v) {
        _this.layout.repulsion = v;
        _this.layout.start();
    });
    this._createSlider(toolbox, "引力", 0, 0.5, default_attractive, function(v) {
        _this.layout.attractive = v;
        _this.layout.start();
    });
    this._createSlider(toolbox, "弹性", 0, 10, default_elastic, function(v) {
        _this.layout.elastic = v;
        _this.layout.start();
    });
    this.toolbox = toolbox;
}

SoftTopo.Data.prototype.destroy = function() {
    this.toolbox.parentNode.removeChild(toolbox);
}