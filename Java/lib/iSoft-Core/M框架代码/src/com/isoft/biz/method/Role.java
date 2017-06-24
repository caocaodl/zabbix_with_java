package com.isoft.biz.method;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final Role NONE = new Role(0);

    /** 个人用户:1 */
    public static final Role TENANT = new Role(1 << 0);

    /** 系统管理员:2 */
    public static final Role LESSOR = new Role(1 << 1);
    
    /** 应用管理员:4 */
    public static final Role APPADMIN = new Role(1 << 2);
    
    /** 组织管理员:8 */
    public static final Role ORGADMIN = new Role(1 << 3);


    /** 任何角色:255 */
    public static final Role ANYONE = new Role(0xFF);

    public static final Map<String, Role> ROLES = new LinkedHashMap<String, Role>(4);
    static {
        ROLES.put("LESSOR", LESSOR);
        ROLES.put("TENANT", TENANT);
        ROLES.put("ANYONE", ANYONE);
        ROLES.put("NONE", NONE);

        ROLES.put("lessor", LESSOR);
        ROLES.put("tenant", TENANT);
        ROLES.put("anyone", ANYONE);
        ROLES.put("none", NONE);
    }

    private int magic;

    public Role(int magic) {
        this.magic = magic;
    }

    public int magic() {
        return magic;
    }

    public Role or(Role r) {
        return new Role(this.magic | r.magic);
    }

    public static boolean isAllow(int role, Role roleMask) {
        return (role & roleMask.magic) > 0;
    }

    public static boolean isAllow(Role role, Role roleMask) {
        return (role.magic & roleMask.magic) > 0;
    }

    public static boolean isLessor(Role role) {
        return (role.magic & LESSOR.magic) > 0;
    }

    public static boolean isLessor(int role) {
        return (role & LESSOR.magic) > 0;
    }

    public static boolean isTenant(Role role) {
        return (role.magic & TENANT.magic) > 0;
    }

    public static boolean isTenant(int role) {
        return (role & TENANT.magic) > 0;
    }

    @Override
    public String toString() {
        return String.valueOf(this.magic);
    }    
}
