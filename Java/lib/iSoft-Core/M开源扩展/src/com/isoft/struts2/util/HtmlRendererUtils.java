package com.isoft.struts2.util;

import java.io.IOException;

import org.apache.commons.el.HTMLEncoder;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;



/**
 * @author Manfred Geiler (latest modification by $Author: mbr $)
 * @version $Revision: 291881 $ $Date: 2005-09-27 05:59:11 -0400 (Tue, 27 Sep 2005) $
 */
public final class HtmlRendererUtils {
	public final static boolean IS_PRETTY_HTML = true;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\r\n");	
	
    private HtmlRendererUtils() {
        // utility class, do not instantiate
    }

    public static void renderDisplayValueOnly(HtmlResponseWriter writer, DisplayValueOnlyCapable component) throws IOException {
        writer.startElement(HTML.SPAN_ELEM);
        if(component instanceof AndurilUIComponent){
            String id = ((AndurilUIComponent) component).getId();
            if (id != null && id.length() > 0) {
                writer.writeAttribute(HTML.ID_ATTR, id);
            }
        }
       
        //writeIdIfNecessary(writer, input, facesContext);

        component.renderDisplayValueOnlyAttributes(writer);
        
        String strValue = RendererUtils.getStringValue((AndurilUIComponent)component);
        HTMLEncoder.encode(writer, strValue, true, true);
        writer.endElement(HTML.SPAN_ELEM);
    }
    
    public interface DisplayValueOnlyCapable {
    	public void renderDisplayValueOnlyAttributes(HtmlResponseWriter writer) throws IOException;
    }
    
    public static void writePrettyLineSeparator(HtmlResponseWriter writer) throws IOException {
		if (IS_PRETTY_HTML) {
			writer.write(LINE_SEPARATOR);
		}
	}
}
