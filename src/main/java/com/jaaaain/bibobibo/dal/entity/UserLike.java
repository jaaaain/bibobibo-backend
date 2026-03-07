package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户点赞表(UserLike)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_like")
public class UserLike implements Serializable {
    private Long id;// 点赞ID
    private Long uid;// 用户ID
    private Integer targetType;// 1-视频,2-评论
    private Long targetId;// 目标ID
    private Integer status;// 1-点赞,0-取消
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间
    private Integer deleted;// 逻辑删除
}

