package com.example.clients;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientsApplication.class, args);
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080/").build();
    }

    @Bean
    CrmClient crmClient(WebClient http) {
        var proxyFactory = HttpServiceProxyFactory
                .builder(new WebClientAdapter(http))
                .build();
        return proxyFactory.createClient(CrmClient.class);
    }

}

@Component
class CrmInitializer implements ApplicationRunner {

    private final CrmClient crm;

    CrmInitializer(CrmClient crm) {
        this.crm = crm;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.crm
                .getCustomers()
                .subscribe(System.out::println);
    }
}

interface CrmClient {

    @GetExchange("/customers")
    Flux<Customer> getCustomers();
}

record Customer(Integer id, String name) {
}