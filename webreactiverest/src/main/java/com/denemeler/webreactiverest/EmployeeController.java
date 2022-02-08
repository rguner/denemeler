package com.denemeler.webreactiverest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
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

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    /*
    @Autowired
    @Qualifier("subscriberTaskExecutor")
    ThreadPoolTaskExecutor subscriberTaskExecutor;
     */

    @Autowired
    @Qualifier("publisherTaskExecutor")
    ThreadPoolTaskExecutor publisherTaskExecutor;

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
                .publishOn(Schedulers.fromExecutor(publisherTaskExecutor))
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

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<Employee> getAllEmployeesStream() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployeesStream :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux =
                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                        .mergeWith(
                                webClient.get()
                                        .uri("/employees")
                                        .retrieve()
                                        .bodyToFlux(Employee.class)
                                        .publishOn(Schedulers.fromExecutor(publisherTaskExecutor))
                                //.log()
                        );
        employeeFlux.subscribe(e -> {
            System.out.println(e.getName() + " " + Thread.currentThread().getName());
        });
        System.out.println("EmployeeController.getAllEmployeesStream bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }

   // curl -v http://localhost:9090/employees/stream2
    @GetMapping(value = "/stream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<Employee> getAllEmployeesStream2() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployeesStream2 :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux =
                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                        .delayElements(Duration.ofMillis(2000))
                        .publishOn(Schedulers.fromExecutor(publisherTaskExecutor))
                //.log()
                ;
        employeeFlux.subscribe(e -> {
            System.out.println(e.getName() + " " + Thread.currentThread().getName());
        });
        System.out.println("EmployeeController.getAllEmployeesStream2 bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }

    @GetMapping(value = "/stream3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<Employee> getAllEmployeesStream3() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployeesStream3 :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux =
                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                        .mergeWith(
                                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                                        .delayElements(Duration.ofMillis(2000))
                                        .publishOn(Schedulers.fromExecutor(publisherTaskExecutor))
                                //.log()
                        );
        employeeFlux.subscribe(e -> {
            System.out.println(e.getName() + " " + Thread.currentThread().getName());
        });
        System.out.println("EmployeeController.getAllEmployeesStream3 bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }

    // curl -v http://localhost:9090/employees/ndjson
    @GetMapping(value = "/ndjson", produces = MediaType.APPLICATION_NDJSON_VALUE)
    private Flux<Employee> getAllEmployeesNdJson() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployeesNdJson :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux =
                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                        .delayElements(Duration.ofMillis(2000))
                        .publishOn(Schedulers.fromExecutor(publisherTaskExecutor))
                //.log()
                ;
        employeeFlux.subscribe(e -> {
            System.out.println(e.getName() + " " + Thread.currentThread().getName());
        });
        System.out.println("EmployeeController.getAllEmployeesNdJson bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }

    ///stream2 ile farkı, Content-Type: text/event-stream olmadığı için delay nedeniyle tüm yanıt gecikiyor. partial yanıt yok..
    @GetMapping(value = "/json")
    private Flux<Employee> getAllEmployeesJson() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployeesJson :" + Thread.currentThread().getName());
        Flux<Employee> employeeFlux =
                Flux.fromStream(this::prepareEmployeeStream)  //.log()
                        .delayElements(Duration.ofMillis(2000))
                        .publishOn(Schedulers.fromExecutor(publisherTaskExecutor));
        employeeFlux.subscribe(e -> {
            System.out.println(e.getName() + " " + Thread.currentThread().getName());
        });
        System.out.println("EmployeeController.getAllEmployeesJson bitti        :" + Thread.currentThread().getName());
        return employeeFlux;
    }


    private Stream<Employee> prepareEmployeeStream() {
        Employee employee1 = new Employee();
        employee1.setId("StaticId1");
        employee1.setName("Static Name1");
        Employee employee2 = new Employee();
        employee2.setId("StaticId2");
        employee2.setName("Static Name2");
        Employee employee3 = new Employee();
        employee3.setId("StaticId3");
        employee3.setName("Static Name3");

        return Stream.of(
                employee1,
                employee2,
                employee3
        );
    }


}
