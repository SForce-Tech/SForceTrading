package com.sforce.gymbuddy.config;

import com.sforce.gymbuddy.filter.JwtRequestFilter;
import com.sforce.gymbuddy.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructor to inject CustomUserDetailsService.
     *
     * @param customUserDetailsService the custom user details service
     * @param jwtRequestFilter         the jwt request filter service
     */
    @Autowired
    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http        the HttpSecurity object to configure
     * @param authManager the AuthenticationManager to use
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter
     *                   chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**", "/api/users/register", "/api/users/login").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.permitAll())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .httpBasic(Customizer.withDefaults())
                .authenticationManager(authManager)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures an in-memory user details manager with a default admin user.
     *
     * @param passwordEncoder the PasswordEncoder to use for encoding passwords
     * @return the configured InMemoryUserDetailsManager
     */
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("theAdmin")
                .password(passwordEncoder.encode("qwerty"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Configures a BCrypt password encoder.
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication manager with both custom and in-memory user
     * details services.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs while configuring the authentication
     *                   manager
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        authenticationManagerBuilder.userDetailsService(inMemoryUserDetailsManager(passwordEncoder()))
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    /**
     * Configures a composite user details service that tries the custom user
     * details service first, and falls back to
     * the in-memory user details manager if the user is not found.
     *
     * @param inMemoryUserDetailsManager the in-memory user details manager
     * @return the configured UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
        return username -> {
            UserDetails userDetails;
            try {
                userDetails = customUserDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                userDetails = inMemoryUserDetailsManager.loadUserByUsername(username);
            }
            return userDetails;
        };
    }
}
