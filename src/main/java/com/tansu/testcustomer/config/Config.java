package com.tansu.testcustomer.config;

import com.tansu.testcustomer.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class Config {
    @Bean
    @Profile(value = {Constants.TEST,Constants.DEV})
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/**");

    }


}

