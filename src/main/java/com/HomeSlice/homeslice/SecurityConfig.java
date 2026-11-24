
package com.HomeSlice.homeslice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    // Configure how users access HomeSlice
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        // Allow users to access the login page
        // without already being signed in
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login.html",
                                "/login.css",
                                "/login.js",
                                "/api/csrf"
                        ).permitAll()
                        // Require authentication
                        .anyRequest().authenticated()
                )
                // custom login page
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        // to dashboard after good login
                        .defaultSuccessUrl("/", true)
                        // failed login
                        .failureUrl("/login.html?error")
                        .permitAll()
                )
                // Keep this for API testing
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
