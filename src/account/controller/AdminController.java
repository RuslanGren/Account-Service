package account.controller;

import account.models.admin.ChangeRoleRequest;
import account.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/user")
    public ResponseEntity<?> getUsers() {
        return adminService.getUsers();
    }

    @DeleteMapping(value = "/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return adminService.deleteUser(email);
    }

    @PutMapping("/user/role")
    public ResponseEntity<?> changeRole(@RequestBody ChangeRoleRequest changeRoleRequest) {
        return adminService.changeRole(changeRoleRequest);
    }

}
