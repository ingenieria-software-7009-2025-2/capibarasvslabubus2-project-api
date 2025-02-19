package com.example.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/mascota")
class MascotaControl {

    @GetMapping
    fun retrieveMascota(): ResponseEntity<Mascota>{
        val miMascota = Mascota(tipo = "gato", name="Keka", peso="4kg")
        return ResponseEntity.ok(miMascota)
    }

    @PostMapping
    fun creatMascota(@RequestBody mascotaBody:MascotaBody): ResponseEntity<Mascota>{
        val miMascota = Mascota(
            tipo = mascotaBody.tipo,
            name = mascotaBody.name,
            peso = mascotaBody.peso)
        return ResponseEntity.ok(miMascota)
    }
}