package com.isoft.dictionary;

import com.isoft.exception.IErrorCode;

public enum ErrorCodeEnum implements IErrorCode{
    
    /** The sql statement is NULL. */
    CODING_SQL_NULL_STATEMENT(ErrorSeverityEnum.CODING, 10001),
    /** The paraMap could not be NULL if the sql contains parameters. */
    CODING_SQL_MISSED_PARAM_BIND(ErrorSeverityEnum.CODING, 10002),    
    /** The variable '{0}' must be enclosed in #/@{}. not space is allowed between #/@ and {. */
    CODING_SQL_VAR_NOT_CLOSED(ErrorSeverityEnum.CODING, 10003),
    /** Could not get property's value with name '{0}' from object '{1}'. */
    CODING_SQL_MISSED_PARAM_VALUE(ErrorSeverityEnum.CODING, 10004),
    CODING_SQL_VO_POPULATED_FAIL(ErrorSeverityEnum.CODING, 10005),
    CODING_LOAD_SQLXML_FAIL(ErrorSeverityEnum.CODING, 10006),
    CODING_BLHMETHOD_IMPLEMENT_ERROR(ErrorSeverityEnum.CODING, 10007),
    CODING_METHOD_IMPLEMENT_ERROR(ErrorSeverityEnum.CODING, 10008),
    
    FRAMEWORK_UNKNOWN_ERROR(ErrorSeverityEnum.FRAMEWORK, 20000),
    FRAMEWORK_DB_NO_CONNECTION_AVAILABLE(ErrorSeverityEnum.FRAMEWORK, 20001),
    FRAMEWORK_DB_TURNON_TRANSACTION_FAIL(ErrorSeverityEnum.FRAMEWORK, 20002),
    FRAMEWORK_DB_TRANSACTION_COMMIT_FAIL(ErrorSeverityEnum.FRAMEWORK, 20003),
    FRAMEWORK_DB_TRANSACTION_ROLLBACK_FAIL(ErrorSeverityEnum.FRAMEWORK, 20004),
    FRAMEWORK_APP_NO_SERVICE_AVAILABLE(ErrorSeverityEnum.FRAMEWORK, 20005),
    FRAMEWORK_EHCACHE_ERROR(ErrorSeverityEnum.FRAMEWORK, 20006),
    FRAMEWORK_ALGORITHM_ERROR(ErrorSeverityEnum.FRAMEWORK, 20007),
    FRAMEWORK_DATASOURCE_ERROR(ErrorSeverityEnum.FRAMEWORK, 20007),
    
    
    DAO_SQL_STATEMENT_CLOSED_FAIL(ErrorSeverityEnum.DAO, 30001),
    DAO_SQL_RESULTSET_CLOSED_FAIL(ErrorSeverityEnum.DAO, 30002),
    
    DAO_DBVC_LOCKED_FAIL(ErrorSeverityEnum.DAO, 30003),
    
    DAO_SQL_EXECUTE_FAIL(ErrorSeverityEnum.DAO, 30004),
    DAO_SQL_INSERT_FAIL(ErrorSeverityEnum.DAO, 30005),
    DAO_SQL_UPDATE_FAIL(ErrorSeverityEnum.DAO, 30006),
    DAO_SQL_DELETE_FAIL(ErrorSeverityEnum.DAO, 30007),
    
    DAO_GEN_FLOWCODE_FAIL(ErrorSeverityEnum.DAO, 30008),
    DAO_DATA_NOT_FOUND(ErrorSeverityEnum.DAO, 30009),
    DAO_PROC_SPAM_FAIL(ErrorSeverityEnum.DAO, 30010),
    
    DAO_SAVE_CM_FAIL(ErrorSeverityEnum.DAO, 30011),
    DAO_PURGE_CM_FAIL(ErrorSeverityEnum.DAO, 30012),
    DAO_RESTORE_CM_FAIL(ErrorSeverityEnum.DAO, 30013),
    DAO_CHANGE_OWNER_FAIL(ErrorSeverityEnum.DAO, 30014),
    
    DAO_SAVE_ITEM_FAIL(ErrorSeverityEnum.DAO, 30015),
    DAO_PURGE_ITEM_FAIL(ErrorSeverityEnum.DAO, 30016),
    DAO_RESTORE_ITEM_FAIL(ErrorSeverityEnum.DAO, 30017),
    
    DAO_SAVE_CORPINFO_FAIL(ErrorSeverityEnum.DAO, 30018),
    
    DAO_SAVE_USERINFO_FAIL(ErrorSeverityEnum.DAO, 30019),
    
    DAO_MERGE_CONTINFO_FAIL(ErrorSeverityEnum.DAO, 30020),
    
    DAO_SQL_PROCEDURE_FAIL(ErrorSeverityEnum.DAO, 30021),
    
    DAO_DUP_ENTRY(ErrorSeverityEnum.DAO, 31062),
    DAO_TRUNCATED_WRONG_VALUE_FOR_FIELD(ErrorSeverityEnum.DAO, 31366),    
    
    BIZLOGIC_SESSION_TIMEOUT(ErrorSeverityEnum.BIZLOGIC, 40001),
    BIZLOGIC_NO_PERMISSION(ErrorSeverityEnum.BIZLOGIC, 40002),
    BIZLOGIC_IP_FORBID(ErrorSeverityEnum.BIZLOGIC, 40003),    
    
    BIZLOGIC_LOGON_FAILURE(ErrorSeverityEnum.BIZLOGIC, 40005),
    BIZLOGIC_LINK_EXPIRED_ACCOUNT_ACTIVE(ErrorSeverityEnum.BIZLOGIC, 40006),
    BIZLOGIC_LINK_EXPIRED_RESET_PASSWORD(ErrorSeverityEnum.BIZLOGIC, 40007),
    
    BIZLOGIC_FLOWCODE_FORMULA_REQUIRED(ErrorSeverityEnum.BIZLOGIC, 40008),
    BIZLOGIC_FLOWCODE_OVERLENGTH(ErrorSeverityEnum.BIZLOGIC, 40009),
    
    BIZLOGIC_SAVE_TEMPLATE_E1(ErrorSeverityEnum.BIZLOGIC, 40010),
    BIZLOGIC_SAVE_TEMPLATE_E2(ErrorSeverityEnum.BIZLOGIC, 40011),
    BIZLOGIC_SAVE_TEMPLATE_E3(ErrorSeverityEnum.BIZLOGIC, 40012),
    
    BIZLOGIC_PARSE_DATE_FAIL(ErrorSeverityEnum.BIZLOGIC, 40013),
    
    BIZLOGIC_INVALID_CHARACTERS_IN_CONTENT(ErrorSeverityEnum.BIZLOGIC, 40014),   
    
    
    BIZLOGIC_ECOMM_ADD_NOTIFICATION_FAIL(ErrorSeverityEnum.BIZLOGIC, 47001),
    BIZLOGIC_ECOMM_CHANGE_NOTIFICATION_FAIL(ErrorSeverityEnum.BIZLOGIC, 47002),
    
    BIZLOGIC_ECOMM_ADCENTER_PREPARE_TOMORROW_CHECK_DATA_FAIL(ErrorSeverityEnum.BIZLOGIC, 47031),
    BIZLOGIC_ECOMM_ADCENTER_UPDATE_AUTIDPARAM_FAIL(ErrorSeverityEnum.BIZLOGIC, 47032),
    
    BIZLOGIC_ECOMM_ACCOUNTMANAGER_UPDATE_ILLEGAL_STATUS_FAIL(ErrorSeverityEnum.BIZLOGIC, 47051),
    BIZLOGIC_ECOMM_ACCOUNTMANAGER_CLEAR_ILLEGAL_STATUS_FAIL(ErrorSeverityEnum.BIZLOGIC, 47052),
    BIZLOGIC_ECOMM_ACCOUNTMANAGER_CLEAR_ILLEGAL_HISTORY_FAIL(ErrorSeverityEnum.BIZLOGIC, 47053),
    
    BIZLOGIC_ECOMM_FINANCECENTER_UPDATE_INVOICE_FAILE(ErrorSeverityEnum.BIZLOGIC, 47070),
    BIZLOGIC_ECOMM_FINANCECENTER_GET_PAYINVOICE_INFO_FAIL(ErrorSeverityEnum.BIZLOGIC, 47071),
    BIZLOGIC_ECOMM_FINANCECENTER_GET_USER_EMAIL_FAIL(ErrorSeverityEnum.BIZLOGIC, 47072),
    BIZLOGIC_ECOMM_FINANCECENTER_NOT_EXIST_INVOICE(ErrorSeverityEnum.BIZLOGIC, 47073),
    
    
    BIZLOGIC_UNKNOWN(ErrorSeverityEnum.BIZLOGIC, 99999);

    private int severity = -1;
    private int errorCode = -1;
    
    private ErrorCodeEnum(ErrorSeverityEnum ese, int errorCode) {
        this.severity = ese.severity();
        this.errorCode = errorCode;
    }

    public int severity() {
        return severity;
    }

    public int errorCode() {
        return errorCode;
    }
    
    public boolean equals(IErrorCode ec) {
        return ec != null
        		&& (ec instanceof ErrorCodeEnum)
        		&& this.severity == ec.severity()
                && this.errorCode == ec.errorCode();
    }
    
}
