/**
 * 请求后台数据的方法
 * 示例
 * 	$.ajax({
 *		type: "POST",//post||get
 *		url: "",//请求路径
 *		data: {},//发送到服务器端参数
 *		async:true,//默认true异步请求
 *		cache: false,//设置为false将不会从浏览器缓存中加载请求信息
 *		dataType: "json",//预期服务器返回的数据类型
 *		success: function(json) {},
 *		error: function(XMLHttpRequest, textStatus, errorThrown) {}
 *	});
 */
SoftTopo.AjaxData = function() {}

/**
 * 初始化拓扑
 */
SoftTopo.AjaxData.prototype.initTopo = function() {
	$.ajax({
		type: "POST",
		url: SoftTopo.initTopo_url,
		data: {},
		cache: false,
		dataType: "json",
		success: function(json) {
			if (json && json.nodes) {
				SoftTopo.App.refreshData(json);
			} else {
				// alert("加载失败");
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("加载失败");
		}
	});
}
