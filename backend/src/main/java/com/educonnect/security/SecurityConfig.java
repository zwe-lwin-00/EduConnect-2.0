package com.educonnect.security;

import com.educonnect.config.SecurityProperties;
import com.educonnect.web.common.RequestIdFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestIdFilter requestIdFilter;
    private final LoginRateLimitFilter loginRateLimitFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final SecurityProperties securityProperties;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RequestIdFilter requestIdFilter,
                          LoginRateLimitFilter loginRateLimitFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          SecurityProperties securityProperties) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.requestIdFilter = requestIdFilter;
        this.loginRateLimitFilter = loginRateLimitFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var auth = securityProperties.getAuth();
        var roles = securityProperties.getRoles();
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(auth.getPermitAllPathPatterns().toArray(new String[0])).permitAll()
                        .requestMatchers("/notifications/**").authenticated()
                        .requestMatchers(auth.getAdminPathPattern()).hasRole(roles.getAdmin())
                        .requestMatchers(auth.getTeacherPathPattern()).hasRole(roles.getTeacher())
                        .requestMatchers(auth.getParentPathPattern()).hasRole(roles.getParent())
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
