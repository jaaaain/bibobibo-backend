package com.jaaaain.bibobibo.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jaaaain.bibobibo.common.enums.UserEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户表(Users)实体类
 * @since 2025-11-15 00:02:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;// 用户ID
    private String username;// 用户名
    private String nickname;// 昵称
    private String avatar;// 头像url
    private String background;// 主页背景url
    private String signature;// 我的签名
    private Date birthday;// 出生日期
    private String password;// 密码
    private String phone;// 手机号
    private UserEnums.Role role;// 用户角色：0 普通用户；1 管理员
    private UserEnums.Gender gender;// 性别：0 保密；1 男；2 女
    private Integer exp;// 经验值
    private Integer coin;// 硬币数
    private UserEnums.Vip vip;// VIP等级：0 普通用户；1 月度VIP；2 年度VIP
    private UserEnums.State state;// 状态：0 正常；1 封禁
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;// 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;// 更新时间
    @TableLogic
    private Integer deleted;// 逻辑删除
}

