package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.dto.UserDto;
import com.khov.sampleRegistration.exception.EmailExistsException;
import com.khov.sampleRegistration.model.Privilege;
import com.khov.sampleRegistration.model.Role;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.repository.VerificationTokenRepository;
import com.khov.sampleRegistration.service.UserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Test
    public void givenNewUser_whenRegistered_thenCorrect() throws EmailExistsException {
        final String userEmail = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(userEmail);

        final User user = userService.registerNewUserAccount(userDto);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    public void givenDetachedUser_whenAccessingEntityAssociations_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        assertNotNull(user.getRoleList());
        user.getRoleList().stream().filter(r -> r != null).forEach(Role::getId);
        user.getRoleList().stream().filter(r -> r != null).forEach(Role::getName);
        user.getRoleList().stream().filter(r -> r != null).forEach(r -> r.getPrivilegeList().stream().filter(p -> p != null).forEach(Privilege::getId));
        user.getRoleList().stream().filter(r -> r != null).forEach(r -> r.getPrivilegeList().stream().filter(p -> p != null).forEach(Privilege::getName));
        user.getRoleList().stream().filter(r -> r != null).forEach(r -> r.getPrivilegeList().stream().map(Privilege::getRoleList).forEach(Assert::assertNotNull));
    }

    @Test
    public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        final Optional<User> userOpt = userService.findById(user.getId());
        User user2 = userOpt.get();
        assertEquals(user, user2);
    }

    @Test
    public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailExistsException {
        final User user = registerUser();
        final User user2 = userService.findByEmail(user.getEmail());
        assertEquals(user, user2);
    }

    @Test(expected = EmailExistsException.class)
    public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {
        final String email = UUID.randomUUID().toString();
        final UserDto userDto = createUserDto(email);

        userService.registerNewUserAccount(userDto);
        userService.registerNewUserAccount(userDto);
    }

    @Test
    public void givenUserRegistered_whenCreateToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
    }

    @Test(expected = Exception.class)
    public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        userService.createVerificationToken(user, token);
    }

    @Test
    public void givenUserAndToken_whenLoadToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getUser());
        assertEquals(user, verificationToken.getUser());
        assertEquals(user.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
        assertTrue(verificationToken.getExpiryDate().toInstant().isAfter(Instant.now()));
    }

    @Test
    public void givenUserAndToken_whenRemovingUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        userService.deleteById(user.getId());
    }

    @Test
    public void givenUserAndToken_whenRemovingTokenThenUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        final int userId = user.getId();
        final int tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }

    @Test
    public void givenUserAndToken_whenRemovingToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        final int tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
    }

    @Test
    public void givenUserAndToken_whenNewTokenRequest_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        final VerificationToken origToken = userService.getVerificationToken(token);
        final VerificationToken newToken = userService.generateNewVerificationToken(token);
        assertNotEquals(newToken.getToken(), origToken.getToken());
        assertNotEquals(newToken.getExpiryDate(), origToken.getExpiryDate());
    }

    @Test
    public void givenUser_whenCreatePasswordResetToken_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
    }

    @Test(expected = Exception.class)
    public void givenUser_whenCreatePasswordResetTokenCreateDuplicate_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        userService.createPasswordResetTokenForUser(user, token);
    }

    @Test
    public void givenUserAndPasswordResetToken_whenRemovingUser_thenCorrect() {
        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        userService.deleteById(user.getId());
    }

    @Test
    public void givenUser_whenChangePassword_thenCorrect() {
        final User user = registerUser();
        final String shapass = user.getShapass();

        final String newPassword = "newpass";
        userService.changeUserPassword(user, newPassword);

        final String newShapass = user.getShapass();
        assertNotEquals(shapass, newShapass);
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
