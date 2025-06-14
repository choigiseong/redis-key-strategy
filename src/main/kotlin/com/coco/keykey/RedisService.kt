package com.coco.keykey

import com.coco.keykey.Const.REDIS_DEFAULT_TIME_UNIT
import com.coco.keykey.Const.REDIS_DEFAULT_TTL
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisService(
    private val db: FakeDB,
    private val template: StringRedisTemplate
) {


    //todo hot key 문제


    //GET말고 SET할 때만 RedisTTLUpdate 사용하자
    @RedisTTLUpdate
    fun get(key: String): String? {
        val redisValue = template.opsForValue().get(key)
        if (redisValue != null) {
            return redisValue
        }
        val value = db.get(key)
        if (value != null) {
            template.opsForValue().set(key, value, REDIS_DEFAULT_TTL, REDIS_DEFAULT_TIME_UNIT)
            return value
        }
        return null
    }
}
