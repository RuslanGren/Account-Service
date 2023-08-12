package account.controller;

import account.models.employee.Employee;
import account.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class EmployerController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> payments(@RequestBody ArrayList<Employee> employees) {
        return employeeService.addPayments(employees);
    }

}
