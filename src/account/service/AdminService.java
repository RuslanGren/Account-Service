package account.service;

import account.exceptions.CustomBadRequestException;
import account.exceptions.RoleNotFoundException;
import account.exceptions.UserNotFoundException;
import account.models.admin.ChangeRoleRequest;
import account.models.admin.UpdateUserAccessRequest;
import account.models.user.Role;
import account.models.user.User;
import account.models.user.UserResponse;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public ResponseEntity<?> deleteUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);

        if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))) {
            throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(user);

        return new ResponseEntity<>(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUsers() {
        userRepository.deleteAll();
        return new ResponseEntity<>(Map.of("status", "Deleted successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> changeRole(ChangeRoleRequest changeRoleRequest) {

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
        } else if (changeRoleRequest.getOperation().equals("GRANT")) {
            if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))
                    || role.getName().equals("ROLE_ADMINISTRATOR")) {
                throw new CustomBadRequestException("The user cannot combine administrative and business roles!");
            }
            updatedRoles.add(role); // add new role
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
                "status", String.format("User %s %s!", request.getUser(), request.getOperation()))
                , HttpStatus.OK);
    }

}