package com.isoft.exception;

public interface IErrorCode{
    String name();
    int ordinal();
    int severity();
    int errorCode();
    boolean equals(IErrorCode ec);
}
