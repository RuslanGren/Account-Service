package account.models.employee;

public class EmployeeRequest {
    private String employee;

    private String period;

    private int salary;

    public EmployeeRequest() {
    }

    public EmployeeRequest(String employee, String period, int salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
