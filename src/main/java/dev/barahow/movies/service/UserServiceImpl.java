package dev.barahow.movies.service;

import dev.barahow.movies.model.AppUser;
import dev.barahow.movies.model.UserRole;
import dev.barahow.movies.repository.RoleRepository;
import dev.barahow.movies.repository.UserRepository;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private MongoTemplate mongoTemplate;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Sanitize input by checking if the input is null or empty
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }

        log.info("Looking up user with email: {}", email);
        AppUser appUser = userRepository.findByEmailIgnoreCase(email);

        if (appUser == null) {
            throw new UsernameNotFoundException("email not found in the database");
        }

        log.info("Found user: {}", appUser);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        appUser.getRoles().forEach(userRole -> {
            log.info("Adding role: {}", userRole.getName());
            authorities.add(new SimpleGrantedAuthority(userRole.getName()));
        });

        return new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), authorities);
    }


    @Override
    public AppUser saveUser(AppUser user) {
        // Sanitize input by checking if the input is null
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        log.info("Saving new user {} to the database", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setLocked(false);
        return userRepository.save(user);
    }

    @Override
    public UserRole saveRole(UserRole role) {
        // Sanitize input by checking if the input is null
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public AppUser createUser(AppUser user) {
        //private String name;
        //private String userName;
       // private  String password;
        hashPassword(user);
        validateUser(user);

        AppUser newUser  = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (newUser != null) {
            throw new IllegalStateException("The email already exists");
        }else {
            Optional<UserRole> userRole = Optional.ofNullable(roleRepository.findByName("USER"));

            if (userRole.isEmpty()) {
                throw new IllegalStateException("Default user Role not found");
            }


            user.setRoles(Collections.singletonList(userRole.get()));
            user.setLocked(false);
newUser= new AppUser(null,user.getUserName(),user.getEmail(),user.getPassword(),user.getRoles());





            log.info("Created a new user {}",newUser);


            return   userRepository.save(newUser);

        }



    }

    @Override
    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

    private void setDefaultUserRole(AppUser user) {
        UserRole defaultUserRole = roleRepository.findByName("USER");
        if (defaultUserRole== null) {
            defaultUserRole = new UserRole("USER");
            roleRepository.save(defaultUserRole);
        }else {

            user.setRoles(Collections.singletonList(defaultUserRole));

        }


    }


    private void hashPassword(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

    }

    private void validateUser(AppUser user) {
        //private String name;
        //private String userName;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        try {


        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AppUser>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }finally {
            factory.close();
        }
}




    @Override
    public void addRoleToUser(String email, String roleName) {
        // Sanitize input by checking if the input strings are null or empty
        if (email == null || email.isEmpty() || roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("Username and role name cannot be null or empty");
        }

        UserRole userRole= roleRepository.insert(new UserRole(roleName));
        mongoTemplate.update(AppUser.class).matching(Criteria.where("email").is(email))
                .apply(new Update().push("roles").value(roleName))
                .first();


        log.info("Adding new role {} to a user {}", roleName, email);
        AppUser user = userRepository.findByEmailIgnoreCase(email);
        user.getRoles().add(userRole);



    }

    @Override
    public AppUser getUser(String email) {
        // Sanitize input by checking if the input is null or empty
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }

        log.info("Fetching user {}", email);
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Page<AppUser> getUsers(Pageable pageable) {
        // Sanitize input by checking if the input is null
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        log.info("Fetching all users");
Page<AppUser> users = userRepository.findAll(pageable);



        return users;
    }

}