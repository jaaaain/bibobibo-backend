package com.jaaaain.bibobibo.app.data;

import com.jaaaain.bibobibo.common.enums.VideoEnums;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

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

        private VideoEnums.Type type; // 类型：0自制；1转载

        private VideoEnums.Visible visible; // 可见范围：0公开；1仅自己

        private VideoEnums.State state; // 状态：0审核中；1通过；2不通过-投稿问题；3违规删除

        private LocalDateTime releaseTime; // 发布时间
        private LocalDateTime releaseTimeMin; // 发布时间最小值
        private LocalDateTime releaseTimeMax; // 发布时间最大值

        private LocalDateTime updateTime; // 更新时间
        private LocalDateTime updateTimeMin; // 更新时间最小值
        private LocalDateTime updateTimeMax; // 更新时间最大值
    }
    // ===VO===
    /** 视频统计信息 */
    @Data
    public static class StatVO {
        private Long play; // 播放
        private Long danmaku; // 弹幕
        private Long like; // 点赞
        private Long favorite;// 收藏
        private Long share; // 分享
        private Long coin; // 投币
    }

    /** 视频预览页卡片 */
    @Data
    public static class CardVO {
        private Long id; // 视频ID
        private String title; // 视频标题
        private String coverUrl; // 封面url
        private Double duration; // 视频时长(秒)
        private LocalDateTime releaseTime;
        private StatVO stat; // 统计信息
        private UserData.BriefVO owner; // UP主信息
    }

    /** 视频播放页详情 */
    @Data
    public static class DetailVO {
        private Long id; // 视频ID
        private String title; // 视频标题
        private String introduction; // 描述
        private String coverUrl; // 封面url
        private String videoUrl; // 视频url
        private String tags; // 标签
        private VideoEnums.Type type; // 类型：-1 未知；0自制；1转载
        private VideoEnums.Visible visible; // 可见范围：0公开；1仅自己
        private VideoEnums.State state; // 状态：-1草稿；0审核中；1通过；2不通过-投稿问题；3违规删除
        private LocalDateTime releaseTime; // 发布时间
        private Double duration; // 视频时长(秒)
        private StatVO statVO; // 统计信息
        private UserData.CardVO owner; // UP主信息
    }

    /** 用户与视频关联信息 */
    @Data
    public static class RelationVO {
        private Boolean attention; // 关注
        private Boolean favorite; // 收藏
        private Boolean like; // 点赞
        private Boolean dislike; // 踩
        private Integer coin; // 投币
    }

    /** 视频草稿信息 */
    @Data
    public static class DraftVO {
        private Long id; // 视频ID
        private String title; // 视频标题
        private String introduction; // 视频简介
        private String coverUrl; // 视频封面url
        private String videoUrl; // 视频url
        private String tags; // 视频标签
        private VideoEnums.Type type; // 视频类型：0自制；1转载
        private VideoEnums.Visible visible; // 视频可见范围：0公开；1仅自己
    }

    // ===DTO===
    /** 更新视频信息 */
    @Data
    public static class UpdateDto {
        private Long id;
        @NotBlank
        private String title; // 视频标题
        private String introduction; // 视频简介
        @NotBlank
        private String coverUrl; // 视频封面url
        private String tags; // 标签
        private VideoEnums.Type type; // 类型：-1 未知；0自制；1转载
        private VideoEnums.Visible visible; // 可见范围：0公开；1仅自己
    }

    // ===POJO===
    /** 视频元信息 */
    @Data
    @Builder
    public static class Meta {
        private Double duration;// 视频时长（秒）
        private Integer width; // 分辨率-宽
        private Integer height;// 分辨率-高
        private String videoCodec; // 视频编码
        private String audioCodec; // 音频编码
        private Long bitrate; // 视频码率
    }



}
