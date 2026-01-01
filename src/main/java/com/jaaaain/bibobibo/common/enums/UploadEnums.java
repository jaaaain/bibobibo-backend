package com.jaaaain.bibobibo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public class UploadEnums {

    public enum FileUploadTypeEnum {
        FILE("file", "file/"),   // 文件
        VIDEO("video", "video/%s/"),   // 视频
        PICTURE("picture", "picture/"), // 图片
        AVATAR("avatar", "avatar/"), // 头像
        COVER("cover", "video/%s/cover/");   // 封面
        @EnumValue
        public final String type;
        public final String path;

        FileUploadTypeEnum(String type, String path) {
            this.type = type;
            this.path = path;
        }
    }

    public enum FileUploadStateEnum {
        INIT(0),    // 初次上传
        RESUME(1),     // 断点续传
        COMPLETED(2);      // 上传完成

        @EnumValue
        public final int value;

        FileUploadStateEnum(int value) {
            this.value = value;
        }
    }

}
