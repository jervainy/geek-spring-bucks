package com.geektime.service.impl

import com.geektime.component.IdGenerator
import com.geektime.mapper.CoffeeMapper
import com.geektime.model.Cacheable
import com.geektime.model.Coffee
import com.geektime.service.ICoffeeService
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CoffeeServiceImpl(val coffeeMapper: CoffeeMapper, val idGenerator: IdGenerator, val redisTemplate: StringRedisTemplate): ICoffeeService {

    override fun page(pageNo: Int, pageSize: Int): PageInfo<Coffee> {
        val pageInfo = PageHelper.startPage<Coffee>(pageNo, pageSize).doSelectPageInfo<Coffee> {
            coffeeMapper.selectList()
        }
        return pageInfo
    }

    @Cacheable(key = "'coffee-' + id")
    override fun selectOne(id: Int): Coffee? = coffeeMapper.selectOne(id)

    @Transactional
    @Cacheable(key = "'coffee-' + coffee.id", evict = true, condition = "coffee.id != null")
    override fun save(coffee: Coffee) {
        if (coffee.id == null) {
            coffee.id = idGenerator.gen("coffee")
            coffeeMapper.insert(coffee)
        } else if (coffee.name != null && coffee.price != null) {
            coffeeMapper.update(coffee)
        }
    }

    @Transactional
    override fun del(id: Int) {
        coffeeMapper.del(id)
        idGenerator.reset("coffee")
    }

    override fun pv(): List<Any> {
        val items = redisTemplate.opsForZSet().reverseRangeWithScores("pv", 0, 10)
        return items?.map {
            mapOf("key" to it.value, "score" to it.score)
        }?: emptyList()
    }


}