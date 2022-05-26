package com.example.customers;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@ResponseBody
@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }


}



@Controller
@ResponseBody
class CustomerRestController {

    private final CustomerRepository repository;

    CustomerRestController(CustomerRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/customers")
    Flux<Customer> customerFlux() {
        return this.repository.findAll();
    }


}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(Integer id, String name) {
}