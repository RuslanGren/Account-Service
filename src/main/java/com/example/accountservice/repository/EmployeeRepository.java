package com.example.accountservice.repository;


import com.example.accountservice.models.employee.Employee;
import com.example.accountservice.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeAndPeriod(User employee, String period);

    ArrayList<Employee> findByEmployeeOrderByIdDesc (User employee);
}
