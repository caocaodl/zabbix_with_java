set SQL_BIG_tables = 1;
set max_join_size = 4294967295;

delimiter $$
drop procedure if exists wk;
create procedure wk(out p_out varchar(200))
 BEGIN
  declare i, history_factor, trends_factor, clock_from, clock_to integer;  
  set clock_from=UNIX_TIMESTAMP('2015/8/4'), clock_to=UNIX_TIMESTAMP('2015/8/28');
  set history_factor=30, trends_factor=90;
  
  DECLARE cur CURSOR FOR select tenantid,itemid,clock-86400*(31*i) clock,value,ns from history where clock between clock_from and clock_to;
  OPEN cur;
  REPEAT
    FETCH cur INTO tenantid,itemid,clock,value,ns;
    IF NOT done THEN
    	set i=1;
		while i<=history_factor do
    	begin
			insert into history values(tenantid,itemid,clock,value,ns);
			set i = i +1;
    	end;
 		end while;
    END IF;
  UNTIL done END REPEAT;
  CLOSE cur;
  
  DECLARE cur CURSOR FOR select tenantid,itemid,clock,num,value_min,value_avg,value_max from trends where clock between clock_from and clock_to;
  OPEN cur;
  REPEAT
    FETCH cur INTO tenantid,itemid,clock,num,value_min,value_avg,value_max;
    IF NOT done THEN
    	set i=1;
    	set clock = clock-86400*(31*i);
		while i<=trends_factor do
    	begin
			insert into trends value(tenantid,itemid,clock,num,value_min,value_avg,value_max);
			set i = i +1;
    	end;
 		end while;
    END IF;
  UNTIL done END REPEAT;
  CLOSE cur;
  
  DECLARE cur CURSOR FOR select tenantid,itemid,clock,num,value_min,value_avg,value_max from trends_uint where clock between clock_from and clock_to;
  OPEN cur;
  REPEAT
    FETCH cur INTO tenantid,itemid,clock,num,value_min,value_avg,value_max;
    IF NOT done THEN
    	set i=1;
    	set clock = clock-86400*(31*i);
		while i<=trends_factor do
    	begin
			insert into trends_uint value(tenantid,itemid,clock,num,value_min,value_avg,value_max);
			set i = i +1;
    	end;
 		end while;
    END IF;
  UNTIL done END REPEAT;
  CLOSE cur;  
  
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
call wk(@a);
select @a;
