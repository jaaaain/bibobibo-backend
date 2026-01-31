package com.jaaaain.bibobibo.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
 
    @Bean
    public OpenAPI swaggerOpenAPI(){
        return new OpenAPI()
                .info(new Info().title("标题")
                        .contact(new Contact())
                        .description("详细介绍")
                        .version("版本v.1"))
                .addSecurityItem(
                        new SecurityRequirement().addList("BearerAuth")
                );
    }
}
