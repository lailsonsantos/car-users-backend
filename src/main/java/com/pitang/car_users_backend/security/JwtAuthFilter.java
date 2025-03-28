package com.pitang.car_users_backend.security;

import com.pitang.car_users_backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o header de autorização
        String header = request.getHeader("Authorization");

        // Se não houver header ou não começar com "Bearer ", segue o filtro sem autenticação
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // Remove "Bearer " do token
        String username;

        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception ex) {
            logger.error("Erro ao obter username a partir do token: {}", ex.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token inválido ou expirado");
            return;
        }

        // Se o username foi obtido e não há autenticação no contexto, prossegue com a validação
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.error("Token não validado para o usuário: {}", username);
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Sessão inválida");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
