package com.denemeler.webresttemplate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @GetMapping("/{id}")
    private Employee getEmployeeById(@PathVariable String id) {

        return null;
    }

    @GetMapping
    private List<Employee> getAllEmployees() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("EmployeeController.getAllEmployees :" + Thread.currentThread().getName());
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Employee[]> employeeList =
                restTemplate.getForEntity(
                        "http://localhost:8080/employees/",
                        Employee[].class);
        Employee[] employees = employeeList.getBody();

        System.out.println("EmployeeController.getAllEmployees bitti            :" + Thread.currentThread().getName());
        return Arrays.asList(employees.clone());
    }

}
