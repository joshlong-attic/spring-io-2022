package com.example.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

}


@Component
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
class K8SRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        System.out.println("Hello, Kubernetes!");
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
        AvailabilityChangeEvent.publish(this.context,
                LivenessState.BROKEN);
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
    Flux<Customer> get() {
        return this.repository.findAll();
    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}