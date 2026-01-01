package com.jaaaain.bibobibo.app.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class VideoData {

    // ===Query===

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {
        private Long uid; // 用户ID

        private String title; // 视频标题
        private String titleLike; // 视频标题模糊匹配

        private String introduction; // 视频简介

        private Double duration; // 视频时长(秒)
        private Double durationMin; // 视频时长(秒)最小值
        private Double durationMax; // 视频时长(秒)最大值

        private String tags; // 标签

        private Integer type; // 类型：0自制；1转载

        private Integer visible; // 可见范围：0公开；1仅自己

        private Integer state; // 状态：0审核中；1通过；2不通过-投稿问题；3违规删除

        private Date releaseTime; // 发布时间
        private Date releaseTimeMin; // 发布时间最小值
        private Date releaseTimeMax; // 发布时间最大值

        private Date updateTime; // 更新时间
        private Date updateTimeMin; // 更新时间最小值
        private Date updateTimeMax; // 更新时间最大值
    }
    // ===VO===
    // ===DTO===
    // ===POJO===
}
