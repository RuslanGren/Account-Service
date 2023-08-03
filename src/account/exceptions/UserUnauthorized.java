package account.exceptions;

public class UserUnauthorized extends RuntimeException {
    public UserUnauthorized(String message) {
        super(message);
    }
}
