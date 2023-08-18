package account.service;

import account.models.log.Log;
import account.models.user.User;
import account.repository.LogRepository;
import account.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {

    private final LogRepository logRepository;

    private final UserRepository userRepository;

    private final HttpServletRequest request;

    @Autowired
    public LogService(LogRepository logRepository, UserRepository userRepository, HttpServletRequest request) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
        this.request = request;
    }

    public ResponseEntity<?> getEvents() {
        return new ResponseEntity<>(logRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    public void accessDeniedLog(String email, String path) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("ACCESS_DENIED");
        log.setSubject(email);
        log.setObject(path);
        log.setPath(path);

        logRepository.save(log);
    }

    public void failedAuthentication(String email) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("LOGIN_FAILED");
        log.setSubject(email);
        log.setObject(request.getRequestURI());
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void failedAuthenticationByUser(User user) {
        failedAuthentication(user.getEmail());
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        userRepository.save(user);
    }

    public void bruteForce(String email) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("BRUTE_FORCE");
        log.setSubject(email);
        log.setObject(request.getRequestURI());
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void lockUserBruteForceAttack(String email) {
        bruteForce(email);

        Log log = new Log();

        log.setDate(new Date());
        log.setAction("LOCK_USER");
        log.setSubject(email);
        log.setObject("Lock user " + email);
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void lockUserByAdmin(String adminEmail, User user) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("LOCK_USER");
        log.setSubject(adminEmail);
        log.setObject("Lock user " + user.getEmail());
        log.setPath(request.getRequestURI());

        logRepository.save(log);

        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unlockUserByAdmin(String adminEmail, User user) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("UNLOCK_USER");
        log.setSubject(adminEmail);
        log.setObject("Unlock user " + user.getEmail());
        log.setPath(request.getRequestURI());

        logRepository.save(log);

        user.setAccountNonLocked(true);
        userRepository.save(user);
    }
}
