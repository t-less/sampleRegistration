package com.khov.sampleRegistration.service;

/**
 *
 * @author t-less
 */
public interface PasswordResetTokenService {

    String validatePasswordResetToken(Integer id, String token);
}
