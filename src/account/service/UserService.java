package account.service;


import account.exceptions.CustomBadRequestException;
import account.models.RegisterRequest;
import account.models.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;


@Service
public class UserService  {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
}