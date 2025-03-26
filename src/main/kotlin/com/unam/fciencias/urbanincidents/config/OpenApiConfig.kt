package com.tuempresa.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API de Usuarios")
                    .version("1.0")
                    .description("API para la gestión de usuarios")
                    .contact(
                        Contact()
                            .name("Soporte")
                            .email("soporte@tuempresa.com")
                            .url("https://tuempresa.com")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Servidor local"),
                    Server().url("https://api.tuempresa.com").description("Servidor en producción")
                )
            )
    }
}
