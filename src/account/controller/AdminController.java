package account.controller;

import account.models.admin.ChangeRoleRequest;
import account.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    @GetMapping("/user")
    public ResponseEntity<?> getUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return adminService.getUsers();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        return adminService.deleteUser(email);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR')")
    @PutMapping("/user/role")
    public ResponseEntity<?> changeRole(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody ChangeRoleRequest changeRoleRequest) {
        return adminService.changeRole(changeRoleRequest);
    }

}
