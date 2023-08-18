package account.security;

import account.models.user.User;
import account.repository.UserRepository;
import account.service.LogService;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationEvents {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserService userService;

    private final UserRepository userRepository;

    private final LogService logService;

    @Autowired
    public AuthenticationEvents(UserService userService, UserRepository userRepository, LogService logService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.logService = logService;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        UserDetails userDetails = (UserDetails) success.getAuthentication().getPrincipal();
        userService.resetFailedAttempts(userDetails);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        UserDetails userDetails = (UserDetails) failures.getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        userOptional.ifPresent(user -> {
            if (!user.isAccountNonLocked()) {
                return;
            }
            logService.failedAuthenticationByUser(user);
            if (user.getFailedAttempt() >= AuthenticationEvents.MAX_FAILED_ATTEMPTS) {
                logService.lockUserBruteForceAttack(email);
            }
        });

        if (userOptional.isEmpty()) {
            logService.failedAuthentication(email);
        }
    }

}
