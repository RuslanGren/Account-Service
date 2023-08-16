package account.service;

import account.exceptions.CustomBadRequestException;
import account.exceptions.UserNotFoundException;
import account.models.admin.ChangeRoleRequest;
import account.models.user.Role;
import account.models.user.User;
import account.models.user.UserResponse;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<?> getUsers() {
        List<UserResponse> result = userRepository.findAllByOrderByIdAsc().stream()
                .map(User::returnUserResponse).toList();

        return new ResponseEntity<>((result), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        if (user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))) {
            throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
        }

        userRepository.delete(user);

        return new ResponseEntity<>(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
    }

    public ResponseEntity<?> changeRole(ChangeRoleRequest changeRoleRequest) {
        User user = userRepository.findByEmail(changeRoleRequest.getUser()).orElseThrow(UserNotFoundException::new);

        Role role = roleRepository.findByName(changeRoleRequest.getRole());
        if (role == null) {
            throw new UserNotFoundException();
        }

        Set<Role> updatedRoles = new HashSet<>(user.getRoles());

        if (changeRoleRequest.getOperation().equals("REMOVE")) {
            if (role.getName().equals("ROLE_ADMINISTRATOR")) {
                throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
            }

            if (!user.getRoles().contains(role)) {
                throw new CustomBadRequestException("The user does not have a role!");
            }

            if (user.getRoles().size() <= 1) {
                throw new CustomBadRequestException("The user must have at least one role!");
            }

            updatedRoles.remove(role); // remove role

        } else if (changeRoleRequest.getOperation().equals("GRANT")) {
            if (role.getName().equals("ROLE_ADMINISTRATOR")) {
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

}