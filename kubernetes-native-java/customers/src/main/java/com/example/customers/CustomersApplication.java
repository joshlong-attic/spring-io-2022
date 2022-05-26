package com.example.customers;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerRepository repository) {
        return args -> Flux
                .just("A", "B", "C")
                .map(name -> new Customer(null, name))
                .flatMap(repository::save)
                .subscribe(System.out::println);
    }
}

@Controller
@ResponseBody
class HealthController {

    private final ApplicationContext context;

    HealthController(ApplicationContext context) {
        this.context = context;
    }

    @GetMapping("/down")
    void down() {
        AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
    }
}

record Customer(@Id Integer id, String name) {
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
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