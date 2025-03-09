package com.unam.fciencias.urbanincidents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.Dotenv

@SpringBootApplication
class UrbanIncidentsApplication
// Foo commentttttttttttttttt
fun main(args: Array<String>) {
    val dotenv = Dotenv.configure().ignoreIfMissing().load()
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    runApplication<UrbanIncidentsApplication>(*args)
}
