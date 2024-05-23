package com.sforce.gymbuddy.service;

import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to get user by email with null or empty email");
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public User saveUser(User user) {
        if (user == null) {
            logger.warn("Attempted to save a null user");
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.debug("Saved User Password: {}", user.getPassword());
        return userRepository.save(user);
    }

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
}
