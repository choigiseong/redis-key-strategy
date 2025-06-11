package com.coco.keykey

import com.coco.keykey.Const.REDIS_DEFAULT_TIME_UNIT
import com.coco.keykey.Const.REDIS_DEFAULT_TTL
import com.coco.keykey.Const.TTL_RENEWAL_THRESHOLD_SECONDS
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisTTLUpdateAOP(
    private val template: StringRedisTemplate
) {
    @AfterReturning(
        pointcut = "@annotation(redisTTLUpdate)",
        returning = "returnValue",
    )
    fun afterReturning(
        joinPoint: JoinPoint,
        returnValue: Any?,
    ) {
        val key = joinPoint.args[0] as String

        if (returnValue == null) {
            return
        }

        val remainingTTLSeconds = template.getExpire(key, TimeUnit.SECONDS)

        if (remainingTTLSeconds == -1L || remainingTTLSeconds == -2L) {
            return
        }

        if (remainingTTLSeconds <= TTL_RENEWAL_THRESHOLD_SECONDS) {
            template.expire(key, REDIS_DEFAULT_TTL, REDIS_DEFAULT_TIME_UNIT)
        }
    }

}