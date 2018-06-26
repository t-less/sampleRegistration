package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.model.Privilege;
import com.khov.sampleRegistration.model.Role;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.repository.PrivilegeRepository;
import com.khov.sampleRegistration.repository.RoleRepository;
import com.khov.sampleRegistration.repository.UserRepository;
import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityRolesIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Role role;
    private Privilege privilege;

    @Before
    public void setUp() {
        privilege = new Privilege("TEST_PRIVILEGE");
        privilegeRepository.save(privilege);

        role = new Role("TEST_ROLE");
        role.setPrivilegeList(Arrays.asList(privilege));
        roleRepository.save(role);

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setShapass(passwordEncoder.encode("1234"));
        user.setEmail("john@doe.com");
        user.setRoleList(Arrays.asList(role));
        user.setEnabled(true);
        userRepository.save(user);

        assertNotNull(userRepository.findByEmail(user.getEmail()));
        assertNotNull(roleRepository.findByName(role.getName()));
        assertNotNull(privilegeRepository.findByName(privilege.getName()));
    }

    @After
    public void clearReposAndData() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        privilegeRepository.deleteAll();
        role = null;
        user = null;
        privilege = null;
    }

    @Test
    public void testDeleteUserBeforeRole() {
        userRepository.delete(user);

        assertNull(userRepository.findByEmail(user.getEmail()));
        assertNotNull(roleRepository.findByName(role.getName()));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteRoleBeforeUser() {
        roleRepository.delete(role);
    }

    @Test
    public void testDeleteRoleBeforePrivilege() {
        userRepository.delete(user);
        roleRepository.delete(role);

        assertNull(roleRepository.findByName(role.getName()));
        assertNotNull(privilegeRepository.findByName(privilege.getName()));

        privilegeRepository.delete(privilege);
        assertNull(privilegeRepository.findByName(privilege.getName()));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeletePrivilegeBeforeRole() {
        privilegeRepository.delete(privilege);
    }

}
