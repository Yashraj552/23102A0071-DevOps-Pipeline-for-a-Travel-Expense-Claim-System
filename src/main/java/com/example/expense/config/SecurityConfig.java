package com.example.expense.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/login", "/webjars/**").permitAll()
                .requestMatchers("/review/**").hasRole("REVIEWER")
                .requestMatchers("/claims/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Demo accounts for the MVP. Passwords are stored hashed. */
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails employee = User.withUsername("employee")
                .password(encoder.encode("password123")).roles("EMPLOYEE").build();
        UserDetails employeeTwo = User.withUsername("employee2")
                .password(encoder.encode("password123")).roles("EMPLOYEE").build();
        UserDetails reviewer = User.withUsername("reviewer")
                .password(encoder.encode("password123")).roles("REVIEWER").build();
        return new InMemoryUserDetailsManager(employee, employeeTwo, reviewer);
    }
}