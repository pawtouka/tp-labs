
package org.example;

public class Manager extends Employee {
    private final int teamSize;

    public Manager(String name, double salary, int teamSize) {
        super(name, salary);

        if (teamSize < 0) {
            throw new IllegalArgumentException(
                    "Размер команды не может быть отрицательным");
        }

        this.teamSize = teamSize;
    }

    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}
