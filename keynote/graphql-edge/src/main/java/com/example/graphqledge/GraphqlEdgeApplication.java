package com.example.graphqledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class GraphqlEdgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlEdgeApplication.class, args);
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}


@Controller
class CrmGraphqlController {

    private final WebClient http;

    CrmGraphqlController(WebClient http) {
        this.http = http;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.http.get()
                .uri("http://localhost:8080/customers")
                .retrieve()
                .bodyToFlux(Customer.class);
    }


}

record Customer(Integer id, String name) {
}