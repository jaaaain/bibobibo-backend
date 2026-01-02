package com.jaaaain.bibobibo.security.auth;

import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.enums.UserEnums;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("auth")
public class AuthContext {

    /** 获取当前登录用户（可能为 null） */
    public UserData.AuthDto getCurrentOrNull() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserData.AuthDto)) {
            return null;
        }

        return (UserData.AuthDto) authentication.getPrincipal();
    }

    /**
     * 必须登录，否则抛异常
     */
    public UserData.AuthDto getCurrent() {
        UserData.AuthDto auth = getCurrentOrNull();
        if (auth == null) {
            throw new RuntimeException("请先登录");
        }
        return auth;
    }

    /**
     * 是否本人（按用户ID）
     */
    public boolean isSelf(Long targetUserId) {
        UserData.AuthDto auth = getCurrentOrNull();
        return auth != null && Objects.equals(auth.getId(), targetUserId);
    }

    /**
     * 是否本人（按用户名）
     */
    public boolean isSelf(String targetUsername) {
        UserData.AuthDto auth = getCurrentOrNull();
        return auth != null && Objects.equals(auth.getUsername(), targetUsername);
    }

    /**
     * 是否管理员
     */
    public boolean isAdmin() {
        UserData.AuthDto auth = getCurrentOrNull();
        return auth != null && auth.getRole() == UserEnums.Role.ADMIN;
    }

    /**
     * 本人或管理员
     */
    public boolean isSelfOrAdmin(Long targetUserId) {
        UserData.AuthDto auth = getCurrentOrNull();
        return auth != null && (isAdmin() || Objects.equals(auth.getId(), targetUserId));
    }
}
