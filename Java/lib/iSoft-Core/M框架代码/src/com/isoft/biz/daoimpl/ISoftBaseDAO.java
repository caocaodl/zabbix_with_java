package com.isoft.biz.daoimpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.utils.DebugUtil;

public abstract class ISoftBaseDAO extends AbstractDAO{

    /** Default constructor loads the SQL statements in the properties file */
    public ISoftBaseDAO(SQLExecutor sqlExecutor) {
        super(sqlExecutor);
    }
    
    @Override
    protected void loadXMLSql(List<Class<?>> baseDAOs, Properties sqlProperties) {
        for(Class<?> baseDAOz:baseDAOs){
            if(!getClass().equals(baseDAOz)){
                String bfileName = baseDAOz.getName();
                bfileName = bfileName.substring(bfileName.lastIndexOf(".") + 1);
                if (bfileName.endsWith(DAO_CLASS_SUFFIX)) {
                    bfileName = bfileName.substring(0,bfileName.length() - DAO_CLASS_SUFFIX.length());
                }

                String sqlConf = bfileName+SQL_XML_SUFFIX;
                InputStream bis = baseDAOz.getResourceAsStream(sqlConf);
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug("LOADING ........ SQL PROPERTIES:"+sqlConf);
                }
                try {
                    sqlProperties.loadFromXML(bis);
                } catch (Throwable e) {
                	System.out.println("Error:"+sqlConf+" NOT FOUND!");
                    throw BizError.createCodingException(
                            ErrorCodeEnum.CODING_LOAD_SQLXML_FAIL, e, sqlConf);
                } finally {
                    if(bis != null){
                        try {
                            bis.close();
                        } catch (IOException e) {
                            if (DebugUtil.isErrorEnabled()) {
                                DebugUtil.error(e);
                            }
                        }
                    }
                }
            }
        }        

        String fileName = getClass().getName();
        //获得当前类的目录
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileName.endsWith(DAO_CLASS_SUFFIX)) {
            fileName = fileName.substring(0,fileName.length() - DAO_CLASS_SUFFIX.length());
            String sqlConf = fileName+SQL_XML_SUFFIX;
            InputStream is = getClass().getResourceAsStream(sqlConf);
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("LOADING ........ SQL PROPERTIES:"+sqlConf);
            }
            try {
                sqlProperties.loadFromXML(is);
            } catch (Exception e) {
            	System.out.println(sqlConf);
                throw BizError.createCodingException(
                        ErrorCodeEnum.CODING_LOAD_SQLXML_FAIL, e, sqlConf);
            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        if (DebugUtil.isErrorEnabled()) {
                            DebugUtil.error(e);
                        }
                    }
                }
            }
        }
    }
}
