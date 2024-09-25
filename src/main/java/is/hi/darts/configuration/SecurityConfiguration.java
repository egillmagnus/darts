package is.hi.darts.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**").permitAll()  // Allow public access
                        .anyRequest().authenticated()  // Other routes require login
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .failureUrl("/login-error")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/", true)  // Redirect after successful login
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll  // Allow everyone to logout
                );

        return http.build();
    }
}
