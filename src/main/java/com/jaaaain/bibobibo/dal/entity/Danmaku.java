package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 弹幕表(Danmaku)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("danmaku")
public class Danmaku implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id; // 弹幕ID
    private Long vid; // 视频ID
    private Long uid; // 用户ID
    private String content; // 弹幕内容
    private Double timePoint; // 弹幕位置(秒)
    private Integer fontSize; // 字体大小
    private String color; // 弹幕颜色 #FFFFFF
    private Integer mode; // 弹幕模式：0滚动；1顶部；2底部
    private Integer state; // 状态：0正常；1审核中；2不通过
    @TableField(fill = FieldFill.INSERT)
    private Date createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime; // 更新时间
    @TableLogic
    private Integer deleted; // 逻辑删除
}
