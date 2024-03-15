package ua.expandapis.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.expandapis.model.entity.User;
import ua.expandapis.model.service.user.JwtUserDetails;
import ua.expandapis.util.JwtTokenUtil;

import static ua.expandapis.TestEnvironmentConstants.TOKEN_PREFIX;

@Slf4j
@SpringBootTest
public class JwtTokenUtilTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private String generateToken() {
        User user = User.builder().id(1L).username("user").password("54hWgew4236wW%").build();
        JwtUserDetails userDetails = new JwtUserDetails(user);

        return jwtTokenUtil.generateToken(userDetails);
    }

    @Test
    public void testGenerateToken() {
        String jwtToken = generateToken();
        log.info("Generated token -> " + jwtToken);
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    public void testValidateToken(){
        String token = generateToken();
        String jwtToken = token.replace(TOKEN_PREFIX, "");

        Assertions.assertTrue(jwtTokenUtil.validateToken(jwtToken));
    }
}
