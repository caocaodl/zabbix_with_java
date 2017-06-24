package com.isoft.utils.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

@SuppressWarnings("deprecation")
public class VelocityDummyLog implements LogSystem {

    public void init(RuntimeServices arg0) throws Exception {
    }

    public void logVelocityMessage(int level, String msg) {
    }

}
