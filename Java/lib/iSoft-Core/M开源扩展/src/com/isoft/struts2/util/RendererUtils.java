package com.isoft.struts2.util;



import java.util.Date;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.convert.Converter;

/**
 * @author Manfred Geiler (latest modification by $Author: mbr $)
 * @version $Revision: 290413 $ $Date: 2005-09-20 06:26:13 -0400 (Tue, 20 Sep
 *          2005) $
 */
public class RendererUtils {

	/**
	 * See JSF Spec. 8.5 Table 8-1
	 * 
	 * @param value
	 * @return boolean
	 */
	public static boolean isDefaultAttributeValue(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() == false;
		} else if (value instanceof Number) {
			if (value instanceof Integer) {
				return ((Number) value).intValue() == Integer.MIN_VALUE;
			} else if (value instanceof Double) {
				return ((Number) value).doubleValue() == Double.MIN_VALUE;
			} else if (value instanceof Long) {
				return ((Number) value).longValue() == Long.MIN_VALUE;
			} else if (value instanceof Byte) {
				return ((Number) value).byteValue() == Byte.MIN_VALUE;
			} else if (value instanceof Float) {
				return ((Number) value).floatValue() == Float.MIN_VALUE;
			} else if (value instanceof Short) {
				return ((Number) value).shortValue() == Short.MIN_VALUE;
			}
		}
		return false;
	}
	
	
	public static String getStringValue(AndurilUIComponent component) {
		Object value = component.getValue();
		Converter converter = component.findConverter(value);
		if (converter == null && value != null) {
			if (value instanceof String) {
				return (String) value;
			}
		}

		if (converter == null) {
			if (value == null) {
				return "";
			} else {
				return value.toString();
			}
		} else {
			String retValue = converter.getAsString(value);
			if (retValue == null || retValue.length() == 0) {
//				String cid = component.getClientId(facesContext);
//				retValue = (String) facesContext.getExternalContext().getRequestParameterMap().get(cid);
			}
			return retValue;
		}
	}
	
	public static Date getDateValue(AndurilUIComponent component){
        Object value = component.getValue();
		if (value == null || value instanceof Date) {
			return (Date) value;
		} else {
			throw new IllegalArgumentException("Expected submitted value of type Date for component: " + component);
		}
    }
}
