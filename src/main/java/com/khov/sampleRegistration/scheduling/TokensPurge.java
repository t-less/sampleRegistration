package com.khov.sampleRegistration.scheduling;

import com.khov.sampleRegistration.repository.VerificationTokenRepository;
import java.time.Instant;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author t-less
 */
@Service
@Transactional
public class TokensPurge {
    @Autowired
    private VerificationTokenRepository tokenRepository;
 
    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {
        Date now = Date.from(Instant.now());
        tokenRepository.deleteAllExpiredSince(now);
    }
}
