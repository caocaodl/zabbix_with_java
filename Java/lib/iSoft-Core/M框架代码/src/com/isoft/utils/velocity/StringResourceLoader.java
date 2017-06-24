package com.isoft.utils.velocity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * @author vzhang
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StringResourceLoader extends ResourceLoader {

    public static final String NAME="string";
    public static final String ENCODING="UTF-8";
    public static final String STRING_RESOURCE_LOADER_CLASS="string.resource.loader.class";
    
    private static String sourceEncoding;
    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache.commons.collections.ExtendedProperties)
     */
    @Override
    public void init(ExtendedProperties arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#getResourceStream(java.lang.String)
     */
    @Override
    public InputStream getResourceStream(String str) throws ResourceNotFoundException {
        if(str==null){
            throw new ResourceNotFoundException("The string template is NULL.");
        }
        try{
            String encoding=sourceEncoding==null? ENCODING:sourceEncoding;
            return new  ByteArrayInputStream(str.getBytes(encoding));      
        }catch(UnsupportedEncodingException ux){
            throw new ResourceNotFoundException("The encoding is not supported.");
        }
      
    }

    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#isSourceModified(org.apache.velocity.runtime.resource.Resource)
     */
    @Override
    public boolean isSourceModified(Resource arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#getLastModified(org.apache.velocity.runtime.resource.Resource)
     */
    @Override
    public long getLastModified(Resource arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    

    /**
     * @return Returns the sourceEncoding.
     */
    public static String getSourceEncoding() {
        return sourceEncoding;
    }
    /**
     * @param sourceEncoding The sourceEncoding to set.
     */
    public static void setSourceEncoding(String sourceEncoding) {
        StringResourceLoader.sourceEncoding = sourceEncoding;
    }
}
