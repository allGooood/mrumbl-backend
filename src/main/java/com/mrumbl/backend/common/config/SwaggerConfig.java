package com.mrumbl.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("MRUMBL API")
                        .description("MRUMBL Backend API Documentation")
                        .version("v1.0")
                );
    }

//    @Bean
//    public OpenAPI openAPI() {
//        String jwt = "JWT";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
//                .name(jwt)
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")
//        );
//
//        return new OpenAPI()
//                .info(new Info()
//                        .title("MRUMBL API")
//                        .description("MRUMBL Backend API Documentation")
//                        .version("v1.0"))
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
}
