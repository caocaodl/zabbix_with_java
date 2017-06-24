/*
 *数据处理类
 *
 */

SoftTopo.Data = function(graph) {
    this.graph = graph;
    this.graph.name="运营商-虚拟链路拓扑";
};

SoftTopo.Data.prototype.createData = function(json) {
    var r = 400,
        angle = 0,
        count,
        perAngle,
        nodes,
        center,
        i = 1;
    if (json.nodes) {
        var _this = this;
        Q.forEach(json.nodes, function(data, index) {
            var node = _this.createNode(Q.Graphs.node, data.name);
            node.set("data", data);
        });
        nodes = this.graph.graphModel.toDatas();
        center = nodes[0];
        SoftTopo.Util.setIcon(center,"g_group");
        center.setStyle(Q.Styles.RENDER_COLOR, "#f00");
        count = this.graph.graphModel.length;
        perAngle = 2 * Math.PI / (count - 1);
        while (i < count) {
            var node = nodes[i++];
            node.setLocation(r * Math.sin(angle), r * Math.cos(angle));
            angle += perAngle;
            this.createEdge(center, node);
        }


        this.graph.ondblclick = function(evt) {
            var element = evt.getData();
            if (element && element.get("data")) {
                var elementData = element.get("data"),
                    id = elementData.id,
                    name = elementData.name;
                name === "admin" ? id = "all" : "";
                window.top.$.workspace.openTab(name + "虚拟链路", window.top.ctxpath + "/platform/iradar/VirtLinkTopoAdminIndex.action?tenantId=" + id);
            }
        }

        this.graph.zoomToOverview();
    }

}
SoftTopo.Data.prototype.createNode = function(photo, name, x, y) {
    var node = this.graph.createNode(name);
    SoftTopo.Util.setIcon(node,"g_group");
    node.setStyle(Q.Styles.RENDER_COLOR, "#5af");
    node.setStyle(Q.Styles.LABEL_FONT_SIZE, 20);
    node.setStyle(Q.Styles.LABEL_POSITION, Q.Position.RIGHT_MIDDLE);
    node.setStyle(Q.Styles.LABEL_ANCHOR_POSITION, Q.Position.LEFT_MIDDLE);
    node.setStyle(Q.Styles.BACKGROUND_COLOR, Q.toColor(0xEEDDDDDD));
    node.setStyle(Q.Styles.PADDING, new Q.Insets(5));
    return node;
}

SoftTopo.Data.prototype.createEdge = function(from, to) {
    var edge = this.graph.createEdge(from, to)
    edge.setStyle(Q.Styles.ARROW_TO, false)
    edge.setStyle(Q.Styles.EDGE_WIDTH, 1);
}