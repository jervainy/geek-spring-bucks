package com.geektime

import com.github.pagehelper.PageInterceptor
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@EnableAspectJAutoProxy
@Configuration
class AppConfig {

    @Bean
    fun mybatisCustomizer() = ConfigurationCustomizer {
        it.addInterceptor(PageInterceptor())
    }




}