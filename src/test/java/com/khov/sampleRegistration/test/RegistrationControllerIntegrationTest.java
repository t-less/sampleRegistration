package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.repository.VerificationTokenRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private MockMvc mockMvc;
    private String token;
    private VerificationToken verificationToken;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = new User();
        user.setEmail(UUID.randomUUID().toString() + "@example.com");
        user.setShapass(UUID.randomUUID().toString());
        user.setFirstName("First");
        user.setLastName("Last");
        userRepository.save(user);

        token = UUID.randomUUID().toString();
        verificationToken = new VerificationToken(token, user);
        verificationToken.setExpiryDate(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)));

        verificationTokenRepository.save(verificationToken);
    }

    @Test
    public void testRegistrationConfirm() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm?token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Your account verified successfully"));
        resultActions.andExpect(view().name("redirect:/login?lang=en"));
    }

    @Test
    public void testRegistrationConfirmWithInvalidToken() throws Exception {
        String invalidToken = "qwerty";
        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm?token=" + invalidToken));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Invalid token."));
        resultActions.andExpect(view().name("redirect:/badUser?lang=en"));
    }

    @Test
    public void testRegistrationConfirmWithExpiredToken() throws Exception {
        verificationToken.setExpiryDate(Date.from(Instant.now().minus(3, ChronoUnit.DAYS)));
        verificationTokenRepository.save(verificationToken);

        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm?token=" + verificationToken.getToken()));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Your registration token has expired. Please register again."));
        resultActions.andExpect(view().name("redirect:/badUser?lang=en"));
    }

    @Test
    public void testRegistrationConfirmWithNullToken() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm?token=" + null));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(flash().attribute("message", "Invalid token."));
        resultActions.andExpect(view().name("redirect:/badUser?lang=en"));
    }

    @Test
    public void testRegistrationValidation() throws Exception {
        final MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("firstName", "");
        param.add("lastName", "");
        param.add("email", "");
        param.add("password", "");
        param.add("matchingPassword", "");

        ResultActions resultActions = this.mockMvc.perform(post("/registration").params(param));
        resultActions.andExpect(status().is(400));
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.error", is("InvaliduserDto")))
                .andExpect(jsonPath("$.message", containsString("{\"field\":\"lastName\",\"defaultMessage\":\"Length must be greater than 1\"}")));
    }

    @Test
    @Ignore("Use only when JavaMailService is configured with proper email and password")
    public void testResendRegisrtationToken() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/user/resendRegistrationToken?token=" + token));
        resultActions.andExpect(status().isOk());
    }

}
