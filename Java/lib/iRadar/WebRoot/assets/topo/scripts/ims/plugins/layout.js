Plugin.reg("operation", function() {

			var result = {};

			result.funcs = {

			};

			result.cfg = {
				xtype : "buttongroup",
				title : "自动排列",
				defaults : {
					scale : "small",
					iconAlign : "top"
				},
				items : [{
							text : "矩形",
							iconCls : "tbar-chart-organisation",
							handler : function() {

							},
							scope : this
						}, {
							text : "圆形",
							iconCls : "tbar-chart-pie",
							handler : function() {

							},
							scope : this
						}, {
							text : "力导向",
							iconCls : "tbar-chart-line",
							handler : function() {
								
								
							}
						}]
			};

			return result;
		});