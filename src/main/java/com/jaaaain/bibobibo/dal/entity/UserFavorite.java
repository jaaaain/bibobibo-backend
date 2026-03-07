package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户收藏表(UserFavorite)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_favorite")
public class UserFavorite implements Serializable {
    private Long id;// 收藏ID
    private Long uid;// 用户ID
    private Integer targetType;// 1-视频,2-专辑
    private Long targetId;// 目标ID
    private Integer groupId;// 所属收藏夹ID
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间
    private Integer deleted;// 逻辑删除
}

