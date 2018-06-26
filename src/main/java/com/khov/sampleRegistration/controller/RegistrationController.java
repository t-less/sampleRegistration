package com.khov.sampleRegistration.controller;

import com.khov.sampleRegistration.dto.UserDto;
import com.khov.sampleRegistration.events.registrationEvents.OnRegistrationCompleteEvent;
import com.khov.sampleRegistration.exception.EmailExistsException;
import com.khov.sampleRegistration.mail.EmailService;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.model.VerificationToken;
import com.khov.sampleRegistration.service.UserService;
import com.khov.sampleRegistration.utils.GenericResponse;
import java.util.Calendar;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author t-less
 */
/**
 *
 * @author t-less
 */
@Controller
public class RegistrationController {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String getRegistrationPage(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid UserDto accountDto, HttpServletRequest request) {
        User registered = createUserAccount(accountDto);
        if (registered == null) {
            throw new EmailExistsException("There is an account with that email adress: "
                    + accountDto.getEmail());
        }
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        return new GenericResponse("success");
    }

    private User createUserAccount(UserDto accountDto) {
        User registered = userService.registerNewUserAccount(accountDto);
        return registered;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/badUser?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String message = messages.getMessage("auth.message.expired", null, locale);
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("expired", true);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/badUser?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        redirectAttributes.addFlashAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login?lang=" + request.getLocale().getLanguage();
    }

    @RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendRegistrationToken(
            HttpServletRequest request, @RequestParam("token") String existingToken) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);

        User user = userService.getUser(newToken.getToken());
        String appUrl
                = "http://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
        String confirmationUrl
                = appUrl + "/regitrationConfirm?token=" + newToken.getToken();
        String message = messages.getMessage("message.resendToken", null, request.getLocale());
        String subject = messages.getMessage("message.resendVerificationToken", null, request.getLocale());
        String text = message + " " + confirmationUrl;
        String recipientAddress = user.getEmail();
        emailService.sendText(recipientAddress, subject, text);
        return new GenericResponse(
                messages.getMessage("message.resendToken", null, request.getLocale()));
    }
}
