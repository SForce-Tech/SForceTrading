package com.sforce.gymbuddy.service;

import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.withUsername;

/**
 * Service for loading user-specific data.
 * This service is used by Spring Security to retrieve user details during
 * authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new CustomUserDetailsService.
     *
     * @param userRepository the user repository
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by username.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated UserDetails object (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or the user
     *                                   has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserBuilder builder = withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles("USER"); // Customize roles as needed

        return builder.build();
    }
}
