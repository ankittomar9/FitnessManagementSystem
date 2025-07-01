package com.fitness.gateway.user;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    /**
     * This method creates a WebClient.Builder bean that can be used to create WebClient instances.
     * The WebClient is a non-blocking, reactive client for making HTTP requests.
    // *
     // @return a WebClient.Builder instance
     */

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://USER-SERVICE")
               // .baseUrl("http://localhost:8081")
                .build();
    }
}