package com.jorami.starterjoramiappjwt.auth.service.implementation;

import com.jorami.starterjoramiappjwt.auth.repository.TokenRepository;
import com.jorami.starterjoramiappjwt.auth.service.LogoutService;
import com.jorami.starterjoramiappjwt.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService, LogoutHandler {

    private final TokenRepository tokenRepository;


    //POST

    /**
     * Déconnecte un utilisateur en invalidant son jeton JWT.
     *
     * @param request l'objet {@link HttpServletRequest} contenant les informations de la requête HTTP,
     *          y compris l'en-tête "Authorization".
     * @param response l'objet {@link HttpServletResponse} permettant de personnaliser la réponse HTTP.
     * @param authentication l'objet {@link Authentication} représentant les informations
     *          d'authentification de l'utilisateur, ou {@code null} si aucune
     *          authentification n'est disponible.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        Token storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

}
