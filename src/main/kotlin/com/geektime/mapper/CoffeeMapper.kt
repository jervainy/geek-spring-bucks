package com.geektime.mapper

import com.geektime.model.Coffee
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CoffeeMapper {

    fun selectList(): List<Coffee>

    fun selectOne(id: Int): Coffee?

    fun insert(entity: Coffee): Int

    fun update(entity: Coffee): Int

    fun del(id: Int): Int

}