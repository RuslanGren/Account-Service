package com.example.accountservice.service;

import com.example.accountservice.exceptions.UserNotFoundException;
import com.example.accountservice.exceptions.CustomBadRequestException;
import com.example.accountservice.models.user.ChangePassRequest;
import com.example.accountservice.models.user.RegisterRequest;
import com.example.accountservice.models.user.Role;
import com.example.accountservice.models.user.User;
import com.example.accountservice.repository.RoleRepository;
import com.example.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService  {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    private void checkPass(String pass) {
        if (pass.length() < 12) {
            throw new CustomBadRequestException("Password length must be 12 chars minimum!");
        }

        Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary",
                "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune",
                "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober",
                "PasswordForNovember", "PasswordForDecember");

        if (breachedPasswords.contains(pass)) {
            throw new CustomBadRequestException("The password is in the hacker's database!");
        }
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        Set<Role> roles = new HashSet<>();

        if (userRepository.findAll().isEmpty()) {
            roles.add(roleRepository.findByName("ROLE_ADMINISTRATOR"));
        } else {
            roles.add(roleRepository.findByName("ROLE_USER"));
        }

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new CustomBadRequestException("User exist!");
        }

        checkPass(request.getPassword()); // check valid of pass if false throw exception

        User user = new User();
        user.setName(request.getName());
        user.setLastname(request.getLastname());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setRoles(roles);

        userRepository.save(user);
        logService.createUser(email);

        return new ResponseEntity<>(user.returnUserResponse(), HttpStatus.OK);
    }

    public ResponseEntity<?> changePass(UserDetails userDetails, ChangePassRequest request) {
        checkPass(request.getPassword());

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomBadRequestException("The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        logService.changePassword(user.getEmail());

        return new ResponseEntity<>(Map.of("email", userDetails.getUsername(),
                "status", "The password has been updated successfully"), HttpStatus.OK);
    }

    public void resetFailedAttempts(UserDetails userDetails) {
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
}