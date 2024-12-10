package org.dci.theratrack.config;


import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        http.csrf(csrf -> csrf.disable()) // Disable CSRF
            .authorizeHttpRequests((requests) -> requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS requests
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/patients/**").hasRole(UserRole.ADMIN.name())
            .requestMatchers("/api/therapists/**").hasRole(UserRole.ADMIN.name())
            .requestMatchers("/api/users/**").hasRole(UserRole.ADMIN.name())
            .requestMatchers("/api/dashboard/**").authenticated()
            .anyRequest().authenticated()
        ).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> {});
        return http.build();
    }
}
 
