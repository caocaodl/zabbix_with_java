package com.isoft.exception;

public interface IErrorSeverity{
    String name();
    int ordinal();
    int severity();
    String prefix();
    boolean equals(IErrorSeverity ec);
}
