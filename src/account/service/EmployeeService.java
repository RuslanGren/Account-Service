package account.service;

import account.exceptions.CustomBadRequestException;
import account.exceptions.UserNotFoundException;
import account.models.user.User;
import account.models.employee.Employee;
import account.models.employee.EmployeeRequest;
import account.repository.EmployeeRepository;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
            User user = checkEmployeeRequest(request);


        }

    }

    public User checkEmployeeRequest(EmployeeRequest employeeRequest) {
        String regexpPeriod = "^((0[1-9])|[1-9]|1[0-2])-(19|20)[0-9]{2}$";

        if (!employeeRequest.getPeriod().matches(regexpPeriod)) {
            throw new CustomBadRequestException("Wrong date!");
        }

        if (employeeRequest.getSalary() < 0) {
            throw new CustomBadRequestException("Salary must be non negative!");
        }

        return userRepository.findByEmail(employeeRequest.getEmployee()).orElseThrow(UserNotFoundException::new);
    }

}
