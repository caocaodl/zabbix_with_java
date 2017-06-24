<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.isoft.iradar.Cphp"%>
<%@ page import="com.isoft.iradar.inc.Defines"%>
<%@ page import="com.isoft.iradar.utils.CJs"%>
<%@ page import="com.isoft.iradar.helpers.CHtml"%>
<%@ page import="com.isoft.types.CArray"%>
<%@ page import="com.isoft.iradar.inc.JsUtil"%>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%
  Map data = (Map)request.getAttribute("data");
%>
<script type="text/x-jquery-tmpl" id="dcheckRowTPL">
	<tr id="dcheckRow_#{dcheckid}">
		<td id="dcheckCell_#{dcheckid}">
			<span class="bold">#{name}</span>
		</td>
		<td>
			<input type="button" class="input link_menu" name="edit" value="<%=CHtml.encode(Cphp._("Edit"))%>"
				onclick="javascript: showNewCheckForm(null, null, '#{dcheckid}');" />
		</td>
		<td>
			<input type="button" class="input link_menu" name="remove" value="<%=CHtml.encode(Cphp._("Remove"))%>"
				onclick="javascript: removeDCheckRow('#{dcheckid}');" />
		</td>
	</tr>
</script>
<script type="text/x-jquery-tmpl" id="uniqRowTPL">
	<div id="uniqueness_criteria_row_#{dcheckid}">
		<input type="radio" id="uniqueness_criteria_#{dcheckid}" name="uniqueness_criteria" class="input radio"
			value="#{dcheckid}" />
		<label for="uniqueness_criteria_#{dcheckid}">#{name}</label>
	</div>
</script>
<script type="text/x-jquery-tmpl" id="newDCheckTPL">
	<div id="new_check_form">
		<div class="objectgroup inlineblock border_dotted ui-corner-all">
			<table class="formElementTable">
				<tbody>
				<tr>
					<td><label for="type"><%=Cphp._("Check type")%></label></td>
					<td><select id="type" name="type" class="input select"></select></td>
				</tr>
				<tr id="newCheckPortsRow" class="hidden">
					<td><label for="ports"><%=Cphp._("Port range")%></label></td>
					<td>
						<input type="text" id="ports" name="ports" value="" class="input text" size="16" maxlength="255">
					</td>
				</tr>
				<tr id="newCheckCommunityRow" class="hidden">
					<td><label for="snmp_community"><%=Cphp._("SNMP community")%></label></td>
					<td><input type="text" id="snmp_community" name="snmp_community" value="" class="input text"
							size="20" maxlength="255"></td>
				</tr>
				<tr id="newCheckKeyRow" class="hidden">
					<td><label for="key_"><%=Cphp._("SNMP Key")%></label></td>
					<td>
						<input type="text" id="key_" name="key_" value="" class="input text" size="20" maxlength="255">
					</td>
				</tr>
				<tr id="newCheckContextRow" class="hidden">
					<td><label for="snmpv3_contextname"><%=Cphp._("Context name")%></label></td>
					<td>
						<input type="text" id="snmpv3_contextname" name="snmpv3_contextname" value="" class="input text" size="20" maxlength="255">
					</td>
				</tr>
				<tr id="newCheckSecNameRow" class="hidden">
					<td><label for="snmpv3_securityname"><%=Cphp._("Security name")%></label></td>
					<td><input type="text" id="snmpv3_securityname" name="snmpv3_securityname" value=""
							class="input text" size="20" maxlength="64"></td>
				</tr>
				<tr id="newCheckSecLevRow" class="hidden">
					<td><label for="snmpv3_securitylevel"><%=Cphp._("Security level")%></label></td>
					<td>
						<select id="snmpv3_securitylevel" name="snmpv3_securitylevel" class="input select" size="1">
							<option value="0"><%=Cphp._("noAuthNoPriv")%> </option>
							<option value="1"><%=Cphp._("authNoPriv")%> </option>
							<option value="2"><%=Cphp._("authPriv")%> </option>
						</select>
					</td>
				</tr>
				<tr id="newCheckAuthProtocolRow" class="hidden">
					<td><label for="snmpv3_authprotocol"><%=Cphp._("Authentication protocol")%></label></td>
					<td>
						<div class="jqueryinputset">
							<input name="snmpv3_authprotocol" id="snmpv3_authprotocol_0" type="radio" checked="checked"
								value="<%=Defines.ITEM_AUTHPROTOCOL_MD5%>" />
							<input name="snmpv3_authprotocol" id="snmpv3_authprotocol_1" type="radio"
								value="<%=Defines.ITEM_AUTHPROTOCOL_SHA%>" />
							<label for="snmpv3_authprotocol_0"><%=Cphp._("MD5")%></label><label for="snmpv3_authprotocol_1"><%=Cphp._("SHA")%></label>
						</div>
					</td>
				</tr>
				<tr id="newCheckAuthPassRow" class="hidden">
					<td><label for="snmpv3_authpassphrase"><%=Cphp._("Authentication passphrase")%></label></td>
					<td><input type="text" id="snmpv3_authpassphrase" name="snmpv3_authpassphrase" value=""
							class="input text" size="20" maxlength="64"></td>
				</tr>
				<tr id="newCheckPrivProtocolRow" class="hidden">
					<td><label for="snmpv3_authprotocol"><%=Cphp._("Authentication protocol")%></label></td>
					<td>
						<div class="jqueryinputset">
							<input name="snmpv3_privprotocol" id="snmpv3_privprotocol_0" type="radio" checked="checked"
								value="<%=Defines.ITEM_PRIVPROTOCOL_DES%>" />
							<input name="snmpv3_privprotocol" id="snmpv3_privprotocol_1" type="radio"
								value="<%=Defines.ITEM_PRIVPROTOCOL_AES%>" />
							<label for="snmpv3_privprotocol_0"><%=Cphp._("DES")%></label><label for="snmpv3_privprotocol_1"><%=Cphp._("AES")%></label>
						</div>
					</td>
				</tr>
				<tr id="newCheckPrivPassRow" class="hidden">
					<td><label for="snmpv3_privpassphrase"><%=Cphp._("Privacy passphrase")%></label></td>
					<td><input type="text" id="snmpv3_privpassphrase" name="snmpv3_privpassphrase" value=""
							class="input text" size="20" maxlength="64"></td>
				</tr>
				</tbody>
			</table>
			<table class="formElementTable">
				<tr>
					<td>
						<input type="button" id="add_new_dcheck" name="add_new_dcheck" class="input button link_menu"
							value="<%=CHtml.encode(Cphp._("Add"))%>" />
					</td>
					<td>
						<input type="button" id="cancel_new_dcheck" name="cancel_new_dcheck" class="input button link_menu"
							value="<%=CHtml.encode(Cphp._("Cancel"))%>" />
					</td>
				</tr>
			</table>
		</div>
	</div>
</script>
<script type="text/javascript">
	var RDA_SVC = {
		ssh: <%=Defines.SVC_SSH%>,
		ldap: <%=Defines.SVC_LDAP%>,
		smtp: <%=Defines.SVC_SMTP%>,
		ftp: <%=Defines.SVC_FTP%>,
		http: <%=Defines.SVC_HTTP%>,
		pop: <%=Defines.SVC_POP%>,
		nntp: <%=Defines.SVC_NNTP%>,
		imap: <%=Defines.SVC_IMAP%>,
		tcp: <%=Defines.SVC_TCP%>,
		agent: <%=Defines.SVC_AGENT%>,
		snmpv1: <%=Defines.SVC_SNMPv1%>,
		snmpv2: <%=Defines.SVC_SNMPv2c%>,
		snmpv3: <%=Defines.SVC_SNMPv3%>,
		icmp: <%=Defines.SVC_ICMPPING%>,
		https: <%=Defines.SVC_HTTPS%>,
		telnet: <%=Defines.SVC_TELNET%>
	};

	var RDA_CHECKLIST = {};

	function discoveryCheckDefaultPort(service) {
		var defPorts = {};
		defPorts[RDA_SVC.ssh] = '22';
		defPorts[RDA_SVC.ldap] = '389';
		defPorts[RDA_SVC.smtp] = '25';
		defPorts[RDA_SVC.ftp] = '21';
		defPorts[RDA_SVC.http] = '80';
		defPorts[RDA_SVC.pop] = '110';
		defPorts[RDA_SVC.nntp] = '119';
		defPorts[RDA_SVC.imap] = '143';
		defPorts[RDA_SVC.tcp] = '0';
		defPorts[RDA_SVC.icmp] = '0';
		defPorts[RDA_SVC.agent] = '10050';
		defPorts[RDA_SVC.snmpv1] = '161';
		defPorts[RDA_SVC.snmpv2] = '161';
		defPorts[RDA_SVC.snmpv3] = '161';
		defPorts[RDA_SVC.https] = '443';
		defPorts[RDA_SVC.telnet] = '23';

		service = service.toString();

		return isset(service, defPorts) ? defPorts[service] : 0;
	}

	function discoveryCheckTypeToString(svcPort) {
		var defPorts = {};
		defPorts[RDA_SVC.ftp] = <%=CJs.encodeJson(Cphp._("FTP"))%>;
		defPorts[RDA_SVC.http] = <%=CJs.encodeJson(Cphp._("HTTP"))%>;
		defPorts[RDA_SVC.https] = <%=CJs.encodeJson(Cphp._("HTTPS"))%>;
		defPorts[RDA_SVC.icmp] = <%=CJs.encodeJson(Cphp._("ICMP ping"))%>;
		defPorts[RDA_SVC.imap] = <%=CJs.encodeJson(Cphp._("IMAP"))%>;
		defPorts[RDA_SVC.tcp] = <%=CJs.encodeJson(Cphp._("TCP"))%>;
		defPorts[RDA_SVC.ldap] = <%=CJs.encodeJson(Cphp._("LDAP"))%>;
		defPorts[RDA_SVC.nntp] = <%=CJs.encodeJson(Cphp._("NNTP"))%>;
		defPorts[RDA_SVC.pop] = <%=CJs.encodeJson(Cphp._("POP"))%>;
		defPorts[RDA_SVC.snmpv1] = <%=CJs.encodeJson(Cphp._("SNMPv1 agent"))%>;
		defPorts[RDA_SVC.snmpv2] = <%=CJs.encodeJson(Cphp._("SNMPv2 agent"))%>;
		defPorts[RDA_SVC.snmpv3] = <%=CJs.encodeJson(Cphp._("SNMPv3 agent"))%>;
		defPorts[RDA_SVC.smtp] = <%=CJs.encodeJson(Cphp._("SMTP"))%>;
		defPorts[RDA_SVC.ssh] = <%=CJs.encodeJson(Cphp._("SSH"))%>;
		defPorts[RDA_SVC.telnet] = <%=CJs.encodeJson(Cphp._("Telnet"))%>;
		defPorts[RDA_SVC.agent] = <%=CJs.encodeJson(Cphp._("iRadar agent"))%>;

		if (typeof svcPort === 'undefined') {
			return defPorts;
		}

		svcPort = parseInt(svcPort, 10);

		return isset(svcPort, defPorts) ? defPorts[svcPort] : <%=CJs.encodeJson(Cphp._("Unknown"))%>;
	}

	function toggleInputs(id, state) {
		jQuery('#' + id).toggle(state);

		if (state) {
			jQuery('#' + id + ' :input').prop('disabled', false);
		}
		else {
			jQuery('#' + id + ' :input').prop('disabled', true);
		}
	}

	function addPopupValues(list) {
		// templates
		var dcheckRowTpl = new Template(jQuery('#dcheckRowTPL').html()),
			uniqRowTpl = new Template(jQuery('#uniqRowTPL').html());

		for (var i = 0; i < list.length; i++) {
			if (empty(list[i])) {
				continue;
			}

			var value = list[i];

			if (typeof value.dcheckid === 'undefined') {
				value.dcheckid = getUniqueId();
			}

			// add
			if (typeof RDA_CHECKLIST[value.dcheckid] === 'undefined') {
				RDA_CHECKLIST[value.dcheckid] = value;

				jQuery('#dcheckListFooter').before(dcheckRowTpl.evaluate(value));

				for (var fieldName in value) {
					if (typeof value[fieldName] === 'string') {
						var input = jQuery('<input>', {
							name: 'dchecks[' + value.dcheckid + '][' + fieldName + ']',
							type: 'hidden',
							value: value[fieldName]
						});

						jQuery('#dcheckCell_' + value.dcheckid).append(input);
					}
				}
			}

			// update
			else {
				RDA_CHECKLIST[value.dcheckid] = value;

				var ignoreNames = ['druleid', 'dcheckid', 'name', 'ports', 'type', 'uniq'];

				// clean values
				jQuery('#dcheckCell_' + value.dcheckid + ' input').each(function(i, item) {
					var itemObj = jQuery(item);

					var name = itemObj.attr('name').replace('dchecks[' + value.dcheckid + '][', '');
					name = name.substring(0, name.length - 1);

					if (jQuery.inArray(name, ignoreNames) == -1) {
						itemObj.remove();
					}
				});

				// set values
				for (var fieldName in value) {
					if (typeof value[fieldName] === 'string') {
						var obj = jQuery('input[name="dchecks[' + value.dcheckid + '][' + fieldName + ']"]');

						if (obj.length) {
							obj.val(value[fieldName]);
						}
						else {
							var input = jQuery('<input>', {
								name: 'dchecks[' + value.dcheckid + '][' + fieldName + ']',
								type: 'hidden',
								value: value[fieldName]
							});

							jQuery('#dcheckCell_' + value.dcheckid).append(input);
						}
					}
				}

				// update check name
				jQuery('#dcheckCell_' + value.dcheckid + ' .bold').text(value['name']);
			}

			// update device uniqueness criteria
			var availableDeviceTypes = [RDA_SVC.agent, RDA_SVC.snmpv1, RDA_SVC.snmpv2, RDA_SVC.snmpv3],
				uniquenessCriteria = jQuery('#uniqueness_criteria_row_' + value.dcheckid);

			if (jQuery.inArray(parseInt(value.type, 10), availableDeviceTypes) > -1) {
				if (uniquenessCriteria.length) {
					jQuery('label[for=uniqueness_criteria_' + value.dcheckid + ']').text(value['name']);
				}
				else {
					jQuery('#uniqList').append(uniqRowTpl.evaluate(value));
				}
			}
			else {
				if (uniquenessCriteria.length) {
					uniquenessCriteria.remove();

					selectUniquenessCriteriaDefault();
				}
			}
		}
	}

	function removeDCheckRow(dcheckid) {
		jQuery('#dcheckRow_' + dcheckid).remove();

		delete(RDA_CHECKLIST[dcheckid]);

		// remove uniqueness criteria
		var obj = jQuery('#uniqueness_criteria_' + dcheckid);

		if (obj.length) {
			if (obj.attr('checked') == 'checked') {
				selectUniquenessCriteriaDefault();
			}

			jQuery('#uniqueness_criteria_row_' + dcheckid).remove();
		}
	}

	function showNewCheckForm(e, dcheckType, dcheckId) {
		var isUpdate = (typeof dcheckId !== 'undefined');

		// remove existing form
		jQuery('#new_check_form').remove();

		if (jQuery('#new_check_form').length == 0) {
			var tpl = new Template(jQuery('#newDCheckTPL').html());

			jQuery('#dcheckList').after(tpl.evaluate());

			// display fields dependent from type
			jQuery('#type').change(function() {
				updateNewDCheckType(dcheckId);
			});

			// display addition snmpv3 security level fields dependent from snmpv3 security level
			jQuery('#snmpv3_securitylevel').change(updateNewDCheckSNMPType);

			// button "add"
			jQuery('#add_new_dcheck').click(function() {
				saveNewDCheckForm(dcheckId);
			});

			// rename button to "update"
			if (isUpdate) {
				jQuery('#add_new_dcheck').val(<%=CJs.encodeJson(Cphp._("Update"))%>);
			}

			// button "remove" form
			jQuery('#cancel_new_dcheck').click(function() {
				jQuery('#new_check_form').remove();
			});

			// port name sorting
			var svcPorts = discoveryCheckTypeToString(),
				portNameSvcValue = {},
				portNameOrder = [];

			for (var key in svcPorts) {
				portNameOrder.push(svcPorts[key]);
				portNameSvcValue[svcPorts[key]] = key;
			}

			portNameOrder.sort();

			for (var i = 0; i < portNameOrder.length; i++) {
				var portName = portNameOrder[i];

				jQuery('#type').append(jQuery('<option>', {
					value: portNameSvcValue[portName],
					text: portName
				}));
			}
		}

		// restore form values
		if (isUpdate) {
			jQuery('#dcheckCell_' + dcheckId + ' input').each(function(i, item) {
				var itemObj = jQuery(item);

				var name = itemObj.attr('name').replace('dchecks[' + dcheckId + '][', '');
				name = name.substring(0, name.length - 1);

				// ignore "name" value bacause it is virtual
				if (name !== 'name') {
					jQuery('#' + name).val(itemObj.val());

					// set radio button value
					var radioObj = jQuery('input[name=' + name + ']');

					if (radioObj.attr('type') == 'radio') {
						radioObj.removeAttr('checked');

						jQuery('#' + name + '_' + itemObj.val()).attr('checked', 'checked');
					}
				}
			});
		}

		updateNewDCheckType(dcheckId);
	}

	function updateNewDCheckType(dcheckId) {
		var dcheckType = parseInt(jQuery('#type').val(), 10);

		var keyRowTypes = {};
		keyRowTypes[RDA_SVC.agent] = true;
		keyRowTypes[RDA_SVC.snmpv1] = true;
		keyRowTypes[RDA_SVC.snmpv2] = true;
		keyRowTypes[RDA_SVC.snmpv3] = true;

		var comRowTypes = {};
		comRowTypes[RDA_SVC.snmpv1] = true;
		comRowTypes[RDA_SVC.snmpv2] = true;

		var secNameRowTypes = {};
		secNameRowTypes[RDA_SVC.snmpv3] = true;

		toggleInputs('newCheckPortsRow', (RDA_SVC.icmp != dcheckType));
		toggleInputs('newCheckKeyRow', isset(dcheckType, keyRowTypes));

		if (isset(dcheckType, keyRowTypes)) {
			var caption = (dcheckType == RDA_SVC.agent)
				? <%=CJs.encodeJson(Cphp._("Key"))%>
				: <%=CJs.encodeJson(Cphp._("SNMP OID"))%>;

			jQuery('#newCheckKeyRow label').text(caption);
		}

		toggleInputs('newCheckCommunityRow', isset(dcheckType, comRowTypes));
		toggleInputs('newCheckSecNameRow', isset(dcheckType, secNameRowTypes));
		toggleInputs('newCheckSecLevRow', isset(dcheckType, secNameRowTypes));
		toggleInputs('newCheckContextRow', isset(dcheckType, secNameRowTypes));

		// get old type
		var oldType = jQuery('#type').data('oldType');

		jQuery('#type').data('oldType', dcheckType);

		// type is changed
		if (RDA_SVC.icmp != dcheckType && typeof oldType !== 'undefined' && dcheckType != oldType) {
			// reset values
			var snmpTypes = [RDA_SVC.snmpv1, RDA_SVC.snmpv2, RDA_SVC.snmpv3],
				ignoreNames = ['druleid', 'name', 'ports', 'type'];

			if (jQuery.inArray(dcheckType, snmpTypes) !== -1 && jQuery.inArray(oldType, snmpTypes) !== -1) {
				// ignore value reset when change type from snmpt's
			}
			else {
				jQuery('#new_check_form input[type="text"]').each(function(i, item) {
					var itemObj = jQuery(item);

					if (jQuery.inArray(itemObj.attr('id'), ignoreNames) < 0) {
						itemObj.val('');
					}
				});

				// reset port to default
				jQuery('#ports').val(discoveryCheckDefaultPort(dcheckType));
			}
		}

		// set default port
		if (jQuery('#ports').val() == '') {
			jQuery('#ports').val(discoveryCheckDefaultPort(dcheckType));
		}

		updateNewDCheckSNMPType();
	}

	function updateNewDCheckSNMPType() {
		var dcheckType = parseInt(jQuery('#type').val(), 10),
			dcheckSecLevType = parseInt(jQuery('#snmpv3_securitylevel').val(), 10);

		var secNameRowTypes = {};
		secNameRowTypes[RDA_SVC.snmpv3] = true;

		var showAuthProtocol = (isset(dcheckType, secNameRowTypes)
			&& (dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV%>
				|| dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV%>));
		var showAuthPass = (isset(dcheckType, secNameRowTypes)
			&& (dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV%>
				|| dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV%>));
		var showPrivProtocol = (isset(dcheckType, secNameRowTypes)
			&& dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV%>);
		var showPrivPass = (isset(dcheckType, secNameRowTypes)
			&& dcheckSecLevType == <%=Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV%>);

		toggleInputs('newCheckAuthProtocolRow', showAuthProtocol);
		toggleInputs('newCheckAuthPassRow', showAuthPass);
		toggleInputs('newCheckPrivProtocolRow', showPrivProtocol);
		toggleInputs('newCheckPrivPassRow', showPrivPass);

		if (showAuthProtocol) {
			jQuery('#newCheckAuthProtocolRow .jqueryinputset').buttonset();
		}
		if (showPrivProtocol) {
			jQuery('#newCheckPrivProtocolRow .jqueryinputset').buttonset();
		}
	}

	function saveNewDCheckForm(dcheckId) {
		var dCheck = jQuery('#new_check_form :input:enabled').serializeJSON();

		// get check id
		dCheck.dcheckid = (typeof dcheckId === 'undefined') ? getUniqueId() : dcheckId;

		// check for duplicates
		for (var rdaDcheckId in RDA_CHECKLIST) {
			if (typeof dcheckId === 'undefined' || (typeof dcheckId !== 'undefined') && dcheckId != rdaDcheckId) {
				if ((typeof dCheck['key_'] === 'undefined' || RDA_CHECKLIST[rdaDcheckId]['key_'] === dCheck['key_'])
						&& (typeof dCheck['type'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['type'] === dCheck['type'])
						&& (typeof dCheck['ports'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['ports'] === dCheck['ports'])
						&& (typeof dCheck['snmp_community'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmp_community'] === dCheck['snmp_community'])
						&& (typeof dCheck['snmpv3_authprotocol'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_authprotocol'] === dCheck['snmpv3_authprotocol'])
						&& (typeof dCheck['snmpv3_authpassphrase'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_authpassphrase'] === dCheck['snmpv3_authpassphrase'])
						&& (typeof dCheck['snmpv3_privprotocol'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_privprotocol'] === dCheck['snmpv3_privprotocol'])
						&& (typeof dCheck['snmpv3_privpassphrase'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_privpassphrase'] === dCheck['snmpv3_privpassphrase'])
						&& (typeof dCheck['snmpv3_securitylevel'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_securitylevel'] === dCheck['snmpv3_securitylevel'])
						&& (typeof dCheck['snmpv3_securityname'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_securityname'] === dCheck['snmpv3_securityname'])
						&& (typeof dCheck['snmpv3_contextname'] === 'undefined'
							|| RDA_CHECKLIST[rdaDcheckId]['snmpv3_contextname'] === dCheck['snmpv3_contextname'])) {
					showModalWindow(
						t(<%=CJs.encodeJson(Cphp._("Discovery check error"))%>),
						<%=CJs.encodeJson(Cphp._("Check already exists."))%>,
						[{text: t(<%=CJs.encodeJson(Cphp._("Close"))%>), click: function() {
							jQuery(this).dialog('destroy');
						}}]
					);

					return null;
				}
			}
		}

		// validate
		var validationErrors = [],
			ajaxChecks = {
				ajaxaction: 'validate',
				ajaxdata: []
			};

		switch (parseInt(dCheck.type, 10)) {
			case RDA_SVC.agent:
				ajaxChecks.ajaxdata.push({
					field: 'itemKey',
					value: dCheck.key_
				});
				break;
			case RDA_SVC.snmpv1:
			case RDA_SVC.snmpv2:
				if (dCheck.snmp_community == '') {
					validationErrors.push(<%=CJs.encodeJson(Cphp._("Incorrect SNMP community."))%>);
				}
			case RDA_SVC.snmpv3:
				if (dCheck.key_ == '') {
					validationErrors.push(<%=CJs.encodeJson(Cphp._("Incorrect SNMP OID."))%>);
				}
				break;
		}

		if (dCheck.type != RDA_SVC.icmp) {
			ajaxChecks.ajaxdata.push({
				field: 'port',
				value: dCheck.ports
			});
		}

		var jqxhr;

		if (ajaxChecks.ajaxdata.length > 0) {
			jQuery('#add_new_dcheck').prop('disabled', true);

			var url = new Curl();
			jqxhr = jQuery.ajax({
				url: url.getPath() + '?output=ajax&sid=' + url.getArgument('sid'),
				data: ajaxChecks,
				dataType: 'json',
				success: function(result) {
					if (!result.result) {
						jQuery.each(result.errors, function(i, val) {
							validationErrors.push(val.error);
						});
					}
				},
				error: function() {
					showModalWindow(
						t(<%=CJs.encodeJson(Cphp._("Discovery check error"))%>),
						<%=CJs.encodeJson(Cphp._("Cannot validate discovery check: invalid request or connection to iRadar server failed."))%>,
						[{text: t(<%=CJs.encodeJson(Cphp._("Close"))%>), click: function() {
							jQuery(this).dialog('destroy');
						}}]
					);

					jQuery('#add_new_dcheck').prop('disabled', false);
				}
			});
		}

		jQuery.when(jqxhr).done(function() {
			jQuery('#add_new_dcheck').prop('disabled', false);

			if (validationErrors.length) {
				showModalWindow(
					t(<%=CJs.encodeJson(Cphp._("Discovery check error"))%>),
					validationErrors.join('\n'),
					[{text: t(<%=CJs.encodeJson(Cphp._("Close"))%>), click: function() {
						jQuery(this).dialog('destroy');
					}}]
				);
			}
			else {
				dCheck.name = jQuery('#type :selected').text();

				if (typeof dCheck.ports !== 'undefined' && dCheck.ports != discoveryCheckDefaultPort(dCheck.type)) {
					dCheck.name += ' (' + dCheck.ports + ')';
				}
				if (dCheck.key_) {
					dCheck.name += ' "' + dCheck.key_ + '"';
				}

				addPopupValues([dCheck]);

				jQuery('#new_check_form').remove();
			}
		});
	}

	function selectUniquenessCriteriaDefault() {
		jQuery('#uniqueness_criteria_ip').attr('checked', 'checked');
	}

	jQuery(document).ready(function() {
		addPopupValues(<%=JsUtil.rda_jssvalue(Cphp.array_values(Nest.value(data,"drule","dchecks").asCArray()))%>);

		jQuery("input:radio[name='uniqueness_criteria'][value=<%=JsUtil.rda_jsvalue(Nest.value(data,"drule","uniqueness_criteria").$())%>]").attr('checked', 'checked');

		jQuery('#newCheck').click(showNewCheckForm);
		jQuery('#clone').click(function() {
			jQuery('#druleid, #delete, #clone').remove();
			jQuery('#cancel').addClass('ui-corner-left');
			jQuery('#form').val('clone');
			jQuery('#name').focus();
		});
	});

	(function($) {
		$.fn.serializeJSON = function() {
			var json = {};

			jQuery.map($(this).serializeArray(), function(n, i) {
				json[n['name']] = n['value'];
			});

			return json;
		};
	})(jQuery);
</script>
