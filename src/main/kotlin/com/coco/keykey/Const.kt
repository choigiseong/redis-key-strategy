package com.coco.keykey

import java.util.concurrent.TimeUnit

object Const {
    const val REDIS_DEFAULT_TTL = 300L
    const val TTL_RENEWAL_THRESHOLD_SECONDS = 5
    val REDIS_DEFAULT_TIME_UNIT = TimeUnit.SECONDS

}