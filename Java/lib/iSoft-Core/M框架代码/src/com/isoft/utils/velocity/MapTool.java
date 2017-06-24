package com.isoft.utils.velocity;

import java.util.Map;

public class MapTool {
    @SuppressWarnings("unchecked")
    public boolean empty(Object obj) {
        if (obj == null) {
            return true;
        }
        Map map = (Map) obj;
        return map.isEmpty();
    }

    public boolean notEmpty(Object obj) {
        return !empty(obj);
    }
}
