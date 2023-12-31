package ua.expandapis.model.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import ua.expandapis.dto.UserRegistrationDTO;

public interface ExtendedUserDetailsService extends UserDetailsService {
    public UserRegistrationDTO createUser(UserRegistrationDTO user);
}
