package ua.expandapis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.WebApplicationContext;
import ua.expandapis.dto.UserRegistrationDTO;
import ua.expandapis.model.entity.User;
import ua.expandapis.model.repository.UserRepository;
import ua.expandapis.model.service.user.JwtUserDetailsService;
import ua.expandapis.util.MapperUtil;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.expandapis.TestEnvironmentConstants.*;

import static ua.expandapis.controller.MappingsConstants.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class AuthControllerIT {

    static final String AUTHORIZATION = "Authorization";
    @Autowired
    private WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    @Autowired
    MapperUtil mapperUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUserDetailsService userService;

    @BeforeEach
    void setUp() {
        log.info("setUp");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    void cleanUp() {
        log.info("cleanUp");
        Optional<User> user = userRepository.findByUsername(USERNAME);
        if(user.isPresent())
            userRepository.deleteById(user.get().getId());
    }

    @Test
    @DisplayName("Test Wac configuration")
    public void testGivenWacAuthController() {

        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertNotNull(servletContext);
        Assertions.assertTrue(servletContext instanceof MockServletContext);
        Assertions.assertNotNull(webApplicationContext.getBean("authController"));
    }

    @Test
    @DisplayName("Register new User")
    public void testRegisterNewUser() throws Exception {
        log.info("Register new User");
        var userDto = registerUserPostRequest();
        assertTrue(userRepository.existsById(userDto.getId()));
        assertEquals(USERNAME, userDto.getUsername());

    }

        private UserRegistrationDTO registerUserPostRequest() throws Exception {
            UserRegistrationDTO userDto = jsonToUserRegistrationDTO(mockMvc.perform(post(AUTH_CONTROLLER_MAPPING + REGISTRATION_MAPPING)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\n" +
                                    "\t\"username\": \"" + USERNAME + "\",\n" +
                                    "\t\"password\": \"" + PASSWORD + "\"\n" +
                                    "}"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
            return userDto;
        }

        private UserRegistrationDTO jsonToUserRegistrationDTO(String json) throws IOException {
            return objectMapper.readValue(json, UserRegistrationDTO.class);
        }

    @Test
    @DisplayName("Invalid User registration format")
    public void testInvalidUserRegistrationFormat() throws Exception {
        log.info("Invalid User registration format");
        invalidRegisterUserPostRequest();
    }
         private void invalidRegisterUserPostRequest() throws Exception {
            mockMvc.perform(post(AUTH_CONTROLLER_MAPPING + REGISTRATION_MAPPING)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\n" +
                                    "\t\"username\": \"" + USERNAME + "\"\n" +
                                    //"\t\"password\": \"" + PASSWORD + "\"\n" +
                                    "}"))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
        }

    @Test
    @DisplayName("Login as new User")
    public void testLoginAsNewUser() throws Exception {
        log.info("Login as new User");
        registerUserPostRequest();
        loginAsNewUserPostRequest(USERNAME, PASSWORD);
    }

        private String loginAsNewUserPostRequest(String username, String password) throws Exception {
            var mvcResult = mockMvc.perform(post(AUTH_CONTROLLER_MAPPING + AUTHENTICATION_MAPPING)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\n" +
                                    "\t\"username\": \"" + username + "\",\n" +
                                    "\t\"password\": \"" + password + "\"\n" +
                                    "}"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();
            return mvcResult.getResponse().getHeader(AUTHORIZATION);
        }

    @Test
    @DisplayName("Incorrect User login name")
    public void testIncorrectUserLoginName() throws Exception {
        log.info("Incorrect User login name");
        registerUserPostRequest();
        assertThatThrownBy(
                ()->      incorrectUserLoginPostRequest(USERNAME+"346234", PASSWORD)
        ).hasCauseInstanceOf(BadCredentialsException.class);
    }
    @Test
    @DisplayName("Incorrect User login password")
    public void testIncorrectUserLoginPassword() throws Exception {
        log.info("Incorrect User login password");
        registerUserPostRequest();
        assertThatThrownBy(
                ()->      incorrectUserLoginPostRequest(USERNAME, PASSWORD+"346234")
        ).hasCauseInstanceOf(BadCredentialsException.class);

    }

    private void incorrectUserLoginPostRequest(String username, String password) throws Exception {
         mockMvc.perform(post(AUTH_CONTROLLER_MAPPING + AUTHENTICATION_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "\t\"username\": \"" + username + "\",\n" +
                                "\t\"password\": \"" + password + "\"\n" +
                                "}"));
                /*
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
                .andReturn();*/

    }
}
