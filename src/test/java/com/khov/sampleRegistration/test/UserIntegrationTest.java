package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.exception.EmailExistsException;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.repository.VerificationTokenRepository;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private Integer tokenId;
    private Integer userId;

    @Before
    public void givenUserAndVerificationToken() throws EmailExistsException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setShapass("SecretPassword");
        user.setFirstName("First");
        user.setLastName("Last");
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @Test
    public void whenContextLoad_thenCorrect() {
        assertEquals(1, userRepository.count());
        assertEquals(1, tokenRepository.count());
    }

    @Test
    @Ignore("needs to go through the service and get transactional semantics")
    public void whenRemovingUser_thenFkViolationException() {
        userRepository.deleteById(userId);
    }

    @Test
    public void whenRemovingTokenThenUser_thenCorrect() {
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }

}
