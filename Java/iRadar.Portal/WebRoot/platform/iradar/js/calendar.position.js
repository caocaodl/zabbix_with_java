
var calendarPosition=getPosition;
/**
 * 获取日期 拾色器 坐标
 * @return {}
 */
getPosition = function(){
	
	var CALENDAR="calendar",//日历
	    LABELCLS="color",//拾色器	    
		obj=arguments[0],		
		colorCls=obj.getAttribute("class"),
		bodyHeight=window.document.body.offsetHeight,
		pos = calendarPosition.apply(this, arguments);
	/**
	 *计算日期插件的座标
	 *
	 */
	var computePosition = function(pos,obj,pluginfo){
		
		var top=0,
			left=0,		
			btnWidth=25,
			calendarWidth=pluginfo.width,
		    calendarHeight=pluginfo.height,
			bodyHeight=window.document.body.offsetHeight,
			bodyWidth=window.document.body.offsetWidth;
		
		//1 计算插件上下显示的位置
	    //计算插件是否可以显示在按钮上方的1/2处

	    if(pos.top>(calendarHeight/2)&&bodyHeight-pos.top>(calendarHeight/2)){
	    	top=pos.top-calendarHeight/2;
	    }else{
	    	top=pos.top-calendarHeight;
	    }   
	    pos.top=top<0?0:top;

		//2 计算插件左右显示的位置

		if((bodyWidth-(pos.left+btnWidth))<calendarWidth){
			
			pos.left=pos.left-calendarWidth;
		}else{
			//右边显示		
			pos.left=pos.left+btnWidth;
		}	
		return pos;
	}
		
	//日期插件	
	if ((obj.name&&obj.name.indexOf(CALENDAR)>-1)||obj.getAttribute("data-timestamp")) {
		pos=obj.tagName.toLocaleLowerCase()=="img"?{top:obj.y,left:obj.x}:pos;
		pos= computePosition(pos,obj,{width:250,height:290});
		
	}else if(colorCls&&colorCls.indexOf(LABELCLS)>-1||(obj.id&&obj.id.indexOf(LABELCLS)>-1)){
	 	//拾色器
	 	
	 	var left=arguments[1].clientX?arguments[1].clientX:pos.left,
	 		top=arguments[1].clientY?arguments[1].clientY:pos.top;
	 	pos.left=left;
	 	pos.top=top;
	 	pos= computePosition(pos,obj,{width:310,height:160});
	 	pos.left-=25;
	}	
	return pos;
}

