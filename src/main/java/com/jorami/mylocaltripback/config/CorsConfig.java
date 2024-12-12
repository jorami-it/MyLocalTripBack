package com.jorami.mylocaltripback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuration globale des CORS
        registry.addMapping("/**")  // Applique la règle à toutes les routes de l'application
                .allowedOrigins("http://localhost:4200")  // Origine autorisée (ex: front-end Angular)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Méthodes autorisées
                .allowedHeaders("*")  // Autorise tous les en-têtes
                .allowCredentials(true)  // Permet d'envoyer des cookies ou informations d'authentification
                .maxAge(3600);  // Durée de mise en cache de la configuration CORS en secondes
    }
}
