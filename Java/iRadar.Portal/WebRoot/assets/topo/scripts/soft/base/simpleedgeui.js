$(function() {
    /**
     * This file is part of Qunee for HTML5.
     * Copyright (c) 2015 by qunee.com
     **/

    function SimpleEdgeUI(edge, graph) {
        Q.doSuperConstructor(this, SimpleEdgeUI, arguments);
    }
    SimpleEdgeUI.prototype = {
        validatePoints: function() {
            this.shape.invalidateData();
            var edge = this.data,
                path = this.path;
            path.clear();
            var fromAgent = edge.fromAgent;
            var toAgent = edge.toAgent;
            if (!fromAgent || !toAgent || fromAgent == toAgent) {
                return;
            }
            var fromUI = this.graph.getUI(fromAgent);
            var toUI = this.graph.getUI(toAgent);
            var fromBounds = fromUI.bodyBounds;
            var toBounds = toUI.bodyBounds;
            var x1 = fromAgent.x,
                y1 = fromAgent.y,
                x2 = toAgent.x,
                y2 = toAgent.y;
            var p1 = fromBounds.getIntersectionPoint(x1, y1, x2, y2);
            var p2 = toBounds.getIntersectionPoint(x2, y2, x1, y1);
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
        }
    }
    Q.extend(SimpleEdgeUI, Q.EdgeUI);
    Q.SimpleEdgeUI = SimpleEdgeUI;
    Q.loadClassPath(SimpleEdgeUI, 'Q.SimpleEdgeUI');//为了能导入导出，需要能全局访问
    
});