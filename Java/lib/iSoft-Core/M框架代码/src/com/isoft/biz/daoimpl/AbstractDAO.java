package com.isoft.biz.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.DAOFactory;
import com.isoft.biz.dao.DB;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.exception.BusinessException;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.persistlayer.DBCompatible;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.server.RunParams;
import com.isoft.utils.DebugUtil;
import com.isoft.utils.SQLUtil;
import com.isoft.utils.VelocityUtil;

public abstract class AbstractDAO extends DBCompatible implements IDAO{
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /** The SQL statements properties file name suffix */
    protected static final String SQL_XML_SUFFIX = "SQL.xml";

    /** The DAO class name suffix */
    protected static final String DAO_CLASS_SUFFIX = "DAO";

    /** Database charset encoding */
    public static final String DATABASE_CHARSET = "UTF-8";

    public static final String SORT_ASCENDING = "ASC";//升序
    public static final String SORT_DESCENDING = "DESC";//降序
    public static final String SQL_CONF_PREFIX ="SQL";//报表sql文件前缀

    /** Properties instance for storing all SQL statements in memory */
    private Properties SQL_PROPERTIES;

    private SQLExecutor sqlExecutor;

    /** Default constructor loads the SQL statements in the properties file */
    public AbstractDAO(SQLExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public SQLExecutor getSqlExecutor() {
        return sqlExecutor;
    }

    /**
     * Load the SQL statements from properties file into memory. This method
     * assumes that the properties file names consist of DAO class name(without
     * DAO suffix) and "SQL.properties" suffix. For example, the properties
     * file name for storing SQL statements for SeekerAccountDAO class is
     * SeekerAccountSQL.properties.
     * @param forceReload to indicate if forcing reload when the SQL statements
     *        are already loaded.
     */
    protected void loadSql(boolean forceReload) {
        if (SQL_PROPERTIES != null && !SQL_PROPERTIES.isEmpty() && !forceReload) {
            return; // no need to load in this situation
        }

        if (SQL_PROPERTIES == null) {
            SQL_PROPERTIES = new Properties();
        } else {
            SQL_PROPERTIES.clear();
        }

        //construct the properties file name by appending the suffix to the
        //class name(with the DAO suffix removed)

        loadXMLSql(getExtendsDAOClass(),SQL_PROPERTIES);
    }

    private List<Class<?>> baseDAOs = new LinkedList<Class<?>>();
    protected List<Class<?>> getExtendsDAOClass() {        
        return baseDAOs;
    }
    
    protected abstract void loadXMLSql(List<Class<?>> baseDaoz,Properties sqlProperties);

    private static final Pattern pattern = Pattern.compile("(\"?)([a-zA-Z_]+)(\"?)#([a-zA-Z]*)");
    /**
     * Returns the SQL statements for the given key defined in the properties
     * file.
     * @param key the SQL statement key defined in the properties file
     * @return String of the SQL statement
     */
    @SuppressWarnings("unchecked")
    protected String getSql(String key) {
        if(DebugUtil.isDebugEnabled()){
            DebugUtil.debug("SQL KEY:"+key);
        }
        String fileName = getClass().getName();
        String sql = SQLUtil.getSqlTemplate(fileName,key);
        if(sql == null){
            loadSql(true);
            sql = SQL_PROPERTIES.getProperty(key);
            if(StringUtils.isEmpty(sql)){
                return null;
            }
            sql = sql.replaceAll("\t", " ");
            sql = doSubtitude(sql);
            if(RunParams.RELEASE_MODEL){
                sql = VelocityUtil.mergeCSE(sql);
            }
            Matcher match=pattern.matcher(sql);
            StringBuffer sqlbuf=new StringBuffer();
            Map mapBean = new HashMap();
            while(match.find()){
                String leftQuot = match.group(1);
                String propName = match.group(2);
                String rightQuot = match.group(3);
                String propType = match.group(4);
                Class propClass = null;
                if(propType == null || propType.length()==0){//短路判断
                    propClass = String.class;
                }else if("Date".equals(propType)){
                    propClass = Date.class;
                }else if("BigDecimal".equals(propType)){
                    propClass = BigDecimal.class;
                }else if("Boolean".equals(propType)){
                    propClass = Boolean.class;
                }else if("Integer".equals(propType)){
                    propClass = Integer.class;
                }else if("Float".equals(propType)){
                    propClass = Float.class;
                }else if("Double".equals(propType)){
                    propClass = Double.class;
                }else if("Long".equals(propType)){
                    propClass = Long.class;
                }else{
                    propClass = String.class;
                }
                mapBean.put(propName, propClass);
                match.appendReplacement(sqlbuf, leftQuot+propName+rightQuot);
            }
            match.appendTail(sqlbuf);
            if(!mapBean.isEmpty()){
                SQLUtil.setSqlVOTemplate(fileName,key,mapBean);
            }
            sql = sqlbuf.toString();
            SQLUtil.setSqlTemplate(fileName,key,sql);
            return sql;
        }else{
            return sql;
        }
    }
    
    private String doSubtitude(String sql) {
		if (!DB.getSubtitudes().isEmpty()) {
			for(Entry<String, String> e:DB.getSubtitudes().entrySet()){
				sql = sql.replaceAll(e.getKey(), e.getValue());
			}
		}
 		return sql;
	}

	@SuppressWarnings("unchecked")
    protected Map<String,?> getSqlVO(String key) {
        String fileName = getClass().getName();
        Map sqlVo = SQLUtil.getSqlVOTemplate(fileName,key);
        if(sqlVo == null){
            getSql(key);
            sqlVo = SQLUtil.getSqlVOTemplate(fileName,key);
        }
        if(DebugUtil.isDebugEnabled()){
            DebugUtil.debug("SQL KEY VO:"+key+"_VO:"+sqlVo);
        }
        return sqlVo;
    }
    
    public String getFlowcode(NameSpaceEnum nameSpace){
    	return DBIDGeneratorFactory.getDBIDGenerator(nameSpace).next();
    }

    protected boolean isErrorDuplicateEntry(BusinessException e) {
        return ErrorCodeEnum.DAO_DUP_ENTRY.equals(e.getErrorCode());
    }
    
    /**
     * 获取指定的DAO
     * @param moduleName
     * @param callDAOIF
     * @return
     */
    @SuppressWarnings("unchecked")
    protected IDAO getDAO(String moduleName,Class callDAOIF){
        return DAOFactory.newDAOImplInstance(moduleName, callDAOIF.getName(),getSqlExecutor());
    }

    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,Map from,String property){
        to.put(property, from.get(property));
    }
    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,String property,String value){
        to.put(property, value);
    }
    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,String property,Boolean value){
        to.put(property, value);
    }
    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,String property,Date value){
        to.put(property, value);
    }
    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,String property,BigDecimal value){
        to.put(property, value);
    }
    @SuppressWarnings("unchecked")
    protected void setMapEntry(Map to,String property,Integer value){
        to.put(property, value);
    }
}
