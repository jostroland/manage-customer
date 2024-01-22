package com.tansu.testcustomer.config;

import com.tansu.testcustomer.utils.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;

import static com.tansu.testcustomer.utils.Constants.*;


@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final Environment environment;

    @Value("${tansu.openapi.url}")
    private String url;

    @Bean
    public OpenAPI customnOpenApiConfig() {

            var server = new Server();
            server.setUrl(url);
            server.setDescription("Server URL in "
                            .concat((Objects.equals(System.getenv("spring.profiles.active"), DEV) ? "Development":"Production"))
                            .concat(" environment")
            );

            var contact = new Contact();
            contact.setEmail("jost@gmail.com");
            contact.setName("Jost Roland");
            contact.setUrl("https://www.tansu.com");

            var mitLicense = new License()
                    .name("MIT License")
                    .url("https://tansu.com/licenses/mit/");

            var info = new Info()
                    .title("Customer Management API")
                    .version("1.0")
                    .contact(contact)
                    .description("This API exposes endpoints to manage customers.").termsOfService("https://www.tansu.com/terms")
                    .license(mitLicense);

            return new OpenAPI().info(info).servers(List.of(server));
        }
}


