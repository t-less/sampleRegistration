package com.khov.sampleRegistration.mail;

/**
 *
 * @author t-less
 */
public interface EmailService {

    void sendText(String to, String subject, String text);
    
    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);
}
