package com.jaaaain.bibobibo.common.constants;

import org.springframework.stereotype.Component;

@Component
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:"; // 登录验证码的key
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_TOKEN_KEY = "login:token:"; // 登录token的key
    public static final Long LOGIN_TOKEN_TTL = 36000L;
    public static final String UPLOAD_KEY = "upload:"; // 上传文件的key
    public static final Long UPLOAD_TTL = 3600L;
}
