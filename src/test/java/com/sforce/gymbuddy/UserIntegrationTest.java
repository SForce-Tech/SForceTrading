package com.sforce.gymbuddy;

import com.sforce.gymbuddy.model.User;
import com.sforce.gymbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("testpassword"));
        user.setEmail("testuser@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        userRepository.save(user);
    }

    @Test
    public void testLoginUser() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "testpassword"))
                .andExpect(status().is3xxRedirection()); // Expecting a redirect after login
    }

    @Test
    public void testRegisterUser() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType("application/json")
                .content(
                        "{\"username\": \"newuser\", \"password\": \"newpassword\", \"email\": \"newuser@example.com\", \"firstName\": \"New\", \"lastName\": \"User\"}"))
                .andExpect(status().isCreated());
    }
}
