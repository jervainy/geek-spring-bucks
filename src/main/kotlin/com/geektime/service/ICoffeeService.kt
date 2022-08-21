package com.geektime.service

import com.geektime.model.Coffee
import com.github.pagehelper.PageInfo

interface ICoffeeService {

    fun page(pageNo: Int, pageSize: Int): PageInfo<Coffee>

    fun selectOne(id: Int): Coffee?

    fun save(coffee: Coffee)

    fun del(id: Int)

    fun pv(): List<Any>

}