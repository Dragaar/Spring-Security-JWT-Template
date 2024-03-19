package ua.expandapis.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.expandapis.security.JwtAuthenticationToken;
import ua.expandapis.util.JwtTokenUtil;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(@Lazy AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Do JWT Filter");

        Optional<String> jwtToken = jwtTokenUtil.getTokenFromHttpServletRequest(request, AUTHORIZATION_HEADER);
        if(jwtToken.isPresent()) {
            configureSecurity(request, jwtToken);
        }

        filterChain.doFilter(request, response);
    }

    private void configureSecurity(HttpServletRequest request, Optional<String> jwtToken) {

        if (jwtToken.isPresent()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

                Authentication jwtAuthToken =
                authenticationManager.authenticate(new JwtAuthenticationToken(null, jwtToken));

               /* jwtAuthToken
                        .setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                        );*/

                SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);

        }
    }
}
