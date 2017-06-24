package com.isoft.iradar.web.Util;

import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CTag;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTSeverity extends CTag {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private IIdentityBean idBean;

	/**
	 * @param string options["id"]
	 * @param string options["name"]
	 * @param int    options["value"]
	 */
	public CTSeverity(IIdentityBean idBean, SQLExecutor executor, Map options) {
		this(idBean, executor, options, null,false);
	}
	
	
	/** 构造方法升级，加入等级代码，方便多种情况调用
	 * @param idBean
	 * @param executor
	 * @param options
	 * @param levelarr
	 */
	public CTSeverity(IIdentityBean idBean, SQLExecutor executor, Map options, CArray<String> levelarr,boolean isSubmit) {
		super("div", "yes");
		this.idBean = idBean;
		new CDiv().useJQueryStyle();//给radio添加样式
		
		attr("id", isset(options,"id") ? Nest.value(options,"id").$() : rda_formatDomId(Nest.value(options,"name").asString()));
		addClass("jqueryinputset control-severity");
		
		CArray items = array();
		StringBuilder jsIds = new StringBuilder();
		StringBuilder jsLabels = new StringBuilder();
		
		CArray styles = CArray.map();
		if(levelarr != null){
			for(Entry<Object, String> e : levelarr.entrySet()){
				int severity = Nest.as(e.getKey()).asInteger();
				Nest.value(styles, severity).$("");
			}
		}else{
			levelarr = getSeverityCaption(idBean, executor);
		}
		
		for (Entry<Object, String> e : levelarr.entrySet()) {
		    int severity = Nest.as(e.getKey()).asInteger();
		    String caption = e.getValue();
			items.add(new CRadioButton(
				Nest.value(options,"name").asString(),
				severity,
				null,
				Nest.value(options,"name").asString()+"_"+severity,
				(Nest.value(options,"value").asInteger() == severity),
				isSubmit?"submit()":null
			));

			String css = this.getSeverityStyleTwo(styles,severity,true);

			CLabel label = new CLabel(caption, Nest.value(options,"name").asString()+"_"+severity, Nest.value(options,"name").asString()+"_label_"+severity);
			label.attr("data-severity", severity);
			label.attr("data-severity-style", css);

			if (Nest.value(options,"value").asInteger() == severity) {
				label.attr("aria-pressed", "true");
				label.addClass(css);
			} else {
				label.attr("aria-pressed", "false");
			}

			items.add(label);

			jsIds.append(", #"+Nest.value(options,"name").$()+"_"+severity);
			jsLabels.append(", #"+Nest.value(options,"name").$()+"_label_"+severity);
		}

		if (jsIds.length()>0) {
			jsIds.delete(0, 2);
			jsLabels.delete(0, 2);
		}

		addItem(items);
		
		String js = getJsTemplate("javascript_for_cseverity");
		String sjsLabels = jsLabels.toString();
		String sjsIds = jsIds.toString();
		String id = Nest.as(getAttribute("id")).asString();
		insert_js(String.format(js, sjsLabels, sjsLabels, id, sjsIds, id),true);
	
	}
	
   public  CArray<String> getSeverityCaption(IIdentityBean idBean, SQLExecutor executor)
   {
		 CArray severities = CArray.map(new Object[] { 
			Integer.valueOf(900), "15分钟", 
			Integer.valueOf(1800), "30分钟", 
			Integer.valueOf(2700),"45分钟", 
			Integer.valueOf(3600), "60分钟" });
		 return severities;
   }
   
     public  String getSeverityStyle(Integer severity, boolean type)
   {
		CArray styles = CArray.map(new Object[] { 
		Integer.valueOf(900), "", 
		Integer.valueOf(1800), "", 
		Integer.valueOf(2700), "", 
		Integer.valueOf(3600), "" });
   
	   if (!type)
	       return "normal";
	   if (styles.containsKey(severity)) {
	      return (String)styles.get(severity);
	    }
	     return "";
     }
     
     public  String getSeverityStyleTwo(CArray styles,Integer severity, boolean type)
     {
  	   if (!type)
  	       return "normal";
  	   if (styles.containsKey(severity)) {
  	      return (String)styles.get(severity);
  	    }
  	     return "";
       }

}
