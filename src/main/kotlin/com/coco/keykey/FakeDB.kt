package com.coco.keykey

import org.springframework.stereotype.Component

@Component
class FakeDB {
    private val db = mutableMapOf<String, String>(
        "1" to "1",
        "2" to "2",
        "3" to "3"
    )

    fun put(key: String, value: String) {
        db[key] = value
    }

    fun get(key: String): String? {
        return db[key]
    }
}