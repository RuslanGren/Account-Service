package com.example.accountservice.controller;


import com.example.accountservice.models.admin.ChangeRoleRequest;
import com.example.accountservice.models.admin.UpdateUserAccessRequest;
import com.example.accountservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "/user/")
    public ResponseEntity<?> getUsers() {
        return adminService.getUsers();
    }

    @DeleteMapping(value = "/user/{userEmail}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String userEmail) {
        return adminService.deleteUser(userDetails, userEmail);
    }

    @DeleteMapping("/user/")
    public ResponseEntity<?> deleteUsers() {
        return adminService.deleteUsers();
    }

    @PutMapping("/user/role")
    public ResponseEntity<?> changeRole(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody ChangeRoleRequest changeRoleRequest) {
        return adminService.changeRole(userDetails, changeRoleRequest);
    }

    @PutMapping("/user/access")
    public ResponseEntity<?> lockUser(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestBody UpdateUserAccessRequest updateUserAccessRequest) {
        return adminService.updateUserAccess(userDetails, updateUserAccessRequest);
    }

}
