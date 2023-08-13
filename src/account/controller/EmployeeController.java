package account.controller;

import account.models.employee.Employee;
import account.models.employee.EmployeeRequest;
import account.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> payments(@RequestBody ArrayList<EmployeeRequest> employees) {
        return employeeService.addPayments(employees);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<?> payments(@RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updatePayment(employeeRequest);
    }

}
