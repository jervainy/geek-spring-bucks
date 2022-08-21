package com.geektime.component

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.io.ByteArrayOutputStream
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class PvCounter(val redisTemplate: StringRedisTemplate): GenericFilterBean() {

    override fun doFilter(p0: ServletRequest, p1: ServletResponse, p2: FilterChain) {
        if (p0 is HttpServletRequest) {
            val url = p0.requestURI
            val value = System.currentTimeMillis().toString().toByteArray()
            redisTemplate.execute { it.pfAdd(url.toByteArray(), value) }
            val count = redisTemplate.execute { it.pfCount(url.toByteArray()) }?: 0L
            redisTemplate.opsForZSet().add("pv", url, count.toDouble())
        }
        p2.doFilter(p0, p1)
    }

}