package com.denemeler.webreactiverest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @Autowired
    @Qualifier("subscriberTaskExecutor")
    ThreadPoolTaskExecutor subscriberTaskExecutor;

    @GetMapping("/{id}")
    private Mono<Employee> getEmployeeById(@PathVariable String id) {

        Mono<Employee> employeeMono = webClient.get()
                .uri("/employees/{id}", "1")
                .retrieve()
                .bodyToMono(Employee.class);

        employeeMono.subscribe(System.out::println);
        return employeeMono;
    }

    @GetMapping
    private Flux<Employee> getAllEmployees() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployees :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux = webClient.get()
                .uri("/employees")
                .retrieve()
                .bodyToFlux(Employee.class)
                .subscribeOn(Schedulers.fromExecutor(subscriberTaskExecutor))
                //.log()
                ;
        //employeeFlux.subscribe(e-> {System.out.println(e.getName() + " " + Thread.currentThread().getName());});
        System.out.println("EmployeeController.getAllEmployees bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }

    @GetMapping("/resttemplate")
    private Flux<Employee> getAllEmployees2() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployees2 :" + Thread.currentThread().getName());
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Employee[]> employeeList =
                restTemplate.getForEntity(
                        "http://localhost:8080/employees/",
                        Employee[].class);
        Employee[] employees = employeeList.getBody();


        Flux<Employee> employeeFlux = Flux.fromIterable(Arrays.asList(employees.clone()))
                .log();

        //employeeFlux.subscribe(System.out::println);
        System.out.println("EmployeeController.getAllEmployees2 bitti            :" + Thread.currentThread().getName());
        return employeeFlux;
    }

}
