package ua.expandapis.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.expandapis.util.JwtTokenUtil;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(@Lazy UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Do JWT Filter");

        Optional<String> jwtToken = jwtTokenUtil.getTokenFromHttpServletRequest(request, AUTHORIZATION_HEADER);
        if(jwtToken.isPresent()) {
            Optional<String> username = jwtTokenUtil.getUsernameFromToken(jwtToken.get());
            configureSecurity(request, jwtToken, username);
        }

        filterChain.doFilter(request, response);
    }

    private void configureSecurity(HttpServletRequest request, Optional<String> jwtToken,
                                   Optional<String> username) {

        //Validate the token
        if (username.isPresent() && jwtToken.isPresent()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            Boolean isTokenValid = jwtTokenUtil.validateToken(jwtToken.get());
            Optional<String> tokenSubject = jwtTokenUtil.getUsernameFromToken(jwtToken.get());

            UserDetails userDetails = userDetailsService.loadUserByUsername(username.get());

            // on valid token configure Spring Security to authentication
            if (isTokenValid &&
                tokenSubject.isPresent() &&
                tokenSubject.get().equals(userDetails.getUsername())
            ) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                        );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }
}
