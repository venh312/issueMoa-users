package com.issuemoa.users.domain.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, String value) {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(key, value);
    }

    public void set(String key, String value, Duration timeout) {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(key, value, timeout);
    }

    public String findByKey(String key) {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        return (String) vop.get(key);
    }

    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
}
