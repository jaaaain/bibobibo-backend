package com.jaaaain.bibobibo.app.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jaaaain.bibobibo.common.enums.UserEnums;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class UserData {
    // ===Query===


    // ===VO===
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleVO {
        private String avatar;            // 头像URL
        private String username;          // 用户名
        private String nickname;          // 昵称
        private Integer exp;              // 经验值
        private Integer level;            // 当前等级（从exp推算）
        private UserEnums.Vip vip;        // VIP等级，0-普通用户，1-月度VIP，2-年度VIP
        private Integer coin;             // 硬币数
        private UserEnums.State state;    // 用户状态，0-正常，1-封禁
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailVO {
        private String username;            // 用户名
        private String nickname;            // 昵称
        private String avatar;              // 头像url
        private String background;          // 主页背景url
        private String signature;           // 我的签名
        private Date birthday;              // 出生日期
        private UserEnums.Gender gender;    // 性别：0 保密；1 男；2 女
        private Integer exp;                // 经验值
        private Integer coin;               // 硬币数
        private UserEnums.Vip vip;          // VIP等级：0 普通用户；1 月度VIP；2 年度VIP
        private UserEnums.State state;      // 状态：0 正常；1 封禁
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



}
