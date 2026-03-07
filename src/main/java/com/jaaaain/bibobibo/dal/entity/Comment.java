package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论表(Comment)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id; // 评论ID
    private Long uid; // 用户ID
    private Long vid; // 视频ID
    private Long rootId; // 根评论ID
    private Long parentId; // 父评论ID
    private Long toUid; // 回复目标用户ID
    private String content; // 评论内容
    private Integer likeCount; // 点赞数
    private Integer badCount; // 点踩数
    private Integer replyCount; // 回复数量
    private Integer isTop; // 是否置顶
    private Integer state; // 状态：0 正常;1 审核中;2 屏蔽
    private String ipLocation; // IP归属地
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableLogic
    private Integer deleted; // 逻辑删除
}
