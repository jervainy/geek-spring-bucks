package com.geektime.controller

import com.geektime.model.Coffee
import com.geektime.service.ICoffeeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/coffee")
class CoffeeController(val coffeeService: ICoffeeService) {


    @PostMapping("/save")
    fun save(@RequestBody dto: Coffee) = coffeeService.save(dto)

    @GetMapping("/page")
    fun page(@RequestParam pageNo: Int = 1, @RequestParam pageSize: Int = 10) = coffeeService.page(pageNo, pageSize)

    @GetMapping("/{id}")
    fun selectOne(@PathVariable("id") id: Int) = coffeeService.selectOne(id)

    @GetMapping("/pv")
    fun pv() = coffeeService.pv()


}