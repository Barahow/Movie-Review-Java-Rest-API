package dev.barahow.movies.service;

import dev.barahow.movies.model.AppUser;
import dev.barahow.movies.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    AppUser saveUser(AppUser user);

    UserRole saveRole(UserRole role);

    void addRoleToUser(String email, String roleName);

    AppUser getUser(String email);
    Page<AppUser> getUsers(Pageable pageable);

    AppUser createUser(AppUser user);

    void deleteUser(AppUser user);
}
