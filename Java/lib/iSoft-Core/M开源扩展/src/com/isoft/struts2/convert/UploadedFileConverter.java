package com.isoft.struts2.convert;

public class UploadedFileConverter extends Converter {

    @Override
    @Deprecated
    public Object getAsObject(String[] values) {
        return null;
    }

    @Override
    public String getAsString(Object value) {
        return "";
    }

}
