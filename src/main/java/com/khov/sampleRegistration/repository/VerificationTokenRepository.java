package com.khov.sampleRegistration.repository;

import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author t-less
 */
@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
    
    void deleteByExpiryDateLessThan(Date now);
    
    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= :now")
    void deleteAllExpiredSince(@Param("now") Date now);
    
    List <VerificationToken> findAllByExpiryDateLessThan(Date now);
}
