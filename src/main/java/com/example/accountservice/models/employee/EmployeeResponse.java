package com.example.accountservice.models.employee;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EmployeeResponse {
    private String name;

    private String lastname;

    private String period;

    private String salary;

    public EmployeeResponse() {
    }

    public EmployeeResponse(String name, String lastname, String period, long salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = convertPeriod(period);
        this.salary = convertSalary(salary);
    }

    public EmployeeResponse(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    private static String convertSalary(long salary) {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }

    private static String convertPeriod(String inputPeriod) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        YearMonth yearMonth = YearMonth.parse(inputPeriod, inputFormatter);

        return outputFormatter.format(yearMonth);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
