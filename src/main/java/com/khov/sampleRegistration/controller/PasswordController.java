package com.khov.sampleRegistration.controller;

import com.khov.sampleRegistration.dto.PasswordDto;
import com.khov.sampleRegistration.dto.UserDto;
import com.khov.sampleRegistration.exception.UserNotFoundException;
import com.khov.sampleRegistration.mail.EmailService;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.service.PasswordResetTokenService;
import com.khov.sampleRegistration.service.UserService;
import com.khov.sampleRegistration.utils.GenericResponse;
import java.util.Locale;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author t-less
 */
@Controller
public class PasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @RequestMapping(value = "/password/forgotPassword", method = RequestMethod.GET)
    public String getForgotPasswordPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "password/forgotPassword";
    }

    @RequestMapping(value = "/password/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(HttpServletRequest request,
            @RequestParam("email") String userEmail) {
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException();
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        
        sendResetTokenMessage(request, user, token);
        
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null,
                        request.getLocale()));
    }

    private void sendResetTokenMessage(HttpServletRequest request, User user, String token) {
        String appUrl
                = "http://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
        String confirmationUrl
                = appUrl + "/password/changePassword?id=" + user.getId() + "&token=" + token;
        String message = messages.getMessage("message.resetPassword", null, request.getLocale());
        String subject = messages.getMessage("message.resentPassSubject", null, request.getLocale());
        String text = message + " " + confirmationUrl;
        String recipientAddress = user.getEmail();
        emailService.sendText(recipientAddress, subject, text);
    }

    @RequestMapping(value = "/password/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(Locale locale, Model model,
            @RequestParam("id") Integer id, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        String result = passwordResetTokenService.validatePasswordResetToken(id, token);
        if (result != null) {
            redirectAttributes.addFlashAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
        redirectAttributes.addFlashAttribute("passwordObj", new PasswordDto());
        return "redirect:/password/updatePassword?lang=" + locale.getLanguage();
    }

    @RequestMapping(value = "/password/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse savePassword(Locale locale, @Valid PasswordDto passwordDto) {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(
                messages.getMessage("message.resetPasswordSuc", null, locale));
    }
}
