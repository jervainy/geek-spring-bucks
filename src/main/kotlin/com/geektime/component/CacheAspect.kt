package com.geektime.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.geektime.model.Cacheable
import lombok.extern.slf4j.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint
import org.springframework.context.expression.MapAccessor
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Slf4j
@Aspect
@Component
class CacheAspect(val redisTemplate: StringRedisTemplate, val objectMapper: ObjectMapper = ObjectMapper()) {

    final val logger = LoggerFactory.getLogger(javaClass)

    @Pointcut("@annotation(com.geektime.model.Cacheable)")
    fun pointCut() {}

    @Around(value = "pointCut()")
    fun around(pjp: ProceedingJoinPoint): Any? {
        if (pjp !is MethodInvocationProceedingJoinPoint) return pjp.proceed()
        val method = (pjp.signature as MethodSignature).method
        val map = argMap(method, pjp)
        val cacheable = method.getAnnotation(Cacheable::class.java)
        if (cacheable.condition.isNotBlank() && !evalSpEL<Boolean>(cacheable.condition, map)) return pjp.proceed()
        val key = evalSpEL<String>(cacheable.key, map)
        if (cacheable.evict) {
            val result = pjp.proceed()
            redisTemplate.delete(key)
            return result
        }
        if (method.returnType == Void.TYPE) return pjp.proceed()
        return try {
            val json = redisTemplate.opsForValue().get(key)
            objectMapper.readValue(json, method.returnType)
        } catch (e: Exception) {
            logger.error(e.message, e)
            val obj = pjp.proceed()
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(obj))
            obj
        }
    }


    private fun argMap(method: Method, pjp: MethodInvocationProceedingJoinPoint): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        for ((index, value ) in method.parameters.withIndex()) {
            map[value.name] = pjp.args[index]
        }
        return map.toMap()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> evalSpEL(el: String, dataMap: Map<String, Any?>): T {
        val parser = SpelExpressionParser()
        val exp = parser.parseExpression(el)
        val ctx = StandardEvaluationContext(dataMap)
        ctx.addPropertyAccessor(MapAccessor())
        ctx.setVariables(dataMap)
        return exp.getValue(ctx) as T
    }

}