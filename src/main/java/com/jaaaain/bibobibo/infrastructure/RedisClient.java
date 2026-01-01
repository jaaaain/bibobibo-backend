package com.jaaaain.bibobibo.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;

    // 设置键值对
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 带过期时间的设置
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 获取值
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        throw new ClassCastException("无法将 " + value.getClass().getName() + " 转换为 " + clazz.getName());
    }

    // 删除键
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}