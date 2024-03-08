package ua.expandapis.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.expandapis.dto.UserRegistrationDTO;
import ua.expandapis.dto.jwt.JwtRequest;
import ua.expandapis.dto.jwt.JwtResponse;
import ua.expandapis.model.service.user.UserDetailsManager;
import ua.expandapis.util.JwtTokenUtil;

import static ua.expandapis.controller.MappingsConstants.*;

@Slf4j
@RestController
@RequestMapping(AUTH_CONTROLLER_MAPPING)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsManager userDetailsManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
                          UserDetailsManager userDetailsManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsManager = userDetailsManager;
    }

    @PostMapping(AUTHENTICATION_MAPPING)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid JwtRequest authenticationRequest)
            throws Exception {
        log.info("Auth User Controller");
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsManager
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }
        private void authenticate(String username, String password) throws Exception {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            } catch (DisabledException e) {
                //custom exception handling
                log.warn("Disabled user try to access");
                throw e;
            } catch (BadCredentialsException e) {
                //custom exception handling
                log.warn("Wrong user credentials");
                throw e;
            }
        }
    @PostMapping(REGISTRATION_MAPPING)
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationDTO userDto) throws Exception {
        log.info("Register User Controller");
        return ResponseEntity.ok(register(userDto));
    }

        private UserRegistrationDTO register(UserRegistrationDTO userDto){
                return userDetailsManager.createUser(userDto);
        }


}
