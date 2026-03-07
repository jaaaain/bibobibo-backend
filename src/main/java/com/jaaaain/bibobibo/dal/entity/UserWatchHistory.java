package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户观看历史表(UserWatchHistory)实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_watch_history")
public class UserWatchHistory implements Serializable {
    private Long id;// 观看历史ID
    private Long uid;// 用户ID
    private Long vid;// 视频ID
    private Integer watchDuration;// 观看时长（秒）
    private Object progress;// 播放进度百分比
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间
    private Integer deleted;// 逻辑删除
}

