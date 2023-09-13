package com.example.accountservice.service;

import com.example.accountservice.exceptions.UserNotFoundException;
import com.example.accountservice.exceptions.CustomBadRequestException;
import com.example.accountservice.exceptions.RoleNotFoundException;
import com.example.accountservice.models.admin.ChangeRoleRequest;
import com.example.accountservice.models.admin.UpdateUserAccessRequest;
import com.example.accountservice.models.user.Role;
import com.example.accountservice.models.user.User;
import com.example.accountservice.models.user.UserResponse;
import com.example.accountservice.repository.RoleRepository;
import com.example.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LogService logService;

    @Autowired
    public AdminService(UserRepository userRepository, RoleRepository roleRepository, LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logService = logService;
    }

    public ResponseEntity<?> getUsers() {
        List<UserResponse> result = userRepository.findAllByOrderByIdAsc().stream()
                .map(User::returnUserResponse).toList();

        return new ResponseEntity<>((result), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(UserDetails userDetails, String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);

        if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))) {
            throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(user);
        logService.deleteUser(userDetails.getUsername(), user.getEmail());

        return new ResponseEntity<>(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUsers() {
        userRepository.deleteAll();
        return new ResponseEntity<>(Map.of("status", "Deleted successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> changeRole(UserDetails userDetails, ChangeRoleRequest changeRoleRequest) {

        User user = userRepository.findByEmailIgnoreCase(changeRoleRequest.getUser()).orElseThrow(UserNotFoundException::new);

        Role role = roleRepository.findByName("ROLE_" + changeRoleRequest.getRole());
        if (role == null) {
            throw new RoleNotFoundException();
        }

        Set<Role> updatedRoles = new HashSet<>(user.getRoles());

        if (changeRoleRequest.getOperation().equals("REMOVE")) {
            if (!user.getRoles().contains(role)) {
                throw new CustomBadRequestException("The user does not have a role!");
            }
            if (role.getName().equals("ROLE_ADMINISTRATOR")) {
                throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
            }
            if (user.getRoles().size() <= 1) {
                throw new CustomBadRequestException("The user must have at least one role!");
            }
            updatedRoles.remove(role); // remove role
            logService.removeRole(userDetails.getUsername(), user.getEmail(), changeRoleRequest.getRole());
        } else if (changeRoleRequest.getOperation().equals("GRANT")) {
            if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))
                    || role.getName().equals("ROLE_ADMINISTRATOR")) {
                throw new CustomBadRequestException("The user cannot combine administrative and business roles!");
            }
            updatedRoles.add(role); // add new role
            logService.grantRole(userDetails.getUsername(), user.getEmail(), changeRoleRequest.getRole());
        } else {
            throw new CustomBadRequestException("Wrong operation!");
        }
        user.setRoles(updatedRoles);
        userRepository.save(user);

        return new ResponseEntity<>(user.returnUserResponse(), HttpStatus.OK);
    }

    public ResponseEntity<?> updateUserAccess(UserDetails userDetails, UpdateUserAccessRequest request) {
        String adminEmail = userDetails.getUsername();
        User user = userRepository.findByEmailIgnoreCase(request.getUser()).orElseThrow(UserNotFoundException::new);

        if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))) {
            throw new CustomBadRequestException("Can't lock the ADMINISTRATOR!");
        }

        if (request.getOperation().equals("LOCK")) {
            logService.lockUserByAdmin(adminEmail, user);
        } else if (request.getOperation().equals("UNLOCK")) {
            logService.unlockUserByAdmin(adminEmail, user);
        } else {
            throw new CustomBadRequestException("Unknown operation!");
        }

        return new ResponseEntity<>(Map.of(
                "status", String.format("User %s %sed!", request.getUser().toLowerCase(), request.getOperation().toLowerCase()))
                , HttpStatus.OK);
    }

}