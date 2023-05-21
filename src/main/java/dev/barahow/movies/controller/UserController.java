package dev.barahow.movies.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.barahow.movies.model.AppUser;
import dev.barahow.movies.model.UserRole;
import dev.barahow.movies.service.UserAuthenticationService;
import dev.barahow.movies.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CacheConfig
public class UserController {

    private final UserService userService;
   // private EnvironmentKey environmentKey;

    private final UserAuthenticationService userAuthenticationService;


    @GetMapping("/user")
    @Cacheable("getAllUsers")
    public ResponseEntity<Page<AppUser>> getAllUsers(@RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "10") @Max(10)
    int size) {


        Page<AppUser> users = userService.getUsers(PageRequest.of(page, size));

        return ResponseEntity.ok().body(users);
    }


    @DeleteMapping("/user/{email}")
    public ResponseEntity<AppUser> deleteAccount(@PathVariable("email") String email, @RequestHeader("Authorization") String authorizationHeader) {

        String loggedInUserEmail = userAuthenticationService.getLoggedInUserEmail(authorizationHeader);


        // retrieve the comment from the database

        AppUser user = userService.getUser(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getEmail().equals(loggedInUserEmail)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        // delete comment all together

        userService.deleteUser(user);


        return ResponseEntity.ok(user);



    }

    @PutMapping("/user/{email}")
    public ResponseEntity<AppUser> editAccount(@PathVariable("email") String email, @RequestBody AppUser updateUser, @RequestHeader("Authorization") String authorization) {
        // retrieve the loggged-in user email

        String loggedInUserEmail = userAuthenticationService.getLoggedInUserEmail(authorization);


        // retrieve the existing user from database

        AppUser user = userService.getUser(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }




        if (!user.getEmail().equals(loggedInUserEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // update the user from content
        user.setEmail(updateUser.getEmail());
        user.setUserName(updateUser.getUserName());
        user.setPassword(updateUser.getPassword());



        AppUser updatedUser = userService.saveUser(user);


        return ResponseEntity.ok(updatedUser);


    }






    @GetMapping("/user/{email}")
    public ResponseEntity<AppUser> getSingleUser(@PathVariable @NotBlank String email) {
        return new ResponseEntity<AppUser>(userService.getUser(email), HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<AppUser> createUser(@RequestBody @Valid AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user").toUriString());
        AppUser createdUser= userService.createUser(user);

        return ResponseEntity.created(uri).body(createdUser);

    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody @Valid AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/save").toUriString());

        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<UserRole> saveRole (@RequestBody  @Valid  UserRole role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/role/save").toUriString());

        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }


    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody @Valid RoleToUserForm form) {

        userService.addRoleToUser(form.getUserName(),form.getRoleName());
        return ResponseEntity.ok().build();
    }


    @GetMapping("token/refresh")
    public  void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String secretKey = System.getenv("MY_APP_SECRET_KEY");
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = verifier.verify(refresh_token);

                String username = decodedJWT.getSubject();


                AppUser appUser = userService.getUser(username);

                String access_token = JWT.create().withSubject(appUser.getEmail())
                        // add 15 min for access_token
                        .withExpiresAt(new Date(System.currentTimeMillis()+15 * 60 *1000))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles",appUser.getRoles().stream().map(UserRole::getName)
                                .collect(Collectors.toList()))
                        .sign(algorithm);


                Map<String, String > tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);





            }catch (JWTVerificationException ex) {

                log.info("lets get an exception already");
                log.info("Error logging in: {}", ex.getMessage());
                response.setHeader("error", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                //response.sendError(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }


        }else{
            throw  new RuntimeException("Refresh token is missing");
        }

    }

}

@Data
class  RoleToUserForm {
    private String userName;
    private String roleName;

}
