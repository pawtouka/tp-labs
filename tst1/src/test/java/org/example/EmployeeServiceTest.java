
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EmployeeServiceTest.LoggingTestWatcher.class)
class EmployeeServiceTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(EmployeeServiceTest.class);

    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private EmployeeService service;

    // Цветные сообщения
    private void logOk(String message) {
        LOG.info("{}[OK] {}{}", GREEN, message, RESET);
    }

    private void logWarn(String message) {
        LOG.warn("{}[WARN] {}{}", YELLOW, message, RESET);
    }

    private void logFail(String message) {
        LOG.error("{}[FAIL] {}{}", RED, message, RESET);
    }

    @BeforeEach
    void setUp() {
        service = new EmployeeService();
        LOG.info("Создан EmployeeService");
    }

    @Test
    void managerSalaryTest() {
        LOG.info("Начинаем managerSalaryTest");

        Manager manager = new Manager("Anna", 5000, 8);

        LOG.info("Имя: {}", manager.getName());
        LOG.info("Зарплата: {}", manager.getSalary());
        LOG.info("Размер команды: {}", manager.getTeamSize());

        assertEquals(5000, manager.getSalary());
        assertEquals(8, manager.getTeamSize());
        assertEquals("Manager", manager.getRole());

        logOk("Зарплата и параметры менеджера проверены");
    }

    @Test
    void developerSalaryTest() {
        LOG.info("Начинаем developerSalaryTest");

        Developer developer =
                new Developer("Ivan", 4000, "Java", 3);

        LOG.info("Имя: {}", developer.getName());
        LOG.info("Зарплата: {}", developer.getSalary());
        LOG.info("Язык: {}", developer.getProgrammingLanguage());
        LOG.info("Опыт работы: {} лет",
                developer.getExperienceYears());

        assertEquals(4000, developer.getSalary());
        assertEquals("Java", developer.getProgrammingLanguage());
        assertEquals(3, developer.getExperienceYears());
        assertEquals("Developer", developer.getRole());

        logOk("Зарплата и параметры разработчика проверены");
    }

    @Test
    void averageSalaryTest() {
        LOG.info("Начинаем averageSalaryTest");

        List<Employee> employees = List.of(
                new Manager("Anna", 5000, 8),
                new Developer("Ivan", 4000, "Java", 3),
                new Developer("Petr", 3000, "Python", 2)
        );

        LOG.info("Количество сотрудников: {}", employees.size());

        double average = service.averageSalary(employees);

        LOG.info("Ожидаемая средняя зарплата: 4000.0");
        LOG.info("Полученная средняя зарплата: {}", average);

        assertEquals(4000.0, average, 0.001);

        logOk("Средняя зарплата рассчитана правильно");
    }

    @Test
    void filterByRoleTest() {
        LOG.info("Начинаем filterByRoleTest");

        List<Employee> employees = List.of(
                new Manager("Anna", 5000, 8),
                new Developer("Ivan", 4000, "Java", 3),
                new Developer("Petr", 3000, "Python", 2)
        );

        LOG.info("Фильтруем сотрудников по роли Developer");

        List<Employee> developers =
                service.filterByRole(employees, "Developer");

        LOG.info("Найдено разработчиков: {}", developers.size());

        assertEquals(2, developers.size());

        assertTrue(developers.stream()
                .allMatch(e -> e.getRole().equals("Developer")));

        logOk("Фильтрация по роли работает правильно");
    }

    @Test
    void emptyNameTest() {
        LOG.info("Начинаем emptyNameTest");

        logWarn("Проверяем менеджера с пустым именем");

        assertThrows(IllegalArgumentException.class,
                () -> new Manager("", 5000, 8));

        logWarn("Проверяем разработчика с пробелами вместо имени");

        assertThrows(IllegalArgumentException.class,
                () -> new Developer("   ", 4000, "Java", 3));

        logOk("Пустые имена корректно отклоняются");
    }

    @Test
    void emptyListTest() {
        LOG.info("Начинаем emptyListTest");

        List<Employee> employees = List.of();

        LOG.info("Проверяем среднюю зарплату пустого списка");

        assertEquals(0.0, service.averageSalary(employees));

        LOG.info("Проверяем фильтрацию пустого списка");

        assertTrue(service.filterByRole(
                employees, "Manager").isEmpty());

        LOG.info("Проверяем сортировку пустого списка");

        assertTrue(service.namesSorted(employees).isEmpty());

        logOk("Пустой список обрабатывается корректно");
    }

    @Test
    void invalidAdditionalParametersTest() {
        LOG.info("Начинаем invalidAdditionalParametersTest");

        logWarn("Размер команды не может быть отрицательным");

        assertThrows(IllegalArgumentException.class,
                () -> new Manager("Anna", 5000, -1));

        logWarn("Язык программирования не может быть пустым");

        assertThrows(IllegalArgumentException.class,
                () -> new Developer("Ivan", 4000, "", 3));

        logWarn("Опыт работы не может быть отрицательным");

        assertThrows(IllegalArgumentException.class,
                () -> new Developer("Ivan", 4000, "Java", -1));

        logOk("Некорректные дополнительные параметры отклоняются");
    }

    static class LoggingTestWatcher implements TestWatcher {

        private static final Logger resultLog =
                LoggerFactory.getLogger("TEST_RESULTS");

        @Override
        public void testSuccessful(
                ExtensionContext context) {

            resultLog.info("{}[OK] Тест {} пройден{}",
                    GREEN,
                    context.getDisplayName(),
                    RESET);
        }

        @Override
        public void testFailed(
                ExtensionContext context,
                Throwable cause) {

            resultLog.error("{}[FAIL] Тест {} провален. Причина: {}{}",
                    RED,
                    context.getDisplayName(),
                    cause.getMessage(),
                    RESET);
        }

        @Override
        public void testAborted(
                ExtensionContext context,
                Throwable cause) {

            resultLog.warn("{}[WARN] Тест {} прерван: {}{}",
                    YELLOW,
                    context.getDisplayName(),
                    cause.getMessage(),
                    RESET);
        }
    }
}
