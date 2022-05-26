package com.example.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class EdgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb) {
        return rlb
                .routes()
                .route(rs -> rs.path("/proxy")
                        .filters(fs -> fs
                                .setPath("/customers")
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                .retry(10)
                        )
                        .uri("http://localhost:8080/")
                )
                .build();
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

    @BatchMapping
    Map<Customer, Profile> profile (List<Customer> customerList) {
        var map = new HashMap<Customer, Profile>();
        for (var c : customerList)
            map.put(c, new Profile(c.id()));
        return map;
    }

    /*
    @SchemaMapping(typeName = "Customer")
    Profile profile(Customer customer) {
        System.out.println("returning Profile for customer # " + customer.id());
        return new Profile(customer.id());
    }*/

    @QueryMapping
    Flux<Customer> customers() {
        return this.http
                .get()
                .uri("http://localhost:8080/customers")
                .retrieve()
                .bodyToFlux(Customer.class);
    }
}


record Customer(Integer id, String name) {
}

record Profile(Integer id) {
}