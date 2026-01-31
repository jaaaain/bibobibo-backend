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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    private final AuthRedisRepo authRedisRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        String header = req.getHeader("Authorization");

        // 1️. 没有 token → 匿名用户，直接放行
        if (!StrUtil.isNotBlank(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, resp);
            return;
        }

        // 2️. 解析 token
        String token = header.substring(7);
        UserData.AuthDto authDto = authRedisRepo.getAuth(token);
        if (authDto == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"msg\":\"token invalid\"}");
            return;
        }

        // 3️. 续期
        authRedisRepo.setAuth(token, authDto);

        // 4️. 构造权限
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + authDto.getRole().name()));

        // 5️. 构造 Authentication（关键）
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        authDto,          // 用户信息
                        null,             // 凭证（一般是 null）
                        authorities       // 权限列表（不能为空，否则 isAuthenticated=false）
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, resp);
    }
}
