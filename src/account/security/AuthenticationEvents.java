package account.security;

import account.exceptions.UserIsLockedException;
import account.models.user.User;
import account.repository.RoleRepository;
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

    private final RoleRepository roleRepository;

    @Autowired
    public AuthenticationEvents(UserService userService, UserRepository userRepository,
                                LogService logService, RoleRepository roleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.logService = logService;
        this.roleRepository = roleRepository;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        UserDetails details = (UserDetails) success.getAuthentication().getPrincipal();

        userService.resetFailedAttempts(details);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        String email = (String) failures.getAuthentication().getPrincipal();

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isAccountNonLocked()) {
                return;
            }
            logService.failedAuthenticationByUser(user);
            if (user.getFailedAttempt() >= MAX_FAILED_ATTEMPTS
                    && !user.getRoles().contains(roleRepository.findByName("ROLE_ADMINISTRATOR"))) {
                logService.lockUserBruteForceAttack(user);
            }
        } else {
            logService.failedAuthentication(email);
        }
    }

}
