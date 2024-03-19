package ua.expandapis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.expandapis.security.filter.JwtRequestFilter;
import ua.expandapis.security.provider.JwtAuthenticationProvider;
import ua.expandapis.util.JwtTokenUtil;


@Configuration
@EnableWebSecurity
@Slf4j
//@EnableMethodSecurity
public class SecurityConfig {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetailsService userDetailsService;
    private InvalidAuthenticationEntryPoint invalidAuthenticationEntryPoint;
    private JwtRequestFilter jwtRequestFilter;

    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired
    public SecurityConfig(JwtTokenUtil jwtTokenUtil, @Lazy UserDetailsService userDetailsService,
                          InvalidAuthenticationEntryPoint invalidAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter, JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.invalidAuthenticationEntryPoint = invalidAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(invalidAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests

                        .requestMatchers(HttpMethod.POST, "/user/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/authenticate").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                standardAuthenticationProvider(),
                jwtAuthenticationProvider
        );
    }

    /* UNSUPPORTED in new Spring
   //https://github.com/spring-projects/spring-security/issues/11926
   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
       log.info("AuthenticationManager -> "+config.getAuthenticationManager().getClass());
       return config.getAuthenticationManager();
   }*/

    @Bean
    public AuthenticationProvider standardAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userDetailsService.loadUserByUsername(username);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
