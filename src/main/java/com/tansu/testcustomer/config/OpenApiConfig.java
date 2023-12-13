package com.tansu.testcustomer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {


    @Value("${tansu.openapi.dev-url}")
    private String devUrl;

    @Value("${tansu.openapi.prod-url}")
    private String prodUrl;
    @Bean
    public OpenAPI customnOpenApiConfig() {

            Server devServer = new Server();
            devServer.setUrl(devUrl);
            devServer.setDescription("Server URL in Development environment");

            Server prodServer = new Server();
            prodServer.setUrl(prodUrl);
            prodServer.setDescription("Server URL in Production environment");

            Contact contact = new Contact();
            contact.setEmail("jost@gmail.com");
            contact.setName("Jost Roland");
            contact.setUrl("https://www.tansu.com");

            License mitLicense = new License().name("MIT License").url("https://tansu.com/licenses/mit/");

            Info info = new Info()
                    .title("Customer Management API")
                    .version("1.0")
                    .contact(contact)
                    .description("This API exposes endpoints to manage customers.").termsOfService("https://www.tansu.com/terms")
                    .license(mitLicense);

            return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
        }



}


