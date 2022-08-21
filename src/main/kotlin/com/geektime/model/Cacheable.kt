package com.geektime.model

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Cacheable(val key: String, val evict: Boolean = false, val condition: String = "")
