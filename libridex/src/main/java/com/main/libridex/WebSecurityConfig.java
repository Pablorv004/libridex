package com.main.libridex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/", "/index", "/login/**", "/register", "/register/send",
                                                "/books/catalog", "/books/details/**", "/about", "/books/search")
                                .permitAll()
                                // Users allowed accesses
                                // Admin only accesses
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                // Allow Static resources access
                                .requestMatchers(
                                                "/resources/**",
                                                "/images/**",
                                                "/icon/**",
                                                "/static/**",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**",
                                                "/vendor/**",
                                                "/fonts/**",
                                                "/webjars/**")
                                .permitAll()
                                // Only allow users to lend, reserve and return.
                                .requestMatchers("/books/lend/**", "/books/reserve/**", "/books/cancelreserve/**",
                                                "/books/return/**",
                                                "/reservations/**")
                                .hasRole("USER")
                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .usernameParameter("email")
                                                .defaultSuccessUrl("/index", true)
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}