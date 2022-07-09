/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Firli
 */


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Jahitku")
                        .description("This is API Jahitku")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .url("jahitku.tailorship@gmail.com")
                                .email("jahitku.tailorship@gmail.com"))
                        .termsOfService("jahitku.tailorship@gmail.com")
                        .license(new License().name("License").url("#"))
                );
    }
}
