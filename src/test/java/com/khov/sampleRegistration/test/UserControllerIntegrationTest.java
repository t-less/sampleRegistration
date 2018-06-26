package com.khov.sampleRegistration.test;

import com.khov.sampleRegistration.model.Role;
import com.khov.sampleRegistration.model.User;
import com.khov.sampleRegistration.repository.RoleRepository;
import com.khov.sampleRegistration.repository.UserRepository;
import com.khov.sampleRegistration.service.UserService;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.UUID;
import javax.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author t-less
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    private String email;
    private MockMvc mockMvc;
    private User user;
    private Role role;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        role = new Role("TEST_ROLE");
        roleRepository.save(role);

        user = new User();
        email = UUID.randomUUID().toString() + "@example.com";
        user.setEmail(email);
        user.setShapass(UUID.randomUUID().toString());
        user.setFirstName("First");
        user.setLastName("Last");
        user.setRoleList(Arrays.asList(role));
        userRepository.save(user);
    }

    @Test
    @WithMockUser(authorities = "write_privilege")
    public void testGetUser() throws Exception {
        User user1 = userRepository.findByEmail(email);
        assertEquals(user1, user);

        Integer userId = user.getId();

        ResultActions resultActions = this.mockMvc.perform(get("/user/view/{id}", userId));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("user/view"));
    }

    @Test
    @WithMockUser(authorities = "write_privilege")
    public void testGetInvalidUser() throws Exception {
        int random = 123;

        ResultActions resultActions = this.mockMvc.perform(get("/user/view/{id}", random));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(model().attribute("message", "User Not Found"));
        resultActions.andExpect(view().name("user/notFound"));
    }

    @Test
    @WithMockUser(authorities = "write_privilege")
    public void testGetAllUsers() throws Exception {
        List<User> users = userService.findAll();

        ResultActions resultActions = this.mockMvc.perform(get("/user/all"));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(model().attribute("users", users));
        resultActions.andExpect(view().name("user/all"));
    }

}
