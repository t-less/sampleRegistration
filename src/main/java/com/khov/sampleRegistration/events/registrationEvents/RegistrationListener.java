package com.khov.sampleRegistration.events.registrationEvents;

import com.khov.sampleRegistration.mail.EmailService;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.service.UserService;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author t-less
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = messages.getMessage("message.regConfirmSubj", null, event.getLocale());

        String appUrl
                = "http://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();

        String confirmationUrl = appUrl + "/registrationConfirm?token=" + token;
        String message = messages.getMessage("message.regSuccess", null, event.getLocale());
        String text = message + " " + confirmationUrl;
        emailService.sendText(recipientAddress, subject, text);
    }
}
