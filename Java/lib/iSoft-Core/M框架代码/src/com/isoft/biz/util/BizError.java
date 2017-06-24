package com.isoft.biz.util;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.exception.BusinessException;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.dictionary.ErrorSeverityEnum;
import com.isoft.exception.IErrorCode;
import com.isoft.exception.IErrorSeverity;
import com.isoft.utils.DebugUtil;

public class BizError {
    private static final Pattern ER_PATTERN_TRUNCATED_WRONG_VALUE_FOR_FIELD = Pattern
            .compile("^Incorrect (\\w+) value: '[\\s\\S]+' for column '(.+)' at row \\d+$");
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @return
     */
    public static BusinessException createCodingException(IErrorCode errorCode) {
        return createException(ErrorSeverityEnum.CODING, errorCode, null);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createCodingException(IErrorCode errorCode, Object ... errorParams) {
        return createException(ErrorSeverityEnum.CODING, errorCode, null, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @return
     */
    public static BusinessException createCodingException(IErrorCode errorCode, Throwable cause) {
        return createException(ErrorSeverityEnum.CODING, errorCode, cause);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createCodingException(IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        return createException(ErrorSeverityEnum.CODING, errorCode, cause, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @return
     */
    public static BusinessException createFrameworkException(IErrorCode errorCode) {
        return createException(ErrorSeverityEnum.FRAMEWORK, errorCode, null);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createFrameworkException(IErrorCode errorCode, Object ... errorParams) {
        return createException(ErrorSeverityEnum.FRAMEWORK, errorCode, null, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @return
     */
    public static BusinessException createFrameworkException(IErrorCode errorCode, Throwable cause) {
        return createException(ErrorSeverityEnum.FRAMEWORK, errorCode, cause);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createFrameworkException(IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        return createException(ErrorSeverityEnum.FRAMEWORK, errorCode, cause, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @return
     */
    public static BusinessException createDAOException(IErrorCode errorCode) {
        return createException(ErrorSeverityEnum.DAO, errorCode, null);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createDAOException(IErrorCode errorCode, Object ... errorParams) {
        return createException(ErrorSeverityEnum.DAO, errorCode, null, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @return
     */
    public static BusinessException createDAOException(IErrorCode errorCode, Throwable cause) {
        return createException(ErrorSeverityEnum.DAO, errorCode, cause);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createDAOException(IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        return createException(ErrorSeverityEnum.DAO, errorCode, cause, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @return
     */
    public static BusinessException createBizLogicException(IErrorCode errorCode) {
        return createException(ErrorSeverityEnum.BIZLOGIC, errorCode, null);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createBizLogicException(IErrorCode errorCode, Object ... errorParams) {
        return createException(ErrorSeverityEnum.BIZLOGIC, errorCode, null, errorParams);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @return
     */
    public static BusinessException createBizLogicException(IErrorCode errorCode, Throwable cause) {
        return createException(ErrorSeverityEnum.BIZLOGIC, errorCode, cause);
    }
    
    /**
     * 参数说明：
     * @param errorCode   异常码
     * @param cause       源异常
     * @param errorParams 异常参数
     * @return
     */
    public static BusinessException createBizLogicException(IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        return createException(ErrorSeverityEnum.BIZLOGIC, errorCode, cause, errorParams);
    }
    
    /**
     * 参数说明：
     * @param severity    异常级别
     * @param errorCode   异常码
     * @param cause       源异常
     * @param errorParams 异常参数
     * @return
     */
    private static BusinessException createException(IErrorSeverity severity, IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        return new BusinessException(severity, errorCode, cause, errorParams);
    }
    
    @SuppressWarnings("unchecked")
    public static BusinessException convertSQLException(SQLException e,
            String sql, List paramList) {
        int errorCode = e.getErrorCode();
        
        if (DebugUtil.isErrorEnabled()) {
            DebugUtil.error(sqlExToStr(sql, e) + paraToStr(paramList));
        }
        
        switch (errorCode) {
        /*
         * Error: 1062 SQLSTATE: 23000 (ER_DUP_ENTRY) 
         * Message: Duplicate entry '%s' for key %d
         */
        case 1062:
            return createDAOException(ErrorCodeEnum.DAO_DUP_ENTRY, e);
        /*
         * Error: 1366 SQLSTATE: HY000 (ER_TRUNCATED_WRONG_VALUE_FOR_FIELD) 
         * Message: Incorrect %s value: '%s' for column '%s' at row %ld
         * integer
         * decimal
         * string
         * date
         * time
         * datetime
         */
        case 1366:
            String lmsg = e.getLocalizedMessage();
            Matcher matcher = ER_PATTERN_TRUNCATED_WRONG_VALUE_FOR_FIELD.matcher(lmsg);
            if(matcher.find()){
                String vtype = matcher.group(1);
                String column = matcher.group(2);
                if("string".equals(vtype)){
                    return createDAOException(
                            ErrorCodeEnum.BIZLOGIC_INVALID_CHARACTERS_IN_CONTENT, e,
                            vtype, column);
                }
                return createDAOException(
                        ErrorCodeEnum.DAO_TRUNCATED_WRONG_VALUE_FOR_FIELD, e,
                        vtype, column);
            }else{
                return createDAOException(ErrorCodeEnum.DAO_SQL_EXECUTE_FAIL, e);
            }            
        default:
            return createDAOException(ErrorCodeEnum.DAO_SQL_EXECUTE_FAIL, e);
        }
    }

    private static String sqlExToStr(String sql, SQLException ex) {
        // if(sql==null) return "";
        // if(ex==null) return "";
        StringBuffer buf = new StringBuffer();

        buf.append("Error message when executing the sql:\n");

        buf.append(StringUtils.repeat("-", 100)).append("\n");
        buf.append(sql).append("\n");
        buf.append(StringUtils.repeat("-", 100)).append("\n");
        buf.append("\n");

        translateSQLException(ex, buf);
        return buf.toString();
    }

    private static void translateSQLException(SQLException ex, StringBuffer buf) {
        if (ex == null)
            return;
        buf.append(" message=");
        buf.append(ex.getMessage()).append("\n");
        buf.append("localizedMessage=").append(ex.getLocalizedMessage()).append("\n");
        buf.append("errorCode=").append(ex.getErrorCode()).append("\n");
        buf.append("sqlState=").append(ex.getSQLState()).append("\n\n");
        translateSQLException(ex.getNextException(), buf);
    }
    
    /**
     * convert the parameter list to String .
     *
     * @param paraList
     * @return the string of the paraList.
     */
    @SuppressWarnings("unchecked")
    public static String paraToStr(List paraList) {
        if (paraList == null || paraList.isEmpty()) {
            return "There is no parameters for this sql.";
        }

        StringBuilder buf = new StringBuilder(" Parameters in this sql: \n");
        int i = 1;
        Object object;
        for (Iterator it = paraList.iterator(); it.hasNext();) {
            object = it.next();
            buf.append(i).append(".");
            if (object == null) {
                buf.append(" value=NULL ");
            } else {
                buf.append("value=").append(object.toString());
                buf.append(",type=").append(object.getClass().getName());
            }
            buf.append("\n");
            i++;
        }
        return buf.toString();
    }
}
