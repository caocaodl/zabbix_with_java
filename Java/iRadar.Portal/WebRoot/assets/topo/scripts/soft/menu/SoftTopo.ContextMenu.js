/**
 *右击菜单
 *
 */
SoftTopo.ContextMenu = function(optionMenu) {
	var menu = optionMenu || [];
	this.init(menu);
};
SoftTopo.ContextMenu.prototype.init = function(menu) {
	//read appconfig
	Q.PopupMenu.prototype.getMenuItems = function(graph, data, evt) {
		var items = [];
		if (data) {
			var isShapeNode = data instanceof Q.ShapeNode,
				isGroup = data instanceof Q.Group,
				isNode = !isShapeNode && data instanceof Q.Node,
				isEdge = data instanceof Q.Edge,
				selectionModel = graph.selectionModel;


		} else {

		}
		//加载 各个模块配置的菜单
		if (menu.length) {
			var _menu = [];
			Q.forEach(menu, function(m) {
				var newMenu = m;
				if (typeof(m) == "object") {

					if (!m.data) {
						m.data = {};
					}
					m.data.eventData = data;
					if (m.before) {
						m.before.call(m.scope, graph, data, evt) ? newMenu = m : newMenu = null;
					}
				}
				newMenu ? _menu.push(newMenu) : null;

			});
			Array.prototype.push.apply(items, _menu);
		}
		return items;
	}
}