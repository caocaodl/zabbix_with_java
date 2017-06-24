/**
 * 默认 数据
 */
var initData = {
	oprationType: 'new',
	item_type: 0, //0：当前响应时间   1：当日利用率
	condition: 1, //0:小于      1：大于
	timeout: 15, //当前响应时间   时间字段
	retry: 1, //当前响应时间    重试次数字段
	status: 0, //状态字段    0：启用  1：停用
	avbRate: 10 //当日利用率    利用率字段		
};
var isEdit = false;
var iscon = true;

jQuery(function($) {
	$('input[type="submit"][name="save"]').mouseover(function() {
		var value = $('#name').val();
		var url = $('#url').val();
		if (value == "") {
			showModalWindow("名称不能为空");
		} else if (url == "") {
			showModalWindow("url不能为空");
		} else if ($("#alarmLineList tr td:last").html() == '没有发现数据' || $("#cdata tr td:last").html() == 'NO found Data') {
			showModalWindow("请添加自定义告警");
		}
		/*else {
					if(url.indexOf(':')<0 || url.indexOf('//')<0 ){
		            	showModalWindow("url格式不正确，请填写标准url，如protocol :// hostname[:port] / path / [;parameters][?query]#fragment");
		            }
					var ishasnotice=false;
					$('input[type="checkbox"]').each(function() {
						   if(this.checked){
							  ishasnotice = true;
							  return false;
						   }
					});
					if(!ishasnotice){
						showModalWindow("请选择事件通知");
					}
				}*/
	});

	jQuery('#addAlarmLine').click(function() { //新增		
		jQuery("#alarmLineTable").load("thttpconf.action", initData, function() {
			jQuery("#delayid label").each(function() { //消除 按钮变大
				jQuery(this).html(jQuery(this).text());
			});
		});
	});
});

/**
 * 切换 监控指标
 * @param itmeType 
 */
function switchItemType(itemType, lineid) {
	if (itemType == 0) { //切换到 当日利用率
		if (lineid != null) { //修改状态下 切换
			initData.lineid = lineid;
		}

		initData.item_type = 1;
		initData.condition = 0;

		jQuery("#alarmLineTable").load("thttpconf.action", initData, function() {
			jQuery("#delayid label").each(function() { //消除 按钮变大
				jQuery(this).html(jQuery(this).text());
			});
		});

	} else { //切换到   当前响应时间
		if (lineid != null) { //修改状态下 切换
			initData.lineid = lineid;
		}

		initData.item_type = 0;
		initData.condition = 1;

		jQuery("#alarmLineTable").load("thttpconf.action", initData, function() {
			jQuery("#delayid label").each(function() { //消除 按钮变大
				jQuery(this).html(jQuery(this).text());
			});
		});
	}
}

/**
 * 添加告警线
 */
function addOpt() {

	iscon = true;
	var new_alarmLine_item_type = jQuery('#new_alarmLine_item_type').val();
	if (new_alarmLine_item_type == '0') {
		new_alarmLine_item_type = '当前响应时间';
	} else {
		new_alarmLine_item_type = '当日可用率';
	}
	var new_alarmLine_condition = jQuery('#new_alarmLine_condition').val();
	if (new_alarmLine_condition == '0') {
		new_alarmLine_condition = '小于';
	} else {
		new_alarmLine_condition = '大于';
	}
	var arr_len = jQuery('#alarmLineList tr').size();
	if (!isEdit) {


		if (arr_len > 1) {
			for (var i = 1; i < arr_len; i++) {
				var addedtype = jQuery('#alarmLineList tr:eq(' + i + ')').children('td:eq(0)').text();
				var addedoperator = jQuery('#alarmLineList tr:eq(' + i + ')').children('td:eq(1)').text();
				if (addedtype == new_alarmLine_item_type && addedoperator == new_alarmLine_condition) {

					iscon = false;
					jQuery("#alarmLineTable").load("thttpconf.action", initData, function() {
						jQuery("#delayid label").each(function() { //消除 按钮变大
							showModalWindow("监控名称相同且条件相同的监控指标告警只能添加一条，\r\n请重新添加或编辑已有的");
							jQuery(this).html(jQuery(this).text());
						});
					});
				}
			}
		}
	}
	if (iscon) {
		var data = {};
		var $editAlarmId = jQuery("#alarmLineTable").data("editAlarmId");
		var isCheck = false;
		var $alarmData = jQuery("#alarmLineTable tr");
		//证明有2条数据存在
		if (($editAlarmId == 0 || $editAlarmId) && $alarmData.length > 3) {
			for (var i = 1; i < $alarmData.length; i++) {
				var addedtype = jQuery('#alarmLineList tr:eq(' + i + ')').children('td:eq(0)').text();
				if(addedtype==""){
					break;
				}
				var addedoperator = jQuery('#alarmLineList tr:eq(' + i + ')').children('td:eq(1)').text();
				var alarmDataId = $(jQuery('#alarmLineList tr:eq(' + (i + 1) + ')').children('td:eq(0)').children("input")).attr("id").split("_")[1];
				console.log(alarmDataId + "===" + $editAlarmId);
				if (alarmDataId != $editAlarmId && addedtype == new_alarmLine_item_type && addedoperator == new_alarmLine_condition) {

					isCheck = true;
					showModalWindow("监控名称相同且条件相同的监控指标告警只能添加一条，\r\n请重新添加或编辑已有的");
					break;

				}
				i++;
			}
		}
		if (isCheck) {
			window.setTimeout(function() {
				lockButton(false);
			}, 5);
			return false;
		};
		//old
		jQuery("#alarmLineList tr input:hidden").each(function() {
			data[this.name] = this.value;
		});

		//new
		jQuery("#formElementTable tr :input[name^='new_alarmLine[']").each(function() {
			data[this.name] = this.value;
		});

		jQuery("#formElementTable tr input[type='radio']:checked").each(function() { //重试次数 状态按钮
			data[this.name] = this.value;
		});

		data["oprationType"] = "add";

		jQuery("#alarmLineList").load("thttpconf.action", data);

		//清除
		jQuery('#alarmLineTable').html("");
		isEdit = false;
	}

}

/**
 * 修改告警线
 */
function editOpt(id) {
	isEdit = true;
	var data = {};
	jQuery("#alarmLineList tr input[name*=" + id + "]").each(function() {
		data[this.name] = this.value;
	});

	data["oprationType"] = "edit";
	data["alarmLineid"] = id;
	jQuery("#alarmLineTable").load("thttpconf.action", data, function() {

		jQuery(this).data({
			editAlarmId: id
		});
		jQuery("#delayid label").each(function() { //消除 按钮变大
			jQuery(this).html(jQuery(this).text());

		});
	});

	jQuery('#alarmLineTable').html("");
}

/**
 * 删除告警线
 */
function delOpt(id) {
	var data = {};

	jQuery("#alarmLineList tr input:hidden").each(function() {
		data[this.name] = this.value;
	});

	data["oprationType"] = "del";
	data["alarmLineid"] = id;

	jQuery("#alarmLineList").load("thttpconf.action", data);
}


/**
 * 取消 添加告警线
 */
function cancelOpt() {
	jQuery("#formElementTable").html("");
}

jQuery(document).keydown(function(event) {
	if (event.keyCode == "13") {
		return false;
	}
});