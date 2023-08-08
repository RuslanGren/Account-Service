package account.service;

import account.exceptions.CustomBadRequestException;
import account.exceptions.UserNotFoundException;
import account.models.RegisterRequest;
import account.models.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary",
                "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune",
                "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober",
                "PasswordForNovember", "PasswordForDecember");

        if (pass.length() < 12) {
            throw new CustomBadRequestException("The password length must be at least 12 chars!");
        }

        if (breachedPasswords.contains(pass)) {
            throw new CustomBadRequestException("The password is in the hacker's database!");
        }
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomBadRequestException("User exist!");
        }

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

    public ResponseEntity<?> changePass(UserDetails userDetails, String new_password) {
        checkPass(new_password);

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
        new_password = passwordEncoder.encode(new_password);

        if (!passwordEncoder.matches(new_password, user.getPassword())) {
            throw new CustomBadRequestException("The passwords must be different!");
        }


    }
}