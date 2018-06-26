package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.model.PasswordResetToken;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.repository.PasswordResetTokenRepository;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private MockMvc mockMvc;
    private User user;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        user = new User();
        user.setEmail(UUID.randomUUID().toString() + "@example.com");
        user.setShapass(UUID.randomUUID().toString());
        user.setFirstName("First");
        user.setLastName("Last");
        userRepository.save(user);
    }

    @Test
    @Ignore("Use only when JavaMailService is configured with proper email and password")
    public void testResetPassword() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/password/resetPassword").param("email", user.getEmail()));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message", is("You should receive an Password Reset Email shortly")));
    }

    @Test
    @Ignore("Use only when JavaMailService is configured with proper email and password")
    public void testResetPasswordWithInvalidEmail() throws Exception {
        String randomEmail = "qwerty";

        ResultActions resultActions = this.mockMvc.perform(post("/password/resetPassword").param("email", randomEmail));
        resultActions.andExpect(status().is(404));
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.error", is("UserNotFound")))
                .andExpect(jsonPath("$.message", is("User Not Found")));
    }

    @Test
    public void testShowChangePasswordPage() throws Exception {
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        ResultActions resultActions = this.mockMvc.perform(get("/password/changePassword?id=" + user.getId() + "&token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(view().name("redirect:/password/updatePassword?lang=en"));
    }

    @Test
    public void testShowChangePasswordPageWithInvalidToken() throws Exception {
        String token = UUID.randomUUID().toString();

        ResultActions resultActions = this.mockMvc.perform(get("/password/changePassword?id=" + user.getId() + "&token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Invalid token."));
        resultActions.andExpect(view().name("redirect:/login?lang=en"));
    }

    @Test
    public void testShowChangePasswordPageWithExpiredToken() throws Exception {
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        assertNotNull(token);

        passwordResetToken.setExpiryDate(Date.from(Instant.now().minus(3, ChronoUnit.DAYS)));
        passwordResetTokenRepository.save(passwordResetToken);

        ResultActions resultActions = this.mockMvc.perform(get("/password/changePassword?id=" + user.getId() + "&token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Your registration token has expired. Please register again."));
        resultActions.andExpect(view().name("redirect:/login?lang=en"));
    }

}
