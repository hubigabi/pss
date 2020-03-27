package pl.utp.pss.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.utp.pss.PssApplication;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.RoleRepository;
import pl.utp.pss.repository.UserRepository;
import pl.utp.pss.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PssApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void getAllUsers() throws Exception {
        User u1 = new User("companyName1", "companyAddress1", "companyNip1",
                "Charles", "Smith", "JohnSmith@gmail.com", "JohnSmith");
        User u2 = new User("companyName2", "companyAddress2", "companyNip1",
                "Richard", "Johnson", "AdamJohnson@gmail.com", "AdamJohnson");
        userRepository.save(u1);
        userRepository.save(u2);

        mvc.perform(MockMvcRequestBuilders.get("/api/user/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"" + u1.getName() + "\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"" + u2.getName() + "\"")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void registerUser() throws Exception {
        User user = new User("companyName1", "companyAddress1", "companyNip1",
                "Qwe", "Rty", "qwerty@gmail.com", "password123");

        mvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                .content(asJsonString(user))
                //.content("{ \"companyAddress\": \"string\", \"companyName\": \"string\", \"companyNip\": \"string\", \"email\": \"string\", \"id\": 0, \"lastName\": \"string\", \"string\": \"name\", \"password\": \"string\", \"registrationDate\": \"2020-02-27\", \"status\": true}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());


        mvc.perform(MockMvcRequestBuilders.get("/api/user/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"" + user.getName() + "\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"lastName\":\"" + user.getLastName() + "\"")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void changePassword() throws Exception {
        User user = userService.getAllUsers().get(0);
        long userId = user.getId();
        String newPassword = "newPassword";

        mvc.perform(MockMvcRequestBuilders.put("/api/user/changePassword?userId=" + userId + "&newPassword=" + newPassword)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(userRepository.findById(userId).get().getPassword(), newPassword);
    }

    @Test
    public void deleteUserById() throws Exception {
        User user = userService.getAllUsers().get(0);
        long userId = user.getId();

        assertTrue(userRepository.findById(userId).isPresent());
        mvc.perform(MockMvcRequestBuilders.delete("/api/user/deleteUserById?userId=" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    public void getAllUsersByRoleName() throws Exception {
        Role role = roleRepository.findAll().get(0);

        assertNotNull(role.getRoleName());

        mvc.perform(MockMvcRequestBuilders.get("/api/user/allUsersByRoleName?roleName=" + role.getRoleName())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString();
                    System.out.println(json);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                    List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {
                    });

                    users.stream()
                            .mapToLong(User::getId)
                            .distinct()
                            .forEach(id ->
                                    assertTrue(userRepository.findById(id).get().getRoles().contains(role))
                            );

                })
                .andDo(MockMvcResultHandlers.print());

    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}