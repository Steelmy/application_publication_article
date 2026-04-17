package com.example.application_publication_article.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Dit à Spring : "Ceci est un fichier de configuration global"
public class SecurityConfig {

    @Bean // Dit à Spring : "Garde cet outil en mémoire, je vais en avoir besoin ailleurs"
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}