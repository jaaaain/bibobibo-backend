package com.jaaaain.bibobibo.middleware.redis;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.common.constants.RedisConstants;
import com.jaaaain.bibobibo.infrastructure.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UploadRedisRepo {
    private final RedisClient redisClient;
    private static final String UPLOAD_KEY = RedisConstants.UPLOAD_KEY;
    private static final Long UPLOAD_TTL = RedisConstants.UPLOAD_TTL;


    public void setUploadSession(String md5, UploadData.UploadSession session) {
        redisClient.setWithExpire(UPLOAD_KEY + md5, session, UPLOAD_TTL, TimeUnit.HOURS);
    }

    public void removeUploadSession(String md5) {
        redisClient.delete(UPLOAD_KEY + md5);
    }

    public UploadData.UploadSession getUploadSession(String md5) {
        return redisClient.get(UPLOAD_KEY + md5, UploadData.UploadSession.class);
    }
}
