-- 获取系统中数据的时间跨度，获取到的值放到下面的存储过程中，并计算需要放大的倍数
select FROM_UNIXTIME(min(clock)), FROM_UNIXTIME(max(clock)) from history;
select FROM_UNIXTIME(min(clock)), FROM_UNIXTIME(max(clock)) from trends;







-- 执行数据填充的存储过程，其中的i\clock_from\clock_to\history_factor\trends_factor，需要根据上面的时间进行计算设置
delimiter $$
drop procedure if exists wk;
create procedure wk()
 BEGIN
  declare i, history_factor, trends_factor, clock_from, clock_to integer;  
  set clock_from=UNIX_TIMESTAMP('2015/9/17'), clock_to=UNIX_TIMESTAMP('2015/9/23');
  set history_factor=5, trends_factor=10;
  set i=1;
  while i<=history_factor do
    begin
      insert into history (tenantid,itemid,clock,value,ns) select tenantid,itemid,clock-86400*(31*i) clock,value,ns from history where clock between clock_from and clock_to;
      insert into history_str (tenantid,itemid,clock,value,ns) select tenantid,itemid,clock-86400*(31*i) clock,value,ns from history_str where clock between clock_from and clock_to;
      insert into history_text (tenantid,id,itemid,clock,value,ns) select tenantid,id+(100000*i),itemid,clock-86400*(31*i) clock,value,ns from history_text where clock between clock_from and clock_to;
      insert into history_uint (tenantid,itemid,clock,value,ns) select tenantid,itemid,clock-86400*(31*i) clock,value,ns from history_uint where clock between clock_from and clock_to;
      set i = i +1;
    end;
  end while;
  
  set i=1;
  while i<=trends_factor do
    begin
      insert into trends (tenantid,itemid,clock,num,value_min,value_avg,value_max) select tenantid,itemid,clock-86400*(31*i) clock,num,value_min,value_avg,value_max from trends where clock between clock_from and clock_to;
      insert into trends_uint (tenantid,itemid,clock,num,value_min,value_avg,value_max) select tenantid,itemid,clock-86400*(31*i) clock,num,value_min,value_avg,value_max from trends_uint where clock between clock_from and clock_to;
      set i = i +1;
    end;
  end while;
END $$
delimiter ;
call wk();



----

delete from history where clock < UNIX_TIMESTAMP('2015/9/17');
delete from history_str where clock < UNIX_TIMESTAMP('2015/9/17');
delete from history_text where clock < UNIX_TIMESTAMP('2015/9/17');
delete from history_uint where clock < UNIX_TIMESTAMP('2015/9/17');
delete from trends where clock < UNIX_TIMESTAMP('2015/9/17');
delete from trends_uint where clock < UNIX_TIMESTAMP('2015/9/17');


delete from history where clock < UNIX_TIMESTAMP('2015/8/4');
delete from history_str where clock < UNIX_TIMESTAMP('2015/8/4');
delete from history_text where clock < UNIX_TIMESTAMP('2015/8/4');
delete from history_uint where clock < UNIX_TIMESTAMP('2015/8/4');
delete from trends where clock < UNIX_TIMESTAMP('2015/8/4');
delete from trends_uint where clock < UNIX_TIMESTAMP('2015/8/4');