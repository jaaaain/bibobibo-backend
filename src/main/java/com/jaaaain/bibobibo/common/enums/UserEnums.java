package com.jaaaain.bibobibo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public class UserEnums {

    /** 用户角色 */
    public enum Role {
        USER(0, "USER"),       // 普通用户
        ADMIN(1, "ADMIN");      // 管理员

        @EnumValue
        public final int value;
        public final String name;
        Role(int value, String name) { this.value = value; this.name = name; }
    }

    /** 性别 */
    public enum Gender {
        SECRET(0),  // 保密
        MALE(1),    // 男
        FEMALE(2);  // 女

        @EnumValue
        public final int value;
        Gender(int value) { this.value = value; }
    }

    /** 会员等级 */
    public enum Vip {
        NORMAL(0),   // 普通用户
        MONTHLY(1),  // 月度VIP
        ANNUAL(2);   // 年度VIP

        @EnumValue
        public final int value;
        Vip(int value) { this.value = value; }
    }

    /** 用户状态 */
    public enum State {
        NORMAL(0),   // 正常
        BANNED(1);   // 封禁

        @EnumValue
        public final int value;
        State(int value) { this.value = value; }
    }
}
