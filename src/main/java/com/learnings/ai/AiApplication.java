package com.learnings.ai;

import com.learnings.ai.service.MovieBookingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiApplication.class, args);
	}

    @Bean
    CommandLineRunner seedVectorStore(MovieBookingService bookingService) {
        return args -> {
            // Seed 1000 movie show documents into the Cassandra vector store
            bookingService.seedMoviesIntoVectorStore(1000);
        };
    }

}
