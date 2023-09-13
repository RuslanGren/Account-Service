package com.example.accountservice.controller;


import com.example.accountservice.models.employee.EmployeeRequest;
import com.example.accountservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> postPayments(@RequestBody ArrayList<EmployeeRequest> employees) {
        return employeeService.addPayments(employees);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<?> putPayment(@RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updatePayment(employeeRequest);
    }

    @GetMapping(value = "/empl/payment", params = "period")
    public ResponseEntity<?> getPaymentByPeriod(@RequestParam String period,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return employeeService.getPaymentByPeriod(period, userDetails);
    }

    @GetMapping(value = "/empl/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails userDetails) {
        return employeeService.getPayment(userDetails);
    }

}
