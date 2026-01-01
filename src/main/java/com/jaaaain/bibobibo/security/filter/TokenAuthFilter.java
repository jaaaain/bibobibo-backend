package com.jaaaain.bibobibo.security.filter;

import cn.hutool.core.util.StrUtil;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.enums.UserEnums;
import com.jaaaain.bibobibo.infrastructure.RedisClient;
import com.jaaaain.bibobibo.middleware.redis.AuthRedisRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    private final RedisClient redisClient;
    private final AuthRedisRepo authRedisRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        String token = req.getHeader("Authorization");
        if (!StrUtil.isNotBlank(token) || !token.startsWith("Bearer ")) {

            UserData.AuthDto authDto = new UserData.AuthDto();
            authDto.setId(1L);
            authDto.setUsername("admin");
            authDto.setRole(UserEnums.Role.ADMIN);
            // 构造匿名用户，后续根据认证用户进行路由过滤
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(authDto, authDto.getClass(), List.of());

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, resp); // 不予构造认证用户，直接进入路由过滤器，只能通过 permitAll 的路由
            return;
        }
        token = token.replace("Bearer ", "");

        UserData.AuthDto authDto = authRedisRepo.getAuth(token);
        if (authDto == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"msg\":\"token invalid\"}");
            return;
        }

        // 续期
        authRedisRepo.setAuth(token, authDto);

        // 构造认证用户，后续根据认证用户进行路由过滤
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(authDto, authDto.getClass(), List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(req, resp);
    }
}
