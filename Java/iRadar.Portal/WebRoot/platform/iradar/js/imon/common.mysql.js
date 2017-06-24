var falgid=0;//自定义告警id值记录
var flagindex=0;//是否重新编辑标志
var isEdit = false;
var itemdata;
var flag=false;
function addshowcalCustom(data){
	isEdit = false;
	itemdata = data;
	if(jQuery('#showcalCustom').attr("class")=="input button shadow ui-corner-all" ||jQuery('#showcalCustom').attr("class")=="input button shadow ui-corner-all out"){
		jQuery('#showcalCustom').attr("class","input button shadow ui-corner-all cc");
		jQuery("#tvmtServes").show();
		jQuery("#type").empty();
		flag=false;
		jQuery.each(itemdata, function(key, value) {
		      jQuery(value).each(function(){     
		    	  jQuery.each(this, function(key, value) {
		    		  jQuery("#type").prepend("<option value='"+value+"'>"+value+"</option>");
		    	  });   
		      });  
		});
		iteminit();
	}else{
		jQuery('#showcalCustom').attr("class","input button shadow ui-corner-all");
		jQuery("#tvmtServes").hide();
	}
}

jQuery(function($) {
	var addnum=10;
	$('#tsave').click(function() {
		var value = $('#name').val();
		var dbusername = $("#dbusername").val();
		var dbuserpass = $("#dbuserpass").val();
		var dbip = $("#dbip").val();
		var dbport = $("#dbport").val();
		var dbjavahome = $("#dbjavahome").val();
		if(value==""){
			showModalWindow("名称不能为空");
			return false;
		} else if (dbusername == "") {
			showModalWindow("数据库用户名不能为空");
			return false;
		} else if (dbuserpass == "") {
			showModalWindow("数据库密码不能为空");
			return false;
		} else if (dbip == "") {
			showModalWindow("数据库所在ip不能为空");
			return false;
		}else if (dbport == "") {
			showModalWindow("数据库端口号不能为空");
			return false;
		} else if (dbjavahome == "") {
			showModalWindow("java路径不能为空");
			return false;
		} else if($("#cdata tr td:last").html()=='没有发现数据'||$("#cdata tr td:last").html()=='NO found Data' || $("#cdata tr td:last").html() == '操作'){
			showModalWindow("请添加自定义告警");
			return false;
		}else {
			getThSetting();
		}
	});
	
	
	$('#chooseItem').click(function(){
		if($('#chooseItem').attr("class") == "input button shadow ui-corner-all"){
			$('#chooseItem').attr("class","input button shadow ui-corner-all cc");
			$('#itemtableid').show();
		}else{
			$('#chooseItem').attr("class","input button shadow ui-corner-all");
			$('#itemtableid').hide();
		}
		
	});
	
	$('#add').click(function(){
		var type=$("#type").val();
		var operator=$("#operator").val();
		var numerical=$("#numerical").val();
		var dbusername = $("#dbusername").val();
		var dbuserpass = $("#dbuserpass").val();
		var dbip = $("#dbip").val();
		var dbport = $("#dbport").val();
		var dbjavahome = $("#dbjavahome").val();
		var chunum;
		var isenable;
		if(numerical){
		}else{
			showModalWindow("请填写阈值");
			return false;
		}
		if(dbusername){
		}else{
			showModalWindow("请填写数据库用户名称");
			return false;
		}
		if(dbuserpass){
		}else{
			showModalWindow("请填写数据库密码");
			return false;
		}
		if(dbip){
		}else{
			showModalWindow("请填写数据库所在ip");
			return false;
		}
		if(dbport){
		}else{
			showModalWindow("请填写数据库端口号");
			return false;
		}
		if(dbjavahome){
		}else{
			showModalWindow("请填写Javahome路径");
			return false;
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
		
		$("#tvmtServes").hide();
		$('#showcalCustom').attr("class","input button shadow ui-corner-all out");
		if(isEdit){
			$("#cdata tr").each(function(){
				if($(this).attr('id')==flagindex){
					flag=true;
				}
			});
		}
		var index=++addnum;
		
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
		var numerical_html= '<td value="'+numerical+'">'+ numerical+'</td>';
		var chunum_html= '<td value="'+chunum+'">'+ chunum+'</td>';
		var isenable_html= '<td value="'+isenable+'">'+ isenable+'</td>';
		
		if(flag){
			var html= type_html + operator_html + numerical_html + chunum_html + isenable_html + 
		    '<td><input id="update1" class="input link_menu icon edit" type="button" onclick="edit(\''+flagindex+'\')" name="update1">'+
		    '<input id="delete1" class="input link_menu icon remove" type="button"  onclick="del(\''+flagindex+'\')"  name="delete1"></td>';
			$('#cdata tr[id='+flagindex+']').empty().html(html);
			$('#cdata tr[id='+flagindex+']').attr("class","pack");
		}else{
			var html='<tr id='+index+' class="pack">'+ type_html + operator_html  + numerical_html + chunum_html + isenable_html + 
		    '<td><input id="update1" class="input link_menu icon edit" type="button" onclick="edit(\''+index+'\')" name="update1">'+
		    '<input id="delete1" class="input link_menu icon remove" type="button"  onclick="del(\''+index+'\')"  name="delete1"></td></tr>';
        	$(html).appendTo('#cdata');
		}
		isEdit=false;
	});
	$('#cancel').click(function(){
		if($('#showcalCustom').attr("class")!="input button shadow ui-corner-all"){
			$('#showcalCustom').attr("class","input button shadow ui-corner-all");
		}
			$("#tvmtServes").hide();
		
	});
	

});

function edit(index){//自定义告警编辑	
	isEdit=true;
	flagindex=index;
	jQuery("#tvmtServes").show();
	jQuery("#type").empty();
	jQuery("#type").append("<option value='"+jQuery("#cdata tr[id="+index+"]").children('td:eq(0)').attr('value')+"'>"+jQuery("#cdata tr[id="+index+"]").children('td:eq(0)').attr('value')+"</option>");
	jQuery("#operator").val(jQuery("#cdata tr[id="+index+"]").children('td:eq(1)').attr('value'));
	jQuery("#numerical").val(jQuery("#cdata tr[id="+index+"]").children('td:eq(2)').attr('value'));
	
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
	
	jQuery("#tvmt li").each(function (i) {//告警描述显示
		jQuery("#tvmt li").attr("class","pack formrow");
	});
}

function del(index){//自定义告警删除
	jQuery("#cdata tr[id="+index+"]").remove();
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

function iteminit(){//初始化监控描述
	jQuery("#operator").val("大于");
	jQuery("#numerical").val("");
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


jQuery(document).keydown(function(event){
	if(event.keyCode == "13"){
		return false;
	}
}); 
