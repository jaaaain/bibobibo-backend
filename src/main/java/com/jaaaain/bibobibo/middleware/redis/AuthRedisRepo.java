package com.jaaaain.bibobibo.middleware.redis;

import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.constants.RedisConstants;
import com.jaaaain.bibobibo.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthRedisRepo {
    private final RedisUtil redisUtil;
    private static final String LOGIN_TOKEN_KEY = RedisConstants.LOGIN_TOKEN_KEY;
    private static final Long LOGIN_TOKEN_TTL = RedisConstants.LOGIN_TOKEN_TTL;
    private static final String LOGIN_CODE_KEY = RedisConstants.LOGIN_CODE_KEY;
    private static final Long LOGIN_CODE_TTL = RedisConstants.LOGIN_CODE_TTL;

    public void setAuth(String token, UserData.AuthDto authDto) {
        redisUtil.setWithExpire(LOGIN_TOKEN_KEY + token, authDto, LOGIN_TOKEN_TTL, TimeUnit.HOURS);
    }

    public UserData.AuthDto getAuth(String token) {
        return redisUtil.get(LOGIN_TOKEN_KEY + token, UserData.AuthDto.class);
    }

    public void removeAuth(String token) {
        redisUtil.delete(LOGIN_TOKEN_KEY + token);
    }

    public void setLoginCode(String key, String code) {
        redisUtil.setWithExpire(LOGIN_CODE_KEY + key, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
    }

    public String getLoginCode(String key) {
        return redisUtil.get(LOGIN_CODE_KEY + key, String.class);
    }

    public void removeLoginCode(String key) {
        redisUtil.delete(LOGIN_CODE_KEY + key);
    }
}
