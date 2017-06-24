Plugin.reg("operation", function() {
	var result = {};

	result.funcs = {

	};

	result.cfg = {
		xtype : "buttongroup",
		title : "菜单",
		autoWidth : true,
		defaults : {
			scale : "small",
			iconAlign : "top"
		},

		items : [
				{
					text : '保存',
					iconCls : 'tb-save',
					handler : function() {
						var editor = Gef.activeEditor;
						var xml = editor.serial();						
						xml=xml.replace(/\'/g,"\"");
						Ext.Msg.wait('正在保存');

						Ext.Ajax.request({
							method : 'post',
							url : Gef.systemSaveData_url,
							success : function(response) {
								try {
									var o = Ext.decode(response.responseText);
									if (o.success === true) {
										Ext.Msg.alert('信息', '操作成功');
									} else {
										Ext.Msg.alert('错误', o.errors.msg);
									}
								} catch (e) {
									Ext.Msg.alert('系统错误', response.responseText);
								}
							},
							failure : function(response) {
								Ext.Msg.alert('系统错误', response.responseText);
							},
							params : {
								xml : xml
							}
						});
					}
				}]
	};

	return result;
});