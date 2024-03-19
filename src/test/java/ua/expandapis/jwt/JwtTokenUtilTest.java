package ua.expandapis.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ua.expandapis.model.entity.User;
import ua.expandapis.model.service.user.JwtUserDetails;
import ua.expandapis.util.JwtTokenUtil;

@Slf4j
@SpringBootTest
public class JwtTokenUtilTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private String createAccessToken() {
        User user = User.builder().id(1L).username("user").password("54hWgew4236wW%").build();
        JwtUserDetails userDetails = new JwtUserDetails(user);

        Authentication authUser = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities());

        return jwtTokenUtil.createAccessToken(authUser);
    }

    @Test
    public void testCreateAccessToken() {
        String jwtToken = createAccessToken();
        log.info("Generated token -> " + jwtToken);
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    public void testValidateToken(){
        String token = createAccessToken();
        Assertions.assertTrue(jwtTokenUtil.validateToken(token));
    }
}
