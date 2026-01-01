package com.jaaaain.bibobibo.middleware.redis;

import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.constants.RedisConstants;
import com.jaaaain.bibobibo.infrastructure.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthRedisRepo {
    private final RedisClient redisClient;
    private static final String LOGIN_TOKEN_KEY = RedisConstants.LOGIN_TOKEN_KEY;
    private static final Long LOGIN_TOKEN_TTL = RedisConstants.LOGIN_TOKEN_TTL;
    private static final String LOGIN_CODE_KEY = RedisConstants.LOGIN_CODE_KEY;
    private static final Long LOGIN_CODE_TTL = RedisConstants.LOGIN_CODE_TTL;

    public void setAuth(String token, UserData.AuthDto authDto) {
        redisClient.setWithExpire(LOGIN_TOKEN_KEY + token, authDto, LOGIN_TOKEN_TTL, TimeUnit.HOURS);
    }

    public UserData.AuthDto getAuth(String token) {
        return redisClient.get(LOGIN_TOKEN_KEY + token, UserData.AuthDto.class);
    }

    public void removeAuth(String token) {
        redisClient.delete(LOGIN_TOKEN_KEY + token);
    }

    public void setLoginCode(String key, String code) {
        redisClient.setWithExpire(LOGIN_CODE_KEY + key, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
    }

    public String getLoginCode(String key) {
        return redisClient.get(LOGIN_CODE_KEY + key, String.class);
    }

    public void removeLoginCode(String key) {
        redisClient.delete(LOGIN_CODE_KEY + key);
    }
}
