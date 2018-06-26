package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.repository.VerificationTokenRepository;
import com.khov.sampleRegistration.scheduling.TokensPurge;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
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
@Transactional
public class TokenExpirationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private TokensPurge tokensPurge;

    private Integer userId;
    private Integer tokenId;

    @Before
    public void givenUserWithExpiredToken() {
        User user = new User();
        user.setEmail(UUID.randomUUID().toString() + "@example.com");
        user.setShapass(UUID.randomUUID().toString());
        user.setFirstName("First");
        user.setLastName("Last");

        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationToken.setExpiryDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));

        tokenRepository.save(verificationToken);

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @Test
    public void whenContextLoad_thenCorrect() {
        assertNotNull(userId);
        assertNotNull(tokenId);
        assertNotNull(userRepository.findById(userId));

        Optional<VerificationToken> verificationToken = tokenRepository.findById(tokenId);
        assertNotNull(verificationToken);

        List<VerificationToken> verifTokens = tokenRepository.findAllByExpiryDateLessThan(Date.from(Instant.now()));
        assertEquals(verificationToken.get(), verifTokens.get(0));
    }
    
    @Test
    public void whenRemoveByJPQLQuery_thenCorrect() {
        tokenRepository.deleteAllExpiredSince(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }
    
    @Test
    public void whenRemoveByGeneratedQuery_thenCorrect() {
        tokenRepository.deleteByExpiryDateLessThan(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }
    
    @Test
    public void whenPurgeTokenTask_thenCorrect() {
        tokensPurge.purgeExpired();
        assertEquals(0, tokenRepository.count());
    }
}
