
package org.example;

public class Developer extends Employee {
    private final String programmingLanguage;
    private final int experienceYears;

    public Developer(
            String name,
            double salary,
            String programmingLanguage,
            int experienceYears) {

        super(name, salary);

        if (programmingLanguage == null
                || programmingLanguage.isBlank()) {
            throw new IllegalArgumentException(
                    "Язык программирования не может быть пустым");
        }

        if (experienceYears < 0) {
            throw new IllegalArgumentException(
                    "Опыт работы не может быть отрицательным");
        }

        this.programmingLanguage = programmingLanguage;
        this.experienceYears = experienceYears;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    @Override
    public String getRole() {
        return "Developer";
    }
}
