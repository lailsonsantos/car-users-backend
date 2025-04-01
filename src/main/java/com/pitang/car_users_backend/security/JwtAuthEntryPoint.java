package com.pitang.car_users_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point para lidar com autenticações não autorizadas.
 * Se não houver credenciais válidas, retorna 401.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Chamado quando o usuário tenta acessar um recurso protegido sem credenciais.
     * @param request HTTP request
     * @param response HTTP response
     * @param authException exceção de autenticação
     * @throws IOException em caso de erro de I/O
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
