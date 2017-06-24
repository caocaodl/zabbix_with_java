
function isset(key, obj) {
	return (is_null(key) || is_null(obj)) ? false : (typeof(obj[key]) != 'undefined');
}

function empty(obj) {
	if (is_null(obj)) {
		return true;
	}
	if (obj === false) {
		return true;
	}
	if (is_string(obj) && obj === '') {
		return true;
	}
	if (typeof(obj) == 'undefined') {
		return true;
	}

	return is_array(obj) && obj.length == 0;
}

function is_null(obj) {
	return (obj == null);
}

function is_number(obj) {
	return isNaN(obj) ? false : (typeof(obj) === 'number');
}

function is_object(obj, instance) {
	if (typeof(instance) === 'object' || typeof(instance) === 'function') {
		if (typeof(obj) === 'object' && obj instanceof instance) {
			return true;
		}
	}
	else {
		if (typeof(obj) === 'object') {
			return true;
		}
	}

	return false;
}

function is_string(obj) {
	return (typeof(obj) === 'string');
}

function is_array(obj) {
	return (obj != null) && (typeof obj == 'object') && ('splice' in obj) && ('join' in obj);
}

function validateNumericBox(obj, allowempty, allownegative) {
	if (obj != null) {
		if (allowempty) {
			if (obj.value.length == 0 || obj.value == null) {
				obj.value = '';
			}
			else {
				if (isNaN(parseInt(obj.value, 10))) {
					obj.value = 0;
				}
				else {
					obj.value = parseInt(obj.value, 10);
				}
			}
		}
		else {
			if (isNaN(parseInt(obj.value, 10))) {
				obj.value = 0;
			}
			else {
				obj.value = parseInt(obj.value, 10);
			}
		}
	}
	if (!allownegative) {
		if (obj.value < 0) {
			obj.value = obj.value * -1;
		}
	}
}

$(document).ready(function() {
	$.ajaxSuccessFilter = function(XMLHttpRequest){
		var flag = false;
		var checkStatus = XMLHttpRequest.getResponseHeader("sessionStatus");
        if(checkStatus == "timeout"){
        	jAlert('登录超时,请重新登录!','信息提示',function(){
        		window.top.location=ctxpath + '/index.action'
        	});
        } else if(checkStatus == "denied"){
        	jAlert('权限不足,拒绝操作!','信息提示');
        } else {
        	flag = true;
        }
        return flag;
	};
	$.ajaxSetup({   
	    contentType:"application/x-www-form-urlencoded;charset=utf-8",
	    type : 'POST',
	    cache:false,
	    beforeSend: function (XMLHttpRequest) {
			XMLHttpRequest.setRequestHeader("If-Modified-Since","0");
			XMLHttpRequest.setRequestHeader("Cache-Control","no-cache");
			if(this.dataType=='json'){
				if(this.data && this.data!=''){
					this.data = this.data+'&its='+new Date().getTime();
				} else {
					this.data = 'its='+new Date().getTime();
				}
			}
		}
	 });
});

// enter键触发查询
$(document).keydown(function (event) {
    if (event.keyCode == 13) {
    	var searchObj = $("div[id=dataTabFilter]");
    	if(searchObj.length == 0){
    		searchObj = $("div[id=cloudHostTabFilter]");
    	}
    	if(searchObj.length == 0){
    		searchObj = $("div[id=imageTabFilter]");
    	}
    	
    	if(searchObj.length > 0){
    		var isFocused = false;
    		searchObj.find("input").each(function(){
    			if($(this).is(":focus")){
    				isFocused = true;
    				return false;
    			}
    		});
    		
    		if(isFocused == false){
        		searchObj.find("select").each(function(){
        			if($(this).is(":focus")){
        				isFocused = true;
        				return false;
        			}
        		});
    		}
    		
    		if(isFocused == false){
    			isFocused = searchObj.find("a[class=button]:first").is(":focus");
    		}
    		
    		if(isFocused == true){
    			searchObj.find("a[class=button]:first").triggerHandler('click');
    		}
    	}
    }
});

function doReset(parent){
	if(parent == undefined){
		$.each($('.resetable-empty'), function(i, field){
	        $(this).val('');
	    });
		$.each($('.resetable-remove'), function(i, field){
	        $(this).remove();
	    });
	} else {
		$.each($('.resetable-empty',parent), function(i, field){
	        $(this).val('');
	    });
		$.each($('.resetable-remove',parent), function(i, field){
	        $(this).remove();
	    });
	}
}

function jsonToField(rowdata, objId) {
	var fields = $('.field',objId);
	$.each(fields, function(i, field){
		//$(field).val('');
	});
	if (rowdata) {
		for(var i in rowdata) {
			if ( $('#'+i,objId).is('input:radio') || $('#'+i,objId).is('input:checkbox'))  {
				/*
				$('#'+i,objId).each( function() {
					if( $(this).val() == rowdata[i] ) {
						$(this)['attr']("checked",true);
					} else {
						$(this)['attr']("checked", false);
					}
				});
				*/
			} else {
				$('#'+i,objId).val(rowdata[i]);
			}
		}
	}
}
function fieldToJson(objId,prefix){
	var fields = $('.field',objId);
	var json = {};
	var imap = {};
	$.each(fields, function(i, field){
		var s = field.name?field.name:field.id;
		var v = $.trim($(field).val());
		
		var idx;
	    var idxkey;
	    var field;
	    var inIdx;
		
		for(var i=0;i<s.length;i++){
			if(i == 0){
				inIdx = false;
				idx = '';
				field = '';
			}
			var c = s.charAt(i);
			if(c == '['){
	            inIdx = true;
	            idx = '';
	            idxkey = s.substring(0,i);
			} else if(c == ']'){
				if(imap[idxkey]==undefined){
	                imap[idxkey] = [];
	            }
				if($.inArray(idx,imap[idxkey])==-1){
	                imap[idxkey][imap[idxkey].length]=idx;
	            }
				field+='['+(imap[idxkey].length -1)+']';
				inIdx = false;
			} else {
				if(inIdx){
	                idx+=c;
	            } else {
	                field+=c;
	            }
			}
		}
		s = field;
		if(prefix != undefined && prefix.length>0){
			s = prefix + s;
		}
		json[s] = v;
	});
    return json;
}

function fieldToJson1(objId){
	var fields = $('.field',objId);
	var json = {};
	var imap = {};
	$.each(fields, function(i, field){
		/*
		if(field.id){
			json[field.id] = $.trim($(field).val());
		} else {
			json[field.name] = $.trim($(field).val());
		}
		*/
		var s = field.name?field.name:field.id;
		if(field.type && field.type.toLowerCase()=='radio' && field.checked == false){
			return;
		}
		var v = $.trim($(field).val());
		var idx;
		var idxkey;
		var field;
		var inIdx;
		var inField;
		var po;
		var o;

		var i;
		for(i=0;i<s.length;i++){
			if(i==0){
				po = null;
				o = json;
				inIdx = false;
				inField = true;
				idx = '';
				field = 'f.';
			}
			
			var c = s.charAt(i);
			if(c == '['){
				po = o;
				o = [];
				if(field.length>0){
					if(po[field] == undefined){
						po[field] = o;
					} else {
						o = po[field];
					}
				}
				if(idx.length>0){
					if(imap[idxkey]==undefined){
						imap[idxkey] = [];
					}
					if($.inArray(idx,imap[idxkey])==-1){
						imap[idxkey][imap[idxkey].length]=idx;
					}
					if(po[imap[idxkey].length-1]==undefined){
						po[imap[idxkey].length-1] = o;
					} else {
						o = po[imap[idxkey].length-1];
					}
				}
				inIdx = true;
				inField = false;
				idx = '';
				idxkey = s.substring(0,i);
			} else if(c == ']') {
				field = '';
			} else if(c == '.') {
				po = o;
				o = {};
				if(field.length>0){
					if(po[field] == undefined){
						po[field] = o;
					} else {
						o = po[field];
					}
				}
				if(idx.length>0){
					if(imap[idxkey]==undefined){
						imap[idxkey] = [];
					}
					if($.inArray(idx,imap[idxkey])==-1){
						imap[idxkey][imap[idxkey].length]=idx;
					}
					if(po[imap[idxkey].length-1]==undefined){
						po[imap[idxkey].length-1] = o;
					} else {
						o = po[imap[idxkey].length-1];
					}					
				}
				inIdx = false;
				inField = true;
				idx = '';
				field = '';
			} else {
				if(inIdx){
					idx+=c;
				} else {
					field+=c;
				}
			}
		}
		if(field.length >0){
			o[field] = v;
		} else if(idx.length>0){
			if(imap[idxkey]==undefined){
				imap[idxkey] = [];
			}
			if($.inArray(idx,imap[idxkey])==-1){
				imap[idxkey][imap[idxkey].length]=idx;
			}
			o[imap[idxkey].length-1] = v;
		}
	});
    return json;
}

function getCheckedLeafIds(tree, nodeIds){ 
  var nodes = tree;
  if(!$.isArray(tree)){
	  nodes = tree.children;
  }
  for(var j=0;j<nodes.length;j++){
      var node = nodes[j];
      //if(!node.isParent && node.checked){
      if(node.checked){
    	  nodeIds[nodeIds.length]=node.id;
      }
      if(node.isParent){
    	  getCheckedLeafIds(node,nodeIds);     
      }
   }
}

function isEmail(val){
	var filter = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i;
	return isEnglish(val) && filter.test(val);
}

//是否是数字验证
function isNumber(oNum){
	if(!oNum)
		return false;
	var str=/^\d+(\.\d+)?$/;
	if(!str.test(oNum))
		return false;
	try{
		if(parseFloat(oNum)!=oNum)
			return false;
	}catch(ex){
		return false;
	}
	return true;
}

function isEnglish(val){
	filter1 = /^([a-zA-Z0-9@_\.\-\ ])+$/;
	return filter1.test(val);
}

// 正则匹配
function isRegExp(data,rule){
	var pattern = new RegExp(rule);
	return pattern.test(data);
}

// 是否正整数
function isInteger(num){
	if(typeof num == "undefined" || !num) return false;
	var pattern = /^\d+$/;
	return isRegExp(num,pattern);
}

//是否副整数
function isNegative(num){
	if(typeof num == "undefined" || !num) return false;
	var pattern = /^[\-]?\d+$/;
	return isRegExp(num,pattern);
}

// 是否制定长度的整数
function isLimitLengthInteger(num,minLen,maxLen){
	if(!isInteger(num)) return false;
	if(typeof minLen != "number" || typeof maxLen != "number") return false;
	var len = num.length;
	if(len > maxLen || len < minLen) return false;
	return true;
}

// 是否手机号码
function isMobile(mobile){
	if(typeof mobile == "undefined" || !mobile) return false;
	var pattern = /^1(3|5|8)\d{9}$/;
	return isRegExp(mobile,pattern);
}

// 是否IP地址
function isIP(ip){
	if(typeof ip == "undefined" || !ip) return false;
	var pattern = /^([1-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3}$/;
	return isRegExp(ip,pattern);
}

function isCidr(cidr){
	if(typeof cidr == "undefined" || !cidr) return false;
	var ps = cidr.split('/');
	return isIP(ps[0]) && isInteger(ps[1]) && (parseInt(ps[1],10)>0) && parseInt(ps[1],10)<32;
}

function isCidrRange(cidr){
	if(typeof cidr == "undefined" || !cidr) return false;
	var ps = cidr.split('/');
	var pattern = /^([0-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3}$/;
	if(isRegExp(ps[0],pattern)){
		if(isInteger(ps[1])){
			var mask = parseInt(ps[1],10);
			if(mask>-1 && mask<32){
				return true;
			}
		}
	}
	return false;
}

//端口验证
function isPort(port){
  	if(port.indexOf('0') == 0) {
  		return false;
  	}else{
  	    if(isInteger(port)){
  	    	var p = parseInt(port,10);
  	    	if(p<1 || p>65535){
  	    		return false;
  	    	}else{
  	    		return true;
  	    	}
  	    }else{
  	    	return false;
  	    }
  	}
}

//icmp端口验证(-1~255)
function isICMPPort(port){
  	if(port.indexOf('0') == 0) {
  		return false;
  	}else{
  	    if(isNegative(port)){
  	    	var p = parseInt(port, 10);
  	    	if(p<-1 || p>255){
  	    		return false;
  	    	}else{
  	    		return true;
  	    	}
  	    }else{
  	    	return false;
  	    }
  	}
}

function Subnet() {   
    //convert ip to ip number(decimal number)
    function ipNumber(ipAddress) {
        var ip = ipAddress.match(/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/);
        if (ip) {
            return (+ip[1]<<24) + (+ip[2]<<16) + (+ip[3]<<8) + (+ip[4]);
        } else {
            throw new Error("IP format is error");
        }
    }
    //Example: 255.255.255.0 convent to 24
    function convertMaskToDec(mask) {
        var x = ipNumber(mask);
        x = x - ((x >>> 1) & 0x55555555);
        x = (x & 0x33333333) + ((x >>> 2) & 0x33333333);
        x = (x + (x >>> 4)) & 0x0F0F0F0F;
        x = x + (x >>> 8);
        x = x + (x >>> 16);
        return x = x & 0x0000003F; 
    }
  
    function ipMask(mask) {
        var x = convertMaskToDec(mask);
        return -1<<(32-x);
    }

    function toArray(val) { 
        var ret = [];
        for (var j = 3; j >= 0; --j) {
               ret[j] |= ((val >>> 8*(3-j)) & (0xff));
         }           
         return ret;
    }

   function format(octets) {
        var str = ""
        for (var i =0; i < octets.length; ++i){
             str += octets[i];
             if (i != octets.length - 1) {
                    str += ".";
              }
        }
        return str;
    }

    this.isInThisSubnet = function(ipAddress) {
        return (ipNumber(ipAddress) & ipMask(this.mask)) == ipNumber(this.getNetwork());
    }

    this.getBroadcast = function() {
       return format(toArray((ipNumber(this.ip) & ipMask(this.mask)) | ~(ipMask(this.mask))));
    } 

    this.getNetwork = function() {
        return format(toArray((ipNumber(this.ip) & ipMask(this.mask))));
    }

    this.getNetmask = function() {
        return this.mask;
    }
    
    this.getCidr = function() {
        return this.getNetwork() + "/" + convertMaskToDec(this.mask);
    }

    this.init = function(ip, mask) {
        this.ip = ip;
        this.mask = format(toArray(-1<<(32-mask)));
    }
}

function isZh(str){
	 var patrn= /[\u4E00-\u9FA5]/g; 
	 if (patrn.test(str)) {
		   return false; 
		} 
	   return true;
} 

function isName(value){
	if(typeof value == "undefined" || !value) return false;
	var pattern = /^[\w\.\-]+$/;
	return isRegExp(value,pattern);
}

function isLimitName(value,minLength,maxLength,name){
	if(typeof value == "undefined" || value == null || value ==""){
		var result = false;
		var msg = "请输入";
		if(typeof name == "string") msg = msg+name+"!";
	}else{
		var result = isName(value);
		var msg = "为字母、-、_、.、数字组成的";
		if(typeof name == "string") msg = name+msg;
		if(typeof minLength == "number" && typeof maxLength == "number" && maxLength >= minLength){
			msg += minLength+"~"+maxLength+"位";
		}
		msg += "字符串!";
		
		if(result){
			if(typeof minLength == "number" && typeof maxLength == "number" && maxLength >= minLength){
				var len = value.length;
				if(len < minLength || len > maxLength){
					result = false;
				} 
			}
		}
	}
	
	if(!result){
		jAlert(msg,"信息提示");
	}
	return result;
}

// ie8下array对indexOf兼容
if(!Array.prototype.indexOf){
    Array.prototype.indexOf = function(target){
    	len = this.length;
        for(var aindex=0;aindex<len;aindex++){
            if(this[aindex] === target) return aindex;
        }
        return -1;
    };
}

Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

function getSearchFilter(gridId){
	var filter = {};
	$('.searchFilter','#'+gridId+'Filter').each(function(i,v){
		filter[(v.name?v.name:v.id)]=$.trim(v.value);
	});
	return filter;
}