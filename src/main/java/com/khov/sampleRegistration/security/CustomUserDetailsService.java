package com.khov.sampleRegistration.security;

import com.khov.sampleRegistration.login.LoginAttemptService;
import com.khov.sampleRegistration.model.Privilege;
import com.khov.sampleRegistration.model.Role;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author t-less
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String email) {
        checkIfAccountIsBlocked();
        try {
            User user = getUserByEmail(email);
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getShapass(), user.getEnabled(), accountNonExpired,
                    credentialsNonExpired, accountNonLocked,
                    getAuthorities(user.getRoleList()));
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkIfAccountIsBlocked() {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }
    }

    private User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(
                    "No user found with username: " + email);
        }
        return user;
    }

    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private List<String> getPrivileges(List<Role> roles) {
        List<String> privileges = new ArrayList<>();
        List<Privilege> privilegeList = new ArrayList<>();
        for (Role role : roles) {
            privilegeList.addAll(role.getPrivilegeList());
        }
        for (Privilege item : privilegeList) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
