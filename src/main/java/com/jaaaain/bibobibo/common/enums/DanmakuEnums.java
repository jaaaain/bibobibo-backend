package com.jaaaain.bibobibo.common.enums;

public class DanmakuEnums {

    /** 弹幕模式 */
    public enum Mode {
        ROLL(0),     // 滚动
        TOP(1),      // 顶部
        BOTTOM(2);   // 底部

        public final int value;
        Mode(int value) { this.value = value; }
    }

    /** 弹幕状态 */
    public enum State {
        NORMAL(0),       // 正常
        REVIEWING(1),    // 审核中
        REJECTED(2);     // 不通过

        public final int value;
        State(int value) { this.value = value; }
    }
}
