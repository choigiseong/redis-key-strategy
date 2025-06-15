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
        returnValue: Boolean?,
    ) {
        if (returnValue !is Boolean || !returnValue) return

        val key = joinPoint.args.getOrNull(0)?.toString() ?: return

        val ttl = template.getExpire(key, TimeUnit.SECONDS)
        if (ttl <= TTL_RENEWAL_THRESHOLD_SECONDS) {
            template.expire(key, REDIS_DEFAULT_TTL, REDIS_DEFAULT_TIME_UNIT)
        }
    }

}