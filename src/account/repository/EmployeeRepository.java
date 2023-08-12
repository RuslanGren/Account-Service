package account.repository;

import account.models.employee.Employee;
import account.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeIdAndPeriod(User employee, String period);
}