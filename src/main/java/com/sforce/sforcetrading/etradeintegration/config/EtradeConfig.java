package com.sforce.sforcetrading.etradeintegration.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EtradeConfig {

    @Bean
    @Profile("dev")
    public AnnotationConfigApplicationContext devContext() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(SandBoxConfig.class);
        ctx.refresh();
        return ctx;
    }

    @Bean
    @Profile("prod")
    public AnnotationConfigApplicationContext prodContext() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(OOauthConfig.class);
        ctx.refresh();
        return ctx;
    }
}
