package com.example.application_publication_article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApplicationPublicationArticleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationPublicationArticleApplication.class, args);
	}

}
