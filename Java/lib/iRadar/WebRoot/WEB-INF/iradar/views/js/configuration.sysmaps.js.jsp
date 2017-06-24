<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<script type="text/x-jquery-tmpl" id="mapElementFormTpl">
	<form id="selementForm" name="selementForm">
		<input type="hidden" id="elementid" name="elementid">
		<table id="elementFormTable" class="formtable">
			<thead>
			<tr class="header">
				<td id="formDragHandler" colspan="2" class="form_row_first move"><%=Cphp._("Edit map element")%></td>
			</tr>
			</thead>
			<tbody>
			<tr>
				<td>
					<label for="elementType"><%=Cphp._("Type")%></label>
				</td>
				<td>
					<select size="1" class="input select" name="elementtype" id="elementType">
						<option value="<%=Defines.SYSMAP_ELEMENT_TYPE_HOST%>"><%=Cphp._("Host")%></option>
						<option value="<%=Defines.SYSMAP_ELEMENT_TYPE_MAP%>"><%=Cphp._("Map")%></option>
						<option value="<%=Defines.SYSMAP_ELEMENT_TYPE_TRIGGER%>"><%=Cphp._("Trigger")%></option>
						<option value="<%=Defines.SYSMAP_ELEMENT_TYPE_HOST_GROUP%>"><%=Cphp._("Host group")%></option>
						<option value="<%=Defines.SYSMAP_ELEMENT_TYPE_IMAGE%>"><%=Cphp._("Image")%></option>
					</select>
				</td>
			</tr>
			<tr id="subtypeRow">
				<td><%=Cphp._("Show")%></td>
				<td>
					<div class="groupingContent">
						<input id="subtypeHostGroup" type="radio" class="input radio" name="elementsubtype" value="0" checked="checked">
						<label for="subtypeHostGroup"><%=Cphp._("Host group")%></label>
						<br />
						<input id="subtypeHostGroupElements" type="radio" class="input radio" name="elementsubtype" value="1">
						<label for="subtypeHostGroupElements"><%=Cphp._("Host group elements")%></label>
					</div>
				</td>
			</tr>
			<tr id="areaTypeRow">
				<td><%=Cphp._("Area type")%></td>
				<td>
					<div class="groupingContent">
						<input id="areaTypeAuto" type="radio" class="input radio" name="areatype" value="0" checked="checked">
						<label for="areaTypeAuto"><%=Cphp._("Fit to map")%></label>
						<br />
						<input id="areaTypeCustom" type="radio" class="input radio" name="areatype" value="1">
						<label for="areaTypeCustom"><%=Cphp._("Custom size")%></label>
					</div>
				</td>
			</tr>
			<tr id="areaSizeRow">
				<td><%=Cphp._("Area size")%></td>
				<td>
					<label for="areaSizeWidth"><%=Cphp._("Width")%></label>
					<input id="areaSizeWidth" type="text" class="input text" name="width" value="200" size="5">
					<label for="areaSizeHeight"><%=Cphp._("Height")%></label>
					<input id="areaSizeHeight" type="text" class="input text" name="height" value="200" size="5">
				</td>
			</tr>
			<tr id="areaPlacingRow">
				<td>
					<label for="areaPlacing"><%=Cphp._("Placing algorithm")%></label>
				</td>
				<td>
					<select id="areaPlacing" class="input select">
						<option value="<%=Defines.SYSMAP_ELEMENT_AREA_VIEWTYPE_GRID%>"><%=Cphp._("Grid")%></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="elementLabel"><%=Cphp._("Label")%></label>
				</td>
				<td>
					<textarea id="elementLabel" cols="56" rows="4" name="label" class="input textarea_standard"></textarea>
				</td>
			</tr>
			<tr>
				<td>
					<label for="label_location"><%=Cphp._("Label location")%></label>
				</td>
				<td>
					<select id="label_location" class="input select" name="label_location">
						<option value="<%=Defines.MAP_LABEL_LOC_DEFAULT%>"><%=Cphp._("Default")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_BOTTOM%>"><%=Cphp._("Bottom")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_LEFT%>"><%=Cphp._("Left")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_RIGHT%>"><%=Cphp._("Right")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_TOP%>"><%=Cphp._("Top")%></option>
					</select>
				</td>
			</tr>
			<tr id="hostGroupSelectRow">
				<td><%=Cphp._("Host group")%></td>
				<td>
					<div id="elementNameHostGroup" class="multiselect" style="width: 312px;"></div>
				</td>
			</tr>
			<tr id="hostSelectRow">
				<td><%=Cphp._("Host")%></td>
				<td>
					<div id="elementNameHost" class="multiselect" style="width: 312px;"></div>
				</td>
			</tr>
			<tr id="triggerSelectRow">
				<td><%=Cphp._("Trigger")%></td>
				<td>
					<input readonly="readonly" size="50" id="elementNameTrigger" name="elementName" class="input">
					<input type="hidden" id="elementExpressionTrigger" name="elementExpressionTrigger">
					<span class="link" onclick="PopUp('popup.action?writeonly=1&dstfrm=selementForm&dstfld1=elementid&dstfld2=elementNameTrigger&dstfld3=elementExpressionTrigger&srctbl=triggers&srcfld1=triggerid&srcfld2=description&srcfld3=expression&with_triggers=1',450,450)"><%=Cphp._("Select")%></span>
				</td>
			</tr>
			<tr id="mapSelectRow">
				<td><%=Cphp._("Map")%></td>
				<td>
					<input readonly="readonly" size="50" id="elementNameMap" name="elementName" class="input">
					<span class="link" onclick='PopUp("popup.action?srctbl=sysmaps&srcfld1=sysmapid&srcfld2=name&dstfrm=selementForm&dstfld1=elementid&dstfld2=elementNameMap&writeonly=1&excludeids[]=#{sysmapid}", 450, 450)'><%=Cphp._("Select")%></span>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<fieldset>
						<legend><%=Cphp._("Icons")%></legend>
						<table>
							<tbody>
							<tr id="useIconMapRow">
								<td colspan="2">
									<label for="use_iconmap" id=use_iconmapLabel><%=Cphp._("Automatic icon selection")%></label>
									<input type="checkbox" name="use_iconmap" id="use_iconmap" class="checkbox" value="1">
								</td>
							</tr>
							<tr>
								<td>
									<label for="iconid_off"><%=Cphp._("Default")%></label>
									<br />
									<select class="input select" name="iconid_off" id="iconid_off"></select>
								</td>
								<td id="iconProblemRow">
									<label for="iconid_on"><%=Cphp._("Problem")%></label>
									<br />
									<select class="input select" name="iconid_on" id="iconid_on"></select>
								</td>
							</tr>
							<tr>
								<td id="iconMainetnanceRow">
									<label for="iconid_maintenance"><%=Cphp._("Maintenance")%></label>
									<br />
									<select class="input select" name="iconid_maintenance" id="iconid_maintenance"></select>
								</td>
								<td id="iconDisabledRow">
									<label for="iconid_disabled"><%=Cphp._("Disabled")%></label>
									<br />
									<select class="input select" name="iconid_disabled" id="iconid_disabled"></select>
								</td>
							</tr>
							</tbody>
						</table>
					</fieldset>
				</td>
			</tr>

			<tr>
				<td><%=Cphp._("Coordinates")%></td>
				<td>
					<label for="x"><%=Cphp._("X")%></label>:
					<input id="x" maxlength="5" value="0" size="5" name="x" class="input">
					<label for="y"><%=Cphp._("Y")%></label>:
					<input maxlength="5" value="0" size="5" id="y" name="y" class="input">
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<fieldset>
						<legend><%=Cphp._("URLs")%></legend>
						<table>
							<thead>
							<tr>
								<td><%=Cphp._("Name")%></td>
								<td><%=Cphp._("URL")%></td>
								<td></td>
							</tr>
							</thead>
							<tbody id="urlContainer"></tbody>
							<tfoot>
							<tr>
								<td colspan="3"><span id="newSelementUrl" class="link_menu"><%=Cphp._("Add")%></span></td>
							</tr>
							</tfoot>
						</table>
					</fieldset>
				</td>
			</tr>
			<tr class="footer">
				<td colspan="2" class="form_row_last">
					<input id="elementApply" class="element-edit-control jqueryinput" type="button" name="apply" value="<%=Chtml.encode(Cphp._("Apply"))%>">
					<input id="elementRemove" class="element-edit-control jqueryinput" type="button" name="remove" value="<%=Chtml.encode(Cphp._("Remove"))%>">
					<input id="elementClose" class="jqueryinput" type="button" name="close" value=<%=Chtml.encode(Cphp._("Close"))%>>
				</td>
			</tr>
			</tbody>
		</table>
	</form>
</script>

<script type="text/x-jquery-tmpl" id="mapMassFormTpl">
	<form id="massForm">
		<table class="formtable">
			<tbody>
			<tr class="header">
				<td id="massDragHandler" colspan="2" class="form_row_first move">
					<%=Cphp._("Mass update elements")%>&nbsp;
					(<span id="massElementCount"></span>&nbsp;<%=Cphp._("elements")%>)
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<%=Cphp._("Selected elements")%>:
					<div id="elements-selected">
						<table class="tableinfo">
							<tbody id="massList"></tbody>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_label" id="chkboxLabel" class="checkbox">
					<label for="chkboxLabel"><%=Cphp._("Label")%></label>
				</td>
				<td>
					<textarea id="massLabel" cols="56" rows="4" name="label" class="input textarea_standard"></textarea>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_label_location" id="chkboxLabelLocation" class="checkbox">
					<label for="chkboxLabelLocation"><%=Cphp._("Label location")%></label>
				</td>
				<td>
					<select id="massLabelLocation" class="input select" name="label_location">
						<option value="<%=Defines.MAP_LABEL_LOC_DEFAULT%>"><%=Cphp._("Default")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_BOTTOM%>"><%=Cphp._("Bottom")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_LEFT%>"><%=Cphp._("Left")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_RIGHT%>"><%=Cphp._("Right")%></option>
						<option value="<%=Defines.MAP_LABEL_LOC_TOP%>"><%=Cphp._("Top")%></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_use_iconmap" id="chkboxMassUseIconmap" class="checkbox">
					<label for="chkboxMassUseIconmap"><%=Cphp._("Automatic icon selection")%></label>
				</td>
				<td>
					<input type="checkbox" name="use_iconmap" id="massUseIconmap" class="checkbox" value="1">
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_iconid_off" id="chkboxMassIconidOff" class="checkbox">
					<label for="chkboxMassIconidOff"><%=Cphp._("Icon (default)")%></label>
				</td>
				<td>
					<select class="input select" name="iconid_off" id="massIconidOff"></select>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_iconid_on" id="chkboxMassIconidOn" class="checkbox">
					<label for="chkboxMassIconidOn"><%=Cphp._("Icon (problem)")%></label>
				</td>
				<td>
					<select class="input select" name="iconid_on" id="massIconidOn"></select>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_iconid_maintenance" id="chkboxMassIconidMaintenance" class="checkbox">
					<label for="chkboxMassIconidMaintenance"><%=Cphp._("Icon (maintenance)")%></label>
				</td>
				<td>
					<select class="input select" name="iconid_maintenance" id="massIconidMaintenance"></select>
				</td>
			</tr>
			<tr>
				<td>
					<input type="checkbox" name="chkbox_iconid_disabled" id="chkboxMassIconidDisabled" class="checkbox">
					<label for="chkboxMassIconidDisabled"><%=Cphp._("Icon (disabled)")%></label>
				</td>
				<td>
					<select class="input select" name="iconid_disabled" id="massIconidDisabled"></select>
				</td>
			</tr>
			<tr class="footer">
				<td colspan="2" class="form_row_last">
					<input id="massApply" class="element-edit-control jqueryinput" type="button" name="apply" value="<%=Chtml.encode(Cphp._("Apply"))%>">
					<input id="massRemove" class="element-edit-control jqueryinput" type="button" name="remove" value="<%=Chtml.encode(Cphp._("Remove"))%>">
					<input id="massClose" class="jqueryinput" type="button" name="close" value=<%=Chtml.encode(Cphp._("Close"))%>>
				</td>
			</tr>
			</tbody>
		</table>
	</form>
</script>

<script type="text/x-jquery-tmpl" id="mapMassFormListRow">
	<tr>
		<td>#{elementType}</td>
		<td>#{elementName}</td>
	</tr>
</script>

<script type="text/x-jquery-tmpl" id="linkFormTpl">
	<div id="mapLinksContainer">
		<table id="element-links" class="tableinfo element-links">
			<caption><%=Cphp._("Links for the selected element")%></caption>
			<thead>
			<tr class="header">
				<td></td>
				<td><%=Cphp._("Element name")%></td>
				<td><%=Cphp._("Link indicators")%></td>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
		<table id="mass-element-links" class="tableinfo element-links">
			<caption><%=Cphp._("Links between the selected elements")%></caption>
			<thead>
			<tr class="header">
				<td></td>
				<td><%=Cphp._("From")%></td>
				<td><%=Cphp._("To")%></td>
				<td><%=Cphp._("Link indicators")%></td>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
	<form id="linkForm" name="linkForm">
		<input type="hidden" name="selementid1">

		<table class="formtable">
			<tbody>
			<tr>
				<td>
					<label for="linklabel"><%=Cphp._("Label")%></label>
				</td>
				<td>
					<textarea cols="48" rows="4" name="label" id="linklabel" class="input textarea_standard"></textarea>
				</td>
			</tr>
			<tr id="link-connect-to">
				<td>
					<label for="selementid2"><%=Cphp._("Connect to")%></label>
				</td>
				<td>
					<select class="input select" name="selementid2" id="selementid2"></select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="drawtype"><%=Cphp._("Type (OK)")%></label>
				</td>
				<td>
					<select size="1" class="input select" name="drawtype" id="drawtype">
						<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_LINE%>"><%=Cphp._("Line")%></option>
						<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_BOLD_LINE%>"><%=Cphp._("Bold line")%></option>
						<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_DOT%>"><%=Cphp._("Dot")%></option>
						<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_DASHED_LINE%>"><%=Cphp._("Dashed line")%></option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="color"><%=Cphp._("Colour (OK)")%></label>
				</td>
				<td>
					<input maxlength="6" size="7" id="color" name="color" class="input colorpicker">
					<div id="lbl_color" class="pointer colorpickerLabel">&nbsp;&nbsp;&nbsp;</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<fieldset>
						<legend><%=Cphp._("Link indicators")%></legend>
						<table>
							<thead>
							<tr>
								<td><%=Cphp._("Triggers")%></td>
								<td><%=Cphp._("Type")%></td>
								<td><%=Cphp._("Colour")%></td>
								<td></td>
							</tr>
							</thead>
							<tbody id="linkTriggerscontainer"></tbody>
							<tfoot>
							<tr>
								<td colspan="4">
									<span class="link_menu" onclick="PopUp('popup.action?srctbl=triggers&srcfld1=triggerid&real_hosts=1&reference=linktrigger&multiselect=1&writeonly=1&with_triggers=1');"><%=Cphp._("Add")%></span>
								</td>
							</tr>
							</tfoot>
						</table>
					</fieldset>
				</td>
			</tr>
			<tr class="footer">
				<td colspan="2" class="form_row_last">
					<input id="formLinkApply" type="button" value="<%=Chtml.encode(Cphp._("Apply"))%>">
					<input id="formLinkRemove" type="button" value="<%=Chtml.encode(Cphp._("Remove"))%>">
					<input id="formLinkClose" type="button" value="<%=Chtml.encode(Cphp._("Close"))%>">
				</td>
			</tr>
			</tbody>
		</table>
	</form>
</script>

<script type="text/x-jquery-tmpl" id="elementLinkTableRowTpl">
	<tr>
		<td><span class="link_menu openlink" data-linkid="#{linkid}"><%=Cphp._("Edit")%></span></td>
		<td>#{toElementName}</td>
		<td class="pre">#{linktriggers}</td>
	</tr>
</script>

<script type="text/x-jquery-tmpl" id="massElementLinkTableRowTpl">
	<tr>
		<td><span class="link_menu openlink" data-linkid="#{linkid}"><%=Cphp._("Edit")%></span></td>
		<td>#{fromElementName}</td>
		<td>#{toElementName}</td>
		<td class="pre">#{linktriggers}</td>
	</tr>
</script>

<script type="text/x-jquery-tmpl" id="linkTriggerRow">
	<tr id="linktrigger_#{linktriggerid}">
		<td>#{desc_exp}</td>
		<td>
			<input type="hidden" name="linktrigger_#{linktriggerid}_desc_exp" value="#{desc_exp}" />
			<input type="hidden" name="linktrigger_#{linktriggerid}_triggerid" value="#{triggerid}" />
			<input type="hidden" name="linktrigger_#{linktriggerid}_linktriggerid" value="#{linktriggerid}" />
			<select id="linktrigger_#{linktriggerid}_drawtype" name="linktrigger_#{linktriggerid}_drawtype" class="input select">
				<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_LINE%>"><%=Cphp._("Line")%></option>
				<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_BOLD_LINE%>"><%=Cphp._("Bold line")%></option>
				<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_DOT%>"><%=Cphp._("Dot")%></option>
				<option value="<%=Defines.GRAPH_ITEM_DRAWTYPE_DASHED_LINE%>"><%=Cphp._("Dashed line")%></option>
			</select>
		</td>
		<td>
			<input maxlength="6" value="#{color}" size="7" id="linktrigger_#{linktriggerid}_color" name="linktrigger_#{linktriggerid}_color" class="input colorpicker">
			<div id="lbl_linktrigger_#{linktriggerid}_color" class="pointer colorpickerLabel">&nbsp;&nbsp;&nbsp;</div>
		</td>
		<td>
			<span class="link_menu triggerRemove" data-linktriggerid="#{linktriggerid}""><%=Cphp._("Remove")%></span>
		</td>
	</tr>
</script>

<script type="text/x-jquery-tmpl" id="selementFormUrls">
	<tr id="urlrow_#{selementurlid}" class="even_row">
		<td><input class="input" name="url_#{selementurlid}_name" type="text" size="16" value="#{name}"></td>
		<td><input class="input" name="url_#{selementurlid}_url" type="text" size="32" value="#{url}"></td>
		<td><span class="link_menu" onclick="jQuery('#urlrow_#{selementurlid}').remove();"><%=Cphp._("Remove")%></span></td>
	</tr>
</script>

<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery('.print-link').click(function () {
		IRADAR.apps.map.object.updateImage();

		jQuery('div.printless').unbind('click').click(function () {
			printLess(false);
			IRADAR.apps.map.object.updateImage();

			return false;
		});

		return false;
	});
})
</script>
