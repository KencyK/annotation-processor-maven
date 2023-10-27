package org.example;

import org.example.EmployeeBuilder;

public class Main {
    public static void main(String[] args) {
        Employee employee = new Employee("abc", 25);
        Employee employee1 = new EmployeeBuilder().name("xyz").age(30).build();
        System.out.println(employee);
        System.out.println(employee1);
    }
}