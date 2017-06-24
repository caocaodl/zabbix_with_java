$(function() {
	var monitorItem = {};
	/**
	 * VM 监控指标
	 * 1.cpu     	cpu使用率
	 * 2.memory	 	内存使用率
	 * 3.upflow	 	网络上行
	 * 4.downflow	网络下行
	 */
	var VMItem = {"itemkey1":"cpu"		,"itemCh1":"CPU"	,"itemtype1":"Bar",
				  "itemkey2":"memory"	,"itemCh2":"MEM"	,"itemtype2":"Bar",
				  "itemkey3":"upflow"	,"itemCh3":"网络上行"	,"itemtype3":"Text",
				  "itemkey4":"downflow"	,"itemCh4":"网络下行"	,"itemtype4":"Text",
				  "length":4};
	/**
	 * TOMCAT 监控指标
	 * 1.errorcount     	每秒请求错误数
	 * 2.activesessions	 	活动会话数
	 * 3.curthreadsbusy	 	繁忙线程数
	 * 4.heapmemoryusage	堆当前使用量
	 */
	var TOMCATItem = {"itemkey1":"errorcount"	    ,"itemCh1":"每秒请求错误数" ,"itemtype1":"Text",
				      "itemkey2":"activesessions"	,"itemCh2":"活动会话数"	   ,"itemtype2":"Text",
				      "itemkey3":"curthreadsbusy"	,"itemCh3":"繁忙线程数"	   ,"itemtype3":"Text",
				      "itemkey4":"heapmemoryusage"	,"itemCh4":"堆当前使用量"  ,"itemtype4":"Text",
					  "length":4};
	/**
	 * MYSQL 监控指标
	 * 1.freememory     	缓存空闲内存
	 * 2.threadsconnected	当前连接数
	 * 3.connections	 	总连接数
	 * 4.queriesper			查询量/秒
	 */
	var MYSQLItem = {"itemkey1":"freememory"		,"itemCh1":"缓存空闲内存","itemype1":"Text",
				  	 "itemkey2":"threadsconnected"	,"itemCh2":"当前连接数" ,"itetype2":"Text",
				  	 "itemkey3":"connections"		,"itemCh3":"总连接数"	,"itetype3":"Text",
				  	 "itemkey4":"system_connections"		,"itemCh4":"系统会话个数"	,"itetype4":"Text",
					 "length":4};
	/**
	 * Server 监控指标
	 * 1.cpu     	cpu使用率
	 * 2.memory	 	内存使用率
	 * 3.upflow	 	网络上行
	 * 4.downflow	网络下行
	 */
	var ServerItem = {"itemkey1":"cpu"		,"itemCh1":"CPU" 	 ,"itemtype1":"Text",
				  	  "itemkey2":"memory"	,"itemCh2":"MEM"	 ,"itemtype2":"Text",
				      "itemkey3":"upflow"	,"itemCh3":"网络上行"	 ,"itemtype3":"Text",
				      "itemkey4":"downflow"	,"itemCh4":"网络下行"	 ,"itemtype4":"Text",
					  "length":4};
	/**
	 * Router||RouteSwitch||Switch 监控指标
	 * 1.RuningTime     运行时间
	 * 2.ifNumber	 	网口数量
	 * 3.NetIFInOutERR	网络上下行丢包
	 * 4.netRate		网络上下行速率
	 */
	var netItem = {"itemkey1":"RuningTime"	 ,"itemCh1":"运行时间" 	,"itemtype1":"Text",
				   "itemkey2":"ifNumber"	 ,"itemCh2":"网口数量"		,"itemtype2":"Text",
				   "itemkey3":"NetIFInOutERR","itemCh3":"网络上下行丢包"	,"itemtype3":"Text",
				   "itemkey4":"netRate"		 ,"itemCh4":"网络上下行速率"	,"itemtype4":"Text",
				   "length":4};
	
	
	monitorItem = {"VM":VMItem,"TOMCAT":TOMCATItem,"MYSQL":MYSQLItem,"Server":ServerItem,"Router":netItem,"RouteSwitch":netItem,"Switch":netItem};
});