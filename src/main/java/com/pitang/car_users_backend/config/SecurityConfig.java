package com.pitang.car_users_backend.config;

import com.pitang.car_users_backend.security.JwtAuthEntryPoint;
import com.pitang.car_users_backend.security.JwtAuthFilter;
import com.pitang.car_users_backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração de segurança, responsável por configurar
 * a autenticação, autorização e filtros de segurança (JWT, etc.).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configura o PasswordEncoder para criptografia de senhas.
     * Aqui, usamos o BCryptPasswordEncoder.
     *
     * @return instância de PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o AuthenticationProvider usando o CustomUserDetailsService
     * para buscar usuários e o BCrypt para encodar senhas.
     *
     * @return provider de autenticação para uso no AuthenticationManager
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Permite injetar o AuthenticationManager (gerenciado pelo Spring)
     * em outras classes, como Controllers.
     *
     * @param config objeto de configuração de autenticação
     * @return instância do AuthenticationManager
     * @throws Exception caso ocorra erro ao obter o manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuração principal da cadeia de filtros de segurança.
     * Define quais rotas são públicas e quais exigem autenticação,
     * desabilita CSRF e define session como STATELESS (usando JWT).
     *
     * @param http objeto HttpSecurity para configurar
     * @return instância final de SecurityFilterChain
     * @throws Exception caso ocorra erro na configuração
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/users", "/api/users/**", "/api/signin").permitAll();
                    auth.requestMatchers("/swagger-ui.html", "/swagger-ui.index.html", "/swagger-ui/**",
                            "/swagger-resources/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll();
                    auth.requestMatchers("/h2-console/**", "/webjars/**").permitAll();
                    auth.anyRequest().authenticated();
                });

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
