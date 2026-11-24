package com.example.guitarpro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
@EnableCaching
@SpringBootApplication
class guitarproApplication

fun main(args: Array<String>) {
	runApplication<guitarproApplication>(*args)
}
