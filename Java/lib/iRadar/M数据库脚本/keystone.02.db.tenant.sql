select concat('alter table `',table_name,'` add column `tenantid` varchar(64) CHARSET utf8 COLLATE utf8_general_ci DEFAULT \'b77cff72790044bcb876ff96f7bab7c2\' first;') tenantid_sql 
from tables 
where table_schema='iradar' 
and table_name not in ('dbversion','nodes','node_cksum')
order by table_name asc;

select concat('alter table `',table_name,'` change `tenantid` `tenantid` varchar(64) CHARSET utf8 COLLATE utf8_general_ci NULL;') tenantid_sql 
from tables 
where table_schema='iradar' 
and table_name not in ('dbversion','nodes','node_cksum')
order by table_name asc;

select concat('alter table `',table_name,'` drop `tenantid`;') tenantid_sql 
from tables 
where table_schema='iradar' 
and table_name not in ('dbversion','nodes','node_cksum')
order by table_name asc;

select concat('alter table `',table_name,'` change `userid` `userid` varchar(64) CHARSET utf8 COLLATE utf8_general_ci NOT NULL;') tenantid_sql 
from tables 
where table_schema='iradar' 
and table_name in (select table_name from COLUMNS where table_schema='iradar' and column_name='userid')
order by table_name asc;