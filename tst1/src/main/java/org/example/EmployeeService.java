package org.example;

import java.util.Comparator;
import java.util.List;

public class EmployeeService {

    public double averageSalary(List<Employee> employees) {
        if (employees.isEmpty()) {
            return 0.0;
        }

        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public List<Employee> filterByRole(
            List<Employee> employees, String role) {

        return employees.stream()
                .filter(e -> e.getRole().equalsIgnoreCase(role))
                .toList();
    }

    public List<String> namesSorted(List<Employee> employees) {
        return employees.stream()
                .map(Employee::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
