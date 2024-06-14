package com.sforce.gymbuddy.controller;

import com.sforce.gymbuddy.dto.PasswordUpdateDTO;
import com.sforce.gymbuddy.dto.UserCreateDTO;
import com.sforce.gymbuddy.dto.UserDTO;
import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.service.UserService;
import com.sforce.gymbuddy.util.JwtUtil;
import com.sforce.gymbuddy.util.RSAUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.regex.Pattern;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Base64;

import java.security.PrivateKey;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PrivateKey privateKey;

    /**
     * Constructs a new UserController with the specified UserService, JwtUtil, and
     * PrivateKey.
     *
     * @param userService the user service to use for user operations
     * @param jwtUtil     the JWT utility to use for generating tokens
     * @param privateKey  the RSA private key for decrypting passwords
     */
    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, PrivateKey privateKey) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.privateKey = privateKey;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

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
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email should be valid");
        }
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
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return a ResponseEntity containing the user or a not found status if the
     *         user is not found
     */
    @GetMapping("/getUser")
    public ResponseEntity<Object> getUserByUsername(@RequestParam String username) {
        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userService.convertToDTO(userOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with username: " + username);
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
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null || userCreateDTO.getEmail() == null || userCreateDTO.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password must not be null");
        }
        try {
            User user = new User(); // Create a User entity from UserCreateDTO
            // Map fields from UserCreateDTO to User entity
            user.setFirstName(userCreateDTO.getFirstName());
            user.setLastName(userCreateDTO.getLastName());
            user.setEmail(userCreateDTO.getEmail());
            user.setUsername(userCreateDTO.getUsername());
            user.setPassword(userCreateDTO.getPassword());
            user.setPhone(userCreateDTO.getPhone());
            user.setAddressLine1(userCreateDTO.getAddressLine1());
            user.setAddressLine2(userCreateDTO.getAddressLine2());
            user.setCity(userCreateDTO.getCity());
            user.setState(userCreateDTO.getState());
            user.setZipCode(userCreateDTO.getZipCode());
            user.setCountry(userCreateDTO.getCountry());

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
     * @return a ResponseEntity containing a JWT token or an unauthorized status if
     *         authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password must not be null");
        }
        try {
            // Decrypt the password
            String decryptedPassword = RSAUtil.decrypt(Base64.getDecoder().decode(loginRequest.getPassword()),
                    privateKey);
            loginRequest.setPassword(decryptedPassword);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password encryption");
        }

        Optional<User> userOptional = userService.authenticateUser(loginRequest.getUsername(),
                loginRequest.getPassword());
        if (userOptional.isPresent()) {
            // Convert User to UserDetails
            UserDetails userDetails = userService.convertToUserDetails(userOptional.get());
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    /**
     * Updates an existing user.
     *
     * @param user the user details to update
     * @return a ResponseEntity containing the updated user or an error status if
     *         update fails
     */
    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID must not be null");
        }
        try {
            // Fetch the existing user from the database
            User existingUser = userService.getUserById(userDTO.getId());
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userDTO.getId());
            }

            // Update the user details
            User updatedUser = userService.updateUserDetails(existingUser, userDTO);
            return ResponseEntity.ok(userService.convertToDTO(updatedUser));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with the same email or username already exists.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An error occurred while updating the user: " + e.getMessage());
        }
    }

    /**
     * Updates the user password
     * 
     * @param userId            the user identifier to update
     * @param passwordUpdateDTO the DTO object that holds the current password and
     *                          new password
     * @return the response entity
     */
    @PutMapping("/updatePassword/{userId}")
    public ResponseEntity<Object> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        try {
            userService.updatePassword(userId, passwordUpdateDTO.getCurrentPassword(),
                    passwordUpdateDTO.getNewPassword());
            return ResponseEntity.ok("Password updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the password: " + e.getMessage());
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @return a ResponseEntity with a status of OK or NOT FOUND
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }
    }
}
