package com.jaaaain.bibobibo.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    // 获取所有匹配的键
    public List<Long> getAllKeys(String keyPrefix){
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if (keys == null) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(key -> {
                    // 从 key 中提取 commentId，例如 "comment:like_count:123" -> 123
                    String commentIdStr = key.substring(key.lastIndexOf(":") + 1);
                    try {
                        return Long.parseLong(commentIdStr);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // 键递增
    public Long incr(String key, Integer increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    // 删除键
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 添加有序集合元素
    public void zAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value.toString(), score);
    }
    // 获取有序集合元素
    public Set<Long> zRevRange(String key, long start, long end) {
        Set<Object> rawSet = redisTemplate.opsForZSet().reverseRange(key, start, end);
        if (rawSet == null) {
            return null;
        }
        return rawSet.stream().map(v -> Long.valueOf(v.toString())).collect(Collectors.toSet());
    }
    // cursor游标分页
    public Set<Long> zRevRangeByScore(String key, double min, double max, long size) {
        Set<Object> rawSet = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, 0, size);
        if (rawSet == null) {
            return null;
        }
        return rawSet.stream().map(v -> Long.valueOf(v.toString())).collect(Collectors.toSet());
    }


    // 有序集合元素递增
    public void zIncrBy(String key, double increment, Object value) {
        redisTemplate.opsForZSet().incrementScore(key, value.toString(), increment);
    }
    // 有序集合元素删除
    public void zRemove(String key, Object value) {
        redisTemplate.opsForZSet().remove(key, value.toString());
    }

    public Double zScore(String key, Long id) {
        return redisTemplate.opsForZSet().score(key, id.toString());
    }
}