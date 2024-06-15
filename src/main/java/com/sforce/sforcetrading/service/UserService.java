package com.sforce.sforcetrading.service;

import com.sforce.sforcetrading.dto.UserDTO;
import com.sforce.sforcetrading.model.User;
import com.sforce.sforcetrading.repository.UserRepository;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * Returns the user for the specified identifier
     * 
     * @param id
     * @return the user
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
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
     * Retrieves a user by username.
     *
     * @param username the username of the user
     * @return an optional containing the user if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
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
     * Updates a user record in the database
     * 
     * @param user the user to update
     * @return the updated user object
     * @throws DataIntegrityViolationException if there is a database constraint
     *                                         violation
     */
    public User updateUserDetails(User existingUser, UserDTO userDTO) {
        if (userDTO.getFirstName() != null) {
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            existingUser.setLastName(userDTO.getLastName());
        }
        if (userDTO.getEmail() != null) {
            Optional<User> existingUserByEmail = userRepository.findByEmail(userDTO.getEmail());
            if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(existingUser.getId())) {
                throw new DataIntegrityViolationException("A user with the same email already exists.");
            }
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUsername() != null) {
            Optional<User> existingUserByUsername = userRepository.findByUsername(userDTO.getUsername());
            if (existingUserByUsername.isPresent()
                    && !existingUserByUsername.get().getId().equals(existingUser.getId())) {
                throw new DataIntegrityViolationException("A user with the same username already exists.");
            }
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPhone() != null) {
            existingUser.setPhone(userDTO.getPhone());
        }
        if (userDTO.getAddressLine1() != null) {
            existingUser.setAddressLine1(userDTO.getAddressLine1());
        }
        if (userDTO.getAddressLine2() != null) {
            existingUser.setAddressLine2(userDTO.getAddressLine2());
        }
        if (userDTO.getCity() != null) {
            existingUser.setCity(userDTO.getCity());
        }
        if (userDTO.getState() != null) {
            existingUser.setState(userDTO.getState());
        }
        if (userDTO.getZipCode() != null) {
            existingUser.setZipCode(userDTO.getZipCode());
        }
        if (userDTO.getCountry() != null) {
            existingUser.setCountry(userDTO.getCountry());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Updates the user password
     * 
     * @param userId          the user identifier to update
     * @param currentPassword the current password
     * @param newPassword     the new password
     */
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @throws IllegalArgumentException if the user is not found
     */
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            logger.error("Error deleting user: User with ID {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }
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
