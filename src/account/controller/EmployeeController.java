package account.controller;

import account.models.employee.EmployeeRequest;
import account.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT')")
    @PostMapping("/acct/payments")
    public ResponseEntity<?> postPayments(@RequestBody ArrayList<EmployeeRequest> employees) {
        return employeeService.addPayments(employees);
    }

    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT')")
    @PutMapping("/acct/payments")
    public ResponseEntity<?> putPayment(@RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updatePayment(employeeRequest);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ACCOUNTANT')")
    @GetMapping(value = "/empl/payment", params = "period")
    public ResponseEntity<?> getPaymentByPeriod(@RequestParam String period,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return employeeService.getPaymentByPeriod(period, userDetails);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ACCOUNTANT')")
    @GetMapping(value = "/empl/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails userDetails) {
        return employeeService.getPayment(userDetails);
    }

}
