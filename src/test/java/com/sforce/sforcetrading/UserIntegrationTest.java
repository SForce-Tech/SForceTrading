package com.sforce.sforcetrading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.sforcetrading.dto.PasswordUpdateDTO;
import com.sforce.sforcetrading.dto.UserCreateDTO;
import com.sforce.sforcetrading.dto.UserDTO;
import com.sforce.sforcetrading.model.User;
import com.sforce.sforcetrading.repository.UserRepository;
import com.sforce.sforcetrading.util.RSAUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private ObjectMapper objectMapper;

        private String jwtToken;

        @BeforeEach
        public void setup() throws Exception {
                userRepository.deleteAll();

                User user = new User();
                user.setUsername("testuser");
                user.setPassword(passwordEncoder.encode("testpassword"));
                user.setEmail("testuser@example.com");
                user.setFirstName("Test");
                user.setLastName("User");

                userRepository.save(user);

                // Get JWT token for authentication
                this.jwtToken = obtainJwtToken("testuser", "testpassword");
        }

        private String obtainJwtToken(String username, String password) throws Exception {
                // Fetch public key from the API
                String publicKeyPem = mockMvc.perform(get("/api/public-key"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString(StandardCharsets.UTF_8);

                // Clean up the public key PEM
                String cleanPublicKeyPem = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                                .replace("-----END PUBLIC KEY-----", "")
                                .replaceAll("\\s+", "");

                PublicKey publicKey = RSAUtil.getPublicKey(cleanPublicKeyPem);

                // Encrypt the password
                String encryptedPassword = Base64.getEncoder().encodeToString(
                                RSAUtil.encrypt(password, publicKey));

                MvcResult result = mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"" + username + "\", \"password\": \"" + encryptedPassword
                                                + "\"}"))
                                .andExpect(status().isOk())
                                .andReturn();

                return result.getResponse().getContentAsString();
        }

        @Test
        public void testRegisterUser() throws Exception {
                UserCreateDTO newUser = new UserCreateDTO();
                newUser.setUsername("newuser");
                newUser.setPassword("newpassword");
                newUser.setEmail("newuser@example.com");
                newUser.setFirstName("New");
                newUser.setLastName("User");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.username", is("newuser")))
                                .andExpect(jsonPath("$.email", is("newuser@example.com")));

                // Test duplicate email
                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andExpect(status().isConflict());

                // Test missing fields
                newUser.setEmail("");
                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.email").value("Email is mandatory"));
        }

        @Test
        public void testLoginUser() throws Exception {
                // Fetch public key from the API
                String publicKeyPem = mockMvc.perform(get("/api/public-key"))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString(StandardCharsets.UTF_8);

                // Clean up the public key PEM
                String cleanPublicKeyPem = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                                .replace("-----END PUBLIC KEY-----", "")
                                .replaceAll("\\s+", "");

                PublicKey publicKey = RSAUtil.getPublicKey(cleanPublicKeyPem);

                // Encrypt the password
                String encryptedPassword = Base64.getEncoder().encodeToString(
                                RSAUtil.encrypt("testpassword", publicKey));

                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"testuser\", \"password\": \"" + encryptedPassword + "\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isNotEmpty());

                // Test invalid credentials
                encryptedPassword = Base64.getEncoder().encodeToString(
                                RSAUtil.encrypt("wrongpassword", publicKey));
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"testuser\", \"password\": \"" + encryptedPassword + "\"}"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().string("Invalid username or password"));

                // Test missing fields
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\": \"\", \"password\": \"\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Username and password must not be null"));
        }

        @Test
        public void testGetAllUsers() throws Exception {
                mockMvc.perform(get("/api/users/listAll")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].username", is("testuser")));
        }

        @Test
        public void testFindUserByEmail() throws Exception {
                mockMvc.perform(get("/api/users/find")
                                .param("email", "testuser@example.com")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email", is("testuser@example.com")));

                // Test user not found
                mockMvc.perform(get("/api/users/find")
                                .param("email", "nonexistent@example.com")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("User not found with email: nonexistent@example.com"));

                // Test invalid email format
                mockMvc.perform(get("/api/users/find")
                                .param("email", "invalid-email")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Email should be valid"));
        }

        @Test
        public void testGetUserByUsername() throws Exception {
                mockMvc.perform(get("/api/users/getUser")
                                .param("username", "testuser")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username", is("testuser")));

                // Test user not found
                mockMvc.perform(get("/api/users/getUser")
                                .param("username", "nonexistentuser")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("User not found with username: nonexistentuser"));
        }

        @Test
        public void testUpdateUser() throws Exception {
                User user = userRepository.findByUsername("testuser").orElseThrow();

                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUsername("updateduser");
                userDTO.setEmail("updateduser@example.com");
                userDTO.setFirstName("Updated");
                userDTO.setLastName("User");

                mockMvc.perform(put("/api/users/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDTO))
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email", is("updateduser@example.com")));
        }

        @Test
        public void testUpdateUserMissingId() throws Exception {

                UserDTO userDTO = new UserDTO();
                userDTO.setId(null);
                userDTO.setUsername("updateduser");
                userDTO.setEmail("updateduser@example.com");
                userDTO.setFirstName("Updated");
                userDTO.setLastName("User");

                // Test missing ID
                mockMvc.perform(put("/api/users/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDTO))
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("User ID must not be null"));
        }

        // todo: update logic, keep getting error: actual 400, when debugging we see
        // "org.springframework.dao.IncorrectResultSizeDataAccessException: Query did
        // not return a unique result: 2 results were returned"
        @Test
        public void testUpdateUserDuplicateEmail() throws Exception {
                // Clear existing data to prevent duplicates
                userRepository.deleteAll();

                // Create and save the first user
                User user = new User();
                user.setUsername("testuser");
                user.setPassword(passwordEncoder.encode("testpassword"));
                user.setEmail("testuser@example.com");
                user.setFirstName("Test");
                user.setLastName("User");
                userRepository.save(user);

                // Create and save the second user
                UserCreateDTO newUser = new UserCreateDTO();
                newUser.setUsername("newuser");
                newUser.setPassword("newpassword");
                newUser.setEmail("anotheruser@example.com"); // Use a different email
                newUser.setFirstName("New");
                newUser.setLastName("User");
                userRepository.save(objectMapper.convertValue(newUser, User.class));

                // Update the first user with the email of the second user
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUsername("updateduser");
                userDTO.setEmail("anotheruser@example.com"); // Try to set to an existing email
                userDTO.setFirstName("Updated");
                userDTO.setLastName("User");

                mockMvc.perform(put("/api/users/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDTO))
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isConflict())
                                .andExpect(content().string("A user with the same email or username already exists."));
        }

        @Test
        public void testUpdatePassword() throws Exception {
                User user = userRepository.findByUsername("testuser").orElseThrow();

                PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
                passwordUpdateDTO.setCurrentPassword("testpassword");
                passwordUpdateDTO.setNewPassword("newpassword");

                mockMvc.perform(put("/api/users/updatePassword/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(passwordUpdateDTO))
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Password updated successfully"));

                // Verify that the password was updated
                User updatedUser = userRepository.findByUsername("testuser").orElseThrow();
                assertTrue(passwordEncoder.matches("newpassword", updatedUser.getPassword()));
        }

        @Test
        public void testUpdatePasswordWrongPassword() throws Exception {
                User user = userRepository.findByUsername("testuser").orElseThrow();

                PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
                passwordUpdateDTO.setCurrentPassword("wrongCurrentPassword");
                passwordUpdateDTO.setNewPassword("newpassword");

                mockMvc.perform(put("/api/users/updatePassword/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(passwordUpdateDTO))
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Current password is incorrect"));

                // Verify that the password was not updated
                User updatedUser = userRepository.findByUsername("testuser").orElseThrow();
                assertTrue(passwordEncoder.matches("testpassword", updatedUser.getPassword()));
        }

        @Test
        public void testDeleteUser() throws Exception {
                User user = userRepository.findByUsername("testuser").orElseThrow();

                mockMvc.perform(delete("/api/users/delete/{userId}", user.getId())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(content().string("User deleted successfully"));
        }

        @Test
        public void testDeleteUserIdNotFound() throws Exception {
                // Test user not found
                mockMvc.perform(delete("/api/users/delete/{userId}", 999)
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("User not found with ID: 999"));
        }

        @AfterEach
        public void tearDown() {
                // Clean up the database
                userRepository.deleteAll();
        }
}
