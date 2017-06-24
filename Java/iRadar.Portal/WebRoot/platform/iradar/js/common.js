// jQuery no conflict
if (typeof(jQuery) != 'undefined') {
	jQuery.noConflict();
}
/**
 * �ж������
 */

(function browser() {
	if (jQuery.browser) {
		return;
	}

	jQuery.browser = {};
	jQuery.browser.mozilla = false;
	jQuery.browser.webkit = false;
	jQuery.browser.opera = false;
	jQuery.browser.msie = false;

	var nAgt = navigator.userAgent;
	jQuery.browser.name = navigator.appName;
	jQuery.browser.fullVersion = '' + parseFloat(navigator.appVersion);
	jQuery.browser.majorVersion = parseInt(navigator.appVersion, 10);
	var nameOffset, verOffset, ix;

	// In Opera, the true version is after "Opera" or after "Version"
	if ((verOffset = nAgt.indexOf("Opera")) != -1) {
		jQuery.browser.opera = true;
		jQuery.browser.name = "Opera";
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 6);
		if ((verOffset = nAgt.indexOf("Version")) != -1)
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
	}
	// In MSIE, the true version is after "MSIE" in userAgent
	else if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
		jQuery.browser.msie = true;
		jQuery.browser.name = "Microsoft Internet Explorer";
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 5);
	}
	// In Chrome, the true version is after "Chrome"
	else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
		jQuery.browser.webkit = true;
		jQuery.browser.name = "Chrome";
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 7);
	}
	// In Safari, the true version is after "Safari" or after "Version"
	else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
		jQuery.browser.webkit = true;
		jQuery.browser.name = "Safari";
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 7);
		if ((verOffset = nAgt.indexOf("Version")) != -1)
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
	}
	// In Firefox, the true version is after "Firefox"
	else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
		jQuery.browser.mozilla = true;
		jQuery.browser.name = "Firefox";
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
	}
	// In most other browsers, "name/version" is at the end of userAgent
	else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
		jQuery.browser.name = nAgt.substring(nameOffset, verOffset);
		jQuery.browser.fullVersion = nAgt.substring(verOffset + 1);
		if (jQuery.browser.name.toLowerCase() == jQuery.browser.name.toUpperCase()) {
			jQuery.browser.name = navigator.appName;
		}
	}

	// trim the fullVersion string at semicolon/space if present
	if ((ix = jQuery.browser.fullVersion.indexOf(";")) != -1) {
		jQuery.browser.fullVersion = jQuery.browser.fullVersion.substring(0, ix);
	}
	if ((ix = jQuery.browser.fullVersion.indexOf(" ")) != -1) {
		jQuery.browser.fullVersion = jQuery.browser.fullVersion.substring(0, ix);
	}

	jQuery.browser.majorVersion = parseInt('' + jQuery.browser.fullVersion, 10);
	if (isNaN(jQuery.browser.majorVersion)) {
		jQuery.browser.fullVersion = '' + parseFloat(navigator.appVersion);
		jQuery.browser.majorVersion = parseInt(navigator.appVersion, 10);
	}
	jQuery.browser.version = jQuery.browser.majorVersion;
})();

/* start table�б���ק */
(function($, window) {

	var __bind = function(fn, me) {
			return function() {
				return fn.apply(me, arguments);
			};
		},
		__slice = [].slice;
	var ResizableColumns, parseWidth, pointerX, setWidth;
	parseWidth = function(node) {
		return parseFloat(node.style.width.replace('%', ''));
	};
	setWidth = function(node, width) {
		width = typeof(width) == "number" ? width + "%" : width;
		return node.style.width = "" + width;
	};
	pointerX = function(e) {
		if (e.type.indexOf('touch') === 0) {
			return (e.originalEvent.touches[0] || e.originalEvent.changedTouches[0]).pageX;
		}
		return e.pageX;
	};
	ResizableColumns = (function() {
		ResizableColumns.prototype.defaults = {
			selector: 'tr.header td:visible',
			columnsSelector: 'tr.header',
			store: window.store,
			syncHandlers: true,
			resizeFromBody: false,
			maxWidth: null,
			minWidth: null
		};

		function ResizableColumns($table, options) {
			this.pointerdown = __bind(this.pointerdown, this);
			this.constrainWidth = __bind(this.constrainWidth, this);
			this.options = $.extend({}, this.defaults, options);
			this.$table = $table;
			this.setHeaders();
			this.restoreColumnWidths();
			this.syncHandleWidths();
			$(window).on('resize.rc', ((function(_this) {
				return function() {
					return _this.syncHandleWidths();
				};
			})(this)));
			if (this.options.start) {
				this.$table.bind('column:resize:start.rc', this.options.start);
			}
			if (this.options.resize) {
				this.$table.bind('column:resize.rc', this.options.resize);
			}
			if (this.options.stop) {
				this.$table.bind('column:resize:stop.rc', this.options.stop);
			}
		}

		ResizableColumns.prototype.triggerEvent = function(type, args, original) {
			var event;
			event = $.Event(type);
			event.originalEvent = $.extend({}, original);
			return this.$table.trigger(event, [this].concat(args || []));
		};

		ResizableColumns.prototype.getColumnId = function($el) {
			return this.$table.data('resizable-columns-id') + '-' + $el.data('resizable-column-id');
		};

		ResizableColumns.prototype.setHeaders = function() {
			this.$tableHeaders = this.$table.find(this.options.selector);
			this.assignPercentageWidths();
			return this.createHandles();
		};

		ResizableColumns.prototype.destroy = function() {
			this.$handleContainer.remove();
			this.$table.removeData('resizableColumns');
			return this.$table.add(window).off('.rc');
		};

		ResizableColumns.prototype.assignPercentageWidths = function() {
			return this.$tableHeaders.each((function(_this) {
				return function(_, el) {
					var $el = $(el),
						width = $el.outerWidth() / _this.$table.width() * 100;
					$el.children().attr("type") === "checkbox" ? width = "15px" : width;
					return setWidth($el[0], width);
				};
			})(this));
		};

		ResizableColumns.prototype.createHandles = function() {
			var _ref;
			if ((_ref = this.$handleContainer) != null) {
				_ref.remove();
			}
			this.$table.before((this.$handleContainer = $("<div class='rc-handle-container' />")));
			this.$tableHeaders.each((function(_this) {
				return function(i, el) {
					var $handle,
						$el = $(el);
					if (!i) {
						var child = $el.children();
						child.attr("type") === "checkbox" ? $el.attr("data-noresize", "") : "";
					};
					if (_this.$tableHeaders.eq(i + 1).length === 0 || (_this.$tableHeaders.eq(i).attr('data-noresize') != null) || (_this.$tableHeaders.eq(i + 1).attr('data-noresize') != null)) {
						return;
					}
					$handle = $("<div class='rc-handle' />");
					$handle.data('th', $el);
					return $handle.appendTo(_this.$handleContainer);
				};
			})(this));
			return this.$handleContainer.on('mousedown touchstart', '.rc-handle', this.pointerdown);
		};

		ResizableColumns.prototype.syncHandleWidths = function() {
			return this.$handleContainer.width(this.$table.width()).find('.rc-handle').each((function(_this) {
				return function(_, el) {
					var $el;
					$el = $(el);
					return $el.css({
						left: $el.data('th').outerWidth() + ($el.data('th').offset().left - _this.$handleContainer.offset().left),
						height: _this.options.resizeFromBody ? _this.$table.height() : _this.$table.find(_this.options.columnsSelector).height()
					});
				};
			})(this));
		};

		ResizableColumns.prototype.saveColumnWidths = function() {
			return this.$tableHeaders.each((function(_this) {
				return function(_, el) {
					var $el;
					$el = $(el);
					if ($el.attr('data-noresize') == null) {
						if (_this.options.store != null) {
							return _this.options.store.set(_this.getColumnId($el), parseWidth($el[0]));
						}
					}
				};
			})(this));
		};

		ResizableColumns.prototype.restoreColumnWidths = function() {
			return this.$tableHeaders.each((function(_this) {
				return function(_, el) {
					var $el, width;
					$el = $(el);
					if ((_this.options.store != null) && (width = _this.options.store.get(_this.getColumnId($el)))) {
						return setWidth($el[0], width);
					}
				};
			})(this));
		};

		ResizableColumns.prototype.totalColumnWidths = function() {
			var total;
			total = 0;
			this.$tableHeaders.each((function(_this) {
				return function(_, el) {
					return total += parseFloat($(el)[0].style.width.replace('%', ''));
				};
			})(this));
			return total;
		};

		ResizableColumns.prototype.constrainWidth = function(width) {
			if (this.options.minWidth != null) {
				width = Math.max(this.options.minWidth, width);
			}
			if (this.options.maxWidth != null) {
				width = Math.min(this.options.maxWidth, width);
			}
			return width;
		};

		ResizableColumns.prototype.pointerdown = function(e) {
			var $currentGrip, $leftColumn, $ownerDocument, $rightColumn, newWidths, startPosition, widths;
			e.preventDefault();
			$ownerDocument = $(e.currentTarget.ownerDocument);
			startPosition = pointerX(e);
			$currentGrip = $(e.currentTarget);
			$leftColumn = $currentGrip.data('th');
			$rightColumn = this.$tableHeaders.eq(this.$tableHeaders.index($leftColumn) + 1);
			widths = {
				left: parseWidth($leftColumn[0]),
				right: parseWidth($rightColumn[0])
			};
			newWidths = {
				left: widths.left,
				right: widths.right
			};
			this.$handleContainer.add(this.$table).addClass('rc-table-resizing');
			$leftColumn.add($rightColumn).add($currentGrip).addClass('rc-column-resizing');
			this.triggerEvent('column:resize:start', [$leftColumn, $rightColumn, newWidths.left, newWidths.right], e);
			$ownerDocument.on('mousemove.rc touchmove.rc', (function(_this) {
				return function(e) {
					var difference;
					difference = (pointerX(e) - startPosition) / _this.$table.width() * 100;
					setWidth($leftColumn[0], newWidths.left = _this.constrainWidth(widths.left + difference));
					setWidth($rightColumn[0], newWidths.right = _this.constrainWidth(widths.right - difference));
					if (_this.options.syncHandlers != null) {
						_this.syncHandleWidths();
					}
					return _this.triggerEvent('column:resize', [$leftColumn, $rightColumn, newWidths.left, newWidths.right], e);
				};
			})(this));
			return $ownerDocument.one('mouseup touchend', (function(_this) {
				return function() {
					$ownerDocument.off('mousemove.rc touchmove.rc');
					_this.$handleContainer.add(_this.$table).removeClass('rc-table-resizing');
					$leftColumn.add($rightColumn).add($currentGrip).removeClass('rc-column-resizing');
					_this.syncHandleWidths();
					_this.saveColumnWidths();
					return _this.triggerEvent('column:resize:stop', [$leftColumn, $rightColumn, newWidths.left, newWidths.right], e);
				};
			})(this));
		};

		return ResizableColumns;

	})();

	return $.fn.extend({
		resizableColumns: function() {
			var args, option;
			option = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
			return this.each(function() {
				var $table, data;
				$table = $(this);
				data = $table.data('resizableColumns');
				if (!data) {
					$table.data('resizableColumns', (data = new ResizableColumns($table, option)));
				}
				if (typeof option === 'string') {
					return data[option].apply(data, args);
				}
			});
		}
	});
})(window.jQuery, window);
/* end table�б���ק */

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
	} else {
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

function SDI(msg) {
	if (GK || WK) {
		console.log(msg);
		return true;
	}

	var div_help = document.getElementById('div_help');

	if (typeof(div_help) == 'undefined' || empty(div_help)) {
		var div_help = document.createElement('div');
		var doc_body = document.getElementsByTagName('body')[0];

		if (empty(doc_body)) {
			return false;
		}

		doc_body.appendChild(div_help);
		div_help.setAttribute('id', 'div_help');
		div_help.setAttribute('style', 'position: absolute; left: 10px; top: 100px; border: 1px red solid; width: 400px; height: 400px; background-color: white; font-size: 12px; overflow: auto; z-index: 20;');
	}

	var pre = document.createElement('pre');
	pre.appendChild(document.createTextNode(msg));
	div_help.appendChild(document.createTextNode('DEBUG INFO: '));
	div_help.appendChild(document.createElement('br'));
	div_help.appendChild(pre);
	div_help.appendChild(document.createElement('br'));
	div_help.appendChild(document.createElement('br'));
	div_help.scrollTop = div_help.scrollHeight;

	return true;
}

function SDJ(obj, name) {
	if (GK || WK) {
		console.dir(obj);
		return true;
	}

	var debug = '';
	name = name || 'none';

	for (var key in obj) {
		if (typeof(obj[key]) == name) {
			continue;
		}
		debug += key + ': ' + obj[key] + ' (' + typeof(obj[key]) + ')' + '\n';
	}

	SDI(debug);
}

function addListener(element, eventname, expression, bubbling) {
	bubbling = bubbling || false;
	element = $(element);

	if (element.addEventListener) {
		element.addEventListener(eventname, expression, bubbling);
		return true;
	} else if (element.attachEvent) {
		element.attachEvent('on' + eventname, expression);
		return true;
	} else {
		return false;
	}
}

function removeListener(element, eventname, expression, bubbling) {
	bubbling = bubbling || false;
	element = $(element);

	if (element.removeEventListener) {
		element.removeEventListener(eventname, expression, bubbling);
		return true;
	} else if (element.detachEvent) {
		element.detachEvent('on' + eventname, expression);
		return true;
	} else {
		return false;
	}
}

function cancelEvent(e) {
	if (!e) {
		e = window.event;
	}

	if (!empty(e)) {
		if (IE) {
			e.cancelBubble = true;
			e.returnValue = false;

			if (IE9 && e.preventDefault) {
				e.preventDefault();
			}
		} else {
			e.stopPropagation();
			e.preventDefault();
		}
	}

	return false;
}

function add_variable(o_el, s_name, x_value, s_formname, o_document) {
	var form;

	if (!o_document) {
		o_document = document;
	}

	if (s_formname) {
		if (!(form = o_document.forms[s_formname])) {
			throw "Missing form with name '" + s_formname + "'.";
		}
	} else if (o_el) {
		if (!(form = o_el.form)) {
			throw "Missing form in 'o_el' object";
		}
	} else {
		if (!(form = this.form)) {
			throw "Missing form in 'this' object";
		}
	}

	var o_variable = o_document.createElement('input');
	if (!o_variable) {
		throw "Can't create element";
	}
	o_variable.type = 'hidden';
	o_variable.name = s_name;
	o_variable.id = s_name;
	o_variable.value = x_value;
	form.appendChild(o_variable);

	return true;
}

function checkAll(form_name, chkMain, shkName) {

	var frmForm = document.forms[form_name];
	var value = frmForm.elements[chkMain].checked;

	chkbxRange.checkAll(shkName, value);

	return true;
}

function checkLocalAll(form_name, chkMain, chkName) {
	var frmForm = document.forms[form_name];
	var checkboxes = $$('input[name=' + chkName + ']');

	for (var i = 0; i < checkboxes.length; i++) {
		if (isset('type', checkboxes[i]) && checkboxes[i].type == 'checkbox') {
			checkboxes[i].checked = frmForm.elements[chkMain].checked;
		}
	}

	return true;
}

function clearAllForm(form) {
	form = $(form);

	var inputs = form.getElementsByTagName('input');
	for (var i = 0; i < inputs.length; i++) {
		var type = inputs[i].getAttribute('type');
		switch (type) {
			case 'button':
			case 'hidden':
			case 'submit':
				break;
			case 'checkbox':
				jQuery(inputs[i]).prop('checked', false).trigger('change');
				break;
			case 'text':
			case 'password':
			default:
				jQuery(inputs[i]).val('').trigger('change');
		}
	}

	var selects = form.getElementsByTagName('select');
	for (var i = 0; i < selects.length; i++) {
		var select = selects[i];
		select.selectedIndex = 0;
		jQuery(select).trigger('change');
	}

	var areas = form.getElementsByTagName('textarea');
	for (var i = 0; i < areas.length; i++) {
		jQuery(areas[i]).val('').trigger('change');
	}

	jQuery('.multiselect').each(function() {
		jQuery(this).multiSelect.clean(jQuery(this).attr('id'));
	});

	return true;
}

function close_window() {
	window.setTimeout('window.close();', 500); // solve bug for Internet Explorer
	return false;
}

function Confirm(msg) {
	return confirm(msg, 'title');
}

function create_var(form_name, var_name, var_value, doSubmit) {
	var objForm = is_string(form_name) ? document.forms[form_name] : form_name;
	if (!objForm) {
		return false;
	}

	var objVar = (typeof(objForm[var_name]) != 'undefined') ? objForm[var_name] : null;
	if (is_null(objVar)) {
		objVar = document.createElement('input');
		objVar.setAttribute('type', 'hidden');
		if (!objVar) {
			return false;
		}
		objVar.setAttribute('name', var_name);
		objVar.setAttribute('id', var_name.replace(']', '').replace('[', '_'));
		objForm.appendChild(objVar);
	}

	if (is_null(var_value)) {
		objVar.parentNode.removeChild(objVar);
	} else {
		objVar.value = var_value;
	}

	if (doSubmit) {
		objForm.submit();
	}

	return false;
}

function getDimensions(obj, trueSide) {
	obj = $(obj);

	if (typeof(trueSide) == 'undefined') {
		trueSide = false;
	}

	var dim = {
		left: 0,
		top: 0,
		right: 0,
		bottom: 0,
		width: 0,
		height: 0
	};

	if (!is_null(obj) && typeof(obj.offsetParent) != 'undefined') {
		var dim = {
			left: parseInt(obj.style.left, 10),
			top: parseInt(obj.style.top, 10),
			right: parseInt(obj.style.right, 10),
			bottom: parseInt(obj.style.bottom, 10),
			width: parseInt(obj.style.width, 10),
			height: parseInt(obj.style.height, 10)
		};

		if (!is_number(dim.top)) {
			dim.top = parseInt(obj.offsetTop, 10);
		}
		if (!is_number(dim.left)) {
			dim.left = parseInt(obj.offsetLeft, 10);
		}
		if (!is_number(dim.width)) {
			dim.width = parseInt(obj.offsetWidth, 10);
		}
		if (!is_number(dim.height)) {
			dim.height = parseInt(obj.offsetHeight, 10);
		}

		if (!trueSide) {
			dim.right = dim.left + dim.width;
			dim.bottom = dim.top + dim.height;
		}
	}

	return dim;
}

function getParent(obj, name) {
	if (obj.parentNode.nodeName.toLowerCase() == name.toLowerCase()) {
		return obj.parentNode;
	} else if (obj.parentNode.nodeName.toLowerCase() == 'body') {
		return null;
	} else {
		return getParent(obj.parentNode, name);
	}
}

function getPosition(obj) {
	obj = $(obj);
	var pos = {
		top: 0,
		left: 0
	};

	if (!is_null(obj) && typeof(obj.offsetParent) != 'undefined') {
		pos.left = obj.offsetLeft;
		pos.top = obj.offsetTop;

		try {
			while (!is_null(obj.offsetParent)) {
				obj = obj.offsetParent;
				pos.left += obj.offsetLeft;
				pos.top += obj.offsetTop;

				if (IE && obj.offsetParent.toString() == 'unknown') {
					break;
				}
			}
		} catch (e) {}
	}

	return pos;
}

function get_bodywidth() {

	var w = parseInt(document.body.scrollWidth),
		w2 = parseInt(document.body.offsetWidth);

	if (!w || !w2) {
		// bug12400
		w = w2 = jQuery(window.frameElement).width();
	}
	return (w2 < w) ? w2 : w;
}

function get_cursor_position(e) {
	e = e || window.event;
	var cursor = {
		x: 0,
		y: 0
	};

	if (e.pageX || e.pageY) {
		cursor.x = e.pageX;
		cursor.y = e.pageY;
	} else {
		var de = document.documentElement;
		var b = document.body;
		cursor.x = e.clientX + (de.scrollLeft || b.scrollLeft) - (de.clientLeft || 0);
		cursor.y = e.clientY + (de.scrollTop || b.scrollTop) - (de.clientTop || 0);
	}

	return cursor;
}

function get_scroll_pos() {
	var scrOfX = 0,
		scrOfY = 0;

	// netscape compliant
	if (typeof(window.pageYOffset) == 'number') {
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	}
	// DOM compliant
	else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	}
	// IE6 standards compliant mode
	else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
		scrOfY = document.documentElement.scrollTop;
		scrOfX = document.documentElement.scrollLeft;
	}

	return [scrOfX, scrOfY];
}

function insertInElement(element_name, text, tagName) {
	var elems = (IE) ? $$(tagName + '[name=' + element_name + ']') : document.getElementsByName(element_name);

	for (var key = 0; key < elems.length; key++) {
		if (typeof(elems[key]) != 'undefined' && !is_null(elems[key])) {
			$(elems[key]).update(text);
		}
	}
}

function openWinCentered(url, name, width, height, params) {
	// var top = Math.ceil((screen.height - height) / 2),
	// 	left = Math.ceil((screen.width - width) / 2);

	// if (params.length > 0) {
	// 	params = ', ' + params;
	// }

	// var windowObj = window.open(new Curl(url).getUrl(), name,
	// 	'width=' + width + ', height=' + height + ', top=' + top + ', left=' + left + params
	// );
	// windowObj.focus();
	name = "rda_popup";
	return PopUp(url, width, height, name);
}

function PopUp(url, width, height, form_name) {
	var $doc = jQuery.noConflict(),
		isJump = false;
	if (window.location != window.parent.location && window.parent.jQuery.ui) {
		$doc = window.parent.jQuery.noConflict();
		isJump = true;
	}
	if (!width) {

		var clientWidth = isJump ? window.parent.document.body.clientWidth : window.document.body.clientWidth,
			distance = 100;
		width = clientWidth - distance * 2;
	}

	if (!height) {

		var clientHeight = isJump ? window.parent.document.body.clientHeight : window.document.body.clientHeight,
			distance = 25;
		height = clientHeight - distance * 2;
	}
	if (!form_name) {
		form_name = 'rda_popup';
	}

	//	var left = (screen.width - (width + 150)) / 2;
	//	var top = (screen.height - (height + 150)) / 2;
	//
	//	var popup = window.open(url, form_name, 'width=' + width + ', height=' + height + ', top=' + top + ', left=' + left + ', resizable=yes, scrollbars=yes, location=no, menubar=no');
	//	popup.focus();



	var ifr = $doc("<iframe id='ifr_" + form_name + "' name='" + form_name + "' />").attr({
		src: url,
		scrolling: 'no',
		frameborder: '0',
		allowtransparency: 'true'
	});

	var dia = ifr.dialog({
		title: "Loading...",
		dialogClass: "open_win",
		draggable: true,
		inline: false,
		modal: true,
		width: width,
		height: height,
		resizable: true,
		minWidth: 200,
		minHeight: 100,
		close: function() {
			$doc(this).dialog('destroy');
		}
	});
	if (isJump) {
		var parentIframe = window.frameElement;
		ifr[0].contentWindow.parent.window.originalParentIframe = {
			"id": parentIframe.id,
			"name": parentIframe.name
		};
	}

	var $top = parseInt(dia.parent().css("top"));
	$top = $top < 0 ? 0 : $top;
	dia.parent().css("top", $top + "px");

	(function injectOpener() {
		var iframeE = ifr[0];
		iframeE.style.opacity = 0;
		var cw = iframeE.contentWindow || iframeE;
		if (cw.close_window) {
			var $ = cw.jQuery;
			$("body").addClass("open_win_body");
			var header = $(".header:first"),
			$content=$("textarea#content");
			if (header.find(":input:visible").length == 0) {
				header.hide();
			}
			if($content.length){
				ifr.css("width","inherit");
			}
			iframeE.style.opacity = 1;
			var title = header.find(".left").text() || cw.document.title;
			ifr.dialog("option", "title", title);

		} else {
			setTimeout(injectOpener, 50);
		}
	})();

	ifr.css("width", width - 20);
	return false;
}

function redirect(uri, method, needle) {
	method = method || 'get';
	var url = new Curl(uri);

	if (method.toLowerCase() == 'get') {
		window.location = url.getUrl();
	} else {
		// useless param just for easier loop
		var action = '';
		var domBody = document.getElementsByTagName('body')[0];
		var postForm = document.createElement('form');
		domBody.appendChild(postForm);
		postForm.setAttribute('method', 'post');

		var args = url.getArguments();
		for (var key in args) {
			if (empty(args[key])) {
				continue;
			}
			if (typeof(needle) != 'undefined' && key.indexOf(needle) > -1) {
				action += '&' + key + '=' + args[key];
				continue;
			}

			var hInput = document.createElement('input');
			hInput.setAttribute('type', 'hidden');
			postForm.appendChild(hInput);
			hInput.setAttribute('name', key);
			hInput.setAttribute('value', args[key]);
		}

		postForm.setAttribute('action', url.getPath() + '?' + action.substr(1));
		postForm.submit();
	}

	return false;
}

function showHide(obj, style) {
	if (typeof(style) == 'undefined') {
		style = 'inline';
	}
	if (is_string(obj)) {
		obj = document.getElementById(obj);
	}
	if (!obj) {
		throw 'showHide(): Object not found.';
	}

	if (obj.style.display != 'none') {
		obj.style.display = 'none';
		return 0;
	} else {
		obj.style.display = style;
		return 1;
	}
}

function showHideVisible(obj) {
	if (is_string(obj)) {
		obj = document.getElementById(obj);
	}
	if (!obj) {
		throw 'showHideVisible(): Object not found.';
	}

	if (obj.style.visibility != 'hidden') {
		obj.style.visibility = 'hidden';
	} else {
		obj.style.visibility = 'visible';
	}
}

function showHideByName(name, style) {
	if (typeof(style) == 'undefined') {
		style = 'none';
	}

	var objs = $$('[name=' + name + ']');

	if (empty(objs)) {
		throw 'showHideByName(): Object not found.';
	}

	for (var i = 0; i < objs.length; i++) {
		var obj = objs[i];
		obj.style.display = style;
	}
}

function switchElementsClass(obj, class1, class2) {
	obj = $(obj);
	if (!obj) {
		return false;
	}

	var result = false;

	if (obj.hasClassName(class1)) {
		obj.removeClassName(class1);
		obj.className = class2 + ' ' + obj.className;
		result = class2;
	} else if (obj.hasClassName(class2)) {
		obj.removeClassName(class2);
		obj.className = class1 + ' ' + obj.className;
		result = class1;
	} else {
		obj.className = class1 + ' ' + obj.className;
		result = class1;
	}

	return result;
}

function rda_throw(msg) {
	throw (msg);
}

/**
 * Returns the file name of the given path
 *
 * @param string path
 * @param string suffix
 *
 * @return string
 */

function basename(path, suffix) {
	var name = path.replace(/^.*[\/\\]/g, '');

	if (typeof(suffix) == 'string' && name.substr(name.length - suffix.length) == suffix) {
		name = name.substr(0, name.length - suffix.length);
	}

	return name;
}

/*
 *��֤����
 *ͨ���true
 *
 */

function testEmail(str) {
	//	var reg = /^(\w)+(\.\w+)*@(\w)+((\.\w{2,3}){1,3})$/;
	var reg = /^(\w)+(\.\w+)*@([a-zA-Z0-9]+[-|\-|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	return reg.test(str) ? true : false;
}

/**
 *ƥ������ ���� ��ĸ �»���
 */

function testTextVal(str) {
	var pattern = /^[\w\u4e00-\u9fa5]+$/gi;
	return pattern.test(str) ? true : false;
}

/**
 *����
 **/

function startCPMasterMain() {
	if (PMasters && PMasters['mainpage']) {
		PMasters['mainpage'].startAllDolls();
	}

}

/**
 *ֹͣ
 **/

function stopCPMasterMain() {
	if (PMasters && PMasters['mainpage']) {
		PMasters['mainpage'].stopAllDolls();
	}

}
//imon custom javascript code
(function() {
	var up = window.opener || window.parent;
	if (!up || up != window.parent) return;

	var frm = up.jQuery(window.frameElement);
	window.close_window = function() {
		frm.dialog('close');
	}

	window.opener = up;
})();
/*
 *将 select 替换为 input 自动填充
 */
function replaceAutocomplete() {

	jQuery.ui.autocomplete.prototype._resizeMenu = function() {
		var ul = this.menu.element;
		ul.outerWidth(Math.max(
			// Firefox wraps long text (possibly a rounding bug)
			// so we add 1px to avoid the wrapping (#7513)
			ul.width("").outerWidth() + 30,
			this.element.outerWidth()
		));
	};
	if (!jQuery.ui.autocomplete.refreshource) {

		jQuery.ui.autocomplete.prototype.refreshSource = function(data) {

			this.options.source = data;
			this._initSource();
			this.element.val(data[0].label);
		}
	};
	if (!jQuery.ui.autocomplete.clearSource) {

		jQuery.ui.autocomplete.prototype.clearSource = function(data) {

			this.options.source = [];
			this._initSource();
		}
	};
	var selectArr = jQuery("select[class='input select autocomplete']", jQuery(".body_header form:first"));
	jQuery.each(selectArr, function(index, selectObj) {
		var AUTOCOMPLETE = "-autocomplete",
			$selectObj = jQuery(selectObj),
			seName = selectObj.getAttribute("name"),
			seId = selectObj.getAttribute("id"),
			seCls = selectObj.getAttribute("class"),
			options = selectObj.options,
			source = [],
			autoInput = jQuery("<input>", {
				"id": seId + AUTOCOMPLETE,
				"name": seName + AUTOCOMPLETE,
				"class": seCls
			});
		for (var i = 0, len = options.length; i < len; i++) {

			var option = options[i];
			source.push({
				label: option.label,
				val: option.value
			});
		}
		autoInput.autocomplete({
			source: source,
			width: 90,
			minLength: 0,
			minChars: 0,
			delay: 10,
			select: function(event, ui) {

				var that = jQuery(this),
					autoInputId = that.attr("id"),
					selectId = autoInputId.substring(0, autoInputId.indexOf(AUTOCOMPLETE)),
					select = jQuery("select[id='" + selectId + "']");
				that.val(ui.item.label);
				// 修改select 选中项 
				select.val(ui.item.val);
				// 必须阻止事件的默认行为，否则autocomplete默认会把ui.item.value设为输入框的value值 
				event.preventDefault();
				select.trigger("change");
				that.blur();
			},
			close: function(event, ui) {
				var that = jQuery(this),
					autoInputId = that.attr("id"),
					selectId = autoInputId.substring(0, autoInputId.indexOf(AUTOCOMPLETE)),
					select = jQuery("select[id='" + selectId + "']");
				if (!that.val()) {
					that.val(select.find("option:selected").text());
				}
			}
		}).on("focus", function() {
			jQuery(this).autocomplete("search", "");
		});
		autoInput.val($selectObj.find("option:selected").text());
		$selectObj.hide().after(autoInput);

	});
};

/*scroll autoload */
function scrollPagTool(id, url, start, end, tenantId) {
	jQuery("iframe", window.parent.document).each(function() {

		if (this.contentDocument == document) {
			jQuery(this).attr("scrolling", "yes").css("height", document.body.clientHeight);
			jQuery("html", document).addClass("body-scroll");
			jQuery(document.body).addClass("body-scroll");
			return false;
		}
	});
	(function scrollLoading() {
		var left = jQuery(window).width() / 2 - 130,
			top = jQuery(window).height() / 2,
			loadingCon = jQuery("<div  id='scrollLoading' class='scrollLoading'>Loading...</div>").css({
				"left": left,
				"top": top
			});

		jQuery(document.body).append(loadingCon);
	})()
	var data = {
		start: start,
		output: "ajax",
		tenantid: tenantId
	};
	jQuery.extend(data, (scrollPagTool.getArgs || jQuery.noop)())
	jQuery('#' + id).scrollPagination({
		'initLoad': true,
		'contentPage': url,
		'contentData': data,
		'scrollTarget': jQuery(window),
		'heightOffset': 20,
		'dataType': 'json',
		'afterLoad': function(loadData) {
			jQuery("#scrollLoading").hide();
		},
		'loader': function(loadData) {
			data.start = loadData.data.start;
			var $tbody = jQuery('#' + id + " tbody"),
				$message = $tbody.find(".message");
			if (loadData.data.contenant || $message.parent().next().length) {

				$message.parent().hide();
			} else {
				$message.parent().show();
			}
			$tbody.append(loadData.data.contenant);
		},
		'beforeLoad': function() {
			var loading = jQuery("#scrollLoading");
			if (loading.is(":hidden")) {
				loading.show();
			}
			var $tbody = jQuery('#' + id + " tbody"),
				$message = $tbody.find(".message");
			$message.parent().hide();


		}
	});
};
/**
 *锁定或解锁保存按钮 默认解锁
 *@parameter lock (true||false)
 **/
function lockButton(lock) {
	var lock = lock ? lock : false;
	window.setTimeout(function() {
		jQuery("#save")[0].disabled = lock;
	}, 1);
}
jQuery(function() {
	//bug13300
	if (frameElement && jQuery(frameElement).parents(".ui-dialog.open_win").length) {
		var $body = jQuery("body"),
			header = jQuery(".header:first");
		if (!$body.hasClass("open_win_body")) {
			$body.addClass("open_win_body");
		}
	}


	//disable save button where click, to fix bug #10960
	jQuery("#save[unauto-disable!='true']").click(function() {
		lockButton(true);
	});

	jQuery("table.tableinfo").resizableColumns({});
	replaceAutocomplete();
	//reset location.hash
	(function resetUrlHash(hash) {

		var hash = window.location.hash,
			selectIframe = window.parent.jQuery("#JS_contentTab").tabs('getSelected').find("iframe")[0];
		if (selectIframe && !hash) {
			// locaiton.hash=iframe.src.hash
			var iframeSrc = window.frameElement.src,
				iframeHash = iframeSrc.substring(iframeSrc.indexOf("#!"), iframeSrc.length);
			window.location.hash = hash = iframeHash;
		}
		if (selectIframe && selectIframe.src.indexOf(hash) == -1) {

			var frames = window.parent.jQuery("iframe"),
				iframe = null,
				hrefChange = true;
			for (var i = 0, len = frames.length; i < len; i++) {
				iframe = frames[i];

				if (iframe.src.indexOf(hash) > -1) {
					hrefChange = false;
					window.parent.jQuery("#JS_contentTab").tabs("select", i);
					break;
				}
				if (jQuery(iframe).height() < 1) {
					hrefChange = false;
					iframe.contentWindow.location.href = iframe.src;
				};
			};
			if (hrefChange) {
				selectIframe.contentWindow.location.href = selectIframe.src;
			};
		}

	})();


});