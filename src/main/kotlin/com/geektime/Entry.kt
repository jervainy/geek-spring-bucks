package com.geektime

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Entry

fun main(args: Array<String>) {
    runApplication<Entry>(*args)
}

