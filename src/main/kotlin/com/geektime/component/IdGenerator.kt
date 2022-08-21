package com.geektime.component

import org.springframework.data.domain.Range
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class IdGenerator(val redisTemplate: StringRedisTemplate) {

    fun gen(key: String): Long {
        val keyBytes = key.toByteArray()
        val last = redisTemplate.execute { conn -> conn.bitPos(keyBytes, true, Range.just(-1)) }?: 0
        if (last == -1L) {
            redisTemplate.opsForValue().setBit(key, 1, true)
            return 1
        }
        redisTemplate.opsForValue().setBit(key, last + 1, true)
        redisTemplate.opsForValue().setBit(key, last, false)
        return last + 1
    }

    fun reset(key: String) = redisTemplate.delete(key)

}