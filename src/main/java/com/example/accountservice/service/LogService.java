package com.example.accountservice.service;


import com.example.accountservice.models.log.Log;
import com.example.accountservice.models.user.User;
import com.example.accountservice.repository.LogRepository;
import com.example.accountservice.repository.UserRepository;
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

    public void createUser(String email) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("CREATE_USER");
        log.setSubject("Anonymous");
        log.setObject(email);
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void changePassword(String email) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("CHANGE_PASSWORD");
        log.setSubject(email);
        log.setObject(email);
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void grantRole(String adminEmail, String email, String role) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("GRANT_ROLE");
        log.setSubject(adminEmail);
        log.setObject(String.format("Grant role %s to %s", role, email));
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void removeRole(String adminEmail, String email, String role) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("REMOVE_ROLE");
        log.setSubject(adminEmail);
        log.setObject(String.format("Remove role %s from %s", role, email));
        log.setPath(request.getRequestURI());

        logRepository.save(log);
    }

    public void deleteUser(String adminEmail, String email) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("DELETE_USER");
        log.setSubject(adminEmail);
        log.setObject(email);
        log.setPath(request.getRequestURI());

        logRepository.save(log);
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

    public void lockUserBruteForceAttack(User user) {
        String email = user.getEmail();
        bruteForce(email);

        Log log = new Log();

        log.setDate(new Date());
        log.setAction("LOCK_USER");
        log.setSubject(email);
        log.setObject("Lock user " + email);
        log.setPath(request.getRequestURI());

        logRepository.save(log);

        user.setAccountNonLocked(false);
        userRepository.save(user);
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
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

}
