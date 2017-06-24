package com.isoft;

import static org.apache.commons.beanutils.ConvertUtils.convert;
import static org.apache.commons.lang3.StringUtils.strip;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Properties;

import com.isoft.server.RunParams;

class iFeature {

	public static boolean debug = true;
	public static boolean debugout = true;
	
	public static String defaultUser = "";
	public static String defaultPassword = "";
	public static String defaultCheckCode = "";
	
	public static String title = "";

	public static boolean enableLoginCheck = true;
	//public static boolean enableGlobalException = true;
	
	public static String validateCodeChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static boolean autoCommit = false;

	static {
		sync();
	}

	private static boolean init = false;
	
	public final static synchronized void sync() {
		if(!init){
			init =true;
			InputStream xml = null;
			try {
				Properties features = new Properties();
				xml = iFeature.class.getResourceAsStream("/features.xml");
				features.loadFromXML(xml);
				Field[] fields = Feature.class.getFields();
				String stripChars = " \t\r\n";
				String fv = null;
				for (Field f : fields) {
					fv = features.getProperty(f.getName());
					if (fv != null && (fv = strip(fv, stripChars)).length() > 0) {
						Object v = f.get(null);
						if(v == null) {
							v = fv;
						}
						f.set(null, convert(fv, v.getClass()));
						features.remove(f.getName());
					}
				}
				if (!features.isEmpty()) {
					throw new Exception("Warning! Unavailable features:"+features);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {			
				RunParams.DEBUG = debug;
				RunParams.DEBUGOUT = debugout;
				RunParams.TITLE = title;			
				if (xml != null) {
					try {
						xml.close();
						xml = null;
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static void show(PrintStream out) throws Exception {
		Field[] fields = Feature.class.getFields();
		for (Field f : fields) {
			out.println(f.getName() + "=" + f.get(null));
		}
	}
	
	public static void main(String[] args) {
		Feature.idsUseConnection = true;
	}
	
}
