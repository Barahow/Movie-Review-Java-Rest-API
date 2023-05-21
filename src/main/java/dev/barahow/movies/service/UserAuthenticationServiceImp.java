package dev.barahow.movies.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.movies.config.PasswordEncoderConfig;
import dev.barahow.movies.model.AppUser;
import dev.barahow.movies.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class UserAuthenticationServiceImp implements UserAuthenticationService {

    public UserService userService;
    public UserRepository userRepository;

    public PasswordEncoderConfig passwordEncoderConfig;
    private final long MAX_FAILED_ATTEMPTS = 5;


    private final long LOCK_TIMEOUT = 10; // 10 min


    @Cacheable("getLoggedInUserEmail")
    @Override
    public String getLoggedInUserEmail(String authorizationToken) {
        String secretKey = System.getenv("MY_APP_SECRET_KEY");
        String token = authorizationToken.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        String email = decodedJWT.getSubject();


        AppUser appUser = userService.getUser(email);

        return appUser.getEmail();


    }

    @Override
    public AppUser getUserByEmail(String email) {
        return userService.getUser(email);
    }

    @Override
    public AppUser getUserByIsLocked(boolean locked) {

        return null;
    }

    @Override
    public void incrementFailedLoginAttempts(String username, String password) {
    /*        int failedAttempts = 0;


            try {
                while (failedAttempts < MAX_FAILED_ATTEMPTS) {
                    AppUser user = userRepository.findByEmailAndPassword(username, password);

                    if (user.isLocked()) {
                        // check if the unlock timeout has expired
                        if (user.getUnLockTime() != null && user.getUnLockTime().isAfter(LocalDateTime.now())) {
                            throw new LockedException("Your account is temporarily locked. Please try again later.");
                        }

                        user.setLocked(false);
                        user.setUnLockTime(null);
                        //userRepository.save(user);
                    }

                    // check if password matches
                    //if it doesnt macth then we increment the failed attempts
                    if (!passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())) {
                        // if password does not match, increment the failed login attempts
                        user.incrementFailedLoginAttempts();
                        failedAttempts++;

                        // check if the user account should be locked
                        log.info("number of failed attempts: " + user.getFailedLoginAttempts());


                        // Successful authentication
                        return;
                    }
                }
            } catch (NullPointerException ex) {
                failedAttempts++;
                // handle null user or other exceptions
                break;
            }

            // handle maximum failed attempts
            if (failedAttempts == MAX_FAILED_ATTEMPTS) {
                AppUser user = userRepository.findByEmailIgnoreCase(username);
                user.setLocked(true);
                user.setUnLockTime(LocalDateTime.now().plusMinutes(LOCK_TIMEOUT));
                //userRepository.save(user);
                throw new LockedException("User account is locked");
            }
        }*/

    }


}


