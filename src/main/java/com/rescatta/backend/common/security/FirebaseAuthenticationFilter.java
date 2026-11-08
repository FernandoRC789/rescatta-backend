package com.rescatta.backend.common.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Verifica el ID Token de Firebase enviado en {@code Authorization: Bearer <token>} y
 * deja disponibles en la request el UID, email y nombre del usuario autenticado, tanto
 * para ciudadanos (app iOS) como para administradores (panel web) — ambos usan Firebase
 * Authentication, la diferencia de rol vive en la tabla {@code app_users}
 * (ver {@code AppUserService}).
 */
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = authorization.substring(BEARER_PREFIX.length());

        try {
            FirebaseToken decodedToken =
                    FirebaseAuth.getInstance().verifyIdToken(idToken);

            request.setAttribute("firebaseUid", decodedToken.getUid());
            request.setAttribute("firebaseEmail", decodedToken.getEmail());
            request.setAttribute("firebaseName", decodedToken.getName());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Token de Firebase inválido o expirado.\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
