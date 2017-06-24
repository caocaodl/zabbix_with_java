/*
 **	Anderson Ferminiano
 **	contato@andersonferminiano.com -- feel free to contact me for bugs or new implementations.
 **	jQuery ScrollPagination
 **	28th/March/2011
 **	http://andersonferminiano.com/jqueryscrollpagination/
 **	You may use this script for free, but keep my credits.
 **	Thank you.
 */

(function($) {


	$.fn.scrollPagination = function(options) {
		var opts = $.extend($.fn.scrollPagination.defaults, options || {});
		var target = opts.scrollTarget;
		if (target == null) {
			target = obj;
		}
		opts.scrollTarget = target;
		return this.each(function() {
			$.fn.scrollPagination.init($(this), opts);
		});

	};

	$.fn.stopScrollPagination = function() {
		return this.each(function() {
			$(this).attr('scrollPagination', 'disabled');
		});

	};

	var loading = true;
	$.fn.scrollPagination.loadContent = function(obj, opts) {
		var target = opts.scrollTarget,
			$target = $(target),
			$dom = $(document),
			mayLoadContent = $target.scrollTop() + opts.heightOffset >= $dom.height() - $target.height();
		//scroll up or down
		if (!opts.initLoad&&(!loading || $target.scrollTop() < $dom.height() - $target.height())) {
			return;
		};
		if ((mayLoadContent || opts.initLoad)) {
			loading = false;
			if (opts.beforeLoad != null) {
				opts.beforeLoad();
			}
			$(obj).children().attr('rel', 'loaded');
			$.ajax({
				type: 'POST',
				url: opts.contentPage,
				data: opts.contentData,
				success: function(data) {
					loading = true;
					opts.initLoad = false;
					opts.loader(data);
					var objectsRendered = $(obj).children('[rel!=loaded]');

					if (opts.afterLoad != null) {
						opts.afterLoad(objectsRendered);
					}
				},
				dataType: opts.dataType
			});
		}

	};

	$.fn.scrollPagination.init = function(obj, opts) {
		var target = opts.scrollTarget;
		$(obj).attr('scrollPagination', 'enabled');

		$(target).scroll(function(event) {
			if ($(obj).attr('scrollPagination') == 'enabled') {
				$.fn.scrollPagination.loadContent(obj, opts);
			} else {
				event.stopPropagation();
			}
		});

		$.fn.scrollPagination.loadContent(obj, opts);

	};

	$.fn.scrollPagination.defaults = {
		'contentPage': null,
		'contentData': {},
		'beforeLoad': null,
		'afterLoad': null,
		'scrollTarget': null,
		'heightOffset': 0,
		'dataType': null,
		'initLoad': false,
		'loader': function(data) {}
	};
})(jQuery);