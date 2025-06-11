package com.coco.keykey

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val redisService: RedisService
) {

    @GetMapping("/key/{key}")
    fun get(@PathVariable("key") key: String): String? {
        return redisService.get(key)
    }
}