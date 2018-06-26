package com.khov.sampleRegistration.service;

import com.khov.sampleRegistration.dto.UserDto;
import com.khov.sampleRegistration.exception.EmailExistsException;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author t-less
 */

public interface UserService {
    
    List<User> findAll();

    Optional<User> findById(Integer id);
    
    User findUserById(Integer id);

    User create(User user);

    User edit(User user);

    void deleteById(Integer id);
    
    User findByEmail(String email);
    
    User registerNewUserAccount(UserDto userDto) throws EmailExistsException;
    
    User getUser(String verificationToken);
 
    void saveRegisteredUser(User user);
 
    void createVerificationToken(User user, String token);
    
    VerificationToken generateNewVerificationToken(String token);
 
    VerificationToken getVerificationToken(String VerificationToken);
    
    void createPasswordResetTokenForUser(User user, String token);
    
    void changeUserPassword(User user, String password);
    
}
