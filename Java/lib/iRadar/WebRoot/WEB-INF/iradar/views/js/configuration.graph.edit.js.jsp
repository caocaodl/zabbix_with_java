<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.iradar.model.CWebUser"%>
<%@ page import="com.isoft.iradar.core.utils.EasyObject"%>
<%@ page import="com.isoft.iradar.inc.GraphsUtil"%>
<%@ page import="com.isoft.types.CArray"%>
<%@ page import="com.isoft.iradar.inc.UsersUtil"%>
<%@ page import="com.isoft.framework.persistlayer.SQLExecutor"%>
<%@ page import="com.isoft.iradar.RadarContext"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="java.util.Map"%>
<%
	Map data = (Map)request.getAttribute("js/configuration.graph.edit.js");
%>
<script type="text/x-jquery-tmpl" id="itemTpl">
<tr id="items_#{number}" class="sortable">
	<!-- icon + hidden -->
	<td>
		<span class="ui-icon ui-icon-arrowthick-2-n-s move"></span>
		<input type="hidden" id="items_#{number}_gitemid" name="items[#{number}][gitemid]" value="#{gitemid}">
		<input type="hidden" id="items_#{number}_graphid" name="items[#{number}][graphid]" value="#{graphid}">
		<input type="hidden" id="items_#{number}_itemid" name="items[#{number}][itemid]" value="#{itemid}">
		<input type="hidden" id="items_#{number}_sortorder" name="items[#{number}][sortorder]" value="#{sortorder}">
		<input type="hidden" id="items_#{number}_flags" name="items[#{number}][flags]" value="#{flags}">
		<%if ((Nest.value(data, "graphtype").asInteger() != Defines.GRAPH_TYPE_PIE) && (Nest.value(data, "graphtype").asInteger() != Defines.GRAPH_TYPE_EXPLODED)) {%>
			<input type="hidden" id="items_#{number}_type" name="items[#{number}][type]" value="<%=Defines.GRAPH_ITEM_SIMPLE%>">
		<%}%>
	</td>

	<!-- row number -->
	<td>
		<span id="items_#{number}_number" class="items_number">#{number_nr}:</span>
	</td>

	<!-- name -->
	<td>
		<span id="items_#{number}_name" class="link" onclick="">#{name}</span>
	</td>

	<!-- type -->
	<%if ((Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_PIE) || (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_EXPLODED)){%>
		<td>
			<select id="items_#{number}_type" name="items[#{number}][type]" class="input select">
				<option value="<%=Defines.GRAPH_ITEM_SIMPLE%>"><%="Simple"%></option>
				<option value="<%=Defines.GRAPH_ITEM_SUM%>"><%="Graph sum"%></option>
			</select>
		</td>
	<%}%>

	<!-- function -->
	<td>
		<select id="items_#{number}_calc_fnc" name="items[#{number}][calc_fnc]" class="input select">
		<%if ((Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_PIE) || (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_EXPLODED)){%>
			<option value="<%=Defines.CALC_FNC_MIN%>"><%=Cphp._("min")%></option>
			<option value="<%=Defines.CALC_FNC_AVG%>"><%=Cphp._("avg")%></option>
			<option value="<%=Defines.CALC_FNC_MAX%>"><%=Cphp._("max")%></option>
			<option value="<%=Defines.CALC_FNC_LST%>"><%=Cphp._("last")%></option>
		<%}else{%>
			<%if (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_NORMAL){%>
				<option value="<%=Defines.CALC_FNC_ALL%>">all</option>
			<%}%>
				<option value="<%=Defines.CALC_FNC_MIN%>"><%=Cphp._("min")%></option>
				<option value="<%=Defines.CALC_FNC_AVG%>"><%=Cphp._("avg")%></option>
				<option value="<%=Defines.CALC_FNC_MAX%>"><%=Cphp._("max")%></option>
		<%}%>
		</select>
	</td>

	<!-- drawtype -->
	<%if (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_NORMAL){%>
		<td>
			<select id="items_#{number}_drawtype" name="items[#{number}][drawtype]" class="input select">
			<%CArray<Integer> drawtypes = GraphsUtil.graph_item_drawtypes();
				for(int drawtype : drawtypes ) {%>
				<option value="<%=drawtype%>"><%=GraphsUtil.graph_item_drawtype2str(drawtype)%></option>
			<%}%>
			</select>
		</td>
	<%}else{%>
		<input type="hidden" id="items_#{number}_drawtype" name="items[#{number}][drawtype]" value="#{drawtype}">
	<%}%>

	<!-- yaxisside -->
	<%if ((Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_NORMAL) || (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_STACKED)){%>
		<td>
			<select id="items_#{number}_yaxisside" name="items[#{number}][yaxisside]" class="input select">
				<option value="<%=Defines.GRAPH_YAXIS_SIDE_LEFT%>"><%=Cphp._("Left")%></option>
				<option value="<%=Defines.GRAPH_YAXIS_SIDE_RIGHT%>"><%=Cphp._("Right")%></option>
			</select>
		</td>
	<%}else{%>
		<input type="hidden" id="items_#{number}_yaxisside" name="items[#{number}][yaxisside]" value="#{yaxisside}">
	<%}%>

	<!-- color -->
	<td>
		<input type="text" id="items_#{number}_color" name="items[#{number}][color]" class="input text colorpicker"
			onchange="javascript: set_color_by_name('items_#{number}_color', this.value);" maxlength="6" size="7" value="">
		<div id="lbl_items_#{number}_color" name="lbl_items[#{number}][color]" title="#" class="pointer colorpickerLabel"
			onclick="javascript: show_color_picker('items_#{number}_color');">&nbsp;&nbsp;&nbsp;</div>
	</td>

	<!-- remove button -->
	<td>
		<input type="button" class="input link_menu" id="items_#{number}_remove" data-remove="#{number}" value="<%=CHtml.encode(Cphp._("Remove"))%>" onclick="removeItem(this);" />
	</td>
</tr>
</script>
<script type="text/javascript">
	function loadItem(number, gitemid, graphid, itemid, name, type, calc_fnc, drawtype, yaxisside, color, flags) {
		var item = {
			number: number,
			number_nr: number + 1,
			gitemid: gitemid,
			graphid: graphid,
			itemid: itemid,
			type: type,
			calc_fnc: calc_fnc,
			drawtype: drawtype,
			yaxisside: yaxisside,
			color: color,
			sortorder: number,
			flags: flags,
			name: name
		};

		var itemTpl = new Template(jQuery('#itemTpl').html());
		jQuery('#itemButtonsRow').before(itemTpl.evaluate(item));
		jQuery('#items_' + number + '_type').val(type);
		jQuery('#items_' + number + '_calc_fnc').val(calc_fnc);
		jQuery('#items_' + number + '_drawtype').val(drawtype);
		jQuery('#items_' + number + '_yaxisside').val(yaxisside);
		jQuery('#items_' + number + '_color').val(color);
		jQuery('#lbl_items_' + number + '_color').attr('title', '#' + color);
		jQuery('#lbl_items_' + number + '_color').css('background-color', '#' + color);

		incrementNextColor();
		rewriteNameLinks();
	}

	function addPopupValues(list) {
		if (!isset('object', list) || list.object != 'itemid') {
			return false;
		}

		for (var i = 0; i < list.values.length; i++) {
			var number = jQuery('#itemsTable tr.sortable').length,
				item = {
				number: number,
				number_nr: number + 1,
				gitemid: null,
				graphid: <%=Nest.value(data, "graphid").$()%>,
				itemid: list.values[i].itemid,
				type: null,
				calc_fnc: null,
				drawtype: 0,
				yaxisside: 0,
				sortorder: number,
				flags: (typeof list.values[i].flags === 'undefined') ? 0 : list.values[i].flags,
				color: getNextColor(1),
				name: list.values[i].name
			};

			var itemTpl = new Template(jQuery('#itemTpl').html());
			jQuery('#itemButtonsRow').before(itemTpl.evaluate(item));
			jQuery('#items_' + item['number'] + '_calc_fnc').val(<%=Defines.CALC_FNC_AVG%>);
			jQuery('#items_' + item['number'] + '_color').val(item['color']);
			jQuery('#lbl_items_' + item['number'] + '_color').attr('title', '#' + item['color']);
			jQuery('#lbl_items_' + item['number'] + '_color').css('background-color', '#' + item['color']);
		}

		activateSortable();
		rewriteNameLinks();
	}

	function getOnlyHostParam() {
		<%if (!Cphp.empty(Nest.value(data, "is_template").$())){%>
			return '&only_hostid=<%=Nest.value(data, "hostid").$()%>';
		<%}else{%>
			return '&real_hosts=1';
		<%}%>
	}

	function rewriteNameLinks() {
		var size = jQuery('#itemsTable tr.sortable').length;

		for (var i = 0; i < size; i++) {
			var nameLink = 'PopUp("popup.action?writeonly=1&dstfrm=graphForm'
				+ '&dstfld1=items_' + i + '_itemid&dstfld2=items_' + i + '_name'
				+ (jQuery('#items_' + i + '_flags').val() == <%=Defines.RDA_FLAG_DISCOVERY_PROTOTYPE%>
					? '&srctbl=prototypes&parent_discoveryid=<%=Nest.value(data, "parent_discoveryid").$()%>'
						+ '&srcfld3=flags&dstfld3=items_' + i + '_flags'
					: '&srctbl=items')
				+ '<%=!Cphp.empty(Nest.value(data, "normal_only").$()) ? "&normal_only=1" : ""%>'
				+ '&srcfld1=itemid&srcfld2=name" + getOnlyHostParam(), 800, 600)';
			jQuery('#items_' + i + '_name').attr('onclick', nameLink);
		}
	}

	function removeItem(obj) {
		var number = jQuery(obj).data('remove');

		jQuery('#items_' + number).find('*').remove();
		jQuery('#items_' + number).remove();

		recalculateSortOrder();
		activateSortable();
	}

	function recalculateSortOrder() {
		var i = 0;

		// rewrite ids, set "tmp" prefix
		jQuery('#itemsTable tr.sortable').find('*[id]').each(function() {
			var obj = jQuery(this);

			obj.attr('id', 'tmp' + obj.attr('id'));
		});

		jQuery('#itemsTable tr.sortable').each(function() {
			var obj = jQuery(this);

			obj.attr('id', 'tmp' + obj.attr('id'));
		});

		// rewrite ids to new order
		jQuery('#itemsTable tr.sortable').each(function() {
			var obj = jQuery(this);

			// rewrite ids in input fields
			obj.find('*[id]').each(function() {
				var obj = jQuery(this),
					id = obj.attr('id').substring(3),
					part1 = id.substring(0, id.indexOf('items_') + 5),
					part2 = id.substring(id.indexOf('items_') + 6);

				part2 = part2.substring(part2.indexOf('_') + 1);

				obj.attr('id', part1 + '_' + i + '_' + part2);
				obj.attr('name', part1 + '[' + i + '][' + part2 + ']');

				// set sortorder
				if (part2 === 'sortorder') {
					obj.val(i);
				}

				// rewrite color action
				if (part1.substring(0, 3) === 'lbl') {
					obj.attr('onclick', 'javascript: show_color_picker("items_' + i + '_color");');
				}
				else if (part2 === 'color') {
					obj.attr('onchange', 'javascript: set_color_by_name("items_' + i + '_color", this.value);');
				}
			});

			// rewrite ids in <tr>
			var id = obj.attr('id').substring(3),
				part1 = id.substring(0, id.indexOf('items_') + 5);

			obj.attr('id', part1 + '_' + i);

			i++;
		});

		i = 0;

		jQuery('#itemsTable tr.sortable').each(function() {
			// set row number
			jQuery('.items_number', this).text((i + 1) + ':');

			// set remove number
			jQuery('#items_' + i + '_remove').data('remove', i);

			i++;
		});

		rewriteNameLinks();
	}

	function initSortable() {
		var itemsTable = jQuery('#itemsTable'),
			itemsTableWidth = itemsTable.width(),
			itemsTableColumns = jQuery('#itemsTable .header td'),
			itemsTableColumnWidths = [];

		itemsTableColumns.each(function() {
			itemsTableColumnWidths[itemsTableColumnWidths.length] = jQuery(this).width();
		});

		itemsTable.sortable({
			disabled: (jQuery('#itemsTable tr.sortable').length < 2),
			items: 'tbody tr.sortable',
			axis: 'y',
			cursor: 'move',
			handle: 'span.ui-icon-arrowthick-2-n-s',
			tolerance: 'pointer',
			opacity: 0.6,
			update: recalculateSortOrder,
			create: function() {
				// force not to change table width
				itemsTable.width(itemsTableWidth);
			},
			helper: function(e, ui) {
				ui.children().each(function(i) {
					var td = jQuery(this);

					td.width(itemsTableColumnWidths[i]);
				});

				// when dragging element on safari, it jumps out of the table
				if (SF) {
					// move back draggable element to proper position
					ui.css('left', (ui.offset().left - 2) + 'px');
				}

				itemsTableColumns.each(function(i) {
					jQuery(this).width(itemsTableColumnWidths[i]);
				});

				return ui;
			},
			start: function(e, ui) {
				jQuery(ui.placeholder).height(jQuery(ui.helper).height());
			}
		});
	}

	function activateSortable() {
		jQuery('#itemsTable').sortable({disabled: (jQuery('#itemsTable tr.sortable').length < 2)});
	}

	jQuery(function($) {
		$('#tab_previewTab').click(function() {
			var name = 'chart3.action';
			var src = '&name=' + encodeURIComponent($('#name').val())
						+ '&width=' + $('#width').val()
						+ '&height=' + $('#height').val()
						+ '&graphtype=' + $('#graphtype').val()
						+ '&legend=' + ($('#show_legend').is(':checked') ? 1 : 0);

			<%if ((Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_PIE) || (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_EXPLODED)){%>
				name = 'chart7.action';
				src += '&graph3d=' + ($('#show_3d').is(':checked') ? 1 : 0);

			<%}else{%>
				<%if (Nest.value(data, "graphtype").asInteger() == Defines.GRAPH_TYPE_NORMAL){%>
					src += '&percent_left=' + $('#percent_left').val()
							+ '&percent_right=' + $('#percent_right').val();
				<% } %>

				src += '&ymin_type=' + $('#ymin_type').val()
							+ '&ymax_type=' + $('#ymax_type').val()
							+ '&yaxismin=' + $('#yaxismin').val()
							+ '&yaxismax=' + $('#yaxismax').val()
							+ '&ymin_itemid=' + $('#ymin_itemid').val()
							+ '&ymax_itemid=' + $('#ymax_itemid').val()
							+ '&showworkperiod=' + ($('#show_work_period').is(':checked') ? 1 : 0)
							+ '&showtriggers=' + ($('#show_triggers').is(':checked') ? 1 : 0);
			<% } %>

			$('#itemsTable tr.sortable').find('*[name]').each(function(index, value) {
				if (!$.isEmptyObject(value) && value.name != null) {
					src += '&' + value.name + '=' + value.value;
				}
			});

			$('#previewTab img')
				.attr('src', 'styles/themes/<%= UsersUtil.getUserTheme(RadarContext.getIdentityBean(), (SQLExecutor)Nest.value(data, "executor").$(),CWebUser.data()) %>/images/preloader.gif')
				.width(80)
				.height(12);

			$('<img />').attr('src', name + '?period=3600' + src).load(function() {
				$('#previewChar img').remove();
				$('#previewChar').append($(this));
			});
		});

		<% if (!Cphp.empty(Nest.value(data, "templateid").$())) {%>
			$('#graphTab input, #graphTab select').each(function() {
				$(this).attr('disabled', 'disabled');
				$('#itemsTable').sortable({disabled: true});
			});

			var size = $('#itemsTable tr.sortable').length;

			for (var i = 0; i < size; i++) {
				$('.ui-icon').attr('class', 'ui-icon ui-icon-arrowthick-2-n-s state-disabled');
				$('#items_' + i + '_name').removeAttr('onclick');
				$('#items_' + i + '_name').removeAttr('class');
				$('#items_' + i + '_color').removeAttr('onchange');
				$('#lbl_items_' + i + '_color').removeAttr('onclick');
				$('#lbl_items_' + i + '_color').attr('class', 'colorpickerLabel');
			}
		<% } %>

		// Y axis min clean unused fields
		$('#ymin_type').change(function() {
			switch ($(this).val()) {
				case '<%=Defines.GRAPH_YAXIS_TYPE_CALCULATED%>':
					$('#yaxismin').val('');
					$('#ymin_name').val('');
					$('#ymin_itemid').val('0');
					break;

				case '<%=Defines.GRAPH_YAXIS_TYPE_FIXED%>':
					$('#ymin_name').val('');
					$('#ymin_itemid').val('0');
					break;

				default:
					$('#yaxismin').val('');
			}

			$('form[name="graphForm"]').submit();
		});

		// Y axis max clean unused fields
		$('#ymax_type').change(function() {
			switch ($(this).val()) {
				case '<%=Defines.GRAPH_YAXIS_TYPE_CALCULATED%>':
					$('#yaxismax').val('');
					$('#ymax_name').val('');
					$('#ymax_itemid').val('0');
					break;

				case '<%=Defines.GRAPH_YAXIS_TYPE_FIXED%>':
					$('#ymax_name').val('');
					$('#ymax_itemid').val('0');
					break;

				default:
					$('#yaxismax').val('');
			}

			$('form[name="graphForm"]').submit();
		});

		initSortable();
	});
</script>
