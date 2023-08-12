package account.service;

import account.models.employee.Employee;
import account.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public ResponseEntity<?> addPayments(ArrayList<Employee> employees) {
        String availableDate="^(0[1-9]|1[012])-((19|2[0-9])[0-9]{2})$";

        for (Employee employee : employees) {
            if
        }

    }
}
