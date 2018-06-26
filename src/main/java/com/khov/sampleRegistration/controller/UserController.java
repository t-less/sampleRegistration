package com.khov.sampleRegistration.controller;

import com.khov.sampleRegistration.login.LoggedUser;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.service.UserService;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author t-less
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/user/view/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable Integer id, WebRequest request, Model model) {
        User user = userService.findUserById(id);
        if (user == null) {
            Locale locale = request.getLocale();
            String message = messageSource.getMessage("message.userNotFound", null, locale);
            model.addAttribute("message", message);
            return "user/notFound";
        }
        LoggedUser loggedUser = new LoggedUser(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        loggedUser.setMainRole(user.getMainRole());
        model.addAttribute("user", loggedUser);
        return "user/view";
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.GET)
    public String getAllUsers(WebRequest request, Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/all";
    }

    @RequestMapping(value = "/user/my", method = RequestMethod.GET)
    public String getProfileForCurrent(WebRequest request, Model model) {
        LoggedUser loggedUser = (LoggedUser) httpServletRequest.getSession().getAttribute("user");
        return "redirect:/user/view/" + loggedUser.getId();
    }

}
