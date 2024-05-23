package com.sforce.gymbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 console access
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 console access without authentication
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .permitAll())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // Disable frame options for H2 console
                ).httpBasic(Customizer.withDefaults()) // Enable Basic Authentication
        ;

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("theAdmin")
                .password("qwerty")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
