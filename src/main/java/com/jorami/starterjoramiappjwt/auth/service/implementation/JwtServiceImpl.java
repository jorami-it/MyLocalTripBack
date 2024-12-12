package com.jorami.starterjoramiappjwt.auth.service.implementation;

import com.jorami.starterjoramiappjwt.auth.service.JwtService;
import com.jorami.starterjoramiappjwt.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;


    /**
     * Vérifie si un token JWT est valide pour un utilisateur donné.
     *
     * @param token le token JWT à valider
     * @param userDetails les détails de l'utilisateur pour lequel le token doit être validé
     * @return {@code true} si le token est valide, sinon {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extrait le nom d'utilisateur (username) contenu dans un token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une réclamation spécifique (claim) d'un token JWT.
     *
     * @param token le token JWT
     * @param claimResolver une fonction pour extraire la réclamation souhaitée
     * @param <T> le type de la réclamation extraite
     * @return la valeur de la réclamation extraite
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * @param userDetails les détails de l'utilisateur
     * @return le token JWT généré
     */
    public String generateToken(UserDetails userDetails) {
        //TODO: Ajouter les éléments que l'on souhaite dans la hashmap
        //TODO: Attention aux infos sensibles dans le token!
        User user = (User) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("firstname", user.getFirstname());
        claims.put("lastname", user.getLastname());
        return generateToken(claims, userDetails);
    }

    /**
     * Génère un token JWT avec des réclamations supplémentaires.
     *
     * @param claims une map contenant les réclamations supplémentaires à inclure dans le token
     * @param userDetails les détails de l'utilisateur
     * @return le token JWT généré
     */
    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    /**
     * Construit un token JWT avec des réclamations, une expiration et une clé de signature.
     *
     * @param extraClaims les réclamations supplémentaires
     * @param userDetails les détails de l'utilisateur
     * @param jwtExpiration la durée de validité du token (en millisecondes)
     * @return le token JWT généré
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Récupère la clé de signature pour signer les tokens JWT.
     *
     * @return la clé de signature
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Vérifie si un token JWT est expiré.
     *
     * @param token le token JWT
     * @return {@code true} si le token est expiré, sinon {@code false}
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration d'un token JWT.
     *
     * @param token le token JWT
     * @return la date d'expiration du token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait toutes les réclamations d'un token JWT.
     *
     * @param token le token JWT
     * @return les réclamations contenues dans le token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
