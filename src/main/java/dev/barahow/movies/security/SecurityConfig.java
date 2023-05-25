package dev.barahow.movies.security;

import dev.barahow.movies.config.PasswordEncoderConfig;
import dev.barahow.movies.filter.CustomAuthenticationFilter;
import dev.barahow.movies.filter.CustomAuthorizationFilter;
import dev.barahow.movies.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;


    private UserAuthenticationService userAuthenticationService;

    private PasswordEncoderConfig passwordEncoderConfig;

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.DEBUG;
    }

    private final AuthenticationConfiguration authenticationConfiguration;


    @Autowired
    public void setUserAuthenticationService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AbstractAuthenticationProcessingFilter filter = new CustomAuthenticationFilter(authenticationManager(), userAuthenticationService);
       // filter.setAuthenticationFailureHandler(new CustomAuthenticationFailedHandler());
        filter.setFilterProcessesUrl("/api/v1/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().requestMatchers("/api/v1/login/**", "/api/token/refresh/**").permitAll();
        http.authorizeHttpRequests().requestMatchers(GET, "/api/v1/movies/**", "/api/v1/reviews/**", "/api/v1/recommendation/**").hasAnyAuthority("USER");
        http.authorizeHttpRequests().requestMatchers(GET, "/api/v1/movies/**", "/api/v1/reviews", "/api/v1/user", "/api/v1/recommendation/**").hasAnyAuthority("ADMIN");
        http.authorizeHttpRequests().requestMatchers(DELETE, "/api/v1/reviews/comment/**").hasAuthority("USER");
        http.authorizeHttpRequests().requestMatchers(POST, "api/v1/movies/**", "/api/v1/reviews/**", "api/v1/user/**").hasAuthority("ADMIN");
        http.authorizeHttpRequests().requestMatchers(PUT, "/api/v1/reviews/comment/**").hasAuthority("USER");
        http.authorizeHttpRequests().requestMatchers(PUT, "api/v1/movies/**", "/api/v1/reviews/**", "api/v1/user/**").hasAuthority("ADMIN");
        http.authorizeHttpRequests().requestMatchers(DELETE, "api/v1/movies/**", "/api/v1/reviews/**", "/api/v1/user/**", "/api/v1/reviews/comment/**").hasAuthority("SUPER_ADMIN");
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.addFilter(filter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderConfig.passwordEncoder());
    }
}


