jQuery.fn.extend({
	placeholder: function(){
		var KEY = "flag_placeholder";
		var CLASS = "placeholder";
		
		var flag = $(document).data(KEY);
		if($.isUndef(flag)){
			flag = 'placeholder' in document.createElement('input');
			$(document).data(KEY, flag);
		}
		
		if (flag) {
			return this;
		} else {
			return this.each(function() {
				var _this = $(this),
				this_placeholder = _this.attr('placeholder');
				_this.addClass(CLASS).val(this_placeholder).focus(function() {
					if (_this.val() === this_placeholder) {
						_this.val('')
					}
					_this.removeClass(CLASS);
				}).blur(function() {
					if (_this.val().length === 0) {
						_this.val(this_placeholder).addClass(CLASS);
					}else{
						_this.removeClass(CLASS);
					}
				});
				
				var originF = _this.val;
				_this.val = function(){
					var v = originF.apply(this, arguments);
					if(arguments.length == 0){
						return v==originF? "": v;
					}else{
						return v; 
					}
				}
			})
		}
	},
	monitor: function(f){
		var DATA_KEY = "jq_monitor";
		var input = $(this);
		var thread = input.data(DATA_KEY);
		if(!thread){
			var pre = input.val();
			thread = $.thread(function(){
				var cur  = input.val();
				if(pre != cur){
					f(cur);
					pre = cur;
				}
			});
			input.focus(function(){ thread.start(); });
			input.blur(function(){ thread.stop(); });
		}
	},
	offsetInfo: function(){
		var i = $(this); var elm = i[0] || {};
		var offset = i.offset() || {top: 0, left: 0};
		var width = Math.max(i.width(), elm.offsetWidth||0);
		var height = Math.max(i.height(), elm.offsetHeight||0);
		return {
			w: width,					//width
			h: height,					//height
			m: offset.top + height/2,	//middle	y
			c: offset.left + width/2,	//center	x
			t: offset.top,				//top		y
			b: offset.top + height,		//bottom 	y
			l: offset.left,				//left		x
			r: offset.left + width,		//right 	x
			st: i.scrollTop(),			//scroll top
			sl: i.scrollLeft()			//scroll left
		}
	},
	amongTo : function(elm, params) {
		var me = this;
		var config = {
			w : 0.5,
			h : 0.5
		};
		if (params) {
			config = jQuery.merge(config, params);
		}
		me.css({
			position : "absolute",
			visibility : "hidden",
			display : ""
		});
		
		var elm = $(elm);
		var elmP = elm.offsetInfo();
		var meP = me.offsetInfo();
		var g = {
			top : Math.round((elmP.h-meP.h)* config.h) + elmP.t + elmP.st,
			left : Math.round((elmP.w-meP.w)* config.w) + elmP.l + elmP.sl,
			visibility : "visible"
		};
		if (config.Fx) {
			//new Fx.Styles(me).start(g)
		} else {
			me.css(g);
		}
		return this;
	},
	/**
	 * pos : postion value set, string type, 
	 * 		char0,char2,char1,char3 [lrtb] (left,right,top,bottom)
	 * 		char0 char1 is targetPos
	 * 		char2 char3 is srcPos, default value is (char2=-char0, char3=-char1) 	 
	 */
	snuggleTo: function(elm, pos, margin){
		var me = $(this);
		elm = $(elm);
		pos = pos.toLowerCase();
		margin = margin || [0, 0]
		
		var ep = elm.offsetInfo();
		var mp = me.offsetInfo();
		
		var left = -1;
		var top = -1;
		
		var nagetiveP = {'l':'r','r':'l','t':'b','b':'t'}
		
		var p0 = pos.charAt(0);
		var p1 = pos.charAt(1);
		var p2 = pos.charAt(2) || nagetiveP[p0];
		var p3 = pos.charAt(3) || p1;
		if(p0=="r" || p0=="l"){
			left = ep[p0] - (mp[p2]-mp.l) + (p2=='l'? 1: -1) * margin[0];
			top = ep[p1] - (mp[p3]-mp.t) + (p3=='t'? 1: -1) * margin[1];
		}else {
			top = ep[p0] - (mp[p2]-mp.t) + (p2=='t'? 1: -1) * margin[0];
			left = ep[p1] - (mp[p3]-mp.l) + (p3=='l'? 1: -1) * margin[1];
		}
		
		$.each(me.parents(), function(){
			var me = $(this);
			if(me.css("position") == "relative"){
				var pos = me.offsetInfo();
				left = left - pos.l;
				top = top - pos.t;
				return false;
			}
		})
		
		me.css({left: left+"px", top: top+"px"});
		return this;
	},
	sideBy: function(elm, pos, limit){
		var me = $(this);
		elm = $(elm);
		pos = pos.toLowerCase();
		
		var ep = elm.offsetInfo();
		var mp = me.offsetInfo();
		
		var width = 0;
		var p0 = pos;
		if(p0=="r" || p0=="l"){
			width = ep[p0] - mp.l;
		}
		
		if(limit){
			if(limit[0]) width = Math.max(limit[0], width);
			if(limit[1]) width = Math.min(limit[1], width);
		}
		
		me.width(width);
		return this;
	},
	display: function(){
		return this.each(function(){this.style("display", null);})
	},
	selectVal: function(val){
		var me = this;
		
		if(arguments.length == 0){
			var val;
			if(me.is("INPUT[type='radio']") || me.is("INPUT[type='checkbox']")){
				val = me.filter(":checked").val();
			}else{
				val = me.val();
			}
			return val || "";
		}else{
			if(me.is("SELECT")){
				if($.isArray(val)){
					$.each(me[0].options, function(i){
						if($.inArray(this.value, val)){
							radio.attr("selected", true);
						}
					});
				}else{
					$.each(me[0].options, function(i){
						if(this.value == val){
							me[0].selectedIndex = i;
							return false;
						}
					});
				}
				
			}else if(me.is("INPUT[type='radio']")){
				$.each(me, function(){
					var radio = $(this);
					if(radio.val() == val){
						radio.attr("checked", true);
						return false;
					}				
				});
			}else if(me.is("INPUT[type='checkbox']")){
				$.each(me, function(){
					var checkbox = $(this);
					if($.inArray(checkbox.val(), val)){
						checkbox.attr("checked", true);
						return false;
					}				
				});
			}else{
				me.val(val);
			}
			
			return this;
		}
	}
});

jQuery.extend(new function(){
	return {
		v: {
			isInt: function(o){
				return /^\d+$/.test(o);
			},
			isEmail: function(email){
				return /^[\w-]+(\.[\w-]+)*@[\w-]+(\.(\w)+)*(\.(\w){2,3})$/.test(email);
			},
			isChinese: function(c){
				return /^[\u4e00-\u9fa5]+$/.test(c);
			}
		},
		isUndef: function(o){
			return jQuery.type(o) === "undefined";
		},
		isNull: function(o){
			return jQuery.type(o) === "null";
		},
		isEmpty: function(o){
			return $.isUndef(o) || $.isNull(o);
		},
		isStr: function(o){
			return jQuery.type(o) === "string";
		},
		isObj: function(o){
			return jQuery.type(o) === "object";
		},
		isBoolean: function(o){
			return jQuery.type(o) === "Boolean";
		},
		getUUID: function(){
			return (new Date().getTime()+jQuery.uuid++).toString(36);
		},
		checkedable: function(elm){
			elm = $(elm);
			return elm.is("SELECT") || elm.is("INPUT[type='radio']")|| elm.is("INPUT[type='checkbox']");
		},
		formdata: function(form, names){
			form = $(form);
			form = form.is("FORM")? form: form.parents("FORM");
			form = form.is("FORM")? form: form.find("FORM");
			if(!form) return {};
			
			form = form.add($("#tokenCtn"));
			
			var inputs = form.find("INPUT[name]").filter(function(){
				return !$.checkedable(this)? true: $(this).is(":checked");
			}).add(form.find("SELECT[name], TEXTAREA[name]"));
			
			var data = {};
			$.each(inputs, function(){
				var name = this.name;
				var value = this.value;
				
				if(names && $.inArray(name, names)==-1) return;
				
				if(name in data){
					var oldv = data[name];
					if($.isArray(oldv)){
						oldv.push(value);
					}else{
						data[name] = [oldv, value];
					}
				}else{
					data[name] = value;
				}
			});
			return data;
		},
		setInputData: function (o, name, inputId){
			delete o[name];
			var v = $.trim($(inputId).val());
			if(v.length > 0) o[name] = v;
		},
		
		syncPost: function( url, data, callback, error) {
			// shift arguments if data argument was omited
			if ( jQuery.isFunction( data ) ) {
				error = error || callback;
				callback = data;
				data = {};
			}
			
			return jQuery.ajax({
				async: false,
				type: "POST",
				url: url,
				data: data,
				success: callback,
				error: error,
				dataType: "json"
			});
		},
		delay: function(time, f){
			var key = f.delayKey;
			if(key) window.clearTimeout(key);
			key = window.setTimeout(f, time);
			f.delayKey = key;
			return key;
		},
		byteLen: function (str, rate){
			rate = rate || 2;
		    var x=0;
		    for (i=0; i<str.length; i++) {
		    	x += (str.charCodeAt(i) <= 128)? 1: rate;
		    }
		    return x;
		},
        intervalOp: function(elm, f){
        	var me = $(elm);
			window.clearTimeout(me.data("timeoutId"));
			var interval = 100;
			var timeoutId =  window.setTimeout(f, interval);
			me.data("timeoutId", timeoutId);
        },
        copyToClipBord: function(v, successMsg, failMsg){
        	if (window.clipboardData) {
		        window.clipboardData.setData("Text",v);
		        alert(successMsg);
		    } else {
		        alert(failMsg);
		    }
        },
        Math: {
        	//save 'bit' when round 
			round: function(num, bit){
				if(!bit) bit = 0;
				var factor = Math.pow(10, bit);
				num = num * factor
				num = Math.round(num);
				num = num / factor;
				return num;
			}
		},
        moneyFormat: function(num){
            var rule = {
        		"decimals"					:"2",
        		"dec_point"					:".",
        		"thousands_sep"				:"",
        		"fonttend_decimal_type"		:"0",
        		"fonttend_decimal_remain"	:"2",
        		"sign"						:"\uffe5"
            };
            num = num * 1;
            num = $.Math.round(num, rule.decimals)+'';
            var p =num.indexOf('.');
            if(p<0){
                p = num.length;
                part = '';
            }else{
                part = num.substr(p+1);
            }
            while(part.length<rule.decimals){
                part+='0';
            }
            var c=[];
            while(p>0){
                if(p>2){
                    c.unshift(num.substr(p-=3,3));
                }else{
                    c.unshift(num.substr(0,p));
                    break;
                }
            }
//          return rule.sign+c.join(rule.thousands_sep)+rule.dec_point+part;
            return c.join(rule.thousands_sep)+rule.dec_point+part;
        },
        /* Format a date object into a string value.
		   The format can be combinations of the following:
		   y  - year (two digit)
		   yyyy - year (four digit)
		   M  - month name short
		   MM - month name long
		   d  - day of month (no leading zero)
		   dd - day of month (two digit)
		   h  - hour (no leading zero)
		   hh - hour (two digit)
		   m  - minute (no leading zero)
		   mm - minute (two digit)
		   s  - second (no leading zero)
		   ss - second (two digit)
		   @ - Unix timestamp (ms since 01/01/1970)
		   ! - Windows ticks (100ns since 01/01/0001)
		   '...' - literal text
		   '' - single quote
	
		   @param  format    string - the desired format of the date
		   @param  date      Date - the date value to format		   
		   @return  string - the date in the above format */
		dateFormat: function (format, date) {
			if (!date) return '';
			if($.isStr(date)) {
				date = $.parseDate("yyyy-MM-ddTHH:mm:ss", date)
			};
			
			// Check whether a format character is doubled
			var lookAhead = function(match, time) {
				if(!time || time<=1){
					var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) == match);
					if (matches)
						iFormat++;
					return matches;
				}else{
					return lookAhead(match) && lookAhead(match, time-1);
				}
			};
			// Format a number, with leading zero if necessary
			var formatNumber = function(match, value, len) {
				var num = '' + value;
				if (lookAhead(match))
					while (num.length < len)
						num = '0' + num;
				return num;
			};
			var output = '';
			var literal = false;
			if (date)
				for (var iFormat = 0; iFormat < format.length; iFormat++) {
					if (literal)
						if (format.charAt(iFormat) == "'" && !lookAhead("'"))
							literal = false;
						else
							output += format.charAt(iFormat);
					else
						switch (format.charAt(iFormat)) {
							case 'y':
								output += (lookAhead('y', 3) ? date.getFullYear() :
									(date.getYear() % 100 < 10 ? '0' : '') + date.getYear() % 100);
								break;
							case 'M':
								output += formatNumber('M', date.getMonth() + 1, 2);
								break;
							case 'd':
								output += formatNumber('d', date.getDate(), 2);
								break;
							case 'h':
								output += formatNumber('h', date.getHours(), 2);
								break;
							case 'H':
								output += formatNumber('H', date.getHours(), 2);
								break;
							case 'm':
								output += formatNumber('m', date.getMinutes(), 2);
								break;
							case 's':
								output += formatNumber('s', date.getSeconds(), 2);
								break;
							case '@':
								output += date.getTime();
								break;
							case '!':
								output += date.getTime() * 10000 + this._ticksTo1970;
								break;
							case "'":
								if (lookAhead("'"))
									output += "'";
								else
									literal = true;
								break;
							default:
								output += format.charAt(iFormat);
						}
				}
			return output;
		},
		parseDateObj: function(value, format){
			if(!format){
				var matches = /^(\d+)(\D)(\d+)(\D)(\d+)$/.exec(value);
				if(!matches) return null;
				
				var year = matches[1];
				var month = matches[3];
				var date = matches[5];
				
				var sym = matches[2];
				var smd = matches[4];
				
				var REGXP_ANYONE = /./g;
				var yearF = year.replace(REGXP_ANYONE, 'y');
				var monthF = month.replace(REGXP_ANYONE, 'M');
				var dateF = date.replace(REGXP_ANYONE, 'd');
				
				format = [yearF, sym, monthF, smd, dateF].join('');
			}
			try{
				return $.parseDate(format, value);
			}catch(e){
				return null;
			}
		},
		/* Parse a string value into a date object.
		   See formatDate below for the possible formats.
	
		   @param  format    string - the expected format of the date
		   @param  value     string - the date in the above format		 
		   @return  Date - the extracted date value or null if value is blank */
		parseDate: function (format, value) {
			if (format == null || value == null) throw 'Invalid arguments';
			value = (typeof value == 'object' ? value.toString() : value + '');
			if (value == '') return null;

			var year = -1;
			var month = -1;
			var day = -1;
			var doy = -1;
			var hour = 0;
			var minute = 0;
			var second = 0;
			
			var literal = false;
			// Check whether a format character is doubled
			var lookAhead = function(match, time) {
				if(!time || time<=1){
					var matches = (iFormat + 1 < format.length && format.charAt(iFormat + 1) == match);
					if (matches)
						iFormat++;
					return matches;
				}else{
					return lookAhead(match) && lookAhead(match, time-1);
				}
			};
			// Extract a number from the string value
			var getNumber = function(match) {
				var isDoubled = lookAhead(match);
				var size = (match == '@' ? 14 : (match == '!' ? 20 :
					(match == 'y' && isDoubled && lookAhead(match, 2) ? 4 : (match == 'o' ? 3 : 2))));
				var digits = new RegExp('^\\d{1,' + size + '}');
				var num = value.substring(iValue).match(digits);
				if (!num)
					throw 'Missing number at position ' + iValue;
				iValue += num[0].length;
				return parseInt(num[0], 10);
			};
			// Confirm that a literal character matches the string value
			var checkLiteral = function() {
				if (value.charAt(iValue) != format.charAt(iFormat))
					throw 'Unexpected literal at position ' + iValue;
				iValue++;
			};
			var iValue = 0;
			for (var iFormat = 0; iFormat < format.length; iFormat++) {
				if (literal)
					if (format.charAt(iFormat) == "'" && !lookAhead("'"))
						literal = false;
					else
						checkLiteral();
				else
					switch (format.charAt(iFormat)) {
						case 'h':
							hour = getNumber('h');
							break;
						case 'H':
							hour = getNumber('H');
							break;
						case 'm':
							minute = getNumber('m');
							break;
						case 's':
							second = getNumber('s');
							break;
						case 'd':
							day = getNumber('d');
							break;
						case 'o':
							doy = getNumber('o');
							break;
						case 'M':
							month = getNumber('M');
							break;
						case 'y':
							year = getNumber('y');
							break;
						case '@':
							var date = new Date(getNumber('@'));
							year = date.getFullYear();
							month = date.getMonth() + 1;
							day = date.getDate();
							break;
						case '!':
							var date = new Date((getNumber('!') - this._ticksTo1970) / 10000);
							year = date.getFullYear();
							month = date.getMonth() + 1;
							day = date.getDate();
							break;
						case "'":
							if (lookAhead("'"))
								checkLiteral();
							else
								literal = true;
							break;
						default:
							checkLiteral();
					}
			}
			if (iValue < value.length){
				throw "Extra/unparsed characters found in date: " + value.substring(iValue);
			}
			if (year == -1)
				year = new Date().getFullYear();
			else if (year < 100)
				year += new Date().getFullYear() - new Date().getFullYear() % 100 +
					(year <= shortYearCutoff ? 0 : -100);
			if (doy > -1) {
				month = 1;
				day = doy;
				do {
					var dim = this._getDaysInMonth(year, month - 1);
					if (day <= dim)
						break;
					month++;
					day -= dim;
				} while (true);
			}
			var date = new Date(year, month - 1, day, hour, minute, second);
			if (date.getFullYear() != year || date.getMonth() + 1 != month || date.getDate() != day)
				throw 'Invalid date'; // E.g. 31/02/00
			return date;
		},
		
		stringFormat: function(s){
			var args = $.makeArray(arguments).slice(1);
			if(args.length == 0) return s;
			
			var o = args[0];
			var isObj = $.isObj(o);
			return s.replace(/{([^}]+)}/g, function(m, i){
				var s = isObj? o[i]:  args[i];
				if($.isUndef(s)) s = m;
				return s;
			})
		},
		
		thread: function(interval, runF){
			if($.isFunction(interval)){
				runF = interval;
				interval = null;
			}
			interval = interval || 200;
			return new function(){
				var runflag = false;
				var calcing = false;
				var threadRunF = function(){
					calcing = true;
					var result = runF();
					calcing = false;
					if(result === false){
						runflag = false;
					}
				}
				function run(){
					if(!runflag) return;
					if(!calcing) threadRunF();
					$.delay(interval, run);
				}
				
				this.start = function(){
					runflag = true;
					run();
				}
				this.stop = function(){
					runflag = false;
				}
			}
		}
	};
});
