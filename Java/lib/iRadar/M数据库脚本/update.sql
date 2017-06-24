ALTER TABLE `acknowledges`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `actions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `alerts`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `application_template`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `applications`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `auditlog`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `auditlog_details`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `autoreg_host`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `conditions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `config`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `dbversion`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `dchecks`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `dhosts`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `drules`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `dservices`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `escalations`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `events`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `expressions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `functions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `globalmacro`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `globalvars`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `graph_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `graph_theme`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `graphs`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `graphs_items`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `group_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `group_prototype`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `groups`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_log`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_str`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_str_sync`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_sync`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_text`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_uint`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `history_uint_sync`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `host_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `host_inventory`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `hostmacro`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `hosts`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `hosts_groups`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `hosts_templates`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `housekeeper`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `httpstep`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `httpstepitem`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `httptest`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `httptestitem`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `icon_map`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `icon_mapping`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `ids`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `images`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `interface`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `interface_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `item_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `items`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `items_applications`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `maintenances`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `maintenances_groups`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `maintenances_hosts`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `maintenances_windows`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `mappings`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `media`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `media_type`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `node_cksum`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `nodes`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opcommand`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opcommand_grp`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opcommand_hst`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opconditions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `operations`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opgroup`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opmessage`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opmessage_grp`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `opmessage_usr`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `optemplate`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `profiles`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `proxy_autoreg_host`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `proxy_dhistory`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `proxy_history`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `regexps`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `rights`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `screens`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `screens_items`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `scripts`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `service_alarms`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `services`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `services_links`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `services_times`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sessions`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `slides`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `slideshows`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_dict`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_func`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_func_bt`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_func_bt_uri`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_id`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_logs`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_param_warn`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_role`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_role_func`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_tenant`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_user`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sys_user_role`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmap_element_url`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmap_url`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmaps`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmaps_elements`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmaps_link_triggers`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `sysmaps_links`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `timeperiods`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `trends`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `trends_uint`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `trigger_depends`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `trigger_discovery`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `triggers`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `user_history`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `users`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `users_groups`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `usrgrp`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;
ALTER TABLE `valuemaps`	CHANGE tenantid tenantid varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT '0' first;