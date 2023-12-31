package ua.expandapis.model.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.expandapis.dto.UserRegistrationDTO;
import ua.expandapis.exeption.UserAlreadyExistAuthenticationException;
import ua.expandapis.model.entity.User;
import ua.expandapis.model.repository.UserRepository;
import ua.expandapis.util.MapperUtil;

@Service
@Slf4j
@Transactional
public class JwtUserDetailsService implements ExtendedUserDetailsService {
    @PersistenceContext
    private EntityManager entityManager;
    private UserRepository userRepository;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public JwtUserDetailsService(UserRepository userRepository, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtUserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        log.info("Load User Service");
        //log.info("ADS - username ->" + email);
        User account = userRepository
                .findByUsername(name)
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        //log.info("ADS - user ->" + account);
        return new JwtUserDetails(account);
    }

    @Override
    public UserRegistrationDTO createUser(UserRegistrationDTO userDTO) {
        log.info("Create new User Service");

        User user = mapperUtil.convertToEntity(userDTO, User.class);
        if(user.getId() == null ) {
            if(userRepository.findByUsername(userDTO.getUsername()).isPresent()){
                log.info("User already exist");
                throw new UserAlreadyExistAuthenticationException("User already exist");
            };

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            entityManager.persist(user);
        }
        return mapperUtil.convertToDto(user, UserRegistrationDTO.class);
    }


}
