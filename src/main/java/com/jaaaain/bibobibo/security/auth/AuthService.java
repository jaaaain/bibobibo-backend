package com.jaaaain.bibobibo.security.auth;

import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.enums.UserEnums;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("auth")
public class AuthService {

    public boolean isSelf(Long targetUserId) {
        UserData.AuthDto authDto = (UserData.AuthDto) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return authDto != null && Objects.equals(authDto.getId(), targetUserId);
    }

    public boolean isSelf(String targetUsername) {
        UserData.AuthDto authDto = (UserData.AuthDto) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return authDto != null && Objects.equals(authDto.getUsername(), targetUsername);
    }

    public boolean isAdmin() {
        UserData.AuthDto authDto = (UserData.AuthDto) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return authDto != null && authDto.getRole() == UserEnums.Role.ADMIN;
    }
}