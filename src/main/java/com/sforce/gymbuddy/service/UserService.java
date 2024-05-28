package com.sforce.gymbuddy.service;

import com.sforce.gymbuddy.dto.UserDTO;
import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for managing users.
 * Provides methods for user registration, authentication, and retrieval.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService.
     *
     * @param userRepository  the user repository
     * @param passwordEncoder the password encoder
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email of the user
     * @return an optional containing the user if found, or empty if not found
     */
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to get user by email with null or empty email");
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Saves a new user or updates an existing user.
     *
     * @param user the user to save
     * @return the saved user
     * @throws DataIntegrityViolationException if there is a database constraint
     *                                         violation
     */
    public User saveUser(User user) throws DataIntegrityViolationException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.debug("Saved User Password: {}", user.getPassword());
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username    the username of the user
     * @param rawPassword the raw password of the user
     * @return an optional containing the authenticated user if credentials are
     *         valid, or empty if invalid
     */
    public Optional<User> authenticateUser(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty() || rawPassword == null || rawPassword.trim().isEmpty()) {
            logger.warn("Attempted to authenticate with null or empty username/password");
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findByUsername(username.trim());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("Stored Password: {}", user.getPassword());
            logger.debug("Raw Password: {}", rawPassword);
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                logger.debug("Password Match: true");
                return Optional.of(user);
            } else {
                logger.debug("Password Match: false");
            }
        } else {
            logger.info("No user found with username: {}", username);
        }
        return Optional.empty();
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the user entity
     * @return the converted UserDTO
     */
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddressLine1(user.getAddressLine1());
        userDTO.setAddressLine2(user.getAddressLine2());
        userDTO.setCity(user.getCity());
        userDTO.setState(user.getState());
        userDTO.setZipCode(user.getZipCode());
        userDTO.setCountry(user.getCountry());
        return userDTO;
    }

    /**
     * Converts a User entity to a UserDetails object.
     *
     * @param user the user entity
     * @return the converted UserDetails
     */
    public UserDetails convertToUserDetails(User user) {
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles("USER"); // Customize roles as needed
        return builder.build();
    }
}
