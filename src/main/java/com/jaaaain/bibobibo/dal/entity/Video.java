package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 视频表(Video)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("video")
public class Video implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id; // 视频ID
    private Long uid; // 用户ID
    private String title; // 视频标题
    private String introduction; // 视频简介
    private String coverUrl; // 视频封面url
    private String videoUrl; // 视频url
    private Double duration; // 视频时长(秒)
    private String tags; // 标签
    private VideoEnums.Type type; // 类型：0自制；1转载
    private VideoEnums.Visible visible; // 可见范围：0公开；1仅自己
    private VideoEnums.State state; // 状态：0审核中；1通过；2不通过-投稿问题；3违规删除
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime releaseTime; // 发布时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableLogic
    private Integer deleted; // 逻辑删除
}
