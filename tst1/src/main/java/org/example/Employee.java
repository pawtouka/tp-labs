package org.example;

public abstract class Employee {
    private final String name;
    private final double salary;

    public Employee(String name, double salary) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        if (salary < 0 || !Double.isFinite(salary)) {
            throw new IllegalArgumentException("Некорректная зарплата");
        }

        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public abstract String getRole();
}
