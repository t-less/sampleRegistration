package com.khov.sampleRegistration.login;

import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.service.UserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author t-less
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = userService.findByEmail(authentication.getName());
            int id = user.getId();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();

            LoggedUser loggedUser = new LoggedUser(id, firstName, lastName, authentication.getName());
            loggedUser.setMainRole(user.getMainRole());
            session.setAttribute("user", loggedUser);

            String redirectLink = "/user/view/" + id;
            redirectStrategy.sendRedirect(request, response, redirectLink);
        }

        clearAuthenticationAttributes(request);
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
