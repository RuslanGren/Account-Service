package com.example.accountservice.service;


import com.example.accountservice.exceptions.CustomBadRequestException;
import com.example.accountservice.models.employee.Employee;
import com.example.accountservice.models.employee.EmployeeRequest;
import com.example.accountservice.models.employee.EmployeeResponse;
import com.example.accountservice.models.user.User;
import com.example.accountservice.repository.EmployeeRepository;
import com.example.accountservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<?> addPayments(ArrayList<EmployeeRequest> requests) {

        for (EmployeeRequest request : requests) {
            checkEmployeeRequest(request); // throw exception if something is wrong

            User user = userRepository.findByEmailIgnoreCase(request.getEmployee())
                    .orElseThrow(() -> new CustomBadRequestException("User not found!"));

            if (employeeRepository.findByEmployeeAndPeriod(user, request.getPeriod()).isPresent()) {
                throw new CustomBadRequestException("Payment data exist!");
            }

            Employee employee = new Employee();
            employee.setEmployee(user);
            employee.setPeriod(request.getPeriod());
            employee.setSalary(request.getSalary());

            employeeRepository.save(employee);
        }

        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> updatePayment(EmployeeRequest request) {
        checkEmployeeRequest(request); // throw exception if something is wrong

        User user = userRepository.findByEmailIgnoreCase(request.getEmployee())
                .orElseThrow(() -> new CustomBadRequestException("User not found!"));

        Employee employee = employeeRepository.findByEmployeeAndPeriod(user, request.getPeriod())
                .orElseThrow(() -> new CustomBadRequestException("Employee not found!"));

        employee.setSalary(request.getSalary());
        employeeRepository.save(employee);

        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

    private void checkEmployeeRequest(EmployeeRequest employeeRequest) {
        String regexpPeriod = "^((0[1-9])|[1-9]|1[0-2])-(19|20)[0-9]{2}$";

        if (!employeeRequest.getPeriod().matches(regexpPeriod)) {
            throw new CustomBadRequestException("Wrong date!");
        }

        if (employeeRequest.getSalary() < 0) {
            throw new CustomBadRequestException("Salary must be non negative!");
        }

    }

    public ResponseEntity<?> getPaymentByPeriod(String period, UserDetails userDetails) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new CustomBadRequestException("User not found!"));

        Employee employee = employeeRepository.findByEmployeeAndPeriod(user, period)
                .orElseThrow(() -> new CustomBadRequestException("Employee not found!"));

        return new ResponseEntity<>(new EmployeeResponse(user.getName(), user.getLastname(),
                employee.getPeriod(), employee.getSalary()), HttpStatus.OK);
    }

    public ResponseEntity<?> getPayment(UserDetails userDetails) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new CustomBadRequestException("User not found!"));

        List<Employee> employees = employeeRepository.findByEmployeeOrderByIdDesc(user);

        List<EmployeeResponse> result = employees.stream()
                .map(employee -> new EmployeeResponse(user.getName(), user.getLastname(),
                        employee.getPeriod(), employee.getSalary()))
                .toList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
