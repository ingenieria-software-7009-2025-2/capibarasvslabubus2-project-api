package com.unam.fciencias.urbanincidents

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.repository.UserRepository
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class UrbanIncidentsApplication {

    @Bean
    fun init(userRepository: UserRepository) = org.springframework.boot.CommandLineRunner {
        // Crea un usuario de prueba
        val testUser = User(email = "test@example.com", password = "12345", token = "tokenDePrueba")
        userRepository.save(testUser)
        println("Usuario de prueba guardado: $testUser")

        // Intentamos recuperarlo para confirmar que la operación de lectura funciona
        val retrievedUser = userRepository.findAll().firstOrNull()
        println("Usuario recuperado de la base de datos: $retrievedUser")
    }
}

fun main(args: Array<String>) {
    runApplication<UrbanIncidentsApplication>(*args)
}