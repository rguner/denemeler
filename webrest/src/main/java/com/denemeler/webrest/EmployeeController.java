package com.denemeler.webrest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/employees")
public class EmployeeController {


    @GetMapping("/{id}")
    private Employee getEmployeeById(@PathVariable String id) {

        Employee employee = new Employee();
        employee.setId("Id1");
        employee.setName("Ramazan Güner");
        return employee;
    }

    @GetMapping
    private List<Employee> getAllEmployees() {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        sleep();
        System.out.println("EmployeeController.getAllEmployees :" + Thread.currentThread().getName());
        List<Employee> list = new ArrayList<>();
        IntStream.range(1,100).forEach( i -> {
                    Employee employee = new Employee();
                    employee.setId("Id1_" + i);
                    employee.setName("Ramazan Güner");
                    list.add(employee);
                    Employee employee2 = new Employee();
                    employee2.setId("Id2_" + i);
                    employee2.setName("Ceyhan Güner");
                    list.add(employee2);
                    Employee employee3 = new Employee();
                    employee3.setId("Id3_" + i);
                    employee3.setName("Şevval Güner");
                    list.add(employee3);
                    Employee employee4 = new Employee();
                    employee4.setId("Id4_" + i);
                    employee4.setName("Ömer Mete Güner");
                    list.add(employee4);
                });
        System.out.println("EmployeeController.getAllEmployees bitti:" + Thread.currentThread().getName());
        return list;
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
