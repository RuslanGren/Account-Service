package account.service;

import account.exceptions.CustomBadRequestException;
import account.exceptions.UserNotFoundException;
import account.models.user.ChangePassRequest;
import account.models.user.RegisterRequest;
import account.models.user.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Service
public class UserService  {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomBadRequestException("User exist!");
        }

        checkPass(request.getPassword()); // check valid of pass if false throw exception

        User user = new User();
        user.setName(request.getName());
        user.setLastname(request.getLastname());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new ArrayList<>(Set.of(roleRepository.findByName("USER").orElseThrow())));

        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> payment(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> changePass(UserDetails userDetails, ChangePassRequest request) {
        checkPass(request.getPassword());

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomBadRequestException("The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>(Map.of("email", userDetails.getUsername(),
                "status", "The password has been updated successfully"), HttpStatus.OK);
    }
}