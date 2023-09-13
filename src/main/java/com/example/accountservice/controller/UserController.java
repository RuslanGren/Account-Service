package com.example.accountservice.controller;


import com.example.accountservice.models.user.ChangePassRequest;
import com.example.accountservice.models.user.RegisterRequest;
import com.example.accountservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/auth/changepass")
    public ResponseEntity<?> changePass(@RequestBody ChangePassRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return userService.changePass(userDetails, request);
    }

}
