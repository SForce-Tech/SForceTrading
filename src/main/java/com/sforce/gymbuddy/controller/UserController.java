package com.sforce.gymbuddy.controller;

import com.sforce.gymbuddy.dto.UserDTO;
import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a new UserController with the specified UserService.
     *
     * @param userService the user service to use for user operations
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a ResponseEntity containing the list of users or a not found status
     *         if no users are found
     */
    @GetMapping("/listAll")
    public ResponseEntity<Object> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        }
        List<UserDTO> userDTOs = users.stream()
                .map(userService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return a ResponseEntity containing the user or a not found status if the
     *         user is not found
     */
    @GetMapping("/find")
    public ResponseEntity<Object> getUserByEmail(@RequestParam String email) {
        email = email.trim().toLowerCase();
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userService.convertToDTO(userOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + email);
        }
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @return a ResponseEntity containing the registered user or an error status if
     *         registration fails
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password must not be null");
        }
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToDTO(savedUser));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with the same email or username already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred while saving the user");
        }
    }

    /**
     * Authenticates a user.
     *
     * @param loginRequest the user login request containing the username and
     *                     password
     * @return a ResponseEntity containing a success message or an unauthorized
     *         status if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password must not be null");
        }
        Optional<User> userOptional = userService.authenticateUser(loginRequest.getUsername(),
                loginRequest.getPassword());
        if (userOptional.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
