package com.khov.sampleRegistration.repository;

import com.khov.sampleRegistration.model.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author t-less
 */
@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Integer> {

    PasswordResetToken findByToken(String token);
}
