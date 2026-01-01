package com.jaaaain.bibobibo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public class VideoEnums {

    /** 视频类型 */
    public enum Type {
        UNKNOWN(-1),   // 未知
        ORIGINAL(0),   // 自制
        REPRINT(1);    // 转载

        @EnumValue
        public final int value;
        Type(int value) { this.value = value; }
    }

    /** 可见范围 */
    public enum Visible {
        PUBLIC(0),     // 公开
        PRIVATE(1);    // 仅自己

        @EnumValue
        public final int value;
        Visible(int value) { this.value = value; }
    }

    /** 审核状态 */
    public enum State {
        DRAFT(-1),            // 草稿
        REVIEWING(0),        // 审核中
        APPROVED(1),         // 通过
        FAILED_SUBMIT(2),    // 不通过 - 投稿问题
        VIOLATION_DELETE(3); // 违规删除

        @EnumValue
        public final int value;
        State(int value) { this.value = value; }
    }
}
