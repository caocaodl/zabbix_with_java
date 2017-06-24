var flagindex=0;//是否重新编辑标志
var falgid=0;//自定义告警id值记录
var itemdata;
var isEdit = false;
var cloudhoststatus ="云主机状态";
//添加告警的方法
function addCustomTwo(data){
	flagindex=0;
	  itemdata=data;
	if(jQuery('#addCustom').attr("class")=="input button shadow ui-corner-all"||jQuery('#addCustom').attr("class")=="input button shadow ui-corner-all out"){
		jQuery('#addCustom').attr("class","input button shadow ui-corner-all cc");
		jQuery("#tvmt").show();
		jQuery("#itemid").empty();
		jQuery("#type").empty();
		jQuery(data).each(function(i){
			var Rand = Math.ceil(Math.random()*1000000000);
			jQuery("#itemid").append('<label role="button" id="'+Rand+'" class="ui-button ui-widget ui-state-default ui-button-text-only  ui-corner-left ui-state-hover "><span id="monitoringName_900000" name="monitoringName" class="ui-button-text"  onclick="loadThresholdSetting(\''
					+data[i].appname+'\',\''+Rand+'\')">'+data[i].appname+'</span></label>'); 
			if(i==0){
				    	jQuery("#"+Rand).addClass('ui-state-active');
				    	jQuery("#untis").html(data[i]["itemsmap"][0].untis);
				    	falgid=Rand;
				    }
		});
		
		var dataone=data[0]["itemsmap"];
		jQuery(dataone).each(function(i){
			jQuery("#type").prepend("<option value='"+dataone[i].name+"'>"+dataone[i].name+"</option>");
			jQuery("#untis").html(dataone[i].units);
		})
		 iteminit();
	}else{
		jQuery('#addCustom').attr("class","input button shadow ui-corner-all");
		jQuery("#itemid").empty();
		jQuery("#type").empty();
		jQuery("#tvmt").hide();
	}	
}
jQuery(function($) {
	var addnum=10;
	$('#tsave').click(function() {
		if($("#cdata tr td:last").html()=='没有发现数据'||$("#cdata tr td:last").html()=='NO found Data' || $("#cdata tr td:last").html() == '操作'){
			showModalWindow("请添加自定义告警");
			return false;
		}else {
			getThSetting();
		}
	});
	//选择监控指标显示与隐藏
	$('#chooseItem').click(function(){
		if($('#chooseItem').attr("class") == "input button shadow ui-corner-all"){
			$('#chooseItem').attr("class","input button shadow ui-corner-all cc");
			$('#itemtableid').hide();
			$('#itemdiv').hide();
		}else{
			$('#chooseItem').attr("class","input button shadow ui-corner-all");
			$('#itemtableid').show();
			$('#itemdiv').show();
		}
	});
	
	//添加自定义告警
	$('#add').click(function(){
		var index=++addnum;
		var type=$("#type").val();
		var operator=$("#operator").val();
		var numerical=$("#numerical").val();
		var numericalcom =  $("#numericalcom").val();
		var hasZh=/.*[\u4e00-\u9fa5]+.*$/;
		var isnum = /^\d+(\.\d+)?$/;
		var chunum;
		var isenable;
		var flag=false;
		
		if(type == cloudhoststatus){
		}else{
			if(!numerical){
				showModalWindow("请填写阈值");
				return false;
			}else if(hasZh.test(numerical)){
				showModalWindow("阈值不能含有中文");
				return false;
			}else if(!isnum.test(numerical)){
				showModalWindow("阈值只能是数字");
				return false;
			}
		}
	
		if(!isEdit){
			var arr_len = jQuery('#cdata tr').size();
			if(arr_len > 1){
				for(var i=1;i<arr_len;i++)
				{   
					var addedtype = jQuery('#cdata tr:eq('+ i +')').children('td:eq(0)').attr('value');
					var addedoperator = jQuery('#cdata tr:eq('+ i +')').children('td:eq(1)').attr('value');
					if(addedtype==type && addedoperator==operator){
						showModalWindow("监控名称相同且条件相同的监控指标告警只能添加一条，\r\n请重新添加或编辑已有的");
						return false;
					}
				}
			}
		} 
	/*	$("#cdata tr").each(function(){
			if($(this).attr('id')==flagindex){
				flag=true;
			}
		});*/
		$("#tvmt").hide();
		$("#itemid").empty();
		$('#addCustom').attr("class","input button shadow ui-corner-all out");
		
		$("input[name=gaonum]").each(function() {
			if($('#gaonum_label_'+$(this).val()).hasClass('ui-state-active')){
				chunum=$('#gaonum_label_'+$(this).val()).text();
			}
		});
		$("input[name=isenable]").each(function() {
			if($('#isenable_label_'+$(this).val()).hasClass('ui-state-active')){
				isenable=$('#isenable_label_'+$(this).val()).text();
			}
		});
		
		if($("#cdata tr td:last").html()=='没有发现数据'||$("#cdata tr td:last").html()=='NO found Data'){
			$("#cdata tr:last").remove();
		};
		var type_html= '<td value="'+type+'">'+ type+'</td>';
		var operator_html= '<td value="'+operator+'">'+ operator+'</td>';
		var numerical_html;
		if(type == cloudhoststatus){
			numerical_html= '<td value="'+numericalcom+'">'+ numericalcom+'</td>';
		}else{
			numerical_html= '<td value="'+numerical+'">'+ numerical+'</td>';
		}
		
		var chunum_html= '<td value="'+chunum+'">'+ chunum+'</td>';
		var isenable_html= '<td value="'+isenable+'">'+ isenable+'</td>';
		if(isEdit){
			var html=type_html + operator_html + numerical_html + chunum_html + isenable_html+ 
		    '<td><input id="update1" class="input link_menu icon edit" type="button" onclick="edit(\''+flagindex+'\')" name="update1">'+
		    '<input id="delete1" class="input link_menu icon remove" type="button"  onclick="del(\''+flagindex+'\')"  name="delete1"></td>';
			$('#cdata tr[id='+flagindex+']').empty().html(html);
			$('#cdata tr[id='+flagindex+']').attr("class","pack");
		}else{
			var html='<tr id='+index+' class="pack">'+type_html + operator_html + numerical_html + chunum_html + isenable_html+ 
		    '<td><input id="update1" class="input link_menu icon edit" type="button" onclick="edit(\''+index+'\')" name="update1">'+
		    '<input id="delete1" class="input link_menu icon remove" type="button"  onclick="del(\''+index+'\')"  name="delete1"></td></tr>';
        	$(html).appendTo('#cdata');
		}
		isEdit=false;
	});
	
	$('#cancel').click(function(){
		if($('#addCustom').attr("class")!="input button shadow ui-corner-all"){
			$('#addCustom').attr("class","input button shadow ui-corner-all");
			}
			$("#type").empty();
			$("#tvmt").hide();
			$("#itemid").empty();
	});
});

function edit(index){
	isEdit=true;
	flagindex=index;
	var typevalue = jQuery("#cdata tr[id="+index+"]").children('td:eq(0)').attr('value');
	var numerical =  jQuery("#cdata tr[id="+index+"]").children('td:eq(2)').attr('value');
	var operator = jQuery("#cdata tr[id="+index+"]").children('td:eq(1)').attr('value');
	jQuery("#tvmt").show();
	jQuery("#type").empty();
	jQuery("#type").append("<option value='"+typevalue+"'>"+typevalue+"</option>");
	
	if(cloudhoststatus==typevalue){
		jQuery("#operator").empty();
		addOperator("operator",operator);
		jQuery("#numerical").hide();
		jQuery("#untis").html("");
		jQuery("#numericalcom").empty();
		jQuery("#numericalcom").show();
		addNumericalcom("numericalcom",numerical);
	}else{
		jQuery("#numericalcom").hide();
		jQuery("#numerical").val(numerical);
		jQuery("#operator").val(operator);
	}
	
	jQuery("input[name=gaonum]").each(function() {
		if(jQuery('#gaonum_label_'+jQuery(this).val()).text()==jQuery("#cdata tr[id="+index+"]").children('td:eq(3)').attr('value')){
			jQuery('#gaonum_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left ui-state-active")
		}else{
			jQuery('#gaonum_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left")
		}
	});
	
	jQuery("input[name=isenable]").each(function() {
		if(jQuery('#isenable_label_'+jQuery(this).val()).text()==jQuery("#cdata tr[id="+index+"]").children('td:eq(4)').attr('value')){
			jQuery('#isenable_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left ui-state-active")
		}else{
			jQuery('#isenable_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left")
		}
	});
	
	jQuery("#tvmt li").each(function (i) {
		jQuery("#tvmt li").attr("class","pack formrow");
	});
}

function del(index){
	jQuery("#cdata tr[id="+index+"]").remove();
}

//显示监控描述
function loadThresholdSetting(appname,Rand){
	jQuery("#type").empty();
	var isInit =true;
	isEdit = false;
	jQuery(itemdata).each(function(i){
		var items=itemdata[i]["itemsmap"];
		if(cloudhoststatus == appname && appname==itemdata[i].appname){
			jQuery(items).each(function (j) {
				jQuery("#type").prepend("<option value='"+items[j].name+"'>"+items[j].name+"</option>");
				jQuery("#untis").html(items[j].units);
			});
			jQuery("#numerical").hide();
			isInit =false;
			
			jQuery("#operator").empty();
			jQuery("#operator").prepend("<option selected='selected' value='等于'>等于</option>")
			 .prepend("<option value='不等于'>不等于</option>");
			
			jQuery("#numericalcom").empty();
			jQuery("#numericalcom").show();
			jQuery("#numericalcom").prepend("<option value='ERROR'>ERROR</option>")
			 .prepend("<option value='ACTIVE'>ACTIVE</option>");

			return false;
		}
		if(appname==itemdata[i].appname){
			jQuery(items).each(function (j) {
				jQuery("#type").prepend("<option value='"+items[j].name+"'>"+items[j].name+"</option>");
				jQuery("#untis").html(items[j].units);
			});
			jQuery("#operator").empty();
			jQuery("#operator").prepend("<option selected='selected' value='大于'>大于</option>")
			 .prepend("<option value='小于'>小于</option>");
			jQuery("#numerical").show();
			jQuery("#numericalcom").hide();
		}
	});
	if(isInit){
		iteminit();
	}
	jQuery("#"+falgid).removeClass('ui-state-active'); 
	jQuery("#"+Rand).addClass('ui-state-active'); 
	falgid=Rand;
}
function getThSetting(){//将自定义告警转换为json格式
	var thsettings = new Array();
	var arr_len = jQuery('#cdata tr').size();
	if(arr_len > 1){
		for(var i=1;i<arr_len;i++)
		{   
			var th_id = jQuery('#cdata tr:eq('+ i +')').attr('id');
			var type = jQuery('#cdata tr:eq('+ i +')').children('td:eq(0)').attr('value');
			var operator = jQuery('#cdata tr:eq('+ i +')').children('td:eq(1)').attr('value');
			var numerical = jQuery('#cdata tr:eq('+ i +')').children('td:eq(2)').attr('value');
			var gaonum = jQuery('#cdata tr:eq('+ i +')').children('td:eq(3)').attr('value');
			var isenable = jQuery('#cdata tr:eq('+ i +')').children('td:eq(4)').attr('value');
			var num=(parseInt(i))-1;
			var thsetting =  new Array();
			var data={"itemid":th_id,
					  "type":type,
					  "operator":operator,
					  "numerical":numerical,
					  "gaonum":gaonum,
					  "isenable":isenable};
			thsettings.push(data);
		}
	}
	jQuery('#detatalarraytest').val(JSON.stringify(thsettings));
}

function iteminit(){
	jQuery("#operator").val("大于");
	
	jQuery("#numerical").val("");
	jQuery("#numerical").show();
	jQuery("#numericalcom").hide();
	jQuery("input[name=gaonum]").each(function(i) {
		if(i==0){
			jQuery('#gaonum_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left ui-state-active")
		}else{
			jQuery('#gaonum_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left")
		}
	});
	
	jQuery("input[name=isenable]").each(function(i) {
		if(i==0){
			jQuery('#isenable_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left ui-state-active")
		}else{
			jQuery('#isenable_label_'+jQuery(this).val()).attr("class","ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left")
		}
	});
	
	
}

function addOperator(id,operator){
	var addoperator = jQuery("#"+id);
	var newoperator="";
	addoperator.append("<option value='"+operator+"'>"+operator+"</option>");
	if("大于"==operator){
		newoperator="小于";
	}else if("小于"==operator){
		newoperator="大于";
	}else if("等于"==operator){
		newoperator="不等于";
	}else if("不等于"==operator){
		newoperator="等于";
	}
	addoperator.append("<option value='"+newoperator+"'>"+newoperator+"</option>");
}

function addNumericalcom(id,numericalcom){
	var addnumericalcomr = jQuery("#"+id);
	var newnumericalcom="";
	addnumericalcomr.append("<option value='"+numericalcom+"'>"+numericalcom+"</option>");
	if("ERROR"==numericalcom){
		newnumericalcom = "ACTIVE";
	}else if("ACTIVE"==numericalcom){
		newnumericalcom = "ERROR";
	}
	addnumericalcomr.append("<option value='"+newnumericalcom+"'>"+newnumericalcom+"</option>");
}

jQuery(document).keydown(function(event){
	if(event.keyCode == "13"){
		return false;
	}
}); 
