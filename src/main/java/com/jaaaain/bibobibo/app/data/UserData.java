package com.jaaaain.bibobibo.app.data;

import com.jaaaain.bibobibo.common.enums.UserEnums;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Data
public class UserData {
    // ===Query===


    // ===VO===
    // =====Common=====
    /** 最小用户信息 */
    @Data
    public static class BriefVO {
        private Long id;
        private String avatar;
        private String nickname;
    }
    /** 用户统计数据 */
    @Data
    public static class StatVO {
        private Long follows;   // 关注
        private Long fans;      // 粉丝
        private Long likes;     // 获赞
        private Long plays;     // 播放量
        private Long videos;    // 投稿数
    }
    // =====Self=====
    /** 个人信息缩要 */
    @Data
    public static class SelfCardVO {
        private Long id;                  // 用户ID
        private String avatar;            // 头像URL
        private String nickname;          // 昵称
        private Integer exp;              // 经验值
        private Integer level;            // 当前等级（从exp推算）
        private UserEnums.Vip vip;        // VIP等级，0-普通用户，1-月度VIP，2-年度VIP
        private Integer coin;             // 硬币数
        private UserEnums.State state;    // 用户状态，0-正常，1-封禁
        private StatVO stat;              // 统计信息
    }

    /** 个人主页信息 */
    @Data
    public static class SelfProfileVO {
        private Long id;                    // 用户ID
        private String username;            // 用户名
        private String nickname;            // 昵称
        private String avatar;              // 头像url
        private String background;          // 主页背景url
        private String signature;           // 我的签名
        private Date birthday;              // 出生日期
        private UserEnums.Gender gender;    // 性别：0 保密；1 男；2 女
        private Integer exp;                // 经验值
        private Integer level;              // 当前等级（从exp推算）
        private Integer coin;               // 硬币数
        private UserEnums.Vip vip;          // VIP等级：0 普通用户；1 月度VIP；2 年度VIP
        private UserEnums.State state;      // 用户状态，0-正常，1-封禁
        private StatVO stat;                // 统计信息
    }

    // =====Public=====
    /** 用户卡片信息 */
    @Data
    public static class CardVO {
        private Long id; // 用户ID
        private String avatar; // 头像url
        private String nickname; // 昵称
        private String signature; // 签名
        private UserEnums.Gender gender; // 性别
        private Integer level; // 当前等级（从exp推算）
        private UserEnums.Vip vip; // VIP等级
        private UserEnums.State state;    // 用户状态，0-正常，1-封禁
        private StatVO stat; // 统计信息
    }

    /** 进入别人主页 */
    @Data
    public static class PublicProfileVO {
        private Long id; // 用户ID
        private String nickname; // 昵称
        private String avatar; // 头像url
        private String signature; // 签名
        private Date birthday; // 出生日期
        private UserEnums.Gender gender; // 性别
        private UserEnums.Vip vip; // VIP等级
        private Integer level; // 当前等级（从exp推算）
        private UserEnums.State state;    // 用户状态，0-正常，1-封禁
        private StatVO stat; // 统计信息
    }

    // ===DTO===
    @Data
    public static class LoginDto {
        @NotNull(message = "用户名不能为空")
        private String username;
        @NotNull(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class RegisterDto {
        @NotNull(message = "用户名不能为空")
        private String username;
        @NotNull(message = "密码不能为空")
        private String password;
        @NotNull(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;
    }

    @Data
    public static class AuthDto {
        private Long id;
        private String username;
        private UserEnums.Role role;
        private String token;
    }

    // ===POJO===



}
