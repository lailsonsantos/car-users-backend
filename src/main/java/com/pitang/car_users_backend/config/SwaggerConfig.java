package com.pitang.car_users_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do Swagger/OpenAPI para documentação dos endpoints.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Cria uma instância de {@link OpenAPI} com as informações básicas da API.
     * @return instância configurada de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minha API")
                        .version("1.0")
                        .description("Documentação da API do meu projeto Spring Boot"));
    }

    /**
     * Cria um grupo de API público para agrupar as rotas da aplicação.
     * @return o agrupamento de rotas para documentação
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
