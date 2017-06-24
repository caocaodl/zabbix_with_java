package com.isoft.utils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.RenderTool;

import com.isoft.dictionary.ColumnStatusEnum;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.utils.velocity.HelperTool;
import com.isoft.utils.velocity.MapTool;
import com.isoft.utils.velocity.StringResourceLoader;
import com.isoft.utils.velocity.VelocityDummyLog;

public class VelocityUtil {
    public static final String DEFAULT_ENCODING="UTF-8";
    
    private static final Pattern CSE_PATTERN = Pattern.compile("(CSE\\[)([A-Z0-9_]+)(\\])");
    @SuppressWarnings("unused")
	private static final Pattern I18N_PATTERN = Pattern.compile("e=\\{([^{}]*)\\}z=\\{([^{}]*)\\}");    

    private static boolean started=false;

    private static VelocityContext toolCtx;

    @SuppressWarnings("unchecked")
    private static void loadToolBox(){

        Map toolMap=new HashMap();
        toolMap.put("date",new DateTool());
        toolMap.put("math",new MathTool());
        toolMap.put("render",new RenderTool());
        toolMap.put("number",new NumberTool());
        toolMap.put("helper",new HelperTool());
        toolMap.put("map",new MapTool());

        toolCtx=new VelocityContext(toolMap);

    }
    private static synchronized void init(){
        // this is used for the stupid Sun One studio,
        // since StringResourceLoader is not referenced directly, it will not be built into the ear file.
        // just force make a fake reference.
        //StringResourceLoader strLoader=new StringResourceLoader();
        // SAXParser parser =new SAXParser();
        //SimpleLog4JLogSystem vlog=new SimpleLog4JLogSystem();
        //VelocityDummyLog velLog=new VelocityDummyLog();
        //----

        try{
        //--
            Properties props=new Properties();

            props.put(VelocityEngine.RESOURCE_LOADER,
                StringResourceLoader.NAME);
            props.put(StringResourceLoader.STRING_RESOURCE_LOADER_CLASS,
        StringResourceLoader.class.getName());
            props.put( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                    VelocityDummyLog.class.getName() );

            // note: since in the database , we only use UTF-8 encoding, only one velocity engine is enough,
            // otherwise, we must have the velocity engine per input encoding...
            props.put(RuntimeConstants.INPUT_ENCODING,DEFAULT_ENCODING);
            props.put(RuntimeConstants.OUTPUT_ENCODING,DEFAULT_ENCODING);

            if(started) return;
            Velocity.init(props);
            loadToolBox();
            started=true;
        }catch(Exception ex){
            ex.printStackTrace(System.err);
        }catch(Throwable ex){
            ex.printStackTrace(System.err);
        }
    }



    /**
     * Merges the specified Velocity template  with the given model and encoding
     * @param templateString the template string
     * @param paraMap the Map that contains model names as keys and model objects
     * as values
     * @param templateEncoding encoding of the tempalte string
     * @param returnEncoding encoding for the returned String
     * @return the merged string
     */
    @SuppressWarnings("unchecked")
    public static  String mergeWithEncoding(String templateString,Map paraMap,String templateEncoding,String returnEncoding){
        if(templateEncoding==null) templateEncoding=DEFAULT_ENCODING;
        if(returnEncoding==null) returnEncoding=DEFAULT_ENCODING;

        if(!started){
            init();
        }
        String str="";
       try{
        Template t = Velocity.getTemplate(templateString,templateEncoding);
        VelocityContext context = new VelocityContext(paraMap,toolCtx);
        StringWriter result = new StringWriter();
         t.merge(context,result);
         str= result.toString();
         if(!templateEncoding.equalsIgnoreCase(returnEncoding)){
             str=new String(str.getBytes(templateEncoding),returnEncoding);
         }
         if(result != null)
             result.close();
       }catch(ParseErrorException pex){
           pex.printStackTrace(System.err);
       }catch(MethodInvocationException iex){
           iex.printStackTrace(System.err);
       }
       catch(Exception ex){
           ex.printStackTrace(System.err);
       }
       return str;
    }

    /**
     * Merge the specified Velocity template  with the given model , the encoding is default to "UTF-8"
     * @param templateString the template string
     * @param paraMap the Map that contains model names as keys and model objects
     * as values
     * @param templateEncoding encoding of the tempalte string
     * @param returnEncoding encoding for the returned String
     * @return the merged string
     */
    @SuppressWarnings("unchecked")
    public static  String merge(String templateString,Map paraMap){
        return mergeWithEncoding(templateString,paraMap,null,null);
    }
    
    public static String mergeCSE(String txt){
        if(txt!=null && txt.length()>0){
            Matcher match=CSE_PATTERN.matcher(txt);
            StringBuffer txtbuf=new StringBuffer();
            while(match.find()){
                String cseName = match.group(2);
                ColumnStatusEnum cse = ColumnStatusEnum.valueOf(cseName);
                String cesStatus = cse.magic();
                match.appendReplacement(txtbuf, cesStatus);
            }
            match.appendTail(txtbuf);
            txt = txtbuf.toString();
        }
        return txt;
    }
    
    private static final Pattern ALL_ENUM_PATTERN = Pattern.compile("((CSE|ECE)\\[)([A-Z0-9_]+)(\\])");
    
    public static String mergeAllEnum(String txt) {
    	if(txt!=null && txt.length()>0) {
    		StringBuffer sb = new StringBuffer();
    		Matcher matcher = ALL_ENUM_PATTERN.matcher(txt);
    		while(matcher.find()) {
    			String enumMagic = null;
    			String enumClass = matcher.group(2);
    			String enumName = matcher.group(3);
    			
    			if("CSE".equals(enumClass)) {
    				ColumnStatusEnum cse = ColumnStatusEnum.valueOf(enumName);
    				enumMagic = cse.magic();
    			}else if("ECE".equals(enumClass)) {
    				ErrorCodeEnum ece = ErrorCodeEnum.valueOf(enumName);
    				enumMagic = String.valueOf(ece.errorCode());
    			}
    			
    			if(enumMagic != null) {
    				matcher.appendReplacement(sb, enumMagic);
    			}
    		}
    		matcher.appendTail(sb);
    		return sb.toString();
    	}else {
    		return txt;
    	}
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        String str = "#if($param.status == 'CSE[CURRENCY_USD]')";
        System.out.println(mergeAllEnum(str));
    }
}
