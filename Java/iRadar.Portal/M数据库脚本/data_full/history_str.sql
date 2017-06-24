delimiter $$
drop procedure if exists wk_history_str;
create procedure wk_history_str()
 BEGIN
  declare history_factor int default 20;
  declare trends_factor int default 60;
  declare clock_from int default UNIX_TIMESTAMP('2015/8/4');
  declare clock_to int default UNIX_TIMESTAMP('2015/8/28');
  
  DECLARE done INT DEFAULT 0;
  declare i, _clock, __clock, _ns int;
  declare _itemid bigint;
  declare _tenantid, _value varchar(255);  
  
  DECLARE cur CURSOR FOR select tenantid,itemid,clock,value,ns from history_str where clock between clock_from and clock_to;
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1; 
  
  SET done = 0;
  OPEN cur;
  REPEAT   
    FETCH cur INTO _tenantid,_itemid,_clock,_value,_ns;
    IF NOT done THEN
     	set i=1;    	 
  		while i<=history_factor do
      	begin
        set __clock = _clock-i+86400*356;
    		insert into history_str values(_tenantid,_itemid,__clock,_value,_ns);
    		set i = i +1;        
      	end;
   		end while;
    END IF;    
  UNTIL done END REPEAT;
  CLOSE cur; 
  
END $$
delimiter ;
call wk_history_str();