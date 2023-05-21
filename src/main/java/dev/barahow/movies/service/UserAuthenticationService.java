package dev.barahow.movies.service;

import dev.barahow.movies.model.AppUser;


public interface UserAuthenticationService {

    String getLoggedInUserEmail(String authorizationToken);
    AppUser getUserByEmail(String Email);

    AppUser getUserByIsLocked(boolean locked);

    void incrementFailedLoginAttempts(String username, String password);
}