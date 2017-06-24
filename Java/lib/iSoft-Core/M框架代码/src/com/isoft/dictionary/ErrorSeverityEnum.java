package com.isoft.dictionary;

import com.isoft.exception.IErrorSeverity;

public enum ErrorSeverityEnum implements IErrorSeverity{
    
    /** 编码异常 */
    CODING(1, "Coding"),
    /** 框架异常 */
    FRAMEWORK(2, "Framework"),
    /** 数据库异常 */
    DAO(3, "Dao"),
    /** 业务逻辑异常 */
    BIZLOGIC(4, "Bizlogic"),
    ;
    
    private int severity = -1;
    private String prefix;
    
    private ErrorSeverityEnum(int severity, String prefix) {
        this.severity = severity;
        this.prefix = prefix;
    }
    
    public int severity() {
        return this.severity;
    }
    
    public String prefix() {
        return this.prefix;
    }
    
    public boolean equals(IErrorSeverity es) {
        return es != null && this.severity() == es.severity();
    }
}
