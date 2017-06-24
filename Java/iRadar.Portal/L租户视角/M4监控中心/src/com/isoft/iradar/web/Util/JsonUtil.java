package com.isoft.iradar.web.Util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JsonUtil {
	
	/** json转换为java集合对象
	 * @param clazz
	 * @param jsons
	 * @return
	 */
	public static <T> List<T> JsonToJavas(T clazz, String jsons) {
        List<T> objs=null;
        JSONArray jsonArray=(JSONArray)JSONSerializer.toJSON(jsons);
        if(jsonArray!=null){
            objs=new ArrayList<T>();
            List list=(List)JSONSerializer.toJava(jsonArray);
            for(Object o:list){
                JSONObject jsonObject=JSONObject.fromObject(o);
                @SuppressWarnings("unchecked")
				T obj=(T)JSONObject.toBean(jsonObject, clazz.getClass());
                objs.add(obj);
            }
        }
        return objs;
    }
	
	/** 将str字符串转化为json字符串
	 * @param str
	 * @return
	 */
	public  static String JsonStr(String str){
		String quotstr=str.replace("&quot;", "\"");
    	String slashstr=quotstr.replace("\\", "");
    	String jsonstr=slashstr.substring(1,slashstr.length()-1);
		return jsonstr;
	}

}
