package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.dto.UserDto;
import com.khov.sampleRegistration.model.PasswordResetToken;
import com.khov.sampleRegistration.repository.PasswordResetTokenRepository;
import com.khov.sampleRegistration.service.PasswordResetTokenService;
import com.khov.sampleRegistration.service.UserService;
import com.khov.sampleRegistration.model.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordResetTokenServiceIntergationTest {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserService userService;

    @Test
    public void givenToken_whenValidation_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String result = passwordResetTokenService.validatePasswordResetToken(user.getId(), token);
        assertEquals(result, null);
    }

    @Test
    public void givenInvalidToken_whenValidation_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();

        userService.createPasswordResetTokenForUser(user, token);

        final String otherToken = UUID.randomUUID().toString();
        String result = passwordResetTokenService.validatePasswordResetToken(user.getId(), otherToken);
        assertEquals(result, "invalidToken");
    }

    @Test
    public void givenExpiredToken_whenValidation_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();

        final PasswordResetToken pToken = new PasswordResetToken(token, user);
        pToken.setExpiryDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));
        passwordResetTokenRepository.save(pToken);

        String result = passwordResetTokenService.validatePasswordResetToken(user.getId(), token);
        assertEquals(result, "expired");
    }

    private UserDto createUserDto(final String email) {
        final UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword("SecretPassword");
        userDto.setMatchingPassword("SecretPassword");
        userDto.setFirstName("First");
        userDto.setLastName("Last");
        return userDto;
    }

    private User registerUser() {
        final String email = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(email);
        final User user = userService.registerNewUserAccount(userDto);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        return user;
    }

}
