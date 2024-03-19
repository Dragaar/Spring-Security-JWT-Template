package ua.expandapis.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.expandapis.security.JwtAuthenticationToken;
import ua.expandapis.util.JwtTokenUtil;

import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private JwtTokenUtil jwtTokenUtil;

    /** Decode and validate the Bearer Token **/
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = String.valueOf(authentication.getCredentials());
        Boolean isTokenValid = jwtTokenUtil.validateToken(token);

        Optional<String> tokenPrincipal = jwtTokenUtil.getUsernameFromToken(token);
        Optional<List<GrantedAuthority>> tokenAuthorities = jwtTokenUtil.getUserAuthorities(token);

        // on valid token configure Spring Security to authentication
        if (isTokenValid &&
                tokenPrincipal.isPresent() && tokenAuthorities.isPresent()
        ) {
            JwtAuthenticationToken jwtAuthToken =
                    new JwtAuthenticationToken(
                            tokenPrincipal.get(),
                            null,
                            tokenAuthorities.get()
                    );
            return jwtAuthToken;
        } else return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
}
