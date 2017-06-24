package com.isoft.dictionary;

public enum FuncIdEnum {
    /** 缺省权限:accDefault */
    DEFAULT_FUNID("accDefault"),
    
    ;

    private String magic;

    private FuncIdEnum(String magic) {
        this.magic = magic;
    }

    public String magic() {
        return magic;
    }

    @Override
    public String toString() {
        return magic;
    }
}
