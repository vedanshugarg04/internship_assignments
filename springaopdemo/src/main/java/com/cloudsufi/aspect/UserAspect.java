package com.cloudsufi.aspect;

import com.cloudsufi.model.UserDocument;
import com.cloudsufi.model.User;
import com.cloudsufi.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAspect {

    private final UserMongoRepository userMongoRepository;

    /**
     * Pointcut: Intercepts the createUser method in UserService.
     */
    @AfterReturning(
            pointcut = "execution(* com.cloudsufi.service.UserService.createUser(..))",
            returning = "savedMysqlUser"
    )
    public void syncToMongoDb(User savedMysqlUser) {
        try {
            log.info("AOP Triggered: Syncing User [{}] to MongoDB...", savedMysqlUser.getUsername());

            UserDocument mongoUser = UserDocument.builder()
                    .mysqlId(savedMysqlUser.getId())
                    .username(savedMysqlUser.getUsername())
                    .email(savedMysqlUser.getEmail())
                    .build();

            userMongoRepository.save(mongoUser);

            log.info("Successfully synced to MongoDB!");
        } catch (Exception e) {
            log.error("Failed to sync to MongoDB. MySQL transaction is untouched. Error: {}", e.getMessage());
        }
    }
}
