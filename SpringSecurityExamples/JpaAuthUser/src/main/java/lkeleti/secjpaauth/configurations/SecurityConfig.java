package lkeleti.secjpaauth.configurations;


import lkeleti.secjpaauth.model.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfig {

    @Autowired
    private WebApplicationContext applicationContext;

    public CustomUserDetailsService userDetailsService;

    @PostConstruct
    public void completeSetup() {
        userDetailsService = applicationContext.getBean(CustomUserDetailsService.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( csrf-> csrf.disable())
                .authorizeRequests(
                        auth -> auth
                                .antMatchers("/api/home").permitAll()
                                .antMatchers("/api/user").hasRole("USER")
                                .antMatchers("/api/admin").hasRole("ADMIN")
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
