package com.coco.keykey

import com.coco.keykey.Const.REDIS_DEFAULT_TIME_UNIT
import com.coco.keykey.Const.REDIS_DEFAULT_TTL
import org.springframework.stereotype.Service
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript

@Service
class RedisService(
    private val db: FakeDB,
    private val template: StringRedisTemplate
) {


    //todo hot key 문제



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

    //GET말고 decrease할 때만 RedisTTLUpdate 사용하자. 이를 통해 최종 일관성까지의 시간을 벌어준다
    @RedisTTLUpdate
    fun decrease(productId: Long, quantity: Int): Boolean {
        val result = template.execute(
            DefaultRedisScript(DECR_LUA, Long::class.java),
            listOf(productId.toString()),
            quantity.toString(),
        )

        return when (result) {
            -1L -> throw IllegalStateException("재고 부족: productId=$productId, 요청 수량=$quantity")
            -2L -> throw IllegalStateException("Redis 캐시 누락 또는 초기화 실패: productId=$productId")
            else -> true
        }
    }

    companion object {
        private val DECR_LUA = """
            local current = redis.call("GET", KEYS[1])
            if not current then return -2 end
            if tonumber(current) < tonumber(ARGV[1]) then return -1 end
            local result = redis.call("DECRBY", KEYS[1], ARGV[1])
            return result
        """.trimIndent()
    }

}
