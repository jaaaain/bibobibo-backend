package com.jaaaain.bibobibo.app.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentData {
    // ===Query===

    // ===VO===
    @Data
    public static class CommentVO {
        private Long id;                    // 评论ID
        private Long vid;                   // 视频ID
        private Long rootId;                // 根评论ID
        private Long parentId;              // 父评论ID
        private String content;             // 评论内容
        private Integer likeCount;          // 点赞数
        private Integer replyCount;         // 回复数量
        private Integer isTop;              // 是否置顶
        private String ipLocation;          // IP归属地
        private LocalDateTime createTime;   // 创建时间
        private UserData.BriefVO user;      // 评论用户信息
        private UserData.BriefVO replyToUser; // 回复目标用户信息（二级评论时使用）
        private Boolean isUpOwner;          // 是否是UP主
        private Boolean isLiked;            // 当前用户是否已点赞
        private Boolean isBad;              // 当前用户是否已点踩
    }
    // ===DTO===
    @Data
    public static class CreateDto {
        @NotNull(message = "视频ID不能为空")
        private Long vid;                   // 视频ID
        private Long rootId;                // 根评论ID（回复时使用）
        private Long parentId;              // 父评论ID（回复时使用）
        private Long toUid;                 // 回复目标用户ID（回复时使用）
        @NotBlank(message = "评论内容不能为空")
        @Size(max = 500, message = "评论内容不能超过500字")
        private String content;             // 评论内容
    }
    // ===POJO===
}
