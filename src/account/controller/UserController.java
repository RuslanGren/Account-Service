package account.controller;

import account.models.ChangePassRequest;
import account.models.RegisterRequest;
import account.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping("/empl/payment")
    public ResponseEntity<?> payment(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.payment(userDetails);
    }

    @PostMapping("/auth/changepass")
    public ResponseEntity<?> changePass(@RequestBody ChangePassRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return userService.changePass(userDetails, request);
    }

}
