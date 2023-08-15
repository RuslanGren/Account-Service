package account.service;

import account.models.log.Log;
import account.repository.LogRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogService {

    private final LogRepository logRepository;

    private final UserRepository userRepository;

    @Autowired
    public LogService(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    public void accessDeniedLog(String email, String path) {
        Log log = new Log();

        log.setDate(new Date());
        log.setAction("ACCESS_DENIED");
        log.setSubject(email);
        log.setObject(path);
        log.setPath(path);

        logRepository.save(log);
    }

}
