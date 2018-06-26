package com.khov.sampleRegistration.events.startup;

import com.khov.sampleRegistration.model.Privilege;
import com.khov.sampleRegistration.model.Role;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.repository.PrivilegeRepository;
import com.khov.sampleRegistration.repository.RoleRepository;
import com.khov.sampleRegistration.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author t-less
 */
@Component
public class InitialDataLoader {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        Privilege readPrivilege
                = createPrivilegeIfNotFound("read_privilege");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("write_privilege");
        Privilege deletePrivilege
                = createPrivilegeIfNotFound("delete_privilege");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, deletePrivilege);
        List<Privilege> userPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("admin", adminPrivileges);
        createRoleIfNotFound("user", userPrivileges);
        createRoleIfNotFound("guest", Arrays.asList(readPrivilege));

        createUserIfNotFound();
        alreadySetup = true;
    }

    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    private Role createRoleIfNotFound(String name, List<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivilegeList(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    private void createUserIfNotFound() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            Role adminRole = roleRepository.findByName("admin");
            user = new User();
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setShapass(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setRoleList(Arrays.asList(adminRole));
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

}
