package com.isoft.imon.topo.core.utils;

public abstract class Assert {

    public static void notNull(Object object, String string) {
        if (object == null) {
            throw new IllegalStateException(string);
        }
    }
    
    public static void notNull(Object object) {
    	notNull(object, "");
    }

    public static void isTrue(boolean bool, String string) {
        if (!bool) {
            throw new IllegalStateException(string);
        }
    }
    
    public static void state(boolean state, String string) {
    	isTrue(state, string);
    }
}
